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


import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.eclipse.wst.sse.core.internal.validate.ValidationReporter;
import org.eclipse.wst.xml.core.internal.validate.AbstractPropagatingValidator;
import org.eclipse.wst.xml.core.internal.validate.ValidationComponent;

class DocumentPropagatingValidator extends AbstractPropagatingValidator {


	private ValidationComponent validator = new HTMLSimpleDocumentValidator();
	private ElementPropagatingValidator propagatee = new ElementPropagatingValidator();

	public DocumentPropagatingValidator() {
		super();
	}

	public void validate(IndexedRegion node) {
		getPropagatee().setReporter(this.reporter);
		super.validate(node);
	}

	public boolean isAdapterForType(Object type) {
		return (type == DocumentPropagatingValidator.class || super.isAdapterForType(type));
	}

	public void setReporter(ValidationReporter reporter) {
		super.setReporter(reporter);
		validator.setReporter(reporter);
		propagatee.setReporter(reporter);
	}

	protected final ValidationComponent getPropagatee() {
		return propagatee;
	}

	protected final ValidationAdapter getValidator() {
		return validator;
	}
}
