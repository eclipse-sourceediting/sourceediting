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
package org.eclipse.wst.sse.core.undo;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.IUndoManager;



public interface StructuredTextUndoManager extends IUndoManager {

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, int cursorPosition, int selectionLength);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label, int cursorPosition, int selectionLength);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label, String description);

	/**
	 * Begin recording undo transactions.
	 */
	void beginRecording(Object requester, String label, String description, int cursorPosition, int selectionLength);

	/**
	 * Disable undo management.
	 */
	void disableUndoManagement();

	/**
	 * Enable undo management.
	 */
	void enableUndoManagement();

	/**
	 * End recording undo transactions.
	 */
	void endRecording(Object requester);

	/**
	 * End recording undo transactions.
	 */
	void endRecording(Object requester, int cursorPosition, int selectionLength);

	/**
	 * Get the redo command even if it's not committed yet.
	 */
	Command getRedoCommand();

	/**
	 * Get the text viewer the undo manager is connected to.
	 */
	ITextViewer getTextViewer();

	/**
	 * Get the undo command even if it's not committed yet.
	 */
	Command getUndoCommand();

	/**
	 * Set the command stack.
	 */
	void setCommandStack(CommandStack commandStack);

	void disconnect(ITextViewer textViewer);
}
