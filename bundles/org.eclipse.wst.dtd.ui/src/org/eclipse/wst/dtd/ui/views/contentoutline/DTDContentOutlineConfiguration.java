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
package org.eclipse.wst.dtd.ui.views.contentoutline;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.wst.dtd.ui.DTDEditorPlugin;
import org.eclipse.wst.dtd.ui.dnd.DTDDragAndDropManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.preferences.PreferenceKeyGenerator;
import org.eclipse.wst.sse.ui.util.Assert;
import org.eclipse.wst.sse.ui.views.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration;
import org.eclipse.wst.ui.dnd.ObjectTransfer;
import org.eclipse.wst.ui.dnd.ViewerDragAdapter;
import org.eclipse.wst.ui.dnd.ViewerDropAdapter;

/**
 * @author nitin
 *  
 */
public class DTDContentOutlineConfiguration extends StructuredContentOutlineConfiguration {

	private DTDContextMenuHelper fMenuHelper;
	private TransferDragSourceListener[] fTransferDragSourceListeners;
	private TransferDropTargetListener[] fTransferDropTargetListeners;
	private Map fViewerContributions;
	private final String OUTLINE_ORDER_PREF = "outline-order"; //$NON-NLS-1$
	private final String OUTLINE_SORT_PREF = "outline-sort"; //$NON-NLS-1$

	/**
	 * @param editor
	 */
	public DTDContentOutlineConfiguration() {
		super();
		fViewerContributions = new HashMap(2);
	}

	public IContributionItem[] createToolbarContributions(TreeViewer viewer) {
		Assert.isTrue(getContentProvider(viewer) instanceof DTDTreeContentProvider);
		IContributionItem[] items = super.createToolbarContributions(viewer);

		SortAction sortAction = new SortAction(viewer, DTDEditorPlugin.getDefault().getPreferenceStore(), getSortPreferenceKey());
		OrderAction orderAction = new OrderAction(viewer, (DTDTreeContentProvider) getContentProvider(viewer), DTDEditorPlugin.getDefault().getPreferenceStore(), getOrderPreferenceKey());
		IContributionItem sortItem = new PropertyChangeUpdateActionContributionItem(sortAction);
		IContributionItem orderItem = new PropertyChangeUpdateActionContributionItem(orderAction);

		if (items == null) {
			items = new IContributionItem[2];
			items[0] = sortItem;
			items[1] = orderItem;
		} else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 2];
			combinedItems[0] = sortItem;
			combinedItems[1] = orderItem;
			System.arraycopy(items, 0, combinedItems, 2, items.length);
			items = combinedItems;
		}
		return items;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getContentProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null) {
			fContentProvider = new DTDTreeContentProvider();
		}
		return super.getContentProvider(viewer);
	}

	/**
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getLabelProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null) {
			fLabelProvider = new DTDLabelProvider();
		}
		return super.getLabelProvider(viewer);
	}

	/**
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getMenuListener(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IMenuListener getMenuListener(TreeViewer viewer) {
		fMenuHelper.createMenuListenersFor(viewer);
		return fMenuHelper.getMenuListener();
	}

	public String getOrderPreferenceKey() {
		return PreferenceKeyGenerator.generateKey(OUTLINE_ORDER_PREF, getDeclaringID());
	}

	public String getSortPreferenceKey() {
		return PreferenceKeyGenerator.generateKey(OUTLINE_SORT_PREF, getDeclaringID());
	}

	/**
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

	public void setEditor(StructuredTextEditor editor) {
		super.setEditor(editor);
		fMenuHelper = new DTDContextMenuHelper(editor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#unconfigure(org.eclipse.jface.viewers.TreeViewer)
	 */
	public void unconfigure(TreeViewer viewer) {
		super.unconfigure(viewer);
		fViewerContributions.remove(viewer);
		fMenuHelper.removeMenuListenersFor(viewer);
	}

}
