/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.core.internal.validation.eclipse;


/**
 * The XSDMessageInfoHelper creates a string with the
 */
public class XSDMessageInfoHelper
{
  public XSDMessageInfoHelper()
  { super();
  }

  public String[] createMessageInfo(String errorKey, String errorMessage)
  { 
    //Now map the error key to what we would want to underline:
    String nameOrValue = "";
    String selectionStrategy = "";
    if(errorKey != null)
    {
      if (errorKey.equals("s4s-elt-invalid-content.1") || errorKey.equals("s4s-elt-must-match.1") || 
    		  errorKey.equals("s4s-att-must-appear") || errorKey.equals("s4s-elt-invalid-content.2"))  //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$  //$NON-NLS-4$
      { 
    	selectionStrategy = "START_TAG"; //$NON-NLS-1$
      }
      else if (errorKey.equals("s4s-att-not-allowed")) //$NON-NLS-1$
      { 
    	selectionStrategy = "ATTRIBUTE_NAME"; //$NON-NLS-1$
        nameOrValue = getFirstStringBetweenSingleQuotes(errorMessage);
      }
      else if (errorKey.equals("s4s-att-invalid-value")) //$NON-NLS-1$
      { 
    	selectionStrategy = "ATTRIBUTE_VALUE"; //$NON-NLS-1$
        nameOrValue = getFirstStringBetweenSingleQuotes(errorMessage);
      }
      else if (errorKey.equals("s4s-elt-character")) //$NON-NLS-1$
      { 
    	selectionStrategy = "TEXT"; //$NON-NLS-1$
      }
      else if (errorKey.equals("src-resolve.4.2") || errorKey.equals("src-resolve"))  //$NON-NLS-1$  //$NON-NLS-2$
      { 
    	selectionStrategy = "VALUE_OF_ATTRIBUTE_WITH_GIVEN_VALUE"; //$NON-NLS-1$
        nameOrValue = getFirstStringBetweenSingleQuotes(errorMessage);
      }
      else if (errorKey.equals("EqRequiredInAttribute") || errorKey.equals("OpenQuoteExpected") || 
    		  errorKey.equals("LessthanInAttValue")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      {
      	selectionStrategy = "ATTRIBUTE_NAME"; //$NON-NLS-1$
        nameOrValue = getFirstStringBetweenQuotes(errorMessage);
      }
      else if (errorKey.equals("ElementUnterminated")) //$NON-NLS-1$
      {
        selectionStrategy = "ENTIRE_ELEMENT"; //$NON-NLS-1$
      }
      else if (errorKey.equals("ETagUnterminated") || errorKey.equals("ETagRequired")) //$NON-NLS-1$  //$NON-NLS-2$
      {
        selectionStrategy = "END_TAG"; //$NON-NLS-1$
      }
    }
    String messageInfo[] = new String[2];
    messageInfo[0] = selectionStrategy;
    messageInfo[1] = nameOrValue;
    return messageInfo;    
  }

  /**
   * This method is used to get the value between the first pair of single quotes
   * It is used to extract information from the error Message (for example
   * an attribute name)
   * 
   * @param s
   * 		The string to extract the value from.
   */
  protected String getFirstStringBetweenSingleQuotes(String s)
  {
    return getFirstStringBetweenDelimiters(s, '\'');
  }

  /**
   * This method is used to get the value between the first pair of quotes
   * It is used to extract information from the error Message (for example
   * an attribute name)
   * 
   * @param s
   * 		The string to extract the value from.
   */
  protected String getFirstStringBetweenQuotes(String s)
  {
    return getFirstStringBetweenDelimiters(s, '\"');
  }

  /**
   * This method is used to get the value between the first start and end occurrences of the given delimiter character.
   * 
   * @param s
   * 		The string to extract the value from.
   * @param delimiter 
   * 		The start and end character
   */
  protected String getFirstStringBetweenDelimiters(String s, char delimiter)
  {
    int first = s.indexOf(delimiter);
    int second = s.indexOf(delimiter, first + 1);
    String stringBetweenDelimiters = null;
    if (first != -1 && second != -1)
    { 
      stringBetweenDelimiters = s.substring(first + 1, second);
    }
    return stringBetweenDelimiters;
  }
}
