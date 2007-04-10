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
import org.eclipse.wst.dtd.core.internal.AttributeEnumList;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.core.internal.Notation;
import org.eclipse.wst.dtd.core.internal.ParameterEntityReference;


// this class is responsible for updating any dtd node in
// response to a change in the node that they reference
public class DTDReferenceUpdater extends DTDVisitor {
	protected boolean isNotation = false;

	protected boolean isParmEntity = false;

	protected boolean isUpdating = false;
	protected String newName = ""; //$NON-NLS-1$
	protected String oldRefName = "", newRefName = ""; //$NON-NLS-1$ //$NON-NLS-2$
	protected DTDNode referencedNode = null;

	// the references List is a cache of the DTDNodes that are changed
	// as a result of a call to nameAboutToChange(). The idea is that
	// if a subsequent call comes in that changes the name of the same
	// object for which this cache exists for, then we optimize the
	// path by just walking the cache
	private List references = new ArrayList();
	protected Object requestor;

	public DTDReferenceUpdater() {

	}

	public void clearCache() {
		referencedNode = null;
		references.clear();
	}

	public synchronized void nameAboutToChange(Object requestor, DTDNode referencedNode, String newName) {
		if (isUpdating) {
			return;
		}
		if (!(referencedNode instanceof Entity || referencedNode instanceof Element || referencedNode instanceof Notation)) {
			// just ignore if it is not one of these
			return;
		}
		if (referencedNode instanceof Entity && !((Entity) referencedNode).isParameterEntity()) {
			// if it is not a parameter entity, ignore as well
			return;
		}

		isUpdating = true;
		this.requestor = requestor;
		oldRefName = referencedNode.getName();
		this.newName = newRefName = newName;
		isParmEntity = false;
		isNotation = referencedNode instanceof Notation;

		if (referencedNode instanceof Entity) {
			isParmEntity = true;
			oldRefName = "%" + oldRefName + ";"; //$NON-NLS-1$ //$NON-NLS-2$
			newRefName = "%" + newRefName + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (this.referencedNode != null) {
			// check if the previous referenced node that was changed
			// is the same as the one that is coming in. if so, just
			// change the previous regions
			if (this.referencedNode == referencedNode) {
				quickUpdate();
				isUpdating = false;
				return;
			}
		}

		// clear the cache if we get here
		this.referencedNode = referencedNode;
		references.clear();
		DTDFile dtdFile = referencedNode.getDTDFile();
		visit(dtdFile);
		isUpdating = false;
	}

	protected void quickUpdate() {
		for (int i = 0; i < references.size(); i++) {
			DTDNode node = (DTDNode) references.get(i);
			if (node instanceof Element) {
				visitElement((Element) node);
			}
			else if (node instanceof AttributeList) {
				visitAttributeList((AttributeList) node);
			}
			else if (node instanceof Attribute) {
				visitAttribute((Attribute) node);
			}
			else if (node instanceof CMBasicNode) {
				visitReference((CMBasicNode) node);
			}
			else if (node instanceof ParameterEntityReference) {
				visitExternalParameterEntityReference((ParameterEntityReference) node);
			}
		}
	}

	public void visitAttribute(Attribute attr) {
		super.visitAttribute(attr);
		if (isParmEntity) {
			// check the attr name and the attr type to see if it
			// needs updating
			if (attr.getName().equals(oldRefName)) {
				attr.setName(requestor, newRefName);
				references.add(attr);
			}
			if (attr.getType().equals(oldRefName)) {
				attr.setType(requestor, newRefName);
				references.add(attr);
			}
		}
		else if (isNotation && attr.getType().equals(Attribute.ENUMERATED_NOTATION)) {
			AttributeEnumList enumList = attr.getEnumList();
			List items = enumList.getItems();
			boolean updateNeeded = false;
			for (int i = 0; i < items.size(); i++) {
				String notationName = (String) items.get(i);
				if (notationName.equals(oldRefName)) {
					updateNeeded = true;
					items.set(i, newName);
				}
			}
			if (updateNeeded) {
				String[] newItems = new String[items.size()];

				enumList.setItems((String[]) items.toArray(newItems));
			}
		}

	}

	public void visitAttributeList(AttributeList attList) {
		if (!isNotation && attList.getName().equals(oldRefName)) {
			attList.setName(requestor, newRefName);
			references.add(attList);
		}
		super.visitAttributeList(attList);
	}

	public void visitElement(Element element) {
		if (isParmEntity) {
			if (element.getName().equals(oldRefName)) {
				element.setName(requestor, newRefName);
				references.add(element);
			}
		}
		super.visitElement(element);
	}

	public void visitExternalParameterEntityReference(ParameterEntityReference parmEntityRef) {
		super.visitExternalParameterEntityReference(parmEntityRef);
		if (parmEntityRef.getName().equals(oldRefName)) {
			parmEntityRef.setReferencedEntity(requestor, newName);
			references.add(parmEntityRef);
		}
	}

	public void visitReference(CMBasicNode node) {
		super.visitReference(node);
		if (isParameterEntityRef(oldRefName) && !isParmEntity) {
			return;
		}

		if (node.getName().equals(oldRefName)) {
			node.setName(requestor, newRefName);
			references.add(node);
		}
	}
}
