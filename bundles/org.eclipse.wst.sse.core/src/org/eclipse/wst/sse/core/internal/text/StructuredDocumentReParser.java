/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.text;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTagParser;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionContainer;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.core.internal.util.Debug;
import org.eclipse.wst.sse.core.internal.util.Utilities;
import org.eclipse.wst.sse.core.utils.StringUtils;


/**
 * This class provides a centralized place to put "reparsing" logic. This is
 * the logic that reparses the text incrementally, as a user types in new
 * characters, or DOM nodes are inserted or deleted. Note: it is not a thread
 * safe class.
 */
public class StructuredDocumentReParser implements IStructuredTextReParser {
	protected IStructuredDocumentRegion dirtyEnd = null;
	protected IStructuredDocumentRegion dirtyStart = null;
	final private String doubleQuote = new String(new char[]{'\"'});
	protected final CoreNodeList EMPTY_LIST = new CoreNodeList();
	protected String fChanges;
	protected String fDeletedText;

	private FindReplaceDocumentAdapter fFindReplaceDocumentAdapter = null;
	protected int fLengthDifference;
	protected int fLengthToReplace;
	protected Object fRequester;
	protected int fStart;
	// note: this is the impl class of IStructuredDocument, not the interface
	// FUTURE_TO_DO: I believe some of these can be made private now.?
	protected BasicStructuredDocument fStructuredDocument;

	/**
	 * variable used in anticiapation of multithreading
	 */
	protected boolean isParsing;
	final private String singleQuote = new String(new char[]{'\''});

	public StructuredDocumentReParser() {
		super();
	}

	public StructuredDocumentEvent _checkBlockNodeList(List blockTagList) {
		StructuredDocumentEvent result = null;
		if (blockTagList != null) {
			for (int i = 0; i < blockTagList.size(); i++) {
				org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker blockTag = (org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker) blockTagList.get(i);
				String tagName = blockTag.getTagName();
				result = checkForCriticalName("<" + tagName); //$NON-NLS-1$
				if (result != null)
					break;
				result = checkForCriticalName("</" + tagName); //$NON-NLS-1$
				if (result != null)
					break;
			}
		}
		return result;
	}

	/**
	 * Common utility for checking for critical word such as " <SCRIPT>"
	 */
	private StructuredDocumentEvent _checkForCriticalWord(String criticalTarget, boolean checkEnd) {
		StructuredDocumentEvent result = null;
		int documentLength = fStructuredDocument.getLength();
		int propLen = fLengthToReplace;
		if (propLen > documentLength)
			propLen = documentLength;
		int startNeighborhood = fStart - criticalTarget.length();
		int adjustInsert = 0;
		if (startNeighborhood < 0) {
			adjustInsert = 0 - startNeighborhood;
			startNeighborhood = 0;
		}
		int endNeighborhood = fStart + fLengthToReplace + criticalTarget.length() - 1;
		if (endNeighborhood > documentLength)
			endNeighborhood = documentLength - 1;
		int oldlen = endNeighborhood - startNeighborhood; // + 1;
		if (oldlen + startNeighborhood > documentLength) {
			oldlen = documentLength - startNeighborhood;
		}
		String oldText = fStructuredDocument.get(startNeighborhood, oldlen);
		String peek = StringUtils.paste(oldText, fChanges, criticalTarget.length() - adjustInsert, fLengthToReplace);
		boolean isCriticalString = checkTagNames(oldText, criticalTarget, checkEnd);
		boolean toBeCriticalString = checkTagNames(peek, criticalTarget, checkEnd);
		if ((isCriticalString != toBeCriticalString) || // OR if both are
					// critical and there's
					// a change in the end
					// tag ('>')
					((isCriticalString && toBeCriticalString) && (changeInIsEndedState(oldText, peek)))) {
			// if it involves a change of a critical string (making one where
			// there wasn't, or removing
			// one where there was one) then reparse everthing.
			result = reparse(0, documentLength - 1);
		}
		return result;
	}

	private int _computeStartOfDifferences(CoreNodeList oldNodes, CoreNodeList newNodes) {
		int startOfDifferences = -1;
		int newNodesLength = newNodes.getLength();
		boolean foundDifference = false;
		boolean done = false;
		// we'll control our loop based on the old List length
		int oldNodesLength = oldNodes.getLength();
		// be sure to check 'done' first, so startOfDifferences isn't
		// icremented if done is true
		done : while ((!done) && (++startOfDifferences < oldNodesLength)) {
			IStructuredDocumentRegion oldNode = oldNodes.item(startOfDifferences);
			// this lessThanEffectedRegion is to check to be sure the node is
			// infact a candidate
			// to be considered as "old". This check is important for the case
			// where some
			// text is replaceing text that
			// appears identical, but is a different instance. For example, if
			// the text
			// is <P><B></B></P> and <B></B> is inserted at postion 3,
			// resulting in <P><B></B><B></B></P>
			// we do not want the
			// first <B> to be considered old ... it is the new one, the
			// second
			// <B> is the old one.
			if (_lessThanEffectedRegion(oldNode)) {
				// be sure to check that we have new nodes to compare against.
				if (startOfDifferences > newNodesLength) {
					foundDifference = false;
					done = true;
					continue done;
				} else {
					//
					IStructuredDocumentRegion newNode = newNodes.item(startOfDifferences);
					// note: shift is 0 while at beginning of list, before the
					// insertion (or deletion) point. After that, it is
					// fStart+fLengthDifference
					if (!(oldNode.sameAs(newNode, 0))) {
						foundDifference = true;
						done = true;
						continue done;
					} else { // if they are equal, then we will be keeping the
						// old one, so
						// we need to be sure its parentDocument is set back
						// to
						// the right instance
						oldNode.setParentDocument(fStructuredDocument);
					}
				}
			} else {
				// we didn't literally find a difference, but we count it as
				// such by implication
				foundDifference = true;
				done = true;
				continue done;
			}
		}
		// if we literally found a difference, then all is ok and we can
		// return
		// it.
		// if we did not literally find one, then we have to decide why.
		if (!foundDifference) {
			if (newNodesLength == oldNodesLength) { // then lists are
				// identical
				// (and may be of zero
				// length)
				startOfDifferences = -1;
			} else {
				if (newNodesLength > oldNodesLength) { // then lists are
					// identical except for
					// newNodes added
					startOfDifferences = oldNodesLength;
				} else {
					if (newNodesLength < oldNodesLength) { // then lists are
						// identical except
						// for old Nodes
						// deleted
						startOfDifferences = newNodesLength;
					}
				}
			}
		}
		return startOfDifferences;
	}

	private int _computeStartOfDifferences(IStructuredDocumentRegion oldNodeParam, ITextRegionList oldRegions, IStructuredDocumentRegion newNodeParam, ITextRegionList newRegions) {
		int startOfDifferences = -1;
		int newRegionsLength = newRegions.size();
		boolean foundDifference = false;
		boolean done = false;
		// we'll control our loop based on the old List length
		int oldRegionsLength = oldRegions.size();
		// be sure to check 'done' first, so startOfDifferences isn't
		// icremented if done is true
		done : while ((!done) && (++startOfDifferences < oldRegionsLength)) {
			ITextRegion oldRegion = oldRegions.get(startOfDifferences);
			// this lessThanEffectedRegion is to check to be sure the node is
			// infact a candidate
			// to be considered as "old". This check is important for the case
			// where some
			// text is replaceing text that
			// appears identical, but is a different instance. For example, if
			// the text
			// is <P><B></B></P> and <B></B> is inserted at postion 3,
			// resulting in <P><B></B><B></B></P>
			// we do not want the
			// first <B> to be considered old ... it is the new one, the
			// second
			// <B> is the old one.
			if (_lessThanEffectedRegion(oldNodeParam, oldRegion)) {
				// be sure to check that we have new nodes to compare against.
				if (startOfDifferences > newRegionsLength) {
					foundDifference = false;
					done = true;
					continue done;
				} else {
					//
					ITextRegion newRegion = newRegions.get(startOfDifferences);
					// note: shift is 0 while at beginning of list, before the
					// insertion (or deletion) point. After that, it is
					// fStart+fLengthDifference
					if (!(oldNodeParam.sameAs(oldRegion, newNodeParam, newRegion, 0))) {
						foundDifference = true;
						done = true;
						continue done;
					} else {
						// if they are equal, then we will be keeping the old
						// one.
						// unlike the flatnode case, there is no reason to
						// update
						// the textstore, since its the same text store in
						// either case
						// (since its the same flatnode)
						//oldRegion.setTextStore(fStructuredDocument.parentDocument);
					}
				}
			} else {
				// we didn't literally find a difference, but we count it as
				// such by implication
				foundDifference = true;
				done = true;
				continue done;
			}
		}
		// if we literally found a difference, then all is ok and we can
		// return
		// it.
		// if we did not literally find one, then we have to decide why.
		if (!foundDifference) {
			if (newRegionsLength == oldRegionsLength) { // then lists are
				// identical (and may
				// be of zero length)
				startOfDifferences = -1;
			} else {
				if (newRegionsLength > oldRegionsLength) { // then lists are
					// identical except
					// for newRegions
					// added
					startOfDifferences = oldRegionsLength;
				} else {
					if (newRegionsLength < oldRegionsLength) { // then lists
						// are identical
						// except for
						// old Nodes
						// deleted
						startOfDifferences = newRegionsLength;
					}
				}
			}
		}
		return startOfDifferences;
	}

	/**
	 * Part 1 of 2 steps to do a core_reparse
	 * 
	 * Parses a portion of the current text in the IStructuredDocument and
	 * returns the raw result
	 */
	private IStructuredDocumentRegion _core_reparse_text(int rescanStart, int rescanEnd) {
		fStructuredDocument.resetParser(rescanStart, rescanEnd);
		return fStructuredDocument.getParser().getDocumentRegions();
	}

	/**
	 * Part 2 of 2 steps to do a core_reparse
	 * 
	 * Integrates a list of StructuredDocumentRegions based on the current
	 * text contents of the IStructuredDocument into the IStructuredDocument
	 * data structure
	 */
	private StructuredDocumentEvent _core_reparse_update_model(IStructuredDocumentRegion newNodesHead, int rescanStart, int rescanEnd, CoreNodeList oldNodes, boolean firstTime) {
		StructuredDocumentEvent result = null;
		CoreNodeList newNodes = null;
		// rescan
		newNodes = new CoreNodeList(newNodesHead);
		// adjust our newNode chain so the offset positions match
		// our text store (not the simple string of text reparsed)
		StructuredDocumentRegionIterator.adjustStart(newNodesHead, rescanStart);
		// initialize the parentDocument variable of each instance in the new
		// chain
		StructuredDocumentRegionIterator.setParentDocument(newNodesHead, fStructuredDocument);
		// initialize the structuredDocument variable of each instance in the
		// new chain
		//StructuredDocumentRegionIterator.setStructuredDocument(newNodesHead,
		// fStructuredDocument);
		//
		if (firstTime) {
			fStructuredDocument.setCachedDocumentRegion(newNodesHead);
			fStructuredDocument.initializeFirstAndLastDocumentRegion();
			// note: since we are inserting nodes, for the first time, there
			// is
			// no adjustments
			// to downstream stuff necessary.
			result = new StructuredDocumentRegionsReplacedEvent(fStructuredDocument, fRequester, oldNodes, newNodes, fChanges, fStart, fLengthToReplace);
		} else {
			// note: integrates changes into model as a side effect
			result = minimumEvent(oldNodes, newNodes);
		}
		result.setDeletedText(fDeletedText);
		return result;
	}

	private CoreNodeList _formMinimumList(CoreNodeList flatnodes, int startOfDifferences, int endOfDifferences) {
		CoreNodeList minimalNodes = null;
		// if startOfDifferces is still its initial value, then we have an
		// empty document
		if (startOfDifferences == -1) {
			minimalNodes = EMPTY_LIST;
		} else {
			// if we do not have any flatnode in our flatnode list, then
			// simply
			// return our standard empty list
			if (flatnodes.getLength() == 0) {
				minimalNodes = EMPTY_LIST;
			} else {
				// if startOfDifferences is greater than endOfDifferences,
				// then
				// that means the calculations "crossed" each other, and
				// hence,
				// there really is no differences, so, again, return the empty
				// list
				if (startOfDifferences > endOfDifferences) {
					minimalNodes = EMPTY_LIST;
				} else {
					// the last check be sure we have some differnces
					if ((endOfDifferences > -1)) {
						minimalNodes = new CoreNodeList(flatnodes.item(startOfDifferences), flatnodes.item(endOfDifferences));
					} else {
						// there were no differences, the list wasn't
						// minimized, so simply return it.
						minimalNodes = flatnodes;
					}
				}
			}
		}
		return minimalNodes;
	}

	private boolean _greaterThanEffectedRegion(IStructuredDocumentRegion oldNode) {
		boolean result = false;
		int nodeStart = oldNode.getStartOffset();
		int changedRegionEnd = fStart + fLengthToReplace - 1;
		result = nodeStart > changedRegionEnd;
		return result;
	}

	private boolean _greaterThanEffectedRegion(IStructuredDocumentRegion oldNode, ITextRegion oldRegion) {
		boolean result = false;
		int regionStartOffset = oldNode.getStartOffset(oldRegion);
		int effectedRegionEnd = fStart + fLengthToReplace - 1;
		result = regionStartOffset > effectedRegionEnd;
		return result;
	}

	private boolean _lessThanEffectedRegion(IStructuredDocumentRegion oldNode) {
		boolean result = false;
		int nodeEnd = oldNode.getEndOffset() - 1;
		result = nodeEnd < fStart;
		return result;
	}

	private boolean _lessThanEffectedRegion(IStructuredDocumentRegion oldNode, ITextRegion oldRegion) {
		boolean result = false;
		int nodeEnd = oldNode.getEndOffset(oldRegion) - 1;
		result = nodeEnd < fStart;
		return result;
	}

	private boolean _regionsSameKind(ITextRegion newRegion, ITextRegion oldRegion) {
		boolean result = false;
		// if one region is a container region, and the other not, always
		// return false
		// else, just check their type.
		//      DW druing refactoring, looks like a "typo" here, using 'old' in
		// both.
		//		if (isContainerRegion(oldRegion) != isContainerRegion(oldRegion))
		if (isCollectionRegion(oldRegion) != isCollectionRegion(newRegion))
			result = false;
		else if (oldRegion.getType() == newRegion.getType())
			result = true;
		return result;
	}

	//	private boolean hasCollectionRegions(ITextRegion aRegion) {
	//		boolean result = false;
	//		if (aRegion instanceof ITextRegionCollection) {
	//			ITextRegionCollection regionContainter = (ITextRegionCollection)
	// aRegion;
	//			ITextRegionList regions = regionContainter.getRegions();
	//			Iterator iterator = regions.iterator();
	//			while (iterator.hasNext()) {
	//				if (aRegion instanceof ITextRegionCollection) {
	//					result = true;
	//					break;
	//				}
	//			}
	//		}
	//		return result;
	//	}
	/**
	 * This method is specifically to detect changes in 'isEnded' state,
	 * although it still does so with heuristics. If number of '>' changes,
	 * assume the isEnded state has changed.
	 */
	private boolean changeInIsEndedState(String oldText, String newText) {
		int nOld = StringUtils.occurrencesOf(oldText, '>');
		int nNew = StringUtils.occurrencesOf(newText, '>');
		return !(nOld == nNew);
	}

	private void checkAndAssignParent(IStructuredDocumentRegion oldNode, ITextRegion region) {
		if (region instanceof ITextRegionContainer) {
			((ITextRegionContainer) region).setParent(oldNode);
			return;
		}
		if (region instanceof ITextRegionCollection) {
			ITextRegionCollection textRegionCollection = (ITextRegionCollection) region;
			ITextRegionList regionList = textRegionCollection.getRegions();
			for (int i = 0; i < regionList.size(); i++) {
				ITextRegion innerRegion = regionList.get(i);
				checkAndAssignParent(oldNode, innerRegion);
			}
		}
	}

	/**
	 * A change to a CDATA tag can result in all being reparsed.
	 */
	private StructuredDocumentEvent checkForCDATA() {
		StructuredDocumentEvent result = null;
		result = checkForCriticalKey("<![CDATA["); //$NON-NLS-1$
		if (result == null)
			result = checkForCriticalKey("]]>"); //$NON-NLS-1$
		return result;
	}

	/**
	 * If a comment start or end tag is being added or deleted, we'll rescan
	 * the whole document. The reason is that content that is revealed or
	 * commented out can effect the interpretation of the rest of the
	 * document. Note: for now this is very XML specific, can refactor/improve
	 * later.
	 */
	protected StructuredDocumentEvent checkForComments() {
		StructuredDocumentEvent result = null;
		result = checkForCriticalKey("<!--"); //$NON-NLS-1$
		if (result == null)
			result = checkForCriticalKey("-->"); //$NON-NLS-1$
		// we'll also check for these degenerate cases
		if (result == null)
			result = checkForCriticalKey("<!--->"); //$NON-NLS-1$
		return result;
	}

	/**
	 * Common utility for checking for critical word such as " <SCRIPT>"
	 */
	protected StructuredDocumentEvent checkForCriticalKey(String criticalTarget) {
		return _checkForCriticalWord(criticalTarget, false);
	}

	/**
	 * Common utility for checking for critical word such as " <SCRIPT>"
	 */
	private StructuredDocumentEvent checkForCriticalName(String criticalTarget) {
		return _checkForCriticalWord(criticalTarget, true);
	}

	//	/**
	//	 * Currently this method is pretty specific to ?ML
	//	 * @deprecated - not really deprecated, but plan to make
	//	 * protected ... I'm not sure why its public or misspelled?
	//	 */
	protected StructuredDocumentEvent checkForCrossStructuredDocumentRegionBoundryCases() {
		StructuredDocumentEvent result = null;
		// Case 1: See if the language's syntax requires that multiple
		// StructuredDocumentRegions be rescanned
		if (result == null) {
			result = checkForCrossStructuredDocumentRegionSyntax();
		}
		// Case 2: "block tags" whose content is left unparsed
		if (result == null) {
			Object parser = fStructuredDocument.getParser();
			if (parser instanceof BlockTagParser) {
				List blockTags = ((BlockTagParser) parser).getBlockMarkers();
				result = _checkBlockNodeList(blockTags);
			}
		}
		// FUTURE_TO_DO: is there a better place to do this?
		// or! do we already do it some other more central place?
		if (result != null) {
			result.setDeletedText(fDeletedText);
		}
		return result;
	}

	/**
	 * Allow a reparser to check for extra syntactic cases that require
	 * parsing beyond the flatNode boundary.
	 * 
	 * This implementation is very XML-centric.
	 */
	protected StructuredDocumentEvent checkForCrossStructuredDocumentRegionSyntax() {
		StructuredDocumentEvent result;
		// Case 1: Quote characters are involved
		result = checkForQuotes();
		if (result == null) {
			// Case 2: The input forms or undoes a comment beginning or
			// comment
			// end
			result = checkForComments();
		}
		if (result == null) {
			// Case 3: The input forms or undoes a processing instruction
			result = checkForPI();
		}
		if (result == null) {
			// Case 4: The input forms or undoes a CDATA section
			result = checkForCDATA();
		}
		return result;
	}

	/**
	 * Checks to see if change request exactly matches the text it would be
	 * replacing. (In future, this, or similar method is where to check for
	 * "read only" attempted change.)
	 */
	private StructuredDocumentEvent checkForNoChange() {
		StructuredDocumentEvent result = null;
		// don't check equals unless lengths match
		// should be a tiny bit faster, since usually not
		// of equal lengths (I'm surprised String's equals method
		// doesn't do this.)
		if ((fChanges != null) && (fDeletedText != null) && (fChanges.length() == fDeletedText.length()) && (fChanges.equals(fDeletedText))) {
			result = new NoChangeEvent(fStructuredDocument, fRequester, fChanges, fStart, fLengthToReplace);
			((NoChangeEvent)result).reason = NoChangeEvent.NO_CONTENT_CHANGE;
		}
		return result;
	}

	/**
	 * A change to a PI tag can result in all being reparsed.
	 */
	private StructuredDocumentEvent checkForPI() {
		StructuredDocumentEvent result = null;
		result = checkForCriticalKey("<?"); //$NON-NLS-1$
		if (result == null)
			result = checkForCriticalKey("?>"); //$NON-NLS-1$
		return result;
	}

	//  For simplicity, if either text to be deleted, or text to be inserted
	// contains at least
	//  one quote, we'll search for previous quote in document, if any, and use
	// that flatnode as
	//  a dirty start, and we'll use end of document as dirty end. We need to
	// assume either \" or
	//  \' is an exceptable quote. (NOTE: this is, loosely, an XML assumption
	// --
	// other languages
	//  would differ, but we'll "hard code" for XML for now.
	// future_TODO: this is a really bad heuristic ... we should be looking
	// for
	// odd number of quotes
	// within a structuredDocumentRegion (or something!) This causes way too
	// much reparsing on
	// simple cases, like deleting a tag with a quoted attribute!
	private StructuredDocumentEvent checkForQuotes() {
		// routine is supported with null or empty string meaning the same
		// thing: deletion
		if (fChanges == null)
			fChanges = ""; //$NON-NLS-1$
		//
		StructuredDocumentEvent result = null;
		try {
			int dirtyStartPos = -1;
			String proposedDeletion = fStructuredDocument.get(fStart, fLengthToReplace);
			if (fStart < fStructuredDocument.getLength()) {
				if ((fChanges.indexOf(singleQuote) > -1) || (proposedDeletion.indexOf(singleQuote) > -1)) {
					IRegion singleQuoteRegion = getFindReplaceDocumentAdapter().find(fStart, singleQuote, false, false, false, false);
					if (singleQuoteRegion != null) {
						dirtyStartPos = singleQuoteRegion.getOffset();
					}
				} else if ((fChanges.indexOf(doubleQuote) > -1) || (proposedDeletion.indexOf(doubleQuote) > -1)) {
					IRegion doubleQuoteRegion = getFindReplaceDocumentAdapter().find(fStart, doubleQuote, false, false, false, false);
					if (doubleQuoteRegion != null) {
						dirtyStartPos = doubleQuoteRegion.getOffset();
					}
				}
			}
			if (dirtyStartPos > -1) {
				// then we found one, do create new structuredDocument event
				// based on the previous quote to end of document
				// except, we need to be positive that the previous quote is
				// in a "safe start" region (e.g. if in JSP content, we need
				// to
				// backup till we include the whole JSP region, in order for
				// it
				// to be correctly re-parsed. The backing up is done in the
				// reparse/find dirty start from hint
				// method.
				result = reparse(dirtyStartPos, fStructuredDocument.getLength() - 1);
			}
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
		if (result != null) {
			result.setDeletedText(fDeletedText);
		}
		return result;
	}

	private StructuredDocumentEvent checkHeuristics() {
		StructuredDocumentEvent result = null;
		result = checkForNoChange();
		if (result == null) {
			result = checkForCrossStructuredDocumentRegionBoundryCases();
			if (result == null) {
				result = quickCheck();
			}
		}
		return result;
	}

	/**
	 * Takes into account "tag name" rules for comparisons; case-insensitive.
	 */
	private boolean checkTagNames(String compareText, String criticalTarget, boolean checkEnd) {
		boolean result = false;
		if ((compareText == null) || (criticalTarget == null))
			return false;
		int posOfCriticalWord = compareText.toLowerCase().indexOf(criticalTarget.toLowerCase());
		result = posOfCriticalWord > -1;
		if (checkEnd && result) {
			// instead of returning true right away, we'll only return true
			// the
			// potentially matched tag is indeed a tag, for example, if
			// <SCRIPT
			// becomes <SCRIPTS we don't want to say the latter is a critical
			// tag
			int lastPos = posOfCriticalWord + criticalTarget.length();
			if (lastPos < compareText.length()) {
				char lastChar = compareText.charAt(lastPos);
				// Future: check formal definition of this java method, vs.
				// XML
				// parsing rules
				result = (!Character.isLetterOrDigit(lastChar));
			}
		}
		return result;
	}

	/**
	 * The core reparsing method ... after the dirty start and dirty end have
	 * been calculated elsewhere, and the text updated.
	 */
	protected StructuredDocumentEvent core_reparse(int rescanStart, int rescanEnd, CoreNodeList oldNodes, boolean firstTime) {
		IStructuredDocumentRegion newNodesHead = null;
		StructuredDocumentEvent result = null;
		newNodesHead = _core_reparse_text(rescanStart, rescanEnd);
		result = _core_reparse_update_model(newNodesHead, rescanStart, rescanEnd, oldNodes, firstTime);
		return result;
	}

	/**
	 * Resets state to "not parsing"
	 */
	private synchronized void endReParse() {
		isParsing = false;
		dirtyStart = null;
		dirtyEnd = null;
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
		// moved to subclass for quick transition
		// 12/6/2001 - Since we've changed the parser/scanner to allow a lone
		// '<' without
		// always interpretting it as start of a tag name, we need to be a
		// little fancier, in order
		// to "skip" over any plain 'ol content between the lone '<' and any
		// potential meating
		// regions past plain 'ol content.
		//		if (isLoneOpenFollowedByContent(result) && (result.getNext() !=
		// null)) {
		//			result = result.getNext();
		//		}
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
		// before
		// an open
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
			// moved to subclass to speed transition
			//			while (isPartOfBlockRegion(potential)) {
			//				potential = potential.getPrevious();
			//			}
			if (potential != null) {
				result = potential;
				fStructuredDocument.setCachedDocumentRegion(result);
			}
		}
		dirtyStart = result;
	}

	protected CoreNodeList formOldNodes(IStructuredDocumentRegion dirtyStart, IStructuredDocumentRegion dirtyEnd) {
		CoreNodeList oldNodes = new CoreNodeList(dirtyStart, dirtyEnd);
		// Now save the old text, that "goes with" the old nodes and regions.
		// Notice we are getting it directly from the text store
		String oldText = null;
		int oldStart = -1;
		int oldEnd = -1;
		// make sure there is some text, if not, use empty string
		// (if one node is not null, the other should ALWAYS be not null too,
		// since it
		// would at least be equal to it.)
		if (dirtyStart != null) {
			oldStart = dirtyStart.getStart();
			oldEnd = dirtyEnd.getEnd();
			oldText = fStructuredDocument.get(oldStart, oldEnd - oldStart);
		} else {
			oldStart = 0;
			oldEnd = 0;
			oldText = ""; //$NON-NLS-1$
		}
		// create a temporary text store for this text
		SubSetTextStore subTextStore = new SubSetTextStore(oldText, oldStart, oldEnd, fStructuredDocument.getLength());
		// Now update the text store of the oldNodes
		StructuredDocumentRegionIterator.setParentDocument(oldNodes, new MinimalDocument(subTextStore));
		return oldNodes;
	}

	/**
	 * @return Returns the findReplaceDocumentAdapter.
	 */
	public FindReplaceDocumentAdapter getFindReplaceDocumentAdapter() {
		if (fFindReplaceDocumentAdapter == null) {
			fFindReplaceDocumentAdapter = new FindReplaceDocumentAdapter(fStructuredDocument);
		}
		return fFindReplaceDocumentAdapter;
	}

	// Note: if thead safety is needed, this and all the other public methods
	// of this class
	// should be synchronized.
	public void initialize(Object requester, int start, int lengthToReplace, String changes) {
		isParsing = true;
		fRequester = requester;
		fStart = start;
		fLengthToReplace = lengthToReplace;
		fChanges = changes;
		// notice this one is derived
		fLengthDifference = Utilities.calculateLengthDifference(fChanges, fLengthToReplace);
		fDeletedText = fStructuredDocument.get(fStart, fLengthToReplace);
	}

	protected void insertNodes(IStructuredDocumentRegion previousOldNode, IStructuredDocumentRegion nextOldNode, CoreNodeList newNodes) {
		//
		IStructuredDocumentRegion firstNew = null;
		IStructuredDocumentRegion lastNew = null;
		//
		IStructuredDocumentRegion oldPrevious = previousOldNode;
		IStructuredDocumentRegion oldNext = nextOldNode;
		//
		if (newNodes.getLength() > 0) {
			// get pointers
			firstNew = newNodes.item(0);
			lastNew = newNodes.item(newNodes.getLength() - 1);
			// switch surrounding StructuredDocumentRegions' references to
			// lists
			if (oldPrevious != null)
				oldPrevious.setNext(firstNew);
			if (oldNext != null) {
				oldNext.setPrevious(lastNew);
			} else {
				// SIDE EFFECT
				// if oldNext is null, that means we are replaceing the
				// lastNode in the chain,
				// so we need to update the structuredDocuments lastNode as
				// the
				// last of the new nodes.
				fStructuredDocument.setLastDocumentRegion(newNodes.item(newNodes.getLength() - 1));
			}
			if (firstNew != null)
				firstNew.setPrevious(oldPrevious);
			if (lastNew != null)
				lastNew.setNext(oldNext);
		}
		// else nothing to insert
	}

	/**
	 * @param oldRegion
	 */
	private boolean isCollectionRegion(ITextRegion aRegion) {
		return (aRegion instanceof ITextRegionCollection);
	}

	/**
	 * @return boolean
	 */
	public boolean isParsing() {
		return isParsing;
	}

	/**
	 * The minimization algorithm simply checks the old nodes to see if any of
	 * them "survived" the rescan and are unchanged. If so, the instance of
	 * the old node is used instead of the new node. Before the requested
	 * change, need to check type, offsets, and text to determine if the same.
	 * After the requested change, need to check type and text, but adjust the
	 * offsets to what ever the change was.
	 */
	protected StructuredDocumentEvent minimumEvent(CoreNodeList oldNodes, CoreNodeList newNodes) {
		StructuredDocumentEvent event = null;
		CoreNodeList minimalOldNodes = null;
		CoreNodeList minimalNewNodes = null;
		// To minimize nodes, we'll collect all those
		// that are not equal into old and new lists
		// Note: we assume that old and new nodes
		// are basically contiguous -- and we force it to be so,
		// by starting at the beginning to
		// find first difference, and then starting at the end to find
		// last difference. Everything in between we assume is different.
		//
		//
		//
		// startOfDifferences is the index into the core node list where the
		// first difference
		// occurs. But it may point into the old or the new list.
		int startOfDifferences = _computeStartOfDifferences(oldNodes, newNodes);
		int endOfDifferencesOld = -1;
		int endOfDifferencesNew = -1;
		// if one of the lists are shorter than where the differences start,
		// then
		// then some portion of the lists are identical
		if ((startOfDifferences >= oldNodes.getLength()) || (startOfDifferences >= newNodes.getLength())) {
			if (oldNodes.getLength() < newNodes.getLength()) {
				// Then there are new regions to add
				//     these lengths will cause the vector of old ones to not
				//     have any elements, and the vector of new regions to have
				//     just the new ones not in common with the old ones
				//startOfDifferences should equal oldNodes.getLength(),
				// calculated above on _computeStartOfDifferences
				minimalOldNodes = EMPTY_LIST;
				endOfDifferencesNew = newNodes.getLength() - 1;
				minimalNewNodes = _formMinimumList(newNodes, startOfDifferences, endOfDifferencesNew);
			} else {
				if (oldNodes.getLength() > newNodes.getLength()) {
					// delete old
					// then there are old regions to delete
					//    these lengths will cause the vector of old regions to
					//    contain the ones to delete, and the vector of new
					// regions
					//    not have any elements
					//startOfDifferences should equal newNodes.getLength(),
					// calculated above on _computeStartOfDifferences
					endOfDifferencesOld = oldNodes.getLength() - 1;
					minimalOldNodes = _formMinimumList(oldNodes, startOfDifferences, endOfDifferencesOld);
					minimalNewNodes = EMPTY_LIST;
				} else
					// unlikely event
					event = new NoChangeEvent(fStructuredDocument, fRequester, fChanges, fStart, fLengthToReplace);
			}
		} else {
			// We found a normal startOfDiffernces, but have not yet found the
			// ends.
			// We'll look for the end of differences by going backwards down
			// the two lists.
			// Here we need a seperate index for each array, since they may be
			// (and
			// probably are) of different lengths.
			int indexOld = oldNodes.getLength() - 1;
			int indexNew = newNodes.getLength() - 1;
			// The greaterThanEffectedRegion is important to gaurd against
			// incorrect counting
			// when something identical is inserted to what's already there
			// (see minimization test case 5)
			// Note: the indexOld > startOfDifferences keeps indexOld from
			// getting too small,
			// so that the subsequent oldNodes.item(indexOld) is always valid.
			while ((indexOld >= startOfDifferences) && (_greaterThanEffectedRegion(oldNodes.item(indexOld)))) {
				if (!(oldNodes.item(indexOld).sameAs(newNodes.item(indexNew), fLengthDifference))) {
					break;
				} else {
					// if they are equal, then we will be keeping the old one,
					// so
					// we need to be sure its parentDocument is set back to
					// the
					// right instance
					oldNodes.item(indexOld).setParentDocument(fStructuredDocument);
				}
				indexOld--;
				indexNew--;
			}
			endOfDifferencesOld = indexOld;
			endOfDifferencesNew = indexNew;
			minimalOldNodes = _formMinimumList(oldNodes, startOfDifferences, endOfDifferencesOld);
			minimalNewNodes = _formMinimumList(newNodes, startOfDifferences, endOfDifferencesNew);
		} /////////////////////////////////////////
		//
		IStructuredDocumentRegion firstDownStreamNode = null;
		event = regionCheck(minimalOldNodes, minimalNewNodes);
		if (event != null) {
			firstDownStreamNode = minimalOldNodes.item(0).getNext();
			if (firstDownStreamNode != null && fLengthDifference != 0) { // if
				// firstDownStream
				// is
				// null,
				// then
				// we're
				// at
				// the
				// end
				// of
				// the
				// document
				StructuredDocumentRegionIterator.adjustStart(firstDownStreamNode, fLengthDifference);
			} //
		} else {
			event = nodesReplacedCheck(minimalOldNodes, minimalNewNodes);
			// now splice the new chain of nodes to where the old chain is (or
			// was)
			// the firstDownStreamNode (the first of those after the new
			// nodes)
			// is
			// remembered as a tiny optimization.
			if (minimalOldNodes.getLength() == 0 && minimalNewNodes.getLength() > 0) {
				// if no old nodes are being deleted, then use the
				// the newNodes offset (minus one) to find the point to
				// update downstream nodes, and after updating downstream
				// nodes postions, insert the new ones.
				int insertOffset = minimalNewNodes.item(0).getStartOffset();
				IStructuredDocumentRegion lastOldUnchangedNode = null;
				if (insertOffset > 0) {
					lastOldUnchangedNode = fStructuredDocument.getRegionAtCharacterOffset(insertOffset - 1);
					firstDownStreamNode = lastOldUnchangedNode.getNext();
				} else {
					// we're inserting at very beginning
					firstDownStreamNode = fStructuredDocument.getFirstStructuredDocumentRegion();
					// SIDE EFFECT: change the firstNode pointer if we're
					// inserting at beginning
					fStructuredDocument.setFirstDocumentRegion(minimalNewNodes.item(0));
				}
				StructuredDocumentRegionIterator.adjustStart(firstDownStreamNode, fLengthDifference);
				insertNodes(lastOldUnchangedNode, firstDownStreamNode, minimalNewNodes);
				// this (nodes replaced) is the only case where we need to
				// update the cached Node
				reSetCachedNode(minimalOldNodes, minimalNewNodes);
			} else {
				firstDownStreamNode = switchNodeLists(minimalOldNodes, minimalNewNodes);
				// no need to adjust the length of the new nodes themselves,
				// they
				// are already correct, but we do need to
				// adjust all "down stream" nodes with the length of the
				// insertion or deletion
				// --- adjustment moved to calling method.
				if (firstDownStreamNode != null) {
					// && event != null
					StructuredDocumentRegionIterator.adjustStart(firstDownStreamNode, fLengthDifference);
				} //
				// this (nodes replaced) is the only case where we need to
				// update the cached Node
				reSetCachedNode(minimalOldNodes, minimalNewNodes);
			}
		}
		return event;
	}

	// TODO: This should be abstract.
	public IStructuredTextReParser newInstance() {
		return new StructuredDocumentReParser();
	}

	protected StructuredDocumentEvent nodesReplacedCheck(CoreNodeList oldNodes, CoreNodeList newNodes) {
		// actually, nothing to check here, since (and assuming) we've already
		// minimized the number of nodes, and ruled out mere region changes
		StructuredDocumentEvent result = new StructuredDocumentRegionsReplacedEvent(fStructuredDocument, fRequester, oldNodes, newNodes, fChanges, fStart, fLengthToReplace);
		return result;
	}

	/**
	 * A method to allow any heuristic "quick checks" that might cover many
	 * many cases, before expending the time on a full reparse.
	 *  
	 */
	public StructuredDocumentEvent quickCheck() {
		StructuredDocumentEvent result = null;
		// if the dirty start is null, then we have an empty document.
		// in which case we'll return null so everything can be
		// reparsed "from scratch" . If its not null, we'll give the flatnode
		// a
		// chance
		// to handle, but only if there is one flatnode involved.
		if (dirtyStart != null && dirtyStart == dirtyEnd) {
			IStructuredDocumentRegion targetNode = dirtyStart;
			result = dirtyStart.updateRegion(fRequester, targetNode, fChanges, fStart, fLengthToReplace);
			if (result != null) {
				// at this point only, we need to update the text store and
				// and downstream nodes.
				// FUTURE_TO_DO: can this dependency on structuredDocument
				// method be eliminated?
				fStructuredDocument.updateDocumentData(fStart, fLengthToReplace, fChanges);
				IStructuredDocumentRegion firstDownStreamNode = targetNode.getNext();
				// then flatnode must have been the last one, so need to
				// update
				// any downstream ones
				if (firstDownStreamNode != null) {
					StructuredDocumentRegionIterator.adjustStart(firstDownStreamNode, fLengthDifference);
				}
			}
		}
		if (result != null) {
			result.setDeletedText(fDeletedText);
		}
		return result;
	}

	/**
	 * If only one node is involved, sees how many regions are changed. If
	 * only one, then its a 'regionChanged' event ... if more than one, its a
	 * 'regionsReplaced' event.
	 */
	protected StructuredDocumentEvent regionCheck(CoreNodeList oldNodes, CoreNodeList newNodes) {
		if (Debug.debugStructuredDocument)
			System.out.println("IStructuredDocument::regionsReplacedCheck"); //$NON-NLS-1$
		//$NON-NLS-1$
		//$NON-NLS-1$
		// the "regionsReplaced" event could only be true if and only if the
		// nodelists
		// are each only "1" in length.
		StructuredDocumentEvent result = null;
		int oldLength = oldNodes.getLength();
		int newLength = newNodes.getLength();
		if ((oldLength != 1) || (newLength != 1)) {
			result = null;
		} else {
			IStructuredDocumentRegion oldNode = oldNodes.item(0);
			IStructuredDocumentRegion newNode = newNodes.item(0);
			result = regionCheck(oldNode, newNode);
		}
		return result;
	}

	/**
	 * If only one node is involved, sees how many regions are changed. If
	 * only one, then its a 'regionChanged' event ... if more than one, its a
	 * 'regionsReplaced' event.
	 */
	protected StructuredDocumentEvent regionCheck(IStructuredDocumentRegion oldNode, IStructuredDocumentRegion newNode) {
		//
		StructuredDocumentEvent result = null;
		ITextRegionList oldRegions = oldNode.getRegions();
		ITextRegionList newRegions = newNode.getRegions();
		ITextRegion[] oldRegionsArray = oldRegions.toArray();
		ITextRegion[] newRegionsArray = newRegions.toArray();
		//
		// for the 'regionsReplaced' event, we don't care if
		// the regions changed due to type, or text,
		// we'll just collect all those that are not equal
		// into the old and new region lists.
		// Note: we, of course, assume that old and new regions
		// are basically contiguous -- and we force it to be so,
		// even if not literally so, by starting at beginning to
		// find first difference, and then starting at end to find
		// last difference. Everything in between we assume is different.
		//
		// going up is easy, we start at zero in each, and continue
		// till regions are not the same.
		int startOfDifferences = _computeStartOfDifferences(oldNode, oldRegions, newNode, newRegions);
		int endOfDifferencesOld = -1;
		int endOfDifferencesNew = -1;
		//
		//
		// if one of the lists are shorter than where the differences start,
		// then
		// then some portion of the lists are identical
		if ((startOfDifferences >= oldRegions.size()) || (startOfDifferences >= newRegions.size())) {
			if (oldRegions.size() < newRegions.size()) {
				// INSERT CASE
				// then there are new regions to add
				//     these lengths will cause the vector of old ones to not
				//     have any elements, and the vector of new regions to have
				//     just the new ones.
				startOfDifferences = oldRegionsArray.length;
				endOfDifferencesOld = oldRegionsArray.length - 1;
				endOfDifferencesNew = newRegionsArray.length - 1;
			} else {
				if (oldRegions.size() > newRegions.size()) {
					// DELETE CASE
					// then there are old regions to delete
					//    these lengths will cause the vector of old regions to
					//    contain the ones to delete, and the vector of new
					// regions
					//    not have any elements
					startOfDifferences = newRegionsArray.length;
					endOfDifferencesOld = oldRegionsArray.length - 1;
					endOfDifferencesNew = newRegionsArray.length - 1;
				} else {
					// else the lists are identical!
					// unlikely event, probably error in current design, since
					// we check for identity at the very beginning of
					// reparsing.
					result = new NoChangeEvent(fStructuredDocument, fRequester, fChanges, fStart, fLengthToReplace);
				}
			}
		} else {
			if ((startOfDifferences > -1) && (endOfDifferencesOld < 0) && (endOfDifferencesNew < 0)) {
				// We found a normal startOfDiffernces, but have not yet found
				// the ends.
				// We'll look for the end of differences by going backwards
				// down the two lists.
				// Here we need a seperate index for each array, since they
				// may
				// be (and
				// probably are) of different lengths.
				int indexOld = oldRegionsArray.length - 1;
				int indexNew = newRegionsArray.length - 1;
				while ((indexOld >= startOfDifferences) && (_greaterThanEffectedRegion(oldNode, oldRegionsArray[indexOld]))) {
					if ((!(oldNode.sameAs(oldRegionsArray[indexOld], newNode, newRegionsArray[indexNew], fLengthDifference)))) {
						//endOfDifferencesOld = indexOne;
						//endOfDifferencesNew = indexTwo;
						break;
					}
					indexOld--;
					indexNew--;
				}
				endOfDifferencesOld = indexOld;
				endOfDifferencesNew = indexNew;
			}
		}
		//
		// result != null means the impossible case above occurred
		if (result == null) {
			// Now form the two vectors of different regions
			ITextRegionList holdOldRegions = new TextRegionListImpl();
			ITextRegionList holdNewRegions = new TextRegionListImpl();
			if (startOfDifferences > -1 && endOfDifferencesOld > -1) {
				for (int i = startOfDifferences; i <= endOfDifferencesOld; i++) {
					holdOldRegions.add(oldRegionsArray[i]);
				}
			}
			if (startOfDifferences > -1 && endOfDifferencesNew > -1) {
				for (int i = startOfDifferences; i <= endOfDifferencesNew; i++) {
					holdNewRegions.add(newRegionsArray[i]);
				}
			}
			if (holdOldRegions.size() == 0 && holdNewRegions.size() == 0) {
				// then this means the regions were identical, which means
				// someone
				// pasted exactly the same thing they had selected, or !!!
				// someone deleted the end bracket of the tag. !!!?
				result = new NoChangeEvent(fStructuredDocument, fRequester, fChanges, fStart, fLengthToReplace);
			} else {
				//If both holdOldRegions and holdNewRegions are of length 1,
				// then its
				// a "region changed" event, else a "regions replaced" event.
				// so we want the new instance of region to become part of the
				// old instance of old node
				if ((holdOldRegions.size() == 1) && (holdNewRegions.size() == 1) && _regionsSameKind((holdNewRegions.get(0)), (holdOldRegions.get(0)))) {
					ITextRegion newOldRegion = swapNewForOldRegion(oldNode, holdOldRegions.get(0), newNode, holdNewRegions.get(0));
					// -- need to update any down stream regions, within this
					// 'oldNode'
					updateDownStreamRegions(oldNode, newOldRegion);
					result = new RegionChangedEvent(fStructuredDocument, fRequester, oldNode, newOldRegion, fChanges, fStart, fLengthToReplace);
				} else {
					replaceRegions(oldNode, holdOldRegions, newNode, holdNewRegions);
					// -- need to update any down stream regions, within this
					// 'oldNode'
					// don't need with the way replaceRegions is implemented.
					// It handles.
					//if(holdNewRegions.size() > 0)
					//updateDownStreamRegions(oldNode, (ITextRegion)
					// holdNewRegions.lastElement());
					result = new RegionsReplacedEvent(fStructuredDocument, fRequester, oldNode, holdOldRegions, holdNewRegions, fChanges, fStart, fLengthToReplace);
				}
			}
		}
		return result;
	}

	/**
	 * An entry point for reparsing. It calculates the dirty start and dirty
	 * end flatnodes based on the start point and length of the changes.
	 *  
	 */
	public StructuredDocumentEvent reparse() {
		StructuredDocumentEvent result = null;
		// if we do not have a cachedNode, then the document
		// must be empty, so simply use 'null' as the dirtyStart and dirtyEnd
		// otherwise, find them.
		if (fStructuredDocument.getCachedDocumentRegion() != null) {
			findDirtyStart(fStart);
			int end = fStart + fLengthToReplace;
			findDirtyEnd(end);
		}
		if (fStructuredDocument.getCachedDocumentRegion() != null) {
			result = checkHeuristics();
		}
		if (result == null) {
			result = reparse(dirtyStart, dirtyEnd);
		}
		endReParse();
		return result;
	}

	/**
	 * An entry point for reparsing. It calculates the dirty start and dirty
	 * end flatnodes based on suggested positions to begin and end. This is
	 * needed for cases where parsing must go beyond the immediate node and
	 * its direct neighbors.
	 *  
	 */
	protected StructuredDocumentEvent reparse(int reScanStartHint, int reScanEndHint) {
		StructuredDocumentEvent result = null;
		// if we do not have a cachedNode, then the document
		// must be empty, so simply use 'null' as the dirtyStart and dirtyEnd
		if (fStructuredDocument.getCachedDocumentRegion() != null) {
			findDirtyStart(reScanStartHint);
			findDirtyEnd(reScanEndHint);
		}
		result = reparse(dirtyStart, dirtyEnd);
		isParsing = false;
		// debug
		//verifyStructured(result);
		return result;
	}

	/**
	 * The core reparsing method ... after the dirty start and dirty end have
	 * been calculated elsewhere.
	 */
	protected StructuredDocumentEvent reparse(IStructuredDocumentRegion dirtyStart, IStructuredDocumentRegion dirtyEnd) {
		StructuredDocumentEvent result = null;
		int rescanStart = -1;
		int rescanEnd = -1;
		boolean firstTime = false;
		//
		// "save" the oldNodes (that may be replaced) in a list
		CoreNodeList oldNodes = formOldNodes(dirtyStart, dirtyEnd);
		if (dirtyStart == null || dirtyEnd == null) {
			// dirtyStart or dirty end are null, then that means we didn't
			// have
			// a
			// cached node, which means we have an empty document, so we
			// just need to rescan the changes
			rescanStart = 0;
			rescanEnd = fChanges.length();
			firstTime = true;
		} else {
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
		// FUTURE_TO_DO -- don't fire "document changed" event till later
		fStructuredDocument.updateDocumentData(fStart, fLengthToReplace, fChanges);
		// ------------------ now the real work
		result = core_reparse(rescanStart, rescanEnd, oldNodes, firstTime);
		//
		// event is returned to the caller, incase there is
		// some opitmization they can do
		return result;
	}

	protected void replaceRegions(IStructuredDocumentRegion oldNode, ITextRegionList oldRegions, IStructuredDocumentRegion newNode, ITextRegionList newRegions) {
		int insertPos = -1;
		ITextRegionList regions = oldNode.getRegions();
		// make a fake flatnode to be new parent of oldRegions, so their text
		// will be right.
		//IStructuredDocumentRegion holdOldStructuredDocumentRegion = new
		// BasicStructuredDocumentRegion(oldNode);
		//
		// need to reset the parent of the new to-be-inserted regions to be
		// the
		// same oldNode that is the one having its regions changed
		// DW, 4/16/2003, removed since ITextRegion no longer has parent.
		// 		ITextRegionContainer oldParent = oldNode;
		//		for (int i = 0; i < newRegions.size(); i++) {
		//			AbstractRegion region = (AbstractRegion) newRegions.elementAt(i);
		//			region.setParent(oldParent);
		//		}
		// if there are no old regions, insert the new regions according to
		// offset
		if (oldRegions.size() == 0) {
			ITextRegion firstNewRegion = newRegions.get(0);
			int firstOffset = newNode.getStartOffset(firstNewRegion);
			// if at beginning, insert there
			if (firstOffset == 0) {
				insertPos = 0;
			} else {
				//
				ITextRegion regionAtOffset = oldNode.getRegionAtCharacterOffset(firstOffset);
				if (regionAtOffset == null)
					insertPos = regions.size();
				else
					insertPos = regions.indexOf(regionAtOffset);
			}
		} else {
			// else, delete old ones before inserting new ones in their place
			ITextRegion firstOldRegion = oldRegions.get(0);
			insertPos = regions.indexOf(firstOldRegion);
			regions.removeAll(oldRegions);
		}
		regions.addAll(insertPos, newRegions);
		// now regions vector of each node should be of equal length,
		// so go through each, and make sure the old regions
		// offsets matches the new regions offsets
		// (we'll just assign them all, but could be slightly more effiecient)
		ITextRegionList allNewRegions = newNode.getRegions();
		for (int i = 0; i < regions.size(); i++) {
			ITextRegion nextOldishRegion = regions.get(i);
			ITextRegion nextNewRegion = allNewRegions.get(i);
			nextOldishRegion.equatePositions(nextNewRegion);
			checkAndAssignParent(oldNode, nextOldishRegion);
		}
		oldNode.setLength(newNode.getLength());
		oldNode.setEnded(newNode.isEnded());
		oldNode.setParentDocument(newNode.getParentDocument());
		// removed concept of part of these regions, so no longer need to do.
		//		for (int i = 0; i < oldRegions.size(); i++) {
		//			ITextRegion region = (ITextRegion) oldRegions.elementAt(i);
		//			region.setParent(holdOldStructuredDocumentRegion);
		//		}
	}

	private void reSetCachedNode(CoreNodeList oldNodes, CoreNodeList newNodes) {
		// use the last newNode as the new cachedNode postion, unless its null
		// (e.g. when nodes are deleted) in which case, assign
		// it to a "safe" node so we don't lose reference to the
		// structuredDocument!
		if (newNodes.getLength() > 0) {
			// use last new node as the cache
			fStructuredDocument.setCachedDocumentRegion(newNodes.item(newNodes.getLength() - 1));
		} else {
			// if cachedNode is an old node, then we're in trouble:
			// we can't leave it as the cached node! and its already
			// been disconnected from the model, so we can't do getNext
			// or getPrevious, so we'll get one that is right before
			// (or right after) the offset of the old nodes that are being
			// deleted.
			// 
			// if newNodesHead and cachedNode are both null, then
			// it means we were asked to insert an empty string into
			// an empty document. So we have nothing to do here
			// (that is, we have no node to cache)
			// similarly if there are no new nodes and no old nodes then
			// nothing to do (but that should never happen ... we shouldn't
			// get there if there is no event to generate).
			if ((fStructuredDocument.getCachedDocumentRegion() != null) && (oldNodes.getLength() > 0)) {
				// note: we can't simple use nodeAtCharacterOffset, since it
				// depends on cachedNode.
				if (oldNodes.includes(fStructuredDocument.getCachedDocumentRegion()))
					fStructuredDocument.setCachedDocumentRegion(fStructuredDocument.getFirstStructuredDocumentRegion());
			}
			if ((fStructuredDocument.getCachedDocumentRegion() == null) && (Debug.displayWarnings)) {
				// this will happen now legitamately when all text is deleted
				// from a document
				System.out.println("Warning: StructuredDocumentReParser::reSetCachedNode: could not find a node to cache! (its ok if all text deleted)"); //$NON-NLS-1$
			}
		}
	}

	public void setStructuredDocument(IStructuredDocument newStructuredDocument) {
		// NOTE: this method (and class) depend on being able to
		// do the following cast (i.e. references some fields directly)
		fStructuredDocument = (BasicStructuredDocument) newStructuredDocument;
		fFindReplaceDocumentAdapter = null;
	}

	private IStructuredDocumentRegion splice(CoreNodeList oldNodes, CoreNodeList newNodes) {
		//
		IStructuredDocumentRegion firstOld = null;
		IStructuredDocumentRegion firstNew = null;
		IStructuredDocumentRegion lastOld = null;
		IStructuredDocumentRegion lastNew = null;
		//
		IStructuredDocumentRegion oldPrevious = null;
		IStructuredDocumentRegion oldNext = null;
		IStructuredDocumentRegion newPrevious = null;
		IStructuredDocumentRegion newNext = null;
		//
		// if called with both arguments empty lists, we can disregard.
		// this happens, for example, when some text is replaced with the
		// identical text.
		if ((oldNodes.getLength() == 0) && (newNodes.getLength() == 0)) {
			return null;
		}
		// get pointers
		if (newNodes.getLength() > 0) {
			firstNew = newNodes.item(0);
			lastNew = newNodes.item(newNodes.getLength() - 1);
		}
		//
		if (oldNodes.getLength() > 0) {
			firstOld = oldNodes.item(0);
			lastOld = oldNodes.item(oldNodes.getLength() - 1);
			if (firstOld != null)
				oldPrevious = firstOld.getPrevious();
			if (lastOld != null)
				oldNext = lastOld.getNext();
		}
		// handle switch
		if (newNodes.getLength() > 0) {
			// switch surrounding StructuredDocumentRegions' references to
			// lists
			if (oldPrevious != null)
				oldPrevious.setNext(firstNew);
			if (newPrevious != null)
				newPrevious.setNext(firstOld);
			if (oldNext != null)
				oldNext.setPrevious(lastNew);
			if (newNext != null)
				newNext.setPrevious(lastOld);
			// switch list pointers to surrounding StructuredDocumentRegions
			if (firstOld != null)
				firstOld.setPrevious(newPrevious);
			if (lastOld != null)
				lastOld.setNext(newNext);
			if (firstNew != null)
				firstNew.setPrevious(oldPrevious);
			if (lastNew != null)
				lastNew.setNext(oldNext);
		} else {
			// short circuit when there are no new nodes
			if (oldPrevious != null)
				oldPrevious.setNext(oldNext);
			if (oldNext != null)
				oldNext.setPrevious(oldPrevious);
		}
		//
		// SIDE EFFECTs
		// if we have oldNodes, and if oldNext or oldPrevious is null,
		// that means we are replacing
		// the lastNode or firstNode the structuredDocuments's chain of nodes,
		// so we need to update the structuredDocuments last or first Node
		// as the last or first of the new nodes.
		// (and sometimes even these will be null! such as when deleting all
		// text in a document).
		if ((oldNext == null) && (oldNodes.getLength() > 0)) {
			if (newNodes.getLength() > 0) {
				fStructuredDocument.setLastDocumentRegion(lastNew);
			} else {
				// in this case, the last node is being deleted, but not
				// replaced
				// with anything. In this case, we can just back up one
				// from the first old node
				fStructuredDocument.setLastDocumentRegion(firstOld.getPrevious());
			}
		}
		if ((oldPrevious == null) && (oldNodes.getLength() > 0)) {
			if (newNodes.getLength() > 0) {
				fStructuredDocument.setFirstDocumentRegion(firstNew);
			} else {
				// in this case the first node is being deleted, but not
				// replaced
				// with anything. So, we just go one forward past the last old
				// node.
				fStructuredDocument.setFirstDocumentRegion(lastOld.getNext());
			}
		}
		// as a tiny optimization, we return the first of the downstream
		// nodes,
		// if any
		return oldNext;
	}

	/**
	 * The purpose of this method is to "reuse" the old container region, when
	 * found to be same (so same instance doesn't change). The goal is to
	 * "transform" the old region, so its equivelent to the newly parsed one.
	 *  
	 */
	private ITextRegion swapNewForOldRegion(IStructuredDocumentRegion oldNode, ITextRegion oldRegion, IStructuredDocumentRegion newNode, ITextRegion newRegion) {
		// makes the old region instance the correct size.
		oldRegion.equatePositions(newRegion);
		// adjusts old node instance appropriately
		oldNode.setLength(newNode.getLength());
		oldNode.setEnded(newNode.isEnded());
		// we do have to set the parent document, since the oldNode's
		// were set to a temporary one, then newNode's have the
		// right one.
		oldNode.setParentDocument(newNode.getParentDocument());
		// if we're transforming a container region, we need to be sure to
		// transfer the new embedded regions, to the old parent
		// Note: if oldRegion hasEmbeddedRegions, then we know the
		// newRegion does too, since we got here because they were the
		// same type.
		if (isCollectionRegion(oldRegion)) { // ||
			// hasContainerRegions(oldRegion))
			// {
			transferEmbeddedRegions(oldNode, (ITextRegionContainer) oldRegion, (ITextRegionContainer) newRegion);
		}
		return oldRegion;
	}

	private IStructuredDocumentRegion switchNodeLists(CoreNodeList oldNodes, CoreNodeList newNodes) {
		IStructuredDocumentRegion result = splice(oldNodes, newNodes);
		// ensure that the old nodes hold no references to the existing model
		if (oldNodes.getLength() > 0) {
			IStructuredDocumentRegion firstItem = oldNodes.item(0);
			firstItem.setPrevious(null);
			IStructuredDocumentRegion lastItem = oldNodes.item(oldNodes.getLength() - 1);
			lastItem.setNext(null);
		}
		return result;
	}

	/**
	 * The purpose of this method is to "reuse" the old container region, when
	 * found to be same (so same instance doesn't change). The goal is to
	 * "transform" the old region, so its equivelent to the newly parsed one.
	 *  
	 */
	private void transferEmbeddedRegions(IStructuredDocumentRegion oldNode, ITextRegionContainer oldRegion, ITextRegionContainer newRegion) {
		// the oldRegion should already have the right parent, since
		// we got here because all's equivelent except the region
		// postions have changed.
		//oldRegion.setParent(newRegion.getParent());
		// but we should check if there's "nested" embedded regions, and if
		// so, we can just move them over. setting their parent as this old
		// region.
		ITextRegionList newRegionsToTransfer = newRegion.getRegions();
		oldRegion.setRegions(newRegionsToTransfer);
		Iterator newRegionsInOldOne = newRegionsToTransfer.iterator();
		while (newRegionsInOldOne.hasNext()) {
			ITextRegion newOne = (ITextRegion) newRegionsInOldOne.next();
			if (isCollectionRegion(newOne)) { // ||
				// hasContainerRegions(newOne)) {
				//((ITextRegionContainer) newOne).setParent(oldRegion);
				oldRegion.setRegions(newRegion.getRegions());
			}
		}
	}

	private void updateDownStreamRegions(IStructuredDocumentRegion flatNode, ITextRegion lastKnownRegion) {
		// so all regions after the last known region (last known to be ok)
		// have to have their start and end values adjusted.
		ITextRegionList regions = flatNode.getRegions();
		int listLength = regions.size();
		int startIndex = 0;
		// first, loop through to find where to start
		for (int i = 0; i < listLength; i++) {
			ITextRegion region = regions.get(i);
			if (region == lastKnownRegion) {
				startIndex = i;
				break;
			}
		}
		// now, beginning one past the last known one, loop
		// through to end of list, adjusting the start and end postions.
		startIndex++;
		for (int j = startIndex; j < listLength; j++) {
			ITextRegion region = regions.get(j);
			region.adjustStart(fLengthDifference);
		}
	}
}
