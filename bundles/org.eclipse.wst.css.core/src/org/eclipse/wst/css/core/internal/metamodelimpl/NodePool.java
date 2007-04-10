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
package org.eclipse.wst.css.core.internal.metamodelimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.css.core.internal.Logger;



class NodePool {

	NodePool() {
		super();
		initializeClassNameTable();
	}

	CSSMMNodeImpl getNode(String tagName, String nodeName) {
		if (nodeName != null) {
			nodeName = nodeName.toLowerCase();
		}
		String className = (String) classNames.get(tagName);
		if (className == null) {
			return null;
		}
		Map nodes = (Map) nodeRegistry.get(className);
		if (nodes == null) {
			nodes = new HashMap();
			nodeRegistry.put(className, nodes);
		}
		CSSMMNodeImpl node = (CSSMMNodeImpl) nodes.get(nodeName);
		if (node == null) {
			node = createNewNode(className);
			nodes.put(nodeName, node);
		}
		return node;
	}

	private CSSMMNodeImpl createNewNode(String className) {
		CSSMMNodeImpl node = null;
		if (className != null) {
			className = getPackageName() + "." + className; //$NON-NLS-1$
			try {
				node = (CSSMMNodeImpl) Class.forName(className).newInstance();
			}
			catch (IllegalAccessException e) {
				Logger.logException(e);
			}
			catch (InstantiationException e) {
				Logger.logException(e);
			}
			catch (ClassNotFoundException e) {
				Logger.logException(e);
			}
		}
		// System.out.println(className + " is Created");
		return node;
	}

	/**
	 * Error check function: find nodes that are not referred by any node
	 */
	Iterator getStrayNodes() {
		List strayNodes = new ArrayList();
		Iterator iMap = nodeRegistry.values().iterator();
		while (iMap.hasNext()) {
			Map nodes = (Map) iMap.next();
			Iterator iNode = nodes.values().iterator();
			while (iNode.hasNext()) {
				CSSMMNodeImpl node = (CSSMMNodeImpl) iNode.next();
				if (node.getReferenceCount() == 0) {
					strayNodes.add(node);
				}
			}
		}
		return strayNodes.iterator();
	}

	// This class must not be inner class :)
	private String getPackageName() {
		if (fPackageName == null) {
			String className = getClass().getName();
			int pos = className.lastIndexOf('.');
			if (0 < pos) {
				fPackageName = className.substring(0, pos);
			}
			else {
				fPackageName = ""; //$NON-NLS-1$
			}
		}
		return fPackageName;
	}

	void initializeClassNameTable() {
		classNames.put(ProfileKeywords.STYLESHEET_DEF, CLASS_STYLE_SHEET);
		classNames.put(ProfileKeywords.CHARSET_RULE_DEF, CLASS_CHARSET_RULE);
		classNames.put(ProfileKeywords.CHARSET_RULE, CLASS_CHARSET_RULE);
		classNames.put(ProfileKeywords.FONTFACE_RULE_DEF, CLASS_FONT_FACE_RULE);
		classNames.put(ProfileKeywords.FONTFACE_RULE, CLASS_FONT_FACE_RULE);
		classNames.put(ProfileKeywords.IMPORT_RULE_DEF, CLASS_IMPORT_RULE);
		classNames.put(ProfileKeywords.IMPORT_RULE, CLASS_IMPORT_RULE);
		classNames.put(ProfileKeywords.MEDIA_RULE_DEF, CLASS_MEDIA_RULE);
		classNames.put(ProfileKeywords.MEDIA_RULE, CLASS_MEDIA_RULE);
		classNames.put(ProfileKeywords.PAGE_RULE_DEF, CLASS_PAGE_RULE);
		classNames.put(ProfileKeywords.PAGE_RULE, CLASS_PAGE_RULE);
		classNames.put(ProfileKeywords.STYLE_RULE_DEF, CLASS_STYLE_RULE);
		classNames.put(ProfileKeywords.STYLE_RULE, CLASS_STYLE_RULE);
		classNames.put(ProfileKeywords.PROPERTY_DEF, CLASS_PROPERTY);
		classNames.put(ProfileKeywords.PROPERTY, CLASS_PROPERTY);
		classNames.put(ProfileKeywords.DESCRIPTOR_DEF, CLASS_DESCRIPTOR);
		classNames.put(ProfileKeywords.DESCRIPTOR, CLASS_DESCRIPTOR);
		classNames.put(ProfileKeywords.CONTAINER_DEF, CLASS_CONTAINER);
		classNames.put(ProfileKeywords.CONTAINER, CLASS_CONTAINER);
		classNames.put(ProfileKeywords.NUMBER_DEF, CLASS_NUMBER);
		classNames.put(ProfileKeywords.NUMBER, CLASS_NUMBER);
		classNames.put(ProfileKeywords.KEYWORD_DEF, CLASS_KEYWORD);
		classNames.put(ProfileKeywords.KEYWORD, CLASS_KEYWORD);
		classNames.put(ProfileKeywords.UNIT, CLASS_UNIT);
		classNames.put(ProfileKeywords.UNIT_DEF, CLASS_UNIT);
		classNames.put(ProfileKeywords.FUNCTION, CLASS_FUNCTION);
		classNames.put(ProfileKeywords.FUNCTION_DEF, CLASS_FUNCTION);
		classNames.put(ProfileKeywords.SELECTOR_EXPRESSION, CLASS_SELECTOR_EXPRESSION);
		classNames.put(ProfileKeywords.PSEUDO_ELEMENT, CLASS_PSEUDO_ELEMENT);
		classNames.put(ProfileKeywords.PSEUDO_ELEMENT_DEF, CLASS_PSEUDO_ELEMENT);
		classNames.put(ProfileKeywords.PSEUDO_CLASS, CLASS_PSEUDO_CLASS);
		classNames.put(ProfileKeywords.PSEUDO_CLASS_DEF, CLASS_PSEUDO_CLASS);
		classNames.put(ProfileKeywords.STRING, CLASS_STRING);
		classNames.put(ProfileKeywords.CATEGORY_DEF, CLASS_CATEGORY);
	}


	private String fPackageName;
	/*
	 * Conversion Tables nodeRegistry : class name -> hash of node instances
	 * classNames : Profile Tag name -> java class name
	 */
	private Map nodeRegistry = new HashMap();
	private Map classNames = new HashMap();

	private final static String CLASS_STYLE_SHEET = "CSSMMStyleSheetImpl"; //$NON-NLS-1$
	private final static String CLASS_CHARSET_RULE = "CSSMMCharsetRuleImpl"; //$NON-NLS-1$
	private final static String CLASS_FONT_FACE_RULE = "CSSMMFontFaceRuleImpl"; //$NON-NLS-1$
	private final static String CLASS_IMPORT_RULE = "CSSMMImportRuleImpl"; //$NON-NLS-1$
	private final static String CLASS_MEDIA_RULE = "CSSMMMediaRuleImpl"; //$NON-NLS-1$
	private final static String CLASS_PAGE_RULE = "CSSMMPageRuleImpl"; //$NON-NLS-1$
	private final static String CLASS_STYLE_RULE = "CSSMMStyleRuleImpl"; //$NON-NLS-1$
	private final static String CLASS_PROPERTY = "CSSMMPropertyImpl"; //$NON-NLS-1$
	private final static String CLASS_DESCRIPTOR = "CSSMMDescriptorImpl"; //$NON-NLS-1$
	private final static String CLASS_CONTAINER = "CSSMMContainerImpl"; //$NON-NLS-1$
	private final static String CLASS_NUMBER = "CSSMMNumberImpl"; //$NON-NLS-1$
	private final static String CLASS_KEYWORD = "CSSMMKeywordImpl"; //$NON-NLS-1$
	private final static String CLASS_UNIT = "CSSMMUnitImpl"; //$NON-NLS-1$
	private final static String CLASS_FUNCTION = "CSSMMFunctionImpl"; //$NON-NLS-1$
	private final static String CLASS_STRING = "CSSMMStringImpl"; //$NON-NLS-1$
	private final static String CLASS_CATEGORY = "CSSMMCategoryImpl"; //$NON-NLS-1$
	private final static String CLASS_SELECTOR_EXPRESSION = "CSSMMSelectorExpressionImpl"; //$NON-NLS-1$
	private final static String CLASS_PSEUDO_CLASS = "CSSMMPseudoClassImpl"; //$NON-NLS-1$
	private final static String CLASS_PSEUDO_ELEMENT = "CSSMMPseudoElementImpl"; //$NON-NLS-1$
}
