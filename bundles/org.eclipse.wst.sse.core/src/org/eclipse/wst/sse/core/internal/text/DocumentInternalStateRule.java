/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/

package org.eclipse.wst.sse.core.internal.text;

import org.eclipse.core.runtime.jobs.ISchedulingRule;


public class DocumentInternalStateRule implements ISchedulingRule {

	private final static int READ_STATE = 2;
	public final static DocumentInternalStateRule READ_RULE = new DocumentInternalStateRule(READ_STATE);

	private final static int WRITE_STATE = 1;
	public final static DocumentInternalStateRule WRITE_RULE = new DocumentInternalStateRule(WRITE_STATE);
	private int fState;

	/**
	 *  
	 */
	public DocumentInternalStateRule(int state) {
		super();
		fState = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean contains(ISchedulingRule rule) {
		boolean result = (rule instanceof DocumentInternalStateRule);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		boolean result = (fState == WRITE_STATE) && (rule instanceof DocumentInternalStateRule);
		return result;
	}

}
