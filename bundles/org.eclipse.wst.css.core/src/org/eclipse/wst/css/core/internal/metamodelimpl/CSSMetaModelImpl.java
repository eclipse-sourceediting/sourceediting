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
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.css.core.internal.metamodel.CSSMMNode;
import org.eclipse.wst.css.core.internal.metamodel.CSSMMStyleSheet;
import org.eclipse.wst.css.core.internal.metamodel.CSSMetaModel;


class CSSMetaModelImpl extends CSSMMNodeImpl implements CSSMetaModel {
	// class CSSMetaModelImpl implements CSSMetaModel {
	public CSSMetaModelImpl() {
		super();
		fNodePool = new NodePool();
	}

	public String getType() {
		return TYPE_META_MODEL;
	}

	public String getName() {
		return NAME_NOT_AVAILABLE;
	}

	/*
	 * @see CSSMMNodeImpl#canContain(CSSMMNode)
	 */
	boolean canContain(CSSMMNode child) {
		if (child == null) {
			return false;
		}
		String type = child.getType();
		return (type == TYPE_STYLE_SHEET || type == TYPE_CATEGORY);
	}

	public CSSMMStyleSheet getStyleSheet() {
		Iterator i = getChildNodes();
		while (i.hasNext()) {
			CSSMMNode node = (CSSMMNode) i.next();
			if (node.getType() == TYPE_STYLE_SHEET) {
				return (CSSMMStyleSheet) node;
			}
		}
		return null;
		// return fStyleSheet;
	}

	public Iterator getCategories() {
		List categories = new ArrayList();
		Iterator i = getChildNodes();
		while (i.hasNext()) {
			CSSMMNode node = (CSSMMNode) i.next();
			if (node.getType() == TYPE_CATEGORY) {
				categories.add(node);
			}
		}
		return categories.iterator();
	}

	void loadCompleted() {
		fNodePool = null;
	}

	// List fCategories = null;
	/*
	 * @see CSSMMNodeImpl#isValid()
	 */
	short getError() {
		if (getStyleSheet() != null) {
			return MetaModelErrors.ERROR_NO_CHILD;
		}
		else {
			return MetaModelErrors.NO_ERROR;
		}
	}

	NodePool getNodePool() {
		return fNodePool;
	}


	private NodePool fNodePool = null;
}
