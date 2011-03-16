/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
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
package org.eclipse.wst.dtd.ui.views.contentoutline;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.DTDResource;
import org.eclipse.wst.dtd.core.internal.NodeList;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;

class DTDLabelProvider extends LabelProvider {

	public DTDLabelProvider() {
		super();
	}

	/**
	 * Returns the image for the label of the given element.
	 * 
	 * @param element
	 *            the element for which to provide the label image
	 * @return the image used to label the element, or <code>null</code> if
	 *         these is no image for the given object
	 */
	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof DTDNode) {
			final String imgPath = ((DTDNode) element).getImagePath();
			image = imgPath != null ? DTDUIPlugin.getDefault().getImage(imgPath) : null;
		}
		else if (element instanceof NodeList) {
			final String imgPath = ((NodeList) element).getImagePath();
			image = imgPath != null ? DTDUIPlugin.getDefault().getImage(imgPath) : null;
		}
		else if (element instanceof DTDFile) {
			image = DTDUIPlugin.getDefault().getImage(DTDResource.DTDFILEICON);
		}
		else {
			image = super.getImage(element);
		}
		return image;
	}

	/**
	 * Returns the text for the label of the given element.
	 * 
	 * @param element
	 *            the element for which to provide the label text
	 * @return the text string used to label the element, or <code>null</code>
	 *         if these is no text label for the given object
	 */
	public String getText(Object element) {
		if (element instanceof DTDNode) {
			String name = ((DTDNode) element).getName();

			// strip leading whitespace (useful for multi-line comments)
			int firstSignificantCharacter = 0;
			int lastVisibleCharacter = name.length() - 1;
			for (firstSignificantCharacter = 0; firstSignificantCharacter < name.length(); firstSignificantCharacter++) {
				if (!Character.isWhitespace(name.charAt(firstSignificantCharacter)))
					break;
			}
			// keep only the first line of text in a multi-line name
			if (firstSignificantCharacter < lastVisibleCharacter) {
				for (lastVisibleCharacter = firstSignificantCharacter + 1; lastVisibleCharacter < name.length(); lastVisibleCharacter++) {
					char character = name.charAt(lastVisibleCharacter);
					if (character == '\r' || character == '\n')
						break;
				}
			}
			if (firstSignificantCharacter > 0 && firstSignificantCharacter < name.length() - 1) {
				name = name.substring(firstSignificantCharacter, lastVisibleCharacter);
			}

			return name;
		}
		else if (element instanceof NodeList) {
			// return ((NodeList) element).getListType();
			return ((NodeList) element).getName();
		}
		else if (element instanceof DTDFile) {
			return ((DTDFile) element).getName();
		}
		return super.getText(element);
	}
}
