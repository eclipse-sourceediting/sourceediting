/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.xml.ui.internal.tabletree;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.wst.xml.internal.ui.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.internal.ui.XMLEditorPluginImages;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;

public class ViewerExpandCollapseAction extends Action {

	protected boolean isExpandAction;
	protected AbstractTreeViewer viewer = null;

	public ViewerExpandCollapseAction(boolean isExpandAction) {
		this.isExpandAction = isExpandAction;
		if (isExpandAction) {
			ImageDescriptor e_imageDescriptor = XMLEditorPluginImageHelper.getInstance().getImageDescriptor(XMLEditorPluginImages.IMG_ETOOL_EXPANDALL);
			ImageDescriptor d_imageDescriptor = XMLEditorPluginImageHelper.getInstance().getImageDescriptor(XMLEditorPluginImages.IMG_DTOOL_EXPANDALL);

			setImageDescriptor(e_imageDescriptor);
			setDisabledImageDescriptor(d_imageDescriptor);
			setToolTipText(XMLUIPlugin.getResourceString("%_UI_INFO_EXPAND_ALL")); //$NON-NLS-1$
		} else {
			ImageDescriptor e_imageDescriptor = XMLEditorPluginImageHelper.getInstance().getImageDescriptor(XMLEditorPluginImages.IMG_ETOOL_COLLAPSEALL);
			ImageDescriptor d_imageDescriptor = XMLEditorPluginImageHelper.getInstance().getImageDescriptor(XMLEditorPluginImages.IMG_DTOOL_COLLAPSEALL);

			setImageDescriptor(e_imageDescriptor);
			setDisabledImageDescriptor(d_imageDescriptor);
			setToolTipText(XMLUIPlugin.getResourceString("%_UI_INFO_COLLAPSE_ALL")); //$NON-NLS-1$
		}
	}

	public void setViewer(AbstractTreeViewer viewer) {
		this.viewer = viewer;
	}

	public void run() {
		if (viewer != null) {
			// temporarily set the visibility to false
			// this has a HUGE performance benefit
			boolean isVisible = viewer.getControl().getVisible();
			viewer.getControl().setVisible(false);

			if (isExpandAction) {
				viewer.expandAll();
			} else {
				viewer.collapseAll();
			}

			// restore the previous visibility state
			// 
			viewer.getControl().setVisible(isVisible);
		}
	}
}

