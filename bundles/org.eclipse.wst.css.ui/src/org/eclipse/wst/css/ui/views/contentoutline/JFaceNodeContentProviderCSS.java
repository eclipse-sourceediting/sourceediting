/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapterFactory;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.stylesheets.MediaList;

/**
 * A Content provider for a JFace viewer used to display CSS nodes. This
 * content provider does not use adapters.
 */
class JFaceNodeContentProviderCSS implements ITreeContentProvider {

	public JFaceNodeContentProviderCSS() {
		super();
	}

	/**
	 */
	protected void addElements(Object element, ArrayList v) {

		ICSSNode node;

		if (element instanceof ICSSModel) {
			ICSSModel model = (ICSSModel) element;
			ICSSDocument doc = model.getDocument();
			node = doc.getFirstChild();
		}
		else if (element instanceof ICSSNode) {
			node = ((ICSSNode) element).getFirstChild();
		}
		else
			return;

		while (node != null) {
			if (node instanceof CSSRule) {
				v.add(node);
			}

			node = node.getNextSibling();
		}

	}

	/**
	 * The visual part that is using this content provider is about to be
	 * disposed. Deallocate all allocated SWT resources.
	 */
	public void dispose() {
	}

	/**
	 * Returns an enumeration containing all child nodes of the given element,
	 * which represents a node in a tree. The difference to
	 * <code>IStructuredContentProvider.getElements(Object)</code> is as
	 * follows: <code>getElements</code> is called to obtain the tree
	 * viewer's root elements. Method <code>getChildren</code> is used to
	 * obtain the children of a given node in the tree, which can can be a
	 * root node, too.
	 */
	public Object[] getChildren(Object object) {
		if (object instanceof ICSSNode) {
			ICSSNode node = (ICSSNode) object;

			short nodeType = node.getNodeType();
			if (nodeType == ICSSNode.STYLERULE_NODE || nodeType == ICSSNode.PAGERULE_NODE || nodeType == ICSSNode.FONTFACERULE_NODE) {
				for (node = node.getFirstChild(); node != null && !(node instanceof ICSSStyleDeclaration); node.getNextSibling()) {
					// nop
				}
			}
			List children = new ArrayList();
			ICSSNode child = (node != null) ? node.getFirstChild() : null;
			while (child != null) {
				if (!(child instanceof ICSSPrimitiveValue) && !(child instanceof MediaList)) {
					children.add(child);
				}
				/*
				 * Required to correctly connect the refreshing behavior to
				 * the tree
				 */
				if (child instanceof INodeNotifier) {
					((INodeNotifier) child).getAdapterFor(IJFaceNodeAdapter.class);
				}
				child = child.getNextSibling();
			}
			return children.toArray();
		}
		return new Object[0];
	}

	/**
	 * Returns an enumeration with the elements belonging to the passed
	 * element. These elements can be presented as rows in a table, items in a
	 * list etc.
	 */
	public Object[] getElements(Object object) {
		// The root is usually an instance of an XMLStructuredModel in
		// which case we want to extract the document.

		if (object instanceof ICSSModel) {
			ArrayList v = new ArrayList();
			// internalGetElements(object, v);
			addElements(object, v);
			Object[] elements = v.toArray();

			for (int i = 0; i < elements.length; i++) {
				/*
				 * Required to correctly connect the refreshing behavior to
				 * the tree
				 */
				if (elements[i] instanceof INodeNotifier) {
					((INodeNotifier) elements[i]).getAdapterFor(IJFaceNodeAdapter.class);
				}
			}

			return elements;
		}
		return new Object[0];

	}

	/**
	 * Returns the parent for the given element. This method can return
	 * <code>null</code> indicating that the parent can't be computed. In
	 * this case the tree viewer can't expand a given node correctly if
	 * requested.
	 */
	public Object getParent(Object object) {
		if (object instanceof ICSSNode) {
			ICSSNode node = ((ICSSNode) object).getParentNode();
			if (node != null && node.getNodeType() == ICSSNode.STYLEDECLARATION_NODE) {
				/*
				 * Required to also correctly connect style declaration to the
				 * refreshing behavior in the tree
				 */
				if (node instanceof INodeNotifier) {
					((INodeNotifier) node).getAdapterFor(IJFaceNodeAdapter.class);
				}
				node = node.getParentNode();
			}
			return node;
		}
		return null;
	}

	/**
	 * Returns <code>true</code> if the given element has children.
	 * Otherwise <code>false</code> is returned.
	 */
	public boolean hasChildren(Object object) {
		// return getAdapter(object).hasChildren((ICSSNode) object);
		if (object instanceof ICSSNode) {
			/*
			 * Required to correctly connect the refreshing behavior to the
			 * tree
			 */
			if (object instanceof INodeNotifier) {
				((INodeNotifier) object).getAdapterFor(IJFaceNodeAdapter.class);
			}

			if (object instanceof ICSSStyleDeclItem)
				return false;
			else
				return ((ICSSNode) object).hasChildNodes();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null && oldInput instanceof IStructuredModel) {
			IJFaceNodeAdapterFactory factory = (IJFaceNodeAdapterFactory) ((IStructuredModel) oldInput).getFactoryRegistry().getFactoryFor(IJFaceNodeAdapter.class);
			if (factory != null) {
				factory.removeListener(viewer);
			}
		}
		if (newInput != null && newInput instanceof IStructuredModel) {
			IJFaceNodeAdapterFactory factory = (IJFaceNodeAdapterFactory) ((IStructuredModel) newInput).getFactoryRegistry().getFactoryFor(IJFaceNodeAdapter.class);
			if (factory != null) {
				factory.addListener(viewer);
			}
			if (newInput instanceof ICSSModel) {
				((INodeNotifier) ((ICSSModel) newInput).getDocument()).getAdapterFor(IJFaceNodeAdapter.class);
			}
		}
	}

	/**
	 */
	/*
	 * protected void internalGetElements(Object element, ArrayList v) {
	 * 
	 * ICSSNode node;
	 * 
	 * if (element instanceof ICSSModel) { ICSSModel model =
	 * (ICSSModel)element; ICSSDocument doc = model.getDocument();
	 * adapterFactory.adapt((INodeNotifier)doc); node = doc.getFirstChild(); }
	 * else if (element instanceof ICSSNode) { node =
	 * ((ICSSNode)element).getFirstChild(); } else { return; }
	 * 
	 * while (node != null) { switch (node.getNodeType()) { case
	 * ICSSNode.STYLEDECLARATION_NODE: adapterFactory.adapt((INodeNotifier)
	 * node); break; case ICSSNode.STYLERULE_NODE: case
	 * ICSSNode.FONTFACERULE_NODE: case ICSSNode.PAGERULE_NODE: case
	 * ICSSNode.IMPORTRULE_NODE: case ICSSNode.MEDIARULE_NODE: v.add(node);
	 * adapterFactory.adapt((INodeNotifier) node); break; default:
	 * adapterFactory.adapt((INodeNotifier) node); break; }
	 * 
	 * node = node.getNextSibling(); } }
	 */
	/**
	 * Checks whether the given element is deleted or not.
	 */
	public boolean isDeleted(Object element) {
		return false;
	}
}