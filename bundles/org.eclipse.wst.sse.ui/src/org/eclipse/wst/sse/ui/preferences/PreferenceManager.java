/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.preferences;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.wst.sse.core.preferences.PreferenceChangeListener;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * @deprecated no longer need a special PreferenceManager for our component.
 *             All preferences should be accessible using the base preference
 *             manager.
 */
public abstract class PreferenceManager {

	protected class EmptyNodeList implements NodeList {
		protected EmptyNodeList() {
			super();
		}

		public int getLength() {
			return 0;
		}

		public Node item(int param1) {
			return null;
		}
	}

	/**
	 * The PreferenceRuntimeException is often thrown by methods when a
	 * service we use throws a checked exception, but we want to convert and
	 * treat as a runtime exception.
	 */
	class PreferenceRuntimeException extends RuntimeException {
		/**
		 * Comment for <code>serialVersionUID</code>
		 */
		private static final long serialVersionUID = 1L;
		private Throwable originalException;

		public PreferenceRuntimeException() {
			super();
		}

		/**
		 * This form of the constructor is used to wrapper another exception.
		 */
		public PreferenceRuntimeException(Throwable t) {
			this();
			originalException = t;
		}

		public String getMessage() {
			String result = super.getMessage();
			if ((result != null) && (!result.endsWith("."))) //$NON-NLS-1$
				result = result + "."; //$NON-NLS-1$
			if (originalException != null) {
				String embeddedMessage = originalException.getMessage();
				embeddedMessage = originalException.getClass().getName() + ": " + originalException.getMessage(); //$NON-NLS-1$
				// not all exceptions have messages (e.g. many
				// NullPointerException)
				String originalError = ResourceHandler.getString("PreferenceManager.0"); //$NON-NLS-1$
				if (result == null)
					result = ""; //$NON-NLS-1$
				if (embeddedMessage != null)
					result = result + "  " + originalError + " " + embeddedMessage; //$NON-NLS-2$//$NON-NLS-1$
				else
					result = result + "  " + originalError + " " + originalException.toString(); //$NON-NLS-2$//$NON-NLS-1$
			}
			return result;
		}

		public Throwable getOriginalException() {
			return originalException;
		}

		public String toString() {
			// we don't put super.toString or getClass to "hide" that it was a
			// SourceEditing exception (otherwise, focus goes on that,
			// instead of original exception.
			String message = getMessage();
			// message should never be null ... but just in case
			return (message != null) ? message : super.toString();
		}
	}

	protected Document document = null;

	protected String fileName = null;
	private List preferenceChangeListeners = new ArrayList(1);

	protected Document _getNewDocumentDOM2() {
		Document result = null;
		// settings
		DocumentBuilder builder = getDocumentBuilder();
		result = builder.newDocument();
		Element settings = result.createElement(getRootElementName());
		result.appendChild(settings);
		return result;

	}

	protected Document _getParsedDocumentDOM2(String filename) {
		Document result = null;
		DocumentBuilder builder = getDocumentBuilder();
		try {
			Reader inputReader = new FileReader(getFilename());
			InputSource inputSource = new InputSource(inputReader);
			result = builder.parse(inputSource);
		} catch (FileNotFoundException e) {
			// file not found is "ok" ... it'll be created if we return null
			result = null;
		} catch (IOException e) {
			result = null;
		} catch (SAXException e) {
			result = null;
		}

		return result;

	}

	public void addPreferenceChangeListener(PreferenceChangeListener l) {
		if (!preferenceChangeListeners.contains(l))
			preferenceChangeListeners.add(l);
	}

	/**
	 * Returns a new document containing the defaults for this manager. This
	 * SHOULD NOT overwrite the actual document stored within this manager,
	 * and while a root element MAY BE created by the DOM implementation, it
	 * is recommended that subclasses NOT RELY upon it being there.
	 * 
	 * @return org.w3c.dom.Document
	 */
	public Document createDefaultPreferences() {
		Document txobj = null;
		txobj = _getNewDocumentDOM2();
		return txobj;
	}

	protected void firePreferenceChangeListeners() {
		if (preferenceChangeListeners != null)
			for (int i = 0; i < preferenceChangeListeners.size(); ++i)
				((PreferenceChangeListener) preferenceChangeListeners.get(i)).preferencesChanged();
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

	private DocumentBuilder getDocumentBuilder() {
		DocumentBuilder result = null;
		try {
			result = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Logger.logException(e);
		}
		return result;
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
		if (document == null)
			load();
		if (document != null)
			return (Element) getNode(getRootElement(), name);
		else
			return null;
	}

	protected abstract String getFilename();

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

	/*************************************************************************
	 * Takes a single string of the form "a/b/c" and ensures that that
	 * structure exists below the head element, down through 'c', and returns
	 * the element 'c'.
	 ************************************************************************/
	public Node getNode(Node node, String name) {
		StringTokenizer tokenizer = new StringTokenizer(name, "/"); //$NON-NLS-1$
		String token = null;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (getNamedChild(node, token) == null) {
				Document localDocument = node.getOwnerDocument();
				node.appendChild(localDocument.createElement(token));
			}
			node = getNamedChild(node, token);
		}
		return node;
	}

	protected Document getParsedDocument(String filename) {
		Document result = null;
		// file name is almost never null,
		// but can be if preferences are being ran
		// outside of a workbench application
		if (filename != null) {
			File existenceTester = new File(filename);
			if (!existenceTester.exists())
				result = null;
			else
				result = _getParsedDocumentDOM2(filename);
		}
		return result;

	}

	/**
	 * Returns the root element of the current document
	 * 
	 * @return org.w3c.dom.Element
	 */
	public Node getRootElement() {
		return getRootElement(getDocument());
	}

	/**
	 * Returns the root element of the current document
	 * 
	 * @return org.w3c.dom.Element
	 */
	public Node getRootElement(Document doc) {
		if (doc == null)
			return null;
		Node rootElement = doc.getFirstChild();
		while (rootElement != null && rootElement.getNodeType() != Node.ELEMENT_NODE && !rootElement.getNodeName().equals(getRootElementName())) {
			rootElement = rootElement.getNextSibling();
		}
		return rootElement;
	}

	/**
	 * The intended name for the root Element of the Document; what is also
	 * listed within the DOCTYPE declaration.
	 * 
	 * @return String
	 */
	public String getRootElementName() {
		return "settings"; //$NON-NLS-1$
	}

	public void load() {
		document = getParsedDocument(getFilename());

		if (document == null) {
			document = createDefaultPreferences();
		}
	}

	public void removePreferenceChangeListener(PreferenceChangeListener l) {
		preferenceChangeListeners.remove(l);
	}

	public void save() {
		if (document == null) {
			document = createDefaultPreferences();
		}
		try {
			// pa_TODO is this still going to be done like this?
			FileWriter output = new FileWriter(getFilename());
			saveDocument(document, output);
			output.flush();
			output.close();
		} catch (IOException e) {
			Logger.logException("Program Error: PreferenceManager::save. Exception saving preferences ", e); //$NON-NLS-1$
			throw new PreferenceRuntimeException(e);
		}
		firePreferenceChangeListeners();
	}

	public void saveDocument(Document document, Writer writer) throws IOException {

		serialize(document, writer);
	}

	private void serialize(Document sourceDocument, Writer writer) throws IOException {
		Source domSource = new DOMSource(sourceDocument);
		try {
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			try {
				serializer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
				serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IllegalArgumentException e) {
				// unsupported properties
			}
			serializer.transform(domSource, new StreamResult(writer));
		} catch (TransformerConfigurationException e) {
			throw new IOException(e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			throw new IOException(e.getMessage());
		} catch (TransformerException e) {
			throw new IOException(e.getMessage());
		}
	}

}
