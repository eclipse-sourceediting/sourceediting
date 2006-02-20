/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.properties.section;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.properties.internal.provisional.ISection;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.core.internal.Notation;

public class DocumentSectionDescriptor extends AbstractSectionDescriptor {
	/**
	 * 
	 */
	public DocumentSectionDescriptor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getId()
	 */
	public String getId() {
		return "org.eclipse.wst.dtd.ui.internal.section.document";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getInputTypes()
	 */
	public List getInputTypes() {
		List list = new ArrayList();
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getSectionClass()
	 */
	public ISection getSectionClass() {
		return new DocumentSection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#getTargetTab()
	 */
	public String getTargetTab() {
		return "org.eclipse.wst.dtd.ui.internal.documentation";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISectionDescriptor#appliesTo(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public boolean appliesTo(IWorkbenchPart part, ISelection selection) {
		Object object = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			object = structuredSelection.getFirstElement();
			/*
			 * if (object instanceof DTDNode) { DTDNode node =
			 * (DTDNode)object; Iterator iterator =
			 * node.getDTDFile().getNodes().iterator(); DTDNode currentNode =
			 * null; DTDNode prevNode = null; while (iterator.hasNext()) {
			 * currentNode = (DTDNode)iterator.next(); if (node == currentNode &&
			 * prevNode != null && prevNode instanceof Comment) return true;
			 * else prevNode = currentNode; }
			 * 
			 * return false; }
			 */
			if (object instanceof Element || object instanceof Entity || object instanceof AttributeList || object instanceof Notation)
				return true;
			else
				return false;
		}
		return false;
	}
}
