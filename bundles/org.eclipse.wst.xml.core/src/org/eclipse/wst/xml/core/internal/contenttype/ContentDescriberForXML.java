/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contenttype;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.eclipse.wst.sse.core.internal.encoding.EncodingMemento;
import org.eclipse.wst.sse.core.internal.encoding.IContentDescriptionExtended;
import org.eclipse.wst.sse.core.internal.encoding.IResourceCharsetDetector;


public final class ContentDescriberForXML implements ITextContentDescriber {
	private final static QualifiedName[] SUPPORTED_OPTIONS = {IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK, IContentDescriptionExtended.DETECTED_CHARSET, IContentDescriptionExtended.UNSUPPORTED_CHARSET, IContentDescriptionExtended.APPROPRIATE_DEFAULT};
	/**
	 * <code>restrictedMode</code> is used just for testing/experiments.
	 * 
	 * If in restrictedMode, our "custom" contentType is seen as valid only in
	 * cases that the platform's standard one does not cover.
	 */
	private final static boolean restrictedMode = true;
	private IResourceCharsetDetector getDetector() {
			return new XMLResourceEncodingDetector();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.InputStream,
	 *      org.eclipse.core.runtime.content.IContentDescription)
	 */
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		// for this special case, always assume invalid, unless
		// our special circumstances are met.
		int result = IContentDescriber.INVALID;

		if (description == null) {
			// purely request for validty
			result = determineValidity(result, contents);
		}
		else {
			result = calculateSupportedOptions(result, contents, description);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.content.ITextContentDescriber#describe(java.io.Reader,
	 *      org.eclipse.core.runtime.content.IContentDescription)
	 */
	public int describe(Reader contents, IContentDescription description) throws IOException {
		// for this special case, always assume invalid, unless
		// our special circumstances are met.
		int result = IContentDescriber.INVALID;

		if (description == null) {
			// purely request for validty
			result = determineValidity(result, contents);
		}
		else {
			result = calculateSupportedOptions(result, contents, description);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.content.IContentDescriber#getSupportedOptions()
	 */
	public QualifiedName[] getSupportedOptions() {

		return SUPPORTED_OPTIONS;
	}

	private int calculateSupportedOptions(int result, InputStream contents, IContentDescription description) throws IOException {
		int returnResult = result;
		if (isRelevent(description)) {
			IResourceCharsetDetector detector = getDetector();
			contents.reset();
			detector.set(contents);
			returnResult = handleCalculations(result, description, detector);
		}
		return returnResult;
	}

	private int determineValidity(int result, InputStream contents) throws IOException {
		int returnResult = result;
		IResourceCharsetDetector detector = getDetector();
		contents.reset();
		detector.set(contents);
		returnResult = determineValidity(detector, returnResult);
		return returnResult;
	}
	private int determineValidity(int result, Reader contents) throws IOException {
		int returnResult = result;
		IResourceCharsetDetector detector = getDetector();
		contents.reset();
		detector.set(contents);
		returnResult = determineValidity(detector, returnResult);
		return returnResult;
	}
	/**
	 * @param contents
	 * @param description
	 * @throws IOException
	 */
	private int calculateSupportedOptions(int result, Reader contents, IContentDescription description) throws IOException {
		int returnResult = result;
		if (isRelevent(description)) {
			IResourceCharsetDetector detector = getDetector();
			detector.set(contents);
			returnResult = handleCalculations(result, description, detector);
		}
		return returnResult;
	}

	private void handleDetectedSpecialCase(IContentDescription description, Object detectedCharset, Object javaCharset) {
		// since equal, we don't need to add, but if our detected version is
		// different than
		// javaCharset, then we should add it. This will happen, for example,
		// if there's
		// differences in case, or differences due to override properties
		if (detectedCharset != null) {
			// if (!detectedCharset.equals(javaCharset)) {
			// description.setProperty(IContentDescriptionExtended.DETECTED_CHARSET,
			// detectedCharset);
			// }

			// Once we detected a charset, we should set the property even
			// though it's the same as javaCharset
			// because there are clients that rely on this property to
			// determine if the charset is actually detected in file or not.
			description.setProperty(IContentDescriptionExtended.DETECTED_CHARSET, detectedCharset);
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
		return result;
	}

	/**
	 * @param description
	 * @param detector
	 * @throws IOException
	 */
	private int handleCalculations(int result, IContentDescription description, IResourceCharsetDetector detector) throws IOException {
		int returnResult = result;
		EncodingMemento encodingMemento = ((XMLResourceEncodingDetector) detector).getEncodingMemento();
		if (description != null) {
			// TODO: I need to verify to see if this BOM work is always done
			// by text type.
			Object detectedByteOrderMark = encodingMemento.getUnicodeBOM();
			if (detectedByteOrderMark != null) {
				Object existingByteOrderMark = description.getProperty(IContentDescription.BYTE_ORDER_MARK);
				// not sure why would ever be different, so if is different,
				// may
				// need to "push" up into base.
				if (!detectedByteOrderMark.equals(existingByteOrderMark))
					description.setProperty(IContentDescription.BYTE_ORDER_MARK, detectedByteOrderMark);
			}


			if (!encodingMemento.isValid()) {
				// note: after setting here, its the mere presence of
				// IContentDescriptionExtended.UNSUPPORTED_CHARSET
				// in the resource's description that can be used to determine
				// if invalid in those cases, the "detected" property contains
				// an "appropriate default" to use.
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
					// if different from the default
					Object defaultCharset = detector.getSpecDefaultEncoding();
					if (defaultCharset != null) {
						if (!defaultCharset.equals(javaCharset)) {
							description.setProperty(IContentDescription.CHARSET, javaCharset);
						}
					}
					else {
						// assuming if there is no spec default, we always
						// need to add.
						// TODO: this is probably a dead branch in current
						// code, should re-examine for removal.
						description.setProperty(IContentDescription.CHARSET, javaCharset);
					}
				}
			}
		}

		returnResult = determineValidity(detector, returnResult);
		return returnResult;
	}

	private int determineValidity(IResourceCharsetDetector detector, int returnResult) {
		// we always expect XMLResourceEncodingDetector, but just to make safe
		// cast.
		if (detector instanceof XMLResourceEncodingDetector) {
			XMLResourceEncodingDetector xmlResourceDetector = (XMLResourceEncodingDetector) detector;
			if (xmlResourceDetector.isDeclDetected()) {
				if (restrictedMode) {
					// if there is no initial whitespace, then platform's
					// default one will do.
					if (xmlResourceDetector.hasInitialWhiteSpace()) {
						returnResult = IContentDescriber.VALID;
					}
				}
				else {
					returnResult = IContentDescriber.VALID;
				}
			}
		}
		return returnResult;
	}

}
