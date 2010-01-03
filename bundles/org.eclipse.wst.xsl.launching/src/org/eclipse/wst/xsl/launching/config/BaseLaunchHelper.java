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
package org.eclipse.wst.xsl.launching.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.wst.xsl.internal.launching.LaunchingPlugin;
import org.eclipse.wst.xsl.launching.XSLLaunchConfigurationConstants;
import org.eclipse.wst.xsl.launching.XSLTRuntime;

/**
 * @since 1.0
 */
public class BaseLaunchHelper {
	private final URL source;
	private final File target;
	protected final LaunchPipeline pipeline;
	private int requestPort = -1;
	private int eventPort = -1;
	private int generatePort = -1;
	private final boolean openFileOnCompletion;
	private final boolean formatFileOnCompletion;
	private final IPath workingDir;

	public BaseLaunchHelper(ILaunchConfiguration configuration)
			throws CoreException {
		workingDir = hydrateWorkingDir(configuration);
		source = hydrateSourceFileURL(configuration);
		target = hydrateOutputFile(configuration);
		pipeline = hydratePipeline(configuration);
		openFileOnCompletion = configuration.getAttribute(
				XSLLaunchConfigurationConstants.ATTR_OPEN_FILE, true);
		formatFileOnCompletion = configuration.getAttribute(
				XSLLaunchConfigurationConstants.ATTR_FORMAT_FILE, false);
	}

	public int getRequestPort() {
		if (requestPort == -1)
			requestPort = findFreePort();
		return requestPort;
	}

	public int getEventPort() {
		if (eventPort == -1)
			eventPort = findFreePort();
		return eventPort;
	}

	public int getGeneratePort() {
		if (generatePort == -1)
			generatePort = findFreePort();
		return generatePort;
	}

	public LaunchPipeline getPipeline() {
		return pipeline;
	}

	public URL getSource() {
		return source;
	}

	public File getTarget() {
		return target;
	}

	public IPath getWorkingDir() {
		return workingDir;
	}

	private static LaunchPipeline hydratePipeline(
			ILaunchConfiguration configuration) throws CoreException {
		LaunchPipeline pipeline = null;
		String s = configuration.getAttribute(
				XSLLaunchConfigurationConstants.ATTR_PIPELINE, (String) null);
		if (s != null && s.length() > 0) {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(s
					.getBytes());
			pipeline = LaunchPipeline.fromXML(inputStream);
		}
		return pipeline;
	}

	public static URL hydrateSourceFileURL(ILaunchConfiguration configuration)
			throws CoreException {
		IPath sourceFile = hydrateSourceFile(configuration);
		return pathToURL(sourceFile);
	}

	private static IPath hydrateWorkingDir(ILaunchConfiguration configuration)
			throws CoreException {
		String expr = configuration
				.getAttribute(XSLLaunchConfigurationConstants.ATTR_WORKING_DIR,
						(String) null);
		return getSubstitutedPath(expr);
	}

	private static IPath hydrateSourceFile(ILaunchConfiguration configuration)
			throws CoreException {
		String sourceFileExpr = configuration.getAttribute(
				XSLLaunchConfigurationConstants.ATTR_INPUT_FILE, (String) null);
		return getSubstitutedPath(sourceFileExpr);
	}

	protected static URL pathToURL(IPath sourceFile) throws CoreException {
		URL url = null;
		try {
			url = sourceFile.toFile().toURI().toURL();
		} catch (MalformedURLException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, sourceFile
							.toString(), e));
		}
		return url;
	}

	public static File hydrateOutputFile(ILaunchConfiguration configuration)
			throws CoreException {
		IPath outputFile = null;
		boolean useDefaultOutputFile = configuration.getAttribute(
				XSLLaunchConfigurationConstants.ATTR_USE_DEFAULT_OUTPUT_FILE,
				true);
		if (!useDefaultOutputFile) {
			String outputFileName = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_OUTPUT_FILENAME,
					(String) null);
			String outputFolderExpr = configuration.getAttribute(
					XSLLaunchConfigurationConstants.ATTR_OUTPUT_FOLDER,
					(String) null);
			outputFile = getSubstitutedPath(outputFolderExpr).append(
					outputFileName);
		} else {
			outputFile = XSLTRuntime
					.defaultOutputFileForInputFile(configuration.getAttribute(
							XSLLaunchConfigurationConstants.ATTR_INPUT_FILE,
							(String) null));
		}
		return outputFile.toFile();
	}

	private static IPath getSubstitutedPath(String path) throws CoreException {
		if (path != null) {
			path = VariablesPlugin.getDefault().getStringVariableManager()
					.performStringSubstitution(path);
			return new Path(path);
		}
		return null;
	}

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free
	 * port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free
	 *         port
	 */
	public static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					LaunchingPlugin.log(e);
				}
			}
		}
		return -1;
	}

	public boolean getOpenFileOnCompletion() {
		return openFileOnCompletion;
	}

	public boolean getFormatFileOnCompletion() {
		return formatFileOnCompletion;
	}

}
