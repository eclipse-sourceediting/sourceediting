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

import junit.framework.TestCase;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;

/**
 * Base test case class which provides methods to 
 * - create logs
 * - read from logs
 * - run log comparison tests
 */
public class BaseTestCase extends TestCase
{
  protected String FILE_PROTOCOL = "file:///"; 
  protected String PLUGIN_ABSOLUTE_PATH;
  protected String SAMPLES_DIR = "testresources/samples/";
  protected String GENERATED_RESULTS_DIR = "testresources/generatedResults/";
  protected String IDEAL_RESULTS_DIR = "testresources/idealResults/";
  protected String LOG_FILE_LOCATION = "results.log";
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
  {
    PLUGIN_ABSOLUTE_PATH = XSDCoreTestsPlugin.getInstallURL();
  }
  
  protected String locateFileUsingCatalog(String namespaceURI)
  {
    URIResolver resolver = URIResolverPlugin.createResolver();
    String result = resolver.resolve("", namespaceURI, "");
    String resolvedURI = resolver.resolvePhysicalLocation("", namespaceURI, result);
    if (resolvedURI == null || resolvedURI.length() == 0)
    {
      return namespaceURI;
    }
    return resolvedURI;
  }

}
