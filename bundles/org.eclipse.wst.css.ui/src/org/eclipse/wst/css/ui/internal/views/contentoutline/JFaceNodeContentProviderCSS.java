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
package org.eclipse.wst.css.ui.internal.views.contentoutline;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSDocument;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSModel;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSPrimitiveValue;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.sse.core.INodeAdapterFactory;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.stylesheets.MediaList;


/**
 * A Content provider for a JFace viewer used to display DOM nodes. This
 * content provider takes an adapter factory to create JFace adapters for the
 * nodes in the tree.
 */
class JFaceNodeContentProviderCSS extends JFaceNodeContentProvider {
	protected INodeAdapterFactory adapterFactory;

	//protected DomainNotifier domainNotifier;
	/**
	 */
	public JFaceNodeContentProviderCSS(INodeAdapterFactory adapterFactory) {
		super(adapterFactory);
		this.adapterFactory = adapterFactory;
	}

	/**
	 */
	protected void adaptElements(Object element) {

		ICSSNode node;

		if (element instanceof ICSSModel) {
			ICSSDocument doc = ((ICSSModel) element).getDocument();
			adapterFactory.adapt((INodeNotifier) doc);
			node = doc.getFirstChild();
		} else if (element instanceof ICSSNode) {
			node = ((ICSSNode) element).getFirstChild();
		} else {
			return;
		}

		while (node != null) {
			//		if (node instanceof CSSRule) {
			adapterFactory.adapt((INodeNotifier) node);
			adaptElements(node);
			//		}
			//		else{
			//			adapterFactory.adapt((INodeNotifier) node);
			//		}

			node = node.getNextSibling();
		}
	}

	/**
	 */
	protected void addElements(Object element, ArrayList v) {

		ICSSNode node;

		if (element instanceof ICSSModel) {
			ICSSModel model = (ICSSModel) element;
			ICSSDocument doc = model.getDocument();
			//      addAdapter((INodeNotifier) doc);
			adapterFactory.adapt((INodeNotifier) doc);
			node = doc.getFirstChild();
		} else if (element instanceof ICSSNode) {
			node = ((ICSSNode) element).getFirstChild();
		} else
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
			//		internalGetElements(object, v);
			addElements(object, v);
			adaptElements(object);
			return v.toArray();
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
		//  IJFaceNodeAdapter adapter = getAdapter(object);
		/*
		 * ICSSNodeAdapter adapter = (ICSSNodeAdapter)getAdapter(object); if
		 * (adapter != null) return adapter.getParent((ICSSNode) object); else
		 * return null;
		 */
		if (object instanceof ICSSNode) {
			ICSSNode node = ((ICSSNode) object).getParentNode();
			if (node != null && node.getNodeType() == ICSSNode.STYLEDECLARATION_NODE) {
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
		//  return getAdapter(object).hasChildren((ICSSNode) object);
		if (object instanceof ICSSNode) {
			if (object instanceof ICSSStyleDeclItem)
				return false;
			else
				return ((ICSSNode) object).hasChildNodes();
		}
		return false;
	}

	/**
	 * Called when the viewer's input is changing from <code>oldInput</code>
	 * to <code>newInput</code>. Both <code>newInput</code> and
	 * <code>oldInput</code> can be <code>null</code>. If
	 * <code>oldInput</code> is <code>null</code> it is the viewer's first
	 * connection to the content provider. If <code>newInput</code> is
	 * <code>null</code> the visual part is disconnected from any input. A
	 * typical implementation of this methods registers the content provider
	 * as a listener to changes on the new input, and deregisters the viewer
	 * from the old input. The content provider then updates the viewer in
	 * response to change notifications from the input.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		//// If there was no old input, then we must be providing content for
		// this part for the first time...
		//if (oldInput == null) {
		//// If the part is an IDomainListener then make the part start
		// listening to us.
		//if (viewer instanceof IDomainListener)
		//domainNotifier.addDomainListener((IDomainListener) viewer);
		//}
		//// If there is no new input, we must clean ourselves up as if we'd
		// never seen the viewer.
		//else
		//if (newInput == null) {
		//// If the part is an IDomainListener, then we make it stop
		// listening to us.
		//if (viewer instanceof IDomainListener)
		//domainNotifier.removeDomainListener((IDomainListener) viewer);

		//}
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
	 * node = node.getNextSibling(); }
	 *  }
	 */
	/**
	 * Checks whether the given element is deleted or not.
	 */
	public boolean isDeleted(Object element) {
		return false;
	}
}