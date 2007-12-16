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
package org.eclipse.wst.xsl.launching;

import java.util.Map;
import java.util.Properties;

/**
 * Represents a particular type of XSLT Processor for which there may be any
 * number of installations.
 */
public interface IProcessorType
{
	String getId();

	String getLabel();

	boolean isJREDefault();

	String getTransformerFactoryName();

	IFeature[] getFeatures();

	Map getFeatureValues();

	IOutputProperty[] getOutputProperties();

	Properties getOutputPropertyValues();
}
