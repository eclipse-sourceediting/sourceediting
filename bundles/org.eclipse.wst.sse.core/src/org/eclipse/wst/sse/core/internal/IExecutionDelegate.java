/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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

import org.eclipse.core.runtime.ISafeRunnable;

/**
 * An abstraction that allows even processing to be performed in a different
 * context, e.g. a different Thread, if needed.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IExecutionDelegate {

	void execute(ISafeRunnable runnable);
}
