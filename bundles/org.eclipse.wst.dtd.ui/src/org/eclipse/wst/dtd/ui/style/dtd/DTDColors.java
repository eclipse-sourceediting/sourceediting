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
package org.eclipse.wst.dtd.ui.style.dtd;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * @deprecated use IStyleConstantsDTD and base ColorRegistry instead TODO remove in C5 or earlier
 */
public class DTDColors {
	public static final Color DTD_DEFAULT = ColorManager.getDefault().getColor(new RGB(0, 0, 0)); //black

	// tags are '<', '!', or '>'
	public static final Color DTD_TAG = ColorManager.getDefault().getColor(new RGB(63, 63, 191)); // blue  

	// tagnames are like ELEMENT, ATTLIST, etc.
	public static final Color DTD_TAGNAME = ColorManager.getDefault().getColor(new RGB(63, 63, 191)); // blue

	public static final Color DTD_COMMENT = ColorManager.getDefault().getColor(new RGB(127, 127, 127)); // grey

	// keywords are constants like IMPLIED or PCDATA
	public static final Color DTD_KEYWORD = ColorManager.getDefault().getColor(new RGB(128, 0, 0)); // dark red

	// strings are anything in quotes
	public static final Color DTD_STRING = ColorManager.getDefault().getColor(new RGB(63, 159, 95)); //green

	// Data are variables
	public static final Color DTD_DATA = ColorManager.getDefault().getColor(new RGB(191, 95, 95)); // light red

	// All the remaining symbols
	public static final Color DTD_SYMBOL = ColorManager.getDefault().getColor(new RGB(128, 0, 0)); // dark red
}
