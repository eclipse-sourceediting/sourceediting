/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.contentmodel;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.wst.sse.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.sse.core.internal.encoding.EncodingHelper;

public class CMDocumentEncodingHelper {
	protected static void setEncodingInfo(CMDocument cmDocument, String uri) {
		if (cmDocument != null) {
			uri = addImpliedFileProtocol(uri);
			InputStream inputStream = null;
			try {
				URL url = new URL(uri);
				inputStream = url.openStream();
				String[] encodingInfo = (String[]) cmDocument.getProperty("encodingInfo");
				if (encodingInfo != null) {
					// if (Display.getCurrent() != null)
					// {
					updateFromEncodingHelper(inputStream, encodingInfo);
					// }
					// else
					// {
					// encodingInfo[0] = "UTF8";
					// encodingInfo[1] = "UTF-8";
					// }
				}
			}
			catch (Exception e) {
			}
			finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					}
					catch (Exception e) {
					}
				}
			}
		}
	}

	private static void updateFromEncodingHelper(InputStream iStream, String[] encodingInfo) {
		EncodingHelper encodingHelper = new EncodingHelper(iStream);
		encodingInfo[0] = encodingHelper.getEncoding() != null ? encodingHelper.getEncoding() : EncodingHelper.getDefaultEncoding();
		encodingInfo[1] = encodingHelper.getEncodingTag() != null ? encodingHelper.getEncodingTag() : EncodingHelper.getDefaultEncodingTag();
	}


	// This code is taken from org.eclipse.wst.xml.uriresolver.util.URIHelper
	// I didn't want to add this plugin as a dependency
	// in order to simplify our xerces dependenies
	protected static final String FILE_PROTOCOL = "file:";
	protected static final String PROTOCOL_PATTERN = ":";

	public static String addImpliedFileProtocol(String uri) {
		if (!hasProtocol(uri)) {
			uri = FILE_PROTOCOL + uri;
		}
		return uri;
	}

	public static boolean hasProtocol(String uri) {
		boolean result = false;
		if (uri != null) {
			int index = uri.indexOf(PROTOCOL_PATTERN);
			if (index != -1 && index > 2) // assume protocol with be length
											// 3 so that the'C' in 'C:/' is
											// not interpreted as a protocol
			{
				result = true;
			}
		}
		return result;
	}
}
