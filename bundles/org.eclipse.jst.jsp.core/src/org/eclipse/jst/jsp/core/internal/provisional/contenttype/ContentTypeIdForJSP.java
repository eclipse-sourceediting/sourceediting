/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.internal.provisional.contenttype;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

/**
 * This class, with its one field, is a convience to provide compile-time
 * safety when refering to a contentType ID. The value of the contenttype id
 * field must match what is specified in plugin.xml file.
 */

public class ContentTypeIdForJSP {
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_JSP = getConstantString();
	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_JSPFRAGMENT = getFragmentConstantString();

	/**
	 * The value of the contenttype id field must match what is specified in
	 * plugin.xml file. Note: this value is intentially set with default
	 * protected method so it will not be inlined.
	 */
	public final static String ContentTypeID_JSPTAG = getTagConstantString();

	private static char[][] JSP_EXTENSIONS;
	private static String JSP = "jsp";

	/**
	 * Don't allow instantiation.
	 */
	private ContentTypeIdForJSP() {
		super();
	}

	static String getConstantString() {
		return "org.eclipse.jst.jsp.core.jspsource"; //$NON-NLS-1$
	}
	
	static String getFragmentConstantString() {
		return "org.eclipse.jst.jsp.core.jspfragmentsource"; //$NON-NLS-1$
	}
	
	static String getTagConstantString() {
		return "org.eclipse.jst.jsp.core.tagsource"; //$NON-NLS-1$
	}

	/**
	 * @param fileName
	 * @return the first index within an array of filename extensions that
	 *         denote the JSP content type or a subtype and match the
	 *         extension of the given filename
	 */
	public static int indexOfJSPExtension(String fileName) {
		int fileNameLength = fileName.length();
		char[][] jspExtensions = getJSPExtensions();
		extensions: for (int i = 0, length = jspExtensions.length; i < length; i++) {
			char[] extension = jspExtensions[i];
			int extensionLength = extension.length;
			int extensionStart = fileNameLength - extensionLength;
			int dotIndex = extensionStart - 1;
			if (dotIndex < 0) continue;
			if (fileName.charAt(dotIndex) != '.') continue;
			for (int j = 0; j < extensionLength; j++) {
				if (fileName.charAt(extensionStart + j) != extension[j])
					continue extensions;
			}
			return dotIndex;
		}
		return -1;
	}
	
	/**
	 * @return an array of all filename extensions that are of the JSP content
	 *         type or one of its subtypes
	 */
	public static char[][] getJSPExtensions() {
		if (JSP_EXTENSIONS == null) {
			IContentType jspContentType = Platform.getContentTypeManager().getContentType(getConstantString());
			HashSet fileExtensions = new HashSet();
			// content types derived from JSP content type should be included (https://bugs.eclipse.org/bugs/show_bug.cgi?id=121715)
			IContentType[] contentTypes = Platform.getContentTypeManager().getAllContentTypes();
			for (int i = 0, length = contentTypes.length; i < length; i++) {
				if (contentTypes[i].isKindOf(jspContentType)) { // note that jspContentType.isKindOf(jspContentType) == true
					String[] fileExtension = contentTypes[i].getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
					for (int j = 0; j < fileExtension.length; j++) {
						fileExtensions.add(fileExtension[j]);
					}
				}
			}
			int length = fileExtensions.size();
			// note that file extensions contains "jsp"
			char[][] extensions = new char[length][];
			extensions[0] = JSP.toCharArray(); // ensure that "jsp" is first
			int index = 1;
			Iterator iterator = fileExtensions.iterator();
			while (iterator.hasNext()) {
				String fileExtension = (String) iterator.next();
				if (JSP.equalsIgnoreCase(fileExtension))
					continue;
				extensions[index++] = fileExtension.toCharArray();
			}
			JSP_EXTENSIONS = extensions;
		}
		return JSP_EXTENSIONS;
	}

}
