/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jan 22, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.css.ui.views.contentoutline;

import java.text.Collator;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.css.ui.internal.CSSUIPlugin;
import org.eclipse.wst.css.ui.internal.editor.CSSEditorPluginImages;
import org.eclipse.wst.sse.ui.views.contentoutline.PropertyChangeUpdateAction;

/**
 * Based on com.ibm.etools.dtd.editor.DTDContentOutlinePage#SortAction
 */
class SortAction extends PropertyChangeUpdateAction {
	private TreeViewer treeViewer;

	public SortAction(TreeViewer viewer, IPreferenceStore store, String preferenceKey) {
		super(CSSUIPlugin.getResourceString("%SortAction.0"), store, preferenceKey, false); //$NON-NLS-1$
		ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(CSSUIPlugin.ID, CSSEditorPluginImages.IMG_OBJ_SORT);
		setImageDescriptor(desc);
		setToolTipText(getText());
		treeViewer = viewer;
		if (isChecked()) {
			treeViewer.setSorter(new ViewerSorter(Collator.getInstance()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		super.update();
		treeViewer.getControl().setVisible(false);
		Object[] expandedElements = treeViewer.getExpandedElements();
		if (isChecked()) {
			treeViewer.setSorter(new ViewerSorter(Collator.getInstance()));
		} else {
			treeViewer.setSorter(null);
		}
		treeViewer.setInput(treeViewer.getInput());
		treeViewer.setExpandedElements(expandedElements);
		treeViewer.getControl().setVisible(true);
	}
}