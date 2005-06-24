package org.eclipse.wst.xml.uriresolver.validation.tests.internal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.util.XMLCatalogResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Validator {
	
	public boolean validationError = false;
	
	public void validateWithSchema_JAXP(String xmlFile, String[] catalogs) {
		try {
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
	
			// Create catalog resolver and set a catalog list.
			XMLCatalogResolver resolver = new XMLCatalogResolver();
			resolver.setPreferPublic(true);
			resolver.setCatalogList(catalogs);
			factory.setAttribute(
					  "http://apache.org/xml/properties/internal/entity-resolver", 
					  resolver);

			DocumentBuilder builder = factory.newDocumentBuilder();
			ErrorHandler handler = new ErrorHandler(this);
			builder.setErrorHandler(handler);
			builder.setEntityResolver(resolver);
			builder.parse(xmlFile);
			
			if (validationError == true)
				System.out.println("XML Document has Error: "
						+ handler.saxParseException.getMessage());
			else
				System.out.println("XML Document is valid");
		} catch (java.io.IOException ioe) {
			System.out.println("IOException " + ioe.getMessage());
		} catch (SAXException e) {
			System.out.println("SAXException " + e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println("ParserConfigurationException                    "
							+ e.getMessage());
		}
		
	}

	
	public void validateWithSchema_XercesSAXParser(String XmlDocumentUrl, String [] catalogs) {
		SAXParser parser = new SAXParser();
		try {
			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature(
					"http://apache.org/xml/features/validation/schema", true);
			
			//	Create catalog resolver and set a catalog list.
			XMLCatalogResolver resolver = new XMLCatalogResolver();
			resolver.setUseLiteralSystemId(true);
			resolver.setPreferPublic(true);
			resolver.setCatalogList(catalogs);
			
			//	Set the resolver on the parser.
			parser.setProperty("http://apache.org/xml/properties/internal/entity-resolver", 
					  resolver);

			ErrorHandler handler = new ErrorHandler(this);
			parser.setErrorHandler(handler);
			
			parser.parse(XmlDocumentUrl);
			if (validationError == true){
				System.out.println("XML Document has Error: "
						+ handler.saxParseException.getMessage());
				throw  handler.saxParseException;
			}
			else{
				System.out.println("XML Document is valid");
			}
				
		} catch (java.io.IOException ioe) {
			System.out.println("IOException " + ioe.getMessage());
		} catch (SAXException e) {
			System.out.println("SAXException " + e.getMessage());
		}
	}
	
	public void validateWithSchema_XercesDOMParser(String XmlDocumentUrl, String [] catalogs) {
		DOMParser parser = new DOMParser();
		try {
			parser.setFeature("http://xml.org/sax/features/validation", true);
			parser.setFeature(
					"http://apache.org/xml/features/validation/schema", true);
			
			//	Create catalog resolver and set a catalog list.
			XMLCatalogResolver resolver = new XMLCatalogResolver();
			resolver.setUseLiteralSystemId(true);
			resolver.setPreferPublic(true);
			resolver.setCatalogList(catalogs);
			
			//	Set the resolver on the parser.
			parser.setProperty("http://apache.org/xml/properties/internal/entity-resolver", 
					  resolver);

			ErrorHandler handler = new ErrorHandler(this);
			parser.setErrorHandler(handler);
			
			parser.parse(XmlDocumentUrl);
			if (validationError == true){
				System.out.println("XML Document has Error: "
						+ handler.saxParseException.getMessage());
				throw  handler.saxParseException;
			}
			else{
				System.out.println("XML Document is valid");
			}
				
		} catch (java.io.IOException ioe) {
			System.out.println("IOException " + ioe.getMessage());
		} catch (SAXException e) {
			System.out.println("SAXException " + e.getMessage());
		}
	}

	private class ErrorHandler extends DefaultHandler {

		Validator validator;

		public SAXParseException saxParseException = null;

		public ErrorHandler(Validator validator) {
			super();
			this.validator = validator;
		}

		public void error(SAXParseException exception) throws SAXException {
			validationError = true;
			saxParseException = exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			validationError = true;
			saxParseException = exception;
		}

		public void warning(SAXParseException exception) throws SAXException {
		}
	}

	
}