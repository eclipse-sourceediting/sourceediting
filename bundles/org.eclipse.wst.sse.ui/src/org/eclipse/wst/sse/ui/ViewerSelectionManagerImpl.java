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



import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wst.sse.core.IModelStateListener;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.core.util.Utilities;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckSelectionListener;
import org.eclipse.wst.sse.ui.extensions.spellcheck.SpellCheckSelectionManager;
import org.eclipse.wst.sse.ui.view.events.CaretEvent;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.ITextSelectionListener;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.eclipse.wst.sse.ui.view.events.TextSelectionChangedEvent;


public class ViewerSelectionManagerImpl implements ViewerSelectionManager, SpellCheckSelectionManager {

	class InternalModelStateListener implements IModelStateListener {

		public void modelResourceDeleted(IStructuredModel model) {
		}

		public void modelAboutToBeChanged(IStructuredModel model) {
			setModelChanging(true);
		}

		public void modelChanged(IStructuredModel model) {
			setModelChanging(false);
		}

		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
		}

		public void modelResourceMoved(IStructuredModel originalmodel, IStructuredModel movedmodel) {
		}

	}

	private IDoubleClickListener[] fNodeDoubleClickListeners;
	private INodeSelectionListener[] fNodeSelectionListeners;
	private ITextSelectionListener[] fTextSelectionListeners;
	private List fSelectedNodes;
	private int fCaretPosition = 0;
	private int fTextSelectionStart;
	private int fTextSelectionEnd;
	private IndexedRegion fTextSelectionStartNode;
	private IndexedRegion fTextSelectionEndNode;
	private CaretMediator fCaretMeditator;
	private boolean isFiringNodeDoubleClick = false;
	private boolean isFiringNodeSelectionChanged = false;
	protected IStructuredModel fModel;
	private InternalModelStateListener internalModelStateListener;
	private boolean fModelChanging = false;

	/**
	 * @deprecated
	 * Subclass should not access fTextWidget directly.
	 * Subclass sub register as NodeSelectionListener and TextSelectionListener of
	 * ViewerSelectionManager if selection info is needed.
	 * 
	 * This field is restore temporarily until Page Designer fixed their problems.
	 */
	protected StyledText fTextWidget;

	public ViewerSelectionManagerImpl() {
		internalModelStateListener = new InternalModelStateListener();
	}

	public ViewerSelectionManagerImpl(ITextViewer textViewer) {
		setTextViewer(textViewer);

		internalModelStateListener = new InternalModelStateListener();
	}

	public synchronized void addNodeDoubleClickListener(IDoubleClickListener listener) {
		// since its most certainly an error if someone is trying to add a
		// null listner,
		// we'll flag it explicitly and ignore
		if (listener == null) {
			Logger.log(Logger.WARNING, "Likely error in ViewerSelectionManagerImpl::addNodeDoubleClickListener should not be called with null listener"); //$NON-NLS-1$
			return;
		}
		// make sure listlistener not already in listening array
		// (and if it is, print a warning to aid debugging, if needed)
		if (Utilities.contains(fNodeDoubleClickListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("ViewerSelectionManager::addNodeDoubleClickListener. listener " + listener + " was added more than once. "); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		else {
			if (Debug.debugStructuredDocument) {
				System.out.println("ViewerSelectionManager::addNodeDoubleClickListener. Adding an instance of " + listener.getClass() + " as a listener on ViewerSelectionManager."); //$NON-NLS-2$//$NON-NLS-1$
			}
			int oldSize = 0;
			if (fNodeDoubleClickListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fNodeDoubleClickListeners.length;
			}
			int newSize = oldSize + 1;
			IDoubleClickListener[] newListeners = new IDoubleClickListener[newSize];
			if (fNodeDoubleClickListeners != null) {
				System.arraycopy(fNodeDoubleClickListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fNodeDoubleClickListeners = newListeners;

		}
	}

	public synchronized void addNodeSelectionListener(INodeSelectionListener listener) {
		// since its most certainly an error if someone is trying to add a
		// null listner,
		// we'll flag it explicitly and ignore
		if (listener == null) {
			Logger.log(Logger.WARNING, "Likely error in ViewerSelectionManagerImpl::addNodeSelectionListener should not but called with null listener"); //$NON-NLS-1$
			return;
		}
		// make sure listener is not already in listening array
		// (and if it is, print a warning to aid debugging, if needed)
		if (Utilities.contains(fNodeSelectionListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("ViewerSelectionManager::addNodeSelectionListener. listener " + listener + " was added more than once. "); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		else {
			if (Debug.debugStructuredDocument) {
				System.out.println("ViewerSelectionManager::addNodeSelectionListener. Adding an instance of " + listener.getClass() + " as a listener on ViewerSelectionManager."); //$NON-NLS-2$//$NON-NLS-1$
			}
			int oldSize = 0;
			if (fNodeSelectionListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fNodeSelectionListeners.length;
			}
			int newSize = oldSize + 1;
			INodeSelectionListener[] newListeners = new INodeSelectionListener[newSize];
			if (fNodeSelectionListeners != null) {
				System.arraycopy(fNodeSelectionListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fNodeSelectionListeners = newListeners;

		}
	}

	public synchronized void addTextSelectionListener(ITextSelectionListener listener) {
		// since its most certainly an error if someone is trying to add a
		// null listner,
		// we'll flag it explicitly and ignore
		if (listener == null) {
			Logger.log(Logger.WARNING, "Likely error in ViewerSelectionManagerImpl::addNodeSelectionListener should not but called with null listener"); //$NON-NLS-1$
			return;
		}
		// make sure listener is not already in listening array
		// (and if it is, print a warning to aid debugging, if needed)
		if (Utilities.contains(fTextSelectionListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("ViewerSelectionManager::addTextSelectionListener. listener " + listener + " was added more than once. "); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		else {
			if (Debug.debugStructuredDocument) {
				System.out.println("ViewerSelectionManager::addTextSelectionListener. Adding an instance of " + listener.getClass() + " as a listener on ViewerSelectionManager."); //$NON-NLS-2$//$NON-NLS-1$
			}
			int oldSize = 0;
			if (fTextSelectionListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fTextSelectionListeners.length;
			}
			int newSize = oldSize + 1;
			ITextSelectionListener[] newListeners = new ITextSelectionListener[newSize];
			if (fTextSelectionListeners != null) {
				System.arraycopy(fTextSelectionListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fTextSelectionListeners = newListeners;

		}
	}

	/**
	 * This method listens to text widget caret movements. This method is
	 * called when a caret (insertion point) move occurs that is NOT the
	 * result of the text changing. Specifcally, as a result of mouse clicks,
	 * PAGE_UP, RIGHT_ARROW, etc.
	 */
	public void caretMoved(final CaretEvent event) {
		if (!isModelChanging()) {
			List selectedNodes = getTextWidgetSelectedNodes(event.getPosition());
			int caretPosition = event.getPosition();
			processSelectionChanged(event.getSource(), selectedNodes, caretPosition, caretPosition);
		}
	}

	protected void currentNodeChanged(Object source, List newSelectedNodes, int caretPosition) {
		// save current node; make sure to clone the selected nodes list, or
		// else there may be a side effect when listeners modify the list
		fSelectedNodes = new Vector(newSelectedNodes);
		// save current caret position
		fCaretPosition = caretPosition;
		// generate and fire event
		NodeSelectionChangedEvent nodeSelectionChangedEvent = new NodeSelectionChangedEvent(source, newSelectedNodes, caretPosition);
		fireNodeSelectionChangedEvent(nodeSelectionChangedEvent);
	}

	/**
	 * Notifies of a double click.
	 * 
	 * @param event
	 *            event object describing the double-click
	 */
	public void doubleClick(DoubleClickEvent event) {
		if (!isModelChanging())
			fireNodeDoubleClickEvent(event);
	}

	protected void fireNodeDoubleClickEvent(DoubleClickEvent event) {
		if ((fNodeDoubleClickListeners != null) && (!isModelChanging())) {
			// we must assign listeners to local variable to be thread safe,
			// since the add and remove listner methods
			// can change this object's actual instance of the listener array
			// from another thread
			// (and since object assignment is atomic, we don't need to
			// synchronize
			isFiringNodeDoubleClick = true;
			try {
				IDoubleClickListener[] holdListeners = fNodeDoubleClickListeners;

				for (int i = 0; i < holdListeners.length; i++) {
					holdListeners[i].doubleClick(event);
				}
			}
			finally {
				isFiringNodeDoubleClick = false;
			}
		}
	}

	protected void fireNodeSelectionChangedEvent(NodeSelectionChangedEvent event) {
		if ((fNodeSelectionListeners != null) && (!isModelChanging())) {
			// we must assign listeners to local variable to be thread safe,
			// since the add and remove listner methods
			// can change this object's actual instance of the listener array
			// from another thread
			// (and since object assignment is atomic, we don't need to
			// synchronize
			isFiringNodeSelectionChanged = true;
			try {
				INodeSelectionListener[] holdListeners = fNodeSelectionListeners;

				for (int i = 0; i < holdListeners.length; i++) {
					holdListeners[i].nodeSelectionChanged(event);
				}
			}
			finally {
				isFiringNodeSelectionChanged = false;
			}
		}
	}

	protected void fireTextSelectionChangedEvent(TextSelectionChangedEvent event) {
		if ((fTextSelectionListeners != null) && (!isModelChanging())) {
			// we must assign listeners to local variable to be thread safe,
			// since the add and remove listner methods
			// can change this object's actual instance of the listener array
			// from another thread
			// (and since object assignment is atomic, we don't need to
			// synchronize
			ITextSelectionListener[] holdListeners = fTextSelectionListeners;
			//
			for (int i = 0; i < holdListeners.length; i++) {
				holdListeners[i].textSelectionChanged(event);
			}
		}
	}

	protected void fireSpellCheckSelectionChangedEvent() {
		if ((fSpellCheckSelectionListeners != null) && (!isModelChanging())) {
			// we must assign listeners to local variable to be thread safe,
			// since the add and remove listner methods
			// can change this object's actual instance of the listener array
			// from another thread
			// (and since object assignment is atomic, we don't need to
			// synchronize
			SpellCheckSelectionListener[] holdListeners = fSpellCheckSelectionListeners;
			//
			for (int i = 0; i < holdListeners.length; i++) {
				holdListeners[i].selectionChanged();
			}
		}
	}

	public int getCaretPosition() {
		return fCaretPosition;
	}

	public List getSelectedNodes() {
		return fSelectedNodes;
	}
	/**
	 * @deprecated
	 * 
	 * @return
	 */
	protected List getTextWidgetSelectedNodes() {
		return getTextWidgetSelectedNodes(fTextSelectionStart);
	}
		

	protected List getTextWidgetSelectedNodes(int offset) {
		if (fModel == null)
			return new ArrayList(0);

		IndexedRegion firstSelectedNode = fModel.getIndexedRegion(offset);
		fTextSelectionStartNode = firstSelectedNode;
		fTextSelectionEndNode = firstSelectedNode;

		// Never send a "null" in the selection
		List selectedNodes = null;
		if(firstSelectedNode != null) {
			selectedNodes = new ArrayList(1);
			selectedNodes.add(firstSelectedNode);
		}
		else {
			selectedNodes = new ArrayList(0);
		}
		return selectedNodes;
	}

	protected boolean isCurrentNodeChanged(List newSelectedNodes) {
		return !newSelectedNodes.equals(fSelectedNodes);
	}

	protected boolean isModelChanging() {
		return fModelChanging;
	}

	protected boolean isTextSelectionChanged(int textSelectionStart, int textSelectionEnd) {
		return ((fTextSelectionStart != textSelectionStart) || (fTextSelectionEnd != textSelectionEnd));
	}

	protected void processSelectionChanged(Object source, List selectedNodes, int selectionStart, int selectionEnd) {
		if (source == null) {
			// source should not be null.
			// log this and ignore this selection changed event.
			// DMW: I "shortened" this log message by removing stack trace,
			// after receiving log
			// from support showing this error. It made the log file
			// confusing.
			// Not clear why the source was null, but probably only related to
			// initialization, or 'startup' with file open.
			Logger.log(Logger.ERROR, "ViewerSelectionManager::processSelectionChanged. Unexpected null source"); //$NON-NLS-1$
		}
		else {
			if (isTextSelectionChanged(selectionStart, selectionEnd))
				textSelectionChanged(source, selectionStart, selectionEnd);

			if (isCurrentNodeChanged(selectedNodes))
				currentNodeChanged(source, selectedNodes, selectionEnd);
		}
	}
	
	/**
	 * @deprecated
	 */
	protected void refresh() {
		//List selectedNodes = getTextWidgetSelectedNodes();
		//boolean nodeChanged = isCurrentNodeChanged(selectedNodes);
		//if (nodeChanged)
		//	currentNodeChanged(this, selectedNodes, fCaretPosition);
	}
	

	public void release() {
		if (fCaretMeditator != null) {
			fCaretMeditator.removeCaretListener(this);
			fCaretMeditator.release();
			fCaretMeditator = null;
		}

		// remove this viewer selection manager from the old model's list of
		// model state listeners
		if (fModel != null)
			fModel.removeModelStateListener(internalModelStateListener);
	}

	public synchronized void removeNodeDoubleClickListener(IDoubleClickListener listener) {
		if ((fNodeDoubleClickListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fNodeDoubleClickListeners, listener)) {
				int oldSize = fNodeDoubleClickListeners.length;
				int newSize = oldSize - 1;
				IDoubleClickListener[] newListeners = new IDoubleClickListener[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fNodeDoubleClickListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fNodeDoubleClickListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fNodeDoubleClickListeners = newListeners;
			}
		}
	}

	public synchronized void removeNodeSelectionListener(INodeSelectionListener listener) {
		if ((fNodeSelectionListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fNodeSelectionListeners, listener)) {
				int oldSize = fNodeSelectionListeners.length;
				int newSize = oldSize - 1;
				INodeSelectionListener[] newListeners = new INodeSelectionListener[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fNodeSelectionListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fNodeSelectionListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fNodeSelectionListeners = newListeners;
			}
		}
	}

	public synchronized void removeTextSelectionListener(ITextSelectionListener listener) {
		if ((fTextSelectionListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fTextSelectionListeners, listener)) {
				int oldSize = fTextSelectionListeners.length;
				int newSize = oldSize - 1;
				ITextSelectionListener[] newListeners = new ITextSelectionListener[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fTextSelectionListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fTextSelectionListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fTextSelectionListeners = newListeners;
			}
		}
	}

	/**
	 * This method listens to tree viewer selection changes. This method is
	 * called when the selection from a tree viewer has changed.
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		// If selection is fired as a result of processing a node change
		// event, then ignore the selection.
		if (isFiringNodeSelectionChanged || isModelChanging())
			return;

		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		int selectionSize = selection.size();
		List selectedNodes = selection.toList();
		int selectionStart = 0;
		int selectionEnd = 0;

		// something selected
		if (selectionSize > 0) {
			IndexedRegion firstSelectedNode = (IndexedRegion) selectedNodes.get(0);
			selectionStart = firstSelectedNode.getStartOffset();
			selectionEnd = firstSelectedNode.getEndOffset();

			// remove all except the first selected node
			selectedNodes = new Vector(1);
			selectedNodes.add(firstSelectedNode);
		}

		// Tell the listeners the original source of NodeSelectionChanged
		// event.
		Object source = event.getSource();

		// Use ViewerSelectionManager as the source if the selection was set
		// programmatically.
		if ((event.getSource() instanceof TreeViewer) && !(event instanceof StructuredTextSelectionChangedEvent))
			source = this;

		processSelectionChanged(source, selectedNodes, selectionStart, selectionEnd);
	}

	protected void setCaretPosition(int caretPosition) {
		fCaretPosition = caretPosition;
	}

	public void setModel(IStructuredModel newModel) {
		if (newModel != fModel) {
			// remove this viewer selection manager from the old model's list
			// of model state listeners
			if (fModel != null)
				fModel.removeModelStateListener(internalModelStateListener);

			fModel = newModel;

			// add this viewer selection manager to the new model's list of
			// model state listeners
			fModel.addModelStateListener(internalModelStateListener);
		}
	}

	protected void setModelChanging(boolean modelChanging) {
		fModelChanging = modelChanging;
	}

	/**
	 * This is the viewer who's caret postion we monitor to determine when to
	 * check if the node has changed. We don't actually need, or save, the
	 * viewer, but do need its text widget, and will register this
	 * viewer selection manager as a listener of the text widget's text selection.
	 */
	public void setTextViewer(ITextViewer newTextViewer) {
		// unhook from previous, if any
		if (fCaretMeditator != null)
			fCaretMeditator.removeCaretListener(this);

		if (newTextViewer != null) {
			StyledText textWidget = newTextViewer.getTextWidget();
			if (textWidget != null) {
				// create new caretmediator, if it doesn't exist yet
				if (fCaretMeditator == null) {
					fCaretMeditator = new CaretMediator(textWidget);
				}
				else {
					fCaretMeditator.setTextWidget(textWidget);
				}
				// and register as a listner
				fCaretMeditator.addCaretListener(this);
		
				// listen to text selections
				textWidget.addSelectionListener(this);
			}

			/**
			 * TODO
			 * Subclass should not access fTextWidget directly.
			 * Subclass sub register as NodeSelectionListener and TextSelectionListener of
			 * ViewerSelectionManager if selection info is needed.
			 * 
			 * The following line is restore temporarily until Page Designer fixed their problems.
			 */
			fTextWidget = textWidget;
		}
	}

	protected void textSelectionChanged(Object source, int textSelectionStart, int textSelectionEnd) {
		// save current text selection
		fTextSelectionStart = textSelectionStart;
		fTextSelectionEnd = textSelectionEnd;
		// save current caret position
		fCaretPosition = fTextSelectionEnd;
		// generate and fire event
		TextSelectionChangedEvent textSelectionChangedEvent = new TextSelectionChangedEvent(source, fTextSelectionStart, fTextSelectionEnd);
		fireTextSelectionChangedEvent(textSelectionChangedEvent);

		// SpellCheck dialog also needs to listen text selection change
		fireSpellCheckSelectionChangedEvent();
	}

	/**
	 * This method listens to text widget default selection changes. This
	 * method is called when default selection occurs in the control. For
	 * example, on some platforms default selection occurs in a List when the
	 * user double-clicks an item or types return in a Text.
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
		if (!isModelChanging()) {
			List selectedNodes = getTextWidgetSelectedNodes(event.x);
			int selectionStart = event.x;
			int selectionEnd = event.y;
			processSelectionChanged(event.getSource(), selectedNodes, selectionStart, selectionEnd);
		}
	}

	/**
	 * This method listens to text widget text selection changes. This method
	 * is called when the text selection in a text widget has changed.
	 */
	public void widgetSelected(SelectionEvent event) {
		// If selection is fired as a result of processing a node change
		// event, then ignore the selection.
		if (isFiringNodeSelectionChanged || isModelChanging())
			return;

		widgetDefaultSelected(event);
	}

	// ISpellCheckSelectionManager
	protected SpellCheckSelectionListener[] fSpellCheckSelectionListeners;

	/**
	 * @see SpellCheckSelectionManager#addSpellCheckSelectionListener(SpellCheckSelectionListener)
	 */
	public void addSpellCheckSelectionListener(SpellCheckSelectionListener listener) {
		// since its most certainly an error if someone is trying to add a
		// null listner,
		// we'll flag it explicitly and ignore
		if (listener == null) {
			Logger.log(Logger.WARNING, "Likely error in ViewerSelectionManagerImpl::addSpellCheckSelectionListener should not but called with null listener"); //$NON-NLS-1$
			return;
		}
		// make sure listener is not already in listening array
		// (and if it is, print a warning to aid debugging, if needed)

		if (Utilities.contains(fSpellCheckSelectionListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("ViewerSelectionManager::addSpellCheckSelectionListener. listener " + listener + " was added more than once. "); //$NON-NLS-2$//$NON-NLS-1$
			}
		}
		else {
			if (Debug.debugStructuredDocument) {
				System.out.println("ViewerSelectionManager::addSpellCheckSelectionListener. Adding an instance of " + listener.getClass() + " as a listener on ViewerSelectionManager."); //$NON-NLS-2$//$NON-NLS-1$
			}
			int oldSize = 0;
			if (fSpellCheckSelectionListeners != null) {
				// normally won't be null, but we need to be sure, for first
				// time through
				oldSize = fSpellCheckSelectionListeners.length;
			}
			int newSize = oldSize + 1;
			SpellCheckSelectionListener[] newListeners = new SpellCheckSelectionListener[newSize];
			if (fSpellCheckSelectionListeners != null) {
				System.arraycopy(fSpellCheckSelectionListeners, 0, newListeners, 0, oldSize);
			}
			// add listener to last position
			newListeners[newSize - 1] = listener;
			//
			// now switch new for old
			fSpellCheckSelectionListeners = newListeners;
		}
	}

	/**
	 * @see SpellCheckSelectionManager#removeSpellCheckSelectionListener(SpellCheckSelectionListener)
	 */
	public void removeSpellCheckSelectionListener(SpellCheckSelectionListener listener) {
		if ((fSpellCheckSelectionListeners != null) && (listener != null)) {
			// if its not in the listeners, we'll ignore the request
			if (Utilities.contains(fSpellCheckSelectionListeners, listener)) {
				int oldSize = fSpellCheckSelectionListeners.length;
				int newSize = oldSize - 1;
				SpellCheckSelectionListener[] newListeners = new SpellCheckSelectionListener[newSize];
				int index = 0;
				for (int i = 0; i < oldSize; i++) {
					if (fSpellCheckSelectionListeners[i] == listener) { // ignore
					}
					else {
						// copy old to new if its not the one we are removing
						newListeners[index++] = fSpellCheckSelectionListeners[i];
					}
				}
				// now that we have a new array, let's switch it for the old
				// one
				fSpellCheckSelectionListeners = newListeners;
			}
		}
	}
}
