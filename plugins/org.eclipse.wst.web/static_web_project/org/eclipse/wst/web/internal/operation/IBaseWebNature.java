/*
 * Created on Dec 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.web.internal.operation;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;

/**
 * @author dfholt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IBaseWebNature extends IProjectNature {
	/**
	 * Insert the method's description here. Creation date: (10/31/2001 2:27:17 PM)
	 * 
	 * @param path
	 * @param s
	 *            java.lang.String
	 */
	void createFile(IPath path, String s) throws org.eclipse.core.runtime.CoreException;

	/**
	 * Insert the method's description here. Creation date: (10/16/2001 1:42:50 PM)
	 * 
	 * @return java.lang.String
	 */
	String getContextRoot();


	/**
	 * This is a proxy method to enable invoking getModuleServerRoot() through the IBaseWebNature
	 * interface. The method can't be defined directly on the interface since it's implemented by
	 * AbstractJavaMOFNatureRuntime
	 * 
	 * Creation date: (10/23/2001 2:24:52 PM)
	 * 
	 * @return org.eclipse.core.resources.IContainer
	 */
	IContainer getRootPublishableFolder();

	/**
	 * @return String
	 */
	String getModuleServerRootName();

	void setModuleServerRootName(String name) throws CoreException;

	/**
	 * @return IContainer
	 */
	IContainer getModuleServerRoot();

	/**
	 * @return org.eclipse.core.runtime.IPath
	 */
	IPath getWebSettingsPath();

	/**
	 * Creation date: (10/30/2001 5:30:42 PM)
	 * 
	 * @return boolean
	 * @deprecated
	 */
	int getWebNatureType();

	/**
	 * Insert the method's description here. Creation date: (10/30/2001 5:30:10 PM)
	 * 
	 * @param type
	 *            boolean
	 * @deprecated
	 */
	void setWebNatureType(int type) throws CoreException;

	/**
	 * Return true if J2EE nature.
	 * 
	 * @return boolean
	 */
	boolean isJ2EE();

	/**
	 * Return true if Static nature.
	 * 
	 * @return boolean
	 */
	boolean isStatic();

	/**
	 * Return the deployable object for use by the server tooling
	 */
	IModule getModule();
	/**
	 * Insert the method's description here. Creation date: (10/23/2001 5:50:15 PM)
	 * 
	 * @return org.eclipse.core.runtime.IPath
	 */
	IPath getWebModulePath();
	/**
	 * @return org.eclipse.core.runtime.IPath
	 */
	IPath getBasicWebModulePath();
	/**
	 * Set the deployable object for use by the server tooling
	 */
	void setModule(IModule module);

	void setContextRoot(String contextRoot) throws CoreException;

	void setFeatureIds(String[] featureIds) throws CoreException;

	String[] getFeatureIds();
}
