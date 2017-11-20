/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.StylesheetViewer;
import org.eclipse.wst.xsl.launching.config.LaunchPipeline;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

/**
 * A convenient base class for actions associated with the stylesheet viewer.
 * 
 * @author Doug Satchwell
 */
public abstract class AbstractStylesheetAction extends SelectionListenerAction {
	/**
	 * The default action type.
	 */
	public static final int DEFAULT = 0;
	/**
	 * The action type for add actions.
	 */
	public static final int ADD = 1;
	/**
	 * The action type for remove actions.
	 */
	public static final int REMOVE = 2;
	/**
	 * The action type for move actions.
	 */
	public static final int MOVE = 3;

	private StylesheetViewer viewer;
	private Button button;
	private Shell shell;

	protected AbstractStylesheetAction(String text, StylesheetViewer viewer) {
		super(text);
		setViewer(viewer);
	}

	/**
	 * Add an array of LaunchTransform's to the viewer.
	 * 
	 * @param res
	 *            launch transforms to add
	 */
	public void addTransforms(LaunchTransform[] res) {
		viewer.addTransforms(res);
	}

	/**
	 * Set the viewer associated with this action.
	 * 
	 * @param viewer
	 *            the viewer
	 */
	public void setViewer(StylesheetViewer viewer) {
		if (this.viewer != null) {
			this.viewer.getViewer().removeSelectionChangedListener(this);
		}
		this.viewer = viewer;
		if (viewer != null) {
			viewer.getViewer().addSelectionChangedListener(this);
			update();
		}
	}

	protected StylesheetViewer getViewer() {
		return viewer;
	}

	/**
	 * Set the button associated with this action.
	 * 
	 * @param button
	 *            the button
	 */
	public void setButton(Button button) {
		this.button = button;
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				run();
			}
		});
		button.setEnabled(isEnabled());
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (button != null) {
			button.setEnabled(enabled);
		}
	}

	protected void update() {
		selectionChanged((IStructuredSelection) getViewer().getViewer()
				.getSelection());
	}

	protected Shell getShell() {
		if (shell == null) {
			shell = getViewer().getShell();
		}
		return shell;
	}

	/**
	 * Set the shell to be used for opening a dialog.
	 * 
	 * @param shell
	 *            the shell to use
	 */
	public void setShell(Shell shell) {
		this.shell = shell;
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return getViewer().updateSelection(getActionType(), selection);
	}

	protected int getActionType() {
		return DEFAULT;
	}

	protected List<?> getOrderedSelection() {
		List<?> selection = ((IStructuredSelection) getViewer().getViewer()
				.getSelection()).toList();
		return selection;
	}

	protected List<LaunchTransform> getEntriesAsList() {
		// IStylesheetEntry[] entries = getViewer().getEntries();
		// List list = new ArrayList(entries.length);
		// for (int i = 0; i < entries.length; i++) {
		// list.add(entries[i]);
		// }
		LaunchPipeline lp = (LaunchPipeline) getViewer().getViewer().getInput();
		return lp.getTransformDefs();
	}

	protected void setEntries(List<?> list) {
		getViewer().setEntries(list.toArray(new LaunchTransform[list.size()]));
		// // update all selection listeners
		// getViewer().setSelection(getViewer().getSelection());
	}

	protected boolean isIndexSelected(IStructuredSelection selection, int index) {
		if (selection.isEmpty()) {
			return false;
		}
		Iterator<?> entries = selection.iterator();
		List<?> list = getEntriesAsList();
		while (entries.hasNext()) {
			Object next = entries.next();
			if (list.indexOf(next) == index) {
				return true;
			}
		}
		return false;
	}

}
