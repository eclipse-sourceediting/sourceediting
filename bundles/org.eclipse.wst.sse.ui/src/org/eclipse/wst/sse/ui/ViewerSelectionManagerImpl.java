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
package org.eclipse.wst.sse.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
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

		public void modelAboutToBeChanged(IStructuredModel model) {
			setModelChanging(true);
		}

		public void modelChanged(IStructuredModel model) {
			setModelChanging(false);
		}

		public void modelDirtyStateChanged(IStructuredModel model, boolean isDirty) {
		}

		public void modelResourceDeleted(IStructuredModel model) {
		}

		public void modelResourceMoved(IStructuredModel originalmodel, IStructuredModel movedmodel) {
		}

	}

	private CaretMediator fCaretMeditator;
	private int fCaretPosition = 0;
	protected IStructuredModel fModel;
	private boolean fModelChanging = false;

	private IDoubleClickListener[] fNodeDoubleClickListeners;
	private INodeSelectionListener[] fNodeSelectionListeners;
	private List fSelectedNodes;

	// ISpellCheckSelectionManager
	protected SpellCheckSelectionListener[] fSpellCheckSelectionListeners;
	private int fTextSelectionEnd;
	// TODO: private field never read locally
	 IndexedRegion fTextSelectionEndNode;
	private ITextSelectionListener[] fTextSelectionListeners;
	private int fTextSelectionStart;
//	 TODO: private field never read locally
	IndexedRegion fTextSelectionStartNode;
	private ITextViewer fTextViewer;
	private InternalModelStateListener internalModelStateListener;
//	 TODO: private field never read locally
	boolean isFiringNodeDoubleClick = false;
	private boolean isFiringNodeSelectionChanged = false;

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
		} else {
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
		} else {
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
		} else {
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

	public synchronized void addTextSelectionListener(ITextSelectionListener listener) {
		// since its most certainly an error if someone is trying to add a
		// null listner,
		// we'll flag it explicitly and ignore
		if (listener == null) {
			Logger.log(Logger.WARNING, "Likely error in ViewerSelectionManagerImpl::addTextSelectionListener should not but called with null listener"); //$NON-NLS-1$
			return;
		}
		// make sure listener is not already in listening array
		// (and if it is, print a warning to aid debugging, if needed)
		if (Utilities.contains(fTextSelectionListeners, listener)) {
			if (Debug.displayWarnings) {
				System.out.println("ViewerSelectionManager::addTextSelectionListener. listener " + listener + " was added more than once. "); //$NON-NLS-2$//$NON-NLS-1$
			}
		} else {
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
			} finally {
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
			} finally {
				isFiringNodeSelectionChanged = false;
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

	public int getCaretPosition() {
		return fCaretPosition;
	}

	public List getSelectedNodes() {
		return fSelectedNodes;
	}

	public ITextViewer getTextViewer() {
		return fTextViewer;
	}

	protected List getTextWidgetSelectedNodes(int offset) {
		if (fModel == null)
			return new ArrayList(0);

		IndexedRegion firstSelectedNode = fModel.getIndexedRegion(offset);
		fTextSelectionStartNode = firstSelectedNode;
		fTextSelectionEndNode = firstSelectedNode;

		// Never send a "null" in the selection
		List selectedNodes = null;
		if (firstSelectedNode != null) {
			selectedNodes = new ArrayList(1);
			selectedNodes.add(firstSelectedNode);
		} else {
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
		} else {
			if (isTextSelectionChanged(selectionStart, selectionEnd))
				textSelectionChanged(source, selectionStart, selectionEnd);

			if (isCurrentNodeChanged(selectedNodes))
				currentNodeChanged(source, selectedNodes, selectionEnd);
		}
	}

	public void release() {
		setTextViewer(null);

		// remove this viewer selection manager from the old model's list of
		// model state listeners
		if (fModel != null)
			fModel.removeModelStateListener(internalModelStateListener);

		// make sure the CaretMediator we created is released as well
		fCaretMeditator.release();
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
					} else {
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
					} else {
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
					} else {
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
					} else {
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

	public void selectionChanged(SelectionChangedEvent event) {
		// If selection is fired as a result of processing a node change
		// event, then ignore the selection.
		if (isFiringNodeSelectionChanged || isModelChanging())
			return;

		ISelection eventSelection = event.getSelection();
		// handle Structured selections
		if (eventSelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) eventSelection;
			//			System.out.println("selection: " + event.getSource() + " [" +
			// selection.toArray().length + "] " +
			// selection.getFirstElement());
			List selectedNodes = selection.toList();
			int selectionStart = 0;
			int selectionEnd = 0;

			// something selected
			if (selectedNodes.size() > 0) {
				IndexedRegion firstSelectedNode = (IndexedRegion) selectedNodes.get(0);
				selectionStart = firstSelectedNode.getStartOffset();
				selectionEnd = firstSelectedNode.getEndOffset();

				// remove all except the first selected node
				selectedNodes = new Vector(1);
				selectedNodes.add(firstSelectedNode);
			}

			processSelectionChanged(event.getSource(), selectedNodes, selectionStart, selectionEnd);
		}
		// handle text selection changes
		else if (eventSelection instanceof ITextSelection) {
			ITextSelection selection = (ITextSelection) eventSelection;
			//			System.out.println("selection: " + event.getSource() + " (" +
			// selection.getOffset() + "+=" + selection.getLength() + ")");
			int selectionStart = selection.getOffset();
			setCaretPosition(selectionStart);
			int selectionEnd = selection.getOffset() + selection.getLength();
			if (true) {
				// option 1: works great for Source Page editors and the XML
				// and XSL Editors
				List selectedNodes = getTextWidgetSelectedNodes(selection.getOffset());
				processSelectionChanged(event.getSource(), selectedNodes, selectionStart, selectionEnd);
			} else {
				// option 2: works with all of the above plus Page Designer,
				// but not as clean nor perfectly
				// TODO: switch to option 1
				Event selectionEvent = new Event();
				selectionEvent.widget = fTextViewer.getTextWidget();
				selectionEvent.display = fTextViewer.getTextWidget().getDisplay();
				selectionEvent.x = selectionStart;
				selectionEvent.y = selectionEnd;
				fTextViewer.getTextWidget().setSelection(selectionStart, selectionEnd);
				widgetDefaultSelected(new SelectionEvent(selectionEvent));
			}
		}
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
	 * viewer, but do need its text widget, and will register this viewer
	 * selection manager as a listener of the text widget's text selection.
	 */
	public void setTextViewer(ITextViewer newTextViewer) {
		// unhook from previous, if any
		if (fCaretMeditator != null) {
			fCaretMeditator.removeCaretListener(this);
			fCaretMeditator.setTextWidget(null);
		}
		if (fTextViewer != null) {
			//			fTextViewer.getSelectionProvider().removeSelectionChangedListener(this);
			StyledText textWidget = fTextViewer.getTextWidget();
			if (textWidget != null) {
				// listen to text selections
				textWidget.removeSelectionListener(this);
			}
		}

		fTextViewer = newTextViewer;

		if (fTextViewer != null) {
			//			fTextViewer.getSelectionProvider().addSelectionChangedListener(this);
			StyledText textWidget = fTextViewer.getTextWidget();
			if (textWidget != null) {
				// create new caret mediator, if it doesn't exist yet
				if (fCaretMeditator == null) {
					fCaretMeditator = new CaretMediator(textWidget);
				} else {
					fCaretMeditator.setTextWidget(textWidget);
				}
				// and register as a listener
				fCaretMeditator.addCaretListener(this);

				// listen to text selections
				textWidget.addSelectionListener(this);
			}
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
}
