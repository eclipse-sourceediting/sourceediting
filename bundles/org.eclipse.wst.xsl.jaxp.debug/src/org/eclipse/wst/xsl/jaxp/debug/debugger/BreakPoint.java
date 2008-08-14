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
package org.eclipse.wst.xsl.jaxp.debug.debugger;

/**
 * A line number in a file.
 * 
 * @author Doug Satchwell
 */
public class BreakPoint
{
	private String file;
	private final int line;

	/**
	 * Construct a new instance of this for the given file and line number.
	 * 
	 * @param file the file path
	 * @param line the line number
	 */
	public BreakPoint(String file, int line)
	{
		this.file = file;
		this.line = line;
	}

	/**
	 * Get the file location.
	 * 
	 * @return the file
	 */
	public String getFile()
	{
		return file;
	}

	/**
	 * Get the line number
	 * 
	 * @return the line number
	 */
	public int getLine()
	{
		return line;
	}

	public int hashCode()
	{
		int hash = 3 * file.hashCode() + 5 * line;
		return hash;
	}

	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (obj instanceof BreakPoint)
		{
			BreakPoint b = (BreakPoint) obj;
			return b.file.equals(file) && b.line == line;
		}
		return false;
	}

	public String toString()
	{
		return file + " " + line; //$NON-NLS-1$
	}
}
