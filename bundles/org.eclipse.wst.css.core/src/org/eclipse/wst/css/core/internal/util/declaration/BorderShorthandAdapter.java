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
 * For 'border' property 'border' will be expanded to 'border-color',
 * 'border-style', 'border-width',
 */
public class BorderShorthandAdapter implements IShorthandAdapter {

	/**
	 * 
	 */
	public BorderShorthandAdapter() {
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
		String color = "", style = "", width = "";//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		PropCMProperty propColor = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLOR);
		PropCMProperty propStyle = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_STYLE);
		PropCMProperty propWidth = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_WIDTH);

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				if (propStyle.canHave(tokens[i].image))
					style = tokens[i].image;
				else if (propWidth.canHave(tokens[i].image))
					width = tokens[i].image;
				else if (propColor.canHave(tokens[i].image))
					color = tokens[i].image;
			}
			else if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER) {
				width = tokens[i].image;
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_HASH) {
				color = tokens[i].image;
			}
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
				color = buf.toString();
			}
		}

		dest.set(propColor.getName(), color);
		dest.set(propStyle.getName(), style);
		dest.set(propWidth.getName(), width);

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
		String color = null, style = null, width = null;
		PropCMProperty propColor = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_COLOR);
		PropCMProperty propStyle = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_STYLE);
		PropCMProperty propWidth = PropCMProperty.getInstanceOf(PropCMProperty.P_BORDER_WIDTH);

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				if (propStyle.canHave(tokens[i].image))
					style = tokens[i].image;
				else if (propWidth.canHave(tokens[i].image))
					width = tokens[i].image;
				else if (propColor.canHave(tokens[i].image))
					color = tokens[i].image;
			}
			else if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER) {
				width = tokens[i].image;
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_HASH) {
				color = tokens[i].image;
			}
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
				color = buf.toString();
			}
		}

		if (propColor == propDest)
			return color;
		else if (propStyle == propDest)
			return style;
		else if (propWidth == propDest)
			return width;
		else
			return null;
	}
}
