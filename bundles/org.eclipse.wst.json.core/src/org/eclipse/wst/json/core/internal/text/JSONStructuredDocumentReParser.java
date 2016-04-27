/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.css.core.internal.text.CSSStructuredDocumentReParser
 *                                           modified in order to process JSON Objects.
 *     Alina Marin <alina@mx.ibm.com> - added a workaround while we fix the JSON lexical analyzer.                         
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.internal.util.RegionIterator;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredTextReParser;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.text.StructuredDocumentReParser;

/**
 * This class provides a centralized place to put "reparsing" logic. This is the
 * logic that reparses the text incrementally, as a user types in new
 * characters, or JSON nodes are inserted or deleted. Note: it is not a thread
 * safe class.
 */
public class JSONStructuredDocumentReParser extends StructuredDocumentReParser {

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
	public JSONStructuredDocumentReParser() {
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
	@Override
	protected StructuredDocumentEvent checkForCrossStructuredDocumentRegionSyntax() {
		int checkStart = fStart;
		int checkEnd = fStart + fLengthToReplace - 1;
		IStructuredDocumentRegion endRegion = fStructuredDocument
				.getRegionAtCharacterOffset(checkEnd);
		if (endRegion != null) {
			checkEnd = endRegion.getEndOffset();
		}

		ReparseRange range = new ReparseRange(checkStart, checkEnd);

		range.expand(getUpdateRangeForDelimiter(range.getStart(),
				range.getEnd()));
		range.expand(getUpdateRangeForUnknownRegion(range.getStart(),
				range.getEnd()));

		// range.expand(getUpdateRangeForQuotes(range.getStart(),
		// range.getEnd()));
		// range.expand(getUpdateRangeForComments(range.getStart(),
		// range.getEnd()));
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
		RegionIterator iterator = new RegionIterator(fStructuredDocument,
				start - 1);
		if (iterator.hasPrev()) {
			iterator.prev(); // skip myself
		}
		while (iterator.hasPrev()) {
			ITextRegion region = iterator.prev();
			if (region != null
					&& region.getType() == JSONRegionContexts.JSON_UNKNOWN) {
				newStart = iterator.getStructuredDocumentRegion()
						.getStartOffset(region);
			} else {
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
		IStructuredDocumentRegion docRegion = fStructuredDocument
				.getRegionAtCharacterOffset(start);
		if (docRegion == null) {
			return null;
		}
		// if (docRegion.getType() == JSONRegionContexts.JSON_DELIMITER) {
		// IStructuredDocumentRegion prevRegion = docRegion.getPrevious();
		// if (prevRegion != null) {
		// return new ReparseRange(prevRegion.getStart(), end);
		// }
		// }

		return null;
	}

	private ReparseRange getUpdateRangeForQuotes(int start, int end) {
		ReparseRange range = new ReparseRange();

		range.expand(getUpdateRangeForPair(start, end, '\"', '\"'));
		range.expand(getUpdateRangeForPair(start, end, '\'', '\''));

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

		range.expand(getUpdateRangeForPair(start, end, '[', ']'));
		range.expand(getUpdateRangeForPair(start, end, '(', ')'));
		range.expand(getUpdateRangeForPair(start, end, '{', '}'));

		return (range.isValid()) ? range : null;
	}

	private StructuredDocumentEvent checkInsideBrace(int start, int end) {
		StructuredDocumentEvent result = null;
		IStructuredDocumentRegion region = fStructuredDocument
				.getRegionAtCharacterOffset(start);
		if (region == null) {
			return null;
		}
		boolean bDeclaration = false;
		String type = region.getType();
		// if (JSONRegionUtil.isDeclarationType(type) || type ==
		// JSONRegionContexts.JSON_LBRACE || type ==
		// JSONRegionContexts.JSON_RBRACE) {
		// bDeclaration = true;
		// }
		// if (!bDeclaration) {
		// IStructuredDocumentRegion prevRegion =
		// JSONUtil.findPreviousSignificantNode(region);
		// if (prevRegion != null) {
		// type = prevRegion.getType();
		// if (JSONRegionUtil.isDeclarationType(type) || type ==
		// JSONRegionContexts.JSON_LBRACE) {
		// bDeclaration = true;
		// }
		// }
		// }
		// if (!bDeclaration) {
		// IStructuredDocumentRegion nextRegion =
		// JSONUtil.findNextSignificantNode(region);
		// if (nextRegion != null) {
		// type = nextRegion.getType();
		// if (JSONRegionUtil.isDeclarationType(type) || type ==
		// JSONRegionContexts.JSON_RBRACE) {
		// bDeclaration = true;
		// }
		// }
		// }
		bDeclaration = true;
		if (bDeclaration) {
			IStructuredDocumentRegion startRegion = findRuleStart(region);
			if (startRegion != null) {
				IStructuredDocumentRegion endRegion = fStructuredDocument
						.getRegionAtCharacterOffset(end);
				if (endRegion != null) {
					result = reparse(startRegion, endRegion);
				}
			}
		}
		return result;
	}

	private IStructuredDocumentRegion findRuleStart(
			IStructuredDocumentRegion startRegion) {
		// find '{'
//		while (region != null
//				&& (region.getType() != JSONRegionContexts.JSON_OBJECT_OPEN && region
//						.getType() != JSONRegionContexts.JSON_ARRAY_OPEN)) {
//			region = region.getPrevious();
//		}
		/*
		 * Workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=491944
		 * Look for the parent object to make JSONTokenizer get the right tokens
		 * for the JSONSourceParser, this change doesn't affect the performance
		 * or the regions that the JSONModelParser will take
		 */
		IStructuredDocumentRegion region = startRegion.getParentDocument().getFirstStructuredDocumentRegion();
		if (region != null) { // '{' is found
			return region;
		}
		return null;
	}

	private boolean isLeadingDeclarationType(String type) {
		return false; // (type == JSONRegionContexts.JSON_PAGE || type ==
						// JSONRegionContexts.JSON_FONT_FACE ||
						// JSONRegionUtil.isSelectorType(type));
	}

	// public StructuredDocumentEvent
	// checkForCrossStructuredDocumentRegionBoundryCases() {
	// return specialPositionUpdate(fStart, fStart + fLengthToReplace - 1);
	// }
	/**
	 * 
	 */
	private ReparseRange getUpdateRangeForPair(int start, int end,
			String opener, String closer) {
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
		} else {
			deletionBuf.append(fDeletedText);
			insertionBuf.append(fChanges);
		}
		String deletion = deletionBuf.toString();
		String insertion = insertionBuf.toString();

		int rangeStart = start;
		int rangeEnd = end;

		if (0 <= deletion.indexOf(opener) || 0 <= deletion.indexOf(closer)
				|| 0 <= insertion.indexOf(opener)
				|| 0 <= insertion.indexOf(closer)) {
			int s, e;
			try {
				if (start <= fStructuredDocument.getLength()) {
					IRegion startRegion = getFindReplaceDocumentAdapter().find(
							start - 1, opener, false, false, false, false);
					if (startRegion != null) {
						s = startRegion.getOffset();
					} else {
						s = -1;
					}
				} else {
					s = -1;
				}
				if (end < fStructuredDocument.getLength() - 1) {
					IRegion endRegion = getFindReplaceDocumentAdapter().find(
							end + 1, closer, true, false, false, false);
					if (endRegion != null) {
						e = endRegion.getOffset();
					} else {
						e = -1;
					}
				} else {
					e = -1;
				}
			} catch (BadLocationException ex) {
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
		} else {
			return null;
		}
	}

	private int findChar(char c, int start, boolean forward)
			throws BadLocationException {
		int stop = forward ? fStructuredDocument.getLength() : 0;
		if (forward) {
			for (; start < stop; start++) {
				if (fStructuredDocument.getChar(start) == c)
					return start;
			}
		} else {
			for (; start >= stop; start--) {
				if (fStructuredDocument.getChar(start) == c)
					return start;
			}
		}
		return -1;
	}

	ReparseRange getUpdateRangeForPair(int start, int end, char opener,
			char closer) {
		StringBuffer deletionBuf = new StringBuffer();
		StringBuffer insertionBuf = new StringBuffer();
		deletionBuf.append(fDeletedText);
		insertionBuf.append(fChanges);
		String deletion = deletionBuf.toString();
		String insertion = insertionBuf.toString();

		int rangeStart = start;
		int rangeEnd = end;

		if (0 <= deletion.indexOf(opener) || 0 <= deletion.indexOf(closer)
				|| 0 <= insertion.indexOf(opener)
				|| 0 <= insertion.indexOf(closer)) {
			int s = -1, e = -1;
			try {
				if (start <= fStructuredDocument.getLength()) {
					s = findChar(opener, start - 1, false);
				}
				if (end < fStructuredDocument.getLength() - 1) {
					e = findChar(closer, end + 1, true);
				}
			} catch (BadLocationException ex) {
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
		} else {
			return null;
		}
	}

	@Override
	public IStructuredTextReParser newInstance() {
		return new JSONStructuredDocumentReParser();
	}
}
