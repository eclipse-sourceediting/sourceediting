package org.eclipse.wst.xml.xpath2.processor.test;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class XPath20TestPlugin extends Plugin {

	// The shared instance
	private static XPath20TestPlugin plugin;
	
	public XPath20TestPlugin() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	public static XPath20TestPlugin getDefault() {
		return plugin;
	}
	
}
