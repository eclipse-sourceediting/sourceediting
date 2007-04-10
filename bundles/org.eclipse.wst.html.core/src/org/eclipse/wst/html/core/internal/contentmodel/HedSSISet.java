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
 * SSI:SET.
 */
final class HedSSISet extends HedSSIBase {

	/**
	 */
	public HedSSISet(ElementCollection collection) {
		super(HTML40Namespace.ElementName.SSI_SET, collection);
	}

	/**
	 * SSI:SET
	 * (var CDATA #IMPLIED)
	 * (value CDATA #IMPLIED)
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		String[] names = {HTML40Namespace.ATTR_NAME_VAR, HTML40Namespace.ATTR_NAME_VALUE};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
