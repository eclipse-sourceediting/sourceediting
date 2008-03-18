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


/**
 * @author Doug Satchwell
 * 
 */
public class Variable extends SourceArtifact
{
	final String name;
	final String select;

	public Variable(SourceFile parentSourceFile, String name, String select)
	{
		super(parentSourceFile);
		this.name = name;
		this.select = select;
	}

	public String getName()
	{
		return name;
	}
	
	public String getSelect()
	{
		return select;
	}
}
