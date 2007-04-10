/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.taglib;

import javax.servlet.jsp.tagext.VariableInfo;


/**
 * Contains info about a TaglibVariable: classname, variablename.
 * @author pavery
 */
public class TaglibVariable {
    
	private String fVarClass = null;
	private String fVarName = null;
	private int fScope;
	private final String ENDL = "\n"; //$NON-NLS-1$

	private final static String AT_END = "AT_END";
	private final static String AT_BEGIN = "AT_BEGIN";
	private final static String NESTED = "NESTED";
	/**
	 * 
	 */
	public TaglibVariable(String varClass, String varName, int scope) {
		setVarClass(varClass);
		setVarName(varName);
		setScope(scope);
	}

	public TaglibVariable(String varClass, String varName, String scope) {
		setVarClass(varClass);
		setVarName(varName);
		setScope(scope);
	}
	/**
	 * @return Returns the fVarClass.
	 */
	public final String getVarClass() {
		return fVarClass;
	}
	/**
	 * @param varClass The fVarClass to set.
	 */
	public final void setVarClass(String varClass) {
		fVarClass = varClass;
	}
	/**
	 * @return Returns the fVarName.
	 */
	public final String getVarName() {
		return fVarName;
	}
	/**
	 * @param varName The fVarName to set.
	 */
	public final void setVarName(String varName) {
		fVarName = varName;
	}
	
	/**
	 * Convenience method.
	 * @return
	 */
	public final String getDeclarationString() {
		return getVarClass() + " " + getVarName() + " = null;" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$
	}
	public int getScope() {
		return fScope;
	}
	public void setScope(int scope) {
		fScope = scope;
	}	
	public void setScope(String scopeString) {
		int scope = VariableInfo.AT_BEGIN;
		
		String trimmedScope = scopeString.trim();
		if (NESTED.equals(trimmedScope)) {
			scope = VariableInfo.NESTED;
		}
		else if (AT_BEGIN.equals(trimmedScope)) {
			scope = VariableInfo.AT_BEGIN;
		}
		else if (AT_END.equals(trimmedScope)) {
			scope = VariableInfo.AT_END;
		}

		fScope = scope;
	}
}
