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
package org.eclipse.wst.css.core.internal.metamodel.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMDescriptor;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMProperty;
import org.eclipse.wst.css.core.internal.metamodel.CSSMetaModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleRule;
import org.w3c.dom.css.CSSFontFaceRule;


public class CSSMetaModelUtil {
	public CSSMetaModelUtil(CSSMetaModel metamodel) {
		super();
		fMetaModel = metamodel;
	}

	public Iterator getProperties() {
		return collectNodesByType(CSSMMNode.TYPE_PROPERTY);
	}

	public Iterator getDescriptors() {
		return collectNodesByType(CSSMMNode.TYPE_DESCRIPTOR);
	}

	public Iterator collectNodesByType(String type) {
		CSSMMTypeCollector collector = new CSSMMTypeCollector();
		collector.apply(fMetaModel, type);
		return collector.getNodes();
	}

	public CSSMMProperty getProperty(String propertyName) {
		Iterator iProperty = getProperties();
		while (iProperty.hasNext()) {
			CSSMMNode node = (CSSMMNode) iProperty.next();
			if (node.getName().equalsIgnoreCase(propertyName)) {
				return (CSSMMProperty) node;
			}
		}
		return null;
	}

	public CSSMMDescriptor getDescriptor(String descriptorName) {
		Iterator iDescriptor = getDescriptors();
		while (iDescriptor.hasNext()) {
			CSSMMNode node = (CSSMMNode) iDescriptor.next();
			if (node.getName().equalsIgnoreCase(descriptorName)) {
				return (CSSMMDescriptor) node;
			}
		}
		return null;
	}

	public CSSMMNode getMetaModelNodeFor(ICSSNode node) {
		if (node instanceof ICSSStyleDeclaration) {
			node = node.getParentNode();
		}
		if (node instanceof ICSSStyleDeclItem) {
			ICSSNode parent = node.getParentNode();
			if (parent != null) {
				parent = parent.getParentNode();
			}
			if (parent instanceof ICSSStyleRule) {
				return getProperty(((ICSSStyleDeclItem) node).getPropertyName());
			}
			else if (parent instanceof CSSFontFaceRule) {
				return getDescriptor(((ICSSStyleDeclItem) node).getPropertyName());
			}
		}
		if (node == null) {
			return null;
		}

		if (fTypeMap == null) {
			fTypeMap = new HashMap();
			fTypeMap.put(new Short(ICSSNode.STYLERULE_NODE), CSSMMNode.TYPE_STYLE_RULE);
			fTypeMap.put(new Short(ICSSNode.FONTFACERULE_NODE), CSSMMNode.TYPE_FONT_FACE_RULE);
			fTypeMap.put(new Short(ICSSNode.PAGERULE_NODE), CSSMMNode.TYPE_PAGE_RULE);
		}

		String nodeType = (String) fTypeMap.get(new Short(node.getNodeType()));
		if (nodeType == null) {
			return null;
		}

		Iterator iNodes = collectNodesByType(nodeType);
		if (iNodes.hasNext()) {
			CSSMMNode targetNode = (CSSMMNode) iNodes.next();
			if (!iNodes.hasNext()) { // it's only one
				return targetNode;
			}
		}

		return null;
	}

	public CSSMetaModel getMetaModel() {
		return fMetaModel;
	}


	private CSSMetaModel fMetaModel = null;
	private Map fTypeMap = null;
}
