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
package org.eclipse.wst.xml.ui.internal.validation.core;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.logging.ILogger;
import org.eclipse.wst.xml.core.internal.validation.core.logging.LoggerFactory;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.validation.XMLValidationUIMessages;

/**
 * A general validate action class that can be extended by validators. This class
 * provides methods to run the action, add markers to a file and will handle
 * showing a dialog with the results of validation (if requested) and prompt to
 * save dirty files (if requested.)
 */
public abstract class ValidateAction extends Action
{
  // Locally used, non-UI strings.
  private static final String REFERENCED_FILE_ERROR_OPEN = "referencedFileError("; //$NON-NLS-1$

  private static final String REFERENCED_FILE_ERROR_CLOSE = ")"; //$NON-NLS-1$

  private static final String REFERENCED_FILE_ERROR = "referencedFileError"; //$NON-NLS-1$

  private static final String GROUP_NAME = "groupName"; //$NON-NLS-1$
  
  private static final String FILE_PROTOCOL_NO_SLASH = "file:"; //$NON-NLS-1$
  private static final String FILE_PROTOCOL = "file:///"; //$NON-NLS-1$
  
  protected static final String COLUMN_NUMBER_ATTRIBUTE = "columnNumber"; //$NON-NLS-1$
  protected static final String SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = "squiggleSelectionStrategy"; //$NON-NLS-1$
  protected static final String SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = "squiggleNameOrValue"; //$NON-NLS-1$

  protected boolean showDialog = true;

  protected IFile file = null;

  protected IReporter reporter;

  protected IValidator validator;

  /**
   * Constructor.
   * 
   * @param file
   *          The file to validate.
   * @param showDialog
   *          Whether or not to show a dialog when validation is complete.
   */
  public ValidateAction(IFile file, boolean showDialog)
  {
    this.showDialog = showDialog;
    this.file = file;
  }

  /**
   * Validate the specified file.
   * 
   * @param file
   *          The file to validate.
   */
  protected abstract void validate(final IFile file);

  protected void addInfoToMessage (ValidationMessage v, IMessage m)
  { // This method is overidden by subclasses
  }
  
  /**
   * Create markers for the valiation messages generated from the validation.
   * 
   * @param iFile
   *          The resource to create the markers on.
   * @param valmessages
   *          The array of validation messages.
   */
  public void createMarkers(IFile iFile, ValidationMessage[] valmessages)
  {
    if (!fileIsOK(iFile))
    {
      return;
    }
    int nummessages = valmessages.length;
    for (int i = 0; i < nummessages; i++)
    {
      ValidationMessage validationMessage = valmessages[i];
      String uri = validationMessage.getUri();

      LocalizedMessage message;
      if (validationMessage.getSeverity() == ValidationMessage.SEV_LOW)
      {
        message = new LocalizedMessage(getValidationFrameworkSeverity(IMarker.SEVERITY_WARNING), validationMessage
            .getMessage(), iFile);
      }
      else
      { 
        message = new LocalizedMessage(getValidationFrameworkSeverity(IMarker.SEVERITY_ERROR), validationMessage.getMessage(), iFile);
      }
      
      message.setLineNo(validationMessage.getLineNumber());
      addInfoToMessage(validationMessage, message);
      
      List nestederrors = validationMessage.getNestedMessages();
      if (nestederrors != null && !nestederrors.isEmpty())
      {
        message.setGroupName(REFERENCED_FILE_ERROR_OPEN + uri + REFERENCED_FILE_ERROR_CLOSE);
      }

      getOrCreateReporter().addMessage(getValidator(), message);

      
    }
    try
    {
      IMarker[] markers = iFile.findMarkers(null, true, IResource.DEPTH_INFINITE);
      for (int i = 0; i < markers.length; i++)
      {
        IMarker marker = markers[i];
        String groupName = null;
        try
        {
          groupName = (String) marker.getAttribute(GROUP_NAME);
        }
        catch (Exception e)
        {
        }

        if (groupName != null && groupName.startsWith(REFERENCED_FILE_ERROR))
        {

          marker.setAttribute(IMarker.DONE, true);
        }
      }
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }

  }

  public void clearMarkers(IFile iFile)
  {
    if (fileIsOK(iFile))
    {
      getOrCreateReporter().removeAllMessages(getValidator(), iFile);
    }
  }

  public void run()
  {
  	try
  	{
  		// CS... a temporary test to avoid performing validation in the absence of xerces 
  		//
  		//dw Class theClass = 
  		Class.forName("org.apache.xerces.xni.parser.XMLParserConfiguration", true, this.getClass().getClassLoader()); //$NON-NLS-1$
  		  		
  		if (fileIsOK(file))
  		{
  			// Check if the editor is dirty for this file. If so, prompt the user to
  			// save before validating.
  			if(showDialog)
  			{
  				checkIfFileDirty(file);
  			}
  			try
  			{
  				validate(file);
  			}
  			catch (Exception e)
  			{
  				ILogger logger = LoggerFactory.getLoggerInstance();
  				logger.logError("", e); //$NON-NLS-1$
  				//      e.printStackTrace();
  			}
  		}
  	}
  	catch (Exception e)
  	{		
  	}
  }

  /**
   * Test whether the file given is OK to use. A file is OK to use if 1. It is
   * not null 2. It exists. 3. The project containing the file is accessible.
   * 
   * @param file The file to check.
   * @return True if the file is OK to use, false otherwise.
   */
  protected boolean fileIsOK(IFile file)
  {
    if (file != null && file.exists() && file.getProject().isAccessible())
    {
      return true;
    }
    return false;
  }

  /**
   * Check if the file is dirty. A file is dirty if there is an open editor for
   * the file that contains changes that haven't been saved.
   * 
   * @param file The file to check to see if it is dirty.
   */
  protected void checkIfFileDirty(IFile file)
  {
    IEditorPart[] dirtyEditors = XMLUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().getDirtyEditors();
    int numeditors = dirtyEditors.length;
    for (int i = 0; i < numeditors; i++)
    {
      IEditorInput editorInput = dirtyEditors[i].getEditorInput();
      if (editorInput instanceof FileEditorInput)
      {
        FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
        if (fileEditorInput.getFile().equals(file))
        {
          String message = XMLValidationUIMessages._UI_SAVE_DIRTY_FILE_MESSAGE;
          String title = XMLValidationUIMessages._UI_SAVE_DIRTY_FILE_TITLE;
          if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), title, message))
          {
            dirtyEditors[i].doSave(null);
          }
          // There can only be one open editor/file so we can break.
          break;
        }
      }
    }
  }

  /**
   * Gets the reporter.
   * 
   * @return Returns a IReporter
   */
  public IReporter getReporter()
  {
    return reporter;
  }

  /**
   * Sets the reporter.
   * 
   * @param reporter
   *          The reporter to set
   */
  public void setReporter(IReporter reporter)
  {
    this.reporter = reporter;
  }

  /**
   * Gets the validator.
   * 
   * @return Returns a IValidator
   */
  public IValidator getValidator()
  {
    return validator;
  }

  /**
   * Sets the validator.
   * 
   * @param validator
   *          The validator to set
   */
  public void setValidator(IValidator validator)
  {
    this.validator = validator;
  }

  protected IReporter getOrCreateReporter()
  {
    if (reporter == null)
    { 
      reporter = new WorkbenchReporter(file.getProject(), new NullProgressMonitor());
    }
    return reporter;
  }

  /**
   * Originally from *validation.Helper
   * 
   * @param severity
   *          The severity given.
   * @return The validation framework severity corresponding to the error.
   */
  protected int getValidationFrameworkSeverity(int severity)
  {
    switch (severity) {
    case IMarker.SEVERITY_ERROR:
      return IMessage.HIGH_SEVERITY;
    case IMarker.SEVERITY_WARNING:
      return IMessage.NORMAL_SEVERITY;
    case IMarker.SEVERITY_INFO:
      return IMessage.LOW_SEVERITY;
    }
    return IMessage.LOW_SEVERITY;
  }
  
  /**
   * Open an error dialog with the given title and message. Show the problems view
   * if it is not already visible.
   * 
   * @param title The title of the error dialog.
   * @param message The message in the error dialog.
   */
  protected void openErrorDialog(String title, String message)
  {
    showProblemsView();
    MessageDialog.openError(Display.getDefault().getActiveShell(), title, message);
  }
  
  /**
   * Open an warning dialog with the given title and message. Show the problems view
   * if it is not already visible.
   * 
   * @param title The title of the warning dialog.
   * @param message The message in the warning dialog.
   */
  protected void openWarningDialog(String title, String message)
  {
    showProblemsView();
    MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, message);
  }
  
  /**
   * Open a valid dialog with the given title and message. 
   * 
   * @param title The title of the valid dialog.
   * @param message The message in the valid dialog.
   */
  protected void openValidDialog(String title, String message)
  {
    MessageDialog.openInformation(Display.getDefault().getActiveShell(), title, message);
  }
  
  /**
   * Show the problems view if it is not already visible.
   */
  protected void showProblemsView()
  {
    IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = dw.getActivePage();
    IWorkbenchPart activePart = page.getActivePart();
    try
    {
      if (page != null)
      {
        page.showView(IPageLayout.ID_PROBLEM_VIEW);
      }
    }
    catch (PartInitException e)
    {
    	Logger.logException(e);
    }
    page.activate(activePart);
  }
  
  /**
   * Format a file name into a correct URI.
   * 
   * @param filename The file name to format.
   * @return The formatted URI.
   */
  protected String createURIForFilePath(String filename)
  {
  	if(!filename.startsWith(FILE_PROTOCOL_NO_SLASH))
    {
      while(filename.startsWith("/")) //$NON-NLS-1$
      {
        filename = filename.substring(1);
      }
      filename = FILE_PROTOCOL + filename;
    }
  	return filename;
  }
  
  /**
   * The validation outcome class holds the results from validating
   * a file.
   * 
   * @author Lawrence Mandel, IBM
   */
  protected class ValidationOutcome
  {
    public boolean isValid = true;

    public boolean hasMessages = false;

    public ValidationOutcome()
    {
    }
  }
}

