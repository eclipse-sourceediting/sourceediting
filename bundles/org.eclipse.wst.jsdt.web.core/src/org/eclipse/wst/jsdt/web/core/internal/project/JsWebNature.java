package org.eclipse.wst.jsdt.web.core.internal.project;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.jsdt.core.IClasspathEntry;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.internal.core.util.Util;
import org.eclipse.wst.jsdt.internal.ui.util.CoreUtility;
import org.eclipse.wst.jsdt.internal.ui.wizards.ClassPathDetector;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;

import sun.misc.FpUtils;

public class JsWebNature implements IProjectNature{
	
	private static final String FILENAME_PROJECT= ".project"; //$NON-NLS-1$
	private static final String FILENAME_CLASSPATH= ".classpath"; //$NON-NLS-1$
	private boolean DEBUG = false;
	
	private IProject fCurrProject;
	private IProgressMonitor monitor;
	private Vector classPathEntries = new Vector();
	private IPath fOutputLocation;
	private JavaProject fJavaProject;
	
	public JsWebNature(){
		monitor = new NullProgressMonitor();
	}
	
	public JsWebNature(IProject project, IProgressMonitor monitor){
		fCurrProject = project;
		if(monitor!=null){
			this.monitor = monitor;
		}else{
			monitor = new NullProgressMonitor();
		}
	}
	
	public void configure() throws CoreException {
		if(hasProjectClassPathFile()){
			JavaProject proj = new JavaProject();
			proj.setProject(fCurrProject);
			IClasspathEntry[] entries= proj.readRawClasspath();
            if(entries!=null && entries.length>0)
            	classPathEntries.addAll(Arrays.asList(entries));
		}
		 initOutputPath();
		 createSourceClassPath();
		 initJREEntry();
		 JsWebNature.addJsNature(fCurrProject, monitor);
		 getJavaProject().setRawClasspath((IClasspathEntry[])classPathEntries.toArray(new IClasspathEntry[]{}), 
				 						  fOutputLocation, monitor);
		 fCurrProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	public void deconfigure() throws CoreException {
		IClasspathEntry[] defaultJRELibrary= PreferenceConstants.getDefaultJRELibrary();
		IClasspathEntry[] entries = getJavaProject().getRawClasspath();
		Vector goodEntries = new Vector();
		 for(int i = 0;i<entries.length;i++){
        	if(entries[i] != defaultJRELibrary[0]){
				goodEntries.add(entries[i]);
			}	
        }
        IPath outputLocation = getJavaProject().getOutputLocation();
        getJavaProject().setRawClasspath((IClasspathEntry[])goodEntries.toArray(new IClasspathEntry[]{}), outputLocation, monitor);
        getJavaProject().deconfigure();
        JsWebNature.removeJsNature(fCurrProject, monitor);
        fCurrProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	public JavaProject getJavaProject(){
		if(fJavaProject==null){
			fJavaProject = new JavaProject();
			JavaCore.create(fCurrProject);
			fJavaProject.setProject(fCurrProject);
		}
		return fJavaProject;
	}
	
	public IProject getProject() {
		return this.fCurrProject;
	}

	public void setProject(IProject project) {
		this.fCurrProject=project;
	}
	
	private IPath getCurrentOutputPath(){
		IPath outputLocation= null;
		if (hasProjectClassPathFile()){
			try {
		       outputLocation= getJavaProject().getOutputLocation();
			} catch (Exception e) {
    			if(DEBUG) System.out.println("Error checking sourcepath:" + e);
    		}
		}
		return outputLocation;
	}
	
	private boolean hasAValidSourcePath(){
		IPath outputLocation= null;
		
		if (hasProjectClassPathFile()){
			try {
				JavaProject proj = new JavaProject();
				proj.setProject(fCurrProject);
				IClasspathEntry[] entries= proj.readRawClasspath();
                for(int i = 0;i<entries.length;i++){
                	if(entries[i].getEntryKind()==IClasspathEntry.CPE_SOURCE){
						return true;
					}	
                }
			} catch (Exception e) {
    			if(DEBUG) System.out.println("Error checking sourcepath:" + e);
    		}
		}
		return false;
	}
	
	private boolean hasProjectClassPathFile(){
		if(fCurrProject==null) return false;
		return fCurrProject.getFile(FILENAME_CLASSPATH).exists();
	}
	
	public static void addJsNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (!project.hasNature(JavaCore.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			String[] newNatures= new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length]= JavaCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		} else {
			if (monitor != null) {
				monitor.worked(1);
			}
		}
	}
	
	
	public static boolean hasJsNature(IProject project){
		boolean valid = false;
		try{
			valid = project.hasNature(JavaCore.NATURE_ID);
		}catch(Exception e){}
		
		return valid;
	}
	
	public static void removeJsNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (project.hasNature(JavaCore.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			String[] newNatures= new String[prevNatures.length - 1];
			
			int k = 0;
			for(int i = 0;i<prevNatures.length;i++){
				if(prevNatures[i]!=JavaCore.NATURE_ID){
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
	
	private void createSourceClassPath(){
		if(hasAValidSourcePath()) return;
		IPath projectPath= fCurrProject.getFullPath();
		classPathEntries.add(JavaCore.newSourceEntry(projectPath));
	}
	
	private void initOutputPath(){
		if(fOutputLocation==null)
			fOutputLocation = getCurrentOutputPath();
		if( fOutputLocation == null)
			fOutputLocation= fCurrProject.getFullPath();
		
	}
	
	private void initJREEntry(){
		IClasspathEntry[] defaultJRELibrary= PreferenceConstants.getDefaultJRELibrary();
		try {
				IClasspathEntry[] entries = getJavaProject().getRawClasspath();
                for(int i = 0;i<entries.length;i++){
                	if(entries[i] == defaultJRELibrary[0]){
						return;
					}	
                }
                classPathEntries.add(defaultJRELibrary[0]);
			} catch (Exception e) {
    			if(DEBUG) System.out.println("Error checking sourcepath:" + e);
    		}
	}
	
}
