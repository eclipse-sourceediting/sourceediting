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
package org.eclipse.wst.xml.ui.views.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.sse.ui.views.properties.StructuredPropertySheetConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


public class XMLPropertySheetConfiguration extends StructuredPropertySheetConfiguration {

	/**
	 *  
	 */
	public XMLPropertySheetConfiguration() {
		super();
	}

	/**
	 * @see org.eclipse.wst.sse.ui.views.properties.PropertySheetConfiguration#getSelection(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public ISelection getSelection(IWorkbenchPart selectingPart, ISelection selection) {
		// On Attr nodes, select the owner Element. On Text nodes, select the
		// parent Element.
		ISelection preferredSelection = selection;
		if (selection instanceof ITextSelection) {
			// on text selection, find the appropriate Node
			ITextSelection textSel = (ITextSelection) selection;
			if (getModel() != null) {
				Object inode = getModel().getIndexedRegion(textSel.getOffset());
				if (inode instanceof Node) {
					Node node = (Node) inode;
					// replace Attribute Node with its owner
					if (node.getNodeType() == Node.ATTRIBUTE_NODE)
						inode = ((Attr) node).getOwnerElement();
					// replace Text Node with its parent
					else if ((node.getNodeType() == Node.TEXT_NODE || (node.getNodeType() == Node.CDATA_SECTION_NODE)) && node.getParentNode() != null) {
						inode = node.getParentNode();
					}
				}
				if (inode != null) {
					List inputList = new ArrayList(1);
					inputList.add(inode);
					preferredSelection = new StructuredSelection(inputList);
				}
			}
		} else if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSel = (IStructuredSelection) selection;
			if (getModel() != null) {
				List inputList = new ArrayList(structuredSel.toList());
				for (int i = 0; i < inputList.size(); i++) {
					Object inode = inputList.get(i);
					if (inode instanceof Node) {
						Node node = (Node) inputList.get(i);
						// replace Attribute Node with its owner
						if (node.getNodeType() == Node.ATTRIBUTE_NODE)
							inputList.set(i, ((Attr) node).getOwnerElement());
						// replace Text Node with its parent
						else if ((node.getNodeType() == Node.TEXT_NODE || (node.getNodeType() == Node.CDATA_SECTION_NODE)) && node.getParentNode() != null) {
							inputList.set(i, node.getParentNode());
						}
					}
				}
				preferredSelection = new StructuredSelection(inputList);
			}
		}
		return preferredSelection;
	}
}
