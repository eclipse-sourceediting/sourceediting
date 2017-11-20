/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 213849 - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

import org.eclipse.wst.xml.ui.internal.preferences.XMLTemplatePreferencePage;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

/**
 * XSLTemplatePreferencePage sets up the template preference page that
 * contains the templates for xpath, xpath 2.0, axis, operators, and 
 * exslt functions. 
 * 
 * @author dcarver
 *
 */
public class XSLTemplatePreferencePage extends XMLTemplatePreferencePage {

	/**
	 * 
	 */
	public XSLTemplatePreferencePage() {
		setPreferenceStore(XSLUIPlugin.getDefault().getPreferenceStore());
		setTemplateStore(XSLUIPlugin.getDefault().getTemplateStore());
		setContextTypeRegistry(XSLUIPlugin.getDefault().getTemplateContextRegistry());
	}
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		XSLUIPlugin.getDefault().savePluginPreferences();
		return ok;
	}	
}
