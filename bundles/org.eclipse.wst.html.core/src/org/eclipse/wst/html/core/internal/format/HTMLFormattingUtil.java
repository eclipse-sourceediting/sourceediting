/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.format;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.w3c.dom.Node;

import com.ibm.icu.util.StringTokenizer;

public class HTMLFormattingUtil {

	private Set fInlineElements;

	public HTMLFormattingUtil() {
		fInlineElements = getInlineSet();
	}

	public boolean isInline(Node node) {
		return node != null && fInlineElements.contains(node.getNodeName().toLowerCase(Locale.US));
	}

	public boolean shouldSkipIndentForNode(Node node) {
		return isInline(node.getParentNode());
	}

	/**
	 * Returns an array of the element names considered as "inline" for the purposes of formatting.
	 * This list represents those stored in the preference store when invoked.
	 * 
	 * @return An array of element names considered to be "inline"
	 */
	public static Object[] getInlineElements() {
		return getInlineSet().toArray();
	}

	/**
	 * Stores the element names to the preference store to be considered as "inline"
	 * 
	 * @param elements The element names considered to be "inline"
	 */
	public static void exportToPreferences(Object[] elements) {
		if (elements != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < elements.length; i++) {
				if (i > 0)
					buffer.append(',');
				buffer.append(elements[i]);
			}
			HTMLCorePlugin.getDefault().getPluginPreferences().setValue(HTMLCorePreferenceNames.INLINE_ELEMENTS, buffer.toString());
		}
	}

	/**
	 * Returns an array of the default element names considered as "inline" for the purposes of formatting.
	 * 
	 * @return An array of the default element names considered to be "inline"
	 */
	public static Object[] getDefaultInlineElements() {
		String inline = HTMLCorePlugin.getDefault().getPluginPreferences().getDefaultString(HTMLCorePreferenceNames.INLINE_ELEMENTS);
		Set defaults = new HashSet();
		StringTokenizer tokenizer = new StringTokenizer(inline, ",");
		while (tokenizer.hasMoreTokens()) {
			defaults.add(tokenizer.nextToken());
		}
		return defaults.toArray();
	}

	private static Set getInlineSet() {
		String inline = HTMLCorePlugin.getDefault().getPluginPreferences().getString(HTMLCorePreferenceNames.INLINE_ELEMENTS);
		Set elements = new HashSet();
		StringTokenizer tokenizer = new StringTokenizer(inline, ",");
		while (tokenizer.hasMoreTokens()) {
			elements.add(tokenizer.nextToken());
		}
		return elements;
	}
}
