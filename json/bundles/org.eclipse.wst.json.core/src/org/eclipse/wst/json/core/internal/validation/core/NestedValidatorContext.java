/*******************************************************************************
 * Copyright (c) 2006, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext
 *                                           modified in order to process JSON Objects.     
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.validation.core;

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
