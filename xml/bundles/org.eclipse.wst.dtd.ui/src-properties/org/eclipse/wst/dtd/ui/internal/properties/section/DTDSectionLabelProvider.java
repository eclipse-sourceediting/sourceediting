/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
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

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.dtd.core.internal.Attribute;
import org.eclipse.wst.dtd.core.internal.AttributeList;
import org.eclipse.wst.dtd.core.internal.CMBasicNode;
import org.eclipse.wst.dtd.core.internal.CMGroupNode;
import org.eclipse.wst.dtd.core.internal.CMRepeatableNode;
import org.eclipse.wst.dtd.core.internal.Comment;
import org.eclipse.wst.dtd.core.internal.DTDFile;
import org.eclipse.wst.dtd.core.internal.DTDNode;
import org.eclipse.wst.dtd.core.internal.Element;
import org.eclipse.wst.dtd.core.internal.Entity;
import org.eclipse.wst.dtd.core.internal.Notation;
import org.eclipse.wst.dtd.core.internal.ParameterEntityReference;
import org.eclipse.wst.dtd.ui.internal.DTDPropertiesMessages;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;

public class DTDSectionLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object object) {
		if (object == null || object.equals(StructuredSelection.EMPTY)) {
			return null;
		}

		Image result = null;

		Object selected = object;
		if (object instanceof IStructuredSelection) {
			selected = ((IStructuredSelection) object).getFirstElement();
		}
		if (selected instanceof DTDFile) {
			return null; // ((DTDFile) selected).getImage();
		}
		else if (selected instanceof DTDNode) {
			if (selected instanceof ParameterEntityReference)
				return null;
			final String imgPath = ((DTDNode) selected).getImagePath();
			return imgPath != null ? DTDUIPlugin.getDefault().getImage(imgPath) : null;
		}
		else if (selected instanceof org.w3c.dom.Element) {
			return null;
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object object) {
		if (object == null || object.equals(StructuredSelection.EMPTY)) {
			return null;
		}

		Object selected = object;
		if (object instanceof IStructuredSelection) {
			selected = ((IStructuredSelection) object).getFirstElement();
		}

		if (selected instanceof DTDFile) {
			return ((DTDFile) selected).getName();
		}
		else if (selected instanceof CMBasicNode) {
			if (((CMBasicNode) selected).isReference())
				return DTDPropertiesMessages._UI_PROPERTIES_VIEW_TITLE_ELEMENT_REF;
			else
				return ((CMBasicNode) selected).getName();
		}
		else if (selected instanceof CMRepeatableNode) {
			CMRepeatableNode node = (CMRepeatableNode) selected;
			String name = node.getName();
			if (node instanceof CMGroupNode) {
				if (((CMGroupNode) node).getConnector() == CMGroupNode.CHOICE)
					name = DTDPropertiesMessages.DTDSectionLabelProvider_0;
				else
					name = DTDPropertiesMessages.DTDSectionLabelProvider_1;
			}
			char occurrence = node.getOccurrence();
			switch (occurrence) {
				case CMRepeatableNode.ONCE :
					name += " [1..1]"; //$NON-NLS-1$
					break;
				case CMRepeatableNode.OPTIONAL :
					name += " [0..1]"; //$NON-NLS-1$
					break;
				case CMRepeatableNode.ONE_OR_MORE :
					name += " [1..*]"; //$NON-NLS-1$
					break;
				case CMRepeatableNode.ZERO_OR_MORE :
					name += " [0..*]"; //$NON-NLS-1$
					break;
			}
			return name;
		}
		else if (selected instanceof DTDNode) {
			if (selected instanceof Element)
				return DTDPropertiesMessages.DTDSectionLabelProvider_6;
			else if (selected instanceof Attribute)
				return DTDPropertiesMessages.DTDSectionLabelProvider_7;
			else if (selected instanceof AttributeList)
				return DTDPropertiesMessages.DTDSectionLabelProvider_8;
			else if (selected instanceof Comment)
				return DTDPropertiesMessages.DTDSectionLabelProvider_9;
			else if (selected instanceof Entity)
				return DTDPropertiesMessages.DTDSectionLabelProvider_10;
			else if (selected instanceof Notation)
				return DTDPropertiesMessages.DTDSectionLabelProvider_11;
			else if (selected instanceof ParameterEntityReference)
				// return "parameter entity reference";
				return null;
			else
				return ((DTDNode) selected).getName();
		}
		else if (selected instanceof org.w3c.dom.Element) {
			return ((org.w3c.dom.Element) selected).getLocalName();
		}
		else if (object instanceof ITextSelection) {
			return ""; //$NON-NLS-1$
		}
		else
			return object.toString();
	}
}