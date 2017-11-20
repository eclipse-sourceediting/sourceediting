/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.saxparser;



public class ErrorMessage {
	private int errorLine, errorColumn, severity;
	private String errorString;


	public ErrorMessage() {
	}

	public void setErrorMessage(String error) {
		errorString = error;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public void setSeverity(String severity) {
		if (severity == "warning") { //$NON-NLS-1$
			this.severity = 0;
		}
		else if (severity == "error") { //$NON-NLS-1$
			this.severity = 1;
		}
		else if (severity == "fatal") { //$NON-NLS-1$
			this.severity = 2;
		}
	}

	public void setErrorLine(int line) {
		errorLine = line;
	}

	public void setErrorColumn(int column) {
		errorColumn = column;
	}

	public String getErrorMessage() {
		return errorString;
	}

	public int getSeverity() {
		return severity;
	}

	public int getErrorLine() {
		return errorLine;
	}

	public int getErrorColumn() {
		return errorColumn;
	}

	private int startOffset = -1;

	public void setErrorStartOffset(int start) {
		startOffset = start;
	}

	public int getErrorStartOffset() {
		return startOffset;
	}

	private int endOffset = -1;

	public void setErrorEndOffset(int end) {
		endOffset = end;
	}

	public int getErrorEndOffset() {
		return endOffset;
	}


	Object object;

	/**
	 * Get the value of the closest object to the error.
	 * 
	 * @return value of object.
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Set the value of closest object to the error.
	 * 
	 * @param v
	 *            Value to assign to object.
	 */
	public void setObject(Object v) {
		this.object = v;
	}


	public void addNewErrorMessageLine(String newLine) {
		errorString += "\n" + newLine; //$NON-NLS-1$
	}
}
