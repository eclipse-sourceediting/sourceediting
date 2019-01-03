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
package org.eclipse.wst.html.core.internal.contentmodel;



import java.util.Arrays;

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;


/**
 * (INS|DEL)
 */
final class HedMarkChanges extends HedFlowContainer {

	/**
	 */
	public HedMarkChanges(String elementName, ElementCollection collection) {
		super(elementName, collection);
	}

	/**
	 * %attrs;
	 * (cite %URI; #IMPLIED)
	 * (datetime %Datetime; #IMPLIED) 
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);

		String[] names = {HTML40Namespace.ATTR_NAME_CITE, HTML40Namespace.ATTR_NAME_DATETIME};
		attributeCollection.getDeclarations(attributes, Arrays.asList(names).iterator());
	}
}
