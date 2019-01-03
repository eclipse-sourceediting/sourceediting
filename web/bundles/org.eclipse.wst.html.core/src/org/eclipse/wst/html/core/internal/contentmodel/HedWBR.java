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



final class HedWBR extends HedEmpty {

	public HedWBR(ElementCollection collection) {
		super(HTML40Namespace.ElementName.WBR, collection);
		// LAYOUT_BREAK.
		// same as BR.
		layoutType = LAYOUT_BREAK;
	}

	protected void createAttributeDeclarations() {
		// No attributes is defined.
		if (attributes != null)
			return;
		attributes = new CMNamedNodeMapImpl();
	}
}
