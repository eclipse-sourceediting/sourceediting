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
package org.eclipse.wst.css.core.internal.metamodel;

public interface CSSMMSelector extends CSSMMNode {
	String getSelectorType();

	String getSelectorString();


	static final String TYPE_EXPRESSION = "Expression"; //$NON-NLS-1$
	static final String TYPE_PSEUDO_CLASS = "PseudoClass"; //$NON-NLS-1$
	static final String TYPE_PSEUDO_ELEMENT = "PseudoElement"; //$NON-NLS-1$

	// embedded expression type
	static final String EXPRESSION_DESCENDANT = "descendant"; //$NON-NLS-1$
	static final String EXPRESSION_CHILD = "child"; //$NON-NLS-1$
	static final String EXPRESSION_ADJACENT = "adjacent"; //$NON-NLS-1$
	static final String EXPRESSION_UNIVERSAL = "universal"; //$NON-NLS-1$
	static final String EXPRESSION_ATTRIBUTE = "attribute"; //$NON-NLS-1$
}
