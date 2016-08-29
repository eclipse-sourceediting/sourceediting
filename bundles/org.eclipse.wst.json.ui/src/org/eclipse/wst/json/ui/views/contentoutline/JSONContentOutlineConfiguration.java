/**
 *  Copyright (c) 2013-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.ui.views.contentoutline;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.contentoutline.JFaceNodeContentProvider;
import org.eclipse.wst.json.ui.internal.contentoutline.JFaceNodeLabelProvider;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration;

/**
 * Configuration for outline view page which shows JSON content.
 * 
 * @see org.eclipse.wst.sse.ui.views.contentoutline.ContentOutlineConfiguration
 */
public class JSONContentOutlineConfiguration extends
		ContentOutlineConfiguration {
	
	private IContentProvider fContentProvider = null;
	private ILabelProvider fLabelProvider = null;
	private final String OUTLINE_SORT_PREF = "outline-sort"; //$NON-NLS-1$
	private static final String OUTLINE_FILTER_PREF = "org.eclipse.wst.json.ui.OutlinePage"; //$NON-NLS-1$

	/**
	 * Create new instance of JSONContentOutlineConfiguration
	 */
	public JSONContentOutlineConfiguration() {
		// Must have empty constructor to createExecutableExtension
		super();
	}

	public IContributionItem[] createToolbarContributions(TreeViewer viewer) {
		IContributionItem[] items = super.createToolbarContributions(viewer);

		SortAction sortAction = new SortAction(viewer, JSONUIPlugin
				.getDefault().getPreferenceStore(), OUTLINE_SORT_PREF);
		IContributionItem sortItem = new PropertyChangeUpdateActionContributionItem(
				sortAction);

		if (items == null) {
			items = new IContributionItem[1];
			items[0] = sortItem;
		} else {
			IContributionItem[] combinedItems = new IContributionItem[items.length + 1];
			combinedItems[0] = sortItem;
			System.arraycopy(items, 0, combinedItems, 1, items.length);
			items = combinedItems;
		}
		return items;
	}

	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null)
			fContentProvider = new JFaceNodeContentProvider();
		return fContentProvider;
	}

	private Object getFilteredNode(Object o) {
		IJSONNode node = null;
		if (o instanceof IJSONNode) {
			node = (IJSONNode) o;
			if (node.getOwnerPairNode() != null) {
				IJSONPair owner = node.getOwnerPairNode();
				IJSONValue value = owner.getValue();
				if (!(value instanceof IJSONArray)) {
					return node.getOwnerPairNode();
				}
			}
			/*
			 * short nodeType = node.getNodeType(); if (node instanceof
			 * IJSONValue) { while (node != null && !(node instanceof
			 * IJSONStyleDeclItem)) { node = node.getParentNode(); } } else if
			 * (nodeType == IJSONNode.STYLEDECLARATION_NODE) { node =
			 * node.getParentNode(); } else if (nodeType ==
			 * IJSONNode.MEDIALIST_NODE) { node = node.getParentNode(); }
			 */
		}
		return node;
	}

	private Object[] getFilteredNodes(Object[] objects) {
		Object[] filtered = new Object[objects.length];
		for (int i = 0; i < filtered.length; i++) {
			filtered[i] = getFilteredNode(objects[i]);
		}
		return filtered;
	}

	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null)
			fLabelProvider = new MyDelegatingStyledCellLabelProvider(
					new JFaceNodeLabelProvider());
		return fLabelProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.
	 * StructuredContentOutlineConfiguration#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return JSONUIPlugin.getDefault().getPreferenceStore();
	}

	public ISelection getSelection(TreeViewer viewer, ISelection selection) {
		ISelection filteredSelection = selection;
		if (selection instanceof IStructuredSelection) {
			Object[] filteredNodes = getFilteredNodes(((IStructuredSelection) selection)
					.toArray());
			filteredSelection = new StructuredSelection(filteredNodes);
		}
		return filteredSelection;
	}

	protected String getOutlineFilterTarget() {
		return OUTLINE_FILTER_PREF;
	}
}