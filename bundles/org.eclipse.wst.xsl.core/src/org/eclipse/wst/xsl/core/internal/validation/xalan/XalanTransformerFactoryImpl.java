/*******************************************************************************
 * Copyright (c) 2007 Standards for Technology in Automotive Retail
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Carver - STAR - bug 224197 - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.core.internal.validation.xalan;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;

import org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor.TransformerFactoryImpl;
import org.apache.xml.utils.SystemIDResolver;
import org.eclipse.wst.xsl.core.internal.compiler.xslt10.processor.CustomStylesheetHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author dcarver
 *
 */
public class XalanTransformerFactoryImpl extends TransformerFactoryImpl {

	/**
	 * <p>
	 * State of secure processing feature.
	 * </p>
	 */
	private boolean m_isSecureProcessing = false;

	/**
	 * 
	 */
	public XalanTransformerFactoryImpl() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Process the source into a Templates object, which is likely a compiled
	 * representation of the source. This Templates object may then be used
	 * concurrently across multiple threads. Creating a Templates object allows
	 * the TransformerFactory to do detailed performance optimization of
	 * transformation instructions, without penalizing runtime transformation.
	 * 
	 * @param source
	 *            An object that holds a URL, input stream, etc.
	 * @return A Templates object capable of being used for transformation
	 *         purposes.
	 * 
	 * @throws TransformerConfigurationException
	 *             May throw this during the parse when it is constructing the
	 *             Templates object and fails.
	 */
	@Override
	public Templates newTemplates(Source source)
			throws TransformerConfigurationException {

		String baseID = source.getSystemId();

		if (null != baseID) {
			baseID = SystemIDResolver.getAbsoluteURI(baseID);
		}

		// if (source instanceof DOMSource) {
		// DOMSource dsource = (DOMSource) source;
		// Node node = dsource.getNode();
		//
		// if (null != node)
		// return processFromNode(node, baseID);
		// else {
		// String messageStr = XSLMessages.createMessage(
		// XSLTErrorResources.ER_ILLEGAL_DOMSOURCE_INPUT, null);
		//
		// throw new IllegalArgumentException(messageStr);
		// }
		// }

		TemplatesHandler builder = newTemplatesHandler();
		builder.setSystemId(baseID);

		try {
			InputSource isource = SAXSource.sourceToInputSource(source);
			isource.setSystemId(baseID);
			XMLReader reader = null;

			if (source instanceof SAXSource)
				reader = ((SAXSource) source).getXMLReader();

			if (null == reader) {

				// Use JAXP1.1 ( if possible )
				try {
					javax.xml.parsers.SAXParserFactory factory = javax.xml.parsers.SAXParserFactory
							.newInstance();

					factory.setNamespaceAware(true);

					if (m_isSecureProcessing) {
						try {
							factory.setFeature(
									XMLConstants.FEATURE_SECURE_PROCESSING,
									true);
						} catch (org.xml.sax.SAXException se) {
						}
					}

					javax.xml.parsers.SAXParser jaxpParser = factory
							.newSAXParser();

					reader = jaxpParser.getXMLReader();
				} catch (javax.xml.parsers.ParserConfigurationException ex) {
					throw new org.xml.sax.SAXException(ex);
				} catch (javax.xml.parsers.FactoryConfigurationError ex1) {
					throw new org.xml.sax.SAXException(ex1.toString());
				} catch (NoSuchMethodError ex2) {
				} catch (AbstractMethodError ame) {
				}
			}

			if (null == reader)
				reader = XMLReaderFactory.createXMLReader();

			// If you set the namespaces to true, we'll end up getting double
			// xmlns attributes. Needs to be fixed. -sb
			// reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
			// true);
			reader.setContentHandler(builder);
			reader.parse(isource);
		} catch (org.xml.sax.SAXException se) {
			if (super.getErrorListener() != null) {
				try {
					super.getErrorListener().fatalError(
							new TransformerException(se));
				} catch (TransformerConfigurationException ex1) {
					throw ex1;
				} catch (TransformerException ex1) {
					throw new TransformerConfigurationException(ex1);
				}
			} else {
				throw new TransformerConfigurationException(se.getMessage(), se);
			}
		} catch (Exception e) {
			if (super.getErrorListener() != null) {
				try {
					super.getErrorListener().fatalError(
							new TransformerException(e));
					return null;
				} catch (TransformerConfigurationException ex1) {
					throw ex1;
				} catch (TransformerException ex1) {
					throw new TransformerConfigurationException(ex1);
				}
			} else {
				throw new TransformerConfigurationException(e.getMessage(), e);
			}
		}

		return builder.getTemplates();
	}

	/**
	 * This class overrides the TransformerFactoryImpl implementation so that a
	 * custom Stylesheet handler that doesn't stop processing at the first error
	 * can be used to get all of the errors in a stylesheet not just the first
	 * one.
	 */
	@Override
	public TemplatesHandler newTemplatesHandler()
			throws TransformerConfigurationException {
		// TODO Auto-generated method stub
		return new CustomStylesheetHandler(this);
	}

	/**
	 * Get a TransformerHandler object that can process SAX ContentHandler
	 * events into a Result, based on the transformation instructions specified
	 * by the argument.
	 * 
	 * @param src
	 *            The source of the transformation instructions.
	 * 
	 * @return TransformerHandler ready to transform SAX events.
	 * 
	 * @throws TransformerConfigurationException
	 */
	@Override
	public TransformerHandler newTransformerHandler(Source src)
			throws TransformerConfigurationException {

		Templates templates = newTemplates(src);
		if (templates == null)
			return null;

		return newTransformerHandler(templates);
	}

}
