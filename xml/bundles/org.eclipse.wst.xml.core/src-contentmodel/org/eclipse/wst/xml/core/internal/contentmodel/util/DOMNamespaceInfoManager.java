/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * DOMNamespaceInfoManager          
 *
 *
 */
public class DOMNamespaceInfoManager
{                               
  public static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$

  public DOMNamespaceInfoManager()
  {
  }                 

  public List getNamespaceInfoList(Element element)
  {
    NamespaceInfoReader reader = new NamespaceInfoReader();
    return reader.getNamespaceInfoList(element);
  }            
   
  public void removeNamespaceInfo(Element element)
  {     
    NamespaceInfoRemover remover = new NamespaceInfoRemover();
    remover.removeNamespaceInfo(element);
  }

  public void addNamespaceInfo(Element element, List namespaceInfoList, boolean needsXSI)
  {  
    // first we create an xmlns attribute for each namespace
    //                   
    Document document = element.getOwnerDocument();
                                
    String schemaLocationValue = "";      //$NON-NLS-1$

    for (Iterator iterator = namespaceInfoList.iterator(); iterator.hasNext(); )
    {
      NamespaceInfo nsInfo = (NamespaceInfo)iterator.next();
      nsInfo.normalize();
               
      if (nsInfo.uri != null) 
      {            
        String attrName = nsInfo.prefix != null ? "xmlns:" + nsInfo.prefix : "xmlns"; //$NON-NLS-1$ //$NON-NLS-2$
        Attr namespaceAttr = document.createAttribute(attrName);  
        namespaceAttr.setValue(nsInfo.uri);
        element.setAttributeNode(namespaceAttr);   

        // in this case we use the attribute "xsi:schemaLocation"
        // here we build up its value
        //
        if (nsInfo.locationHint != null)
        {
          schemaLocationValue += nsInfo.uri;
          schemaLocationValue += " "; //$NON-NLS-1$
          schemaLocationValue += nsInfo.locationHint;
          schemaLocationValue += " ";    //$NON-NLS-1$
        }   

        if (nsInfo.uri.equals(XSI_URI))
        {
          needsXSI = false;
        }
      }     
      else if (nsInfo.locationHint != null)
      {
        // in this case we use the attribute "xsi:noNamespaceSchemaLocation"
        //
        Attr attr = document.createAttribute("xsi:noNamespaceSchemaLocation");   //$NON-NLS-1$
        attr.setValue(nsInfo.locationHint);
        element.setAttributeNode(attr);
      } 
    } 

    if (needsXSI)
    {
      // we add an xmlns:xsi attribute to define 'xsi:schemaLocation' attribute
      //   
      Attr attr = document.createAttribute("xmlns:xsi"); //$NON-NLS-1$
      attr.setValue(XSI_URI);
      element.setAttributeNode(attr);
    }

    if (schemaLocationValue.length() > 0)
    {
      // create the "xsi:schemaLocation" attribute
      //
      Attr attr = document.createAttribute("xsi:schemaLocation"); //$NON-NLS-1$
      attr.setValue(schemaLocationValue);
      element.setAttributeNode(attr);
    }                             
  } 
       
  /**
   *
   */
  protected static class NamespaceInfoReader extends NamespaceAttributeVisitor
  {  
    protected List namespaceInfoList = new Vector();

    public List getNamespaceInfoList(Element element)
    {
      visitElement(element);
      return namespaceInfoList;
    }
                       

    public void visitXSINoNamespaceSchemaLocationAttribute(Attr attr, String value)
    {
      NamespaceInfo info = createNamespaceInfo();
      info.locationHint = value;      
    }
    
    public void visitXMLNamespaceAttribute(Attr attr, String prefix, String uri)
    {           
      NamespaceInfo info = createNamespaceInfo();
      info.uri = uri;
      info.prefix = prefix;      
      super.visitXMLNamespaceAttribute(attr, prefix, uri);
    }

    public void visitXSISchemaLocationValuePair(String uri, String locationHint)
    {    
      NamespaceInfo info = getNamespaceInfoForURI(uri);            
      if (info != null)
      {
        info.locationHint = locationHint;
      } 
      else
      {
        info = createNamespaceInfo();
        info.uri = uri;
        info.locationHint = locationHint;
      }
    }                                                                 

    protected NamespaceInfo getNamespaceInfoForURI(String uri)
    {    
      NamespaceInfo result = null;
      for (Iterator i = namespaceInfoList.iterator(); i.hasNext(); )
      {
        NamespaceInfo info = (NamespaceInfo)i.next();
        if (info.uri != null && info.uri.equals(uri))
        {
          result = info;
          break;
        }
      }         
      return result;
    }     

    protected NamespaceInfo createNamespaceInfo()
    {
      NamespaceInfo info = new NamespaceInfo();
      namespaceInfoList.add(info);
      return info;
    }
  }
     
                                                
  /**
   *
   */
  protected static class NamespaceInfoRemover extends NamespaceAttributeVisitor
  {                   
    protected List attributesToRemove = new Vector();

    public void removeNamespaceInfo(Element element)
    {  
      visitElement(element);
      removeAttributes();
    }   

    public void visitXSINoNamespaceSchemaLocationAttribute(Attr attr, String value)
    {
      attributesToRemove.add(attr);
    }        
    
    public void visitXMLNamespaceAttribute(Attr attr, String namespacePrefix, String namespaceURI)
    {   
      attributesToRemove.add(attr);
      super.visitXMLNamespaceAttribute(attr, namespacePrefix, namespaceURI);
    }
    
    public void visitXSISchemaLocationAttribute(Attr attr, String value)
    {
      attributesToRemove.add(attr);
    }   

    public void removeAttributes()
    {
      for (Iterator i = attributesToRemove.iterator(); i.hasNext(); )
      {
        Attr attr = (Attr)i.next();
        Element element = attr.getOwnerElement();
        if (element != null)
        {
          element.removeAttributeNode(attr);
        }
      }
    }   
  }


}
