/*******************************************************************************
 * Copyright (c) 2009 Frits Jalvingh and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frits Jalvingh - initial version (bugfix for 150794)
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.style.java;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * This rule matches the double-quoted strings present in JSP tag attributes. A double-quoted
 * strings starts with \" (two characters!) and ends with \" (two characters!) too. The sequence
 * \" is escaped by the horror \\\" (4 chars!?) as per the JSP spec.
 *
 * @author <a href="mailto:jal@etc.to">Frits Jalvingh</a>
 * Created on Aug 5, 2007
 */
public class DoubleQuotedStringRule implements IPredicateRule {
	private IToken	fToken;
	private int		m_qc;

	public DoubleQuotedStringRule(IToken tok) {
		fToken = tok;
	}
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if(resume) {
			if(findEnd(scanner, m_qc))
				return fToken;
		} else {
			int c= scanner.read();
			if(c == '\\') {
				c = scanner.read();
				if(c == '"' || c == '\'') {
					if(findEnd(scanner, c))
						return fToken;
				}
				scanner.unread();
			}
			scanner.unread();
		}
		return Token.UNDEFINED;
	}

	private boolean findEnd(ICharacterScanner scanner, int qc) {
		m_qc = qc;
		int count = 0;
		int c;
		int nsl = 0;
		while((c= scanner.read()) != ICharacterScanner.EOF) {
			count++;
			if(c == '\\') {
				nsl++;
				if(nsl >= 4)
					nsl = 0;
			} else if(c == qc) {
				if(nsl == 1)
					return true;
				nsl = 0;
			} else
				nsl= 0;
		}
		while(--count >= 0)
			scanner.unread();
		return false;
	}

	public IToken getSuccessToken() {
		return fToken;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}
}
