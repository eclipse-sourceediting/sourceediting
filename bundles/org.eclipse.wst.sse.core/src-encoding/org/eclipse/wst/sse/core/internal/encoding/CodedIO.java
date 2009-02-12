/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.encoding;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.wst.sse.core.internal.encoding.util.Assert;
import org.eclipse.wst.sse.core.internal.encoding.util.Logger;
import org.osgi.framework.Bundle;


public abstract class CodedIO {

	private final boolean DEBUG = false;
	
	public static final int MAX_BUF_SIZE = 1024 * 8;

	public static final int MAX_MARK_SIZE = MAX_BUF_SIZE;

	public static final String NO_SPEC_DEFAULT = "NoSpecDefault"; //$NON-NLS-1$

	private static Properties overridenCharsets = null;

	/**
	 * <p>
	 * There are two well known understood cases where the standard/default
	 * Java Mappings are not sufficient. (Thanks to Hirotaka Matsumoto for
	 * providing these two). I believe there are others that individual
	 * customers have requested to override on a case by case basis, but I've
	 * lost the details. TODO-future: document some of those use-cases.
	 * </p>
	 * <ul>
	 * <li>ISO-8859-8-I</li>
	 * <p>
	 * In the code conversion point of view, ISO-9959-8 and ISO-8859-8-I are
	 * the same. However. the representation on the browser is different. (
	 * It's very very hard to explain this into the words, but once you will
	 * see, you will understand it :) Many BiDi HTML/JSPs use ISO-8859-8-I in
	 * META/page directive. So WSAD needs to support this encoding.
	 * </p>
	 * <li>X-SJIS</li>
	 * <p>
	 * Because Mosaic/Navigator 2.0 supported only X-SJIS/X-EUC-JP, lots of
	 * old HTML files used X-SJIS/X-EUC-JP so that the customers still want us
	 * to support this code conversion for HTML files.
	 * </p>
	 * </ul>
	 * 
	 * @param detectedCharsetName
	 * @return the detectedCharsetName, if no overrides, otherwise the charset
	 *         name that should be used instead of detectedCharsetName
	 */
	/**
	 * This method is deliberatly 'default access' since clients should not
	 * need to access this information directly.
	 */
	static public String checkMappingOverrides(String detectedCharsetName) {
		// This method MUST return what was passed in, if
		// there are no
		// overrides.
		String result = detectedCharsetName;
		String newResult = getOverridenCharsets().getProperty(detectedCharsetName);
		if (newResult != null) {
			result = newResult;
		}
		return result;
	}

	/**
	 * Note: once this instance is created, trace info still needs to be
	 * appended by caller, depending on the context its created.
	 */
	public static EncodingMemento createEncodingMemento(byte[] detectedBom, String javaCharsetName, String detectedCharsetName, String unSupportedName, String specDefaultEncoding, String reason) {
		EncodingMemento result = new EncodingMemento();
		result.setJavaCharsetName(javaCharsetName);
		result.setDetectedCharsetName(detectedCharsetName);
		// TODO: if detectedCharset and spec default is
		// null, need to use "work
		// bench based" defaults.
		if (specDefaultEncoding == null)
			result.setAppropriateDefault(NO_SPEC_DEFAULT);
		else
			result.setAppropriateDefault(specDefaultEncoding);
		if (unSupportedName != null) {
			result.setInvalidEncoding(unSupportedName);
		}
		// check if valid
		try {
			Charset.isSupported(javaCharsetName);
		} catch (IllegalCharsetNameException e) {
			result.setInvalidEncoding(javaCharsetName);
		}

		// check UTF83ByteBOMUsed and UnicodeStream
		if (detectedBom != null) {
			if (detectedBom.length == 2)
				result.setUnicodeStream(true);
			else if (detectedBom.length == 3)
				result.setUTF83ByteBOMUsed(true);
			result.setUnicodeBOM(detectedBom);
		}
		return result;
	}

	/**
	 * Note: once this instance is created, trace info still needs to be
	 * appended by caller, depending on the context its created.
	 */
	public static EncodingMemento createEncodingMemento(String detectedCharsetName) {
		return createEncodingMemento(detectedCharsetName, null);
	}

	/**
	 * Note: once this instance is created, trace info still needs to be
	 * appended by caller, depending on the context its created.
	 */
	public static EncodingMemento createEncodingMemento(String detectedCharsetName, String reason) {
		return createEncodingMemento(detectedCharsetName, reason, null);
	}

	/**
	 * Note: once this instance is created, trace info still needs to be
	 * appended by caller, depending on the context its created.
	 */
	public static EncodingMemento createEncodingMemento(String detectedCharsetName, String reason, String specDefaultEncoding) {
		EncodingMemento result = new EncodingMemento();
		result = new EncodingMemento();
		String javaCharset = getAppropriateJavaCharset(detectedCharsetName);
		result.setJavaCharsetName(javaCharset);
		result.setDetectedCharsetName(detectedCharsetName);
		// TODO: if detectedCharset and spec default is
		// null, need to use "work
		// bench based" defaults.
		if (specDefaultEncoding == null)
			result.setAppropriateDefault(NO_SPEC_DEFAULT);
		else
			result.setAppropriateDefault(specDefaultEncoding);
		// check if valid
		try {
			Charset.isSupported(javaCharset);
		} catch (IllegalCharsetNameException e) {
			result.setInvalidEncoding(javaCharset);
		}

		return result;
	}

	/**
	 * This method can return null, if invalid charset name (in which case
	 * "appropriateDefault" should be used, if a name is really need for some
	 * "save anyway" cases).
	 * 
	 * @param detectedCharsetName
	 * @return
	 */
	public static String getAppropriateJavaCharset(String detectedCharsetName) {
		// we don't allow null argument (or risk NPE or
		// IllegalArgumentException later at several
		// points.
		Assert.isNotNull(detectedCharsetName, "illegal charset argument. it can not be null"); //$NON-NLS-1$
		String result = detectedCharsetName;
		// 1. Check explicit mapping overrides from
		// property file
		result = CodedIO.checkMappingOverrides(detectedCharsetName);
		// 2. Use the "canonical" name from JRE mappings
		// Note: see Charset JavaDoc, the name you get one
		// with can be alias,
		// the name you get back is "standard" name.
		Charset javaCharset = null;
		// Note: this will immediatly throw
		// "UnsuppotedCharsetException" if it
		// invalid. Issue: Is it more client friendly to
		// eat that exception and return null?
		javaCharset = Charset.forName(result);
		if (javaCharset != null) {
			result = javaCharset.name();
		}
		return result;
	}

	/**
	 * @return Returns the overridenCharsets.
	 */
	private static Properties getOverridenCharsets() {
		if (overridenCharsets == null) {
			overridenCharsets = new Properties();
			Bundle keyBundle = Platform.getBundle(ICodedResourcePlugin.ID);
			IPath keyPath = new Path("config/override.properties"); //$NON-NLS-1$
			URL location = Platform.find(keyBundle, keyPath);
			InputStream propertiesInputStream = null;
			try {
				propertiesInputStream = location.openStream();
				overridenCharsets.load(propertiesInputStream);
			} catch (IOException e) {
				// if can't read, just assume there's no
				// overrides
				// and repeated attempts will not occur,
				// since they
				// will be represented by an empty
				// Properties object
			}
		}
		return overridenCharsets;
	}

	/**
	 * This class need not be instantiated (though its subclasses can be).
	 */
	protected CodedIO() {
		super();
	}

	protected EncodingMemento createMemento(IContentDescription contentDescription) {
		EncodingMemento result;
		String appropriateDefault = contentDescription.getContentType().getDefaultCharset();
		String detectedCharset = (String) contentDescription.getProperty(IContentDescriptionExtended.DETECTED_CHARSET);
		String unSupportedCharset = (String) contentDescription.getProperty(IContentDescriptionExtended.UNSUPPORTED_CHARSET);
		String javaCharset = contentDescription.getCharset();
		// integrity checks for debugging
		if (javaCharset == null) {
			Logger.log(Logger.INFO_DEBUG, "charset equaled null!"); //$NON-NLS-1$
		} else if (javaCharset.length() == 0) {
			Logger.log(Logger.INFO_DEBUG, "charset equaled emptyString!"); //$NON-NLS-1$
		}
		byte[] BOM = (byte[]) contentDescription.getProperty(IContentDescription.BYTE_ORDER_MARK);
		//result = (EncodingMemento)
		// contentDescription.getProperty(IContentDescriptionExtended.ENCODING_MEMENTO);
		result = createEncodingMemento(BOM, javaCharset, detectedCharset, unSupportedCharset, appropriateDefault, null);
		if (!result.isValid()) {
			result.setAppropriateDefault(appropriateDefault);
			// integrity check for debugging "invalid" cases.
			// the apprriate default we have, should equal what's in the
			// detected field. (not sure this is always required)
			if (DEBUG && appropriateDefault != null && !appropriateDefault.equals(detectedCharset)) {
				Logger.log(Logger.INFO_DEBUG, "appropriate did not equal detected, as expected for invalid charset case"); //$NON-NLS-1$
			}
		}
		return result;
	}
}
