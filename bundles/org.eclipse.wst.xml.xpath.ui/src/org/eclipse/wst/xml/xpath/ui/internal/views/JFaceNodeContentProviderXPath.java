/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.views;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JFaceNodeContentProviderXPath implements ITreeContentProvider {

	protected IJFaceNodeAdapter getAdapter(Object adaptable) {
		if (adaptable instanceof INodeNotifier) {
			INodeAdapter adapter = ((INodeNotifier) adaptable)
					.getAdapterFor(IJFaceNodeAdapter.class);
			if (adapter instanceof IJFaceNodeAdapter) {
				return (IJFaceNodeAdapter) adapter;
			}
		}

		return null;
	}

	public Object[] getChildren(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);
		if (adapter != null) {
			return adapter.getChildren(object);
		}
		return new Object[0];
	}

	public Object[] getElements(Object object) {
		if (object instanceof IDOMModel) {
			Object topNode = ((IDOMModel) object).getDocument();
			IJFaceNodeAdapter adapter = getAdapter(topNode);
			if (adapter != null) {
				return adapter.getElements(topNode);
			}
		} else if (object instanceof NodeList) {
			NodeList nodeList = (NodeList) object;
			if (nodeList.getLength() == 0) {
				return new Object[] { new EmptyNodeList() };
			} else {
				Node[] nodes = new Node[nodeList.getLength()];
				for (int i = 0; i < nodes.length; i++) {
					nodes[i] = nodeList.item(i);
				}
				return nodes;
			}
		}
		return new Object[0];
	}

	public Object getParent(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);
		if (adapter != null) {
			return adapter.getParent(object);
		}
		return null;
	}

	public boolean hasChildren(Object object) {
		IJFaceNodeAdapter adapter = getAdapter(object);
		if (adapter != null) {
			return adapter.hasChildren(object);
		}
		return false;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}
}