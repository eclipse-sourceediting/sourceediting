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

import java.net.URL;

public class URLRecord implements ITaglibRecord {
	URL url;
	String uri;
	String baseLocation;

	public boolean equals(Object obj) {
		if (!(obj instanceof URLRecord))
			return false;
		return ((URLRecord) obj).baseLocation.equals(baseLocation) || ((URLRecord) obj).uri.equals(uri) || ((URLRecord) obj).url.equals(url);
	}

	public String getBaseLocation() {
		return baseLocation;
	}

	public URLRecord() {
		super();
	}

	public int getRecordType() {
		return ITaglibRecord.URL;
	}

	/**
	 * @return Returns the uri.
	 */
	public String getURI() {
		return uri;
	}

	/**
	 * @return Returns the URL.
	 */
	public URL getURL() {
		return url;
	}

	public String toString() {
		return "URLRecord: " + baseLocation + " <-> " + uri; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
