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
package org.eclipse.wst.xsl.internal.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.xsl.launching.IStylesheetEntry;
import org.eclipse.wst.xsl.launching.IStylesheetParameter;

public class FileStylesheetEntry implements IStylesheetEntry
{
	private final IPath path;
	private final List<IStylesheetParameter> parameters = new ArrayList<IStylesheetParameter>();

	public FileStylesheetEntry(IPath path)
	{
		this.path = path;
	}

	public IPath getPath()
	{
		return path;
	}

	public String getMemento() throws CoreException
	{
		return null;
	}

	public int getType()
	{
		return WORKSPACE_FILE;
	}

	public List<IStylesheetParameter> getParameters()
	{
		return parameters;
	}

	public void addParameter(IStylesheetParameter parameter)
	{
		parameters.add(parameter);
	}

	public void removeParameter(IStylesheetParameter parameter)
	{
		parameters.remove(parameter);
	}
}
