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
package org.eclipse.wst.sse.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * TODO remove in C5 or earlier
 * @deprecated use the base SharedTextColors or 
 * PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry()
 * instead (removing in C5 or earlier)
 */
public class StructuredSharedTextColors implements ISharedTextColors {
	private Map fDisplayTable;

	public StructuredSharedTextColors() {
		super();
	}

	public Color getColor(RGB rgb) {
		if (rgb == null)
			return null;

		if (fDisplayTable == null)
			fDisplayTable = new HashMap(2);

		Display display = PlatformUI.getWorkbench().getDisplay();

		Map colorTable = (Map) fDisplayTable.get(display);
		if (colorTable == null) {
			colorTable = new HashMap(10);
			fDisplayTable.put(display, colorTable);
		}

		Color color = (Color) colorTable.get(rgb);
		if (color == null) {
			color = new Color(display, rgb);
			colorTable.put(rgb, color);
		}

		return color;
	}

	public void dispose() {
		if (fDisplayTable != null) {
			Iterator j = fDisplayTable.values().iterator();
			while (j.hasNext()) {
				Iterator i = ((Map) j.next()).values().iterator();
				while (i.hasNext()) {
					((Color) i.next()).dispose();
					// technically would probably not need to remove, 
					// but at least makes inspection/probing easier
					i.remove();
				}
			}
		}
	}

}
