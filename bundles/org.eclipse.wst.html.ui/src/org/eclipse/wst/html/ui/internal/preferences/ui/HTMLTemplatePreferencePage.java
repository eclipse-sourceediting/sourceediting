/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.ui.internal.preferences.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.eclipse.wst.html.ui.internal.HTMLUIPlugin;
import org.eclipse.wst.html.ui.internal.editor.IHelpContextIds;


/**
 * Preference page for HTML templates
 */
public class HTMLTemplatePreferencePage extends TemplatePreferencePage {
	
	public HTMLTemplatePreferencePage() {
		HTMLUIPlugin htmlEditorPlugin = HTMLUIPlugin.getDefault();
		
		setPreferenceStore(htmlEditorPlugin.getPreferenceStore());
		setTemplateStore(htmlEditorPlugin.getTemplateStore());
		setContextTypeRegistry(htmlEditorPlugin.getTemplateContextRegistry());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
  	  boolean ok = super.performOk();
  	  HTMLUIPlugin.getDefault().savePluginPreferences();
	  return ok;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#isShowFormatterSetting()
	 */
	protected boolean isShowFormatterSetting() {
		// template formatting has not been implemented
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite ancestor) {
		Control c = super.createContents(ancestor);
		WorkbenchHelp.setHelp(c, IHelpContextIds.HTML_PREFWEBX_TEMPLATES_HELPID);
		return c;
	}
}
