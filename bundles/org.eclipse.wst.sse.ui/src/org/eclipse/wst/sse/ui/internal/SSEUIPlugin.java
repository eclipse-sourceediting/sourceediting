/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal;



import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.sse.core.internal.tasks.TaskScanningScheduler;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistry;
import org.eclipse.wst.sse.ui.internal.provisional.registry.AdapterFactoryRegistryImpl;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.osgi.framework.BundleContext;


public class SSEUIPlugin extends AbstractUIPlugin {

	public final static String ID = "org.eclipse.wst.sse.ui"; //$NON-NLS-1$

	static SSEUIPlugin instance = null;
	
	private FormToolkit fDialogsFormToolkit;

	public static SSEUIPlugin getDefault() {
		return instance;
	}

	public synchronized static SSEUIPlugin getInstance() {
		return instance;
	}

	private TextHoverManager fTextHoverManager;

	public SSEUIPlugin() {
		super();
		instance = this;
	}

	public AdapterFactoryRegistry getAdapterFactoryRegistry() {
		return AdapterFactoryRegistryImpl.getInstance();

	}

	/**
	 * Return text hover manager
	 * 
	 * @return TextHoverManager
	 */
	public TextHoverManager getTextHoverManager() {
		if (fTextHoverManager == null) {
			fTextHoverManager = new TextHoverManager();
		}
		return fTextHoverManager;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		/**
		 * If the user starts the workbench with
		 * -Dorg.eclipse.wst.sse.core.taskscanner=off, the scanner should be
		 * disabled
		 */
		String scan = System.getProperty("org.eclipse.wst.sse.core.taskscanner"); //$NON-NLS-1$
		if (scan == null || !scan.equalsIgnoreCase("off")) { //$NON-NLS-1$
			TaskScanningScheduler.startup();
		}
	}
	
	public FormToolkit getDialogsFormToolkit() {
		if (fDialogsFormToolkit == null) {
			FormColors colors= new FormColors(Display.getCurrent());
			colors.setBackground(null);
			colors.setForeground(null);	
			fDialogsFormToolkit= new FormToolkit(colors);
		}
		return fDialogsFormToolkit;
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		TaskScanningScheduler.shutdown();
	}
}
