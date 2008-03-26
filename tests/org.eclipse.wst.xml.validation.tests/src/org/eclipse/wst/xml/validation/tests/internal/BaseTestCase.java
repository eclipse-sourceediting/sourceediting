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

package org.eclipse.wst.xml.validation.tests.internal;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.wst.xml.core.internal.validation.XMLValidationConfiguration;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.core.internal.validation.eclipse.XMLValidator;


/**
 * Base test case class which provides methods to 
 * - create logs
 * - read from logs
 * - run log comparison tests
 */
public class BaseTestCase extends TestCase
{
  protected String FILE_PROTOCOL = "file:///"; 
  protected String PLUGIN_ABSOLUTE_PATH;
  protected String SAMPLES_DIR = "testresources/samples/";
  protected XMLValidator validator = XMLValidator.getInstance();
  protected XMLValidationConfiguration configuration;
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws IOException
  {
    PLUGIN_ABSOLUTE_PATH = XMLValidatorTestsPlugin.getPluginLocation().toString() + "/";
    configuration = new XMLValidationConfiguration();
    try
    {
      configuration.setFeature(XMLValidationConfiguration.WARN_NO_GRAMMAR, false);
	  configuration.setFeature(XMLValidationConfiguration.INDICATE_NO_GRAMMAR, 0);
    }
    catch(Exception e)
    {
      fail("Unable to set the feature on the XML validation configuration.");
    }
  }
  
  /**
   * Run a validator test. The test will run the validator, and compare the validation result with
   * the information specified.
   * 
   * @param testfile The file to run the validator test on.
   * @param keys The list of allows message keys.
   * @param numErrors The number of expected errors.
   * @param numWarnings The number of expected warnings.
   */
  public void runTest(String testfile, List keys, int numErrors, int numWarnings)
  {
    ValidationReport valreport = validator.validate(testfile, null, configuration);
	
	ValidationMessage[] valmessages = valreport.getValidationMessages();
    int nummessages = valmessages.length;
	
	int errorCount = 0;
	int warningCount = 0;

    for(int i = 0; i < nummessages; i++)
    {
      ValidationMessage valmes = valmessages[i];
	  String key = valmes.getKey();
	  assertTrue("The message key " + key + " is not correct.", keys.contains(key));
      if(valmes.getSeverity() == ValidationMessage.SEV_LOW)
      {
        warningCount++;
      }
      else
      {
        errorCount++;
      }
    }
    assertEquals(errorCount + " errors were reported but " + numErrors + " errors were expected.", numErrors, errorCount);
	assertEquals(warningCount + " warnings were reported but " + numWarnings + " warnings were expected.", numWarnings, warningCount);
  }
  
  
}
