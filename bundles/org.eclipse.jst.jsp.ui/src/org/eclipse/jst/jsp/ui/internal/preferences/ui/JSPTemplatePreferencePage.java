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
package org.eclipse.jst.jsp.ui.internal.preferences.ui;

import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.editor.IHelpContextIds;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;


/**
 * Preference page for JSP templates
 */
public class JSPTemplatePreferencePage extends TemplatePreferencePage {

	public JSPTemplatePreferencePage() {
		JSPUIPlugin jspEditorPlugin = JSPUIPlugin.getDefault();
		
		setPreferenceStore(jspEditorPlugin.getPreferenceStore());
		setTemplateStore(jspEditorPlugin.getTemplateStore());
		setContextTypeRegistry(jspEditorPlugin.getTemplateContextRegistry());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
  	  boolean ok = super.performOk();
  	  JSPUIPlugin.getDefault().savePluginPreferences();
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
		PlatformUI.getWorkbench().getHelpSystem().setHelp(c, IHelpContextIds.JSP_PREFWEBX_TEMPLATES_HELPID);
		return c;
	}
}
