/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.sse.core.tests.adaptdom;

import org.eclipse.wst.sse.core.INodeAdapter;
import org.eclipse.wst.sse.core.INodeNotifier;
import org.w3c.dom.Attr;


/**
 * Insert the type's description here.
 * Creation date: (1/9/01 3:37:01 PM) 
 * @author: David Williams
 */
public class AdapterForAttrTestOnly implements INodeAdapter {
	/**
	 * CAAdapterTestOnly constructor comment.
	 */
	public AdapterForAttrTestOnly() {
		super();
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		if (type instanceof Attr)
			return true;
		else
			return false;
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		System.out.println("INodeAdapter AdapterForAttrTestOnly " +  this.hashCode() + " has been notified of " + INodeNotifier.EVENT_TYPE_STRINGS[eventType] + " (when " + notifier + " notified it)");
	}
}