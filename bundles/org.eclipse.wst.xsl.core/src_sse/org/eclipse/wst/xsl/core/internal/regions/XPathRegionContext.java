/*******************************************************************************
 * Copyright (c) 2008 Standards for Technology in Automotive Retail and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.regions;

public interface XPathRegionContext {

	public static final String BLOCK_TEXT = "BLOCK_TEXT"; //$NON-NLS-1$

	public static final String UNDEFINED = "UNDEFINED"; //$NON-NLS-1$

	public static final String WHITE_SPACE = "WHITE_SPACE"; //$NON-NLS-1$

	public static final String XPATH_CONTENT = "XPATH_CONTENT"; //$NON-NLS-1$
	public static final String XPATH_SEPARATOR_DIV = "XPATH_SEPARATOR_DIV"; //$NON-NLS-1$
	public static final String XPATH_FUNCTION_PARAMETERS_OPEN = "XPATH_FUNCTION_PARAMETERS_OPEN"; //$NON-NLS-1$
	public static final String XPATH_FUNCTION_PARAMETERS_CLOSE = "XPATH_FUNCTION_PARAMETERS_CLOSE"; //$NON-NLS-1$
	public static final String XPATH_FUNCTION_NAME = "XPATH_FUNCTION_NAME"; //$NON-NLS-1$
}
