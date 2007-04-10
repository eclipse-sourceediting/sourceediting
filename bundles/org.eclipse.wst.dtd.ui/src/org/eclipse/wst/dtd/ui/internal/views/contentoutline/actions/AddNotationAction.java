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

import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;


public class AddNotationAction extends BaseAction {

	public AddNotationAction(DTDModelImpl model, String label) {
		super(model, label);
	}

	public void run() {
		DTDNode selectedNode = getFirstNodeSelected();

		getModel().getDTDFile().createNotation(selectedNode, "NewNotation", true); //$NON-NLS-1$

		// newNotation.setName(DTDUniqueNameHelper.getUniqueNotationName(dtdFile));
	}
}
