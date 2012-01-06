/*******************************************************************************
 * Copyright (c) 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xml.core.internal.validation.core;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.validation.AnnotationMsg;

/**
 * An abstract validator that assists validators in running and contributing
 * nested messages in the validation results. 
 * *note: Subclasses do not need to contribute nested messages in order to
 * benefit from the use of this class. This class takes care of iterating
 * through results for validators that use the standard context.
 */
public abstract class AbstractNestedValidator extends AbstractValidator implements IValidatorJob 
{
  // Locally used, non-UI strings.
  private static final String REFERENCED_FILE_ERROR_OPEN = "referencedFileError("; //$NON-NLS-1$
  private static final String REFERENCED_FILE_ERROR_CLOSE = ")"; //$NON-NLS-1$
  private static final String FILE_PROTOCOL_NO_SLASH = "file:"; //$NON-NLS-1$
  private static final String FILE_PROTOCOL = "file:///"; //$NON-NLS-1$
  private final String GET_FILE = "getFile"; //$NON-NLS-1$
  private final String GET_PROJECT_FILES = "getAllFiles"; //$NON-NLS-1$
  private final String GET_INPUTSTREAM = "inputStream"; //$NON-NLS-1$
  
  // Internal strings. These strings are common addition information types.
  protected static final String COLUMN_NUMBER_ATTRIBUTE = "columnNumber"; //$NON-NLS-1$
  protected static final String SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE = "squiggleSelectionStrategy"; //$NON-NLS-1$
  protected static final String SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE = "squiggleNameOrValue"; //$NON-NLS-1$

  /**
   * Perform the validation using version 2 of the validation framework.
   */
  public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
	  ValidationResult result = new ValidationResult();  
	  IReporter reporter = result.getReporter(monitor);
		IFile file = null;
		if (resource instanceof IFile)file = (IFile)resource;
		if (file != null)
		{
		  NestedValidatorContext nestedcontext = getNestedContext(state, false);
	      boolean teardownRequired = false;
	      if (nestedcontext == null)
	      {
	        // validationstart was not called, so manually setup and tear down
	        nestedcontext = getNestedContext(state, true);
	        nestedcontext.setProject(file.getProject());
	        setupValidation(nestedcontext);
	        teardownRequired = true;
	      }
	      else {
	    	  nestedcontext.setProject(file.getProject());
	      }
		  validate(file, null, result, reporter, nestedcontext);

	      if (teardownRequired)
	        teardownValidation(nestedcontext);
		}
	    return result;
  }
 

  /* (non-Javadoc)
   * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#validateInJob(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
   */
  public IStatus validateInJob(IValidationContext context, IReporter reporter) throws ValidationException 
  {
	NestedValidatorContext nestedcontext = new NestedValidatorContext();
	setupValidation(nestedcontext);
	String[] fileURIs = context.getURIs();
	if (fileURIs != null && fileURIs.length > 0) 
	{
	  int numFiles = fileURIs.length;
	  for (int i = 0; i < numFiles && !reporter.isCancelled(); i++) 
	  {
	    String fileName = fileURIs[i];
	    if (fileName != null)
	    {          
	      Object []parms = {fileName};

	      IFile file = (IFile) context.loadModel(GET_FILE, parms);
	      if (file != null && shouldValidate(file)) 
	      { 
	    	  nestedcontext.setProject(file.getProject());
	    	// The helper may not have a file stored in it but may have an InputStream if being
	    	// called from a source other than the validation framework such as an editor.
	        if (context.loadModel(GET_INPUTSTREAM) instanceof InputStream)
	        {
	          validate(file, (InputStream)context.loadModel(GET_INPUTSTREAM), null, reporter, nestedcontext); //do we need the fileName?  what is int ruleGroup?
	        }
	        else
	        {
	    	  validate(file, null, null, reporter, nestedcontext);
	        }
	      }
	    }
	  }
	}
	// TODO: Is this needed? Shouldn't the framework pass the complete list? 
	// Should I know that I'm validating a project as opposed to files?
	else 
    {
      Object []parms = {getValidatorID()};
      Collection files = (Collection) context.loadModel(GET_PROJECT_FILES, parms);
      // files can be null if they're outside of the workspace
      if (files != null) {
	      Iterator iter = files.iterator();
	      while (iter.hasNext() && !reporter.isCancelled()) 
	      {
	        IFile file = (IFile) iter.next();
	        if(shouldValidate(file))
	        {
		      validate(file, null, null, reporter, nestedcontext);
	        }
	      }
      }
    }
	
	teardownValidation(nestedcontext);
	if(reporter.isCancelled())
	  return Status.CANCEL_STATUS;
    return Status.OK_STATUS;
  }
  
  /**
   * Provides the id of this validator. The ID is used by the validation
   * framework. It usually is the fully qualified class name of the class
   * implementing the IValidator interface.
   * 
   * @return a String with the ID of this validator.
   */
  protected String getValidatorID()
  {
    return this.getClass().getName();
  }
  
  /**
   * Perform set up before validation runs. Subclasses may implement this
   * method to perform validation specific set up.
   * 
   * @param context
   * 		The context of the current validation.
   */
  protected void setupValidation(NestedValidatorContext context)
  {
	// Default implementation does nothing.
  }
  
  /**
   * Perform tear down after validation runs. Subclasses may implement this
   * method to perform validation specific tear down.
   * 
   * @param context
   * 		The context of the current validation.
   */
  protected void teardownValidation(NestedValidatorContext context)
  {
	// Default implementation does nothing.
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
   */
  public ISchedulingRule getSchedulingRule(IValidationContext arg0) 
  {
	// TODO review whether returning a null rule is correct. Gary had a suggestion in the bug report.
	return null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
   */
  public void cleanup(IReporter arg0) 
  {
    // No cleanup to perform. Subclasses are free to implement this method.
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
   */
  public void validate(IValidationContext context, IReporter reporter) throws ValidationException 
  {  
	validateInJob(context, reporter);
  }
	
	
  /**
   * Determine if a given file should be validated. 
   * 
   * @param file The file that may be validated.
   * @return True if the file should be validated, false otherwise.
   */
  private static boolean shouldValidate(IFile file) 
  {
	IResource resource = file;
	do 
	{
	  if (resource.isDerived() || resource.isTeamPrivateMember() || 
		  !resource.isAccessible() || resource.getName().charAt(0) == '.') 
	  {
		return false;
	  }
	  resource = resource.getParent();
	}while ((resource.getType() & IResource.PROJECT) == 0);
	
	return true;
  }
  
  /**
   * Validate the given file and use the reporter for the validation messages.
   * This method does not perform the validation logic but rather delegates
   * to the validate method in subclasses to validate. This method is responsible
   * for reporting the messages that result from validation.
   * 
   * @param file 
   * 		An IFile to validate.
   * @param inputstream 
   * 		An InputStream that represents the file. The InputStream may be null
   * 		in which case the files should be validated from the IFile.
   * @param result - The validation result
   * @param reporter 
   * 		The reporter with which to report validation messages.
   * @param context
   * 		The context of the current validation.
   */
  private void validate(IFile file, InputStream inputstream, ValidationResult result, IReporter reporter, NestedValidatorContext context)
  {  
	Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, file.getFullPath().toString());
    reporter.displaySubtask(this, message);
    
	String locationString = null;		
	if (file.getLocation() != null) {
		locationString = file.getLocation().toString();
	}
	if (locationString == null && file.getLocationURI() != null) {
		locationString = file.getLocationURI().toString();
	}
	if (locationString == null) {
		locationString = file.getFullPath().toString();
	}
	String uri = createURIForFilePath(locationString);

	clearMarkers(file, this, reporter);
	
	ValidationReport valreport = null;
	if (result == null)
	  valreport = validate(uri, inputstream, context);
	else
	  valreport = validate(uri, inputstream, context, result);
	
	createMarkers(file, valreport.getValidationMessages(), reporter);
	        
	try
	{
	  file.setSessionProperty(ValidationMessage.ERROR_MESSAGE_MAP_QUALIFIED_NAME, valreport.getNestedMessages());
	}
	catch(CoreException e)
	{
	  System.out.println("Unable to set nested messages property."); //$NON-NLS-1$
	}
  }
	
  /**
   * Validate the given file and use the reporter for the validation messages.
   * Clients must implement this method with their specific validation logic.
   * 
   * @param uri
   * 		The URI of the file to validate.
   * @param inputstream 
   * 		An InputStream that represents the file. The InputStream may be null
   * 		in which case the files should be validated from the IFile.
   * @param context
   * 		The context of the current validation.
   * @return
   * 		A validation report summarizing the validation.
   */
  public abstract ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context);
  
  /**
   * Validate the given file and use the reporter for the validation messages.
   * Clients should override this method with their specific validation logic.
   * This method should now be used instead of the abstract version.
   * Design decision to not make this abstract.
   * 
   * @param uri
   * @param inputstream
   * @param context
   * @param result
   * @return
   */
  public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context, ValidationResult result)
  {
    return validate(uri, inputstream, context);
  }

  public ValidationReport validateWithSetup(String uri, InputStream inputstream, NestedValidatorContext context)
  {
    setupValidation(context);
    return validate(uri, inputstream, context);
  }


  /**
   * This method clears all the markers on the given IFile for a specified
   * validator.
   * This is a convenience method for subclasses.
   * 
   * @param iFile
   * 		The IFile from which to clear the markers.
   * @param validator
   * 		The validator for which to remove the markers.
   * @param reporter
   * 		The reporter that can remove the markers.
   */
  private void clearMarkers(IFile iFile, IValidator validator, IReporter reporter)
  {
	if (fileIsAccessible(iFile))
	{
	  reporter.removeAllMessages(validator, iFile);
	}
  }
	  
  /**
   * Test whether the given file is accessible and may be used for validation. A file is 
   * available if 
   * 1. It is not null.
   * 2. It exists. 
   * 3. The project containing the file is accessible.
   * 
   * @param file 
   * 		The file to check to ensure it is accessible.
   * @return 
   * 		True if the file is accessible, false otherwise.
   */
  private boolean fileIsAccessible(IFile file)
  {
    if (file != null && file.exists() && file.getProject().isAccessible())
	{
	  return true;
	}
	return false;
  }
	  
  /**
   * Format a file name into a correct URI. 
   * This is a convenience method for subclasses.
   * 
   * @param filename 
   * 		The file name to format.
   * @return 
   * 		
   * The formatted URI.
   */
  private String createURIForFilePath(String filename)
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
   * Create markers for the valiation messages generated from the validation.
   * 
   * @param iFile
   *          The resource to create the markers on.
   * @param valmessages
   *          The array of validation messages.
   */
  public void createMarkers(IFile iFile, ValidationMessage[] valmessages, IReporter reporter)
  {
    if (!fileIsAccessible(iFile))
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
        message = new LocalizedMessage(IMessage.NORMAL_SEVERITY, 
        		validationMessage.getMessage(), iFile);
      }
      else
      { 
        message = new LocalizedMessage(IMessage.HIGH_SEVERITY, validationMessage.getMessage(), iFile);
      }
      
      message.setLineNo(validationMessage.getLineNumber());
      addInfoToMessage(validationMessage, message);
      Object[] objlist = validationMessage.getMessageArguments();
      if (objlist != null && objlist.length ==1 ){
    	  Object obj = objlist[0];
    	  if (obj instanceof AnnotationMsg){
	    	 message.setAttribute(AnnotationMsg.PROBMLEM_ID, new Integer(((AnnotationMsg)obj).getProblemId()));
	    	 message.setAttribute(AnnotationMsg.LENGTH, new Integer(((AnnotationMsg)obj).getLength()));
	    	  Object obj1 = ((AnnotationMsg)obj).getAttributeValueText();
	    	  if (obj1 instanceof String){
	    		  message.setAttribute(AnnotationMsg.ATTRVALUETEXT, obj1);
	    	  }
	    	  else if ( obj1 instanceof Object[]){
	    		  Object[] objArray = (Object[])obj1;
	    		  message.setAttribute(AnnotationMsg.ATTRVALUENO, new Integer(objArray.length));
	    		  for (int j=0; j <objArray.length;j++){
	    			  Object obj2 = objArray[j];
	    			  String attrName = AnnotationMsg.ATTRNO + j;
	    			  message.setAttribute(attrName, obj2);
	    		  }
	    		  
	    	  }
    	  }
      }
      List nestederrors = validationMessage.getNestedMessages();
      if (nestederrors != null && !nestederrors.isEmpty())
      {
        message.setGroupName(REFERENCED_FILE_ERROR_OPEN + uri + REFERENCED_FILE_ERROR_CLOSE);
      }

      reporter.addMessage(this, message);
	      
    }
  }
	  
  /**
   * This method allows the addition of information to the validation message
   * @param validationmessage
   * 		The ValidationMessage to retrieve the information from.
   * @param message
   * 		The IMessage to add the information to.
   */
  protected void addInfoToMessage (ValidationMessage validationmessage, IMessage message)
  { 
	// This method may be overridden by subclasses
  }

  /**
   * Get the nested validation context.
   * 
   * @param state
   *          the validation state.
   * @param create
   *          when true, a new context will be created if one is not found
   * @return the nested validation context.
   */
  protected NestedValidatorContext getNestedContext(ValidationState state, boolean create)
  {
    NestedValidatorContext context = null;
    if (create)
    {
      context = new NestedValidatorContext();
    }
    return context;
  }

  /**
   * A localized message is a specialized type of IMessage that allows setting
   * and using a localized message string for a message.
   */
  class LocalizedMessage extends Message 
  {
    private String _message = null;

	public LocalizedMessage(int severity, String messageText) 
	{
	  this(severity, messageText, null);
	}

	public LocalizedMessage(int severity, String messageText, IResource targetObject) 
	{
	  this(severity, messageText, (Object) targetObject);
	}

	public LocalizedMessage(int severity, String messageText, Object targetObject) 
	{
	  super(null, severity, null);
	  setLocalizedMessage(messageText);
	  setTargetObject(targetObject);
	}

	public void setLocalizedMessage(String message) 
	{
	  _message = message;
	}

	public String getLocalizedMessage() 
	{
	  return _message;
	}

	public String getText() 
	{
	  return getLocalizedMessage();
	}

	public String getText(ClassLoader cl) 
	{
	  return getLocalizedMessage();
	}

	public String getText(Locale l) 
	{
	  return getLocalizedMessage();
	}

	public String getText(Locale l, ClassLoader cl) 
	{
	  return getLocalizedMessage();
	}
  }
}
