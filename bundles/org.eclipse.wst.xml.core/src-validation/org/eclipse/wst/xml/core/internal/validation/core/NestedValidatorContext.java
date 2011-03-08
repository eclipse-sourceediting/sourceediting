/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation.core;

import org.eclipse.core.resources.IProject;


/**
 * A context class for validators to be able to determine the context of
 * given validation session. Currently this class is only used to identify
 * the unique context.
 */
public class NestedValidatorContext 
{
	private IProject fProject;

	public void setProject(IProject project) {
		fProject = project;
	}

	public IProject getProject() {
		return fProject;
	}
}
