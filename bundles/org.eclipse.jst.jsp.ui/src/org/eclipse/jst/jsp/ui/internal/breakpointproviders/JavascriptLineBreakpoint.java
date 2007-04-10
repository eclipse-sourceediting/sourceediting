/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jul 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.eclipse.jst.jsp.ui.internal.breakpointproviders;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author davidw
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class JavascriptLineBreakpoint {

	/**
	 * @param res
	 * @param lineNumber
	 * @param pos
	 * @param pos1
	 */
	public JavascriptLineBreakpoint(IResource res, int lineNumber, int pos, int pos1) {

		// TODO Should be deleted? Along with calling class?
	}

	/**
	 * 
	 */
	public JavascriptLineBreakpoint() {
		super();
	}

	/**
	 * 
	 */
	public IResource getResource() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public int getLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

}
