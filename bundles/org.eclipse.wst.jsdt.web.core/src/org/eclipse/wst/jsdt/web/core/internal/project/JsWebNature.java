package org.eclipse.wst.jsdt.web.core.internal.project;

import java.util.Arrays;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.jsdt.core.IClasspathEntry;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;

public class JsWebNature implements IProjectNature {
	
	private static final String FILENAME_CLASSPATH = ".classpath"; //$NON-NLS-1$
	
	public static void addJsNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (!project.hasNature(JavaCore.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = JavaCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		} else {
			if (monitor != null) {
				monitor.worked(1);
			}
		}
	}
	
	public static boolean hasJsNature(IProject project) {
		boolean valid = false;
		try {
			valid = project.hasNature(JavaCore.NATURE_ID);
		} catch (Exception e) {
		}
		
		return valid;
	}
	
	public static void removeJsNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (project.hasNature(JavaCore.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length - 1];
			
			int k = 0;
			for (int i = 0; i < prevNatures.length; i++) {
				if (prevNatures[i] != JavaCore.NATURE_ID) {
					newNatures[k++] = prevNatures[i];
				}
			}
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		} else {
			if (monitor != null) {
				monitor.worked(1);
			}
		}
	}
	
	private Vector		   classPathEntries = new Vector();
	private boolean		  DEBUG			= false;
	private IProject		 fCurrProject;
	
	private JavaProject	  fJavaProject;
	
	private IPath			fOutputLocation;
	
	private IProgressMonitor monitor;
	
	public JsWebNature() {
		monitor = new NullProgressMonitor();
	}
	
	public JsWebNature(IProject project, IProgressMonitor monitor) {
		fCurrProject = project;
		if (monitor != null) {
			this.monitor = monitor;
		} else {
			monitor = new NullProgressMonitor();
		}
	}
	
	public void configure() throws CoreException {
		if (hasProjectClassPathFile()) {
			IClasspathEntry[] entries = getRawClassPath();
			if (entries != null && entries.length > 0) {
				classPathEntries.addAll(Arrays.asList(entries));
			}
		}
		initOutputPath();
		createSourceClassPath();
		initJREEntry();
		JsWebNature.addJsNature(fCurrProject, monitor);
		fJavaProject = (JavaProject) JavaCore.create(fCurrProject);
		fJavaProject.setProject(fCurrProject);
		try {
			fJavaProject.setRawClasspath((IClasspathEntry[]) classPathEntries.toArray(new IClasspathEntry[] {}), fOutputLocation, monitor);
		} catch (Exception e) {
			System.out.println(e);
		}
		fCurrProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}
	
	private void createSourceClassPath() {
		if (hasAValidSourcePath()) {
			return;
		}
		IPath projectPath = fCurrProject.getFullPath();
		classPathEntries.add(JavaCore.newSourceEntry(projectPath));
	}
	
	public void deconfigure() throws CoreException {
		IClasspathEntry[] defaultJRELibrary = PreferenceConstants.getDefaultJRELibrary();
		IClasspathEntry[] entries = getRawClassPath();
		Vector goodEntries = new Vector();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i] != defaultJRELibrary[0]) {
				goodEntries.add(entries[i]);
			}
		}
		IPath outputLocation = getJavaProject().getOutputLocation();
		getJavaProject().setRawClasspath((IClasspathEntry[]) goodEntries.toArray(new IClasspathEntry[] {}), outputLocation, monitor);
		getJavaProject().deconfigure();
		JsWebNature.removeJsNature(fCurrProject, monitor);
		fCurrProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}
	
	private IPath getCurrentOutputPath() {
		IPath outputLocation = null;
		if (hasProjectClassPathFile()) {
			try {
				outputLocation = getJavaProject().getOutputLocation();
			} catch (Exception e) {
				if (DEBUG) {
					System.out.println("Error checking sourcepath:" + e);
				}
			}
		}
		return outputLocation;
	}
	
	public JavaProject getJavaProject() {
		
		if (fJavaProject == null) {
			fJavaProject = (JavaProject) JavaCore.create(fCurrProject);
			fJavaProject.setProject(fCurrProject);
		}
		return fJavaProject;
	}
	
	public IProject getProject() {
		return this.fCurrProject;
	}
	
	private IClasspathEntry[] getRawClassPath() {
		JavaProject proj = new JavaProject();
		proj.setProject(fCurrProject);
		return proj.readRawClasspath();
	}
	
	private boolean hasAValidSourcePath() {
		if (hasProjectClassPathFile()) {
			try {
				
				IClasspathEntry[] entries = getRawClassPath();
				for (int i = 0; i < entries.length; i++) {
					if (entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						return true;
					}
				}
			} catch (Exception e) {
				if (DEBUG) {
					System.out.println("Error checking sourcepath:" + e);
				}
			}
		}
		return false;
	}
	
	private boolean hasProjectClassPathFile() {
		if (fCurrProject == null) {
			return false;
		}
		return fCurrProject.getFile(JsWebNature.FILENAME_CLASSPATH).exists();
	}
	
	private void initJREEntry() {
		IClasspathEntry[] defaultJRELibrary = PreferenceConstants.getDefaultJRELibrary();
		try {
			IClasspathEntry[] entries = getRawClassPath();
			for (int i = 0; i < entries.length; i++) {
				if (entries[i] == defaultJRELibrary[0]) {
					return;
				}
			}
			classPathEntries.add(defaultJRELibrary[0]);
		} catch (Exception e) {
			if (DEBUG) {
				System.out.println("Error checking sourcepath:" + e);
			}
		}
	}
	
	private void initOutputPath() {
		if (fOutputLocation == null) {
			fOutputLocation = getCurrentOutputPath();
		}
		if (fOutputLocation == null) {
			fOutputLocation = fCurrProject.getFullPath();
		}
		
	}
	
	public void setProject(IProject project) {
		this.fCurrProject = project;
	}
	
}
