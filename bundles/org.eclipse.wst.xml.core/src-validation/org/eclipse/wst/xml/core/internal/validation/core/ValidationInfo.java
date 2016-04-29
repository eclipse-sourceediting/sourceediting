/*******************************************************************************
 * Copyright (c) 2001, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver (STAR) - bug 297005 - Some static constants not made final.
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validation.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.preferences.XMLCorePreferenceNames;
import org.eclipse.wst.xml.core.internal.validation.XMLValidationMessages;

/**
 * This class handles messages from a validator. This class can handle
 * 
 * @author Lawrence Mandel, IBM
 */
public class ValidationInfo implements ValidationReport
{
  private boolean WRAPPER_ERROR_SUPPORT_ENABLED = true;
  public static final int  SEV_ERROR = 0;
  public static final int SEV_WARNING = 1;
  
  private String validating_file_uri = null;
  private URL validating_file_url = null;
  private boolean valid = true;
  private List messages = new ArrayList();
  private HashMap nestedMessages = new HashMap();
  private IScopeContext[] sameFilePreferenceContext = null;

  /**
   * Constructor.
   * 
   * @param uri
   *            The URI of the file for the validation.
   */
  public ValidationInfo(String uri)
  {
    if(uri != null)
    {
      this.validating_file_uri = uri;
      try
      {
        this.validating_file_url = new URL(uri);
      } catch (MalformedURLException e)
      {
      }
    }
  }

  public String getFileURI()
  {
    return validating_file_uri;
  }

  public boolean isValid()
  {
    return valid;
  }

  /**
   * Add an error message.
   * 
   * @param message The message to add.
   * @param line The line location of the message.
   * @param column The column location of the message.
   * @param uri The URI of the file that contains the message.
   */
  public void addError(String message, int line, int column, String uri)
  {
    addError(message, line, column, uri, null, null);
  }  
  
  /**
   * 
   * Add an error message.
   * 
   * @param message The message to add.
   * @param line The line location of the message.
   * @param column The column location of the message.
   * @param uri The URI of the file that contains the message.
   * @param key The key for the message.
   * @param messageArguments more information about the error
   */
  public void addError(String message, int line, int column, String uri, String key, Object[] messageArguments)
  {    
    if(addMessage(message, line, column, uri, SEV_ERROR, key, messageArguments))
    {
      valid = false;
    }
  }

  /**
   * Add a warning message.
   * 
   * @param message The string message of the warning.
   * @param line The line location of the warning.
   * @param column The column location of the warning.
   * @param uri The URI of the file that contains the warning.
   */
  public void addWarning(String message, int line, int column, String uri)
  {
    addWarning(message, line, column, uri, null, null);
  }
  
  /**
   * 
   * Add an error message.
   * 
   * @param message The message to add.
   * @param line The line location of the message.
   * @param column The column location of the message.
   * @param uri The URI of the file that contains the message.
   * @param key The key for the message.
   * @param messageArguments more information about the error
   */
  public void addWarning(String message, int line, int column, String uri, String key, Object[] messageArguments)
  {    
    addMessage(message, line, column, uri, SEV_WARNING, key, messageArguments);
  }
  
  /**
   * Add a message to the list. Return true if successful, false otherwise.
   * 
   * @param message The message to add to the list.
   * @param line The line location of the message.
   * @param column The column location of the message.
   * @param uri The URI of the file that contains the message.
   * @param severity The severity of the message.
   * @param key the Xerces error key for this error
   * @param messageArguments more information on the error
   * @return True if the message was successfully added, false otherwise.
   */
  private boolean addMessage(String message, int line, int column, String uri, int severity, String key, Object[] messageArguments)
  {
    boolean successfullyAdded = false;
    // If the message if null there is nothing to add.
    if(message == null)
    {
      return successfullyAdded;
    }
    String errorURI = normalize(uri);
    URL errorURL = null;
    if (errorURI != null)
    {
      try
      {
        errorURL = new URL(errorURI);
      } catch (MalformedURLException e)
      {
      }
      //errorURI = normalizeURI(errorURI);
    }
    //boolean doDialog = true;
    if (errorURL != null)
    {
      boolean isSameFile = (validating_file_url != null && validating_file_url.sameFile(errorURL));
      int validationErrorSeverity = -1;
      if( !isSameFile) {
    	  // Error is in referenced file. Pull from prefs. 
          int referencedFileSeverity = Platform.getPreferencesService().getInt(XMLCorePlugin.getDefault().getBundle().getSymbolicName(), 
        		  XMLCorePreferenceNames.INDICATE_REFERENCED_FILE_CONTAINS_ERRORS, 0, getPreferenceScopes());
          // ignore = -1,  warning = 1,  error = 2
          if( referencedFileSeverity == 1 ) {
        	  validationErrorSeverity = ValidationMessage.SEV_LOW;
          } else if( referencedFileSeverity == 2) {
        	  validationErrorSeverity = ValidationMessage.SEV_NORMAL;
          } // else leave as -1 and ignore
      } else {
    	  validationErrorSeverity = (severity == SEV_ERROR ? ValidationMessage.SEV_NORMAL : ValidationMessage.SEV_LOW);
      }
      successfullyAdded = true;
      if( validationErrorSeverity == -1 ) {
    	  // Only possible if error is in different file and user chose to ignore
    	  return successfullyAdded;
      }
      
      
      // Add to the appropriate list if nested error support is off or
      // this message is for the current file.
      if (!WRAPPER_ERROR_SUPPORT_ENABLED || isSameFile) {
    	// effectively just isSameFile since WRAPPER_ERROR_SUPPORT_ENABLED is always true?

        ValidationMessage valmes = new ValidationMessage(message, line,
            column, validating_file_uri, key, messageArguments);
        valmes.setSeverity(validationErrorSeverity);
        messages.add(valmes);
      }
      // If nested error support is enabled create a nested error.
      else if (WRAPPER_ERROR_SUPPORT_ENABLED)
      {
        String nesteduri = errorURL.toExternalForm();
        ValidationMessage nestedmess = new ValidationMessage(message, line,
            column, nesteduri, key, messageArguments);
        nestedmess.setSeverity(validationErrorSeverity);
        
        ValidationMessage container = (ValidationMessage) nestedMessages.get(nesteduri);
        if(container == null)
        {
          container = new ValidationMessage(NLS.bind(XMLValidationMessages._UI_REF_FILE_ERROR_MESSAGE, new Object [] { nesteduri }), 1, 0, nesteduri);
       
          // Initially set the nested error to a warning. This will automatically be changed
          // to an error if a nested message has a severity of error.
          container.setSeverity(ValidationMessage.SEV_LOW);
          nestedMessages.put(nesteduri, container);
          messages.add(container);
        }
        container.addNestedMessage(nestedmess);
      }
    }
    return successfullyAdded;
  }
  
  private IScopeContext[] getPreferenceScopes() {
	  if( sameFilePreferenceContext == null ) {
		  sameFilePreferenceContext = createPreferenceScopes(validating_file_url);
	  }
	  return sameFilePreferenceContext;
  }
  
  private IScopeContext[] createPreferenceScopes(URL url) {
	  IProject p = null;
	  try {
		  URI uri = new URI(url.toString());
		  IFile[] matching = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
		  if( matching != null && matching.length > 0 ) {
			  p = matching[0].getProject();
		  }
	  } catch(URISyntaxException urie) {
		  // shouldn't happen, but ignore
	  }
	  return createPreferenceScopesFromProject(p);
  }
  private IScopeContext[] createPreferenceScopesFromProject(IProject project) {
	  if (project != null && project.isAccessible()) {
		  final ProjectScope projectScope = new ProjectScope(project);
		  if (projectScope.getNode(XMLCorePlugin.getDefault().getBundle().getSymbolicName()).getBoolean(XMLCorePreferenceNames.USE_PROJECT_SETTINGS, false))
			return new IScopeContext[]{projectScope, new InstanceScope(), new DefaultScope()};
	  }
	  return new IScopeContext[]{ InstanceScope.INSTANCE, DefaultScope.INSTANCE};
  }

  
  /**
   * Add a nested message to the validation information.
   * 
   * @param message The string message of the validation message.
   * @param line The line location of the validation message.
   * @param column The column location of the validation message.
   * @param uri The URI of the validation message.
   * @param severity The severity of the validation message.
   */
//  public void addNestedMessage(String message, int line, int column, String uri, int severity)
//  {
//    ValidationMessage nestedmess = new ValidationMessage(message, line, column, uri);
//    if(severity == SEV_WARNING)
//    {
//      nestedmess.setSeverity(ValidationMessage.SEV_LOW);
//    }
//    else
//    {
//      nestedmess.setSeverity(ValidationMessage.SEV_NORMAL);
//    }
//    ValidationMessage container = (ValidationMessage)nestedMessages.get(uri);
//    if(container == null)
//    {
//      container = new ValidationMessage(XMLCoreValidationPlugin.getResourceString(_UI_REF_FILE_ERROR_MESSAGE, uri), 1, 0, validating_file_uri);
//      // Initially set the nested error to a warning. This will automatically be changed
//      // to an error if a nested message has a severity of error.
//      container.setSeverity(ValidationMessage.SEV_LOW);
//      nestedMessages.put(uri, container);
//      messages.add(container);
//    }
//    container.addNestedMessage(nestedmess);
//  }

  /**
   * @see org.eclipse.wsdl.validate.ValidationReport#getValidationMessages()
   */
  public ValidationMessage[] getValidationMessages()
  {
    return (ValidationMessage[])messages.toArray(new ValidationMessage[messages.size()]);
  }

  public HashMap getNestedMessages()
  {
    return nestedMessages;
  }
  
  /**
   * Put the URI in a standard format.
   * 
   * @param uri The URI to put into a standard format.
   * @return The standard format of the URI.
   */
  private String normalize(String uri)
  {
//    if(uri.startsWith("platform:"))
//    {
//      try
//      {
//        uri = Platform.resolve(new URL(uri)).toString();
//      }
//      catch(Exception e)
//      {
//      }
//    }
    uri = uri.replaceAll("%20"," "); //$NON-NLS-1$ //$NON-NLS-2$
    uri = uri.replaceAll("%5E", "^"); //$NON-NLS-1$ //$NON-NLS-2$
    uri = uri.replace('\\','/');
    
    return uri;
  }
  
}
