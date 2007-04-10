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

package org.eclipse.wst.sse.core.internal;

import org.eclipse.core.runtime.jobs.ILock;

/**
 * 
 * Not API: not to be used or implemented by clients. This is a special
 * purpose interface to help guard some threading issues betweeen model and
 * document. Will be changed soon.
 *  
 */

public interface ILockable {

	ILock getLockObject();

}
