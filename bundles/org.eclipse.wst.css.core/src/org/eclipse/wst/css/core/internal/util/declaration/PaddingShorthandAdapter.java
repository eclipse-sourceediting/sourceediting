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
 * For 'padding' property 'padding' will be expanded to 'padding-top',
 * 'padding-right', 'padding-bottom', 'padding-left',
 */
public class PaddingShorthandAdapter implements IShorthandAdapter {

	/**
	 * 
	 */
	public PaddingShorthandAdapter() {
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
			if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE)
				idents[j++] = tokens[i].image;
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
		dest.set(PropCMProperty.P_PADDING_TOP, dests[0]);
		dest.set(PropCMProperty.P_PADDING_RIGHT, dests[1]);
		dest.set(PropCMProperty.P_PADDING_BOTTOM, dests[2]);
		dest.set(PropCMProperty.P_PADDING_LEFT, dests[3]);

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
			if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE)
				idents[j++] = tokens[i].image;
			if (j == 4)
				break;
		}

		if (j == 0)
			return null;
		else if (j == 1)
			return idents[0];
		else if (j == 2) {
			if (propDest.getName() == PropCMProperty.P_PADDING_TOP || propDest.getName() == PropCMProperty.P_PADDING_BOTTOM)
				return idents[0];
			else
				return idents[1];
		}
		else if (j == 3) {
			if (propDest.getName() == PropCMProperty.P_PADDING_TOP)
				return idents[0];
			else if (propDest.getName() == PropCMProperty.P_PADDING_BOTTOM)
				return idents[2];
			else
				return idents[1];
		}
		else {
			if (propDest.getName() == PropCMProperty.P_PADDING_TOP)
				return idents[0];
			else if (propDest.getName() == PropCMProperty.P_PADDING_RIGHT)
				return idents[1];
			else if (propDest.getName() == PropCMProperty.P_PADDING_BOTTOM)
				return idents[2];
			else if (propDest.getName() == PropCMProperty.P_PADDING_LEFT)
				return idents[3];
			else
				return null;
		}

	}
}
