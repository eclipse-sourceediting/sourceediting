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
package org.eclipse.wst.sse.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.wst.sse.core.FactoryRegistry;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.ViewerSelectionManager;
import org.eclipse.wst.sse.ui.edit.util.ActiveEditorActionHandler;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.ITextSelectionListener;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.sse.ui.view.events.TextSelectionChangedEvent;


public class StructuredTextEditorContentOutlinePage extends ContentOutlinePage implements INodeSelectionListener, ITextSelectionListener, IUpdate, IAdaptable {
	/**
	 * @deprecated
	 */
	// Disables Tree redraw during large model changes
	protected class ControlRedrawEnabler implements IModelStateListener {
		public void modelAboutToBeChanged(IStructuredModel model) {
			setControlRedraw(false);
		}

		public void modelChanged(IStructuredModel model) {
			setControlRedraw(true);
		}

		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
		}

		public void modelResourceDeleted(IStructuredModel model) {
		}

		public void modelResourceMoved(IStructuredModel originalmodel, IStructuredModel movedmodel) {
		}

		private void setControlRedraw(boolean doRedraw) {
			// check if we're on a Display Thread
			if (Display.getCurrent() != null) {
				setRedraw(doRedraw);
			}
			else {
				final boolean redrawOrNot = doRedraw;
				Runnable modifyRedraw = new Runnable() {
					public void run() {
						setRedraw(redrawOrNot);
					}
				};
				/*
				 * This may not result in the enablement change happening
				 * "soon enough", but better to do it later than to cause a
				 * deadlock
				 */
				Display.getDefault().asyncExec(modifyRedraw);
			}
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
		}

		public void modelReinitialized(IStructuredModel structuredModel) {
		}
	}

	class ShowInTarget implements IShowInTarget {
		/*
		 * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
		 */
		public boolean show(ShowInContext context) {
			if (getViewerSelectionManager() == null) {
				return false;
			}
			boolean shown = false;
			List selectedNodes = getViewerSelectionManager().getSelectedNodes();
			if (selectedNodes == null) {
				selectedNodes = new ArrayList(0);
			}
			ISelection selection = new StructuredSelection(getConfiguration().getNodes(selectedNodes));
			if (!selection.isEmpty()) {
				setSelection(selection, true);
				shown = selection.equals(fSelection);
			}
			return shown;
		}
	}

	/*
	 * Menu listener to create the additions group; required since the context
	 * menu is cleared every time it is shown
	 */
	class AdditionGroupAdder implements IMenuListener {
		public void menuAboutToShow(IMenuManager manager) {
			IContributionItem[] items = manager.getItems();
			if (items.length > 0 && items[items.length - 1].getId() != null) {
				manager.insertAfter(items[items.length - 1].getId(), new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			}
			else {
				manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		}
	}

	protected static ContentOutlineConfiguration NULL_CONFIGURATION = new ContentOutlineConfiguration();
	private TransferDragSourceListener[] fActiveDragListeners;
	private TransferDropTargetListener[] fActiveDropListeners;
	private ContentOutlineConfiguration fConfiguration;

	private MenuManager fContextMenuManager;
	private boolean fContextMenuRegistered = false;
	private DelegatingDragAdapter fDragAdapter;
	private DragSource fDragSource;
	private DelegatingDropAdapter fDropAdapter;
	private DropTarget fDropTarget;
	protected IStructuredModel fModel;
	// Current selection, maintained so selection doesn't bounce back from the
	// Tree
	ISelection fSelection;
	protected SourceEditorTreeViewer fTreeViewer;
	protected ViewerSelectionManager fViewerSelectionManager;
	private IMenuListener fGroupAdder = null;

	public StructuredTextEditorContentOutlinePage() {
		super();
		fSelection = StructuredSelection.EMPTY;
		fGroupAdder = new AdditionGroupAdder();
	}

	/**
	 * @see ContentOutlinePage#createControl
	 */
	public void createControl(Composite parent) {
		fTreeViewer = new SourceEditorTreeViewer(new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		Object adapter = getConfiguration().getAdapter(key);
		if (adapter == null) {
			if (key.equals(IShowInTarget.class)) {
				adapter = new ShowInTarget();
			}
		}
		return adapter;
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
	 * @return com.ibm.sed.treemodel.IStructuredModel
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
		FactoryRegistry factoryRegistry = getModel().getFactoryRegistry();
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

	void registerContextMenu() {
		if (!fContextMenuRegistered && getTreeViewer() != null && getTreeViewer().getControl() != null) {
			IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
			if (page != null) {
				IEditorPart ownerEditor = page.getActiveEditor();
				if (ownerEditor != null) {
					fContextMenuRegistered = true;
					getSite().registerContextMenu(ownerEditor.getEditorSite().getId() + "#outlinecontext", fContextMenuManager, this);
				}
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
				fContextMenuManager.removeMenuListener(fGroupAdder);
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
				menubar.remove(IWorkbenchActionConstants.MB_ADDITIONS);
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
			fContextMenuManager.addMenuListener(fGroupAdder);
			// (re)set the providers
			fTreeViewer.setLabelProvider(getConfiguration().getLabelProvider(fTreeViewer));
			fTreeViewer.setContentProvider(getConfiguration().getContentProvider(fTreeViewer));
			if (getConfiguration().getSelectionChangedListener(fTreeViewer) != null)
				addSelectionChangedListener(getConfiguration().getSelectionChangedListener(fTreeViewer));
			if (getConfiguration().getDoubleClickListener(fTreeViewer) != null)
				fTreeViewer.addDoubleClickListener(getConfiguration().getDoubleClickListener(fTreeViewer));

			// view toolbar
			IContributionItem[] toolbarItems = getConfiguration().getToolbarContributions(fTreeViewer);
			if (toolbarItems.length > 0 && getSite() != null && getSite().getActionBars() != null && getSite().getActionBars().getToolBarManager() != null) {
				IContributionManager toolbar = getSite().getActionBars().getToolBarManager();
				for (int i = 0; i < toolbarItems.length; i++) {
					toolbar.add(toolbarItems[i]);
				}
				toolbar.update(true);
			}
			// view menu
			IContributionManager menu = getSite().getActionBars().getMenuManager();
			if (menu != null) {
				IContributionItem[] menuItems = getConfiguration().getMenuContributions(fTreeViewer);
				if (menuItems.length > 0) {
					for (int i = 0; i < menuItems.length; i++) {
						menuItems[i].setVisible(true);
						menu.add(menuItems[i]);
						menuItems[i].update();
					}
					menu.update(true);
				}
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
		registerContextMenu();
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
			// if (fModel != null) {
			// fModel.removeModelStateListener(fInternalModelStateListener);
			// }
			IJFaceNodeAdapterFactory adapterFactory = getViewerRefreshFactory();
			if (adapterFactory != null) {
				adapterFactory.removeListener(fTreeViewer);
			}
			fModel = newModel;
			if (getTreeViewer() != null && getControl() != null && !getControl().isDisposed()) {
				setConfiguration(getConfiguration());
				fTreeViewer.setInput(fModel);
				update();
			}
			// fModel.addModelStateListener(fInternalModelStateListener);
			adapterFactory = getViewerRefreshFactory();
			if (adapterFactory != null) {
				adapterFactory.addListener(fTreeViewer);
			}
		}
	}

	void setRedraw(boolean doRedraw) {
		Control control = getControl();
		if ((control != null) && (!control.isDisposed())) {
			control.setRedraw(doRedraw);
		}
	}

	public void setSelection(ISelection selection) {
		setSelection(selection, getConfiguration().isLinkedWithEditor(getTreeViewer()));
	}

	protected void setSelection(ISelection selection, boolean reveal) {
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
				getTreeViewer().setSelection(fSelection, reveal);
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
	 * @see org.eclipse.wst.sse.ui.view.events.ITextSelectionListener#textSelectionChanged(org.eclipse.wst.sse.ui.view.events.TextSelectionChangedEvent)
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
