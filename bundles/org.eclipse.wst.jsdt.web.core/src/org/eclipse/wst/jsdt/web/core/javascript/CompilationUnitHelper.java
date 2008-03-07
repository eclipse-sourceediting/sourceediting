package org.eclipse.wst.jsdt.web.core.javascript;

import org.eclipse.wst.jsdt.core.WorkingCopyOwner;

/**
 * To ensure there is only one instance of ProblemRequestor and WorkingCopyOwner
 * for JSP plugins. These were removed from JSPTranslation to ensure that the
 * JSPTranslation was not held in memory by any type of JDT lists (caching
 * search results, etc...)
 * 
 * @author pavery
 */
public class CompilationUnitHelper {
	private static CompilationUnitHelper instance;
	
	public synchronized static final CompilationUnitHelper getInstance() {
		if (CompilationUnitHelper.instance == null) {
			CompilationUnitHelper.instance = new CompilationUnitHelper();
		}
		return CompilationUnitHelper.instance;
	}
	private JsProblemRequestor fProblemRequestor = null;
	private WorkingCopyOwner fWorkingCopyOwner = null;
	
	private CompilationUnitHelper() {
	// force use of instance
	}
	
	public JsProblemRequestor getProblemRequestor() {
		if (fProblemRequestor == null) {
			fProblemRequestor = new JsProblemRequestor();
		}
		return fProblemRequestor;
	}
	
	public WorkingCopyOwner getWorkingCopyOwner() {
		if (fWorkingCopyOwner == null) {
			fWorkingCopyOwner = new WorkingCopyOwner() {
				
				public String toString() {
					return "JSP Working copy owner"; //$NON-NLS-1$
				}
			};
		}
		return fWorkingCopyOwner;
	}
}
