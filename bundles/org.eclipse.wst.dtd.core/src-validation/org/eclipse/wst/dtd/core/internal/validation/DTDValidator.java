/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.dtd.core.internal.validation;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.xml.core.internal.validation.core.LazyURLInputStream;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.ibm.icu.util.StringTokenizer;

/**
 * DTD validation.
 */
public class DTDValidator {
	/**
	 * An entity resolver that wraps a URI resolver.
	 */
	class DTDEntityResolver implements EntityResolver {
		private String fBaseLocation = null;
		private URIResolver fURIResolver = null;

		/**
		 * Constructor.
		 * 
		 * @param idresolver
		 *            The idresolver this entity resolver wraps.
		 * @param baselocation
		 *            The base location to resolve with.
		 */
		public DTDEntityResolver(URIResolver uriresolver, String baselocation) {
			this.fURIResolver = uriresolver;
            
            // TODO cs: we never seem to set a URIResolver
            // I create one here up front just incase
            //
            if (fURIResolver == null)
            {
              fURIResolver = URIResolverPlugin.createResolver();              
            }  
			this.fBaseLocation = baselocation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
		 *      java.lang.String)
		 */
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			String location = null;
            
            
			if (fBaseLocation.equals(systemId)) {
				location = systemId;
			}
			else {
				location = fURIResolver.resolve(fBaseLocation, publicId, systemId);
			}
			InputSource is = null;
			if (location != null && !location.equals("")) //$NON-NLS-1$
			{
			  // CS : while working on bug 113537 I noticed we're not using the LazyURLInputStream
		      // so fixed this to avoid leaking file handles
			  //
              String physical = fURIResolver.resolvePhysicalLocation(fBaseLocation, publicId, location); 
			  is = new InputSource(location);
			  is.setByteStream(new LazyURLInputStream(physical));
			}
			return is;
		}
	}

	/**
	 * An error handler for DTD validation.
	 * 
	 * @author Lawrence Mandel, IBM
	 */
	class DTDErrorHandler implements ErrorHandler {

		private final int ERROR = 0;
		private final ValidationInfo fValidationInfo;

		private final int WARNING = 1;

		/**
		 * Constructor.
		 * 
		 * @param valinfo
		 *            The validation info object to use to register validation
		 *            messages.
		 */
		public DTDErrorHandler(ValidationInfo valinfo) {
			this.fValidationInfo = valinfo;
		}

		/**
		 * Add a validation message with the given severity.
		 * 
		 * @param exception
		 *            The exception that contains the information about the
		 *            message.
		 * @param severity
		 *            The severity of the validation message.
		 */
		protected void addValidationMessage(SAXParseException exception, int severity) {
			if (exception.getSystemId() != null) {
				if (severity == WARNING) {
					fValidationInfo.addWarning(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId());
				}
				else {
					fValidationInfo.addError(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId());
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		public void error(SAXParseException exception) throws SAXException {
			addValidationMessage(exception, ERROR);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException exception) throws SAXException {
			addValidationMessage(exception, ERROR);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		public void warning(SAXParseException exception) throws SAXException {
			addValidationMessage(exception, WARNING);
		}
	}

	class MultiHandler extends DefaultHandler implements org.xml.sax.DTDHandler, ContentHandler, LexicalHandler, DeclHandler {
		private static final String ELEMENT_MODIFIERS = "*+?"; //$NON-NLS-1$

		private static final String MODEL_DELIMITERS = ",()| "; //$NON-NLS-1$

		private Map fElemDecls = new HashMap();

		private Hashtable fElemRefs = new Hashtable();

		private List fIgnoreElemModel = new ArrayList();

		private List fIgnoreElemRefs = new ArrayList();

		private Locator fLocator = null;

		public MultiHandler() {
			fIgnoreElemRefs.add("#PCDATA"); //$NON-NLS-1$
			fIgnoreElemModel.add("ANY"); //$NON-NLS-1$
			fIgnoreElemModel.add("EMPTY"); //$NON-NLS-1$
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.DeclHandler#attributeDecl(java.lang.String,
		 *      java.lang.String, java.lang.String, java.lang.String,
		 *      java.lang.String)
		 */
		public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
		 */
		public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String,
		 *      java.lang.String)
		 */
		public void elementDecl(String name, String model) throws SAXException {
			// Add each referenced element to the list of referenced elements
			int line = fLocator.getLineNumber();
			int column = fLocator.getColumnNumber();
			String uri = fLocator.getSystemId();			
			// Add this element to the list of declared elements.
			List locations = (List) fElemDecls.get(name);
			if (locations == null) {
				locations = new ArrayList();
				fElemDecls.put(name, locations);
			}
			locations.add(new ElementLocation(column, line, uri));

			// Return if the element model should be ignored. The model should
			// be
			// ignored in such cases as when it is equal to EMPTY or ANY.
			if (fIgnoreElemModel.contains(model)) {
				return;
			}

			StringTokenizer strtok = new StringTokenizer(model, MODEL_DELIMITERS);
			while (strtok.hasMoreTokens()) {
				String token = strtok.nextToken();
				int tokenlength = token.length();
				if (ELEMENT_MODIFIERS.indexOf(token.charAt(tokenlength - 1)) != -1) {
					token = token.substring(0, tokenlength - 1);
					// If the token is now empty (it was only ?,* or +) then
					// continue.
					if (token.length() == 0) {
						continue;
					}
				}
				if (fIgnoreElemRefs.contains(token)) {
					continue;
				}
				ElementRefLocation elemLoc = (ElementRefLocation) fElemRefs.get(token);
				ElementRefLocation tokenLoc = new ElementRefLocation(line, column, uri, elemLoc);
				fElemRefs.put(token, tokenLoc);
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
		 */
		public void endCDATA() throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#endDTD()
		 */
		public void endDTD() throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
		 */
		public void endEntity(String arg0) throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.DeclHandler#externalEntityDecl(java.lang.String,
		 *      java.lang.String, java.lang.String)
		 */
		public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
			// No method impl.
		}

		/**
		 * Get the list of element declarations.
		 * 
		 * @return The list of element declarations.
		 */
		public Map getElementDeclarations() {
			return fElemDecls;
		}

		/**
		 * Get the element references hashtable.
		 * 
		 * @return The element references hashtable.
		 */
		public Hashtable getElementReferences() {
			return fElemRefs;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.DeclHandler#internalEntityDecl(java.lang.String,
		 *      java.lang.String)
		 */
		public void internalEntityDecl(String name, String value) throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
		 */
		public void setDocumentLocator(Locator locator) {
			super.setDocumentLocator(locator);
			this.fLocator = locator;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
		 */
		public void startCDATA() throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String,
		 *      java.lang.String, java.lang.String)
		 */
		public void startDTD(String name, String publicId, String systemId) throws SAXException {
			// No method impl.
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
		 */
		public void startEntity(String name) throws SAXException {
			// No method impl.
		}
	}

	/**
	 * Keeps track of element location information
	 *
	 */
	class ElementLocation {
		int column = -1;
		int line = -1;
		String uri;
		ElementLocation(int column, int line, String uri) {
			this.column = column;
			this.line = line;
			this.uri = uri;
		}
	}

	private URIResolver fResolver = null;

	public DTDValidator() {
		super();
	}

	/**
	 * Set the URI resolver to use with XSD validation.
	 * 
	 * @param uriresolver
	 *            The URI resolver to use.
	 */
	public void setURIResolver(URIResolver uriresolver) {
		this.fResolver = uriresolver;
	}

	/**
	 * Validate the DTD file located at the URI.
	 * 
	 * @param uri
	 *            The URI of the file to validate.
	 * @return A validation report for the validation.
	 */
	public ValidationReport validate(String uri) {
		ValidationInfo valinfo = new ValidationInfo(uri);
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			XMLReader reader = parser.getXMLReader();
			MultiHandler dtdHandler = new MultiHandler();
			reader.setProperty("http://xml.org/sax/properties/declaration-handler", dtdHandler); //$NON-NLS-1$
			reader.setProperty("http://xml.org/sax/properties/lexical-handler", dtdHandler); //$NON-NLS-1$
			reader.setContentHandler(dtdHandler);
			reader.setDTDHandler(dtdHandler);
			reader.setErrorHandler(new DTDErrorHandler(valinfo));
			reader.setEntityResolver(new DTDEntityResolver(fResolver, uri));
			String document = "<!DOCTYPE root SYSTEM \"" + uri + "\"><root/>"; //$NON-NLS-1$ //$NON-NLS-2$

			reader.parse(new InputSource(new StringReader(document)));

			Map elemDecls = dtdHandler.getElementDeclarations();
			Hashtable elemRefs = dtdHandler.getElementReferences();
			validateElementReferences(elemDecls, elemRefs, valinfo);

			validateDuplicateElementDecls(elemDecls, valinfo);
		}
		catch (ParserConfigurationException e) {

		}
		catch (IOException e) {

		}
		catch (SAXException e) {

		}
		return valinfo;
	}

	private void validateDuplicateElementDecls(Map elemDecls, ValidationInfo valinfo) {
		final Iterator it = elemDecls.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry elem = (Map.Entry) it.next();
			List locations = (List) elem.getValue();
			if (locations.size() > 1) {
				final Iterator locationIterator = locations.iterator();
				while (locationIterator.hasNext()) {
					ElementLocation elemLoc = (ElementLocation) locationIterator.next();
					valinfo.addError(NLS.bind(DTDValidationMessages._ERROR_DUPLICATE_ELEMENT_DECLARATION, "'" + elem.getKey() + "'"), elemLoc.line, elemLoc.column, elemLoc.uri); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	/**
	 * Validate the element references in the DTD. An element reference is
	 * <!ELEMENT elem (elementReference)>
	 * 
	 * @param elemDecls
	 *            A list of valid element declarations.
	 * @param elemRefs
	 *            A hashtable containing element references as keys and
	 *            locations in the document as values.
	 * @param valinfo
	 *            The validation info object to store validation information.
	 */
	private void validateElementReferences(Map elemDecls, Hashtable elemRefs, ValidationInfo valinfo) {
		Enumeration keys = elemRefs.keys();
		while (keys.hasMoreElements()) {
			String elemRef = (String) keys.nextElement();
			// If the element hasn't been declared create an error.
			if (!elemDecls.containsKey(elemRef)) {
				ElementRefLocation elemLoc = (ElementRefLocation) elemRefs.get(elemRef);
				do {
					valinfo.addError(NLS.bind(DTDValidationMessages._ERROR_REF_ELEMENT_UNDEFINED, "'" + elemRef + "'"), elemLoc.getLine(), elemLoc.getColumn(), elemLoc.getURI()); //$NON-NLS-1$ //$NON-NLS-2$
					elemLoc = elemLoc.getNext();
				}
				while (elemLoc != null);
			}
		}
	}

}
