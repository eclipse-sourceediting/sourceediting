/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.preferences.JSPUIPreferenceNames;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter;

public class JavaCharacterPairInserter extends AbstractCharacterPairInserter implements IPropertyChangeListener {

	private boolean fCloseBrackets = true;
	private boolean fCloseStrings = true;

	protected boolean shouldPair(ISourceViewer viewer, char c) {
		IDocument document = viewer.getDocument();
		final Point selection = viewer.getSelectedRange();
		final int offset = selection.x;
		final int length = selection.y;

		try {
			IRegion startLine= document.getLineInformationOfOffset(offset);
			IRegion endLine= document.getLineInformationOfOffset(offset + length);
	
			JavaHeuristicScanner scanner = new JavaHeuristicScanner(document);
			int nextToken = scanner.nextToken(offset + length, endLine.getOffset() + endLine.getLength());
			String next = nextToken == Symbols.TokenEOF ? null : document.get(offset, scanner.getPosition() - offset).trim();
			int prevToken = scanner.previousToken(offset - 1, startLine.getOffset());
			int prevTokenOffset = scanner.getPosition() + 1;
			String previous = prevToken == Symbols.TokenEOF ? null : document.get(prevTokenOffset, offset - prevTokenOffset).trim();
			
			switch (c) {
				case '(':
					if (!fCloseBrackets || nextToken == Symbols.TokenLPAREN || nextToken == Symbols.TokenIDENT	|| next != null && next.length() > 1)
						return false;
					break;
	
				case '<':
					if (!fCloseBrackets || nextToken == Symbols.TokenLESSTHAN
							|| 		   prevToken != Symbols.TokenLBRACE
									&& prevToken != Symbols.TokenRBRACE
									&& prevToken != Symbols.TokenSEMICOLON
									&& prevToken != Symbols.TokenSYNCHRONIZED
									&& prevToken != Symbols.TokenSTATIC
									&& (prevToken != Symbols.TokenIDENT || !isAngularIntroducer(previous))
									&& prevToken != Symbols.TokenEOF)
						return false;
					break;
	
				case '[':
					if (!fCloseBrackets || nextToken == Symbols.TokenIDENT || next != null && next.length() > 1)
						return false;
					break;
	
				case '\'':
				case '"':
					if (!fCloseStrings || nextToken == Symbols.TokenIDENT
							|| prevToken == Symbols.TokenIDENT
							|| prevToken == Symbols.TokenSQUOTE
							|| nextToken == Symbols.TokenSQUOTE
							|| prevToken == Symbols.TokenDQUOTE
							|| nextToken == Symbols.TokenDQUOTE
							|| next != null && next.length() > 1
							|| previous != null && previous.length() > 1)
						return false;
					break;
	
				default:
					return false;
			}
		} catch (BadLocationException e) {
			return false;
		}
		return true;
	}

	private boolean isAngularIntroducer(String identifier) {
		return identifier.length() > 0
				&& (Character.isUpperCase(identifier.charAt(0))
						|| identifier.startsWith("final") //$NON-NLS-1$
						|| identifier.startsWith("public") //$NON-NLS-1$
						|| identifier.startsWith("public") //$NON-NLS-1$
						|| identifier.startsWith("protected") //$NON-NLS-1$
						|| identifier.startsWith("private")); //$NON-NLS-1$
	}

	protected char getPair(char c) {
		switch (c) {
			case '\'':
			case '"':
				return c;
			case '(':
				return ')';
			case '[':
				return ']';
			case '<':
				return '>';
			default:
				throw new IllegalArgumentException();
		}
	}

	protected char getEscapeChar(char c) {
		switch (c) {
			case '\'':
			case '"':
				return '\\';
			default:
				return 0;
		}
	}

	public boolean hasPair(char c) {
		switch (c) {
			case '"':
			case '\'':
			case '[':
			case '(':
			case '<':
				return true;
			default:
				return false;
		}
	}

	public void initialize() {
		IPreferenceStore store = JSPUIPlugin.getInstance().getPreferenceStore();
		fCloseStrings = store.getBoolean(JSPUIPreferenceNames.TYPING_CLOSE_STRINGS);
		fCloseBrackets = store.getBoolean(JSPUIPreferenceNames.TYPING_CLOSE_BRACKETS);
		store.addPropertyChangeListener(this);
	}

	public void dispose() {
		JSPUIPlugin.getInstance().getPreferenceStore().removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (JSPUIPreferenceNames.TYPING_CLOSE_BRACKETS.equals(event.getProperty()))
			fCloseBrackets = ((Boolean) event.getNewValue()).booleanValue();
		else if (JSPUIPreferenceNames.TYPING_CLOSE_STRINGS.equals(event.getProperty()))
			fCloseStrings = ((Boolean) event.getNewValue()).booleanValue();
	}
}
