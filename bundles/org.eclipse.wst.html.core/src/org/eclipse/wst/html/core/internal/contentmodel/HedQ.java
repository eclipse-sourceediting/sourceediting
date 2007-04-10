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

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;


/**
 * Q.
 */
final class HedQ extends HedInlineContainer {

	/**
	 */
	public HedQ(ElementCollection collection) {
		super(HTML40Namespace.ElementName.Q, collection);
		correctionType = CORRECT_DUPLICATED;
	}

	/**
	 * %attrs;
	 * (cite %URI; #IMPLIED) 
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal
		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_CITE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
