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



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.util.EditorUtility;


/**
 * TODO remove in C5
 * @deprecated Text attributes can be created on the fly and the base 
 * ColorRegistry can be used to access cached colors.
 */
public class StructuredTextColors {
	private static ArrayList instances = null;

	public TextAttribute RTF_HTML_COMMENT_BORDER = null;
	public TextAttribute RTF_HTML_COMMENT_TEXT = null;
	public TextAttribute RTF_JSP_JAVA_DIRECTIVE = null;
	public TextAttribute RTF_TAG_BORDER = null;
	public TextAttribute RTF_TAG_NAME = null;
	public TextAttribute RTF_TAG_ATTRIBUTE_NAME = null;
	public TextAttribute RTF_TAG_ATTRIBUTE_VALUE = null;
	public TextAttribute RTF_TAG_ATTRIBUTE_EQUALS = null;
	public TextAttribute RTF_HTML_SCRIPTING_CONTENT = null;
	public TextAttribute RTF_JSP_SCRIPTING_CONTENT = null;
	public TextAttribute SCRIPT_AREA_BORDER = null;
	public TextAttribute CDATA_MARKER_BORDER = null;
	public TextAttribute CDATA_TEXT = null;
	public TextAttribute RTF_JSP_GENERIC_BORDER = null;
	public TextAttribute DOCTYPE_BORDER = null;
	public TextAttribute DOCTYPE_NAME = null;
	public TextAttribute DOCTYPE_EXTERNAL_ID = null;
	public TextAttribute DOCTYPE_EXTERNAL_ID_PUBREF = null;
	public TextAttribute DOCTYPE_EXTERNAL_ID_SYSREF = null;
	public TextAttribute PI_CONTENT = null;
	public TextAttribute PI_BORDER = null;
	public TextAttribute DECL_BORDER = null;
	public TextAttribute XML_CONTENT = null;

	/**
	 */
	public StructuredTextColors() {
		super();
		init();

	}

	/**
	 * @deprecated use EditorUtility.getColor instead.  Note that method is only temporary
	 * till the base has better support for the base themes
	 */
	public Color getColor(RGB rgb) {
		return EditorUtility.getColor(rgb);
	}

	/**
	 * @deprecated - since no longer a singleton, will 
	 * eventually do away with this method. And, instead of
	 * this class, can use SharedStructuredTextColors instead, for 
	 * pure Color caching. This class can still be used for text attributes.
	 */
	public synchronized static StructuredTextColors getInstance() {
		// returning a new instance every time
		//    	StructuredTextColors colors =  new StructuredTextColors();
		//    	// save for disposal later, just in case client doesn't do it
		//    	if(instances == null)
		//    		instances = new ArrayList(6);
		//    	instances.add(colors);
		return new StructuredTextColors();
	}

	/**
	 * This method added specifically for "shutdown" code, where the 
	 * class needs to dispoose of its colors, but if its never been 
	 * initialized (that is, never used) then we don't want to create it 
	 * just for the sake of disposing it.
	 */
	public static List getInstancesIfExists() {
		return instances;
	}

	protected void init() {
		// use null for background to maintain current system backgound.
		Color defaultForeground = null;
		Color defaultBackground = null;

		// DEEMPHASIZE = new TextAttribute(getColor(DARK_GREY), defaultBackground, SWT.NORMAL);

		RTF_HTML_COMMENT_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		RTF_HTML_COMMENT_TEXT = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		RTF_JSP_JAVA_DIRECTIVE = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		RTF_TAG_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		RTF_TAG_NAME = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		RTF_TAG_ATTRIBUTE_NAME = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		RTF_TAG_ATTRIBUTE_VALUE = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		RTF_TAG_ATTRIBUTE_EQUALS = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		RTF_HTML_SCRIPTING_CONTENT = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		RTF_JSP_SCRIPTING_CONTENT = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		SCRIPT_AREA_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		CDATA_MARKER_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		CDATA_TEXT = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		RTF_JSP_GENERIC_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		DOCTYPE_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		DOCTYPE_NAME = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		DOCTYPE_EXTERNAL_ID = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		DOCTYPE_EXTERNAL_ID_PUBREF = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		DOCTYPE_EXTERNAL_ID_SYSREF = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);

		PI_CONTENT = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		PI_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		DECL_BORDER = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
		XML_CONTENT = new TextAttribute(defaultForeground, defaultBackground, SWT.NORMAL);
	}

	/**
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {

		// simple check on "system colors"
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (display == null) {
			throw new IllegalStateException("must be ran in workbench"); //$NON-NLS-1$
		}
		System.out.println("COLOR_WHITE:        " + display.getSystemColor(SWT.COLOR_WHITE)); //$NON-NLS-1$
		System.out.println("COLOR_BLACK:        " + display.getSystemColor(SWT.COLOR_BLACK)); //$NON-NLS-1$
		System.out.println("COLOR_RED:          " + display.getSystemColor(SWT.COLOR_RED)); //$NON-NLS-1$
		System.out.println("COLOR_DARK_RED:     " + display.getSystemColor(SWT.COLOR_DARK_RED)); //$NON-NLS-1$
		System.out.println("COLOR_GREEN:        " + display.getSystemColor(SWT.COLOR_GREEN)); //$NON-NLS-1$
		System.out.println("COLOR_DARK_GREEN:   " + display.getSystemColor(SWT.COLOR_DARK_GREEN)); //$NON-NLS-1$
		System.out.println("COLOR_YELLOW:       " + display.getSystemColor(SWT.COLOR_YELLOW)); //$NON-NLS-1$
		System.out.println("COLOR_DARK_YELLOW:  " + display.getSystemColor(SWT.COLOR_DARK_YELLOW)); //$NON-NLS-1$
		System.out.println("COLOR_BLUE:         " + display.getSystemColor(SWT.COLOR_BLUE)); //$NON-NLS-1$
		System.out.println("COLOR_DARK_BLUE:    " + display.getSystemColor(SWT.COLOR_DARK_BLUE)); //$NON-NLS-1$
		System.out.println("COLOR_MAGENTA:      " + display.getSystemColor(SWT.COLOR_MAGENTA)); //$NON-NLS-1$
		System.out.println("COLOR_DARK_MAGENTA: " + display.getSystemColor(SWT.COLOR_DARK_MAGENTA)); //$NON-NLS-1$
		System.out.println("COLOR_CYAN:         " + display.getSystemColor(SWT.COLOR_CYAN)); //$NON-NLS-1$
		System.out.println("COLOR_DARK_CYAN:    " + display.getSystemColor(SWT.COLOR_DARK_CYAN)); //$NON-NLS-1$
		System.out.println("COLOR_GRAY:         " + display.getSystemColor(SWT.COLOR_GRAY)); //$NON-NLS-1$
		System.out.println("COLOR_DARK_GRAY:    " + display.getSystemColor(SWT.COLOR_DARK_GRAY)); //$NON-NLS-1$
		/*
		 public static final int COLOR_WHITE        = 1;
		 public static final int COLOR_BLACK        = 2;
		 public static final int COLOR_RED          = 3;
		 public static final int COLOR_DARK_RED     = 4;
		 public static final int COLOR_GREEN        = 5;
		 public static final int COLOR_DARK_GREEN   = 6;
		 public static final int COLOR_YELLOW       = 7;
		 public static final int COLOR_DARK_YELLOW  = 8;
		 public static final int COLOR_BLUE         = 9;
		 public static final int COLOR_DARK_BLUE    = 10;
		 public static final int COLOR_MAGENTA      = 11;
		 public static final int COLOR_DARK_MAGENTA = 12;
		 public static final int COLOR_CYAN         = 13;
		 public static final int COLOR_DARK_CYAN    = 14;
		 public static final int COLOR_GRAY         = 15;
		 public static final int COLOR_DARK_GRAY    = 16;
		 */
	}

	/**
	 * This method typically does not need to be called by clients. 
	 * It is intended to be called during shutdown, to be sure OS handles
	 * are released.
	 */
	public void dispose() {
	}
}
