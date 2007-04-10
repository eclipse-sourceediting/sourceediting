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
package org.eclipse.wst.css.core.internal.formatter;



import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.cleanup.CSSCleanupStrategy;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.w3c.dom.css.CSSPrimitiveValue;


/**
 * 
 */
public class PrimitiveValueFormatter extends DefaultCSSSourceFormatter {

	private static PrimitiveValueFormatter instance;

	/**
	 * 
	 */
	PrimitiveValueFormatter() {
		super();
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, StringBuffer source) {
		int start = ((IndexedRegion) node).getStartOffset();
		int end = ((IndexedRegion) node).getEndOffset();
		Preferences preferences = CSSCorePlugin.getDefault().getPluginPreferences();
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		if (end > 0) { // format source
			IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
			CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, new FormatRegion(start, end - start), stgy);
			boolean appendQuote = regions.length > 1 && node.getParentNode() instanceof ICSSStyleDeclItem && isCleanup() && getCleanupStrategy(node).isQuoteValues() && (((ICSSStyleDeclItem) node.getParentNode()).getPropertyName().equals(PropCMProperty.P_FONT) || ((ICSSStyleDeclItem) node.getParentNode()).getPropertyName().equals(PropCMProperty.P_FONT_FAMILY) || ((ICSSStyleDeclItem) node.getParentNode()).getPropertyName().equals(PropCMProperty.P_VOICE_FAMILY));
			String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);

			StringBuffer strBuf = new StringBuffer();
			boolean skipSpace = false;
			for (int i = 0; i < regions.length; i++) {
				if (i != 0 && !skipSpace)
					appendSpaceBefore(node, regions[i], strBuf);
				skipSpace = false;
				strBuf.append(decoratedPropValueRegion(regions[i], stgy));
				if (regions[i].getType() == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION)
					skipSpace = true;
			}

			if (appendQuote) {
				source.append(quote);
				String str = strBuf.toString();
				str = str.replace('\'', ' ');
				str = str.replace('\"', ' ');
				str = str.trim();
				source.append(str);
				source.append(quote);
			}
			else {
				source.append(strBuf);
			}
		}
		else { // generate source
			ICSSPrimitiveValue value = (ICSSPrimitiveValue) node;
			short type = value.getPrimitiveType();
			String quote = preferences.getString(CSSCorePreferenceNames.FORMAT_QUOTE);

			String str = null;
			switch (type) {
				case CSSPrimitiveValue.CSS_NUMBER :
				case CSSPrimitiveValue.CSS_PERCENTAGE :
				case CSSPrimitiveValue.CSS_EMS :
				case CSSPrimitiveValue.CSS_EXS :
				case CSSPrimitiveValue.CSS_PX :
				case CSSPrimitiveValue.CSS_CM :
				case CSSPrimitiveValue.CSS_MM :
				case CSSPrimitiveValue.CSS_IN :
				case CSSPrimitiveValue.CSS_PT :
				case CSSPrimitiveValue.CSS_PC :
				case CSSPrimitiveValue.CSS_DEG :
				case CSSPrimitiveValue.CSS_RAD :
				case CSSPrimitiveValue.CSS_GRAD :
				case CSSPrimitiveValue.CSS_MS :
				case CSSPrimitiveValue.CSS_S :
				case CSSPrimitiveValue.CSS_HZ :
				case CSSPrimitiveValue.CSS_KHZ :
				case CSSPrimitiveValue.CSS_DIMENSION :
				case ICSSPrimitiveValue.CSS_INTEGER :
					if (value.getFloatValue(type) == ((int) value.getFloatValue(type))) {
						str = Integer.toString((int) value.getFloatValue(type));
					}
					else {
						str = Float.toString(value.getFloatValue(type));
					}
					break;
				case CSSPrimitiveValue.CSS_IDENT :
				case ICSSPrimitiveValue.CSS_HASH :
				case ICSSPrimitiveValue.CSS_INHERIT_PRIMITIVE :
					str = value.getStringValue();
					if (str != null) {
						if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER)
							str = str.toUpperCase();
						else
							str.toLowerCase();
					}
					break;
				case CSSPrimitiveValue.CSS_STRING :
				case CSSPrimitiveValue.CSS_URI :
				case CSSPrimitiveValue.CSS_ATTR :
				case ICSSPrimitiveValue.CSS_URANGE :
				case ICSSPrimitiveValue.CSS_FORMAT :
				case ICSSPrimitiveValue.CSS_LOCAL :
				case ICSSPrimitiveValue.CSS_COMMA :
				case ICSSPrimitiveValue.CSS_SLASH :
					str = value.getStringValue();
			}

			String preStr = null, postStr = null;
			switch (type) {
				case CSSPrimitiveValue.CSS_ATTR :
					preStr = "attr(";//$NON-NLS-1$
					postStr = ")";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_CM :
					postStr = "cm";//$NON-NLS-1$
					break;
				// case ICSSPrimitiveValue.CSS_COUNTER:
				case CSSPrimitiveValue.CSS_DEG :
					postStr = "deg";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_EMS :
					postStr = "em";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_EXS :
					postStr = "ex";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_GRAD :
					postStr = "grad";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_HZ :
					postStr = "Hz";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_IN :
					postStr = "in";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_KHZ :
					postStr = "kHz";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_MM :
					postStr = "mm";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_MS :
					postStr = "ms";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_PC :
					postStr = "pc";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_PERCENTAGE :
					postStr = "%";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_PT :
					postStr = "pt";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_PX :
					postStr = "px";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_RAD :
					postStr = "rad";//$NON-NLS-1$
					break;
				// case ICSSPrimitiveValue.CSS_RECT:
				// case ICSSPrimitiveValue.CSS_RGBCOLOR:
				case CSSPrimitiveValue.CSS_S :
					postStr = "s";//$NON-NLS-1$
					break;
				case CSSPrimitiveValue.CSS_STRING :
					quote = CSSUtil.detectQuote(str, quote);
					preStr = quote;
					postStr = quote;
					break;
				case CSSPrimitiveValue.CSS_URI :
					quote = CSSUtil.detectQuote(str, quote);
					preStr = "url(" + quote;//$NON-NLS-1$
					postStr = quote + ")";//$NON-NLS-1$
					break;
				// the followings are original primitive values
				case CSSPrimitiveValue.CSS_IDENT :
				case CSSPrimitiveValue.CSS_UNKNOWN :
				// use str
				case ICSSPrimitiveValue.CSS_INTEGER :
				case ICSSPrimitiveValue.CSS_HASH :
				case ICSSPrimitiveValue.CSS_SLASH :
				case ICSSPrimitiveValue.CSS_COMMA :
				case CSSPrimitiveValue.CSS_DIMENSION :
				case CSSPrimitiveValue.CSS_NUMBER :
				case ICSSPrimitiveValue.CSS_INHERIT_PRIMITIVE :
					break;
				case ICSSPrimitiveValue.CSS_URANGE :
					preStr = "U+";//$NON-NLS-1$
					break;
				case ICSSPrimitiveValue.CSS_FORMAT :
					quote = CSSUtil.detectQuote(str, quote);
					preStr = "format(" + quote;//$NON-NLS-1$
					postStr = quote + ")";//$NON-NLS-1$
					break;
				case ICSSPrimitiveValue.CSS_LOCAL :
					quote = CSSUtil.detectQuote(str, quote);
					preStr = "local(" + quote;//$NON-NLS-1$
					postStr = quote + ")";//$NON-NLS-1$
					break;
			}
			if (preferences.getInt(CSSCorePreferenceNames.CASE_PROPERTY_VALUE) == CSSCorePreferenceNames.UPPER) {
				if (preStr != null)
					preStr = preStr.toUpperCase();
				if (postStr != null)
					postStr = postStr.toUpperCase();
			}
			if (preStr != null)
				source.append(preStr);
			if (str != null)
				source.append(str);
			if (postStr != null)
				source.append(postStr);
		}
	}

	/**
	 * 
	 */
	protected void formatPre(ICSSNode node, IRegion region, StringBuffer source) {
		CSSCleanupStrategy stgy = getCleanupStrategy(node);

		IStructuredDocument structuredDocument = node.getOwnerDocument().getModel().getStructuredDocument();
		CompoundRegion[] regions = getRegionsWithoutWhiteSpaces(structuredDocument, region, stgy);
		CompoundRegion[] outside = getOutsideRegions(structuredDocument, region);
		for (int i = 0; i < regions.length; i++) {
			if (i != 0 || needS(outside[0]))
				appendSpaceBefore(node, regions[i], source);
			source.append(decoratedPropValueRegion(regions[i], stgy));
		}
		if (needS(outside[1]) && !isIncludesPreEnd(node, region))
			appendSpaceBefore(node, outside[1], source);
	}

	/**
	 * 
	 */
	public synchronized static PrimitiveValueFormatter getInstance() {
		if (instance == null)
			instance = new PrimitiveValueFormatter();
		return instance;
	}
}
