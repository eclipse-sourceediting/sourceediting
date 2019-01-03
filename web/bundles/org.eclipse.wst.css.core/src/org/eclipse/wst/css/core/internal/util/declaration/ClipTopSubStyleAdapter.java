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
package org.eclipse.wst.css.core.internal.util.declaration;



import org.eclipse.wst.css.core.internal.contentmodel.PropCMSubProperty;

/**
 * For top value of 'clip' property's rect() function
 */
public class ClipTopSubStyleAdapter extends ClipSubStyleAdapter {

	/**
	 * 
	 */
	public ClipTopSubStyleAdapter() {
		super();
	}

	/**
	 * 
	 */
	String get(org.w3c.dom.css.Rect rect) {
		return rect.getTop().getCssText();
	}

	/**
	 * 
	 */
	int index() {
		return 0;
	}

	/**
	 * 
	 */
	public void set(ICSS2Properties properties, String value) throws org.w3c.dom.DOMException {
		String right = properties.get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_RIGHT));
		String bottom = properties.get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_BOTTOM));
		String left = properties.get(PropCMSubProperty.getInstanceOf(PropCMSubProperty.PSUB_CLIP_LEFT));

		set(properties, value, right, bottom, left, value == null || value.length() == 0);
	}
}
