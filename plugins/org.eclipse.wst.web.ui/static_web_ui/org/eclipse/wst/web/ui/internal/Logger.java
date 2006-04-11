/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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
package org.eclipse.wst.web.ui.internal;



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
	private static final String PLUGIN_ID = "org.eclipse.wst.web.ui"; //$NON-NLS-1$
	/**
	 * true if both platform and this plugin are in debug mode
	 */
	public static final boolean DEBUG = Platform.inDebugMode() && "true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.wst.web.ui/debug")); //$NON-NLS-1$ //$NON-NLS-2$

	/*
	 * Keep our own copy in case we want to add other severity levels
	 */
	public static final int OK = IStatus.OK;
	public static final int INFO = IStatus.INFO;
	public static final int WARNING = IStatus.WARNING;
	public static final int ERROR = IStatus.ERROR;

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
		message = (message != null) ? message : ""; //$NON-NLS-1$
		Status statusObj = new Status(level, PLUGIN_ID, level, message, exception);
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
