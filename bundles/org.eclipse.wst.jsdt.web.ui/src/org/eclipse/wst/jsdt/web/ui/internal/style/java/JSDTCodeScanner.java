/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
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
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.wst.jsdt.web.core.javascript.JsDataTypes;

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
	private IToken fStringToken;
	private IToken fTypeToken;
	
	/**
	 * Creates a Java code scanner
	 */
	public JSDTCodeScanner() {
		super();
	}
	
	public void initializeRules() {
		List rules = new ArrayList();
		// Add rule for multiple line comments.
		rules.add(new MultiLineRule("/*", "*/", fSingleLineCommentToken));//$NON-NLS-1$ //$NON-NLS-2$
		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", fSingleLineCommentToken));//$NON-NLS-1$
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
		} else if (tokenKey == IStyleConstantsJSDT.JAVA_DEFAULT) {
			fDefaultToken = new Token(data);
		}
	}
}
