/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.style.java;

import org.eclipse.wst.jsdt.web.core.internal.java.JsDataTypes;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * A Java code scanner.
 */
class JavaCodeScanner extends org.eclipse.jface.text.rules.RuleBasedScanner {
	private IToken fKeywordToken;
	private IToken fTypeToken;
	private IToken fStringToken;
	private IToken fSingleLineCommentToken;
	private IToken fDefaultToken;

	private static String[] fgKeywords = JsDataTypes.KEYWORDS;
	private static String[] fgTypes = JsDataTypes.TYPES;
	private static String[] fgConstants = JsDataTypes.CONSTANTS;

	/**
	 * Creates a Java code scanner
	 */
	public JavaCodeScanner() {
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
		for (int i = 0; i < fgKeywords.length; i++) {
			wordRule.addWord(fgKeywords[i], fKeywordToken);
		}
		for (int i = 0; i < fgTypes.length; i++) {
			wordRule.addWord(fgTypes[i], fTypeToken);
		}
		for (int i = 0; i < fgConstants.length; i++) {
			wordRule.addWord(fgConstants[i], fTypeToken);
		}
		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	public void setTokenData(String tokenKey, Object data) {
		if (tokenKey == IStyleConstantsJSPJava.JAVA_KEYWORD) {
			fKeywordToken = new Token(data);
			fTypeToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSPJava.JAVA_STRING) {
			fStringToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT) {
			fSingleLineCommentToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSPJava.JAVA_DEFAULT) {
			fDefaultToken = new Token(data);
		}
	}
}
