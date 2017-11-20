/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core;

/**
 * Preference constants specific to the XSL validator.
 * 
 * @author Doug Satchwell
 * @see org.eclipse.core.resources.IMarker
 */
public class ValidationPreferences
{
	
	/**
	 * The maximum number of errors allowed per XSL file.
	 * <p>
	 * Value is of type <code>Integer</code>.
	 * </p>
	 */
	public static final String MAX_ERRORS = "MAX_ERRORS"; //$NON-NLS-1$
	
	/**
	 * The level at which a missing parameter is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String MISSING_PARAM = "MISSING_PARAM"; //$NON-NLS-1$
	
	/**
	 * The level at which an invalid XPath is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String XPATHS = "CHECK_XPATHS"; //$NON-NLS-1$
	
	/**
	 * The level at which an unresolved called template is reported. 
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String CALL_TEMPLATES = "CHECK_CALL_TEMPLATES"; //$NON-NLS-1$
	
	/**
	 * The level at which a parameter that does not have a default value and does not specify a value is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String EMPTY_PARAM = "EMPTY_PARAM"; //$NON-NLS-1$
	
	/**
	 * The level at which an unresolved import or included is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String MISSING_INCLUDE = "MISSING_INCLUDE"; //$NON-NLS-1$
	
	/**
	 * The level at which an include/import circular references is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String CIRCULAR_REF = "CIRCULAR_REF"; //$NON-NLS-1$
	
	/**
	 * The level at which a template conflict is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String TEMPLATE_CONFLICT = "TEMPLATE_CONFLICT"; //$NON-NLS-1$
	
	/**
	 * The level at which a parameter without a name attribute is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String NAME_ATTRIBUTE_MISSING = "NAME_ATTRIBUTE_MISSING"; //$NON-NLS-1$
	
	/**
	 * The level at which a parameter with an empty name attribute is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String NAME_ATTRIBUTE_EMPTY = "NAME_ATTRIBUTE_EMPTY"; //$NON-NLS-1$
	
	/**
	 * The level at which a duplicate parameter is reported.
	 * <p>
	 * Value is one of <code>IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO</code>
	 * </p>
	 */
	public static final String DUPLICATE_PARAMETER = "DUPLICATE_PARAMETER"; //$NON-NLS-1$
}
