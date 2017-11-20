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

import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.widgets.Control;

/**
 * Used to help with accessibility.
 * 
 * @author Doug Satchwell
 */
public class ControlAccessibleListener extends AccessibleAdapter {
	private final String controlName;

	private ControlAccessibleListener(String name) {
		controlName = name;
	}

	@Override
	public void getName(AccessibleEvent e) {
		e.result = controlName;
	}

	/**
	 * Helper for adding an instance of this to the given control.
	 * 
	 * @param comp
	 *            the control to add this to
	 * @param name
	 *            the name for this
	 */
	public static void addListener(Control comp, String name) {
		// strip mnemonic
		String[] strs = name.split("&"); //$NON-NLS-1$
		StringBuffer stripped = new StringBuffer();
		for (String element : strs) {
			stripped.append(element);
		}
		comp.getAccessible().addAccessibleListener(
				new ControlAccessibleListener(stripped.toString()));
	}
}
