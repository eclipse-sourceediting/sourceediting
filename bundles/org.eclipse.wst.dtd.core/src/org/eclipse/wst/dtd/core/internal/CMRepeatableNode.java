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

import org.eclipse.wst.dtd.core.internal.parser.DTDRegionTypes;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


public abstract class CMRepeatableNode extends CMNode {

	public static final char ONCE = '1';
	public static final char ONE_OR_MORE = '+';
	public static final char OPTIONAL = '?';
	public static final char ZERO_OR_MORE = '*';


	public CMRepeatableNode(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode);
	}

	public String getImagePath() {
		return DTDResource.ELEMENTREFICON;
	}

	public char getOccurrence() {
		ITextRegion occurRegion = getOccurrenceRegion();
		if (occurRegion != null && occurRegion.getType() == DTDRegionTypes.OCCUR_TYPE) {
			return getStructuredDTDDocumentRegion().getText(occurRegion).charAt(0);
		}
		return CMRepeatableNode.ONCE;
	}

	// returns the occurrenceregion, or the last region where the occurrence
	// region should appear after
	abstract public ITextRegion getOccurrenceRegion();

	public void setOccurrence(char occurrence) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_CM_REP_NODE_CHG_OCCUR); //$NON-NLS-1$
		setOccurrence(this, occurrence);
		endRecording(this);
	}

	public void setOccurrence(Object requestor, char occurrence) {
		if (getOccurrence() != occurrence) {
			ITextRegion region = getOccurrenceRegion();
			if (region != null) {
				if (region.getType().equals(DTDRegionTypes.OCCUR_TYPE)) {
					if (occurrence == CMRepeatableNode.ONCE) {
						// we need to remove the occur region from the flat
						// model;
						getDTDFile().getStructuredDocument().replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(region), 1, ""); //$NON-NLS-1$
					}
					else {
						// Region oldOccur = region.createCopy();
						getDTDFile().getStructuredDocument().replaceText(requestor, getStructuredDTDDocumentRegion().getStartOffset(region), 1, String.valueOf(occurrence));
						// changeStructuredDocument(oldOccur, region);
					}
				}
				else if (occurrence != CMRepeatableNode.ONCE) {
					// System.out.println(getString());
					// we need to create an occurrenceRegion
					replaceText(requestor, getStructuredDTDDocumentRegion().getEndOffset(region), 0, String.valueOf(occurrence));
				}
			}
		}
	}

}// CMRepeatableNode
