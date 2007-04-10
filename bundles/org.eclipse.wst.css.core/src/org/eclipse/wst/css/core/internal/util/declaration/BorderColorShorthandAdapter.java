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
package org.eclipse.wst.css.core.internal.util.declaration;



import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.parserz.CSSTextParser;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;


/**
 * For 'border-color' property 'border-color' will be expanded to
 * 'border-top-color', 'border-right-color', 'border-bottom-color',
 * 'border-left-color',
 */
public class BorderColorShorthandAdapter implements IShorthandAdapter {

	/**
	 * 
	 */
	public BorderColorShorthandAdapter() {
		super();
	}

	/**
	 * 
	 */
	public boolean expand(String source, CSSPropertyContext dest) {
		CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_DECLARATION_VALUE, source);
		CSSTextToken[] tokens = parser.getTokens();
		if (tokens.length <= 0) {
			return false;
		}
		String[] idents = new String[4];
		int j = 0;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_HASH)
				idents[j++] = tokens[i].image;
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION) {
				StringBuffer buf = new StringBuffer();
				while (i < tokens.length) {
					if (tokens[i].kind == CSSRegionContexts.CSS_COMMENT) {
						i++;
						continue;
					}
					buf.append(tokens[i].image);
					if (tokens[i++].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE)
						break;
				}
				i--;
				idents[j++] = buf.toString();
			}
			if (j == 4)
				break;
		}

		String[] dests = new String[4];
		if (j == 0)
			return true;
		else if (j == 1) {
			dests[0] = dests[1] = dests[2] = dests[3] = idents[0];
		}
		else if (j == 2) {
			dests[0] = dests[2] = idents[0];
			dests[1] = dests[3] = idents[1];
		}
		else if (j == 3) {
			dests[0] = idents[0];
			dests[1] = dests[3] = idents[1];
			dests[2] = idents[2];
		}
		else {
			for (int k = 0; k < 4; k++)
				dests[k] = idents[k];
		}
		dest.set(PropCMProperty.P_BORDER_TOP_COLOR, dests[0]);
		dest.set(PropCMProperty.P_BORDER_RIGHT_COLOR, dests[1]);
		dest.set(PropCMProperty.P_BORDER_BOTTOM_COLOR, dests[2]);
		dest.set(PropCMProperty.P_BORDER_LEFT_COLOR, dests[3]);

		return true;
	}

	/**
	 * 
	 */
	public String extract(String source, PropCMProperty propDest) {
		CSSTextParser parser = new CSSTextParser(CSSTextParser.MODE_DECLARATION_VALUE, source);
		CSSTextToken[] tokens = parser.getTokens();
		if (tokens.length <= 0) {
			return null;
		}
		String[] idents = new String[4];
		int j = 0;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_HASH)
				idents[j++] = tokens[i].image;
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION) {
				StringBuffer buf = new StringBuffer();
				while (i < tokens.length) {
					if (tokens[i].kind == CSSRegionContexts.CSS_COMMENT) {
						i++;
						continue;
					}
					buf.append(tokens[i].image);
					if (tokens[i++].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE)
						break;
				}
				i--;
				idents[j++] = buf.toString();
			}
			if (j == 4)
				break;
		}

		if (j == 0)
			return null;
		else if (j == 1)
			return idents[0];
		else if (j == 2) {
			if (propDest.getName() == PropCMProperty.P_BORDER_TOP_COLOR || propDest.getName() == PropCMProperty.P_BORDER_BOTTOM_COLOR)
				return idents[0];
			else
				return idents[1];
		}
		else if (j == 3) {
			if (propDest.getName() == PropCMProperty.P_BORDER_TOP_COLOR)
				return idents[0];
			else if (propDest.getName() == PropCMProperty.P_BORDER_BOTTOM_COLOR)
				return idents[2];
			else
				return idents[1];
		}
		else {
			if (propDest.getName() == PropCMProperty.P_BORDER_TOP_COLOR)
				return idents[0];
			else if (propDest.getName() == PropCMProperty.P_BORDER_RIGHT_COLOR)
				return idents[1];
			else if (propDest.getName() == PropCMProperty.P_BORDER_BOTTOM_COLOR)
				return idents[2];
			else if (propDest.getName() == PropCMProperty.P_BORDER_LEFT_COLOR)
				return idents[3];
			else
				return null;
		}

	}
}
