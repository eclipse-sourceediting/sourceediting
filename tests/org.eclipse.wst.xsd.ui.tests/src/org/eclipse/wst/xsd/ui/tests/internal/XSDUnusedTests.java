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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;

public class XSDUnusedTests extends BaseTestCase
{
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(XSDUnusedTests.class);
  }

  public void testImport001()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Simple/Test.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("Import001.xsd".equals(d1.getSchemaLocation()));
  }

  public void testImport001RepeatRemovalOnce()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Simple/Test.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("Import001.xsd".equals(d1.getSchemaLocation()));
    
    importManager.performRemoval(schema);
    list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport001RepeatRemovalTwice()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Simple/Test.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("Import001.xsd".equals(d1.getSchemaLocation()));
    
    importManager.performRemoval(schema);
    list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
    
    importManager.performRemoval(schema);
    list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

 
  public void testImport002()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main001.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d1.getSchemaLocation()));
  }

  public void testImport003()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main002.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Import1.xsd".equals(d1.getSchemaLocation()));
  }

  public void testImport004()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main003.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 2);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Import1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d2.getSchemaLocation()));
  }

  public void testImport005()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main004.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 2);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Import3.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d2.getSchemaLocation()));
  }

  public void testImport006()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main005.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 5);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Import1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(2);
    assertTrue("../Import2.xsd".equals(d2.getSchemaLocation()));
    XSDSchemaDirective d3 = (XSDSchemaDirective) list.get(3);
    assertTrue("../Import3.xsd".equals(d3.getSchemaLocation()));
    XSDSchemaDirective d4 = (XSDSchemaDirective) list.get(4);
    assertTrue("../Import4.xsd".equals(d4.getSchemaLocation()));
    XSDSchemaDirective d5 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d5.getSchemaLocation()));
  }

  public void testImport007()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main006.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Import1.xsd".equals(d1.getSchemaLocation()));
  }

  public void testImport008()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main007.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 2);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Import1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include2.xsd".equals(d2.getSchemaLocation()));
  }

  public void testImport009()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main008.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 2);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Import1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include2.xsd".equals(d2.getSchemaLocation()));
  }

  public void testImport010()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Import1.xsd".equals(d1.getSchemaLocation()));
  }

  public void testImport011()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009a.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport012()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009b.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 2);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include2.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include3.xsd".equals(d2.getSchemaLocation()));
  }

  public void testImport013()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009c.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 3);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include2.xsd".equals(d2.getSchemaLocation()));
    XSDSchemaDirective d3 = (XSDSchemaDirective) list.get(2);
    assertTrue("../Include3.xsd".equals(d3.getSchemaLocation()));
  }

  public void testImport014()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009d.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 2);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include3.xsd".equals(d2.getSchemaLocation()));
  }

  public void testImport015()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009e.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include2.xsd".equals(d1.getSchemaLocation()));
  }

  public void testImport016()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009f.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 5);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../A.xsd".equals(d2.getSchemaLocation()));
    XSDSchemaDirective d3 = (XSDSchemaDirective) list.get(2);
    assertTrue("../Include5.xsd".equals(d3.getSchemaLocation()));

    XSDSchemaDirective d4 = (XSDSchemaDirective) list.get(3);
    assertTrue("../Include2.xsd".equals(d4.getSchemaLocation()));
    XSDSchemaDirective d5 = (XSDSchemaDirective) list.get(4);
    assertTrue("../Include3.xsd".equals(d5.getSchemaLocation()));
  }

  public void testImport017()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009g.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 2);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include2.xsd".equals(d2.getSchemaLocation()));
  }

  public void testImport018()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Unused/test/Main009h.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 3);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include5.xsd".equals(d2.getSchemaLocation()));
    XSDSchemaDirective d3 = (XSDSchemaDirective) list.get(2);
    assertTrue("../Include3.xsd".equals(d3.getSchemaLocation()));
  }

  public void testCircularIncludes001()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Includes/circular/test/Main001.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include4.xsd".equals(d1.getSchemaLocation()));
  }

  public void testCircularIncludes002()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Includes/circular/test/Main002.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 6);
    XSDSchemaDirective d1 = (XSDSchemaDirective) list.get(0);
    assertTrue("../Include1.xsd".equals(d1.getSchemaLocation()));
    XSDSchemaDirective d2 = (XSDSchemaDirective) list.get(1);
    assertTrue("../Include2.xsd".equals(d2.getSchemaLocation()));
    XSDSchemaDirective d3 = (XSDSchemaDirective) list.get(2);
    assertTrue("../Include3.xsd".equals(d3.getSchemaLocation()));
    XSDSchemaDirective d4 = (XSDSchemaDirective) list.get(3);
    assertTrue("../Include4.xsd".equals(d4.getSchemaLocation()));
    XSDSchemaDirective d5 = (XSDSchemaDirective) list.get(4);
    assertTrue("../Include5.xsd".equals(d5.getSchemaLocation()));
    XSDSchemaDirective d6 = (XSDSchemaDirective) list.get(5);
    assertTrue("../Include6.xsd".equals(d6.getSchemaLocation()));

  }

}