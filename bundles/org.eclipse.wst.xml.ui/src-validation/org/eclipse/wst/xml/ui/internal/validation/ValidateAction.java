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

package org.eclipse.wst.xml.ui.internal.validation;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationReport;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.ui.internal.Logger;

                
/**
 * This class managers the 'UI' related details of validation
 * Here's a quick overview of the details : 
 *   - manages Marker creation based on the results of the validation
 *   - (optionally) displays dialog to summarize the results of validation
 * 
 * @author Craig Salter, IBM
 * @author Lawrence Mandel, IBM
 */
public class ValidateAction extends org.eclipse.wst.xml.ui.internal.validation.core.ValidateAction
{ 
  private static final String _UI_VALIDATION_FAILED = "_UI_VALIDATION_FAILED";
  private static final String _UI_THE_XML_FILE_IS_NOT_VALID = "_UI_THE_XML_FILE_IS_NOT_VALID";
  private static final String _UI_VALIDATION_SUCEEDED = "_UI_VALIDATION_SUCEEDED";
  private static final String _UI_THE_XML_FILE_IS_VALID_WITH_WARNINGS = "_UI_THE_XML_FILE_IS_VALID_WITH_WARNINGS";
  private static final String _UI_THE_XML_FILE_IS_WELL_FORMED_WITH_WARNINGS = "_UI_THE_XML_FILE_IS_WELL_FORMED_WITH_WARNINGS";
  private static final String _UI_NO_GRAMMAR_WARNING = "_UI_NO_GRAMMAR_WARNING";
  private static final String _UI_THE_XML_FILE_IS_VALID = "_UI_THE_XML_FILE_IS_VALID";
  private static final String _UI_THE_XML_FILE_IS_WELL_FORMED = "_UI_THE_XML_FILE_IS_WELL_FORMED";
  //dw private static final String _UI_MESSAGE_LIMITE_EXCEEDED = "_UI_MESSAGE_LIMITE_EXCEEDED";
  
  private InputStream inputStream;
  

  /**
   * Constructor.
   * 
   * @param file The file to validate.
   * @param showDialog Whether to display a dialog after validation.
   */
  public ValidateAction(IFile file, boolean showDialog)
  {   
    super(file, showDialog);
  }  

  /*
   * Store additional information in the message parameters
   * param[0] = the column number of the error
   * param[1] = the 'squiggle selection strategy' for which DOM part to squiggle
   * param[2] = the name or value of what is to be squiggled
   */
  protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message)
  { 
    if (inputStream != null)
    {
      XMLMessageInfoHelper messageInfoHelper = new XMLMessageInfoHelper();
      String[] messageInfo = messageInfoHelper.createMessageInfo(validationMessage.getKey(), validationMessage.getMessageArguments());
      
	  message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
	  message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, messageInfo[0]);
	  message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, messageInfo[1]);
    }  
  }
  
  protected void validate(final IFile file)
  {      
    final XMLValidationOutcome valoutcome = new XMLValidationOutcome();
    IPath path = file.getLocation();
    final String uri = createURIForFilePath(path.toString());

    IWorkspaceRunnable op = new IWorkspaceRunnable() 
    {
      public void run(IProgressMonitor progressMonitor) throws CoreException 
      {         
        XMLValidator validator = XMLValidator.getInstance();

        clearMarkers(file);
        XMLValidationReport valreport = null;
        if (inputStream != null)
        {
          valreport = validator.validate(uri, inputStream);
        }
        else
        {
          valreport = validator.validate(uri);
        }
        
        valoutcome.isValid = valreport.isValid();
        if(valreport.getValidationMessages().length == 0)
        {
          valoutcome.hasMessages = false;
        }
        else
        {
          valoutcome.hasMessages = true;
        }
        valoutcome.isGrammarEncountered = valreport.isGrammarEncountered();
        createMarkers(file, valreport.getValidationMessages());
        
        file.setSessionProperty(ValidationMessage.ERROR_MESSAGE_MAP_QUALIFIED_NAME, valreport.getNestedMessages());
      }
    };    

    
    try
    {
      ResourcesPlugin.getWorkspace().run(op, null);
//      String internalErrorMessage = null;
//      if (validator.getInternalError() != null)
//      {
//        internalErrorMessage =  XMLValidatePlugin.getString("_UI_VALIDATION_INTERNAL_ERROR");
//        internalErrorMessage += " : " + validator.getInternalError();
//      }
                 
      if (showDialog)
      {
        // The file is invalid.
        if (!valoutcome.isValid)
        {
          String title = resourceBundle.getString(_UI_VALIDATION_FAILED);
          String message = resourceBundle.getString(_UI_THE_XML_FILE_IS_NOT_VALID);
          openErrorDialog(title, message);
        }
        else
        {
          // The file is valid however warnings were issued.
          if(valoutcome.hasMessages)
          {
            String title = resourceBundle.getString(_UI_VALIDATION_SUCEEDED);
            String message = valoutcome.isGrammarEncountered ?
            			resourceBundle.getString(_UI_THE_XML_FILE_IS_VALID_WITH_WARNINGS) : 
            			resourceBundle.getString(_UI_THE_XML_FILE_IS_WELL_FORMED_WITH_WARNINGS) + 
            			resourceBundle.getString(_UI_NO_GRAMMAR_WARNING);                             
            
            openWarningDialog(title, message);
          }
          // The file is valid with no warnings.
          else
          {
            String title = resourceBundle.getString(_UI_VALIDATION_SUCEEDED);
            String message = valoutcome.isGrammarEncountered ?
            			resourceBundle.getString(_UI_THE_XML_FILE_IS_VALID) : 
            			resourceBundle.getString(_UI_THE_XML_FILE_IS_WELL_FORMED) + 
            			resourceBundle.getString(_UI_NO_GRAMMAR_WARNING);                             
            
            openValidDialog(title, message);
          }
        }
      }
    }

    catch (CoreException e)
    {
      Logger.logException(e);
    }                           
  } 
  
  /**
   * An XML specific validation outcome that includes whether a grammar 
   * was encountered.
   * 
   * @author Lawrence Mandel, IBM
   */
  protected class XMLValidationOutcome extends ValidationOutcome
  {
    public boolean isGrammarEncountered = false;
  }
  public void setInputStream(InputStream inputStream)
  { this.inputStream = inputStream;
  }
}
