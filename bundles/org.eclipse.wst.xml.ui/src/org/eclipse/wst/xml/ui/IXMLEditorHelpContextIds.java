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
package org.eclipse.wst.xml.ui;

import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;

/**
 * @deprecated use org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds instead TODO remove
 */
public interface IXMLEditorHelpContextIds extends IHelpContextIds {
	// org.eclipse.wst.xml.ui.
	public static final String XMLPREFIX = XMLEditorPlugin.ID + "."; //$NON-NLS-1$

	// XML Files Preference Page
	// Line delimiter group
	public static final String PREFFILE_EOL_CODE_HELPID = XMLPREFIX + "xmlp6000"; //$NON-NLS-1$
	// Encoding group 
	public static final String PREFFILE_ENCODING_HELPID = XMLPREFIX + "xmlp6100"; //$NON-NLS-1$
	
	// XML Source Preference Page
	// Formatting group - Split lines, Line width, Set one property per line, Split multiple attributes each on a new line, Indent using tabs, Clear all blank lines
	public static final String PREFSRC_FORMATTING_HELPID = XMLPREFIX + "xmlp1200"; //$NON-NLS-1$
	// Content assist group - Automatically make suggestions, Prompt when these characters are inserted
	public static final String PREFSRC_CONTENT_ASSIST_HELPID = XMLPREFIX + "xmlp1300"; //$NON-NLS-1$
	// Using Inferred Grammar
	public static final String PREF_INFERRED_GRAMMAR_HELPID = XMLPREFIX + "xmlp4400"; //$NON-NLS-1$
	
	// XML Cleanup dialog
	public static final String CLEANUP_XML_HELPID = XMLPREFIX + "xmlm1200"; //$NON-NLS-1$
}
