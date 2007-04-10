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
package org.eclipse.wst.xml.core.internal.parser;

import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.text.StructuredDocumentReParser;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


public class XMLStructuredDocumentReParser extends StructuredDocumentReParser {

	public XMLStructuredDocumentReParser() {
		super();
	}

	protected IStructuredDocumentRegion findDirtyEnd(int end) {
		// Caution: here's one place we have to cast
		IStructuredDocumentRegion result = fStructuredDocument.getRegionAtCharacterOffset(end);
		// if not well formed, get one past, if there is something there
		if ((result != null) && (!result.isEnded())) {
			if (result.getNext() != null) {
				result = result.getNext();
			}
		}
		// also, get one past if exactly equal to the end (this was needed
		// as a simple fix to when a whole exact region is deleted.
		// there's probably a better way.
		if ((result != null) && (end == result.getEnd())) {
			if (result.getNext() != null) {
				result = result.getNext();
			}
		}

		// 12/6/2001 - Since we've changed the parser/scanner to allow a lone
		// '<' without
		// always interpretting it as start of a tag name, we need to be a
		// little fancier, in order
		// to "skip" over any plain 'ol content between the lone '<' and any
		// potential meating
		// regions past plain 'ol content.
		if (isLoneOpenFollowedByContent(result) && (result.getNext() != null)) {
			result = result.getNext();
		}

		if (result != null)
			fStructuredDocument.setCachedDocumentRegion(result);
		dirtyEnd = result;

		return dirtyEnd;
	}

	protected void findDirtyStart(int start) {
		IStructuredDocumentRegion result = fStructuredDocument.getRegionAtCharacterOffset(start);
		// heuristic: if the postion is exactly equal to the start, then
		// go back one more, if it exists. This prevents problems with
		// insertions
		// of text that should be merged with the previous node instead of
		// simply hung
		// off of it as a separate node (ex.: XML content inserted right
		// before an open
		// bracket should become part of the previous content node)
		if (result != null) {
			IStructuredDocumentRegion previous = result.getPrevious();
			if ((previous != null) && ((!(previous.isEnded())) || (start == result.getStart()))) {
				result = previous;
			}
			// If we are now at the end of a "tag dependent" content area (or
			// JSP area)
			// then we need to back up all the way to the beginning of that.
			IStructuredDocumentRegion potential = result;
			while (isPartOfBlockRegion(potential)) {
				potential = potential.getPrevious();
			}
			if (potential != null) {
				result = potential;
				fStructuredDocument.setCachedDocumentRegion(result);
			}
		}
		dirtyStart = result;
	}

	/**
	 * The rule is, that is we are content, preceded by lone <, then we need
	 * to advance one more for dirty end.
	 */
	protected boolean isLoneOpenFollowedByContent(IStructuredDocumentRegion flatNode) {
		boolean result = false;
		String type = flatNode.getType();
		if (type == DOMRegionContext.XML_CONTENT) {
			IStructuredDocumentRegion previous = flatNode.getPrevious();
			String previousType = null;
			if (previous != null) {
				previousType = previous.getType();
			}
			if (previousType != null) {
				result = (previousType == DOMRegionContext.XML_TAG_OPEN);
			}
		}
		return result;
	}

	protected boolean isPartOfBlockRegion(IStructuredDocumentRegion flatNode) {
		boolean result = false;
		String type = flatNode.getType();
		result = (type == DOMRegionContext.BLOCK_TEXT);
		return result;
	}

	public IStructuredTextReParser newInstance() {
		return new XMLStructuredDocumentReParser();
	}

}
