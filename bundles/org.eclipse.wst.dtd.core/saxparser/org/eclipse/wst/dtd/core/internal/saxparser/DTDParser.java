/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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

import org.eclipse.wst.xml.uriresolver.util.IdResolver;
import org.eclipse.wst.xml.uriresolver.util.IdResolverImpl;
import org.eclipse.wst.xml.uriresolver.util.URIHelper;
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
	private String contentString = "";
	private String declString = "";

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
			encoding = "UTF-8";

		this.canonical = canonical;
		try {
			SAXParser sparser = SAXParserFactory.newInstance().newSAXParser();
			reader = sparser.getXMLReader();
			reader.setProperty("http://xml.org/sax/properties/declaration-handler", this);
			reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
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
	public void parse(String uri) {
		try {
			boolean isReadable = URIHelper.isReadableURI(uri, true);
			if (!isReadable) {
				throw new Exception("DTD parse error. Can not read the specified URI : " + uri);
			}

			String document = "<!DOCTYPE root SYSTEM \"" + uri + "\"><root/>";
			entityDepth = 0;
			currentDTD = new DTD(uri);
			reader.parse(new InputSource(new StringReader(document)));
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
		declString = "<!NOTATION";
		if (entityDepth == 1 || parsingExternalPEReference) {
			String xs = " ";
			xs += name;
			if (publicId == null)
				xs += " SYSTEM " + "\"" + systemId + "\"";
			else
				xs += " PUBLIC " + "\"" + publicId + "\" " + "\"" + systemId + "\"";
			contentString += xs;
			parseCurrentDeclaration();
		}
	}

	//
	// DeclHandler methods
	//

	public void elementDecl(String name, String model) throws SAXException {
		startDeclaration(DeclNode.ELEMENT);
		declString = "<!ELEMENT";

		// KB: With early versions of Xerces 2.x, startEntity("[dtd]") used to
		// be called before this method
		// so entityDepth was 1. Using JAXP changed that and startEntity() is
		// not called back anymore.
		// Because of this, we need to handle the case where entityDepth == 0
		// as well.
		if (entityDepth == 0 || entityDepth == 1 || parsingExternalPEReference)
			parseCurrentDeclarationHelper(name + " " + model);
	}

	protected String attributeString = "";

	public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
		startDeclaration(DeclNode.ATTLIST);
		declString = "<!ATTLIST";

		attributeString += " " + aName;
		if (type != null)
			attributeString += " " + type;
		if (valueDefault != null)
			attributeString += " " + valueDefault;
		if (value != null)
			attributeString += " '" + value + "'";

		attributeString = eName + " " + attributeString + ">";
		parseCurrentDeclarationHelper(attributeString);
		attributeString = "";
	}

	public void internalEntityDecl(String name, String value) throws SAXException {
		startDeclaration(DeclNode.INTERNAL_ENTITY);
		declString = "<!ENTITY";

		boolean isPEDecl = false;
		String localName = name;
		if (entityDepth == 1 || parsingExternalPEReference) {
			// build up a string: e.g. " % STRDOM "(#PCDATA)">"
			String xs = " ";
			if (name.startsWith("%")) {
				isPEDecl = true;
				localName = name.substring(1);
				xs += "% ";
				xs += name.substring(1);
			}
			else
				xs += name;

			xs += " \"" + value + "\">";

			contentString += xs;
			// save expanded entity value
			expandedEntityValue = value;
			parseCurrentDeclaration();

		}
	}

	public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
		startDeclaration(DeclNode.EXTERNAL_ENTITY);
		declString = "<!ENTITY";

		if (entityDepth == 1 || parsingExternalPEReference) {
			// build up a string:
			// ex1) " % FIELD SYSTEM "oagis_fields.dtd">"
			// ex2) " % FIELD PUBLIC "FOO">"
			String xs = " ";
			if (name.startsWith("%")) {
				xs += "% ";
				xs += name.substring(1);
			}
			else
				xs += name;

			if (systemId != null)
				xs += " SYSTEM \"" + systemId + "\">";
			else if (publicId != null)
				xs += " PUBLIC \"" + publicId + "\">";

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
		if (name.equals("[dtd]")) {
			startDeclaration(DeclNode.START_ENTITY_DTD);
		}
		else if (currentDeclNode == null) {
			// PE reference in markupdecl section
			if (name.startsWith("%")) {
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
					IdResolver idResolver = new IdResolverImpl(currentDTD.getName());
					String uri = idResolver.resolveId(publicId, systemId);
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
			String xs = (String) reader.getProperty("http://xml.org/sax/properties/xml-string");
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
		if (name.equals("[dtd]")) {
			startDeclaration(DeclNode.END_ENTITY_DTD);
		}
		else if (!peRefStack.empty()) {
			parseExternalPEReference(name);
		}
		else if (entityDepth == 1 || parsingExternalPEReference) {
			if (!contentString.endsWith(name + ";")) {
				String xs = (String) reader.getProperty("http://xml.org/sax/properties/xml-string");
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
			comment = new String(ch);
		else
			comment += "\n" + new String(ch); // append all comments.
		// The comment will get reset after
		// it has been attached to the following
		// declaration.
	}

	//
	// ErrorHandler methods
	//

	/* Warning. */
	public void warning(SAXParseException ex) {
		String error = "[Source Line] " + declString + contentString + "\n";
		error += "[Warning    ] " + getLocationString(ex) + ": " + ex.getMessage();
		setErrorInformation(ex, "warning");

		parseCurrentDeclaration();
	}

	/* Error. */
	public void error(SAXParseException ex) {
		String error = "[Source Line] " + declString + contentString + "\n";
		error += "[Error   ] " + getLocationString(ex) + ": " + ex.getMessage();
		setErrorInformation(ex, "error");

		parseCurrentDeclaration();
	}

	/* Fatal error. */
	public void fatalError(SAXParseException ex) throws SAXException {
		String error = "[Source Line] " + declString + contentString + "\n";
		error += "[Fatal Error] " + getLocationString(ex) + ": " + ex.getMessage();
		setErrorInformation(ex, "fatal");

		parseCurrentDeclaration();
	}

	/* Returns a string of the location. */
	private String getLocationString(SAXParseException ex) {
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

	}

	private void startDeclaration(int type) {
		if (type == DeclNode.END_DTD || type == DeclNode.END_ENTITY_DTD || type == DeclNode.COMMENT || type == DeclNode.ERROR) {
			previousDeclNode = currentDeclNode;
			currentDeclNode = null;
		}
		else {
			currentDeclNode = new DeclNode(type);
			contentString = "";
			declString = "";
			expandedEntityValue = null;
		}
	}

	private void parseCurrentDeclaration() {
		parseCurrentDeclarationHelper(contentString);
		contentString = "";
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
			if (declarationString.endsWith(">"))
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
						createErrorMessage("Expecting '(' in content model", "error");
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
						createErrorMessage(errorString, "error");
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
		if (name.startsWith("%")) {
			name = name.substring(1); // strip out the % from entity name
		}
		EntityDecl e = new EntityDecl(name, currentDTD.getName(), null, true);
		e.setEntityReferenced(true);
		e.setErrorMessage(errorMessage);
		errorMessage = null;
		currentDTD.addDecl(e);
	}

	void printBaseNodes() {
		Enumeration dtds = dtdList.elements();
		while (dtds.hasMoreElements()) {
			DTD dtd = (DTD) dtds.nextElement();
			Enumeration en = dtd.externalElements();

			while (en.hasMoreElements()) {
				BaseNode n = (BaseNode) en.nextElement();
			}
		}
	}

	private boolean isDTDLoaded(String uri) {
		boolean loaded = false;
		String list = "";
		if (dtdList != null) {
			Enumeration en = dtdList.elements();
			while (en.hasMoreElements()) {
				DTD dtd = (DTD) en.nextElement();
				list += dtd.getName();
				list += ",";
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
		IdResolver idResolver = new IdResolverImpl(currentDTD.getName());
		String uri = idResolver.resolveId(publicId, systemId);
		result = new InputSource(uri);
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
			if (arg.startsWith("-")) {

				if (arg.equals("-c")) {
					canonical = true;
					continue;
				}

				if (arg.equals("-x")) {
					expandEntityReferences = true;
					continue;
				}

				if (arg.equals("-h")) {
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

		System.err.println("usage: java sax.DTDParser (options) uri ...");
		System.err.println();
		System.err.println("options:");
		System.err.println("  -c       Canonical XML output.");
		System.err.println("  -x       Expand entity references.");
		System.err.println("  -h       This help screen.");

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
