/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

/**
 * A reconciler that allows IReconcilingStrategies to be assigned to partition
 * types. The reconciler will call the registered IReconcilingStrategy when
 * dirty regions which contain partitions of a specific type need to be
 * reconciled.
 * 
 * @since 1.1
 */
public interface IConfigurableReconciler {
	/**
	 * Sets the reconciling strategy for the reconciler to use for the
	 * specified content type.
	 * 
	 * @param partitionType
	 *            the partition type
	 * @param strategy
	 *            the reconciling strategy
	 */
	void setReconcilingStrategy(String partitionType, IReconcilingStrategy strategy);
}
