/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.style.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.wst.jsdt.web.core.javascript.JsDataTypes;
import org.eclipse.wst.xml.ui.internal.style.IStyleConstantsXML;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
class JSDTCodeScanner extends org.eclipse.jface.text.rules.RuleBasedScanner {
	private static String[] fgConstants = JsDataTypes.CONSTANTS;
	private static String[] fgKeywords = JsDataTypes.KEYWORDS;
	private static String[] fgTypes = JsDataTypes.TYPES;
	private IToken fDefaultToken;
	private IToken fKeywordToken;
	private IToken fSingleLineCommentToken;
	private IToken fMultiLineCommentToken;
	private IToken fStringToken;
	private IToken fTypeToken;
	private IToken fHTMLCommentBorderToken;
	
	/**
	 * Creates a Java code scanner
	 */
	public JSDTCodeScanner() {
		super();
	}
	
	public void initializeRules() {
		List rules = new ArrayList();
		// Add rule for multiple line comments.
		rules.add(new MultiLineRule("/*", "*/", fMultiLineCommentToken));//$NON-NLS-1$ //$NON-NLS-2$
		// Add rule for single line comments.
		rules.add(new NoneInclusiveEndSequenceSingleLineRule("//", "-->", fSingleLineCommentToken));//$NON-NLS-1$
		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", fStringToken, '\\'));//$NON-NLS-2$//$NON-NLS-1$
		rules.add(new SingleLineRule("'", "'", fStringToken, '\\'));//$NON-NLS-2$//$NON-NLS-1$
		// Add generic whitespace rule.
		// rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));
		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new JavaWordDetector(), fDefaultToken);
		for (int i = 0; i < JSDTCodeScanner.fgKeywords.length; i++) {
			wordRule.addWord(JSDTCodeScanner.fgKeywords[i], fKeywordToken);
		}
		for (int i = 0; i < JSDTCodeScanner.fgTypes.length; i++) {
			wordRule.addWord(JSDTCodeScanner.fgTypes[i], fTypeToken);
		}
		for (int i = 0; i < JSDTCodeScanner.fgConstants.length; i++) {
			wordRule.addWord(JSDTCodeScanner.fgConstants[i], fTypeToken);
		}
		rules.add(wordRule);
		
		//add word rule for HTML style comment delimiters
		rules.add(new WordRule(new HTMLCommentDetector(), this.fHTMLCommentBorderToken));
		
		//add rule for text after leading HTML comment delimiter
		rules.add(new NoneInclusiveStartSequenceEndOfLineRule("<!--", this.fSingleLineCommentToken));
		
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
	
	public void setTokenData(String tokenKey, Object data) {
		if (tokenKey == IStyleConstantsJSDT.JAVA_KEYWORD) {
			fKeywordToken = new Token(data);
			fTypeToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSDT.JAVA_STRING) {
			fStringToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSDT.JAVA_SINGLE_LINE_COMMENT) {
			fSingleLineCommentToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSDT.JAVA_MULTI_LINE_COMMENT) {
			fMultiLineCommentToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSDT.JAVA_DEFAULT) {
			fDefaultToken = new Token(data);
		} else if(tokenKey == IStyleConstantsXML.COMMENT_BORDER) {
			fHTMLCommentBorderToken = new Token(data);
		}
	}
	
	/**
	 * <p>Detector for HTML comment delimiters.</p>
	 */
	private static class HTMLCommentDetector implements IWordDetector {

		/**
		 * @see IWordDetector#isWordStart(char)
		 */
		public boolean isWordStart(char c) {
			return (c == '<' || c == '-');
		}

		/**
		 * @see IWordDetector#isWordPart(char)
		 */
		public boolean isWordPart(char c) {
			return (c == '-' || c == '!' || c == '>');
		}
	}
	
	/**
	 * <p>Same as a {@link SingleLineRule} except the given end sequence is not counted as part of the match.</p>
	 * 
	 * @see SingleLineRule
	 */
	private static class NoneInclusiveEndSequenceSingleLineRule extends SingleLineRule {

		/**
		 * @param startSequence start sequence included in rule match
		 * @param endSequence end sequence that will end this rule but will not be counted as part of the match
		 * @param token to return on a match by this rule
		 */
		public NoneInclusiveEndSequenceSingleLineRule(String startSequence, String endSequence, IToken token) {
			super(startSequence, endSequence, token);
		}

		/**
		 * <p>If the end sequence is detected then scanner is rewind to just before the end sequence,
		 * otherwise acts the same as {@link PatternRule#endSequenceDetected}</p>
		 * 
		 * @see PatternRule#endSequenceDetected
		 */
		protected boolean endSequenceDetected(ICharacterScanner scanner) {
			boolean success = super.endSequenceDetected(scanner);
			if(success) {
				for (int length = this.fEndSequence.length-1; length > 0; length--) {
					scanner.unread();
				}
				
				if(this.sequenceDetected(scanner, this.fEndSequence, false)) {
					for (int length = this.fEndSequence.length; length > 0; length--) {
						scanner.unread();
					}
				}
			}
			return success;
		}
	}
	
	/**
	 * <p>Same as an {@link EndOfLineRule} except the given start sequence is not counted as part of the match.</p>
	 * 
	 * @see EndOfLineRule
	 */
	private static class NoneInclusiveStartSequenceEndOfLineRule extends EndOfLineRule {

		/**
		 * @param startSequence start sequence the identifies the start of this match but is not counted as part of the match
		 * @param token to return on a match by this rule
		 */
		public NoneInclusiveStartSequenceEndOfLineRule(String startSequence, IToken token) {
			super(startSequence, token);
		}
		
		/**
		 * <p>Same as overridden function except unreads the scanner back the length of the start sequence
		 * since the start sequence is not counted as part of the match.</p>
		 * 
		 * @see org.eclipse.jface.text.rules.PatternRule#doEvaluate(org.eclipse.jface.text.rules.ICharacterScanner, boolean)
		 */
		protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
			if (resume) {
				if (endSequenceDetected(scanner))
					return fToken;
			} else {
				//unread the length of the start sequence since it is not counted as part of the match
				for(int i = 0; i < this.fStartSequence.length && scanner.getColumn() >= 0; ++i) {
					scanner.unread();
				}

				int c= scanner.read();
				if (c == fStartSequence[0]) {
					if (sequenceDetected(scanner, fStartSequence, false)) {
						if (endSequenceDetected(scanner))
							return fToken;
					}
				}
				
				//be sure to re-read the length of the start sequence if we did not match
				for(int i = 0; i < this.fStartSequence.length && scanner.getColumn() >= 0; ++i) {
					scanner.read();
				}
			}

			scanner.unread();
			return Token.UNDEFINED;
		}
	}
}
