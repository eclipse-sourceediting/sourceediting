/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.java;

import java.io.File;

/**
 * @author pavery
 */
public class JSP2ServletNameUtil {
	
	/**
	 * WAS mangles Tom&Jerry as: _Tom_26_Jerry; this takes in the mangled name
	 * and returns the original name.
	 * 
	 * Unmangles the qualified type name.  If an underscore is found
	 * it is assumed to be a mangled representation of a non-alpha, 
	 * non-digit character of the form _NN_, where NN are hex digits
	 * representing the encoded character.  This routine converts it
	 * back to the original character.
	 */
	public final static String unmangle(String qualifiedTypeName) {
		if(qualifiedTypeName.charAt(0) != '_')
			return qualifiedTypeName;
		
		StringBuffer buf = new StringBuffer();
		String possible = ""; //$NON-NLS-1$
	
		// remove the .java extension if there is one
		if(qualifiedTypeName.endsWith(".java"))//$NON-NLS-1$
			qualifiedTypeName = qualifiedTypeName.substring(0, qualifiedTypeName.length() - 5);
		
		for(int i = 1; i < qualifiedTypeName.length(); i++) {  // start at index 1 b/c 1st char is always '_'
			char c = qualifiedTypeName.charAt(i);
			if(c == '_') {
				int endIndex = qualifiedTypeName.indexOf('_', i+1);
				if(endIndex == -1)
					buf.append(c);
				else {
					char unmangled;
					try {
						possible = qualifiedTypeName.substring(i+1, endIndex); 
					    if(isValid(possible)) {
							unmangled = (char)Integer.decode("0x" + possible).intValue();//$NON-NLS-1$
							i = endIndex;
					    }
					    else { 
					        unmangled = c;
					    }
						
					} catch(NumberFormatException e) {
						unmangled = c;
					}
					buf.append(unmangled);
				}
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/**
	 * Determine if given string is a valid Hex representation of an ASCII character (eg. 2F -> /)
	 * @param possible
	 * @return
	 */
	private static boolean isValid(String possible) {
	    boolean result = false;
	    if(possible.length() == 2){	    
	        char c1 = possible.charAt(0);
	        char c2 = possible.charAt(1);
	        // 1st character must be a digit
	        if(Character.isDigit(c1)) {
	            // 2nd character must be digit or upper case letter A-F
	            if(Character.isDigit(c2)) {
	                result = true;
	            }
	            else if(Character.isUpperCase(c2) && (c2 == 'A' || c2 == 'B' || c2 ==  'C'|| c2 ==  'D'|| c2 ==  'E'|| c2 == 'F')) {
	                result = true;
	            }
	        }
	    }
        return result;
	}
	
	/**
	 * Mangle string to WAS-like specifications
	 *
	 */
	public final static String mangle(String name) {	  
	  StringBuffer modifiedName = new StringBuffer();
	  
	  // extension (.jsp, .jspf, .jspx, etc...) should already be encoded in name
	  
	  int length = name.length();
	  // in case name is forbidden (a number, class, for, etc...)
	  modifiedName.append('_');
	
	  // ensure rest of characters are valid	
	  for (int i=0; i< length; i++) {
		  char currentChar = name.charAt(i);
		  // [290769] Mangle _ since mangling inserts _ as a delimiter
		  if (currentChar == '_' || !Character.isJavaIdentifierPart(currentChar))
			  modifiedName.append(mangleChar(currentChar));
		  else
			  modifiedName.append(currentChar);
	  }
	  return modifiedName.toString();
	  
	}

	/**
	 * take a character and return its hex equivalent
	 */
	private final static String mangleChar(char ch) {
	  if ( ch == File.separatorChar ) {
		  ch = '/';
	  }
	
	  if ( Character.isLetterOrDigit(ch) == true ) {
		  return "" + ch; //$NON-NLS-1$
	  }
	  return "_" + Integer.toHexString(ch).toUpperCase() + "_"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
