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

package org.eclipse.wst.xsd.core.tests.internal;

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xsd.contentmodel.internal.CMDocumentFactoryXSD;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

/**
 * Test class for bug fixes.
 */
public class BugFixesTest extends BaseTestCase
{
  protected String BUGFIXES_DIR = "BugFixes/";
  
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(BugFixesTest.class);
  }
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
  {
    super.setUp();
  }
  
  // Add tests here
  
  @SuppressWarnings("unchecked")
  public void testXSIType()
  {
    String soapSchemaURI = locateFileUsingCatalog("http://schemas.xmlsoap.org/wsdl/soap/");

    CMDocumentFactoryXSD factory = new CMDocumentFactoryXSD();
    assertNotNull("Assert factory is not null", factory);
    
    CMDocument cmDocument = factory.createCMDocument(soapSchemaURI);
    assertNotNull("Assert CMDocument is not null", cmDocument);
        
    CMNamedNodeMap elements = cmDocument.getElements();
    
    boolean foundDesiredElement = false;
    for (Iterator<CMElementDeclaration> i = elements.iterator(); i.hasNext(); )
    {
      CMElementDeclaration element = i.next();
      if ("binding".equals(element.getElementName()))
      {
        foundDesiredElement = true;
        
        CMNamedNodeMap attributes = element.getAttributes();
        assertNotNull(attributes);  // Three attributes: required, transport and style
        assertTrue(attributes.getLength() == 3);  // If the xsi:type was present, it would be 4 attributes
        
        CMNode attrNode = null;
        
        attrNode = attributes.getNamedItem("required");
        assertNotNull(attrNode);
        attrNode = attributes.getNamedItem("transport");
        assertNotNull(attrNode);
        attrNode = attributes.getNamedItem("style");
        assertNotNull(attrNode);
        attrNode = attributes.getNamedItem("type");  // Should be null!
        assertNull(attrNode);
        break;
      }
    }
    assertTrue(foundDesiredElement); // if we didn't even find the binding element, then something terrible went wrong
  }
  
  @SuppressWarnings("unchecked")
  public void testStackOverflow()
  {
    String vxmlSchemaURI = locateFileUsingCatalog("http://www.w3.org/TR/voicexml20/vxml.xsd");
    
    // See bug 206138
    
    // Two ways to test this.
    // First way. Call findTypesDerivedFrom from XSDImpl.
    
    XSDSchema xsdSchema = XSDImpl.buildXSDModel(vxmlSchemaURI);
    assertNotNull(xsdSchema);
    boolean foundDesiredType = false;
    for (Iterator<XSDTypeDefinition> types = xsdSchema.getTypeDefinitions().iterator(); types.hasNext(); )
    {
      XSDTypeDefinition type = types.next();
      if (type instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) type;
        if ("basic.event.handler".equals(complexType.getName()))
        {
          foundDesiredType = true;
          List<XSDTypeDefinition> list = XSDImpl.findTypesDerivedFrom(complexType);
          int size = list.size();
          // assertTrue(size == 1);  // if we got something back, then great, there was no out of stack error
          assertTrue(size >= 0);
          // Because of bug 203048, there is a change in behaviour to redefined types.  
          // The complex type named speaker is no longer circular.   In terms of this junit, the value returned is not relevant
          // since we just want some length back (i.e. there was no crash from a stack overflow).
          break;
        }
      }
    }
    assertTrue(foundDesiredType);  // if we didn't even find the complex type, then something terrible went wrong
    
    // Second way to test via content model
    
    CMDocumentFactoryXSD factory = new CMDocumentFactoryXSD();
    assertNotNull("Assert factory is not null", factory);
    
    CMDocument cmDocument = factory.createCMDocument(vxmlSchemaURI);
    assertNotNull("Assert CMDocument is not null", cmDocument);
        
    CMNamedNodeMap elements = cmDocument.getElements();
    
    boolean foundDesiredElement = false;
    for (Iterator<CMElementDeclaration> i = elements.iterator(); i.hasNext(); )
    {
      CMElementDeclaration element = i.next();
      if ("noinput".equals(element.getElementName()))
      {
        CMNamedNodeMap attributes = element.getAttributes();
        assertNotNull(attributes);
        // assertTrue(attributes.getLength() == 3);  // if we got something back, then great, there was no out of stack error
        // Because of bug 203048, there is a change in behaviour to redefined types.  
        // The complex type named speaker is no longer circular.   In terms of this junit, the value returned is not relevant
        // since we just want some length back (i.e. there was no crash from a stack overflow).
        assertTrue(attributes.getLength() >= 0);
        foundDesiredElement = true;
        break;
      }
    }
    assertTrue(foundDesiredElement);  // if we didn't even find the noinput element, then something terrible went wrong
  }
  
  public void testXSITypeVsTypeAttr() 
  {
      
    // See bug 225447, 225819
  
    // Load the XSD file
    String XSD_FILE_NAME = "XSITypeTest.xsd";
    String fileURI = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + XSD_FILE_NAME;
    CMDocumentFactoryXSD factory = new CMDocumentFactoryXSD();
    assertNotNull("Assert factory is not null", factory);
    CMDocument cmDocument = factory.createCMDocument(fileURI);
    assertNotNull("Assert CMDocument is not null", cmDocument);
  
    // Check and obtain the two global elements (elementA and elementB)
    CMNamedNodeMap elements = cmDocument.getElements();
    assertEquals(elements.getLength(), 2);
    CMElementDeclaration cmElementDeclaration = (CMElementDeclaration)elements.item(0);
    CMElementDeclaration cmElementDeclarationA = null;
    CMElementDeclaration cmElementDeclarationB = null;
    if ("elementA".equals(cmElementDeclaration.getElementName()))
    {
      cmElementDeclarationA = cmElementDeclaration;
      cmElementDeclarationB = (CMElementDeclaration)elements.item(1);
    }
    else
    {
      cmElementDeclarationB = cmElementDeclaration;
      cmElementDeclarationA = (CMElementDeclaration)elements.item(1);
    }
  
    // elementA has a "type" attribute with "X" enumerated value, make sure it appears in the model
    CMNamedNodeMap attributesA = cmElementDeclarationA.getAttributes();
    assertEquals(attributesA.getLength(), 1);
    CMAttributeDeclaration cmAttributeDeclarationA = (CMAttributeDeclaration)attributesA.item(0);
    assertEquals("type", cmAttributeDeclarationA.getAttrName());
    CMDataType attrTypeA = cmAttributeDeclarationA.getAttrType();
    String[] enumeratedValuesA = attrTypeA.getEnumeratedValues();
    assertEquals(1, enumeratedValuesA.length);
    assertEquals("X", enumeratedValuesA[0]);
  
    // elementB does not have a "type" attribute, make sure the xsi:type appears in the model
    CMNamedNodeMap attributesB = cmElementDeclarationB.getAttributes();
    assertEquals(attributesB.getLength(), 1);
    CMAttributeDeclaration cmAttributeDeclarationB = (CMAttributeDeclaration)attributesB.item(0);
    assertEquals("type", cmAttributeDeclarationB.getAttrName());
    CMDataType attrTypeB = cmAttributeDeclarationB.getAttrType();
    assertEquals("typeNames", attrTypeB.getDataTypeName());
  }

  public void testEnumerationsInComplexTypesWithSimpleContent()
  {
    // See bug 215514

    // Obtain the Web Application schema
    String vxmlSchemaURI = locateFileUsingCatalog("http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd");
    XSDSchema xsdSchema = XSDImpl.buildXSDModel(vxmlSchemaURI);
    assertNotNull(xsdSchema);

    // The type transport-guaranteeType is defined as a complex type with simple type content
    // It has 3 enumerated values
    String typeName = "transport-guaranteeType";
    for (Iterator<XSDTypeDefinition> types = xsdSchema.getTypeDefinitions().iterator(); types.hasNext();)
    {
      XSDTypeDefinition type = types.next();
      if (type instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition)type;
        if (typeName.equals(complexType.getName()))
        {
          String[] enumeratedValuesForType = XSDImpl.getEnumeratedValuesForType(complexType);
          // Ensure that the 3 enumerated values are returned
          assertEquals(3, enumeratedValuesForType.length);
          return;
        }
      }
    }
  }
}