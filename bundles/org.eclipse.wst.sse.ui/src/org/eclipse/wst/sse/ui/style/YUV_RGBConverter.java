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
package org.eclipse.wst.sse.ui.style;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * A utility class to do various color manipulations
 */
public class YUV_RGBConverter {
	/**
	 * This class "holds" the YUV values corresponding to RGB color
	 */
	public class YUV {
		/**
		 * normalize to "average" gamma 2.2222 or 1/0.45
		 */
		double gammaNormalized(double colorComponent) {
			if (colorComponent < 0.018) {
				return colorComponent * 0.45;
			}
			else {
				return 1.099 * Math.pow(colorComponent, 0.45) - 0.099;
			}
		}

		double inverseGammaNormalized(double colorComponent) {
			if (colorComponent < 0.018) {
				return colorComponent * .222;
			}
			else {
				return Math.pow(((.9099 * colorComponent + 0.09)), 2.22);
			}
		}

		class NormalizedRGB {
			private final double maxRGB = 256.0;
			double red;
			double green;
			double blue;

			public NormalizedRGB(RGB rgb) {
				// first normalize to between 0 - 1
				red = rgb.red / maxRGB;
				green = rgb.green / maxRGB;
				blue = rgb.blue / maxRGB;

				red = gammaNormalized(red);
				green = gammaNormalized(green);
				blue = gammaNormalized(blue);

			}
		}

		private RGB originalRGB;
		private NormalizedRGB normalizedRGB;
		private double y = -1;
		private double u = -1;
		private double v = -1;

		private YUV() {
			super();
		}

		public YUV(RGB rgb) {
			this();
			originalRGB = rgb;
			normalizedRGB = new NormalizedRGB(rgb);
			// force calculations
			getY();
			getV();
			getU();
		}

		public YUV(double y, double u, double v) {
			this();
			this.y = y;
			this.u = u;
			this.v = v;
		}

		public double getU() {
			if (u == -1) {
				u = 0.4949 * (normalizedRGB.blue - getY());
			}
			return u;

		}

		public double getV() {
			if (v == -1) {
				v = 0.877 * (normalizedRGB.red - getY());
			}
			return v;
		}

		public double getY() {
			if (y == -1) {
				y = 0.299 * normalizedRGB.red + 0.587 * normalizedRGB.green + 0.114 * normalizedRGB.blue;
			}
			return y;
		}

		/**
		 * @return RGB based on original RGB and current YUV values;
		 */
		public RGB getRGB() {
			RGB result = null;
			double r = getY() + 1.14 * getV();
			double g = getY() - 0.395 * getU() - 0.58 * getV();
			double b = getY() + 2.032 * getU();

			int red = (int) (inverseGammaNormalized(r) * 256);
			int green = (int) (inverseGammaNormalized(g) * 256);
			int blue = (int) (inverseGammaNormalized(b) * 256);
			if (red < 0)
				red = 0;
			else if (red > 255)
				red = 255;
			if (green < 0)
				green = 0;
			else if (green > 255)
				green = 255;
			if (blue < 0)
				blue = 0;
			else if (blue > 255)
				blue = 255;

			result = new RGB(red, green, blue);
			return result;
		}

	}

	public YUV_RGBConverter() {
		super();
	}

	public RGB transformRGB(RGB originalRGB, double scaleFactor, double target) {
		RGB transformedRGB = null;
		//CCIR601 yuv = new CCIR601(originalRGB);
		YUV yuv = new YUV(originalRGB);
		double y = yuv.getY();
		// zero is black, one is white
		if (y < target) {
			// is "dark" make lighter
			y = y + ((target - y) * scaleFactor);
		}
		else {
			// is "light" make darker
			y = y - ((y - target) * scaleFactor);
		}
		//yuv.setY(y);
		YUV newYUV = new YUV(y, yuv.getU(), yuv.getV());
		//CCIR601 newYUV = new CCIR601(y, yuv.getCb601(), yuv.getCr601());
		transformedRGB = newYUV.getRGB();
		return transformedRGB;
	}

	public RGB transformRGBToGrey(RGB originalRGB, double scaleFactor, double target) {
		RGB transformedRGB = null;
		// we left the "full" API method signature, but this 
		// version does not take into account originalRGB, though
		// it might someday. 
		// for now, we'll simply make the new RGB grey, either a little 
		// lighter, or a little darker than background.
		double y = 0;
		double mid = 0.5;
		// zero is black, one is white
		if (target < mid) {
			// is "dark" make lighter
			y = target + scaleFactor;
		}
		else {
			// is "light" make darker
			y = target - scaleFactor;
		}
		int c = (int) Math.round(y * 255);
		// just to gaurd against mis-use, or scale's values greater
		// than mid point (and possibly rounding error)
		if (c > 255)
			c = 255;
		if (c < 0)
			c = 0;
		transformedRGB = new RGB(c, c, c);
		return transformedRGB;
	}

	public double calculateYComponent(Color targetColor) {
		return new YUV(targetColor.getRGB()).getY();
	}
}
