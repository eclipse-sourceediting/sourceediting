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
/*
 * Created on Jan 22, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.dtd.ui.views.contentoutline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.wst.dtd.ui.DTDEditorPlugin;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImageHelper;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImages;
import org.eclipse.wst.sse.ui.views.contentoutline.PropertyChangeUpdateAction;


/**
 * Based on com.ibm.etools.dtd.editor.DTDContentOutlinePage#OrderAction
 */
class OrderAction extends PropertyChangeUpdateAction {
	private DTDTreeContentProvider contentProvider;
	private TreeViewer treeViewer;

	public OrderAction(TreeViewer viewer, DTDTreeContentProvider provider, IPreferenceStore store, String preferenceKey) {
		super(DTDEditorPlugin.getResourceString("_UI_BUTTON_GROUP_ITEMS_LOGICALLY"), store, preferenceKey, false); //$NON-NLS-1$
		setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_OBJ_ORGANIZE_DTD_LOGICALLY));
		treeViewer = viewer;
		contentProvider = provider;
		setToolTipText(getText());
		contentProvider.setShowLogicalOrder(isChecked());
	}

	public void update() {
		super.update();
		setChecked(getPreferenceStore().getBoolean(getPreferenceKey()));
		//	treeViewer.getControl().setVisible(false);
		//	treeViewer.getControl().setRedraw(false);
		Object[] expandedElements = treeViewer.getExpandedElements();
		ISelection selection = treeViewer.getSelection();
		contentProvider.setShowLogicalOrder(isChecked());

		//treeViewer.setInput(treeViewer.getInput());
		treeViewer.refresh(treeViewer.getInput());

		treeViewer.setExpandedElements(expandedElements);
		treeViewer.setSelection(selection);
		// treeViewer.getControl().setVisible(true);
		//	treeViewer.getControl().setRedraw(true);

	}
}
