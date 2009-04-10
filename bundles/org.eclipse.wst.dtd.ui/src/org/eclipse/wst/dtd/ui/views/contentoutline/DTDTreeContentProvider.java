/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.Comment;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.core.internal.NodeList;
import org.eclipse.wst.dtd.core.internal.Notation;
import org.eclipse.wst.dtd.core.internal.ParameterEntityReference;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.event.IDTDFileListener;
import org.eclipse.wst.dtd.core.internal.event.NodesEvent;

class DTDTreeContentProvider implements ITreeContentProvider, IDTDFileListener {

	private Object fInputObject;
	protected Viewer fViewer;

	// A cached set of IndexedNodeLists, required for getParent to return the
	// correct instances mapping to TreeItems
	protected Object[] logicalNodeLists = null;

	private boolean showLogicalOrder = false;

	public DTDTreeContentProvider() {
		super();
	}

	public void dispose() {
		fViewer = null;
	}

	private void expandToNode(DTDNode node) {
		DTDFile dtdFile = node.getDTDFile();
		// first expand the root
		AbstractTreeViewer viewer = (AbstractTreeViewer) fViewer;
		viewer.expandToLevel(dtdFile, 1);
		NodeList listToExpand = null;
		if (node instanceof Element || node instanceof ParameterEntityReference) {
			listToExpand = dtdFile.getElementsAndParameterEntityReferences();
		}
		else if (node instanceof Notation) {
			listToExpand = dtdFile.getNotations();
		}
		else if (node instanceof Entity) {
			listToExpand = dtdFile.getEntities();
		}
		else if (node instanceof Comment) {
			listToExpand = dtdFile.getComments();
		}
		if (listToExpand != null) {
			viewer.expandToLevel(listToExpand, 1);
		}
		viewer.expandToLevel(node, 0);
	}

	public Object[] getChildren(Object parentElement) {
		// return the lists of nodes when in logical order mode, all the Nodes
		// otherwise
		if (parentElement instanceof DTDFile) {
			if (isShowLogicalOrder()) {
				// return the visible node lists
				if (logicalNodeLists == null) {
					Iterator nodeLists = ((DTDFile) parentElement).getNodeLists().iterator();
					List visibleLists = new ArrayList(7);
					while (nodeLists.hasNext()) {
						NodeList list = (NodeList) nodeLists.next();
						if (isVisibleNodeList(list)) {
							visibleLists.add(list);
						}
					}
					logicalNodeLists = visibleLists.toArray();
				}
				return logicalNodeLists;
			}
			else {
				// return the visible nodes
				List allNodes = ((DTDFile) parentElement).getNodes();
				List visibleNodes = new ArrayList(allNodes.size());
				for (int i = 0; i < allNodes.size(); i++) {
					Object o = allNodes.get(i);
					if (isVisibleNode(o)) {
						visibleNodes.add(o);
					}
				}
				return visibleNodes.toArray();
			}
		}
		else if (parentElement instanceof NodeList) {
			return ((NodeList) parentElement).getNodes().toArray();
		}
		else if (parentElement instanceof Element) {
			// always group the attributes directly under the element
			Object[] children = ((DTDNode) parentElement).getChildren();
			List attributes = ((Element) parentElement).getElementAttributes();
			Object[] logicalChildren = new Object[children.length + attributes.size()];
			int index = 0;
			for (index = 0; index < children.length; index++) {
				logicalChildren[index] = children[index];
			}
			for (Iterator iter = attributes.iterator(); iter.hasNext();) {
				logicalChildren[index++] = iter.next();
			}
			return logicalChildren;
		}
		else if (parentElement instanceof DTDNode) {
			return ((DTDNode) parentElement).getChildren();
		}
		return Collections.EMPTY_LIST.toArray();
	}

	public Object[] getElements(java.lang.Object inputElement) {
		Object[] elements = null;
		// Always show the DTDFile "node"
		if (inputElement instanceof DTDModelImpl) {
			elements = new Object[]{((DTDModelImpl) inputElement).getDTDFile()};
		}
		if (elements == null) {
			elements = new Object[0];
		}
		return elements;
	}

	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof DTDNode) {
			DTDNode node = (DTDNode) element;
			if (element instanceof Attribute) {
				// then we must say that the element with the same name
				// as our attribute's parent attributelist is our parent
				parent = node.getParentNode();
				if (parent != null && parent instanceof AttributeList) {
					return getParent(parent);
				}
			}
			if (element instanceof AttributeList) {
				// then we must say that the element with the same name
				// as our attributelist is our parent
				String attListName = ((AttributeList) element).getName();
				Iterator iter = node.getDTDFile().getElementsAndParameterEntityReferences().getNodes().iterator();
				while (iter.hasNext() && parent == null) {
					DTDNode currentNode = (DTDNode) iter.next();
					if (currentNode instanceof Element && currentNode.getName().equals(attListName)) {
						parent = currentNode;
					}
				}
			}

			if (parent == null) {
				parent = ((DTDNode) element).getParentNode();
			}

			// if showing in the logical order, return the IndexedNodeList
			// acting as the parent in the tree
			if (parent == null) {
				if (isShowLogicalOrder()) {
					Object[] indexedNodeLists = getChildren(((DTDModelImpl) fInputObject).getDTDFile());
					for (int i = 0; i < indexedNodeLists.length && parent == null; i++) {
						if (indexedNodeLists[i] instanceof NodeList) {
							if (((NodeList) indexedNodeLists[i]).getNodes().contains(element))
								parent = indexedNodeLists[i];
						}
					}
				}
				else {
					parent = ((DTDModelImpl) fInputObject).getDTDFile();
				}
			}
		}
		else if (element instanceof NodeList && fInputObject instanceof DTDModelImpl) {
			parent = ((DTDModelImpl) fInputObject).getDTDFile();
		}
		return parent;
	}

	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children.length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		fViewer = viewer;

		if (fInputObject instanceof DTDModelImpl) {
			((DTDModelImpl) fInputObject).getDTDFile().removeDTDFileListener(this);
		}
		fInputObject = newInput;
		if (fInputObject instanceof DTDModelImpl) {
			((DTDModelImpl) fInputObject).getDTDFile().addDTDFileListener(this);
		}
	}

	/**
	 * Get the value of showLogicalOrder.
	 * 
	 * @return value of showLogicalOrder.
	 */
	public boolean isShowLogicalOrder() {
		return showLogicalOrder;
	}

	private boolean isVisibleNode(Object o) {
		if (o instanceof AttributeList) {
			return false;
		}
		return true;
	}

	private boolean isVisibleNodeList(NodeList nodeList) {
		/*
		 * All NodesLists should be visible because you can momentarily have
		 * an ATTLIST (for example) without a corresponding ELEMENT
		 * declaration
		 */
		return true;// !nodeList.getListType().equals(DTDRegionTypes.ATTLIST_TAG);
	}

	public void nodeChanged(final DTDNode node) {
		if (fViewer instanceof StructuredViewer) {
			// System.out.println("node changed notified");
			// System.out.println("selection before = " +
			// ((StructuredViewer)view).getSelection());
			if (node instanceof AttributeList && isShowLogicalOrder()) {
				// in this case, we are showing attributes under the element.
				// refresh the element object instead
				Iterator iter = node.getDTDFile().getNodes().iterator();
				while (iter.hasNext()) {
					DTDNode currentNode = (DTDNode) iter.next();
					if (currentNode.getName().equals(node.getName()) && currentNode instanceof Element) {
						((StructuredViewer) fViewer).refresh(currentNode, true);
					}
				} // end of while ()
			}
			else {
				// do standard stuff
				fViewer.getControl().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (fViewer.getControl().isDisposed())
							return;
						if (node.getParentNode() != null) {
							((StructuredViewer) fViewer).refresh(node.getParentNode(), true);
						}
						((StructuredViewer) fViewer).refresh(node, true);
					}
				});
			}
			
			if (node instanceof Attribute) {
				fViewer.getControl().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (fViewer.getControl().isDisposed())
							return;
						/*
						 * go from the attribute to its list and then owning element
						 * so we refresh the tree item there as well
						 */
						Object attrList = node.getParentNode();
						if (attrList != null && attrList instanceof AttributeList) {
							String attListName = ((AttributeList) attrList).getName();
							Iterator iter = node.getDTDFile().getElementsAndParameterEntityReferences().getNodes().iterator();
							Object parent = null;
							while (iter.hasNext() && parent == null) {
								DTDNode currentNode = (DTDNode) iter.next();
								if (currentNode instanceof Element && currentNode.getName().equals(attListName)) {
									parent = currentNode;
								}
							}
							if (parent != null) {
								((StructuredViewer) fViewer).refresh(parent, true);
							}
						}
					}
				});
			}
			// System.out.println("selection after = " +
			// ((StructuredViewer)view).getSelection());
		}
	}

	public void nodesAdded(NodesEvent event) {
		if (fViewer instanceof AbstractTreeViewer) {
			StructuredViewer viewer = (StructuredViewer) fViewer;
			ISelection selection = viewer.getSelection();

			Object firstObj = (!selection.isEmpty() && selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection).getFirstElement() : null;
			DTDNode oldSelectedNode = null;
			if (firstObj instanceof DTDNode) {
				oldSelectedNode = (DTDNode) firstObj;
			}

			final AbstractTreeViewer abstractTreeViewer = (AbstractTreeViewer) fViewer;
			for (Iterator it = event.getNodes().iterator(); it.hasNext();) {
				Object node = it.next();
				final Object parent = getParent(node);
				// Bug 111100 - If it is a top level node (ie. parent is a
				// DTDFile),
				// insert the node directly to improve performance
				if (parent instanceof DTDFile) {
					Object[] objs = getChildren(parent);
					for (int i = 0; i < objs.length; i++) {
						if (objs[i] == node) {
							abstractTreeViewer.insert(parent, node, i);
							break;
						}
					}
				}
				// If the parent node is not a DTDFile, just refresh the
				// parent for now
				else if (parent != null) {
					fViewer.getControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (fViewer.getControl().isDisposed())
								return;
							abstractTreeViewer.refresh(parent, true);
						}
					});
				}
				// You should never reach this block, if you do, just refresh
				// the whole tree
				else {
					fViewer.getControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (fViewer.getControl().isDisposed())
								return;
							abstractTreeViewer.refresh(true);
						}
					});
				}
			}

			Iterator iter = event.getNodes().iterator();
			List newSelection = new ArrayList();
			while (iter.hasNext()) {
				DTDNode node = (DTDNode) iter.next();
				if (oldSelectedNode == null || node.getStructuredDTDDocumentRegion() != oldSelectedNode.getStructuredDTDDocumentRegion() || node.getStartOffset() != oldSelectedNode.getStartOffset() || node.getEndOffset() != oldSelectedNode.getEndOffset()) {
					// add to selection
					newSelection.add(node);
					expandToNode(node);
				}
			}
			if (newSelection.size() > 0) {
				viewer.setSelection(new StructuredSelection(newSelection));
			}

		}
	}

	public void nodesRemoved(NodesEvent event) {
		if (fViewer instanceof AbstractTreeViewer) {
			AbstractTreeViewer abstractTreeViewer = (AbstractTreeViewer) fViewer;
			for (Iterator iter = event.getNodes().iterator(); iter.hasNext();) {
				abstractTreeViewer.remove(iter.next());
			}
		}

	}

	/**
	 * Set the value of showLogicalOrder.
	 * 
	 * @param value
	 *            Value to assign to showLogicalOrder.
	 */
	public void setShowLogicalOrder(boolean value) {
		this.showLogicalOrder = value;
		if (!value) {
			// if not using logical order, lose the cached lists
			logicalNodeLists = null;
		}
	}
}
