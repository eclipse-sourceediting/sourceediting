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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;

/**
 * Base class for PCDATA type element declarations.<br>
 */
abstract class HedPcdata extends HTMLElemDeclImpl {

	/**
	 */
	public HedPcdata(String elementName, ElementCollection collection) {
		super(elementName, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_PCDATA;
	}

	/**
	 * Content.<br>
	 * PCDATA type always returns <code>null</code>.
	 * <br>
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMContent
	 */
	public CMContent getContent() {
		return null;
	}

	/**
	 * Content type.<br>
	 * @see org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration
	 */
	public int getContentType() {
		return CMElementDeclaration.PCDATA;
	}
}
