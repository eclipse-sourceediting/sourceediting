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
package org.eclipse.wst.xsd.ui.internal.wizards;

import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;


class RegexNode
{
  private String contents;
  private int min;
  private int max;
  private int repeat;
  private int quantifier;
  private boolean hasParens;
  private boolean autoEscapeStatus;

  
  /* Quantifiers. */
  public static final int SINGLE = 0;    
  public static final int STAR = 1;
  public static final int PLUS = 2;
  public static final int OPTIONAL = 3;
  public static final int REPEAT = 4; 
  public static final int RANGE = 5;

  /* Regex quantifiers.  First column is the on-screen textual representation, second column is the 
   on-screen regex representation.  The two are concatenated together to form the on-screen
   representation.
   Indexing of this array must correspond to the values of the quantifier constants above.
  */
  private static final String[][] regexQuantifiers =
  { 
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_QUANTIFIER_SINGLE"),   ""  },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_QUANTIFIER_STAR"),     "*" },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_QUANTIFIER_PLUS"),     "+" },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_QUANTIFIER_OPTIONAL"), "?" },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_QUANTIFIER_REPEAT"),   ""  },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_QUANTIFIER_RANGE"),    ""  },
  };


  /* Regex tokens.  First column is the on-screen representation, second column is the regex representation. 
     More tokens can be added, but it is assumed that "Current Selection" is the last element in the array.
     If this is not the case, then the value of the SELECTION constant below will need to be changed 
     accordingly.
     Also note that because these are java Strings, backslashes must be escaped (this is only relevant to the
     second column of the array).
   */
  private static final String[][] regexTerms =
  { 
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TERM_ANY_CHAR"),   "."     },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TERM_ALPHANUMERIC_CHAR"),   "\\w"     },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TERM_WHITESPACE"), "\\s"   },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TERM_DIGIT"),      "\\d"   },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TERM_UPPER"),      "[A-Z]" },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TERM_LOWER"),      "[a-z]" },
    { XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_TERM_SELECTION"),  ""      },
  };

  /* Token enumerated constants. */

  // SELECTION must correspond to the index in regexTerms of "Current Selection".  This is assumed to be
  //  the last element.
  public static final int SELECTION = regexTerms.length - 1;

  public static final int EMPTY = -1;

  /* 
  The metacharacters recognized by XML Schema.
  Note that the double backslash ("\\") actually represents an escaped single backslash character ("\").
  */ 
  private static final String metacharacters = ".\\?*+{}()[]";


  public static String getRegexTermText(int i)
  {
    if (i == SELECTION)
    {
      return regexTerms[i][0];
    }
    else
    {
      return regexTerms[i][0] + " ( " + regexTerms[i][1] + " )";
    }
  }

  public static String getRegexTermValue(int i)
  {
    if (i == SELECTION) // shouldn't happen
    {
      return "";
    }
    else
    {
      return regexTerms[i][1];
    }
  }

  public static int getNumRegexTerms()
  {
    return regexTerms.length;
  }

  public static String getQuantifierText(int i)
  {
    String result = regexQuantifiers[i][0];
    
    if (!regexQuantifiers[i][1].equals(""))
    {
      result += " ( ";
      result += regexQuantifiers[i][1];
      result += " )";
    }

    return result;
  }

  public RegexNode()
  {
    this.contents = "";
    this.quantifier = SINGLE;
    this.min = EMPTY;
    this.max = EMPTY;
    this.repeat = EMPTY;
    this.hasParens = false;

  }

  
  public String getContents()
  {
    return contents;
  }
  
  public void setContents(String contents)
  {
    this.contents = contents;
  }


  public int getQuantifier()
  {
    return quantifier;
  }

  public void setQuantifier(int quantifier)
  {
    this.quantifier = quantifier;
  }


  public int getMin()
  {
    return min;
  }

  public void setMin(int min)
  {
    this.min = min;
  }

  /**
   * Sets this.min to the integer representation of min iff min is a valid value (i.e. greater than 0).
   * Sets this.min to EMPTY if it is not.
   *
   * @param min The new min value
   * @returns Whether min was a valid value
   */  
  public boolean setMin(String min)
  {
    this.min = convertToInt(min);

    // min > max is an invalid case, unless max is EMPTY (since EMPTY == -1).
    if ( (this.max != EMPTY) && (this.min > this.max) )
    {
      return false;
    }
    
    return (this.min >= 0);
  }

  
  public int getMax()
  {
    return max;
  }
  
  public void setMax(int max)
  {
    this.max = max;
  }

  /**
   * Sets this.max to the integer representation of max iff max is a valid value (i.e. greater than 0).
   * Sets this.max to EMPTY if it is not.
   *
   * @param max The new max value
   * @returns Whether max was a valid value, or (whether max == the empty string && min has a valid value)
   */ 
  public boolean setMax(String max)
  {
    this.max = convertToInt(max);

    // The empty string is a valid max value iff min has a valid value.
    // This is due to the fact that {n,} means "at least n times" in regex parlance.
    if (max.equals("") && this.min != EMPTY)
    {
      return true;
    }
    
    else if (this.max < this.min)
    {
      return false;
    }
    
    else
    {
      return (this.max >= 0);
    }
  }


 
  public int getRepeat()
  {
    return repeat;
  }

  public void setRepeat(int repeat)
  {
    this.repeat = repeat;
  }

  /**
   * Sets this.repeat to the integer representation of repeat iff repeat is a valid value (i.e. greater than 0).
   * Sets this.repeat to EMPTY if it is not.
   *
   * @param repeat The new repeat value
   * @returns Whether repeat was a valid value
   */
  public boolean setRepeat(String repeat)
  {
    this.repeat = convertToInt(repeat);
    return (this.repeat >= 0);
  }



  /**
   * Returns the integer representation of s.  If s is less than zero, or if s is not an int
   * (i.e. if Integer.parseInt would throw a NumberFormatException), then returns EMPTY.
   *
   * @param s The String to convert.
   * @returns The integer representation of s.
   */
  private int convertToInt(String s)
  {
    int result;
    try
    {
      result = Integer.parseInt(s);
      if (result < 0)
      {
        result = EMPTY;
      }
    }
    catch (NumberFormatException e)
    {
      result = EMPTY;
    }

    return result;
  }


  public boolean getHasParens()
  {
    return hasParens;
  }

  public void setHasParens(boolean status)
  {
    this.hasParens = status;
  }

  public boolean getAutoEscapeStatus()
  {
    return autoEscapeStatus;
  }

  public void setAutoEscapeStatus(boolean status)
  {
    this.autoEscapeStatus = status;
  }

  /**
   * Returns an escaped version of s.  In other words, each occurrence of a metacharacter ( .\?*+{}()[] )
   * is replaced by that character preceded by a backslash.
   *
   * @param s The String to escape.
   * @returns An escaped version of s.
   */
  private String addEscapeCharacters(String s)
  {
    StringBuffer result = new StringBuffer("");

    for (int i = 0; i < s.length(); i++)
    {
      char currentChar = s.charAt(i);

      if (isMetachar(currentChar))
      {
        result.append("\\"); // Note that this is an escaped backslash, not a double backslash.
      }
      result.append(currentChar);

    }

    return result.toString();
  }

  /**
   * Checks whether c is a metacharacter as defined in the static variable metacharacters.
   *
   * @param c The character to check.
   * @returns Whether c is a metacharacter.
   */
  private boolean isMetachar(char c)
  {
    return metacharacters.indexOf(c) != -1;
  }
  

  public boolean hasValidMin()
  {
    return (min != EMPTY);
  }

  public boolean hasValidMax()
  {
    return(max != EMPTY);
  }

  public boolean hasValidRepeat()
  {
    return(repeat != EMPTY);
  }

  public String toString()
  {
    String result = "";
    
    if (hasParens)
    {
      result += "(";
    }
    
    if (autoEscapeStatus)
    {
       result += addEscapeCharacters(contents);
    }
    else 
    {
      result += contents;
    }


    if (hasParens)
    {
      result += ")";
    }
    
    switch (quantifier)
    {
      case STAR:
        result += "*";
        break;
      
      case PLUS:
        result += "+";
        break;
      
      case OPTIONAL:
        result += "?";
        break;
      
      case REPEAT:
        result += "{" + repeat + "}";
        break;
      
      case RANGE:
        result += "{" + min + ",";
        if (max == EMPTY)
        {
          result += "";
        }
        else
        {
          result += max;
        }       
        result += "}";
        break;
      
      // SINGLE is a fall through           

    }
    return result;

  }


}
