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
package org.eclipse.wst.sse.ui.internal.reconcile;

import org.eclipse.jface.text.reconciler.IReconcileStep;


/**
 * Implementation of <code>IReconcileAnnotationKey</code>
 * note: clients should use the method StructuredReconcileStep#createKey(String partitionType, int scope)
 * 
 * @author pavery
 */
public class ReconcileAnnotationKey implements IReconcileAnnotationKey {

	private IReconcileStep fReconcileStep = null;
	private String fPartitionType = null;
	private int fScope;

	public ReconcileAnnotationKey(IReconcileStep step, String partitionType, int scope) {
		fReconcileStep = step;
		fPartitionType = partitionType;
		fScope = scope;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.xml.reconcile.IReconcileAnnotationKey#getId()
	 */
	public String getPartitionType() {
		return fPartitionType;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.xml.reconcile.IReconcileAnnotationKey#getScope()
	 */
	public int getScope() {
		return fScope;
	}

	/**
	 * @see org.eclipse.wst.sse.ui.xml.reconcile.IReconcileAnnotationKey#getStep()
	 */
	public IReconcileStep getStep() {
		return fReconcileStep;
	}

	public String toString() {
		return this.getClass() + "\r\nid: " + fPartitionType + "\nscope: " + fScope; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
