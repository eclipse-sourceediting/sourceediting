/**
 * 
 */
package org.eclipse.wst.jsdt.web.core.internal.project;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.jsdt.core.ClasspathContainerInitializer;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.web.core.internal.java.JsNameManglerUtil;

/**
 * @author childsb
 * 
 */
public class WebProjectClassPathContainerInitializer extends ClasspathContainerInitializer {
/* Some tokens for us to identify mangled paths */
	private static final String MANGLED_BUTT1 = "htm";
	private static final String MANGLED_BUTT2 = ".js";
	
	private static String getUnmangedHtmlPath(String containerPathString) {
		if (containerPathString == null) {
			return null;
		}
		if (containerPathString.toLowerCase().indexOf(WebProjectClassPathContainerInitializer.MANGLED_BUTT1) != -1 && containerPathString.toLowerCase().indexOf(WebProjectClassPathContainerInitializer.MANGLED_BUTT2) != -1) {
			return JsNameManglerUtil.unmangle(containerPathString);
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.wst.jsdt.core.IJavaProject)
	 */
	@Override
	public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
		/* dont remove from this project */
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.wst.jsdt.core.IJavaProject)
	 */
	@Override
	public String getDescription(IPath containerPath, IJavaProject project) {
		if (containerPath.equals(JsWebNature.VIRTUAL_CONTAINER_PATH)) {
			return new String("Web Project support for JSDT");
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
	@Override
	public URI getHostPath(IPath path, IJavaProject project) {
		// TODO Auto-generated method stub
		String htmlPath = WebProjectClassPathContainerInitializer.getUnmangedHtmlPath(path.toString());
		if (htmlPath != null) {
			try {
				return new URI(htmlPath);
			} catch (URISyntaxException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.jsdt.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath,
	 *      org.eclipse.wst.jsdt.core.IJavaProject)
	 */
	@Override
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
	/* need this to activate the jsdt.web plugin */
	}
}
