/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.sse.ui.internal.correction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;

public interface IQuickFixProcessor {
	/**
	 * Returns true if the processor can fix the given problem. This test
	 * should be an optimistic guess and be extremly cheap.
	 */
	boolean canFix(Annotation annnotation);

	/**
	 * Collects proposals for fixing the given problem.
	 */
	ICompletionProposal[] getProposals(Annotation annnotation) throws CoreException;
}
