/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.core.internal.validation.eclipse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test the Eclipse specific XSD validator.
 */
public class XSDValidatorTest extends TestCase 
{
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XSDValidatorTest.class);
  }
  
  /**
   * Test to ensure the URI resolver is not null in the Eclipse
   * specific XSD validator.
   */
  public void testURIResolverIsRegistered()
  {
	XSDValidatorWrapper validator = new XSDValidatorWrapper();
	assertNotNull("The URI resolver is null.", validator.getURIResolver());
  }

}
