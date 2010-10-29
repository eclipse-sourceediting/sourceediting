/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.dtd.core.internal.saxparser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.validation.core.LazyURLInputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class DTDParser extends DefaultHandler implements ContentHandler, DTDHandler, ErrorHandler, DeclHandler, LexicalHandler {
	private String contentString = ""; //$NON-NLS-1$
	//private String declString = ""; //$NON-NLS-1$

	/* Canonical output. */
	protected boolean canonical;

	protected XMLReader reader;
	int entityDepth;

	// dmw 11/15 private field never read locally
	// private Vector declElements;
	private String comment = null;
	private ErrorMessage errorMessage = null;
	private List errorMessages = new ArrayList();
	private DeclNode previousDeclNode = null;
	private DeclNode currentDeclNode = null;
	private BaseNode lastBaseNode = null;

	private boolean entityEnd = false;
	private int lineNumber;

	private DTD currentDTD = null;
	private Vector dtdList = new Vector();

	private Stack dtdStack = new Stack();
	private Stack peRefStack = new Stack();
	private Stack parsingPERefStack = new Stack();

	private EntityPool entityPool = new EntityPool();
	private String expandedEntityValue = null;
	private Hashtable elementPool = new Hashtable();

	boolean parsingExternalPEReference = false;
	protected boolean expandEntityReferences = true;

	private Locator locator = null;

	private Locator getLocator() {
		return locator;
	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public DTDParser(boolean canonical) throws UnsupportedEncodingException {
		this(null, canonical);
	}

	protected DTDParser(String encoding, boolean canonical) throws UnsupportedEncodingException {

		if (encoding == null)
			encoding = "UTF-8"; //$NON-NLS-1$

		this.canonical = canonical;
		try {
			SAXParser sparser = SAXParserFactory.newInstance().newSAXParser();
			reader = sparser.getXMLReader();
			reader.setProperty("http://xml.org/sax/properties/declaration-handler", this); //$NON-NLS-1$
			reader.setProperty("http://xml.org/sax/properties/lexical-handler", this); //$NON-NLS-1$
			reader.setContentHandler(this);
			reader.setDTDHandler(this);
			reader.setErrorHandler(this);
			reader.setEntityResolver(this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* Prints the output from the SAX callbacks. */
	public void parse(String logicalURI) {
		try {			
			// CS ensure that the physical URI is considered where streams are required
			// and the logicalURI is used as the DTD's baseLocation
			String physicalURI = URIResolverPlugin.createResolver().resolvePhysicalLocation("", "", logicalURI); //$NON-NLS-1$ //$NON-NLS-2$
			boolean isReadable = URIHelper.isReadableURI(physicalURI, true);
			if (!isReadable) {
				throw new Exception("DTD parse error. Can not read the specified URI : " + logicalURI); //$NON-NLS-1$
			}			
			String document = "<!DOCTYPE root SYSTEM \"" + logicalURI + "\"><root/>"; //$NON-NLS-1$ //$NON-NLS-2$
			entityDepth = 0;
			currentDTD = new DTD(logicalURI);
			InputSource inputSource = new InputSource(new StringReader(document));
			inputSource.setSystemId(logicalURI + ".xml"); //$NON-NLS-1$
			reader.parse(inputSource);
		}
		catch (SAXParseException se) {
			if (currentDTD != null)
				currentDTD.setIsExceptionDuringParse(true);
		}
		catch (Exception e) {
			if (currentDTD != null)
				currentDTD.setIsExceptionDuringParse(true);
		}

	}

	/*
	 * @deprecated Entity references are always expanded.
	 */
	public void setExpandEntityReferences(boolean expandEntityReferences) {
		this.expandEntityReferences = true;
	}

	/*
	 * @deprecated Entity references are always expanded.
	 */
	public boolean getExpandEntityReferences() {
		return expandEntityReferences;
	}

	public Vector getDTDList() {
		return dtdList;
	}

	public Hashtable getElementPool() {
		return elementPool;
	}

	public EntityPool getEntityPool() {
		return entityPool;
	}

	public List getErrorMessages() {
		return errorMessages;
	}

	//
	// DocumentHandler methods
	//

	public void startDocument() {
	}

	public void endDocument() {
	}

	public void processingInstruction(String target, String data) {
	}

	//
	// DTDHandler methods
	//

	public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
	}

	public void notationDecl(String name, String publicId, String systemId) throws SAXException {
		startDeclaration(DeclNode.NOTATION);
		//declString = "<!NOTATION"; //$NON-NLS-1$
		if (entityDepth == 1 || parsingExternalPEReference) {
			String xs = " "; //$NON-NLS-1$
			xs += name;
			if (publicId == null)
				xs += " SYSTEM " + "\"" + systemId + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			else
				xs += " PUBLIC " + "\"" + publicId + "\" " + "\"" + systemId + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			contentString += xs;
			parseCurrentDeclaration();
		}
	}

	//
	// DeclHandler methods
	//

	public void elementDecl(String name, String model) throws SAXException {
		startDeclaration(DeclNode.ELEMENT);
		//declString = "<!ELEMENT"; //$NON-NLS-1$

		// KB: With early versions of Xerces 2.x, startEntity("[dtd]") used to
		// be called before this method
		// so entityDepth was 1. Using JAXP changed that and startEntity() is
		// not called back anymore.
		// Because of this, we need to handle the case where entityDepth == 0
		// as well.
		if (entityDepth == 0 || entityDepth == 1 || parsingExternalPEReference)
			parseCurrentDeclarationHelper(name + " " + model); //$NON-NLS-1$
	}

	protected String attributeString = ""; //$NON-NLS-1$

	public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
		startDeclaration(DeclNode.ATTLIST);
		//declString = "<!ATTLIST"; //$NON-NLS-1$

		attributeString += " " + aName; //$NON-NLS-1$
		if (type != null)
			attributeString += " " + type; //$NON-NLS-1$
		if (valueDefault != null)
			attributeString += " " + valueDefault; //$NON-NLS-1$
		if (value != null)
			attributeString += " '" + value + "'"; //$NON-NLS-1$ //$NON-NLS-2$

		attributeString = eName + " " + attributeString + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		parseCurrentDeclarationHelper(attributeString);
		attributeString = ""; //$NON-NLS-1$
	}

	public void internalEntityDecl(String name, String value) throws SAXException {
		startDeclaration(DeclNode.INTERNAL_ENTITY);
		//declString = "<!ENTITY"; //$NON-NLS-1$

		if (entityDepth == 1 || parsingExternalPEReference) {
			// build up a string: e.g. " % STRDOM "(#PCDATA)">"
			String xs = " "; //$NON-NLS-1$
			if (name.startsWith("%")) { //$NON-NLS-1$
				xs += "% "; //$NON-NLS-1$
				xs += name.substring(1);
			}
			else
				xs += name;

			xs += " \"" + value + "\">"; //$NON-NLS-1$ //$NON-NLS-2$

			contentString += xs;
			// save expanded entity value
			expandedEntityValue = value;
			parseCurrentDeclaration();

		}
	}

	public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
		startDeclaration(DeclNode.EXTERNAL_ENTITY);
		//declString = "<!ENTITY"; //$NON-NLS-1$

		if (entityDepth == 1 || parsingExternalPEReference) {
			// build up a string:
			// ex1) " % FIELD SYSTEM "oagis_fields.dtd">"
			// ex2) " % FIELD PUBLIC "FOO">"
			String xs = " "; //$NON-NLS-1$
			if (name.startsWith("%")) { //$NON-NLS-1$
				xs += "% "; //$NON-NLS-1$
				xs += name.substring(1);
			}
			else
				xs += name;

			if (systemId != null)
				xs += " SYSTEM \"" + systemId + "\">"; //$NON-NLS-1$ //$NON-NLS-2$
			else if (publicId != null)
				xs += " PUBLIC \"" + publicId + "\">"; //$NON-NLS-1$ //$NON-NLS-2$

			contentString += xs;
			parseCurrentDeclaration();
		}
	}

	//
	// LexicalHandler methods
	//

	public void startCDATA() throws SAXException {
	}

	public void endCDATA() throws SAXException {
	}

	public void startDTD(String name, String publicId, String systemId) throws SAXException {
		dtdList.removeAllElements();
		dtdList.addElement(currentDTD);
	}

	/**
	 * Report the end of DTD declarations.
	 * 
	 * @exception SAXException
	 *                The application may raise an exception.
	 * @see #startDTD
	 */
	public void endDTD() throws SAXException {
		startDeclaration(DeclNode.END_DTD);
	}

	/**
	 * Report the beginning of an entity.
	 * 
	 * The start and end of the document entity are not reported. The start
	 * and end of the external DTD subset are reported using the pseudo-name
	 * "[dtd]". All other events must be properly nested within start/end
	 * entity events.
	 * 
	 * @param name
	 *            The name of the entity. If it is a parameter entity, the
	 *            name will begin with '%'.
	 * @exception SAXException
	 *                The application may raise an exception.
	 * @see #endEntity
	 */

	public void startEntity(String name) throws SAXException {
		if (name.equals("[dtd]")) { //$NON-NLS-1$
			startDeclaration(DeclNode.START_ENTITY_DTD);
		}
		else if (currentDeclNode == null) {
			// PE reference in markupdecl section
			if (name.startsWith("%")) { //$NON-NLS-1$
				String peName = name.substring(1);
				EntityDecl en = entityPool.referPara(peName);

				peRefStack.push(name);
				parsingPERefStack.push(new Boolean(parsingExternalPEReference));

				String systemId = en.getSystemId();
				String publicId = en.getPublicId();
				if (systemId == null) {
					parsingExternalPEReference = false;
				}
				else {
					URIResolver idResolver = URIResolverPlugin.createResolver();
					String uri = idResolver.resolve(currentDTD.getName(), publicId, systemId);
					if (!isDTDLoaded(uri)) {
						// not loaded
						// out.println(" DTD not loaded .. create new DTD: "
						// );
						dtdStack.push(currentDTD);
						DTD newDTD = new DTD(uri);
						dtdList.addElement(newDTD);
						currentDTD = newDTD;
						parsingExternalPEReference = true;
					}
					else {
						// out.println(" DTD already loaded .. " );
						parsingExternalPEReference = false;
					}
				}
			}
		}
		else if (entityDepth == 1 || parsingExternalPEReference) {
			String xs = (String) reader.getProperty("http://xml.org/sax/properties/xml-string"); //$NON-NLS-1$
			contentString += xs;
		}
		entityDepth++;
	}

	/**
	 * Report the end of an entity.
	 * 
	 * @param name
	 *            The name of the entity that is ending.
	 * @exception SAXException
	 *                The application may raise an exception.
	 * @see #startEntity
	 */
	public void endEntity(String name) throws SAXException {
		if (name.equals("[dtd]")) { //$NON-NLS-1$
			startDeclaration(DeclNode.END_ENTITY_DTD);
		}
		else if (!peRefStack.empty()) {
			parseExternalPEReference(name);
		}
		else if (entityDepth == 1 || parsingExternalPEReference) {
			if (!contentString.endsWith(name + ";")) { //$NON-NLS-1$
				String xs = (String) reader.getProperty("http://xml.org/sax/properties/xml-string"); //$NON-NLS-1$
				contentString += xs;
			}
		}
		entityDepth--;
		entityEnd = true;
	}

	protected boolean parseExternalPEReference(String name) {
		if (!peRefStack.empty()) {
			if (((String) peRefStack.peek()).equals(name)) {
				peRefStack.pop();
				if (parsingExternalPEReference) {
					currentDTD = (DTD) dtdStack.pop();
					addPEReferenceNode(name);
				}
				parsingExternalPEReference = ((Boolean) parsingPERefStack.pop()).booleanValue();
				return true;
			}
		}
		return false;
	}

	/**
	 * Report an XML comment anywhere in the document.
	 * 
	 * This callback will be used for comments inside or outside the document
	 * element, including comments in the external DTD subset (if read).
	 * 
	 * @param ch
	 *            An array holding the characters in the comment.
	 * @param start
	 *            The starting position in the array.
	 * @param length
	 *            The number of characters to use from the array.
	 * @exception SAXException
	 *                The application may raise an exception.
	 */
	public void comment(char ch[], int start, int length) throws SAXException {
		startDeclaration(DeclNode.COMMENT);

		if (comment == null)
			comment = new String(ch, start, length);
		else
			comment += "\n" + new String(ch, start, length); // append all comments. //$NON-NLS-1$
		// The comment will get reset after
		// it has been attached to the following
		// declaration.
	}

	//
	// ErrorHandler methods
	//

	/* Warning. */
	public void warning(SAXParseException ex) {
		/*String error = "[Source Line] " + declString + contentString + "\n";  //$NON-NLS-1$//$NON-NLS-2$
		error += "[Warning    ] " + getLocationString(ex) + ": " + ex.getMessage();  //$NON-NLS-1$//$NON-NLS-2$*/
		setErrorInformation(ex, "warning"); //$NON-NLS-1$

		parseCurrentDeclaration();
	}

	/* Error. */
	public void error(SAXParseException ex) {
		/*String error = "[Source Line] " + declString + contentString + "\n";  //$NON-NLS-1$//$NON-NLS-2$
		error += "[Error   ] " + getLocationString(ex) + ": " + ex.getMessage();  //$NON-NLS-1$//$NON-NLS-2$*/
		setErrorInformation(ex, "error"); //$NON-NLS-1$

		parseCurrentDeclaration();
	}

	/* Fatal error. */
	public void fatalError(SAXParseException ex) throws SAXException {
		/*String error = "[Source Line] " + declString + contentString + "\n";  //$NON-NLS-1$//$NON-NLS-2$
		error += "[Fatal Error] " + getLocationString(ex) + ": " + ex.getMessage();  //$NON-NLS-1$//$NON-NLS-2$*/
		setErrorInformation(ex, "fatal"); //$NON-NLS-1$

		parseCurrentDeclaration();
	}

	/* Returns a string of the location. */
	/*private String getLocationString(SAXParseException ex) {
		StringBuffer str = new StringBuffer();

		String systemId = ex.getSystemId();
		if (systemId != null) {
			int index = systemId.lastIndexOf('/');
			if (index != -1)
				systemId = systemId.substring(index + 1);
			str.append(systemId);
		}
		str.append(':');
		str.append(ex.getLineNumber());
		str.append(':');
		str.append(ex.getColumnNumber());

		return str.toString();

	}*/

	private void startDeclaration(int type) {
		if (type == DeclNode.END_DTD || type == DeclNode.END_ENTITY_DTD || type == DeclNode.COMMENT || type == DeclNode.ERROR) {
			previousDeclNode = currentDeclNode;
			currentDeclNode = null;
		}
		else {
			currentDeclNode = new DeclNode(type);
			contentString = ""; //$NON-NLS-1$
			//declString = ""; //$NON-NLS-1$
			expandedEntityValue = null;
		}
	}

	private void parseCurrentDeclaration() {
		parseCurrentDeclarationHelper(contentString);
		contentString = ""; //$NON-NLS-1$
	}

	private void parseCurrentDeclarationHelper(String declarationString) {
		DTDScanner scanner;
		String name;

		if (currentDeclNode == null && previousDeclNode != null && entityEnd) {
			currentDeclNode = previousDeclNode;
		}

		// save the previous declNode
		if (currentDeclNode != null) {
			BaseNode baseNode = null;
			// strip out the ending markup symbol ">"
			if (declarationString.endsWith(">")) //$NON-NLS-1$
				declarationString = declarationString.substring(0, declarationString.length() - 1);

			int sIndex;
			String cString;
			int type = currentDeclNode.getType();
			switch (type) {
				case DeclNode.ELEMENT : {
					name = TString.word(declarationString, 1);

					// strip out the name
					sIndex = declarationString.indexOf(name) + name.length();
					cString = declarationString.substring(sIndex);

					baseNode = new ElementDecl(name, currentDTD.getName());
					scanner = new DTDScanner(currentDTD.getName(), cString);
					CMNode contentModel = scanner.scanContentModel();
					if (contentModel == null && errorMessage == null) {
						createErrorMessage("Expecting '(' in content model", "error"); //$NON-NLS-1$ //$NON-NLS-2$
					}

					((ElementDecl) baseNode).setContentModelNode(contentModel);
					elementPool.put(name, baseNode);
					break;
				}
				case DeclNode.INTERNAL_ENTITY :
				case DeclNode.EXTERNAL_ENTITY : {
					if (!parseExternalPEReference(declarationString)) {
						scanner = new DTDScanner(currentDTD.getName(), declarationString);
						baseNode = scanner.scanEntityDecl();
						// System.out.println("----baseNode (" +
						// declarationString + ")" + baseNode);
						if (type == DeclNode.INTERNAL_ENTITY && expandedEntityValue != null) {
							((EntityDecl) baseNode).expandedValue = expandedEntityValue;
						}
						if (baseNode != null) {
							entityPool.add((EntityDecl) baseNode);
						}
					}

					break;
				}
				case DeclNode.NOTATION : {
					scanner = new DTDScanner(currentDTD.getName(), declarationString);
					baseNode = scanner.scanNotationDecl();
					break;
				}
				case DeclNode.ATTLIST : {
					name = TString.word(declarationString, 1);

					// strip out the name
					sIndex = declarationString.indexOf(name) + name.length();
					cString = declarationString.substring(sIndex);

					baseNode = new Attlist(name, currentDTD.getName());
					scanner = new DTDScanner(currentDTD.getName(), cString.trim());
					Vector attrs = scanner.scanAttlistDecl(entityPool);
					String errorString = scanner.getErrorString();

					if (attrs.size() > 0) {
						((Attlist) baseNode).setAttDefs(attrs);
					}
					if (errorString != null) {
						createErrorMessage(errorString, "error"); //$NON-NLS-1$
					}
					break;
				}
				default : {
					currentDeclNode = null;
				}
			}

			if (baseNode != null) {
				if (comment != null) {
					baseNode.setComment(comment);
					comment = null;
				}

				if (errorMessage != null) {
					baseNode.setErrorMessage(errorMessage);
					errorMessage = null;
				}

				if (currentDeclNode != null) {
					currentDTD.addDecl(baseNode);
				}
				lastBaseNode = baseNode;
			}

			currentDeclNode = null;
		}
		else {
			if (lastBaseNode != null && errorMessage != null && lastBaseNode.getErrorMessage() == null) {
				lastBaseNode.setErrorMessage(errorMessage);
				errorMessage = null;
			}
		}
	}

	private void addPEReferenceNode(String name) {
		if (name.startsWith("%")) { //$NON-NLS-1$
			name = name.substring(1); // strip out the % from entity name
		}
		EntityDecl e = new EntityDecl(name, currentDTD.getName(), null, true);
		e.setEntityReferenced(true);
		e.setErrorMessage(errorMessage);
		errorMessage = null;
		currentDTD.addDecl(e);
	}

	private boolean isDTDLoaded(String uri) {
		boolean loaded = false;
		//String list = ""; //$NON-NLS-1$
		if (dtdList != null) {
			Enumeration en = dtdList.elements();
			while (en.hasMoreElements()) {
				DTD dtd = (DTD) en.nextElement();
				//list += dtd.getName();
				//list += ","; //$NON-NLS-1$
				if (dtd.getName().equals(uri)) {
					loaded = true;
					break;
				}
			}
		}
		return loaded;
	}

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		InputSource result = null;
		URIResolver idResolver = URIResolverPlugin.createResolver();
		String logicalURI = idResolver.resolve(currentDTD.getName(), publicId, systemId);
        // bug 113537, ensure physical resolution gets done here
		// right before we attempt to open a stream
		String physicalURI = idResolver.resolvePhysicalLocation(currentDTD.getName(), publicId, logicalURI);
		result = new InputSource(logicalURI);
		if(!(physicalURI == null || physicalURI.equals("") || URIHelper.hasProtocol(physicalURI))) //$NON-NLS-1$
		{
		  physicalURI = "file:///" + physicalURI; //$NON-NLS-1$
		}
		result.setByteStream(new LazyURLInputStream(physicalURI));
		return result;
	}

	//
	// Main
	//

	/** Main program entry point. */
	public static void main(String argv[]) {

		// is there anything to do?
		if (argv.length == 0) {
			printUsage();
			System.exit(1);
		}

		// vars
		boolean canonical = false;
		boolean expandEntityReferences = false;
		DTDParser parser;

		// check parameters
		for (int i = 0; i < argv.length; i++) {
			String arg = argv[i];

			// options
			if (arg.startsWith("-")) { //$NON-NLS-1$

				if (arg.equals("-c")) { //$NON-NLS-1$
					canonical = true;
					continue;
				}

				if (arg.equals("-x")) { //$NON-NLS-1$
					expandEntityReferences = true;
					continue;
				}

				if (arg.equals("-h")) { //$NON-NLS-1$
					printUsage();
					System.exit(1);
				}
			}
			try {
				parser = new DTDParser(canonical);
				parser.setExpandEntityReferences(expandEntityReferences);
				parser.parse(arg);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	} // main(String[])

	/** Prints the usage. */
	private static void printUsage() {

		System.err.println("usage: java sax.DTDParser (options) uri ..."); //$NON-NLS-1$
		System.err.println();
		System.err.println("options:"); //$NON-NLS-1$
		System.err.println("  -c       Canonical XML output."); //$NON-NLS-1$
		System.err.println("  -x       Expand entity references."); //$NON-NLS-1$
		System.err.println("  -h       This help screen."); //$NON-NLS-1$

	} // printUsage()

	private void setErrorInformation(SAXParseException ex, String severity) {
		errorMessage = new ErrorMessage();

		if (ex.getLineNumber() == -1) {
			errorMessage.setErrorLine(lineNumber);
		}
		else {
			errorMessage.setErrorLine(ex.getLineNumber());
		}


		errorMessage.setErrorMessage(ex.getMessage());
		errorMessage.setErrorColumn(ex.getColumnNumber());
		errorMessage.setSeverity(severity);
		errorMessages.add(errorMessage);
	}

	private void createErrorMessage(String message, String severity) {
		errorMessage = new ErrorMessage();

		Locator locator = getLocator();

		errorMessage.setErrorLine(locator.getLineNumber());
		errorMessage.setErrorColumn(locator.getColumnNumber());

		errorMessage.setErrorMessage(message);
		errorMessage.setSeverity(severity);

		errorMessages.add(errorMessage);
	}

}
