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
package org.eclipse.wst.sse.ui.internal.correction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class ContributedProcessorDescriptor {
	private static final String CLASS = "class"; //$NON-NLS-1$

	private IConfigurationElement fConfigurationElement;
	private Object fProcessorInstance;

	public ContributedProcessorDescriptor(IConfigurationElement element) {
		fConfigurationElement = element;
		fProcessorInstance = null;
	}

	public Object getProcessor() throws CoreException {
		if (fProcessorInstance == null && fConfigurationElement != null) {
			fProcessorInstance = fConfigurationElement.createExecutableExtension(CLASS);
		}
		return fProcessorInstance;
	}
}
