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
package org.eclipse.wst.html.core.internal.contentmodel.chtml;



import org.eclipse.wst.html.core.internal.contentmodel.HTMLElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;

/**
 * This class is intended to represent a complex type definition
 * in XML Schema.<br>
 */
abstract class ComplexTypeDefinition {

	protected CMGroupImpl content = null;
	protected ElementCollection collection = null;
	protected String primaryCandidateName = null;

	/**
	 * ComplexTypeDefinition constructor comment.
	 */
	public ComplexTypeDefinition(ElementCollection elementCollection) {
		super();
		collection = elementCollection;
		createContent();
	}

	/**
	 * Create an actual content model.
	 * This method should be called once and only once in the constructor.<br>
	 */
	protected abstract void createContent();

	/**
	 * @return org.eclipse.wst.xml.core.internal.contentmodel.CMGroup
	 */
	public CMGroup getContent() {
		return content;
	}

	/**
	 * @return int; Should be one of ANY, EMPTY, ELEMENT, MIXED, PCDATA, CDATA,
	 * those are defined in CMElementDeclaration.
	 */
	/* NOTE: Do we need LOGICAL type?
	 * -- 3/7/2001
	 */
	public abstract int getContentType();

	/**
	 * Get content hint.
	 */
	public HTMLElementDeclaration getPrimaryCandidate() {
		if (primaryCandidateName == null)
			return null;
		return (HTMLElementDeclaration) collection.getNamedItem(primaryCandidateName);
	}

	/**
	 * Name of complex type definition.
	 * Each singleton must know its own name.
	 * All names should be defined in
	 * {@link <code>ComplexTypeDefinitionFactory</code>} as constants.<br>
	 * @return java.lang.String
	 */
	public abstract String getTypeName();
}
