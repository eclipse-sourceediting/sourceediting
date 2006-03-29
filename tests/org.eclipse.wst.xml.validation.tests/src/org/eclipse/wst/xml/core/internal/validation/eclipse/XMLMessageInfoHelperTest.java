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
 * Test case to test the XMLMessageInfoHelper class.
 */
public class XMLMessageInfoHelperTest extends TestCase 
{
  private XMLMessageInfoHelper helper = new XMLMessageInfoHelper();
  
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XMLMessageInfoHelperTest.class);
  }
  
  /**
   * Test that the createMessageInfo method returns {"", null} when
   * given a non-existant key and null message arguments.
   */
  public void testNotExistantKeyAndNullMessageArguments()
  {
	String[] messageInfo = helper.createMessageInfo("NON_EXISTANT_KEY", null);
	assertEquals("The selection strategy returned was not an empty string.", "", messageInfo[0]);
	assertNull("The nameOrValue returned was not null.", messageInfo[1]);
  }
  
  /**
   * Test that the createMessageInfo method returns {"", null} when
   * given a non-existant key and null message arguments.
   */
  public void testNullKeyAndMessageArguments()
  {
	String[] messageInfo = helper.createMessageInfo(null, null);
	assertEquals("The selection strategy returned was not an empty string.", "", messageInfo[0]);
	assertNull("The nameOrValue returned was not null.", messageInfo[1]);
  }

}
