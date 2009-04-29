/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.util;

import java.util.ArrayList;

import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

// issue (cs) remove this class!!
public class XSDDOMHelper
{

  private static String XMLSchemaURI = "http://www.w3.org/2001/XMLSchema";

  /**
   * Constructor for XSDDOMHelper.
   */
  public XSDDOMHelper()
  {
    super();
  }

  public Node getChildNode(Element parent, String childName)
  {
/*    NodeList nodeList = parent.getElementsByTagNameNS(XMLSchemaURI, childName);
    if (nodeList.getLength() > 0)
      return nodeList.item(0);
    return null;
*/
    NodeList list = null;
    if (parent != null)
    {
      list = parent.getChildNodes();
    }

    if (list != null)
    {
      // Performance issue perhaps?
      for (int i = 0; i < list.getLength(); i++)
      {
        if (list.item(i) instanceof Element)
        {
          if (list.item(i).getLocalName().equals(childName))
          {
            return list.item(i);
          }
        }
      }
    }
    return null;
  }




  public void changeDerivedByType(Element element, String derivedByType, String type)
  {
    Document doc = element.getOwnerDocument();

    String prefix = element.getPrefix();
    prefix = prefix == null ? "" : prefix + ":";

    Element derivedByElement = getDerivedByElement(element);
    
    if (derivedByElement != null && derivedByElement.getLocalName().equals(derivedByType))
    {
    	return; // it's already the derived by type
    }
    Element newNode;
  	if (derivedByType.equals("restriction"))
  	{
    	newNode = doc.createElementNS(XSDDOMHelper.XMLSchemaURI, prefix + XSDConstants.RESTRICTION_ELEMENT_TAG);
    }
    else
    {
    	newNode = doc.createElementNS(XSDDOMHelper.XMLSchemaURI, prefix + XSDConstants.EXTENSION_ELEMENT_TAG);
    }
	
    newNode.setAttribute("base", type);

    if (derivedByElement != null)
    {
      if (derivedByElement.hasChildNodes())
      {        
        NodeList nodes = derivedByElement.getChildNodes();
        // use clones so we don't have a refresh problem
        for (int i = 0; i < nodes.getLength(); i++)
        {
          Node node = nodes.item(i);       
          newNode.appendChild(node.cloneNode(true));
        }
      }
	  element.replaceChild(newNode, derivedByElement);
    }
    else 
	{
		Element parent = (Element) element.getParentNode();				// get back to complexType
        NodeList nodes = parent.getChildNodes();
		ArrayList nodeSaveList = new ArrayList();
		
		// save children. (nodes turns out to be the same object as parent;
		// deleting them from parent will delete them from nodes.)
        for (int i = 0; i < nodes.getLength(); i++)			
        {
          Node node = nodes.item(i);      
		  nodeSaveList.add(node);
        }

        // remove children so we can surround them by complexContent
        for (int i = 0; i < nodeSaveList.size(); i++)			
        {
          Node node = (Node) nodeSaveList.get(i);      
          parent.removeChild(node);
        }
		
		// build a complexContent element
		Element complexContent = doc.createElementNS(XSDDOMHelper.XMLSchemaURI, prefix + XSDConstants.COMPLEXCONTENT_ELEMENT_TAG);
		parent.appendChild(complexContent);					// insert into complexType
		complexContent.appendChild(newNode);				// insert derivation type
        for (int i = 0; i < nodeSaveList.size(); i++)			// insert children previously of complexType
        {
          Node node = (Node) nodeSaveList.get(i);       
          newNode.appendChild(node.cloneNode(true));
        }
		
		parent.appendChild(complexContent);
		formatChild(complexContent);
    }
  }

  
  /**
   * Get the derived by node given the complexContent or simpleContent node
   */
  public Element getDerivedByElement(Element element)
  {
    Node restrictionChild = getChildNode(element, "restriction");
    Node extensionChild = getChildNode(element, "extension");
    if (restrictionChild != null)
    {
      if (restrictionChild instanceof Element)
      {
        return (Element)restrictionChild;
      }
    }
    
    if (extensionChild != null)
    {
      if (extensionChild instanceof Element)
      {
        return (Element)extensionChild;
      }
    }
    return null;
  }
  
  
  public Element getDerivedByElementFromSimpleType(Element element)
  {
    Node atomic = getChildNode(element, "restriction");
    Node list = getChildNode(element, "list");
    Node union = getChildNode(element, "union");
    if (atomic instanceof Element)
	  {
	    return (Element)atomic;
	  }
    if (list instanceof Element)
	  {
	    return (Element)list;
	  }
    if (union instanceof Element)
	  {
	    return (Element)union;
	  }
    return null;
  }

  /**
   * Get the derived by node given the ComplexType node
   * Returns the first one, if say, the INVALID schema has more than one
   */
  public Element getDerivedByElementFromComplexType(Element element)
  {
    NodeList nl = element.getChildNodes();
    int j = 0;
    for (j = 0; j < nl.getLength(); j++)
    {
      Node aNode = nl.item(j);
      if (inputEquals(aNode, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
      {
        break; 
      }
      else if (inputEquals(aNode, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
      {
        break;
      }
    }
    Element derivedByNode = getDerivedByElement((Element)nl.item(j));
    return derivedByNode;
  }

  /**
   * Get the content model given the ComplexType node
   * Returns the first one, if say, the INVALID schema has more than one
   */
  public Element getContentModelFromParent(Element element)
  {
    NodeList nl = element.getChildNodes();
    int j = 0;
    boolean modelExists = false;
    int length = nl.getLength();
    for (j = 0; j < length; j++)
    {
      Node aNode = nl.item(j);
      if (inputEquals(aNode, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
      {
        modelExists = true;
        break; 
      }
      else if (inputEquals(aNode, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
      {
        modelExists = true;
        break;
      }
      else if (inputEquals(aNode, XSDConstants.SEQUENCE_ELEMENT_TAG, false))
      {
        modelExists = true;
        break;
      }
      else if (inputEquals(aNode, XSDConstants.CHOICE_ELEMENT_TAG, false))
      {
        modelExists = true;
        break;
      }
      else if (inputEquals(aNode, XSDConstants.ALL_ELEMENT_TAG, false))
      {
        modelExists = true;
        break;
      }
    }
    if (!modelExists)
    {
      return null;
    }

    Element derivedByNode = (Element)nl.item(j);
    return derivedByNode;
  }

  /**
   * 
   */
  public void changeContentModel(Element complexTypeElement, String contentModel, Element sequenceChoiceOrAllElement)
  {
    Document doc = complexTypeElement.getOwnerDocument();

    String prefix = complexTypeElement.getPrefix();
    prefix = prefix == null ? "" : prefix + ":";
    
    Element contentModelElement = getContentModelFromParent(complexTypeElement);

    if (contentModelElement.getLocalName().equals(contentModel))
    {
      return; // it's already the content model 
    }
    Element newNode;
    newNode = doc.createElementNS(XSDDOMHelper.XMLSchemaURI, prefix + contentModel);

    if (contentModelElement.hasChildNodes())
    {        
      NodeList nodes = contentModelElement.getChildNodes();
      // use clones so we don't have a refresh problem
      for (int i = 0; i < nodes.getLength(); i++)
      {
        Node node = nodes.item(i);
        if (node instanceof Element)
        {
          if (node.getLocalName().equals(XSDConstants.ANNOTATION_ELEMENT_TAG))
          {
            if (!(XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.SEQUENCE_ELEMENT_TAG, false) ||
                XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.CHOICE_ELEMENT_TAG, false) ||
                XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.ALL_ELEMENT_TAG, false)))
            {
              newNode.appendChild(node.cloneNode(true));
            }
          }
          else if (node.getLocalName().equals(XSDConstants.RESTRICTION_ELEMENT_TAG) ||
                    node.getLocalName().equals(XSDConstants.EXTENSION_ELEMENT_TAG))
          {
            newNode.appendChild(node.cloneNode(true));
            if (sequenceChoiceOrAllElement != null)
            {
              node.appendChild(sequenceChoiceOrAllElement);
            }
          }
          else
          {
            removeNodeAndWhitespace(node);
          }
        }
        else
        {
          newNode.appendChild(node.cloneNode(true)); 
        }
      }
    }
    complexTypeElement.replaceChild(newNode, contentModelElement);
  }

  public Element cloneElement(Element parent, Element sourceNode)
  {
    Document doc = parent.getOwnerDocument();
    String prefix = parent.getPrefix();
    prefix = prefix == null ? "" : prefix + ":";
    
    Element newNode = doc.createElementNS(XSDDOMHelper.XMLSchemaURI, prefix + sourceNode.getLocalName());

    if (sourceNode.hasChildNodes())
    {        
      NodeList nodes = sourceNode.getChildNodes();
      // use clones so we don't have a refresh problem
      for (int i = 0; i < nodes.getLength(); i++)
      {
        Node node = nodes.item(i);
        newNode.appendChild(node.cloneNode(true));
      }
    }
    return newNode;
//    parent.replaceChild(newNode, sourceNode);
  }

  public static boolean hasOnlyWhitespace(Node node)
  {
    NodeList list = node.getChildNodes();
    int length = list.getLength();
    boolean hasOnlyWhitespace = true;
    for (int i = 0; i < length; i++)
    {
      Node child = list.item(i);
      if (child.getNodeType() != Node.TEXT_NODE)
      {
        hasOnlyWhitespace = false;
        break;
      }
      else
      {
        String value = child.getNodeValue();
        String trimmedValue = value.trim();
        if (trimmedValue.length() != 0)
        {
          hasOnlyWhitespace = false;
        }
      }
    }  
    
    return hasOnlyWhitespace;
  }

  public static void removeNodeAndWhitespace(Node node)
  {
    Node parentNode = node.getParentNode();
    
    Node nextElement = getNextElementNode(node);
    Node previousElement = getPreviousElementNode(node);

    Node nextSibling = node.getNextSibling();
    if (nextSibling instanceof Text)
    {
      parentNode.removeChild(nextSibling);
    }

    if (parentNode != null)
    {
		  parentNode.removeChild(node);
    }

    if (nextElement != null)
    {
			formatChild(nextElement);
    }

		if (previousElement != null)
		{
			formatChild(previousElement);
		}
  }

	public static void formatChild(Node child)
	{
    if (child instanceof IDOMNode)
    {
      IDOMModel model = ((IDOMNode)child).getModel();
      try
      {
        // tell the model that we are about to make a big model change
        model.aboutToChangeModel();
        
	      IStructuredFormatProcessor formatProcessor = new FormatProcessorXML();
		    formatProcessor.formatNode(child);
      }
      finally
      {
        // tell the model that we are done with the big model change
        model.changedModel(); 
      }
    }
  }
  

  private static Node getNextElementNode(Node node)
  {
    Node next = node.getNextSibling();
    
    while (!(next instanceof Element) && next != null)
    {
      next = next.getNextSibling();
    }
    if (next instanceof Text)
    {
    	return null;
    }
    return next;
  }

	private static Node getPreviousElementNode(Node node)
	{
		Node previous = node.getPreviousSibling();
    
		while (!(previous instanceof Element) && previous != null)
		{
			previous = previous.getPreviousSibling();
		}
    if (previous instanceof Text)
    {
      return null;
    }
    return previous;
	}

 

  // issue (cs) what's this method supposed to do?
  // bizzare name
  public static boolean inputEquals(Object input, String tagname, boolean isRef)
  {
    if (input instanceof Element)
    {
      Element element = (Element) input;
      if (element.getLocalName().equals(tagname))
      {
        boolean refPresent = element.hasAttribute("ref");

        return refPresent == isRef;
      }
    }
    return false;
  }

 
}
