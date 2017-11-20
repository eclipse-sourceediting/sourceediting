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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

public class NonContentBasedEncodingRules {

	private static final String getJavaPlatformDefaultEncoding() {
		String enc = System.getProperty("file.encoding"); //$NON-NLS-1$
		// return blank as null
		if (enc != null && enc.trim().length() == 0) {
			enc = null;
		}
		return enc;
	}


	public static String getUserSpecifiedDefaultForContentType(IFile iFile) {
		String enc = null;
		
		IContentType contentType = null;
		try {
			contentType = iFile.getContentDescription().getContentType();
			
			// first try to get base's default encoding for content type 
			if (contentType != null) {
				enc = contentType.getDefaultCharset();
			}

			// next try to get sse's default encoding for content type
			if (enc == null || enc.trim().length() == 0) {
				enc = ContentBasedPreferenceGateway.getPreferencesString(contentType, CommonEncodingPreferenceNames.INPUT_CODESET);
			}
			
			// return blank as null
			if (enc != null && enc.trim().length() == 0) {
				enc = null;
			}
		} catch (CoreException e) {
			// if core exception occurs, assume no preference!
			enc = null;
		}
		return enc;
	}

	public static String getUserSpecifiedDefaultForContentType(String contentTypeId) {
		String enc = null;
		
		// first try to get base's default encoding for content type
		IContentType contentType = Platform.getContentTypeManager().getContentType(contentTypeId);
		if (contentType != null) {
			enc = contentType.getDefaultCharset();
		}
		
		// next try to get sse's default encoding for content type
		if (enc == null || enc.trim().length() == 0) {
			enc = ContentBasedPreferenceGateway.getPreferencesString(contentTypeId, CommonEncodingPreferenceNames.INPUT_CODESET);
		}
		
		// return blank as null
		if (enc != null && enc.trim().length() == 0) {
			enc = null;
		}
		return enc;
	}

	private static final String getWorkbenchSpecifiedDefaultEncoding() {
		ResourcesPlugin resourcePlugin = ResourcesPlugin.getPlugin();
		String enc = resourcePlugin.getPluginPreferences().getString(ResourcesPlugin.PREF_ENCODING);
		// return blank as null
		if (enc != null && enc.trim().length() == 0) {
			enc = null;
		}
		return enc;
	}

	/**
	 * @param specDefault
	 *            This is the default charset name that would ordinarily be
	 *            used for a particular type of content. Null may be
	 *            specififed for those types with no spec default. If the spec
	 *            default is known (and passed in), then it will be returned
	 *            after being checked to see if there's be any user specified
	 *            "override" for that charset (which would be rare). In other
	 *            words, if the spec is known, there's little reason to use
	 *            this method.
	 * @return the charset that should be used according to the rules
	 *         established by this class.
	 */
	public static final String useDefaultNameRules(String specDefault) {
		String enc = null;
		String result = null;
		enc = specDefault;
		if (enc != null) {
			result = enc;
		} else {
			enc = ContentTypeEncodingPreferences.getUserSpecifiedDefaultEncodingPreference();
			if (enc != null && enc.trim().length() > 0) {
				result = enc.trim();
			} else {
				if (enc == null || enc.trim().length() == 0) {
					enc = getWorkbenchSpecifiedDefaultEncoding();
					if (enc != null) {
						result = enc.trim();
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
		result = CodedIO.checkMappingOverrides(result);
		return result;
	}

	private NonContentBasedEncodingRules() {
		super();
	}
}
