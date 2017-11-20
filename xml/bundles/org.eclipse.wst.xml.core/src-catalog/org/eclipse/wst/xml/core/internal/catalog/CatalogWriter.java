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
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.catalog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogElement;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IDelegateCatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.IRewriteEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ISuffixEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Write OASIS XML Catalog format
 * 
 */
public final class CatalogWriter
{
  private Document doc;

  public void write(ICatalog xmlCatalog, String uri) throws FileNotFoundException, IOException
  {
	OutputStream outputStream = null;
	try {
      visitCatalog(xmlCatalog);
      outputStream = getOutputStream(uri);
      serialize(outputStream);
	}
	finally {
	  if(outputStream != null) {
		outputStream.close();
	  }
	}
  }

  public void write(ICatalog catalog, OutputStream os) throws FileNotFoundException, IOException
  {
    if (catalog != null)
    {
      visitCatalog(catalog);
      serialize(os);
    }
  }

  private void visitCatalog(ICatalog xmlCatalog)
  {
    try
    {
      doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }
    catch (ParserConfigurationException e)
    {
      Logger.logException(e);
    }
    if (doc == null)
      return;
    Element catalogElement = doc.createElementNS(OASISCatalogConstants.namespaceName, OASISCatalogConstants.TAG_CATALOG);
    doc.appendChild(catalogElement);
    processCatalogEntries(xmlCatalog, catalogElement);
    processNextCatalogs(xmlCatalog, catalogElement);
    processDelegateCatalogs(xmlCatalog, catalogElement);
    processSuffixEntries(xmlCatalog, catalogElement);
    processRewriteEntries(xmlCatalog, catalogElement);
  }

  private void processRewriteEntries(ICatalog catalog, Element parent)
  {
    IRewriteEntry[] catalogEntries = catalog.getRewriteEntries();
    
    for (int i = 0; i < catalogEntries.length; i++)
    {
      IRewriteEntry entry = catalogEntries[i];
      String startString = entry.getStartString();
      String prefix = entry.getRewritePrefix();
	  Element childElement = null;
	 
     switch (entry.getEntryType())
      {
        case IRewriteEntry.REWRITE_TYPE_SYSTEM :
          childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_REWRITE_SYSTEM);
          if (childElement != null)
          {
            childElement.setAttribute(OASISCatalogConstants.ATTR_SYSTEM_ID_START_STRING, startString);
            childElement.setAttribute(OASISCatalogConstants.ATTR_REWRITE_PREFIX, prefix);
          }
          break;
        case IRewriteEntry.REWRITE_TYPE_URI:
          childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_REWRITE_URI);
          if (childElement != null)
          {
	            childElement.setAttribute(OASISCatalogConstants.ATTR_URI_START_STRING, startString);
	            childElement.setAttribute(OASISCatalogConstants.ATTR_REWRITE_PREFIX, prefix);
          }
          break;
        default :
          break;
      }
      if (childElement != null)
      {
        setAttributes(entry, childElement);
        parent.appendChild(childElement);
      }
    }
  }

  private void processSuffixEntries(ICatalog catalog, Element parent)
  {
    ISuffixEntry[] suffixEntries = catalog.getSuffixEntries();
    
    for (int i = 0; i < suffixEntries.length; i++)
    {
      ISuffixEntry entry = suffixEntries[i];
      String suffixString = entry.getSuffix();
      String uri = entry.getURI();
	  Element childElement = null;
	 
     switch (entry.getEntryType())
      {
        case ISuffixEntry.SUFFIX_TYPE_SYSTEM :
          childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_SYSTEM_SUFFIX);
          if (childElement != null)
          {
            childElement.setAttribute(OASISCatalogConstants.ATTR_SYSTEM_ID_SUFFFIX, suffixString);
            childElement.setAttribute(OASISCatalogConstants.ATTR_URI, uri);
          }
          break;
        case ISuffixEntry.SUFFIX_TYPE_URI:
          childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_URI_SUFFIX);
          if (childElement != null)
          {
	            childElement.setAttribute(OASISCatalogConstants.ATTR_URI_SUFFIX, suffixString);
	            childElement.setAttribute(OASISCatalogConstants.ATTR_URI, uri);
          }
          break;
        default :
          break;
      }
      if (childElement != null)
      {
        setAttributes(entry, childElement);
        parent.appendChild(childElement);
      }
    }
  }

  private void processDelegateCatalogs(ICatalog catalog, Element parent)
  {
    IDelegateCatalog[] delegateCatalogs = catalog.getDelegateCatalogs();

    for (int i = 0; i < delegateCatalogs.length; i++)
    {
      IDelegateCatalog entry = delegateCatalogs[i];
      String prefixString = entry.getStartString();
      String catalogLocation = entry.getCatalogLocation();
      Element childElement = null;

      switch (entry.getEntryType())
      {
      case IDelegateCatalog.DELEGATE_TYPE_PUBLIC:
        childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_DELEGATE_PUBLIC);
        if (childElement != null)
        {
          childElement.setAttribute(OASISCatalogConstants.ATTR_PUBLIC_ID_START_STRING, prefixString);
          childElement.setAttribute(OASISCatalogConstants.ATTR_CATALOG, catalogLocation);
        }
        break;
      case IDelegateCatalog.DELEGATE_TYPE_SYSTEM:
        childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_DELEGATE_SYSTEM);
        if (childElement != null)
        {
          childElement.setAttribute(OASISCatalogConstants.ATTR_SYSTEM_ID_START_STRING, prefixString);
          childElement.setAttribute(OASISCatalogConstants.ATTR_CATALOG, catalogLocation);
        }
        break;
      case IDelegateCatalog.DELEGATE_TYPE_URI:
        childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_DELEGATE_URI);
        if (childElement != null)
        {
          childElement.setAttribute(OASISCatalogConstants.ATTR_URI_START_STRING, prefixString);
          childElement.setAttribute(OASISCatalogConstants.ATTR_CATALOG, catalogLocation);
        }
        break;
      default :
        break;
      }
      if (childElement != null)
      {
        setAttributes(entry, childElement);
        parent.appendChild(childElement);
      }
    }
  }

  private void processCatalogEntries(ICatalog catalog, Element parent)
  {
    ICatalogEntry[] catalogEntries = catalog.getCatalogEntries();
  
    for (int i = 0; i < catalogEntries.length; i++)
    {
      ICatalogEntry entry = catalogEntries[i];
      String key = entry.getKey();
      String uri = entry.getURI();
      Element childElement = null;
	 
      switch (entry.getEntryType())
      {
        case ICatalogEntry.ENTRY_TYPE_PUBLIC :
          childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_PUBLIC);
          if (childElement != null)
          {
            childElement.setAttribute(OASISCatalogConstants.ATTR_PUBLIC_ID, key);
            childElement.setAttribute(OASISCatalogConstants.ATTR_URI, uri);
          }
          break;
        case ICatalogEntry.ENTRY_TYPE_SYSTEM :
          childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_SYSTEM);
          if (childElement != null)
          {
            childElement.setAttribute(OASISCatalogConstants.ATTR_SYSTEM_ID, key);
            childElement.setAttribute(OASISCatalogConstants.ATTR_URI, uri);
          }
          break;
        case ICatalogEntry.ENTRY_TYPE_URI :
          childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_URI);
          if (childElement != null)
          {
            childElement.setAttribute(OASISCatalogConstants.ATTR_NAME, key);
            childElement.setAttribute(OASISCatalogConstants.ATTR_URI, uri);
          }
          break;
        default :
          break;
      }
      if (childElement != null)
      {
    	  setAttributes(entry, childElement);
        parent.appendChild(childElement);
      }
    }
  }

  private void setAttributes(ICatalogElement entry, Element childElement)
  {
    String[] attributes = entry.getAttributes();   
    for (int j = 0; j < attributes.length; j++)
    {
      String attrName = attributes[j];
      if (attrName != null && !attrName.equals("")) //$NON-NLS-1$
      {
        String attrValue = entry.getAttributeValue(attrName);
        if (childElement != null && attrValue != null)
        {
          childElement.setAttribute(attrName, attrValue);
        }
      }
    }
    String id = entry.getId();
    if (id != null)
    {
      childElement.setAttribute(OASISCatalogConstants.ATTR_ID, id);
    }
  }

  private void processNextCatalogs(ICatalog catalog, Element parent)
  {
    // handle catalog entries
    INextCatalog[] nextCatalogs = catalog.getNextCatalogs();
    Element childElement = null;
   //dw String attrValue = null;
    for (int i = 0; i < nextCatalogs.length; i++)
    {
      INextCatalog delegate = nextCatalogs[i];
      childElement = parent.getOwnerDocument().createElement(OASISCatalogConstants.TAG_NEXT_CATALOG);
      if (childElement != null)
      {
        parent.appendChild(childElement);
        String location = delegate.getCatalogLocation();
        if (location != null)
        {
          childElement.setAttribute(OASISCatalogConstants.ATTR_CATALOG, location);
        }
        setAttributes(delegate, childElement);
      }
    }
  }

  private void serialize(OutputStream outputStream) throws FileNotFoundException, IOException
  {
    if (doc == null)
      return;
    try
    {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
      transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
      // Unless a width is set, there will be only line breaks but no
      // indentation.
      // The IBM JDK and the Sun JDK don't agree on the property name,
      // so we set them both.
      //
      transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
      String encoding = "UTF-8"; // TBD //$NON-NLS-1$
      if (encoding != null)
      {
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
      }
      transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
    }
    catch (TransformerException e)
    {
      Logger.logException(e);
    }
  }

  private OutputStream getOutputStream(String uri) throws FileNotFoundException, IOException
  {
    String filePath = removeProtocol(uri);
    File file = new File(filePath);
    OutputStream stream = new FileOutputStream(file);
    return stream;
  }
  
  protected static String removeProtocol(String uri)
  {
    String result = uri;  
    String protocol_pattern = ":"; //$NON-NLS-1$
    if (uri != null)
    {
      int index = uri.indexOf(protocol_pattern);
      if (index > 2)
      {
        result = result.substring(index + protocol_pattern.length());                 
      }
    }
    return result;
  }
}
