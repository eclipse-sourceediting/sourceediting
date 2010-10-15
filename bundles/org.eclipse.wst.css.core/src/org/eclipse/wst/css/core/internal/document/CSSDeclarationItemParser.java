/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.document;

import java.util.Iterator;

import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.parserz.CSSRegionContexts;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICounter;
import org.eclipse.wst.css.core.internal.util.CSSUtil;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.TextRegionListImpl;
import org.w3c.dom.css.CSSPrimitiveValue;


/**
 * 
 */
class CSSDeclarationItemParser {
	/**
	 * 
	 */
	final class FloatInfo {
		/**
		 * 
		 */
		FloatInfo(String text) {
			parse(text);
		}

		/**
		 * 
		 */
		void parse(String text) {
			StringBuffer bufValue = new StringBuffer();
			StringBuffer bufIdent = new StringBuffer();
			boolean bNum = true;
			int len = text.length();
			for (int i = 0; i < len; i++) {
				char c = text.charAt(i);
				if (bNum) {
					// Only add +/- if it's the first character in the value buffer
					if ('0' <= c && c <= '9' || c == '.' || ((c == '+' || c == '-') && i == 0)) {
						bufValue.append(c);
					}
					else {
						bufIdent.append(c);
						bNum = false;
					}
				}
				else {
					bufIdent.append(c);
				}
			}
			String valueStr = bufValue.toString();
			try {
				fValue = Float.valueOf(valueStr).floatValue();
			}
			catch (NumberFormatException e) {
				bufIdent.insert(0, valueStr);
			}
			fIdentifier = bufIdent.toString();
			fType = getFloatValueType(valueStr, fIdentifier);
		}

		/**
		 * 
		 */
		float getValue() {
			return fValue;
		}

		/**
		 * 
		 */
		String getIdentifier() {
			return fIdentifier;
		}

		/**
		 * 
		 */
		short getValueType() {
			return fType;
		}

		private float fValue = 0.0f;
		private String fIdentifier = null;
		private short fType = CSSPrimitiveValue.CSS_UNKNOWN;
	}

	final static int S_NORMAL = 0;
	final static int S_FUNCTION = 1;
	final static int S_FONT_SLASH = 2;
	final static int S_COMMA_SEPARATION = 3;
	private ICSSDocument fDocument = null;
	private IStructuredDocumentRegion fParentRegion = null;
	private boolean fTempStructuredDocument = false;
	private CSSModelUpdateContext fUpdateContext = null;

	/**
	 * CSSDeclarationItemParser constructor comment.
	 */
	CSSDeclarationItemParser(ICSSDocument doc) {
		super();
		fDocument = doc;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createAttrValue(ITextRegionList regions) {
		String funcName = getFunctionName(regions);
		if (funcName == null || !funcName.toLowerCase().equals("attr")) { //$NON-NLS-1$
			return null;
		}
		String accepts[] = {CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT};
		ITextRegionList valueRegions = getFunctionParameters(regions, accepts);
		if (valueRegions.size() != 1) {
			return null;
		}

		CSSPrimitiveValueImpl value = getCSSPrimitiveValue(CSSPrimitiveValue.CSS_ATTR);
		if (value == null) {
			return null;
		}

		ITextRegion region = valueRegions.get(0);
		value.setValue(getText(region));

		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createCountersValue(ITextRegionList regions) {
		String funcName = getFunctionName(regions);
		if (funcName == null || !funcName.toLowerCase().equals("counters")) { //$NON-NLS-1$
			return null;
		}

		String accepts[] = {CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT, CSSRegionContexts.CSS_DECLARATION_VALUE_STRING};
		ITextRegionList valueRegions = getFunctionParameters(regions, accepts);
		int size = valueRegions.size();
		if (size != 2 && size != 3) {
			return null;
		}

		CounterImpl value = getCounter();
		if (value == null) {
			return null;
		}

		for (int i = 0; i < size; i++) {
			ITextRegion region = valueRegions.get(i);
			String text = getText(region);
			CSSAttrImpl attr = null;
			switch (i) {
				case 0 :
					value.setIdentifier(text);
					attr = value.getAttributeNode(ICounter.IDENTIFIER);
					break;
				case 1 :
					value.setSeparator(text);
					attr = value.getAttributeNode(ICounter.SEPARATOR);
					break;
				case 2 :
					value.setListStyle(text);
					attr = value.getAttributeNode(ICounter.LISTSTYLE);
					break;
				default :
					break;
			}
			if (attr != null) {
				attr.setRangeRegion(fParentRegion, region, region);
			}
		}

		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createCounterValue(ITextRegionList regions) {
		String funcName = getFunctionName(regions);
		if (funcName == null || !funcName.toLowerCase().equals("counter")) { //$NON-NLS-1$
			return null;
		}

		String accepts[] = {CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT};
		ITextRegionList valueRegions = getFunctionParameters(regions, accepts);
		int size = valueRegions.size();
		if (size != 1 && size != 2) {
			return null;
		}

		CounterImpl value = getCounter();
		if (value == null) {
			return null;
		}

		for (int i = 0; i < size; i++) {
			ITextRegion region = valueRegions.get(i);
			String text = getText(region);
			CSSAttrImpl attr = null;
			switch (i) {
				case 0 :
					value.setIdentifier(text);
					attr = value.getAttributeNode(ICounter.IDENTIFIER);
					break;
				case 1 :
					value.setListStyle(text);
					attr = value.getAttributeNode(ICounter.LISTSTYLE);
					break;
				default :
					break;
			}
			if (attr != null) {
				attr.setRangeRegion(fParentRegion, region, region);
			}
		}

		return value;
	}

	/**
	 * 
	 */
	private CSSStyleDeclItemImpl createDeclarationItem(ITextRegionList nodeRegions) {
		CSSStyleDeclItemImpl item = null;
		String name = getPropertyName(nodeRegions);
		if (name != null) {
			item = getCSSStyleDeclItem(name);
		}
		return item;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createFloatValue(String text, String type) {
		FloatInfo info = new FloatInfo(text);
		CSSPrimitiveValueImpl value = getCSSPrimitiveValue(info.getValueType());
		if (value != null) {
			value.setValue(info.getValue());
		}
		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createFormatValue(ITextRegionList regions) {
		String funcName = getFunctionName(regions);
		if (funcName == null || !funcName.toLowerCase().equals("format")) { //$NON-NLS-1$
			return null;
		}
		String accepts[] = {CSSRegionContexts.CSS_DECLARATION_VALUE_STRING};
		ITextRegionList valueRegions = getFunctionParameters(regions, accepts);
		// format can take variable args.
		if (valueRegions.size() == 0) {
			return null;
		}

		CSSPrimitiveValueImpl value = getCSSPrimitiveValue(ICSSPrimitiveValue.CSS_FORMAT);
		if (value == null) {
			return null;
		}

		ITextRegion region = valueRegions.get(0);
		value.setValue(CSSUtil.extractStringContents(getText(region)));

		return value;

	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createLocalValue(ITextRegionList regions) {
		String funcName = getFunctionName(regions);
		if (funcName == null || !funcName.toLowerCase().equals("local")) { //$NON-NLS-1$
			return null;
		}
		String accepts[] = {CSSRegionContexts.CSS_DECLARATION_VALUE_STRING};
		ITextRegionList valueRegions = getFunctionParameters(regions, accepts);
		if (valueRegions.size() != 1) {
			return null;
		}

		CSSPrimitiveValueImpl value = getCSSPrimitiveValue(ICSSPrimitiveValue.CSS_LOCAL);
		if (value == null) {
			return null;
		}

		ITextRegion region = valueRegions.get(0);
		value.setValue(CSSUtil.extractStringContents(getText(region)));

		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createPrimitiveValue(ITextRegion region) {
		if (region == null) {
			return null;
		}
		CSSPrimitiveValueImpl value = null;
		String type = region.getType();
		String text = getText(region);
		if (isBlank(type)) {
			value = null;
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER || type == CSSRegionContexts.CSS_DECLARATION_VALUE_DIMENSION || type == CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE) {
			value = createFloatValue(text, type);
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_STRING || type == CSSRegionContexts.CSS_DECLARATION_VALUE_URI || type == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT || type == CSSRegionContexts.CSS_DECLARATION_VALUE_HASH) {
			value = createStringValue(text, type);
		}

		if (value == null) {
			value = createStringValue(text, type);
		}

		if (!fTempStructuredDocument && value != null) {
			value.setRangeRegion(fParentRegion, region, region);
		}

		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createPrimitiveValue(ITextRegionList regions) {
		CSSPrimitiveValueImpl value = null;
		CSSUtil.stripSurroundingSpace(regions);
		if (regions.isEmpty()) {
			return null;
		}
		ITextRegion region = regions.get(0);
		if (region == null) {
			return null;
		}
		String type = region.getType();
		if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION) {
			String text = getText(region).toLowerCase();
			if (text.equals("rgb(")) { //$NON-NLS-1$
				value = createRgbValue(regions);
			}
			else if (text.equals("counter(")) { //$NON-NLS-1$
				value = createCounterValue(regions);
			}
			else if (text.equals("counters(")) { //$NON-NLS-1$
				value = createCountersValue(regions);
			}
			else if (text.equals("attr(")) { //$NON-NLS-1$
				value = createAttrValue(regions);
			}
			else if (text.equals("format(")) { //$NON-NLS-1$
				value = createFormatValue(regions);
			}
			else if (text.equals("local(")) { //$NON-NLS-1$
				value = createLocalValue(regions);
			}
			else if (text.equals("rect(")) { //$NON-NLS-1$
				value = createRectValue(regions);
			}
			if (value == null) {
				value = createStringValue(regions);
			}
		}
		else {
			value = createStringValue(regions);
		}

		if (!fTempStructuredDocument && value != null) {
			value.setRangeRegion(fParentRegion, regions.get(0), regions.get(regions.size() - 1));
		}

		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createRectValue(ITextRegionList regions) {
		String funcName = getFunctionName(regions);
		if (funcName == null || !funcName.toLowerCase().equals("rect")) { //$NON-NLS-1$
			return null;
		}
		String accepts[] = {CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER, CSSRegionContexts.CSS_DECLARATION_VALUE_DIMENSION, CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT}; // IDENT:
																																												// for
																																												// 'auto'
		ITextRegionList valueRegions = getFunctionParameters(regions, accepts);
		if (valueRegions.size() != 4) {
			return null;
		}

		RectImpl value = getRect();
		if (value == null) {
			return null;
		}

		for (int i = 0; i < 4; i++) {
			ITextRegion region = valueRegions.get(i);
			CSSPrimitiveValueImpl childValue = null;
			switch (i) {
				case 0 :
					childValue = (CSSPrimitiveValueImpl) value.getTop();
					break;
				case 1 :
					childValue = (CSSPrimitiveValueImpl) value.getRight();
					break;
				case 2 :
					childValue = (CSSPrimitiveValueImpl) value.getBottom();
					break;
				case 3 :
					childValue = (CSSPrimitiveValueImpl) value.getLeft();
					break;
				default :
					break;
			}
			if (childValue == null) {
				return null;
			}
			String text = getText(region);
			String type = region.getType();
			if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT) {
				setStringValue(childValue, text, type);
			}
			else {
				setFloatValue(childValue, text, type);
			}
			if (!fTempStructuredDocument) {
				childValue.setRangeRegion(fParentRegion, region, region);
			}
		}

		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createRgbValue(ITextRegionList regions) {
		String funcName = getFunctionName(regions);
		if (funcName == null || !funcName.toLowerCase().equals("rgb")) { //$NON-NLS-1$
			return null;
		}
		String accepts[] = {CSSRegionContexts.CSS_DECLARATION_VALUE_NUMBER, CSSRegionContexts.CSS_DECLARATION_VALUE_PERCENTAGE};
		ITextRegionList valueRegions = getFunctionParameters(regions, accepts);
		if (valueRegions.size() != 3) {
			return null;
		}

		RGBColorImpl value = getRGBColor();
		if (value == null) {
			return null;
		}

		for (int i = 0; i < 3; i++) {
			ITextRegion region = valueRegions.get(i);
			CSSPrimitiveValueImpl childValue = null;
			switch (i) {
				case 0 :
					childValue = (CSSPrimitiveValueImpl) value.getRed();
					break;
				case 1 :
					childValue = (CSSPrimitiveValueImpl) value.getGreen();
					break;
				case 2 :
					childValue = (CSSPrimitiveValueImpl) value.getBlue();
					break;
				default :
					break;
			}
			if (childValue == null) {
				return null;
			}
			setFloatValue(childValue, getText(region), region.getType());
			if (!fTempStructuredDocument) {
				childValue.setRangeRegion(fParentRegion, region, region);
			}
		}

		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createStringValue(String text, String type) {
		short valueType = getStringValueType(text, type);
		CSSPrimitiveValueImpl value = getCSSPrimitiveValue(valueType);
		if (value != null) {
			if (valueType == CSSPrimitiveValue.CSS_URI) {
				text = CSSUtil.extractUriContents(text);
			}
			else if (valueType == CSSPrimitiveValue.CSS_STRING) {
				text = CSSUtil.extractStringContents(text);
			}
			value.setValue(text);
		}
		return value;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl createStringValue(ITextRegionList regions) {
		String type = CSSRegionContexts.CSS_DECLARATION_VALUE_IDENT;
		if (regions.size() == 1) {
			ITextRegion region = regions.get(0);
			type = region.getType();
		}
		return createStringValue(makeString(regions), type);
	}

	/**
	 * 
	 */
	private CounterImpl getCounter() {
		CounterImpl node;
		if (fUpdateContext != null && fUpdateContext.isActive()) {
			node = fUpdateContext.getCounter();
		}
		else {
			node = (CounterImpl) fDocument.createCSSPrimitiveValue(CSSPrimitiveValue.CSS_COUNTER);
		}
		return node;
	}

	/**
	 * 
	 */
	private CSSPrimitiveValueImpl getCSSPrimitiveValue(short type) {
		CSSPrimitiveValueImpl node;
		if (fUpdateContext != null && fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSPrimitiveValue(type);
		}
		else {
			node = (CSSPrimitiveValueImpl) fDocument.createCSSPrimitiveValue(type);
		}
		return node;
	}

	/**
	 * 
	 */
	private CSSStyleDeclItemImpl getCSSStyleDeclItem(String propertyName) {
		CSSStyleDeclItemImpl node;
		if (fUpdateContext != null && fUpdateContext.isActive()) {
			node = fUpdateContext.getCSSStyleDeclItem(propertyName);
		}
		else {
			node = (CSSStyleDeclItemImpl) fDocument.createCSSStyleDeclItem(propertyName);
		}
		return node;
	}

	/**
	 * 
	 */
	static short getFloatValueType(String ident) {
		ident = ident.toLowerCase();
		short valueType;
		if (ident.length() == 0) {
			valueType = CSSPrimitiveValue.CSS_NUMBER;
		}
		else if (ident.equals("%")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PERCENTAGE;
		}
		else if (ident.equalsIgnoreCase("em")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_EMS;
		}
		else if (ident.equalsIgnoreCase("ex")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_EXS;
		}
		else if (ident.equalsIgnoreCase("px")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PX;
		}
		else if (ident.equalsIgnoreCase("cm")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_CM;
		}
		else if (ident.equalsIgnoreCase("mm")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_MM;
		}
		else if (ident.equalsIgnoreCase("in")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_IN;
		}
		else if (ident.equalsIgnoreCase("pt")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PT;
		}
		else if (ident.equalsIgnoreCase("pc")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PC;
		}
		else if (ident.equalsIgnoreCase("deg")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_DEG;
		}
		else if (ident.equalsIgnoreCase("rad")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_RAD;
		}
		else if (ident.equalsIgnoreCase("grad")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_GRAD;
		}
		else if (ident.equalsIgnoreCase("ms")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_MS;
		}
		else if (ident.equalsIgnoreCase("s")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_S;
		}
		else if (ident.equalsIgnoreCase("hz")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_HZ;
		}
		else if (ident.equalsIgnoreCase("khz")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_KHZ;
		}
		else {
			valueType = CSSPrimitiveValue.CSS_DIMENSION;
		}
		return valueType;
	}

	/**
	 * 
	 */
	static short getFloatValueType(String value, String ident) {
		ident = ident.toLowerCase();
		short valueType;
		if (ident.length() == 0) {
			if (0 <= value.indexOf('.')) {
				valueType = CSSPrimitiveValue.CSS_NUMBER;
			}
			else {
				valueType = ICSSPrimitiveValue.CSS_INTEGER;
			}
		}
		else if (ident.equals("%")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PERCENTAGE;
		}
		else if (ident.equalsIgnoreCase("em")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_EMS;
		}
		else if (ident.equalsIgnoreCase("ex")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_EXS;
		}
		else if (ident.equalsIgnoreCase("px")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PX;
		}
		else if (ident.equalsIgnoreCase("cm")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_CM;
		}
		else if (ident.equalsIgnoreCase("mm")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_MM;
		}
		else if (ident.equalsIgnoreCase("in")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_IN;
		}
		else if (ident.equalsIgnoreCase("pt")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PT;
		}
		else if (ident.equalsIgnoreCase("pc")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_PC;
		}
		else if (ident.equalsIgnoreCase("deg")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_DEG;
		}
		else if (ident.equalsIgnoreCase("rad")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_RAD;
		}
		else if (ident.equalsIgnoreCase("grad")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_GRAD;
		}
		else if (ident.equalsIgnoreCase("ms")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_MS;
		}
		else if (ident.equalsIgnoreCase("s")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_S;
		}
		else if (ident.equalsIgnoreCase("hz")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_HZ;
		}
		else if (ident.equalsIgnoreCase("khz")) { //$NON-NLS-1$
			valueType = CSSPrimitiveValue.CSS_KHZ;
		}
		else {
			valueType = CSSPrimitiveValue.CSS_DIMENSION;
		}
		return valueType;
	}

	/**
	 * 
	 */
	private String getFunctionName(ITextRegionList regions) {
		if (regions == null || regions.size() < 2) {
			return null;
		}
		ITextRegion firstRegion = regions.get(0);
		if (firstRegion.getType() != CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION) {
			return null;
		}
		ITextRegion lastRegion = regions.get(regions.size() - 1);
		if (lastRegion.getType() != CSSRegionContexts.CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE) {
			return null;
		}
		String text = getText(firstRegion);
		return text.substring(0, text.length() - 1);
	}

	/**
	 * this method has no validation check, then regions must be passed
	 * getFunctionName()...
	 */
	private ITextRegionList getFunctionParameters(ITextRegionList regions, String[] accepts) {
		ITextRegionList newRegions = new TextRegionListImpl();
		int nAccepts = (accepts != null) ? accepts.length : 0;
		Iterator i = regions.iterator();
		i.next(); // skip FUNCTION
		while (i.hasNext()) {
			ITextRegion region = (ITextRegion) i.next();
			if (region == null) {
				continue;
			}
			String type = region.getType();
			if (isBlank(type)) {
				continue;
			}
			if (nAccepts == 0) {
				newRegions.add(region);
			}
			else {
				for (int iAccept = 0; iAccept < nAccepts; iAccept++) {
					if (type == accepts[iAccept]) {
						newRegions.add(region);
						break;
					}
				}
			}
		}
		return newRegions;
	}

	/**
	 * 
	 */
	// private String getPropertyName(IStructuredDocumentRegion flatNode) {
	// Vector nodeRegions = new Vector(flatNode.getRegions());
	// return getPropertyName(nodeRegions);
	// }
	/**
	 * 
	 */
	private String getPropertyName(ITextRegionList nodeRegions) {
		ITextRegionList nameRegions = new TextRegionListImpl();
		String name = null;
		while (!nodeRegions.isEmpty()) {
			ITextRegion region = nodeRegions.remove(0);
			if (region == null) {
				continue;
			}
			String type = region.getType();
			if (type == CSSRegionContexts.CSS_DECLARATION_SEPARATOR) {
				CSSUtil.stripSurroundingSpace(nameRegions);
				name = makeString(nameRegions);
				break;
			}
			else {
				nameRegions.add(region);
			}
		}
		return name;
	}

	/**
	 * 
	 */
	private RectImpl getRect() {
		RectImpl node;
		if (fUpdateContext != null && fUpdateContext.isActive()) {
			node = fUpdateContext.getRect();
		}
		else {
			node = (RectImpl) fDocument.createCSSPrimitiveValue(CSSPrimitiveValue.CSS_RECT);
		}
		return node;
	}

	/**
	 * 
	 */
	private RGBColorImpl getRGBColor() {
		RGBColorImpl node;
		if (fUpdateContext != null && fUpdateContext.isActive()) {
			node = fUpdateContext.getRGBColor();
		}
		else {
			node = (RGBColorImpl) fDocument.createCSSPrimitiveValue(CSSPrimitiveValue.CSS_RGBCOLOR);
		}
		return node;
	}

	/**
	 * 
	 */
	private short getStringValueType(String text, String type) {
		short valueType;
		if (text.toLowerCase().equals("inherit")) { //$NON-NLS-1$
			valueType = ICSSPrimitiveValue.CSS_INHERIT_PRIMITIVE;
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_URI) {
			valueType = CSSPrimitiveValue.CSS_URI;
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_STRING) {
			valueType = CSSPrimitiveValue.CSS_STRING;
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_HASH) {
			valueType = ICSSPrimitiveValue.CSS_HASH;
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR && text.equals(",")) { //$NON-NLS-1$
			valueType = ICSSPrimitiveValue.CSS_COMMA;
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR && text.equals("/")) { //$NON-NLS-1$
			valueType = ICSSPrimitiveValue.CSS_SLASH;
		}
		else if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_UNICODE_RANGE) {
			valueType = ICSSPrimitiveValue.CSS_URANGE;
		}
		else {
			valueType = CSSPrimitiveValue.CSS_IDENT;
		}
		return valueType;
	}

	/**
	 * 
	 */
	private String makeString(ITextRegionList regions) {
		StringBuffer buf = new StringBuffer();
		boolean bSpace = false;
		for (Iterator i = regions.iterator(); i.hasNext();) {
			ITextRegion region = (ITextRegion) i.next();
			String type = region.getType();
			if (!bSpace && isBlank(type)) {
				buf.append(" "); //$NON-NLS-1$
				bSpace = true;
			}
			else {
				// [274945] Multiple regions should have the spaces between collapsed
				String text = i.hasNext() ? getCollapsedText(region) : getText(region);
				buf.append(text);
				bSpace = false;
			}
		}

		return buf.toString();
	}

	/**
	 * 
	 */
	void setStructuredDocumentTemporary(boolean bTemp) {
		fTempStructuredDocument = bTemp;
	}

	/**
	 * 
	 */
	private void setFloatValue(CSSPrimitiveValueImpl value, String text, String type) {
		FloatInfo info = new FloatInfo(text);
		value.setFloatValue(info.getValueType(), info.getValue());
	}

	/**
	 * 
	 */
	private void setStringValue(CSSPrimitiveValueImpl value, String text, String type) {
		short valueType = getStringValueType(text, type);
		value.setStringValue(valueType, text);
	}


	/**
	 * 
	 */
	void setUpdateContext(CSSModelUpdateContext updateContext) {
		fUpdateContext = updateContext;
	}

	/**
	 * 
	 */
	public static boolean hasColonSeparator(IStructuredDocumentRegion flatNode) {
		if (flatNode == null) {
			return false;
		}
		ITextRegionList regions = flatNode.getRegions();
		if (regions == null || regions.size() == 0) {
			return false;
		}

		for (Iterator i = regions.iterator(); i.hasNext();) {
			ITextRegion region = (ITextRegion) i.next();
			if (region == null) {
				continue;
			}
			if (region.getType() == CSSRegionContexts.CSS_DECLARATION_SEPARATOR) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 
	 */
	CSSStyleDeclItemImpl setupDeclarationItem(IStructuredDocumentRegion flatNode) {
		if (flatNode == null) {
			return null;
		}
		if (!hasColonSeparator(flatNode)) {
			return null;
		}

		fParentRegion = flatNode;

		ITextRegionList nodeRegions = new TextRegionListImpl(flatNode.getRegions()); // make
		// copy
		CSSStyleDeclItemImpl newItem = createDeclarationItem(nodeRegions);
		if (newItem == null) {
			return null;
		}
		if (!fTempStructuredDocument && flatNode != null) {
			newItem.setRangeStructuredDocumentRegion(flatNode, flatNode);
		}

		CSSUtil.stripSurroundingSpace(nodeRegions);
		// Now, nodeRegions just has regions for value.
		setupValues(newItem, nodeRegions);
		return newItem;
	}

	void setupValues(ICSSStyleDeclItem item, IStructuredDocumentRegion parentRegion, ITextRegionList nodeRegions) {
		fParentRegion = parentRegion;
		setupValues(item, nodeRegions);
	}

	/**
	 * nodeRegions must be broken. If you need after, make copy of them.
	 */
	private void setupValues(ICSSStyleDeclItem item, ITextRegionList nodeRegions) {
		if (item == null) {
			return;
		}

		ICSSPrimitiveValue value;
		ITextRegionList regionBuf = new TextRegionListImpl();

		String propertyName = item.getPropertyName().toLowerCase();
		boolean bFont = (propertyName.equals(PropCMProperty.P_FONT));
		// (short-hand) font
		int status = (propertyName.equals(PropCMProperty.P_VOICE_FAMILY) || propertyName.equals(PropCMProperty.P_FONT_FAMILY)) ? S_COMMA_SEPARATION : S_NORMAL;
		while (!nodeRegions.isEmpty()) {
			value = null;
			ITextRegion region = nodeRegions.remove(0);
			if (region == null) {
				continue;
			}
			String type = region.getType();
			// if (type == CSSRegionContexts.CSS_DECLARATION_DELIMITER || type
			// == CSSRegionContexts.CSS_RBRACE) {
			// break;
			// }
			switch (status) {
				case S_NORMAL :
					if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_FUNCTION) {
						regionBuf.add(region);
						status = S_FUNCTION;
					}
					else if (bFont && type == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR && fParentRegion.getText(region).equals("/")) { //$NON-NLS-1$
						value = createPrimitiveValue(region);
						status = S_FONT_SLASH;
					}
					else if (!isBlank(type)) {
						value = createPrimitiveValue(region);
					}
					break;
				case S_FUNCTION :
					if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_PARENTHESIS_CLOSE) {
						regionBuf.add(region);
						value = createPrimitiveValue(regionBuf);
						regionBuf.clear();
						status = S_NORMAL;
					}
					else if (!isBlank(type)) {
						regionBuf.add(region);
					}
					break;
				case S_FONT_SLASH :
					if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_DIMENSION) {
						value = createPrimitiveValue(region);
						status = S_COMMA_SEPARATION;
					}
					else if (!isBlank(type)) {
						value = createPrimitiveValue(region);
					}
					break;
				case S_COMMA_SEPARATION :
					if (type == CSSRegionContexts.CSS_DECLARATION_VALUE_OPERATOR && fParentRegion.getText(region).equals(",")) { //$NON-NLS-1$
						value = createPrimitiveValue(regionBuf);
						regionBuf.clear();
						if (value != null) {
							if (fUpdateContext == null || !fUpdateContext.isActive()) {
								item.appendValue(value);
							}
						}
						value = createPrimitiveValue(region);
					}
					else {
						regionBuf.add(region);
					}
					break;
				default :
					break;
			}
			if (value != null) {
				if (fUpdateContext == null || !fUpdateContext.isActive()) {
					item.appendValue(value);
				}
			}
		}
		if (!regionBuf.isEmpty()) {
			value = createPrimitiveValue(regionBuf);
			if (fUpdateContext == null || !fUpdateContext.isActive()) {
				item.appendValue(value);
			}
		}
	}

	private String getCollapsedText(ITextRegion region) {
		if (fParentRegion == null)
			return ""; //$NON-NLS-1$
		StringBuffer text = new StringBuffer(fParentRegion.getFullText(region));
		if (region.getLength() > region.getTextLength())
			text.replace(region.getTextLength(), region.getLength(), " "); //$NON-NLS-1$
		return text.toString();
	}

	private String getText(ITextRegion region) {
		return (fParentRegion != null) ? fParentRegion.getText(region) : ""; //$NON-NLS-1$ 
	}

	private static boolean isBlank(String type) {
		return (type == CSSRegionContexts.CSS_S || type == CSSRegionContexts.CSS_COMMENT || type == CSSRegionContexts.CSS_DECLARATION_VALUE_S);
	}

}
