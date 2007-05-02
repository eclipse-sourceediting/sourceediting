/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.validation.tests.internal;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The plugin class for this test plugin.
 */
public class XSDValidationTestsPlugin extends Plugin
{
  protected static Bundle pluginBundle;

  public XSDValidationTestsPlugin()
  {
  }
  
  /**
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  public void start(BundleContext context) throws Exception 
  {
	super.start(context);
	pluginBundle = context.getBundle();
  }
  
  /**
   * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
   */
  public void stop(BundleContext context) throws Exception 
  {
	pluginBundle = null;
	super.stop(context);
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
