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
package org.eclipse.wst.xsl.internal.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The XSL Debug UI plugin.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class XSLDebugUIPlugin extends AbstractUIPlugin {
	/**
	 * The id of this.
	 */
	public static final String PLUGIN_ID = "org.eclipse.wst.xsl.debug.ui"; //$NON-NLS-1$

	// The shared instance
	private static XSLDebugUIPlugin plugin;

	private XSLDebugUILaunchListener launchListener = new XSLDebugUILaunchListener();

	/**
	 * Create a new instance of this.
	 */
	public XSLDebugUIPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		launchListener.start();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		launchListener.stop();
		super.stop(context);
	}

	/**
	 * Get the singleton instance of this.
	 * 
	 * @return the singleton
	 */
	public static XSLDebugUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Get an ImageDescriptor from a path in this bundle.
	 * 
	 * @param path
	 *            the path to the image
	 * @return the ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Convenience method for opening a given preference page.
	 * 
	 * @param id
	 *            the id of the preference page
	 * @param page
	 *            the preference page to show
	 */
	public static void showPreferencePage(String id, IPreferencePage page) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);

		PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(XSLDebugUIPlugin
				.getActiveWorkbenchShell(), manager);
		final boolean[] result = new boolean[] { false };
		BusyIndicator.showWhile(XSLDebugUIPlugin.getStandardDisplay(),
				new Runnable() {
					public void run() {
						dialog.create();
						dialog.setMessage(targetNode.getLabelText());
						result[0] = (dialog.open() == Window.OK);
					}
				});
	}

	/**
	 * Get the current Display if possible, or else the default Display.
	 * 
	 * @return the current or default Display
	 */
	public static Display getStandardDisplay() {
		Display display;
		display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	/**
	 * Get the active workbench window from the workbench.
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * Get the shell of the active workbench window.
	 * 
	 * @return the active workbench shell
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	/**
	 * Get the active page of the active workbench window.
	 * 
	 * @return the active page
	 */
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow w = getActiveWorkbenchWindow();
		if (w != null) {
			return w.getActivePage();
		}
		return null;
	}

	/**
	 * Log the given exception by creating a new Status.
	 * 
	 * @param e
	 *            the exception to log
	 */
	public static void log(Exception e) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, 0, "", e)); //$NON-NLS-1$
	}

	/**
	 * Log the given core exception.
	 * 
	 * @param e
	 *            the exception to log
	 */
	public static void log(CoreException e) {
		getDefault().getLog().log(e.getStatus());
	}
}
