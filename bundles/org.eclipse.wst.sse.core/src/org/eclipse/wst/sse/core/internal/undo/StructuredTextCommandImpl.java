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
package org.eclipse.wst.sse.core.internal.undo;



import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;


public class StructuredTextCommandImpl extends AbstractCommand implements StructuredTextCommand {

	protected IDocument fDocument = null; // needed for updating the text
	protected String fTextDeleted = null;
	protected int fTextEnd = -1;
	protected String fTextInserted = null;
	protected int fTextStart = -1;

	/**
	 * We have no-arg constructor non-public to force document to be specfied.
	 *  
	 */
	protected StructuredTextCommandImpl() {
		super();
	}

	public StructuredTextCommandImpl(IDocument document) {
		this();
		fDocument = document; // needed for updating the text
	}

	public void execute() {
	}

	/**
	 * getTextDeleted method comment.
	 */
	public java.lang.String getTextDeleted() {
		return fTextDeleted;
	}

	/**
	 * textEnd is the same as (textStart + textInserted.length())
	 */
	public int getTextEnd() {
		return fTextEnd;
	}

	/**
	 * getTextInserted method comment.
	 */
	public java.lang.String getTextInserted() {
		return fTextInserted;
	}

	/**
	 * getTextStart method comment.
	 */
	public int getTextStart() {
		return fTextStart;
	}

	protected boolean prepare() {
		return true;
	}

	public void redo() {
		if (fDocument instanceof IStructuredDocument) {
			// note: one of the few places we programatically ignore read-only
			// settings
			((IStructuredDocument) fDocument).replaceText(this, fTextStart, fTextDeleted.length(), fTextInserted, true);
		} else {
			try {
				fDocument.replace(fTextStart, fTextDeleted.length(), fTextInserted);
			} catch (BadLocationException e) {
				// assumed impossible, for now
				Logger.logException(e);
			}
		}
	}

	/**
	 * setTextDeleted method comment.
	 */
	public void setTextDeleted(java.lang.String textDeleted) {
		fTextDeleted = textDeleted;
	}

	/**
	 * setTextEnd method comment.
	 */
	public void setTextEnd(int textEnd) {
		fTextEnd = textEnd;
	}

	/**
	 * setTextInserted method comment.
	 */
	public void setTextInserted(java.lang.String textInserted) {
		fTextInserted = textInserted;
	}

	/**
	 * setTextStart method comment.
	 */
	public void setTextStart(int textStart) {
		fTextStart = textStart;
	}

	public void undo() {
		if (fDocument instanceof IStructuredDocument) {
			// note: one of the few places we programatically ignore read-only
			// settings
			((IStructuredDocument) fDocument).replaceText(this, fTextStart, fTextInserted.length(), fTextDeleted, true);
		} else {
			try {
				fDocument.replace(fTextStart, fTextInserted.length(), fTextDeleted);
			} catch (BadLocationException e) {
				// assumed impossible, for now
				Logger.logException(e);
			}
		}
	}
}
