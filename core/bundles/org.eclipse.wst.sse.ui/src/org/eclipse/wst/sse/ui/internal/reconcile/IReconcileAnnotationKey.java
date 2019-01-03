/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.jface.text.reconciler.IReconcileStep;

/**
 * Defines an annotation key that the
 * <code>AbstractStructuredTextReconcilingStrategy</code> knows how to
 * remove appropriately.
 * 
 * @deprecated not API, this will probably just become an abstract class
 * 
 * @author pavery
 */
public interface IReconcileAnnotationKey {
	static final int PARTIAL = 1;
	static final int TOTAL = 0;

	String getPartitionType();

	//int getScope();

	IReconcileStep getStep();
}
