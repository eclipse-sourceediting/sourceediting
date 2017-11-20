/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.formatter;


public class XMLFormattingConstraints {
	public final static String PRESERVE = "PRESERVE"; //$NON-NLS-1$
	public final static String COLLAPSE = "COLLAPSE"; //$NON-NLS-1$
	public final static String IGNORE = "IGNORE"; //$NON-NLS-1$
	public final static String IGNOREANDTRIM = "IGNOREANDTRIM"; //$NON-NLS-1$
	public final static String DEFAULT = "DEFAULT"; //$NON-NLS-1$
	public final static String REPLACE = "REPLACE"; //$NON-NLS-1$
	
	public final static String INDENT = "INDENT"; //$NON-NLS-1$
	public final static String NEW_LINE = "NEW_LINE"; //$NON-NLS-1$
	public final static String INLINE = "INLINE"; //$NON-NLS-1$

	private int fAvailableLineWidth = 0;
	private int fIndentLevel = 0;
	private String fIndentStrategy;
	private String fWhitespaceStrategy;
	private boolean fIsIndentStrategyAHint = false;
	private boolean fIsWhitespaceStrategyAHint = false;

	/**
	 * Initializes the values in this formatting constraint with values from
	 * constraints
	 * 
	 * @param constraints
	 *            cannot be null
	 */
	public void copyConstraints(XMLFormattingConstraints constraints) {
		setAvailableLineWidth(constraints.getAvailableLineWidth());
		setIndentLevel(constraints.getIndentLevel());
		setIndentStrategy(constraints.getIndentStrategy());
		setWhitespaceStrategy(constraints.getWhitespaceStrategy());
	}

	public int getAvailableLineWidth() {
		return fAvailableLineWidth;
	}

	public void setAvailableLineWidth(int lineWidth) {
		fAvailableLineWidth = lineWidth;
	}

	public int getIndentLevel() {
		return fIndentLevel;
	}

	public void setIndentLevel(int indentLevel) {
		fIndentLevel = indentLevel;
	}

	public String getIndentStrategy() {
		return fIndentStrategy;
	}

	public void setIndentStrategy(String indentStrategy) {
		fIndentStrategy = indentStrategy;
	}

	public String getWhitespaceStrategy() {
		return fWhitespaceStrategy;
	}

	public void setWhitespaceStrategy(String whitespaceStrategy) {
		fWhitespaceStrategy = whitespaceStrategy;
	}

	public boolean isIndentStrategyAHint() {
		return fIsIndentStrategyAHint;
	}

	public void setIsIndentStrategyAHint(boolean isIndentStrategyAHint) {
		fIsIndentStrategyAHint = isIndentStrategyAHint;
	}

	public boolean isWhitespaceStrategyAHint() {
		return fIsWhitespaceStrategyAHint;
	}

	public void setIsWhitespaceStrategyAHint(boolean isWhitespaceStrategyAHint) {
		fIsWhitespaceStrategyAHint = isWhitespaceStrategyAHint;
	}
}