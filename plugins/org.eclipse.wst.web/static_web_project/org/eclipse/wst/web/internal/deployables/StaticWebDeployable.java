/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.internal.deployables;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.util.IStaticWeb;
import org.eclipse.wst.server.core.util.ProjectModule;
import org.eclipse.wst.web.internal.ISimpleWebNatureConstants;
import org.eclipse.wst.web.internal.SimpleWebNatureRuntimeUtilities;
import org.eclipse.wst.web.internal.operation.IStaticWebNature;

public class StaticWebDeployable extends ProjectModule implements IStaticWeb {

	public StaticWebDeployable(IProject project) {
		super(project);
		setWebNature(getStaticWebNature());
	}

	/**
	 * TODO Changed setModule to setProject to react to changes in IStaticWebNature. Verify if this change is correct.
	 * @param staticWebNature
	 */
	private void setWebNature(IStaticWebNature nature) {
		nature.setProject(getProject());
	}

	public String getFactoryId() {
		return "org.eclipse.jst.j2ee.internal.internal.internal.web.deployables.static"; //$NON-NLS-1$
	}

	/**
	 * Returns true if this deployable currently exists, and false if it has been deleted or moved
	 * and is no longer represented by this deployable.
	 * 
	 * @return boolean
	 */
	public boolean exists() {
		if (getProject() == null || !getProject().exists())
			return false;
		try {
			return (this.project.hasNature(ISimpleWebNatureConstants.STATIC_NATURE_ID));
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}

	protected IStaticWebNature getStaticWebNature() {
		return SimpleWebNatureRuntimeUtilities.getStaticRuntime(this.project);
	}

	public String getContextRoot() {
		IStaticWebNature nature = getStaticWebNature();
		if (nature != null)
			return nature.getContextRoot();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.util.ProjectModule#getRootFolder()
	 */
	public IPath getRootFolder() {
		// TODO Auto-generated method stub
		return getStaticWebNature().getRootPublishableFolder().getProjectRelativePath();
	}

	public String getType() {
		return "web.static"; //$NON-NLS-1$
	}

	public String getVersion() {
		return "1.0"; //$NON-NLS-1$
	}

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.IModule#validate(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus validate(IProgressMonitor monitor) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.IModule#getModuleType()
     */
    public IModuleType getModuleType() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.IModule#getChildModules(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IModule[] getChildModules(IProgressMonitor monitor) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }
}