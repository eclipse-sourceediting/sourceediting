/*******************************************************************************
 * Copyright (c) 2008 David Carver and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.ui.templates;

/**
 * 
 * @author dcarver
 *
 */
public class TemplateContextTypeIdsXPath {

	/**
	 * TODO: Add Javadoc
	 */
	public static final String XPATH = getXPath();
	
	/**
	 * TODO: Add Javadoc
	 */
	public static final String XPATH2 = getXPath2();


	/**
	 * TODO: Add Javadoc
	 */
	public static final String AXIS = getAXIS();
	
	
	/**
	 * TODO: Add Javadoc
	 */
	public static final String EXSLT = getEXSLT();


	private static String getXPath() {
		return "xsl_xpath"; //$NON-NLS-1$
	}

	private static String getXPath2() {
		return "xpath_2"; //$NON-NLS-1$
	}
	
	private static String getAXIS() {
		return "xpath_axis"; //$NON-NLS-1$
	}
	
	private static String getEXSLT() {
		return "exslt_functions"; //$NON-NLS-1$
	}
	
	private static String getOperator() {
		return "xpath_operator"; //$NON-NLS-1$
	}

	private static String getCustom() {
		return "extension_function"; //$NON-NLS-1$
	}
	

}
