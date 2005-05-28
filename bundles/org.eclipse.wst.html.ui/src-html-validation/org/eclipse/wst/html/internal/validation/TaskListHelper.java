/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.internal.validation;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * pa_TODO moved this class here for now to remove dependency on
 * validation packages
 * 
 * This class is intended to be used only by the validation framework. The
 * TaskListHelper class will be removed in Milestone 4.
 */
public class TaskListHelper {
	private static TaskListHelper _taskListHelper = null;

	public static TaskListHelper getTaskList() {
		if (_taskListHelper == null) {
			_taskListHelper = new TaskListHelper();
		}
		return _taskListHelper;
	}

	/**
	 * This method adds a message to a resource in the task list.
	 */
	public void addTask(String pluginId, IResource resource, String location, String messageId, String message, int markerType, String targetObjectName, String groupName, int offset, int length) throws CoreException {
		TaskListUtility.addTask(pluginId, resource, location, messageId, message, markerType, targetObjectName, groupName, offset, length);
	}

	/**
	 * This method removes all messages from a resource in the task list.
	 */
	public void removeAllTasks(IResource resource, String owner, String objectName) throws CoreException {
		TaskListUtility.removeAllTasks(resource, owner, objectName);
	}
}