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
 * Test case to test the XSDMessageInfoHelper class.
 */
public class XSDMessageInfoHelperTest extends TestCase 
{
  private XSDMessageInfoHelperWrapper helper = new XSDMessageInfoHelperWrapper();
  
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XSDMessageInfoHelperTest.class);
  }
  
  /**
   * Test that the createMessageInfo method returns {"", ""} when
   * given a non-existant key and null message arguments.
   */
  public void testNotExistantKeyAndNullMessageArguments()
  {
	String[] messageInfo = helper.createMessageInfo("NON_EXISTANT_KEY", null);
	assertEquals("The selection strategy returned was not an empty string.", "", messageInfo[0]);
	assertEquals("The nameOrValue returned was not an empty string.", "", messageInfo[1]);
  }
  
  /**
   * Test that the createMessageInfo method returns {"", ""} when
   * given a non-existant key an null message arguments.
   */
  public void testNullKeyAndMessageArguments()
  {
	String[] messageInfo = helper.createMessageInfo(null, null);
	assertEquals("The selection strategy returned was not an empty string.", "", messageInfo[0]);
	assertEquals("The nameOrValue returned was not an empty string.", "", messageInfo[1]);
  }
  
  /**
   * Test the method getFirstStringBetweenSingleQuotes. There are a few tests:
   * 1. Test that a simple string with single quotes 'name' returns name.
   * 2. Test that a string with no single quotes returns null.
   * 3. Test that a string with a single single quote 'name returns null.
   * 4. Test that a string with 3 single quotes 'name'name2' returns name.
   */
  public void testGetFirstStringBetweenSingleQuotes()
  {
	// 1. Test that a simple string with single quotes 'name' returns name.
	String result = helper.getFirstStringBetweenSingleQuotes("'name'");
	assertEquals("The returned string was not \"name\" for the string \"'name'\".", "name", result);
	
	// 2. Test that a string with no single quotes returns null.
	result = helper.getFirstStringBetweenSingleQuotes("name");
	assertNull("The returned string was not null for the string \"name\" with no single quotes.", result);
	
	// 3. Test that a string with a single single quote 'name returns null.
	result = helper.getFirstStringBetweenSingleQuotes("'name");
	assertNull("The returned string was not null for the string \"'name\" with one single quote.", result);
	
	// 4. Test that a string with 3 single quotes 'name'name2' returns name.
	result = helper.getFirstStringBetweenSingleQuotes("'name'name2'");
	assertEquals("The returned string was not \"name\" for the string \"'name'name2'\" with 3 single quotes.", "name", result);
  }

}
