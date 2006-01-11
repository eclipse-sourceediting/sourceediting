package org.eclipse.wst.xsd.ui.internal.refactor;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.wst.xml.core.internal.provisional.NameValidator;

public class Checks {
	
	public static RefactoringStatus checkName(String name) {
		RefactoringStatus result= new RefactoringStatus();
		if ("".equals(name)) //$NON-NLS-1$
			return RefactoringStatus.createFatalErrorStatus("RefactoringMessages.Checks_Choose_name"); 
		return result;
	}
	
	public static boolean isAlreadyNamed(RefactoringComponent element, String name){
		return name.equals(element.getName());
	}
	
	public static RefactoringStatus checkComponentName(String name) {
		RefactoringStatus result= new RefactoringStatus();
		if (!NameValidator.isValid(name)) //$NON-NLS-1$
			return RefactoringStatus.createFatalErrorStatus("RefactoringMessages.Checks_Choose_name"); 

		return result;
	}

}
