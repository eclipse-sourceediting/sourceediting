/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;



import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.wst.css.core.internal.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


class XMLReaderUtil {

	static public XMLReader createXMLReader(String className) throws SAXException {
		try {
			return SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		}
		catch (ParserConfigurationException e) {
			Logger.logException(e);
		}
		catch (FactoryConfigurationError e) {
			Logger.logException(e);
		}
		return null;
	}
}
