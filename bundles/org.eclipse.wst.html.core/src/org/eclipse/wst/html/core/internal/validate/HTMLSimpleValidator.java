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


import org.eclipse.wst.xml.core.internal.validate.ValidationComponent;

class HTMLSimpleValidator extends CompositeValidator {

	/**
	 * HTMLSimpleValidator constructor comment.
	 */
	public HTMLSimpleValidator() {
		super();

		ValidationComponent[] validators = new ValidationComponent[5];

		validators[0] = new HTMLAttributeValidator();
		validators[1] = new HTMLElementContentValidator();
		validators[2] = new SyntaxValidator();
		validators[3] = new HTMLElementAncestorValidator();
		validators[4] = new NamespaceValidator();

		register(validators);
	}

	/**
	 * Allowing the INodeAdapter to compare itself against the type
	 * allows it to return true in more than one case.
	 */
	public boolean isAdapterForType(Object type) {
		return ((type == HTMLSimpleValidator.class) || super.isAdapterForType(type));
	}
}
