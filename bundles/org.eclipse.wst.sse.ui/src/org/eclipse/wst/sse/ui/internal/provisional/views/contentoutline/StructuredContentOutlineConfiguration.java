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
package org.eclipse.wst.sse.ui.internal.provisional.views.contentoutline;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImageHelper;
import org.eclipse.wst.sse.ui.internal.editor.EditorPluginImages;

public class StructuredContentOutlineConfiguration extends ContentOutlineConfiguration {

	/**
	 * Structured source files tend to have large/long tree structures. Add a
	 * collapse action to help with navigation.
	 */
	private class CollapseTreeAction extends Action {
		private TreeViewer fTreeViewer = null;

		public CollapseTreeAction(TreeViewer viewer) {
			super(SSEUIMessages.StructuredContentOutlineConfiguration_0, AS_PUSH_BUTTON); //$NON-NLS-1$
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

	private class ToggleLinkAction extends PropertyChangeUpdateAction {
		public ToggleLinkAction(IPreferenceStore store, String preference) {
			super(SSEUIMessages.StructuredContentOutlineConfiguration_1, store, preference, true); //$NON-NLS-1$
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
	private String fContentTypeIdentifier = null;

	private IEditorPart fEditor = null;

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
					return new ShowInContext(getEditor().getEditorInput(), getEditor().getEditorSite().getSelectionProvider().getSelection());
				}
			};
		}
		else if (key.equals(IShowInTargetList.class)) {
			adapter = getEditor().getAdapter(key);
		}
		else {
			adapter = super.getAdapter(key);
		}
		return adapter;
	}

	public String getContentTypeID(TreeViewer treeViewer) {
		if (fContentTypeIdentifier != null) {
			return fContentTypeIdentifier;
		}
		return super.getContentTypeID(treeViewer);
	}

	/**
	 * @return
	 */
	protected IEditorPart getEditor() {
		return fEditor;
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

	protected IPreferenceStore getPreferenceStore() {
		return SSEUIPlugin.getInstance().getPreferenceStore();
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
	public void setEditor(IEditorPart editor) {
		fEditor = editor;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		super.setInitializationData(config, propertyName, data);

		fContentTypeIdentifier = config.getAttribute("contentTypeId"); //$NON-NLS-1$
		if (data != null) {
			if (data instanceof String && data.toString().length() > 0) {
				fContentTypeIdentifier = (String) data;
			}
		}
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
	}

}
