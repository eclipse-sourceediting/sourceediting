/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.contentmodel.tld;

import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.JSP12TLDNames;
import org.eclipse.jst.jsp.core.internal.contentmodel.tld.provisional.TLDVariable;
import org.eclipse.wst.sse.core.utils.StringUtils;


public class TLDVariableImpl implements TLDVariable {
	// optional - defaults to true
	private boolean declare = true;

	private String fDescription;
	// required
	private String nameFromAttribute;
	// required
	private String nameGiven;
	// optional - defaults to NESTED
	private String scope = JSP12TLDNames.VARIABLE_SCOPE_NESTED;
	// required - defaults to a String
	private String variableClass = "java.lang.String"; //$NON-NLS-1$

	private String fAlias;

	public boolean getDeclare() {
		return declare;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return fDescription;
	}

	public String getNameFromAttribute() {
		return nameFromAttribute;
	}

	public String getNameGiven() {
		return nameGiven;
	}

	public String getScope() {
		return scope;
	}

	public String getVariableClass() {
		return variableClass;
	}

	public void setDeclare(boolean declare) {
		this.declare = declare;
	}

	public void setDeclareString(String decl) {
		if (decl != null) {
			setDeclare(decl.equals(JSP12TLDNames.TRUE) || decl.equals(JSP12TLDNames.YES));
		}
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		fDescription = description;
	}

	public void setNameFromAttribute(String nameFromAttribute) {
		this.nameFromAttribute = nameFromAttribute;
	}

	public void setNameGiven(String nameGiven) {
		this.nameGiven = nameGiven;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setVariableClass(String variableClass) {
		this.variableClass = variableClass;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append("\n\t name given:" + StringUtils.escape(getNameGiven())); //$NON-NLS-1$
		buffer.append("\n\t name from attribute:" + StringUtils.escape(getNameFromAttribute())); //$NON-NLS-1$
		// Boolean.toString(boolean) is introduced in JDK 1.4
		// buffer.append("\n\t declare:" +
		// StringUtils.escape(Boolean.toString(getDeclare())));
		buffer.append("\n\t declare:" + StringUtils.toString(getDeclare())); //$NON-NLS-1$
		buffer.append("\n\t scope:" + StringUtils.escape(getScope())); //$NON-NLS-1$
		buffer.append("\n\t variable class:" + StringUtils.escape(getVariableClass())); //$NON-NLS-1$
		return buffer.toString();
	}

	public String getAlias() {
		return fAlias;
	}

	public void setAlias(String alias) {
		fAlias = alias;
	}
}
