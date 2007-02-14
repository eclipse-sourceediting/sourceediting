/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.validate;



import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.sse.core.internal.validate.ValidationReporter;

public abstract class ValidationComponent implements ValidationAdapter {

	protected ValidationReporter reporter = null;

	/**
	 * ValidationComponent constructor comment.
	 */
	public ValidationComponent() {
		super();
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type allows it
	 * to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return (type == ValidationAdapter.class);
	}

	/**
	 */
	public void notifyChanged(org.eclipse.wst.sse.core.internal.provisional.INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		// This method will be implemented in the V2.
	}

	/**
	 */
	public void setReporter(ValidationReporter reporter) {
		this.reporter = reporter;
	}

	ValidationReporter getReporter() {
		return this.reporter;
	}
}
