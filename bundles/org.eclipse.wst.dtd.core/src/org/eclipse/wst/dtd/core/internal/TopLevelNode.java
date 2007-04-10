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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


// interface for nodes that can exist at the top level in a dtdfile
// eg. entity, notation, element, comment, attlist, or unrecognized stuff (ie
// <junk dkfjdl>
public abstract class TopLevelNode extends DTDNode {

	private ArrayList flatNodes = new ArrayList();

	public TopLevelNode(DTDFile dtdFile, IStructuredDocumentRegion flatNode) {
		super(dtdFile, flatNode);
		flatNodes.add(flatNode);
	}

	public void addWhitespaceStructuredDocumentRegion(IStructuredDocumentRegion node) {
		flatNodes.add(node);
	}

	// specialize this so we delete the objects flat node range
	// AND any whitespace
	public void delete() {
		beginRecording(getDTDFile(), DTDCoreMessages._UI_LABEL_TOP_LEVEL_NODE_DELETE); //$NON-NLS-1$
		IStructuredDocumentRegion first = (IStructuredDocumentRegion) flatNodes.get(0);
		IStructuredDocumentRegion last = (IStructuredDocumentRegion) flatNodes.get(flatNodes.size() - 1);
		int startOffset = first.getStartOffset();
		int endOffset = last.getEndOffset();

		replaceText(getDTDFile(), startOffset, endOffset - startOffset, ""); //$NON-NLS-1$
		endRecording(getDTDFile());
	}

	public ITextRegion getEndRegion() {
		int size = flatNode.getRegions().size();
		if (size > 0) {
			return flatNode.getRegions().get(size - 1);
		}
		return null;
	}

	// includes what gettext gives us plus any whitespace
	// trailing it
	public String getFullText() {
		StringBuffer sb = new StringBuffer();
		Iterator iter = flatNodes.iterator();
		while (iter.hasNext()) {
			IStructuredDocumentRegion fNode = (IStructuredDocumentRegion) iter.next();
			sb.append(fNode.getText());
		}
		return sb.toString();
	}

	public ITextRegion getStartRegion() {
		if (flatNode.getRegions().size() > 0) {
			return flatNode.getRegions().get(0);
		}
		return null;
	}

	public RegionIterator iterator() {
		// System.out.println("create region iter " + this.getClass() + " with
		// start , end = " + getStartOffset() + ", " +getEndOffset());
		return new RegionIterator(flatNode, getStartOffset(), getEndOffset());
	}
}
