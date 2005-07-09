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

package org.eclipse.wst.xml.core.internal.validation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.parsers.StandardParserConfiguration;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.validation.core.logging.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;

/**
 * This class performs validation using a xerces sax parser.  
 * Here's a quick overview of the details : 
 *   - an ErrorHandler is used to collect errors into a list (so they may be displayed by the UI)
 *   - an EntityResolver is used along with the xerces "external-schemaLocation" property to implement XML Catalog support
 */
public class XMLValidator
{
  public static final String copyright = "(c) Copyright IBM Corporation 2002.";
  protected URIResolver uriResolver = null;
  //protected MyEntityResolver entityResolver = null;
  protected Hashtable ingoredErrorKeyTable = new Hashtable();
  
  protected ResourceBundle resourceBundle;

  protected static final String IGNORE_ALWAYS = "IGNORE_ALWAYS";
  protected static final String IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL = "IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL";
  protected static final String PREMATURE_EOF = "PrematureEOF";
  protected static final String ROOT_ELEMENT_TYPE_MUST_MATCH_DOCTYPEDECL = "RootElementTypeMustMatchDoctypedecl";
  protected static final String MSG_ELEMENT_NOT_DECLARED = "MSG_ELEMENT_NOT_DECLARED";
  
  private static final String _UI_PROBLEMS_VALIDATING_FILE_NOT_FOUND = "_UI_PROBLEMS_VALIDATING_FILE_NOT_FOUND";
  private static final String _UI_PROBLEMS_VALIDATING_UNKNOWN_HOST = "_UI_PROBLEMS_VALIDATING_UNKNOWN_HOST";
  
  private static final String FILE_NOT_FOUND_KEY = "FILE_NOT_FOUND";

  /**
   * Constructor.
   */
  public XMLValidator()
  {                          
	resourceBundle = ResourceBundle.getBundle("org.eclipse.wst.xml.core.internal.validation.xmlvalidation");
    // Here we add some error keys that we need to filter out when we're validation 
    // against a DTD without any element declarations.       
    ingoredErrorKeyTable.put(PREMATURE_EOF, IGNORE_ALWAYS);
    ingoredErrorKeyTable.put(ROOT_ELEMENT_TYPE_MUST_MATCH_DOCTYPEDECL, IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL);
    ingoredErrorKeyTable.put(MSG_ELEMENT_NOT_DECLARED, IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL);
  }

  /**
   * Set the URI Resolver to use.
   * 
   * @param uriResolver The URI Resolver to use.
   */
  public void setURIResolver(URIResolver uriResolver)
  {
    this.uriResolver = uriResolver;
    //entityResolver = new MyEntityResolver(uriResolver);
  }

 
  /**
   * Create an XML Reader.
   * 
   * @return The newly created XML reader or null if unsuccessful.
   * @throws Exception
   */
  protected XMLReader createXMLReader(final XMLValidationInfo valinfo, XMLEntityResolver entityResolver) throws Exception
  {     
    XMLReader reader = null;
    // move to Xerces-2... add the contextClassLoader stuff
    ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
    try
    {
      Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
      StandardParserConfiguration configuration = new MyStandardParserConfiguration(valinfo);
      reader = new org.apache.xerces.parsers.SAXParser(configuration)
      {
        public void startDocument(org.apache.xerces.xni.XMLLocator theLocator, java.lang.String encoding, NamespaceContext nscontext, org.apache.xerces.xni.Augmentations augs) 
        {
          valinfo.setXMLLocator(theLocator);
          super.startDocument(theLocator, encoding, nscontext, augs); 
        }
      };

      reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
      reader.setFeature("http://xml.org/sax/features/namespace-prefixes", valinfo.isNamespaceEncountered());
      reader.setFeature("http://xml.org/sax/features/namespaces", valinfo.isNamespaceEncountered());              
      reader.setFeature("http://xml.org/sax/features/validation", valinfo.isGrammarEncountered()); 
      reader.setFeature("http://apache.org/xml/features/validation/schema", valinfo.isGrammarEncountered());
      
      // MH make sure validation works even when a customer entityResolver is note set (i.e. via setURIResolver())
      if (entityResolver != null)
      {  
        reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", entityResolver);
      }  
      reader.setProperty("http://xml.org/sax/properties/declaration-handler", new MyDeclHandler());     
    } 
    catch(Exception e)
    { 
      //TODO: log error message;
      //e.printStackTrace();
    }
    finally
    {
      Thread.currentThread().setContextClassLoader(prevClassLoader);
    }
    return reader;
  }  

  /**
   * Validate the file located at the given URI.
   * 
   * @param uri The URI of the file to validate.
   * @return Returns an XML validation report.
   */
  public XMLValidationReport validate(String uri)
  {
    return validate(uri, null);  
  }

  final String createStringForInputStream(InputStream inputStream)
  {
    // Here we are reading the file and storing to a stringbuffer.
    StringBuffer fileString = new StringBuffer();
    try
    {
      InputStreamReader inputReader = new InputStreamReader(inputStream);
      BufferedReader reader = new BufferedReader(inputReader);
      char[] chars = new char[1024];
      int numberRead = reader.read(chars);
      while (numberRead != -1)
      {
        fileString.append(chars, 0, numberRead);
        numberRead = reader.read(chars);
      }
    }
    catch (Exception e)
    {
      //TODO: log error message
      //e.printStackTrace();
    }
    return fileString.toString();
  }
  /**
   * Validate the inputStream
   * 
   * @param uri The URI of the file to validate.
   * @param the inputStream of the file to validate
   * @return Returns an XML validation report.
   */
  public XMLValidationReport validate(String uri, InputStream inputStream)
  {
    Reader reader1 = null; // Used for the preparse.
    Reader reader2 = null; // Used for validation parse.
    
    if (inputStream != null)
    {  
      String string = createStringForInputStream(inputStream);
      reader1 = new StringReader(string);
      reader2 = new StringReader(string);
    } 
        
    XMLValidationInfo valinfo = new XMLValidationInfo(uri);
    MyEntityResolver entityResolver = new MyEntityResolver(uriResolver); 
    ValidatorHelper helper = new ValidatorHelper(); 
    try
    {  
        helper.computeValidationInformation(uri, reader1, uriResolver);
        valinfo.setDTDEncountered(helper.isDTDEncountered);
        valinfo.setElementDeclarationCount(helper.numDTDElements);
        valinfo.setNamespaceEncountered(helper.isNamespaceEncountered);
        valinfo.setGrammarEncountered(helper.isGrammarEncountered);
        XMLReader reader = createXMLReader(valinfo, entityResolver);
        XMLErrorHandler errorhandler = new XMLErrorHandler(valinfo);
        reader.setErrorHandler(errorhandler);
        
        InputSource inputSource = new InputSource(uri);
        inputSource.setCharacterStream(reader2);
        reader.parse(inputSource);      
    }
    catch (SAXParseException saxParseException)
    {
      // These errors are caught by the error handler.
      //addValidationMessage(valinfo, saxParseException);
    }                 
    catch (IOException ioException)
    {
      addValidationMessage(valinfo, ioException);
    }                 
    catch (Exception exception)
    {  
    	LoggerFactory.getLoggerInstance().logError(exception.getLocalizedMessage(), exception);
    }
     
    
    return valinfo;
       
  }

  /**
   * Add a validation message to the specified list.
   * 
   * @param valinfo The validation info object to add the error to.
   * @param exception The exception that contains the validation information.
   */
  protected void addValidationMessage(XMLValidationInfo valinfo, IOException exception)
  { 
    String messageStr = exception.getMessage();
	Throwable cause = exception.getCause();
	while(messageStr == null && cause != null){
		cause = exception.getCause();
		if(cause != null){
			messageStr = cause.getMessage();
		}
	}
    if (messageStr != null)
    {
      if (exception instanceof FileNotFoundException)
      {
        messageStr = MessageFormat.format(resourceBundle.getString(_UI_PROBLEMS_VALIDATING_FILE_NOT_FOUND), new Object [] { messageStr });
      }
      else if (exception instanceof UnknownHostException)
      {
    	messageStr = MessageFormat.format(resourceBundle.getString(_UI_PROBLEMS_VALIDATING_UNKNOWN_HOST), new Object [] { messageStr });
      }
      XMLLocator locator = valinfo.getXMLLocator();
      valinfo.addError(messageStr, locator != null ? locator.getLineNumber() : 1, locator != null ? locator.getColumnNumber() : 0, valinfo.getFileURI(), FILE_NOT_FOUND_KEY, null);
    }
  }
                                                                    
  /**
   * Add a validation message to the specified list.
   * 
   * @param valinfo The validation info object to add the error to.
   * @param exception The exception that contains the validation information.
   */
  protected void addValidationMessage(XMLValidationInfo valinfo, SAXParseException exception)
  { 
    if (exception.getMessage() != null)
    { 
      valinfo.addError(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId());
    }
  }

  
  /**
   * A custom entity resolver that uses the URI resolver specified to resolve entities.
   */
  protected class MyEntityResolver implements XMLEntityResolver 
  {
    private URIResolver uriResolver;
    
    /**
     * The reported exceptions list allows the entity resolver to only
     * throw an exception the first time a namespace cannot be located.
     * This prevents Xerces from producing a warning on every line of the
     * XML document.
     */
    private List reportedExceptions = new ArrayList();
    
    /**
     * Constructor.
     * 
     * @param uriResolver The URI resolver to use with this entity resolver.
     */
    public MyEntityResolver(URIResolver uriResolver)
    {
      this.uriResolver = uriResolver;
    }
    
    /* (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLEntityResolver#resolveEntity(org.apache.xerces.xni.XMLResourceIdentifier)
     */
    public XMLInputSource resolveEntity(XMLResourceIdentifier rid) throws XNIException, IOException
    {
      XMLInputSource result = null;

      if (uriResolver != null)
      {         
		String id = rid.getPublicId();
		if(id == null){
			id = rid.getNamespace();
		}
        String systemId = uriResolver.resolve(rid.getBaseSystemId(), id, rid.getLiteralSystemId());                         
        result = new XMLInputSource(id, rid.getExpandedSystemId(), rid.getBaseSystemId());   
		if(systemId != null)
		{
		  try
		  {
		    result.setByteStream(new URL(systemId).openStream());
		  }
		  catch(Exception e)
		  {
			  //System.out.println(e);
        if(!reportedExceptions.contains(rid.getExpandedSystemId()))
        {
          reportedExceptions.add(rid.getExpandedSystemId());
          // Throw an exception to indicate that the document could not be read.
		  IOException expt = new IOException();
		  expt.initCause(e);
		  throw expt;
        }
		  }
		}
      }
      return result;
    }
  }
  
  /**
   * An error handler to catch errors encountered while parsing the XML document.
   */
  protected class XMLErrorHandler implements org.xml.sax.ErrorHandler
  {

    private final int ERROR = 0;
    private final int WARNING = 1;
    private XMLValidationInfo valinfo;
    
    /**
     * Constructor.
     * 
     * @param valinfo The XML validation info object that will hold the validation messages.
     */
    public XMLErrorHandler(XMLValidationInfo valinfo)
    {
      this.valinfo = valinfo;
    }

    /**
     * Add a validation message with the given severity.
     * 
     * @param exception The exception that contains the message.
     * @param severity The severity of the message.
     */
    
    protected void addValidationMessage(SAXParseException exception, int severity)
    {
      if(exception.getSystemId() != null)
      {       	
        if(severity == WARNING)
        {
          valinfo.addWarning(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId());
        }
        else
        {
          valinfo.addError(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId(), valinfo.getCurrentErrorKey(), valinfo.getMessageArguments());
        }
      }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception) throws SAXException
    {
      addValidationMessage(exception, ERROR);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception) throws SAXException
    {
      addValidationMessage(exception, ERROR);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception) throws SAXException
    {
      addValidationMessage(exception, WARNING);
    }
  }
                                                                          
  /** 
   * This class is used to count the elementDecls that are encountered in a DTD.
   */
  protected class MyDeclHandler implements DeclHandler 
  {
    
    /**
     * Constructor.
     * 
     * @param valinfo The XMLValidationInfo object that will count the declarations.
     */
    public MyDeclHandler()
    {
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#attributeDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) 
    {
    }                                    

    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
     */
    public void elementDecl(String name, String model) 
    {
      //valinfo.increaseElementDeclarationCount();
    }
  
    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#externalEntityDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void externalEntityDecl(String name, String publicId, String systemId) 
    {
    }      

    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#internalEntityDecl(java.lang.String, java.lang.String)
     */
    public void internalEntityDecl(String name, String value) 
    {
    }
  }

  /**
   * A StandardParserConfiguration that creates an error reporter which can ignore
   * DTD error messages for DTD's with no elements defined.
   */

  protected class MyStandardParserConfiguration extends StandardParserConfiguration
  {
  	XMLValidationInfo valinfo = null;
  	
  	/**
  	 * Constructor.
  	 * 
  	 * @param valinfo The XMLValidationInfo object to use.
  	 */
  	public MyStandardParserConfiguration(XMLValidationInfo valinfo)
  	{
  	  this.valinfo = valinfo;
  	}

    /* (non-Javadoc)
     * @see org.apache.xerces.parsers.DTDConfiguration#createErrorReporter()
     */
    protected XMLErrorReporter createErrorReporter() 
    {
    	return new XMLErrorReporter()
		{
    		/* (non-Javadoc)
    		 * @see org.apache.xerces.impl.XMLErrorReporter#reportError(java.lang.String, java.lang.String, java.lang.Object[], short)
    		 */
    		public void reportError(String domain, String key, Object[] arguments, short severity) throws XNIException 
    	    {                    
		      boolean reportError = true;
              valinfo.setCurrentErrorKey(key);  
			  valinfo.setMessageArguments(arguments);
		      String ignoreCondition = (String)ingoredErrorKeyTable.get(key);
		      if (ignoreCondition != null)
		      {
		        if (ignoreCondition.equals(XMLValidator.IGNORE_IF_DTD_WITHOUT_ELEMENT_DECL))
		        {                    
		          boolean isDTDWithoutElementDeclarationEncountered = valinfo.isDTDWithoutElementDeclarationEncountered(); 
		          reportError = !isDTDWithoutElementDeclarationEncountered;  
		        }
		        else 
		        {
		          reportError = false;
		        }
		      }
		                
		      if (reportError)
		      {
		        super.reportError(domain, key, arguments, severity);
		      }
		    }
		};
    }
  }
}
