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
package org.eclipse.wst.dtd.core.internal;

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


// interface for nodes that can exist at the top level in a dtdfile
// eg. entity, notation, element, comment, attlist, or unrecognized stuff (ie
// <junk dkfjdl>
public abstract class NamedTopLevelNode extends TopLevelNode {

	private String tagStartType;

	public NamedTopLevelNode(DTDFile dtdFile, IStructuredDocumentRegion flatNode, String type) {
		super(dtdFile, flatNode);
		tagStartType = type;
	}

	public ITextRegion getNameRegion() {
		// name region is located after the whitespace (which is after
		// the elementtag
		RegionIterator iter = iterator();
		getNextRegion(iter, tagStartType);

		if (!iter.hasNext()) {
			return null;
		}

		ITextRegion region = iter.next();
		if (!region.getType().equals(DTDRegionTypes.WHITESPACE)) {
			return null;
		}

		if (!iter.hasNext()) {
			return null;
		}

		region = iter.next();
		if (region.getType().equals(DTDRegionTypes.NAME)) {
			return region;
		}

		// we normally stop here, but for entities, we have to see if we are
		// at a '%'. if so, we skip that and find the name after the
		// whitespace again
		if (tagStartType == DTDRegionTypes.ENTITY_TAG && region.getType().equals(DTDRegionTypes.PERCENT) && iter.hasNext()) {
			region = iter.next();

			if (!region.getType().equals(DTDRegionTypes.WHITESPACE)) {
				return null;
			}

			if (!iter.hasNext()) {
				return null;
			}

			region = iter.next();
			if (region.getType().equals(DTDRegionTypes.NAME)) {
				return region;
			}
		}

		return null;
	}

	public ITextRegion getStartTag(RegionIterator iter) {
		return getNextRegion(iter, tagStartType);
	}

	public ITextRegion getWhitespaceAfterName() {
		ITextRegion nameRegion = getNameRegion();
		RegionIterator iter = iterator();
		// skip past the element tag region
		getNextRegion(iter, tagStartType);
		boolean foundName = false;
		while (iter.hasNext()) {
			ITextRegion region = iter.next();
			if (!foundName && nameRegion != null && region == nameRegion) {
				foundName = true;
			}

			if (region.getType().equals(DTDRegionTypes.WHITESPACE)) {
				// there is no name region or we have already passed it
				if (nameRegion == null || foundName) {
					return region;
				}
			}
		}
		return null;
	}

	public void setName(Object requestor, String name) {
		ITextRegion nameRegion = getNameRegion();
		if (nameRegion != null) {
			super.setName(requestor, name);
		}
		else {
			RegionIterator iter = iterator();
			ITextRegion elementTagRegion = getNextRegion(iter, tagStartType);
			int replaceLength = 0;
			if (iter.hasNext()) {
				ITextRegion region = iter.next();
				if (region.getType().equals(DTDRegionTypes.WHITESPACE)) {
					if (region.getLength() >= 2) {
						// there are 2 spaces between 'ELEMENT' and the
						// content
						// Change replace length to 1 so that the new name and
						// the content are separated by a single space
						replaceLength = 1;
					}
				}
			}

			// beginRecording(requestor, "Name Change");
			String newText = " " + name; //$NON-NLS-1$
			replaceText(requestor, getStructuredDTDDocumentRegion().getEndOffset(elementTagRegion), replaceLength, newText);
			// endRecording(requestor);
		}
	}

}
