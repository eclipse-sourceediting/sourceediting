/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contenttype;

import java.io.IOException;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.eclipse.wst.common.encoding.IContentDescriptionExtended;
import org.eclipse.wst.common.encoding.IResourceCharsetDetector;

public class ContentDescriberForJSP extends AbstractContentDescriber implements ITextContentDescriber {
	private final static QualifiedName[] SUPPORTED_OPTIONS = {
				IContentDescription.CHARSET, 
				IContentDescription.BYTE_ORDER_MARK, 
				IContentDescriptionExtended.DETECTED_CHARSET, 
				IContentDescriptionExtended.UNSUPPORTED_CHARSET, 
				IContentDescriptionExtended.APPROPRIATE_DEFAULT,
				IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, 
				IContentDescriptionForJSP.LANGUAGE_ATTRIBUTE};

	public QualifiedName[] getSupportedOptions() {
		return SUPPORTED_OPTIONS;
	}

	protected IResourceCharsetDetector getDetector() {
		return new JSPResourceEncodingDetector();
	}

	protected void handleCalculations(IContentDescription description, IResourceCharsetDetector detector) throws IOException {
		// handle standard ones first, to be sure detector processes
		super.handleCalculations(description, detector);
		// now do those specific for JSPs
		// note: detector should always be of correct instance, but we'll check, for now.
		if (detector instanceof JSPResourceEncodingDetector) {
			JSPResourceEncodingDetector jspDetector = (JSPResourceEncodingDetector) detector;
			String language = jspDetector.getLanguage();
			if (language != null && language.length() > 0) {
				description.setProperty(IContentDescriptionForJSP.LANGUAGE_ATTRIBUTE, language);
			}
			String contentTypeAttribute = jspDetector.getContentType(); 
			if (contentTypeAttribute != null && contentTypeAttribute.length() > 0) {
				description.setProperty(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, contentTypeAttribute);
			}
		}
	}
}