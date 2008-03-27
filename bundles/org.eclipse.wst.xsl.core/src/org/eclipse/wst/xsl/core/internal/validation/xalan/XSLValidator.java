/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.core.internal.validation.xalan;


import java.io.File;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.apache.xml.utils.DefaultErrorHandler;

/**
 * TODO: Add Javadoc
 * 
 * @author Doug Satchwell
 * 
 */
public class XSLValidator {
	private static XSLValidator instance;

	private static final byte[] XPATH_LOCK = new byte[0];

	private XSLValidator() {
	}

	/**
	 * TODO: Add Javadoc
	 * 
	 * @param uri
	 * @param xslFile
	 * @return
	 * @throws CoreException
	 */
	public ValidationReport validate(String uri, IFile xslFile) throws CoreException {
		ErrorListener errorListener = new XSLValidationReport();

		synchronized (XPATH_LOCK) {
			XalanTransformerFactoryImpl transformer = new XalanTransformerFactoryImpl();
			transformer.setErrorListener(errorListener);

		   try {
	 	     if (transformer.getFeature(SAXSource.FEATURE))
			    {
			      // If so, we can safely cast.
			      SAXTransformerFactory saxfactory = ((SAXTransformerFactory) transformer);
			      // Create factory for SAX parser
			      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			      saxParserFactory.setNamespaceAware(true);
			      
			      // Get a SAX parser
			      XMLReader xmlparser = saxParserFactory.newSAXParser().getXMLReader();
			      xmlparser.setErrorHandler(new DefaultErrorHandler());
			      Templates compiled = saxfactory.newTemplates(new SAXSource(xmlparser, new InputSource(uri)));
			    }
		   } catch (Exception ex) {
			   XSLCorePlugin.log(ex);
		   }
			
		}
		return (ValidationReport)errorListener;
	}

	
	/**
	 * TODO: Add Javadoc
	 * 
	 * @return
	 */
	public static XSLValidator getInstance() {
		if (instance == null)
			instance = new XSLValidator();
		return instance;
	}
}
