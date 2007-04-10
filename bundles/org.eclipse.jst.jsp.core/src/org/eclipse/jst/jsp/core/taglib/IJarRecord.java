/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.taglib;

import org.eclipse.core.runtime.IPath;

/**
 * A record to a .jar file directly referencable as a tag library.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 1.0
 */

public interface IJarRecord extends ITaglibRecord {
	/**
	 * @return Returns the location of the .jar in the file-system.
	 */
	public IPath getLocation();

	/**
	 * @return Returns the recommended/default prefix if one was given.
	 */
	public String getShortName();

	/**
	 * @deprecated - use the descriptor's URI value
	 * @return Returns the uri.
	 */
	String getURI();
}
