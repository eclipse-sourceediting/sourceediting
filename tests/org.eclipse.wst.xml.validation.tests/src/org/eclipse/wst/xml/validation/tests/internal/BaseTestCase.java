/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.validation.tests.internal;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.wst.xml.validation.internal.core.ValidationMessage;
import org.eclipse.wst.xml.validation.internal.core.ValidationReport;
import org.eclipse.wst.xml.validation.internal.ui.eclipse.XMLValidator;


/**
 * Base test case class which provides methods to 
 * - create logs
 * - read from logs
 * - run log comparison tests
 * 
 * @author Lawrence Mandel, IBM
 */
public class BaseTestCase extends TestCase
{
  protected String FILE_PROTOCOL = "file:///"; 
  protected String PLUGIN_ABSOLUTE_PATH;
  protected String SAMPLES_DIR = "testresources/samples/";
  protected XMLValidator validator = XMLValidator.getInstance();
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
  {
    PLUGIN_ABSOLUTE_PATH = XMLValidatorTestsPlugin.getPluginLocation().toString() + "/";
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
    ValidationReport valreport = validator.validate(testfile);
	
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
