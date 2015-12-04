/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.ui.views.contentoutline.SortAction
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.ui.views.contentoutline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.json.ui.internal.JSONUIMessages;
import org.eclipse.wst.json.ui.internal.JSONUIPlugin;
import org.eclipse.wst.json.ui.internal.editor.JSONEditorPluginImages;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;

/*
 * Based on DTDContentOutlinePage#SortAction
 * Sort the outline by JSON key names.
 */
class SortAction extends PropertyChangeUpdateAction {
	private TreeViewer treeViewer;

	public SortAction(TreeViewer viewer, IPreferenceStore store,
			String preferenceKey) {
		super(JSONUIMessages.SortAction_0, store, preferenceKey, false); //$NON-NLS-1$
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(
				JSONUIPlugin.PLUGIN_ID, JSONEditorPluginImages.IMG_OBJ_SORT);
		setImageDescriptor(desc);
		setToolTipText(getText());
		treeViewer = viewer;
		if (isChecked()) {
			treeViewer.setComparator(new ViewerComparator());
		}
	}

	public void update() {
		super.update();
		treeViewer.getControl().setRedraw(false);
		Object[] expandedElements = treeViewer.getExpandedElements();
		if (isChecked()) {
			treeViewer.setComparator(new ViewerComparator());
		} else {
			treeViewer.setComparator(null);
		}
		treeViewer.setInput(treeViewer.getInput());
		treeViewer.setExpandedElements(expandedElements);
		treeViewer.getControl().setRedraw(true);
	}
}
