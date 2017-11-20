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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;
import org.eclipse.wst.xsl.internal.debug.ui.tabs.main.StylesheetViewer;

/**
 * An action that opens a dialog.
 * 
 * @author Doug Satchwell
 */
public class OpenDialogAction extends AbstractStylesheetAction {
	protected static final String LAST_PATH_SETTING = "LAST_PATH_SETTING"; //$NON-NLS-1$
	private String fPrefix = null;

	/**
	 * Create a new instance of this
	 * 
	 * @param label
	 *            the dialog title
	 * @param viewer
	 *            a viewer that this dialog is associated with
	 * @param dialogSettingsPrefix
	 *            the prefix to use for saving dialog preferences
	 */
	public OpenDialogAction(String label, StylesheetViewer viewer,
			String dialogSettingsPrefix) {
		super(label, viewer);
		fPrefix = dialogSettingsPrefix;
	}

	protected String getDialogSettingsPrefix() {
		return fPrefix;
	}

	protected String getDialogSetting(String settingName) {
		return getDialogSettings().get(
				getDialogSettingsPrefix() + "." + settingName); //$NON-NLS-1$
	}

	protected void setDialogSetting(String settingName, String value) {
		getDialogSettings().put(
				getDialogSettingsPrefix() + "." + settingName, value); //$NON-NLS-1$
	}

	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = XSLDebugUIPlugin.getDefault()
				.getDialogSettings();
		return settings;
	}

	@Override
	protected int getActionType() {
		return ADD;
	}
}
