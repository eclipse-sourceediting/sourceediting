/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.xml.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;

public class UncommentActionXML extends CommentActionXML {
	public UncommentActionXML(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	protected void processAction() {
		fModel.beginRecording(this, XMLUIMessages.Uncomment_tooltip);
		fModel.aboutToChangeModel();

		for (int i = fSelectionStartLine; i <= fSelectionEndLine; i++) {
			try {
				if (fDocument.getLineLength(i) > 0 && isCommentLine(i)) {
					int lineOffset = fDocument.getLineOffset(i);
					IRegion region = fDocument.getLineInformation(i);
					String string = fDocument.get(region.getOffset(), region.getLength());
					int openCommentOffset = lineOffset + string.indexOf(OPEN_COMMENT);
					int closeCommentOffset = lineOffset + string.indexOf(CLOSE_COMMENT) - OPEN_COMMENT.length();
					uncomment(openCommentOffset, closeCommentOffset);
				}
			} catch (BadLocationException e) {
				throw new SourceEditingRuntimeException();
			}
		}

		fModel.changedModel();
		fModel.endRecording(this);
	}

	protected void uncomment(int openCommentOffset, int closeCommentOffset) {
		try {
			fDocument.replace(openCommentOffset, OPEN_COMMENT.length(), ""); //$NON-NLS-1$
			fDocument.replace(closeCommentOffset, CLOSE_COMMENT.length(), ""); //$NON-NLS-1$
		} catch (BadLocationException e) {
			throw new SourceEditingRuntimeException();
		}
	}
}
