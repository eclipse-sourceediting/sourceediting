/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeConstants;
import org.eclipse.wst.html.core.internal.validate.HTMLValidationAdapterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
import org.w3c.dom.Element;

/**
 * This validator validates the contents of the content type of the JSP, like
 * the HTML regions in a JSP with content type="text/html"
 */
public class JSPContentValidator extends JSPValidator {
	private static final String HTTP_JAVA_SUN_COM_JSP_PAGE = "http://java.sun.com/JSP/Page"; //$NON-NLS-1$
	private static final String XMLNS = "xmlns"; //$NON-NLS-1$
	private static final String XMLNS_JSP = "xmlns:jsp"; //$NON-NLS-1$


	/*
	 * Copied from HTMLValidator
	 */
	private HTMLValidationReporter getReporter(IReporter reporter, IFile file, IDOMModel model) {
		return new HTMLValidationReporter(this, reporter, file, model);
	}

	/*
	 * Copied from HTMLValidator
	 */
	private boolean hasHTMLFeature(IDOMDocument document) {
		DocumentTypeAdapter adapter = (DocumentTypeAdapter) document.getAdapterFor(DocumentTypeAdapter.class);
		if (adapter == null)
			return false;
		return adapter.hasFeature(HTMLDocumentTypeConstants.HTML);
	}

	private boolean isXMLJSP(IDOMDocument document) {
		Element root = document.getDocumentElement();
		return root != null && (root.hasAttribute(XMLNS_JSP) || HTTP_JAVA_SUN_COM_JSP_PAGE.equals(root.getAttribute(XMLNS)));
	}

	private void validate(IFile file, int kind, ValidationState state, IProgressMonitor monitor, IDOMModel model, IReporter reporter) {
		IDOMDocument document = model.getDocument();
		if (document == null)
			return; // error

		boolean isXMLJSP = isXMLJSP(document);
		boolean hasHTMLFeature = hasHTMLFeature(document);

		if (hasHTMLFeature && !isXMLJSP) {
			INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
			ValidationAdapter adapter = (ValidationAdapter) factory.adapt(document);
			if (adapter != null) {
				HTMLValidationReporter rep = getReporter(reporter, file, model);
				rep.clear();
				adapter.setReporter(rep);
				adapter.validate(document);
			}
		}
		if (!hasHTMLFeature && isXMLJSP) {
			Validator xmlValidator = new Validator();
			xmlValidator.validate(file, kind, state, monitor);
		}
	}

	/*
	 * Mostly copied from HTMLValidator
	 */
	private void validate(IReporter reporter, IFile file, IDOMModel model) {
		if (file == null || model == null)
			return; // error
		IDOMDocument document = model.getDocument();
		if (document == null)
			return; // error

		// This validator currently only handles validating HTML content in
		// JSP
		boolean hasXMLFeature = isXMLJSP(document);
		boolean hasHTMLFeature = hasHTMLFeature(document);
		if (hasHTMLFeature && !hasXMLFeature) {
			INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
			ValidationAdapter adapter = (ValidationAdapter) factory.adapt(document);
			if (adapter == null)
				return; // error

			HTMLValidationReporter rep = getReporter(reporter, file, model);
			rep.clear();
			adapter.setReporter(rep);
			adapter.validate(document);
		}
	}

	public ValidationResult validate(final IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		ValidationResult result = new ValidationResult();
		final IReporter reporter = result.getReporter(monitor);

		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead((IFile) resource);
			if (!reporter.isCancelled() && model instanceof IDOMModel) {
				reporter.removeAllMessages(this, resource);
				validate((IFile) resource, kind, state, monitor, (IDOMModel) model, reporter);
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

		return result;
	}


	protected void validateFile(IFile f, IReporter reporter) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(f);
			if (!reporter.isCancelled() && model instanceof IDOMModel) {
				reporter.removeAllMessages(this, f);
				validate(reporter, f, (IDOMModel) model);
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

}
