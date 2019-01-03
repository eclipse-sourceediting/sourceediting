/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.validate;


import org.eclipse.wst.xml.core.internal.validate.ValidationComponent;

class HTMLSimpleDocumentValidator extends CompositeValidator {

	/**
	 * HTMLSimpleDocumentValidator constructor comment.
	 */
	public HTMLSimpleDocumentValidator() {
		super();

		ValidationComponent[] validators = new ValidationComponent[2];

		validators[0] = new HTMLDocumentContentValidator();
		validators[1] = new SyntaxValidator();

		register(validators);
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return ((type == HTMLSimpleDocumentValidator.class) || super.isAdapterForType(type));
	}
}
