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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** 
 * @deprecated using base Template framework TODO remove in C5
 */
public class XMLMacroManager extends PreferenceManager {

	private static XMLMacroManager instance = null;

	public Document createDefaultPreferences() {
		Element macros = null;

		Document document = super.createDefaultPreferences();
		if (document == null)
			return document;

		while (document.getChildNodes().getLength() > 0)
			document.removeChild(document.getLastChild());
		macros = document.createElement("macros"); //$NON-NLS-1$

		macros.appendChild(newMacro(document, "comment <!-- -->", null, "<!-- <|c> -->", MacroHelper.TAG)); //$NON-NLS-2$//$NON-NLS-1$
		document.appendChild(macros);
		return document;
	}

	public String getFilename() {
		if (fileName == null) {
			fileName = Platform.getPlugin(org.eclipse.wst.sse.core.IModelManagerPlugin.ID).getStateLocation().toString() + "/xmlmacros.xml"; //$NON-NLS-1$
		}
		return fileName;
	}

	/**
	 * The intended name for the root Element of the Document; what is also
	 * listed within the DOCTYPE declaration.
	 * @return String
	 */
	public String getRootElementName() {
		return "macros"; //$NON-NLS-1$
	}

	public static XMLMacroManager getXMLMacroManager() {
		if (instance == null)
			instance = new XMLMacroManager();
		return instance;
	}

	protected Element newMacro(Document doc, String name, String iconPath, String completion, String context) {
		Element macro = doc.createElement("macro"); //$NON-NLS-1$
		if (name != null)
			macro.setAttribute("name", name); //$NON-NLS-1$
		if (iconPath != null)
			macro.setAttribute("icon", iconPath); //$NON-NLS-1$
		if (context != null)
			macro.setAttribute("context", context); //$NON-NLS-1$
		CDATASection cdata = doc.createCDATASection(completion);
		//	macro.addTextElement(cdata);
		macro.appendChild(cdata);
		return macro;
	}
}
