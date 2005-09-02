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

import java.util.List;

import org.eclipse.core.runtime.IPath;

/**
 * TaglibRecord for a JAR file, also includes the records for any .tld files
 * contained within it.
 */

public class JarRecord implements ITaglibRecord {
	IPath location;
	String prefix;
	List urlRecords;
	boolean has11TLD;

	public boolean equals(Object obj) {
		if (!(obj instanceof JarRecord))
			return false;
		return ((JarRecord) obj).location.equals(location);
	}

	/**
	 * @return Returns the location.
	 */
	public IPath getLocation() {
		return location;
	}

	/**
	 * @return Returns the recommended/default prefix if one was given.
	 */
	public String getPrefix() {
		return prefix;
	}

	public int getRecordType() {
		return ITaglibRecord.JAR;
	}

	/**
	 * 
	 */
	public List getURLRecords() {
		return urlRecords;
	}

	public String toString() {
		return "JarRecord: " + location + " <-> " + urlRecords; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
