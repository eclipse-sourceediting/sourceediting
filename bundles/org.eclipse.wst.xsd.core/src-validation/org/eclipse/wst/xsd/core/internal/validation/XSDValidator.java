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
package org.eclipse.wst.xsd.core.internal.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.xml.core.internal.validation.XMLValidator;
import org.eclipse.wst.xml.core.internal.validation.core.LazyURLInputStream;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * The XSDValidator will validate XSD files.
 * 
 * @author Lawrence Mandel, IBM
 */
public class XSDValidator
{

  private final boolean WRAPPER_ERROR_SUPPORT_ENABLED = true;

  private final String XML_INSTANCE_DOC_TOP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<root \n" 
  + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n";

  private final String XML_INSTANCE_DOC_MID = " xsi:noNamespaceSchemaLocation=\"";

  private final String XML_INSTANCE_DOC_BOT = "\">\n" + "</root>\n";

  private final String DUMMY_URI = "http://example.org/dummy";
  
  private URIResolver uriresolver = null;

  public ValidationReport validate(String uri)
  {
    return validate(uri, null);
  }

  /**
   * Validate the XSD file specified by the URI.
   * 
   * @param uri
   *          The URI of the XSD file to validate.
   */
  public ValidationReport validate(String uri, InputStream inputStream)
  {
    ValidationInfo valinfo = new ValidationInfo(uri);
    try
    {
      String ns = null;
      String schemaLocationString = "";

      URL url = new URL(uri);
      if (url != null)
      {
        BufferedReader bufreader = new BufferedReader(new InputStreamReader(url.openStream()));

        if (bufreader != null)
        {
          StringBuffer source = new StringBuffer();
          while (bufreader.ready())
          {
            source.append(bufreader.readLine());
          }
          bufreader.close();
          int tn = source.indexOf("targetNamespace");
          if (tn != -1)
          {
            int firstquote = source.indexOf("\"", tn) + 1;
            int secondquote = source.indexOf("\"", firstquote);
            ns = source.substring(firstquote, secondquote);
          }
        }
        bufreader.close();
      }

      XSDErrorHandler errorHandler = new XSDErrorHandler(valinfo);
      try
      {
        StringBuffer instanceDoc = new StringBuffer(XML_INSTANCE_DOC_TOP);
        if (ns != null && !ns.equals(""))
        {
          instanceDoc.append(" xmlns=\"").append(ns).append("\"\n");
          instanceDoc.append(" xsi:schemaLocation=\"");
          instanceDoc.append(ns);
          instanceDoc.append(" ");
        } else
        {
          instanceDoc.append(XML_INSTANCE_DOC_MID);
        }
        instanceDoc.append(uri.replaceAll(" ", "%20"));
        if (!schemaLocationString.equals(""))
        {
          instanceDoc.append(" ").append(schemaLocationString);
        }
        instanceDoc.append(XML_INSTANCE_DOC_BOT);
        InputSource is = new InputSource(new StringReader(instanceDoc.toString()));
        is.setSystemId(DUMMY_URI);

        String soapFile = "platform:/plugin/org.eclipse.wst.wsdl.validation./xsd/xml-soap.xsd";
        XMLReader reader = new SAXParser();
        try
        {
          reader.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
          reader.setFeature("http://xml.org/sax/features/namespaces", true);
          reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
          reader.setFeature("http://xml.org/sax/features/validation", true);
          reader.setFeature("http://apache.org/xml/features/validation/schema", true);
          reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
          reader.setFeature("http://xml.org/sax/features/external-general-entities", true);
          reader.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
          reader.setFeature("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", true);

          reader.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", 
          		"http://xml.apache.org/xml-soap " + soapFile);
          reader.setErrorHandler(errorHandler);

          if (uriresolver != null)
          {
            XSDEntityResolver resolver = new XSDEntityResolver(uriresolver, uri, inputStream);
            try
            {
              reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", resolver);
            }
            catch (Exception e)
            {
              // TODO: log failure to register the entity resolver.
            }
          }

          reader.parse(is);

        } catch (SAXException e)
        {
          //LoggerFactory.getLoggerInstance().logError("XSD Validator Exception: ", e);
        }
      } catch (IOException except)
      {
        //LoggerFactory.getLoggerInstance().logError("XSD Validator Exception: ", except);
      }
    } catch (Exception e)
    { 
      //LoggerFactory.getLoggerInstance().logError("XSD Validator Exception: ", e);
    }

    return valinfo;
  }

  /**
   * Set the URI resolver to use with XSD validation.
   * 
   * @param uriresolver
   *          The URI resolver to use.
   */
  public void setURIResolver(URIResolver uriresolver)
  {
    this.uriresolver = uriresolver;
  }

  /**
   * The XSDErrorHandler handle Xerces parsing errors and puts the errors
   * into the given ValidationInfo object.
   * 
   * @author Lawrence Mandel, IBM
   */
  protected class XSDErrorHandler implements org.xml.sax.ErrorHandler
  {

    private final int ERROR = 0;

    private final int WARNING = 1;

    private final ValidationInfo valinfo;

    public XSDErrorHandler(ValidationInfo valinfo)
    {
      this.valinfo = valinfo;
    }
    
    /**
     * Add a validation message with the given severity.
     * 
     * @param exception The exception that contains the information about the message.
     * @param severity The severity of the validation message.
     */
    protected void addValidationMessage(SAXParseException exception, int severity)
    { 
      // get the error key by taking the substring of what is before the ':' in the error message:
      String errorKey = exception.getLocalizedMessage();
      if (errorKey != null)
      {
        int index = errorKey.indexOf(':');
        if (index != -1)
        { errorKey = errorKey.substring(0, index);
        }
      }
      
      if (exception.getSystemId() != null && !exception.getSystemId().equals(DUMMY_URI))
      {
        if (severity == WARNING)
        {
          valinfo.addWarning(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId());
        }
        else
        {
          valinfo.addError(exception.getLocalizedMessage(), exception.getLineNumber(), exception.getColumnNumber(), exception.getSystemId(), errorKey, null);
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
   * The XSDEntityResolver wraps an idresolver to provide entity resolution to
   * the XSD validator.
   */
  protected class XSDEntityResolver implements XMLEntityResolver
  {
    private URIResolver uriresolver = null;

    private InputStream inputStream;

    /**
     * Constructor.
     * 
     * @param idresolver
     *          The idresolver this entity resolver wraps.
     * @param baselocation The base location to resolve with.
     */
    public XSDEntityResolver(URIResolver uriresolver, String baselocation, InputStream inputStream)
    {
      this.uriresolver = uriresolver;
      this.inputStream = inputStream;
    }
    
    /* (non-Javadoc)
     * @see org.apache.xerces.xni.parser.XMLEntityResolver#resolveEntity(org.apache.xerces.xni.XMLResourceIdentifier)
     */
    public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws XNIException, IOException
    {
      // consider the resourceIdentifier's fields to see we're actually in the dummy xml file
      // and if this is the reference to the schema we want to validate
      // ... if so return an XMLInputSource built from the dirty copy of the schema
      boolean isDummyXML = DUMMY_URI.equals(resourceIdentifier.getBaseSystemId());
      if (isDummyXML && inputStream != null)
      {
        XMLInputSource inputSource = new XMLInputSource(null, resourceIdentifier.getLiteralSystemId(), null, inputStream, null);
        return inputSource;
      }
      else
      {
        // TODO cs: In revision 1.1 we explicitly opened a stream to ensure
        // file I/O problems produced messages. I've remove this fudge for now
        // since I can't seem to reproduce the problem it was intended to fix.
        // I'm hoping the newer Xerces code base has fixed this problem and the fudge is defunct.
        return XMLValidator._internalResolveEntity(uriresolver, resourceIdentifier);
      }
    }
  }   
}
