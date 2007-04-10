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



import java.util.Arrays;

import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;

/**
 * TITLE.
 */
final class HedTITLE extends HedPcdata {

	/**
	 */
	public HedTITLE(ElementCollection collection) {
		super(CHTMLNamespace.ElementName.TITLE, collection);
		layoutType = LAYOUT_HIDDEN;
	}

	/**
	 * TITLE.
	 * %i18n;
	 */
	protected void createAttributeDeclarations() {
		if (attributes != null)
			return; // already created.
		if (attributeCollection == null)
			return; // fatal

		attributes = new CMNamedNodeMapImpl();

		// %i18n;
		attributeCollection.getI18n(attributes);
	}

	/**
	 * Exclusion.
	 * <code>TITLE</code> has the exclusion.
	 * It is <code>%head.misc;</code>.
	 * %head.misc; is <code>SCRIPT|STYLE|META|LINK|OBJECT</code>.
	 * <br>
	 */
	public CMContent getExclusion() {
		if (exclusion != null)
			return exclusion; // already created.
		if (elementCollection == null)
			return null;

		// (SCRIPT|STYLE|META|LINK|OBJECT)
		exclusion = new CMGroupImpl(CMGroup.CHOICE, 1, 1);

		String[] names = {CHTMLNamespace.ElementName.META,};
		elementCollection.getDeclarations(exclusion, Arrays.asList(names).iterator());
		return exclusion;
	}
}
