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

import org.eclipse.core.resources.IResourceDelta;

public interface ITaglibRecordEvent {
	ITaglibRecord getTaglibRecord();

	int getType();

	int ADDED = IResourceDelta.ADDED;
	int CHANGED = IResourceDelta.CHANGED;
	int REMOVED = IResourceDelta.REMOVED;
}
