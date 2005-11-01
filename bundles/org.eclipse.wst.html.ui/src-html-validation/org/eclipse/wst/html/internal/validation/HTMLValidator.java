/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.internal.validation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.html.core.internal.validate.HTMLValidationAdapterFactory;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Text;

public class HTMLValidator implements IValidator, ISourceValidator {
	private static final String ORG_ECLIPSE_JST_JSP_CORE_JSPSOURCE = "org.eclipse.jst.jsp.core.jspsource";
	private static final String ORG_ECLIPSE_WST_HTML_CORE_HTMLSOURCE = "org.eclipse.wst.html.core.htmlsource";

	static boolean shouldValidate(IFile file) {
		IResource resource = file;
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || (resource.getName().charAt(0) == '.' && resource.getType() == IResource.FOLDER)) {
				return false;
			}
			resource = resource.getParent();
		}
		while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	private IDocument fDocument;

	public HTMLValidator() {
		super();
	}

	/**
	 */
	public void cleanup(IReporter reporter) {
		// nothing to do
	}

	/**
	 */
	protected IDOMModel getModel(IProject project, IFile file) {
		if (project == null || file == null)
			return null;
		if (!file.exists())
			return null;
		if (!canHandle(file))
			return null;

		IStructuredModel model = null;
		IModelManager manager = StructuredModelManager.getModelManager();

		try {
			try {
				model = manager.getModelForRead(file);
			}
			catch (UnsupportedEncodingException ex) {
				// retry ignoring META charset for invalid META charset
				// specification
				// recreate input stream, because it is already partially read
				model = manager.getModelForRead(file, new String(), null);
			}
		}
		catch (UnsupportedEncodingException ex) {
		}
		catch (IOException ex) {
		}
		catch (CoreException ex) {
		}

		if (model == null)
			return null;
		if (!(model instanceof IDOMModel)) {
			releaseModel(model);
			return null;
		}
		return (IDOMModel) model;
	}

	/**
	 */
	protected HTMLValidationReporter getReporter(IReporter reporter, IFile file, IDOMModel model) {
		return new HTMLValidationReporter(this, reporter, file, model);
	}

	/**
	 * Check file extension to validate
	 */
	private boolean canHandle(IFile file) {
		boolean result = false;
		if (file != null) {
			try {
				IContentTypeManager contentTypeManager = Platform.getContentTypeManager();

				IContentDescription contentDescription = file.getContentDescription();
				IContentType htmlContentType = contentTypeManager.getContentType(ORG_ECLIPSE_WST_HTML_CORE_HTMLSOURCE);
				if (contentDescription != null) {
					IContentType fileContentType = contentDescription.getContentType();

					if (htmlContentType != null) {
						if (fileContentType.isKindOf(htmlContentType)) {
							result = true;
						}
						else {
							// ISSUE: here's a little "backwards" dependancy.
							// there should be a "JSPEMBEDDEDHTML validator"
							// contributed by JSP plugin.
							IContentType jspContentType = contentTypeManager.getContentType(ORG_ECLIPSE_JST_JSP_CORE_JSPSOURCE);
							if (jspContentType != null) {
								result = fileContentType.isKindOf(jspContentType);
							}
						}
					}
				}
				else if (htmlContentType != null) {
					result = htmlContentType.isAssociatedWith(file.getName());
				}
			}
			catch (CoreException e) {
				// should be rare, but will ignore to avoid logging "encoding
				// exceptions" and the like here.
				// Logger.logException(e);
			}
		}
		return result;
	}

	/**
	 */
	private boolean hasHTMLFeature(IDOMDocument document) {
		DocumentTypeAdapter adapter = (DocumentTypeAdapter) document.getAdapterFor(DocumentTypeAdapter.class);
		if (adapter == null)
			return false;
		return adapter.hasFeature("HTML");//$NON-NLS-1$
	}

	/**
	 */
	protected void releaseModel(IStructuredModel model) {
		if (model != null)
			model.releaseFromRead();
	}

	/**
	 */
	public void validate(IValidationContext helper, IReporter reporter) {
		if (helper == null)
			return;
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		String[] deltaArray = helper.getURIs();
		if (deltaArray != null && deltaArray.length > 0) {
			validateDelta(helper, reporter);
		}
		else {
			validateFull(helper, reporter);
		}
	}
	
	/**
	 * This validate call is for the ISourceValidator partial document validation approach
	 * @param dirtyRegion
	 * @param helper
	 * @param reporter
	 *  @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator
	 */
	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		
		if (helper == null || fDocument == null)
			return;
		
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		
		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
		if (model == null)
			return; // error
	
		try {
		
			IDOMDocument document = null;
			if(model instanceof IDOMModel) {
				document = ((IDOMModel)model).getDocument();
			}

			if (document == null || !hasHTMLFeature(document))
				return ; // ignore
	
			ITextFileBuffer fb = FileBufferModelManager.getInstance().getBuffer(fDocument);
			if(fb == null)
				return;
			
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fb.getLocation());
			if(file == null || !file.exists())
				return;
			
			// this will be the wrong region if it's Text (instead of Element)
			// we don't know how to validate Text
			IndexedRegion ir = model.getIndexedRegion(dirtyRegion.getOffset());
			if(ir instanceof Text) {
				while(ir != null && ir instanceof Text) {
					// it's assumed that this gets the IndexedRegion to
					// the right of the end offset
					ir = model.getIndexedRegion(ir.getEndOffset());
				}
			}
			
			if(ir instanceof INodeNotifier) {
				
				INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
				ValidationAdapter adapter = (ValidationAdapter) factory.adapt((INodeNotifier)ir);
				if (adapter == null)
					return; // error
				
				if (reporter != null) {
					HTMLValidationReporter rep = null;
					rep = getReporter(reporter, file, (IDOMModel)model);
					rep.clear();
					adapter.setReporter(rep);
					
					String fileName = "";
					IPath filePath = file.getFullPath();
					if (filePath != null) {
						fileName = filePath.toString();
					}
					String args[] = new String[]{fileName};
		
					Message mess = new LocalizedMessage(IMessage.LOW_SEVERITY, NLS.bind(HTMLUIMessages.MESSAGE_HTML_VALIDATION_MESSAGE_UI_, args));
					mess.setParams(args);
					reporter.displaySubtask(this, mess);
				}
				adapter.validate(ir);
			}
		}
		finally {
			if(model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator
	 */
	public void connect(IDocument document) {
		fDocument = document;
	}
	/**
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator
	 */
	public void disconnect(IDocument document) {
		// don't need to do anything
	}
	
	/**
	 */
	protected HTMLValidationResult validate(IDOMModel model, IFile file) {
		IProject prj = null;
		if (file != null) {
			prj = file.getProject();
		}
		if ((prj == null) && (model != null)) {
			URIResolver res = model.getResolver();
			if (res != null) {
				prj = res.getProject();
			}
		}
		final WorkbenchReporter reporter = new WorkbenchReporter(prj, new NullProgressMonitor());
		return validate(reporter, file, model);
	}

	/**
	 */
	private HTMLValidationResult validate(IReporter reporter, IFile file, IDOMModel model) {
		if (file == null || model == null)
			return null; // error
		IDOMDocument document = model.getDocument();
		if (document == null)
			return null; // error
		if (!hasHTMLFeature(document))
			return null; // ignore

		INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
		ValidationAdapter adapter = (ValidationAdapter) factory.adapt(document);
		if (adapter == null)
			return null; // error

		HTMLValidationReporter rep = getReporter(reporter, file, model);
		rep.clear();
		adapter.setReporter(rep);
		if (reporter != null) {
			String fileName = "";
			IPath filePath = file.getFullPath();
			if (filePath != null) {
				fileName = filePath.toString();
			}
			String args[] = new String[]{fileName};

			// Message mess = new Message("HTMLValidation", //$NON-NLS-1$
			// SeverityEnum.LOW_SEVERITY,
			// "MESSAGE_HTML_VALIDATION_MESSAGE_UI_", //$NON-NLS-1$
			// args);
			Message mess = new LocalizedMessage(IMessage.LOW_SEVERITY, NLS.bind(HTMLUIMessages.MESSAGE_HTML_VALIDATION_MESSAGE_UI_, args));
			mess.setParams(args);
			reporter.displaySubtask(this, mess);
		}
		adapter.validate(document);
		return rep.getResult();
	}

	/**
	 */
	private void validateContainer(IValidationContext helper, IReporter reporter, IContainer container) {
		try {
			IResource[] resourceArray = container.members(false);
			for (int i = 0; i < resourceArray.length; i++) {
				IResource resource = resourceArray[i];
				if (resource == null)
					continue;
				if (resource instanceof IFile) {
					validateFile(helper, reporter, (IFile) resource);
				}
				else if (resource instanceof IContainer) {
					validateContainer(helper, reporter, (IContainer) resource);
				}
			}
		}
		catch (CoreException ex) {
		}
	}

	/**
	 */
	private void validateDelta(IValidationContext helper, IReporter reporter) {
		String[] deltaArray = helper.getURIs();
		for (int i = 0; i < deltaArray.length; i++) {
			String delta = deltaArray[i];
			if (delta == null)
				continue;
			IResource resource = getResource(delta);
			if (resource == null || !(resource instanceof IFile))
				continue;
			validateFile(helper, reporter, (IFile) resource);
		}
	}

	/**
	 */
	private void validateFile(IValidationContext helper, IReporter reporter, IFile file) {
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		if (!shouldValidate(file)) {
			return;
		}
		IDOMModel model = getModel(file.getProject(), file);
		if (model == null)
			return;

		try {
			validate(reporter, file, model);
		}
		finally {
			releaseModel(model);
		}
	}

	/**
	 */
	private void validateFull(IValidationContext helper, IReporter reporter) {
		IProject project = null;
		String[] fileDelta = helper.getURIs();
		if (helper instanceof IWorkbenchContext) {
			IWorkbenchContext wbHelper = (IWorkbenchContext) helper;
			project = wbHelper.getProject();
		}
		else {
			// won't work for project validation (b/c nothing in file delta)
			project = getResource(fileDelta[0]).getProject();
		}
		if (project == null)
			return;
		validateContainer(helper, reporter, project);
	}

	/*
	 * added to get rid or dependency on IWorkbenchHelper
	 * 
	 */
	public IResource getResource(String delta) {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(delta));
	}
}