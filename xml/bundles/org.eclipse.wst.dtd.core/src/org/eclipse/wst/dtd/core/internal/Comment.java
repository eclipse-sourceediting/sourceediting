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
import org.eclipse.wst.dtd.core.internal.text.RegionIterator;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;


public class Comment extends NamedTopLevelNode {

	public class StartEndPair {
		public int startOffset, endOffset;
	}

	public Comment(DTDFile file, IStructuredDocumentRegion flatNode) {
		super(file, flatNode, DTDRegionTypes.COMMENT_START);
	}

	public String getImagePath() {
		return DTDResource.COMMENTICON;
	}

	public String getName() {
		String text = getText();
		if (text.length() <= 30) {
			return text;
		}
		else {
			return text.substring(0, 29) + "..."; //$NON-NLS-1$
		}
	}

	private void getStartAndEndOffsetForText(StartEndPair pair) {
		RegionIterator iter = iterator();
		ITextRegion commentStartTag = getStartTag(iter);
		ITextRegion endCommentTag = getNextRegion(iter, DTDRegionTypes.COMMENT_END);
		pair.endOffset = getStructuredDTDDocumentRegion().getEndOffset();
		if (commentStartTag != null) {
			pair.startOffset = getStructuredDTDDocumentRegion().getEndOffset(commentStartTag);
		}
		if (endCommentTag != null) {
			pair.endOffset = getStructuredDTDDocumentRegion().getStartOffset(endCommentTag);
		}
	}

	public String getText() {
		String text = getStructuredDTDDocumentRegion().getText();
		int flatNodeStart = getStructuredDTDDocumentRegion().getStartOffset();
		StartEndPair pair = new StartEndPair();
		getStartAndEndOffsetForText(pair);
		return text.substring(pair.startOffset - flatNodeStart, pair.endOffset - flatNodeStart);
	}

	public void setText(String newText) {
		beginRecording(this, DTDCoreMessages._UI_LABEL_COMMENT_CHG); //$NON-NLS-1$
		StartEndPair pair = new StartEndPair();
		getStartAndEndOffsetForText(pair);
		replaceText(this, pair.startOffset, pair.endOffset - pair.startOffset, newText);
		endRecording(this);
	}

}
