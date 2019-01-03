/*******************************************************************************
 * Copyright (c) 2008, 2018 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching.internal;

import org.eclipse.wst.xsl.jaxp.launching.ITransformerFactory;

public class TransformerFactory implements ITransformerFactory
{
	private final String factoryClass;
	private final String name;
	
	public TransformerFactory(String name,String factoryClass)
	{
		super();
		this.factoryClass = factoryClass;
		this.name = name;
	}

	public String getFactoryClass()
	{
		return factoryClass;
	}

	public String getName()
	{
		return name;
	}

}
