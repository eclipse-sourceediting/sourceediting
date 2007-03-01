package org.eclipse.wst.jsdt.web.core.internal.nature;


import org.eclipse.jsdt.ui.JavaUI;
import org.eclipse.jsdt.ui.PreferenceConstants;
import org.eclipse.jsdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;


import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jsdt.core.IClasspathEntry;
import org.eclipse.jsdt.core.IJavaProject;
import org.eclipse.jsdt.core.JavaCore;
import org.eclipse.jsdt.core.JavaModelException;
import org.eclipse.jsdt.internal.core.JavaProject;
import org.eclipse.jsdt.internal.corext.util.Messages;
import org.eclipse.jsdt.internal.ui.util.CoreUtility;
import org.eclipse.jsdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jsdt.internal.ui.wizards.ClassPathDetector;
import org.eclipse.jsdt.internal.ui.wizards.JavaProjectWizardFirstPage;
import org.eclipse.jsdt.internal.ui.wizards.JavaProjectWizardSecondPage;
import org.eclipse.jsdt.internal.ui.wizards.NewWizardMessages;



public class JSDTWebNature implements IProjectNature{

	private static final String FILENAME_PROJECT= ".project"; //$NON-NLS-1$
	private static final String FILENAME_CLASSPATH= ".classpath"; //$NON-NLS-1$

	private URI fCurrProjectLocation; // null if location is platform location
	private IProject fCurrProject;
	
	private IJavaProject fJavaProject;
	
	private boolean fKeepContent;

	private File fDotProjectBackup;
	private File fDotClasspathBackup;
	private Boolean fIsAutobuild = true;

	
	
	private IProgressMonitor monitor;
	
	private static boolean DEBUG = true;
	
	private JavaCapabilityConfigurationPage fJavaCapabilityConfiguration;
	
	public JSDTWebNature(IProject project, IProgressMonitor monitor){
		this.monitor=monitor;
		this.fCurrProject = project;
	}
	
	public boolean isValidJSDTProject(){
		
		
		return	fCurrProject!=null && JavaProject.hasJavaNature(fCurrProject) &&  getOutputPath()!=null && isValidSourcePath();
		
	}

	private JavaCapabilityConfigurationPage getJavaCapabilityConfig(){
		if(fJavaCapabilityConfiguration==null){
			fJavaCapabilityConfiguration = new JavaCapabilityConfigurationPage();
		}
		return fJavaCapabilityConfiguration;
	}
	
	public JSDTWebNature(IProject project){
		this(project,new NullProgressMonitor());
	}
	
	public IPath getOutputPath(){
		try {
			
				return ((JavaCore.create(fCurrProject)).getOutputLocation());
				
			
		} catch (Exception e) {
			
			if(DEBUG) System.out.println("Error checking sourcepath:" + e);
		}
		return null;
	}
	
	public boolean isValidSourcePath(){
		
			
			try {
				IClasspathEntry[] cpentries = (((JavaCore.create(fCurrProject)).getRawClasspath()));
				
				for(int i = 0;i<cpentries.length;i++){
					if(cpentries[i].getEntryKind()==IClasspathEntry.CPE_SOURCE){
						
						return true;
					}
				}
			} catch (Exception e) {
				
				if(DEBUG) System.out.println("Error checking sourcepath:" + e);
			}
			
			
		
		return false;
	}
	
	public void configure() throws CoreException {
		
		URI fCurrProjectLocation= URIUtil.toURI(fCurrProject.getLocation());
		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		
		try {
			monitor.beginTask("Initializing JavaScript builder.", 7); 
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			URI realLocation= fCurrProjectLocation;
			if (fCurrProjectLocation == null) {  // inside workspace
				try {
					URI rootLocation= ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
					realLocation= new URI(rootLocation.getScheme(), null,
						Path.fromPortableString(rootLocation.getPath()).append(fCurrProject.getName()).toString(),
						null);
				} catch (URISyntaxException e) {
					Assert.isTrue(false, "Can't happen"); //$NON-NLS-1$
				}
			}

			rememberExistingFiles(realLocation);
            
			getJavaCapabilityConfig().createProject(fCurrProject, fCurrProjectLocation, new SubProgressMonitor(monitor, 2));
				
			IClasspathEntry[] entries= null;
			IPath outputLocation= null;
	
			
			if (!fCurrProject.getFile(FILENAME_CLASSPATH).exists()) { 
					final ClassPathDetector detector= new ClassPathDetector(fCurrProject, new SubProgressMonitor(monitor, 2));
					entries= detector.getClasspath();
                    outputLocation= detector.getOutputLocation();
			} else {
					monitor.worked(2);
			}
			
			IPath srcPath = null;
			
			if(fCurrProject instanceof JavaProject){
				IClasspathEntry[] cpentries = (((JavaProject)fCurrProject).getRawClasspath());
				
				for(int i = 0;i<cpentries.length;i++){
					if(cpentries[i].getEntryKind()==IClasspathEntry.CPE_SOURCE){
						srcPath = cpentries[i].getPath();
					}
				}
				
			}
			
			if( srcPath==null ){
				IPreferenceStore store= PreferenceConstants.getPreferenceStore();
				srcPath = new Path(store.getString(PreferenceConstants.SRCBIN_SRCNAME));
				
				if (srcPath.segmentCount() > 0) {
					IFolder folder= fCurrProject.getFolder(srcPath);
					CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
						
				final IPath projectPath= fCurrProject.getFullPath();

				// configure the classpath entries, including the default jre library.
				List cpEntries= new ArrayList();
				cpEntries.add(JavaCore.newSourceEntry(projectPath.append(srcPath)));
				cpEntries.addAll(Arrays.asList(getDefaultClasspathEntry()));
				entries= (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);

			} else {
				IPath projectPath= fCurrProject.getFullPath();
				List cpEntries= new ArrayList();
				cpEntries.add(JavaCore.newSourceEntry(projectPath));
				cpEntries.addAll(Arrays.asList(getDefaultClasspathEntry()));
				entries= (IClasspathEntry[]) cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);

			}

			if( getOutputPath()==null  ){
				IPreferenceStore store= PreferenceConstants.getPreferenceStore();
				IPath binPath= new Path(store.getString(PreferenceConstants.SRCBIN_BINNAME));
				
				if (binPath.segmentCount() > 0 && !binPath.equals(srcPath)) {
					IFolder folder= fCurrProject.getFolder(binPath);
					CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
				
				final IPath projectPath= fCurrProject.getFullPath();

				// configure the output location
				outputLocation= projectPath.append(binPath);
			} else {
				IPath projectPath= fCurrProject.getFullPath();
				outputLocation= projectPath;
				monitor.worked(2);
			}
			
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			fJavaProject = JavaCore.create(fCurrProject);
			getJavaCapabilityConfig().init(fJavaProject, outputLocation, entries, false);
			getJavaCapabilityConfig().configureJavaProject(new SubProgressMonitor(monitor, 3)); // create the Java project to allow the use of the new source folder page
			fCurrProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			
		}catch(Exception e){
			if(DEBUG) System.out.println("Error creating/configuring JSDT project.." + e);
		}finally {
			CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue());
			monitor.done();
		}
		
	}

	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		if (fCurrProject == null || !fCurrProject.exists()) {
			return;
		}
		
		try {
			doRemoveProject(monitor);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			if(DEBUG) System.out.println("Error removing JSDT nature from project.." + e);
		}
		
		
	}

	public IProject getProject() {
		return this.fCurrProject;
	}

	public void setProject(IProject project) {
		this.fCurrProject=project;
	}
	
	public IJavaProject getJavaProject() {
		if(fJavaProject==null && isValidJSDTProject()){
			return JavaCore.create(fCurrProject);
			
		}
		return fJavaProject;
	}
	
	private IClasspathEntry[] getDefaultClasspathEntry() {
		IClasspathEntry[] defaultJRELibrary= PreferenceConstants.getDefaultJRELibrary();
		return defaultJRELibrary;
	}
	
	private void rememberExistingFiles(URI projectLocation) throws CoreException {
		fDotProjectBackup= null;
		fDotClasspathBackup= null;
		
		IFileStore file= EFS.getStore(projectLocation);
		if (file.fetchInfo().exists()) {
			IFileStore projectFile= file.getChild(FILENAME_PROJECT);
			if (projectFile.fetchInfo().exists()) {
				fDotProjectBackup= createBackup(projectFile, "project-desc"); //$NON-NLS-1$ 
			}
			IFileStore classpathFile= file.getChild(FILENAME_CLASSPATH);
			if (classpathFile.fetchInfo().exists()) {
				fDotClasspathBackup= createBackup(classpathFile, "classpath-desc"); //$NON-NLS-1$ 
			}
		}
	}
	
	public void doRemoveProject(IProgressMonitor monitor) throws InvocationTargetException {
		final boolean noProgressMonitor= (fCurrProjectLocation == null); // inside workspace
		if (monitor == null || noProgressMonitor) {
			monitor= new NullProgressMonitor();
		}
		monitor.beginTask(NewWizardMessages.JavaProjectWizardSecondPage_operation_remove, 3); 
		try {
			try {
				URI projLoc= fCurrProject.getLocationURI();
				
			    boolean removeContent= !fKeepContent && fCurrProject.isSynchronized(IResource.DEPTH_INFINITE);
			    fCurrProject.delete(removeContent, false, new SubProgressMonitor(monitor, 2));
				
				restoreExistingFiles(projLoc, new SubProgressMonitor(monitor, 1));
			} finally {
				CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue()); // fIsAutobuild must be set
				fIsAutobuild= null;
			}
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
			fCurrProject= null;
			fKeepContent= false;
		}
	}
	
	private File createBackup(IFileStore source, String name) throws CoreException {
		try {
			File bak= File.createTempFile("eclipse-" + name, ".bak");  //$NON-NLS-1$//$NON-NLS-2$
			copyFile(source, bak);
			return bak;
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, Messages.format(NewWizardMessages.JavaProjectWizardSecondPage_problem_backup, name), e); 
			throw new CoreException(status);
		} 
	}
	private void copyFile(IFileStore source, File target) throws IOException, CoreException {
		InputStream is= source.openInputStream(EFS.NONE, null);
		FileOutputStream os= new FileOutputStream(target);
		copyFile(is, os);
	}
	
	private void copyFile(File source, IFileStore target, IProgressMonitor monitor) throws IOException, CoreException {
		FileInputStream is= new FileInputStream(source);
		OutputStream os= target.openOutputStream(EFS.NONE, monitor);
		copyFile(is, os);
	}
	
	private void copyFile(InputStream is, OutputStream os) throws IOException {		
		try {
			byte[] buffer = new byte[8192];
			while (true) {
				int bytesRead= is.read(buffer);
				if (bytesRead == -1)
					break;
				
				os.write(buffer, 0, bytesRead);
			}
		} finally {
			try {
				is.close();
			} finally {
				os.close();
			}
		}
	}
	private void restoreExistingFiles(URI projectLocation, IProgressMonitor monitor) throws CoreException {
		int ticks= ((fDotProjectBackup != null ? 1 : 0) + (fDotClasspathBackup != null ? 1 : 0)) * 2;
		monitor.beginTask("", ticks); //$NON-NLS-1$
		try {
			if (fDotProjectBackup != null) {
				IFileStore projectFile= EFS.getStore(projectLocation).getChild(FILENAME_PROJECT);
				projectFile.delete(EFS.NONE, new SubProgressMonitor(monitor, 1));
				copyFile(fDotProjectBackup, projectFile, new SubProgressMonitor(monitor, 1));
			}
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, NewWizardMessages.JavaProjectWizardSecondPage_problem_restore_project, e); 
			throw new CoreException(status);
		}
		try {
			if (fDotClasspathBackup != null) {
				IFileStore classpathFile= EFS.getStore(projectLocation).getChild(FILENAME_CLASSPATH);
				classpathFile.delete(EFS.NONE, new SubProgressMonitor(monitor, 1));
				copyFile(fDotClasspathBackup, classpathFile, new SubProgressMonitor(monitor, 1));
			}
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, NewWizardMessages.JavaProjectWizardSecondPage_problem_restore_classpath, e); 
			throw new CoreException(status);
		}
	}
}
