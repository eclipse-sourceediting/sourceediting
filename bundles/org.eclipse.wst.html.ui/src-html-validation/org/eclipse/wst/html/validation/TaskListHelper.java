package org.eclipse.wst.html.validation;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2000,2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

/**
 * pa_TODO moved this class here for now to remove dependency on com.ibm.etools.validation.
 * 
 * This class is intended to be used only by the validation framework.
 * The TaskListHelper class will be removed in Milestone 4.
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