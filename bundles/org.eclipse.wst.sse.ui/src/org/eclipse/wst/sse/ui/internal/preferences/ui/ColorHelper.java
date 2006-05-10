/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
package org.eclipse.wst.sse.ui.internal.preferences.ui;



import com.ibm.icu.util.StringTokenizer;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.wst.sse.ui.internal.Logger;


public class ColorHelper {
	public final static String BACKGROUND = "background";//$NON-NLS-1$
	public final static String BOLD = "bold";//$NON-NLS-1$
	public final static String FOREGROUND = "foreground";//$NON-NLS-1$
	public final static String NAME = "name";//$NON-NLS-1$
	private final static String STYLE_SEPARATOR = "|"; //$NON-NLS-1$ 

	/**
	 * Return an RGB String given the int r, g, b values
	 */
	public static String getColorString(int r, int g, int b) {
		return "#" + getHexString(r, 2) + getHexString(g, 2) + getHexString(b, 2);//$NON-NLS-1$
	}

	private static String getHexString(int value, int minWidth) {
		String hexString = Integer.toHexString(value);
		for (int i = hexString.length(); i < minWidth; i++) {
			hexString = "0" + hexString;//$NON-NLS-1$
		}
		return hexString;
	}

	/**
	 * Generates a preference string to be placed in preferences from the
	 * given String array.
	 * 
	 * @param stylePrefs
	 *            assumes not null and should be in the form of String[0] =
	 *            Foreground RGB String, String[1] = Background RGB String,
	 *            String[2] = Bold true/false
	 * 
	 * @return String in the form of Foreground RGB String | Background RGB
	 *         String | Bold true/false
	 */
	public static String packStylePreferences(String[] stylePrefs) {
		StringBuffer styleString = new StringBuffer();

		if (stylePrefs.length == 3) {
			for (int i = 0; i < 3; ++i) {
				String s = stylePrefs[i];
				styleString.append(s);

				// add in the separator (except on last iteration)
				if (i != 2) {
					styleString.append(" " + STYLE_SEPARATOR + " "); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}

		return styleString.toString();
	}

	/**
	 * @return org.eclipse.swt.graphics.RGB
	 * @param anRGBString
	 *            java.lang.String
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
			} catch (NumberFormatException nfExc) {
				Logger.logException("Could not load highlighting preference for color " + anRGBString, nfExc);//$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * @return java.lang.String
	 * @param anRGB
	 *            org.eclipse.swt.graphics.RGB
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

	/**
	 * Extracts the foreground (RGB String), background (RGB String), bold
	 * (boolean String) from the given preference string.
	 * 
	 * @param preference
	 *            should be in the form of Foreground RGB String | Background
	 *            RGB String | Bold true/false
	 * @return String[] where String[0] = Foreground RGB String, String[1] =
	 *         Background RGB String, String[2] = Bold true/false OR null if
	 *         ran into problems extracting
	 */
	public static String[] unpackStylePreferences(String preference) {
		String[] stylePrefs = null;
		if (preference != null) {
			StringTokenizer st = new StringTokenizer(preference, STYLE_SEPARATOR);
			if (st.countTokens() == 3) {
				String foreground = st.nextToken().trim();
				String background = st.nextToken().trim();
				String bold = st.nextToken().trim();

				stylePrefs = new String[3];
				stylePrefs[0] = foreground;
				stylePrefs[1] = background;
				stylePrefs[2] = bold;
			}
		}

		return stylePrefs;
	}
}
