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
package org.eclipse.wst.sse.ui.views.contentoutline;

import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.ViewerSelectionManager;
import org.eclipse.wst.sse.ui.ui.ActiveEditorActionHandler;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.ITextSelectionListener;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.sse.ui.view.events.TextSelectionChangedEvent;


public class StructuredTextEditorContentOutlinePage extends ContentOutlinePage implements INodeSelectionListener, ITextSelectionListener, IUpdate {
	// Disables Tree redraw during large model changes
	protected class ControlRedrawEnabler implements IModelStateListener {
		public void modelAboutToBeChanged(IStructuredModel model) {
			Control control = getControl();
			if ((control != null) && (!control.isDisposed()))
				control.setRedraw(false);
		}

		public void modelChanged(IStructuredModel model) {
			Control control = getControl();
			if ((control != null) && (!control.isDisposed()))
				control.setRedraw(true);
		}

		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
			// do nothing
		}

		public void modelResourceDeleted(IStructuredModel model) {
			// do nothing
		}

		public void modelResourceMoved(IStructuredModel originalmodel, IStructuredModel movedmodel) {
			// do nothing
		}
	}

	protected static ContentOutlineConfiguration NULL_CONFIGURATION = new ContentOutlineConfiguration();
	private TransferDragSourceListener[] fActiveDragListeners;
	private TransferDropTargetListener[] fActiveDropListeners;
	private ContentOutlineConfiguration fConfiguration;

	private MenuManager fContextMenuManager;
	private DelegatingDragAdapter fDragAdapter;
	private DragSource fDragSource;
	private DelegatingDropAdapter fDropAdapter;
	private DropTarget fDropTarget;
	protected IStructuredModel fModel;
	// current selection, maintained so selection doesn't bounce back from the
	// Tree
	ISelection fSelection;
	protected SourceEditorTreeViewer fTreeViewer;
	protected ViewerSelectionManager fViewerSelectionManager;
	private IModelStateListener fInternalModelStateListener;

	public StructuredTextEditorContentOutlinePage() {
		super();
		fInternalModelStateListener = new ControlRedrawEnabler();
		fSelection = StructuredSelection.EMPTY;
	}

	/**
	 * @see ContentOutlinePage#createControl
	 */
	public void createControl(Composite parent) {
		fTreeViewer = new SourceEditorTreeViewer(new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
		// create the context menu
		fContextMenuManager = new MenuManager("#popup"); //$NON-NLS-1$
		fContextMenuManager.setRemoveAllWhenShown(true);
		Menu menu = fContextMenuManager.createContextMenu(fTreeViewer.getControl());
		fTreeViewer.getControl().setMenu(menu);
		fDragAdapter = new DelegatingDragAdapter();
		fDragSource = new DragSource(fTreeViewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		fDropAdapter = new DelegatingDropAdapter();
		fDropTarget = new DropTarget(fTreeViewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		setConfiguration(getConfiguration());
		fTreeViewer.setInput(getModel());
		IJFaceNodeAdapterFactory adapterFactory = getViewerRefreshFactory();
		if (adapterFactory != null) {
			adapterFactory.addListener(fTreeViewer);
		}
		// update local selection on invalid selection to prevent bounces
		fTreeViewer.addInvalidSelectionListener(new ISelectionListener() {
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				fSelection = selection;
			}
		});
		fTreeViewer.addPostSelectionChangedListener(this);
	}

	public void dispose() {
		super.dispose();
		// remove this text viewer from the old model's list of model state
		// listeners
		if (fModel != null)
			fModel.removeModelStateListener(fInternalModelStateListener);
		// disconnect from the ViewerSelectionManager
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.removeNodeSelectionListener(this);
		}
		IJFaceNodeAdapterFactory adapterFactory = getViewerRefreshFactory();
		if (adapterFactory != null) {
			adapterFactory.removeListener(fTreeViewer);
		}
		setConfiguration(NULL_CONFIGURATION);
	}

	/**
	 * @return
	 */
	public ContentOutlineConfiguration getConfiguration() {
		if (fConfiguration == null)
			return NULL_CONFIGURATION;
		return fConfiguration;
	}

	public Control getControl() {
		if (getTreeViewer() == null)
			return null;
		return getTreeViewer().getControl();
	}

	/**
	 */
	protected IStructuredModel getModel() {
		return fModel;
	}

	protected List getSelectedNodes(NodeSelectionChangedEvent event) {
		return getConfiguration().getSelectedNodes(event);
	}

	public ISelection getSelection() {
		if (getTreeViewer() == null)
			return StructuredSelection.EMPTY;
		return getTreeViewer().getSelection();
	}

	/**
	 * Returns this page's tree viewer.
	 * 
	 * @return this page's tree viewer, or <code>null</code> if
	 *         <code>createControl</code> has not been called yet
	 */
	protected TreeViewer getTreeViewer() {
		return fTreeViewer;
	}

	protected IJFaceNodeAdapterFactory getViewerRefreshFactory() {
		if (getModel() == null)
			return null;
		IFactoryRegistry factoryRegistry = getModel().getFactoryRegistry();
		IJFaceNodeAdapterFactory adapterFactory = (IJFaceNodeAdapterFactory) factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		return adapterFactory;
	}

	/**
	 * Returns this page's viewer selection manager.
	 * 
	 * @return this page's viewer selection manager, or <code>null</code> if
	 *         <code>setViewerSelectionManager</code> has not been called
	 *         yet
	 */
	public ViewerSelectionManager getViewerSelectionManager() {
		return fViewerSelectionManager;
	}

	public void nodeSelectionChanged(NodeSelectionChangedEvent event) {
		if (getTreeViewer() != null && getConfiguration().isLinkedWithEditor(getTreeViewer())) {
			List selectedNodes = getSelectedNodes(event);
			if (selectedNodes != null) {
				StructuredSelection selection = new StructuredSelection(selectedNodes);
				setSelection(selection);
				int caretPosition = event.getCaretPosition();
				((SourceEditorTreeViewer) getTreeViewer()).setCaretPosition(caretPosition);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (!fSelection.equals(event.getSelection())) {
			super.selectionChanged(event);
		}
	}

	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		getSite().getActionBars().setGlobalActionHandler(ActionFactory.UNDO.getId(), new ActiveEditorActionHandler(getSite(), ActionFactory.UNDO.getId()));
		getSite().getActionBars().setGlobalActionHandler(ActionFactory.REDO.getId(), new ActiveEditorActionHandler(getSite(), ActionFactory.REDO.getId()));
	}

	/**
	 * @param configuration
	 */
	public void setConfiguration(ContentOutlineConfiguration configuration) {
		// intentionally do not check to see if the new configuration != old
		// configuration
		if (fTreeViewer != null) {
			// remove the key listeners
			if (fTreeViewer.getControl() != null && !fTreeViewer.getControl().isDisposed()) {
				KeyListener[] listeners = getConfiguration().getKeyListeners(fTreeViewer);
				for (int i = 0; i < listeners.length; i++) {
					fTreeViewer.getControl().removeKeyListener(listeners[i]);
				}
			}
			// remove any menu listeners
			if (fContextMenuManager != null) {
				IMenuListener listener = getConfiguration().getMenuListener(fTreeViewer);
				if (listener != null)
					fContextMenuManager.removeMenuListener(listener);
			}
			// clear the selection changed and double click listeners from the
			// configuration
			if (getConfiguration().getSelectionChangedListener(fTreeViewer) != null)
				removeSelectionChangedListener(getConfiguration().getSelectionChangedListener(fTreeViewer));
			if (getConfiguration().getDoubleClickListener(fTreeViewer) != null)
				fTreeViewer.removeDoubleClickListener(getConfiguration().getDoubleClickListener(fTreeViewer));
			IContributionItem[] toolbarItems = getConfiguration().getToolbarContributions(fTreeViewer);
			if (toolbarItems.length > 0 && getSite() != null && getSite().getActionBars() != null && getSite().getActionBars().getToolBarManager() != null) {
				IContributionManager toolbar = getSite().getActionBars().getToolBarManager();
				for (int i = 0; i < toolbarItems.length; i++) {
					toolbar.remove(toolbarItems[i]);
				}
				toolbar.update(false);
			}
			IContributionItem[] menuItems = getConfiguration().getMenuContributions(fTreeViewer);
			if (menuItems.length > 0 && getSite().getActionBars().getMenuManager() != null) {
				IContributionManager menubar = getSite().getActionBars().getMenuManager();
				for (int i = 0; i < menuItems.length; i++) {
					menubar.remove(menuItems[i]);
				}
				menubar.update(false);
			}
			// clear the DnD listeners and transfer types
			if (fDragAdapter != null && !fDragAdapter.isEmpty() && !fDragSource.isDisposed() && fDragSource.getTransfer().length > 0) {
				if (fActiveDragListeners != null) {
					for (int i = 0; i < fActiveDragListeners.length; i++) {
						fDragAdapter.removeDragSourceListener(fActiveDragListeners[i]);
					}
				}
				fActiveDragListeners = null;
				fDragSource.removeDragListener(fDragAdapter);
				fDragSource.setTransfer(new Transfer[0]);
			}
			if (fDropAdapter != null && !fDropAdapter.isEmpty() && !fDropTarget.isDisposed() && fDropTarget.getTransfer().length > 0) {
				if (fActiveDropListeners != null) {
					for (int i = 0; i < fActiveDropListeners.length; i++) {
						fDropAdapter.removeDropTargetListener(fActiveDropListeners[i]);
					}
				}
				fActiveDropListeners = null;
				fDropTarget.removeDropListener(fDropAdapter);
				fDropTarget.setTransfer(new Transfer[0]);
			}
			// release any ties to this tree viewer
			getConfiguration().unconfigure(fTreeViewer);
		}

		fConfiguration = configuration;
		if (fConfiguration == null)
			fConfiguration = NULL_CONFIGURATION;
		fSelection = StructuredSelection.EMPTY;

		if (fTreeViewer != null && fTreeViewer.getControl() != null && !fTreeViewer.getControl().isDisposed()) {
			// add a menu listener if one is provided
			IMenuListener listener = getConfiguration().getMenuListener(fTreeViewer);
			if (listener != null)
				fContextMenuManager.addMenuListener(listener);
			// (re)set the providers
			fTreeViewer.setLabelProvider(getConfiguration().getLabelProvider(fTreeViewer));
			fTreeViewer.setContentProvider(getConfiguration().getContentProvider(fTreeViewer));
			if (getConfiguration().getSelectionChangedListener(fTreeViewer) != null)
				addSelectionChangedListener(getConfiguration().getSelectionChangedListener(fTreeViewer));
			if (getConfiguration().getDoubleClickListener(fTreeViewer) != null)
				fTreeViewer.addDoubleClickListener(getConfiguration().getDoubleClickListener(fTreeViewer));
			IContributionItem[] toolbarItems = getConfiguration().getToolbarContributions(fTreeViewer);
			if (toolbarItems.length > 0 && getSite() != null && getSite().getActionBars() != null && getSite().getActionBars().getToolBarManager() != null) {
				IContributionManager toolbar = getSite().getActionBars().getToolBarManager();
				for (int i = 0; i < toolbarItems.length; i++) {
					toolbar.add(toolbarItems[i]);
				}
				toolbar.update(true);
			}
			IContributionItem[] menuItems = getConfiguration().getMenuContributions(fTreeViewer);
			if (menuItems.length > 0 && getSite().getActionBars().getMenuManager() != null) {
				IContributionManager menu = getSite().getActionBars().getMenuManager();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].setVisible(true);
					menu.add(menuItems[i]);
					menuItems[i].update();
				}
				menu.update(true);
			}
			// add the allowed DnD listeners and types
			TransferDragSourceListener[] dragListeners = fConfiguration.getTransferDragSourceListeners(fTreeViewer);
			if (fDragAdapter != null && dragListeners.length > 0) {
				for (int i = 0; i < dragListeners.length; i++) {
					fDragAdapter.addDragSourceListener(dragListeners[i]);
				}
				fActiveDragListeners = dragListeners;
				fDragSource.addDragListener(fDragAdapter);
				fDragSource.setTransfer(fDragAdapter.getTransfers());
			}
			TransferDropTargetListener[] dropListeners = fConfiguration.getTransferDropTargetListeners(fTreeViewer);
			if (fDropAdapter != null && dropListeners.length > 0) {
				for (int i = 0; i < dropListeners.length; i++) {
					fDropAdapter.addDropTargetListener(dropListeners[i]);
				}
				fActiveDropListeners = dropListeners;
				fDropTarget.addDropListener(fDropAdapter);
				fDropTarget.setTransfer(fDropAdapter.getTransfers());
			}
			// add the key listeners
			KeyListener[] listeners = getConfiguration().getKeyListeners(fTreeViewer);
			for (int i = 0; i < listeners.length; i++) {
				fTreeViewer.getControl().addKeyListener(listeners[i]);
			}
		}
	}

	/**
	 * Sets focus to a part in the page.
	 */
	public void setFocus() {
		getTreeViewer().getControl().setFocus();
	}

	/**
	 * Sets the input of the outline page
	 */
	public void setModel(IStructuredModel newModel) {
		if (newModel != fModel) {
			if (fModel != null)
				fModel.removeModelStateListener(fInternalModelStateListener);
			fModel = newModel;
			if (getTreeViewer() != null && getControl() != null && !getControl().isDisposed()) {
				setConfiguration(getConfiguration());
				fTreeViewer.setInput(fModel);
				update();
			}
			fModel.addModelStateListener(fInternalModelStateListener);
		}
	}

	public void setSelection(ISelection selection) {
		if (getTreeViewer() != null && selection instanceof IStructuredSelection) {
			/**
			 * Selection sent to the Tree widget comes back as a
			 * selectionChanged event. To avoid bouncing an externally set
			 * selection back to our listeners, track the last selection that
			 * originated elsewhere so we can skip sending it back out. If
			 * selection came from the Tree widget (by user interaction), it
			 * will be different.
			 */
			if (!fSelection.equals(selection)) {
				if (selection == null || ((IStructuredSelection) selection).getFirstElement() == null) {
					fSelection = StructuredSelection.EMPTY;
				}
				else {
					fSelection = selection;
				}
				getTreeViewer().setSelection(fSelection, getConfiguration().isLinkedWithEditor(getTreeViewer()));
			}
		}
	}

	public void setViewerSelectionManager(ViewerSelectionManager viewerSelectionManager) {
		// disconnect from old one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.removeNodeSelectionListener(this);
			fViewerSelectionManager.removeTextSelectionListener(this);
		}
		fViewerSelectionManager = viewerSelectionManager;
		// connect to new one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.addNodeSelectionListener(this);
			fViewerSelectionManager.addTextSelectionListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public void textSelectionChanged(TextSelectionChangedEvent event) {
		if (getConfiguration().isLinkedWithEditor(getTreeViewer())) {
			int caretPosition = event.getTextSelectionStart();
			((SourceEditorTreeViewer) getTreeViewer()).setCaretPosition(caretPosition);
		}
	}

	/**
	 * redraws the tree
	 */
	public void update() {
		if (getTreeViewer() != null) {
			Control control = getTreeViewer().getControl();
			control.setRedraw(false);
			getTreeViewer().refresh();
			control.setRedraw(true);
		}
	}
}
