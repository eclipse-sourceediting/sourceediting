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

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.dtd.core.CMNode;
import org.eclipse.wst.dtd.core.DTDFile;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.DTDPlugin;
import org.eclipse.wst.dtd.core.util.DTDBatchNodeDelete;

public class DeleteAction extends SelectionListenerAction {

	public DeleteAction(String label) {
		super(label);
	}

	public boolean updateSelection(IStructuredSelection sel) {
		if (!super.updateSelection(sel))
			return false;

		Object selectedObject = sel.getFirstElement();
		if (selectedObject instanceof DTDNode && !(selectedObject instanceof CMNode && ((CMNode) selectedObject).isRootElementContent())) {
			setEnabled(true);
			return true; // enable delete menu item
		}
		else {
			setEnabled(false);
			return false; // disable it - grey out
		}
	}

	public void run() {
		IStructuredSelection selection = getStructuredSelection();

		Iterator iter = selection.iterator();
		DTDBatchNodeDelete batchDelete = null;
		DTDFile dtdFile = null;
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof DTDNode) {
				DTDNode node = (DTDNode) element;
				dtdFile = node.getDTDFile();
				if (batchDelete == null) {
					batchDelete = new DTDBatchNodeDelete(dtdFile);
				}
				batchDelete.addNode((DTDNode) element);
			}
		}
		dtdFile.getDTDModel().beginRecording(this, DTDPlugin.getDTDString("_UI_LABEL_DTD_FILE_DELETE")); //$NON-NLS-1$
		batchDelete.deleteNodes(this);
		dtdFile.getDTDModel().endRecording(this);
	}
}
