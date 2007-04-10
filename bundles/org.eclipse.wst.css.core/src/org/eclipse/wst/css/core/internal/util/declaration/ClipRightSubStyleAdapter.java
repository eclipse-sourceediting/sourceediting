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
package org.eclipse.wst.css.core.internal.util.declaration;



import org.eclipse.wst.css.core.internal.contentmodel.PropCMSubProperty;

/**
 * For right value of 'clip' property's rect() function
 */
public class ClipRightSubStyleAdapter extends ClipSubStyleAdapter {

	/**
	 * 
	 */
	public ClipRightSubStyleAdapter() {
		super();
	}

	/**
	 * 
	 */
	String get(org.w3c.dom.css.Rect rect) {
		return rect.getRight().getCssText();
	}

	/**
	 * 
	 */
	int index() {
		return 1;
	}

	/**
	 * 
	 */
	public void set(ICSS2Properties properties, String value) throws org.w3c.dom.DOMException {
		String top = properties.get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_TOP));
		String bottom = properties.get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_BOTTOM));
		String left = properties.get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_LEFT));

		set(properties, top, value, bottom, left, value == null || value.length() == 0);
	}
}
