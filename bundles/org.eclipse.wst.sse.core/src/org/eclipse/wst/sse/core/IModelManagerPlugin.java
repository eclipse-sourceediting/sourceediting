/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
/*
 * Created on Aug 6, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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

	public abstract ModelHandlerRegistry getModelHandlerRegistry();

	public abstract IModelManager getModelManager();

	public Preferences getPluginPreferences();
}
