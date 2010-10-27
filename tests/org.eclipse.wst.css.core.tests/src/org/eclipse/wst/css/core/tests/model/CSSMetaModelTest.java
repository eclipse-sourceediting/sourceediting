/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.tests.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.contentmodel.IValID;
import org.eclipse.wst.css.core.internal.contentmodel.PropCMProperty;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMProperty;
import org.eclipse.wst.css.core.internal.metamodel.CSSMetaModel;
import org.eclipse.wst.css.core.internal.metamodel.CSSProfileRegistry;
import org.eclipse.wst.css.core.internal.metamodel.util.CSSMetaModelUtil;

public class CSSMetaModelTest extends TestCase {

	public void testContentPropertyValues2r1() {
		CSSMetaModel model = CSSProfileRegistry.getInstance().getDefaultProfile().getMetaModel();
		if (model != null) {
			CSSMetaModelUtil util = new CSSMetaModelUtil(model);
			CSSMMProperty property = util.getProperty(PropCMProperty.P_CONTENT);
			Iterator it = property.getValues();
			Set set = new HashSet(2);
			set.add(IValID.V_NORMAL);
			set.add(IValID.V_NONE);
			while (it.hasNext()) {
				CSSMMNode node = (CSSMMNode) it.next();
				if (set.contains(node.getName())) {
					assertEquals("Property should be a keyword", CSSMMNode.TYPE_KEYWORD, node.getType());
					set.remove(node.getName());
				}
			}
			assertTrue("The content property is missing values added for CSS 2 revision 1.", set.isEmpty());
		}
	}

	public void testCursorPropertyValues2r1() {
		CSSMetaModel model = CSSProfileRegistry.getInstance().getDefaultProfile().getMetaModel();
		if (model != null) {
			CSSMetaModelUtil util = new CSSMetaModelUtil(model);
			CSSMMProperty property = util.getProperty(PropCMProperty.P_CURSOR);
			Iterator it = property.getValues();
			Set set = new HashSet(1);
			set.add(IValID.V_PROGRESS);
			while (it.hasNext()) {
				CSSMMNode node = (CSSMMNode) it.next();
				if (set.contains(node.getName())) {
					assertEquals("Property should be a keyword", CSSMMNode.TYPE_KEYWORD, node.getType());
					set.remove(node.getName());
				}
			}
			assertTrue("The content property is missing values added for CSS 2 revision 1.", set.isEmpty());
		}
	}

	public void testDisplayPropertyValues2r1() {
		CSSMetaModel model = CSSProfileRegistry.getInstance().getDefaultProfile().getMetaModel();
		if (model != null) {
			CSSMetaModelUtil util = new CSSMetaModelUtil(model);
			CSSMMProperty property = util.getProperty(PropCMProperty.P_DISPLAY);
			Iterator it = property.getValues();
			Set set = new HashSet(1);
			set.add(IValID.V_INLINE_BLOCK);
			while (it.hasNext()) {
				CSSMMNode node = (CSSMMNode) it.next();
				if (set.contains(node.getName())) {
					assertEquals("Property should be a keyword", CSSMMNode.TYPE_KEYWORD, node.getType());
					set.remove(node.getName());
				}
			}
			assertTrue("The content property is missing values added for CSS 2 revision 1.", set.isEmpty());
		}
	}

	public void testWhitespacePropertyValues2r1() {
		CSSMetaModel model = CSSProfileRegistry.getInstance().getDefaultProfile().getMetaModel();
		if (model != null) {
			CSSMetaModelUtil util = new CSSMetaModelUtil(model);
			CSSMMProperty property = util.getProperty(PropCMProperty.P_WHITE_SPACE);
			Iterator it = property.getValues();
			Set set = new HashSet(1);
			set.add(IValID.V_PRE_LINE);
			set.add(IValID.V_PRE_WRAP);
			while (it.hasNext()) {
				CSSMMNode node = (CSSMMNode) it.next();
				if (set.contains(node.getName())) {
					assertEquals("Property should be a keyword", CSSMMNode.TYPE_KEYWORD, node.getType());
					set.remove(node.getName());
				}
			}
			assertTrue("The content property is missing values added for CSS 2 revision 1.", set.isEmpty());
		}
	}

	public void testColorPropertyValues2r1() {
		CSSMetaModel model = CSSProfileRegistry.getInstance().getDefaultProfile().getMetaModel();
		if (model != null) {
			CSSMetaModelUtil util = new CSSMetaModelUtil(model);
			CSSMMProperty property = util.getProperty(PropCMProperty.P_COLOR);
			Iterator it = property.getValues();
			Set set = new HashSet(1);
			set.add(IValID.V_ORANGE);
			while (it.hasNext()) {
				CSSMMNode node = (CSSMMNode) it.next();
				if (set.contains(node.getName())) {
					assertEquals("Property should be a keyword", CSSMMNode.TYPE_KEYWORD, node.getType());
					set.remove(node.getName());
				}
			}
			assertTrue("The content property is missing values added for CSS 2 revision 1.", set.isEmpty());
		}
	}
}
