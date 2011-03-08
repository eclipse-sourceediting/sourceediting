/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
		List parents = getParents(element);
		Object parent = null;
		if(parents.size() > 0) {
			parent = parents.get(0);
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

	/**
	 * <p>If a node changed then refresh the tree for that node</p>
	 * 
	 * @see org.eclipse.wst.dtd.core.internal.event.IDTDFileListener#nodeChanged(org.eclipse.wst.dtd.core.internal.DTDNode)
	 */
	public void nodeChanged(final DTDNode node) {
		refreshTree(node);
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

			//update the tree
			refreshTree(event);

			final List nodes = event.getNodes();
			if (!nodes.isEmpty()) {
				final DTDNode node = (DTDNode) nodes.get(0);
				if (oldSelectedNode == null || node.getStructuredDTDDocumentRegion() != oldSelectedNode.getStructuredDTDDocumentRegion() || node.getStartOffset() != oldSelectedNode.getStartOffset() || node.getEndOffset() != oldSelectedNode.getEndOffset()) {
					expandToNode(node);
					viewer.setSelection(new StructuredSelection(node));
				}
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

		//update the tree
		refreshTree(event);
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
	
	/**
	 * <p>Used to update the tree after a node event such as a node added or removed.</p>
	 * @param event the {@link NodesEvent} that caused the tree to need updating
	 */
	private void refreshTree(NodesEvent event) {
		for (Iterator it = event.getNodes().iterator(); it.hasNext();) {
			Object node = it.next();
			this.refreshTree(node);
			
		}
	}
	
	/**
	 * <p>Refreshes the tree from the parents of the given node.</p>
	 * @param node refresh the tree from the parents of this node
	 */
	private void refreshTree(Object node) {
		List parents = getParents(node);
		if(parents.size() > 0) {
			for(int p = 0; p < parents.size(); ++p) {
				final Object parent = parents.get(p);
			
				// Bug 111100 - If it is a top level node (ie. parent is a
				// DTDFile),
				// insert the node directly to improve performance
				if (parent instanceof DTDFile) {
					Object[] objs = getChildren(parent);
					for (int i = 0; i < objs.length; i++) {
						if (objs[i] == node) {
							((AbstractTreeViewer) fViewer).insert(parent, node, i);
							break;
						}
					}
				}
				
				this.refreshTreeNode(parent, true);
			}
		}
	}
	
	/**
	 * @param element get the tree parents of this element
	 * @return {@link List} of parents of the given element
	 */
	private List getParents(Object element) {
		List parents = new ArrayList();
		
		Object parent = null;
		if (element instanceof DTDNode) {
			DTDNode node = (DTDNode) element;
			if (element instanceof Attribute) {
				parent = node.getParentNode();
				if (parent != null && parent instanceof AttributeList) {
					parents.addAll(getElementParentsOfAttributeList((AttributeList)parent));
				}
			} else if(element instanceof AttributeList) {
				parents.addAll(getElementParentsOfAttributeList((AttributeList)element));
			}

			// if showing in the logical order, return the IndexedNodeList
			// acting as a parent in the tree
			if (isShowLogicalOrder()) {
				Object[] indexedNodeLists = getChildren(((DTDModelImpl) fInputObject).getDTDFile());
				for (int i = 0; i < indexedNodeLists.length && parent == null; i++) {
					if (indexedNodeLists[i] instanceof NodeList) {
						if (((NodeList) indexedNodeLists[i]).getNodes().contains(element)) {
							parents.add(indexedNodeLists[i]);
						}
					}
				}
			}
			
			//try and get the simple parent
			parent = ((DTDNode) element).getParentNode();
			if(parent != null) {
				parents.add(parent);
			}
			
			//if no parents found must be new nodes so refresh from root
			if(parents.size() == 0) {
				parents.add(((DTDModelImpl) fInputObject).getDTDFile());
			}
		}else if (element instanceof NodeList && fInputObject instanceof DTDModelImpl) {
			parents.add(((DTDModelImpl) fInputObject).getDTDFile());
		}
		
		return parents;
	}
	
	/**
	 * @param attList get the element parents of the given {@link AttributeList}
	 * @return the element parents of the given {@link AttributeList}, if no parents
	 * can be found the list contains the DTD file element
	 */
	private List getElementParentsOfAttributeList(AttributeList attList) {
		List parents = new ArrayList();
		Iterator iterAttList = attList.getDTDFile().getNodes().iterator();
		while (iterAttList.hasNext()) {
			DTDNode currentNode = (DTDNode) iterAttList.next();
			if (currentNode instanceof Element &&
					currentNode.getName().equals(attList.getName())) {

				parents.add(currentNode);
			}
		}
		
		if(parents.size() == 0) {
			parents.add(((DTDModelImpl) fInputObject).getDTDFile());
		}
		
		return parents;
	}
	
	/**
	 * <p>Executes a refresh on the {@link AbstractTreeViewer} for the given
	 * node</p>
	 * 
	 * @param node refresh the tree for this node
	 * @param updateLabels <code>true</code> to update the labels on the tree,
	 * <code>false</code> otherwise.
	 */
	private void refreshTreeNode(final Object node, final boolean updateLabels) {
		fViewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!fViewer.getControl().isDisposed()) {
					if(node != null) {
						((AbstractTreeViewer) fViewer).refresh(node, updateLabels);
					} else {
						((AbstractTreeViewer) fViewer).refresh(updateLabels);
					}
				}
			}
		});
	}
}
