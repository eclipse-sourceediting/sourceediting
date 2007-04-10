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

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.dtd.core.internal.CMNode;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.util.DTDBatchNodeDelete;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;


public class DeleteAction extends SelectionListenerAction {

	public DeleteAction(String label) {
		super(label);
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
		dtdFile.getDTDModel().beginRecording(this, DTDUIMessages._UI_LABEL_DTD_FILE_DELETE); //$NON-NLS-1$
		batchDelete.deleteNodes(this);
		dtdFile.getDTDModel().endRecording(this);
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
}
