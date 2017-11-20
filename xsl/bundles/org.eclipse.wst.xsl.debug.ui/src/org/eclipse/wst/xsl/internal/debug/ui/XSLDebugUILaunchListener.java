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

import java.io.File;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.xsl.launching.config.BaseLaunchHelper;

/**
 * A listener to XSL launches. When an XSL launch is terminated, this performs
 * the UI parts of the XSL launching - such as open the editor on the output
 * file, and format it.
 * 
 * @author Doug Satchwell
 * @since 1.0
 */
public class XSLDebugUILaunchListener implements ILaunchesListener2 {
	public static final String XSL_LAUNCH_CONFIGURATION_TYPE = "org.eclipse.wst.xsl.launching.launchConfigurationType"; //$NON-NLS-1$

	/**
	 * Starts the launch listening
	 */
	public void start() {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}

	/**
	 * Stops the launch listening
	 */
	public void stop() {
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
	}

	public void launchesTerminated(ILaunch[] launches) {
		for (ILaunch launch : launches) {
			ILaunchConfigurationType configType = null;
			try {
				configType = launch.getLaunchConfiguration().getType();
			} catch (CoreException e) {
				// do nothing
			}
			if (configType != null
					&& XSL_LAUNCH_CONFIGURATION_TYPE.equals(configType
							.getIdentifier())) {
				try {
					BaseLaunchHelper launchHelper = new BaseLaunchHelper(launch
							.getLaunchConfiguration());
					File file = launchHelper.getTarget();
					IFile ifile = ResourcesPlugin.getWorkspace().getRoot()
							.getFileForLocation(
									new Path(file.getAbsolutePath()));
					if (ifile != null) {// refresh this workspace file..
						try {
							ifile.refreshLocal(IResource.DEPTH_ZERO,
									new NullProgressMonitor());
						} catch (CoreException e) {
							XSLDebugUIPlugin.log(e);
						}
					}
					openFileIfRequired(launchHelper);
				} catch (CoreException e) {
					XSLDebugUIPlugin.log(e);
				}
			}
		}
	}

	public void launchesAdded(ILaunch[] launches) {
		// do nothing
	}

	public void launchesChanged(ILaunch[] launches) {
		// do nothing
	}

	public void launchesRemoved(ILaunch[] launches) {
		// do nothing
	}

	private void openFileIfRequired(final BaseLaunchHelper launchHelper) {
		if (launchHelper.getOpenFileOnCompletion()) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						// Open editor on new file.
						IWorkbenchWindow dw = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow();
						File file = launchHelper.getTarget();
						Path path = new Path(file.getAbsolutePath());
						IFileStore filestore = EFS.getLocalFileSystem()
								.getStore(path);
						IDE
								.openEditorOnFileStore(dw.getActivePage(),
										filestore);

						if (launchHelper.getFormatFileOnCompletion()) {
							// format the editor contents
							IHandlerService p = (IHandlerService) PlatformUI
									.getWorkbench().getService(
											IHandlerService.class);
							p
									.executeCommand(
											"org.eclipse.wst.sse.ui.format.document", null); //$NON-NLS-1$
						}
					} catch (PartInitException e) {
						XSLDebugUIPlugin.log(e);
					} catch (CommandException e) {
						XSLDebugUIPlugin.log(e);
					}
				}
			});
		}
	}
}
