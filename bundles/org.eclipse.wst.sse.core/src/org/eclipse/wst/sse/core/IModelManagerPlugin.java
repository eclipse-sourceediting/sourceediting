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

package org.eclipse.wst.sse.core;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;


/**
 * @deprecated use StructuredModelManager instead
 */
public interface IModelManagerPlugin {
	//  private static ModelManagerPlugin instance;
	public final static String ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$

	/**
	 * @deprecated use ModelHandlerRegistry.getInstance() instead
	 */
	public abstract ModelHandlerRegistry getModelHandlerRegistry();

	/**
	 * @deprecated use StructuredModelManager.getInstance().getModelManager() instead
	 */
	public abstract IModelManager getModelManager();

	public Preferences getPluginPreferences();
}
