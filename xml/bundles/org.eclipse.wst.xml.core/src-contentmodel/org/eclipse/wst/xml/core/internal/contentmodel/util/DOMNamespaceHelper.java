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

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class DOMNamespaceHelper
{                
  protected static String getURIForPrefix(Element element, String prefix)
  {              
    String result = null;
	  String nsAttrName = null;
	  if (prefix != null && prefix.length() > 0) 
    {
		  nsAttrName = "xmlns:" + prefix; //$NON-NLS-1$
	  }
	  else 
    {
		  nsAttrName = "xmlns"; //$NON-NLS-1$
	  }
            
    // assume the node is qualified... look up the URI base on the prefix
    //
	  for (Node node = element; node != null; node = node.getParentNode()) 
    {
	   	if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        Element theElement = (Element)node;
		    Attr attr = theElement.getAttributeNode(nsAttrName);
		    if (attr != null) 
        {
          result = attr.getValue();
        }
      }
      else
      {
        break;
      }
    }
         
    // see if we can find some info from an 'implicit' namespace
    //
    if (result == null)
    {                                
      NamespaceTable table = new NamespaceTable(element.getOwnerDocument());
      result = table.getURIForPrefix(prefix);
    }
    return result;
	}

  public static String getNamespaceURI(Node node)
  {            
    String result = null;
    if (node.getNodeType() == Node.ELEMENT_NODE)
    {               
      Element element = (Element)node;
      String prefix = element.getPrefix();
      result = getURIForPrefix(element, prefix);
    }
    else if (node.getNodeType() == Node.ATTRIBUTE_NODE)
    {      
      Attr attr = (Attr)node;
      String prefix = attr.getPrefix();
      result = getURIForPrefix(attr.getOwnerElement(), prefix);
    }
    return result;
  }                 

  // todo... this is an ugly hack... needs to be fixed
  //
  public static String computePrefix(CMNode cmNode, Node parentNode)
  {
    String result = null;
    for (Node node = parentNode; node != null; node = node.getParentNode())
    {
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        result = getPrefix(node.getNodeName());
        if (result != null)
        {
          break;
        }
      }
    }
    return result;
  }        


  public static String getPrefix(String name)
  {
    String prefix = null;
    int index = name.indexOf(":"); //$NON-NLS-1$
    if (index != -1)
    {
      prefix = name.substring(0, index);
    }
    return prefix;
  }


  public static String getUnprefixedName(String name)
  {
    int index = name.indexOf(":"); //$NON-NLS-1$
    if (index != -1)
    {
      name = name.substring(index + 1);
    }
    return name;
  }


  public static String computeName(CMNode cmNode, Node parent, String prefix)
  {     
    return computeName(cmNode, parent, prefix, null);
  }   


  public static String computeName(CMNode cmNode, Node parent, String prefix, NamespaceTable namespaceTable)
  {
    String result = cmNode.getNodeName();

    // if the cmNode has a hard coded prefix then we don't need to do anything
    //
    if (getPrefix(result) == null)
    {              
      String qualification = (String)cmNode.getProperty("http://org.eclipse.wst/cm/properties/nsPrefixQualification"); //$NON-NLS-1$
      // see if we need a namespace prefix
      //
      if (qualification != null && qualification.equals("qualified")) //$NON-NLS-1$
      {            
        if (prefix == null)                 
        {
          // todo... add getCMDocument() method to CMNode
          // for now use this getProperty() hack
          CMDocument cmDocument = (CMDocument)cmNode.getProperty("CMDocument"); //$NON-NLS-1$
          if (cmDocument != null)          
          {     
            String namespaceURI = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI");    //$NON-NLS-1$
            if (namespaceURI != null)
            {   
              // use the NamespaceTable to figure out the correct prefix for this namespace uri
              //      
              if (namespaceTable == null)
              {                                            
                Document document = parent.getNodeType() == Node.DOCUMENT_NODE ? (Document)parent : parent.getOwnerDocument();
                namespaceTable = new NamespaceTable(document);
                if (parent instanceof Element)
                {
                  namespaceTable.addElementLineage((Element)parent);
                }
              }
              prefix = namespaceTable.getPrefixForURI(namespaceURI);
            }
          }
        }
        if (prefix != null && prefix.length() > 0)
        {
          result = prefix + ":" + result; //$NON-NLS-1$
        }
      }    
    }
    return result;
  }  
  

  public static String[] getURIQualifiedNameComponents(String uriQualifiedName)
  {
    String[] result = new String[2];
    int firstIndex = uriQualifiedName.indexOf("["); //$NON-NLS-1$
    int lastIndex = uriQualifiedName.indexOf("]"); //$NON-NLS-1$
    if (firstIndex != -1 && lastIndex > firstIndex)
    {
      result[0] = uriQualifiedName.substring(firstIndex + 1, lastIndex);
      result[1] = uriQualifiedName.substring(lastIndex + 1);
    }  
    else
    {
      result[1] = uriQualifiedName;
    }                              
    return result;
  }   
}
