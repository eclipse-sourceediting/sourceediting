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

public class ImportsTest extends BaseTestCase
{
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(ImportsTest.class);
  }

  public void testImport001()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByAttribute/Used/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport002()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByAttribute/Unused/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
  }

  public void testImport003()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByAttributeGroup/Used/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport004()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByAttributeGroup/Unused/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
  }

  public void testImport005()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByComplexType/Used/Main-BaseType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport006()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByComplexType/Used/Main-BaseType2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport007()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByComplexType/Used/Main-BaseType3.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport008()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByComplexType/Used/Main-GlobalElementType.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport009()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByComplexType/Used/Main-LocalElementType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport010()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByComplexType/Used/Main-LocalElementType2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport011()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByElement/Used/Main-CT.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport012()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByElement/Used/Main-Group.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport013()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByGroupDefinition/Used/Main-CT.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport014()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefByGroupDefinition/Used/Main-Group.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport015()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-GlobalAttributeType.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport016()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-GlobalElementType.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport017()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-List.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport018()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-LocalAttributeType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport019()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-LocalElementType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport020()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-LocalElementType2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport021()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-Restriction.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport022()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-Union1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport023()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-Union2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testImport024()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/ImportsWithNamespace/RefBySimpleType/Used/Main-Union3.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

}