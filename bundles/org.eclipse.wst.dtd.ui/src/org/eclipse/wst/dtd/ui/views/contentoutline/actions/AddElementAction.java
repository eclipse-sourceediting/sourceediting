/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.views.contentoutline.actions;

import org.eclipse.wst.dtd.core.DTDFile;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.util.DTDUniqueNameHelper;
import org.eclipse.wst.sse.ui.StructuredTextEditor;


public class AddElementAction extends BaseAction {

	public AddElementAction(StructuredTextEditor editor, String label) {
		super(editor, label);
	}

	public void run() {
		DTDNode selectedNode = getFirstNodeSelected();
		DTDFile dtdFile = getModel().getDTDFile();
		String elementName = DTDUniqueNameHelper.getUniqueElementName(dtdFile);
		dtdFile.createElement(selectedNode, elementName, true); //$NON-NLS-1$
	}
}
