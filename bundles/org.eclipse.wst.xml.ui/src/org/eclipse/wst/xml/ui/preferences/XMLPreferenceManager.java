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
package org.eclipse.wst.xml.ui.preferences;



import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @deprecated XML Macros used this class but macros have been replaced by base templates TODO remove in C5
 */
public class XMLPreferenceManager extends PreferenceManager {

	static private XMLPreferenceManager instance = null;
	// todo: we should expose all the default values.
	// we did the tabwidth, so structured text viewer can use
	// it incase it gets an impossible value, like -5, or 9 million.
	/** DEFAULT_TABWIDTH is the default spaces to use when displaying tab character */
	public final static int DEFAULT_TABWIDTH = 4;
	/** DEFAULT_SPACES is the default spaces put into source code when formatting with spaces */
	public final static int DEFAULT_SPACES = 4;

	public Document createDefaultPreferences() {

		if (org.eclipse.wst.sse.core.util.Debug.displayInfo)
			System.out.println(getClass().getName() + " creating default preferences");//$NON-NLS-1$
		Document document = super.createDefaultPreferences();
		if (document == null)
			return document;

		while (document.getChildNodes().getLength() > 0)
			document.removeChild(document.getLastChild());
		Element settings = document.createElement(getRootElementName());
		document.appendChild(settings);

		// workaround for XML4J implementation bug
		Element spacer = document.createElement("spaceholder");//$NON-NLS-1$
		settings.appendChild(spacer);

		return document;
	}

	public String getFilename() {
		if (fileName == null) {
			fileName = Platform.getPlugin(org.eclipse.wst.sse.core.IModelManagerPlugin.ID).getStateLocation().toString() + "/xmlprefs.xml";//$NON-NLS-1$
			//		fileName = "workbench/xmlprefs.xml";
		}
		return fileName;
	}

	public synchronized static XMLPreferenceManager getInstance() {
		if (instance == null)
			instance = new XMLPreferenceManager();
		return instance;
	}
}
