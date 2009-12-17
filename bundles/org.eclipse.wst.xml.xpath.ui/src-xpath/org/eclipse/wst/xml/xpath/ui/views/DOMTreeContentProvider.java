/*******************************************************************************
 * Copyright (c) 2005-2007 Orangevolt (www.orangevolt.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Orangevolt (www.orangevolt.com) - XSLT support
 *     Jesper Steen Moller - refactored Orangevolt XSLT support into WST
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Deprecated
public class DOMTreeContentProvider implements ITreeContentProvider {
	Node node = null;

	static final Object[] NOTHING = new Object[0];

	private Object[] nodeList2Array(NodeList nl) {
		Object[] oa = new Object[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++) {
			oa[i] = nl.item(i);
		}

		return oa;
	}

	public Object[] getChildren(Object parentElement) {
		return parentElement == null ? NOTHING
				: nodeList2Array(((Node) parentElement).getChildNodes());
	}

	public Object getParent(Object element) {
		return ((Node) element).getParentNode();
	}

	public boolean hasChildren(Object element) {
		return ((Node) element).hasChildNodes();
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof Element) {
			node = (Element) newInput;
		} else if (newInput instanceof Document) {
			node = ((Document) newInput).getDocumentElement();
		}
	}
}