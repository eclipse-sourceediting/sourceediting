/*******************************************************************************
 * Copyright (c) 2005, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gregory Amerson - [bug 432978] better support for multiple AT_BEGIN variables
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.taglib;

import javax.servlet.jsp.tagext.VariableInfo;

import org.eclipse.wst.sse.core.utils.StringUtils;

/**
 * Contains info about a TaglibVariable: classname, variablename.
 */
public class TaglibVariable {

	private String fVarClass = null;
	private String fVarName = null;
	private int fScope;
	private String fDescription;

	/** fixed end-of-line value */
	private final String ENDL = "\n"; //$NON-NLS-1$

	private final static String AT_END = "AT_END";
	private final static String AT_BEGIN = "AT_BEGIN";
	private final static String NESTED = "NESTED";

	public static final int M_PRIVATE = 2;
	public static final int M_NONE = 1;
	public static final int M_REASSIGN = 4;

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

	public TaglibVariable(String varClass, String varName, String scope, String description) {
		setVarClass(varClass);
		setVarName(varName);
		setScope(scope);
		setDescription(description);
	}

	TaglibVariable(String varClass, String varName, int scope, String description) {
		setVarClass(varClass);
		setVarName(varName);
		setScope(scope);
		setDescription(description);
	}

	/**
	 * @return Returns the fVarClass.
	 */
	public final String getVarClass() {
		return fVarClass;
	}

	/**
	 * @param varClass
	 *            The fVarClass to set.
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
	 * @param varName
	 *            The fVarName to set.
	 */
	public final void setVarName(String varName) {
		fVarName = varName;
	}

	/**
	 * Convenience method.
	 * 
	 * @return
	 */
	public final String getDeclarationString() {
		return getDeclarationString(false, M_NONE);
	}

	/**
	 * Convenience method.
	 * 
	 * @return
	 */
	public final String getDeclarationString(boolean includeDoc, int style) {
		return getDeclarationString(includeDoc, "pageContext", style); //$NON-NLS-1$
	}

	public final String getDeclarationString(String context) {
		return getDeclarationString(false, context, M_NONE);
	}

	public final String getDeclarationString(boolean includeDoc, String context, int style) {
		String declaration = null;
		/*
		 * no description for now --JDT would need to show it for local
		 * variables and ILocalVariable has no "doc range"
		 */
		if (includeDoc && getDescription() != null) {
			if ((style & M_PRIVATE) > 0) {
				declaration = "/** " + ENDL + StringUtils.replace(getDescription(), "*/", "*\\/") + ENDL + " */ " + ENDL + "private " + getVarClass() + " " + getVarName() + " = (" + getVarClass() + ") "+context+".getAttribute(\"" + getVarName() + "\");" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
			}
			else {
				declaration = "/** " + ENDL + StringUtils.replace(getDescription(), "*/", "*\\/") + ENDL + " */ " + ENDL + (((style & M_REASSIGN) > 0 ) ? "" : getVarClass()) + " " + getVarName() + " = (" + getVarClass() + ") "+context+".getAttribute(\"" + getVarName() + "\");" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			}
		}
		else {
			if ((style & M_PRIVATE) > 0 ) {
				declaration = "private " + getVarClass() + " " + getVarName() + " = (" + getVarClass() + ") "+context+".getAttribute(\"" + getVarName() + "\");" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			}
			else {
				declaration = (((style & M_REASSIGN) > 0 ) ? "" : getVarClass()) + " " + getVarName() + " = (" + getVarClass() + ") "+context+".getAttribute(\"" + getVarName() + "\");" + ENDL; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			}
		}
		return declaration;
	}

	public String getDescription() {
		return fDescription;
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

	public void setDescription(String description) {
		fDescription = description;
	}
}
