/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Frits Jalvingh - contributions for bug 150794
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.style.java;



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

	private static String[] fgKeywords = {"abstract", //$NON-NLS-1$
				"break", //$NON-NLS-1$
				"case", "catch", "class", "continue", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"default", "do", //$NON-NLS-2$//$NON-NLS-1$
				"else", "extends", //$NON-NLS-2$//$NON-NLS-1$
				"final", "finally", "for", //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"if", "implements", "import", "instanceof", "interface", //$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"native", "new", //$NON-NLS-2$//$NON-NLS-1$
				"package", "private", "protected", "public", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"return", //$NON-NLS-1$
				"static", "super", "switch", "synchronized", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"this", "throw", "throws", "transient", "try", //$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"volatile", //$NON-NLS-1$
				"while", //$NON-NLS-1$
				"strictfp",//$NON-NLS-1$
	};
	private static String[] fgTypes = {"void", "boolean", "char", "byte", "short", "int", "long", "float", "double"};//$NON-NLS-9$//$NON-NLS-8$//$NON-NLS-7$//$NON-NLS-6$//$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	private static String[] fgConstants = {"false", "null", "true"};//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$

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
		//rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new JavaWordDetector(), fDefaultToken);
		for (int i = 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], fKeywordToken);
		for (int i = 0; i < fgTypes.length; i++)
			wordRule.addWord(fgTypes[i], fTypeToken);
		for (int i = 0; i < fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], fTypeToken);
		rules.add(wordRule);

		// Add the double-quoted string rule
		rules.add(new DoubleQuotedStringRule(fStringToken));

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
