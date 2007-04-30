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
package org.eclipse.wst.jsdt.web.core.internal.java;

import java.util.Properties;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

import com.ibm.icu.util.StringTokenizer;

/**
 * class to handle chunks of text/regions with escaped character data
 * 
 * @author pavery
 */
public class EscapedTextUtil {

	public static Properties fXMLtoJavaLookup = null;

	/**
	 * @return unescaped full text of that region, "" if there is no text
	 */
	public static String getUnescapedText(IStructuredDocumentRegion parent, ITextRegion r) {
		String test = (parent != r) ? parent.getFullText(r) : parent.getFullText();
		return EscapedTextUtil.getUnescapedText(test);
	}

	public static String getUnescapedText(String test) {
		EscapedTextUtil.initLookup();
		StringBuffer buffer = new StringBuffer();
		if (test != null) {
			StringTokenizer st = new StringTokenizer(test, "&;", true); //$NON-NLS-1$
			String tok1, tok2, tok3, transString;
			while (st.hasMoreTokens()) {
				tok1 = tok2 = tok3 = transString = ""; //$NON-NLS-1$
				tok1 = st.nextToken();
				if (tok1.equals("&") && st.hasMoreTokens()) //$NON-NLS-1$
				{
					tok2 = st.nextToken();
					if (st.hasMoreTokens()) {
						tok3 = st.nextToken();
					}
				}
				if (!(transString = EscapedTextUtil.fXMLtoJavaLookup.getProperty(tok1 + tok2 + tok3, "")).equals("")) //$NON-NLS-2$ //$NON-NLS-1$
				{
					buffer.append(transString);
				} else {
					buffer.append(tok1 + tok2 + tok3);
				}
			}
			return buffer.toString();
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * initialize lookup tables
	 */
	private static void initLookup() {
		EscapedTextUtil.fXMLtoJavaLookup = new Properties();
		EscapedTextUtil.fXMLtoJavaLookup.setProperty("&apos;", "'"); //$NON-NLS-2$ //$NON-NLS-1$
		EscapedTextUtil.fXMLtoJavaLookup.setProperty("&quot;", "\""); //$NON-NLS-2$ //$NON-NLS-1$
		EscapedTextUtil.fXMLtoJavaLookup.setProperty("&amp;", "&"); //$NON-NLS-2$ //$NON-NLS-1$
		EscapedTextUtil.fXMLtoJavaLookup.setProperty("&lt;", "<"); //$NON-NLS-2$ //$NON-NLS-1$
		EscapedTextUtil.fXMLtoJavaLookup.setProperty("&gt;", ">"); //$NON-NLS-2$ //$NON-NLS-1$
		EscapedTextUtil.fXMLtoJavaLookup.setProperty("&nbsp;", " "); //$NON-NLS-2$ //$NON-NLS-1$
	}

	/**
	 * Get the String representation of an entity reference.
	 */
	public static String translateEntityReference(String entity) {
		return EscapedTextUtil.fXMLtoJavaLookup.getProperty(entity, entity);
	}
}
