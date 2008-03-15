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
package org.eclipse.wst.xsl.core.internal.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;

public class Include extends SourceArtifact
{
	public static final int INCLUDE = 1;
	public static final int IMPORT = 2;
	private final String href;
	private final int type;
	
	public Include(SourceFile parentSourceFile, String href, int type)
	{
		super(parentSourceFile);
		this.href = href;
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}
	
/*	public SourceFile getSourceFile()
	{
		// TODO this depends on the project settings and URIResolver
		IFile includedFile = parentSourceFile.getFile().getProject().getFile(new Path("xsl/"+href));
		return parentSourceFile.sourceFiles.get(includedFile);
	} */
}
