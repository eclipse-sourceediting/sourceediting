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

/**
 * TaglibRecord for a taglib mapping from a web.xml file
 */

public class ServletRecord implements ITaglibRecord {
	IPath location;
	String prefix;
	List tldRecords = new ArrayList(0);

	public boolean equals(Object obj) {
		if (!(obj instanceof ServletRecord))
			return false;
		return ((ServletRecord) obj).location.equals(location);
	}

	/**
	 * @return Returns the recommended/default prefix if one was given.
	 */
	public String getPrefix() {
		return prefix;
	}

	public int getRecordType() {
		return ITaglibRecord.WEB_XML;
	}

	/**
	 * 
	 */
	public List getTLDRecords() {
		return tldRecords;
	}

	/**
	 * @return Returns the webxml.
	 */
	public IPath getWebXML() {
		return location;
	}

	public String toString() {
		return "ServletRecord: " + location + tldRecords; //$NON-NLS-1$
	}
}
