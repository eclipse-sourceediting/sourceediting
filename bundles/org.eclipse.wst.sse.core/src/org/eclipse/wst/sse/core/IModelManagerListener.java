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



/**
 * Interface for those wanting to listen to events fired by the model manager.
 */
public interface IModelManagerListener {

	/**
	 * One or more models are about to be changed. This typically is initiated by
	 * one client of the model (such as LinksBuilder), to signal that several models
	 * are about to be changed. These models might be interrelated, so a listener
	 * (such as FrameManger) will know to a "massive" change is about to take place.
	 */
	void modelsAboutToBeChanged();

	/**
	 * Signals that the changes foretold by modelsAboutToBeChanged have been made.
	 * A typical use might be to refresh, or to resume processing that was suspended
	 * as a result of modelsAboutToBeChanged.
	 */
	void modelsChanged();
}
