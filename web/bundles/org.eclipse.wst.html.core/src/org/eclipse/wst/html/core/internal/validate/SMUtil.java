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
package org.eclipse.wst.html.core.internal.validate;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

final class SMUtil {

	private SMUtil() {
		super();
	}

	/* get an ancestor element ignoring implicit ones. */
	public static Element getParentElement(Node child) {
		if (child == null)
			return null;

		Node p = child.getParentNode();
		while (p != null) {
			if (p.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) p;
			}
			p = p.getParentNode();
		}
		return null;
	}
}
