/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.css.core.document.ICSSNode;
import org.eclipse.wst.css.core.document.ICSSStyleDeclItem;
import org.eclipse.wst.css.core.document.ICSSValue;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.sse.core.IAdapterFactory;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateActionContributionItem;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration;

public class CSSContentOutlineConfiguration extends StructuredContentOutlineConfiguration {
	private final String OUTLINE_SORT_PREF = "outline-sort"; //$NON-NLS-1$
	private IContentProvider fContentProvider = null;
	private ILabelProvider fLabelProvider = null;

	public CSSContentOutlineConfiguration() {
		super();
	}

	public IContributionItem[] createToolbarContributions(TreeViewer viewer) {
		IContributionItem[] items = super.createToolbarContributions(viewer);

		SortAction sortAction = new SortAction(viewer, CSSUIPlugin.getDefault().getPreferenceStore(), OUTLINE_SORT_PREF);
		IContributionItem sortItem = new PropertyChangeUpdateActionContributionItem(sortAction);

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

	/**
	 * @see com.ibm.sse.editor.views.contentoutline.ContentOutlineConfiguration#getContentProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public IContentProvider getContentProvider(TreeViewer viewer) {
		if (fContentProvider == null && getFactory() != null)
			fContentProvider = new JFaceNodeContentProviderCSS((IAdapterFactory) getFactory());
		return fContentProvider;
	}

	/**
	 * @see com.ibm.sse.editor.views.contentoutline.ContentOutlineConfiguration#getLabelProvider(org.eclipse.jface.viewers.TreeViewer)
	 */
	public ILabelProvider getLabelProvider(TreeViewer viewer) {
		if (fLabelProvider == null && getFactory() != null)
			fLabelProvider = new JFaceNodeLabelProviderCSS((IAdapterFactory) getFactory());
		return fLabelProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.views.contentoutline.ContentOutlineConfiguration#getNodes(java.util.List)
	 */
	public List getNodes(List nodes) {
		List filteredNodes = new ArrayList(nodes);

		List targetNodes = new ArrayList();
		Iterator i = filteredNodes.iterator();
		while (i.hasNext()) {
			Object obj = i.next();
			if (obj instanceof ICSSNode) {
				ICSSNode node = (ICSSNode) obj;
				short nodeType = node.getNodeType();
				if (node instanceof ICSSValue) {
					while (node != null && !(node instanceof ICSSStyleDeclItem)) {
						node = node.getParentNode();
					}
				} else if (nodeType == ICSSNode.STYLEDECLARATION_NODE) {
					node = node.getParentNode();
				} else if (nodeType == ICSSNode.MEDIALIST_NODE) {
					node = node.getParentNode();
				}
				if (node != null) {
					obj = node;
				}
			}
			targetNodes.add(obj);
		}

		return targetNodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.views.contentoutline.ContentOutlineConfiguration#getSelectedNodes(com.ibm.sse.editor.view.events.NodeSelectionChangedEvent)
	 */
	public List getSelectedNodes(NodeSelectionChangedEvent event) {
		return getNodes(event.getSelectedNodes());
	}

	/**
	 * @deprecated use key directly (no need for generator)
	 */
	public String getSortPreferenceKey() {
//		return PreferenceKeyGenerator.generateKey(OUTLINE_SORT_PREF, getDeclaringID());
		return OUTLINE_SORT_PREF;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.views.contentoutline.StructuredContentOutlineConfiguration#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore() {
		return CSSUIPlugin.getDefault().getPreferenceStore();
	}
}