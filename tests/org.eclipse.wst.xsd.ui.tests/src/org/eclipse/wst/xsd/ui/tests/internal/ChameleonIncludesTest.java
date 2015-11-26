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

public class ChameleonIncludesTest extends BaseTestCase
{
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(ChameleonIncludesTest.class);
  }
  
  public void testChameleonTest001()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByAttribute/Used/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest002()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByAttribute/Unused/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
  }

  public void testChameleonTest003()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByAttributeGroup/Used/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest004()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByAttributeGroup/Unused/Main.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 1);
  }

  public void testChameleonTest005()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByComplexType/Used/Main-BaseType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest006()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByComplexType/Used/Main-BaseType2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest007()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByComplexType/Used/Main-BaseType3.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest008()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByComplexType/Used/Main-GlobalElementType.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest009()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByComplexType/Used/Main-LocalElementType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest010()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByComplexType/Used/Main-LocalElementType2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest011()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByElement/Used/Main-CT.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest012()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByElement/Used/Main-Group.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest013()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByGroupDefinition/Used/Main-CT.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest014()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefByGroupDefinition/Used/Main-Group.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest015()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-GlobalAttributeType.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest016()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-GlobalElementType.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest017()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-List.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest018()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-LocalAttributeType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest019()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-LocalElementType1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest020()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-LocalElementType2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest021()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-Restriction.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest022()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-Union1.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest023()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-Union2.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

  public void testChameleonTest024()
  {
    XSDSchema schema = getXSDSchema(TC_ROOT_FOLDER + "/Chameleon-NoNamespace/RefBySimpleType/Used/Main-Union3.xsd");
    importManager.performRemoval(schema);
    List list = importManager.getUnusedXSDDirectives();
    assertTrue(list.size() == 0);
  }

}