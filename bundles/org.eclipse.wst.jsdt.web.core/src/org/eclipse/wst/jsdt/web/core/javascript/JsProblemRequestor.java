package org.eclipse.wst.jsdt.web.core.javascript;

/**
 * @author pavery
 */
import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.jsdt.core.IProblemRequestor;
import org.eclipse.wst.jsdt.core.compiler.IProblem;

class JsProblemRequestor implements IProblemRequestor {
	private List fCollectedProblems;
	private boolean fIsActive = false;
	private boolean fIsRunning = false;
	
	public void acceptProblem(IProblem problem) {
		if (isActive()) {
			fCollectedProblems.add(problem);
		}
	}
	
	public void beginReporting() {
		fIsRunning = true;
		fCollectedProblems = new ArrayList();
	}
	
	public void endReporting() {
		fIsRunning = false;
	}
	
	/**
	 * @return the list of collected problems
	 */
	public List getCollectedProblems() {
		return fCollectedProblems;
	}
	
	public boolean isActive() {
		return fIsActive && fCollectedProblems != null;
	}
	
	public boolean isRunning() {
		return fIsRunning;
	}
	
	/**
	 * Sets the active state of this problem requestor.
	 * 
	 * @param isActive
	 *            the state of this problem requestor
	 */
	public void setIsActive(boolean isActive) {
		if (fIsActive != isActive) {
			fIsActive = isActive;
			if (fIsActive) {
				startCollectingProblems();
			} else {
				stopCollectingProblems();
			}
		}
	}
	
	/**
	 * Tells this annotation model to collect temporary problems from now on.
	 */
	private void startCollectingProblems() {
		fCollectedProblems = new ArrayList();
	}
	
	/**
	 * Tells this annotation model to no longer collect temporary problems.
	 */
	private void stopCollectingProblems() {
	// do nothing
	}
}
