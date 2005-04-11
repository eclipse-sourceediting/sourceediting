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
package org.eclipse.wst.html.validation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.html.core.internal.validate.HTMLValidationAdapterFactory;
import org.eclipse.wst.html.ui.internal.HTMLUIMessages;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.INodeAdapterFactory;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.util.URIResolver;
import org.eclipse.wst.sse.core.validate.ValidationAdapter;
import org.eclipse.wst.validation.internal.operations.IWorkbenchHelper;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IFileDelta;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.document.IDOMDocument;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclispe.wst.validation.internal.core.Message;

public class HTMLValidator implements IValidator {
	/**
	 */
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
		if (file == null)
			return false;
		String name = file.getFullPath().toString();
		if (name == null)
			return false;
		int index = name.lastIndexOf('.');
		if (index < 0)
			return false;
		String ext = name.substring(index + 1);
		if (ext == null || ext.length() == 0)
			return false;
		ext = ext.toLowerCase();
		return (ext.startsWith("htm") || //$NON-NLS-1$
					ext.startsWith("jsp") || //$NON-NLS-1$
					ext.equals("jsf") || //$NON-NLS-1$
					ext.startsWith("xht") || //$NON-NLS-1$
					ext.startsWith("shtm") || //$NON-NLS-1$
					ext.startsWith("wml") || //$NON-NLS-1$
		ext.equals("jhtml"));//$NON-NLS-1$
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
	public void validate(IValidationContext helper, IReporter reporter, IFileDelta[] deltaArray) {
		if (helper == null)
			return;
		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}
		if (deltaArray != null && deltaArray.length > 0) {
			validateDelta(helper, reporter, deltaArray);
		}
		else {
			validateFull(helper, reporter, deltaArray);
		}
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
			String args[] = new String[]{file.getFullPath().toString()};

			// Message mess = new Message("HTMLValidation", //$NON-NLS-1$
			// SeverityEnum.LOW_SEVERITY,
			// "MESSAGE_HTML_VALIDATION_MESSAGE_UI_", //$NON-NLS-1$
			// args);
			Message mess = new LocalizedMessage(IMessage.LOW_SEVERITY, HTMLUIMessages.MESSAGE_HTML_VALIDATION_MESSAGE_UI_);
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
	private void validateDelta(IValidationContext helper, IReporter reporter, IFileDelta[] deltaArray) {
		for (int i = 0; i < deltaArray.length; i++) {
			IFileDelta delta = deltaArray[i];
			if (delta == null || delta.getDeltaType() == IFileDelta.DELETED)
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
	private void validateFull(IValidationContext helper, IReporter reporter, IFileDelta[] fileDelta) {
		IProject project = null;
		if (helper instanceof IWorkbenchHelper) {
			IWorkbenchHelper wbHelper = (IWorkbenchHelper) helper;
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
	 * @see com.ibm.sse.editor.extensions.validator.IWorkbenchHelper#getResource(com.ibm.sse.editor.extensions.validator.IFileDelta)
	 */
	public IResource getResource(IFileDelta delta) {
		IResource res = null;
		if (delta instanceof IResource)
			res = (IResource) delta;
		else
			res = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(delta.getFileName()));
		return res;
	}
}