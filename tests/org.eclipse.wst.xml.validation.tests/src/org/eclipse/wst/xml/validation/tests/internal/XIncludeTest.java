/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.validation.tests.internal;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;


/**
 * Tests the XInclude feature. 
 */
public class XIncludeTest extends BaseTestCase
{
  public void testSupportsXInclude()
  {
    try
    {
      configuration.setFeature(XMLValidationConfiguration.USE_XINCLUDE, true);
    }
    catch (Exception e)
    {
      fail();
    }

    String testFile = getTestFile();
    List keys = new ArrayList();
    int numErrors = 0;
    int numWarnings = 0;

    runTest(testFile, keys, numErrors, numWarnings);
  }

  public void testCanTurnOffXInclude()
  {
    try
    {
      configuration.setFeature(XMLValidationConfiguration.USE_XINCLUDE, false);
    }
    catch (Exception e)
    {
      fail();
    }

    String testFile = getTestFile();
    List keys = new ArrayList();
    keys.add("cvc-complex-type.2.4.a");
    int numErrors = 1;
    int numWarnings = 0;

    runTest(testFile, keys, numErrors, numWarnings);
  }

  private String getTestFile()
  {
    String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + "XMLExamples/XInclude/master.xml";
    return testfile;
  }
}
