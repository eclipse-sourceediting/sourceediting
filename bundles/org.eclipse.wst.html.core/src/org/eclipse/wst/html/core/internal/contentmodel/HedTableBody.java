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
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;


/**
 * THEAD/TFOOT/TBODY
 */
final class HedTableBody extends HTMLElemDeclImpl {

	private static String[] terminators = {HTML40Namespace.ElementName.CAPTION, HTML40Namespace.ElementName.COL, HTML40Namespace.ElementName.COLGROUP, HTML40Namespace.ElementName.THEAD, HTML40Namespace.ElementName.TBODY, HTML40Namespace.ElementName.TFOOT};

	/**
	 */
	public HedTableBody(String elementName, ElementCollection collection) {
		super(elementName, collection);
		typeDefinitionName = ComplexTypeDefinitionFactory.CTYPE_TR_CONTAINER;
		correctionType = CORRECT_EMPTY;
		layoutType = LAYOUT_BLOCK;
		if (elementName == HTML40Namespace.ElementName.TBODY) {
			omitType = OMIT_BOTH;
		}
		else {
			omitType = OMIT_END;
		}
		indentChild = true;
	}

	/**
	 * TBODY/TFOOT/THEAD
	 * %attrs;
	 * %cellhalign;
	 * %cellvalign;
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		// %cellhalign;
		attributeCollection.getCellhalign(attributes);
		// %cellvalign;
		attributeCollection.getCellvalign(attributes);
	}

	/**
	 * THEAD, TFOOT and TBODY have terminators.
	 * @return java.util.Iterator
	 */
	protected Iterator getTerminators() {
		return Arrays.asList(terminators).iterator();
	}
}
