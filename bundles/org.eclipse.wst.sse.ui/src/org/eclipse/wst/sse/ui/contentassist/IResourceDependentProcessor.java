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
package org.eclipse.wst.sse.ui.contentassist;

import org.eclipse.core.resources.IResource;

/**
 * Interface for classes that require an IResource to operate properly (eg.
 * some ContentAssistProcessors)
 * 
 * @author pavery
 */
public interface IResourceDependentProcessor {
	void initialize(IResource resource);
}
