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
          assertTrue(size == 1);  // if we got something back, then great, there was no out of stack error
          break;
        }
      }
    }
    assertTrue(foundDesiredType);  // if we didn't even find the binding element, then something terrible went wrong
    
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
        assertTrue(attributes.getLength() == 3);  // if we got something back, then great, there was no out of stack error
        foundDesiredElement = true;
        break;
      }
    }
    assertTrue(foundDesiredElement);  // if we didn't even find the binding element, then something terrible went wrong
  }

}