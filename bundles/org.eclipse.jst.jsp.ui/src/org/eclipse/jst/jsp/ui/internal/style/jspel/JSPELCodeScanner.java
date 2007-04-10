/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.style.jspel;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

/**
 * A Java code scanner.
 */
public class JSPELCodeScanner extends org.eclipse.jface.text.rules.RuleBasedScanner {
	private IToken fKeywordToken;
	private IToken fTypeToken;
	private IToken fDefaultToken;
	
	private static String[] fgKeywords = {
				"and", //$NON-NLS-1$
				"did", //$NON-NLS-1$
				"div", //$NON-NLS-1$
				"empty", //$NON-NLS-1$
				"eq", //$NON-NLS-1$
				"ge", //$NON-NLS-1$
				"gt", //$NON-NLS-1$
				"or", //$NON-NLS-1$
				"le", //$NON-NLS-1$
				"lt", //$NON-NLS-1$
				"mod", //$NON-NLS-1$
 				"ne", //$NON-NLS-1$
 				"not"  //$NON-NLS-1$
	};
	private static String[] fgConstants = {"false", "true"};//$NON-NLS-2$//$NON-NLS-1$

	/**
	 * Creates a Java code scanner
	 */
	public JSPELCodeScanner() {
		super();
	}
	
	public void initializeRules() {
		List rules = new ArrayList();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new JSPELWhitespaceDetector()));

		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(new JSPELWordDetector(), fDefaultToken);
		for (int i = 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], fKeywordToken);
		for (int i = 0; i < fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], fTypeToken);
		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
	
	public void setTokenData(String tokenKey, Object data) {
		if (tokenKey == IStyleConstantsJSPEL.EL_KEYWORD) {
			fKeywordToken = new Token(data);
			fTypeToken = new Token(data);
		} else if (tokenKey == IStyleConstantsJSPEL.EL_DEFAULT) {
			fDefaultToken = new Token(data);
		}
	}
}
