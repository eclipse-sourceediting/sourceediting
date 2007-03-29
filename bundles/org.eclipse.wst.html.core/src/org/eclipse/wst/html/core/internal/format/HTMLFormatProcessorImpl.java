/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.format;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xml.core.internal.provisional.format.StructuredFormatPreferencesXML;
import org.w3c.dom.Node;

public class HTMLFormatProcessorImpl extends FormatProcessorXML {
	protected String getFileExtension() {
		return "html"; //$NON-NLS-1$
	}

	protected IStructuredFormatter getFormatter(Node node) {
		IStructuredFormatter formatter = HTMLFormatterFactory.getInstance().createFormatter(node, getFormatPreferences());

		return formatter;
	}

	public IStructuredFormatPreferences getFormatPreferences() {
		if (fFormatPreferences == null) {
			fFormatPreferences = new StructuredFormatPreferencesXML();

			Preferences preferences = HTMLCorePlugin.getDefault().getPluginPreferences();
			if (preferences != null) {
				fFormatPreferences.setLineWidth(preferences.getInt(HTMLCorePreferenceNames.LINE_WIDTH));
				((StructuredFormatPreferencesXML) fFormatPreferences).setSplitMultiAttrs(preferences.getBoolean(HTMLCorePreferenceNames.SPLIT_MULTI_ATTRS));
				((StructuredFormatPreferencesXML) fFormatPreferences).setAlignEndBracket(preferences.getBoolean(HTMLCorePreferenceNames.ALIGN_END_BRACKET));
				fFormatPreferences.setClearAllBlankLines(preferences.getBoolean(HTMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES));

				char indentChar = ' ';
				String indentCharPref = preferences.getString(HTMLCorePreferenceNames.INDENTATION_CHAR);
				if (HTMLCorePreferenceNames.TAB.equals(indentCharPref)) {
					indentChar = '\t';
				}
				int indentationWidth = preferences.getInt(HTMLCorePreferenceNames.INDENTATION_SIZE);

				StringBuffer indent = new StringBuffer();
				for (int i = 0; i < indentationWidth; i++) {
					indent.append(indentChar);
				}
				fFormatPreferences.setIndent(indent.toString());
			}
		}

		return fFormatPreferences;
	}
}