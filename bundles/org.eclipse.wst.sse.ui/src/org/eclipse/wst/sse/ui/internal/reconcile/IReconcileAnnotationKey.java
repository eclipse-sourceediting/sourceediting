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
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.jface.text.reconciler.IReconcileStep;

/**
 * Defines an annotation key that the
 * <code>AbstractStructuredTextReconcilingStrategy</code> knows how to
 * remove appropriately.
 * 
 * @author pavery
 */
public interface IReconcileAnnotationKey {
	static final int PARTIAL = 1;
	static final int TOTAL = 0;

	String getPartitionType();

	int getScope();

	IReconcileStep getStep();
}
