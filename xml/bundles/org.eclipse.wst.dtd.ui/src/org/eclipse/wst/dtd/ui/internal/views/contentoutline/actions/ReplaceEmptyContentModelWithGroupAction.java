/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;

public class ReplaceEmptyContentModelWithGroupAction extends BaseAction {

	public ReplaceEmptyContentModelWithGroupAction(DTDModelImpl model, String label) {
		super(model, label);
	}

	public void run() {
		DTDNode node = getFirstNodeSelected();
		if (node instanceof Element) {
			CMNode contentModel = ((Element) node).getContentModel();
			if (CMNode.EMPTY.equals(contentModel.getType())) {
				getModel().beginRecording(this, getText());
				((Element) node).replaceContentModel(this, "()"); //$NON-NLS-1$
				getModel().endRecording(this);
			}
		}
	}

	protected boolean updateSelection(IStructuredSelection selection) {
		boolean rc = super.updateSelection(selection);
		DTDNode node = getFirstNodeSelected(selection);
		setEnabled(node instanceof Element);
		return rc;
	}
}
