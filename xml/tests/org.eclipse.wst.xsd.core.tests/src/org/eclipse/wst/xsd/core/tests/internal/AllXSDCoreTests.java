/*******************************************************************************
 * Copyright (c) 2008, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
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