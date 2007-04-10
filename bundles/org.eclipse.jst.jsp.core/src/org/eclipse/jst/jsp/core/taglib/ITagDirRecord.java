/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
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
 * A record representing a folder of .tag/.tagx files
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 1.0
 */
public interface ITagDirRecord extends ITaglibRecord {
	/**
	 * @return Returns the path within the workspace to this directory.
	 */
	IPath getPath();
}
