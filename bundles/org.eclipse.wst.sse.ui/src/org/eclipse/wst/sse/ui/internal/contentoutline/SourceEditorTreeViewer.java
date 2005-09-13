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
package org.eclipse.wst.sse.ui.internal.contentoutline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.StructuredTextSelectionChangedEvent;

/**
 * @deprecated
 */

public class SourceEditorTreeViewer extends TreeViewer {
	private int fCaretPosition;
	private ISelectionListener[] fInvalidSelectionListeners = null;

	/**
	 * SourceEditorTreeViewer constructor comment.
	 * 
	 * @param parent
	 *            org.eclipse.swt.widgets.Composite
	 */
	public SourceEditorTreeViewer(Composite parent) {
		super(parent);
	}

	/**
	 * SourceEditorTreeViewer constructor comment.
	 * 
	 * @param tree
	 *            org.eclipse.swt.widgets.Tree
	 */
	public SourceEditorTreeViewer(Tree tree) {
		super(tree);
	}

	public synchronized void addInvalidSelectionListener(ISelectionListener listener) {
		ISelectionListener[] newListeners = fInvalidSelectionListeners;
		if (fInvalidSelectionListeners != null && fInvalidSelectionListeners.length > 0) {
			List oldListeners = new ArrayList(Arrays.asList(fInvalidSelectionListeners));
			if (!oldListeners.contains(listener)) {
				oldListeners.add(listener);
				newListeners = (ISelectionListener[]) oldListeners.toArray(new ISelectionListener[0]);
			}
		}
		else {
			newListeners = new ISelectionListener[]{listener};
		}
		fInvalidSelectionListeners = newListeners;
	}

	/**
	 * @param newSelection
	 */
	private void fireInvalidSelection(ISelection newSelection) {
		if (fInvalidSelectionListeners != null) {
			ISelectionListener[] listeners = fInvalidSelectionListeners;
			for (int i = 0; i < listeners.length; i++) {
				listeners[i].selectionChanged(null, newSelection);
			}
		}
	}

	protected void handleInvalidSelection(ISelection invalidSelection, ISelection newSelection) {
		IStructuredModel model = (IStructuredModel) getInput();
		if (model != null) {
			Object selectedNode = model.getIndexedRegion(fCaretPosition);
			if (selectedNode != null)
				newSelection = new StructuredSelection(selectedNode);
			// notify listeners that the TreeViewer has received an invalid
			// selection
			fireInvalidSelection(newSelection);
			setSelectionToWidget(newSelection, true);
		}
		super.handleInvalidSelection(invalidSelection, newSelection);
	}

	protected void handleSelect(SelectionEvent event) {
		// handle case where an earlier selection listener disposed the
		// control.
		Control control = getControl();
		if (control != null && !control.isDisposed()) {
			updateStructuredTextSelection(getSelection());
		}
	}

	/*
	 * (non-Javadoc) Method declared on StructuredViewer.
	 */
	public void internalRefresh(Object element) {
		// If the input object is null just return.
		if (element == null)
			return;

		// The following skip refresh logic tries to optimize performance of
		// the content outliner.
		// However, when there is a structure change to the model, even if the
		// selection is the same
		// we should refresh the tree in case the structure change is in the
		// children of the current selection.
		// The following logic commented out for 178731.
		//
		// Skip refresh if new selection equals old selection.
		// org.eclipse.jface.viewers.ISelection selection = getSelection();
		// if (selection instanceof
		// org.eclipse.jface.viewers.IStructuredSelection)
		// if (((org.eclipse.jface.viewers.IStructuredSelection)
		// selection).size() == 1)
		// if (((org.eclipse.jface.viewers.IStructuredSelection)
		// selection).getFirstElement().equals(element))
		// return;

		Widget item = findItem(element);
		if (item != null) {
			// pick up structure changes too
			internalRefresh(item, element, true);
		}
	}

	/**
	 * Refreshes the tree starting at the given widget.
	 * 
	 * @param widget
	 *            the widget
	 * @param element
	 *            the element
	 * @param doStruct
	 *            <code>true</code> if structural changes are to be picked
	 *            up, and <code>false</code> if only label provider changes
	 *            are of interest
	 */
	private void internalRefresh(Widget widget, Object element, boolean doStruct) {
		// To avoid flashing, set repaint off while updating the tree.
		// getControl().setRedraw(false);

		if (widget instanceof Item) {
			if (doStruct) {
				updatePlus((Item) widget, element);
			}
			updateItem(widget, element);
		}

		if (doStruct) {
			Object[] children = getSortedChildren(element);
			updateChildren(widget, element, children);
		}
		// recurse
		Item[] children = getChildren(widget);
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				Widget item = children[i];
				Object data = item.getData();
				if (data != null)
					internalRefresh(item, data, doStruct);
			}
		}

		// The purpose of the following line of code was to minimize the
		// number of items in the tree needed to be repainted.
		// Expanding/collapsing the tree for the user is a little confusing.
		// Also, with new improvements to cut down unnesessary
		// notifications to the tree, this may not be needed.
		// expandToLevel(element, 1);

		// getControl().setRedraw(true);
	}

	public synchronized void removeInvalidSelectionListener(ISelectionListener listener) {
		ISelectionListener[] newListeners = fInvalidSelectionListeners;
		if (fInvalidSelectionListeners != null && fInvalidSelectionListeners.length > 1) {
			List oldListeners = new ArrayList(Arrays.asList(fInvalidSelectionListeners));
			if (oldListeners.contains(listener)) {
				oldListeners.remove(listener);
				newListeners = (ISelectionListener[]) oldListeners.toArray(new ISelectionListener[0]);
			}
		}
		// there was only 1, remove the entire array
		else {
			newListeners = null;
		}
		fInvalidSelectionListeners = newListeners;
	}

	/**
	 * Runs the given updateCode while selection changed notification is
	 * turned off. This ensures that no selection changes are fired as a side
	 * effect of updating the SWT control.
	 */

	/*
	 * protected void preservingSelection(Runnable updateCode) { // This
	 * method is overridden to get rid of the selection handling // that the
	 * super classes do after the update. We have added our own // selection
	 * handling in here. IStructuredModel model = (IStructuredModel)
	 * getInput(); IndexedRegion node = null;
	 * 
	 * if (model != null) node = model.getIndexedRegion(fCaretPosition);
	 * 
	 * updateCode.run();
	 * 
	 * if (model != null) if (node == null) setSelection(new
	 * StructuredSelection()); else setSelection(new
	 * StructuredSelection(node)); }
	 */
	public void setCaretPosition(int caretPosition) {
		fCaretPosition = caretPosition;
	}

	public void setSelection(ISelection selection, boolean reveal) {
		if (reveal)
			updateStructuredTextSelection(selection);

		super.setSelection(selection, reveal);
	}

	protected void updateStructuredTextSelection(ISelection selection) {
		SelectionChangedEvent event = new StructuredTextSelectionChangedEvent(this, selection);
		fireSelectionChanged(event);
	}
}
