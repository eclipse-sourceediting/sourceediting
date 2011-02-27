/**********************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 * Jesper Steen M�ller - adapted from org.eclipse.core.internal.content
 * David Carver - bug 284200 - make sure we get a non XML Include parser configuration
 **********************************************************************/

package org.eclipse.wst.xsl.core.resolver;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.*;

import org.apache.xerces.jaxp.SAXParserImpl;
import org.eclipse.wst.xsl.core.XSLCore;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A simple XML parser to find the XSL version of a given XML file, expectedly a XSLT stylesheet.
 */
public final class XSLVersionHandler extends DefaultHandler implements LexicalHandler, ErrorHandler {

	private static final String XSLT_STYLESHEET = "stylesheet"; //$NON-NLS-1$

	private static final String XSLT_TRANSFORM = "transform"; //$NON-NLS-1$

	private static final String XSLT_VERSION = "version"; //$NON-NLS-1$

	/**
	 * This is the value of the version attribute in the XSLT file.
	 * This member variable is <code>null</code> unless the file has been
	 * parsed successful to the point of finding the top-level element,
	 * and locating the 'version' attribute there.
	 */
	private String versionAttribute = null;

	/**
	 * TODO: Add Javadoc
	 */
	public XSLVersionHandler() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
	 */
	public final void comment(final char[] ch, final int start, final int length) {
		// Not interested.
	}

	/**
	 * Creates a new SAX parser for use within this instance.
	 * 
	 * @return The newly created parser.
	 * 
	 * @throws ParserConfigurationException
	 *             If a parser of the given configuration cannot be created.
	 * @throws SAXException
	 *             If something in general goes wrong when creating the parser.
	 * @throws SAXNotRecognizedException
	 *             If the <code>XMLReader</code> does not recognize the
	 *             lexical handler configuration option.
	 * @throws SAXNotSupportedException
	 *             If the <code>XMLReader</code> does not support the lexical
	 *             handler configuration option.
	 */
	private final synchronized SAXParser createParser(SAXParserFactory parserFactory) throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {
		// Initialize the parser.
		final SAXParserImpl parser = (SAXParserImpl)parserFactory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();
		reader.setProperty("http://xml.org/sax/properties/lexical-handler", this); //$NON-NLS-1$
		
		reader.setErrorHandler(this); // This helps to ignore errors
		// disable DTD validation
		try {
			//	be sure validation is "off" or the feature to ignore DTD's will not apply
			reader.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		} catch (SAXNotRecognizedException e) {
			// not a big deal if the parser does not recognize the features
		} catch (SAXNotSupportedException e) {
			// not a big deal if the parser does not support the features
		}
		return parser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
	 */
	public final void endCDATA() {
		// Not interested.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endDTD()
	 */
	public final void endDTD() {
		// Not interested.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
	 */
	public final void endEntity(final String name) {
		// Not interested.
	}

	/**
	 * TODO: Add Javadoc
	 * @return
	 */
	public String getVersionAttribute() {
		return versionAttribute;
	}

	/**
	 * TODO: Add Javadoc
	 * @param contents
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public boolean parseContents(InputSource contents) throws IOException, ParserConfigurationException, SAXException {
		// Parse the file into we have what we need (or an error occurs).
		try {
			SAXParserFactory factory = XSLCorePlugin.getDefault().getFactory();
			if (factory == null)
				return false;
			if (factory.isXIncludeAware()) {
				factory.setXIncludeAware(false);
			}
			final SAXParser parser = createParser(factory);
			// to support external entities specified as relative URIs (see bug 63298)
			parser.parse(contents, this);
		} catch (StopParsingException e) {
			// Abort the parsing normally. Fall through...
		}
		return true;
	}

	/*
	 * Resolve external entity definitions to an empty string.  This is to speed
	 * up processing of files with external DTDs.  Not resolving the contents 
	 * of the DTD is ok, as only the System ID of the DTD declaration is used.
	 * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		return new InputSource(new StringReader("")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
	 */
	public final void startCDATA() {
		// Not interested.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public final void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
		// Not interested.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public final void startElement(final String uri, final String elementName, final String qualifiedName, final Attributes attributes) throws SAXException {
		if (uri.equals(XSLCore.XSL_NAMESPACE_URI) && (XSLT_TRANSFORM.equals(elementName) || XSLT_STYLESHEET.equals(elementName))) {
			versionAttribute = attributes.getValue(XSLT_VERSION);
		} else {
			versionAttribute = ""; //$NON-NLS-1$
		}
		throw new StopParsingException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
	 */
	public final void startEntity(final String name) {
		// Not interested.
	}
	
	@Override
	public void warning(SAXParseException e) throws SAXException {
		// Not interested.
	}
	
	@Override
	public void error(SAXParseException e) throws SAXException {
		// Not interested.
	}
	
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		// Not interested.
	}
}
