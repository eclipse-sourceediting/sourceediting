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

import org.eclipse.wst.html.core.internal.provisional.HTML40Namespace;


/**
 * NOBR -- not standard tag but it is commonly used.
 */
final class HedNOBR extends HedInlineContainer {


	public HedNOBR(ElementCollection collection) {
		super(HTML40Namespace.ElementName.NOBR, collection);
		correctionType = HTMLElementDeclaration.CORRECT_DUPLICATED;
	}

	protected void createAttributeDeclarations() {
		// No attributes is defined.
		if (attributes != null)
			return;
		attributes = new CMNamedNodeMapImpl();
	}
}
