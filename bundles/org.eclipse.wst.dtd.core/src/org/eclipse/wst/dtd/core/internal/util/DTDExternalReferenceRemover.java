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
package org.eclipse.wst.dtd.core.internal.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;


/*
 * This class is responsible for updating the model when items are deleted or
 * so that items referenced by it are cleaned up note that top level nodes are
 * queued up for deletion so that iteration over the list of nodes from the
 * dtdfile is not messed up. Note that when an external parmeter entity
 * changes, the client of the model (e.g. editor) must be a DTDFileListener
 * implementing the listener's interface to keep the model's referential
 * integrity (See DTDModelImpl for example).
 */

public class DTDExternalReferenceRemover extends DTDVisitor {

	protected DTDBatchNodeDelete batchDelete;
	protected List externalElementsAndParmEntities = new ArrayList();

	protected boolean isParmEntity = false;

	protected boolean isUpdating = false;
	protected DTDNode nodeToDelete;
	protected String oldRefName = ""; //$NON-NLS-1$
	protected Object requestor;

	public DTDExternalReferenceRemover() {

	}

	public synchronized void externalReferenceAboutToChange(Object requestor, Entity entity) {
		if (isUpdating) {
			return;
		}
		if (!entity.isParameterEntity() || !entity.isExternalEntity()) {
			// if it is not an external parameter entity, ignore as well
			return;
		}

		isUpdating = true;
		this.requestor = requestor;

		DTDFile dtdFile = entity.getDTDFile();
		if (batchDelete == null) {
			batchDelete = new DTDBatchNodeDelete(dtdFile);
		}

		// See the comment at the head of this file regarding
		// external parameter entities.
		// externalElementsAndParmEntities =
		// dtdFile.getDTDModel().getExternalModels().getElementContentNames(entity.getPublicID(),
		// dtdFile.getDTDModel().resolveID(entity.getPublicID(),
		// entity.getSystemID()));

		visit(dtdFile);

		batchDelete.deleteNodes(requestor);

		isUpdating = false;
	}

	public boolean isMatchingName(String name) {
		return externalElementsAndParmEntities.contains(name);
	}

	public void visitAttribute(Attribute attr) {
		super.visitAttribute(attr);
		String attrName = attr.getName();
		String attrType = attr.getType();

		if (isParameterEntityRef(attrName)) {
			if (isMatchingName(attrName)) {
				attr.setName(requestor, "TempName"); //$NON-NLS-1$
			}
		}
		if (isParameterEntityRef(attrType)) {
			if (isMatchingName(attrType)) {
				attr.setType(requestor, Attribute.CDATA);
			}
		}
	}

	public void visitAttributeList(AttributeList attList) {
		super.visitAttributeList(attList);
		String attListName = attList.getName();
		if (isParameterEntityRef(attListName)) {
			if (isMatchingName(attListName)) {
				attList.setName(requestor, "TempName"); //$NON-NLS-1$
			}
		}
	}

	public void visitElement(Element element) {
		String elementName = element.getName();
		if (isParameterEntityRef(elementName)) {
			if (isMatchingName(elementName)) {
				element.setName(requestor, "TempName"); //$NON-NLS-1$
			}
		}
		super.visitElement(element);
	}

	public void visitReference(CMBasicNode node) {
		super.visitReference(node);
		String refName = node.getName();
		if (isMatchingName(refName)) {
			batchDelete.addNode(node);
		}
	}


}
