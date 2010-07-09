/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.html.core.internal.document.HTMLDocumentTypeConstants;
import org.eclipse.wst.html.core.internal.validate.HTMLValidationAdapterFactory;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * This validator validates the contents of the content type of the JSP, like
 * the HTML regions in a JSP with content type="text/html"
 */
public class JSPContentValidator extends JSPValidator {
	private IContentType fJSPFContentType;

	/*
	 * Copied from HTMLValidator
	 */
	private boolean hasHTMLFeature(IDOMDocument document) {
		DocumentTypeAdapter adapter = (DocumentTypeAdapter) document.getAdapterFor(DocumentTypeAdapter.class);
		if (adapter == null)
			return false;
		return adapter.hasFeature(HTMLDocumentTypeConstants.HTML);
	}

	/**
	 * Returns JSP fragment content type
	 * 
	 * @return jspf content type
	 */
	private IContentType getJSPFContentType() {
		if (fJSPFContentType == null) {
			fJSPFContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
		}
		return fJSPFContentType;
	}


	private boolean fragmentCheck(IFile file) {
		boolean shouldValidate = true;
		// quick check to see if this is possibly a jsp fragment
		if (getJSPFContentType().isAssociatedWith(file.getName())) {
			// get preference for validate jsp fragments
			shouldValidate = FragmentValidationTools.shouldValidateFragment(file);
		}
		return shouldValidate;
	}

	/*
	 * Copied from HTMLValidator
	 */
	private HTMLValidationReporter getReporter(IReporter reporter, IFile file, IDOMModel model) {
		return new HTMLValidationReporter(this, reporter, file, model);
	}

	/*
	 * Mostly copied from HTMLValidator
	 */
	protected void validate(IReporter reporter, IFile file, IDOMModel model) {
		if (file == null || model == null)
			return; // error
		IDOMDocument document = model.getDocument();
		if (document == null)
			return; // error

		// This validator currently only handles validating HTML content in
		// JSP
		if (hasHTMLFeature(document)) {
			INodeAdapterFactory factory = HTMLValidationAdapterFactory.getInstance();
			ValidationAdapter adapter = (ValidationAdapter) factory.adapt(document);
			if (adapter == null)
				return; // error

			HTMLValidationReporter rep = getReporter(reporter, file, model);
			rep.clear();
			adapter.setReporter(rep);
			adapter.validate(document);

			MarkupValidatorDelegate delegate = (MarkupValidatorDelegate) Platform.getAdapterManager().getAdapter(model, MarkupValidatorDelegate.class);
			if (delegate != null)
				delegate.validate(file, reporter);
		}
	}

	protected void validateFile(IFile f, IReporter reporter) {
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getModelForRead(f);
			if (!reporter.isCancelled() && model instanceof IDOMModel) {
				reporter.removeAllMessages(this, f);
				if (fragmentCheck(f)) {
					validate(reporter, f, (IDOMModel) model);
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
}
