package org.eclipse.wst.xsl.tests;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class XSLTestPlugin extends Plugin {

	// The plug-in ID
	/**
	 * TODO: Add JavaDoc
	 */
	public static final String PLUGIN_ID = "org.eclipse.wst.xsl.core"; //$NON-NLS-1$

	private BundleContext bundleContext;
	
	// The shared instance
	private static XSLTestPlugin plugin;	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static XSLTestPlugin getDefault() {
		return plugin;
	}
	
}
