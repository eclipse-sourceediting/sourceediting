/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.validation.tests.internal;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
/**
 * The plugin class for this test plugin.
 * 
 * @author Lawrence Mandel, IBM
 */
public class XMLValidatorTestsPlugin extends Plugin
{
  private static XMLValidatorTestsPlugin plugin = null;
 
  public XMLValidatorTestsPlugin()
  {
  	plugin = this;
  }

 public static String getPluginLocation()
  {
    try
    {
      return new Path(Platform.resolve(plugin.getBundle().getEntry("/")).getFile()).removeTrailingSeparator().toString();
    } catch (IOException e)
    {
    }
    return null;
  }
}
