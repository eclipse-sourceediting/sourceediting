/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;


public class TagDirRecord implements ITaglibRecord {
	IPath location;
	String shortName;
	// a List holding Strings of .tag and .tagx filenames relative to the
	// tagdir's location
	List tags = new ArrayList(0);

	/**
	 * @return Returns the location.
	 */
	public IPath getLocation() {
		return location;
	}

	public short getRecordType() {
		return ITaglibRecord.TAGDIR;
	}

	/**
	 * @return Returns the shortName.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return Returns the tags.
	 */
	public String[] getTags() {
		return (String[]) tags.toArray(new String[tags.size()]);
	}
}
