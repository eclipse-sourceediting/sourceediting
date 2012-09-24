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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;

public interface IStructuredTextUndoManager {

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
	 * Connect the mediator to the undo manager.
	 */
	void connect(IDocumentSelectionMediator mediator);

	/**
	 * Disable undo management.
	 */
	void disableUndoManagement();

	/**
	 * Disconnect the mediator from the undo manager.
	 */
	void disconnect(IDocumentSelectionMediator mediator);

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
	 * <p>
	 * Normally, the undo manager can figure out the best times when to end a
	 * pending command and begin a new one ... to the structure of a structued
	 * document. There are times, however, when clients may wish to override
	 * those algorithms and end one earlier than normal. The one known case is
	 * for multipage editors. If a user is on one page, and type '123' as
	 * attribute value, then click around to other parts of page, or different
	 * pages, then return to '123|' and type 456, then "undo" they typically
	 * expect the undo to just undo what they just typed, the 456, not the
	 * whole attribute value.
	 * <p>
	 * If there is no pending command, the request is ignored.
	 */
	public void forceEndOfPendingCommand(Object requester, int currentPosition, int length);

	/**
	 * Some clients need to do complicated things with undo stack. Plus, in
	 * some cases, if clients setCommandStack temporarily, they have
	 * reponsibility to set back to original one when finished.
	 */
	public CommandStack getCommandStack();

	/**
	 * Get the redo command even if it's not committed yet.
	 */
	Command getRedoCommand();

	/**
	 * Get the undo command even if it's not committed yet.
	 */
	Command getUndoCommand();

	/**
	 * Redo the last command in the undo manager.
	 */
	void redo();

	/**
	 * Redo the last command in the undo manager and notify the requester
	 * about the new selection.
	 */
	void redo(IDocumentSelectionMediator requester);

	/**
	 * Returns whether at least one text change can be repeated. A text change
	 * can be repeated only if it was executed and rolled back.
	 * 
	 * @return <code>true</code> if at least on text change can be repeated
	 */
	boolean redoable();

	/**
	 * Set the command stack.
	 */
	void setCommandStack(CommandStack commandStack);

	/**
	 * Undo the last command in the undo manager.
	 */
	void undo();

	/**
	 * Undo the last command in the undo manager and notify the requester
	 * about the new selection.
	 */
	void undo(IDocumentSelectionMediator requester);

	/**
	 * Returns whether at least one text change can be rolled back.
	 * 
	 * @return <code>true</code> if at least one text change can be rolled
	 *         back
	 */
	boolean undoable();
}
