/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.css.core.internal.metamodelimpl;



import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import org.eclipse.wst.css.core.internal.Logger;
import org.eclipse.wst.css.core.internal.metamodel.CSSMetaModel;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;



class ProfileLoader {


	/**
	 * Constructor for ProfileLoader.
	 */
	private ProfileLoader() {
		super();
	}

	ProfileLoader(ResourceBundle resourceBundle, boolean logging) {
		this();
		fResourceBundle = resourceBundle;
		fLogging = logging;
	}

	static void loadProfile(CSSMetaModelImpl metamodel, InputStream input, ResourceBundle resourceBundle, boolean logging) {
		try {
			XMLReader xmlReader = XMLReaderUtil.createXMLReader(PARSER_NAME);
			// XMLReaderFactory.createXMLReader(PARSER_NAME);
			xmlReader.setContentHandler(new ProfileHandler(metamodel, resourceBundle, logging));
			xmlReader.parse(new InputSource(input));
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		catch (SAXException e) {
			Logger.logException(e);
		}
	}

	CSSMetaModel loadProfile(InputStream input) {
		if (fMetaModel == null) {
			fMetaModel = new CSSMetaModelImpl();
		}
		loadProfile(fMetaModel, input, fResourceBundle, fLogging);
		fMetaModel.loadCompleted();
		return fMetaModel;
	}


	CSSMetaModelImpl fMetaModel = null;
	private final static String PARSER_NAME = "org.apache.xerces.parsers.SAXParser"; //$NON-NLS-1$
	private ResourceBundle fResourceBundle = null;
	private boolean fLogging = false;
}
