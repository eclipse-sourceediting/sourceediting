/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.wst.sse.core.internal.encoding.CommonCharsetNames;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.validate.ValidationMessage;

/**
 * Sets default values for JSP Core preferences
 */
public class JSPCorePreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(JSPCorePlugin.getDefault().getBundle().getSymbolicName());

		// compiler/validation preferences
		node.putBoolean(JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true);

		// code generation preferences
		node.put(CommonEncodingPreferenceNames.INPUT_CODESET, ""); //$NON-NLS-1$
		String defaultEnc = "ISO-8859-1";//$NON-NLS-1$
		String systemEnc = System.getProperty("file.encoding"); //$NON-NLS-1$
		if (systemEnc != null) {
			defaultEnc = CommonCharsetNames.getPreferredDefaultIanaName(systemEnc, "ISO-8859-1");//$NON-NLS-1$
		}
		node.put(CommonEncodingPreferenceNames.OUTPUT_CODESET, defaultEnc);
		node.put(CommonEncodingPreferenceNames.END_OF_LINE_CODE, ""); //$NON-NLS-1$

		// this could be made smarter by actually looking up the content
		// type's valid extensions
		node.put(JSPCorePreferenceNames.DEFAULT_EXTENSION, "jsp"); //$NON-NLS-1$

		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_DIFFERENT_URIS, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_SAME_URIS, ValidationMessage.IGNORE);
		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_MISSING_PREFIX, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_MISSING_URI_OR_TAGDIR, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_TAGLIB_UNRESOLVABLE_URI_OR_TAGDIR, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_PAGE_SUPERCLASS_NOT_FOUND, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_INCLUDE_NO_FILE_SPECIFIED, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_DIRECTIVE_INCLUDE_FILE_NOT_FOUND, ValidationMessage.ERROR);

		node.putInt(JSPCorePreferenceNames.VALIDATION_JAVA_LOCAL_VARIABLE_NEVER_USED, ValidationMessage.IGNORE);
		node.putInt(JSPCorePreferenceNames.VALIDATION_JAVA_ARGUMENT_IS_NEVER_USED, ValidationMessage.IGNORE);
		node.putInt(JSPCorePreferenceNames.VALIDATION_JAVA_NULL_LOCAL_VARIABLE_REFERENCE, ValidationMessage.IGNORE);
		node.putInt(JSPCorePreferenceNames.VALIDATION_JAVA_POTENTIAL_NULL_LOCAL_VARIABLE_REFERENCE, ValidationMessage.IGNORE);
		node.putInt(JSPCorePreferenceNames.VALIDATION_JAVA_UNUSED_IMPORT, ValidationMessage.IGNORE);

		node.putInt(JSPCorePreferenceNames.VALIDATION_EL_SYNTAX, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_EL_LEXER, ValidationMessage.IGNORE);
		node.putInt(JSPCorePreferenceNames.VALIDATION_EL_FUNCTION_UNDEFINED, ValidationMessage.ERROR);

		node.putInt(JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_MISSING_REQUIRED_ATTRIBUTE, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_UNKNOWN_ATTRIBUTE, ValidationMessage.WARNING);
		node.putInt(JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_NON_EMPTY_INLINE_TAG, ValidationMessage.WARNING);
		node.putInt(JSPCorePreferenceNames.VALIDATION_ACTIONS_SEVERITY_UNEXPECTED_RTEXPRVALUE, ValidationMessage.WARNING);

		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_VALIDATION_MESSAGE, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_NOT_FOUND, ValidationMessage.WARNING);
		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_NOT_INSTANTIATED, ValidationMessage.WARNING);
		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TEI_CLASS_RUNTIME_EXCEPTION, ValidationMessage.WARNING);
		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_TAG_HANDLER_CLASS_NOT_FOUND, ValidationMessage.WARNING);
		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_USEBEAN_INVALID_ID, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_USBEAN_MISSING_TYPE_INFO, ValidationMessage.ERROR);
		node.putInt(JSPCorePreferenceNames.VALIDATION_TRANSLATION_USEBEAN_AMBIGUOUS_TYPE_INFO, ValidationMessage.WARNING);
	}
}
