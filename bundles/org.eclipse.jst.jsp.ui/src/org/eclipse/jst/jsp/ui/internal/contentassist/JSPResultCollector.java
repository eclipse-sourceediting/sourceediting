/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.contentassist;

import java.util.Arrays;

import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.ResultCollector;

/**
 * 
 * @author pavery
 * @deprecated getting rid of JDT internal. Use JSPCompletionRequestor
 */
public class JSPResultCollector extends ResultCollector {

	int fJavaToJSPOffset;
	boolean doFilter = false;

	char[] translatorInternalIgnoreClass = "JspWriter".toCharArray(); //$NON-NLS-1$
	char[] translatorInternalIgnorePackage = "javax.servlet.jsp".toCharArray(); //$NON-NLS-1$
	String translatorInternalIgnoreMethodstarter = "print"; //$NON-NLS-1$

	public int getJavaToJSPOffset() {
		return fJavaToJSPOffset;
	}

	/**
	 * @param newJavaToJSPOffset int
	 */
	public void setJavaToJSPOffset(int newJavaToJSPOffset) {
		fJavaToJSPOffset = newJavaToJSPOffset;
	}

	public JavaCompletionProposal[] getResults() {
		JavaCompletionProposal[] javaDocumentResults = super.getResults();
		for (int i = 0; i < javaDocumentResults.length; i++)
			javaDocumentResults[i].setReplacementOffset(javaDocumentResults[i].getReplacementOffset() + fJavaToJSPOffset);
		return javaDocumentResults;
	}

	/**
	 * @see org.eclipse.jdt.core.ICompletionRequestor#acceptMethod(char[], char[], char[], char[][], char[][], char[][], char[], char[], char[], int, int, int, int)
	 */
	public void acceptMethod(char[] declaringTypePackageName, char[] declaringTypeName, char[] selector, char[][] parameterPackageNames, char[][] parameterTypeNames, char[][] parameterNames, char[] returnTypePackageName, char[] returnTypeName, char[] completionName, int modifiers, int completionStart, int completionEnd, int relevance) {
		// if this method is the exact package and type name and starts with the same method name, don't suggest it
		if (!doFilter || !(Arrays.equals(translatorInternalIgnorePackage, declaringTypePackageName) && Arrays.equals(translatorInternalIgnoreClass, declaringTypeName) && selector != null && new String(selector).startsWith(translatorInternalIgnoreMethodstarter))) {
			//super.acceptMethod(declaringTypePackageName, declaringTypeName, selector, parameterPackageNames, parameterTypeNames, parameterNames, returnTypePackageName, returnTypeName, completionName, modifiers, completionStart, completionEnd, relevance);
		}
	}

}