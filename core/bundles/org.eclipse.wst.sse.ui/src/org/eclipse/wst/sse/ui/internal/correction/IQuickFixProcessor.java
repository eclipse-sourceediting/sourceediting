/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.correction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;

/**
 * @deprecated since 2.0 RC0 Use
 *             org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
 */
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
