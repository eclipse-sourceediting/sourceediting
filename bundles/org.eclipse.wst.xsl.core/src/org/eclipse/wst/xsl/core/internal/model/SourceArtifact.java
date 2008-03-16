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

import org.eclipse.core.runtime.PlatformObject;

/**
 * TODO: Add JavaDoc
 * @author Doug Satchwell
 *
 */
public class SourceArtifact extends PlatformObject
{
	final SourceFile parentSourceFile;
	int lineNumber;
	int columnNumber;
	
	/**
	 * TODO: Add JavaDoc
	 * @param parentSourceFile
	 */
	public SourceArtifact(SourceFile parentSourceFile)
	{
		this.parentSourceFile = parentSourceFile;
	}

	/**
	 * TODO: Add JavaDoc
	 * @param lineNumber
	 */
	public void setLineNumber(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	/**
	 * TODO: Add JavaDoc
	 * @param columnNumber
	 */
	public void setColumnNumber(int columnNumber)
	{
		this.columnNumber = columnNumber;
	}

	/**
	 * TODO: Add JavaDoc
	 * @return
	 */
	public SourceFile getParentSourceFile()
	{
		return parentSourceFile;
	}

	/**
	 * TODO: Add JavaDoc
	 * @return
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

	/**
	 * TODO: Add JavaDoc
	 * @return
	 */
	public int getColumnNumber()
	{
		return columnNumber;
	}
}
