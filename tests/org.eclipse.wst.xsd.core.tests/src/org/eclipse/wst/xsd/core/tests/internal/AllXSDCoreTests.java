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
import junit.framework.Test;
/**
 * The root test suite that contains all other XSD Core test suites.
 */
public class AllXSDCoreTests extends junit.framework.TestSuite
{
  /**
   * Create this test suite.
   * 
   * @return This test suite.
   */
  public static Test suite()
  {
    return new AllXSDCoreTests();
  }
  
  /**
   * Constructor
   */
  public AllXSDCoreTests()
  {
    super("AllXSDCoreTests");
    addTest(BugFixesTest.suite());
  }
}