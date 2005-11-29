package org.eclipse.jst.jsp.ui.tests.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.jsp.ui.tests.JSPUITestsPlugin;

/**
 * Some utilities for creating projects, and copying files into the workspace.
 */
public class ProjectUtil {

	public static final String JAVA_NATURE_ID = "org.eclipse.jdt.core.javanature";
	
	/**
	 * Add a library entry (like a jar) to the classpath of a project.
	 * The jar must be in your poject. You can copy the jar into your workspace using
	 * copyBundleEntryIntoWorkspace(String entryname, String fullPath)
	 * 
	 * @param proj assumed it has java nature
	 * @param pathToJar project relative, no leading slash
	 */
	public static void addLibraryEntry(IProject proj, String pathToJar) {
		
		IPath projLocation = proj.getLocation();
		IPath absJarPath = projLocation.append(pathToJar);
		
		IJavaProject jProj = JavaCore.create(proj);
		
		IClasspathEntry strutsJarEntry = JavaCore.newLibraryEntry(absJarPath, null, null);
		try {
			IClasspathEntry[] currentEntries = jProj.getRawClasspath();
			
			List l = new ArrayList();
			l.addAll(Arrays.asList(currentEntries));
			l.add(strutsJarEntry);
			
			IClasspathEntry[] newEntries = (IClasspathEntry[])l.toArray(new IClasspathEntry[l.size()]);
			jProj.setRawClasspath(newEntries, new NullProgressMonitor());
		}
		catch (JavaModelException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * 
	 * @param entryname path relative to TEST plugin starting w/ a "/"
	 * 						(eg. "/testfiles/bugnumber/struts-logic.tld")
	 * @param fullPath path relative to junit test workpace
	 * 						(eg. "/myruntimeproj/struts-logic.tld")
	 * @return
	 */
	public static IFile copyBundleEntryIntoWorkspace(String entryname, String fullPath) {
		IFile file = null;
		URL entry = JSPUITestsPlugin.getDefault().getBundle().getEntry(entryname);
		if (entry != null) {
			try {
				byte[] b = new byte[2048];
				InputStream input = entry.openStream();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				int i = -1;
				while ((i = input.read(b)) > -1) {
					output.write(b, 0, i);
				}
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fullPath));
				if (file != null) {
					file.create(new ByteArrayInputStream(output.toByteArray()), true, new NullProgressMonitor());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	public static  IProject createProject(String name, IPath location, String[] natureIds) {
		IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(name);
		if (location != null) {
			description.setLocation(location);
		}
		if (natureIds != null) {
			description.setNatureIds(natureIds);
		}
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		try {
			project.create(description, new NullProgressMonitor());
			project.open(new NullProgressMonitor());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return project;
	}
}
