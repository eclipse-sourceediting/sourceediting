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
package org.eclipse.wst.xml.validation.tests.internal;

import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;

/**
 * A test class to test the line number adjustments made in the XML validator.
 */
public class LineNumberAdjustmentsTest extends BaseTestCase
{
	private static final String LINE_NUMBER_ADJUSTMENTS_DIR = "LineNumberAdjustments/";
	
	/**
	 * Run a validator test. The test will run the validator, and compare the validation result with
	 * the information specified.
	 * 
	 * @param testfile 
	 * 			The file to run the validator test on.
	 * @param key 
	 * 			The message key.
	 * @param numErrors 
	 * 			The number of expected errors.
	 * @param numWarnings 
	 * 			The number of expected warnings.
	 * @param lineno
	 * 			The line number the message should be located on.
	 * @param columnno
	 * 			The column number the message should be located on.
	 */
	public void runTest(String testfile, String key, int numErrors, int numWarnings, int lineno, int columnno)
	{
	  ValidationReport valreport = validator.validate(testfile, null, configuration);
		
      ValidationMessage[] valmessages = valreport.getValidationMessages();
	  int nummessages = valmessages.length;
		
      int errorCount = 0;
	  int warningCount = 0;

	  for(int i = 0; i < nummessages; i++)
	  {
	    ValidationMessage valmes = valmessages[i];
		String messkey = valmes.getKey();
		assertTrue("The message key " + key + " is not correct.", key.equals(messkey));
		assertEquals("The line number is incorrect.", lineno, valmes.getLineNumber());
		assertEquals("The column number is incorrect.", columnno, valmes.getColumnNumber());
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
	
	/**
	 * Test /LineNumberAdjustments/cvc-complex-type.2.3/cvc-complex-type.2.3.xml.
	 */
	public void testcvccomplextype23()
	{
	  String testname = "cvc-complex-type.2.3";
	  String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + LINE_NUMBER_ADJUSTMENTS_DIR + "cvc-complex-type.2.3/" + testname + ".xml";
      String key = "cvc-complex-type.2.3";
	  int lineno = 2;
	  int columnno = 187;
	  int numErrors = 1;
	  int numWarnings = 0;

	  runTest(testfile, key, numErrors, numWarnings, lineno, columnno);
	}
	
	/**
	 * Test /LineNumberAdjustments/cvc-complex-type.2.4.b/cvc-complex-type.2.4.b.xml.
	 */
	public void testcvccomplextype24b()
	{
	  String testname = "cvc-complex-type.2.4.b";
	  String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + LINE_NUMBER_ADJUSTMENTS_DIR + "cvc-complex-type.2.4.b/" + testname + ".xml";
      String key = "cvc-complex-type.2.4.b";
	  int lineno = 2;
	  int columnno = 187;
	  int numErrors = 1;
	  int numWarnings = 0;

	  runTest(testfile, key, numErrors, numWarnings, lineno, columnno);
	}
	
	/**
	 * Test /LineNumberAdjustments/MSG_CONTENT_INCOMPLETE/MSG_CONTENT_INCOMPLETE.xml.
	 */
	public void testMsgContentIncomplete()
	{
	  String testname = "MSG_CONTENT_INCOMPLETE";
	  String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + LINE_NUMBER_ADJUSTMENTS_DIR + "MSG_CONTENT_INCOMPLETE/" + testname + ".xml";
      String key = "MSG_CONTENT_INCOMPLETE";
	  int lineno = 6;
	  int columnno = 7;
	  int numErrors = 1;
	  int numWarnings = 0;

	  runTest(testfile, key, numErrors, numWarnings, lineno, columnno);
	}
	
	/**
	 * Test /LineNumberAdjustments/MSG_CONTENT_INVALID/MSG_CONTENT_INVALID.xml.
	 */
	public void testMsgContentInvalid()
	{
	  String testname = "MSG_CONTENT_INVALID";
	  String testfile = FILE_PROTOCOL + PLUGIN_ABSOLUTE_PATH + SAMPLES_DIR + LINE_NUMBER_ADJUSTMENTS_DIR + "MSG_CONTENT_INVALID/" + testname + ".xml";
      String key = "MSG_CONTENT_INVALID";
	  int lineno = 6;
	  int columnno = 7;
	  int numErrors = 1;
	  int numWarnings = 0;

	  runTest(testfile, key, numErrors, numWarnings, lineno, columnno);
	}
}
