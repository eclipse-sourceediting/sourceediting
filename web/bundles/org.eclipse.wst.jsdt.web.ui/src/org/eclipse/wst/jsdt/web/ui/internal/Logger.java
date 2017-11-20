/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import com.ibm.icu.util.StringTokenizer;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class Logger {
	public static final int ERROR = IStatus.ERROR; // 4
	public static final int ERROR_DEBUG = 200 + Logger.ERROR;
	public static final int INFO = IStatus.INFO; // 1
	public static final int INFO_DEBUG = 200 + Logger.INFO;
	public static final int OK = IStatus.OK; // 0
	public static final int OK_DEBUG = 200 + Logger.OK;
	private static final String PLUGIN_ID = "org.eclipse.wst.jsdt.web.ui"; //$NON-NLS-1$
	private static final String TRACEFILTER_LOCATION = "/debug/tracefilter"; //$NON-NLS-1$
	public static final int WARNING = IStatus.WARNING; // 2
	public static final int WARNING_DEBUG = 200 + Logger.WARNING;
	
	/**
	 * Adds message to log.
	 * 
	 * @param level
	 *            severity level of the message (OK, INFO, WARNING, ERROR,
	 *            OK_DEBUG, INFO_DEBUG, WARNING_DEBUG, ERROR_DEBUG)
	 * @param message
	 *            text to add to the log
	 * @param exception
	 *            exception thrown
	 */
	protected static void _log(int level, String message, Throwable exception) {
		if (level == Logger.OK_DEBUG || level == Logger.INFO_DEBUG || level == Logger.WARNING_DEBUG || level == Logger.ERROR_DEBUG) {
			if (!Logger.isDebugging()) {
				return;
			}
		}
		int severity = IStatus.OK;
		switch (level) {
			case INFO_DEBUG:
			case INFO:
				severity = IStatus.INFO;
			break;
			case WARNING_DEBUG:
			case WARNING:
				severity = IStatus.WARNING;
			break;
			case ERROR_DEBUG:
			case ERROR:
				severity = IStatus.ERROR;
		}
		message = (message != null) ? message : "null"; //$NON-NLS-1$
		Status statusObj = new Status(severity, Logger.PLUGIN_ID, severity, message, exception);
		Bundle bundle = Platform.getBundle(Logger.PLUGIN_ID);
		if (bundle != null) {
			Platform.getLog(bundle).log(statusObj);
		}
	}
	
	/**
	 * Prints message to log if category matches /debug/tracefilter option.
	 * 
	 * @param message
	 *            text to print
	 * @param category
	 *            category of the message, to be compared with
	 *            /debug/tracefilter
	 */
	protected static void _trace(String category, String message, Throwable exception) {
		if (Logger.isTracing(category)) {
			message = (message != null) ? message : "null"; //$NON-NLS-1$
			Status statusObj = new Status(IStatus.OK, Logger.PLUGIN_ID, IStatus.OK, message, exception);
			Bundle bundle = Platform.getBundle(Logger.PLUGIN_ID);
			if (bundle != null) {
				Platform.getLog(bundle).log(statusObj);
			}
		}
	}
	
	/**
	 * @return true if the platform is debugging
	 */
	public static boolean isDebugging() {
		return Platform.inDebugMode();
	}
	
	/**
	 * Determines if currently tracing a category
	 * 
	 * @param category
	 * @return true if tracing category, false otherwise
	 */
	public static boolean isTracing(String category) {
		if (!Logger.isDebugging()) {
			return false;
		}
		String traceFilter = Platform.getDebugOption(Logger.PLUGIN_ID + Logger.TRACEFILTER_LOCATION);
		if (traceFilter != null) {
			StringTokenizer tokenizer = new StringTokenizer(traceFilter, ","); //$NON-NLS-1$
			while (tokenizer.hasMoreTokens()) {
				String cat = tokenizer.nextToken().trim();
				if (category.equals(cat)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void log(int level, String message) {
		Logger._log(level, message, null);
	}
	
	public static void log(int level, String message, Throwable exception) {
		Logger._log(level, message, exception);
	}
	
	public static void logException(String message, Throwable exception) {
		Logger._log(Logger.ERROR, message, exception);
	}
	
	public static void logException(Throwable exception) {
		Logger._log(Logger.ERROR, exception.getMessage(), exception);
	}
	
	public static void trace(String category, String message) {
		Logger._trace(category, message, null);
	}
	
	public static void traceException(String category, String message, Throwable exception) {
		Logger._trace(category, message, exception);
	}
	
	public static void traceException(String category, Throwable exception) {
		Logger._trace(category, exception.getMessage(), exception);
	}
}
