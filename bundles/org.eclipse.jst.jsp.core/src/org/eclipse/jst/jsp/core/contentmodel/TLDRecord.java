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
package org.eclipse.jst.jsp.core.contentmodel;

import org.eclipse.core.runtime.IPath;


public class TLDRecord implements ITaglibRecord {
	IPath location;
	String uri;

	public short getRecordType() {
		return ITaglibRecord.TLD;
	}

	/**
	 * @return Returns the filesystem location.
	 */
	public IPath getLocation() {
		return location;
	}

	/**
	 * @return Returns the uri.
	 */
	public String getURI() {
		return uri;
	}
}
