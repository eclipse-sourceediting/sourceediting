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
package org.eclipse.wst.xsl.jaxp.launching.internal;

import org.eclipse.wst.xsl.jaxp.launching.IOutputProperty;

public class OutputProperty implements IOutputProperty
{
	private final String name;
	private final String desc;

	public OutputProperty(String key, String desc)
	{
		this.name = key;
		this.desc = desc;
	}

	public String getDescription()
	{
		return desc;
	}

	public String getURI()
	{
		return name;
	}

}
