package org.eclipse.wst.jsdt.web.core.internal.project;


import org.eclipse.wst.jsdt.ui.JavaUI;
import org.eclipse.wst.jsdt.ui.PreferenceConstants;
import org.eclipse.wst.jsdt.ui.wizards.JavaCapabilityConfigurationPage;
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.wst.jsdt.core.IClasspathEntry;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.core.JavaProject;
import org.eclipse.wst.jsdt.internal.corext.util.Messages;
import org.eclipse.wst.jsdt.internal.ui.JavaPlugin;
import org.eclipse.wst.jsdt.internal.ui.util.CoreUtility;
import org.eclipse.wst.jsdt.internal.ui.util.ExceptionHandler;
import org.eclipse.wst.jsdt.internal.ui.wizards.ClassPathDetector;
import org.eclipse.wst.jsdt.internal.ui.wizards.JavaProjectWizardFirstPage;
import org.eclipse.wst.jsdt.internal.ui.wizards.JavaProjectWizardSecondPage;
import org.eclipse.wst.jsdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.CPListElement;



public class JsWebNature implements IProjectNature{
	private String fBuildPath;
	private IJavaProject fCurrJProject;
	private Vector fClassPathList = new Vector();
	private static final String FILENAME_PROJECT= ".project"; //$NON-NLS-1$
	private static final String FILENAME_CLASSPATH= ".classpath"; //$NON-NLS-1$
	private String fUserSettingsTimeStamp;
	private long fFileTimeStamp;
	
	private URI fCurrProjectLocation; // null if location is platform location
	private IProject fCurrProject;
	
	private IJavaProject fJavaProject;
	
	private boolean fKeepContent;

	private File fDotProjectBackup;
	private File fDotClasspathBackup;
	private Boolean fIsAutobuild = true;

	
	
	private IProgressMonitor monitor;
	
	private static boolean DEBUG = true;
	
	private JsBuildPathBlocks fJavaCapabilityConfiguration;
	
	public JsWebNature(IProject project, IProgressMonitor monitor){
		this.monitor=monitor;
		this.fCurrProject = project;
	}
	
	public boolean isValidJSDTProject(){
		
		
		return	fCurrProject!=null && JavaProject.hasJavaNature(fCurrProject) &&  getOutputPath()!=null && isValidSourcePath();
		
	}

//	private JsBuildPathBlocks getJavaCapabilityConfig(){
//		if(fJavaCapabilityConfiguration==null){
//			fJavaCapabilityConfiguration = new JsBuildPathBlocks();
//		}
//		return fJavaCapabilityConfiguration;
//	}
	
	public JsWebNature(IProject project){
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
				
				//if(DEBUG) System.out.println("Error checking sourcepath:" + e);
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
            
			createProject(fCurrProject, fCurrProjectLocation, new SubProgressMonitor(monitor, 2));
				
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
				//IFolder folder= fCurrProject.getFolder(srcPath);
				//CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
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
				//
				if ( binPath.segmentCount() > 0 &&  !binPath.equals(srcPath)) {
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
			
			//fJavaProject = proj.getJavaProject();
			
			//getJavaCapabilityConfig().createProject(, fCurrProjectLocation, monitor);
			fJavaProject = JavaCore.create(fCurrProject);
			init(fJavaProject, outputLocation, entries);
			addJavaNature(fJavaProject.getProject(), monitor);
			configureJavaProject(new SubProgressMonitor(monitor, 3)); // create the Java project to allow the use of the new source folder page
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
	public void init(IJavaProject jproject, IPath outputLocation, IClasspathEntry[] classpathEntries) {
		fCurrJProject= jproject;
		boolean projectExists= false;
		List newClassPath= null;
		IProject project= fCurrJProject.getProject();
		projectExists= (project.exists() && project.getFile(".classpath").exists()); //$NON-NLS-1$
		if  (projectExists) {
			if (outputLocation == null) {
				outputLocation=  fCurrJProject.readOutputLocation();
			}
			if (classpathEntries == null) {
				classpathEntries=  fCurrJProject.readRawClasspath();
			}
		}
		if (outputLocation == null) {
			outputLocation= getDefaultOutputLocation(jproject);
		}			

		if (classpathEntries != null) {
			newClassPath= getExistingEntries(classpathEntries);
		}
		if (newClassPath == null) {
			newClassPath= getDefaultClassPath(jproject);
		}
		
		List exportedEntries = new ArrayList();
		for (int i= 0; i < newClassPath.size(); i++) {
			CPListElement curr= (CPListElement) newClassPath.get(i);
			if (curr.isExported() || curr.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				exportedEntries.add(curr);
			}
		}
		fClassPathList.clear();
		fClassPathList.addAll(newClassPath);
		fBuildPath = outputLocation.makeRelative().toString();
		
	}
	public static IPath getDefaultOutputLocation(IJavaProject jproj) {
		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
		if (store.getBoolean(PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ)) {
			String outputLocationName= store.getString(PreferenceConstants.SRCBIN_BINNAME);
			return jproj.getProject().getFullPath().append(outputLocationName);
		} else {
			return jproj.getProject().getFullPath();
		}
	}	
	private ArrayList getExistingEntries(IClasspathEntry[] classpathEntries) {
		ArrayList newClassPath= new ArrayList();
		for (int i= 0; i < classpathEntries.length; i++) {
			IClasspathEntry curr= classpathEntries[i];
			newClassPath.add(CPListElement.createFromExisting(curr, fCurrJProject));
		}
		return newClassPath;
	}
	private List getDefaultClassPath(IJavaProject jproj) {
		List list= new ArrayList();
		IResource srcFolder;
		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
		String sourceFolderName= store.getString(PreferenceConstants.SRCBIN_SRCNAME);
		if (store.getBoolean(PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ) && sourceFolderName.length() > 0) {
			srcFolder= jproj.getProject().getFolder(sourceFolderName);
		} else {
			srcFolder= jproj.getProject();
		}

		list.add(new CPListElement(jproj, IClasspathEntry.CPE_SOURCE, srcFolder.getFullPath(), srcFolder));

		IClasspathEntry[] jreEntries= PreferenceConstants.getDefaultJRELibrary();
		list.addAll(getExistingEntries(jreEntries));
		return list;
	}
	
	public static void createProject(IProject project, URI locationURI, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}				
		monitor.beginTask(NewWizardMessages.BuildPathsBlock_operationdesc_project, 10); 

		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc= project.getWorkspace().newProjectDescription(project.getName());
				if (locationURI != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(locationURI)) {
					locationURI= null;
				}
				desc.setLocationURI(locationURI);
				project.create(desc, monitor);
				monitor= null;
			}
			if (!project.isOpen()) {
				project.open(monitor);
				monitor= null;
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}
	public static void addJavaNature(IProject project, IProgressMonitor monitor) throws CoreException {
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
	
	public void configureJavaProject(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		
		flush(fClassPathList, getOutputLocation(), getJavaProject(), monitor);
		
		
	}
	public IPath getOutputLocation() {
		return new Path(fBuildPath).makeAbsolute();
	}
	public static void flush(List classPathEntries, IPath outputLocation, IJavaProject javaProject, IProgressMonitor monitor) throws CoreException, OperationCanceledException {		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		monitor.setTaskName(NewWizardMessages.BuildPathsBlock_operationdesc_java); 
		monitor.beginTask("", classPathEntries.size() * 4 + 4); //$NON-NLS-1$
		try {
			
			IProject project= javaProject.getProject();
			IPath projPath= project.getFullPath();
			
			IPath oldOutputLocation;
			try {
				oldOutputLocation= javaProject.getOutputLocation();		
			} catch (CoreException e) {
				oldOutputLocation= projPath.append(PreferenceConstants.getPreferenceStore().getString(PreferenceConstants.SRCBIN_BINNAME));
			}
			
			if (oldOutputLocation.equals(projPath) && !outputLocation.equals(projPath)) {
				if (JsBuildPathBlocks.hasClassfiles(project)) {
					if (JsBuildPathBlocks.getRemoveOldBinariesQuery(JavaPlugin.getActiveWorkbenchShell()).doQuery(projPath)) {
						JsBuildPathBlocks.removeOldClassfiles(project);
					}
				}
			}
			
			monitor.worked(1);
			
			IWorkspaceRoot fWorkspaceRoot= JavaPlugin.getWorkspace().getRoot();
			
			//create and set the output path first
			if (!fWorkspaceRoot.exists(outputLocation)) {
				IFolder folder= fWorkspaceRoot.getFolder(outputLocation);
				CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
				folder.setDerived(true);		
			} else {
				monitor.worked(1);
			}
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			int nEntries= classPathEntries.size();
			IClasspathEntry[] classpath= new IClasspathEntry[nEntries];
			int i= 0;
			
			for (Iterator iter= classPathEntries.iterator(); iter.hasNext();) {
				CPListElement entry= (CPListElement)iter.next();
				classpath[i]= entry.getClasspathEntry();
				i++;
				
				IResource res= entry.getResource();
				//1 tick
				if (res instanceof IFolder && entry.getLinkTarget() == null && !res.exists()) {
					CoreUtility.createFolder((IFolder)res, true, true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
				
				//3 ticks
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IPath folderOutput= (IPath) entry.getAttribute(CPListElement.OUTPUT);
					if (folderOutput != null && folderOutput.segmentCount() > 1) {
						IFolder folder= fWorkspaceRoot.getFolder(folderOutput);
						CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
					} else {
						monitor.worked(1);
					}
					
					IPath path= entry.getPath();
					if (projPath.equals(path)) {
						monitor.worked(2);
						continue;	
					}
					
					if (projPath.isPrefixOf(path)) {
						path= path.removeFirstSegments(projPath.segmentCount());
					}
					IFolder folder= project.getFolder(path);
					IPath orginalPath= entry.getOrginalPath();
					if (orginalPath == null) {
						if (!folder.exists()) {
							//New source folder needs to be created
							if (entry.getLinkTarget() == null) {
								CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 2));
							} else {
								folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, new SubProgressMonitor(monitor, 2));
							}
						}
					} else {
						if (projPath.isPrefixOf(orginalPath)) {
							orginalPath= orginalPath.removeFirstSegments(projPath.segmentCount());
						}
						IFolder orginalFolder= project.getFolder(orginalPath);
						if (entry.getLinkTarget() == null) {
							if (!folder.exists()) {
								//Source folder was edited, move to new location
								IPath parentPath= entry.getPath().removeLastSegments(1);
								if (projPath.isPrefixOf(parentPath)) {
									parentPath= parentPath.removeFirstSegments(projPath.segmentCount());
								}
								if (parentPath.segmentCount() > 0) {
									IFolder parentFolder= project.getFolder(parentPath);
									if (!parentFolder.exists()) {
										CoreUtility.createFolder(parentFolder, true, true, new SubProgressMonitor(monitor, 1));
									} else {
										monitor.worked(1);
									}
								} else {
									monitor.worked(1);
								}
								orginalFolder.move(entry.getPath(), true, true, new SubProgressMonitor(monitor, 1));
							}
						} else {
							if (!folder.exists() || !entry.getLinkTarget().equals(entry.getOrginalLinkTarget())) {
								orginalFolder.delete(true, new SubProgressMonitor(monitor, 1));
								folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, new SubProgressMonitor(monitor, 1));
							}
						}
					}
				} else {
					monitor.worked(3);
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}

			javaProject.setRawClasspath(classpath, outputLocation, new SubProgressMonitor(monitor, 2));
		} finally {
			monitor.done();
		}
	}


}
