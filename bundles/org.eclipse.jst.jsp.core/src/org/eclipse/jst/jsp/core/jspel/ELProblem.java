/*******************************************************************************
 * Copyright (c) 2005, 2006 BEA Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BEA Systems - initial implementation
 *     
 *******************************************************************************/

package org.eclipse.jst.jsp.core.jspel;

import org.eclipse.jface.text.Position;
import org.eclipse.jst.jsp.core.internal.java.IJSPProblem;

/**
 * All ELProblems are currently assumed to be errors.
 */
public class ELProblem implements IJSPProblem {
	private Position fPos;
	private String fMessage;
	private int fId = IJSPProblem.ELProblem;

	/**
	 * @param pos should be relative to the JSP document the error is to be reported on
	 * @param message
	 */
	public ELProblem(Position pos, String message)	{
		fPos = pos;
		fMessage = message;
	}

	public ELProblem(Position pos, String message, int id)	{
		fPos = pos;
		fMessage = message;
		fId = id;
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
		return true;
	}

	public boolean isWarning() {
		return false;
	}

	public void setSourceEnd(int sourceEnd) {}

	public void setSourceLineNumber(int lineNumber) {}

	public void setSourceStart(int sourceStart) {}
}
