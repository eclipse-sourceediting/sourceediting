package org.eclipse.wst.xsl.internal;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.preferences.XMLUIPreferenceNames;
import org.eclipse.wst.xml.ui.internal.templates.TemplateContextTypeIdsXML;

/**
 * XSLUIPlugin
 * @author dcarver
 *
 */
public class XSLUIPlugin extends AbstractUIPlugin {
	
	/**
	 * A Singleton Instance of this plugin.
	 */
	protected static XSLUIPlugin INSTANCE;
	
	/**
	 * The template store for the xsl editor.
	 * 
	 */
	private TemplateStore fTemplateStore;
	
    private ScopedPreferenceStore preferenceStore;
    
	/**
	 * The template context type registry for the xml editor.
	 */
	private ContributionContextTypeRegistry fContextTypeRegistry;
	
	/**
	 * The plugin id for this plugin.
	 */
	static public String PLUGIN_ID = "org.eclipse.wst.xsl.ui";
	/**
	 * Constructor Class
	 */
	public XSLUIPlugin() {
	   super();
	   INSTANCE = this;
	}
	
	/**
	 * Returns a default instance
	 * @return
	 */
	public static XSLUIPlugin getDefault() {
		if (INSTANCE == null) {
			INSTANCE = new XSLUIPlugin();
		}
		return INSTANCE;
	}

	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public synchronized static XSLUIPlugin getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new XSLUIPlugin();
		}
		return INSTANCE;
	}
	
	
	
	/**
	 * Utility methods for logging exceptions.
	 * 
	 * @param e 
	 * @param severity 
	 */
	public static void log(Throwable e, int severity) {
		IStatus status = null;
		if (e instanceof CoreException) {
			status = ((CoreException)e).getStatus();
		} else {
			String m = e.getMessage();
			status = new Status(severity, PLUGIN_ID, 0, m==null? "<no message>" : m, e); //$NON-NLS-1$
		}
		System.out.println(e.getClass().getName()+": "+status); //$NON-NLS-1$
		INSTANCE.getLog().log(status);
	}
	
	/**
	 * 
	 * @param throwable
	 */
	public static void log(Throwable throwable) { 
		log(throwable, IStatus.ERROR); }	

	
	/**
	 * TODO: Add Javadoc
	 */
    @Override
	public IPreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = (ScopedPreferenceStore) PlatformUI
					.getPreferenceStore();
		}
		return preferenceStore;
	}
    
	/**
	 * Returns the template store for the xml editor templates.
	 * 
	 * @return the template store for the xml editor templates
	 */
	public TemplateStore getTemplateStore() {
		if (fTemplateStore == null) {
			fTemplateStore = new ContributionTemplateStore(getTemplateContextRegistry(), getPreferenceStore(), "org.eclipse.wst.xsl.ui.xpath_custom_templates");
			try {
				fTemplateStore.load();
			}
			catch (IOException e) {
				XSLUIPlugin.log(e);
			}
		}
		return fTemplateStore;
	}
	
	/**
	 * Returns the template context type registry for the xsl plugin.
	 * 
	 * @return the template context type registry for the xsl plugin
	 */
	public ContextTypeRegistry getTemplateContextRegistry() {
		if (fContextTypeRegistry == null) {
			ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
			registry.addContextType("xsl_xpath");
			registry.addContextType("xpath_operator");
			registry.addContextType("xpath_axis");
			registry.addContextType("exslt_function");
			registry.addContextType("xpath_2");
			registry.addContextType("extension_function");
			fContextTypeRegistry = registry;
		}

		return fContextTypeRegistry;
	}
}
