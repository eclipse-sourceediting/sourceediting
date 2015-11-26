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
package org.eclipse.wst.xsd.validation.tests.internal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsd.core.internal.validation.XSDValidationConfiguration;
import org.eclipse.wst.xsd.core.internal.validation.eclipse.XSDValidator;

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
  protected String GENERATED_RESULTS_DIR = "testresources/generatedResults/";
  protected String IDEAL_RESULTS_DIR = "testresources/idealResults/";
  protected String LOG_FILE_LOCATION = "results.log";
  protected static final String PLUGIN_NAME = "org.eclipse.wst.xsd.validation.tests";
  private XSDValidator validator = XSDValidator.getInstance();
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp()
  {
    PLUGIN_ABSOLUTE_PATH = XSDValidationTestsPlugin.getInstallURL();
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
	runTest(testfile, loglocation, idealloglocation, new XSDValidationConfiguration());
  }
  /**
   * Run a validator test. The test will run the validator, log the results and compare the results
   * with the ideal results. The test will only pass if the two log files are the same.
   * 
   * @param testfile 
   * 		The file to run the validator test on.
   * @param loglocation 
   * 		The location to create the log file.
   * @param idealloglocation 
   * 		The location of the ideal log file.
   * @param configuration 
   * 		The XSDValidationConfiguration for this validation test.
   */
  public void runTest(String testfile, String loglocation, String idealloglocation, XSDValidationConfiguration configuration)
  {
    ValidationReport valreport = validator.validate(testfile, null, configuration);
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
         String message = valmes.getMessage();
         message = message.replaceAll("'[^']*" + getPluginName() + "[^'/]*/", "'");
         message = message.replaceAll("[(][^(]*" + getPluginName() + "[^'/]*/", "(");
         warningsString.append(message + " [" + valmes.getLineNumber() +", " + valmes.getColumnNumber() +"]\n");
         warningsString.append(createNestedMessageString(valmes.getNestedMessages()));
       }
       else
       {
         numerrors++;
         String message = valmes.getMessage();
         message = message.replaceAll("'[^']*" + getPluginName() + "[^'/]*/", "'");
         message = message.replaceAll("[(][^(]*" + getPluginName() + "[^'/]*/", "(");
         errorsString.append(message + " [" + valmes.getLineNumber() +", " + valmes.getColumnNumber() +"]\n");
         errorsString.append(createNestedMessageString(valmes.getNestedMessages()));
       }
     }
     StringBuffer log = new StringBuffer();
     log.append("number of errors      : " + numerrors + "\n");
     if(numwarnings > 0)
     {
       log.append("number of warnings    : " + numwarnings + "\n");
     }
     log.append("\n------------error list-------------------------------------------\n");
     if(numerrors == 0)
     {
       log.append("(none)\n");
     }
     else
     {
       log.append(errorsString.toString());
     }
     if(numwarnings > 0)
     {
       log.append("------------warning list-----------------------------------------\n");
       log.append(errorsString.toString());
     }
     log.append(warningsString.toString());
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

  protected String getPluginName()
  {
    return PLUGIN_NAME;
  }
  
  private String createNestedMessageString(List nestedMessages)
  {
    return createNestedMessageString(nestedMessages, 0);
  }
  
  private String createNestedMessageString(List nestedMessages, int depth)
  {
    if(nestedMessages != null && nestedMessages.size() > 0)
    {
      String messageString = "";
      Iterator nestedMesIter = nestedMessages.iterator();
      while(nestedMesIter.hasNext())
      {
        ValidationMessage nesvalmes = (ValidationMessage)nestedMesIter.next();
        for(int i = 0; i < depth; i++)
        {
          messageString += " ";
        }
        // If the message contains any file references make them relative.
        String message = nesvalmes.getMessage();
        message = message.replaceAll("'[^']*" + getPluginName() + "[^'/]*/", "'");
        message = message.replaceAll("[(][^(]*" + getPluginName() + "[^'/]*/", "[(]");
        messageString += ("-> " + message + " [" + nesvalmes.getLineNumber() +", " + nesvalmes.getColumnNumber() +"]\n");
        messageString += createNestedMessageString(nesvalmes.getNestedMessages(), depth + 1);
      }
      return messageString;
    }
    else
    {
      return "";
    }
  }
  
  protected IProject createSimpleProject(String projectName, String[] files)
  {
    final IWorkspace workspace = ResourcesPlugin.getWorkspace();
    final IProject project = workspace.getRoot().getProject(projectName);
    if(!project.exists())
    {
      try
	  {
        project.create(null);
      }
      catch(CoreException e)
	  {
        fail("Could not create project " + projectName + e);
      } 
    }
    if(!project.isOpen())
    {
      try
	  {
        project.open(null);
	  }
      catch(CoreException e)
      {
        fail("Could not open project " + projectName + e);
	  }
    }
    try
    {
      IProjectDescription projectDescription = project.getDescription(); 
      projectDescription.setName(projectName);
      project.setDescription(projectDescription, null);
    }
    catch(Exception e)
    {
      fail("Unable to set project properties for project " + projectName + ". " + e);
    }

    if(files != null)
    {
      int numfiles = files.length;
      for(int i = 0; i < numfiles; i++)
      {
        try
		{
          String filename = files[i];
          filename = filename.replace('\\','/');
          if(filename.startsWith("file:"))
          {
            filename = filename.substring(5);
			while(filename.startsWith("/"))
	        {
	          filename = filename.substring(1);
	        }
          }
          
          File file = new File(filename);
          FileInputStream in = new FileInputStream(file);
          IFile iFile = project.getFile(file.getName());
          if(!iFile.exists())
          {
            iFile.create(in, true, null);
            in.close();
          }
		}
        catch(Exception e)
		{
		  fail("Unable to locate file " + files[i]);
		} 
      }
    }
    try
    {
      project.refreshLocal(-1, null);
    }
    catch(Exception e)
    {}
    return project;
  }
}
