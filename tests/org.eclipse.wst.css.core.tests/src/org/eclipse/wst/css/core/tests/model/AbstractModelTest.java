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
package org.eclipse.wst.css.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.css.core.internal.CSSCorePlugin;
import org.eclipse.wst.css.core.internal.preferences.CSSCorePreferenceNames;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleSheet;
import org.eclipse.wst.css.core.tests.util.FileUtil;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;

public class AbstractModelTest extends TestCase {
	protected ICSSModel fModel = null;

	protected void setUp() throws Exception {
		super.setUp();

		Preferences prefs = CSSCorePlugin.getDefault().getPluginPreferences();
		prefs.setValue(CSSCorePreferenceNames.CASE_IDENTIFIER, CSSCorePreferenceNames.LOWER);
		prefs.setValue(CSSCorePreferenceNames.CASE_PROPERTY_NAME, CSSCorePreferenceNames.LOWER);
		prefs.setValue(CSSCorePreferenceNames.CASE_PROPERTY_VALUE, CSSCorePreferenceNames.LOWER);

		fModel = FileUtil.createModel();
	}

	protected void tearDown() throws Exception {
		if (fModel != null) {
			fModel.releaseFromEdit();
			fModel = null;
		}
		super.tearDown();
	}

	protected ICSSModel getModel() {
		return fModel;
	}

	protected ICSSStyleSheet getStyleSheet() {
		return (ICSSStyleSheet) fModel.getDocument();
	}

	class PrimitiveNumber {
		short type;
		float value;

		PrimitiveNumber(short newType, float newValue) {
			type = newType;
			value = newValue;
		}
	}

	class PrimitiveString {
		short type;
		String value;

		PrimitiveString(short newType, String newValue) {
			type = newType;
			value = newValue;
		}
	}

	/* for int, float */
	protected void checkPrimitiveNumber(CSSValue actual, PrimitiveNumber expected) {
		assertTrue(actual instanceof CSSPrimitiveValue);
		assertEquals(expected.type, ((CSSPrimitiveValue) actual).getPrimitiveType());
		assertEquals(expected.value, ((CSSPrimitiveValue) actual).getFloatValue(expected.type), 0);
	}

	/* for string */
	protected void checkPrimitiveString(CSSValue actual, PrimitiveString expected) {
		assertTrue(actual instanceof CSSPrimitiveValue);
		assertEquals(expected.type, ((CSSPrimitiveValue) actual).getPrimitiveType());
		assertEquals(expected.value, ((CSSPrimitiveValue) actual).getStringValue());
	}

	/* for counter */
	protected void checkPrimitiveCounter(CSSValue actual, String identifier, String listStyle, String separator) {
		assertTrue(actual instanceof CSSPrimitiveValue);
		assertEquals(CSSPrimitiveValue.CSS_COUNTER, ((CSSPrimitiveValue) actual).getPrimitiveType());
		Counter counter = ((CSSPrimitiveValue) actual).getCounterValue();
		assertEquals(identifier, counter.getIdentifier());
		assertEquals(listStyle, counter.getListStyle());
		assertEquals(separator, counter.getSeparator());
	}

	/* for rect */
	protected void checkPrimitiveRect(CSSValue actual, Object[] expectedArray) {
		assertTrue(actual instanceof CSSPrimitiveValue);
		assertEquals(CSSPrimitiveValue.CSS_RECT, ((CSSPrimitiveValue) actual).getPrimitiveType());
		Rect rect = ((CSSPrimitiveValue) actual).getRectValue();
		CSSPrimitiveValue value;
		Object expected;
		value = rect.getTop();
		expected = expectedArray[0];
		if (expected instanceof PrimitiveNumber) {
			checkPrimitiveNumber(value, (PrimitiveNumber) expected);
		}
		else {
			checkPrimitiveString(value, (PrimitiveString) expected);
		}
		value = rect.getRight();
		expected = expectedArray[1];
		if (expected instanceof PrimitiveNumber) {
			checkPrimitiveNumber(value, (PrimitiveNumber) expected);
		}
		else {
			checkPrimitiveString(value, (PrimitiveString) expected);
		}
		value = rect.getBottom();
		expected = expectedArray[2];
		if (expected instanceof PrimitiveNumber) {
			checkPrimitiveNumber(value, (PrimitiveNumber) expected);
		}
		else {
			checkPrimitiveString(value, (PrimitiveString) expected);
		}
		value = rect.getLeft();
		expected = expectedArray[3];
		if (expected instanceof PrimitiveNumber) {
			checkPrimitiveNumber(value, (PrimitiveNumber) expected);
		}
		else {
			checkPrimitiveString(value, (PrimitiveString) expected);
		}
	}

	/* for rgb */
	protected void checkPrimitiveRgb(CSSValue actual, Object[] expectedArray) {
		assertTrue(actual instanceof CSSPrimitiveValue);
		assertEquals(CSSPrimitiveValue.CSS_RGBCOLOR, ((CSSPrimitiveValue) actual).getPrimitiveType());
		RGBColor color = ((CSSPrimitiveValue) actual).getRGBColorValue();
		CSSPrimitiveValue value;
		Object expected;
		value = color.getRed();
		expected = expectedArray[0];
		if (expected instanceof PrimitiveNumber) {
			checkPrimitiveNumber(value, (PrimitiveNumber) expected);
		}
		else {
			checkPrimitiveString(value, (PrimitiveString) expected);
		}
		value = color.getGreen();
		expected = expectedArray[1];
		if (expected instanceof PrimitiveNumber) {
			checkPrimitiveNumber(value, (PrimitiveNumber) expected);
		}
		else {
			checkPrimitiveString(value, (PrimitiveString) expected);
		}
		value = color.getBlue();
		expected = expectedArray[2];
		if (expected instanceof PrimitiveNumber) {
			checkPrimitiveNumber(value, (PrimitiveNumber) expected);
		}
		else {
			checkPrimitiveString(value, (PrimitiveString) expected);
		}
	}

}
