/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;



import java.util.Properties;
import com.ibm.icu.util.StringTokenizer;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * class to handle chunks of text/regions
 * with escaped character data
 * @author pavery
 */
public class EscapedTextUtil {

	public static Properties fXMLtoJavaLookup = null;

	/**
	 * @return unescaped full text of that region, "" if there is no text
	 */
	public static String getUnescapedText(IStructuredDocumentRegion parent, ITextRegion r) {
		String test = (parent != r) ? parent.getFullText(r) : parent.getFullText();
		return getUnescapedText(test);
	}

	public static String getUnescapedText(String test) {
		initLookup();
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
				if (!(transString = fXMLtoJavaLookup.getProperty(tok1 + tok2 + tok3, "")).equals("")) //$NON-NLS-2$ //$NON-NLS-1$
				{
					buffer.append(transString);
				}
				else {
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
		fXMLtoJavaLookup = new Properties();
		fXMLtoJavaLookup.setProperty("&apos;", "'"); //$NON-NLS-2$ //$NON-NLS-1$
		fXMLtoJavaLookup.setProperty("&quot;", "\""); //$NON-NLS-2$ //$NON-NLS-1$
		fXMLtoJavaLookup.setProperty("&amp;", "&"); //$NON-NLS-2$ //$NON-NLS-1$
		fXMLtoJavaLookup.setProperty("&lt;", "<"); //$NON-NLS-2$ //$NON-NLS-1$
		fXMLtoJavaLookup.setProperty("&gt;", ">"); //$NON-NLS-2$ //$NON-NLS-1$
		fXMLtoJavaLookup.setProperty("&nbsp;", " "); //$NON-NLS-2$ //$NON-NLS-1$
	}

	/**
	 * Get the String representation of an entity reference.
	 */
	public static String translateEntityReference(String entity) {
		return fXMLtoJavaLookup.getProperty(entity, entity);
	}
}

