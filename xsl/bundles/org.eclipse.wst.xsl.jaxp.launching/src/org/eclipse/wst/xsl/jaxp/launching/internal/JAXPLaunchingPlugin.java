package org.eclipse.wst.xsl.jaxp.launching.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JAXPLaunchingPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.wst.xsl.jaxp.launching"; //$NON-NLS-1$

	// The shared instance
	private static JAXPLaunchingPlugin plugin;
	
	/**
	 * The constructor
	 */
	public JAXPLaunchingPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JAXPLaunchingPlugin getDefault() {
		return plugin;
	}

	public static void log(Exception e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 0, "", e)); //$NON-NLS-1$
	}

	public static void log(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}
}
