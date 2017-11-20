/*******************************************************************************
 * Copyright (c) 2002, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Moeller - Added XML Catalogs 1.1 support
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.wst.common.uriresolver.internal.URI;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.XMLCoreMessages;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IDelegateCatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IRewriteEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ISuffixEntry;
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
      factory.setValidating(false);
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);//$NON-NLS-1$
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
      String base = xmlCatalog.getBase();
      if(base == null || base == "") {  //$NON-NLS-1$
    	  base = xmlCatalog.getLocation();
      }
      baseURIStack.push(base);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      // set base attribute xml:base
      String base = attributes.getValue(OASISCatalogConstants.ATTR_BASE); //$NON-NLS-1$
      if (base != null && !base.equals("")) //$NON-NLS-1$
      {
          // since the xml:base attribute can be relative to the enclosing element's effective base, we're maintaining a
          // stack of absolute URIs
		  if (URI.createURI(base).isRelative())
          {
            base = URI.resolveRelativeURI(base, baseURIStack.peek().toString());
          }

          baseURIStack.push(URIHelper.ensureURIProtocolFormat(base));
      } else {
    	  baseURIStack.push(baseURIStack.peek());
      }

	  // processing for backward compatibility start
	  if (localName.equals(CompatabilityConstants.TAG_USER_ENTRY))
      {
        String typeName = attributes.getValue("", CompatabilityConstants.ATT_TYPE); //$NON-NLS-1$
        boolean isSystem = false;
        if (typeName != null)
        {
        	isSystem = (typeName.compareToIgnoreCase("SYSTEM") == 0); //$NON-NLS-1$
        }
        ICatalogEntry catalogEntry = new CatalogEntry(isSystem ? ICatalogEntry.ENTRY_TYPE_SYSTEM : ICatalogEntry.ENTRY_TYPE_PUBLIC);
        catalogEntry.setKey(attributes.getValue("", CompatabilityConstants.ATT_ID)); //$NON-NLS-1$
        String entryUri = attributes.getValue("", CompatabilityConstants.ATT_URI); //$NON-NLS-1$
        
        // For relative URIs, try to resolve them using the corresponding base URI.
        catalogEntry.setURI(resolveRelative(entryUri));

        String webURL = attributes.getValue("", CompatabilityConstants.ATT_WEB_URL); //$NON-NLS-1$
        if (webURL != null)
        {
        	catalogEntry.setAttributeValue(
        			ICatalogEntry.ATTR_WEB_URL, webURL);
        }
		catalog.addCatalogElement(catalogEntry);
		return;
      }
	  //  processing for backward compatibility end
	  ICatalogElement catalogElement = null;

      if (OASISCatalogConstants.TAG_PUBLIC.equals(localName))
      {
    	  // 6.5.3. The public Entry
    	  catalogElement = createEntry(attributes, ICatalogEntry.ENTRY_TYPE_PUBLIC, OASISCatalogConstants.ATTR_PUBLIC_ID);
      }
      else if (OASISCatalogConstants.TAG_SYSTEM.equals(localName))
      {
    	  // 6.5.4. The system Element
    	  catalogElement = createEntry(attributes, ICatalogEntry.ENTRY_TYPE_SYSTEM, OASISCatalogConstants.ATTR_SYSTEM_ID);
      }
      else if (OASISCatalogConstants.TAG_URI.equals(localName))
      {
    	  // 6.5.9. The uri Element
    	  catalogElement = createEntry(attributes, ICatalogEntry.ENTRY_TYPE_URI, OASISCatalogConstants.ATTR_NAME);
      }
      else if (OASISCatalogConstants.TAG_REWRITE_SYSTEM.equals(localName))
      {
    	  // 6.5.5. The rewriteSystem Element
    	  catalogElement = createRewrite(attributes, IRewriteEntry.REWRITE_TYPE_SYSTEM, OASISCatalogConstants.ATTR_SYSTEM_ID_START_STRING);
      }
      else if (OASISCatalogConstants.TAG_REWRITE_URI.equals(localName))
      {
    	  // 6.5.9. The uri Element
    	  catalogElement = createRewrite(attributes, IRewriteEntry.REWRITE_TYPE_URI, OASISCatalogConstants.ATTR_URI_START_STRING);
      }
      else if (OASISCatalogConstants.TAG_DELEGATE_PUBLIC.equals(localName))
      {
    	  // 6.5.7. The delegatePublic Element
    	  catalogElement = createDelegate(attributes, IDelegateCatalog.DELEGATE_TYPE_PUBLIC, OASISCatalogConstants.ATTR_PUBLIC_ID_START_STRING);
      }
      else if (OASISCatalogConstants.TAG_DELEGATE_SYSTEM.equals(localName))
      {
    	  // 6.5.8. The delegateSystem Element
    	  catalogElement = createDelegate(attributes, IDelegateCatalog.DELEGATE_TYPE_SYSTEM, OASISCatalogConstants.ATTR_SYSTEM_ID_START_STRING);
      }
      else if (OASISCatalogConstants.TAG_DELEGATE_URI.equals(localName))
      {
    	  // 6.5.12. The delegateURI Element
    	  catalogElement = createDelegate(attributes, IDelegateCatalog.DELEGATE_TYPE_URI, OASISCatalogConstants.ATTR_URI_START_STRING);
      }
      else if (OASISCatalogConstants.TAG_SYSTEM_SUFFIX.equals(localName))
      {
    	  // 6.5.6. The systemSuffix Element
    	  catalogElement = createSuffix(attributes, ISuffixEntry.SUFFIX_TYPE_SYSTEM, OASISCatalogConstants.ATTR_SYSTEM_ID_SUFFFIX);
      }
      else if (OASISCatalogConstants.TAG_URI_SUFFIX.equals(localName))
      {
    	  // 6.5.11. The uriSuffix Element
    	  catalogElement = createSuffix(attributes, ISuffixEntry.SUFFIX_TYPE_URI, OASISCatalogConstants.ATTR_URI_SUFFIX);
      }
      else if (OASISCatalogConstants.TAG_NEXT_CATALOG.equals(localName))
      {
        catalogElement = createNextCatalog(attributes);
      }
      
      if (catalogElement == null)
      {
    	// do not set the extra information
        return;
      }

	  String attrId = attributes.getValue("", OASISCatalogConstants.ATTR_ID);//$NON-NLS-1$
	  if (attrId != null && ! "".equals(attrId)) catalogElement.setId(attrId);//$NON-NLS-1$
      // process any other attributes
      for (int j = 0; j < attributes.getLength(); j++)
      {
        String attrName = attributes.getLocalName(j);
        if (!attrName.equals(OASISCatalogConstants.ATTR_URI) && !attrName.equals(OASISCatalogConstants.ATTR_NAME) && !attrName.equals(OASISCatalogConstants.ATTR_PUBLIC_ID)
            && !attrName.equals(OASISCatalogConstants.ATTR_SYSTEM_ID) && !attrName.equals(OASISCatalogConstants.ATTR_CATALOG) && !attrName.equals(OASISCatalogConstants.ATTR_ID)
            && !attrName.equals(OASISCatalogConstants.ATTR_BASE))
        {
          String attrValue = attributes.getValue(attrName);
          if (attrValue != null && !attrValue.equals("")) //$NON-NLS-1$
          {
            catalogElement.setAttributeValue(attrName, attrValue);
          }
        }
      }
      catalog.addCatalogElement(catalogElement);

    }

	private ICatalogElement createNextCatalog(Attributes attributes) {
		String location = attributes.getValue("", OASISCatalogConstants.ATTR_CATALOG);    //$NON-NLS-1$
        NextCatalog delegate = new NextCatalog();
        delegate.setBase((String)baseURIStack.peek());
        delegate.setCatalogLocation(location);  
		return delegate;
	}

    private ICatalogEntry createEntry(Attributes attributes, int entryType, String keyAttributeName) {
    	String key = attributes.getValue("", keyAttributeName); //$NON-NLS-1$
    	if (key == null || key.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_entry_key_not_set);
    		return null;
    	}
    	String entryURI = attributes.getValue("", OASISCatalogConstants.ATTR_URI); //$NON-NLS-1$
    	if (entryURI == null || entryURI.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_entry_uri_not_set);
    		return null;
    	}
    	CatalogEntry entry = new CatalogEntry(entryType);
    	entry.setKey(key);
    	entry.setURI(resolveRelative(entryURI));
    	return entry;
    }

    private IRewriteEntry createRewrite(Attributes attributes, int entryType, String prefixStringName) {
    	String startString = attributes.getValue("", prefixStringName); //$NON-NLS-1$
    	if (startString == null || startString.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_rewrite_startString_not_set);
    		return null;
    	}
    	String prefix = attributes.getValue("", OASISCatalogConstants.ATTR_REWRITE_PREFIX); //$NON-NLS-1$
    	if (prefix == null || prefix.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_rewrite_prefix_not_set);
    		return null;
    	}
    	RewriteEntry entry = new RewriteEntry(entryType);
    	entry.setStartString(startString);
    	entry.setRewritePrefix(resolveRelative(prefix));
    	return entry;
    }

    private IDelegateCatalog createDelegate(Attributes attributes, int entryType, String startStringAttrName) {
    	String startString = attributes.getValue("", startStringAttrName); //$NON-NLS-1$
    	if (startString == null || startString.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_delegate_prefix_not_set);
    		return null;
    	}
    	String catalogUri = attributes.getValue("", OASISCatalogConstants.ATTR_CATALOG); //$NON-NLS-1$
    	if (catalogUri == null || catalogUri.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_delegate_prefix_not_set);
    		return null;
    	}
    	DelegateCatalog entry = new DelegateCatalog(entryType);
    	entry.setStartString(startString);
    	entry.setCatalogLocation(catalogUri);
    	return entry;
    }

    private ISuffixEntry createSuffix(Attributes attributes, int entryType, String suffixAttrName) {
    	String suffix = attributes.getValue("", suffixAttrName); //$NON-NLS-1$
    	if (suffix == null || suffix.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_suffix_string_not_set);
    		return null;
    	}
    	String uri = attributes.getValue("", OASISCatalogConstants.ATTR_URI); //$NON-NLS-1$
    	if (uri == null || uri.equals("")) //$NON-NLS-1$
    	{
    		Logger.log(Logger.ERROR, XMLCoreMessages.Catalog_suffix_uri_not_set);
    		return null;
    	}
    	SuffixEntry entry = new SuffixEntry(entryType);
    	entry.setSuffix(suffix);
    	entry.setURI(resolveRelative(uri));
    	return entry;
    }

	private String resolveRelative(String entryURI) 
	{
		if(URI.createURI(entryURI).isRelative()) 
		{
        	entryURI = URI.resolveRelativeURI(entryURI, baseURIStack.peek().toString());
        }
		return URIHelper.ensureURIProtocolFormat(entryURI);
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
	  
	  public static final String TAG_ID_XML_CATALOG_SETTINGS = "XMLCatalogSettings"; //$NON-NLS-1$
	  public static final String TAG_ID_USER_ENTRIES = "UserEntries"; //$NON-NLS-1$
	  public static final String TAG_USER_ENTRY = "UserEntry"; //$NON-NLS-1$
	  public static final String ATT_TYPE = "TYPE"; //$NON-NLS-1$
	  public static final String ATT_ID = "ID"; //$NON-NLS-1$
	  public static final String ATT_URI = "URI"; //$NON-NLS-1$
	  public static final String ATT_WEB_URL = "WEB_URL"; //$NON-NLS-1$
  }
}
