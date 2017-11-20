/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.sse.core.internal.encoding.CommonCharsetNames;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;

/**
 * Sets default values for CSS Core preferences
 */
public class CSSCorePreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IEclipsePreferences node = DefaultScope.INSTANCE
				.getNode(CSSCorePlugin.getDefault().getBundle().getSymbolicName());

		// formatting preferences
		node.putInt(CSSCorePreferenceNames.LINE_WIDTH, 72);
		node.putBoolean(CSSCorePreferenceNames.CLEAR_ALL_BLANK_LINES, false);
		node.put(CSSCorePreferenceNames.INDENTATION_CHAR, CSSCorePreferenceNames.TAB);
		node.putInt(CSSCorePreferenceNames.INDENTATION_SIZE, 1);

		// cleanup preferences
		node.putBoolean(CSSCorePreferenceNames.QUOTE_ATTR_VALUES, true);
		node.putBoolean(CSSCorePreferenceNames.FORMAT_SOURCE, true);

		// code generation preferences
		node.put(CommonEncodingPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$
		String defaultEnc = "UTF-8";//$NON-NLS-1$
		String systemEnc = System.getProperty("file.encoding"); //$NON-NLS-1$
		if (systemEnc != null) {
			defaultEnc = CommonCharsetNames.getPreferredDefaultIanaName(systemEnc, "UTF-8");//$NON-NLS-1$
		}
		node.put(CommonEncodingPreferenceNames.OUTPUT_CODESET, defaultEnc);
		node.put(CommonEncodingPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$

		// this could be made smarter by actually looking up the content
		// type's valid extensions
		node.put(CSSCorePreferenceNames.DEFAULT_EXTENSION, "css"); //$NON-NLS-1$

		// additional css core preferences
		node.putInt(CSSCorePreferenceNames.FORMAT_PROP_PRE_DELIM, 0);
		node.putInt(CSSCorePreferenceNames.FORMAT_PROP_POST_DELIM, 1);
		node.put(CSSCorePreferenceNames.FORMAT_QUOTE, "\"");//$NON-NLS-1$
		node.put(CSSCorePreferenceNames.FORMAT_BETWEEN_VALUE, " ");//$NON-NLS-1$
		node.putBoolean(CSSCorePreferenceNames.FORMAT_SPACE_BETWEEN_SELECTORS, true);
		node.putBoolean(CSSCorePreferenceNames.FORMAT_QUOTE_IN_URI, true);
		node.putBoolean(CSSCorePreferenceNames.WRAPPING_ONE_PER_LINE, true);
		node.putBoolean(CSSCorePreferenceNames.WRAPPING_PROHIBIT_WRAP_ON_ATTR, true);
		node.putBoolean(CSSCorePreferenceNames.WRAPPING_NEWLINE_ON_OPEN_BRACE, false);
		node.putInt(CSSCorePreferenceNames.CASE_IDENTIFIER, CSSCorePreferenceNames.LOWER);
		node.putInt(CSSCorePreferenceNames.CASE_SELECTOR, CSSCorePreferenceNames.LOWER);
		node.putInt(CSSCorePreferenceNames.CASE_PROPERTY_NAME, CSSCorePreferenceNames.LOWER);
		node.putInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE, CSSCorePreferenceNames.LOWER);

		// CSS cleanup preferences
		node.putInt(CSSCorePreferenceNames.CLEANUP_CASE_IDENTIFIER, CSSCorePreferenceNames.ASIS);
		node.putInt(CSSCorePreferenceNames.CLEANUP_CASE_PROPERTY_NAME, CSSCorePreferenceNames.ASIS);
		node.putInt(CSSCorePreferenceNames.CLEANUP_CASE_PROPERTY_VALUE, CSSCorePreferenceNames.ASIS);
		node.putInt(CSSCorePreferenceNames.CLEANUP_CASE_SELECTOR, CSSCorePreferenceNames.ASIS);
	}

}
