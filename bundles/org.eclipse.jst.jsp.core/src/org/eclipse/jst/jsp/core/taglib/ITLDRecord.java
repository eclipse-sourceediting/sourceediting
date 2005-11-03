/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
 * A record representing a standalone .tld file.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ITLDRecord extends ITaglibRecord {

	/**
	 * @return Returns the path within the workspace.
	 */
	IPath getPath();

	/**
	 * @return Returns the recommended/default prefix if one was given.
	 */
	String getShortName();

	/**
	 * @return Returns the uri.
	 */
	String getURI();

}
