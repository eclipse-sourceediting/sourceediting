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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.common.ui.internal.dnd.DragAndDropCommand;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;
import org.eclipse.wst.dtd.ui.internal.dnd.DTDDragAndDropManager;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.internal.util.Assert;
import org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration;

/**
 * Configuration for outline view page which shows DTD content.
 * 
 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration
 * @since 1.0
 */
public class DTDContentOutlineConfiguration extends ContentOutlineConfiguration {
	private IContentProvider fContentProvider = null;
	private ILabelProvider fLabelProvider = null;

	private DTDContextMenuHelper fMenuHelper;
	private TransferDragSourceListener[] fTransferDragSourceListeners;
	private TransferDropTargetListener[] fTransferDropTargetListeners;
	private Map fViewerContributions;
	private final String OUTLINE_ORDER_PREF = "outline-order"; //$NON-NLS-1$
	private final String OUTLINE_SORT_PREF = "outline-sort"; //$NON-NLS-1$
	private static final String OUTLINE_FILTER_PREF = "org.eclipse.wst.dtd.ui.OutlinePage"; //$NON-NLS-1$
	
	/**
	 * Default constructor for DTDContentOutlineConfiguration.
	 */
	public DTDContentOutlineConfiguration() {
		// Must have empty constructor to createExecutableExtension
		super();
		fViewerContributions = new HashMap(2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#createToolbarContributions(org.eclipse.jface.viewers.TreeViewer)
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
		IMenuListener listener = null;
		if (fMenuHelper == null && viewer.getInput() instanceof DTDModelImpl) {
			fMenuHelper = new DTDContextMenuHelper((DTDModelImpl) viewer.getInput());
			fMenuHelper.createMenuListenersFor(viewer);
		}
		if (fMenuHelper != null) {
			listener = fMenuHelper.getMenuListener();
		}
		return listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return DTDUIPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getTransferDragSourceListeners(org.eclipse.jface.viewers.TreeViewer)
	 */
	public TransferDragSourceListener[] getTransferDragSourceListeners(final TreeViewer treeViewer) {
		if (fTransferDragSourceListeners == null) {
			fTransferDragSourceListeners = new TransferDragSourceListener[]{new TransferDragSourceListener() {
				public void dragFinished(DragSourceEvent event) {
					LocalSelectionTransfer.getTransfer().setSelection(null);
				}
	
				public void dragSetData(DragSourceEvent event) {
				}
	
				public void dragStart(DragSourceEvent event) {
					LocalSelectionTransfer.getTransfer().setSelection(treeViewer.getSelection());
				}
	
				public Transfer getTransfer() {
					return LocalSelectionTransfer.getTransfer();
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
	public TransferDropTargetListener[] getTransferDropTargetListeners(final TreeViewer treeViewer) {
		if (fTransferDropTargetListeners == null) {
			fTransferDropTargetListeners = new TransferDropTargetListener[]{new TransferDropTargetListener() {
				public void dragEnter(DropTargetEvent event) {
				}
	
				public void dragLeave(DropTargetEvent event) {
				}
	
				public void dragOperationChanged(DropTargetEvent event) {
				}
	
				public void dragOver(DropTargetEvent event) {
					event.feedback = DND.FEEDBACK_SELECT;
					float feedbackFloat = getHeightInItem(event);
					if (feedbackFloat > 0.75) {
						event.feedback = DND.FEEDBACK_INSERT_AFTER;
					}
					else if (feedbackFloat < 0.25) {
						event.feedback = DND.FEEDBACK_INSERT_BEFORE;
					}
					event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				}
	
				public void drop(DropTargetEvent event) {
					if (event.operations != DND.DROP_NONE && LocalSelectionTransfer.getTransfer().getSelection() instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer().getSelection();
						if (selection != null && !selection.isEmpty() && event.item != null && event.item.getData() != null) {
							/*
							 * the command uses these numbers instead of the
							 * feedback constants (even though it converts in
							 * the other direction as well)
							 */
							float feedbackFloat = getHeightInItem(event);
	
							final DragAndDropCommand command = new DTDDragAndDropManager().createCommand(event.item.getData(), feedbackFloat, event.operations, event.detail, selection.toList());
							if (command != null && command.canExecute()) {
								SafeRunnable.run(new SafeRunnable() {
									public void run() throws Exception {
										command.execute();
									}
								});
							}
						}
					}
				}
	
				public void dropAccept(DropTargetEvent event) {
				}
	
				private float getHeightInItem(DropTargetEvent event) {
					if(event.item == null) return .5f;
					if (event.item instanceof TreeItem) {
						TreeItem treeItem = (TreeItem) event.item;
						Control control = treeItem.getParent();
						Point point = control.toControl(new Point(event.x, event.y));
						Rectangle bounds = treeItem.getBounds();
						return (float) (point.y - bounds.y) / (float) bounds.height;
					}
					else if (event.item instanceof TableItem) {
						TableItem tableItem = (TableItem) event.item;
						Control control = tableItem.getParent();
						Point point = control.toControl(new Point(event.x, event.y));
						Rectangle bounds = tableItem.getBounds(0);
						return (float) (point.y - bounds.y) / (float) bounds.height;
					}
					else {
						return 0.0F;
					}
				}
				
				public Transfer getTransfer() {
					return LocalSelectionTransfer.getTransfer();
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
			fMenuHelper.removeMenuListenersFor(viewer);
			fMenuHelper = null;
		}
	}
	
	protected String getOutlineFilterTarget(){
		return OUTLINE_FILTER_PREF ;
	}
}
