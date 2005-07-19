/*
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *   IBM - Initial API and implementation
 *   Jens Lukowski/Innoopract - initial renaming/restructuring
 * 
 */
package org.eclipse.wst.html.ui.internal.autoedit;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.html.core.internal.preferences.HTMLCorePreferenceNames;
import org.eclipse.wst.html.ui.internal.Logger;

/**
 * AutoEditStrategy to handle characters inserted when Tab key is pressed
 */
public class AutoEditStrategyForTabs implements IAutoEditStrategy {

	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		// spaces for tab character
		if (command.text != null && command.text.length() > 0 && command.text.charAt(0) == '\t')
			smartInsertForTab(command, document);
	}

	/**
	 * Insert spaces for tabs
	 * 
	 * @param command
	 */
	private void smartInsertForTab(DocumentCommand command, IDocument document) {
		// tab key was pressed. now check preferences to see if need to insert
		// spaces instead of tab
		Preferences preferences = HTMLCorePlugin.getDefault().getPluginPreferences();
		if (HTMLCorePreferenceNames.SPACE.equals(preferences.getString(HTMLCorePreferenceNames.INDENTATION_CHAR))) {
			int indentationWidth = preferences.getInt(HTMLCorePreferenceNames.INDENTATION_SIZE);

			StringBuffer indent = new StringBuffer();
			if (indentationWidth != 0) {
				int indentSize = indentationWidth;
				try {
					IRegion firstLine = document.getLineInformationOfOffset(command.offset);
					int offsetInLine = command.offset - firstLine.getOffset();
					int remainder = offsetInLine % indentationWidth;

					indentSize = indentationWidth - remainder;
				} catch (BadLocationException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}

				for (int i = 0; i < indentSize; i++)
					indent.append(' ');
			}

			// replace \t characters with spaces
			command.text = indent.toString();
		}
	}
}
