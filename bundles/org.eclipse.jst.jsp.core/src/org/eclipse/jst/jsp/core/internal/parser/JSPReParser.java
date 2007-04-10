/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.parser;


import java.util.Iterator;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP11TLDNames;
import org.eclipse.jst.jsp.core.internal.provisional.JSP12Namespace;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.StructuredDocumentRegionParser;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegionList;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.text.CoreNodeList;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.xml.core.internal.parser.XMLStructuredDocumentReParser;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

public class JSPReParser extends XMLStructuredDocumentReParser {

	/**
	 * Allow a reparser to check for extra syntactic cases that require
	 * parsing beyond the flatNode boundary.
	 * 
	 * This implementation adds JSP language markers (comments are handled
	 * elsewhere).
	 */
	protected StructuredDocumentEvent checkForCrossStructuredDocumentRegionSyntax() {
		StructuredDocumentEvent result = super.checkForCrossStructuredDocumentRegionSyntax();
		// None of the superclass' cases were valid, so check for JSP cases
		if (result == null) {
			result = checkForJSP();
		}
		return result;
	}

	/**
	 * A change to a JSP tag can result in all being reparsed.
	 */
	private StructuredDocumentEvent checkForJSP() {
		StructuredDocumentEvent result = null;
		result = checkForCriticalKey("<%"); //$NON-NLS-1$
		if (result == null)
			result = checkForCriticalKey("<%="); //$NON-NLS-1$
		if (result == null)
			result = checkForCriticalKey("<%!"); //$NON-NLS-1$
		if (result == null)
			result = checkForCriticalKey("%>"); //$NON-NLS-1$

		return result;
	}

	/**
	 * If a comment start or end tag is being added or deleted, we'll rescan
	 * the whole document. The reason is that content that is revealed or
	 * commented out can effect the interpretation of the rest of the
	 * document. Note: for now this is very XML/JSP specific, can
	 * refactor/improve later.
	 */
	protected StructuredDocumentEvent checkForComments() {

		StructuredDocumentEvent result = super.checkForComments();

		if (result == null)
			result = checkForCriticalKey("<%--"); //$NON-NLS-1$
		if (result == null)
			result = checkForCriticalKey("--%>"); //$NON-NLS-1$
		// we'll also check for these degenerate cases
		if (result == null)
			result = checkForCriticalKey("<%---%>"); //$NON-NLS-1$

		return result;
	}

	/**
	 * The core reparsing method ... after the dirty start and dirty end have
	 * been calculated elsewhere. - this method overrides, does not extend
	 * super's method. changes/fixes to super may have to be made here as
	 * well.
	 */
	protected StructuredDocumentEvent reparse(IStructuredDocumentRegion dirtyStart, IStructuredDocumentRegion dirtyEnd) {
		StructuredDocumentEvent result = null;
		int rescanStart = -1;
		int rescanEnd = -1;
		boolean firstTime = false;
		boolean detectedBreakingChange = false;

		//
		// "save" the oldNodes (that may be replaced) in a list
		CoreNodeList oldNodes = formOldNodes(dirtyStart, dirtyEnd);

		if (containsBreakingChange(oldNodes) || isBreakingWithNestedTag(dirtyStart, dirtyEnd)) {
			if (Debug.debugTaglibs)
				System.out.println("reparse: is taglib or include"); //$NON-NLS-1$
			detectedBreakingChange = true;
			rescanStart = 0;
			rescanEnd = fStructuredDocument.getLength() + fLengthDifference;
			oldNodes = formOldNodes(fStructuredDocument.getFirstStructuredDocumentRegion(), fStructuredDocument.getLastStructuredDocumentRegion());
			clearTaglibInfo();
		}
		else if (dirtyStart == null || dirtyEnd == null) {
			// dirtyStart or dirty end are null, then that means we didn't
			// have a
			// cached node, which means we have an empty document, so we
			// just need to rescan the changes
			rescanStart = 0;
			rescanEnd = fChanges.length();
			firstTime = true;
		}
		else {
			// set the start of the text to rescan
			rescanStart = dirtyStart.getStart();
			//
			// set the end of the text to rescan
			// notice we use the same rationale as for the rescanStart,
			// with the added caveat that length has to be added to it,
			// to compensate for the new text which has been added or deleted.
			// If changes has zero length, then "length" will be negative,
			// since
			// we are deleting text. Otherwise, use the difference between
			// what's selected to be replaced and the length of the new text.
			rescanEnd = dirtyEnd.getEnd() + fLengthDifference;
		}

		// now that we have the old stuff "saved" away, update the document
		// with the changes.
		fStructuredDocument.updateDocumentData(fStart, fLengthToReplace, fChanges);
		// ------------------ now the real work
		result = core_reparse(rescanStart, rescanEnd, oldNodes, firstTime);
		//

		// if we did not detect a breaking type of change at the beginning,
		// but
		// do now, then reparse all! If we did detect them, then we may or may
		// not detect again, but presumably we've already set up to re-parsed
		// everthing, so no need to do again.
		if ((!detectedBreakingChange) && (containsBreakingChange(oldNodes))) {
			clearTaglibInfo();
			// reparse all
			oldNodes = formOldNodes(fStructuredDocument.getFirstStructuredDocumentRegion(), fStructuredDocument.getLastStructuredDocumentRegion());
			result = core_reparse(0, fStructuredDocument.getLength(), oldNodes, firstTime);
		}

		// event is returned to the caller, incase there is
		// some optimization they can do
		return result;
	}

	/**
	 * Verifies that the regions given, representing the contents of a
	 * IStructuredDocumentRegion, contain regions that could alter the
	 * behavior of the parser or the parsing of areas outside of the regions
	 * given.
	 */
	private boolean isBreakingChange(IStructuredDocumentRegion node, ITextRegionList regions) {
		return isTaglibOrInclude(node, regions) || isJspRoot(regions);
	}

	/**
	 * Verifies that the regions given, representing the regions touched by a
	 * text change have: 1) ...an insertion at the textEndOffset of an
	 * XML_TAG_OPEN that's in it's own IStructuredDocumentRegion and preceded
	 * by an unended IStructuredDocumentRegion 2) ...a deletion happening in
	 * an XML_EMPTY_TAG_CLOSE that ends a ITextRegionContainer 3) ...an
	 * insertion happening with a ' <' character somewhere in an XML attribute
	 * name or value 4) ...a deletion of a normal XML_TAG_CLOSE since
	 * subsequent tags become attribute values
	 */

	private boolean isBreakingWithNestedTag(boolean changesIncludeA_lt, boolean delsIncludeA_gt, IStructuredDocumentRegion parent, ITextRegion region) {
		boolean result = false;

		IStructuredDocumentRegion previous = parent.getPrevious();
		// case 1 test
		if (parent.getRegions().size() == 1 && region.getType() == DOMRegionContext.XML_TAG_OPEN && (previous == null || (!previous.isEnded() || previous.getType() == DOMRegionContext.XML_CONTENT))) {
			result = true;
		}
		// case 2 test
		if (region instanceof ITextRegionContainer) {
			ITextRegionContainer container = (ITextRegionContainer) region;
			ITextRegion internal = container.getRegions().get(container.getRegions().size() - 1);
			if (internal.getType() == DOMRegionContext.WHITE_SPACE && container.getRegions().size() >= 2)
				internal = container.getRegions().get(container.getRegions().size() - 2);
			if (internal.getType() == DOMRegionContext.XML_EMPTY_TAG_CLOSE) {
				result = true;
			}
		}
		// case 3 test
		if (changesIncludeA_lt && (region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME || region.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {
			result = true;
		}
		// case 4 test
		if (delsIncludeA_gt && region.getType() == DOMRegionContext.XML_TAG_CLOSE) {
			result = true;
		}
		return result;
	}

	/**
	 * Verifies that the regions given, representing the contents of a
	 * IStructuredDocumentRegion, includes a jsp:root tag
	 */
	private boolean isJspRoot(ITextRegionList regions) {
		return regions.size() > 1 && regions.get(0).getType() == DOMRegionContext.XML_TAG_OPEN && regions.get(1).getType() == DOMJSPRegionContexts.JSP_ROOT_TAG_NAME;
	}

	/**
	 * Verifies that the regions given, representing the contents of a
	 * IStructuredDocumentRegion, includes a valid taglib directive or include
	 * directive
	 */
	private boolean isTaglibOrInclude(IStructuredDocumentRegion node, ITextRegionList regions) {
		boolean sizeAndTypesMatch = (regions.size() > 1) && (regions.get(1).getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) && (regions.get(0).getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_OPEN || regions.get(0).getType() == DOMRegionContext.XML_TAG_OPEN);
		if (!sizeAndTypesMatch)
			return false;
		ITextRegion region = regions.get(1);
		String directiveName = node.getText(region);
		return sizeAndTypesMatch && (directiveName.equals(JSP11TLDNames.TAGLIB) || directiveName.equals(JSP11TLDNames.INCLUDE) || directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_TAGLIB) || directiveName.equals(JSP12Namespace.ElementName.DIRECTIVE_INCLUDE));
	}

	private void clearTaglibInfo() {
		if (Debug.debugTaglibs)
			System.out.println("clearing taglib info"); //$NON-NLS-1$
		RegionParser parser = fStructuredDocument.getParser();
		if (parser instanceof StructuredDocumentRegionParser)
			((StructuredDocumentRegionParser) parser).resetHandlers();
	}

	private boolean containsBreakingChange(IStructuredDocumentRegionList list) {
		boolean contains = false;
		for (int i = 0; i < list.getLength(); i++) {
			IStructuredDocumentRegion node = list.item(i);
			if (isBreakingChange(node, node.getRegions())) {
				contains = true;
				break;
			}
		}
		return contains;
	}

	protected IStructuredDocumentRegion findDirtyEnd(int end) {

		IStructuredDocumentRegion result = super.findDirtyEnd(end);

		// if not well formed, get one past, if its not null

		// now, if any of to-be-scanned flatnodes are the start of a jsp
		// region, we'll
		// reparse all the way to the end, to be sure we detect embedded
		// regions (or not-embedded regions) correctly.
		// notice we don't need to do if we're only processing one node.
		// notice too we have a strong assumption here that dirtyStart has
		// already been found!
		//
		// note that dirtyEnd is not checked in the do-block below, so we'll
		// check it first.
		if (isJSPEmbeddedStartOrEnd(result)) {
			result = fStructuredDocument.getLastStructuredDocumentRegion();
		}
		else {
			// when end node and start node are the same, we only need the
			// above
			// check, otherwise, there's a few cases that we'll search the
			// rest of the
			// flatnodes needlessly.
			if (result != dirtyStart) {
				IStructuredDocumentRegion searchNode = dirtyStart;
				do {
					if (isJSPEmbeddedStartOrEnd(searchNode)) {
						result = fStructuredDocument.getLastStructuredDocumentRegion();
						break;
					}
					else {
						searchNode = searchNode.getNext();
					}
					// if we get to the current dirty end, or end of
					// flatnodes, without finding JSP region then we
					// don't need to check further
				}
				while ((searchNode != result) && (searchNode != null));
			}
		}
		// result should never be null, but cachedNode needs to be protected
		// from being changed to null
		if (result != null)
			fStructuredDocument.setCachedDocumentRegion(result);
		dirtyEnd = result;
		return dirtyEnd;
	}

	private boolean isBreakingWithNestedTag(IStructuredDocumentRegion start, IStructuredDocumentRegion end) {
		boolean result = false;
		boolean changesIncludeA_lt = fChanges != null && fChanges.indexOf('<') >= 0;
		boolean delsIncludeA_gt = fDeletedText != null && fDeletedText.indexOf('>') >= 0;

		// List regions = new ArrayList();
		IStructuredDocumentRegion node = start;
		int endReplace = fStart + fLengthToReplace;
		while (end != null && node != end.getNext()) {
			Iterator i = node.getRegions().iterator();
			while (i.hasNext()) {
				ITextRegion region = (ITextRegion) i.next();
				if (intersects(node, region, fStart, endReplace)) {

					result = isBreakingWithNestedTag(changesIncludeA_lt, delsIncludeA_gt, node, region);

					if (result)
						break;
				}
			}
			node = node.getNext();
			if (result)
				break;
		}
		return result;
	}

	private boolean intersects(IStructuredDocumentRegion node, ITextRegion region, int low, int high) {
		int start = node.getStartOffset(region);
		int end = node.getEndOffset(region);
		return (end >= low && start <= high) || (start <= low && end >= low) || (start <= high && end >= high);
	}

	/**
	 * Returns true if potentially could be a jsp embedded region. Things like
	 * JSP Declaration can't be embedded.
	 */
	private boolean isJSPEmbeddedStartOrEnd(IStructuredDocumentRegion flatNode) {
		boolean result = false;
		String type = flatNode.getType();
		result = ((type == DOMJSPRegionContexts.JSP_SCRIPTLET_OPEN) || (type == DOMJSPRegionContexts.JSP_EXPRESSION_OPEN) || (type == DOMJSPRegionContexts.JSP_DECLARATION_OPEN));
		return result;
	}

	/**
	 * extends super class behavior
	 */
	protected boolean isPartOfBlockRegion(IStructuredDocumentRegion flatNode) {
		boolean result = false;
		String type = flatNode.getType();
		result = ((type == DOMJSPRegionContexts.JSP_CLOSE) || (type == DOMJSPRegionContexts.JSP_CONTENT) || super.isPartOfBlockRegion(flatNode));
		return result;
	}

	public IStructuredTextReParser newInstance() {
		return new JSPReParser();
	}

	public StructuredDocumentEvent quickCheck() {
		if (containsBreakingChange(new CoreNodeList(dirtyStart, dirtyEnd)))
			return null;
		return super.quickCheck();
	}

}
