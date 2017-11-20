/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.launching;

import java.util.Map;

/**
 * A particular type of XSLT Processor (e.g. Xalan) for which there may be any number of installs.
 * 
 * @see IProcessorInstall
 * @author Doug Satchwell
 */
public interface IProcessorType
{
	String getId();

	String getLabel();

	boolean isJREDefault();

	ITransformerFactory[] getTransformerFactories();

	IAttribute[] getAttributes();

	Map<String, String> getAttributeValues();

	IOutputProperty[] getOutputProperties();

	Map<String, String> getOutputPropertyValues();

	ITransformerFactory getDefaultTransformerFactory();
}
