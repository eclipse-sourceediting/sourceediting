/*******************************************************************************
 * Copyright (c) 2009  Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Carver (bug 264788) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.util;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.content.*;

/**
 * XMLContetType handles the gathering of XML content type related information
 * between the platforms xml content type and WTP's specific version.
 *  
 * @author David Carver
 * @since 1.0
 *
 */
public class XMLContentType {
	private static final String XMLSOURCE_CONTENTTYPE = "org.eclipse.wst.xml.core.xmlsource"; //$NON-NLS-1$
	private static final String PLATFORM_XMLSOURCE_CONTENTTYPE = "org.eclipse.core.runtime.xml"; //$NON-NLS-1$

	private IContentTypeManager contentTypeManager = null;

	public XMLContentType() {
		contentTypeManager = Platform.getContentTypeManager();
		IContentType contentType;
	}
	
	/**
	 * Returns all the extensions associated with an XML Content Type. 
	 * @return The array of file extensions
	 * @since 1.0
	 */
	public String[] getFileExtensions() {
		IContentType[] contentTypes = getAllXMLContentTypes();
		ArrayList<String> xmlFileExtensions = new ArrayList<String>();
		if (contentTypes.length > 0) {
			for (int cnt = 0; cnt < contentTypes.length; cnt++) {
				String[] exts = getFileSpecs(contentTypes[cnt]);
				if (exts != null) {
					for (int sub = 0; sub < exts.length; sub++) {
						if (!xmlFileExtensions.contains(exts[sub])) {
							xmlFileExtensions.add(exts[sub]);
						}
					}
				}
			}
		}
		
		String[] fileExtensions = new String[xmlFileExtensions.size()];
		xmlFileExtensions.toArray(fileExtensions);
		return fileExtensions;
	}	
	
	private IContentType[] getAllXMLContentTypes() {
		ArrayList<IContentType> arrayList = new ArrayList<IContentType>(Arrays.asList(contentTypeManager.getAllContentTypes()));
		ArrayList<IContentType> copyContents = (ArrayList<IContentType>)arrayList.clone();
	
		for(IContentType contentType : arrayList) {
			if (!isXMLContentType(contentType)) {
					copyContents.remove(contentType);
			}
		}
		IContentType[] contentTypes = new IContentType[copyContents.size()];
		copyContents.toArray(contentTypes);
		return contentTypes;
	}

	private boolean isXMLContentType(IContentType contentType) {
		return contentType.getId().equals(PLATFORM_XMLSOURCE_CONTENTTYPE) ||
			  contentType.getId().equals(XMLSOURCE_CONTENTTYPE) ||
			  isKindOfWTPXML(contentType) ||
			  isKindOfXMLSource(contentType);
	}
	
	private boolean isKindOfXMLSource(IContentType contentType) {
		IContentType platformContentType = contentTypeManager.getContentType(PLATFORM_XMLSOURCE_CONTENTTYPE);
		boolean returnValue = false;
		if (platformContentType != null) {
			returnValue = contentType.isKindOf(platformContentType);
		}
		return returnValue;
	}
	
	private boolean isKindOfWTPXML(IContentType contentType) {
		IContentType wtpContentType = contentTypeManager.getContentType(XMLSOURCE_CONTENTTYPE);
		boolean returnValue = false;
		if (wtpContentType != null) {
			returnValue = contentType.isKindOf(wtpContentType);
		}
		return returnValue;
	}

	private String[] getFileSpecs(IContentType contentType) {
		return contentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
	}	
}
