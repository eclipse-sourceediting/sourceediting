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

import org.eclipse.wst.jsdt.core.WorkingCopyOwner;

/**




 * Provisional API: This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
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
