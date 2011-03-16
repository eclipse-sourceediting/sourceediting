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
package org.eclipse.wst.dtd.core.internal;

import java.util.ArrayList;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


public class AttributeList extends NamedTopLevelNode {
	public AttributeList(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode, DTDRegionTypes.ATTLIST_TAG);
	}

	public void addAttribute(String name) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_ATTR_LIST_ADD); //$NON-NLS-1$

		DTDNode lastAttribute = (DTDNode) getLastChild();
		if (lastAttribute != null) {
			replaceText(this, lastAttribute.getEndOffset(), 0, "\n\t" + name + " CDATA #IMPLIED"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			ITextRegion nameRegion = getNameRegion();
			if (nameRegion != null) {
				replaceText(this, getStructuredDTDDocumentRegion().getEndOffset(nameRegion), 0, "\n\t" + name + " CDATA #IMPLIED"); //$NON-NLS-1$ //$NON-NLS-2$
			}

		}

		endRecording(this);
	}

	public String getImagePath() {
		return DTDResource.ATTRIBUTELISTICON;
	}

	public void insertIntoModel(Object requestor, Attribute reference, Attribute node, boolean isAfter) {
		int offset = 0;
		String newText = ""; //$NON-NLS-1$
		String nodeText = node.getFullNodeText();
		if (!isAfter) {
			offset = reference.getStartOffset();
		}
		else {
			// try and get next child
			Attribute attr = (Attribute) reference.getNextSibling();
			if (attr != null) {
				offset = attr.getStartOffset();
			}
			else {
				// just use the end offset
				offset = reference.getWhitespaceEndOffset();
			}
		}
		newText += nodeText;// + (isLastChild ? "\n" : "\n\t");
		if (!node.hasTrailingWhitespace()) {
			newText += "\n\t"; //$NON-NLS-1$
		}
		replaceText(requestor, offset, 0, newText);
	}

	public void resolveRegions() {
		removeChildNodes();
		RegionIterator iter = iterator();

		if (getNameRegion() != null) {
			// we skip past the name token is our name
			skipPastName(iter);
		}

		ArrayList children = new ArrayList();
		Attribute attribute = null;
		boolean trailingWhitespace = false;
		while (iter.hasNext()) {
			ITextRegion currentRegion = iter.next();
			if (currentRegion.getType().equals(DTDRegionTypes.ATTRIBUTE_NAME)) {
				attribute = new Attribute(getDTDFile(), getStructuredDTDDocumentRegion());
				children.add(attribute);
				appendChild(attribute);
				trailingWhitespace = false;
			}
			if (attribute != null && currentRegion.getType() != DTDRegionTypes.END_TAG) {
				if (!trailingWhitespace) {
					attribute.addRegion(currentRegion);
				}
				else {
					if (currentRegion.getType() == DTDRegionTypes.WHITESPACE) {
						attribute.addWhitespaceRegion(currentRegion);
					}
				}

				// the following prevents extra whitespace from being picked
				// up by the attribute
				if (currentRegion.getType() == DTDRegionTypes.REQUIRED_KEYWORD || currentRegion.getType() == DTDRegionTypes.IMPLIED_KEYWORD || currentRegion.getType() == DTDRegionTypes.SINGLEQUOTED_LITERAL || currentRegion.getType() == DTDRegionTypes.DOUBLEQUOTED_LITERAL) {
					trailingWhitespace = true;
				}
			}
		}
		int numKids = children.size();
		for (int i = 0; i < numKids; i++) {
			((Attribute) children.get(i)).resolveRegions();
		} // end of for ()
	}
}
