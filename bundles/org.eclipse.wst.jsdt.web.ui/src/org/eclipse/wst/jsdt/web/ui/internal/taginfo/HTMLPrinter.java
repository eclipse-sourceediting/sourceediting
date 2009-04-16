/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.internal.taginfo;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;

/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
class HTMLPrinter {
	static RGB BG_COLOR_RGB = null;
	static {
		final Display display = Display.getDefault();
		if (display != null && !display.isDisposed()) {
			try {
				display.asyncExec(new Runnable() {
					/*
					 * @see java.lang.Runnable#run()
					 */
					public void run() {
						HTMLPrinter.BG_COLOR_RGB = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND).getRGB();
					}
				});
			} catch (SWTError err) {
				// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=45294
				if (err.code != SWT.ERROR_DEVICE_DISPOSED) {
					throw err;
				}
			}
		}
	}
	
	public static void addBullet(StringBuffer buffer, String bullet) {
		if (bullet != null) {
			buffer.append("<li>"); //$NON-NLS-1$
			buffer.append(bullet);
			buffer.append("</li>"); //$NON-NLS-1$
		}
	}
	
	public static void addPageEpilog(StringBuffer buffer) {
		buffer.append("</font></body></html>"); //$NON-NLS-1$
	}
	
	public static void addPageProlog(StringBuffer buffer) {
		HTMLPrinter.insertPageProlog(buffer, buffer.length());
	}
	
	public static void addParagraph(StringBuffer buffer, Reader paragraphReader) {
		if (paragraphReader != null) {
			HTMLPrinter.addParagraph(buffer, HTMLPrinter.read(paragraphReader));
		}
	}
	
	public static void addParagraph(StringBuffer buffer, String paragraph) {
		if (paragraph != null) {
			buffer.append("<p>"); //$NON-NLS-1$
			buffer.append(paragraph);
		}
	}
	
	public static void addSmallHeader(StringBuffer buffer, String header) {
		if (header != null) {
			buffer.append("<h5>"); //$NON-NLS-1$
			buffer.append(header);
			buffer.append("</h5>"); //$NON-NLS-1$
		}
	}
	
	private static void appendColor(StringBuffer buffer, RGB rgb) {
		buffer.append('#');
		buffer.append(Integer.toHexString(rgb.red));
		buffer.append(Integer.toHexString(rgb.green));
		buffer.append(Integer.toHexString(rgb.blue));
	}
	
	private static void appendStyleSheetLink(StringBuffer buffer, URL styleSheetURL) {
		if (styleSheetURL == null) {
			return;
		}
		buffer.append("<head>"); //$NON-NLS-1$
		buffer.append("<LINK REL=\"stylesheet\" HREF= \""); //$NON-NLS-1$
		buffer.append(styleSheetURL);
		buffer.append("\" CHARSET=\"ISO-8859-1\" TYPE=\"text/css\">"); //$NON-NLS-1$
		buffer.append("</head>"); //$NON-NLS-1$
	}
	
	public static String convertToHTMLContent(String content) {
		content = HTMLPrinter.replace(content, '&', "&amp;"); //$NON-NLS-1$
		content = HTMLPrinter.replace(content, '"', "&quot;"); //$NON-NLS-1$
		content = HTMLPrinter.replace(content, '<', "&lt;"); //$NON-NLS-1$
		return HTMLPrinter.replace(content, '>', "&gt;"); //$NON-NLS-1$
	}
	
	public static void endBulletList(StringBuffer buffer) {
		buffer.append("</ul>"); //$NON-NLS-1$
	}
	
	private static RGB getBgColor() {
		if (HTMLPrinter.BG_COLOR_RGB != null) {
			return HTMLPrinter.BG_COLOR_RGB;
		}
		// RGB value of info bg color on WindowsXP
		return new RGB(255, 255, 225);
	}
	
	public static void insertPageProlog(StringBuffer buffer, int position) {
		HTMLPrinter.insertPageProlog(buffer, position, HTMLPrinter.getBgColor());
	}
	
	public static void insertPageProlog(StringBuffer buffer, int position, RGB bgRGB) {
		if (bgRGB == null) {
			HTMLPrinter.insertPageProlog(buffer, position);
		} else {
			StringBuffer pageProlog = new StringBuffer(60);
			pageProlog.append("<html><body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			HTMLPrinter.appendColor(pageProlog, bgRGB);
			pageProlog.append("\"><font size=-1>"); //$NON-NLS-1$
			buffer.insert(position, pageProlog.toString());
		}
	}
	
	public static void insertPageProlog(StringBuffer buffer, int position, RGB bgRGB, URL styleSheetURL) {
		if (bgRGB == null) {
			HTMLPrinter.insertPageProlog(buffer, position, styleSheetURL);
		} else {
			StringBuffer pageProlog = new StringBuffer(300);
			pageProlog.append("<html>"); //$NON-NLS-1$
			HTMLPrinter.appendStyleSheetLink(pageProlog, styleSheetURL);
			pageProlog.append("<body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			HTMLPrinter.appendColor(pageProlog, bgRGB);
			pageProlog.append("\"><font size=-1>"); //$NON-NLS-1$
			buffer.insert(position, pageProlog.toString());
		}
	}
	
	public static void insertPageProlog(StringBuffer buffer, int position, URL styleSheetURL) {
		HTMLPrinter.insertPageProlog(buffer, position, HTMLPrinter.getBgColor(), styleSheetURL);
	}
	
	static String read(Reader rd) {
		StringBuffer buffer = new StringBuffer();
		char[] readBuffer = new char[2048];
		try {
			int n = rd.read(readBuffer);
			while (n > 0) {
				buffer.append(readBuffer, 0, n);
				n = rd.read(readBuffer);
			}
			return buffer.toString();
		} catch (IOException x) {
			// never expected
			Logger.log(Logger.WARNING_DEBUG, x.getMessage(), x);
		}
		return null;
	}
	
	private static String replace(String text, char c, String s) {
		int previous = 0;
		int current = text.indexOf(c, previous);
		if (current == -1) {
			return text;
		}
		StringBuffer buffer = new StringBuffer();
		while (current > -1) {
			buffer.append(text.substring(previous, current));
			buffer.append(s);
			previous = current + 1;
			current = text.indexOf(c, previous);
		}
		buffer.append(text.substring(previous));
		return buffer.toString();
	}
	
	public static void startBulletList(StringBuffer buffer) {
		buffer.append("<ul>"); //$NON-NLS-1$
	}
	
	private HTMLPrinter() {
	// nothing
	}
}
