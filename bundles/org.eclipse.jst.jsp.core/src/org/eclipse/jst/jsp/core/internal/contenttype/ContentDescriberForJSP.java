/*******************************************************************************
 * Copyright (c) 2004-2006 IBM Corporation and others.
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
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.IContentDescriptionForJSP;
import org.eclipse.wst.html.core.internal.provisional.contenttype.ContentTypeFamilyForHTML;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IContentDescriptionExtended;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;

public final class ContentDescriberForJSP implements ITextContentDescriber {
	private final static QualifiedName[] SUPPORTED_OPTIONS = {IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK, IContentDescriptionExtended.DETECTED_CHARSET, IContentDescriptionExtended.UNSUPPORTED_CHARSET, IContentDescriptionExtended.APPROPRIATE_DEFAULT, IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, IContentDescriptionForJSP.LANGUAGE_ATTRIBUTE, IContentDescriptionForJSP.CONTENT_FAMILY_ATTRIBUTE};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.InputStream,
	 *      org.eclipse.core.runtime.content.IContentDescription)
	 */
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		int result = IContentDescriber.INVALID;

		// if discription is null, we are just being asked to
		// assess contents validity
		if (description != null) {
			result = calculateSupportedOptions(contents, description);
		}
		else {
			result = determineValidity(contents);
		}

		return result;
	}

	private int determineValidity(InputStream contents) {
		// There's little to prove, via contents, that
		// a file is JSP, so always return interminant, and
		// let filetypes decide.
		return IContentDescriber.INDETERMINATE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.content.ITextContentDescriber#describe(java.io.Reader,
	 *      org.eclipse.core.runtime.content.IContentDescription)
	 */
	public int describe(Reader contents, IContentDescription description) throws IOException {
		int result = IContentDescriber.INVALID;

		// if discription is null, we are just being asked to
		// assess contents validity
		if (description != null) {
			result = calculateSupportedOptions(contents, description);
		}
		else {
			result = determineValidity(contents);
		}

		return result;
	}

	private int determineValidity(Reader contents) {
		// There's little to prove, via contents, that
		// a file is JSP, so always return interminant, and
		// let filetypes decide.
		return IContentDescriber.INDETERMINATE;
	}

	public QualifiedName[] getSupportedOptions() {
		return SUPPORTED_OPTIONS;
	}

	private int calculateSupportedOptions(InputStream contents, IContentDescription description) throws IOException {
		int result = IContentDescriber.INDETERMINATE;
		if (isRelevent(description)) {
			IResourceCharsetDetector detector = getDetector();
			detector.set(contents);
			handleCalculations(description, detector);
			result = IContentDescriber.VALID;
		}
		return result;
	}

	/**
	 * @param contents
	 * @param description
	 * @throws IOException
	 */
	private int calculateSupportedOptions(Reader contents, IContentDescription description) throws IOException {
		int result = IContentDescriber.INDETERMINATE;
		if (isRelevent(description)) {
			IResourceCharsetDetector detector = getDetector();
			detector.set(contents);
			handleCalculations(description, detector);
			result = IContentDescriber.VALID;
		}
		return result;
	}

	private IResourceCharsetDetector getDetector() {
		return new JSPResourceEncodingDetector();
	}

	private void handleCalculations(IContentDescription description, IResourceCharsetDetector detector) throws IOException {
		// handle standard ones first, to be sure detector processes
		handleStandardCalculations(description, detector);
		// now do those specific for JSPs
		// note: detector should always be of correct instance, but we'll
		// check, for now.
		if (detector instanceof JSPResourceEncodingDetector) {
			JSPResourceEncodingDetector jspDetector = (JSPResourceEncodingDetector) detector;
			String language = jspDetector.getLanguage();
			if (language != null && language.length() > 0) {
				description.setProperty(IContentDescriptionForJSP.LANGUAGE_ATTRIBUTE, language);
			}
			/*
			 * content type is literally the content type that's in the page
			 * directive
			 */
			String contentTypeAttribute = jspDetector.getContentType();
			if (contentTypeAttribute != null && contentTypeAttribute.length() > 0) {
				description.setProperty(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, contentTypeAttribute);
			}
			/*
			 * content family is the general class of content, when its
			 * different from what's defined by content type
			 */
			if (jspDetector.isXHTML() || jspDetector.isWML()) {
				// ISSUE: long term this logic and value should be contributed by extension point
				description.setProperty(IContentDescriptionForJSP.CONTENT_FAMILY_ATTRIBUTE, ContentTypeFamilyForHTML.HTML_FAMILY);
			}


		}
	}

	private void handleDetectedSpecialCase(IContentDescription description, Object detectedCharset, Object javaCharset) {
		if (detectedCharset != null) {
			// Once we detected a charset, we should set the property even
			// though it's the same as javaCharset
			// because there are clients that rely on this property to
			// determine if the charset is actually detected in file or not.
			description.setProperty(IContentDescriptionExtended.DETECTED_CHARSET, detectedCharset);
		}
	}

	/**
	 * @param description
	 * @param detector
	 * @throws IOException
	 */
	private void handleStandardCalculations(IContentDescription description, IResourceCharsetDetector detector) throws IOException {
		// note: if we're asked for one, we set them all. I need to be sure if
		// called
		// mulitiple times (one for each, say) that we don't waste time
		// processing same
		// content again.
		EncodingMemento encodingMemento = ((JSPResourceEncodingDetector) detector).getEncodingMemento();
		// TODO: I need to verify to see if this BOM work is always done
		// by text type.
		Object detectedByteOrderMark = encodingMemento.getUnicodeBOM();
		if (detectedByteOrderMark != null) {
			Object existingByteOrderMark = description.getProperty(IContentDescription.BYTE_ORDER_MARK);
			// not sure why would ever be different, so if is different, may
			// need to "push" up into base.
			if (!detectedByteOrderMark.equals(existingByteOrderMark))
				description.setProperty(IContentDescription.BYTE_ORDER_MARK, detectedByteOrderMark);
		}


		if (!encodingMemento.isValid()) {
			// note: after setting here, its the mere presence of
			// IContentDescriptionExtended.UNSUPPORTED_CHARSET
			// in the resource's description that can be used to determine if
			// invalid
			// in those cases, the "detected" property contains an
			// "appropriate default" to use.
			description.setProperty(IContentDescriptionExtended.UNSUPPORTED_CHARSET, encodingMemento.getInvalidEncoding());
			description.setProperty(IContentDescriptionExtended.APPROPRIATE_DEFAULT, encodingMemento.getAppropriateDefault());
		}

		Object detectedCharset = encodingMemento.getDetectedCharsetName();
		Object javaCharset = encodingMemento.getJavaCharsetName();

		// we always include detected, if its different than java
		handleDetectedSpecialCase(description, detectedCharset, javaCharset);

		if (javaCharset != null) {
			Object existingCharset = description.getProperty(IContentDescription.CHARSET);
			if (javaCharset.equals(existingCharset)) {
				handleDetectedSpecialCase(description, detectedCharset, javaCharset);
			}
			else {
				// we may need to add what we found, but only need to add
				// if different from the default.
				Object defaultCharset = detector.getSpecDefaultEncoding();
				if (defaultCharset != null) {
					if (!defaultCharset.equals(javaCharset)) {
						description.setProperty(IContentDescription.CHARSET, javaCharset);
					}
				}
				else {
					// assuming if there is no spec default, we always need to
					// add, I'm assuming
					description.setProperty(IContentDescription.CHARSET, javaCharset);
				}
			}
		}

	}

	/**
	 * @param description
	 * @return
	 */
	private boolean isRelevent(IContentDescription description) {
		boolean result = false;
		if (description == null)
			result = false;
		else if (description.isRequested(IContentDescription.BYTE_ORDER_MARK))
			result = true;
		else if (description.isRequested(IContentDescription.CHARSET))
			result = true;
		else if (description.isRequested(IContentDescriptionExtended.APPROPRIATE_DEFAULT))
			result = true;
		else if (description.isRequested(IContentDescriptionExtended.DETECTED_CHARSET))
			result = true;
		else if (description.isRequested(IContentDescriptionExtended.UNSUPPORTED_CHARSET))
			result = true;
		else if (description.isRequested(IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE))
			result = true;
		else if (description.isRequested(IContentDescriptionForJSP.LANGUAGE_ATTRIBUTE))
			result = true;
		return result;
	}
}