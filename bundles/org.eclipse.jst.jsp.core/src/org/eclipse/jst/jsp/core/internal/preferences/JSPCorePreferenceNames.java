/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.preferences;


/**
 * Common preference keys used by JSP core
 */
public class JSPCorePreferenceNames {
	private JSPCorePreferenceNames() {
		// empty private constructor so users cannot instantiate class
	}

	/**
	 * The default extension to use when none is specified in the New JSP File
	 * Wizard.
	 * <p>
	 * Value is of type <code>String</code>.
	 * </p>
	 */
	public static final String DEFAULT_EXTENSION = "defaultExtension"; //$NON-NLS-1$

	/**
	 * Indicates if JSP fragments should be compiled/validated. JSP fragments
	 * will be validated when true.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String VALIDATE_FRAGMENTS = "validateFragments";//$NON-NLS-1$

	/**
	 * Indicates that JSP validation will use per-project preferences
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 */
	public static final String VALIDATION_USE_PROJECT_SETTINGS = "validation.use-project-settings";

	/**
	 * Validation preference keys
	 */
	public static final String VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_DIFFERENT_URIS="validation.directive-taglib-duplicate-prefixes-different-uris";//$NON-NLS-1$
	public static final String VALIDATION_DIRECTIVE_TAGLIB_DUPLICATE_PREFIXES_SAME_URIS="validation.directive-taglib-duplicate-prefixes-same-uris";//$NON-NLS-1$
	public static final String VALIDATION_DIRECTIVE_TAGLIB_MISSING_PREFIX="validation.directive-taglib-missing-prefix";//$NON-NLS-1$
	public static final String VALIDATION_DIRECTIVE_TAGLIB_MISSING_URI_OR_TAGDIR="validation.directive-taglib-missing-uri-or-tagdir";//$NON-NLS-1$
	public static final String VALIDATION_DIRECTIVE_TAGLIB_UNRESOLVABLE_URI_OR_TAGDIR="validation.directive-taglib-unresolvable-uri-or-tagdir";//$NON-NLS-1$
	public static final String VALIDATION_DIRECTIVE_PAGE_SUPERCLASS_NOT_FOUND="validation.directive-page-superclass-not-found";//$NON-NLS-1$
	public static final String VALIDATION_DIRECTIVE_INCLUDE_NO_FILE_SPECIFIED="validation.directive-include-fragment-file-not-specified";//$NON-NLS-1$
	public static final String VALIDATION_DIRECTIVE_INCLUDE_FILE_NOT_FOUND="validation.directive-include-fragment-file-not-found";//$NON-NLS-1$
	
	public static final String VALIDATION_JAVA_LOCAL_VARIABLE_NEVER_USED ="validation.java-local-variable-is-never-used";
	public static final String VALIDATION_JAVA_ARGUMENT_IS_NEVER_USED  ="validation.java-";
	public static final String VALIDATION_JAVA_NULL_LOCAL_VARIABLE_REFERENCE  ="validation.java-null-local-variable-reference";
	public static final String VALIDATION_JAVA_POTENTIAL_NULL_LOCAL_VARIABLE_REFERENCE  ="validation.java-potential-null-local-variable-reference";
	public static final String VALIDATION_JAVA_UNUSED_IMPORT  ="validation.java-unused-import";
	
	public static final String VALIDATION_EL_SYNTAX  ="validation.el-general-syntax";
	public static final String VALIDATION_EL_LEXER  ="validation.el-lexical-failure";

	public static final String VALIDATION_ACTIONS_SEVERITY_MISSING_REQUIRED_ATTRIBUTE = "validation.actions-missing-required-attribute";
	public static final String VALIDATION_ACTIONS_SEVERITY_UNKNOWN_ATTRIBUTE = "validation.actions-unknown-attribute";
	public static final String VALIDATION_ACTIONS_SEVERITY_UNEXPECTED_RTEXPRVALUE = "validation.actions-unexpected-rtexprvalue";
	public static final String VALIDATION_ACTIONS_SEVERITY_NON_EMPTY_INLINE_TAG = "validation.actions-non-empty-inline-tag";
	
	public static final String VALIDATION_TRANSLATION_TEI_VALIDATION_MESSAGE = "validation.translation-tei-message";
	public static final String VALIDATION_TRANSLATION_TEI_CLASS_NOT_FOUND = "validation.translation-tei-class-not-found";
	public static final String VALIDATION_TRANSLATION_TEI_CLASS_NOT_INSTANTIATED = "validation.translation-tei-class-not-instantiated";
	public static final String VALIDATION_TRANSLATION_TEI_CLASS_RUNTIME_EXCEPTION = "validation.translation-tei-class-runtime-exception";	
	public static final String VALIDATION_TRANSLATION_TAG_HANDLER_CLASS_NOT_FOUND = "validation.translation-tag-class-not-found";
	public static final String VALIDATION_TRANSLATION_USEBEAN_INVALID_ID= "validation.translation-usebean-invalid-id";
	public static final String VALIDATION_TRANSLATION_USBEAN_MISSING_TYPE_INFO= "validation.translation-usebean-missing-type-info";
	public static final String VALIDATION_TRANSLATION_USEBEAN_AMBIGUOUS_TYPE_INFO = "validation.translation-usebean-ambiguous-type-info";
}
