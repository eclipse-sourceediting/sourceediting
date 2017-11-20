/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.core.internal.provisional;

import org.eclipse.wst.sse.core.internal.model.ModelLifecycleEvent;

/**
 * This is an early version of a class that may change over the next few
 * milestones.
 */

public interface IModelLifecycleListener {

	void processPostModelEvent(ModelLifecycleEvent event);

	void processPreModelEvent(ModelLifecycleEvent event);
}
