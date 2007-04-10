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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;


/**
 * A logger that will log to the log file in the eclipse metadata directory.
 */
public class EclipseLogger implements ILogger
{
  
  public void logError(String error, Throwable exception)
  {
    XMLCorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, XMLCorePlugin.getDefault().getBundle().getSymbolicName(), IStatus.ERROR, error, exception));
  }
 
  public void logWarning(String warning, Throwable exception)
  {
    XMLCorePlugin.getDefault().getLog().log(new Status(IStatus.WARNING, XMLCorePlugin.getDefault().getBundle().getSymbolicName(), IStatus.WARNING, warning, exception));
  }
}
