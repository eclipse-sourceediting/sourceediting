/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.preferences.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.eclipse.wst.xml.ui.XMLEditorPlugin;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;


/**
 * Preference page for XML templates
 */
public class XMLTemplatePreferencePage extends TemplatePreferencePage {
	
	public XMLTemplatePreferencePage() {
		XMLEditorPlugin xmlEditorPlugin = (XMLEditorPlugin) Platform.getPlugin(XMLEditorPlugin.ID);
		
		setPreferenceStore(xmlEditorPlugin.getPreferenceStore());
		setTemplateStore(xmlEditorPlugin.getTemplateStore());
		setContextTypeRegistry(xmlEditorPlugin.getTemplateContextRegistry());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
  	  boolean ok = super.performOk();
  	  Platform.getPlugin(XMLEditorPlugin.ID).savePluginPreferences();
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
		WorkbenchHelp.setHelp(c, IHelpContextIds.XML_PREFWEBX_TEMPLATES_HELPID);
		return c;
	}
}
