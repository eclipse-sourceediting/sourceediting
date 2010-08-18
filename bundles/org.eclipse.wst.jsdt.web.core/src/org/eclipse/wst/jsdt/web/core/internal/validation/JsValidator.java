/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.jsdt.core.compiler.IProblem;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JsValidator extends AbstractValidator implements IValidator, IExecutableExtension {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jsvalidator")).booleanValue(); //$NON-NLS-1$
	private IValidator fMessageOriginator;
	private Set fValidFileExts = new HashSet();
	
	private static final String[] METADATA_FILES = new String[]{".settings/.jsdtscope",".settings/org.eclipse.wst.jsdt.ui.superType.container",".settings/org.eclipse.wst.jsdt.ui.superType.name"};
	
//	private static String [] jsdtValidator = {"org.eclipse.wst.jsdt.web.core.internal.validation.JsBatchValidator"}; //$NON-NLS-1$

	
	protected class LocalizedMessage extends Message {
		private String _message = null;
		
		public LocalizedMessage(int severity, String messageText) {
			this(severity, messageText, null);
		}
		
		public LocalizedMessage(int severity, String messageText, IResource targetObject) {
			this(severity, messageText, (Object) targetObject);
		}
		
		public LocalizedMessage(int severity, String messageText, Object targetObject) {
			super(null, severity, null);
			setLocalizedMessage(messageText);
			setTargetObject(targetObject);
		}
		
		public String getLocalizedMessage() {
			return _message;
		}
		
		
		public String getText() {
			return getLocalizedMessage();
		}
		
		
		public String getText(ClassLoader cl) {
			return getLocalizedMessage();
		}
		
		
		public String getText(Locale l) {
			return getLocalizedMessage();
		}
		
		
		public String getText(Locale l, ClassLoader cl) {
			return getLocalizedMessage();
		}
		
		public void setLocalizedMessage(String message) {
			_message = message;
		}
	}
	public JsValidator() {
		this.fMessageOriginator = this;
	}
	
	/**
	 * Creates an IMessage from an IProblem
	 * 
	 * @param problem
	 * @param f
	 * @param translation
	 * @param textDoc
	 * @return message representation of the problem, or null if it could not
	 *         create one
	 */
	private IMessage createMessageFromProblem(IProblem problem, IFile f, IJsTranslation translation, IDocument textDoc) {
		int sourceStart = problem.getSourceStart();
		int sourceEnd = problem.getSourceEnd();
		if (sourceStart == -1) {
			return null;
		}
		
		/*
		 * Bug 241794 - Validation shows errors when using JSP Expressions
		 * inside JavaScript code
		 */
		IStructuredDocument doc = (IStructuredDocument) textDoc;
		IStructuredDocumentRegion documentRegion = doc.getRegionAtCharacterOffset(sourceStart);
		if (documentRegion != null) {
			ITextRegion textRegion = documentRegion.getRegionAtCharacterOffset(sourceStart);
			/*
			 * Filter out problems from areas that aren't simple JavaScript,
			 * e.g. JSP.
			 */
			if (textRegion != null && textRegion instanceof ITextRegionCollection)
				return null;
		}

		int sev = problem.isError() ? IMessage.HIGH_SEVERITY : (problem.isWarning() ? IMessage.NORMAL_SEVERITY : IMessage.LOW_SEVERITY);
		IMessage m = new LocalizedMessage(sev, problem.getMessage(), f);
		// line numbers for marker starts @ 1
		// line numbers from document starts @ 0
		try {
			int lineNo = textDoc.getLineOfOffset(sourceStart) + 1;
			m.setLineNo(lineNo);
			m.setOffset(sourceStart);
			m.setLength(sourceEnd - sourceStart + 1);
		}
		catch (BadLocationException e) {
			Logger.logException(e);
		}
		return m;
	}
	
	void performValidation(IFile f, IReporter reporter, IStructuredModel model, boolean inBatch) {
		if (model instanceof IDOMModel) {
			IDOMModel domModel = (IDOMModel) model;
			setupAdapterFactory(domModel);
			IDOMDocument xmlDoc = domModel.getDocument();
			JsTranslationAdapter translationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			//translationAdapter.resourceChanged();
			IJsTranslation translation = translationAdapter.getJsTranslation(false);
			if (!reporter.isCancelled()) {
				translation.setProblemCollectingActive(true);
				translation.reconcileCompilationUnit();
				List problems = translation.getProblems();
//				if(!inBatch) reporter.removeAllMessages(this, f);
				// add new messages
				for (int i = 0; i < problems.size() && !reporter.isCancelled(); i++) {
					IMessage m = createMessageFromProblem((IProblem) problems.get(i), f, translation, domModel.getStructuredDocument());
					if (m != null) {
						reporter.addMessage(fMessageOriginator, m);
					}
				}
			}
		}
	}
	
	/* Read the definition for this validator and the declared valid file extensions
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		IConfigurationElement[] includes = config.getChildren("include"); //$NON-NLS-1$
		for (int i = 0; i < includes.length; i++) {
			IConfigurationElement[] fileexts = includes[i].getChildren("fileext"); //$NON-NLS-1$
			for (int j = 0; j < fileexts.length; j++) {
				String fileext = fileexts[j].getAttribute("ext"); //$NON-NLS-1$
				if (fileext != null) {
					fValidFileExts.add(fileext);
				}
			}
		}
	}
	
	/**
	 * Ensures that our translation adapter is present before we try to use it
	 * 
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {
		if (sm.getFactoryRegistry().getFactoryFor(IJsTranslation.class) == null) {
			JsTranslationAdapterFactory factory = new JsTranslationAdapterFactory();
			sm.getFactoryRegistry().addFactory(factory);
		}
	}
	
	boolean shouldValidate(IFile file) {
		IResource resource = file;
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
				return false;
			}
			resource = resource.getParent();
		} while ((resource.getType() & IResource.PROJECT) == 0);
		return fValidFileExts.isEmpty() || fValidFileExts.contains(file.getFileExtension());
	}
	
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		/* Added by BC ---- */
		// if(true) return;
		/* end Added by BC ---- */
		
		String[] uris = helper.getURIs();
		if (uris.length > 0) {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IFile currentFile = null;
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				currentFile = wsRoot.getFile(new Path(uris[i]));
				reporter.removeAllMessages(this, currentFile);
				if (currentFile != null && currentFile.exists()) {
					if (shouldValidate(currentFile) ){ //&& fragmentCheck(currentFile)) {
						int percent = (i * 100) / uris.length + 1;
						IMessage message = new LocalizedMessage(IMessage.LOW_SEVERITY, percent + "% " + uris[i]); //$NON-NLS-1$
						reporter.displaySubtask(this, message);
						validateFile(currentFile, reporter);
					}
					if (DEBUG) {
						System.out.println("validating: [" + uris[i] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		} else {
			// if uris[] length 0 -> validate() gets called for each project
			if (helper instanceof IWorkbenchContext) {
				IProject project = ((IWorkbenchContext) helper).getProject();
				JSFileVisitor visitor = new JSFileVisitor(reporter);
				try {
					// collect all jsp files for the project
					project.accept(visitor, IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
					if (DEBUG) {
						e.printStackTrace();
					}
				}
				IFile[] files = visitor.getFiles();
				for (int i = 0; i < files.length && !reporter.isCancelled(); i++) {
					int percent = (i * 100) / files.length + 1;
					IMessage message = new LocalizedMessage(IMessage.LOW_SEVERITY, percent + "% " + files[i].getFullPath().toString()); //$NON-NLS-1$
					reporter.displaySubtask(this, message);
					validateFile(files[i], reporter);
					if (DEBUG) {
						System.out.println("validating: [" + files[i] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
	}

	protected class JSFileVisitor implements IResourceProxyVisitor {
		private List fFiles = new ArrayList();
		private IReporter fReporter = null;
		
		public JSFileVisitor(IReporter reporter) {
			fReporter = reporter;
		}
		
		public final IFile[] getFiles() {
			return (IFile[]) fFiles.toArray(new IFile[fFiles.size()]);
		}
		
		
		public boolean visit(IResourceProxy proxy) throws CoreException {
			// check validation
			if (fReporter.isCancelled()) {
				return false;
			}
			if (proxy.getType() == IResource.FILE) {
				if (Util.isJsType(proxy.getName())) {
					IFile file = (IFile) proxy.requestResource();
					if (file.exists() && shouldValidate(file)) {
						if (DEBUG) {
							System.out.println("(+) JSPValidator adding file: " + file.getName()); //$NON-NLS-1$
						}
						fFiles.add(file);
						// don't search deeper for files
						return false;
					}
				}
			}
			return true;
		}
	}
	public void cleanup(IReporter reporter) {
		// nothing to do
	}
	/**
	 * Validate one file. It's assumed that the file has JSP content type.
	 * 
	 * @param f
	 * @param reporter
	 */
	
	protected void validateFile(IFile f, IReporter reporter) {
		if (JsValidator.DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " validating: " + f); //$NON-NLS-1$
		}
		IStructuredModel model = null;
		try {
			// get jsp model, get tranlsation
			model = StructuredModelManager.getModelManager().getModelForRead(f);
			if (!reporter.isCancelled() && model != null) {
				// get DOM model then translation
				//WorkbenchReporter.removeAllMessages(f.getProject(), jsdtValidator, f.toString());
				//reporter.removeAllMessages(fMessageOriginator, f);
				performValidation(f, reporter, model, false);
			}
		} catch (IOException e) {
			Logger.logException(e);
		} catch (CoreException e) {
			Logger.logException(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
	}
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		ValidationResult result = new ValidationResult();
		IReporter reporter = result.getReporter(monitor);
		IFile file = (IFile) resource;
		validateFile(file, reporter);
		result.setDependsOn(createDependencies(file));
		return result;
	}

	private IResource[] createDependencies(IFile file) {
		IFile[] depends = new IFile[METADATA_FILES.length];
		for (int i = 0; i < METADATA_FILES.length; i++) {
			depends[i] = file.getProject().getFile(METADATA_FILES[i]);
		}
		return depends;
	}
}
