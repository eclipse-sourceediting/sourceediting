package org.eclipse.wst.xsl.internal.xpath.rules;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Single character rule. If the character is matched, the token is returned and
 * the style is applied.
 * 
 * @author Michal Chmielewski (michal.chmielewski@oracle.com)
 * @date Oct 26, 2006
 * 
 */

public class SingleCharRule implements IRule {

	IToken fToken;

	String fChars;

	/**
	 * Return a brand new shiny Single Character Rule.
	 * 
	 * @param token
	 * @param chars
	 */
	
	public SingleCharRule(IToken token, String chars) {
		Assert.isNotNull(token);
		fToken = token;
		Assert.isNotNull(chars);
		fChars = chars;
	}

	/**
	 * 
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int ch = scanner.read();
		if (fChars.indexOf(ch) >= 0) {
			return fToken;
		}
		scanner.unread();

		return Token.UNDEFINED;
	}
}