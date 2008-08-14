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
package org.eclipse.wst.xsl.jaxp.debug.invoker;

import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;


/**
 * Interface used by the launched process to define the XSL transformation pipeline.
 * The <code>setAttributes</code> and <code>addStylesheet</code> methods should be called
 * before calling <code>transform</code> to do the actual transformation.
 * 
 * <p>
 * If <code>addStylesheet</code> is not called before <code>transform</code>, then
 * it is assumed that the source document contains and embedded stylesheet instruction.
 * </p>
 * 
 * @author Doug Satchwell
 */
public interface IProcessorInvoker
{
	/**
	 * Set the processor-specific attributes to use.
	 * 
	 * @param attributes a map of String v. String attributes
	 */
	void setAttributes(Map attributes);

	/**
	 * Add a stylesheet to the pipeline (order is important).
	 * 
	 * @param stylesheet the URL of the stylesheet to add
	 * @param parameters the map of parameters for the stylesheet
	 * @param outputProperties the output properties
	 * @param resolver the <code>URIResolver</code> to use
	 * @throws TransformerConfigurationException if stylesheet could not be added
	 */
	void addStylesheet(URL stylesheet, Map parameters, Properties outputProperties, URIResolver resolver) throws TransformerConfigurationException;

	/**
	 * Perform the actual transformation.
	 * 
	 * @param source the URL of the XML source document
	 * @param res the transformation result
	 * @throws TransformationException if the transformation failed
	 */
	void transform(URL source, Result res) throws TransformationException;
}
