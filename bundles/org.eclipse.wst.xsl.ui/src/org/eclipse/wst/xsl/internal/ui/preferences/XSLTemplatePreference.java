/**
 * 
 */
package org.eclipse.wst.xsl.internal.ui.preferences;

import org.eclipse.wst.xml.ui.internal.preferences.XMLTemplatePreferencePage;
import org.eclipse.wst.xsl.internal.XSLUIPlugin;

/**
 * @author dcarver
 *
 */
public class XSLTemplatePreference extends XMLTemplatePreferencePage {

	/**
	 * 
	 */
	public XSLTemplatePreference() {
		setPreferenceStore(XSLUIPlugin.getDefault().getPreferenceStore());
		setTemplateStore(XSLUIPlugin.getDefault().getTemplateStore());
		setContextTypeRegistry(XSLUIPlugin.getDefault().getTemplateContextRegistry());

		
	}
	
	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@SuppressWarnings("restriction")
	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		XSLUIPlugin.getDefault().savePluginPreferences();
		return ok;
	}	

	
	

}
