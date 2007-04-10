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
 * For 'background' property 'background' will be expanded to
 * 'background-attachment', 'background-color', 'background-image',
 * 'background-position', 'background-repeat',
 */
public class BackgroundShorthandAdapter implements IShorthandAdapter {

	/**
	 * 
	 */
	public BackgroundShorthandAdapter() {
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
		String color = "", image = "", repeat = "", attach = "", pos = "";//$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		PropCMProperty propColor = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_COLOR);
		PropCMProperty propImage = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_IMAGE);
		PropCMProperty propRepeat = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_REPEAT);
		PropCMProperty propAttach = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_ATTACHMENT);
		PropCMProperty propPos = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_POSITION);

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				if (propColor.canHave(tokens[i].image))
					color = tokens[i].image;
				else if (propImage.canHave(tokens[i].image))
					image = tokens[i].image;
				else if (propRepeat.canHave(tokens[i].image))
					repeat = tokens[i].image;
				else if (propAttach.canHave(tokens[i].image))
					attach = tokens[i].image;
				else if (propPos.canHave(tokens[i].image)) {
					if (pos == null || pos.length() <= 0)
						pos = tokens[i].image;
					else
						pos = pos + " " + tokens[i].image;//$NON-NLS-1$
				}
			}
			else if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE) {
				if (pos == null || pos.length() <= 0)
					pos = tokens[i].image;
				else
					pos = pos + " " + tokens[i].image;//$NON-NLS-1$
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_URI) {
				image = tokens[i].image;
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
		dest.set(propImage.getName(), image);
		dest.set(propRepeat.getName(), repeat);
		dest.set(propAttach.getName(), attach);
		dest.set(propPos.getName(), pos);

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
		String color = null, image = null, repeat = null, attach = null, pos = null;
		PropCMProperty propColor = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_COLOR);
		PropCMProperty propImage = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_IMAGE);
		PropCMProperty propRepeat = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_REPEAT);
		PropCMProperty propAttach = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_ATTACHMENT);
		PropCMProperty propPos = PropCMProperty.getInstanceOf(PropCMProperty.P_BG_POSITION);

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				if (propColor.canHave(tokens[i].image))
					color = tokens[i].image;
				else if (propImage.canHave(tokens[i].image))
					image = tokens[i].image;
				else if (propRepeat.canHave(tokens[i].image))
					repeat = tokens[i].image;
				else if (propAttach.canHave(tokens[i].image))
					attach = tokens[i].image;
				else if (propPos.canHave(tokens[i].image)) {
					if (pos == null)
						pos = tokens[i].image;
					else
						pos = pos + " " + tokens[i].image;//$NON-NLS-1$
				}
			}
			else if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE) {
				if (pos == null)
					pos = tokens[i].image;
				else
					pos = pos + " " + tokens[i].image;//$NON-NLS-1$
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_URI) {
				image = tokens[i].image;
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
		else if (propImage == propDest)
			return image;
		else if (propRepeat == propDest)
			return repeat;
		else if (propAttach == propDest)
			return attach;
		else if (propPos == propDest)
			return pos;
		else
			return null;
	}
}
