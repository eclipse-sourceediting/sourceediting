package org.eclipse.wst.web.internal.operation;

/*
 * Licensed Material - Property of IBM (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. US
 * Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP
 * Schedule Contract with IBM Corp.
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchContext;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;
import org.eclipse.wst.web.internal.WSTWebPlugin;

import org.eclipse.jem.util.emf.workbench.ProjectUtilities;

/*
 * Licensed Materials - Property of IBM, WebSphere Studio Workbench (c) Copyright IBM Corp 2000
 */
public class StaticWebNatureRuntime implements IStaticWebNature {
	static protected String PROJECTTYPE_STATIC_VALUE = "STATIC"; //$NON-NLS-1$
	static protected String PROJECTTYPE_J2EE_VALUE = "J2EE"; //$NON-NLS-1$

	// Version number may not change with every release,
	// only when changes necessitate a new version number
	public static int CURRENT_VERSION = 500;

	public static int instanceCount = 1;
	public int instanceStamp = 0;
	protected String contextRoot = null;
	protected String[] featureIds = null;
	protected int fVersion = -1;

	//protected WebSettings fWebSettings;


	protected IModule module;
	protected IProject project;

//	private static final String LINKS_BUILDER_ID = "com.ibm.etools.webtools.additions.linksbuilder"; //$NON-NLS-1$

	/**
	 * WebNatureRuntime constructor comment.
	 */
	public StaticWebNatureRuntime() {
		super();
		instanceStamp = instanceCount;
		instanceCount++;

		class WebSettingsModifier implements IResourceChangeListener, IResourceDeltaVisitor {

			public void resourceChanged(IResourceChangeEvent event) {
				if (event.getSource() instanceof IWorkspace) {
					IResourceDelta delta = event.getDelta();
					switch (event.getType()) {
						case IResourceChangeEvent.PRE_BUILD :
							if (delta != null) {
								try {
									delta.accept(this);
								} catch (CoreException e) {
									//Ignore
								}
							}
							break;
					}
				}
			}

			public boolean visit(IResourceDelta delta) throws CoreException {
				if (delta != null) {
					// get target IResource
					final IResource resource = delta.getResource();
					if (resource != null) {
						if (resource.getType() == IResource.FILE) {
							// If the websettings file is being modified, reset all the cached
							// values
							// in the nature
							IFile file = (IFile) resource;
							if ((file.getName().equals(ISimpleWebNatureConstants.WEBSETTINGS_FILE_NAME)) && (resource.getProject().getName().equals(getProject().getName()))) {
								resetWebSettings();
							}
						}
					}
					return true;
				}
				return false;
			}

		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new WebSettingsModifier(), IResourceChangeEvent.PRE_BUILD);

	}

	/*
	 * Do nothing with a cvs ignore file for web projects, till a better solution is found from OTI
	 */

	public void addCVSIgnoreFile() {
		//Do nothing
	}

	/**
	 * Adds a builder to the build spec for the given project.
	 */
	protected void addToFrontOfBuildSpec(String builderID) throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				found = true;
				break;
			}
		}
		if (!found) {
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			ICommand[] newCommands = new ICommand[commands.length + 1];
			System.arraycopy(commands, 0, newCommands, 1, commands.length);
			newCommands[0] = command;
			IProjectDescription desc = getProject().getDescription();
			desc.setBuildSpec(newCommands);
			getProject().setDescription(desc, null);
		}
	}

	/**
	 * Create a default file for the user given the name (directory relative to the project) and the
	 * default contents for the file.
	 * 
	 * @param newFilePath -
	 *            IPath
	 * @param newFileContents -
	 *            String
	 */
	public void createFile(IPath newFilePath, String newFileContents) throws CoreException {

		IPath projectPath = project.getFullPath();
		IWorkspace workspace = WSTWebPlugin.getWorkspace();

		createFolder(newFilePath.removeLastSegments(1).toString());

		IFile outputFile = workspace.getRoot().getFile(projectPath.append(newFilePath));
		outputFile.refreshLocal(IResource.DEPTH_INFINITE, null);

		InputStream inputStream = new ByteArrayInputStream(newFileContents.getBytes());
		if (!(outputFile.exists()))
			outputFile.create(inputStream, true, null);
	}

	/**
	 * Create the folders for the project we have just created.
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 *                The exception description.
	 */
	protected void createFolders() throws CoreException {
		// Create the WEB_MODULE directory
		createFolder(getBasicWebModulePath());
	}

	/**
	 * Create a new nature runtime from the project info
	 */
	//	public static StaticWebNatureRuntime createRuntime(WebProjectInfo info) throws CoreException
	// {
	//		IProject project = info.getProject();
	//		if (!hasRuntime(project)) {
	//			addNatureToProject(project, ISimpleWebNatureConstants.STATIC_NATURE_ID);
	//			StaticWebNatureRuntime runtime
	// =(StaticWebNatureRuntime)WebNatureRuntimeUtilities.getRuntime(project);
	//			runtime.initializeFromInfo(info);
	//			return runtime;
	//		}
	//		return getRuntime(project);
	//	}
	/**
	 * Removes this nature from the project.
	 * 
	 * @see IProjectNature#deconfigure
	 */
	public void deconfigure() throws CoreException {
		//		super.deconfigure();
		//		removeFromBuildSpec(J2EEPlugin.LINKS_BUILDER_ID);
	}

	/*
	 * Returns the context root that the server is configured with (also called the web app path).
	 * This is the path that the war is placed on within the deployed server. This path must be
	 * included as the first segment of a doc relative path specification within an html file.
	 */
	public String getContextRoot() {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
		if (contextRoot == null) {
//			WebSettings settings = getWebSettings();
//			contextRoot = settings.getContextRoot();
//			if (contextRoot == null)
//				contextRoot = getProject().getName();
		}	
		return contextRoot;
	}

//	protected WebSettings getWebSettings() {
//		if (fWebSettings == null) {
//			fWebSettings = new WebSettings(getProject());
//		}
//		return fWebSettings;
//	}

	/*
	 * Returns the root that the server runs off of. For projects created in v4, this is
	 * webApplication. For projects created in v5.0, this is Web Content. For projects created in
	 * v5.0.1 and later, this is configurable per project by the user.
	 */
	public IContainer getModuleServerRoot() {
		return getProject().getFolder(getModuleServerRootName());
	}


	/*
	 * Returns the name of the module server root directory. For projects created in v4, this is
	 * webApplication. For projects created in v5.0, this is Web Content. For projects created in
	 * v5.0.1 and later, this is configurable per project by the user.
	 */
	public String getModuleServerRootName() {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		String name = getWebSettings().getWebContentName();
//		if (name != null)
//			return name;

		int version = getVersion();
		// If created in V5 or beyond
		if (version != -1 && version >= 500)
			return ISimpleWebNatureConstants.WEB_MODULE_DIRECTORY_;

		return ISimpleWebNatureConstants.WEB_MODULE_DIRECTORY_V4;
	}


	public void setModuleServerRootName(String name) throws CoreException {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		getWebSettings().setWebContentName(name);
//		getWebSettings().write();
	}

	/**
	 * Return the root location for loading mof resources; defaults to the source folder, subclasses
	 * may override
	 */
	public IContainer getEMFRoot() {
		return getModuleServerRoot();
	}

	/**
	 * Return the nature's ID.
	 */
	public String getNatureID() {
		return ISimpleWebNatureConstants.STATIC_NATURE_ID;
	}

	/**
	 * Return the ID of the plugin that this nature is contained within.
	 */
	protected String getPluginID() {
		return WSTWebPlugin.PLUGIN_ID;	
		}
	/**
	 * Insert the method's description here. Creation date: (10/30/2001 11:12:41 PM)
	 * 
	 * @return org.eclipse.core.resources.IContainer
	 */
	public IContainer getRootPublishableFolder() {
		return getModuleServerRoot();
	}

	/**
	 * Get a WebNatureRuntime that corresponds to the supplied project.
	 * 
	 * @return com.ibm.itp.wt.IWebNature
	 * @param project
	 *            com.ibm.itp.core.api.resources.IProject
	 */
	public static StaticWebNatureRuntime getRuntime(IProject project) {
		try {
			StaticWebNatureRuntime a = (StaticWebNatureRuntime) project.getNature(ISimpleWebNatureConstants.STATIC_NATURE_ID);
			return a;
		} catch (CoreException e) {
			return null;
		}
	}


	public IPath getBasicWebModulePath() {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		WebSettings webSettings = getWebSettings();
//		String name = webSettings.getWebContentName();
		String name = null;
		if (name == null) {
			int version = getVersion();
			// If created in V5 or beyond
			if (version != -1 && version >= 500)
				return ISimpleWebNatureConstants.WEB_MODULE_PATH_;

			return ISimpleWebNatureConstants.WEB_MODULE_PATH_V4;
		}
		return new Path(name);
	}


	public IPath getWebModulePath() {
		return getProjectPath().append(getBasicWebModulePath());
	}


	/**
	 * Insert the method's description here. Creation date: (10/30/2001 5:25:06 PM)
	 * 
	 * @return boolean
	 * @deprecated
	 */
	public int getWebNatureType() {

		return ISimpleWebNatureConstants.STATIC_WEB_PROJECT;
	}


	/*
	 * Return the current version number.
	 */
	public static int getCurrentVersion() {
		return CURRENT_VERSION;
	}


	/*
	 * Return the version number stored in the web settings file. The version number is used to
	 * determine when the web project was created (i.e., under what product version). The current
	 * version number does not necessarily change with each product version -- it's only changed
	 * when it becomes necessary to distinguish a new version from a prior version.
	 */
	public int getVersion() {

		if (fVersion == -1) {
			try {
//				To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead				
//				String versionString = getWebSettings().getVersion();
				String versionString = null;
				if (versionString != null)
					fVersion = Integer.parseInt(versionString);
			} catch (NumberFormatException e) {
				//Ignore
			}
		}
		return fVersion;
	}


	public IPath getWebSettingsPath() {
		return getProjectPath().append(ISimpleWebNatureConstants.WEBSETTINGS_FILE_NAME);
	}

	/**
	 * Return whether or not the project has a runtime created on it.
	 * 
	 * @return boolean
	 * @param project
	 *            com.ibm.itp.core.api.resources.IProject
	 */
	public static boolean hasRuntime(IProject project) {
		try {
			return project.hasNature(ISimpleWebNatureConstants.STATIC_NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}

	/**
	 * Insert the method's description here. Creation date: (10/31/2001 5:32:12 PM)
	 * 
	 * @param info
	 *            com.ibm.iwt.webproject.WebProjectInfo
	 * @exception org.eclipse.core.runtime.CoreException
	 *                The exception description.
	 */
/*	public void initializeFromInfo(WebProjectInfo info) throws org.eclipse.core.runtime.CoreException {
		int natureType = info.getWebProjectType();

		WebSettings webSettings = getWebSettings();
		webSettings.setProjectType(convertNatureTypeToString(natureType));
		webSettings.setWebContentName(info.getWebContentName());
		webSettings.write();

		createFolders();
		//		super.initializeFromInfo(info);
	}*/

	/**
	 * Insert the method's description here. Creation date: (11/1/2001 2:25:22 PM)
	 * 
	 * @param builderID
	 *            java.lang.String
	 * @exception org.eclipse.core.runtime.CoreException
	 *                The exception description.
	 */
	protected void removeFromBuildSpec(String builderID) throws org.eclipse.core.runtime.CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		boolean found = false;
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID)) {
				found = true;
				break;
			}
		}
		if (!found) {
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			ICommand[] newCommands = new ICommand[commands.length + 1];
			System.arraycopy(commands, 0, newCommands, 1, commands.length);
			newCommands[0] = command;
			IProjectDescription desc = getProject().getDescription();
			desc.setBuildSpec(newCommands);
			getProject().setDescription(desc, null);
		}

	}

	protected void resetWebSettings() {
//		fWebSettings = null;
		contextRoot = null;

	}


	public void contributeToNature(EMFWorkbenchContext aNature) {
		//		if (emfNature == aNature) return;
		//		super.contributeToNature(aNature);
		//		WorkbenchURIConverter converter = (WorkbenchURIConverter)
		// aNature.getContext().getURIConverter();
		//		converter.addInputContainer(getProject());
	}

	protected String convertNatureTypeToString(int type) {
		if (type == ISimpleWebNatureConstants.STATIC_WEB_PROJECT)
			return PROJECTTYPE_STATIC_VALUE;

		return PROJECTTYPE_J2EE_VALUE;
	}

	/**
	 * Set the web nature's type to either Static (ISimpleWebNatureConstants.STATIC_WEB_NATURE) or J2EE
	 * (ISimpleWebNatureConstants.J2EE_WEB_NATURE)
	 * 
	 * @param newIsStaticWebProject
	 *            boolean
	 * @deprecated
	 */
	public void setWebNatureType(int natureType) throws CoreException {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		getWebSettings().setProjectType(convertNatureTypeToString(natureType));
//		getWebSettings().write();

	}


	public String[] getFeatureIds() {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		WebSettings settings = getWebSettings();
//		featureIds = settings.getFeatureIds();
		if (featureIds == null)
			featureIds = new String[0];

		return featureIds;
	}

	public void setFeatureIds(String[] featureIds) throws CoreException {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		WebSettings webSettings = getWebSettings();
//		webSettings.setFeatureIds(featureIds);
//		webSettings.write();
	}

	/**
	 * Configures the project with this nature.
	 * 
	 * @see IProjectNature#configure()
	 */
	public void primConfigure() throws CoreException {
		//		super.primConfigure();

		// this order is depended upon (see RATLC00855322)
		addToFrontOfBuildSpec(WSTWebPlugin.VALIDATION_BUILDER_ID);
//		addToFrontOfBuildSpec(LINKS_BUILDER_ID);
	}

	public void setContextRoot(String newContextRoot) throws CoreException {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		getWebSettings().setContextRoot(newContextRoot);
//		getWebSettings().write();
		contextRoot = newContextRoot;
	}

	public void removeFeatureId(String featureId) throws CoreException {
//		To do : Needs rework  for flexibile project ModuleCore.getFirstArtifactEditForRead		
//		WebSettings webSettings = getWebSettings();
//		webSettings.removeFeatureId(featureId);
//		webSettings.write();
	}


	/**
	 * Configures the project with this nature. This is called by <code>IProject.addNature</code>
	 * and should not be called directly by clients. The nature extension id is added to the list of
	 * natures on the project by <code>IProject.addNature</code>, and need not be added here.
	 * 
	 * All subtypes must call super.
	 * 
	 * @exception CoreException
	 *                if this method fails.
	 */
	public void configure() throws org.eclipse.core.runtime.CoreException {
		primConfigure();

	}

	/**
	 * Gets the deployable.
	 * 
	 * @return Returns a IDeployable
	 */
	public IModule getModule() {
		return module;
	}

	/**
	 * Sets the deployable.
	 * 
	 * @param deployable
	 *            The deployable to set
	 */
	public void setModule(IModule module) {
		this.module = module;
	}

	/**
	 * Returns the project to which this project nature applies.
	 * 
	 * @return the project handle
	 */
	public org.eclipse.core.resources.IProject getProject() {
		return project;
	}

	/**
	 * Sets the project to which this nature applies. Used when instantiating this project nature
	 * runtime. This is called by <code>IProject.addNature</code> and should not be called
	 * directly by clients.
	 * 
	 * @param project
	 *            the project to which this nature applies
	 */
	public void setProject(org.eclipse.core.resources.IProject newProject) {
		project = newProject;
		//need to be called here since getNature and createNature will not call it
		try {
			configure();
		} catch (CoreException e) {
			//Ignore
		}
	}

	/**
	 * Create a folder relative to the project based on aProjectRelativePathString.
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 *                The exception description.
	 */
	public IFolder createFolder(String aProjectRelativePathString) throws CoreException {
		if (aProjectRelativePathString != null && aProjectRelativePathString.length() > 0)
			return createFolder(new Path(aProjectRelativePathString));
		return null;
	}

	/**
	 * Create a folder relative to the project based on aProjectRelativePathString.
	 * 
	 * @exception com.ibm.itp.core.api.resources.CoreException
	 *                The exception description.
	 */
	public IFolder createFolder(IPath aProjectRelativePath) throws CoreException {
		if (aProjectRelativePath != null && !aProjectRelativePath.isEmpty()) {
			IFolder folder = getWorkspace().getRoot().getFolder(getProjectPath().append(aProjectRelativePath));
			if (!folder.exists())
				folder.create(true, true, null);
			return folder;
		}
		return null;
	}

	/**
	 * Adds a nauture to a project
	 */
	protected static void addNatureToProject(IProject proj, String natureId) throws CoreException {
		ProjectUtilities.addNatureToProject(proj, natureId);
	}

	/**
	 * Return the full path of the project.
	 */
	protected IPath getProjectPath() {
		return getProject().getFullPath();
	}

	public IWorkspace getWorkspace() {
		return getProject().getWorkspace();
	}


	/**
	 * @see IBaseWebNature#isJ2EE()
	 */
	public boolean isJ2EE() {
		return false;
	}

	/**
	 * @see IBaseWebNature#isStatic()
	 */
	public boolean isStatic() {
		return true;
	}

}