/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.web.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;
import org.eclipse.wst.common.componentcore.internal.ModuleMigratorManager;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class ModuleCoreValidatorMarkerResolutions

    implements IMarkerResolutionGenerator
    
{
    public IMarkerResolution[] getResolutions( IMarker marker )
    {
        return new IMarkerResolution[] 
        { 
            new ModuleCoreMigrationResolution( marker ) 
        };
    }
    
    private class ModuleCoreMigrationResolution extends WorkbenchMarkerResolution
    
        implements IMarkerResolution,IMarkerResolution2
        
    {
        private final IMarker theMarker;
        private final String MARKERTYPE = "org.eclipse.wst.common.modulecore.ModuleCoreValidatorMarker";
//        private final IMarker[] NO_MARKERS= new IMarker[0];
        
        public ModuleCoreMigrationResolution( IMarker marker )
        {
            this.theMarker = marker;
        }
        
        public String getLabel()
        {
            return Resources.migrateMetaData;
        }

        
        
        public void run( IMarker marker )
        {
            
            IProject proj = marker.getResource().getProject();
            
            try
            {
        		ModuleMigratorManager manager = ModuleMigratorManager.getManager(proj);
        		if (!manager.isMigrating() && !ResourcesPlugin.getWorkspace().isTreeLocked()) 
        				manager.migrateOldMetaData(proj,true);
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }

		public String getDescription() {
			return Resources.migrateMetaData;
		}

		public Image getImage() {
			// TODO Auto-generated method stub
			return null;
		}

		public IMarker[] findOtherMarkers(IMarker[] markers) {
			List marks = new ArrayList();
			for (int i = 0; i < markers.length; i++) {
				IMarker marker = markers[i];
				try {
					if (marker.getType().equals(MARKERTYPE) && !(marker.equals(theMarker)))
						marks.add(marker);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return (IMarker[])marks.toArray(new IMarker[marks.size()]);
		}
     
    }

    private static final class Resources
    
        extends NLS
        
    {
    	public static String migrateMetaData;
        
        static
        {
            initializeMessages( ModuleCoreValidatorMarkerResolutions.class.getName(), 
                                Resources.class );
        }
    }
    
    
}
