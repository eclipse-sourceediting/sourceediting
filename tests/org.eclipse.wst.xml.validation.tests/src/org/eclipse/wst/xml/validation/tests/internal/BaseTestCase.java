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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

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
  protected String FILE_PROTOCOL = "file://"; 
  protected String PLUGIN_ABSOLUTE_PATH;
  protected String SAMPLES_DIR = "testresources/samples/";
  protected String GENERATED_RESULTS_DIR = "testresources/generatedResults/";
  protected String IDEAL_RESULTS_DIR = "testresources/idealResults/";
  protected String LOG_FILE_LOCATION = "results.log";
  private XMLValidator validator = XMLValidator.getInstance();
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
  {
    PLUGIN_ABSOLUTE_PATH = XMLValidatorTestsPlugin.getPluginLocation().toString() + "/";
  }
  
  /**
   * Run a validator test. The test will run the validator, log the results and compare the results
   * with the ideal results. The test will only pass if the two log files are the same.
   * 
   * @param testfile The file to run the validator test on.
   * @param loglocation The location to create the log file.
   * @param idealloglocation The location of the ideal log file.
   */
  public void runTest(String testfile, String loglocation, String idealloglocation)
  {
    ValidationReport valreport = validator.validate(testfile);
    try
    {
      createLog(loglocation, valreport);
      String generatedLog = getStringFromFile(loglocation);
      String idealLog = getStringFromFile(idealloglocation);
      assertEquals(idealLog, generatedLog);
    } catch (Exception e)
    {
      fail("Could not compare log files");
    }
  }
  
  /**
   * Get a string representation of a file.
   * 
   * @param filename the file name of the file to get the string representation.
   * @return The string representation if successful.
   * @throws Exception Thrown if unable to create a string representaion of the file.
   */
  private String getStringFromFile(String filename) throws Exception
  {
    StringBuffer filestring = new StringBuffer();
    Reader reader = null;
    BufferedReader bufreader = null;
    try
    {
      File file = new File(filename);
      reader = new FileReader(file);
      bufreader = new BufferedReader(reader);
      while (bufreader.ready())
      {
        filestring.append(bufreader.readLine() + "\n");
      }
    } catch (FileNotFoundException e)
    {
      throw new Exception();
    } finally
    {
      bufreader.close();
      reader.close();
    }
    return filestring.toString();
  }
  
  /**
   * Create a log file for an XSD test.
   * 
   * @param filename The name of the log file to create.
   * @param valreport The validation report for this file.
   * @return The file contents as a string if successful or null if not successful.
   */
  private String createLog(String filename, ValidationReport valreport)
  {
     ValidationMessage[] valmessages = valreport.getValidationMessages();
     int nummessages = valmessages.length;//validator.getErrors().size() + validator.getWarnings().size();
     StringBuffer errorsString = new StringBuffer();
     StringBuffer warningsString = new StringBuffer();
     int numerrors = 0;
     int numwarnings = 0;
     for(int i = 0; i < nummessages; i++)
     {
       ValidationMessage valmes = valmessages[i];
       if(valmes.getSeverity() == ValidationMessage.SEV_LOW)
       {
         numwarnings++;
         warningsString.append(valmes.getMessage() + " [" + valmes.getLineNumber() +", " + valmes.getColumnNumber() +"]\n");
       }
       else
       {
         numerrors++;
         errorsString.append(valmes.getMessage() + " [" + valmes.getLineNumber() +", " + valmes.getColumnNumber() +"]\n");
       }
     }
     StringBuffer log = new StringBuffer();
     log.append("number of errors      : " + numerrors + "\n");
     log.append("number of warnings    : " + numwarnings + "\n\n");
     log.append("------------error list-------------------------------------------\n");
     if(numerrors == 0)
     {
       log.append("(none)\n");
     }
     else
     {
       log.append(errorsString.toString());
     }
     log.append("------------warning list-----------------------------------------\n");
     if(numwarnings == 0)
     {
       log.append("(none)\n");
     }
     else
     {
       log.append(warningsString.toString());
     }
     log.append("-----------------------------------------------------------------\n");
   
     DataOutputStream outStream = null;
    try
    {
      File logfile = new File(filename);
      File parent = logfile.getParentFile();
  	if (!parent.exists()) 
  	{
  	  parent.mkdirs();
  	}
      if(logfile.exists())
      {
        logfile.delete();
      }
      logfile.createNewFile();
      
      outStream = new DataOutputStream(new FileOutputStream(filename, true));
      outStream.writeBytes(log.toString());
      outStream.close();

    } catch (IOException e)
    {
      // If we can't write the log then clear the log.
      log.delete(0, log.length());
    }
    return log.toString();
  }
}
