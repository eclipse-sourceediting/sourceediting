/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;
import org.eclipse.wst.sse.core.internal.model.ModelManagerImpl;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.osgi.framework.Bundle;

/**
 * Class to allow access to properly configured implementors of IModelManager.
 * 
 * @since  1.5  org.eclipse.wst.sse.core
 */
final public class StructuredModelManager {
	/**
	 * Do not allow instances to be created.
	 */
	private StructuredModelManager() {
		super();
	}

	/**
	 * Provides access to the instance of IModelManager. Returns null if model
	 * manager can not be created or is not valid (such as, when workbench is
	 * shutting down).
	 * 
	 * @return IModelManager - returns the one model manager for structured
	 *         models or null if the owning bundle is neither active nor
	 *         starting.
	 */
	public static IModelManager getModelManager() {
		boolean isReady = false;
		IModelManager modelManager = null;
		while (!isReady) {
			Bundle localBundle = Platform.getBundle(SSECorePlugin.ID);
			int state = localBundle.getState();
			if (state == Bundle.ACTIVE) {
				isReady = true;
				// getInstance is a synchronized static method.
				modelManager = ModelManagerImpl.getInstance();
			}
			else if (state == Bundle.STARTING) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignore, just loop again
				}
			}
			else if (state == Bundle.STOPPING || state == Bundle.UNINSTALLED) {
				isReady = true;
				modelManager = null;
			}
			else {
				// not sure about other states, 'resolved', 'installed'
				isReady = true;
				modelManager = null;
			}
		}
		return modelManager;
	}
}
