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
package org.eclipse.wst.dtd.core.internal.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;

import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * TODO: Kihup and Nitin, reevaluate the use and implementation of this class
 */

// import org.apache.xerces.parsers.SAXParser;
// import org.apache.xerces.xni.Augmentations;
// import org.apache.xerces.xni.XMLLocator;
// import org.apache.xerces.xni.XMLString;
// import org.apache.xerces.xni.XNIException;
public class DTDSAXParser extends SAXParser {

	private List ignoredEntityRefs = new ArrayList();

	// private XMLLocator locator;
	//
	public List getIgnoredEntityRefs() {
		return ignoredEntityRefs;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.parsers.SAXParser#getParser()
	 */
	public Parser getParser() throws SAXException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.parsers.SAXParser#getProperty(java.lang.String)
	 */
	public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.parsers.SAXParser#getXMLReader()
	 */
	public XMLReader getXMLReader() throws SAXException {
		// TODO Auto-generated method stub
		return null;
	}

	//
	// public XMLLocator getLocator()
	// {
	// return locator;
	// }
	//
	// public void startDocument
	// (XMLLocator locator,
	// String encoding,
	// Augmentations augs)
	// {
	// this.locator = locator;
	// super.startDocument(locator,encoding,augs);
	// }
	//
	// public void ignoredCharacters(XMLString text, Augmentations augs)
	// throws XNIException
	// {
	// String s =
	// text.length > 0 ? new String(text.ch,text.offset,text.length) : "";
	// //System.out.println("ignoredCharacters: " + s);
	//
	// StringTokenizer tokenizer = new StringTokenizer(s,";");
	// try
	// {
	// String token = null;
	// while (tokenizer.hasMoreTokens())
	// {
	// token = tokenizer.nextToken();
	// if (isEntityRef(token))
	// registerEntityRef(token);
	// }
	// }
	// catch (NoSuchElementException e)
	// {
	// e.printStackTrace();
	// }
	// }
	//
	// TODO: never used
	boolean isEntityRef(String token) {
		// Looking for the pattern "nnnn%nnnnn".
		if (token.indexOf('%') != -1)
			return true; // candidate for entity reference
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.parsers.SAXParser#isNamespaceAware()
	 */
	public boolean isNamespaceAware() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.parsers.SAXParser#isValidating()
	 */
	public boolean isValidating() {
		// TODO Auto-generated method stub
		return false;
	}

	// TODO: never used
	void registerEntityRef(String token) {
		int index = token.lastIndexOf('%');
		if (index == -1)
			return;

		String refName = token.substring(index, token.length());
		// System.out.println("entity ref name is: " + refName);
		if (refName.indexOf(' ') != -1 || refName.indexOf('\t') != -1 || refName.indexOf('\n') != -1)
			return;
		else
			// we found entity reference
			ignoredEntityRefs.add(refName + ";"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.parsers.SAXParser#setProperty(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
		// TODO Auto-generated method stub

	}

}
