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
package org.eclipse.wst.xml.core.internal.validate;

import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.w3c.dom.Node;


public abstract class AbstractPropagatingValidator extends ValidationComponent {

	/**
	 * Constructor for AbstractPropagatingValidator.
	 */
	public AbstractPropagatingValidator() {
		super();
	}

	protected abstract ValidationComponent getPropagatee();

	protected abstract ValidationAdapter getValidator();

	/**
	 * @see com.ibm.sed.adapters.validate.ValidationAdapter#validate(IndexedRegion)
	 */
	public void validate(IndexedRegion node) {
		if (node == null)
			return;
		getValidator().validate(node);


		Propagator.propagateToChildElements(getPropagatee(), (Node) node);
	}

}
