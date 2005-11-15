/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.ui.internal.tabletree;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.actions.NodeAction;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.eclipse.wst.xml.ui.internal.dnd.XMLDragAndDropManager;
import org.w3c.dom.Document;

public class XMLTableTreeViewer extends TreeViewer implements IDesignViewer {

	class NodeActionMenuListener implements IMenuListener {
		public void menuAboutToShow(IMenuManager menuManager) {
			// used to disable NodeSelection listening while running
			// NodeAction
			XMLNodeActionManager nodeActionManager = new XMLNodeActionManager(((IDOMDocument) getInput()).getModel(), XMLTableTreeViewer.this) {
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

	protected XMLTreeExtension treeExtension;

	public XMLTableTreeViewer(Composite parent) {
		super(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		// set up providers
		this.treeExtension = new XMLTreeExtension(getTree());

		XMLTableTreeContentProvider provider = new XMLTableTreeContentProvider();
		setContentProvider(provider);
		setLabelProvider(provider);

		createContextMenu();

		XMLDragAndDropManager.addDragAndDropSupport(this);
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

	public ISelectionProvider getSelectionProvider() {
		return this;
	}

	public String getTitle() {
		return XMLEditorMessages.XMLTableTreeViewer_0;
	}

	protected void handleDispose(DisposeEvent event) {
		super.handleDispose(event);
		treeExtension.dispose();
		setDocument(null);
	}

	public void refresh() {
		treeExtension.resetCachedData();
		super.refresh();
	}

	public void refresh(Object o) {
		treeExtension.resetCachedData();
		super.refresh(o);
	}
	
	public void refresh(boolean updateLabels) {
		treeExtension.resetCachedData();
		super.refresh(updateLabels);
		getControl().redraw();
	}
	
	public void refresh(Object element, boolean updateLabels) {
		treeExtension.resetCachedData();
		super.refresh(element, updateLabels);
		getControl().redraw();
	}

	public void setDocument(IDocument document) {
		/*
		 * let the text editor to be the one that manages the model's lifetime
		 */
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);

			if (model != null && model instanceof IDOMModel) {
				Document domDoc = null;
				domDoc = ((IDOMModel) model).getDocument();
				setInput(domDoc);
				treeExtension.setIsUnsupportedInput(false);
			}
			else {
				treeExtension.setIsUnsupportedInput(true);
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}

	}

}