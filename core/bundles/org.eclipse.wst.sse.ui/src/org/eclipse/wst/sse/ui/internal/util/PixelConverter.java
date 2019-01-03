/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.util;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Control;


/**
 * @deprecated - use {@link org.eclipse.jface.layout.PixelConverter}
 */
public class PixelConverter extends org.eclipse.jface.layout.PixelConverter {

	public PixelConverter(Control control) {
		super(control);
	}

	public PixelConverter(Font font) {
		super(font);
	}	
}
