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
package org.eclipse.wst.dtd.ui.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DTDPreferenceManager extends PreferenceManager {

	static private DTDPreferenceManager instance = null;

	public Document createDefaultPreferences() {
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
			fileName = Platform.getPlugin(org.eclipse.wst.sse.core.IModelManagerPlugin.ID).getStateLocation().toString() + "/dtdprefs.xml";//$NON-NLS-1$
		}
		return fileName;
	}

	public synchronized static DTDPreferenceManager getInstance() {
		if (instance == null)
			instance = new DTDPreferenceManager();
		return instance;
	}
}
