/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.internal.undo;



import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.undo.StructuredTextCommand;

public class StructuredTextCommandImpl extends AbstractCommand implements StructuredTextCommand {

	protected IStructuredDocument fStructuredDocument = null; // needed for updating the text
	protected String fTextDeleted = null;
	protected String fTextInserted = null;
	protected int fTextStart = -1;
	protected int fTextEnd = -1;

	public StructuredTextCommandImpl(IStructuredDocument structuredDocument) {
		super();

		fStructuredDocument = structuredDocument; // needed for updating the text
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
		if (fStructuredDocument != null)
			fStructuredDocument.replaceText(this, fTextStart, fTextDeleted.length(), fTextInserted);
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
		if (fStructuredDocument != null)
			fStructuredDocument.replaceText(this, fTextStart, fTextInserted.length(), fTextDeleted);
	}
}
