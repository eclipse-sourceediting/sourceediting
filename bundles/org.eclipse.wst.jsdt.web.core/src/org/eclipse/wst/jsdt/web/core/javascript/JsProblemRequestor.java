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
package org.eclipse.wst.jsdt.web.core.javascript;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
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
