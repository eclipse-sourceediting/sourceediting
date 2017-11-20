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
import org.eclipse.emf.common.command.CompoundCommand;



public class StructuredTextCompoundCommandImpl extends CompoundCommand implements CommandCursorPosition {
	protected int fRedoCursorPosition = -1;
	protected int fRedoSelectionLength = 0;

	protected int fUndoCursorPosition = -1;
	protected int fUndoSelectionLength = 0;

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 */
	public StructuredTextCompoundCommandImpl() {
		super();
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param resultIndex
	 *            int
	 */
	public StructuredTextCompoundCommandImpl(int resultIndex) {
		super(resultIndex);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param resultIndex
	 *            int
	 * @param commandList
	 *            java.util.List
	 */
	public StructuredTextCompoundCommandImpl(int resultIndex, java.util.List commandList) {
		super(resultIndex, commandList);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param resultIndex
	 *            int
	 * @param label
	 *            java.lang.String
	 */
	public StructuredTextCompoundCommandImpl(int resultIndex, String label) {
		super(resultIndex, label);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param resultIndex
	 *            int
	 * @param label
	 *            java.lang.String
	 * @param commandList
	 *            java.util.List
	 */
	public StructuredTextCompoundCommandImpl(int resultIndex, String label, java.util.List commandList) {
		super(resultIndex, label, commandList);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param resultIndex
	 *            int
	 * @param label
	 *            java.lang.String
	 * @param description
	 *            java.lang.String
	 */
	public StructuredTextCompoundCommandImpl(int resultIndex, String label, String description) {
		super(resultIndex, label, description);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param resultIndex
	 *            int
	 * @param label
	 *            java.lang.String
	 * @param description
	 *            java.lang.String
	 * @param commandList
	 *            java.util.List
	 */
	public StructuredTextCompoundCommandImpl(int resultIndex, String label, String description, java.util.List commandList) {
		super(resultIndex, label, description, commandList);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param commandList
	 *            java.util.List
	 */
	public StructuredTextCompoundCommandImpl(java.util.List commandList) {
		super(commandList);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param label
	 *            java.lang.String
	 */
	public StructuredTextCompoundCommandImpl(String label) {
		super(label);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param label
	 *            java.lang.String
	 * @param commandList
	 *            java.util.List
	 */
	public StructuredTextCompoundCommandImpl(String label, java.util.List commandList) {
		super(label, commandList);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param label
	 *            java.lang.String
	 * @param description
	 *            java.lang.String
	 */
	public StructuredTextCompoundCommandImpl(String label, String description) {
		super(label, description);
	}

	/**
	 * StructuredTextCompoundCommandImpl constructor comment.
	 * 
	 * @param label
	 *            java.lang.String
	 * @param description
	 *            java.lang.String
	 * @param commandList
	 *            java.util.List
	 */
	public StructuredTextCompoundCommandImpl(String label, String description, java.util.List commandList) {
		super(label, description, commandList);
	}

	/**
	 * Returns the cursor position to be set to after this command is redone.
	 * 
	 * @return int
	 */
	public int getRedoCursorPosition() {
		int cursorPosition = -1;

		if (fRedoCursorPosition != -1)
			cursorPosition = fRedoCursorPosition;
		else if (!commandList.isEmpty()) {
			int commandListSize = commandList.size();
			Command lastCommand = (Command) commandList.get(commandListSize - 1);

			if (lastCommand instanceof CommandCursorPosition)
				cursorPosition = ((CommandCursorPosition) lastCommand).getRedoCursorPosition();
		}

		return cursorPosition;
	}

	/**
	 * Returns the length of text to be selected after this command is redone.
	 * 
	 * @return int
	 */
	public int getRedoSelectionLength() {
		return fRedoSelectionLength;
	}

	/**
	 * Returns the cursor position to be set to after this command is undone.
	 * 
	 * @return int
	 */
	public int getUndoCursorPosition() {
		int cursorPosition = -1;

		if (fUndoCursorPosition != -1)
			cursorPosition = fUndoCursorPosition;
		else if (!commandList.isEmpty()) {
			// never used
			//int commandListSize = commandList.size();
			Command firstCommand = (Command) commandList.get(0);

			if (firstCommand instanceof CommandCursorPosition)
				cursorPosition = ((CommandCursorPosition) firstCommand).getUndoCursorPosition();
		}

		return cursorPosition;
	}

	/**
	 * Returns the length of text to be selected after this command is undone.
	 * 
	 * @return int
	 */
	public int getUndoSelectionLength() {
		return fUndoSelectionLength;
	}

	/**
	 * Sets the cursor position to be used after this command is redone.
	 */
	public void setRedoCursorPosition(int cursorPosition) {
		fRedoCursorPosition = cursorPosition;
	}

	/**
	 * Sets the length of text to be selected after this command is redone.
	 */
	public void setRedoSelectionLength(int selectionLength) {
		fRedoSelectionLength = selectionLength;
	}

	/**
	 * Sets the cursor position to be used after this command is undone.
	 */
	public void setUndoCursorPosition(int cursorPosition) {
		fUndoCursorPosition = cursorPosition;
	}

	/**
	 * Sets the length of text to be selected after this command is undone.
	 */
	public void setUndoSelectionLength(int selectionLength) {
		fUndoSelectionLength = selectionLength;
	}
}
