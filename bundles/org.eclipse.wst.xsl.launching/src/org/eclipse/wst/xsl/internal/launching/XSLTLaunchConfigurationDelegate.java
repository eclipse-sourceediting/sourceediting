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
package org.eclipse.wst.xsl.internal.launching;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugEvent;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.xsl.launching.IDebugger;
import org.eclipse.wst.xsl.launching.IProcessorInstall;
import org.eclipse.wst.xsl.launching.IProcessorInvoker;
import org.eclipse.wst.xsl.launching.IProcessorJar;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;
import org.eclipse.wst.xsl.launching.config.LaunchHelper;
import org.eclipse.wst.xsl.launching.model.IXSLConstants;
import org.eclipse.wst.xsl.launching.model.XSLDebugTarget;

public class XSLTLaunchConfigurationDelegate extends JavaLaunchDelegate implements IDebugEventSetListener
{
	private String mode;
	private LaunchHelper launchHelper;
	
	private class XSLDebugEventSetListener implements IDebugEventSetListener
	{
		private ILaunch launch;

		public XSLDebugEventSetListener(ILaunch launch)
		{
			this.launch = launch;
		}
		
		public void handleDebugEvents(DebugEvent[] events)
		{
			for (DebugEvent debugEvent : events)
			{
				if (debugEvent.getSource() == launch.getProcesses()[0] && debugEvent.getKind() == DebugEvent.TERMINATE)
				{
					// remove self as listener
					DebugPlugin.getDefault().removeDebugEventListener(this);
					File file = launchHelper.getTarget();
					IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(file.getAbsolutePath()));
					if (ifile != null)
					{// refresh this workspace file..
						try
						{
							ifile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor(){
								@Override
								public void done()
								{
									openFileIfRequired();
								}
							});
						}
						catch (CoreException e)
						{
							LaunchingPlugin.log(e);
						}
					}
					else
					{
						openFileIfRequired();
					}
				}
			}
		}

		private void openFileIfRequired()
		{
			// TODO this is dirty - need to declare extension point and move the UI code into the UI plugin
			if (launchHelper.getOpenFileOnCompletion())
			{
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
				{
					public void run()
					{
						// Open editor on new file.
						IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						try
						{
							File file = launchHelper.getTarget();
							Path path = new Path(file.getAbsolutePath());
							IFileStore filestore = EFS.getLocalFileSystem().getStore(path);
							IDE.openEditorOnFileStore(dw.getActivePage(), filestore);
						}
						catch (PartInitException e)
						{
							LaunchingPlugin.log(e);
						}
					}
				});
			}
		}
	}

	@Override
	public synchronized void launch(ILaunchConfiguration configuration, final String mode, final ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		this.mode = mode;
		launchHelper = new LaunchHelper(configuration);
		launchHelper.save(getLaunchConfigFile());
		DebugPlugin.getDefault().addDebugEventListener(new XSLDebugEventSetListener(launch));

		// the super.launch will add a Java source director if we set it to null here
		final ISourceLocator configuredLocator = launch.getSourceLocator();
		launch.setSourceLocator(null);

		super.launch(configuration, mode, launch, monitor);
		
		// now get the java source locator
		final ISourceLocator javaSourceLookupDirector = (ISourceLocator)launch.getSourceLocator();
		// now add our own participant to the java director
		launch.setSourceLocator(new ISourceLocator(){

			public Object getSourceElement(IStackFrame stackFrame) 
			{
				// simply look at one and then the other
				Object sourceElement = javaSourceLookupDirector.getSourceElement(stackFrame);
				if (sourceElement == null)
					sourceElement = configuredLocator.getSourceElement(stackFrame);
				return sourceElement;
			}});
		
		IDebugTarget target = new XSLDebugTarget(launch, launch.getProcesses()[0], launchHelper);
		launch.addDebugTarget(target);
	}

	/**
	 * Get the Java breakpoint and the XSL breakpoints
	 */
	@Override
	protected IBreakpoint[] getBreakpoints(ILaunchConfiguration configuration)
	{
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		if (!breakpointManager.isEnabled())
			return null;
		
		IBreakpoint[] javaBreakpoints = super.getBreakpoints(configuration);
		IBreakpoint[] xslBreakpoints = breakpointManager.getBreakpoints(IXSLConstants.ID_XSL_DEBUG_MODEL);
		IBreakpoint[] breakpoints = new IBreakpoint[javaBreakpoints.length+xslBreakpoints.length];
		System.arraycopy(javaBreakpoints, 0, breakpoints, 0, javaBreakpoints.length);
		System.arraycopy(xslBreakpoints, 0, breakpoints, javaBreakpoints.length, xslBreakpoints.length);
		
		return breakpoints;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException
	{
		if (mode.equals(ILaunchManager.DEBUG_MODE))
		{
			final IProcessorInstall install = getProcessorInstall(configuration, ILaunchManager.RUN_MODE);
			if (install.getDebugger() == null)
			{
				final boolean[] result = new boolean[]
				{ false };
				// open a dialog for choosing a different install that does have
				// an associated debugger
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						String debuggingInstallId = LaunchingPlugin.getDefault().getPluginPreferences().getString(XSLLaunchConfigurationConstants.ATTR_DEFAULT_DEBUGGING_INSTALL_ID);
						IProcessorInstall processor = XSLTRuntime.getProcessor(debuggingInstallId);

						IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						result[0] = MessageDialog.openQuestion(dw.getShell(), "XSLT Processor Debugger", "The " + install.getName() + " XSLT processor does not support debugging.\n"
								+ "Would you like to debug using the default " + processor.getName() + " processor instead?");
					}
				});
				return result[0];
			}
		}
		return super.preLaunchCheck(configuration, mode, monitor);
	}

	@Override
	public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) throws CoreException
	{
		// comment this out in order to get java debugging as well as XSL debugging
//		if (ILaunchManager.DEBUG_MODE.equals(mode))
//			return super.getVMRunner(configuration, ILaunchManager.RUN_MODE);
		return super.getVMRunner(configuration, mode);
	}

	private File getLaunchConfigFile()
	{
		IPath launchPath = Platform.getStateLocation(LaunchingPlugin.getDefault().getBundle()).append("launch");
		File launchDir = launchPath.toFile();
		if (!launchDir.exists())
			launchDir.mkdir();
		File featuresFile = new File(launchDir, "launch.xml");
		return featuresFile;
	}

	@Override
	public IPath getWorkingDirectoryPath(ILaunchConfiguration configuration) throws CoreException
	{
		String path = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR_WORKING_DIR, (String) null);
		if (path != null)
		{
			path = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(path);
			return new Path(path);
		}
		return null;
	}

	private IProcessorInvoker getProcessorInvokerDescriptor(ILaunchConfiguration configuration) throws CoreException
	{
		String invokerId = configuration.getAttribute(XSLLaunchConfigurationConstants.INVOKER_DESCRIPTOR, (String) null);
		if (invokerId == null)
			invokerId = "org.eclipse.wst.xsl.launching.jaxp.invoke";
		return XSLTRuntime.getProcessorInvoker(invokerId);
	}

	public static IProcessorInstall getProcessorInstall(ILaunchConfiguration configuration, String mode) throws CoreException
	{
		IProcessorInstall install;
		boolean useDefaultProcessor = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_PROCESSOR, true);
		if (useDefaultProcessor)
		{
			install = XSLTRuntime.getDefaultProcessor();
		}
		else
		{
			String processorId = configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_PROCESSOR, "");
			install = XSLTRuntime.getProcessor(processorId);
		}
		if (mode.equals(ILaunchManager.DEBUG_MODE) && install.getDebugger() == null)
		{
			String debuggingInstallId = LaunchingPlugin.getDefault().getPluginPreferences().getString(XSLLaunchConfigurationConstants.ATTR_DEFAULT_DEBUGGING_INSTALL_ID);
			install = XSLTRuntime.getProcessor(debuggingInstallId);
		}
		return install;
	}

	@Override
	public String getMainTypeName(ILaunchConfiguration configuration) throws CoreException
	{
		if (ILaunchManager.DEBUG_MODE.equals(mode))
			return "org.eclipse.wst.xsl.debugger.DebugRunner";
		return "org.eclipse.wst.xsl.invoker.Main";
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException
	{
		// classname, sourceurl, output file
		IProcessorInvoker invoker = getProcessorInvokerDescriptor(configuration);
		String clazz = invoker.getInvokerClassName();

		StringBuffer sb = new StringBuffer();
		sb.append(clazz);
		sb.append(" ");
		sb.append("\"" + getLaunchConfigFile().getAbsolutePath() + "\"");
		sb.append(" ");
		sb.append("\"" + launchHelper.getSource() + "\"");
		sb.append(" ");
		sb.append("\"" + launchHelper.getTarget().getAbsolutePath() + "\"");
		if (ILaunchManager.DEBUG_MODE.equals(mode))
		{
			IProcessorInstall install = getProcessorInstall(configuration, mode);
			if (install.getDebugger() != null)
			{
				IDebugger debugger = install.getDebugger();
				String className = debugger.getClassName();
				sb.append(" -debug ").append(className).append(" ");
				sb.append(launchHelper.getRequestPort()).append(" ").append(launchHelper.getEventPort());
			}
		}

		return sb.toString();
	}

	@Override
	public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException
	{
		// get the classpath defined by the user
		String[] userClasspath = super.getClasspath(configuration);

		// get the classpath required for the transformation
		IProcessorInvoker invoker = getProcessorInvokerDescriptor(configuration);
		List<String> invokerCP = new ArrayList<String>();
		for (String entry : invoker.getClasspathEntries())
		{
			invokerCP.add(entry);
		}

		// add the debugger...
		IProcessorInstall install = getProcessorInstall(configuration, mode);
		if (ILaunchManager.DEBUG_MODE.equals(mode) && install.getDebugger() != null)
		{
			String[] jars = install.getDebugger().getClassPath();
			for (String jar : jars)
			{
				invokerCP.add(jar);
			}
		}

		String[] invokerClasspath = (String[]) invokerCP.toArray(new String[0]);

		// add them together
		String[] classpath = new String[userClasspath.length + invokerClasspath.length];
		System.arraycopy(invokerClasspath, 0, classpath, 0, invokerClasspath.length);
		System.arraycopy(userClasspath, 0, classpath, invokerClasspath.length, userClasspath.length);

		return classpath;
	}

	@Override
	public String getVMArguments(ILaunchConfiguration configuration) throws CoreException
	{
		String vmargs = super.getVMArguments(configuration);

		IProcessorInstall install = getProcessorInstall(configuration, mode);
		if (install != null && !install.getProcessorType().isJREDefault())
		{
			// clear the endorsed dir
			File tempDir = getEndorsedDir();
			if (tempDir.exists())
			{
				File[] children = tempDir.listFiles();
				for (File child : children)
				{
					child.delete();
				}
				tempDir.delete();
			}
			tempDir.mkdir();

			// move the required jars to the endorsed dir
			IProcessorJar[] jars = install.getProcessorJars();
			for (int i = 0; i < jars.length; i++)
			{
				URL entry = jars[i].asURL();
				if (entry == null)
					throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, "Could not locate jar file " + jars[i], null));
				File file = new File(tempDir, "END_" + i + ".jar");
				moveFile(entry, file);
			}
			// add the endorsed dir
			vmargs += " -Djava.endorsed.dirs=\"" + tempDir.getAbsolutePath() + "\"";
			String tfactory = install.getProcessorType().getTransformerFactoryName();
			if (tfactory != null)
				vmargs += " -Djavax.xml.transform.TransformerFactory=" + install.getProcessorType().getTransformerFactoryName();
		}
		return vmargs;
	}

	private File getEndorsedDir()
	{
		IPath tempLocation = Platform.getStateLocation(LaunchingPlugin.getDefault().getBundle()).append("endorsed");
		return tempLocation.toFile();
	}

	private static void moveFile(URL src, File target) throws CoreException
	{
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try
		{
			bos = new BufferedOutputStream(new FileOutputStream(target));
			bis = new BufferedInputStream(src.openStream());
			while (bis.available() > 0)
			{
				int size = bis.available();
				if (size > 1024)
					size = 1024;
				byte[] b = new byte[size];
				bis.read(b, 0, b.length);
				bos.write(b);
			}
		}
		catch (IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, "Error copying url " + src + " to " + target, e));
		}
		finally
		{
			if (bis != null)
			{
				try
				{
					bis.close();
				}
				catch (IOException e)
				{
					LaunchingPlugin.log(e);
				}
			}
			if (bos != null)
			{
				try
				{
					bos.close();
				}
				catch (IOException e)
				{
					LaunchingPlugin.log(e);
				}
			}
		}
	}
}
