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
	public static final String AXIS = getAXIS();


	private static String getXPath() {
		return "xsl_xpath"; //$NON-NLS-1$
	}
	
	private static String getAXIS() {
		return "xpath_axis"; //$NON-NLS-1$
	}
	

}
