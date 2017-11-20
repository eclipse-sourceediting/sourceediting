/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

/**
 * A class for holding validation message information. Holds the message and the
 * message location.
 */
public class ValidationMessage
{
  public static final QualifiedName ERROR_MESSAGE_MAP_QUALIFIED_NAME = new QualifiedName("org.eclipse.wst.xml.validation", "errorMessageMap"); //$NON-NLS-1$ //$NON-NLS-2$
  protected String message;
  protected int lineNumber;
  protected int columnNumber;
  protected String uri;
  protected List nestedErrors;
  protected String key;
  protected Object[] messageArguments;
  protected int startOffset;
  protected int severity = IMessage.NORMAL_SEVERITY;
  public static final int SEV_HIGH = IMessage.HIGH_SEVERITY;
  public static final int SEV_NORMAL = IMessage.NORMAL_SEVERITY;
  public static final int SEV_LOW = IMessage.LOW_SEVERITY;

  /**
   * Constructor.
   * 
   * @param message The message for the validation message.
   * @param lineNumber The line location of the message.
   * @param columnNumber The column location of the message.
   */
  public ValidationMessage(String message, int lineNumber, int columnNumber)
  {
    this(message, lineNumber, columnNumber, null);
  }

  /** 
   * Constructor.
   * 
   * @param message The message for the validation message.
   * @param lineNumber The line location of the message.
   * @param columnNumber The column location of the message.
   * @param uri The uri of the file the message is for.
   */
   public ValidationMessage(String message, int lineNumber, int columnNumber, String uri)
  {
     this(message, lineNumber, columnNumber, uri, null, null);
  }
   
   /** 
    * Constructor.
    * 
    * @param message The message for the validation message.
    * @param lineNumber The line location of the message.
    * @param columnNumber The column location of the message.
    * @param uri The uri of the file the message is for.
    * @param key a unique string representing the error
    * @param messageArguments the arguments Xerces uses to create the message
    */
    public ValidationMessage(String message, int lineNumber, int columnNumber, String uri, String key, Object[] messageArguments)
   {
     this.message = message;
     this.lineNumber = lineNumber;
     this.columnNumber = columnNumber;
     this.uri = uri;
     this.key = key;
	 this.messageArguments = messageArguments;
     this.startOffset = 0;    
   }
   

  /**
   * Get the message for this valiation message.
   * 
   * @return The message for this validation message.
   */
  public String getMessage()
  {
    return message;
  }

  /**
   * Get the column location.
   * 
   * @return The column number.
   */
  public int getColumnNumber()
  {
    return columnNumber;
  }

  /**
   * Get the line location.
   * 
   * @return The line number.
   */
  public int getLineNumber()
  {
    return lineNumber;
  }

  /**
   * Get the uri for the file that contains the message.
   * 
   * @return The uri of the file that contains the message.
   */
  public String getUri()
  {
    return uri;
  }
  
  /**
   * Add a nested validation message to this validation message.
   * 
   * @param validationMessage The validation message to add as a nested message.
   */
  public void addNestedMessage(ValidationMessage validationMessage)
  {
    if (nestedErrors == null)
    {
      nestedErrors = new ArrayList();
    }
    nestedErrors.add(validationMessage);
    int validaitonmessageSeverity = validationMessage.getSeverity();
    if(validaitonmessageSeverity == SEV_NORMAL || validaitonmessageSeverity == SEV_HIGH)
    {
      setSeverity(SEV_NORMAL);
    }
  }

  /**
   * Get the list of nested validation messages.
   * 
   * @return The list of nested validation messages.
   */
  public List getNestedMessages()
  {
    return nestedErrors != null ? nestedErrors : Collections.EMPTY_LIST;
  }
  
  /**
   * Get the severity of the defect.
   * 
   * @return The severity of the defect.
   */
  public int getSeverity()
  {
  	return severity;
  }
  
  /**
   * Set the severity of the message.
   * 
   * @param sev The severity to set.
   */
  public void setSeverity(int sev)
  {
  	severity = sev;
  }
  
  public void setStartOffset(int offset)
  {
    this.startOffset = offset;
  }
  
  /**
   * @return Returns the key.
   */
  public String getKey()
  {
    return key;
  }
  
  public Object[] getMessageArguments()
  {
	  return this.messageArguments;
  }
  

  public void setMessage(String message)
  {
    this.message = message;
  }  
                                     
}
