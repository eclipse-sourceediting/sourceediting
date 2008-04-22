/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
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


public abstract class AbstractPropagatingValidator extends ValidationComponent {

	/**
	 * Constructor for AbstractPropagatingValidator.
	 */
	public AbstractPropagatingValidator() {
		super();
	}

	protected abstract ValidationComponent getPropagatee();

	protected abstract ValidationAdapter getValidator();


	public void validate(IndexedRegion node) {
		if (node == null)
			return;
		getValidator().validate(node);

		propagateToChildElements(getPropagatee(), (Node) node);
	}

	private void propagateToChildElements(ValidationComponent validator, Node parent) {
		if (parent == null)
			return;
		Class clazz = validator.getClass();

		Node child = parent.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				INodeNotifier notifier = (INodeNotifier) child;
				ValidationAdapter va = (ValidationAdapter) notifier.getExistingAdapter(clazz);
				if (va == null) {
					notifier.addAdapter(validator);
					va = validator;
				}
				// bug 143213 - Can't batch validate open HTML files when
				// as-you-type validation is enabled
				va.setReporter(validator.getReporter());
				va.validate((IndexedRegion) child);
			}
			child = child.getNextSibling();
		}
	}

}
