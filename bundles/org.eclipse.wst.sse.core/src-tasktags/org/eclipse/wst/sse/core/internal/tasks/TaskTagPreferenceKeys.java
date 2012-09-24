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
package org.eclipse.wst.sse.core.internal.tasks;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.sse.core.internal.SSECorePlugin;

public final class TaskTagPreferenceKeys {
	public static final String TASK_TAG_CONTENTTYPES_IGNORED = "ignored-contentTypes"; //$NON-NLS-1$
	public static final String TASK_TAG_ENABLE = "enabled"; //$NON-NLS-1$
	public static final String TASK_TAG_NODE = SSECorePlugin.ID + IPath.SEPARATOR + "task-tags"; //$NON-NLS-1$
	public static final String TASK_TAG_PER_PROJECT = "use-project-settings"; //$NON-NLS-1$
	public static final String TASK_TAG_PRIORITIES = "taskPriorities"; //$NON-NLS-1$
	public static final String TASK_TAG_TAGS = "taskTags"; //$NON-NLS-1$
}
