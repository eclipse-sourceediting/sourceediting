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
package org.eclipse.wst.sse.ui.internal.text;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;


/**
 * Matches the start and ending characters of IStructuredDocumentRegions with
 * the given allowed types. Note that Eclipse R3M8 only paints single
 * character-wide matches and this isn't true pair matching behavior. See RFE
 * #56836 at https://bugs.eclipse.org/bugs/show_bug.cgi?id=56836.
 */
public class DocumentRegionEdgeMatcher implements ICharacterPairMatcher {

	public static final String ID = "characterpairmatcher"; //$NON-NLS-1$

	protected int fAnchor;

	protected ICharacterPairMatcher fNextMatcher;

	protected List fRegionTypes;

	public DocumentRegionEdgeMatcher(String[] validContexts, ICharacterPairMatcher nextMatcher) {
		fRegionTypes = Arrays.asList(validContexts);
		fNextMatcher = nextMatcher;
	}

	/*
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#clear()
	 */
	public void clear() {
		if (fNextMatcher != null)
			fNextMatcher.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#dispose()
	 */
	public void dispose() {
		if (fNextMatcher != null)
			fNextMatcher.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#getAnchor()
	 */
	public int getAnchor() {
		if (fAnchor < 0 && fNextMatcher != null)
			return fNextMatcher.getAnchor();
		return fAnchor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.ICharacterPairMatcher#match(org.eclipse.jface.text.IDocument,
	 *      int)
	 */
	public IRegion match(IDocument document, int offset) {
		if(offset < 0 || offset >= document.getLength())
			return null;
		
		IRegion match = null;
		if (!fRegionTypes.isEmpty() && document instanceof IStructuredDocument) {
			IStructuredDocumentRegion docRegion = ((IStructuredDocument) document).getRegionAtCharacterOffset(offset);
			if (docRegion != null) {
				// look at the previous document region first since its end ==
				// this one's start
				if (docRegion.getPrevious() != null && docRegion.getPrevious().getEndOffset() == offset && fRegionTypes.contains(docRegion.getPrevious().getType())) {
					fAnchor = ICharacterPairMatcher.RIGHT;
					match = new Region(docRegion.getPrevious().getStartOffset(), 1);
				}
				// check for offset in the last text region for a match to
				// document region start offset
				else if (fRegionTypes.contains(docRegion.getType()) && docRegion.getStartOffset(docRegion.getLastRegion()) <= offset && offset <= docRegion.getEndOffset(docRegion.getLastRegion())) {
					fAnchor = ICharacterPairMatcher.RIGHT;
					match = new Region(docRegion.getStartOffset(), 1);
				}
				// check for offset in the first text region for a match to
				// document region end offset
				else if (fRegionTypes.contains(docRegion.getType())) {
					if (docRegion.getStartOffset(docRegion.getFirstRegion()) <= offset && offset <= docRegion.getEndOffset(docRegion.getFirstRegion())) {
						fAnchor = ICharacterPairMatcher.LEFT;
						match = new Region(docRegion.getEndOffset() - 1, 1);
					}
				}
			}
		}
		if (match == null && fNextMatcher != null) {
			fAnchor = -1;
			match = fNextMatcher.match(document, offset);
		}
		return match;
	}
}
