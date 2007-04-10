/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation.core.logging;

/**
 * The interface for a standard logger. Allows for logging errors and warnings.
 * 
 * @author Lawrence Mandel, IBM
 */
public interface ILogger
{
  /**
   * Log an error message.
   * 
   * @param error The error message to log.
   * @param exception The exception to log.
   */
  public void logError(String error, Throwable exception);

  /**
   * Log a warning message.
   * 
   * @param warning The warning message to log.
   * @param exception The exception to log.
   */
  public void logWarning(String warning, Throwable exception);
}
