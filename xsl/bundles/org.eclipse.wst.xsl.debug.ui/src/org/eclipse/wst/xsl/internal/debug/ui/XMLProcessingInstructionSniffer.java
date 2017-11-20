/*******************************************************************************
 * Copyright (c) 2013 Jesper Steen Moller and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jesper Steen Moller - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.xsl.internal.debug.ui;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @since 3.5
 */
public final class XMLProcessingInstructionSniffer extends DefaultHandler {
	/**
	 * An exception indicating that the parsing should stop. This is usually
	 * triggered when the top-level element has been found.
	 */
	private class StopParsingException extends SAXException {
		/**
		 * All serializable objects should have a stable serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs an instance of <code>StopParsingException</code> with a
		 * <code>null</code> detail message.
		 */
		public StopParsingException() {
			super((String) null);
		}
	}

	private Map<String, List<Map<String,String>>> processingInstructions = new TreeMap<String, List<Map<String,String>>>();

	void addInstruction(String key, Map<String,String> attributes) {
		List<Map<String, String>> list = processingInstructions.get(key);
		if (list == null) {
			list = new LinkedList<Map<String,String>>();
			processingInstructions.put(key, list);
		}
		list.add(attributes);
	}

	public List<Map<String,String>> getProcessingInstructions(String key) {
		return processingInstructions.get(key);
	}

	Pattern PSEUDO_ATTRIBUTES_REGEX = Pattern.compile("\\s*(\\w+)\\s*=\\s*['\"]([^'\"]+)['\"]\\s*"); //$NON-NLS-1$
	
	private Map<String,String> parsePseudoAttributes(String pseudoAttributes) {
		Map<String, String> attrs = new TreeMap<String, String>();
		Matcher matcher = PSEUDO_ATTRIBUTES_REGEX.matcher(pseudoAttributes);
		while (matcher.find()) {
			String value = matcher.group(2);
			// TODO: Unescape character references and predefined entity references: "&amp;" | "&lt;" | "&gt;" | "&quot;" | "&apos;"
			attrs.put(matcher.group(1), value);
		}
		return attrs;
	}
	
	public XMLProcessingInstructionSniffer() {
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
	private final SAXParser createParser(SAXParserFactory parserFactory) throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {
		// Initialize the parser.
		final SAXParser parser = parserFactory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();
//		reader.setProperty("http://xml.org/sax/properties/lexical-handler", this); //$NON-NLS-1$
		// disable DTD validation (bug 63625)
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

	public boolean parseContents(InputSource contents) throws IOException, ParserConfigurationException, SAXException {
		// Parse the file into we have what we need (or an error occurs).
	
		try {
			final SAXParser parser = createParser(SAXParserFactory.newInstance());
			// to support external entities specified as relative URIs (see bug 63298)
			contents.setSystemId("/"); //$NON-NLS-1$
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
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		return new InputSource(new StringReader("")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public final void startElement(final String uri, final String elementName, final String qualifiedName, final Attributes attributes) throws SAXException {
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
	public void processingInstruction(String target, String data)
			throws SAXException {
		addInstruction(target, parsePseudoAttributes(data));
	}

//	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
//		String xml = "<?xml-stylesheet href='where.xsl'?><root><element/></root>";
//		XMLProcessingInstructionSniffer sniffer = new XMLProcessingInstructionSniffer();
//		sniffer.parseContents(new InputSource(new StringReader(xml)));
//		List<Map<String, String>> instructions = sniffer.getProcessingInstructions("xml-stylesheet");
//		System.out.println(instructions);
//	}
}
