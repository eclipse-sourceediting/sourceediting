/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
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

/**
 * A listener for changes in the index's records.
 */
public interface ITaglibIndexListener {

	/**
	 * Notifies this listener that a change in the TaglibIndex, described by
	 * an ITaglibIndexDelta, has occurred
	 * 
	 * @param delta
	 *            the delta of changes
	 */
	void indexChanged(ITaglibIndexDelta delta);
}
