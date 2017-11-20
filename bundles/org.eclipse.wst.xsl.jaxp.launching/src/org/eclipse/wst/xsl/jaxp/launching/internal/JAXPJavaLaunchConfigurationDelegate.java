/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsl.jaxp.launching.IDebugger;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInstall;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.launching.IProcessorJar;
import org.eclipse.wst.xsl.jaxp.launching.ITransformerFactory;
import org.eclipse.wst.xsl.jaxp.launching.JAXPLaunchConfigurationConstants;
import org.eclipse.wst.xsl.jaxp.launching.JAXPRuntime;
import org.eclipse.wst.xsl.jaxp.launching.model.JAXPDebugTarget;
import org.eclipse.wst.xsl.launching.model.IXSLConstants;

public class JAXPJavaLaunchConfigurationDelegate extends JavaLaunchDelegate
		implements IDebugEventSetListener {
	private String mode;
	LaunchHelper launchHelper;

	@Override
	public synchronized void launch(ILaunchConfiguration configuration,
			final String mode, final ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		this.mode = mode;
		launchHelper.save(getLaunchConfigFile());

		// set the launch name
		IProcessorInstall install = getProcessorInstall(configuration, mode);
		String tfactory = getTransformerFactory(install);
		String name = install.getName();
		if (tfactory != null)
			name += "[" + tfactory + "]"; //$NON-NLS-1$//$NON-NLS-2$
		launch.setAttribute("launchName", name); //$NON-NLS-1$

		// the super.launch will add a Java source director if we set it to null
		// here
		final ISourceLocator configuredLocator = launch.getSourceLocator();
		launch.setSourceLocator(null);

		super.launch(configuration, mode, launch, monitor);

		// now get the java source locator
		final ISourceLocator javaSourceLookupDirector = launch
				.getSourceLocator();
		// now add our own participant to the java director
		launch.setSourceLocator(new ISourceLocator() {

			public Object getSourceElement(IStackFrame stackFrame) {
				// simply look at one and then the other
				Object sourceElement = javaSourceLookupDirector
						.getSourceElement(stackFrame);
				if (sourceElement == null)
					sourceElement = configuredLocator
							.getSourceElement(stackFrame);
				return sourceElement;
			}
		});

		// IJavaDebugTarget javaTarget =
		// (IJavaDebugTarget)launch.getDebugTarget();
		// launch.removeDebugTarget(javaTarget);

		IDebugTarget javaTarget = launch.getDebugTarget();
		IDebugTarget xslTarget = new JAXPDebugTarget(launch, launch
				.getProcesses()[0], launchHelper);

		// remove java as the primary target and make xsl the primary target
		launch.removeDebugTarget(javaTarget);
		launch.addDebugTarget(xslTarget);
		// add this here to make java the non-primary target
		// launch.addDebugTarget(javaTarget);

		// launch.addDebugTarget(new JavaXSLDebugTarget(launch,
		// launch.getProcesses()[0], launchHelper, javaTarget));

	}

	/**
	 * Get the Java breakpoint and the XSL breakpoints
	 */
	@Override
	protected IBreakpoint[] getBreakpoints(ILaunchConfiguration configuration) {
		IBreakpointManager breakpointManager = DebugPlugin.getDefault()
				.getBreakpointManager();
		if (!breakpointManager.isEnabled())
			return null;

		IBreakpoint[] javaBreakpoints = super.getBreakpoints(configuration);
		IBreakpoint[] xslBreakpoints = breakpointManager
				.getBreakpoints(IXSLConstants.ID_XSL_DEBUG_MODEL);
		IBreakpoint[] breakpoints = new IBreakpoint[javaBreakpoints.length
				+ xslBreakpoints.length];
		System.arraycopy(javaBreakpoints, 0, breakpoints, 0,
				javaBreakpoints.length);
		System.arraycopy(xslBreakpoints, 0, breakpoints,
				javaBreakpoints.length, xslBreakpoints.length);

		return breakpoints;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		this.launchHelper = new LaunchHelper(configuration);
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			// TODO don't like having UI code in the launching plugin...where
			// else can it go?
			final IProcessorInstall install = getProcessorInstall(
					configuration, ILaunchManager.RUN_MODE);
			if (install.getDebugger() == null) {
				final boolean[] result = new boolean[] { false };
				// open a dialog for choosing a different install that does have
				// an associated debugger
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					public void run() {
						String debuggingInstallId = JAXPLaunchingPlugin
								.getDefault()
								.getPluginPreferences()
								.getString(
										JAXPLaunchConfigurationConstants.ATTR_DEFAULT_DEBUGGING_INSTALL_ID);
						IProcessorInstall processor = JAXPRuntime
								.getProcessor(debuggingInstallId);

						IWorkbenchWindow dw = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow();

						String title = Messages.XSLTLaunchConfigurationDelegate_0;
						String message = Messages.XSLTLaunchConfigurationDelegate_1
								+ install.getName()
								+ Messages.XSLTLaunchConfigurationDelegate_2
								+ Messages.XSLTLaunchConfigurationDelegate_3
								+ processor.getName()
								+ Messages.XSLTLaunchConfigurationDelegate_4;

						MessageDialog dialog = new MessageDialog(dw.getShell(),
								title, null, message, MessageDialog.QUESTION,
								new String[] { IDialogConstants.OK_LABEL,
										IDialogConstants.CANCEL_LABEL }, 0); // yes
																				// is
																				// the
																				// default

						result[0] = dialog.open() == 0;
					}
				});
				return result[0];
			} else {
				String debuggerTF = install.getDebugger()
						.getTransformerFactory();
				String installTF = launchHelper.getTransformerFactory() == null ? null
						: launchHelper.getTransformerFactory()
								.getFactoryClass();
				if (!debuggerTF.equals(installTF)) {
					PlatformUI.getWorkbench().getDisplay().syncExec(
							new Runnable() {
								public void run() {
									IWorkbenchWindow dw = PlatformUI
											.getWorkbench()
											.getActiveWorkbenchWindow();

									String title = Messages.JAXPJavaLaunchConfigurationDelegate_0;
									String message = install.getName()
											+ Messages.JAXPJavaLaunchConfigurationDelegate_1
											+ launchHelper
													.getTransformerFactory()
													.getName()
											+ Messages.JAXPJavaLaunchConfigurationDelegate_2
											+ Messages.JAXPJavaLaunchConfigurationDelegate_3
											+ launchHelper
													.getTransformerFactory()
													.getName()
											+ Messages.JAXPJavaLaunchConfigurationDelegate_4;

									MessageDialog dialog = new MessageDialog(
											dw.getShell(),
											title,
											null,
											message,
											MessageDialog.WARNING,
											new String[] {
													IDialogConstants.OK_LABEL,
													IDialogConstants.CANCEL_LABEL },
											0); // yes is the default
									dialog.open();
								}
							});
				}
			}
		}
		return super.preLaunchCheck(configuration, mode, monitor);
	}

	@Override
	public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		// comment this out in order to get java debugging as well as XSL
		// debugging
		// if (ILaunchManager.DEBUG_MODE.equals(mode))
		// return super.getVMRunner(configuration, ILaunchManager.RUN_MODE);
		return super.getVMRunner(configuration, mode);
	}

	private File getLaunchConfigFile() {
		IPath launchPath = Platform.getStateLocation(
				JAXPLaunchingPlugin.getDefault().getBundle()).append("launch"); //$NON-NLS-1$
		File launchDir = launchPath.toFile();
		if (!launchDir.exists()) {
			 launchDir.mkdir();
		}
		File file = new File(launchDir, "launch.xml"); //$NON-NLS-1$
		return file;
	}

	@Override
	public IPath getWorkingDirectoryPath(ILaunchConfiguration configuration)
			throws CoreException {
		return launchHelper.getWorkingDir();
	}

	private IProcessorInvoker getProcessorInvokerDescriptor(
			ILaunchConfiguration configuration) throws CoreException {
		String invokerId = configuration.getAttribute(
				JAXPLaunchConfigurationConstants.INVOKER_DESCRIPTOR,
				Messages.JAXPJavaLaunchConfigurationDelegate_5);
		return JAXPRuntime.getProcessorInvoker(invokerId);
	}

	public static IProcessorInstall getProcessorInstall(
			ILaunchConfiguration configuration, String mode)
			throws CoreException {
		IProcessorInstall install = LaunchHelper
				.getProcessorInstall(configuration);
		if (mode.equals(ILaunchManager.DEBUG_MODE)
				&& install.getDebugger() == null) {
			String debuggingInstallId = JAXPLaunchingPlugin
					.getDefault()
					.getPluginPreferences()
					.getString(
							JAXPLaunchConfigurationConstants.ATTR_DEFAULT_DEBUGGING_INSTALL_ID);
			install = JAXPRuntime.getProcessor(debuggingInstallId);
		}
		return install;
	}

	@Override
	public String getMainTypeName(ILaunchConfiguration configuration)
			throws CoreException {
		if (ILaunchManager.DEBUG_MODE.equals(mode))
			return "org.eclipse.wst.xsl.jaxp.debug.debugger.DebugRunner"; //$NON-NLS-1$
		return "org.eclipse.wst.xsl.jaxp.debug.invoker.internal.Main"; //$NON-NLS-1$
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration)
			throws CoreException {
		// classname, sourceurl, output file
		IProcessorInvoker invoker = getProcessorInvokerDescriptor(configuration);
		String clazz = invoker.getInvokerClassName();

		StringBuffer sb = new StringBuffer();
		sb.append(clazz);
		sb.append(" "); //$NON-NLS-1$
		sb.append("\"" + getLaunchConfigFile().getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(" "); //$NON-NLS-1$
		sb.append("\"" + launchHelper.getSource() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(" "); //$NON-NLS-1$
		sb.append("\"" + launchHelper.getTarget().getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			IProcessorInstall install = getProcessorInstall(configuration, mode);
			if (install.getDebugger() != null) {
				IDebugger debugger = install.getDebugger();
				String className = debugger.getClassName();
				sb.append(" -debug ").append(className).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
				sb.append(launchHelper.getRequestPort());
				sb.append(" ").append(launchHelper.getEventPort()); //$NON-NLS-1$
				sb.append(" ").append(launchHelper.getGeneratePort()); //$NON-NLS-1$
			}
		}

		return sb.toString();
	}

	@Override
	public String[] getClasspath(ILaunchConfiguration configuration)
			throws CoreException {
		// get the classpath defined by the user
		String[] userClasspath = super.getClasspath(configuration);

		// get the classpath required for the transformation
		IProcessorInvoker invoker = getProcessorInvokerDescriptor(configuration);
		List<String> invokerCP = new ArrayList<String>();
		for (String entry : invoker.getClasspathEntries()) {
			invokerCP.add(entry);
		}

		// add the debugger...
		IProcessorInstall install = getProcessorInstall(configuration, mode);
		if (ILaunchManager.DEBUG_MODE.equals(mode)
				&& install.getDebugger() != null) {
			String[] jars = install.getDebugger().getClassPath();
			for (String jar : jars) {
				invokerCP.add(jar);
			}
		}

		String[] invokerClasspath = invokerCP.toArray(new String[0]);

		// add them together
		String[] classpath = new String[userClasspath.length
				+ invokerClasspath.length];
		System.arraycopy(invokerClasspath, 0, classpath, 0,
				invokerClasspath.length);
		System.arraycopy(userClasspath, 0, classpath, invokerClasspath.length,
				userClasspath.length);

		return classpath;
	}

	@Override
	public String getVMArguments(ILaunchConfiguration configuration)
			throws CoreException {
		String vmargs = super.getVMArguments(configuration);

		IProcessorInstall install = getProcessorInstall(configuration, mode);
		if (install != null && !install.getProcessorType().isJREDefault()) {
			// clear the endorsed dir
			File tempDir = getEndorsedDir();
			if (tempDir.exists()) {
				File[] children = tempDir.listFiles();
				for (File child : children) {
					child.delete();
				}
				tempDir.delete();
			}
			tempDir.mkdir();

			// move the required jars to the endorsed dir
			IProcessorJar[] jars = install.getProcessorJars();
			for (int i = 0; i < jars.length; i++) {
				URL entry = jars[i].asURL();
				if (entry == null)
					throw new CoreException(new Status(IStatus.ERROR,
							JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR,
							Messages.XSLTLaunchConfigurationDelegate_23
									+ jars[i], null));
				File file = new File(tempDir, "END_" + i + ".jar"); //$NON-NLS-1$ //$NON-NLS-2$
				moveFile(entry, file);
			}
			// add the endorsed dir
			vmargs += " -Djava.endorsed.dirs=\"" + tempDir.getAbsolutePath() + "\""; //$NON-NLS-1$ //$NON-NLS-2$

			String tfactory = getTransformerFactory(install);
			if (tfactory != null)
				vmargs += " -Djavax.xml.transform.TransformerFactory=" + tfactory; //$NON-NLS-1$

			// if (ILaunchManager.DEBUG_MODE.equals(mode))
			// {
			// // in debug mode, set the logging to ERROR. This prevents the
			// console from popping up on top of the result view!
			// try
			// {
			// URL url =
			// FileLocator.resolve(FileLocator.find(Platform.getBundle(JAXPLaunchingPlugin.PLUGIN_ID),
			// new Path("/log4j.debug.properties"), null));
			//					vmargs += " -Dlog4j.configuration=\""+url.toExternalForm()+"\""; //$NON-NLS-1$
			// }
			// catch (IOException e)
			// {
			// JAXPLaunchingPlugin.log(e);
			// }
			// }
		}
		return vmargs;
	}

	private String getTransformerFactory(IProcessorInstall install) {
		String tfactory = null;
		if (ILaunchManager.DEBUG_MODE.equals(mode))
			tfactory = install.getDebugger().getTransformerFactory();
		else {
			ITransformerFactory t = launchHelper.getTransformerFactory();
			if (t != null)
				tfactory = t.getFactoryClass();
		}
		return tfactory;
	}

	private File getEndorsedDir() {
		IPath tempLocation = Platform.getStateLocation(
				JAXPLaunchingPlugin.getDefault().getBundle())
				.append("endorsed"); //$NON-NLS-1$
		return tempLocation.toFile();
	}

	private static void moveFile(URL src, File target) throws CoreException {
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(target));
			bis = new BufferedInputStream(src.openStream());
			while (bis.available() > 0) {
				int size = bis.available();
				if (size > 1024)
					size = 1024;
				byte[] b = new byte[size];
				bis.read(b, 0, b.length);
				bos.write(b);
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					JAXPLaunchingPlugin.PLUGIN_ID, IStatus.ERROR,
					Messages.XSLTLaunchConfigurationDelegate_7 + src
							+ Messages.XSLTLaunchConfigurationDelegate_31
							+ target, e));
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					JAXPLaunchingPlugin.log(e);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					JAXPLaunchingPlugin.log(e);
				}
			}
		}
	}
}
