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



import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/** 
 * @deprecated using base Template framework TODO remove in C5
 */
public class MacroHelper {

	// contexts - preference data strings ***DO NOT TRANSLATE***
	public final static String TEXT_INSERTION = "<|t>";//$NON-NLS-1$
	public final static String CURSOR_POSITION = "<|c>";//$NON-NLS-1$
	public final static String ANY = "all"; //$NON-NLS-1$
	public final static String TAG = "tag"; //$NON-NLS-1$
	public final static String ATTRIBUTE = "attribute"; //$NON-NLS-1$
	public final static String ATTRIBUTEVALUE = "attribute_value"; //$NON-NLS-1$
	public final static String DISABLED = "disabled"; //$NON-NLS-1$
	public final static String MACRO = "macro"; //$NON-NLS-1$
	public final static String MACROS = "macros"; //$NON-NLS-1$
	public final static String CONTEXT = "context"; //$NON-NLS-1$
	public final static String NAME = "name"; //$NON-NLS-1$

	/**
	 * MacroHelper constructor comment.
	 */
	public MacroHelper() {
		super();
	}

	private String getCompletion(Node macro) {
		NodeList children = macro.getChildNodes();
		Node cdata = children.item(0);
		if (cdata != null && cdata.getNodeType() == Node.CDATA_SECTION_NODE) {
			String completion = cdata.getNodeValue();
			if (completion != null)
				return completion;
		}
		return "";//$NON-NLS-1$
	}

	public String getCompletionString(Node macro) {
		String rawCompletion = getCompletion(macro);
		String noCursor = StringUtils.replace(rawCompletion, CURSOR_POSITION, "");//$NON-NLS-1$
		return StringUtils.replace(noCursor, TEXT_INSERTION, "");//$NON-NLS-1$
	}

	public String getContext(Node macro) {
		String defaultContext = ANY;
		NamedNodeMap attrs = macro.getAttributes();
		if (attrs == null)
			return defaultContext;
		Node macroContext = attrs.getNamedItem(CONTEXT);//$NON-NLS-1$
		if (macroContext == null)
			return defaultContext;
		return macroContext.getNodeValue();
	}

	public int getCursorPosition(Node macro) {
		String rawCompletion = getCompletion(macro);
		int cursorPos = rawCompletion.indexOf(CURSOR_POSITION);
		int textPos = rawCompletion.indexOf(TEXT_INSERTION);
		int actualLength = getCompletionString(macro).length();
		if (textPos < 0)
			textPos = actualLength;
		if (cursorPos < 0)
			cursorPos = actualLength;

		if (cursorPos <= textPos)
			return cursorPos;
		if (cursorPos > textPos)
			return cursorPos - TEXT_INSERTION.length();
		return actualLength;
	}

	public String getIconLocation(Node macro) {
		String defaultPath = XMLEditorPluginImages.IMG_OBJ_TAG_MACRO;
		NamedNodeMap attrs = macro.getAttributes();
		if (attrs == null)
			return defaultPath;
		Node icon = attrs.getNamedItem("icon");//$NON-NLS-1$
		if (icon == null)
			return defaultPath;
		return icon.getNodeValue();
	}

	public String getName(Node macro) {
		String defaultName = "";//$NON-NLS-1$
		NamedNodeMap attrs = macro.getAttributes();
		if (attrs == null)
			return defaultName;
		Node name = attrs.getNamedItem("name");//$NON-NLS-1$
		if (name == null)
			return defaultName;
		return name.getNodeValue();
	}

	public int getTextInsertionPosition(Node macro) {
		String rawCompletion = getCompletion(macro);
		int cursorPos = rawCompletion.indexOf(CURSOR_POSITION);
		int textPos = rawCompletion.indexOf(TEXT_INSERTION);
		int actualLength = getCompletionString(macro).length();
		if (textPos < 0)
			textPos = actualLength;
		if (cursorPos < 0)
			cursorPos = actualLength;

		if (textPos <= cursorPos)
			return textPos;
		if (textPos > cursorPos)
			return textPos - CURSOR_POSITION.length();
		return getCompletionString(macro).length();
	}

	public boolean isContext(Node macro, String context) {
		return context.equals(getContext(macro));
	}

	public boolean isEnabled(Node macro) {
		NamedNodeMap attrs = macro.getAttributes();
		if (attrs == null)
			return true;
		Node macroDisabled = attrs.getNamedItem(DISABLED);//$NON-NLS-1$
		if (macroDisabled == null)
			return true;
		return false;
	}
}
