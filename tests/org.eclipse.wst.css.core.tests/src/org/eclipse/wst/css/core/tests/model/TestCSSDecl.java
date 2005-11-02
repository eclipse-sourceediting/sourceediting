/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/

package org.eclipse.wst.css.core.tests.model;

import junit.framework.TestCase;

import org.eclipse.wst.css.core.internal.provisional.document.ICSSStyleDeclaration;
import org.eclipse.wst.css.core.internal.util.CSSStyleDeclarationFactory;
import org.eclipse.wst.css.core.internal.util.declaration.CSSPropertyContext;

public class TestCSSDecl extends TestCase {
	public void testDecl() {
		CSSPropertyContext context = new CSSPropertyContext();
		ICSSStyleDeclaration decl = CSSStyleDeclarationFactory.getInstance().createStyleDeclaration();
		context.initialize(decl);
		decl.setCssText(getString() != null ? getString() : "");//$NON-NLS-1$
	}

	private String getString() {
		return null;
	}
}
