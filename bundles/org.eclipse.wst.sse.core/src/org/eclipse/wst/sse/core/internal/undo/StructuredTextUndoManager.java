/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Møller - initial IDocumentExtension4 support - #102822
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.undo;

import java.util.EventObject;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.SSECoreMessages;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.events.IStructuredDocumentListener;
import org.eclipse.wst.sse.core.internal.provisional.events.NewDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.NoChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionChangedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.RegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentEvent;
import org.eclipse.wst.sse.core.internal.provisional.events.StructuredDocumentRegionsReplacedEvent;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.util.Assert;
import org.eclipse.wst.sse.core.internal.util.Utilities;

public class StructuredTextUndoManager implements IStructuredTextUndoManager {

	class InternalCommandStackListener implements CommandStackListener {
		public void commandStackChanged(EventObject event) {
			resetInternalCommands();
		}
	}

	class InternalStructuredDocumentListener implements IStructuredDocumentListener {

		public void newModel(NewDocumentEvent structuredDocumentEvent) {
			// Do nothing. Do not push the new model's structuredDocument
			// changes
			// onto the undo command stack, or else the user may be able to
			// undo
			// an existing file to an empty file.
		}

		public void noChange(NoChangeEvent structuredDocumentEvent) {
			// Since "no change", do nothing.
		}

		public void nodesReplaced(StructuredDocumentRegionsReplacedEvent structuredDocumentEvent) {
			processStructuredDocumentEvent(structuredDocumentEvent);
		}

		private void processStructuredDocumentEvent(String textDeleted, String textInserted, int textStart, int textEnd) {
			if (fTextCommand != null && textStart == fTextCommand.getTextEnd()) {
				// append to the text command
				fTextCommand.setTextDeleted(fTextCommand.getTextDeleted().concat(textDeleted));
				fTextCommand.setTextInserted(fTextCommand.getTextInserted().concat(textInserted));
				fTextCommand.setTextEnd(textEnd);
			}
			else if (fTextCommand != null && textStart == fTextCommand.getTextStart() - 1 && textEnd <= fTextCommand.getTextEnd() - 1 && textDeleted.length() == 1 && textInserted.length() == 0 && fTextCommand.getTextDeleted().length() > 0) {
				// backspace pressed
				// erase a character in the file
				fTextCommand.setTextDeleted(textDeleted.concat(fTextCommand.getTextDeleted()));
				fTextCommand.setTextStart(textStart);
			}
			else {
				createNewTextCommand(textDeleted, textInserted, textStart, textEnd);
			}

			// save cursor position
			fCursorPosition = textEnd;
		}

		private void processStructuredDocumentEvent(StructuredDocumentEvent structuredDocumentEvent) {
			// Note: fListening tells us if we should listen to the
			// StructuredDocumentEvent.
			// fListening is set to false right before the undo/redo process
			// and
			// then set to true again
			// right after the undo/redo process to block out and ignore all
			// StructuredDocumentEvents generated
			// by the undo/redo process.

			// Process StructuredDocumentEvent if fListening is true.
			//
			// We are executing a command from the command stack if the
			// requester
			// is a command (for example, undo/redo).
			// We should not process the flat model event when we are
			// executing a
			// command from the command stack.
			if (fUndoManagementEnabled && !(structuredDocumentEvent.getOriginalRequester() instanceof Command)) {
				// check requester if not recording
				if (!fRecording)
					checkRequester(structuredDocumentEvent.getOriginalRequester());

				// process the structuredDocumentEvent
				String textDeleted = structuredDocumentEvent.getDeletedText();
				String textInserted = structuredDocumentEvent.getText();
				int textStart = structuredDocumentEvent.getOffset();
				int textEnd = textStart + textInserted.length();
				processStructuredDocumentEvent(textDeleted, textInserted, textStart, textEnd);
			}
		}

		public void regionChanged(RegionChangedEvent structuredDocumentEvent) {
			processStructuredDocumentEvent(structuredDocumentEvent);
		}

		public void regionsReplaced(RegionsReplacedEvent structuredDocumentEvent) {
			processStructuredDocumentEvent(structuredDocumentEvent);
		}

	}

	private static final String TEXT_CHANGE_TEXT = SSECoreMessages.Text_Change_UI_; //$NON-NLS-1$
	private CommandStack fCommandStack = null;
	private StructuredTextCompoundCommandImpl fCompoundCommand = null;
	private String fCompoundCommandDescription = null;
	private String fCompoundCommandLabel = null;
	int fCursorPosition = 0;
	// private IStructuredModel fStructuredModel = null;
	private IDocument fDocument;
	private InternalCommandStackListener fInternalCommandStackListener;
	// private Map fTextViewerToListenerMap = new HashMap();
	private IStructuredDocumentListener fInternalStructuredDocumentListener;
	private IDocumentSelectionMediator[] fMediators = null;
	private boolean fRecording = false;
	private int fRecordingCount = 0;
	private Object fRequester;
	StructuredTextCommandImpl fTextCommand = null;
	private int fUndoCursorPosition = -1;
	boolean fUndoManagementEnabled = true;
	private int fUndoSelectionLength = 0;

	public StructuredTextUndoManager() {
		this(new BasicCommandStack());
	}

	public StructuredTextUndoManager(CommandStack commandStack) {
		setCommandStack(commandStack);
	}

	private void addDocumentSelectionMediator(IDocumentSelectionMediator mediator) {
		if (!Utilities.contains(fMediators, mediator)) {
			int oldSize = 0;

			if (fMediators != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fMediators.length;
			}

			int newSize = oldSize + 1;
			IDocumentSelectionMediator[] newMediators = new IDocumentSelectionMediator[newSize];
			if (fMediators != null) {
				System.arraycopy(fMediators, 0, newMediators, 0, oldSize);
			}

			// add the new undo mediator to last position
			newMediators[newSize - 1] = mediator;

			// now switch new for old
			fMediators = newMediators;
		}
		else {
			removeDocumentSelectionMediator(mediator);
			addDocumentSelectionMediator(mediator);
		}
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

		// update label and desc only on the first level when recording is
		// nested
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

	void checkRequester(Object requester) {
		if (fRequester != null && !fRequester.equals(requester)) {
			// Force restart of recording so the last compound command is
			// closed.
			//
			// However, we should not force restart of recording when the
			// request came from StructuredDocumentToTextAdapter or
			// XMLModelImpl
			// because cut/paste requests and character inserts to the
			// textViewer are from StructuredDocumentToTextAdapter,
			// and requests to delete a node in the XMLTableTreeViewer are
			// from XMLModelImpl (which implements IStructuredModel).

			if (!(requester instanceof IStructuredModel || requester instanceof IStructuredDocument)) {
				resetInternalCommands();
			}
		}
	}



	public void connect(IDocumentSelectionMediator mediator) {
		Assert.isNotNull(mediator);
		if (fDocument == null) {
			// add this undo manager as structured document listener
			fDocument = mediator.getDocument();
			// future_TODO: eventually we want to refactor or allow either
			// type of document, but for now, we'll do instanceof check, and
			// fail
			// if not right type
			if (fDocument instanceof IStructuredDocument) {
				((IStructuredDocument) fDocument).addDocumentChangedListener(getInternalStructuredDocumentListener());
			}
			else {
				throw new IllegalArgumentException("only meditator with structured documents currently handled"); //$NON-NLS-1$
			}
		}
		else {
			// if we've already had our document set, we'll just do this fail
			// fast integrity check
			if (!fDocument.equals(mediator.getDocument()))
				throw new IllegalStateException("Connection to undo manager failed. Document for document selection mediator inconistent with undo manager."); //$NON-NLS-1$
		}

		addDocumentSelectionMediator(mediator);
	}

	void createNewTextCommand(String textDeleted, String textInserted, int textStart, int textEnd) {
		StructuredTextCommandImpl textCommand = new StructuredTextCommandImpl(fDocument);
		textCommand.setLabel(TEXT_CHANGE_TEXT);
		textCommand.setDescription(TEXT_CHANGE_TEXT);
		textCommand.setTextStart(textStart);
		textCommand.setTextEnd(textEnd);
		textCommand.setTextDeleted(textDeleted);
		textCommand.setTextInserted(textInserted);

		if (fRecording) {
			if (fCompoundCommand == null) {
				StructuredTextCompoundCommandImpl compoundCommand = new StructuredTextCompoundCommandImpl();
				compoundCommand.setUndoCursorPosition(fUndoCursorPosition);
				compoundCommand.setUndoSelectionLength(fUndoSelectionLength);

				compoundCommand.setLabel(fCompoundCommandLabel);
				compoundCommand.setDescription(fCompoundCommandDescription);
				compoundCommand.append(textCommand);

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

	public void disconnect(IDocumentSelectionMediator mediator) {
		removeDocumentSelectionMediator(mediator);

		if (fMediators != null && fMediators.length == 0 && fDocument != null) {
			// remove this undo manager as structured document listener
			// future_TODO: eventually we want to refactor or allow either
			// type of document, but for now, we'll do instanceof check, and
			// fail
			// if not right type
			if (fDocument instanceof IStructuredDocument) {
				((IStructuredDocument) fDocument).removeDocumentChangedListener(getInternalStructuredDocumentListener());
			}
			else {
				throw new IllegalArgumentException("only meditator with structured documents currently handled"); //$NON-NLS-1$
			}
			// if no longer listening to document, then dont even track it
			// anymore
			// (this allows connect to reconnect to document again)
			fDocument = null;
		}
	}

	public void enableUndoManagement() {
		fUndoManagementEnabled = true;
	}

	public void endRecording(Object requester) {
		int cursorPosition = (fTextCommand != null) ? fTextCommand.getTextEnd() : -1;
		int selectionLength = 0;

		endRecording(requester, cursorPosition, selectionLength);
	}

	public void endRecording(Object requester, int cursorPosition, int selectionLength) {
		// Recording could be stopped by forceEndOfPendingCommand(). Make sure
		// we are still recording before proceeding, or else fRecordingCount
		// may not be balanced.
		if (fRecording) {
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
				
				// Finally execute the commands accumulated in the compound command.
				
				if (fCompoundCommand != null) {
					fCommandStack.execute(fCompoundCommand);
				}
				
				fRecording = false;

				// reset compound command only when fRecordingCount ==
				// 0
				fCompoundCommand = null;
				fCompoundCommandLabel = null;
				fCompoundCommandDescription = null;

				// Also reset fRequester
				fRequester = null;
			}
		}
	}

	/**
	 * Utility method to find model given document
	 */
	private IStructuredModel findStructuredModel(IDocument document) {
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IStructuredModel structuredModel = modelManager.getExistingModelForRead(document);
		return structuredModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager#forceEndOfPendingCommand(java.lang.Object,
	 *      int, int)
	 */
	public void forceEndOfPendingCommand(Object requester, int currentPosition, int length) {
		if (fRecording)
			endRecording(requester, currentPosition, length);
		else
			resetInternalCommands();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.core.undo.IStructuredTextUndoManager#getCommandStack()
	 */
	public CommandStack getCommandStack() {
		return fCommandStack;
	}

	/**
	 * @return
	 */
	private CommandStackListener getInternalCommandStackListener() {
		if (fInternalCommandStackListener == null) {
			fInternalCommandStackListener = new InternalCommandStackListener();
		}
		return fInternalCommandStackListener;
	}

	/**
	 * @return
	 */
	private IStructuredDocumentListener getInternalStructuredDocumentListener() {
		if (fInternalStructuredDocumentListener == null) {
			fInternalStructuredDocumentListener = new InternalStructuredDocumentListener();
		}
		return fInternalStructuredDocumentListener;
	}

	public Command getRedoCommand() {
		return fCommandStack.getRedoCommand();
	}

	public Command getUndoCommand() {
		return fCommandStack.getUndoCommand();
	}

	public void redo() {
		redo(null);
	}

	public void redo(IDocumentSelectionMediator requester) {
		IStructuredModel model = findStructuredModel(fDocument);

		if (redoable()) {
			IDocumentExtension4 docExt4 = null;
			DocumentRewriteSession rewriteSession = null;
			try {
				if (model != null)
					model.aboutToChangeModel();

				Command redoCommand = getRedoCommand();
				if (redoCommand instanceof CompoundCommand &&
						model.getStructuredDocument() instanceof IDocumentExtension4) {
					docExt4 = (IDocumentExtension4)model.getStructuredDocument();
				}
				rewriteSession = (docExt4 == null) ? null :
					docExt4.startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED);

				// make sure to redo before setting document selection
				fCommandStack.redo();

				// set document selection
				setRedoDocumentSelection(requester, redoCommand);
			}
			finally {
				if (docExt4 != null && rewriteSession != null)
					docExt4.stopRewriteSession(rewriteSession);
				if (model != null) {
					model.changedModel();
					model.releaseFromRead();
				}
			}
		}
	}

	public boolean redoable() {
		return fCommandStack.canRedo();
	}

	private void removeDocumentSelectionMediator(IDocumentSelectionMediator mediator) {
		if (fMediators != null && mediator != null) {
			// if its not in the array, we'll ignore the request
			if (Utilities.contains(fMediators, mediator)) {
				int oldSize = fMediators.length;
				int newSize = oldSize - 1;
				IDocumentSelectionMediator[] newMediators = new IDocumentSelectionMediator[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fMediators[i] == mediator) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newMediators[index++] = fMediators[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fMediators = newMediators;
			}
		}
	}

	void resetInternalCommands() {
		// Either the requester of the structured document change event is
		// changed, or the command stack is changed. Need to reset internal
		// commands so we won't continue to append changes.
		fCompoundCommand = null;
		fTextCommand = null;

		// Also reset fRequester
		fRequester = null;
	}

	public void setCommandStack(CommandStack commandStack) {
		if (fCommandStack != null)
			fCommandStack.removeCommandStackListener(getInternalCommandStackListener());

		fCommandStack = commandStack;

		if (fCommandStack != null)
			fCommandStack.addCommandStackListener(getInternalCommandStackListener());
	}

	private void setRedoDocumentSelection(IDocumentSelectionMediator requester, Command command) {
		int cursorPosition = -1;
		int selectionLength = 0;

		if (command instanceof CommandCursorPosition) {
			CommandCursorPosition commandCursorPosition = (CommandCursorPosition) command;
			cursorPosition = commandCursorPosition.getRedoCursorPosition();
			selectionLength = commandCursorPosition.getRedoSelectionLength();
		}
		else if (command instanceof StructuredTextCommand) {
			StructuredTextCommand structuredTextCommand = (StructuredTextCommand) command;
			cursorPosition = structuredTextCommand.getTextStart();
			selectionLength = structuredTextCommand.getTextInserted().length();
		}

		if (cursorPosition > -1 && fMediators != null && fMediators.length > 0) {
			for (int i = 0; i < fMediators.length; i++) {
				IDocument document = fMediators[i].getDocument();
				fMediators[i].undoOperationSelectionChanged(new UndoDocumentEvent(requester, document, cursorPosition, selectionLength));
			}
		}
	}

	private void setUndoDocumentSelection(IDocumentSelectionMediator requester, Command command) {
		int cursorPosition = -1;
		int selectionLength = 0;

		if (command instanceof CommandCursorPosition) {
			CommandCursorPosition commandCursorPosition = (CommandCursorPosition) command;
			cursorPosition = commandCursorPosition.getUndoCursorPosition();
			selectionLength = commandCursorPosition.getUndoSelectionLength();
		}
		else if (command instanceof StructuredTextCommand) {
			StructuredTextCommand structuredTextCommand = (StructuredTextCommand) command;
			cursorPosition = structuredTextCommand.getTextStart();
			selectionLength = structuredTextCommand.getTextDeleted().length();
		}

		if (cursorPosition > -1 && fMediators != null && fMediators.length > 0) {
			for (int i = 0; i < fMediators.length; i++) {
				IDocument document = fMediators[i].getDocument();
				fMediators[i].undoOperationSelectionChanged(new UndoDocumentEvent(requester, document, cursorPosition, selectionLength));
			}
		}
	}

	public void undo() {
		undo(null);
	}

	public void undo(IDocumentSelectionMediator requester) {
		// Force an endRecording before undo.
		//
		// For example, recording was turned on on the Design Page of
		// PageDesigner.
		// Then undo is invoked on the Source Page. Recording should be
		// stopped before we undo.
		// Note that redo should not be available when we switch to the Source
		// Page.
		// Therefore, this force ending of recording is not needed in redo.
		if (fRecording)
			endRecording(this);

		if (undoable()) {
			IStructuredModel model = findStructuredModel(fDocument);
			IDocumentExtension4 docExt4 = null;
			DocumentRewriteSession rewriteSession = null;

			try {
				if (model != null)
					model.aboutToChangeModel();

				Command undoCommand = getUndoCommand();
				if (undoCommand instanceof CompoundCommand &&
						model.getStructuredDocument() instanceof IDocumentExtension4) {
					docExt4 = (IDocumentExtension4)model.getStructuredDocument();
				}
				rewriteSession = (docExt4 == null) ? null :
					docExt4.startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED);
				
				// make sure to undo before setting document selection
				fCommandStack.undo();

				// set document selection
				setUndoDocumentSelection(requester, undoCommand);
			}
			finally {
				if (docExt4 != null && rewriteSession != null)
					docExt4.stopRewriteSession(rewriteSession);
				if (model != null) {
					model.changedModel();
					model.releaseFromRead();
				}
			}
		}
	}

	public boolean undoable() {
		return fCommandStack.canUndo();
	}
}
