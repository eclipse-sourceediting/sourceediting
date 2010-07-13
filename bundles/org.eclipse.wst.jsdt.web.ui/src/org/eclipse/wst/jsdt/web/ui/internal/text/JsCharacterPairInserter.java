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
package org.eclipse.wst.jsdt.web.ui.internal.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.internal.ui.JavaScriptPlugin;
import org.eclipse.wst.jsdt.internal.ui.text.JavaHeuristicScanner;
import org.eclipse.wst.jsdt.internal.ui.text.Symbols;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter;

/**
 * <p>Inserts character pairs in script regions in HTML and JSP documents based on the Javascript
 * character pairing preferences.</p>
 */
public class JsCharacterPairInserter extends AbstractCharacterPairInserter implements IPropertyChangeListener{

	private boolean fCloseStrings;
	private boolean fCloseBrackets;
	private boolean fCloseBraces;
	private boolean fCloseAngularBrackets;
	
	/**
	 * @see org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter#hasPair(char)
	 */
	public boolean hasPair(char c) {
		switch (c) {
			case '(':
			case '<':
			case '[':
			case '\'':
			case '\"':
			case '{':
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter#shouldPair(org.eclipse.jface.text.source.ISourceViewer, char)
	 */
	protected boolean shouldPair(ISourceViewer viewer, char c) {
		IDocument document= viewer.getDocument();

		final Point selection= viewer.getSelectedRange();
		final int offset= selection.x;
		final int length= selection.y;

		try {
			IRegion startLine= document.getLineInformationOfOffset(offset);
			IRegion endLine= document.getLineInformationOfOffset(offset + length);

			JavaHeuristicScanner scanner= new JavaHeuristicScanner(document);
			int nextToken= scanner.nextToken(offset + length, endLine.getOffset() + endLine.getLength());
			String next= nextToken == Symbols.TokenEOF ? null : document.get(offset, scanner.getPosition() - offset).trim();
			int prevToken= scanner.previousToken(offset - 1, startLine.getOffset());
			int prevTokenOffset= scanner.getPosition() + 1;
			String previous= prevToken == Symbols.TokenEOF ? null : document.get(prevTokenOffset, offset - prevTokenOffset).trim();

			switch (c) {
				case '(':
					if (!fCloseBrackets
							|| nextToken == Symbols.TokenLPAREN
							|| nextToken == Symbols.TokenIDENT
							|| next != null && next.length() > 1)
						return false;
					break;

				case '<':
					if (!(fCloseAngularBrackets && fCloseBrackets)
							|| nextToken == Symbols.TokenLESSTHAN
							|| 		   prevToken != Symbols.TokenLBRACE
									&& prevToken != Symbols.TokenRBRACE
									&& prevToken != Symbols.TokenSEMICOLON
									&& prevToken != Symbols.TokenSYNCHRONIZED
									&& prevToken != Symbols.TokenSTATIC
									&& (prevToken != Symbols.TokenIDENT || !isAngularIntroducer(previous))
									&& prevToken != Symbols.TokenEOF)
						return false;
					break;

				case '{':
					if (!fCloseBraces
							|| nextToken == Symbols.TokenIDENT
							|| next != null && next.length() > 1)
						return false;
					break;
				case '[':
					if (!fCloseBrackets
							|| nextToken == Symbols.TokenIDENT
							|| next != null && next.length() > 1)
						return false;
					break;

				case '\'':
				case '"':
					if (!fCloseStrings
							|| nextToken == Symbols.TokenIDENT
							|| prevToken == Symbols.TokenIDENT
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
	
	/**
	 * @see org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter#getPair(char)
	 */
	protected char getPair(char c) {
		switch (c) {
			case '(':
				return ')';
			case '<':
				return '>';
			case '[':
				return ']';
			case '{':
				return '}';
			case '\'':
				return c;
			case '\"':
				return c;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	/**
	 * <p>Initialize the preference listener</p>
	 * 
	 * @see org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter#initialize()
	 */
	public void initialize() {
		super.initialize();
		IPreferenceStore preferenceStore = JavaScriptPlugin.getDefault().getPreferenceStore();
		this.fCloseStrings = preferenceStore.getBoolean(PreferenceConstants.EDITOR_CLOSE_STRINGS);
		this.fCloseBrackets = preferenceStore.getBoolean(PreferenceConstants.EDITOR_CLOSE_BRACKETS);
		this.fCloseBraces = preferenceStore.getBoolean(PreferenceConstants.EDITOR_CLOSE_BRACES);
		this.fCloseAngularBrackets = JavaScriptCore.VERSION_1_5.compareTo(
				preferenceStore.getString(JavaScriptCore.COMPILER_SOURCE)) <= 0;
		preferenceStore.addPropertyChangeListener(this);
	}
	
	/**
	 * <p>Dispose the preference listener</p>
	 * 
	 * @see org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter#dispose()
	 */
	public void dispose() {
		JavaScriptPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (PreferenceConstants.EDITOR_CLOSE_STRINGS.equals(event.getProperty())){
			this.fCloseStrings = ((Boolean) event.getNewValue()).booleanValue();
		} else if (PreferenceConstants.EDITOR_CLOSE_BRACKETS.equals(event.getProperty())) {
			this.fCloseBrackets = ((Boolean) event.getNewValue()).booleanValue();
		} else if (PreferenceConstants.EDITOR_CLOSE_BRACES.equals(event.getProperty())) {
			this.fCloseBraces = ((Boolean) event.getNewValue()).booleanValue();
		} else if (JavaScriptCore.COMPILER_SOURCE.equals(event.getProperty())) {
			IPreferenceStore preferenceStore = JavaScriptPlugin.getDefault().getPreferenceStore();
			this.fCloseAngularBrackets = JavaScriptCore.VERSION_1_5.compareTo(preferenceStore.getString(
						JavaScriptCore.COMPILER_SOURCE)) <= 0;
		}
	}
	
	/**
	 * TODO: IAN: comment me
	 * @param identifier
	 * @return
	 */
	private boolean isAngularIntroducer(String identifier) {
		return identifier.length() > 0
				&& (Character.isUpperCase(identifier.charAt(0))
						|| identifier.startsWith("final") //$NON-NLS-1$
						|| identifier.startsWith("public") //$NON-NLS-1$
						|| identifier.startsWith("public") //$NON-NLS-1$
						|| identifier.startsWith("protected") //$NON-NLS-1$
						|| identifier.startsWith("private")); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.wst.sse.ui.typing.AbstractCharacterPairInserter#getExitPolicy(char, char, org.eclipse.jface.text.IDocument)
	 */
	protected IExitPolicy getExitPolicy(char exit, char escape, IDocument document) {
		return new ExitPolicy(exit, escape, document);
	}
	
	/**
	 * <p>An exit policy that deals with a user adding a carriage return between {}.
	 * In that case rather then exiting to the exit position the linked mode is exited
	 * and the carriage return inserted.</p>
	 */
	private static class ExitPolicy implements IExitPolicy {

		/** exit character */
		private char fExit;
		
		/** escape character for the exit character */
		private char fEscape;
		
		/** document to execute this exit policy on */
		private IDocument fDocument;

		/**
		 * <p>Default constructor</p>
		 * 
		 * @param exit exit character
		 * @param escape escape character for the exit character
		 * @param document document to execute this exit policy on
		 */
		public ExitPolicy(char exit, char escape, IDocument document) {
			fExit = exit;
			fEscape = escape;
			fDocument = document;
		}

		/**
		 * @see org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy#doExit(org.eclipse.jface.text.link.LinkedModeModel, org.eclipse.swt.events.VerifyEvent, int, int)
		 */
		public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length) {
			if(!isMasked(offset)) {
				// if exit character then exit to exit location
				if (event.character == fExit) {
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
				}
				
				// if carriage return and previous character is { then exit linked mode and insert return
				if (event.character == SWT.CR && offset > 0) {
					try {
						if (this.fDocument.getChar(offset - 1) == '{') {
							return new ExitFlags(ILinkedModeListener.EXIT_ALL, true);
						}
					} catch (BadLocationException e) {
						Logger.logException("Error while trying to exit linked mode", e); //$NON-NLS-1$
					}
				}
			}
			return null;
		}

		/**
		 * <p>Determine if the exit character has been escaped.<p>
		 * 
		 * @param offset current offset in the document
		 * @return <code>true</code> if exit character escaped, <code>false</code> otherwise
		 */
		private boolean isMasked(int offset) {
			try {
				return fEscape == fDocument.getChar(offset - 1);
			} catch (BadLocationException e) {
			}
			return false;
		}
	}
}
