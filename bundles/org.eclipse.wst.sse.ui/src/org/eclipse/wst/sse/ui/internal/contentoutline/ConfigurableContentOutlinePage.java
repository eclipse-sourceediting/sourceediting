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
package org.eclipse.wst.sse.ui.internal.contentoutline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension4;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration;


public class ConfigurableContentOutlinePage extends ContentOutlinePage implements IAdaptable {
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

	/**
	 * Provides double-click registration so it can be done before the Control
	 * is created.
	 */
	private class DoubleClickProvider implements IDoubleClickListener {
		private IDoubleClickListener[] listeners = null;

		void addDoubleClickListener(IDoubleClickListener newListener) {
			if (listeners == null) {
				listeners = new IDoubleClickListener[]{newListener};
			}
			else {
				IDoubleClickListener[] newListeners = new IDoubleClickListener[listeners.length + 1];
				System.arraycopy(listeners, 0, newListeners, 0, listeners.length);
				newListeners[listeners.length] = newListener;
				listeners = newListeners;
			}
		}

		public void doubleClick(DoubleClickEvent event) {
			fireDoubleClickEvent(event);
		}

		private void fireDoubleClickEvent(final DoubleClickEvent event) {
			IDoubleClickListener[] firingListeners = listeners;
			for (int i = 0; i < firingListeners.length; ++i) {
				final IDoubleClickListener l = firingListeners[i];
				Platform.run(new SafeRunnable() {
					public void run() {
						l.doubleClick(event);
					}
				});
			}
		}

		void removeDoubleClickListener(IDoubleClickListener oldListener) {
			if (listeners != null) {
				if (listeners.length == 1 && listeners[0].equals(oldListener)) {
					listeners = null;
				}
				else {
					List newListeners = new ArrayList(Arrays.asList(listeners));
					newListeners.remove(oldListener);
					listeners = (IDoubleClickListener[]) newListeners.toArray(new IDoubleClickListener[listeners.length - 1]);
				}
			}
		}
	}

	/**
	 * Listens to post selection from the selection service, applying it to
	 * the tree viewer.
	 */
	private class PostSelectionServiceListener implements ISelectionListener {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			// from selection service
			_DEBUG_TIME = System.currentTimeMillis();
			if (getControl() != null && !getControl().isDisposed() && !getControl().isFocusControl() && !fSelectionProvider.isFiringSelection()) {
				/*
				 * Do not allow selection from other parts to affect selection
				 * in the tree widget if it has focus. Selection events
				 * "bouncing" off of other parts are all that we can receive
				 * if we have focus (since we forward selection to the
				 * service), and only the user should affect selection if we
				 * have focus.
				 */
				ISelection validContentSelection = getConfiguration().getSelection(getTreeViewer(), selection);
				getTreeViewer().refresh(true);
				boolean isLinked = getConfiguration().isLinkedWithEditor(getTreeViewer());
				if (isLinked) {
					getTreeViewer().setSelection(validContentSelection, true);
				}
			}
			if (_DEBUG) {
				System.out.println("(O:" + (System.currentTimeMillis() - _DEBUG_TIME) + "ms) " + part + " : " + selection);
			}
		}
	}

	/**
	 * Forwards post-selection from the tree viewer to the listeners while
	 * acting as this page's selection provider.
	 */
	private class SelectionProvider implements IPostSelectionProvider {
		private class PostSelectionChangedListener implements ISelectionChangedListener {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!isFiringSelection()) {
					fireSelectionChanged(event, postListeners);
				}
			}
		}

		private class SelectionChangedListener implements ISelectionChangedListener {
			public void selectionChanged(SelectionChangedEvent event) {
				if (!isFiringSelection()) {
					fireSelectionChanged(event, listeners);
				}
			}
		}

		private boolean isFiringSelection = false;
		private ListenerList listeners = new ListenerList();
		private ListenerList postListeners = new ListenerList();
		private ISelectionChangedListener postSelectionChangedListener = new PostSelectionChangedListener();
		private ISelectionChangedListener selectionChangedListener = new SelectionChangedListener();

		public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
			postListeners.add(listener);
		}

		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		public void fireSelectionChanged(final SelectionChangedEvent event, ListenerList listenerList) {
			isFiringSelection = true;
			Object[] listeners = listenerList.getListeners();
			for (int i = 0; i < listeners.length; ++i) {
				final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
				Platform.run(new SafeRunnable() {
					public void run() {
						l.selectionChanged(event);
					}
				});
			}
			isFiringSelection = false;
		}

		public ISelectionChangedListener getPostSelectionChangedListener() {
			return postSelectionChangedListener;
		}

		public ISelection getSelection() {
			if (getTreeViewer() != null) {
				return getTreeViewer().getSelection();
			}
			return StructuredSelection.EMPTY;
		}

		public ISelectionChangedListener getSelectionChangedListener() {
			return selectionChangedListener;
		}

		public boolean isFiringSelection() {
			return isFiringSelection;
		}

		public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
			postListeners.remove(listener);
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			listeners.remove(listener);
		};

		public void setSelection(ISelection selection) {
			if (isFiringSelection) {
				getTreeViewer().setSelection(selection);
			}
		};
	}

	private class ShowInTarget implements IShowInTarget {
		/*
		 * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
		 */
		public boolean show(ShowInContext context) {
			setSelection(context.getSelection());
			return getTreeViewer().getSelection().equals(context.getSelection());
		}
	}

	protected static ContentOutlineConfiguration NULL_CONFIGURATION = new ContentOutlineConfiguration() {

		public IContentProvider getContentProvider(TreeViewer viewer) {
			return new ITreeContentProvider() {
				public void dispose() {
				}

				public Object[] getChildren(Object parentElement) {
					return null;
				}

				public Object[] getElements(Object inputElement) {
					return null;
				}

				public Object getParent(Object element) {
					return null;
				}

				public boolean hasChildren(Object element) {
					return false;
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
			};
		}
	};

	private static final String OUTLINE_CONTEXT_MENU_ID = "org.eclipse.wst.sse.ui.StructuredTextEditor.OutlineContext"; //$NON-NLS-1$

	private static final String OUTLINE_CONTEXT_MENU_POSTFIX = ".source.OutlineContext"; //$NON-NLS-1$
	private final boolean _DEBUG = "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.ui/contentOutline")); //$NON-NLS-1$  //$NON-NLS-2$;

	private long _DEBUG_TIME = 0;

	private TransferDragSourceListener[] fActiveDragListeners;
	private TransferDropTargetListener[] fActiveDropListeners;
	private ContentOutlineConfiguration fConfiguration;

	private Menu fContextMenu;
	private String fContextMenuId;

	private MenuManager fContextMenuManager;
	private DoubleClickProvider fDoubleClickProvider = null;

	private DelegatingDragAdapter fDragAdapter;
	private DragSource fDragSource;
	private DelegatingDropAdapter fDropAdapter;
	private DropTarget fDropTarget;
	private IMenuListener fGroupAdder = null;
	private Object fInput = null;
	private IEditorPart fOwnerEditor;

	private ISelectionListener fSelectionListener = null;
	SelectionProvider fSelectionProvider = null;

	public ConfigurableContentOutlinePage() {
		super();
		fGroupAdder = new AdditionGroupAdder();
		fSelectionProvider = new SelectionProvider();
	}

	/**
	 * Adds a listener to a list of those notified when someone double-clicks
	 * in the page.
	 * 
	 * @param newListener -
	 *            the listener to add
	 */
	public void addDoubleClickListener(IDoubleClickListener newListener) {
		if (fDoubleClickProvider == null) {
			fDoubleClickProvider = new DoubleClickProvider();
		}
		fDoubleClickProvider.addDoubleClickListener(newListener);
	}

	private String computeContextMenuID() {
		String id = null;
		ITextEditor editor = null;
		if (fOwnerEditor instanceof ITextEditor) {
			editor = (ITextEditor) fOwnerEditor;
		}
		else {
			editor = (ITextEditor) fOwnerEditor.getAdapter(ITextEditor.class);
		}
		if (editor != null) {
			IDocumentProvider provider = editor.getDocumentProvider();
			if (provider instanceof IDocumentProviderExtension4) {
				IContentType type;
				try {
					type = ((IDocumentProviderExtension4) provider).getContentType(fOwnerEditor.getEditorInput());
					if (type != null) {
						id = type.getId();
					}
				}
				catch (CoreException e) {
					// do nothing, we'll use a generic ID instead
				}
			}
		}
		return id;
	}

	/**
	 * @see ContentOutlinePage#createControl
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);

		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		if (page != null) {
			fOwnerEditor = page.getActiveEditor();
		}

		// create the context menu
		fContextMenuManager = new MenuManager("#popup"); //$NON-NLS-1$
		fContextMenuManager.setRemoveAllWhenShown(true);
		Menu menu = fContextMenuManager.createContextMenu(getControl());
		getControl().setMenu(menu);

		fDragAdapter = new DelegatingDragAdapter();
		fDragSource = new DragSource(getControl(), DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
		fDropAdapter = new DelegatingDropAdapter();
		fDropTarget = new DropTarget(getControl(), DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);

		setConfiguration(getConfiguration());

		/*
		 * ContentOutlinePage only implements ISelectionProvider while the
		 * tree viewer implements both ISelectionProvider and
		 * IPostSelectionProvider. Use an ISelectionProvider that listens to
		 * post selection from the tree viewer and forward only post selection
		 * to the selection service.
		 */
		getTreeViewer().addPostSelectionChangedListener(fSelectionProvider.getPostSelectionChangedListener());
		getTreeViewer().addSelectionChangedListener(fSelectionProvider.getSelectionChangedListener());
		if (fDoubleClickProvider == null) {
			fDoubleClickProvider = new DoubleClickProvider();
		}
		getTreeViewer().addDoubleClickListener(fDoubleClickProvider);
		getSite().setSelectionProvider(fSelectionProvider);
	}

	public void dispose() {
		super.dispose();
		getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(getSelectionListener());
		if (fDoubleClickProvider != null) {
			getTreeViewer().removeDoubleClickListener(fDoubleClickProvider);
		}

		// dispose menu controls
		if (fContextMenu != null) {
			fContextMenu.dispose();
		}
		if (fContextMenuManager != null) {
			IMenuListener listener = getConfiguration().getMenuListener(getTreeViewer());
			if (listener != null)
				fContextMenuManager.removeMenuListener(listener);
			fContextMenuManager.removeMenuListener(fGroupAdder);
			fContextMenuManager.removeAll();
			fContextMenuManager.dispose();
		}
		setConfiguration(NULL_CONFIGURATION);
	}

	IEditorPart getActiveEditorPart() {
		return getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		Object adapter = null;
		if (key.equals(IShowInTarget.class)) {
			adapter = new ShowInTarget();
		}
		final IEditorPart editor = getActiveEditorPart();

		if (key.equals(IShowInSource.class) && editor != null) {
			adapter = new IShowInSource() {
				public ShowInContext getShowInContext() {
					return new ShowInContext(editor.getEditorInput(), editor.getEditorSite().getSelectionProvider().getSelection());
				}
			};
		}
		else if (key.equals(IShowInTargetList.class) && editor != null) {
			adapter = editor.getAdapter(key);
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

	public ISelection getSelection() {
		return fSelectionProvider.getSelection();
	}

	private ISelectionListener getSelectionListener() {
		if (fSelectionListener == null) {
			fSelectionListener = new PostSelectionServiceListener();
		}
		return fSelectionListener;
	}


	public void init(IPageSite pageSite) {
		super.init(pageSite);
		pageSite.getWorkbenchWindow().getSelectionService().addPostSelectionListener(getSelectionListener());
	}

	/**
	 * Removes a listener to a list of those notified when someone
	 * double-clicks in the page.
	 * 
	 * @param oldListener -
	 *            the listener to remove
	 */
	public void removeDoubleClickListener(IDoubleClickListener oldListener) {
		if (fDoubleClickProvider != null) {
			fDoubleClickProvider.removeDoubleClickListener(oldListener);
		}
	}

	/**
	 * @param configuration
	 */
	public void setConfiguration(ContentOutlineConfiguration configuration) {
		// intentionally do not check to see if the new configuration != old
		// configuration
		if (getTreeViewer() != null) {
			// remove the key listeners
			if (getControl() != null && !getControl().isDisposed()) {
				KeyListener[] listeners = getConfiguration().getKeyListeners(getTreeViewer());
				if (listeners != null) {
					for (int i = 0; i < listeners.length; i++) {
						getControl().removeKeyListener(listeners[i]);
					}
				}
			}
			// remove any menu listeners
			if (fContextMenuManager != null) {
				IMenuListener listener = getConfiguration().getMenuListener(getTreeViewer());
				if (listener != null)
					fContextMenuManager.removeMenuListener(listener);
				fContextMenuManager.removeMenuListener(fGroupAdder);
			}
			// clear the selection changed and double click listeners from the
			// configuration
			if (getConfiguration().getSelectionChangedListener(getTreeViewer()) != null)
				removeSelectionChangedListener(getConfiguration().getSelectionChangedListener(getTreeViewer()));
			if (getConfiguration().getPostSelectionChangedListener(getTreeViewer()) != null)
				getTreeViewer().removePostSelectionChangedListener(getConfiguration().getPostSelectionChangedListener(getTreeViewer()));

			IContributionManager toolbar = getSite().getActionBars().getToolBarManager();
			if (toolbar != null) {
				IContributionItem[] toolbarItems = getConfiguration().getToolbarContributions(getTreeViewer());
				if (toolbarItems != null && toolbarItems.length > 0) {
					for (int i = 0; i < toolbarItems.length; i++) {
						toolbar.remove(toolbarItems[i]);
					}
					toolbar.update(false);
				}
			}

			IContributionManager menubar = getSite().getActionBars().getMenuManager();
			if (menubar != null) {
				IContributionItem[] menuItems = getConfiguration().getMenuContributions(getTreeViewer());
				if (menuItems != null && menuItems.length > 0) {
					for (int i = 0; i < menuItems.length; i++) {
						menubar.remove(menuItems[i]);
					}
					menubar.remove(IWorkbenchActionConstants.MB_ADDITIONS);
					menubar.update(false);
				}
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
			getConfiguration().getContentProvider(getTreeViewer()).inputChanged(getTreeViewer(), fInput, null);
			// release any ties to this tree viewer
			getConfiguration().unconfigure(getTreeViewer());
		}

		fConfiguration = configuration;
		if (fConfiguration == null) {
			fConfiguration = NULL_CONFIGURATION;
		}

		if (getTreeViewer() != null && getControl() != null && !getControl().isDisposed()) {
			// add a menu listener if one is provided
			if (fContextMenuManager != null) {
				IMenuListener listener = getConfiguration().getMenuListener(getTreeViewer());
				if (listener != null)
					fContextMenuManager.addMenuListener(listener);
				fContextMenuManager.addMenuListener(fGroupAdder);
			}
			// (re)set the providers
			getTreeViewer().setLabelProvider(getConfiguration().getLabelProvider(getTreeViewer()));
			getTreeViewer().setContentProvider(getConfiguration().getContentProvider(getTreeViewer()));
			if (getConfiguration().getSelectionChangedListener(getTreeViewer()) != null)
				addSelectionChangedListener(getConfiguration().getSelectionChangedListener(getTreeViewer()));
			if (getConfiguration().getPostSelectionChangedListener(getTreeViewer()) != null)
				getTreeViewer().addPostSelectionChangedListener(getConfiguration().getPostSelectionChangedListener(getTreeViewer()));

			// view toolbar
			IContributionManager toolbar = getSite().getActionBars().getToolBarManager();
			if (toolbar != null) {
				IContributionItem[] toolbarItems = getConfiguration().getToolbarContributions(getTreeViewer());
				if (toolbarItems != null) {
					for (int i = 0; i < toolbarItems.length; i++) {
						toolbar.add(toolbarItems[i]);
					}
					toolbar.update(true);
				}
			}
			// view menu
			IContributionManager menu = getSite().getActionBars().getMenuManager();
			if (menu != null) {
				IContributionItem[] menuItems = getConfiguration().getMenuContributions(getTreeViewer());
				if (menuItems != null) {
					for (int i = 0; i < menuItems.length; i++) {
						menuItems[i].setVisible(true);
						menu.add(menuItems[i]);
						menuItems[i].update();
					}
					menu.update(true);
				}
			}
			// add the allowed DnD listeners and types
			TransferDragSourceListener[] dragListeners = fConfiguration.getTransferDragSourceListeners(getTreeViewer());
			if (fDragAdapter != null && dragListeners.length > 0) {
				for (int i = 0; i < dragListeners.length; i++) {
					fDragAdapter.addDragSourceListener(dragListeners[i]);
				}
				fActiveDragListeners = dragListeners;
				fDragSource.addDragListener(fDragAdapter);
				fDragSource.setTransfer(fDragAdapter.getTransfers());
			}
			TransferDropTargetListener[] dropListeners = fConfiguration.getTransferDropTargetListeners(getTreeViewer());
			if (fDropAdapter != null && dropListeners.length > 0) {
				for (int i = 0; i < dropListeners.length; i++) {
					fDropAdapter.addDropTargetListener(dropListeners[i]);
				}
				fActiveDropListeners = dropListeners;
				fDropTarget.addDropListener(fDropAdapter);
				fDropTarget.setTransfer(fDropAdapter.getTransfers());
			}
			// add the key listeners
			KeyListener[] listeners = getConfiguration().getKeyListeners(getTreeViewer());
			if (listeners != null) {
				for (int i = 0; i < listeners.length; i++) {
					getControl().addKeyListener(listeners[i]);
				}
			}
		}

		if (fInput != null) {
			setInput(fInput);
		}
	}

	public void setInput(Object newInput) {
		fInput = newInput;
		/*
		 * Intentionally not optimized for checking new input vs. old input so
		 * that any existing content providers can be updated
		 */
		if (getControl() != null && !getControl().isDisposed()) {
			getTreeViewer().setInput(fInput);
			updateContextMenuId();
		}
	}

	/**
	 * Updates the outline page's context menu for the current input
	 */
	private void updateContextMenuId() {
		String contextMenuId = null;
		// update outline view's context menu control and ID

		if (fOwnerEditor == null) {
			IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
			if (page != null) {
				fOwnerEditor = page.getActiveEditor();
			}
		}

		contextMenuId = computeContextMenuID();

		if (contextMenuId == null) {
			contextMenuId = OUTLINE_CONTEXT_MENU_ID;
		}

		/*
		 * Update outline context menu id if updating to a new id or if
		 * context menu is not already set up
		 */
		if (!contextMenuId.equals(fContextMenuId) || (fContextMenu == null)) {
			fContextMenuId = contextMenuId;

			if (getControl() != null && !getControl().isDisposed()) {
				// dispose of previous context menu
				if (fContextMenu != null) {
					fContextMenu.dispose();
				}
				if (fContextMenuManager != null) {
					IMenuListener listener = getConfiguration().getMenuListener(getTreeViewer());
					if (listener != null)
						fContextMenuManager.removeMenuListener(listener);
					fContextMenuManager.removeMenuListener(fGroupAdder);
					fContextMenuManager.removeAll();
					fContextMenuManager.dispose();
				}

				fContextMenuManager = new MenuManager(fContextMenuId, fContextMenuId);
				fContextMenuManager.setRemoveAllWhenShown(true);
				IMenuListener listener = getConfiguration().getMenuListener(getTreeViewer());
				if (listener != null)
					fContextMenuManager.addMenuListener(listener);
				fContextMenuManager.addMenuListener(fGroupAdder);

				fContextMenu = fContextMenuManager.createContextMenu(getControl());
				getControl().setMenu(fContextMenu);

				getSite().registerContextMenu(fContextMenuId, fContextMenuManager, this);

				/*
				 * also register this menu for source page part and structured
				 * text outline view ids
				 */
				if (fOwnerEditor != null) {
					String partId = fOwnerEditor.getSite().getId();
					if (partId != null) {
						getSite().registerContextMenu(partId + OUTLINE_CONTEXT_MENU_POSTFIX, fContextMenuManager, this);
					}
				}
				getSite().registerContextMenu(OUTLINE_CONTEXT_MENU_ID, fContextMenuManager, this);
			}
		}
	}

}
