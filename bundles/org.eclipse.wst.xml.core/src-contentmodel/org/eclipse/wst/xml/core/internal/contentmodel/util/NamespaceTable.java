/*******************************************************************************
 * Copyright (c) 2002, 2009 IBM Corporation and others.
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
                          
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class NamespaceTable extends NamespaceAttributeVisitor
{                   
  public Hashtable hashtable = new Hashtable();

/**
 * @deprecated
 * @param document - no longer used
 */
public NamespaceTable(Document document)
  {         
	  this();
    //DOMExtension domExtension = DOMExtensionProviderRegistry.getInstance().getDOMExtension(document);
    //if (domExtension != null)
    //{                          
    //  addNamespaceInfoList(domExtension.getImplictNamespaceInfoList(), true);
    // }
  }   

  private NamespaceTable()
  {       
	  super();
  }  

  public boolean isNamespaceEncountered()
  {
    return hashtable.values().size() > 0;
  }

  public String getPrefixForURI(String uri)
  {           
    String result = null;
    NamespaceInfo entry = getNamespaceInfoForURI(uri, true);
    if (entry != null) 
    {
      result = entry.prefix;
    }                         
    return result;
  }
      

  public String getURIForPrefix(String prefix)
  {           
    String result = null;
    NamespaceInfo info = getNamespaceInfoForPrefix(prefix);
    if (info != null) 
    {
      result = info.uri;
    }                         
    return result;
  }    

          
  protected boolean isMatchingString(String a, String b)
  {
    return ((a == null && b == null) || (a != null && b != null && a.equals(b)));
  }
   

  public NamespaceInfo getNamespaceInfoForURI(String uri)
  {                                                     
    return getNamespaceInfoForURI(uri, false);
  }


  public NamespaceInfo getNamespaceInfoForURI(String uri, boolean testImplied)
  {
    NamespaceInfo result = null;
    for (Iterator i = hashtable.values().iterator(); i.hasNext(); )
    {                    
      NamespaceInfo nsInfo = (NamespaceInfo)i.next(); 
      if (isMatchingString(nsInfo.uri, uri))
      {                 
        result = nsInfo;
        if (testImplied && nsInfo.getProperty("isImplied") != null) //$NON-NLS-1$
        {
          // continue
        }            
        else
        {
          break;
        }
      }
    }
    return result;
  } 


  public void setLocationHintForURI(String uri, String locationHint)
  {      
   // List list = new Vector();
    for (Iterator i = hashtable.values().iterator(); i.hasNext(); )
    {                    
      NamespaceInfo nsInfo = (NamespaceInfo)i.next(); 
      if (isMatchingString(nsInfo.uri, uri))
      {                                               
        nsInfo.locationHint = locationHint;
      }
    }
  }
    

  public NamespaceInfo getNamespaceInfoForPrefix(String prefix)
  {                                      
    prefix = prefix != null ? prefix : ""; //$NON-NLS-1$
    return (NamespaceInfo)hashtable.get(prefix);
  }   


  public void visitXMLNamespaceAttribute(Attr attr, String namespacePrefix, String namespaceURI)
  {                                 
    NamespaceInfo nsInfo = new NamespaceInfo();      
    nsInfo.prefix = namespacePrefix;
    nsInfo.uri = namespaceURI;      
   
    NamespaceInfo matchingNamespaceInfo = getNamespaceInfoForURI(namespaceURI);
    if (matchingNamespaceInfo != null)
    {                           
      nsInfo.locationHint = matchingNamespaceInfo.locationHint;
    }                                      

    internalAddNamespaceInfo(namespacePrefix, nsInfo);

    super.visitXMLNamespaceAttribute(attr, namespacePrefix, namespaceURI);
  } 
                 
  public void visitXSISchemaLocationValuePair(String uri, String locationHint)
  {
    setLocationHintForURI(uri, locationHint);
  }   

  public void addNamespaceInfo(NamespaceInfo info)
  {                           
    String key  = (info.prefix != null) ? info.prefix : ""; //$NON-NLS-1$
    internalAddNamespaceInfo(key, info);
  }

  protected void internalAddNamespaceInfo(String key, NamespaceInfo info)
  {
    hashtable.put(key, info);
  }

  protected void addNamespaceInfoList(List list, boolean isImplied)
  {       
    if (list != null)
    {
      for (Iterator i = list.iterator(); i.hasNext(); )
      {
        NamespaceInfo info = (NamespaceInfo)i.next();
        NamespaceInfo clone = new NamespaceInfo(info);    
        if (isImplied)
        {
          clone.setProperty("isImplied", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        addNamespaceInfo(clone);
      }
    }
  }

  public void addNamespaceInfoList(List list)
  {  
    addNamespaceInfoList(list, false);  
  }

  public void visitXSINoNamespaceSchemaLocationAttribute(Attr attr, String locationHint)
  {
    addNoNamespaceSchemaLocation(locationHint);
  } 

  public void addNoNamespaceSchemaLocation(String locationHint)
  {
    NamespaceInfo nsInfo = new NamespaceInfo();      
    nsInfo.prefix = null;
    nsInfo.uri = "";       //$NON-NLS-1$
    nsInfo.locationHint = locationHint;  
    internalAddNamespaceInfo("", nsInfo); //$NON-NLS-1$
  } 

  public void addNamespaceInfo(String prefix, String uri, String locationHint)
  {
    NamespaceInfo nsInfo = new NamespaceInfo();      
    nsInfo.prefix = prefix;
    nsInfo.uri = uri;      
    nsInfo.locationHint = locationHint;  
    internalAddNamespaceInfo(prefix != null ? prefix : "", nsInfo); //$NON-NLS-1$
  } 

  public void addElement(Element element)
  {   
    visitElement(element);  
  }      
         
  public void addElementLineage(Element targetElement)
  {
    List list = NamespaceTable.getElementLineage(targetElement);                 
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      Element element = (Element)i.next();
      addElement(element);
    }            
  }

  public static List getElementLineage(Element element)
  {          
    List result = new ArrayList();             
    for (Node node = element; node != null; node = node.getParentNode())
    {                               
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        result.add(0, node);                      
      }
      else
      {
        break;
      }
    }  
    return result;
  }    

  public Collection getNamespaceInfoCollection()
  {              
    return hashtable.values();
  } 

  public List getNamespaceInfoList()
  {                               
    List list = new Vector();
    list.addAll(hashtable.values());
    return list;
  }
} 
