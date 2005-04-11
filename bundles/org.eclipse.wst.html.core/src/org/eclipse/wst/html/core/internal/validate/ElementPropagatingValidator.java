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
package org.eclipse.wst.html.core.internal.validate;


import org.eclipse.wst.sse.core.validate.ValidationAdapter;
import org.eclipse.wst.sse.core.validate.ValidationReporter;
import org.eclipse.wst.xml.core.internal.validate.AbstractPropagatingValidator;
import org.eclipse.wst.xml.core.internal.validate.ValidationComponent;

class ElementPropagatingValidator extends AbstractPropagatingValidator {


	private ValidationComponent validator = new HTMLSimpleValidator();

	public ElementPropagatingValidator() {
		super();
	}

	public boolean isAdapterForType(Object type) {
		return (type == ElementPropagatingValidator.class || super.isAdapterForType(type));
	}

	public void setReporter(ValidationReporter reporter) {
		super.setReporter(reporter);
		validator.setReporter(reporter);
	}

	/**
	 * @see com.ibm.sed.validate.html.AbstractPropagatingValidator#getPropagatee()
	 */
	protected ValidationComponent getPropagatee() {
		return this;
	}

	/**
	 * @see com.ibm.sed.validate.html.AbstractPropagatingValidator#getValidator()
	 */
	protected ValidationAdapter getValidator() {
		return validator;
	}
}