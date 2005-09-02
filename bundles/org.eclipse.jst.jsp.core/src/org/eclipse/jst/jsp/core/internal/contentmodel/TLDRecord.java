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

import org.eclipse.core.runtime.IPath;


/**
 * TaglibRecord for a standalone .tld file
 */
public class TLDRecord implements ITaglibRecord {
	IPath location;
	String prefix;
	String uri;

	public boolean equals(Object obj) {
		if (!(obj instanceof TLDRecord))
			return false;
		return ((TLDRecord) obj).location.equals(location);
	}

	/**
	 * @return Returns the filesystem location.
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
		return ITaglibRecord.TLD;
	}

	/**
	 * @return Returns the uri.
	 */
	public String getURI() {
		return uri;
	}

	public String toString() {
		return "TLDRecord: " + location + " <-> " + uri; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
