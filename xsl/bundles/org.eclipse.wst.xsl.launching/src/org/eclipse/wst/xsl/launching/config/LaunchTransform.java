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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LaunchTransform
{
	public static final String RESOURCE_TYPE = "resource"; //$NON-NLS-1$
	public static final String EXTERNAL_TYPE = "external"; //$NON-NLS-1$

	private final String stylesheet;
	private final String pathType;
	private String resolver;
	private final Set<LaunchAttribute> parameters = new HashSet<LaunchAttribute>();
	private LaunchPipeline pipeline;

	/**
	 * The path may be an IResource to a file in the workspace, or an IPath to a
	 * file outside of the workspace - depending on the pathType. The path
	 * should be created using IPath.toPortableString(). The path may also
	 * contain variables defined by the VariablePlugin.
	 * 
	 * @param path
	 * @param pathType
	 */
	public LaunchTransform(String path, String pathType)
	{
		stylesheet = path;
		this.pathType = pathType;
	}

	public Set<LaunchAttribute> getParameters()
	{
		return parameters;
	}
	
	public void addParameter(LaunchAttribute parameter)
	{
		parameters.add(parameter);
	}

	public String getResolver()
	{
		return resolver;
	}

	public void setResolver(String resolver)
	{
		this.resolver = resolver;
	}

	public IPath getPath() throws CoreException
	{
		String substitutedValue = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(stylesheet);
		return Path.fromPortableString(substitutedValue);
	}

	public String getPathType()
	{
		return pathType;
	}

	/**
	 * The full path to the file in the local file system (with any string
	 * subsitutions already made).
	 * 
	 * @return
	 * @throws CoreException
	 */
	public IPath getLocation() throws CoreException
	{
		IPath partialPath = getPath();
		IPath fullPath = null;
		if (RESOURCE_TYPE.equals(pathType))
		{
			IFile file = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(partialPath);
			fullPath = file.getLocation();
		}
		else if (EXTERNAL_TYPE.equals(pathType))
		{
			fullPath = partialPath;
		}
		return fullPath;
	}

	public Element asXML(Document doc)
	{
		Element tdefEl = doc.createElement("Transform"); //$NON-NLS-1$
		tdefEl.setAttribute("path", stylesheet); //$NON-NLS-1$
		tdefEl.setAttribute("pathType", pathType); //$NON-NLS-1$
		if (resolver != null)
			tdefEl.setAttribute("uriResolver", resolver); //$NON-NLS-1$
		Element paramsEl = doc.createElement("Parameters"); //$NON-NLS-1$
		tdefEl.appendChild(paramsEl);
		for (Iterator<LaunchAttribute> iter = parameters.iterator(); iter.hasNext();)
		{
			LaunchAttribute param = iter.next();
			Element propEl = doc.createElement("Parameter"); //$NON-NLS-1$
			propEl.setAttribute("name", param.uri); //$NON-NLS-1$
			propEl.setAttribute("value", param.value); //$NON-NLS-1$
			paramsEl.appendChild(propEl);
		}
		return tdefEl;
	}

	public static LaunchTransform fromXML(Element transformEl)
	{
		String path = transformEl.getAttribute("path"); //$NON-NLS-1$
		String pathType = transformEl.getAttribute("pathType"); //$NON-NLS-1$

		LaunchTransform tdef = new LaunchTransform(path, pathType);

		String uriResolver = transformEl.getAttribute("uriResolver"); //$NON-NLS-1$
		tdef.setResolver(uriResolver);

		Element paramsEl = (Element) transformEl.getElementsByTagName("Parameters").item(0); //$NON-NLS-1$
		NodeList paramEls = paramsEl.getElementsByTagName("Parameter"); //$NON-NLS-1$
		for (int i = 0; i < paramEls.getLength(); i++)
		{
			Element paramEl = (Element) paramEls.item(i);
			String name = paramEl.getAttribute("name"); //$NON-NLS-1$
			String type = paramEl.getAttribute("type"); //$NON-NLS-1$
			String value = paramEl.getAttribute("value"); //$NON-NLS-1$
			tdef.addParameter(new LaunchAttribute(name, type, value));
		}

		return tdef;
	}

	public void setPipeline(LaunchPipeline pipeline)
	{
		this.pipeline = pipeline;
	}

	public LaunchPipeline getPipeline()
	{
		return pipeline;
	}

	/*
	 * private File getURIResolverDirectory(ILaunchConfiguration configuration)
	 * throws CoreException { File resolverDir = null; String baseURIType =
	 * configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_BASE_URI_TYPE,
	 * XSLLaunchConfigurationConstants.BASE_URI_WORKING_DIR_RELATIVE); if
	 * (XSLLaunchConfigurationConstants.BASE_URI_ABSOLUTE.equals(baseURIType)) {
	 * String uriDir =
	 * configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_BASE_URI_DIRECTORY,
	 * (String)null); if (uriDir!=null) { String pathExpr =
	 * VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(uriDir);
	 * Path path = new Path(pathExpr); if (path.isAbsolute()) { File dir = new
	 * File(path.toOSString()); if (dir.isDirectory() && dir.exists()) { return
	 * dir; } // This may be a workspace relative path returned by a variable. //
	 * However variable paths start with a slash and thus are thought to // be
	 * absolute, so fall through to below } IResource res =
	 * ResourcesPlugin.getWorkspace().getRoot().findMember(path); if (res
	 * instanceof IContainer && res.exists()) return res.getLocation().toFile();
	 * abort("URI path is invalid",null,0); } } else if
	 * (XSLLaunchConfigurationConstants.BASE_URI_STYLESHEET_RELATIVE.equals(baseURIType)) {
	 * resolverDir = getStylesheetRelativeURIResolver(configuration); } // no
	 * need to handle the case of working_dir relative - this is the default for
	 * JAXP so return null return resolverDir; }
	 * 
	 * private File getStylesheetRelativeURIResolver(ILaunchConfiguration
	 * configuration) throws CoreException { List stylesheets =
	 * configuration.getAttribute(XSLLaunchConfigurationConstants.ATTR_STYLESHEETS_LIST,(List)null);
	 * if (stylesheets == null || stylesheets.size() == 0) abort("No stylesheets
	 * defined for this launch configuration", null, 0); else { String sheet =
	 * (String)stylesheets.get(0); String pathExpr =
	 * VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(sheet);
	 * Path path = new Path(pathExpr); if (path.isAbsolute()) { File file = new
	 * File(path.toOSString()); if (file.exists()) { File dir =
	 * file.getParentFile(); return dir; } // This may be a workspace relative
	 * path returned by a variable. // However variable paths start with a slash
	 * and thus are thought to // be absolute IResource res =
	 * ResourcesPlugin.getWorkspace().getRoot().findMember(path); IContainer
	 * parent = res.getParent(); if (parent.exists()) return
	 * parent.getLocation().toFile(); abort("Stylesheet path is
	 * invalid",null,0); } else { IResource res =
	 * ResourcesPlugin.getWorkspace().getRoot().findMember(path); IContainer
	 * parent = res.getParent(); if (parent.exists()) return
	 * parent.getLocation().toFile(); abort("Stylesheet path is
	 * invalid",null,0); } } return null; }
	 */
}
