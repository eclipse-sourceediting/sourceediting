/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.wst.sse.core.AdapterFactory;
import org.eclipse.wst.sse.ui.IReleasable;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration;
import org.eclipse.wst.ui.dnd.ObjectTransfer;
import org.eclipse.wst.ui.dnd.ViewerDragAdapter;
import org.eclipse.wst.ui.dnd.ViewerDropAdapter;
import org.eclipse.wst.xml.ui.ui.dnd.XMLDragAndDropManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


public class XMLContentOutlineConfiguration extends StructuredContentOutlineConfiguration {

	protected class ActionManagerMenuListener implements IMenuListener, IReleasable {
		private XMLNodeActionManager fActionManager;
		private TreeViewer fTreeViewer;

		public ActionManagerMenuListener(TreeViewer viewer) {
			fTreeViewer = viewer;
			fActionManager = createNodeActionManager(fTreeViewer);
		}

		public void menuAboutToShow(IMenuManager manager) {
			if (fActionManager != null)
				fActionManager.fillContextMenu(manager, fTreeViewer.getSelection());
		}

		public void release() {
			fTreeViewer = null;
			fActionManager.setModel(null);
		}
	}

	protected ActionManagerMenuListener fContextMenuFiller = null;

	private TransferDragSourceListener[] fTransferDragSourceListeners;
	private TransferDropTargetListener[] fTransferDropTargetListeners;

	public XMLContentOutlineConfiguration() {
		super();
	}

	protected XMLNodeActionManager createNodeActionManager(TreeViewer treeViewer) {
		return new XMLNodeActionManager(getEditor().getModel(), treeViewer);
	}

	/**
	 */
	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null) {
			if (getFactory() != null) {
				fContentProvider = new JFaceNodeContentProvider((AdapterFactory) getFactory());
			}
			else {
				fContentProvider = super.getContentProvider(viewer);
			}
		}
		return fContentProvider;
	}

	/**
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null) {
			if (getFactory() != null) {
				fLabelProvider = new JFaceNodeLabelProvider((AdapterFactory) getFactory());
			}
			else {
				fLabelProvider = super.getLabelProvider(viewer);
			}
		}
		return fLabelProvider;
	}

	/**
	 */
	public IMenuListener getMenuListener(TreeViewer viewer) {
		if (fContextMenuFiller == null) {
			fContextMenuFiller = new ActionManagerMenuListener(viewer);
		}
		return fContextMenuFiller;
	}

	public List getSelectedNodes(NodeSelectionChangedEvent event) {
		List selectedNodes = new ArrayList(super.getSelectedNodes(event));

		for (int i = 0; i < selectedNodes.size(); i++) {
			Object selectedNode = selectedNodes.get(i);
			if (selectedNode instanceof Node) {
				Node eachNode = (Node) selectedNode;
				//replace attribute node in selection with its parent
				if (eachNode.getNodeType() == Node.ATTRIBUTE_NODE)
					selectedNodes.set(i, ((Attr) eachNode).getOwnerElement());
				//replace TextNode in selection with its parent
				else if (eachNode.getNodeType() == Node.TEXT_NODE)
					selectedNodes.set(i, eachNode.getParentNode());
			}
		}

		return selectedNodes;
	}

	/**
	 */
	public TransferDragSourceListener[] getTransferDragSourceListeners(TreeViewer treeViewer) {
		if (fTransferDragSourceListeners == null) {
			// emulate the XMLDragAndDropManager
			final ViewerDragAdapter dragAdapter = new ViewerDragAdapter(treeViewer);
			fTransferDragSourceListeners = new TransferDragSourceListener[]{new TransferDragSourceListener() {
				public void dragFinished(DragSourceEvent event) {
					dragAdapter.dragFinished(event);
				}

				public void dragSetData(DragSourceEvent event) {
					dragAdapter.dragSetData(event);
				}

				public void dragStart(DragSourceEvent event) {
					dragAdapter.dragStart(event);
				}

				public Transfer getTransfer() {
					return ObjectTransfer.getInstance();
				}
			}};
		}

		return fTransferDragSourceListeners;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public TransferDropTargetListener[] getTransferDropTargetListeners(TreeViewer treeViewer) {
		if (fTransferDropTargetListeners == null) {
			// emulate the XMLDragAnDropManager
			final ViewerDropAdapter dropAdapter = new ViewerDropAdapter(treeViewer, new XMLDragAndDropManager());
			fTransferDropTargetListeners = new TransferDropTargetListener[]{new TransferDropTargetListener() {
				public void dragEnter(DropTargetEvent event) {
					dropAdapter.dragEnter(event);
				}

				public void dragLeave(DropTargetEvent event) {
					dropAdapter.dragLeave(event);
				}

				public void dragOperationChanged(DropTargetEvent event) {
					dropAdapter.dragOperationChanged(event);
				}

				public void dragOver(DropTargetEvent event) {
					dropAdapter.dragOver(event);
				}

				public void drop(DropTargetEvent event) {
					dropAdapter.drop(event);
				}

				public void dropAccept(DropTargetEvent event) {
					dropAdapter.dropAccept(event);
				}

				public Transfer getTransfer() {
					return ObjectTransfer.getInstance();
				}

				public boolean isEnabled(DropTargetEvent event) {
					return getTransfer().isSupportedType(event.currentDataType);
				}
			}};
		}
		return fTransferDropTargetListeners;
	}

	/**
	 */
	public void unconfigure(TreeViewer viewer) {
		super.unconfigure(viewer);
		fTransferDragSourceListeners = null;
		fTransferDropTargetListeners = null;
		if (fContextMenuFiller != null) {
			fContextMenuFiller.release();
			fContextMenuFiller = null;
		}
		//		TODO: Add DnD support
		//		XMLDragAndDropManager.addDragAndDropSupport(fTreeViewer);
	}
}
