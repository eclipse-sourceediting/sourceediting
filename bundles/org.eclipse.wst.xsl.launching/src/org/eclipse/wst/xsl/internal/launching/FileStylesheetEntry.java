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

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.xsl.launching.IStylesheetEntry;

public class FileStylesheetEntry implements IStylesheetEntry
{
	private final IPath path;

	public FileStylesheetEntry(IPath path)
	{
		this.path = path;
	}

	public IPath getPath()
	{
		return path;
	}
}
