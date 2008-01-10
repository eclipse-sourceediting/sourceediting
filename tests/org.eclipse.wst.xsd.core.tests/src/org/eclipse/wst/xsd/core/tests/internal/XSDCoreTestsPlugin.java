/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.core.tests.internal;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class XSDCoreTestsPlugin extends Plugin
{
  // The plug-in ID
  public static final String PLUGIN_ID = "org.eclipse.wst.xsd.core.tests";
  protected static Bundle pluginBundle;

  // The shared instance
  private static XSDCoreTestsPlugin plugin;

  /**
   * The constructor
   */
  public XSDCoreTestsPlugin()
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;
    pluginBundle = context.getBundle();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    pluginBundle = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static XSDCoreTestsPlugin getDefault()
  {
    return plugin;
  }

  /**
   * Get the install URL of this plugin.
   * 
   * @return the install url of this plugin
   */
  public static String getInstallURL()
  {
    try
    {
      return FileLocator.resolve(pluginBundle.getEntry("/")).getFile();
    }
    catch (IOException e)
    {
      return null;
    }
  }
}
