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
package org.eclipse.wst.xsd.ui.tests.internal;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Wrapper test suite for all XSD UI tests.
 */
public class AllXSDUITests extends TestSuite
{
  /**
   * Create this test suite.
   * 
   * @return This test suite.
   */
  public static Test suite()
  {
    return new AllXSDUITests();
  }

  /**
   * Constructor
   */
  public AllXSDUITests()
  {
    super("AllXSDUITests");
    // XSD Includes tests
    addTest(ChameleonIncludesTest.suite());
    // Testing used imports aren't removed (ensure logic to determine
    // what is or what is not used is correct)
    addTest(ImportsTest.suite());
    // Unused XSD imports tests
    addTest(XSDUnusedTests.suite());
    // XSD xml ns table cleanup
    addTest(XSDXMLNSCleanupTests.suite());
  }
}
