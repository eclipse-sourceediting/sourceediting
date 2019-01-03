/*******************************************************************************
 * Copyright (c) 2009 Jesper Steen Moeller
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;


public class TypedCatalogElement extends CatalogElement 
{
	protected int entryType;

	public TypedCatalogElement(int type, int entryType) 
	{
		super(type);
		this.entryType = entryType;
	}

	final public void setEntryType(int value) 
	{
	    entryType = value;
	}

	final public int getEntryType() 
	{
	    return entryType;
	}
	
	public Object clone() 
	{
		TypedCatalogElement temp = (TypedCatalogElement)super.clone();
		temp.setEntryType(this.getEntryType());
		return temp;
	}
}
