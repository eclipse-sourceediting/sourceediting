/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.provisional.format;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.format.AbstractStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.w3c.dom.Node;

public class FormatProcessorXML extends AbstractStructuredFormatProcessor {
	protected IStructuredFormatPreferences fFormatPreferences = null;

	protected String getFileExtension() {
		return "xml"; //$NON-NLS-1$
	}

	public IStructuredFormatPreferences getFormatPreferences() {
		if (fFormatPreferences == null) {
			fFormatPreferences = new StructuredFormatPreferencesXML();

			Preferences preferences = getModelPreferences();
			if (preferences != null) {
				fFormatPreferences.setLineWidth(preferences.getInt(XMLCorePreferenceNames.LINE_WIDTH));
				((StructuredFormatPreferencesXML) fFormatPreferences).setSplitMultiAttrs(preferences.getBoolean(XMLCorePreferenceNames.SPLIT_MULTI_ATTRS));
				((StructuredFormatPreferencesXML) fFormatPreferences).setAlignEndBracket(preferences.getBoolean(XMLCorePreferenceNames.ALIGN_END_BRACKET));
				((StructuredFormatPreferencesXML) fFormatPreferences).setPreservePCDATAContent(preferences.getBoolean(XMLCorePreferenceNames.PRESERVE_CDATACONTENT));
				fFormatPreferences.setClearAllBlankLines(preferences.getBoolean(XMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES));

				char indentChar = ' ';
				String indentCharPref = preferences.getString(XMLCorePreferenceNames.INDENTATION_CHAR);
				if (XMLCorePreferenceNames.TAB.equals(indentCharPref)) {
					indentChar = '\t';
				}
				int indentationWidth = preferences.getInt(XMLCorePreferenceNames.INDENTATION_SIZE);

				StringBuffer indent = new StringBuffer();
				for (int i = 0; i < indentationWidth; i++) {
					indent.append(indentChar);
				}
				fFormatPreferences.setIndent(indent.toString());
			}
		}

		return fFormatPreferences;
	}

	protected IStructuredFormatter getFormatter(Node node) {
		// 262135 - NPE during format of empty document
		if (node == null)
			return null;

		short nodeType = node.getNodeType();
		IStructuredFormatter formatter = null;
		switch (nodeType) {
			case Node.ELEMENT_NODE : {
				formatter = new ElementNodeFormatter();
				break;
			}
			case Node.TEXT_NODE : {
				formatter = new TextNodeFormatter();
				break;
			}
			case Node.CDATA_SECTION_NODE : {
				formatter = new NoMoveFormatter();
				break;
			}
			case Node.COMMENT_NODE : {
				formatter = new CommentNodeFormatter();
				break;
			}
			case Node.PROCESSING_INSTRUCTION_NODE : {
				formatter = new NodeFormatter();
				break;
			}
			case Node.DOCUMENT_NODE : {
				formatter = new DocumentNodeFormatter();
				break;
			}
			case Node.ENTITY_REFERENCE_NODE : {
				formatter = new NoMoveFormatter();
				break;
			}
			default : {
				formatter = new NodeFormatter();
			}
		}

		// init fomatter
		formatter.setFormatPreferences(getFormatPreferences());
		formatter.setProgressMonitor(fProgressMonitor);

		return formatter;
	}

	protected Preferences getModelPreferences() {
		return XMLCorePlugin.getDefault().getPluginPreferences();
	}

	protected void refreshFormatPreferences() {
		fFormatPreferences = null;
	}
}
