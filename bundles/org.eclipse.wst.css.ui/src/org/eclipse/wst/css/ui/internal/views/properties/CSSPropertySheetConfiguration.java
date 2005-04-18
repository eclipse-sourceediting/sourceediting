/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
/*
 * Created on Jan 23, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.css.ui.internal.views.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.css.core.internal.provisional.document.ICSSNode;
import org.eclipse.wst.sse.ui.internal.provisional.views.properties.StructuredPropertySheetConfiguration;

public class CSSPropertySheetConfiguration extends StructuredPropertySheetConfiguration {
	public CSSPropertySheetConfiguration() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.views.properties.StructuredPropertySheetConfiguration#getSelection(org.eclipse.jface.viewers.ISelection,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public ISelection getSelection(IWorkbenchPart selectingPart, ISelection selection) {
		ISelection preferredSelection = super.getSelection(selectingPart, selection);
		if (preferredSelection instanceof IStructuredSelection) {
			Object[] objects = ((IStructuredSelection) preferredSelection).toArray();
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] instanceof ICSSNode) {
					ICSSNode node = (ICSSNode) objects[i];
					while (node.getNodeType() == ICSSNode.PRIMITIVEVALUE_NODE || node.getNodeType() == ICSSNode.STYLEDECLITEM_NODE) {
						node = node.getParentNode();
						objects[i] = node;

					}
				}
			}
			preferredSelection = new StructuredSelection(objects);
		}
		return preferredSelection;
	}
}