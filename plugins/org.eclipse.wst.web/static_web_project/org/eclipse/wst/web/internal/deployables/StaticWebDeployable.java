/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.internal.deployables;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.util.IStaticWeb;
import org.eclipse.wst.server.core.util.ProjectModule;

public class StaticWebDeployable extends ProjectModule implements IStaticWeb {

	protected IVirtualComponent component = null;
	 
	public StaticWebDeployable(IProject project, IVirtualComponent component) {
		super(project);
		this.component = component;
	}

	public String getFactoryId() {
		return StaticWebDeployableFactory.getFactoryId();
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
		return true;
	}

	public String getContextRoot() {
		Properties props = component.getMetaProperties();
		if(props.containsKey("context-root")) //$NON-NLS-1$
			return props.getProperty("context-root"); //$NON-NLS-1$
		return component.getName();
	}

	public String getType() {
		return IModuleConstants.WST_WEB_MODULE;
	}

	public String getVersion() {
		return "1.0"; //$NON-NLS-1$
	}

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.IModule#getModuleType()
     */
    public IModuleType getModuleType() {
		return new IModuleType() {

			public String getId() {
				return getType();
			}

			public String getName() {
				return getModuleTypeName();
			}

			public String getVersion() {
				return getModuleTypeVersion();
			}
		};

	}
    
    public String getModuleTypeName(){
        return getName();
    }
    
    public String getModuleTypeVersion(){
        return getVersion();
    }

    /**
     * Returns the child modules of this module.
     * 
     * @return org.eclipse.wst.server.core.model.IModule[]
     */
    public IModule[] getChildModules() {
        List list = new ArrayList();
//
//        if (this.archives != null) {
//            int size = this.archives.length;
//            for (int i = 0; i < size; i++)
//                list.add(this.archives[i]);
//        }
//
        IModule[] children = new IModule[list.size()];
        list.toArray(children);
        return children;
    }

}