/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeConstants;
import org.eclipse.wst.html.core.internal.validate.HTMLValidationAdapterFactory;
import org.eclipse.wst.html.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.util.URIResolver;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Text;

public class HTMLValidator extends AbstractValidator implements IValidatorJob, ISourceValidator, IExecutableExtension {
	private static final String ORG_ECLIPSE_WST_HTML_CORE_HTMLSOURCE = "org.eclipse.wst.html.core.htmlsource"; //$NON-NLS-1$

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
	private IContentTypeManager fContentTypeManager;
	private IContentType[] fOtherSupportedContentTypes = null;
	private String[] fAdditionalContentTypesIDs = null;
	private IContentType fHTMLContentType;

	public HTMLValidator() {
		super();
		fContentTypeManager = Platform.getContentTypeManager();
		fHTMLContentType = fContentTypeManager.getContentType(ORG_ECLIPSE_WST_HTML_CORE_HTMLSOURCE);
	}

	/**
	 */
	public void cleanup(IReporter reporter) {
		// nothing to do
	}

	/**
	 * Gets list of content types this validator is interested in
	 * 
	 * @return All HTML-related content types
	 */
	private IContentType[] getOtherSupportedContentTypes() {
		if (fOtherSupportedContentTypes == null) {
			List contentTypes = new ArrayList(3);
			if (fAdditionalContentTypesIDs != null) {
				for (int i = 0; i < fAdditionalContentTypesIDs.length; i++) {
					IContentType type = Platform.getContentTypeManager().getContentType(fAdditionalContentTypesIDs[i]);
					if (type != null) {
						contentTypes.add(type);
					}
				}
			}
			fOtherSupportedContentTypes = (IContentType[]) contentTypes.toArray(new IContentType[contentTypes.size()]);
		}
		return fOtherSupportedContentTypes;
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

		IModelManager manager = StructuredModelManager.getModelManager();
		if (manager == null)
			return null;

		IStructuredModel model = null;
		try {
			file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
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
		catch (CoreException e) {
			Logger.logException(e);
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
				IContentDescription contentDescription = file.getContentDescription();
				if (contentDescription != null) {
					IContentType fileContentType = contentDescription.getContentType();
					if (fileContentType.isKindOf(fHTMLContentType)) {
						result = true;
					}
					else {
						IContentType[] otherTypes = getOtherSupportedContentTypes();
						for (int i = 0; i < otherTypes.length; i++) {
							result = result || fileContentType.isKindOf(otherTypes[i]);
						}
					}
				}
				else if (fHTMLContentType != null) {
					result = fHTMLContentType.isAssociatedWith(file.getName());
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
		return adapter.hasFeature(HTMLDocumentTypeConstants.HTML);
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
	 * This validate call is for the ISourceValidator partial document
	 * validation approach
	 * 
	 * @param dirtyRegion
	 * @param helper
	 * @param reporter
	 * @see org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator
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
			if (model instanceof IDOMModel) {
				document = ((IDOMModel) model).getDocument();
			}

			if (document == null || !hasHTMLFeature(document)) {
				// handled in finally clause
				// model.releaseFromRead();
				return; //ignore
			}
			
			IPath filePath = null;
			IFile file = null;

			ITextFileBuffer fb = FileBufferModelManager.getInstance().getBuffer(fDocument);
			if (fb != null) {
				filePath = fb.getLocation();

				if (filePath.segmentCount() > 1) {
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
					if (!file.isAccessible()) {
						file = null;
					}
				}
			}
			else {
				filePath = new Path(model.getId());
			}

			// this will be the wrong region if it's Text (instead of Element)
			// we don't know how to validate Text
			IndexedRegion ir = getCoveringNode(dirtyRegion); //  model.getIndexedRegion(dirtyRegion.getOffset());
			if (ir instanceof Text) {
				while (ir != null && ir instanceof Text) {
					// it's assumed that this gets the IndexedRegion to
					// the right of the end offset
					ir = model.getIndexedRegion(ir.getEndOffset());
				}
			}
			
			if (ir instanceof INodeNotifier) {

				INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
				ValidationAdapter adapter = (ValidationAdapter) factory.adapt((INodeNotifier) ir);
				if (adapter == null)
					return; // error

				if (reporter != null) {
					HTMLValidationReporter rep = null;
					rep = getReporter(reporter, file, (IDOMModel) model);
					rep.clear();
					adapter.setReporter(rep);

					Message mess = new LocalizedMessage(IMessage.LOW_SEVERITY, filePath.toString().substring(1));
					reporter.displaySubtask(this, mess);
				}
				adapter.validate(ir);
			}
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	private IndexedRegion getCoveringNode(IRegion dirtyRegion) {
		
		IndexedRegion largestRegion = null;
		if(fDocument instanceof IStructuredDocument) {
			IStructuredDocumentRegion[] regions = ((IStructuredDocument) fDocument).getStructuredDocumentRegions(dirtyRegion.getOffset(), dirtyRegion.getLength());
			largestRegion = getLargest(regions);
		}
		return largestRegion;
	}
	protected IndexedRegion getLargest(IStructuredDocumentRegion[] sdRegions) {
		
		if(sdRegions == null || sdRegions.length == 0)
			return null;
		 
		IndexedRegion currentLargest = getCorrespondingNode(sdRegions[0]);
		for (int i = 0; i < sdRegions.length; i++) {
		    if(!sdRegions[i].isDeleted()) {
    			IndexedRegion corresponding = getCorrespondingNode(sdRegions[i]);
    			
    			if(currentLargest instanceof Text)
    				currentLargest = corresponding;
    			
                if(corresponding != null) {
                	if(!(corresponding instanceof Text)) {
	        			if (corresponding.getStartOffset() <= currentLargest.getStartOffset()  
	        						&&  corresponding.getEndOffset() >= currentLargest.getEndOffset() )
	        				currentLargest = corresponding;
                	}
                }
                
            }
		}
		return currentLargest;
	}
	protected IndexedRegion getCorrespondingNode(IStructuredDocumentRegion sdRegion) {
		IStructuredModel sModel = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
        IndexedRegion indexedRegion = null;
        try {
            if (sModel != null) 
                indexedRegion = sModel.getIndexedRegion(sdRegion.getStart());    
        } finally {
            if (sModel != null)
                sModel.releaseFromRead();
        }
        return indexedRegion;
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
		fDocument = null;
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
				if (resource == null || reporter.isCancelled())
					continue;
				if (resource instanceof IFile) {
					Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, resource.getFullPath().toString().substring(1));
					reporter.displaySubtask(this, message);
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

			if (reporter != null) {
				Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, delta.substring(1));
				reporter.displaySubtask(this, message);
			}

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
		else if(fileDelta.length > 0){
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
		Path path = new Path(delta);
		if (path.segmentCount() > 1)
			return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if (path.segmentCount() == 1)
			return ResourcesPlugin.getWorkspace().getRoot().getProject(delta);
		return null;
	}

	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter) throws ValidationException {
		// Exception catching was removed, see
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=123600
		IStatus status = Status.OK_STATUS;
		validate(helper, reporter);
		return status;
	}
	
	/**
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		fAdditionalContentTypesIDs = new String[0];
		if (data != null) {
			if (data instanceof String && data.toString().length() > 0) {
				fAdditionalContentTypesIDs = StringUtils.unpack(data.toString());
			}
		}
	}

	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		ValidationResult result = new ValidationResult();
		IReporter reporter = result.getReporter(monitor);
		validateFile(null, reporter, (IFile) resource);
		return result;
	}
}