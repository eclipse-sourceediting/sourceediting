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
package org.eclipse.wst.sse.ui.preferences.ui;



import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.ui.Logger;


public class ColorHelper {

	public final static String NAME = "name";//$NON-NLS-1$
	public final static String FOREGROUND = "foreground";//$NON-NLS-1$
	public final static String BACKGROUND = "background";//$NON-NLS-1$
	public final static String BOLD = "bold";//$NON-NLS-1$

	/**
	 * @return org.eclipse.swt.graphics.RGB
	 * @param anRGBString java.lang.String
	 */
	public static RGB toRGB(String anRGBString) {
		RGB result = null;
		if (anRGBString.length() > 6 && anRGBString.charAt(0) == '#') {
			int r = 0;
			int g = 0;
			int b = 0;
			try {
				r = Integer.valueOf(anRGBString.substring(1, 3), 16).intValue();
				g = Integer.valueOf(anRGBString.substring(3, 5), 16).intValue();
				b = Integer.valueOf(anRGBString.substring(5, 7), 16).intValue();
				result = new RGB(r, g, b);
			}
			catch (NumberFormatException nfExc) {
				Logger.logException("Could not load highlighting preference for color " + anRGBString, nfExc);//$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * @return java.lang.String
	 * @param anRGB org.eclipse.swt.graphics.RGB
	 */
	public static String toRGBString(RGB anRGB) {
		if (anRGB == null)
			return "#000000";//$NON-NLS-1$
		String red = Integer.toHexString(anRGB.red);
		while (red.length() < 2)
			red = "0" + red;//$NON-NLS-1$
		String green = Integer.toHexString(anRGB.green);
		while (green.length() < 2)
			green = "0" + green;//$NON-NLS-1$
		String blue = Integer.toHexString(anRGB.blue);
		while (blue.length() < 2)
			blue = "0" + blue;//$NON-NLS-1$
		return "#" + red + green + blue;//$NON-NLS-1$
	}
}
