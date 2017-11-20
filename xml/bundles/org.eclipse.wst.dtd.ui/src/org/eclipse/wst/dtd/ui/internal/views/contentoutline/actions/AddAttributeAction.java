/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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


package org.eclipse.wst.dtd.ui.internal.views.contentoutline.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;


public class AddAttributeAction extends BaseAction {

	public AddAttributeAction(DTDModelImpl model, String label) {
		super(model, label);
	}

	public void run() {
		DTDNode selectedNode = getFirstNodeSelected();
		String newName = "NewAttribute"; //$NON-NLS-1$
		if (selectedNode instanceof AttributeList) {
			((AttributeList) selectedNode).addAttribute(newName);
		}
		else if (selectedNode instanceof Element) {
			((Element) selectedNode).addAttribute(newName);
		}
	}

	protected boolean updateSelection(IStructuredSelection selection) {
		boolean rc = super.updateSelection(selection);
		DTDNode node = getFirstNodeSelected(selection);
		if (node instanceof Element) {
			// System.out.println("attribute set to true");
			setEnabled(true);
		}
		else {
			// System.out.println("attribute set to false");
			setEnabled(false);
		}

		return rc;
	}

}
