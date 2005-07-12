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
package org.eclipse.wst.xml.ui.internal.views.contentoutline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.wst.common.ui.internal.dnd.ObjectTransfer;
import org.eclipse.wst.common.ui.internal.dnd.ViewerDragAdapter;
import org.eclipse.wst.common.ui.internal.dnd.ViewerDropAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapterFactory;
import org.eclipse.wst.sse.ui.internal.IReleasable;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline.StructuredContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.internal.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeContentProvider;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.eclipse.wst.xml.ui.internal.dnd.XMLDragAndDropManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class XMLContentOutlineConfiguration extends StructuredContentOutlineConfiguration {
	private IContentProvider fContentProvider = null;
	private ILabelProvider fLabelProvider = null;
	/*
	 * Preference key for Show Attributes
	 */
	private final String OUTLINE_SHOW_ATTRIBUTE_PREF = "outline-show-attribute-editor"; //$NON-NLS-1$

	private class ActionManagerMenuListener implements IMenuListener, IReleasable {
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

	/**
	 * Toggle action for whether or not to display element's first attribute
	 */
	private class ToggleShowAttributeAction extends PropertyChangeUpdateAction {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
		private TreeViewer fTreeViewer;

		public ToggleShowAttributeAction(IPreferenceStore store, String preference, TreeViewer treeViewer) {
			super(XMLUIMessages.XMLContentOutlineConfiguration_0, store, preference, false);
			setToolTipText(getText());
			// images needed
			// setDisabledImageDescriptor(SYNCED_D);
			// setImageDescriptor(SYNCED_E);
			fTreeViewer = treeViewer;
			update();
		}

		public void update() {
			super.update();
			updateForShowAttributes(isChecked(), fTreeViewer);
		}
	}

	protected ActionManagerMenuListener fContextMenuFiller = null;

	private TransferDragSourceListener[] fTransferDragSourceListeners;
	private TransferDropTargetListener[] fTransferDropTargetListeners;

	public XMLContentOutlineConfiguration() {
		super();
	}

	protected IContributionItem[] createMenuContributions(TreeViewer viewer) {
		IContributionItem[] items;
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=88444
		IContributionItem showAttributeItem = new PropertyChangeUpdateActionContributionItem(new ToggleShowAttributeAction(getPreferenceStore(), OUTLINE_SHOW_ATTRIBUTE_PREF, viewer));
		items = super.createMenuContributions(viewer);
		if (items == null) {
			items = new IContributionItem[]{showAttributeItem};
		}
		else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 1];
			System.arraycopy(items, 0, combinedItems, 0, items.length);
			combinedItems[items.length] = showAttributeItem;
			items = combinedItems;
		}
		return items;
	}

	protected XMLNodeActionManager createNodeActionManager(TreeViewer treeViewer) {
		return new XMLNodeActionManager(getEditor().getModel(), treeViewer);
	}

	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null) {
			if (getFactory() != null) {
				fContentProvider = new JFaceNodeContentProvider((INodeAdapterFactory) getFactory());
			}
			else {
				fContentProvider = super.getContentProvider(viewer);
			}
		}
		return fContentProvider;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline.ContentOutlineConfiguration#getLabelProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null) {
			if (getFactory() != null) {
				fLabelProvider = new JFaceNodeLabelProvider((INodeAdapterFactory) getFactory());
			}
			else {
				fLabelProvider = super.getLabelProvider(viewer);
			}
		}
		return fLabelProvider;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline.ContentOutlineConfiguration#getMenuListener(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IMenuListener getMenuListener(TreeViewer viewer) {
		if (fContextMenuFiller == null) {
			fContextMenuFiller = new ActionManagerMenuListener(viewer);
		}
		return fContextMenuFiller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getNodes(java.util.List)
	 */
	public List getNodes(List nodes) {
		List filteredNodes = new ArrayList(super.getNodes(nodes));
		for (int i = 0; i < filteredNodes.size(); i++) {
			Object selectedNode = filteredNodes.get(i);
			if (selectedNode instanceof Node) {
				Node eachNode = (Node) selectedNode;
				// replace attribute node in selection with its parent
				if (eachNode.getNodeType() == Node.ATTRIBUTE_NODE)
					filteredNodes.set(i, ((Attr) eachNode).getOwnerElement());
				// replace TextNode in selection with its parent
				else if (eachNode.getNodeType() == Node.TEXT_NODE)
					filteredNodes.set(i, eachNode.getParentNode());
			}
		}
		return filteredNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getSelectedNodes(org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent)
	 */
	public List getSelectedNodes(NodeSelectionChangedEvent event) {
		return getNodes(super.getSelectedNodes(event));
	}

	/**
	 * @see org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline.ContentOutlineConfiguration#getTransferDragSourceListeners(org.eclipse.jface.viewers.TreeViewer)
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
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getTransferDropTargetListeners(org.eclipse.jface.viewers.TreeViewer)
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

	public void unconfigure(TreeViewer viewer) {
		super.unconfigure(viewer);
		fTransferDragSourceListeners = null;
		fTransferDropTargetListeners = null;
		if (fContextMenuFiller != null) {
			fContextMenuFiller.release();
			fContextMenuFiller = null;
		}
		// TODO: Add DnD support
		// XMLDragAndDropManager.addDragAndDropSupport(fTreeViewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return XMLUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Updates show attributes flag in JFaceNodeAdapter to indicate whether or
	 * not to show attributes in outline view. Also refreshes tree view due to
	 * label updates.
	 * 
	 * @param showAttr
	 * @param viewer
	 */
	void updateForShowAttributes(boolean showAttr, TreeViewer viewer) {
		((JFaceNodeAdapterFactory) getFactory()).setShowAttribute(showAttr);
		// refresh the outline view
		viewer.refresh();
	}
}
