/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.internal.parser.CSSRegionUtil;
import org.eclipse.wst.css.core.parser.CSSRegionContexts;
import org.eclipse.wst.css.core.util.CSSUtil;
import org.eclipse.wst.css.core.util.RegionIterator;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.text.StructuredDocumentReParser;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.text.ITextRegion;


/**
 * This class provides a centralized place to put "reparsing" logic. This is
 * the logic that reparses the text incrementally, as a user types in new
 * characters, or DOM nodes are inserted or deleted. Note: it is not a thread
 * safe class.
 */
public class CSSStructuredDocumentReParser extends StructuredDocumentReParser {

	class ReparseRange {
		ReparseRange() {
			reset();
		}

		ReparseRange(int start, int end) {
			fRangeStart = start;
			fRangeEnd = end;
		}

		void setStart(int start) {
			fRangeStart = start;
		}

		void setEnd(int end) {
			fRangeEnd = end;
		}

		int getStart() {
			return fRangeStart;
		}

		int getEnd() {
			return fRangeEnd;
		}

		boolean isValid() {
			return (0 < fRangeEnd - fRangeStart) ? true : false;
		}

		void reset() {
			fRangeStart = Integer.MAX_VALUE;
			fRangeEnd = 0;
		}

		void expand(ReparseRange range) {
			if (range == null || !range.isValid()) {
				return;
			}
			int start = range.getStart();
			if (start < fRangeStart) {
				fRangeStart = start;
			}
			int end = range.getEnd();
			if (fRangeEnd < end) {
				fRangeEnd = end;
			}
		}

		private int fRangeStart;
		private int fRangeEnd;
	}

	/**
	 * 
	 */
	public CSSStructuredDocumentReParser() {
		super();
	}

	/**
	 * 
	 */
	// public StructuredDocumentEvent
	// checkForCrossStructuredDocumentRegionBoundryCases2() {
	// StructuredDocumentEvent result = specialPositionUpdate(fStart, fStart +
	// fLengthToReplace - 1);
	// if (result == null) {
	// result = checkInsideBrace();
	// }
	// return result;
	// }
	protected StructuredDocumentEvent checkForCrossStructuredDocumentRegionSyntax() {
		int checkStart = fStart;
		int checkEnd = fStart + fLengthToReplace - 1;
		IStructuredDocumentRegion endRegion = fStructuredDocument.getRegionAtCharacterOffset(checkEnd);
		if (endRegion != null) {
			checkEnd = endRegion.getEndOffset();
		}

		ReparseRange range = new ReparseRange(checkStart, checkEnd);

		range.expand(getUpdateRangeForDelimiter(range.getStart(), range.getEnd()));
		range.expand(getUpdateRangeForUnknownRegion(range.getStart(), range.getEnd()));

		range.expand(getUpdateRangeForQuotes(range.getStart(), range.getEnd()));
		range.expand(getUpdateRangeForComments(range.getStart(), range.getEnd()));
		range.expand(getUpdateRangeForBraces(range.getStart(), range.getEnd()));

		StructuredDocumentEvent result;

		result = checkInsideBrace(range.getStart(), range.getEnd());

		if (result == null) {
			result = reparse(range.getStart(), range.getEnd());
		}

		return result;
	}

	private ReparseRange getUpdateRangeForUnknownRegion(int start, int end) {
		int newStart = start;
		RegionIterator iterator = new RegionIterator(fStructuredDocument, start - 1);
		if (iterator.hasPrev()) {
			iterator.prev(); // skip myself
		}
		while (iterator.hasPrev()) {
			ITextRegion region = iterator.prev();
			if (region != null && region.getType() == CSSRegionContexts.CSS_UNKNOWN) {
				newStart = iterator.getStructuredDocumentRegion().getStartOffset(region);
			}
			else {
				break;
			}
		}

		if (start != newStart) {
			return new ReparseRange(newStart, end);
		}

		return null;
	}

	private ReparseRange getUpdateRangeForDelimiter(int start, int end) {
		if (dirtyStart != null && dirtyStart.getStart() < start) {
			start = dirtyStart.getStart();
		}
		IStructuredDocumentRegion docRegion = fStructuredDocument.getRegionAtCharacterOffset(start);
		if (docRegion == null) {
			return null;
		}
		if (docRegion.getType() == CSSRegionContexts.CSS_DELIMITER) {
			IStructuredDocumentRegion prevRegion = docRegion.getPrevious();
			if (prevRegion != null) {
				return new ReparseRange(prevRegion.getStart(), end);
			}
		}

		return null;
	}

	private ReparseRange getUpdateRangeForQuotes(int start, int end) {
		ReparseRange range = new ReparseRange();

		range.expand(getUpdateRangeForPair(start, end, "\"", "\"")); //$NON-NLS-2$//$NON-NLS-1$
		range.expand(getUpdateRangeForPair(start, end, "\'", "\'")); //$NON-NLS-2$//$NON-NLS-1$

		return (range.isValid()) ? range : null;
	}

	private ReparseRange getUpdateRangeForComments(int start, int end) {
		ReparseRange range = new ReparseRange();

		range.expand(getUpdateRangeForPair(start, end, "/*", "*/")); //$NON-NLS-2$//$NON-NLS-1$
		range.expand(getUpdateRangeForPair(start, end, "<%", "%>")); //$NON-NLS-2$//$NON-NLS-1$

		return (range.isValid()) ? range : null;
	}

	private ReparseRange getUpdateRangeForBraces(int start, int end) {
		ReparseRange range = new ReparseRange();

		range.expand(getUpdateRangeForPair(start, end, "[", "]")); //$NON-NLS-2$//$NON-NLS-1$
		range.expand(getUpdateRangeForPair(start, end, "(", ")")); //$NON-NLS-2$//$NON-NLS-1$
		range.expand(getUpdateRangeForPair(start, end, "{", "}")); //$NON-NLS-2$//$NON-NLS-1$

		return (range.isValid()) ? range : null;
	}

	private StructuredDocumentEvent checkInsideBrace(int start, int end) {
		StructuredDocumentEvent result = null;
		IStructuredDocumentRegion region = fStructuredDocument.getRegionAtCharacterOffset(start);
		if (region == null) {
			return null;
		}
		boolean bDeclaration = false;
		String type = region.getType();
		if (CSSRegionUtil.isDeclarationType(type) || type == CSSRegionContexts.CSS_LBRACE || type == CSSRegionContexts.CSS_RBRACE) {
			bDeclaration = true;
		}
		if (!bDeclaration) {
			IStructuredDocumentRegion prevRegion = CSSUtil.findPreviousSignificantNode(region);
			if (prevRegion != null) {
				type = prevRegion.getType();
				if (CSSRegionUtil.isDeclarationType(type) || type == CSSRegionContexts.CSS_LBRACE) {
					bDeclaration = true;
				}
			}
		}
		if (!bDeclaration) {
			IStructuredDocumentRegion nextRegion = CSSUtil.findNextSignificantNode(region);
			if (nextRegion != null) {
				type = nextRegion.getType();
				if (CSSRegionUtil.isDeclarationType(type) || type == CSSRegionContexts.CSS_RBRACE) {
					bDeclaration = true;
				}
			}
		}

		if (bDeclaration) {
			IStructuredDocumentRegion startRegion = findRuleStart(region);
			if (startRegion != null) {
				IStructuredDocumentRegion endRegion = fStructuredDocument.getRegionAtCharacterOffset(end);
				if (endRegion != null) {
					result = reparse(startRegion, endRegion);
				}
			}
		}
		return result;
	}

	private IStructuredDocumentRegion findRuleStart(IStructuredDocumentRegion startRegion) {
		IStructuredDocumentRegion region = startRegion;
		// find '{'
		while (region != null && region.getType() != CSSRegionContexts.CSS_LBRACE) {
			region = region.getPrevious();
		}
		// if (region == startRegion) {
		// return null;
		// }
		// else
		if (region != null) { // '{' is found
			region = region.getPrevious();
			if (isLeadingDeclarationType(region.getType())) {
				return region;
			}
		}
		return null;
	}

	private boolean isLeadingDeclarationType(String type) {
		return (type == CSSRegionContexts.CSS_PAGE || type == CSSRegionContexts.CSS_FONT_FACE || CSSRegionUtil.isSelectorType(type));
	}

	// public StructuredDocumentEvent
	// checkForCrossStructuredDocumentRegionBoundryCases() {
	// return specialPositionUpdate(fStart, fStart + fLengthToReplace - 1);
	// }
	/**
	 * 
	 */
	private ReparseRange getUpdateRangeForPair(int start, int end, String opener, String closer) {
		StringBuffer deletionBuf = new StringBuffer();
		StringBuffer insertionBuf = new StringBuffer();
		int quoteLen = Math.max(opener.length(), closer.length());
		if (1 < quoteLen) {
			/**
			 * ex) opener = "ABC", closer = "XYZ" model: ABCDEF.......UVWXYZ
			 * deletion: CDEF.......UVWX pre/post: AB / YZ
			 */
			int addStart = start - (quoteLen - 1);
			if (0 <= addStart) {
				String addStr = fStructuredDocument.get(addStart, quoteLen - 1);
				deletionBuf.append(addStr);
				insertionBuf.append(addStr);
			}
			deletionBuf.append(fDeletedText);
			insertionBuf.append(fChanges);
			if (end + (quoteLen - 1) < fStructuredDocument.getLength()) {
				String addStr = fStructuredDocument.get(end + 1, quoteLen - 1);
				deletionBuf.append(addStr);
				insertionBuf.append(addStr);
			}
		}
		else {
			deletionBuf.append(fDeletedText);
			insertionBuf.append(fChanges);
		}
		String deletion = deletionBuf.toString();
		String insertion = insertionBuf.toString();

		int rangeStart = start;
		int rangeEnd = end;

		if (0 <= deletion.indexOf(opener) || 0 <= deletion.indexOf(closer) || 0 <= insertion.indexOf(opener) || 0 <= insertion.indexOf(closer)) {
			int s, e;
			try {
				if (start <= fStructuredDocument.getLength()) {
					IRegion startRegion = getFindReplaceDocumentAdapter().find(start - 1, opener, false, false, false, false);
					if (startRegion != null) {
						s = startRegion.getOffset();
					}
					else {
						s = -1;
					}
				}
				else {
					s = -1;
				}
				if (end < fStructuredDocument.getLength() - 1) {
					IRegion endRegion = getFindReplaceDocumentAdapter().find(end + 1, closer, true, false, false, false);
					if (endRegion != null) {
						e = endRegion.getOffset();
					}
					else {
						e = -1;
					}
				}
				else {
					e = -1;
				}
			}
			catch (BadLocationException ex) {
				Logger.logException(ex);
				return null;
			}
			if (0 <= s) {
				rangeStart = Math.min(rangeStart, s);
			}
			if (0 <= e) {
				rangeEnd = Math.max(rangeEnd, e);
			}
		}

		if (rangeStart < start || end < rangeEnd) {
			return new ReparseRange(rangeStart, rangeEnd);
		}
		else {
			return null;
		}
	}

	// /**
	// * A method to allow any heuristic "quick checks" that might cover many
	// * many cases, before expending the time on a full reparse.
	// *
	// */
	// public StructuredDocumentEvent quickCheck2() {
	// StructuredDocumentEvent result = null;
	// int end = fStart + fLengthToReplace - 1;
	//		
	// result = specialNodeUpdate(fStart, end);
	//
	// if (result == null) {
	// result = super.quickCheck();
	// }
	//
	// return result;
	// }

	// /**
	// *
	// */
	// private StructuredDocumentEvent specialNodeUpdate(int start, int end) {
	// int reparseStart = start;
	// int reparseEnd = end;
	// IStructuredDocumentRegion targetNode;
	// IStructuredDocumentRegion node;
	// // targetNode = skipLeadingBlank(node);
	// // if (targetNode != null && targetNode != node &&
	// // (targetNode instanceof
	// // com.ibm.sed.structuredDocument.impl.IStructuredDocumentRegion)) {
	// node = fStructuredDocument.getRegionAtCharacterOffset(start);
	// targetNode = CSSUtil.findPreviousSignificantNode(node);
	// if (targetNode != null && targetNode != node) {
	// reparseStart = Math.min(reparseStart, targetNode.getStart());
	// }
	//
	// // targetNode = skipTrailingBlank(node);
	// // if (targetNode != null && targetNode != node &&
	// // (targetNode instanceof
	// // com.ibm.sed.structuredDocument.impl.IStructuredDocumentRegion)) {
	// node = fStructuredDocument.getRegionAtCharacterOffset(end);
	// targetNode = CSSUtil.findNextSignificantNode(node);
	// if (targetNode != null && targetNode != node) {
	// reparseEnd = Math.max(reparseEnd, targetNode.getEnd());
	// }
	//
	// if (reparseStart != start || reparseEnd != end) {
	// return reparse(reparseStart, reparseEnd);
	// } else {
	// return null;
	// }
	// }
	// /**
	// * If following charater(s) is in changes, It is necessary to parse
	// nodes
	// * which are in forward and/or backward.
	// *
	// * openers : [, (, \/\* closers : ], ), \*\/ quotes : ", '
	// */
	// private StructuredDocumentEvent specialPositionUpdate(int start, int
	// end) {
	// String[] openers = { "[", "(", "/*", "\"", "'", "<%" };
	// //$NON-NLS-6$//$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	// String[] closers = { "]", ")", "*/", "\"", "'", "%>" };
	// //$NON-NLS-6$//$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	//
	// int rangeStart = Integer.MAX_VALUE;
	// int rangeEnd = 0;
	//
	// for (int i = 0; i < openers.length && i < closers.length; i++) {
	// ReparseRange range = getUpdateRangeForPair(start, end, openers[i],
	// closers[i]);
	// if (range != null) {
	// rangeStart = Math.min(rangeStart, range.getStart());
	// rangeEnd = Math.max(rangeEnd, range.getEnd());
	// break;
	// }
	// }
	//
	// if (rangeStart < rangeEnd) {
	// return reparse(rangeStart, rangeEnd);
	// } else {
	// return null;
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.model.internal.text.StructuredDocumentReParser#newInstance()
	 */
	public IStructuredTextReParser newInstance() {
		return new CSSStructuredDocumentReParser();
	}
}