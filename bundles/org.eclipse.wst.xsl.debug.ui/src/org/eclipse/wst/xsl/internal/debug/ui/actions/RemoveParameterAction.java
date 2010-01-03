/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 245772 - NLS Message refactoring
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.ParameterViewer;
import org.eclipse.wst.xsl.launching.config.LaunchAttribute;

/**
 * An action that removes a selection from a viewer.
 * 
 * @author Doug Satchwell
 */
public class RemoveParameterAction extends AbstractParameterAction {
	/**
	 * Create a new instance of this.
	 * 
	 * @param viewer
	 *            the viewer that will have its selection removed
	 */
	public RemoveParameterAction(ParameterViewer viewer) {
		super(Messages.RemoveParameterAction, viewer);
	}

	@Override
	public void run() {
		IStructuredSelection sel = getStructuredSelection();
		if (sel.size() > 0) {
			LaunchAttribute[] entries = new LaunchAttribute[sel.size()];
			int i = 0;
			for (Iterator<?> iter = sel.iterator(); iter.hasNext(); i++) {
				LaunchAttribute att = (LaunchAttribute) iter.next();
				entries[i] = att;
			}
			getViewer().removeEntries(entries);
		}
	}
}
