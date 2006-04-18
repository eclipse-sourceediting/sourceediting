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
import junit.framework.Test;

import org.eclipse.wst.xsd.core.internal.validation.XSDValidationConfigurationTest;
import org.eclipse.wst.xsd.core.internal.validation.eclipse.ValidatorTest;
import org.eclipse.wst.xsd.core.internal.validation.eclipse.XSDMessageInfoHelperTest;
import org.eclipse.wst.xsd.core.internal.validation.eclipse.XSDValidatorTest;
/**
 * The root test suite that contains all other XSD validator test suites.
 */
public class AllXSDTests extends junit.framework.TestSuite
{
  /**
   * Create this test suite.
   * 
   * @return This test suite.
   */
  public static Test suite()
  {
    return new AllXSDTests();
  }
  
  /**
   * Constructor
   */
  public AllXSDTests()
  {
    super("AllXSDTests");
    addTest(ValidatorTest.suite());
    addTest(XSDMessageInfoHelperTest.suite());
    addTest(XSDValidatorTest.suite());
    addTest(BugFixesTest.suite());
    addTest(PathsTest.suite());
    addTestSuite(XSDValidationConfigurationTest.class);
  }
}