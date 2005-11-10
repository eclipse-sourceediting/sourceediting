/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.web.internal;

import java.util.StringTokenizer;

public class WebPropertiesUtil {
	private static final char[] BAD_CHARS = {'/', '\\', ':'};
	/**
	 * @param project
	 *            org.eclipse.core.resources.IProject
	 */
	/**
	 * Returns a error message that states whether a context root is valid or not returns null if
	 * context root is fine
	 * 
	 * @return java.lang.String
	 * @param contextRoot
	 *            java.lang.String
	 */
	public static String validateContextRoot(String contextRoot) {

		if (contextRoot == null)
			return null;

		String errorMessage = null;

		String name = contextRoot;
		if (name.equals("") || name == null) { //$NON-NLS-1$
			//  this was added because the error message shouldnt be shown initially. It should be
			// shown only if context root field is edited to
			errorMessage = ResourceHandler.StaticWebProjectWizardBasePage_Page_Title; 
			
			//errorMessage = ProjectSupportResourceHandler.getString("Context_Root_cannot_be_empty_2"); //$NON-NLS-1$
			return errorMessage;
		}

		/*******************************************************************************************
		 * // JZ - fix to defect 204264, "/" is valid in context root if (name.indexOf("//") != -1) {
		 * //$NON-NLS-1$ errorMessage = "// are invalid characters in a resource name"; return
		 * errorMessage;
		 *  }
		 ******************************************************************************************/

		if (name.trim().equals(name)) {
			StringTokenizer stok = new StringTokenizer(name, "."); //$NON-NLS-1$
			outer : while (stok.hasMoreTokens()) {
				String token = stok.nextToken();
				for (int i = 0; i < token.length(); i++) {
					if (!(token.charAt(i) == '_') && !(token.charAt(i) == '-') && !(token.charAt(i) == '/') && Character.isLetterOrDigit(token.charAt(i)) == false) {
						if (Character.isWhitespace(token.charAt(i))) {
							//Removed because context roots can contain white space
							//errorMessage =
							//	ResourceHandler.getString("_Context_root_cannot_conta_UI_");//$NON-NLS-1$
							// = " Context root cannot contain whitespaces."
						} else {
							errorMessage = ResourceHandler.StaticWebProjectWizardBasePage_Page_Title; 
							
							//errorMessage = ProjectSupportResourceHandler.getString("The_character_is_invalid_in_a_context_root", new Object[]{(new Character(token.charAt(i))).toString()}); //$NON-NLS-1$
							break outer;
						}
					}
				}
			}
		} // en/ end of if(name.trim
		else
			errorMessage = ResourceHandler.StaticWebProjectWizardBasePage_Page_Title; 
			//errorMessage = ProjectSupportResourceHandler.getString("Names_cannot_begin_or_end_with_whitespace_5"); //$NON-NLS-1$

		return errorMessage;
	}


	/**
	 * Return true if the string contains any of the characters in the array.
	 */
	private static boolean contains(String str, char[] chars) {
		for (int i = 0; i < chars.length; i++) {
			if (str.indexOf(chars[i]) != -1)
				return true;
		}
		return false;
	}


	public static String validateFolderName(String folderName) {
		if (folderName.length() == 0)
			return ResourceHandler.StaticWebProjectWizardBasePage_Page_Title; 
			
			//return ProjectSupportResourceHandler.getString("Folder_name_cannot_be_empty_2"); //$NON-NLS-1$

		if (contains(folderName, BAD_CHARS))
			return ResourceHandler.StaticWebProjectWizardBasePage_Page_Title; 
			
			//return ProjectSupportResourceHandler.getString("Folder_name_is_not_valid", new Object[]{folderName}); //$NON-NLS-1$

		return null;
	}

}