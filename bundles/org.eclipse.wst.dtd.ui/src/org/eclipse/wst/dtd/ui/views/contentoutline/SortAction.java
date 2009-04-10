/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.views.contentoutline;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImageHelper;
import org.eclipse.wst.dtd.ui.internal.editor.DTDEditorPluginImages;
import org.eclipse.wst.sse.ui.internal.contentoutline.PropertyChangeUpdateAction;

class SortAction extends PropertyChangeUpdateAction {
	private TreeViewer treeViewer;
	private ViewerComparator fComparator;

	public SortAction(TreeViewer viewer, IPreferenceStore store, String preferenceKey) {
		super(DTDUIMessages._UI_BUTTON_SORT_ITEMS, store, preferenceKey, false); //$NON-NLS-1$
		setImageDescriptor(DTDEditorPluginImageHelper.getInstance().getImageDescriptor(DTDEditorPluginImages.IMG_OBJ_SORT));
		setToolTipText(getText());
		fComparator = new DTDContentOutlineComparator();
		treeViewer = viewer;
		if (isChecked()) {
			treeViewer.setComparator(fComparator);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		super.update();
		treeViewer.getControl().setRedraw(false);
		Object[] expandedElements = treeViewer.getExpandedElements();
		if (isChecked()) {
			treeViewer.setComparator(fComparator);
		}
		else {
			treeViewer.setComparator(null);
		}
		treeViewer.setInput(treeViewer.getInput());
		treeViewer.setExpandedElements(expandedElements);
		treeViewer.getControl().setRedraw(true);
	}
}
