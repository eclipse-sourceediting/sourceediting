/*******************************************************************************
 * Copyright (c) 2005, 2010 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     IBM Corporation - Bug 298304 User-configurable severities for EL problems
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.jspel;

import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.java.IJSPProblem;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;

/**
 * <p>Represents an EL problem in a JSP document</p>
 */
public class ELProblem implements IJSPProblem {
	private Position fPos;
	private String fMessage;
	private int fId = IJSPProblem.ELProblem;
	private int fSeverity;

	/**
	 * @param pos should be relative to the JSP document the error is to be reported on
	 * @param message
	 */
	public ELProblem(Position pos, String message)	{
		this(pos, message, ValidationMessage.ERROR);
	}

	public ELProblem(Position pos, String message, int id)	{
		this(pos, message, ValidationMessage.ERROR, id);
	}

	public ELProblem(int severity, Position pos, String message) {
		fPos = pos;
		fMessage = message;
		fSeverity = severity;
	}

	public ELProblem(Position pos, String message, int severity, int id)	{
		fPos = pos;
		fMessage = message;
		fId = id;
		fSeverity = severity;
	}
	
	public String getMessage() {
		return fMessage;
	}
	
	public Position getPosition() {
		return fPos;
	}

	public String[] getArguments() {
		return null;
	}

	public int getID() {
		return fId;
	}
	
	public int getEID() {
		return fId;
	}

	public char[] getOriginatingFileName() {
		return null;
	}

	public int getSourceEnd() {
		return fPos.getOffset() + fPos.getLength();
	}

	public int getSourceLineNumber() {
		return 0;
	}

	public int getSourceStart() {
		return fPos.getOffset();
	}

	public boolean isError() {
		return fSeverity == ValidationMessage.ERROR;
	}

	public boolean isWarning() {
		return fSeverity == ValidationMessage.WARNING;
	}

	public void setSourceEnd(int sourceEnd) {}

	public void setSourceLineNumber(int lineNumber) {}

	public void setSourceStart(int sourceStart) {}
}
