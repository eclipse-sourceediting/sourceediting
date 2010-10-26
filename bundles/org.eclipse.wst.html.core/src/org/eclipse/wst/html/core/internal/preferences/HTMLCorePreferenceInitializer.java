/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.html.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.html.core.internal.HTMLCorePlugin;
import org.eclipse.wst.sse.core.internal.encoding.CommonCharsetNames;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;

/**
 * Sets default values for HTML Core preferences
 */
public class HTMLCorePreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(HTMLCorePlugin.getDefault().getBundle().getSymbolicName());

		// formatting preferences
		node.putInt(HTMLCorePreferenceNames.LINE_WIDTH, 72);
		node.putBoolean(HTMLCorePreferenceNames.CLEAR_ALL_BLANK_LINES, false);
		node.put(HTMLCorePreferenceNames.INDENTATION_CHAR, HTMLCorePreferenceNames.TAB);
		node.putInt(HTMLCorePreferenceNames.INDENTATION_SIZE, 1);
		node.putBoolean(HTMLCorePreferenceNames.SPLIT_MULTI_ATTRS, false);
		node.putBoolean(HTMLCorePreferenceNames.ALIGN_END_BRACKET, false);

		// cleanup preferences
		node.putInt(HTMLCorePreferenceNames.CLEANUP_TAG_NAME_CASE, HTMLCorePreferenceNames.ASIS);
		node.putInt(HTMLCorePreferenceNames.CLEANUP_ATTR_NAME_CASE, HTMLCorePreferenceNames.ASIS);
		// node.putBoolean(HTMLCorePreferenceNames.COMPRESS_EMPTY_ELEMENT_TAGS,
		// true);
		node.putBoolean(HTMLCorePreferenceNames.INSERT_REQUIRED_ATTRS, true);
		node.putBoolean(HTMLCorePreferenceNames.INSERT_MISSING_TAGS, true);
		node.putBoolean(HTMLCorePreferenceNames.QUOTE_ATTR_VALUES, true);
		node.putBoolean(HTMLCorePreferenceNames.FORMAT_SOURCE, true);
		node.putBoolean(HTMLCorePreferenceNames.CONVERT_EOL_CODES, false);

		// code generation preferences
		node.put(CommonEncodingPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$
		String defaultEnc = "UTF-8";//$NON-NLS-1$
		String systemEnc = System.getProperty("file.encoding"); //$NON-NLS-1$
		if (systemEnc != null) {
			defaultEnc = CommonCharsetNames.getPreferredDefaultIanaName(systemEnc, "UTF-8");//$NON-NLS-1$
		}
		node.put(CommonEncodingPreferenceNames.OUTPUT_CODESET, defaultEnc);
		node.put(CommonEncodingPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$

		node.putInt(HTMLCorePreferenceNames.TAG_NAME_CASE, HTMLCorePreferenceNames.LOWER);
		node.putInt(HTMLCorePreferenceNames.ATTR_NAME_CASE, HTMLCorePreferenceNames.LOWER);

		// this could be made smarter by actually looking up the content
		// type's valid extensions
		node.put(HTMLCorePreferenceNames.DEFAULT_EXTENSION, "html"); //$NON-NLS-1$

		node.put(HTMLCorePreferenceNames.INLINE_ELEMENTS, "a,abbr,acronym,b,basefont,big,br,cite,em,font,i,img,input,label,li,q,s,select,small,span,strike,strong,sub,sup,td,th,title,u");
		
		initializeValidationPreferences(node);
	}
	
	/**
	 * Initializes the default validation preferences
	 * @param node the Eclipse preference node
	 */
	private void initializeValidationPreferences(IEclipsePreferences node) {
		// Attributes
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_UNDEFINED_NAME, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_UNDEFINED_VALUE, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_NAME_MISMATCH, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_INVALID_NAME, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_INVALID_VALUE, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_DUPLICATE, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_VALUE_MISMATCH, ValidationMessage.ERROR);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_VALUE_UNCLOSED, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_VALUE_RESOURCE_NOT_FOUND, ValidationMessage.IGNORE);
		node.putInt(HTMLCorePreferenceNames.ATTRIBUTE_OBSOLETE_NAME, ValidationMessage.WARNING);
		
		
		// Elements
		node.putInt(HTMLCorePreferenceNames.ELEM_UNKNOWN_NAME, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_INVALID_NAME, ValidationMessage.ERROR);
		node.putInt(HTMLCorePreferenceNames.ELEM_START_INVALID_CASE, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_END_INVALID_CASE, ValidationMessage.ERROR);
		node.putInt(HTMLCorePreferenceNames.ELEM_MISSING_START, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_MISSING_END, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_UNNECESSARY_END, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_INVALID_DIRECTIVE, ValidationMessage.ERROR);
		node.putInt(HTMLCorePreferenceNames.ELEM_INVALID_CONTENT, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_DUPLICATE, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_COEXISTENCE, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_UNCLOSED_START_TAG, ValidationMessage.ERROR);
		node.putInt(HTMLCorePreferenceNames.ELEM_UNCLOSED_END_TAG, ValidationMessage.ERROR);
		node.putInt(HTMLCorePreferenceNames.ELEM_INVALID_EMPTY_TAG, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.ELEM_OBSOLETE_NAME, ValidationMessage.WARNING);
		
		
		// DOCTYPE
		node.putInt(HTMLCorePreferenceNames.DOC_DUPLICATE, ValidationMessage.ERROR);
		node.putInt(HTMLCorePreferenceNames.DOC_INVALID_CONTENT, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.DOC_DOCTYPE_UNCLOSED, ValidationMessage.ERROR);
		
		// Text
		node.putInt(HTMLCorePreferenceNames.TEXT_INVALID_CONTENT, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.TEXT_INVALID_CHAR, ValidationMessage.WARNING);
		
		// Comment
		node.putInt(HTMLCorePreferenceNames.COMMENT_INVALID_CONTENT, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.COMMENT_UNCLOSED, ValidationMessage.ERROR);
		
		// CDATA
		node.putInt(HTMLCorePreferenceNames.CDATA_INVALID_CONTENT, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.CDATA_UNCLOSED, ValidationMessage.ERROR);
		
		// Processing Instructions
		node.putInt(HTMLCorePreferenceNames.PI_INVALID_CONTENT, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.PI_UNCLOSED, ValidationMessage.ERROR);
		
		// Entity Reference
		node.putInt(HTMLCorePreferenceNames.REF_UNDEFINED, ValidationMessage.WARNING);
		node.putInt(HTMLCorePreferenceNames.REF_INVALID_CONTENT, ValidationMessage.WARNING);
	}
}
