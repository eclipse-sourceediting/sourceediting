/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.project;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJsGlobalScopeContainer;
import org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.eclipse.wst.jsdt.web.core.javascript.JsNameManglerUtil;
import org.eclipse.wst.jsdt.web.core.javascript.WebRootFinder;

/**
*
* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class WebProjectJsGlobalScopeContainerInitializer extends JsGlobalScopeContainerInitializer  {
	private static final String CONTAINER_DESCRIPTION = Messages.getString("WebProjectJsGlobalScopeContainerInitializer.0"); //$NON-NLS-1$
	
	public static final char[] LIB_NAME = {'b','r','o','w','s','e','r','W','i','n','d','o','w','.','j','s'};
	/* Some tokens for us to identify mangled paths */
	private static final String MANGLED_BUTT1 = "htm"; //$NON-NLS-1$
	private static final String MANGLED_BUTT2 = ".js"; //$NON-NLS-1$
	
	//private IJavaScriptProject javaProject;
	
	
	private static String getUnmangedHtmlPath(String containerPathString) {
		if (containerPathString == null) {
			return null;
		}
		if (containerPathString.toLowerCase().indexOf(WebProjectJsGlobalScopeContainerInitializer.MANGLED_BUTT1) != -1 && containerPathString.toLowerCase().indexOf(WebProjectJsGlobalScopeContainerInitializer.MANGLED_BUTT2) != -1) {
			return JsNameManglerUtil.unmangle(containerPathString);
		}
		return null;
	}
	public LibraryLocation getLibraryLocation() {
		return null;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer#canUpdateJsGlobalScopeContainer(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.wst.jsdt.core.IJavaScriptProject)
	 */
	
	public boolean canUpdateJsGlobalScopeContainer(IPath containerPath, IJavaScriptProject project) {
		/* dont remove from this project */
		return false;
	}
	
	
	protected IJsGlobalScopeContainer getContainer(IPath containerPath, IJavaScriptProject project) {
		return this;
	}
	
	
	public String getDescription() {
		return WebProjectJsGlobalScopeContainerInitializer.CONTAINER_DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer#getDescription(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.wst.jsdt.core.IJavaScriptProject)
	 */
	
	public String getDescription(IPath containerPath, IJavaScriptProject javaProject) {
		if (containerPath.equals(new Path(JsWebNature.VIRTUAL_CONTAINER))) {
			return WebProjectJsGlobalScopeContainerInitializer.CONTAINER_DESCRIPTION;
		}
		
		String containerPathString = containerPath.toString();
		IPath webContext = getWebContextRoot(javaProject);
		String fileExtension = containerPath.getFileExtension();
		if(containerPath.equals(getWebContextRoot(javaProject)) || (fileExtension!=null && fileExtension.equals("js"))) { //$NON-NLS-1$
			return webContext.toString();
		}
		String unmangled = WebProjectJsGlobalScopeContainerInitializer.getUnmangedHtmlPath(containerPathString);
		if (unmangled != null) {
			IPath projectPath = javaProject.getPath();
			/* Replace the project path with the project name */
			if (unmangled.indexOf(projectPath.toString()) >= 0) {
				unmangled = javaProject.getDisplayName() + ":" + unmangled.substring(projectPath.toString().length()); //$NON-NLS-1$
			}
			return unmangled;
		}
		return containerPathString;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.JsGlobalScopeContainerInitializer#getHostPath(org.eclipse.core.runtime.IPath)
	 */
	
	public URI getHostPath(IPath path, IJavaScriptProject project) {
		// TODO Auto-generated method stub
		String htmlPath = WebProjectJsGlobalScopeContainerInitializer.getUnmangedHtmlPath(path.toString());
		if (htmlPath != null) {
			URI fileUri =  new Path(htmlPath).toFile().toURI();
			return fileUri;
			//			try {
//				return new URI(htmlPath);
//			} catch (URISyntaxException ex) {
//				ex.printStackTrace();
//			}
		}
//		else {
//			try {
//				return new URI(path.toString());
//			} catch (URISyntaxException ex) {
//				// TODO Auto-generated catch block
//				ex.printStackTrace();
//			}
//		}
		return null;
	}
	
	
	public int getKind() {
		return IJsGlobalScopeContainer.K_SYSTEM;
	}
	
	
	public IPath getPath() {
		return new Path(JsWebNature.VIRTUAL_CONTAINER);
	}

	/**
	 * @deprecated Use {@link #getIncludepathEntries()} instead
	 */
	public IIncludePathEntry[] getClasspathEntries() {
		return getIncludepathEntries();
	}
	public IIncludePathEntry[] getIncludepathEntries() {
		
		//IIncludePathEntry entry=null;
		
	return new IIncludePathEntry[0];
//		try {
//		
//			
//			
//			IPath contextPath = getWebContextRoot(javaProject);
//			//entry =JavaScriptCore.newLibraryEntry(contextPath.makeAbsolute(), null,null, new IAccessRule[0], new IIncludePathAttribute[0], true);
//			//entry =JavaScriptCore.newLibraryEntry(contextPath.makeAbsolute(), null, null, new IAccessRule[0], new IIncludePathAttribute[0], true);
//			//entry =JavaScriptCore.newSourceEntry(contextPath.makeAbsolute());
//			entry = new ClasspathEntry(
//					IPackageFragmentRoot.K_SOURCE,
//					IIncludePathEntry.CPE_SOURCE,
//					contextPath.makeAbsolute(),
//					ClasspathEntry.INCLUDE_ALL, ClasspathEntry.EXCLUDE_NONE,
//					null, // source attachment
//					null, // source attachment root
//					null, // custom output location
//					false,
//					null,
//					false, // no access rules to combine
//					new IIncludePathAttribute[] {ClasspathEntry.EXCLUDE_VALIDATE}); 
//			
//		} catch (RuntimeException ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//		}
//		
//		if(entry!=null) return new IIncludePathEntry[] {entry};
//		return new IIncludePathEntry[0];
	}
	public void initialize(IPath containerPath, IJavaScriptProject project) throws CoreException {
		//this.javaProject = project;
		super.initialize(containerPath, project);
		
	}
	
	public static IPath getWebContextRoot(IJavaScriptProject javaProject) {
		IPath projectRelativeWebRoot = WebRootFinder.getWebContentFolder(javaProject.getProject());	
		IPath webRootPath = javaProject.getPath().append(projectRelativeWebRoot);
		return webRootPath;
	}
	
//	public IPath[] getAllHtmlInProject() {
//		final ArrayList found = new ArrayList();
//		String webRoot = getWebContextRoot(javaProject).toString();	
//			IResourceProxyVisitor visitor = new IResourceProxyVisitor()
//			{
//				public boolean visit( IResourceProxy proxy ) throws CoreException
//				{
//					if ( proxy.getName().endsWith( ".htm" ) )
//					{
//						IPath path = proxy.requestResource().getLocation();
//						found.add(path);
//						//IIncludePathEntry newLibraryEntry = JavaScriptCore.newLibraryEntry( path,null, null, new IAccessRule[ 0 ], new IIncludePathAttribute[ 0 ], true );
//						//entries.add( newLibraryEntry );
//						return false;
//					}
//					
//					return true;
//				}
//			};
//			try
//			{
//				javaProject.getProject().findMember( new Path(webRoot) ).accept( visitor, 0 );
//			}
//			catch ( CoreException e )
//			{
//			}
//		
//		
//		return (IPath[])found.toArray(new IPath[found.size()]);
//	
//	}
	
}
