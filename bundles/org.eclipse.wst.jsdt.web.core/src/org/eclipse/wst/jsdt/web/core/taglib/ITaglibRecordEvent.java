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
package org.eclipse.wst.jsdt.web.core.taglib;

import org.eclipse.core.resources.IResourceDelta;

/**
 * Describes changes to the known records within the TaglibIndex.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ITaglibRecordEvent {

	/**
	 * @return the record that was changed
	 */
	ITaglibRecord getTaglibRecord();

	/**
	 * @return the type of change, one of ADDED, CHANGED, or REMOVED
	 */
	int getType();

	int ADDED = IResourceDelta.ADDED;
	int CHANGED = IResourceDelta.CHANGED;
	int REMOVED = IResourceDelta.REMOVED;
}
