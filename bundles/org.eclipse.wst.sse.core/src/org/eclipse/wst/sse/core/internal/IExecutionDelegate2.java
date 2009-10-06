/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.sse.core.internal;

import org.eclipse.core.runtime.ISafeRunnable;

/**
 * @noimplement
 */
public interface IExecutionDelegate2 extends IExecutionDelegate {
	void execute(ISafeRunnable runnable);
}
