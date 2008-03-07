package org.eclipse.wst.xsl.internal.ui.preferences;

import java.io.IOException;

import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.wst.xsl.internal.XSLUIPlugin;

/**
 * 
 * @author dcarver
 *
 */
public class XPathEditorTemplateAccess {
	static XPathEditorTemplateAccess templateAccess = null;
	
	/** The template store. */
	private ContributionTemplateStore fStore;
	
	/** The context type registry. */
	private ContributionContextTypeRegistry fRegistry;
	
	/**
	 * 
	 * @return
	 */
	public static XPathEditorTemplateAccess getDefault() {
		if (templateAccess == null) {
			templateAccess  = new XPathEditorTemplateAccess();
		}
		
		return templateAccess;
	}
	
	/**
	 * Returns this plug-in's template store.
	 * 
	 * @return the template store of this plug-in instance
	 */
	public ContributionTemplateStore getTemplateStore() {
		if (fStore == null) {
			fStore= new ContributionTemplateStore(//null,
					getContextTypeRegistry(),
					XSLUIPlugin.getInstance().getPreferenceStore(), "xpath.editor");
			try {
				fStore.load();
			} catch (IOException e) {
				XSLUIPlugin.log(e);
			}
		}
		return fStore;
	}

	/**
	 * Returns this plug-in's context type registry.
	 * 
	 * @return the context type registry for this plug-in instance
	 */
	public ContributionContextTypeRegistry getContextTypeRegistry() {
		if (fRegistry == null) {
			// create and configure the contexts available in the template editor
			fRegistry= new ContributionContextTypeRegistry();
			fRegistry.addContextType("xpath");
			//fRegistry.addContextType("jscript");

		}
		return fRegistry;
	}

}
