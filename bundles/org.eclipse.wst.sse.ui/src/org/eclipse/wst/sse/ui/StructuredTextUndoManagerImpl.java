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
package org.eclipse.wst.sse.ui;



import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.IModelManager;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.events.NewModelEvent;
import org.eclipse.wst.sse.core.events.NoChangeEvent;
import org.eclipse.wst.sse.core.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.text.IStructuredDocument;
import org.eclipse.wst.sse.core.undo.CommandCursorPosition;
import org.eclipse.wst.sse.core.undo.StructuredTextCommand;
import org.eclipse.wst.sse.core.undo.StructuredTextUndoManager;
import org.eclipse.wst.sse.core.util.Utilities;
import org.eclipse.wst.sse.ui.internal.undo.StructuredTextCommandImpl;
import org.eclipse.wst.sse.ui.internal.undo.StructuredTextCompoundCommandImpl;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.view.events.ITextSelectionListener;
import org.eclipse.wst.sse.ui.view.events.TextSelectionChangedEvent;


public class StructuredTextUndoManagerImpl implements CommandStackListener, StructuredTextUndoManager, ITextSelectionListener {


	class UndoManagerStructuredDocumentListener implements IStructuredDocumentListener {


		public void newModel(NewModelEvent structuredDocumentEvent) {
			// Do nothing. Do not push the new model's structuredDocument changes onto the undo command stack,
			// or else the user may be able to undo an existing file to an empty file.
		}

		public void noChange(NoChangeEvent structuredDocumentEvent) {
			// Since "no change", do nothing.
		}

		public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
			processStructuredDocumentEvent(structuredDocumentEvent);
		}

		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
			processStructuredDocumentEvent(structuredDocumentEvent);
		}

		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
			processStructuredDocumentEvent(structuredDocumentEvent);
		}

	}

	/** Translatable strings */
	protected static final String TEXT_CHANGE_TEXT = ResourceHandler.getString("Text_Change_UI_"); // used in undo/redo action text/desc //$NON-NLS-1$ = "Text Change"
	protected ITextViewer[] fTextViewers = null;
	protected IStructuredModel fStructuredModel = null;
	protected CommandStack fCommandStack = null;
	protected StructuredTextCompoundCommandImpl fCompoundCommand = null;
	protected StructuredTextCommandImpl fTextCommand = null;
	protected boolean fRecording = false;
	protected boolean fUndoManagementEnabled = true;
	protected Object fRequester;
	protected String fCompoundCommandLabel = null;
	protected String fCompoundCommandDescription = null;
	protected int fRecordingCount = 0;
	protected int fUndoCursorPosition = -1;
	protected int fUndoSelectionLength = 0;
	protected int fCursorPosition = 0;
	IStructuredDocumentListener fStructuredDocumentListener = new UndoManagerStructuredDocumentListener();

	public StructuredTextUndoManagerImpl() {
		this(new BasicCommandStack());
	}

	public StructuredTextUndoManagerImpl(CommandStack commandStack) {
		setCommandStack(commandStack);
	}

	protected void addTextViewer(ITextViewer textViewer) {
		if (!Utilities.contains(fTextViewers, textViewer)) {
			int oldSize = 0;

			if (fTextViewers != null) {
				// normally won't be null, but we need to be sure, for first time through
				oldSize = fTextViewers.length;
			}

			int newSize = oldSize + 1;
			ITextViewer[] newTextViewers = new ITextViewer[newSize];
			if (fTextViewers != null) {
				System.arraycopy(fTextViewers, 0, newTextViewers, 0, oldSize);
			}

			// add the new text viewer to last position
			newTextViewers[newSize - 1] = textViewer;

			// now switch new for old
			fTextViewers = newTextViewers;
		}
		else {
			removeTextViewer(textViewer);
			addTextViewer(textViewer);
		}
	}

	public void beginCompoundChange() {
		if (fStructuredModel != null)
			fStructuredModel.aboutToChangeModel();

		if (fRecording)
			beginRecording(this, fCompoundCommandLabel, fCompoundCommandDescription, fUndoCursorPosition, fUndoSelectionLength);
		else
			beginRecording(this);
	}

	public void beginRecording(Object requester) {
		beginRecording(requester, null, null);
	}

	public void beginRecording(Object requester, int cursorPosition, int selectionLength) {
		beginRecording(requester, null, null);

		fUndoCursorPosition = cursorPosition;
		fUndoSelectionLength = selectionLength;
	}

	public void beginRecording(Object requester, String label) {
		beginRecording(requester, label, null);
	}

	public void beginRecording(Object requester, String label, int cursorPosition, int selectionLength) {
		beginRecording(requester, label, null);

		fUndoCursorPosition = cursorPosition;
		fUndoSelectionLength = selectionLength;
	}

	public void beginRecording(Object requester, String label, String description) {
		// save the requester
		fRequester = requester;

		// update label and desc only on the first level when recording is nested
		if (fRecordingCount == 0) {
			fCompoundCommandLabel = label;
			if (fCompoundCommandLabel == null)
				fCompoundCommandLabel = TEXT_CHANGE_TEXT;

			fCompoundCommandDescription = description;
			if (fCompoundCommandDescription == null)
				fCompoundCommandDescription = TEXT_CHANGE_TEXT;

			// clear commands
			fTextCommand = null;
			fCompoundCommand = null;
		}

		// update counter and flag
		fRecordingCount++;
		fRecording = true;

		// no undo cursor position and undo selection length specified
		// reset undo cursor position and undo selection length
		fUndoCursorPosition = -1;
		fUndoSelectionLength = 0;
	}

	public void beginRecording(Object requester, String label, String description, int cursorPosition, int selectionLength) {
		beginRecording(requester, label, description);

		fUndoCursorPosition = cursorPosition;
		fUndoSelectionLength = selectionLength;
	}

	protected void checkRequester(Object requester) {
		if ((fRequester != null) && (!fRequester.equals(requester))) {
			// Force restart of recording so the last compound command is closed.
			//
			// However, we should not force restart of recording when the request came from StructuredDocumentToTextAdapter or XMLModelImpl
			// because cut/paste requests and character inserts to the textViewer are from StructuredDocumentToTextAdapter,
			// and requests to delete a node in the XMLTableTreeViewer are from XMLModelImpl (which implements IStructuredModel).
			if (!((requester instanceof StructuredDocumentToTextAdapter) || (requester instanceof IStructuredModel) || (requester instanceof IStructuredDocument))) {
				if (fRecording)
					endRecording(fRequester);
				else
					resetInternalCommands();

				// save the requester
				fRequester = requester;
			}
		}
	}

	/**
	 * This is called with the {@link CommandStack}'s state has changed.
	 */
	public void commandStackChanged(java.util.EventObject event) {
		resetInternalCommands();
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#connect
	 */
	public void connect(ITextViewer textViewer) {
		addTextViewer(textViewer);

		if (fStructuredModel == null) {
			// connect to model
			IModelManagerPlugin modelManagerPlugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
			IModelManager modelManager = modelManagerPlugin.getModelManager();
			fStructuredModel = modelManager.getExistingModelForRead(textViewer.getDocument());
			IStructuredDocument structuredDocument = fStructuredModel.getStructuredDocument();
			structuredDocument.addModelChangedListener(fStructuredDocumentListener);
		}
	}

	protected void createNewTextCommand(String textDeleted, String textInserted, int textStart, int textEnd) {
		StructuredTextCommandImpl textCommand = new org.eclipse.wst.sse.ui.internal.undo.StructuredTextCommandImpl(fStructuredModel.getStructuredDocument());
		textCommand.setLabel(TEXT_CHANGE_TEXT);
		textCommand.setDescription(TEXT_CHANGE_TEXT);
		textCommand.setTextStart(textStart);
		textCommand.setTextEnd(textEnd);
		textCommand.setTextDeleted(textDeleted);
		textCommand.setTextInserted(textInserted);

		if (fRecording) {
			if (fCompoundCommand == null) {
				StructuredTextCompoundCommandImpl compoundCommand = new StructuredTextCompoundCommandImpl();
				// undo cursor position and undo selection length were set in beginRecord
				// they were saved until the first text command (because empty compound cannot be pushed onto the stack)
				if (fUndoCursorPosition > -1) {
					compoundCommand.setUndoCursorPosition(fUndoCursorPosition);
					compoundCommand.setUndoSelectionLength(fUndoSelectionLength);
				}
				else {
					compoundCommand.setUndoCursorPosition(textStart);
					compoundCommand.setUndoSelectionLength(textDeleted.length());
				}
				// reset undo cursor position and undo selection length
				fUndoCursorPosition = -1;
				fUndoSelectionLength = 0;

				compoundCommand.setLabel(fCompoundCommandLabel);
				compoundCommand.setDescription(fCompoundCommandDescription);
				compoundCommand.append(textCommand);
				fCommandStack.execute(compoundCommand);

				fCompoundCommand = compoundCommand;
			}
			else {
				fCompoundCommand.append(textCommand);
			}
		}
		else {
			fCommandStack.execute(textCommand);
		}

		fTextCommand = textCommand;
	}

	/**
	 * Disable undo management.
	 */
	public void disableUndoManagement() {
		fUndoManagementEnabled = false;
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#disconnect
	 */
	public void disconnect() {
		removeTextViewers();

		if ((fTextViewers == null || fTextViewers.length == 0) && fStructuredModel != null)
			disconnectFromModel();
	}

	public void disconnect(ITextViewer textViewer) {
		removeTextViewer(textViewer);

		if ((fTextViewers == null || fTextViewers.length == 0) && fStructuredModel != null)
			disconnectFromModel();
	}

	protected void disconnectFromModel() {
		IStructuredDocument structuredDocument = fStructuredModel.getStructuredDocument();
		structuredDocument.removeModelChangedListener(fStructuredDocumentListener);
		fStructuredModel.releaseFromRead();
		fStructuredModel = null;
	}

	/**
	 * Enable undo management.
	 */
	public void enableUndoManagement() {
		fUndoManagementEnabled = true;
	}

	public void endCompoundChange() {
		endRecording(this);

		if (fStructuredModel != null)
			fStructuredModel.changedModel();
	}

	public void endRecording(Object requester) {
		int cursorPosition = (fTextCommand != null) ? fTextCommand.getTextEnd() : -1;
		int selectionLength = 0;

		endRecording(requester, cursorPosition, selectionLength);
	}

	public void endRecording(Object requester, int cursorPosition, int selectionLength) {
		if (fCompoundCommand != null) {
			fCompoundCommand.setRedoCursorPosition(cursorPosition);
			fCompoundCommand.setRedoSelectionLength(selectionLength);
		}

		// end recording is a logical stopping point for text command,
		// even when fRecordingCount > 0 (in nested beginRecording)
		fTextCommand = null;

		// update counter and flag
		if (fRecordingCount > 0)
			fRecordingCount--;
		if (fRecordingCount == 0) {
			fRecording = false;

			// reset compound command only when fRecordingCount == 0
			fCompoundCommand = null;
			fCompoundCommandLabel = null;
			fCompoundCommandDescription = null;
		}
	}

	public CommandStack getCommandStack() {
		return fCommandStack;
	}

	/**
	 * Get the redo command.
	 */
	public Command getRedoCommand() {
		return fCommandStack.getRedoCommand();
	}

	/**
	 * Get the last text viewer the undo manager is connected to.
	 */
	public ITextViewer getTextViewer() {
		if ((fTextViewers != null) && (fTextViewers.length > 0))
			return fTextViewers[fTextViewers.length - 1];
		else
			return null;
	}

	/**
	 * Get the undo command.
	 */
	public Command getUndoCommand() {
		return fCommandStack.getUndoCommand();
	}

	protected void processStructuredDocumentEvent(StructuredDocumentEvent structuredDocumentEvent) {
		// Note: fListening tells us if we should listen to the StructuredDocumentEvent.
		// fListening is set to false right before the undo/redo process and then set to true again
		// right after the undo/redo process to block out and ignore all StructuredDocumentEvents generated
		// by the undo/redo process.

		// Process StructuredDocumentEvent if fListening is true.
		//
		// We are executing a command from the command stack if the requester is a command (for example, undo/redo).
		// We should not process the flat model event when we are executing a command from the command stack.
		if (fUndoManagementEnabled && !(structuredDocumentEvent.getOriginalSource() instanceof Command)) {
			// check requester
			checkRequester(structuredDocumentEvent.getOriginalSource());

			// process the structuredDocumentEvent
			String textDeleted = structuredDocumentEvent.getDeletedText();
			String textInserted = structuredDocumentEvent.getText();
			int textStart = structuredDocumentEvent.getOriginalStart();
			int textEnd = textStart + textInserted.length();
			processStructuredDocumentEvent(textDeleted, textInserted, textStart, textEnd);
		}
	}

	protected void processStructuredDocumentEvent(String textDeleted, String textInserted, int textStart, int textEnd) {
		if ((fTextCommand != null) && (textStart == fTextCommand.getTextEnd())) {
			// append to the text command
			fTextCommand.setTextDeleted(fTextCommand.getTextDeleted().concat(textDeleted));
			fTextCommand.setTextInserted(fTextCommand.getTextInserted().concat(textInserted));
			fTextCommand.setTextEnd(textEnd);
		}
		else if (
		// 267437 - undo string messed up when backspace and delete are intermixed
		(fTextCommand != null) && (textStart == fTextCommand.getTextStart() - (textEnd - textStart + 1)) && (textEnd <= fTextCommand.getTextEnd() - (textEnd - textStart + 1)) && (textDeleted.length() == 1) && (textInserted.length() == 0)) {
			// backspace pressed

			// erase a character just inserted
			if (fTextCommand.getTextInserted().length() > 0) {
				fTextCommand.setTextInserted(fTextCommand.getTextInserted().substring(0, fTextCommand.getTextEnd() - fTextCommand.getTextStart() - 1));
				fTextCommand.setTextEnd(textEnd);
			}
			// erase a character in the file
			else {
				fTextCommand.setTextDeleted(textDeleted.concat(fTextCommand.getTextDeleted()));
				fTextCommand.setTextStart(textStart);
			}
		}
		else {
			createNewTextCommand(textDeleted, textInserted, textStart, textEnd);
		}

		// save cursor position
		fCursorPosition = textEnd;
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#redo
	 */
	public void redo() {
		if (redoable()) {
			try {
				if (fStructuredModel != null)
					fStructuredModel.aboutToChangeModel();

				Command redoCommand = getRedoCommand();

				fCommandStack.redo(); // make sure to redo before setting cursor

				// set cursor if cursorPosition is available
				if (redoCommand instanceof CommandCursorPosition) {
					int cursorPosition = ((CommandCursorPosition) redoCommand).getRedoCursorPosition();
					int selectionLength = ((CommandCursorPosition) redoCommand).getRedoSelectionLength();

					if (getTextViewer() != null && cursorPosition > -1)
						getTextViewer().setSelectedRange(cursorPosition, selectionLength);
				}
				else if (redoCommand instanceof StructuredTextCommand) {
					StructuredTextCommand structuredTextRedoCommand = (StructuredTextCommand) redoCommand;
					int cursorPosition = structuredTextRedoCommand.getTextStart() + structuredTextRedoCommand.getTextInserted().length();

					if (getTextViewer() != null && cursorPosition > -1)
						getTextViewer().setSelectedRange(cursorPosition, 0);
				}
				// else leave cursor alone for other commands

			}
			finally {
				if (fStructuredModel != null)
					fStructuredModel.changedModel();
			}
		}
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#redoable
	 */
	public boolean redoable() {
		return fCommandStack.canRedo();
	}

	protected void removeTextViewer(ITextViewer textViewer) {
		if ((fTextViewers != null) && (textViewer != null)) {
			// if its not in the array, we'll ignore the request
			if (Utilities.contains(fTextViewers, textViewer)) {
				int oldSize = fTextViewers.length;
				int newSize = oldSize - 1;
				ITextViewer[] newTextViewers = new ITextViewer[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fTextViewers[i] == textViewer) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newTextViewers[index++] = fTextViewers[i];
					}
				}
				// now that we have a new array, let's switch it for the old one
				fTextViewers = newTextViewers;
			}
		}
	}

	protected void removeTextViewers() {
		if ((fTextViewers != null) && (fTextViewers.length > 0)) {
			for (int i = 0; i < fTextViewers.length; i++) {
				removeTextViewer(fTextViewers[i]);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#reset
	 */
	public void reset() {
		if (fCommandStack != null)
			fCommandStack.flush();
	}

	protected void resetInternalCommands() {
		fCompoundCommand = null;
		fTextCommand = null;
	}

	public void setCommandStack(CommandStack commandStack) {
		if (fCommandStack != null)
			fCommandStack.removeCommandStackListener(this);

		fCommandStack = commandStack;

		if (fCommandStack != null)
			fCommandStack.addCommandStackListener(this);
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#setMaximalUndoLevel
	 */
	public void setMaximalUndoLevel(int undoLevel) {
		// Do nothing. StructuredTextUndoManager supports unlimited undo level.
	}

	public void textSelectionChanged(TextSelectionChangedEvent event) {
		if (event.getSource() == getTextViewer().getTextWidget()) {
			// only listen to textSelectionChanged event from text widget
			// ignore textSelectionChanged event caused by outline view selection, for example

			int newCursorPosition = event.getTextSelectionStart();
			if (newCursorPosition != fCursorPosition)
				// textSelectionChanged is a logical stopping point for text command
				fTextCommand = null;
		}
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#undo
	 */
	public void undo() {
		// Force an endRecording before undo.
		//
		// For example, recording was turned on on the Design Page of PageDesigner.
		// Then undo is invoked on the Source Page. Recording should be stopped before we undo.
		// Note that redo should not be available when we switch to the Source Page.
		// Therefore, this force ending of recording is not needed in redo.
		if (fRecording)
			endRecording(this);

		if (undoable()) {
			try {
				if (fStructuredModel != null)
					fStructuredModel.aboutToChangeModel();

				Command undoCommand = getUndoCommand();

				fCommandStack.undo(); // make sure to undo before setting cursor

				// set cursor if cursorPosition is available
				if (undoCommand instanceof CommandCursorPosition) {
					int cursorPosition = ((CommandCursorPosition) undoCommand).getUndoCursorPosition();
					int selectionLength = ((CommandCursorPosition) undoCommand).getUndoSelectionLength();

					if (getTextViewer() != null && cursorPosition > -1)
						getTextViewer().setSelectedRange(cursorPosition, selectionLength);
				}
				else if (undoCommand instanceof StructuredTextCommand) {
					StructuredTextCommand structuredTextUndoCommand = (StructuredTextCommand) undoCommand;
					int cursorPosition = structuredTextUndoCommand.getTextStart() + structuredTextUndoCommand.getTextDeleted().length();

					if (getTextViewer() != null && cursorPosition > -1)
						getTextViewer().setSelectedRange(cursorPosition, 0);
				}
				// else leave cursor alone for other commands
			}
			finally {
				if (fStructuredModel != null)
					fStructuredModel.changedModel();
			}
		}
	}

	/**
	 * @see org.eclipse.jface.text.IUndoManager#undoable
	 */
	public boolean undoable() {
		return fCommandStack.canUndo();
	}
}
