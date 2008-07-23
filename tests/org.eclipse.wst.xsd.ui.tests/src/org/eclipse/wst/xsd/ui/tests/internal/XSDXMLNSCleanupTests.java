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

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.xsd.XSDSchema;

public class XSDXMLNSCleanupTests extends BaseTestCase
{
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XSDXMLNSCleanupTests.class);
  }

  public void testCleanup001()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main001.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 0);
    assertTrue(usedPrefixes.size() == 14);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns01"));
    assertTrue(usedPrefixes.contains("tns02"));
    assertTrue(usedPrefixes.contains("tns03"));
    assertTrue(usedPrefixes.contains("tns04"));
    assertTrue(usedPrefixes.contains("tns05"));
    assertTrue(usedPrefixes.contains("tns06"));
    assertTrue(usedPrefixes.contains("tns07"));
    assertTrue(usedPrefixes.contains("tns08"));
    assertTrue(usedPrefixes.contains("tns09"));
    assertTrue(usedPrefixes.contains("tns10"));
    assertTrue(usedPrefixes.contains("tns11"));
    assertTrue(usedPrefixes.contains("tns12"));
    assertTrue(usedPrefixes.contains("p"));
  }

  public void testCleanup002()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main002.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 1);
    assertTrue(unusedPrefixes.contains("tns05"));

    assertTrue(usedPrefixes.size() == 6);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns01"));
    assertTrue(usedPrefixes.contains("tns02"));
    assertTrue(usedPrefixes.contains("tns03"));
    assertTrue(usedPrefixes.contains("tns04"));
    assertTrue(usedPrefixes.contains("p"));
  }

  public void testCleanup003()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main003.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 0);
    assertTrue(usedPrefixes.size() == 14);
    assertTrue(usedPrefixes.contains("xsd"));
    assertTrue(usedPrefixes.contains("tns01"));
    assertTrue(usedPrefixes.contains("tns02"));
    assertTrue(usedPrefixes.contains("tns03"));
    assertTrue(usedPrefixes.contains("tns04"));
    assertTrue(usedPrefixes.contains("tns05"));
    assertTrue(usedPrefixes.contains("tns06"));
    assertTrue(usedPrefixes.contains("tns07"));
    assertTrue(usedPrefixes.contains("tns08"));
    assertTrue(usedPrefixes.contains("tns09"));
    assertTrue(usedPrefixes.contains("tns10"));
    assertTrue(usedPrefixes.contains("tns11"));
    assertTrue(usedPrefixes.contains("tns12"));
    assertTrue(usedPrefixes.contains("p"));
  }

  public void testCleanup004()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main004.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 12);
    assertTrue(unusedPrefixes.contains("tns02"));
    assertTrue(unusedPrefixes.contains("tns03"));
    assertTrue(unusedPrefixes.contains("tns04"));
    assertTrue(unusedPrefixes.contains("tns05"));
    assertTrue(unusedPrefixes.contains("tns06"));
    assertTrue(unusedPrefixes.contains("tns07"));
    assertTrue(unusedPrefixes.contains("tns08"));
    assertTrue(unusedPrefixes.contains("tns09"));
    assertTrue(unusedPrefixes.contains("tns10"));
    assertTrue(unusedPrefixes.contains("tns11"));
    assertTrue(unusedPrefixes.contains("tns12"));
    assertTrue(unusedPrefixes.contains(null));

    assertTrue(usedPrefixes.size() == 3);
    assertTrue(usedPrefixes.contains("xsd"));
    assertTrue(usedPrefixes.contains("tns01"));
    assertTrue(usedPrefixes.contains("p"));
  }

  public void testCleanup005()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main005.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 12);
    assertTrue(unusedPrefixes.contains("tns01"));
    assertTrue(unusedPrefixes.contains("tns02"));
    assertTrue(unusedPrefixes.contains("tns03"));
    assertTrue(unusedPrefixes.contains("tns04"));
    assertTrue(unusedPrefixes.contains("tns05"));
    assertTrue(unusedPrefixes.contains("tns06"));
    assertTrue(unusedPrefixes.contains("tns07"));
    assertTrue(unusedPrefixes.contains("tns08"));
    assertTrue(unusedPrefixes.contains("tns09"));
    assertTrue(unusedPrefixes.contains("tns10"));
    assertTrue(unusedPrefixes.contains("tns11"));
    assertTrue(unusedPrefixes.contains("tns12"));

    assertTrue(usedPrefixes.size() == 3);
    assertTrue(usedPrefixes.contains("xsd"));
    assertTrue(usedPrefixes.contains("p"));
    assertTrue(usedPrefixes.contains(null));
  }

  public void testCleanup006()
  {
    // Null target namespace schema

    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main006.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 1);
    assertTrue(unusedPrefixes.contains("tns01"));
    assertTrue(usedPrefixes.size() == 3);
    assertTrue(usedPrefixes.contains("xsd"));
    assertTrue(usedPrefixes.contains("p"));
    // This was added since references were made without a prefix
    assertTrue(usedPrefixes.contains(null));

  }

  public void testCleanup007()
  {
    // Test Unused Null Prefix
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main007.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 1);
    assertTrue(unusedPrefixes.contains(null));
    assertTrue(usedPrefixes.size() == 3);
    assertTrue(usedPrefixes.contains("xsd"));
    assertTrue(usedPrefixes.contains("p"));
    assertTrue(usedPrefixes.contains("tns"));

  }

  public void testCleanup008()
  {
    // Test Used Null Prefix
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/xmlnsCleanup/test/Main008.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 1);
    assertTrue(unusedPrefixes.contains("p"));
    assertTrue(usedPrefixes.size() == 3);
    assertTrue(usedPrefixes.contains("xsd"));
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns"));
  }

  // Using the test XSDs from the Unused folder
  public void testCleanup009()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main001.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 0);
    assertTrue(usedPrefixes.size() == 2);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns"));
  }

  // Using the test XSDs from the Unused folder
  public void testCleanup010()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main002.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 1);
    assertTrue(unusedPrefixes.contains("imp"));
    assertTrue(usedPrefixes.size() == 2);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns"));
  }

  // Using the test XSDs from the Unused folder
  public void testCleanup011()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main003.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 1);
    assertTrue(unusedPrefixes.contains("imp"));
    assertTrue(usedPrefixes.size() == 2);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns"));
  }

  // Using the test XSDs from the Unused folder
  public void testCleanup012()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main004.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 1);
    assertTrue(unusedPrefixes.contains("imp"));
    assertTrue(usedPrefixes.size() == 2);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns"));
  }

  // Using the test XSDs from the Unused folder
  public void testCleanup013()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main005.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 2);
    assertTrue(unusedPrefixes.contains("imp"));
    assertTrue(unusedPrefixes.contains("imp4"));
    assertTrue(usedPrefixes.size() == 2);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns"));
  }

  // Using the test XSDs from the Unused folder
  public void testCleanup014()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main006.xsd");
    importManager.performRemoval(schema);
    Set unusedPrefixes = importManager.getUnusedPrefixes();
    Set usedPrefixes = importManager.getUsedPrefixes();
    assertTrue(unusedPrefixes.size() == 0);
    assertTrue(usedPrefixes.size() == 3);
    assertTrue(usedPrefixes.contains(null));
    assertTrue(usedPrefixes.contains("tns"));
    assertTrue(usedPrefixes.contains("p"));
  }

}