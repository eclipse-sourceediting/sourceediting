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
package org.eclipse.wst.xml.core.internal.validate;

import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.validate.ValidationAdapter;
import org.w3c.dom.Node;

public abstract class AbstractPropagatingValidator extends ValidationComponent {

	/**
	 * Constructor for AbstractPropagatingValidator.
	 */
	public AbstractPropagatingValidator() {
		super();
	}

	/**
	 */
	public void validate(IndexedRegion node) {
		if (node == null)
			return;
		getValidator().validate(node);


		Propagator.propagateToChildElements(getPropagatee(), (Node) node);
	}

	protected abstract ValidationAdapter getValidator();

	protected abstract ValidationComponent getPropagatee();

}
