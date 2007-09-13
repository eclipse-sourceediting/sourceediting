/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
 * The logger factory allows for simple use of whatever logging mechanism is in
 * place. The tool can specify a custom logging class and can request the
 * logger from this class.
 * 
 * @author Lawrence Mandel, IBM
 * 
 * @deprecated - not used in WTP or by any adopters as of 9/12/2007
 */
public class LoggerFactory
{
  private static ILogger      logger            = null;
  private static ClassLoader  classloader       = null;
  private static String       loggerClass       = null;
  private static final String commandlinelogger = "org.eclipse.wst.xml.validation.internal.core.logging.CommandLineLogger"; //$NON-NLS-1$
  private static final String eclipselogger     = "org.eclipse.wst.xml.validation.internal.core.logging.EclipseLogger"; //$NON-NLS-1$
  private static String       defaultlogger     = commandlinelogger;

  /**
   * Get the one and only instance of the logger.
   * 
   * @return The one and only instance of the logger.
   */
  public static ILogger getLoggerInstance()
  {
    if(logger == null)
    {
      if(loggerClass != null)
      {
        if(classloader != null)
        {
          try
          {
            Class lc = classloader.loadClass(loggerClass);
            logger = (ILogger) lc.newInstance();
          }
          catch (Exception e)
          {
          }
        }
        if(logger == null)
        {
          try
          {
            Class lc = LoggerFactory.class.getClassLoader().loadClass(loggerClass);
            logger = (ILogger) lc.newInstance();
          }
          catch (Exception e)
          {
          }
        }
      }
      if(logger == null)
      {
        try
        {
          Class lc = LoggerFactory.class.getClassLoader().loadClass(defaultlogger);
          logger = (ILogger) lc.newInstance();
        }
        catch (Exception e)
        {
        }
      }
    }
    return logger;
  }

  /**
   * Specify the logger implementation to be used.
   * 
   * @param classname
   *            The name of the logger class.
   * @param loggerclassloader
   *            The classloader to use to load the logger. If null, the default
   *            classloader will be used.
   */
  public static void specifyLogger(String classname, ClassLoader loggerclassloader)
  {
    loggerClass = classname;
    classloader = loggerclassloader;
  }

  /**
   * Set the default logger to the eclipse logger.
   */
  public static void useEclipseLogger()
  {
    defaultlogger = eclipselogger;
  }
}
