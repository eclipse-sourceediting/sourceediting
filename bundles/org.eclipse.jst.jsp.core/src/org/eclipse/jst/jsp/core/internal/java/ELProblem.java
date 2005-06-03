package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.jface.text.Position;

public class ELProblem {
	private Position fPos;
	private String fMessage;
	
	public ELProblem(Position pos, String message)	{
		fPos = pos;
		fMessage = message;
	}
	
	public String getMessage() {
		return fMessage;
	}
	

	public Position getPosition() {
		return fPos;
	}
}
