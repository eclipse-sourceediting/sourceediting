/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.validate;

import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.validate.ValidationAdapter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @deprecated since 2.0 M5 - if propogateToChildElement is needed, just copy
 *             method to your own class
 */
public class Propagator {

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

	/**
	 * Propagator is just a placeholder of utilities. Don't instantiate.
	 */
	private Propagator() {
		super();
	}
}
