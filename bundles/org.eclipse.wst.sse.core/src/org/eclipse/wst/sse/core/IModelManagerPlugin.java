/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.core;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;


/**
 * @author davidw
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IModelManagerPlugin {
	//  private static ModelManagerPlugin instance;
	public final static String ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$

	public abstract IModelManager getModelManager();

	public abstract ModelHandlerRegistry getModelHandlerRegistry();

	public Preferences getPluginPreferences();
}
