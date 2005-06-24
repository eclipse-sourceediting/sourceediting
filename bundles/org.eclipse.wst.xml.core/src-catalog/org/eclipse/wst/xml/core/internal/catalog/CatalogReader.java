package org.eclipse.wst.xml.core.internal.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCoreMessages;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * 
 */
public final class CatalogReader
{
  public static void read(Catalog xmlCatalog, InputStream input) throws IOException
  {
    try
    {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser parser = factory.newSAXParser();
      parser.parse(new InputSource(input), new CatalogContentHandler(xmlCatalog));
    }
    catch (ParserConfigurationException e)
    {
      Logger.logException(e);
    }
    catch (SAXException e)
    {
      Logger.logException(e);
    }
  }
  protected static class CatalogContentHandler extends DefaultHandler
  {
    protected Catalog catalog;
    protected Stack baseURIStack = new Stack();

    public CatalogContentHandler(Catalog xmlCatalog)
    {
      this.catalog = xmlCatalog;
      baseURIStack.push(xmlCatalog.getBase());
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      // set base
      String base = attributes.getValue("", OASISCatalogConstants.ATTR_BASE);
      if (base != null && !base.equals(""))
      {
          baseURIStack.push(base);    
      }

	  // processing for backward compatability start
	  if (localName.equals(CompatabilityConstants.TAG_USER_ENTRY))
      {
		int type = ICatalogEntry.ENTRY_TYPE_PUBLIC;
        String typeName = attributes.getValue("", CompatabilityConstants.ATT_TYPE);
        if (typeName != null)
        {
          if (typeName.compareToIgnoreCase("SYSTEM") == 0)
          {
            type = ICatalogEntry.ENTRY_TYPE_SYSTEM;
          }
        }
		  ICatalogElement catalogElement = catalog.createCatalogElement(type);
	      if (catalogElement instanceof CatalogEntry)
	      {
	        CatalogEntry catalogEntry = (CatalogEntry) catalogElement;
		    String key = attributes.getValue("", CompatabilityConstants.ATT_ID);   		    
	        catalogEntry.setKey(key);
	        String entryUri = attributes.getValue("", CompatabilityConstants.ATT_URI);   
	        catalogEntry.setURI(entryUri);  
	        String webURL = attributes.getValue("", CompatabilityConstants.ATT_WEB_URL);
			if (webURL != null)
			{
				catalogEntry.setAttributeValue(
						ICatalogEntry.ATTR_WEB_URL, webURL);
			}
	      }
		  catalog.addCatalogElement(catalogElement);
		  return;
      }
	  //  processing for backward compatability start
      
	  int type = ICatalogEntry.ENTRY_TYPE_PUBLIC;
	  String key = null;
	 //dw String catalogId = attributes.getValue("", OASISCatalogConstants.ATTR_ID);
      if (OASISCatalogConstants.TAG_PUBLIC.equals(localName))
      {
        key = attributes.getValue("", OASISCatalogConstants.ATTR_PUBLIC_ID);
      }
      else if (OASISCatalogConstants.TAG_SYSTEM.equals(localName))
      {
        key = attributes.getValue("", OASISCatalogConstants.ATTR_SYSTEM_ID);
        type = ICatalogEntry.ENTRY_TYPE_SYSTEM;
      }
      else if (OASISCatalogConstants.TAG_URI.equals(localName))
      {
        key = attributes.getValue("", OASISCatalogConstants.ATTR_NAME);
        type = ICatalogEntry.ENTRY_TYPE_URI;
      }
      else if (OASISCatalogConstants.TAG_NEXT_CATALOG.equals(localName))
      {
        String nextCatalogId = attributes.getValue("", OASISCatalogConstants.ATTR_ID);

        String location = attributes.getValue("", OASISCatalogConstants.ATTR_CATALOG);   
        NextCatalog delegate = new NextCatalog();
        delegate.setCatalogLocation(location);  
        delegate.setId(nextCatalogId);
        catalog.addCatalogElement(delegate);
        return;
      }
      else{
    	  // do not handle other entries
    	  return;
      }
      if (key == null || key.equals(""))
      {
        Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_entry_key_not_set);
        return;
      }
      String entryURI = attributes.getValue("", OASISCatalogConstants.ATTR_URI); // mandatory
      if (entryURI == null || entryURI.equals(""))
      {
        Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_entry_uri_not_set);
        return;
      }
 
      ICatalogElement catalogElement = catalog.createCatalogElement(type);
      if (catalogElement instanceof CatalogEntry)
      {
        CatalogEntry catalogEntry = (CatalogEntry) catalogElement;
        catalogEntry.setKey(key); 
        catalogEntry.setURI(entryURI);     
      }
      // process any other attributes
      for (int j = 0; j < attributes.getLength(); j++)
      {
        String attrName = attributes.getLocalName(j);
        if (!attrName.equals(OASISCatalogConstants.ATTR_URI) && !attrName.equals(OASISCatalogConstants.ATTR_NAME) && !attrName.equals(OASISCatalogConstants.ATTR_PUBLIC_ID)
            && !attrName.equals(OASISCatalogConstants.ATTR_SYSTEM_ID) && !attrName.equals(OASISCatalogConstants.ATTR_CATALOG) && !attrName.equals(OASISCatalogConstants.ATTR_ID)
            && !attrName.equals(OASISCatalogConstants.ATTR_BASE))
        {
          String attrValue = attributes.getValue(attrName);
          if (attrValue != null && !attrValue.equals(""))
          {
            catalogElement.setAttributeValue(attrName, attrValue);
          }
        }
      }
      catalog.addCatalogElement(catalogElement);

    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
      if (baseURIStack.size() > 0)
      {
        baseURIStack.pop();
      }
    }
    
  }  
  
  // for backward compatability
  interface CompatabilityConstants{
	  
	  public static final String TAG_ID_XML_CATALOG_SETTINGS = "XMLCatalogSettings";
	  public static final String TAG_ID_USER_ENTRIES = "UserEntries";
	  public static final String TAG_USER_ENTRY = "UserEntry";
	  public static final String ATT_TYPE = "TYPE";
	  public static final String ATT_ID = "ID";
	  public static final String ATT_URI = "URI";
	  public static final String ATT_WEB_URL = "WEB_URL";
  }
}
