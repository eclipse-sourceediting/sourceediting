/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - bug 157254 
 *******************************************************************************/

package org.eclipse.wst.xsd.core.tests.internal;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xsd.contentmodel.internal.CMDocumentFactoryXSD;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl.DocumentationImpl;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.contentmodel.internal.XSDImpl.XSDModelGroupAdapter;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.osgi.framework.Bundle;

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
    String namespaceURI = "http://www.w3.org/TR/voicexml20/vxml.xsd";
	String vxmlSchemaURI = locateFileUsingCatalog(namespaceURI);
    
    // See bug 206138
    
    // Two ways to test this.
    // First way. Call findTypesDerivedFrom from XSDImpl.
    
    assertNotNull("unable to locate file for " + namespaceURI, vxmlSchemaURI);
    assertTrue("unable to locate file for " + namespaceURI, vxmlSchemaURI.length() > 0);
    XSDSchema xsdSchema = XSDImpl.buildXSDModel(vxmlSchemaURI);
    assertNotNull("failed to build model for " + vxmlSchemaURI,xsdSchema);
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
          assertTrue("no types found in XSD", size >= 0);
          // Because of bug 203048, there is a change in behaviour to redefined types.  
          // The complex type named speaker is no longer circular.   In terms of this junit, the value returned is not relevant
          // since we just want some length back (i.e. there was no crash from a stack overflow).
          break;
        }
      }
    }
    assertTrue("type \"basic.event.handler\" not found in XSD", foundDesiredType);  // if we didn't even find the complex type, then something terrible went wrong
    
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
    assertTrue("element \"noinput\"r not found in XSD", foundDesiredElement);  // if we didn't even find the noinput element, then something terrible went wrong
  }
  
  public void testXSDTypeWhitespaceFacets() {
	  // Bug [194698] - Test that the correct whitespace facets are applied to the types
	  String XSD_FILE_NAME = "XSDWhitespace.xsd";
	  String fileURI = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + XSD_FILE_NAME;
	  CMDocumentFactoryXSD factory = new CMDocumentFactoryXSD();
	  assertNotNull("Assert factory is not null", factory);
	  CMDocument cmDocument = factory.createCMDocument(fileURI);
	  assertNotNull("Assert CMDocument is not null", cmDocument);
	  CMElementDeclaration elemDecl = (CMElementDeclaration)cmDocument.getElements().item(0);
	  assertEquals("test", elemDecl.getNodeName());
	  assertTrue(elemDecl.getContent() instanceof XSDModelGroupAdapter);
	  XSDModelGroupAdapter group = (XSDModelGroupAdapter) elemDecl.getContent();
	  CMNodeList list = group.getChildNodes();
	  XSDElementDeclarationAdapter adapter = null;
	  
	  String nodeName = null, expected = null;
	  CMDataType type = null;
	  // Iterate over the child nodes of the element, examining the whitespace facets */
	  for(int i = 0; i < list.getLength(); i++) {
		  adapter = (XSDElementDeclarationAdapter) list.item(i);

		  nodeName = adapter.getNodeName();
		  assertNotNull(nodeName);
		  assertTrue(nodeName.contains("-"));
		  type = adapter.getDataType();
		  assertNotNull(type);
		  
		  expected = nodeName.substring(nodeName.indexOf('-') + 1);
		  assertEquals(expected, type.getProperty(XSDImpl.PROPERTY_WHITESPACE_FACET));
	  }
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
  
  public void testGlobalElementDocumentation()
  {
    // See bug 157254

	Bundle bundle = Platform.getBundle("org.eclipse.wst.xsd.core.tests");
	URL url = bundle.getEntry("/testresources/samples/documentation/globalreftest.xsd");
    
	CMDocument document = XSDImpl.buildCMDocument(url.toExternalForm());
	assertNotNull("Content model loaded Null", document);
    
    CMNamedNodeMap elements = document.getElements();
    
    CMElementDeclaration node =  (CMElementDeclaration)elements.getNamedItem("rootTest");
	assertNotNull("Missing rootElement", node);

    CMElementDeclaration testElement = (CMElementDeclaration)node.getLocalElements().getNamedItem("test");
	assertNotNull("Missing test element", testElement);

    CMNodeList documentation = (CMNodeList)testElement.getProperty("documentation");
    if (documentation.getLength() == 0) {
    	fail("test global element missing documentation.");
    }
    for (int cnt = 0; cnt < documentation.getLength(); cnt++) {
    	DocumentationImpl doc = (DocumentationImpl) documentation.item(cnt);
    	assertEquals("Test global element missing documentation.", "This some global documentation", doc.getValue());
    }
    
    testElement = (CMElementDeclaration)node.getLocalElements().getNamedItem("testElement");
    documentation = (CMNodeList)testElement.getProperty("documentation");
    if (documentation.getLength() == 0) {
    	fail("testElement local element missing documentation.");
    }
    for (int cnt = 0; cnt < documentation.getLength(); cnt++) {
    	DocumentationImpl doc = (DocumentationImpl) documentation.item(cnt);
    	assertEquals("testElement documentation wrong.", "This is an override", doc.getValue());
    }    
  }  

  public void testGlobalAtrributeDocumentation()
  {
    // See bug 157254

	Bundle bundle = Platform.getBundle("org.eclipse.wst.xsd.core.tests");
	URL url = bundle.getEntry("/testresources/samples/documentation/TestAttributeRefs.xsd");
    
	CMDocument document = XSDImpl.buildCMDocument(url.toExternalForm());
	assertNotNull("Content model loaded Null", document);
    
    CMNamedNodeMap elements = document.getElements();
    
    CMElementDeclaration node =  (CMElementDeclaration)elements.getNamedItem("object");
	assertNotNull("Missing object element", node);
	
	CMNamedNodeMap attributes =  node.getAttributes();
	testGlobalAttr1Documentation(attributes);
	testGlobalAttr2Documentation(attributes);
	testGlobalAttr3Documentation(attributes);
	testGlobalAttr4Documentation(attributes);
	testLocalAttrDocumentation(attributes);
	
 }
  
  public void testForBug176420() {
	  // Obtain the model from /testresources/samples/testSchemaForBug176420.xsd
	  Bundle bundle = Platform.getBundle("org.eclipse.wst.xsd.core.tests");
	  URL url = bundle.getEntry("/testresources/samples/testSchemaForBug176420.xsd");
	  CMDocument document = XSDImpl.buildCMDocument(url.toExternalForm());
	  assertNotNull("Content model loaded Null", document);
	  
	  // Obtain the enumerated values of the root element
	  CMNode cmNode = document.getElements().item(0);
	  String[] enumeratedValues = ((CMElementDeclaration)cmNode).getDataType().getEnumeratedValues();

	  // Verify that all 12 enumerated values are included
	  assertEquals(12, enumeratedValues.length);
  }
  
	private void testLocalAttrDocumentation(CMNamedNodeMap attributes) {
		CMAttributeDeclaration attribute = (CMAttributeDeclaration) attributes.getNamedItem("localAttr");
		assertNotNull("Missing localAttr attribute.");
		
		CMNodeList documentation = (CMNodeList)attribute.getProperty("documentation");
		if (documentation.getLength() == 0) {
			fail("Unable to find documentation for localAttr");
		}
		assertEquals("Wrong number of documentation annotations.", 2, documentation.getLength());
		assertEquals("Incorrect annotation for localAttr:",
					 "PASS! Multiple documentation elements for local attribute part 1",
					 ((DocumentationImpl)documentation.item(0)).getValue().trim());
		assertEquals("Incorrect annotation for localAttr:",
					 "PASS! Multiple documentation elements for local attribute part 2",
				     ((DocumentationImpl)documentation.item(1)).getValue().trim());
	}


	private void testGlobalAttr1Documentation(CMNamedNodeMap attributes) {
		CMAttributeDeclaration attribute = (CMAttributeDeclaration) attributes.getNamedItem("globalAttr1");
		assertNotNull("Missing globalAttr1 attribute.");
		
		CMNodeList documentation = (CMNodeList)attribute.getProperty("documentation");
		if (documentation.getLength() == 0) {
			fail("Unable to find documentation for globalAttr1");
		}
		assertEquals("Wrong number of documentation annotations.", 2, documentation.getLength());
		assertEquals("Incorrect first annotation for globalAttr1:",
		"PASS! Documentation for attribute ref overrides the resolved attribute ref documentation",
		((DocumentationImpl)documentation.item(0)).getValue().trim());
		
		assertEquals("Incorrect second annotation for globalAttr1:",
		"PASS! Multiple documentation elements.",
		((DocumentationImpl)documentation.item(1)).getValue().trim());
	}  

	private void testGlobalAttr2Documentation(CMNamedNodeMap attributes) {
		CMAttributeDeclaration attribute = (CMAttributeDeclaration) attributes.getNamedItem("globalAttr2");
		assertNotNull("Missing globalAttr1 attribute.");
		
		CMNodeList documentation = (CMNodeList)attribute.getProperty("documentation");
		if (documentation.getLength() == 0) {
			fail("Unable to find documentation for globalAttr2");
		}
		assertEquals("Wrong number of documentation annotations.", 1, documentation.getLength());
		assertEquals("Incorrect annotation for globalAttr2:",
		"PASS! Documentation for resolved attribute ref when the attribute ref does not have documentation",
		((DocumentationImpl)documentation.item(0)).getValue().trim());
	}
	
	private void testGlobalAttr3Documentation(CMNamedNodeMap attributes) {
		CMAttributeDeclaration attribute = (CMAttributeDeclaration) attributes.getNamedItem("globalAttr3");
		assertNotNull("Missing globalAttr1 attribute.");
		
		CMNodeList documentation = (CMNodeList)attribute.getProperty("documentation");
		if (documentation.getLength() == 0) {
			fail("Unable to find documentation for globalAttr3");
		}
		assertEquals("Wrong number of documentation annotations.", 1, documentation.getLength());
		assertEquals("Incorrect annotation for globalAttr3:",
		"PASS! Documentation for resolved attribute ref when the attribute ref has an annotation but does not have documentation",
		((DocumentationImpl)documentation.item(0)).getValue().trim());
	}
	
	private void testGlobalAttr4Documentation(CMNamedNodeMap attributes) {
		CMAttributeDeclaration attribute = (CMAttributeDeclaration) attributes.getNamedItem("globalAttr4");
		assertNotNull("Missing globalAttr1 attribute.");
		
		CMNodeList documentation = (CMNodeList)attribute.getProperty("documentation");
		if (documentation.getLength() == 0) {
			fail("Documentation element not returned for globalAttr4");
		}
		assertNull("globalAttr4 returned data when non expected.", ((DocumentationImpl)documentation.item(0)).getValue());
	}

	/**
	 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=322841
	 */
	public void testBase64BinaryDefaultValue()
	{
	  Bundle bundle = Platform.getBundle(XSDCoreTestsPlugin.PLUGIN_ID);
	  URL url = bundle.getEntry("/testresources/samples/base64Binary/Test.xsd"); //$NON-NLS-1$

	  CMDocument cmDocument = XSDImpl.buildCMDocument(url.toExternalForm());
	  assertNotNull(cmDocument);

	  CMNamedNodeMap elements = cmDocument.getElements();

	  CMElementDeclaration cmElementDeclaration = (CMElementDeclaration)elements.getNamedItem("Test"); //$NON-NLS-1$
	  assertNotNull(cmElementDeclaration);

	  CMDataType dataType = cmElementDeclaration.getDataType();
	  assertNotNull(dataType);

	  String impliedValue = dataType.generateInstanceValue();
	  assertEquals("MA==", impliedValue); //$NON-NLS-1$
	}  		
}