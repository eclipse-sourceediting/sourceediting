/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.dtd.ui.views.contentoutline.actions;

import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

public class AddNotationAction extends BaseAction {

	public AddNotationAction(StructuredTextEditor editor, String label) {
		super(editor, label);
	}

	public void run() {
		DTDNode selectedNode = getFirstNodeSelected();

		getModel().getDTDFile().createNotation(selectedNode, "NewNotation", true); //$NON-NLS-1$

		//      newNotation.setName(DTDUniqueNameHelper.getUniqueNotationName(dtdFile));
	}
}
