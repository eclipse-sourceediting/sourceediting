/*******************************************************************************
 * Copyright (c) 2009 Jesper Steen Moeller
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IRewriteEntry;

public class RewriteEntry extends TypedCatalogElement implements IRewriteEntry, Cloneable 
{
	String startString;
	String rewritePrefix;

	protected RewriteEntry(int anEntryType) 
	{
		super(ICatalogElement.TYPE_REWRITE, anEntryType);
	}

	public String getStartString() 
	{
		return startString;
	}

	public void setStartString(String startString) 
	{
		this.startString = startString;
	}

	public String getRewritePrefix() 
	{
		return rewritePrefix;
	}

	public void setRewritePrefix(String rewritePrefix) 
	{
		this.rewritePrefix = rewritePrefix;
	}

	public Object clone() 
	{
		RewriteEntry entry = (RewriteEntry) super.clone();
		entry.setRewritePrefix(rewritePrefix);
		entry.setStartString(startString);
		return entry;
	}
}
