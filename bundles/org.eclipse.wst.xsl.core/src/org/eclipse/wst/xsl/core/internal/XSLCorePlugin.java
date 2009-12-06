/*******************************************************************************
 * Copyright (c) 2008 Jesper Steen M�ller and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jesper Steen M�ller - XSL core plugin
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal;

import javax.xml.parsers.SAXParserFactory;

import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class XSLCorePlugin extends Plugin {

	// The plug-in ID
	/**
	 * TODO: Add JavaDoc
	 */
	public static final String PLUGIN_ID = "org.eclipse.wst.xsl.core"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public static final String USE_PROJECT_SETTINGS = "use-project-settings";//$NON-NLS-1$
	
	// The shared instance
	private static XSLCorePlugin plugin;
	
	private SAXParserFactoryImpl saxParserFactory = null;

	/**
	 * The constructor
	 */
	public XSLCorePlugin() {
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
	public static XSLCorePlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Return the registered SAX parser factory or null if one
	 * does not exist.	 * @return
	 * @return returns a SAXParserFactory
	 */
	public SAXParserFactory getFactory() {
		if (saxParserFactory == null) {
			saxParserFactory = new SAXParserFactoryImpl();
		}
		SAXParserFactory theFactory = saxParserFactory; 
		if (theFactory != null) {
			theFactory.setNamespaceAware(true);
			theFactory.setXIncludeAware(false);
			theFactory.setValidating(false);
		}
		return theFactory;
	}
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Logs the specified exception.
	 * 
	 * @param e throwable to log 
	 */
	public static void log(Throwable e) {
		if (e instanceof CoreException) {
			log(((CoreException)e).getStatus());
		} else {
			log(newErrorStatus(Messages.XSLCorePlugin_coreError, e));
		}
	}
	
	/**
	 * Returns a new error status for this plug-in with the given message
	 * @param message the message to be included in the status
	 * @param exception the exception to be included in the status or <code>null</code> if none
	 * @return a new error status
	 */
	public static IStatus newErrorStatus(String message, Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, exception);
	}	
}
