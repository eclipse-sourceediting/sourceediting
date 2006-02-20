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

import org.eclipse.jface.text.TextSelection;
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

public class DTDSectionLabelProvider extends LabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object object) {
		if (object == null || object.equals(StructuredSelection.EMPTY)) {
			return null;
		}

		Image result = null;
		if (object instanceof StructuredSelection) {
			Object selected = ((StructuredSelection) object).getFirstElement();
			if (selected instanceof DTDFile) {
				return null; // ((DTDFile) selected).getImage();
			}
			else if (selected instanceof DTDNode) {
				if (selected instanceof ParameterEntityReference)
					return null;
				return ((DTDNode) selected).getImage();
			}
			else if (selected instanceof org.w3c.dom.Element) {
				return null;
			}
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

		if (object instanceof StructuredSelection) {
			Object selected = ((StructuredSelection) object).getFirstElement();
			if (selected instanceof DTDFile) {
				return null; // ((DTDFile) selected).getName();
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
						name = "choice";
					else
						name = "sequence";
				}
				char occurrence = node.getOccurrence();
				switch (occurrence) {
					case CMRepeatableNode.ONCE :
						name += " [1..1]";
						break;
					case CMRepeatableNode.OPTIONAL :
						name += " [0..1]";
						break;
					case CMRepeatableNode.ONE_OR_MORE :
						name += " [1..*]";
						break;
					case CMRepeatableNode.ZERO_OR_MORE :
						name += " [0..*]";
						break;
				}
				return name;
			}
			else if (selected instanceof DTDNode) {
				if (selected instanceof Element)
					return "element";
				else if (selected instanceof Attribute)
					return "attribute";
				else if (selected instanceof AttributeList)
					return "attribute list";
				else if (selected instanceof Comment)
					return "comment";
				else if (selected instanceof Entity)
					return "entity";
				else if (selected instanceof Notation)
					return "notation";
				else if (selected instanceof ParameterEntityReference)
					// return "parameter entity reference";
					return null;
				else
					return ((DTDNode) selected).getName();
			}
			else if (selected instanceof org.w3c.dom.Element) {
				return ((org.w3c.dom.Element) selected).getLocalName();
			}
			else
				return "";
		}
		else if (object instanceof TextSelection) {
			return "";
		}
		else
			return object.toString();
	}
}