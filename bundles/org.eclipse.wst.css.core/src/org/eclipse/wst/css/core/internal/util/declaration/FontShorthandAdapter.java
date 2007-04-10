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



import org.eclipse.wst.css.core.internal.contentmodel.IValID;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.parserz.CSSTextParser;
import org.eclipse.wst.css.core.internal.parserz.CSSTextToken;


/**
 * For 'font' property 'font' will be expanded to 'font-family', 'font-size',
 * 'font-style', 'font-variant', 'font-weight', 'line-height',
 */
public class FontShorthandAdapter implements IShorthandAdapter {

	/**
	 * 
	 */
	public FontShorthandAdapter() {
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
		String style = null, variant = null, weight = null, size = null, height = null, family = null;
		PropCMProperty propFont = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT);
		PropCMProperty propStyle = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STYLE);
		PropCMProperty propVariant = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_VARIANT);
		PropCMProperty propWeight = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_WEIGHT);
		PropCMProperty propSize = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE);
		boolean bNormalSpecified = false;

		int i = 0;
		for (; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				// first of all, check font idents
				if (i == 0) {
					for (int j = 0; j < propFont.getNumChild(); j++) {
						Object obj = propFont.getChildAt(i);
						if (obj instanceof String && tokens[i].image.compareToIgnoreCase(obj.toString()) == 0)
							return false;
					}
				}
				// value "normal" is shared !!
				if (tokens[i].image.equalsIgnoreCase(IValID.V_NORMAL)) {
					bNormalSpecified = true;
				}
				else {
					if (propStyle.canHave(tokens[i].image))
						style = tokens[i].image;
					else if (propVariant.canHave(tokens[i].image))
						variant = tokens[i].image;
					else if (propWeight.canHave(tokens[i].image))
						weight = tokens[i].image;
					else if (propSize.canHave(tokens[i].image)) {
						size = tokens[i].image;
						break; // if size found, break loop
					}
				}
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER && weight == null && propWeight.canHave(tokens[i].image)) {
				weight = tokens[i].image;
			}
			else if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE) {
				size = tokens[i].image;
				break; // if size found, break loop
			}
		}

		if (bNormalSpecified) {
			if (style == null)
				style = IValID.V_NORMAL;
			if (variant == null)
				variant = IValID.V_NORMAL;
			if (weight == null)
				weight = IValID.V_NORMAL;
		}

		// skip whitespace
		for (i++; i < tokens.length; i++) {
			if (tokens[i].kind != CSSRegionContexts.CSS_S || tokens[i].kind != CSSRegionContexts.CSS_DECLARATION_VALUE_S)
				break;
		}

		// line-height ?
		if (i < tokens.length && tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR && tokens[i].image.equals("/")) { //$NON-NLS-1$
			for (i++; i < tokens.length; i++) {
				if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
					height = tokens[i++].image;
					break;
				}
			}
		}

		// font-family
		StringBuffer buf = new StringBuffer();
		for (; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_COMMENT)
				buf.append(" ");//$NON-NLS-1$
			else
				buf.append(tokens[i].image);
		}
		family = buf.toString().trim();

		dest.set(PropCMProperty.P_FONT_STYLE, style);
		dest.set(PropCMProperty.P_FONT_VARIANT, variant);
		dest.set(PropCMProperty.P_FONT_WEIGHT, weight);
		dest.set(PropCMProperty.P_FONT_SIZE, size);
		dest.set(PropCMProperty.P_LINE_HEIGHT, height);
		dest.set(PropCMProperty.P_FONT_FAMILY, family);

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
		String style = null, variant = null, weight = null, size = null, height = null, family = null;
		PropCMProperty propFont = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT);
		PropCMProperty propStyle = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_STYLE);
		PropCMProperty propVariant = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_VARIANT);
		PropCMProperty propWeight = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_WEIGHT);
		PropCMProperty propSize = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_SIZE);
		PropCMProperty propHeight = PropCMProperty.getInstanceOf(PropCMProperty.P_LINE_HEIGHT);
		PropCMProperty propFamily = PropCMProperty.getInstanceOf(PropCMProperty.P_FONT_FAMILY);
		boolean bNormalSpecified = false;

		int i = 0;
		for (; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				// first of all, check font idents
				if (i == 0) {
					for (int j = 0; j < propFont.getNumChild(); j++) {
						Object obj = propFont.getChildAt(i);
						if (obj instanceof String && tokens[i].image.compareToIgnoreCase(obj.toString()) == 0)
							return null;
					}
				}
				// value "normal" is shared !!
				if (tokens[i].image.equalsIgnoreCase(IValID.V_NORMAL)) {
					bNormalSpecified = true;
				}
				else {
					if (propStyle.canHave(tokens[i].image))
						style = tokens[i].image;
					else if (propVariant.canHave(tokens[i].image))
						variant = tokens[i].image;
					else if (propWeight.canHave(tokens[i].image))
						weight = tokens[i].image;
					else if (propSize.canHave(tokens[i].image)) {
						size = tokens[i].image;
						break; // if size found, break loop
					}
				}
			}
			else if (tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER && weight == null && propWeight.canHave(tokens[i].image)) {
				weight = tokens[i].image;
			}
			else if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE) {
				size = tokens[i].image;
				break; // if size found, break loop
			}
		}

		if (bNormalSpecified) {
			if (style == null)
				style = IValID.V_NORMAL;
			if (variant == null)
				variant = IValID.V_NORMAL;
			if (weight == null)
				weight = IValID.V_NORMAL;
		}

		// skip whitespace
		for (i++; i < tokens.length; i++) {
			if (tokens[i].kind != CSSRegionContexts.CSS_S || tokens[i].kind != CSSRegionContexts.CSS_DECLARATION_VALUE_S)
				break;
		}

		// line-height ?
		if (i < tokens.length && tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR && tokens[i].image.equals("/")) { //$NON-NLS-1$
			for (i++; i < tokens.length; i++) {
				if (org.eclipse.wst.css.core.internal.util.CSSUtil.isLength(tokens[i]) || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || tokens[i].kind == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
					height = tokens[i++].image;
					break;
				}
			}
		}

		// font-family
		StringBuffer buf = new StringBuffer();
		for (; i < tokens.length; i++) {
			if (tokens[i].kind == CSSRegionContexts.CSS_COMMENT)
				buf.append(" ");//$NON-NLS-1$
			else
				buf.append(tokens[i].image);
		}
		family = buf.toString().trim();

		if (propStyle == propDest)
			return style;
		else if (propVariant == propDest)
			return variant;
		else if (propWeight == propDest)
			return weight;
		else if (propSize == propDest)
			return size;
		else if (propHeight == propDest)
			return height;
		else if (propFamily == propDest)
			return family;
		else
			return null;
	}
}
