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

import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;


public class AddAttributeListAction extends BaseAction {

	public AddAttributeListAction(DTDModelImpl model, String label) {
		super(model, label);
	}

	public void run() {
		DTDNode selectedNode = getFirstNodeSelected();
		DTDFile dtdFile = getModel().getDTDFile();
		String attListName = "NewAttList"; //$NON-NLS-1$
		if (selectedNode != null) {
			DTDNode topLevelNode = dtdFile.getTopLevelNodeAt(selectedNode.getStartOffset());
			if (topLevelNode instanceof Element) {
				attListName = ((Element) topLevelNode).getName();
			}
		}

		getModel().getDTDFile().createAttributeList(selectedNode, attListName, true);
		// newElement.setName(DTDUniqueNameHelper.getUniqueElementName(dtdFile));

	}
}
