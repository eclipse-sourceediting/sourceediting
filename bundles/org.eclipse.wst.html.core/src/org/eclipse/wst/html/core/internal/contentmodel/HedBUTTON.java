/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
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
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;

/**
 * BUTTON.
 */
final class HedBUTTON extends HedFlowContainer {

	/**
	 */
	public HedBUTTON(ElementCollection collection) {
		super(HTML40Namespace.ElementName.BUTTON, collection);
		layoutType = LAYOUT_BLOCK;
	}

	/**
	 * %attrs;
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %attrs;
		attributeCollection.getAttrs(attributes);
		
		//different sets of attributes for html 4 & 5
		attributeCollection.createAttributeDeclarations(HTML40Namespace.ElementName.BUTTON, attributes);
	
	}

	/**
	 * Exclusion.
	 * <code>FORM</code> has the exclusion.
	 * It is <code>FORM</code> itself.
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null; // fatal

		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);
		String[] names = {HTML40Namespace.ElementName.A, HTML40Namespace.ElementName.FORM, HTML40Namespace.ElementName.ISINDEX, HTML40Namespace.ElementName.FIELDSET, HTML40Namespace.ElementName.IFRAME};
		elementCollection.getDeclarations(exclusion, Arrays.asList(names).iterator());
		elementCollection.getFormctrl(exclusion);

		return exclusion;
	}

	/**
	 */
	public CMNamedNodeMap getProhibitedAncestors() {
		if (prohibitedAncestors != null)
			return prohibitedAncestors;

		String[] names = {HTML40Namespace.ElementName.BUTTON};
		prohibitedAncestors = elementCollection.getDeclarations(names);

		return prohibitedAncestors;
	}
}
