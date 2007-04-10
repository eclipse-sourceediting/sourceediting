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
package org.eclipse.wst.html.core.internal.validate;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.sse.core.internal.validate.ValidationReporter;
import org.eclipse.wst.xml.core.internal.validate.ValidationComponent;

abstract class CompositeValidator extends ValidationComponent {

	protected Vector components = new Vector();

	/**
	 * CompositeValidator constructor comment.
	 */
	public CompositeValidator() {
		super();
	}

	/**
	 */
	public void setReporter(ValidationReporter reporter) {
		super.setReporter(reporter);

		Iterator i = components.iterator();
		while (i.hasNext()) {
			ValidationAdapter component = (ValidationAdapter) i.next();
			if (component == null)
				continue;
			component.setReporter(reporter);
		}
	}

	/**
	 */
	public void validate(IndexedRegion node) {
		Iterator i = components.iterator();
		while (i.hasNext()) {
			ValidationComponent component = (ValidationComponent) i.next();
			if (component == null)
				continue;
			component.validate(node);
		}
	}

	/**
	 */
	void add(ValidationComponent validator) {
		components.add(validator);
	}

	/**
	 * This method registers all components in 'validators'.
	 * Each derivative must call this methid in its constructor.
	 */
	protected void register(ValidationComponent[] validators) {
		for (int i = 0; i < validators.length; i++) {
			if (validators[i] != null)
				add(validators[i]);
		}
	}
}
