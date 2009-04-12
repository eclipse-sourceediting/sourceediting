/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.core.internal;



import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * Small convenience class to log messages to plugin's log file and also, if
 * desired, the console. This class should only be used by classes in this
 * plugin. Other plugins should make their own copy, with appropriate ID.
 */
public class Logger {
	private static final String PLUGIN_ID = "org.eclipse.wst.sse.core"; //$NON-NLS-1$
	/**
	 * true if both platform and this plugin are in debug mode
	 */
	public static final boolean DEBUG = Platform.inDebugMode() && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/debug")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging adapter
	 * notification time
	 */
	public static final boolean DEBUG_ADAPTERNOTIFICATIONTIME = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/dom/adapter/notification/time")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging structured
	 * document
	 */
	public static final boolean DEBUG_DOCUMENT = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/structureddocument")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging file buffer
	 * model management
	 */
	public static final boolean DEBUG_FILEBUFFERMODELMANAGEMENT = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/filebuffers/modelmanagement")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging file buffer
	 * models not being released on shutdown
	 */
	public static final boolean DEBUG_FILEBUFFERMODELLEAKS = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/filebuffers/leaks")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging formatting
	 */
	public static final boolean DEBUG_FORMAT = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/format")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging text buffer
	 * lifecycle
	 */
	public static final boolean DEBUG_TEXTBUFFERLIFECYCLE = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/filebuffers/lifecycle")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging model
	 * lifecycle
	 */
	public static final boolean DEBUG_LIFECYCLE = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/structuredmodel/lifecycle")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging model state
	 */
	public static final boolean DEBUG_MODELSTATE = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/structuredmodel/state")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging model lock
	 * state
	 */
	public static final boolean DEBUG_MODELLOCK = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/structuredmodel/locks")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging model
	 * manager
	 */
	public static final boolean DEBUG_MODELMANAGER = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/structuredmodel/modelmanager")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging task tags
	 */
	public static final boolean DEBUG_TASKS = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging task tags
	 * content type detection
	 */
	public static final boolean DEBUG_TASKSCONTENTTYPE = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/detection")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging task tags
	 * jobs
	 */
	public static final boolean DEBUG_TASKSJOB = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/job")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging task tags
	 * overall performance
	 */
	public static final boolean DEBUG_TASKSOVERALLPERF = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/overalltime")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging task tags
	 * performance
	 */
	public static final boolean DEBUG_TASKSPERF = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/time")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging task tags
	 * preferences
	 */
	public static final boolean DEBUG_TASKSPREFS = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/preferences")); //$NON-NLS-1$ //$NON-NLS-2$
	/**
	 * true if platform and plugin are in debug mode and debugging task tags
	 * registry
	 */
	public static final boolean DEBUG_TASKSREGISTRY = DEBUG && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.sse.core/tasks/registry")); //$NON-NLS-1$ //$NON-NLS-2$

	/*
	 * Keep our own copy in case we want to add other severity levels
	 */
	public static final int OK = IStatus.OK;
	public static final int INFO = IStatus.INFO;
	public static final int WARNING = IStatus.WARNING;
	public static final int ERROR = IStatus.ERROR;
	public static final int OK_DEBUG = 200 + OK;
	public static final int INFO_DEBUG = 200 + INFO;
	public static final int WARNING_DEBUG = 200 + WARNING;
	public static final int ERROR_DEBUG = 200 + ERROR;

	/**
	 * @return true if the platform is debugging
	 */
	private static boolean isDebugging() {
		return Platform.inDebugMode();
	}

	/**
	 * Adds message to log.
	 * 
	 * @param level
	 *            severity level of the message (OK, INFO, WARNING, ERROR,
	 * @param message
	 *            text to add to the log
	 * @param exception
	 *            exception thrown
	 */
	private static void _log(int level, String message, Throwable exception) {
		if (level == OK_DEBUG || level == INFO_DEBUG || level == WARNING_DEBUG || level == ERROR_DEBUG) {
			if (!isDebugging())
				return;
		}
		int severity = IStatus.OK;
		switch (level) {
			case INFO_DEBUG :
			case INFO :
				severity = IStatus.INFO;
				break;
			case WARNING_DEBUG :
			case WARNING :
				severity = IStatus.WARNING;
				break;
			case ERROR_DEBUG :
			case ERROR :
				severity = IStatus.ERROR;
		}
		message = (message != null) ? message : ""; //$NON-NLS-1$
		Status statusObj = new Status(severity, PLUGIN_ID, severity, message, exception);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		if (bundle != null)
			Platform.getLog(bundle).log(statusObj);
	}

	/**
	 * Write a message to the log with the given severity level
	 * 
	 * @param level
	 *            ERROR, WARNING, INFO, OK
	 * @param message
	 *            message to add to the log
	 */
	public static void log(int level, String message) {
		_log(level, message, null);
	}

	/**
	 * Writes a message and exception to the log with the given severity level
	 * 
	 * @param level
	 *            ERROR, WARNING, INFO, OK
	 * @param message
	 *            message to add to the log
	 * @param exception
	 *            exception to add to the log
	 */
	public static void log(int level, String message, Throwable exception) {
		_log(level, message, exception);
	}

	/**
	 * Writes the exception as an error in the log along with an accompanying
	 * message
	 * 
	 * @param message
	 *            message to add to the log
	 * @param exception
	 *            exception to add to the log
	 */
	public static void logException(String message, Throwable exception) {
		_log(IStatus.ERROR, message, exception);
	}

	/**
	 * Writes the exception as an error in the log
	 * 
	 * @param exception
	 *            exception to add to the log
	 */
	public static void logException(Throwable exception) {
		_log(IStatus.ERROR, exception.getMessage(), exception);
	}
}
