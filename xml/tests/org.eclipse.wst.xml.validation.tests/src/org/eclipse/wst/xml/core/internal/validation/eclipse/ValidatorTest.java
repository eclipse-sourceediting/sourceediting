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
package org.eclipse.wst.xml.core.internal.validation.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.validation.tests.internal.XMLValidatorTestsPlugin;

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
	
	// Test that the message contains the three attributes when the key is set to "ElementUnterminated".
	ValidationMessage validationMessage2 = new ValidationMessage("", 1, 1, "", "ElementUnterminated", null);
	Message message2 = new Message();
	validator.addInfoToMessage(validationMessage2, message2);
	assertEquals("COLUMN_NUMBER_ATTRIBUTE was correctly set to 1 for a ValidationMessage with the key ENTIRE_ELEMENT. COLUMN_NUMBER_ATTRIBUTE = " + message2.getAttribute(COLUMN_NUMBER_ATTRIBUTE), new Integer(1), message2.getAttribute(COLUMN_NUMBER_ATTRIBUTE));
	assertEquals("SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE was not ENTIRE_ELEMENT. SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = " + message2.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE), "ENTIRE_ELEMENT", message2.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE));
	assertNull("SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE was not null for a ValidationMessage with the key ENTIRE_ELEMENT. SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = " + message2.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE), message2.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE));
	
    // Test that the message contains the three attributes when the key is set to "EntityNotDeclared".
	ValidationMessage validationMessage3 = new ValidationMessage("", 1, 1, "", "EntityNotDeclared", new Object[]{"MyName"});
	Message message3 = new Message();
	validator.addInfoToMessage(validationMessage3, message3);
	assertEquals("COLUMN_NUMBER_ATTRIBUTE was correctly set to 1 for a ValidationMessage with the key EntityNotDeclared. COLUMN_NUMBER_ATTRIBUTE = " + message3.getAttribute(COLUMN_NUMBER_ATTRIBUTE), new Integer(1), message3.getAttribute(COLUMN_NUMBER_ATTRIBUTE));
	assertEquals("SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE was not TEXT_ENTITY_REFERENCE. SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = " + message3.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE), "TEXT_ENTITY_REFERENCE", message3.getAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE));
	assertEquals("SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE was not set to MyName for a ValidationMessage with the key EntityNotDeclared. SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = " + message3.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE), "MyName", message3.getAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE));
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
	  String PLUGIN_ABSOLUTE_PATH = XMLValidatorTestsPlugin.getPluginLocation().toString() + "/";
	  String uri = "file:///" + PLUGIN_ABSOLUTE_PATH + "testresources/samples/XMLExamples/PublicationCatalogue/Catalogue.xml";
	  ValidationReport report1 = validator.validate(uri, null, null);
	  ValidationReport report2 = null;
	  InputStream is = null;
	  try
	  {
	    is = new URL(uri).openStream();
	    report2 = validator.validate(uri, is, null);
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
	  uri = "file:///" + PLUGIN_ABSOLUTE_PATH + "testresources/samples/Paths/Dash-InPath/DashInPathInvalid.xml";
	  report1 = validator.validate(uri, null, null);
	  report2 = null;
	  is = null;
	  try
	  {
	    is = new URL(uri).openStream();
	    report2 = validator.validate(uri, is, null);
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
  
  /**
   * Test that the warn no grammar preference is read from the XML core preferences.
   * There are three tests to perform.
   * 1. Test that the default preference is enabled.
   * 2. Test that setting the preference to disabled works.
   * 3. Test that setting the preference to enabled works.
   */
  public void testWarnNoGrammarPreference()
  {
	// Test that the default preference is disabled.
	validator.setupValidation(null);
	assertNotSame("The default warn no grammar preference is not enabled.", new Integer(0), new Integer(validator.getIndicateNoGrammarPreference()));
	
	// Test that the preference is read when disabled.
	XMLCorePlugin.getDefault().getPluginPreferences().setValue(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR, 0);
	validator.setupValidation(null);
	assertEquals("The warn no grammar preference is not ignore when the preference is set to ignore.", 0, validator.getIndicateNoGrammarPreference());

	// Test that the preference is read when enabled.
	XMLCorePlugin.getDefault().getPluginPreferences().setValue(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR, 2);
	validator.setupValidation(null);
	assertEquals("The warn no grammar preference is not error when the preference is set to error.", 2, validator.getIndicateNoGrammarPreference());
	
  }

}
