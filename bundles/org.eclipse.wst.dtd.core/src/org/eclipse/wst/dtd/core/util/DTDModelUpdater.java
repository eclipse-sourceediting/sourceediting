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
package org.eclipse.wst.dtd.core.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.dtd.core.Attribute;
import org.eclipse.wst.dtd.core.AttributeList;
import org.eclipse.wst.dtd.core.CMBasicNode;
import org.eclipse.wst.dtd.core.DTDFile;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.Element;
import org.eclipse.wst.dtd.core.Entity;
import org.eclipse.wst.dtd.core.ParameterEntityReference;


// this class is responsible for updating the model when items
// are deleted or a external parm entity changes so that 
// items referenced by it are cleaned up
// note that top level nodes are queued up for deletion so that
// iteration over the list of nodes from the dtdfile is not messed up
public class DTDModelUpdater extends DTDVisitor {

	public DTDModelUpdater() {

	}

	protected boolean isUpdating = false;

	protected boolean isParmEntity = false;
	protected String oldRefName = ""; //$NON-NLS-1$
	protected Object requestor;
	protected DTDNode nodeToDelete;

	protected List nodesToDelete = new ArrayList();

	public synchronized void objectAboutToBeDeleted(Object requestor, DTDNode node) {
		if (isUpdating) {
			return;
		}
		if (!(node instanceof Entity || node instanceof Element)) {
			// just ignore if it is not one of these
			return;
		}
		if (node instanceof Entity && !((Entity) node).isParameterEntity()) {
			// if it is not a parameter entity, ignore as well
			return;
		}


		isUpdating = true;
		this.requestor = requestor;
		this.nodeToDelete = node;
		oldRefName = node.getName();
		isParmEntity = false;
		nodesToDelete.clear();

		if (node instanceof Entity) {
			Entity entity = (Entity) node;
			isParmEntity = true;
			oldRefName = "%" + oldRefName + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		DTDFile dtdFile = node.getDTDFile();
		visit(dtdFile);

		for (int i = 0; i < nodesToDelete.size(); i++) {
			dtdFile.deleteNode(requestor, (DTDNode) nodesToDelete.get(i));
		}

		isUpdating = false;
	}

	public void visitElement(Element element) {
		if (isParmEntity) {
			if (element.getName().equals(oldRefName)) {
				element.setName(requestor, "TempName"); //$NON-NLS-1$
			}
		}
		super.visitElement(element);
	}

	public void visitAttributeList(AttributeList attList) {
		super.visitAttributeList(attList);
		if (attList.getName().equals(oldRefName)) {
			if (isParmEntity) {
				attList.setName(requestor, "TempName"); //$NON-NLS-1$
			}
			else {
				// save up for later deletion
				nodesToDelete.add(attList);
			}
		}
	}

	public void visitAttribute(Attribute attr) {
		super.visitAttribute(attr);
		if (isParmEntity) {
			if (attr.getName().equals(oldRefName)) {
				attr.setName(requestor, "TempName"); //$NON-NLS-1$
			}
			if (attr.getType().equals(oldRefName)) {
				attr.setType(requestor, Attribute.CDATA);
			}
		}
		// check the attr name and the attr type to see if it 
		// needs updating
	}

	public void visitReference(CMBasicNode node) {
		super.visitReference(node);

		if (node.getName().equals(oldRefName)) {
			DTDNode parent = (DTDNode) node.getParentNode();
			parent.delete(requestor, node);
		}
	}

	public void visitExternalParameterEntityReference(ParameterEntityReference parmEntityRef) {
		super.visitExternalParameterEntityReference(parmEntityRef);
		if (isParmEntity && parmEntityRef.getName().equals(oldRefName)) {
			nodesToDelete.add(parmEntityRef);
		}
	}
}
