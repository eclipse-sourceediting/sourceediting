/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.launching;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public interface IStylesheetEntry
{
	/**
	 * Type identifier for project file workspace files.
	 */
	int WORKSPACE_FILE = 1;

	/**
	 * Type identifier for external files
	 */
	int EXTERNAL_FILE = 2;

	/**
	 * Type identifier for URL's
	 */
	int URL = 3;

	int getType();

	String getMemento() throws CoreException;

	IPath getPath();

	void addParameter(IStylesheetParameter parameter);

	void removeParameter(IStylesheetParameter parameter);

	List getParameters();
}
