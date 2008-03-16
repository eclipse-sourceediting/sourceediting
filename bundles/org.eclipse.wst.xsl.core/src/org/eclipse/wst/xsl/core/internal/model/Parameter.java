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
 * TODO: Add JavaDoc
 * @author Dough Satchwell
 *
 */
public class Parameter extends SourceArtifact
{
	final String name;
	final String select;
	boolean value;

	/**
	 * TODO: Add JavaDoc
	 * @param sf
	 * @param name
	 * @param select
	 */
	public Parameter(SourceFile sf, String name, String select)
	{
		super(sf);
		this.name = name;
		this.select = select;
		value = select != null;
	}

	/**
	 * TODO: Add JavaDoc
	 * @param b
	 */
	public void setValue(boolean b)
	{
		value = b;
	}

	/**
	 * TODO: add JavaDoc
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * TODO: Add JavaDoc
	 * @return
	 */
	public String getSelect()
	{
		return select;
	}

	/**
	 * TODO: Add JavaDoc
	 * @return
	 */
	public boolean isValue()
	{
		return value;
	}
}
