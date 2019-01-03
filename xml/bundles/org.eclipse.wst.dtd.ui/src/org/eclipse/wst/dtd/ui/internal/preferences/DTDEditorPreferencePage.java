/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.dtd.ui.internal.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.wst.dtd.ui.internal.DTDUIMessages;
import org.eclipse.wst.dtd.ui.internal.DTDUIPlugin;

public class DTDEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DTDEditorPreferencePage() {
		super();
	}


	public DTDEditorPreferencePage(int style) {
		super(style);
	}


	public DTDEditorPreferencePage(String title, ImageDescriptor image, int style) {
		super(title, image, style);
	}


	public DTDEditorPreferencePage(String title, int style) {
		super(title, style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(DTDUIPreferenceNames.ACTIVATE_PROPERTIES, DTDUIMessages.ShowProperties, getFieldEditorParent()));
	}

	protected Control createContents(Composite parent) {
		setPreferenceStore(DTDUIPlugin.getDefault().getPreferenceStore());
		new PreferenceLinkArea(parent, SWT.WRAP | SWT.MULTI, "org.eclipse.wst.sse.ui.preferences.editor", DTDUIMessages._UI_STRUCTURED_TEXT_EDITOR_PREFS_LINK, (IWorkbenchPreferenceContainer) getContainer(), null).getControl().setLayoutData(GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).grab(true, false).create());//$NON-NLS-1$
		new Label(parent, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());
		return super.createContents(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
}
