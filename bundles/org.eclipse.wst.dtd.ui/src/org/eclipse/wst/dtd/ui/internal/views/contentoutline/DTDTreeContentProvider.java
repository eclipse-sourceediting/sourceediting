/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.internal.views.contentoutline;

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


/**
 * Based on com.ibm.etools.dtd.editor.viewers.DTDTreeContentProvider
 */

public class DTDTreeContentProvider implements ITreeContentProvider, IDTDFileListener {

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
				if (logicalNodeLists == null) {
					DTDFile file = (DTDFile) parentElement;
					Object[] children = file.getNodeLists().toArray();
					for (int i = 0; i < children.length; i++) {
						children[i] = new IndexedNodeList((NodeList) children[i]);
					}
					logicalNodeLists = children;
				}
				return logicalNodeLists;
			}
			else {
				return ((DTDFile) parentElement).getNodes().toArray();
			}
		}
		else if (parentElement instanceof NodeList) {
			return ((NodeList) parentElement).getNodes().toArray();
		}
		else if (parentElement instanceof IndexedNodeList) {
			return ((IndexedNodeList) parentElement).getTarget().getNodes().toArray();
		}
		else if (parentElement instanceof Element && isShowLogicalOrder()) {
			// then group the attributes under the element
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
			if (element instanceof Attribute && isShowLogicalOrder()) {
				// then we must say that the element with the same name
				// as our attributelist is our parent
				parent = node.getParentNode();
				if (parent != null) {
					String attListName = ((DTDNode) parent).getName();
					Iterator iter = node.getDTDFile().getNodes().iterator();
					while (iter.hasNext()) {
						DTDNode currentNode = (DTDNode) iter.next();
						if (currentNode instanceof AttributeList && currentNode.getName().equals(attListName)) {
							parent = currentNode;
						}
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
						if (indexedNodeLists[i] instanceof IndexedNodeList) {
							if (((IndexedNodeList) indexedNodeLists[i]).contains(element))
								parent = indexedNodeLists[i];
						}
					}
				}
				else {
					parent = ((DTDModelImpl) fInputObject).getDTDFile();
				}
			}
		}
		else if (element instanceof IndexedNodeList && fInputObject instanceof DTDModelImpl) {
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

	public void nodeChanged(DTDNode node) {
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
						((StructuredViewer) fViewer).refresh(currentNode);
					}
				} // end of while ()
			}
			else {
				// do standard stuff
				((StructuredViewer) fViewer).refresh(node);
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

			// for now just refresh the whole view
			AbstractTreeViewer abstractTreeViewer = (AbstractTreeViewer) fViewer;
			abstractTreeViewer.refresh();

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
	 * @param v
	 *            Value to assign to showLogicalOrder.
	 */
	public void setShowLogicalOrder(boolean v) {
		this.showLogicalOrder = v;
		if (!v) {
			// if not using logical order, lose the cached lists
			logicalNodeLists = null;
		}
	}
}
