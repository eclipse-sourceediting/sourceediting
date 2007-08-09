/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSPJavaValidator extends JSPValidator implements ISourceValidator {
	private static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		if (helper instanceof IWorkbenchContext) {
			/*
			 * Wrap validation inside of a JavaModelOperation to collapse
			 * event notification. For this reason, we use a single build rule
			 * when running batch validation.
			 */
			return ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
		}
		/*
		 * For other kinds of validation, use no specific rule
		 */
		return null;
	}

	private IValidator fMessageOriginator;
	private IStructuredModel fModel;
	private IFile fFile;
	private boolean fEnableSourceValidation;

	public JSPJavaValidator() {
		this.fMessageOriginator = this;
	}

	void performValidation(IFile f, IReporter reporter, IStructuredModel model) {
		IDOMModel domModel = (IDOMModel) model;

		setupAdapterFactory(domModel);
		IDOMDocument xmlDoc = domModel.getDocument();
		JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
		JSPTranslation translation = translationAdapter.getJSPTranslation();

		if (!reporter.isCancelled()) {
			translation.setProblemCollectingActive(true);
			translation.reconcileCompilationUnit();
			List problems = translation.getProblems();
			// add new messages
			for (int i = 0; i < problems.size() && !reporter.isCancelled(); i++) {
				IMessage m = createMessageFromProblem((IProblem) problems.get(i), f, translation, domModel.getStructuredDocument());
				if (m != null)
					reporter.addMessage(fMessageOriginator, m);
			}
		}
	}

	public JSPJavaValidator(IValidator validator) {
		this.fMessageOriginator = validator;
	}

	public void connect(IDocument document) {
		IFile file = null;
		try {
			fModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (fModel != null) {
				String baseLocation = fModel.getBaseLocation();
				// The baseLocation may be a path on disk or relative to the
				// workspace root. Don't translate on-disk paths to
				// in-workspace resources.
				IPath basePath = new Path(baseLocation);
				if (basePath.segmentCount() > 1) {
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
					/*
					 * If the IFile doesn't exist, make sure it's not returned
					 */
					if (!file.exists())
						file = null;
				}
			}
		}
		finally {
			if (fModel != null) {
				fModel.releaseFromRead();
			}
		}
		fFile = file;
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=199175
		fEnableSourceValidation = (fFile != null && fModel != null && JSPBatchValidator.isBatchValidatorPreferenceEnabled(fFile) && shouldReallyValidate(fFile));
		if(DEBUG) {
			Logger.log(Logger.INFO, getClass().getName() + " enablement for source validation: " + fEnableSourceValidation); //$NON-NLS-1$
		}
	}

	public void disconnect(IDocument document) {
		fModel = null;
		fFile = null;
	}

	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		if(fEnableSourceValidation) {
			if(DEBUG) {
				Logger.log(Logger.INFO, getClass().getName() + " validating "); //$NON-NLS-1$
			}
			performValidation(fFile, reporter, fModel);
		}
	}

	public void validate(final IValidationContext helper, final IReporter reporter) throws ValidationException {
		/*
		 * Use the current Job's rule for the JavaModelOperation
		 */
		Job currentJob = Platform.getJobManager().currentJob();
		ISchedulingRule rule = null;
		if (currentJob != null) {
			rule = currentJob.getRule();
		}

		IWorkspaceRunnable validationRunnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				doValidate(helper, reporter);
			}
		};
		try {
			JavaCore.run(validationRunnable, rule, new NullProgressMonitor());
		}
		catch (CoreException e) {
			// we're only allowed to throw a ValidationException here
			if (e.getCause() instanceof ValidationException) {
				throw (ValidationException) e.getCause();
			}
			throw new ValidationException(new LocalizedMessage(IMessage.ERROR_AND_WARNING, e.getMessage()), e);
		}
	}

	void doValidate(IValidationContext helper, IReporter reporter) throws CoreException {
		try {
			super.validate(helper, reporter);
		}
		catch (ValidationException e) {
			// we're only allowed to throw a CoreException within the runnable
			String pluginId = JSPCorePlugin.getDefault().getBundle().getSymbolicName();
			throw new CoreException(new Status(IStatus.ERROR, pluginId, 0, pluginId, e));
		}
	}

	/**
	 * Validate one file. It's assumed that the file has JSP content type.
	 * 
	 * @param f
	 * @param reporter
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		IStructuredModel model = null;
		try {
			// get jsp model, get translation
			if (!reporter.isCancelled()) {
				model = StructuredModelManager.getModelManager().getModelForRead(f);
				if (model instanceof IDOMModel) {
					reporter.removeAllMessages(fMessageOriginator, f);
					performValidation(f, reporter, model);
				}
			}
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * When loading model from a file, you need to explicitly add adapter
	 * factory.
	 * 
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {
		if (sm.getFactoryRegistry().getFactoryFor(IJSPTranslation.class) == null) {
			JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
			sm.getFactoryRegistry().addFactory(factory);
		}
	}

	/**
	 * Creates an IMessage from an IProblem
	 * 
	 * @param problem
	 * @param f
	 * @param translation
	 * @param structuredDoc
	 * @return message representation of the problem, or null if it could not
	 *         create one
	 */
	private IMessage createMessageFromProblem(IProblem problem, IFile f, JSPTranslation translation, IStructuredDocument structuredDoc) {

		int sourceStart = translation.getJspOffset(problem.getSourceStart());
		int sourceEnd = translation.getJspOffset(problem.getSourceEnd());
		if (sourceStart == -1)
				return null;

		// line number for marker starts @ 1
		// line number from document starts @ 0
		int lineNo = structuredDoc.getLineOfOffset(sourceStart) + 1;

		int sev = problem.isError() ? IMessage.HIGH_SEVERITY : IMessage.NORMAL_SEVERITY;

		IMessage m = new LocalizedMessage(sev, problem.getMessage(), f);

		m.setLineNo(lineNo);
		m.setOffset(sourceStart);
		m.setLength(sourceEnd - sourceStart + 1);

		// need additional adjustment for problems from
		// indirect (included) files
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=119633
		if (translation.isIndirect(problem.getSourceStart())) {
			adjustIndirectPosition(m, translation);
		}

		return m;
	}

	/**
	 * Assumed the message offset is an indirect position. In other words, an
	 * error from an included file.
	 * 
	 * @param m
	 * @param translation
	 */
	private void adjustIndirectPosition(IMessage m, JSPTranslation translation) {

		if (!(translation instanceof JSPTranslationExtension))
			return;

		IDocument jspDoc = ((JSPTranslationExtension) translation).getJspDocument();
		if (!(jspDoc instanceof IStructuredDocument))
			return;

		IStructuredDocument sDoc = (IStructuredDocument) jspDoc;
		IStructuredDocumentRegion[] regions = sDoc.getStructuredDocumentRegions(0, m.getOffset() + m.getLength());
		// iterate backwards until you hit the include directive
		for (int i = regions.length - 1; i >= 0; i--) {

			IStructuredDocumentRegion region = regions[i];
			if (region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				if (getDirectiveName(region).equals("include")) { //$NON-NLS-1$
					ITextRegion fileValueRegion = getAttributeValueRegion(region, "file"); //$NON-NLS-1$
					m.setOffset(region.getStartOffset(fileValueRegion));
					m.setLength(fileValueRegion.getTextLength());
					break;
				}
			}
		}
	}
}
