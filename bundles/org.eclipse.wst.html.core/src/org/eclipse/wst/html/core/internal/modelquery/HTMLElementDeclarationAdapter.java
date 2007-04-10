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
package org.eclipse.wst.html.core.internal.modelquery;



import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;

/**
 */
public class HTMLElementDeclarationAdapter implements ElementDeclarationAdapter {

	private CMElementDeclaration declaration = null;

	/**
	 */
	public HTMLElementDeclarationAdapter() {
		super();
	}

	/**
	 */
	public CMElementDeclaration getDeclaration() {
		return this.declaration;
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return (type == ElementDeclarationAdapter.class);
	}

	/**
	 */
	public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
	}

	/**
	 */
	public void setDeclaration(CMElementDeclaration declaration) {
		this.declaration = declaration;
	}
}
