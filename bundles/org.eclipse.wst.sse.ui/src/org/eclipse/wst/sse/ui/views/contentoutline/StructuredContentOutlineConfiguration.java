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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.wst.sse.core.IFactoryRegistry;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.ViewerSelectionManager;
import org.eclipse.wst.sse.ui.internal.Logger;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;

public class StructuredContentOutlineConfiguration extends ContentOutlineConfiguration {
	/**
	 * Structured source files tend to have large/long tree structures. Add a
	 * collapse action to help with navigation.
	 */
	protected class CollapseTreeAction extends Action {
		private TreeViewer fTreeViewer = null;

		public CollapseTreeAction(TreeViewer viewer) {
			super(SSEUIPlugin.getResourceString("%StructuredContentOutlineConfiguration.0"), AS_PUSH_BUTTON); //$NON-NLS-1$
			setImageDescriptor(COLLAPSE_E);
			setDisabledImageDescriptor(COLLAPSE_D);
			setToolTipText(getText());
			fTreeViewer = viewer;
		}

		public void run() {
			super.run();
			fTreeViewer.collapseAll();
		}
	}

	protected class ToggleLinkAction extends PropertyChangeUpdateAction {

		public ToggleLinkAction(IPreferenceStore store, String preference) {
			super(SSEUIPlugin.getResourceString("%StructuredContentOutlineConfiguration.1"), store, preference, true); //$NON-NLS-1$
			setToolTipText(getText());
			setDisabledImageDescriptor(SYNCED_D);
			setImageDescriptor(SYNCED_E);
			update();
		}

		public void update() {
			super.update();
			setLinkWithEditor(isChecked());
		}
	}

	protected ImageDescriptor COLLAPSE_D = EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_DLCL_COLLAPSEALL);
	protected ImageDescriptor COLLAPSE_E = EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_ELCL_COLLAPSEALL);

	private StructuredTextEditor fEditor = null;

	private boolean fIsLinkWithEditor = false;
	private Map fMenuContributions = null;

	private Map fToolbarContributions = null;

	private final String OUTLINE_LINK_PREF = "outline-link-editor"; //$NON-NLS-1$

	ImageDescriptor SYNCED_D = EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_DLCL_SYNCED);
	ImageDescriptor SYNCED_E = EditorPluginImageHelper.getInstance().getImageDescriptor(EditorPluginImages.IMG_ELCL_SYNCED);

	public StructuredContentOutlineConfiguration() {
		super();
	}

	/**
	 * @param viewer
	 * @return
	 */
	protected IContributionItem[] createMenuContributions(TreeViewer viewer) {
		IContributionItem[] items;
		IContributionItem toggleLinkItem = new PropertyChangeUpdateActionContributionItem(new ToggleLinkAction(getPreferenceStore(), OUTLINE_LINK_PREF));
		items = super.getMenuContributions(viewer);
		if (items == null) {
			items = new IContributionItem[]{toggleLinkItem};
		}
		else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 1];
			System.arraycopy(items, 0, combinedItems, 0, items.length);
			combinedItems[items.length] = toggleLinkItem;
			items = combinedItems;
		}
		return items;
	}

	protected IContributionItem[] createToolbarContributions(TreeViewer viewer) {
		IContributionItem[] items;
		IContributionItem collapseAllItem = new ActionContributionItem(new CollapseTreeAction(viewer));
		items = super.getToolbarContributions(viewer);
		if (items == null) {
			items = new IContributionItem[]{collapseAllItem};
		}
		else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 1];
			System.arraycopy(items, 0, combinedItems, 0, items.length);
			combinedItems[items.length] = collapseAllItem;
			// combinedItems[items.length + 1] = toggleLinkItem;
			items = combinedItems;
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		Object adapter = null;
		if (key.equals(IShowInSource.class)) {
			adapter = new IShowInSource() {
				public ShowInContext getShowInContext() {
					return new ShowInContext(getEditor().getEditorInput(), getEditor().getSelectionProvider().getSelection());
				}
			};
		}
		else if (key.equals(IShowInTargetList.class)) {
			adapter = getEditor().getAdapter(key);
		}
		else
			adapter = super.getAdapter(key);
		return adapter;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getDoubleClickListener(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IDoubleClickListener getDoubleClickListener(TreeViewer viewer) {
		return (ViewerSelectionManager) getEditor().getAdapter(ViewerSelectionManager.class);
	}

	/**
	 * @return
	 */
	public StructuredTextEditor getEditor() {
		return fEditor;
	}

	protected IJFaceNodeAdapterFactory getFactory() {
		IFactoryRegistry factoryRegistry = getEditor().getModel().getFactoryRegistry();
		IJFaceNodeAdapterFactory adapterFactory = (IJFaceNodeAdapterFactory) factoryRegistry.getFactoryFor(IJFaceNodeAdapter.class);
		if (adapterFactory == null) {
			Logger.log(Logger.ERROR, "model has no JFace adapter factory"); //$NON-NLS-1$
		}
		return adapterFactory;
	}

	/**
	 * @deprecated just use the preference key directly
	 * @return
	 */
	protected String getLinkPreferenceKey() {
		return OUTLINE_LINK_PREF;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getMenuContributions(org.eclipse.jface.viewers.TreeViewer)
	 */
	public final IContributionItem[] getMenuContributions(TreeViewer viewer) {
		if (fMenuContributions == null) {
			fMenuContributions = new HashMap();
		}

		IContributionItem[] items = (IContributionItem[]) fMenuContributions.get(viewer);
		if (items == null) {
			items = createMenuContributions(viewer);
			fMenuContributions.put(viewer, items);
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getSelectedNodes(org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent)
	 */
	public List getSelectedNodes(NodeSelectionChangedEvent event) {
		if (isLinkedWithEditor())
			return super.getSelectedNodes(event);
		else
			return null;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getSelectionChangedListener(org.eclipse.jface.viewers.TreeViewer)
	 */
	public ISelectionChangedListener getSelectionChangedListener(TreeViewer viewer) {
		return (ViewerSelectionManager) getEditor().getAdapter(ViewerSelectionManager.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#getToolbarContributions(org.eclipse.jface.viewers.TreeViewer)
	 */
	public final IContributionItem[] getToolbarContributions(TreeViewer viewer) {
		if (fToolbarContributions == null) {
			fToolbarContributions = new HashMap();
		}

		IContributionItem[] items = (IContributionItem[]) fToolbarContributions.get(viewer);
		if (items == null) {
			items = createToolbarContributions(viewer);
			fToolbarContributions.put(viewer, items);
		}
		return items;
	}

	protected boolean isLinkedWithEditor() {
		return fIsLinkWithEditor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration#isLinkedWithEditor(org.eclipse.jface.viewers.TreeViewer)
	 */
	public boolean isLinkedWithEditor(TreeViewer treeViewer) {
		return isLinkedWithEditor();
	}

	/**
	 * @param editor
	 */
	public void setEditor(StructuredTextEditor editor) {
		fEditor = editor;
	}

	/**
	 * @param isLinkWithEditor
	 *            The isLinkWithEditor to set.
	 */
	protected void setLinkWithEditor(boolean isLinkWithEditor) {
		fIsLinkWithEditor = isLinkWithEditor;
	}

	public void unconfigure(TreeViewer viewer) {
		super.unconfigure(viewer);
		if (fToolbarContributions != null) {
			IContributionItem[] items = (IContributionItem[]) fToolbarContributions.get(viewer);
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					if (items[i] instanceof PropertyChangeUpdateActionContributionItem) {
						((PropertyChangeUpdateActionContributionItem) items[i]).disconnect();
					}
				}
				fToolbarContributions.remove(viewer);
			}
		}
		if (fMenuContributions != null) {
			IContributionItem[] items = (IContributionItem[]) fMenuContributions.get(viewer);
			if (items != null) {
				for (int i = 0; i < items.length; i++) {
					if (items[i] instanceof PropertyChangeUpdateActionContributionItem) {
						((PropertyChangeUpdateActionContributionItem) items[i]).disconnect();
					}
				}
				fMenuContributions.remove(viewer);
			}
		}
		fLabelProvider = null;
		fContentProvider = null;
	}

	protected IPreferenceStore getPreferenceStore() {
		return SSEUIPlugin.getInstance().getPreferenceStore();
	}
}
