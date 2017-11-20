/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.core.internal.validation.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsd.validation.tests.internal.XSDValidationTestsPlugin;

/**
 * A test class for the Validator class.
 */
public class ValidatorTest extends TestCase 
{
  ValidatorWrapper validator = new ValidatorWrapper();
	  
  /**
   * Create a tests suite from this test class.
   * 
   * @return A test suite containing this test class.
   */
  public static Test suite()
  {
    return new TestSuite(ValidatorTest.class);
  }
	  
  /**
   * Test the addInfoToMessage method. The following tests are performed:<br/>
   * 1. When the validation message contains a null key nothing is added to the message.<br/>
   * 2. When the message contains the key "ENTIRE_ELEMENT" the three attributes are added
   * to the method and the SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE is null.<br/>
   * 3. When the message contains the key "EntityNotDeclared" the three attributes are set.
   */
  public void testAddInfoToMessage()
  {
    // These strings are common addition information types.
	String COLUMN_NUMBER_ATTRIBUTE = "columnNumber"; //$NON-NLS-1$
	String SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = "squiggleSelectionStrategy"; //$NON-NLS-1$
	String SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = "squiggleNameOrValue"; //$NON-NLS-1$
	
	// Test that the message does not contain the attributes when the method is called
	// with a null key.
	ValidationMessage validationMessage = new ValidationMessage("", 1, 1, "");
	Message message = new Message();
	validator.addInfoToMessage(validationMessage, message);
	assertNull("COLUMN_NUMBER_ATTRIBUTE was not null for a ValidationMessage with a null key. COLUMN_NUMBER_ATTRIBUTE = " + message.getAttribute(COLUMN_NUMBER_ATTRIBUTE), message.getAttribute(COLUMN_NUMBER_ATTRIBUTE));
	assertNull("SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE was not null for a ValidationMessage with a null key. SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = " + message.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE), message.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE));
	assertNull("SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE was not null for a ValidationMessage with a null key. SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = " + message.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE), message.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE));
	
	// Test that the message contains the three attributes when the key is set to "s4s-elt-character".
	ValidationMessage validationMessage2 = new ValidationMessage("", 1, 1, "", "s4s-elt-character", null);
	Message message2 = new Message();
	validator.addInfoToMessage(validationMessage2, message2);
	assertEquals("COLUMN_NUMBER_ATTRIBUTE was correctly set to 1 for a ValidationMessage with the key s4s-elt-character. COLUMN_NUMBER_ATTRIBUTE = " + message2.getAttribute(COLUMN_NUMBER_ATTRIBUTE), new Integer(1), message2.getAttribute(COLUMN_NUMBER_ATTRIBUTE));
	assertEquals("SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE was not TEXT. SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = " + message2.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE), "TEXT", message2.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE));
	assertEquals("SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE was not an empty string for a ValidationMessage with the key s4s-elt-character. SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = " + message2.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE), "", message2.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE));
	
    // Test that the message contains the three attributes when the key is set to "src-resolve".
	ValidationMessage validationMessage3 = new ValidationMessage("'MyName'", 1, 1, "", "src-resolve", null);
	Message message3 = new Message();
	validator.addInfoToMessage(validationMessage3, message3);
	assertEquals("COLUMN_NUMBER_ATTRIBUTE was correctly set to 1 for a ValidationMessage with the key src-resolve. COLUMN_NUMBER_ATTRIBUTE = " + message3.getAttribute(COLUMN_NUMBER_ATTRIBUTE), new Integer(1), message3.getAttribute(COLUMN_NUMBER_ATTRIBUTE));
	assertEquals("SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE was not VALUE_OF_ATTRIBUTE_WITH_GIVEN_VALUE. SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = " + message3.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE), "VALUE_OF_ATTRIBUTE_WITH_GIVEN_VALUE", message3.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE));
	assertEquals("SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE was not set to MyName for a ValidationMessage with the key src-resolve. SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = " + message3.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE), "MyName", message3.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE));
  }
  
  /**
   * Test the validate method. Tests to be performed:<br/>
   * 1. Test that validating a valid file from a URI or an input stream produces the same result.<br/>
   * 2. Test that validating an invalid file from a URI or an input stream produces the same result.
   */
  public void testValidate()
  {
	try
	{
	  // Test that validating a valid file from a URI and an input stream produces the same result.
	  String PLUGIN_ABSOLUTE_PATH = XSDValidationTestsPlugin.getInstallURL();
	  String uri = "file:///" + PLUGIN_ABSOLUTE_PATH + "testresources/samples/Paths/Dash-InPath/DashInPathValid.xsd";
	  ValidationReport report1 = validator.validate(uri, null, new NestedValidatorContext());
	  ValidationReport report2 = null;
	  InputStream is = null;
	  try
	  {
	    is = new URL(uri).openStream();
	    report2 = validator.validate(uri, is, new NestedValidatorContext());
	  }
	  catch(Exception e)
	  {
		fail("A problem occurred while validating a valid file with an inputstream: " + e);
	  }
	  finally
	  {
		if(is != null)
		{
		  try
		  {
		    is.close();
		  }
		  catch(IOException e)
		  {
			// Do nothing.
		  }
		}
	  }
	  assertTrue("Validation using a URI did not product a valid validation result.", report1.isValid());
	  assertEquals("Validation using URI and using inputstream of the same file produces different numbers of errors.", report1.getValidationMessages().length, report2.getValidationMessages().length);
	  
      // Test that validating an invalid file from a URI and an input stream produces the same result.
	  uri = "file:///" + PLUGIN_ABSOLUTE_PATH + "testresources/samples/Paths/Dash-InPath/DashInPathInvalid.xsd";
	  report1 = validator.validate(uri, null, new NestedValidatorContext());
	  report2 = null;
	  is = null;
	  try
	  {
	    is = new URL(uri).openStream();
	    report2 = validator.validate(uri, is, new NestedValidatorContext());
	  }
	  catch(Exception e)
	  {
		fail("A problem occurred while validating an invalid file with an inputstream: " + e);
	  }
	  finally
	  {
		if(is != null)
		{
		  try
		  {
		    is.close();
		  }
		  catch(IOException e)
		  {
			// Do nothing.
		  }
		}
	  }
	  assertFalse("Validation using a URI did not product an invalid validation result.", report1.isValid());
	  assertEquals("Validation using URI and using inputstream of the same file produces different numbers of errors.", report1.getValidationMessages().length, report2.getValidationMessages().length);
	}
	catch(Exception e)
	{
	  fail("Unable to locate plug-in location: " + e);
	}
  }
}
