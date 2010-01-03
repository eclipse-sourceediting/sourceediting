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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.ParameterViewer;

/**
 * A convenient base class for add/remove parameter actions.
 * 
 * @author Doug Satchwell
 */
public abstract class AbstractParameterAction extends SelectionListenerAction {
	private ParameterViewer viewer;
	private Button button;
	private Shell shell;

	protected AbstractParameterAction(String text, ParameterViewer viewer) {
		super(text);
		setViewer(viewer);
	}

	/**
	 * Set the viewer.
	 * 
	 * @param viewer
	 *            the parametere viewer
	 */
	public void setViewer(ParameterViewer viewer) {
		if (this.viewer != null) {
			this.viewer.getViewer().removeSelectionChangedListener(this);
		}
		this.viewer = viewer;
		if (viewer != null) {
			viewer.getViewer().addSelectionChangedListener(this);
			update();
		}
	}

	protected ParameterViewer getViewer() {
		return viewer;
	}

	/**
	 * Set the button associated with the action.
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
	 * Set the shell that will be used for opening a dialog.
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
		return AbstractStylesheetAction.DEFAULT;
	}
}
