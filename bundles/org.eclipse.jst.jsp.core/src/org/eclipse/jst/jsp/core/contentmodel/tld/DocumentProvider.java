/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.contentmodel.tld;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jst.jsp.core.Logger;
import org.eclipse.wst.sse.core.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.core.util.JarUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * An XML Creator/Reader/Writer that
 * 1) Ignores any DocumentType Nodes found within the document
 *    (as well as any entities)
 * 2) Ignores any errors/exceptions from Xerces when loading a
 *    document
 * 3) Can load Documents from within a .JAR file (***read-only***)
 */

public class DocumentProvider {
	/**
	 * 
	 * @param args java.lang.String[]
	 */
	public static void main(String[] args) {
		if (args.length < 2)
			return;
		DocumentProvider p = new DocumentProvider();
		p.setFileName(args[0]);
		p.setRootElementName(args[1]);
		p.getDocument();

	}

	protected Document document = null;
	protected ErrorHandler errorHandler = null;
	private String fBaseReference;
	protected String fileName = null;
	protected boolean fValidating = true;
	protected InputStream inputStream = null;
	protected String jarFileName = null;
	protected EntityResolver resolver = null;

	protected Node rootElement = null;
	protected String rootElementName = null;

	public DocumentProvider() {
	}

	protected Document _getParsedDocumentDOM2() {
		Document result = null;

		InputStream is = null;
		try {
			DocumentBuilder builder = getDocumentBuilder();
			//		DOMParser parser = new DOMParser();
			//		parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);//$NON-NLS-1$
			//		parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);//$NON-NLS-1$
			//		parser.setErrorHandler(getNullErrorHandler());
			//		parser.setEntityResolver(getNullEntityResolver());
			//		is = getInputStream();
			builder.setEntityResolver(getEntityResolver());
			builder.setErrorHandler(getNullErrorHandler());
			is = getInputStream();
			if (is != null)
				result = builder.parse(is, getBaseReference());
		}
		catch (SAXException e) {
			result = null;
			// parsing exception, notify the user?
			Logger.log(Logger.WARNING, "SAXException while reading descriptor" + e); //$NON-NLS-1$
		}
		catch (FileNotFoundException e) {
			// NOT an "exceptional case"; do not Log
		}
		catch (IOException e) {
			Logger.log(Logger.WARNING, "IOException while reading descriptor" + e); //$NON-NLS-1$
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					// what can be done?
				}
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	public String getBaseReference() {
		return fBaseReference;
	}

	/**
	 * 
	 * @return Document
	 */
	public Document getDocument() {
		if (document == null)
			load();
		return document;
	}

	protected DocumentBuilder getDocumentBuilder() {
		return CommonXML.getDocumentBuilder(isValidating());
	}

	protected DOMImplementation getDomImplementation() {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e1) {
			Logger.logException(e1);
		}
		catch (FactoryConfigurationError e1) {
			Logger.logException(e1);
		}
		DOMImplementation impl = builder.getDOMImplementation();
		return impl;
	}

	/****************************************************
	 * Takes a single string of the form "a/b/c" and
	 * ensures that that structure exists below the
	 * head element, down through 'c', and returns a
	 * <em>single</em> element 'c'.  For multiple
	 * elements (such as multiple &lt;macro&gt; elements
	 * contained within a single &lt;macros&gt; element,
	 * full DOM access is required for searching and
	 * child element manipulation.
	 ***************************************************/
	public Element getElement(String name) {
		if (document == null)
			load();
		if (document != null)
			return (Element) getNode(getRootElement(), name);
		else
			return null;
	}

	/**
	 * Returns an EntityResolver that won't try to load and resolve ANY entities
	 */
	private EntityResolver getEntityResolver() {
		if (resolver == null) {
			resolver = new EntityResolver() {
				public InputSource resolveEntity(String publicID, String systemID) throws SAXException, IOException {
					InputSource result = null;
					if (getBaseReference() != null) {
						try {
							URL spec = new URL("file://" + getBaseReference()); //$NON-NLS-1$
							URL url = new URL(spec, systemID);
							if(url.getProtocol().startsWith("file:")) { //$NON-NLS-1$
								URLConnection connection = url.openConnection();
								result = new InputSource(systemID != null ? systemID : "/_" + toString()); //$NON-NLS-1$
								result.setPublicId(publicID);
								result.setByteStream(connection.getInputStream());
							}
						}
						catch (Exception e) {
							result = null;
						}
					}
					if (result == null) {
						result = new InputSource(new StringReader("")); //$NON-NLS-1$
						result.setPublicId(publicID);
						result.setSystemId(systemID != null ? systemID : "/_" + getClass().getName()); //$NON-NLS-1$
					}
					return result;
				}
			};
		}
		return resolver;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getFileName() {
		return fileName;
	}

	/**
	 * Returns and input stream to use as the source of the Document
	 * 1) from an InputStream set on this instance
	 * 2) from a JAR file with the given entry name
	 * 3) from a normal file
	 * 
	 * @return InputStream
	 */
	public InputStream getInputStream() throws FileNotFoundException {
		if (inputStream != null)
			return inputStream;
		else if (isJAR()) {
			return JarUtilities.getInputStream(getJarFileName(), getFileName());
		}
		else {
			return new FileInputStream(getFileName());
		}
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getJarFileName() {
		return jarFileName;
	}

	protected Node getNamedChild(Node parent, String childName) {
		if (parent == null) {
			return null;
		}
		NodeList childList = parent.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			if (childList.item(i).getNodeName().equals(childName))
				return childList.item(i);
		}
		return null;
	}

	protected Document getNewDocument() {
		Document result = null;
		try {
			result = getDomImplementation().createDocument("", getRootElementName(), null); //$NON-NLS-1$
			NodeList children = result.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				result.removeChild(children.item(i));
			}
			// we're going through this effort to avoid a NS element
			Element settings = result.createElement(getRootElementName());
			result.appendChild(settings);
			return result;
		}
		catch (DOMException e) {
			Logger.logException(e);
		}
		return null;
	}

	/****************************************************
	 * Takes a single string of the form "a/b/c" and
	 * ensures that that structure exists below the
	 * head element, down through 'c', and returns the
	 * element 'c'.
	 ***************************************************/
	protected Node getNode(Node node, String name) {
		StringTokenizer tokenizer = new StringTokenizer(name, "/"); //$NON-NLS-1$
		String token = null;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (getNamedChild(node, token) == null)
				node.appendChild(document.createElement(token));
			node = getNamedChild(node, token);
		}
		return node;
	}

	/**
	 * Returns an ErrorHandler that will not stop the parser on reported errors
	 */
	private ErrorHandler getNullErrorHandler() {
		if (errorHandler == null) {
			errorHandler = new ErrorHandler() {
				public void error(SAXParseException exception) throws SAXException {
					Logger.log(Logger.WARNING, "SAXParseException while reading descriptor" + exception.getMessage()); //$NON-NLS-1$
				}

				public void fatalError(SAXParseException exception) throws SAXException {
					Logger.log(Logger.WARNING, "SAXParseException while reading descriptor" + exception.getMessage()); //$NON-NLS-1$
				}

				public void warning(SAXParseException exception) throws SAXException {
					Logger.log(Logger.WARNING, "SAXParseException while reading descriptor" + exception.getMessage()); //$NON-NLS-1$
				}
			};
		}
		return errorHandler;
	}

	protected Document getParsedDocument() {
		Document result = null;
		if (inputStream == null) {
			File existenceTester = null;
			if (isJAR())
				existenceTester = new File(getJarFileName());
			else
				existenceTester = new File(getFileName());
			if (!existenceTester.exists())
				return null;
		}

		result = _getParsedDocumentDOM2();

		return result;

	}

	/**
	 * Returns the root Element of the current document
	 * @return org.w3c.dom.Element
	 */
	public Node getRootElement() {
		return getRootElement(getDocument());
	}

	/**
	 * Returns the/a root Element for the current document
	 * @return org.w3c.dom.Element
	 */
	protected Node getRootElement(Document doc) {
		if (doc == null)
			return null;
		if (doc.getDocumentElement() != null)
			return doc.getDocumentElement();
		try {
			Element newRootElement = doc.createElement(getRootElementName());
			doc.appendChild(newRootElement);
			return newRootElement;
		}
		catch (DOMException e) {
			Logger.logException(e);
		}
		return null;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getRootElementName() {
		return rootElementName;
	}

	protected boolean isJAR() {
		return getJarFileName() != null;
	}

	/**
	 * @return
	 */
	public boolean isValidating() {
		return fValidating;
	}

	public void load() {
		// rootElementName and fileName are expected to be defined at this point
		document = getParsedDocument();
		if (document != null) {
			rootElement = getRootElement(document);
		}

		if (document == null || rootElement == null) {
			document = getNewDocument();
			if (document != null) {
				NodeList children = document.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE && children.item(i).getNodeName().equals(getRootElementName()))
						rootElement = (Element) children.item(i);
				}
				if (rootElement == null) {
					for (int i = 0; i < children.getLength(); i++) {
						if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
							rootElement = (Element) children.item(i);
							break;
						}
					}
				}
			}
		}
	}

	protected void saveDocument(Document odocument, OutputStream stream) throws IOException {
		CommonXML.serialize(odocument, stream);
	}

	/**
	 * @param string
	 */
	public void setBaseReference(String string) {
		fBaseReference = string;
	}

	/**
	 * 
	 * @param newFileName java.lang.String
	 */
	public void setFileName(java.lang.String newFileName) {
		fileName = newFileName;
	}

	/**
	 * Sets the inputStream for which to provide a Document.
	 * @param inputStream The inputStream to set
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * 
	 * @param newJarFileName java.lang.String
	 */
	public void setJarFileName(java.lang.String newJarFileName) {
		jarFileName = newJarFileName;
	}

	/**
	 * 
	 * @param newRootElementName java.lang.String
	 */
	public void setRootElementName(java.lang.String newRootElementName) {
		rootElementName = newRootElementName;
	}

	/**
	 * @param b
	 */
	public void setValidating(boolean b) {
		fValidating = b;
	}

	public void store() {
		if (isJAR())
			return;

		if (rootElement == null) {
			document = getNewDocument();
			rootElement = document.getDocumentElement();
		}

		try {
			OutputStream ostream = new FileOutputStream(getFileName());

			storeDocument(document, ostream);

			ostream.flush();
			ostream.close();
		}
		catch (IOException e) {
			Logger.logException("Exception saving document " + getFileName(), e); //$NON-NLS-1$
			throw new SourceEditingRuntimeException(e);
		}
	}

	protected void storeDocument(Document odocument, OutputStream ostream) {
		try {
			saveDocument(odocument, ostream);
		}
		catch (IOException e) {
			Logger.logException(e);
		}
	}
}