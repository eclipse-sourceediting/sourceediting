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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @deprecated Use base ColorRegistry instead TODO remove in C5 or earlier
 */
public class ColorManager {

	private static ColorManager fgColorManager;

	public static ColorManager getDefault() {
		if (fgColorManager == null) {
			fgColorManager = new ColorManager();
		}
		return fgColorManager;
	}

	protected Map fColorTable = new HashMap(10);

	private ColorManager() {
	}

	public void dispose() {
		Iterator e = fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}

	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(getDisplay(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

	private Display getDisplay() {

		return PlatformUI.getWorkbench().getDisplay();
	}

}
