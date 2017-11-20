/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;
import org.eclipse.wst.html.core.tests.utils.FileUtil;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.css.ElementCSSInlineStyle;

public class BUG124835SetStyleAttributeValueTest extends TestCase {
	/**
	 * Tests setting some css attribute values including setting the same
	 * value several times for different attributes
	 */
	public void testSetAttributeValue() {
		final String ABSOLUTE = "absolute";
		final String ONE = "1";
		final String PX = "50px";

		IDOMModel model = FileUtil.createHTMLModel();
		try {
			IDOMDocument doc = model.getDocument();
			Element element = doc.createElement(HTML40Namespace.ElementName.DIV);
			element.setAttribute(HTML40Namespace.ATTR_NAME_ID, "Layer0");
			element.setAttribute(HTML40Namespace.ATTR_NAME_STYLE, "");
			setInlineStyle(element, PropCMProperty.P_POSITION, ABSOLUTE);
			setInlineStyle(element, PropCMProperty.P_Z_INDEX, ONE);
			setInlineStyle(element, PropCMProperty.P_WIDTH, PX);
			setInlineStyle(element, PropCMProperty.P_HEIGHT, PX);
			setInlineStyle(element, PropCMProperty.P_TOP, PX);
			setInlineStyle(element, PropCMProperty.P_LEFT, PX);

			assertEquals(ABSOLUTE, getInlineStyle(element, PropCMProperty.P_POSITION));
			assertEquals(ONE, getInlineStyle(element, PropCMProperty.P_Z_INDEX));
			assertEquals(PX, getInlineStyle(element, PropCMProperty.P_WIDTH));
			assertEquals(PX, getInlineStyle(element, PropCMProperty.P_HEIGHT));
			assertEquals(PX, getInlineStyle(element, PropCMProperty.P_TOP));
			assertEquals(PX, getInlineStyle(element, PropCMProperty.P_LEFT));
		}
		finally {
			if (model != null) {
				model.releaseFromEdit();
			}
		}
	}

	private void setInlineStyle(Element element, String name, String value) {
		ICSSStyleDeclaration decl = null;
		try {
			decl = (ICSSStyleDeclaration) ((ElementCSSInlineStyle) element).getStyle();
		}
		catch (ClassCastException ex) {
			// do nothing
		}
		if (decl != null) {
			String priority = decl.getPropertyPriority(name);
			decl.setProperty(name, value, priority);
		}
	}


	private String getInlineStyle(Element element, String name) {
		String value = null;
		ICSSStyleDeclaration decl = null;
		try {
			decl = (ICSSStyleDeclaration) ((ElementCSSInlineStyle) element).getStyle();
		}
		catch (ClassCastException ex) {
			// do nothing
		}
		if (decl != null) {
			value = decl.getPropertyValue(name);
		}
		return value;
	}
}
