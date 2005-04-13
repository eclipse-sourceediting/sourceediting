/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.format.AbstractStructuredFormatProcessor;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatPreferences;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatter;
import org.eclipse.wst.sse.core.internal.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.document.CDATASectionImpl;
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
				fFormatPreferences.setLineWidth(preferences.getInt(CommonModelPreferenceNames.LINE_WIDTH));
				((IStructuredFormatPreferencesXML) fFormatPreferences).setSplitMultiAttrs(preferences.getBoolean(CommonModelPreferenceNames.SPLIT_MULTI_ATTRS));
				fFormatPreferences.setClearAllBlankLines(preferences.getBoolean(CommonModelPreferenceNames.CLEAR_ALL_BLANK_LINES));

				if (preferences.getBoolean(CommonModelPreferenceNames.INDENT_USING_TABS))
					fFormatPreferences.setIndent("\t"); //$NON-NLS-1$
				else {
					int tabWidth = SSECorePlugin.getDefault().getPluginPreferences().getInt(CommonModelPreferenceNames.TAB_WIDTH);
					String indent = ""; //$NON-NLS-1$
					for (int i = 0; i < tabWidth; i++) {
						indent += " "; //$NON-NLS-1$
					}
					fFormatPreferences.setIndent(indent);
				}
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
				if (node instanceof CDATASectionImpl)
					formatter = new NodeFormatter();
				else
					formatter = new TextNodeFormatter();
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
