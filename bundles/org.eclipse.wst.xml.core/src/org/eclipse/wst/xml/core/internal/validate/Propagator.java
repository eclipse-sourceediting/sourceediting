/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.internal.validate;

import org.eclipse.wst.sse.core.INodeNotifier;
import org.eclipse.wst.sse.core.IndexedRegion;
import org.eclipse.wst.sse.core.validate.ValidationAdapter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Propagator {

	/**
	 * Propagator is just a placeholder of utilities.
	 * Don't instantiate.
	 */
	private Propagator() {
		super();
	}

	public static void propagateToChildElements(ValidationComponent validator, Node parent) {
		if (parent == null)
			return;
		Class clazz = validator.getClass();

		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child == null || child.getNodeType() != Node.ELEMENT_NODE)
				continue;

			INodeNotifier notifier = (INodeNotifier) child;
			ValidationAdapter va = (ValidationAdapter) notifier.getExistingAdapter(clazz);
			if (va == null) {
				notifier.addAdapter(validator);
				va = validator;
			}
			va.validate((IndexedRegion) child);
		}
	}
}
