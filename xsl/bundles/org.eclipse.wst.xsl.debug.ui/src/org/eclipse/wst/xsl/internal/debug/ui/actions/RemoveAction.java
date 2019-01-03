/*******************************************************************************
 * Copyright (c) 2007, 2010 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 245772 - NLS Message refactoring
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.StylesheetViewer;
import org.eclipse.wst.xsl.launching.config.LaunchTransform;

/**
 * An action that removes a selection from a viewer.
 * 
 * @author Doug Satchwell
 */
public class RemoveAction extends AbstractStylesheetAction {
	/**
	 * Create a new instance of this.
	 * 
	 * @param viewer
	 *            the viewer that will have its selection removed
	 */
	public RemoveAction(StylesheetViewer viewer) {
		super(Messages.RemoveAction_Text, viewer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) getViewer()
				.getViewer().getSelection();
		getViewer().removeEntries(
				(LaunchTransform[]) selection.toList().toArray(
						new LaunchTransform[0]));
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		if (selection.isEmpty()) {
			return false;
		}
		return getViewer().updateSelection(getActionType(), selection);
	}

	@Override
	protected int getActionType() {
		return MOVE;
	}
}
