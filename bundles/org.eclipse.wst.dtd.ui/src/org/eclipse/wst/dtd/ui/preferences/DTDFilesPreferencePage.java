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
package org.eclipse.wst.dtd.ui.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.dtd.core.DTDPlugin;
import org.eclipse.wst.dtd.ui.DTDEditorPlugin;
import org.eclipse.wst.dtd.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage;


public class DTDFilesPreferencePage extends AbstractPreferencePage {

	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents(parent);
		WorkbenchHelp.setHelp(composite, IHelpContextIds.DTD_PREFWEBX_FILES_HELPID);

		setSize(composite);
		loadPreferences();

		return composite;
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return DTDEditorPlugin.getDefault().getPreferenceStore();
	}

	protected void doSavePreferenceStore() {
		DTDPlugin.getInstance().savePluginPreferences(); // model
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.sse.ui.preferences.ui.AbstractPreferencePage#getModelPreferences()
	 */
	protected Preferences getModelPreferences() {
		return DTDPlugin.getInstance().getPluginPreferences();
	}

	public boolean performOk() {
		boolean result = super.performOk();

		doSavePreferenceStore();

		return result;
	}
}
