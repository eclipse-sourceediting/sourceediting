/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;

/**
 * Sets default values for XML Core preferences
 */
public class XMLCorePreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(XMLCorePlugin.getDefault().getBundle().getSymbolicName());

		// formatting preferences
		node.putInt(XMLCorePreferenceNames.LINE_WIDTH, 72);
		node.putBoolean(XMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES, false);
		node.put(XMLCorePreferenceNames.INDENTATION_CHAR, XMLCorePreferenceNames.TAB);
		node.putInt(XMLCorePreferenceNames.INDENTATION_SIZE, 1);
		node.putBoolean(XMLCorePreferenceNames.SPLIT_MULTI_ATTRS, false);
		node.putBoolean(XMLCorePreferenceNames.ALIGN_END_BRACKET, false);
		node.putBoolean(XMLCorePreferenceNames.PRESERVE_CDATACONTENT, false);
		node.putBoolean(XMLCorePreferenceNames.SPACE_BEFORE_EMPTY_CLOSE_TAG, true);
		node.putBoolean(XMLCorePreferenceNames.FORMAT_COMMENT_TEXT, true);
		node.putBoolean(XMLCorePreferenceNames.FORMAT_COMMENT_JOIN_LINES, true);
		// cleanup preferences
		node.putBoolean(XMLCorePreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS, true);
		node.putBoolean(XMLCorePreferenceNames.INSERT_REQUIRED_ATTRS, true);
		node.putBoolean(XMLCorePreferenceNames.INSERT_MISSING_TAGS, true);
		node.putBoolean(XMLCorePreferenceNames.QUOTE_ATTR_VALUES, true);
		node.putBoolean(XMLCorePreferenceNames.FORMAT_SOURCE, true);
		node.putBoolean(XMLCorePreferenceNames.CONVERT_EOL_CODES, false);
		node.putBoolean(XMLCorePreferenceNames.FIX_XML_DECLARATION, true);

		node.put(CommonEncodingPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$
		node.put(CommonEncodingPreferenceNames.OUTPUT_CODESET, "UTF-8");//$NON-NLS-1$
		node.put(CommonEncodingPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$

		// this could be made smarter by actually looking up the content
		// type's valid extensions
		node.put(XMLCorePreferenceNames.DEFAULT_EXTENSION, "xml"); //$NON-NLS-1$

		node.putBoolean(XMLCorePreferenceNames.WARN_NO_GRAMMAR, true);
		// 1 = IMarker.SEVERITY_WARNING
		node.putInt(XMLCorePreferenceNames.INDICATE_NO_GRAMMAR, 1);
		node.putInt(XMLCorePreferenceNames.INDICATE_NO_DOCUMENT_ELEMENT, -1); // ignore
		node.putBoolean(XMLCorePreferenceNames.USE_XINCLUDE, false);
		node.putBoolean(XMLCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS, true);
		node.putBoolean(XMLCorePreferenceNames.CMDOCUMENT_GLOBAL_CACHE_ENABLED, true);
		
		node.putBoolean(XMLCorePreferenceNames.MARKUP_VALIDATION, false);
		node.putInt(XMLCorePreferenceNames.ATTRIBUTE_HAS_NO_VALUE, 2);
		node.putInt(XMLCorePreferenceNames.END_TAG_WITH_ATTRIBUTES, 2);
		node.putInt(XMLCorePreferenceNames.MISSING_START_TAG, 2);
		node.putInt(XMLCorePreferenceNames.MISSING_END_TAG, 2);
		node.putInt(XMLCorePreferenceNames.MISSING_TAG_NAME, 2);
		node.putInt(XMLCorePreferenceNames.MISSING_QUOTES, 2);
		node.putInt(XMLCorePreferenceNames.MISSING_CLOSING_BRACKET, 2);
		node.putInt(XMLCorePreferenceNames.MISSING_CLOSING_QUOTE, 2);
		node.putInt(XMLCorePreferenceNames.NAMESPACE_IN_PI_TARGET, 2);
		node.putInt(XMLCorePreferenceNames.WHITESPACE_AT_START, 2);
		node.putInt(XMLCorePreferenceNames.WHITESPACE_BEFORE_TAGNAME, 2);
	}
}
