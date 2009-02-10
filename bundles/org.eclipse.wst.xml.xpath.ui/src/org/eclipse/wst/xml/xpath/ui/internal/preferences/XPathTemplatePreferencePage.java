/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.xpath.ui.internal.preferences;

import org.eclipse.wst.xml.ui.internal.preferences.XMLTemplatePreferencePage;
import org.eclipse.wst.xml.xpath.ui.internal.XPathUIPlugin;

/**
 * XSLTemplatePreferencePage sets up the template preference page that contains
 * the templates for xpath, xpath 2.0, axis, operators, and exslt functions.
 * 
 * @author dcarver
 * 
 */
public class XPathTemplatePreferencePage extends XMLTemplatePreferencePage {

	/**
	 * 
	 */
	public XPathTemplatePreferencePage() {
		setPreferenceStore(XPathUIPlugin.getDefault().getPreferenceStore());
		setTemplateStore(XPathUIPlugin.getDefault().getXPathTemplateStore());
		setContextTypeRegistry(XPathUIPlugin.getDefault()
				.getXPathTemplateContextRegistry());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		XPathUIPlugin.getDefault().savePluginPreferences();
		return ok;
	}
}
