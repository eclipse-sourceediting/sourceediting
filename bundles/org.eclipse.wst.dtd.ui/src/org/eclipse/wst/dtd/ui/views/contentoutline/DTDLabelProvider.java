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
package org.eclipse.wst.dtd.ui.views.contentoutline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.dtd.core.DTDFile;
import org.eclipse.wst.dtd.core.DTDNode;
import org.eclipse.wst.dtd.core.NodeList;

/**
 */

public class DTDLabelProvider extends LabelProvider {

	public DTDLabelProvider() {
		super();
	}

	/**
	 * Returns the image for the label of the given element.
	 *
	 * @param element the element for which to provide the label image
	 * @return the image used to label the element, or <code>null</code>
	 *   if these is no image for the given object
	 */
	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof DTDNode) {
			image = ((DTDNode) element).getImage();
		}
		else if (element instanceof NodeList) {
			image = ((NodeList) element).getImage();
		}
		else if (element instanceof IndexedNodeList) {
			return ((IndexedNodeList) element).getTarget().getImage();
		}
		else if (element instanceof DTDFile) {
			image = ((DTDFile) element).getImage();
		}
		else {
			image = super.getImage(element);
		}
		return image;
	}

	/**
	 * Returns the text for the label of the given element.
	 *
	 * @param element the element for which to provide the label text
	 * @return the text string used to label the element, or <code>null</code>
	 *   if these is no text label for the given object
	 */
	public String getText(Object element) {
		if (element instanceof DTDNode) {
			String name = ((DTDNode) element).getName();

			// strip leading whitespace (useful for multi-line comments)
			int i = 0;
			for (i = 0; i < name.length(); i++) {
				if (!Character.isWhitespace(name.charAt(i)))
					break;
			}
			if (i > 0 && i < name.length() - 1)
				name = name.substring(i);

			return name;
		}
		else if (element instanceof NodeList) {
			return ((NodeList) element).getListType();
		}
		else if (element instanceof IndexedNodeList) {
			return ((IndexedNodeList) element).getTarget().getName();
		}
		else if (element instanceof DTDFile) {
			return ((DTDFile) element).getName();
		}
		return super.getText(element);
	}
}
