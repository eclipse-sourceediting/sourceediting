/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.taglib;

import org.eclipse.core.resources.IResourceDelta;

/**
 * Describes changes to the known records within the TaglibIndex.
 * 
 * <p>
 * @noimplement This interface is not intended to be implemented by clients.
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
