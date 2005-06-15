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

package org.eclipse.wst.xsd.validation.internal.ui.eclipse;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.validation.internal.core.ValidationMessage;
import org.eclipse.wst.xml.validation.internal.core.ValidationReport;



/**
 * This class manages the 'UI' related details of validation. Here's a quick
 * overview of the details : 
 * - manages Marker creation based on the results of the validation 
 * - (optionally) displays dialog to summarize the results of validation 
 * 
 * @author Lawrence Mandel, IBM
 * @author Keith Chong, IBM
 */
public class ValidateAction extends org.eclipse.wst.xml.validation.internal.core.ValidateAction
{ 
  // Property file strings.
  private static final String _UI_DIALOG_XML_SCHEMA_INVALID_TEXT = "_UI_DIALOG_XML_SCHEMA_INVALID_TEXT";
  private static final String _UI_DIALOG_XML_SCHEMA_INVALID_TITLE = "_UI_DIALOG_XML_SCHEMA_INVALID_TITLE";
  private static final String _UI_VALIDATION_INTERNAL_ERROR = "_UI_VALIDATION_INTERNAL_ERROR";
  private static final String _UI_DIALOG_XML_SCHEMA_VALID_TITLE = "_UI_DIALOG_XML_SCHEMA_VALID_TITLE";
  private static final String _UI_DIALOG_XML_SCHEMA_VALID_TEXT = "_UI_DIALOG_XML_SCHEMA_VALID_TEXT";
  private static final String _UI_MESSAGE_LIMIT_EXCEEDED = "_UI_DIALOG_XML_SCHEMA_LIMITE_EXCEEDED";
  private static final String _UI_DIALOG_XML_SCHEMA_VALID_WITH_WARNINGS = "_UI_DIALOG_XML_SCHEMA_VALID_WITH_WARNINGS";

  private InputStream inputStream;
  
  /**
   * Constructor.
   * 
   * @param file The file to validate.
   * @param showDialog Whether or not to show a dialog when validation is complete.
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
  protected void addInfoToMessage (ValidationMessage validationMessage, IMessage message)
  {   
    if (inputStream != null)
    {
      XSDMessageInfoHelper messageInfoHelper = new XSDMessageInfoHelper();
	  String[] messageInfo = messageInfoHelper.createMessageInfo(validationMessage.getMessage(), validationMessage.getKey());

	  message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
	  message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE, messageInfo[0]);
	  message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE, messageInfo[1]);
    }  
  }
  
  protected void validate(final IFile file)
  {
    final ValidationOutcome valoutcome = new ValidationOutcome();
    IPath path = file.getLocation();
    final String uri = createURIForFilePath(path.toString());
    
    IWorkspaceRunnable op = new IWorkspaceRunnable()
    {

      public void run(IProgressMonitor progressMonitor) throws CoreException
      {
        clearMarkers(file);
        XSDValidator validator = XSDValidator.getInstance();
        ValidationReport valreport = validator.validate(uri, inputStream);
        valoutcome.isValid = valreport.isValid();
        if(valreport.getValidationMessages().length == 0)
        {
          valoutcome.hasMessages = false;
        }
        else
        {
          valoutcome.hasMessages = true;
        }
        createMarkers(file, valreport.getValidationMessages());

        file.setSessionProperty(ValidationMessage.ERROR_MESSAGE_MAP_QUALIFIED_NAME, valreport.getNestedMessages());
      }
    };

    try
    {
      ResourcesPlugin.getWorkspace().run(op, null);
      String internalErrorMessage = null;

      if (showDialog)
      {
        if (!valoutcome.isValid)
        {
          String message = XSDValidatorManager.getString(_UI_DIALOG_XML_SCHEMA_INVALID_TEXT);
          String title = XSDValidatorManager.getString(_UI_DIALOG_XML_SCHEMA_INVALID_TITLE);
          if (internalErrorMessage != null)
          {
            message = message + "\n" + internalErrorMessage;
          }
          openErrorDialog(title, message);
        } 
        else if (valoutcome.isValid && valoutcome.hasMessages)
        {
          String message = XSDValidatorManager.getString(_UI_DIALOG_XML_SCHEMA_VALID_WITH_WARNINGS);
          String title = XSDValidatorManager.getString(_UI_DIALOG_XML_SCHEMA_VALID_TITLE);
          openWarningDialog(title, message);
        } 
        else
        {
          String message = XSDValidatorManager.getString(_UI_DIALOG_XML_SCHEMA_VALID_TEXT);
          String title = XSDValidatorManager.getString(_UI_DIALOG_XML_SCHEMA_VALID_TITLE);
          //String message = validator.isGrammarEncountered() ?
          //                 XSDValidatorManager.getString("_UI_THE_XML_FILE_IS_VALID") :
          //                XSDValidatorManager.getString("_UI_THE_XML_FILE_IS_WELL_FORMED") +
          //                 XSDValidatorManager.getString("_UI_NO_GRAMMAR_WARNING");
          openValidDialog(title, message);
        }
      }
    } 

	catch (CoreException e)
    {
    }
  }
  public void setInputStream(InputStream inputStream)
  { this.inputStream = inputStream;
  }
}