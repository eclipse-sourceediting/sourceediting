/**
 *  Copyright (c) 2013-2021 Angelo ZERR. and others
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.internal.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.preferences.JSONUIPreferenceNames;
import org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter;

/**
 * Character pair inserter for JSON to auto close JSON character like '{', '['.
 *
 */
public class JSONCharacterPairInserter extends AbstractCharacterPairInserter implements IPropertyChangeListener {

	private boolean fCloseStrings;
	private boolean fCloseBrackets;
	private boolean fCloseBraces;

	public boolean hasPair(char c) {
		switch (c) {
		case '"':
		case '\'':
		case '[':
		case '{':
			return true;
		default:
			return false;
		}
	}

	@Override
	public void initialize() {
		IPreferenceStore store = JSONUIPlugin.getDefault().getPreferenceStore();
		fCloseStrings = store.getBoolean(JSONUIPreferenceNames.TYPING_CLOSE_STRINGS);
		fCloseBraces = store.getBoolean(JSONUIPreferenceNames.TYPING_CLOSE_BRACES);
		fCloseBrackets = store.getBoolean(JSONUIPreferenceNames.TYPING_CLOSE_BRACKETS);
		store.addPropertyChangeListener(this);
	}

	protected char getPair(char c) {
		switch (c) {
		case '\'':
		case '"':
			return c;
		case '[':
			return ']';
		case '{':
			return '}';
		default:
			throw new IllegalArgumentException();
		}
	}

	class ExitPolicy implements IExitPolicy {

		private char fExit;
		private char fEscape;
		private IDocument fDocument;

		public ExitPolicy(char exit, char escape, IDocument document) {
			fExit = exit;
			fEscape = escape;
			fDocument = document;
		}

		public ExitFlags doExit(LinkedModeModel model, VerifyEvent event,
				int offset, int length) {
			if (!isMasked(offset)) {
				if (event.character == fExit)
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET,
							false);
				if (event.character == '\r' || event.character == '\n'
						&& offset > 0) {
					try {
						if (fDocument.getChar(offset - 1) == '{') {
							return new ExitFlags(ILinkedModeListener.EXIT_ALL,
									true);
						}
					} catch (BadLocationException e) {
					}
				}
			}
			return null;
		}

		private boolean isMasked(int offset) {
			try {
				return fEscape == fDocument.getChar(offset - 1);
			} catch (BadLocationException e) {
			}
			return false;
		}
	}

	protected IExitPolicy getExitPolicy(char exit, char escape,
			IDocument document) {
		return new ExitPolicy(exit, escape, document);
	}
	public void propertyChange(PropertyChangeEvent event) {
		if (JSONUIPreferenceNames.TYPING_CLOSE_BRACKETS.equals(event.getProperty()))
			fCloseBrackets = ((Boolean) event.getNewValue()).booleanValue();
		else if (JSONUIPreferenceNames.TYPING_CLOSE_BRACES.equals(event.getProperty()))
			fCloseBraces = ((Boolean) event.getNewValue()).booleanValue();
		else if (JSONUIPreferenceNames.TYPING_CLOSE_STRINGS.equals(event.getProperty()))
			fCloseStrings = ((Boolean) event.getNewValue()).booleanValue();
	}

	@Override
	protected boolean shouldPair(ISourceViewer viewer, char c) {
		switch (c) {
			case '\'' :
			case '"' :
				return fCloseStrings;
			case '[' :
			case '(' :
				return fCloseBrackets;
			case '{' :
				return fCloseBraces;
			default :
				return false;
		}
	}
}
