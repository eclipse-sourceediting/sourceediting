/*******************************************************************************
 * Copyright (c) 2007, 2013 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     Jesper Steen Moller - Bug 404956: Launching an XML file as 'XSL Transformation' doesn't transform anything
 *******************************************************************************/
package org.eclipse.wst.xsl.jaxp.debug.invoker.internal;

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.wst.xsl.jaxp.debug.invoker.IProcessorInvoker;
import org.eclipse.wst.xsl.jaxp.debug.invoker.TransformationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * An implementation of <code>IProcessorInvoker</code> that uses JAXP as the underlying
 * transformation mechanism.
 * 
 * @author Doug Satchwell
 */
public class JAXPSAXProcessorInvoker implements IProcessorInvoker
{
	private static final Log log = LogFactory.getLog(JAXPSAXProcessorInvoker.class);

	private XMLReader reader;
	private SAXTransformerFactory tFactory;
	private TransformerHandler th;

	/**
	 * Create a new instance of this.
	 * 
	 * @throws SAXException if errors occur while creating an <code>XMLReader</code>
	 * @throws ParserConfigurationException if errors occur while creating an <code>XMLReader</code>
	 */
	public JAXPSAXProcessorInvoker() throws SAXException, ParserConfigurationException
	{
		reader = createReader();
	}

	protected XMLReader createReader() throws SAXException, ParserConfigurationException
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		return spf.newSAXParser().getXMLReader();
	}

	public void setAttributes(Map attributes) throws TransformerFactoryConfigurationError
	{
		createTransformerFactory();
		for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			String uri = (String) entry.getKey();
			Object value = entry.getValue();
			log.info(Messages.getString("JAXPSAXProcessorInvoker.0") + uri + Messages.getString("JAXPSAXProcessorInvoker.1") + value); //$NON-NLS-1$ //$NON-NLS-2$
			tFactory.setAttribute(uri, value);
		}
	}

	protected TransformerFactory createTransformerFactory()
	{
		tFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
		tFactory.setErrorListener(new ErrorListener()
		{

			public void error(TransformerException exception) throws TransformerException
			{
				log.error(exception.getMessageAndLocation());
			}

			public void fatalError(TransformerException exception) throws TransformerException
			{
				log.error(exception.getMessageAndLocation(), exception);
				throw exception;
			}

			public void warning(TransformerException exception) throws TransformerException
			{
				log.warn(exception.getMessageAndLocation());
			}

		});
		return tFactory;
	}

	public void addStylesheet(URL stylesheet, Map parameters, Properties outputProperties, URIResolver resolver) throws TransformerConfigurationException
	{
		InputSource inputsource = new InputSource(stylesheet.toString());

		// TODO parse document with linenumbers

		// XMLReader reader = createReader();
		// LineReadingContentHandler ch = new LineReadingContentHandler();
		// reader.setContentHandler(ch);
		SAXSource source = new SAXSource(inputsource);

		addStylesheet(source, resolver, parameters, outputProperties);

	}

	protected Transformer addStylesheet(Source source, URIResolver resolver, Map parameters, Properties outputProperties) throws TransformerConfigurationException
	{
		if (tFactory == null)
			createTransformerFactory();

		TransformerHandler newTh = tFactory.newTransformerHandler(source);
		Transformer transformer = newTh.getTransformer();

		if (resolver != null)
			transformer.setURIResolver(resolver);

		if (parameters != null)
		{
			for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				String name = (String) entry.getKey();
				Object value = entry.getValue();
				log.info(Messages.getString("JAXPSAXProcessorInvoker.2") + name + Messages.getString("JAXPSAXProcessorInvoker.3") + value); //$NON-NLS-1$ //$NON-NLS-2$
				transformer.setParameter(name, value);
			}
		}
		if (outputProperties != null)
		{
			StringBuffer sb = new StringBuffer();
			for (Iterator iter = outputProperties.entrySet().iterator(); iter.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iter.next();
				sb.append(entry.getKey()).append("=").append(entry.getValue()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (outputProperties.size() > 0)
			{
				log.info(Messages.getString("JAXPSAXProcessorInvoker.6") + sb.toString()); //$NON-NLS-1$
				transformer.setOutputProperties(outputProperties);
			}
		}

		if (th != null)
			th.setResult(new SAXResult(newTh));
		else {
			reader.setContentHandler(newTh);
			try {
				reader.setProperty("http://xml.org/sax/properties/lexical-handler", newTh); //$NON-NLS-1$
			} catch (SAXNotRecognizedException ex) {
				log.warn(Messages.getString("JAXPSAXProcessorInvoker_4")); //$NON-NLS-1$
			} catch (SAXNotSupportedException e) {
				log.warn(Messages.getString("JAXPSAXProcessorInvoker_5")); //$NON-NLS-1$
			}
		}
		th = newTh;
		return th.getTransformer();
	}

	public void transform(URL sourceURL, Result res) throws TransformationException
	{
		transform(new InputSource(sourceURL.toExternalForm()), res);
	}

	/**
	 * Transform using an InputSource rather than a URL
	 * 
	 * @param inputsource the InputSource to use
	 * @param res the Result
	 * @throws TransformationException if an error occurred during transformation
	 */
	public void transform(InputSource inputsource, Result res) throws TransformationException
	{
		try
		{
			if (th == null)
			{// no stylesheets have been added, so try to use embedded...
				SAXSource saxSource = new SAXSource(inputsource);
				Source src = saxSource;
				String media = null, title = null, charset = null;
				src = tFactory.getAssociatedStylesheet(src, media, title, charset);
				if (src != null)
				{
					addStylesheet(src, null, Collections.EMPTY_MAP, new Properties());
				}
				else
				{
					throw new TransformationException(Messages.getString("JAXPSAXProcessorInvoker.7") + inputsource.getSystemId()); //$NON-NLS-1$
				}
			}
			th.setResult(res);
			log.info(Messages.getString("JAXPSAXProcessorInvoker.8")); //$NON-NLS-1$
			reader.parse(inputsource);
			log.info(Messages.getString("JAXPSAXProcessorInvoker.9")); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			throw new TransformationException(e.getMessage(), e);
		}
	}
}
