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

import java.io.Reader;
import java.util.List;
import java.util.Vector;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * A helper class for the XML validator.
 * 
 * @author Craig Salter, IBM
 * @author Lawrence Mandel, IBM
 */
public class ValidatorHelper
{                           
  public static final String copyright = "(c) Copyright IBM Corporation 2002.";
  public List namespaceURIList = new Vector();
  public boolean isGrammarEncountered = false;    
  public boolean isDTDEncountered = false;
  public boolean isNamespaceEncountered = false;
  public String schemaLocationString = "";
  public int numDTDElements = 0;

  public static final boolean IS_LINUX = java.io.File.separator.equals("/");

  /**
   * Constructor.
   */
  public ValidatorHelper()
  {
  }
 
  /**
   * Create an XML Reader.
   * 
   * @return An XML Reader if one can be created or null.
   * @throws Exception
   */
  protected XMLReader createXMLReader() throws Exception
  {     
    XMLReader reader = null;
    ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
    try
    {
      Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
      reader = new org.apache.xerces.parsers.SAXParser();     

      reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
      reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      reader.setFeature("http://xml.org/sax/features/namespaces", false);
      reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      reader.setContentHandler(new MyContentHandler());
      reader.setErrorHandler(new InternalErrorHandler()); 

      LexicalHandler lexicalHandler = new LexicalHandler()
      {      
        public void startDTD (String name, String publicId, String systemId)
        {
          isGrammarEncountered = true;   
          isDTDEncountered = true;
        }

        public void endDTD() throws SAXException
        {
        }

        public void startEntity(String name) throws SAXException
        {
        }

        public void endEntity(String name) throws SAXException
        {
        }

        public void startCDATA() throws SAXException
        {
        }
	
        public void endCDATA() throws SAXException
        {
        }
 
        public void comment (char ch[], int start, int length) throws SAXException
        {
        }
      };
      reader.setProperty("http://xml.org/sax/properties/lexical-handler", lexicalHandler);
    }
    finally
    {
      Thread.currentThread().setContextClassLoader(prevClassLoader);
    }
    return reader;
  }  

  /**
   * An error handler to suppress error and warning information.
   */
  private class InternalErrorHandler implements org.xml.sax.ErrorHandler
  {
    public void error(SAXParseException exception) throws SAXException
    {
    }

    public void fatalError(SAXParseException exception) throws SAXException
    {
    }

    public void warning(SAXParseException exception) throws SAXException
    {
    }
  }

 
  /**
   * Figures out the information needed for validation.
   * 
   * @param uri The uri of the file to validate.
   * @param uriResolver A helper to resolve locations.
   */
  public void computeValidationInformation(String uri, Reader characterStream, URIResolver uriResolver)
  {
    try
    {
      XMLReader reader = createXMLReader();  
      InputSource inputSource = new InputSource(uri);
      inputSource.setCharacterStream(characterStream);
      reader.parse(inputSource);
    }
    catch (Exception e)
    {     
      //System.out.println(e);
    }
  }
  
 

  /**
   * Handle the content while parsing the file.
   */
  class MyContentHandler extends org.xml.sax.helpers.DefaultHandler
  {      
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    boolean isRootElement = true;
    
    public void error(SAXParseException e) throws SAXException
    {
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException
    {
    }
    /* (non-Javadoc)
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) throws SAXException
    {
    }
    public String getPrefix(String name)
    {
      String prefix = null;
      int index = name.indexOf(":");
      if (index != -1)
      {
        prefix = name.substring(0, index);
      }
      return prefix;
    }    
        
    public String getUnprefixedName(String name)
    {
      int index = name.indexOf(":");
      if (index != -1)
      {
        name = name.substring(index + 1);
      }
      return name;
    }
    
    public String getPrefixedName(String prefix, String localName)
    {
      return prefix != null && prefix.length() > 0 ? prefix + ":" + localName : localName;     
    }

    public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)
    {      
      //String explicitLocation = null;
      if (isRootElement)
      {  
        
        isRootElement = false;  
        int nAtts = atts.getLength();    
        String schemaInstancePrefix = null;
        for (int i =0; i < nAtts; i++)
        {              
          String attributeName = atts.getQName(i);       
          if (attributeName.equals("xmlns") || attributeName.startsWith("xmlns:"))
          {                                         
            isNamespaceEncountered = true;    
            String value = atts.getValue(i);                 
            if (value.startsWith("http://www.w3.org/") && value.endsWith("/XMLSchema-instance"))
            {
              schemaInstancePrefix = attributeName.equals("xmlns") ? "" : getUnprefixedName(attributeName);
            }                   
          }                 
        }
        
        String prefix = getPrefix(rawName);
        String rootElementNamespaceDeclarationName = (prefix != null && prefix.length() > 0) ? "xmlns:" + prefix : "xmlns";
        String rootElementNamespace = rootElementNamespaceDeclarationName != null ? atts.getValue(rootElementNamespaceDeclarationName) : null;        
        
        String location = null;
        
        // first we use any 'xsi:schemaLocation' or 'xsi:noNamespaceSchemaLocation' attribute
        // to determine a location
        if (schemaInstancePrefix != null)
        {                     
          location = atts.getValue(getPrefixedName(schemaInstancePrefix, "noNamespaceSchemaLocation"));
          if (location == null)
          {
            location = atts.getValue(getPrefixedName(schemaInstancePrefix, "schemaLocation"));  
          }
        }  
        if (location == null && rootElementNamespace != null)
        {
          location = URIResolverPlugin.createResolver().resolve(null, rootElementNamespace, null);                                          
        }           
        
        if (location != null)
        {  
          isGrammarEncountered = true;
        }        
      }
    }     
    /* (non-Javadoc)
     * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
     */
    public void elementDecl(String name, String model) 
    {
      numDTDElements++;
    }
  }   
       
  
  /**
   * Replace all instances in the string of the old pattern with the new pattern.
   * 
   * @param string The string to replace the patterns in.
   * @param oldPattern The old pattern to replace.
   * @param newPattern The pattern used for replacement.
   * @return The modified string with all occurrances of oldPattern replaced by new Pattern.
   */
  protected static String replace(String string, String oldPattern, String newPattern)
  {     
    int index = 0;
    while (index != -1)
    {
      index = string.indexOf(oldPattern, index);
      if (index != -1)
      {
        string = string.substring(0, index) + newPattern + string.substring(index + oldPattern.length());
        index = index + oldPattern.length();
      }
    }
    return string;
  }
}