package org.eclipse.wst.jsdt.web.core.internal.project;


	/*******************************************************************************
	 * Copyright (c) 2000, 2006 IBM Corporation and others.
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html
	 *
	 * Contributors:
	 *     IBM Corporation - initial API and implementation
	 *******************************************************************************/


	import java.net.URI;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.List;
import java.util.Vector;

	import org.eclipse.core.runtime.CoreException;
	import org.eclipse.core.runtime.IPath;
	import org.eclipse.core.runtime.IProgressMonitor;
	import org.eclipse.core.runtime.IStatus;
	import org.eclipse.core.runtime.NullProgressMonitor;
	import org.eclipse.core.runtime.OperationCanceledException;
	import org.eclipse.core.runtime.Path;
	import org.eclipse.core.runtime.SubProgressMonitor;

	import org.eclipse.core.resources.IContainer;
	import org.eclipse.core.resources.IFile;
	import org.eclipse.core.resources.IFolder;
	import org.eclipse.core.resources.IProject;
	import org.eclipse.core.resources.IProjectDescription;
	import org.eclipse.core.resources.IResource;
	import org.eclipse.core.resources.IWorkspaceRoot;
	import org.eclipse.core.resources.ResourcesPlugin;

	import org.eclipse.swt.SWT;
	import org.eclipse.swt.events.SelectionAdapter;
	import org.eclipse.swt.events.SelectionEvent;
	import org.eclipse.swt.graphics.Image;
	import org.eclipse.swt.layout.GridData;
	import org.eclipse.swt.layout.GridLayout;
	import org.eclipse.swt.widgets.Composite;
	import org.eclipse.swt.widgets.Control;
	import org.eclipse.swt.widgets.Display;
	import org.eclipse.swt.widgets.Shell;
	import org.eclipse.swt.widgets.TabFolder;
	import org.eclipse.swt.widgets.TabItem;
	import org.eclipse.swt.widgets.Widget;

	import org.eclipse.jface.dialogs.Dialog;
	import org.eclipse.jface.dialogs.IDialogConstants;
	import org.eclipse.jface.dialogs.MessageDialog;
	import org.eclipse.jface.operation.IRunnableContext;
	import org.eclipse.jface.preference.IPreferenceStore;
	import org.eclipse.jface.viewers.ILabelProvider;
	import org.eclipse.jface.viewers.ITreeContentProvider;
	import org.eclipse.jface.viewers.ViewerFilter;
	import org.eclipse.jface.window.Window;


	import org.eclipse.wst.jsdt.core.IClasspathEntry;
	import org.eclipse.wst.jsdt.core.IJavaModelStatus;
	import org.eclipse.wst.jsdt.core.IJavaProject;
	import org.eclipse.wst.jsdt.core.JavaConventions;
	import org.eclipse.wst.jsdt.core.JavaCore;

	import org.eclipse.wst.jsdt.internal.corext.util.Messages;

	import org.eclipse.wst.jsdt.ui.PreferenceConstants;

	import org.eclipse.wst.jsdt.internal.ui.JavaPlugin;
	import org.eclipse.wst.jsdt.internal.ui.JavaPluginImages;
	import org.eclipse.wst.jsdt.internal.ui.dialogs.StatusInfo;
	import org.eclipse.wst.jsdt.internal.ui.dialogs.StatusUtil;
	import org.eclipse.wst.jsdt.internal.ui.util.CoreUtility;
	import org.eclipse.wst.jsdt.internal.ui.viewsupport.ImageDisposer;
	import org.eclipse.wst.jsdt.internal.ui.wizards.IStatusChangeListener;
	import org.eclipse.wst.jsdt.internal.ui.wizards.NewWizardMessages;
	import org.eclipse.wst.jsdt.internal.ui.wizards.TypedElementSelectionValidator;
	import org.eclipse.wst.jsdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.CPListElement;
	import org.eclipse.wst.jsdt.internal.ui.wizards.buildpaths.newsourcepage.NewSourceContainerWorkbookPage;
	import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
	import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.DialogField;
	import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
	import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.IListAdapter;
	import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
	import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.wst.jsdt.internal.ui.wizards.dialogfields.StringButtonDialogField;

public class JsBuildPathBlocks {

		public static interface IRemoveOldBinariesQuery {
			
			/**
			 * Do the callback. Returns <code>true</code> if .class files should be removed from the
			 * old output location.
			 * @param oldOutputLocation The old output location
			 * @return Returns true if .class files should be removed.
			 * @throws OperationCanceledException
			 */
			boolean doQuery(IPath oldOutputLocation) throws OperationCanceledException;
			
		}


		private IWorkspaceRoot fWorkspaceRoot;

		
		private String fBuildPath;
		
		private StatusInfo fClassPathStatus;
		private StatusInfo fOutputFolderStatus;	
		private StatusInfo fBuildPathStatus;

		private IJavaProject fCurrJProject;
			
		private IPath fOutputLocationPath;
		
		private IStatusChangeListener fContext;
		private Control fSWTWidget;	
		private TabFolder fTabFolder;
		
		private int fPageIndex;

		private String fUserSettingsTimeStamp;
		private long fFileTimeStamp;
	    
	    private IRunnableContext fRunnableContext;
	    private boolean fUseNewPage;
	
		private final static int IDX_UP= 0;
		private final static int IDX_DOWN= 1;
		private final static int IDX_TOP= 3;
		private final static int IDX_BOTTOM= 4;
		private final static int IDX_SELECT_ALL= 6;
		private final static int IDX_UNSELECT_ALL= 7;
		
		private Vector fClassPathList = new Vector();
		
		public JsBuildPathBlocks() {
			
			
			fCurrJProject= null;
		}
		
		
		/**
		 * Initializes the classpath for the given project. Multiple calls to init are allowed,
		 * but all existing settings will be cleared and replace by the given or default paths.
		 * @param jproject The java project to configure. Does not have to exist.
		 * @param outputLocation The output location to be set in the page. If <code>null</code>
		 * is passed, jdt default settings are used, or - if the project is an existing Java project- the
		 * output location of the existing project 
		 * @param classpathEntries The classpath entries to be set in the page. If <code>null</code>
		 * is passed, jdt default settings are used, or - if the project is an existing Java project - the
		 * classpath entries of the existing project
		 */	
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
		
		
		private String getEncodedSettings() {
			StringBuffer buf= new StringBuffer();	
			CPListElement.appendEncodePath(fOutputLocationPath, buf).append(';');

			int nElements= fClassPathList.size();
			buf.append('[').append(nElements).append(']');
			for (int i= 0; i < nElements; i++) {
				CPListElement elem= (CPListElement) fClassPathList.elementAt(i);
				elem.appendEncodedSettings(buf);
			}
			return buf.toString();
		}
		
		public boolean hasChangesInDialog() {
			String currSettings= getEncodedSettings();
			return !currSettings.equals(fUserSettingsTimeStamp);
		}
		
		public boolean hasChangesInClasspathFile() {
			IFile file= fCurrJProject.getProject().getFile(".classpath"); //$NON-NLS-1$
			return fFileTimeStamp != file.getModificationStamp();
		}
		
		public boolean isClassfileMissing() {
			return !fCurrJProject.getProject().getFile(".classpath").exists(); //$NON-NLS-1$
		}
		
		public void initializeTimeStamps() {
			IFile file= fCurrJProject.getProject().getFile(".classpath"); //$NON-NLS-1$
			fFileTimeStamp= file.getModificationStamp();
			fUserSettingsTimeStamp= getEncodedSettings();
		}

		private ArrayList getExistingEntries(IClasspathEntry[] classpathEntries) {
			ArrayList newClassPath= new ArrayList();
			for (int i= 0; i < classpathEntries.length; i++) {
				IClasspathEntry curr= classpathEntries[i];
				newClassPath.add(CPListElement.createFromExisting(curr, fCurrJProject));
			}
			return newClassPath;
		}
		
		// -------- public api --------
		
		/**
		 * @return Returns the Java project. Can return <code>null<code> if the page has not
		 * been initialized.
		 */
		public IJavaProject getJavaProject() {
			return fCurrJProject;
		}
		
		/**
		 *  @return Returns the current output location. Note that the path returned must not be valid.
		 */	
		public IPath getOutputLocation() {
			return new Path(fBuildPath).makeAbsolute();
		}
		
		/**
		 *  @return Returns the current class path (raw). Note that the entries returned must not be valid.
		 */	
		public IClasspathEntry[] getRawClassPath() {
			//List elements=  fClassPathList.getElements();
			int nElements= fClassPathList.size();
			IClasspathEntry[] entries= new IClasspathEntry[fClassPathList.size()];

			for (int i= 0; i < nElements; i++) {
				CPListElement currElement= (CPListElement) fClassPathList.get(i);
				entries[i]= currElement.getClasspathEntry();
			}
			return entries;
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
		
		public static IPath getDefaultOutputLocation(IJavaProject jproj) {
			IPreferenceStore store= PreferenceConstants.getPreferenceStore();
			if (store.getBoolean(PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ)) {
				String outputLocationName= store.getString(PreferenceConstants.SRCBIN_BINNAME);
				return jproj.getProject().getFullPath().append(outputLocationName);
			} else {
				return jproj.getProject().getFullPath();
			}
		}	
			

		
		

		
		
		private IStatus findMostSevereStatus() {
			return StatusUtil.getMostSevere(new IStatus[] { fClassPathStatus, fOutputFolderStatus, fBuildPathStatus });
		}
		
		
		/**
		 * Validates the build path.
		 */
		public void updateClassPathStatus() {
			fClassPathStatus.setOK();
			
		//	List elements= fClassPathList.getElements();
		
			CPListElement entryMissing= null;
			CPListElement entryDeprecated= null;
			int nEntriesMissing= 0;
			IClasspathEntry[] entries= new IClasspathEntry[fClassPathList.size()];

			for (int i= fClassPathList.size()-1 ; i >= 0 ; i--) {
				CPListElement currElement= (CPListElement)fClassPathList.get(i);
				boolean isChecked= true;
				if (currElement.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					
				} else {
					currElement.setExported(isChecked);
				}

				entries[i]= currElement.getClasspathEntry();
				if (currElement.isMissing()) {
					nEntriesMissing++;
					if (entryMissing == null) {
						entryMissing= currElement;
					}
				}
				if (entryDeprecated == null & currElement.isDeprecated()) {
					entryDeprecated= currElement;
				}
			}
					
			if (nEntriesMissing > 0) {
				if (nEntriesMissing == 1) {
					fClassPathStatus.setWarning(Messages.format(NewWizardMessages.BuildPathsBlock_warning_EntryMissing, entryMissing.getPath().toString())); 
				} else {
					fClassPathStatus.setWarning(Messages.format(NewWizardMessages.BuildPathsBlock_warning_EntriesMissing, String.valueOf(nEntriesMissing))); 
				}
			} else if (entryDeprecated != null) {
				fClassPathStatus.setInfo(entryDeprecated.getDeprecationMessage());
			}
					
	/*		if (fCurrJProject.hasClasspathCycle(entries)) {
				fClassPathStatus.setWarning(NewWizardMessages.getString("BuildPathsBlock.warning.CycleInClassPath")); //$NON-NLS-1$
			}
	*/		
			updateBuildPathStatus();
		}

		/**
		 * Validates output location & build path.
		 */	
		private void updateOutputLocationStatus() {
			fOutputLocationPath= null;
			
			String text= fBuildPath;
			if ("".equals(text)) { //$NON-NLS-1$
				fOutputFolderStatus.setError(NewWizardMessages.BuildPathsBlock_error_EnterBuildPath); 
				return;
			}
			IPath path= getOutputLocation();
			fOutputLocationPath= path;
			
			IResource res= fWorkspaceRoot.findMember(path);
			if (res != null) {
				// if exists, must be a folder or project
				if (res.getType() == IResource.FILE) {
					fOutputFolderStatus.setError(NewWizardMessages.BuildPathsBlock_error_InvalidBuildPath); 
					return;
				}
			}
			
			fOutputFolderStatus.setOK();
			
			String pathStr= fBuildPath;
			Path outputPath= (new Path(pathStr));
			pathStr= outputPath.lastSegment();
			if (pathStr.equals(".settings") && outputPath.segmentCount() == 2) { //$NON-NLS-1$
				fOutputFolderStatus.setWarning(NewWizardMessages.OutputLocation_SettingsAsLocation);
			}
			
			if (pathStr.charAt(0) == '.' && pathStr.length() > 1) {
				fOutputFolderStatus.setWarning(Messages.format(NewWizardMessages.OutputLocation_DotAsLocation, pathStr));
			}
			
			updateBuildPathStatus();
		}
			
		private void updateBuildPathStatus() {
			System.out.println("Refactor/remove JsBuildPathBlocks.updateBuildPathStatus()");
		}
		
		// -------- creation -------------------------------
		
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
			initializeTimeStamps();
			
			
		}
	    	
		/*
		 * Creates the Java project and sets the configured build path and output location.
		 * If the project already exists only build paths are updated.
		 */
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
		
		public static boolean hasClassfiles(IResource resource) throws CoreException {
			if (resource.isDerived()) { 
				return true;
			}		
			if (resource instanceof IContainer) {
				IResource[] members= ((IContainer) resource).members();
				for (int i= 0; i < members.length; i++) {
					if (hasClassfiles(members[i])) {
						return true;
					}
				}
			}
			return false;
		}
		

		public static void removeOldClassfiles(IResource resource) throws CoreException {
			if (resource.isDerived()) {
				resource.delete(false, null);
			} else if (resource instanceof IContainer) {
				IResource[] members= ((IContainer) resource).members();
				for (int i= 0; i < members.length; i++) {
					removeOldClassfiles(members[i]);
				}
			}
		}
		
		public static IRemoveOldBinariesQuery getRemoveOldBinariesQuery(final Shell shell) {
			return new IRemoveOldBinariesQuery() {
				public boolean doQuery(final IPath oldOutputLocation) throws OperationCanceledException {
					final int[] res= new int[] { 1 };
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							Shell sh= shell != null ? shell : JavaPlugin.getActiveWorkbenchShell();
							String title= NewWizardMessages.BuildPathsBlock_RemoveBinariesDialog_title; 
							String message= Messages.format(NewWizardMessages.BuildPathsBlock_RemoveBinariesDialog_description, oldOutputLocation.toString()); 
							MessageDialog dialog= new MessageDialog(sh, title, null, message, MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
							res[0]= dialog.open();
						}
					});
					if (res[0] == 0) {
						return true;
					} else if (res[0] == 1) {
						return false;
					}
					throw new OperationCanceledException();
				}
			};
		}	

	
		
		private CPListElement findElement(IClasspathEntry entry) {
			for (int i= 0, len= fClassPathList.size(); i < len; i++) {
				CPListElement curr= (CPListElement) fClassPathList.get(i);
				if (curr.getEntryKind() == entry.getEntryKind() && curr.getPath().equals(entry.getPath())) {
					return curr;
				}
			}
			return null;
		}
		
	


}
