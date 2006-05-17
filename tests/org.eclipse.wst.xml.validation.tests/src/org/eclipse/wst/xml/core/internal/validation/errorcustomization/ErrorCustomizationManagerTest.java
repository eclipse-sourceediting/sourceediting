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
package org.eclipse.wst.xml.core.internal.validation.errorcustomization;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;
import org.eclipse.wst.xml.core.internal.validation.XMLValidator;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.errorcustomization.ErrorCustomizationManager.ErrorMessageInformation;
import org.eclipse.wst.xml.validation.tests.internal.XMLValidatorTestsPlugin;

/**
 * Test the ErrorCustomizationManager class.
 */
public class ErrorCustomizationManagerTest extends TestCase 
{
	
  /**
   * Test the startElement method with the following tests:
   * 1. Test that the root element is properly pushed to the stack.
   * 2. Test that a subsequent element is properly pushed to the stack and
   *    registered as a child element.
   */
  public void testStartElement()
  {
	// 1. Test that the root element is properly pushed to the stack.
	String namespace1 = "http://namespace1";
	String localname1 = "localname";
	ErrorCustomizationManagerWrapper manager = new ErrorCustomizationManagerWrapper();
	assertEquals("1. The element information stack is not empty to start.", 0, manager.getElementInformationStack().size());
	manager.startElement(namespace1, localname1);
	ElementInformation elemInfo = (ElementInformation)manager.getElementInformationStack().pop();
	assertEquals("1. The element information stack is not empty to after popping the top element.", 0, manager.getElementInformationStack().size());
	assertEquals("1. The namespace specified on the element information is not http://namespace1 it is " + elemInfo.getNamespace(), namespace1, elemInfo.getNamespace());
	assertEquals("1. The local name specified on the element information is not localname it is " + elemInfo.getLocalname(), localname1, elemInfo.getLocalname());
	
	// 2. Test that a subsequent element is properly pushed to the stack and registered as a child element.
	String localname2 = "localname2";
    ErrorCustomizationManagerWrapper manager2 = new ErrorCustomizationManagerWrapper();
	assertEquals("2. The element information stack is not empty to start.", 0, manager2.getElementInformationStack().size());
	manager2.startElement(namespace1, localname1);
	manager2.startElement(namespace1, localname2);
	assertEquals("2. The element information stack does not contain 2 elements.", 2, manager2.getElementInformationStack().size());
	ElementInformation elemInfo2 = (ElementInformation)manager2.getElementInformationStack().pop();
	assertEquals("2. The element information stack does not contain 1 element after popping the top element.", 1, manager2.getElementInformationStack().size());
	assertEquals("2. The namespace specified on the element information child is not http://namespace1 it is " + elemInfo2.getNamespace(), namespace1, elemInfo2.getNamespace());
	assertEquals("2. The local name specified on the element information child is not localname2 it is " + elemInfo2.getLocalname(), localname2, elemInfo2.getLocalname());
	ElementInformation elemInfo3 = (ElementInformation)manager2.getElementInformationStack().pop();
	assertEquals("2. The element information stack does not contain 0 elements after popping the top element.", 0, manager2.getElementInformationStack().size());
	assertEquals("2. The namespace specified on the element information is not http://namespace1 it is " + elemInfo3.getNamespace(), namespace1, elemInfo3.getNamespace());
	assertEquals("2. The local name specified on the element information is not localname it is " + elemInfo3.getLocalname(), localname1, elemInfo3.getLocalname());
	assertEquals("2. The element information does not contain 1 child element it contains " + elemInfo3.getChildren().size(), 1, elemInfo3.getChildren().size());
	ElementInformation elemInfo4 = (ElementInformation)elemInfo3.getChildren().get(0);
	assertEquals("2. The namespace specified on the element information's child is not http://namespace1 it is " + elemInfo4.getNamespace(), namespace1, elemInfo4.getNamespace());
	assertEquals("2. The local name specified on the element information's child is not localname2 it is " + elemInfo4.getLocalname(), localname2, elemInfo4.getLocalname());
  }
  
  /**
   * Test the endElement method with the following tests:
   * 1. Check that the last element on the stack is successfully popped.
   * 2. Test that an element in a sample namespace produces the expected message.
   * 3. Test that an element not in a sample namespace retains its value.
   */
  public void testEndElement()
  {
	// 1. Check that the last element on the stack is successfully popped.
	String namespace1 = "http://namespace1";
    String localname1 = "localname";
	ErrorCustomizationManagerWrapper manager = new ErrorCustomizationManagerWrapper();
	manager.getElementInformationStack().push(new ElementInformation(namespace1, localname1));
	manager.endElement(namespace1, localname1);
	assertEquals("1. The stack still contains an element.", 0, manager.getElementInformationStack().size());
	
	// 2. Test that an element in a sample namespace produces the expected message.
	String namespace2 = "XMLValidationTestSampleNamespace";
	ErrorCustomizationRegistry.getInstance().addErrorMessageCustomizer(namespace2, new SampleErrorMessageCustomizer());
	ErrorCustomizationManagerWrapper manager2 = new ErrorCustomizationManagerWrapper();
	manager2.getElementInformationStack().push(new ElementInformation(namespace2, localname1));
	ErrorMessageInformation emi = manager2.new ErrorMessageInformation();
	emi.message = new ValidationMessage("SampleMessage", 1, 2, namespace2);
	manager2.setMessageForConsideration(emi);
	manager2.endElement(namespace2, localname1);
	assertEquals("2. The message was not customized to AAAA. The message is " + emi.message.getMessage(), "AAAA", emi.message.getMessage());
	
	// 3. Test that an element not in a sample namespace retains its value.
	String namespace3 = "XMLValidationTestSampleNamespace3";
	ErrorCustomizationManagerWrapper manager3 = new ErrorCustomizationManagerWrapper();
	manager3.getElementInformationStack().push(new ElementInformation(namespace3, localname1));
	ErrorMessageInformation emi2 = manager3.new ErrorMessageInformation();
	emi2.message = new ValidationMessage("SampleMessage", 1, 2, namespace3);
	manager3.setMessageForConsideration(emi2);
	manager3.endElement(namespace3, localname1);
	assertEquals("3. The message did not retain its value of SampleMessage. The message is " + emi2.message.getMessage(), "SampleMessage", emi2.message.getMessage());
  }
  
  /**
   * Test the considerReportedError method with the following tests:
   * 1. Check that the messageForConsideration is not set if there are no current
   *    validation messages.
   * 2. Check that the messageForConsideration is set correctly if there is a
   *    validation message.
   */
  public void testConsiderReportedError()
  {
	// 1. Check that the messageForConsideration is not set if there are no current validation messages.
	String namespace1 = "http://namespace1";
	ErrorCustomizationManagerWrapper manager = new ErrorCustomizationManagerWrapper();
	ValidationInfo valinfo = new ValidationInfo(namespace1);
	manager.considerReportedError(valinfo, "key", null);
	assertNull("1. The messageForConsideration is not null when no validation messages exist.", manager.getMessageForConsideration());
	
	// 2. Check that the messageForConsideration is set correctly if there is a validation message.
	ErrorCustomizationManagerWrapper manager2 = new ErrorCustomizationManagerWrapper();
	ValidationInfo valinfo2 = new ValidationInfo(namespace1);
	valinfo2.addError("message", 1, 1, namespace1);
	manager2.considerReportedError(valinfo2, "key", null);
	assertNotNull("2. The messageForConsideration is null when a validation message exists.", manager2.getMessageForConsideration());
  }
  
  /**
   * Test that an error customizer is only called for the
   * correct element.
   */
  public void testErrorReportedOnCorrectElement()
  {
	  IErrorMessageCustomizer testCustomizer = new IErrorMessageCustomizer()
	  {
		  public String customizeMessage(ElementInformation elementInfo, String key, Object[] arguments)
		  {
			  if(elementInfo.getLocalname().equals("child1"))
			  {
				  fail("An error was reported for the child1 element.");
			  }
			  return null;
		  }
	  };
	  ErrorCustomizationRegistry registry = ErrorCustomizationRegistry.getInstance();
	  registry.addErrorMessageCustomizer("http://www.example.org/simplenested", testCustomizer);
	  try
	  {
		  String PLUGIN_ABSOLUTE_PATH = XMLValidatorTestsPlugin.getPluginLocation().toString() + "/";
		  String uri = "file:///" + PLUGIN_ABSOLUTE_PATH + "testresources/samples/bugfixes/CustomErrorReportedOnCorrectElement/simplenested.xml";
		  
		  XMLValidator validator = new XMLValidator();
		  validator.validate(uri, null, new XMLValidationConfiguration());
		  validator.validate(uri, null, new XMLValidationConfiguration());
	  }
	  catch(IOException e)
	  {
		  fail("An exception occurred while running the test:" + e);
	  }
  }
}
