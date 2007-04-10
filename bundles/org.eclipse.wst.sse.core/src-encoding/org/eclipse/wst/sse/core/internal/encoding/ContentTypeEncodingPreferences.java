/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.wst.sse.core.internal.encoding.util.Assert;


/**
 * A convenience class to statically get preferenences.
 */

public abstract class ContentTypeEncodingPreferences {

	// actually a null/empty string also means use workbench default so this
	// constant might not really be necessary
	public static final String WORKBENCH_DEFAULT = "WORKBENCH_DEFAULT"; //$NON-NLS-1$


	private static final String getJavaPlatformDefaultEncoding() {
		// note: its important to use this system property,
		// instead
		// of
		// ByteToCharConverter.getDefault().getCharacterEncoding()
		// inorder to handle changes "on the fly". the
		// ByteToCharConverter
		// default is apparently set only when VM starts.
		// There's not really any "cusomter scnererios"
		// that change the
		// default encoding "on the fly", but its at least
		// used during
		// our automated tests.
		String enc = System.getProperty("file.encoding"); //$NON-NLS-1$
		// return blank as null
		if (enc != null && enc.trim().length() == 0) {
			enc = null;
		}
		return enc;
	}


	public static final String getPreferredNewLineDelimiter(String contentTypeId) {
		String result = null;
		String newLineCode = null;
		newLineCode = ContentBasedPreferenceGateway.getPreferencesString(contentTypeId, CommonEncodingPreferenceNames.END_OF_LINE_CODE);
		if (newLineCode == null)
			result = null;
		else if (newLineCode.equals(CommonEncodingPreferenceNames.CR))
			result = CommonEncodingPreferenceNames.STRING_CR;
		else if (newLineCode.equals(CommonEncodingPreferenceNames.LF))
			result = CommonEncodingPreferenceNames.STRING_LF;
		else if (newLineCode.equals(CommonEncodingPreferenceNames.CRLF))
			result = CommonEncodingPreferenceNames.STRING_CRLF;
		return result;
	}

	/**
	 * Returns current output encoding preference for contentTypeIdentifier
	 * (unique IANA encoding)
	 */
	public static final String getUserPreferredCharsetName(String contentTypeId) {
		String prefEncoding = ContentBasedPreferenceGateway.getPreferencesString(contentTypeId, CommonEncodingPreferenceNames.OUTPUT_CODESET);
		String encoding = prefEncoding;
		// get workbench encoding preference if preference
		// requests it
		if (prefEncoding == null || prefEncoding.trim().length() == 0 || prefEncoding.equals(ContentTypeEncodingPreferences.WORKBENCH_DEFAULT)) {
			encoding = ContentTypeEncodingPreferences.getWorkbenchPreferredCharsetName();
		}
		return encoding;
	}

	/**
	 * Utility method to get specified preference. Subclasses can't override,
	 * since we expect this to work in a consistent way. Note: this is
	 * specific to HTML and CSS and is intended to supply a "default spec"
	 * other than the workbench platform's default, only for those cases where
	 * there is no encoding specified anywhere else, e.g. in the file, or as a
	 * file or folder property.
	 */
	public static final String getUserSpecifiedDefaultEncodingPreference() {
		String ContentTypeID_HTML = "org.eclipse.wst.html.core.htmlsource"; //$NON-NLS-1$
		return getUserSpecifiedDefaultEncodingPreference(ContentTypeID_HTML);
	}

	public static final String getUserSpecifiedDefaultEncodingPreference(String contentTypeID) {
		String enc = null;

		// first try to get base's default encoding for content type
		IContentType contentType = Platform.getContentTypeManager().getContentType(contentTypeID);
		if (contentType != null) {
			enc = contentType.getDefaultCharset();
		}
		
		// next try to get sse's default encoding for content type
		if (enc == null || enc.trim().length() == 0) {
			enc = ContentBasedPreferenceGateway.getPreferencesString(contentTypeID, CommonEncodingPreferenceNames.INPUT_CODESET);
		}
		
		// next, just try and use workbench encoding
		if (enc == null || enc.trim().length() == 0) {
			enc = getWorkbenchSpecifiedDefaultEncoding();
		}
		
		// return blank as null
		if (enc != null && enc.trim().length() == 0) {
			enc = null;
		}
		return enc;
	}

	/**
	 * Returns Workbench encoding preference. Note: if workbench encoding is
	 * null, platform encoding will be returned.
	 */
	private static final String getWorkbenchPreferredCharsetName() {
		String charset = ResourcesPlugin.getEncoding();
		charset = CommonCharsetNames.getIanaPreferredCharsetName(charset);
		return charset;
	}

	/**
	 * Returns Workbench encoding preference. Will return null if none
	 * specified.
	 */
	private static final String getWorkbenchSpecifiedDefaultEncoding() {
		ResourcesPlugin resourcePlugin = ResourcesPlugin.getPlugin();
		String enc = resourcePlugin.getPluginPreferences().getString(ResourcesPlugin.PREF_ENCODING);
		// return blank as null
		if (enc != null && enc.trim().length() == 0) {
			enc = null;
		}
		return enc;
	}

	public static final String useDefaultNameRules(IResourceCharsetDetector encodingProvider) {
		String result = null;
		String enc = null;
		enc = encodingProvider.getSpecDefaultEncoding();
		if (enc != null) {
			result = enc;
		} else {
			enc = getUserSpecifiedDefaultEncodingPreference();
			if (enc != null && enc.trim().length() > 0) {
				result = enc;
			} else {
				if (enc == null || enc.trim().length() == 0) {
					enc = getWorkbenchSpecifiedDefaultEncoding();
					if (enc != null) {
						result = enc;
					}
				}
				if (enc == null || enc.trim().length() == 0) {
					enc = getJavaPlatformDefaultEncoding();
					// enc should never be null (but we'll
					// check anyway)
					if (enc != null) {
						result = enc;
					}
				}
			}
		}
		Assert.isNotNull(enc, "post condition invalid"); //$NON-NLS-1$
		result = CodedIO.checkMappingOverrides(enc);
		return result;
	}

}
