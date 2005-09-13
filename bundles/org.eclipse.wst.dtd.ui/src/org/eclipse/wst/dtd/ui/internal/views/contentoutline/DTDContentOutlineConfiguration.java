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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
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
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.dnd.DTDDragAndDropManager;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline.StructuredContentOutlineConfiguration;
import org.eclipse.wst.sse.ui.internal.util.Assert;

/**
 * A StructuredContentOutlineConfiguration for DTD models
 * 
 * @plannedfor 1.0
 * 
 */
public class DTDContentOutlineConfiguration extends StructuredContentOutlineConfiguration {
	private IContentProvider fContentProvider = null;
	private ILabelProvider fLabelProvider = null;

	private DTDContextMenuHelper fMenuHelper;
	private TransferDragSourceListener[] fTransferDragSourceListeners;
	private TransferDropTargetListener[] fTransferDropTargetListeners;
	private Map fViewerContributions;
	private final String OUTLINE_ORDER_PREF = "outline-order"; //$NON-NLS-1$
	private final String OUTLINE_SORT_PREF = "outline-sort"; //$NON-NLS-1$

	public DTDContentOutlineConfiguration() {
		super();
		fViewerContributions = new HashMap(2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration#createToolbarContributions(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IContributionItem[] createToolbarContributions(TreeViewer viewer) {
		Assert.isTrue(getContentProvider(viewer) instanceof DTDTreeContentProvider, "invalid content provider on viewer"); //$NON-NLS-1$
		IContributionItem[] items = super.createToolbarContributions(viewer);

		SortAction sortAction = new SortAction(viewer, DTDUIPlugin.getDefault().getPreferenceStore(), OUTLINE_SORT_PREF);
		OrderAction orderAction = new OrderAction(viewer, (DTDTreeContentProvider) getContentProvider(viewer), DTDUIPlugin.getDefault().getPreferenceStore(), OUTLINE_ORDER_PREF);
		IContributionItem sortItem = new PropertyChangeUpdateActionContributionItem(sortAction);
		IContributionItem orderItem = new PropertyChangeUpdateActionContributionItem(orderAction);

		if (items == null) {
			items = new IContributionItem[2];
			items[0] = sortItem;
			items[1] = orderItem;
		}
		else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 2];
			combinedItems[0] = sortItem;
			combinedItems[1] = orderItem;
			System.arraycopy(items, 0, combinedItems, 2, items.length);
			items = combinedItems;
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getContentProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null) {
			fContentProvider = new DTDTreeContentProvider();
		}
		// return super.getContentProvider(viewer);
		return fContentProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getLabelProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null) {
			fLabelProvider = new DTDLabelProvider();
		}
		// return super.getLabelProvider(viewer);
		return fLabelProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getMenuListener(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IMenuListener getMenuListener(TreeViewer viewer) {
		if (fMenuHelper == null) {
//			fMenuHelper = new DTDContextMenuHelper(getEditor());
			System.out.println("DTDContextMenuHelper not implemented");
		}
//		fMenuHelper.createMenuListenersFor(viewer);
//		return fMenuHelper.getMenuListener();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return DTDUIPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getTransferDragSourceListeners(org.eclipse.jface.viewers.TreeViewer)
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
			final ViewerDropAdapter dropAdapter = new ViewerDropAdapter(treeViewer, new DTDDragAndDropManager());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#unconfigure(org.eclipse.jface.viewers.TreeViewer)
	 */
	public void unconfigure(TreeViewer viewer) {
		super.unconfigure(viewer);
		fViewerContributions.remove(viewer);
		if (fMenuHelper != null) {
//			fMenuHelper.removeMenuListenersFor(viewer);
		}
	}
}
