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
package org.eclipse.wst.xml.core.internal.validation.eclipse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test the Eclipse specific XML validator.
 */
public class XMLValidatorTest extends TestCase 
{
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XMLValidatorTest.class);
  }
  
  /**
   * Test to ensure the URI resolver is not null in the Eclipse
   * specific XML validator.
   */
  public void testURIResolverIsRegistered()
  {
	XMLValidatorWrapper validator = new XMLValidatorWrapper();
	assertNotNull("The URI resolver is null.", validator.getURIResolver());
  }

}
