/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.ui.internal.tabletree;

import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.ViewerSelectionManager;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.xml.core.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.modelquery.ModelQueryUtil;
import org.eclipse.wst.xml.internal.ui.IDesignViewer;
import org.eclipse.wst.xml.internal.ui.XMLEditorResourceHandler;
import org.eclipse.wst.xml.ui.actions.NodeAction;
import org.eclipse.wst.xml.ui.dnd.XMLDragAndDropManager;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.w3c.dom.Document;

public class XMLTableTreeViewer extends TreeViewer implements IDesignViewer {

	/**
	 * This class is used to improve source editing performance by coalescing
	 * multiple notifications for an element change into a single refresh
	 */
	class DelayedRefreshTimer implements Runnable {
		private final int delta = 2000;
		protected Object objectPendingRefresh;
		protected ISelection pendingSelection;
		protected Object prevObject;
		protected XMLTableTreeViewer viewer;

		public DelayedRefreshTimer(XMLTableTreeViewer treeViewer) {
			this.viewer = treeViewer;
		}

		public boolean isRefreshPending() {
			return objectPendingRefresh != null;
		}

		public void refresh(Object object) {
			if (prevObject == object) {
				objectPendingRefresh = object;
				getDisplay().timerExec(delta, this);
			} else {
				if (objectPendingRefresh != null) {
					viewer.doRefresh(objectPendingRefresh, false);
					objectPendingRefresh = null;
				}
				viewer.doRefresh(object, false);
			}
			prevObject = object;
		}

		private Display getDisplay() {

			return PlatformUI.getWorkbench().getDisplay();
		}

		public void run() {
			// defect 239677 ensure that the viewer's control is not disposed
			//
			if (objectPendingRefresh != null && !viewer.getTree().isDisposed()) {
				viewer.doRefresh(objectPendingRefresh, true);
				if (pendingSelection != null) {
					// see fireSelectionChanged comment about jumping cursor
					// problem
					//
					viewer.setSelection(pendingSelection, true);
					pendingSelection = null;
				}
				objectPendingRefresh = null;
				prevObject = null;
			}
		}

		public void setSelection(ISelection selection) {
			pendingSelection = selection;
		}
	}

	class DelayingNodeSelectionListener implements INodeSelectionListener {
		public void nodeSelectionChanged(NodeSelectionChangedEvent event) {
			//			if (isNodeSelectionListenerEnabled &&
			// !event.getSource().equals(this)) {
			if (!event.getSource().equals(XMLTableTreeViewer.this)) {
				List selectedNodes = event.getSelectedNodes();
				ISelection selection = new StructuredSelection(selectedNodes);

				// for performance purposes avoid large multi-selections
				// 
				if (selectedNodes.size() < 100) {
					if (timer.isRefreshPending()) {
						timer.setSelection(selection);
					} else {
						setSelection(selection, true);
					}
				}
			}
		}
	}

	class InternalModelStateListener implements IModelStateListener {

		public void modelAboutToBeChanged(IStructuredModel model) {
			ignoreRefresh = true;
		}

		public void modelChanged(IStructuredModel model) {
			ignoreRefresh = false;
			refresh();
		}

		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
		}

		public void modelResourceDeleted(IStructuredModel model) {
		}

		public void modelResourceMoved(IStructuredModel originalmodel, IStructuredModel movedmodel) {
		}

		public void modelAboutToBeReinitialized(IStructuredModel structuredModel) {
		}

		public void modelReinitialized(IStructuredModel structuredModel) {
		}
	}

	class NodeActionMenuListener implements IMenuListener {
		public void menuAboutToShow(IMenuManager menuManager) {
			// used to disable NodeSelection listening while running
			// NodeAction
			XMLNodeActionManager nodeActionManager = new XMLNodeActionManager(fModel, XMLTableTreeViewer.this) {
				public void beginNodeAction(NodeAction action) {
					super.beginNodeAction(action);
				}

				public void endNodeAction(NodeAction action) {
					super.endNodeAction(action);
				}
			};
			nodeActionManager.fillContextMenu(menuManager, getSelection());
		}
	}

	protected CellEditor cellEditor;

	int count = 0;

	protected IModelStateListener fInternalModelStateListener = new InternalModelStateListener();
	protected IStructuredModel fModel = null;
	protected INodeSelectionListener fNodeSelectionListener;

	protected ViewerSelectionManager fViewerSelectionManager;

	protected boolean ignoreRefresh;

	protected DelayedRefreshTimer timer;
	protected XMLTreeExtension treeExtension;

	public XMLTableTreeViewer(Composite parent) {
		super(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		// set up providers
		this.treeExtension = new XMLTreeExtension(getTree());

		XMLTableTreeContentProvider provider = new XMLTableTreeContentProvider();
		provider.addViewer(this);
		setContentProvider(provider);
		setLabelProvider(provider);

		createContextMenu();

		XMLDragAndDropManager.addDragAndDropSupport(this);
		timer = new DelayedRefreshTimer(this);
	}

	/**
	 * This creates a context menu for the viewer and adds a listener as well
	 * registering the menu for extension.
	 */
	protected void createContextMenu() {
		MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
		contextMenu.add(new Separator("additions")); //$NON-NLS-1$
		contextMenu.setRemoveAllWhenShown(true);
		contextMenu.addMenuListener(new NodeActionMenuListener());
		Menu menu = contextMenu.createContextMenu(getControl());
		getControl().setMenu(menu);
	}

	protected void doRefresh(Object o, boolean fromDelayed) {
		treeExtension.resetCachedData();
		super.refresh(o);
	}

	protected void fireSelectionChanged(SelectionChangedEvent event) {
		if (!getTree().isDisposed() && !getTree().isFocusControl()) {
			// defect 246094
			// Various jumping cursor problems are caused when a selection
			// 'delayed' selection occurs.
			// These delayed selections are caused two ways:
			//
			//  - when DelayedRefreshTimer calls doRefresh() ... the
			// 'preserveSelection' causes selection to occur
			//  - when DelayedRefreshTimer performs a 'pending' selection
			// 
			// Since we only want to update the selectionManager on an explict
			// user action
			// (and not some selection that is merely a resonse to the
			// selection manager)
			// we ensure that the tree has focus control before firing events
			// to the selectionManager.
			// 
			removeSelectionChangedListener(fViewerSelectionManager);
			super.fireSelectionChanged(event);
			addSelectionChangedListener(fViewerSelectionManager);
		} else {
			super.fireSelectionChanged(event);
		}
	}


	public INodeSelectionListener getNodeSelectionListener() {
		if (fNodeSelectionListener == null)
			fNodeSelectionListener = new DelayingNodeSelectionListener();
		return fNodeSelectionListener;
	}

	public String getTitle() {
		return XMLEditorResourceHandler.getResourceString("%XMLTableTreeViewer.0"); //$NON-NLS-1$
	}

	protected void handleDispose(DisposeEvent event) {
		super.handleDispose(event);
		treeExtension.dispose();
		setModel(null);
		setViewerSelectionManager(null);

		//		if (fViewerSelectionManager != null) {
		//			fViewerSelectionManager.removeNodeSelectionListener(getNodeSelectionListener());
		//		}
		//
		//		fOverlayIconManager.setResource(null);
		//		super.handleDispose(event);
		//
		//		if (fModel != null)
		//			fModel.removeModelStateListener(fInternalModelStateListener);
	}

	public void refresh() {
		if (!ignoreRefresh && !getControl().isDisposed()) {
			treeExtension.resetCachedData();
			super.refresh();

			//			if (B2BHacks.IS_UNIX) {
			// this is required to fix defect 193792
			// this fixes the problem where the 'paintHandler'drawn portions
			// of tree weren't repainted properly
			//
			getTree().redraw(0, 0, getTree().getBounds().width, getTree().getBounds().height, false);
			getTree().update();
			//			}
		}
	}

	public void refresh(Object o) {
		if (!ignoreRefresh && !getControl().isDisposed() && timer != null) {
			if (getTree().isVisible()) {
				doRefresh(o, false);
			} else {
				timer.refresh(o);
			}
		}
	}

	public void setModel(IStructuredModel model) {
		// remove
		if (fModel != null) {
			fModel.removeModelStateListener(fInternalModelStateListener);
		}

		fModel = model;
		Document domDoc = null;

		if (fModel != null && fModel instanceof IDOMModel) {
			model.addModelStateListener(fInternalModelStateListener);
			ModelQuery mq = ModelQueryUtil.getModelQuery(model);
			treeExtension.setModelQuery(mq);
			domDoc = ((IDOMModel) fModel).getDocument();
			setInput(domDoc);
		}
	}

	// the following methods implement the IDesignViewer interface
	// - getControl() is implemented via AdvancedTableTreeViewer
	//
	public void setViewerSelectionManager(ViewerSelectionManager viewerSelectionManager) {
		// disconnect from old one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.removeNodeSelectionListener(getNodeSelectionListener());
			removeSelectionChangedListener(fViewerSelectionManager);
		}

		fViewerSelectionManager = viewerSelectionManager;

		// connect to new one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.addNodeSelectionListener(getNodeSelectionListener());
			addSelectionChangedListener(fViewerSelectionManager);
		}
	}

}