package org.eclipse.wst.jsdt.web.core.internal.java;

import org.eclipse.jsdt.core.WorkingCopyOwner;

/**
 * To ensure there is only one instance of ProblemRequestor and WorkingCopyOwner
 * for JSP plugins. These were removed from JSPTranslation to ensure that the
 * JSPTranslation was not held in memory by any type of JDT lists (caching
 * search results, etc...)
 * 
 * @author pavery
 */
public class CompilationUnitHelper {

	private JSPProblemRequestor fProblemRequestor = null;
	private WorkingCopyOwner fWorkingCopyOwner = null;
	private static CompilationUnitHelper instance;

	private CompilationUnitHelper() {
		// force use of instance
	}

	public synchronized static final CompilationUnitHelper getInstance() {

		if (instance == null) {
			instance = new CompilationUnitHelper();
		}
		return instance;
	}

	public JSPProblemRequestor getProblemRequestor() {

		if (fProblemRequestor == null) {
			fProblemRequestor = new JSPProblemRequestor();
		}
		return fProblemRequestor;
	}

	public WorkingCopyOwner getWorkingCopyOwner() {

		if (fWorkingCopyOwner == null) {
			fWorkingCopyOwner = new WorkingCopyOwner() {
				@Override
				public String toString() {
					return "JSP Working copy owner"; //$NON-NLS-1$
				}
			};
		}
		return fWorkingCopyOwner;
	}
}
