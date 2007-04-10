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
import org.eclipse.wst.dtd.core.internal.document.DTDModelImpl;
import org.eclipse.wst.dtd.core.internal.util.LabelValuePair;


public class AddParameterEntityReferenceAction extends BaseAction {
	public AddParameterEntityReferenceAction(DTDModelImpl model, String label) {
		super(model, label);
	}

	public String getFirstExternalParameterEntity(DTDFile dtdFile) {
		LabelValuePair[] freeExternalEntities = dtdFile.getDTDModel().createParmEntityContentItems(null);

		if (freeExternalEntities.length > 0) {
			return (String) freeExternalEntities[0].fValue;
			// return (Entity) freeExternalEntities[0].fValue;
		}
		return null;
	}

	public void run() {
		DTDFile dtdFile = getModel().getDTDFile();
		String extEntity = getFirstExternalParameterEntity(dtdFile);
		DTDNode selectedNode = getFirstNodeSelected();
		if (extEntity != null) {
			dtdFile.createParameterEntityReference(selectedNode, extEntity, true);
		}
		else {
			dtdFile.createParameterEntityReference(selectedNode, "%NewEntityReference;", true); //$NON-NLS-1$
		}
	}
}
