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
 * For 'list-style' property 'list-style' will be expanded to
 * 'list-style-image', 'list-style-position', 'list-style-type',
 */
public class ListStyleShorthandAdapter implements IShorthandAdapter {

	/**
	 * 
	 */
	public ListStyleShorthandAdapter() {
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
		String image = "", pos = "", type = "";//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
		PropCMProperty propPos = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_POSITION);
		PropCMProperty propType = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_TYPE);
		PropCMProperty propImage = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_IMAGE);

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				if (propPos.canHave(tokens[i].image))
					pos = tokens[i].image;
				else { // value="none" is shared !!
					if (propType.canHave(tokens[i].image))
						type = tokens[i].image;
					if (propImage.canHave(tokens[i].image))
						image = tokens[i].image;
				}
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_URI) {
				image = tokens[i].image;
			}
		}

		dest.set(propPos.getName(), pos);
		dest.set(propType.getName(), type);
		dest.set(propImage.getName(), image);

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
		String image = null, pos = null, type = null;
		PropCMProperty propPos = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_POSITION);
		PropCMProperty propType = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_TYPE);
		PropCMProperty propImage = PropCMProperty.getInstanceOf(PropCMProperty.P_LIST_STYLE_IMAGE);

		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				if (propPos.canHave(tokens[i].image))
					pos = tokens[i].image;
				else { // value="none" is shared !!
					if (propType.canHave(tokens[i].image))
						type = tokens[i].image;
					if (propImage.canHave(tokens[i].image))
						image = tokens[i].image;
				}
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_URI) {
				image = tokens[i].image;
			}
		}

		if (propPos == propDest)
			return pos;
		else if (propType == propDest)
			return type;
		else if (propImage == propDest)
			return image;
		else
			return null;
	}
}
