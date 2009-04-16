package org.eclipse.jst.jsp.core.internal.java;

import org.eclipse.jdt.core.compiler.IProblem;

public interface IJSPProblem extends IProblem {

	int F_PROBLEM_ID_LITERAL = 0xA0000000;
	
	int TEIClassNotFound = F_PROBLEM_ID_LITERAL + 1;
	int TEIValidationMessage = F_PROBLEM_ID_LITERAL + 2;
	int TEIClassNotInstantiated = F_PROBLEM_ID_LITERAL + 3;
	int TEIClassMisc = F_PROBLEM_ID_LITERAL + 4;	
	int TagClassNotFound = F_PROBLEM_ID_LITERAL + 5;
	int UseBeanInvalidID = F_PROBLEM_ID_LITERAL + 6;
	int UseBeanMissingTypeInfo = F_PROBLEM_ID_LITERAL + 7;
	int UseBeanAmbiguousType  = F_PROBLEM_ID_LITERAL + 8;
	
	/**
	 * @return the ID of this JSP problem
	 */
	int getEID();

}
