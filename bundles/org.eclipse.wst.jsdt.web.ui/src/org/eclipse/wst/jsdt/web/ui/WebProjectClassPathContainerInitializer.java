/**
 * 
 */
package org.eclipse.wst.jsdt.web.ui;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.jsdt.core.ClasspathContainerInitializer;
import org.eclipse.wst.jsdt.core.IClasspathContainer;
import org.eclipse.wst.jsdt.core.IClasspathEntry;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.compiler.libraries.LibraryLocation;
import org.eclipse.wst.jsdt.internal.ui.IClasspathContainerInitialzerExtension;
import org.eclipse.wst.jsdt.web.core.internal.java.JsNameManglerUtil;
import org.eclipse.wst.jsdt.web.core.internal.java.WebRootFinder;
import org.eclipse.wst.jsdt.web.core.internal.project.JsWebNature;

/**
 * @author childsb
 * 
 */
public class WebProjectClassPathContainerInitializer extends ClasspathContainerInitializer implements IClasspathContainerInitialzerExtension {
	private static final String CONTAINER_DESCRIPTION = Messages.getString("WebProjectClassPathContainerInitializer.0"); //$NON-NLS-1$
	
	public static final char[] LIB_NAME = {'b','r','o','w','s','e','r','W','i','n','d','o','w','.','j','s'};
	/* Some tokens for us to identify mangled paths */
	private static final String MANGLED_BUTT1 = "htm"; //$NON-NLS-1$
	private static final String MANGLED_BUTT2 = ".js"; //$NON-NLS-1$
	
	//private IJavaProject javaProject;
	
	
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
		return null;
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
	
	public String getDescription(IPath containerPath, IJavaProject javaProject) {
		if (containerPath.equals(new Path(JsWebNature.VIRTUAL_CONTAINER))) {
			return WebProjectClassPathContainerInitializer.CONTAINER_DESCRIPTION;
		}
		
		String containerPathString = containerPath.toString();
		IPath webContext = getWebContextRoot(javaProject);
		String fileExtension = containerPath.getFileExtension();
		if(containerPath.equals(getWebContextRoot(javaProject)) || (fileExtension!=null && fileExtension.equals("js"))) { //$NON-NLS-1$
			return webContext.toString();
		}
		String unmangled = WebProjectClassPathContainerInitializer.getUnmangedHtmlPath(containerPathString);
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
		return IClasspathContainer.K_SYSTEM;
	}
	
	
	public IPath getPath() {
		return new Path(JsWebNature.VIRTUAL_CONTAINER);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.jsdt.internal.ui.IClasspathContainerInitialzerExtension#getImage(org.eclipse.core.runtime.IPath, java.lang.String, org.eclipse.wst.jsdt.core.IJavaProject)
	 */
	public ImageDescriptor getImage(IPath containerPath, String element, IJavaProject project) {
		return ImageDescriptor.createFromFile(this.getClass(),"web1.JPG"); //$NON-NLS-1$
	}
	public IClasspathEntry[] getClasspathEntries() {
		
		//IClasspathEntry entry=null;
		
	return new IClasspathEntry[0];
//		try {
//		
//			
//			
//			IPath contextPath = getWebContextRoot(javaProject);
//			//entry =JavaCore.newLibraryEntry(contextPath.makeAbsolute(), null,null, new IAccessRule[0], new IClasspathAttribute[0], true);
//			//entry =JavaCore.newLibraryEntry(contextPath.makeAbsolute(), null, null, new IAccessRule[0], new IClasspathAttribute[0], true);
//			//entry =JavaCore.newSourceEntry(contextPath.makeAbsolute());
//			entry = new ClasspathEntry(
//					IPackageFragmentRoot.K_SOURCE,
//					IClasspathEntry.CPE_SOURCE,
//					contextPath.makeAbsolute(),
//					ClasspathEntry.INCLUDE_ALL, ClasspathEntry.EXCLUDE_NONE,
//					null, // source attachment
//					null, // source attachment root
//					null, // custom output location
//					false,
//					null,
//					false, // no access rules to combine
//					new IClasspathAttribute[] {ClasspathEntry.EXCLUDE_VALIDATE}); 
//			
//		} catch (RuntimeException ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//		}
//		
//		if(entry!=null) return new IClasspathEntry[] {entry};
//		return new IClasspathEntry[0];
	}
	public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
		//this.javaProject = project;
		super.initialize(containerPath, project);
		
	}
	
	public static IPath getWebContextRoot(IJavaProject javaProject) {
		String webRoot = WebRootFinder.getWebContentFolder(javaProject.getProject()).toString();	
		IPath webRootPath = javaProject.getPath().append(webRoot);
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
//						//IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry( path,null, null, new IAccessRule[ 0 ], new IClasspathAttribute[ 0 ], true );
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
