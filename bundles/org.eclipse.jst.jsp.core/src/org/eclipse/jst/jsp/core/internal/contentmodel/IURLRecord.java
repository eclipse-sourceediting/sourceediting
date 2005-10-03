/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel;

import java.net.URL;

public interface IURLRecord extends ITaglibRecord {

	String getBaseLocation();

	/**
	 * @return Returns the recommended/default prefix if one was given.
	 */
	String getShortName();

	/**
	 * @return Returns the uri for this TLD.
	 */
	String getURI();

	/**
	 * @return Returns the URL to this TLD.
	 */
	URL getURL();
}
