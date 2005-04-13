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
package org.eclipse.wst.sse.ui.internal.properties;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.ui.internal.ViewerSelectionManager;
import org.eclipse.wst.sse.ui.internal.actions.ActiveEditorActionHandler;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapterFactory;
import org.eclipse.wst.sse.ui.internal.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.internal.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.sse.ui.views.properties.IPropertySourceExtension;
import org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration;


/**
 * A configurable IPropertySheetPage since the standard PropertySheetPage does
 * not expose its viewer field.
 */

public class ConfigurablePropertySheetPage extends PropertySheetPage implements INodeSelectionListener {

	private PropertySheetConfiguration fConfiguration;
	// has the widget been created?
	private boolean fIsRealized = false;
	private IMenuManager fMenuManager;

	protected PageBook fParentPageBook = null;
	// are we refreshing the contents?
	protected boolean fRefreshing = false;

	protected RemoveAction fRemoveAction;
	private IStatusLineManager fStatusLineManager;

	protected IStructuredModel fStructuredModel = null;
	private IToolBarManager fToolBarManager;

	private ViewerSelectionManager fViewerSelectionManager;

	private final PropertySheetConfiguration NULL_CONFIGURATION = new PropertySheetConfiguration();

	public ConfigurablePropertySheetPage() {
		super();
	}

	public void createControl(Composite parent) {
		setPropertySourceProvider(getConfiguration().getPropertySourceProvider());
		super.createControl(parent);
		if (parent instanceof PageBook)
			fParentPageBook = (PageBook) parent;
	}

	public void dispose() {
		// disconnect from the ViewerSelectionManager
		if (getViewerSelectionManager() != null) {
			getViewerSelectionManager().removeNodeSelectionListener(this);
		}
		setModel(null);
		setConfiguration(null);
		super.dispose();
	}

	/**
	 * @return
	 */
	public PropertySheetConfiguration getConfiguration() {
		if (fConfiguration == null)
			fConfiguration = NULL_CONFIGURATION;
		return fConfiguration;
	}

	public IStructuredModel getModel() {
		return fStructuredModel;
	}

	protected IJFaceNodeAdapterFactory getViewerRefreshFactory() {
		if (getModel() == null)
			return null;
		FactoryRegistry factoryRegistry = getModel().getFactoryRegistry();
		IJFaceNodeAdapterFactory adapterFactory = (IJFaceNodeAdapterFactory) factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		return adapterFactory;
	}

	/**
	 * @return Returns the viewerSelectionManager.
	 */
	public ViewerSelectionManager getViewerSelectionManager() {
		return fViewerSelectionManager;
	}

	/*
	 * @see PropertySheetPage#handleEntrySelection(ISelection)
	 */
	public void handleEntrySelection(ISelection selection) {
		// Useful for enabling/disabling actions based on the
		// selection (or lack thereof). Also, ensure that the
		// control exists before sending selection to it.
		if (fIsRealized && selection != null) {
			super.handleEntrySelection(selection);
			fRemoveAction.setEnabled(!selection.isEmpty());
			//			if (selection != null && !selection.isEmpty() && selection
			// instanceof IStructuredSelection) {
			//				IPropertySheetEntry entry = (IPropertySheetEntry)
			// ((IStructuredSelection) selection).getFirstElement();
			//			}
		}
	}

	/**
	 * @return Returns the isRealized.
	 */
	public boolean isRealized() {
		return fIsRealized;
	}

	public void makeContributions(IMenuManager menuManager, IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {
		super.makeContributions(menuManager, toolBarManager, statusLineManager);
		fMenuManager = menuManager;
		fToolBarManager = toolBarManager;
		fStatusLineManager = statusLineManager;

		fRemoveAction = new RemoveAction(this);
		toolBarManager.add(fRemoveAction);
		menuManager.add(fRemoveAction);
		getConfiguration().addContributions(menuManager, toolBarManager, statusLineManager);

		menuManager.update(true);
		fIsRealized = true;
	}

	public void nodeSelectionChanged(NodeSelectionChangedEvent event) {
		selectionChanged(null, new StructuredSelection(event.getSelectedNodes()));
	}

	/**
	 * @see org.eclipse.ui.views.properties.PropertySheetPage#refresh()
	 */
	public void refresh() {
		/**
		 * Avoid refreshing the property sheet if it is already doing so. A
		 * refresh can prompt an active cell editor to close, applying the
		 * value and altering the selected node. In that case, a loop could
		 * occur.
		 */

		if (!fRefreshing) {
			fRefreshing = true;
			super.refresh();
			fRefreshing = false;
		} else {
			// detected a loop in the property sheet (shouldn't happen)
		}
	}

	public void remove() {
		if (getControl() instanceof Tree) {
			TreeItem[] items = ((Tree) getControl()).getSelection();
			List selectedNodes = getViewerSelectionManager().getSelectedNodes();
			if (items != null && items.length == 1 && selectedNodes != null) {
				Object data = items[0].getData();
				if (data instanceof IPropertySheetEntry) {
					IPropertySheetEntry entry = (IPropertySheetEntry) data;
					ISelection selection = getConfiguration().getSelection(null, new StructuredSelection(selectedNodes));
					if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
						IPropertySource source = getConfiguration().getPropertySourceProvider().getPropertySource(((IStructuredSelection) selection).getFirstElement());
						if (source != null && source instanceof IPropertySourceExtension) {
							((IPropertySourceExtension) source).removeProperty(entry.getDisplayName());
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		super.selectionChanged(part, getConfiguration().getSelection(part, selection));
	}

	public void setActionBars(IActionBars actionBars) {
		super.setActionBars(actionBars);
		getSite().getActionBars().setGlobalActionHandler(ActionFactory.UNDO.getId(), new ActiveEditorActionHandler(getSite(), ActionFactory.UNDO.getId()));
		getSite().getActionBars().setGlobalActionHandler(ActionFactory.REDO.getId(), new ActiveEditorActionHandler(getSite(), ActionFactory.REDO.getId()));
	}

	/**
	 * @param configuration
	 *            The configuration to set.
	 */
	public void setConfiguration(PropertySheetConfiguration configuration) {
		if (fConfiguration != null && isRealized()) {
			fConfiguration.removeContributions(fMenuManager, fToolBarManager, fStatusLineManager);
			fConfiguration.unconfigure();
		}

		fConfiguration = configuration;

		if (fConfiguration != null) {
			setPropertySourceProvider(fConfiguration.getPropertySourceProvider());
			if (isRealized())
				fConfiguration.addContributions(fMenuManager, fToolBarManager, fStatusLineManager);
		}
	}

	/**
	 * Asks this page to take focus within its pagebook view.
	 */
	public void setFocus() {
		super.setFocus();
		if (fParentPageBook != null)
			fParentPageBook.showPage(getControl());
	}

	/**
	 * Sets the model.
	 * 
	 * @param model
	 *            The model to set
	 */
	public void setModel(IStructuredModel model) {
		if (model != fStructuredModel) {
			IJFaceNodeAdapterFactory refresher = getViewerRefreshFactory();
			if (refresher != null)
				refresher.removeListener(this);
			fStructuredModel = model;
			refresher = getViewerRefreshFactory();
			if (refresher != null)
				refresher.addListener(this);
		}
	}

	public void setViewerSelectionManager(ViewerSelectionManager viewerSelectionManager) {
		// disconnect from old one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.removeNodeSelectionListener(this);
		}

		fViewerSelectionManager = viewerSelectionManager;

		// connect to new one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.addNodeSelectionListener(this);
		}
	}
}
