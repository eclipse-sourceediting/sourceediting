/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.project;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.jsdt.core.IClasspathContainer;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.compiler.libraries.BasicBrowserLibraryClassPathContainerInitializer;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.eclipse.wst.jsdt.core.compiler.libraries.SystemLibraryLocation;

import org.eclipse.wst.jsdt.web.core.internal.JsCorePlugin;
import org.eclipse.wst.jsdt.web.core.internal.java.JsNameManglerUtil;

/**
 * @author childsb
 * 
 */
public class WebProjectClassPathContainerInitializer extends BasicBrowserLibraryClassPathContainerInitializer {
	private static final String CONTAINER_DESCRIPTION = "Web Project support for JSDT";
	
	public static final char[] LIB_NAME = {'b','r','o','w','s','e','r','W','i','n','d','o','w','.','j','s'};
	/* Some tokens for us to identify mangled paths */
	private static final String MANGLED_BUTT1 = "htm";
	private static final String MANGLED_BUTT2 = ".js";
	public static final String VIRTUAL_CONTAINER = "org.eclipse.wst.jsdt.launching.WebProject";
	
	private static String getUnmangedHtmlPath(String containerPathString) {
		if (containerPathString == null) {
			return null;
		}
		if (containerPathString.toLowerCase().indexOf(WebProjectClassPathContainerInitializer.MANGLED_BUTT1) != -1 && containerPathString.toLowerCase().indexOf(WebProjectClassPathContainerInitializer.MANGLED_BUTT2) != -1) {
			return JsNameManglerUtil.unmangle(containerPathString);
		}
		return null;
	}
	public LibraryLocation getLibraryLocation() {
		return new WebBrowserLibLocation();
	}
	
	class WebBrowserLibLocation extends SystemLibraryLocation {
		WebBrowserLibLocation() {
			super();
		}
		
		
		public char[][] getLibraryFileNames() {
			//return new char[][] { WebProjectClassPathContainerInitializer.LIB_NAME };
			return new char[0][];
		}
		
		
		protected String getPluginId() {
			return JsCorePlugin.PLUGIN_ID;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.wst.jsdt.core.IJavaProject)
	 */
	
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		/* dont remove from this project */
		return false;
	}
	
	
	protected IClasspathContainer getContainer(IPath containerPath, IJavaProject project) {
		return this;
	}
	
	
	public String getDescription() {
		return WebProjectClassPathContainerInitializer.CONTAINER_DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.wst.jsdt.core.IJavaProject)
	 */
	
	public String getDescription(IPath containerPath, IJavaProject project) {
		if (containerPath.equals(new Path(WebProjectClassPathContainerInitializer.VIRTUAL_CONTAINER))) {
			return WebProjectClassPathContainerInitializer.CONTAINER_DESCRIPTION;
		}
		String containerPathString = containerPath.toString();
		String unmangled = WebProjectClassPathContainerInitializer.getUnmangedHtmlPath(containerPathString);
		if (unmangled != null) {
			IPath projectPath = project.getPath();
			/* Replace the project path with the project name */
			if (unmangled.indexOf(projectPath.toString()) >= 0) {
				unmangled = project.getDisplayName() + ":" + unmangled.substring(projectPath.toString().length());
			}
			return unmangled;
		}
		return containerPathString;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.ClasspathContainerInitializer#getHostPath(org.eclipse.core.runtime.IPath)
	 */
	
	public URI getHostPath(IPath path, IJavaProject project) {
		// TODO Auto-generated method stub
		String htmlPath = WebProjectClassPathContainerInitializer.getUnmangedHtmlPath(path.toString());
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
		return IClasspathContainer.K_APPLICATION;
	}
	
	
	public IPath getPath() {
		return new Path(WebProjectClassPathContainerInitializer.VIRTUAL_CONTAINER);
	}
}
