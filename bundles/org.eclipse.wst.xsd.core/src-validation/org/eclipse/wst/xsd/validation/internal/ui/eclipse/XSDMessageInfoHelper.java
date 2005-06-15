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


/**
 * @author Mark Hutchinson
 * 
 * The XSDMessageInfoHelper creates a string with the
 */
public class XSDMessageInfoHelper
{
  public XSDMessageInfoHelper()
  { super();
  }

  public String[] createMessageInfo(String errorMessage, String errorKey)
  { 
    //Now map the error key to what we would want to underline:
    String nameOrValue = "";
    String selectionStrategy = "";
    if (errorKey.equals("s4s-elt-invalid-content.1") || errorKey.equals("s4s-elt-must-match.1") || errorKey.equals("s4s-att-must-appear") || errorKey.equals("s4s-elt-invalid-content.2"))
    { selectionStrategy = "START_TAG";
    }
    else if (errorKey.equals("s4s-att-not-allowed"))
    { selectionStrategy = "ATTRIBUTE_NAME";
      nameOrValue = getFirstStringBetweenSingleQuotes(errorMessage);
    }
    else if (errorKey.equals("s4s-att-invalid-value"))
    { selectionStrategy = "ATTRIBUTE_VALUE";
      nameOrValue = getFirstStringBetweenSingleQuotes(errorMessage);
    }
    else if (errorKey.equals("s4s-elt-character"))
    { selectionStrategy = "TEXT";
    }
    else if (errorKey.equals("src-resolve.4.2") || errorKey.equals("src-resolve"))
    { selectionStrategy = "VALUE_OF_ATTRIBUTE_WITH_GIVEN_VALUE";
      nameOrValue = getFirstStringBetweenSingleQuotes(errorMessage);
    }
    String messageInfo[] = new String[2];
    messageInfo[0] = selectionStrategy;
    messageInfo[1] = nameOrValue;
    return messageInfo;    
  }

  /*
   * Mark Hutchinson
   * 
   * This method is used to get the value between the first pair of single quotes
   * It is used to extract information from the error Message (for example
   * an attribute name)
   */
  private String getFirstStringBetweenSingleQuotes(String s)
  {
    int first = s.indexOf("'");
    int second = s.indexOf("'", first + 1);
    String betweenQuotes = null;
    if (first != -1 && second != -1)
    { betweenQuotes = s.substring(first + 1, second);
    }
    return betweenQuotes;
  }
}
