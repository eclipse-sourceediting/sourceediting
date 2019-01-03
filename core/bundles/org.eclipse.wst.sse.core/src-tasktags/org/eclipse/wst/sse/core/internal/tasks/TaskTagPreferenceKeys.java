/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
