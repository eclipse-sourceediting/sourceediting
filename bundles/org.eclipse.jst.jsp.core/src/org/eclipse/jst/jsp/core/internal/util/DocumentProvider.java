/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.util;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.wst.sse.core.internal.util.JarUtilities;
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

import com.ibm.icu.util.StringTokenizer;

/**
 * An XML Creator/Reader/Writer that 1) Ignores any DocumentType Nodes found
 * within the document (as well as any entities) 2) Ignores any
 * errors/exceptions from Xerces when loading a document 3) Can load Documents
 * from within a .JAR file (***read-only***)
 */

public class DocumentProvider {
	private Document document = null;
	private ErrorHandler errorHandler = null;
	private String fBaseReference;
	private String fileName = null;
	private boolean fValidating = false;
	private InputStream inputStream = null;
	private String jarFileName = null;
	private EntityResolver resolver = null;

	private Node rootElement = null;
	private String rootElementName = null;

	public DocumentProvider() {
		super();
	}

	private String _getFileName() {
		if (inputStream != null)
			return null;
		else if (isJAR()) {
			return getJarFileName();
		}
		return getFileName();
	}

	private Document _getParsedDocumentDOM2() {
		Document result = null;

		InputStream is = null;
		try {
			DocumentBuilder builder = getDocumentBuilder();
			// DOMParser parser = new DOMParser();
			// parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error",
			// false);//$NON-NLS-1$
			// parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
			// false);//$NON-NLS-1$
			// parser.setErrorHandler(getNullErrorHandler());
			// parser.setEntityResolver(getNullEntityResolver());
			// is = getInputStream();
			builder.setEntityResolver(getEntityResolver());
			builder.setErrorHandler(getNullErrorHandler());
			is = getInputStream();
			if (is != null)
				result = builder.parse(is, getBaseReference());
		}
		catch (SAXException e) {
			// parsing exception, notify the user?
			Logger.log(Logger.WARNING_DEBUG, "SAXException while reading descriptor " + _getFileName() + " " + e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (FileNotFoundException e) {
			// NOT an "exceptional case"; do not Log
		}
		catch (IOException e) {
			Logger.log(Logger.WARNING_DEBUG, "IOException while reading descriptor " + _getFileName() + " " + e); //$NON-NLS-1$ //$NON-NLS-2$
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
		return getDocument(true);
	}

	public Document getDocument(boolean createEmptyOnFailure) {
		if (document == null)
			load(createEmptyOnFailure);
		return document;
	}

	DocumentBuilder fDocumentBuilder = null;
	
	private DocumentBuilder getDocumentBuilder() {
		if (fDocumentBuilder == null) {
			fDocumentBuilder = CommonXML.getDocumentBuilder(isValidating());
		}
		return fDocumentBuilder;
	}

	private DOMImplementation getDomImplementation() {
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

	/*************************************************************************
	 * Takes a single string of the form "a/b/c" and ensures that that
	 * structure exists below the head element, down through 'c', and returns
	 * a <em>single</em> element 'c'. For multiple elements (such as
	 * multiple &lt;macro&gt; elements contained within a single
	 * &lt;macros&gt; element, full DOM access is required for searching and
	 * child element manipulation.
	 ************************************************************************/
	public Element getElement(String name) {
		Element result = null;
		if (document == null)
			load(false);
		if (document != null) {
			result = (Element) getNode(getRootElement(), name);
		}
		return result;
	}

	/**
	 * Returns an EntityResolver that won't try to load and resolve ANY
	 * entities
	 */
	private EntityResolver getEntityResolver() {
		if (resolver == null) {
			resolver = new EntityResolver() {
				public InputSource resolveEntity(String publicID, String systemID) throws SAXException, IOException {
					InputSource result = null;
					if (isValidating()) {
						try {
							URL spec = new URL("file://" + getBaseReference()); //$NON-NLS-1$
							URL url = new URL(spec, systemID);
							if (url.getProtocol().startsWith("file:")) { //$NON-NLS-1$
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
						result = new InputSource(new ByteArrayInputStream(new byte[0]));
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
	public String getFileName() {
		return fileName;
	}

	/**
	 * Returns and input stream to use as the source of the Document 1) from
	 * an InputStream set on this instance 2) from a JAR file with the given
	 * entry name 3) from a normal file
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
			return new BufferedInputStream(new FileInputStream(getFileName()));
		}
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getJarFileName() {
		return jarFileName;
	}

	private Node getNamedChild(Node parent, String childName) {
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

	private Document getNewDocument() {
		Document result = null;
		try {
			result = getDomImplementation().createDocument("http://www.w3.org/XML/1998/namespace", getRootElementName(), null); //$NON-NLS-1$
			NodeList children = result.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				result.removeChild(children.item(i));
			}
			// we're going through this effort to avoid a NS element
			Element settings = result.createElementNS("http://www.w3.org/XML/1998/namespace", getRootElementName()); //$NON-NLS-1$
			result.appendChild(settings);
			return result;
		}
		catch (DOMException e) {
			Logger.logException(e);
		}
		return null;
	}

	/*************************************************************************
	 * Takes a single string of the form "a/b/c" and ensures that that
	 * structure exists below the head element, down through 'c', and returns
	 * the element 'c'.
	 ************************************************************************/
	private Node getNode(Node node, String name) {
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
	 * Returns an ErrorHandler that will not stop the parser on reported
	 * errors
	 */
	private ErrorHandler getNullErrorHandler() {
		if (errorHandler == null) {
			errorHandler = new ErrorHandler() {
				public void error(SAXParseException exception) throws SAXException {
					Logger.log(Logger.WARNING_DEBUG, "SAXParseException with " + fBaseReference + "/" + getJarFileName() + "/" + getFileName() + " (error) while reading descriptor: " + exception.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}

				public void fatalError(SAXParseException exception) throws SAXException {
					Logger.log(Logger.WARNING_DEBUG, "SAXParseException with " + fBaseReference + "/" + getJarFileName() + "/" + getFileName() + " (fatalError) while reading descriptor: " + exception.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

				public void warning(SAXParseException exception) throws SAXException {
					Logger.log(Logger.WARNING_DEBUG, "SAXParseException with " + fBaseReference + "/" + getJarFileName() + "/" + getFileName() + " (warning) while reading descriptor: " + exception.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			};
		}
		return errorHandler;
	}

	private Document getParsedDocument() {
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
	 * 
	 * @return org.w3c.dom.Element
	 */
	public Node getRootElement() {
		return getRootElement(getDocument());
	}

	/**
	 * Returns the/a root Element for the current document
	 * 
	 * @return org.w3c.dom.Element
	 */
	private Node getRootElement(Document doc) {
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

	private boolean isJAR() {
		return getJarFileName() != null;
	}

	/**
	 * @return
	 */
	public boolean isValidating() {
		return fValidating;
	}

	void load(boolean createEmptyOnFailure) {
		// rootElementName and fileName are expected to be defined at this
		// point
		document = getParsedDocument();
		if (document != null) {
			if (rootElementName != null)
				rootElement = getRootElement(document);
			else
				rootElement = document.getDocumentElement();
		}

		if (document == null || rootElement == null) {
			if (createEmptyOnFailure) {
				document = getNewDocument();
				if (document != null) {
					NodeList children = document.getChildNodes();
					for (int i = 0; i < children.getLength(); i++) {
						if (children.item(i).getNodeType() == Node.ELEMENT_NODE && children.item(i).getNodeName().equals(getRootElementName()))
							rootElement = children.item(i);
					}
					if (rootElement == null) {
						for (int i = 0; i < children.getLength(); i++) {
							if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
								rootElement = children.item(i);
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param string
	 */
	public void setBaseReference(String string) {
		fBaseReference = string;
	}

	/**
	 * 
	 * @param newFileName
	 *            java.lang.String
	 */
	public void setFileName(java.lang.String newFileName) {
		fileName = newFileName;
	}

	/**
	 * Sets the inputStream for which to provide a Document.
	 * 
	 * @param inputStream
	 *            The inputStream to set
	 */
	public void setInputStream(InputStream iStream) {
		this.inputStream = iStream;
	}

	/**
	 * 
	 * @param newJarFileName
	 *            java.lang.String
	 */
	public void setJarFileName(java.lang.String newJarFileName) {
		jarFileName = newJarFileName;
	}

	/**
	 * 
	 * @param newRootElementName
	 *            java.lang.String
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
}
