/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal.provisional.tasks;

import org.eclipse.core.resources.IMarker;

/**
 * Simple representation of the values that make up a Task Tag
 */
public final class TaskTag {

	public static final int PRIORITY_HIGH = IMarker.PRIORITY_HIGH;
	public static final int PRIORITY_LOW = IMarker.PRIORITY_LOW;
	public static final int PRIORITY_NORMAL = IMarker.PRIORITY_NORMAL;

	/**
	 * this task tag's priority
	 */
	private int fPriority = PRIORITY_NORMAL;
	
	/**
	 * this task tag's "tagging" text
	 */
	private String fTag = null;

	public TaskTag(String tag, int priority) {
		super();
		fTag = tag;
		fPriority = priority;
	}

	public int getPriority() {
		return fPriority;
	}

	public String getTag() {
		return fTag;
	}

	public String toString() {
		return getTag() + ":" + getPriority(); //$NON-NLS-1$
	}
}
