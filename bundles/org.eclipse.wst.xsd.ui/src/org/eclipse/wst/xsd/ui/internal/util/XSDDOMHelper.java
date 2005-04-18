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
package org.eclipse.wst.xsd.ui.internal.util;

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

public class XSDDOMHelper
{
  public static final int LENGTH_FACET        =  1;
  public static final int MIN_LENGTH_FACET    =  2;
  public static final int MAX_LENGTH_FACET    =  3;
  public static final int PATTERN_FACET       =  4;
  public static final int ENUM_FACET          =  5;
  public static final int WHITE_SPACE_FACET   =  6;

  public static final int MAX_INCLUSIVE_FACET =  7;
  public static final int MAX_EXCLUSIVE_FACET =  8;
  public static final int MIN_INCLUSIVE_FACET =  9;
  public static final int MIN_EXCLUSIVE_FACET =  10;

  public static final int TOTAL_DIGITS_FACET     = 11;
  public static final int FRACTION_DIGITS_FACET  = 12;

  public static final int N_FACETS            = 13;

  public static String[][] dataType =
  {
    //
    // Table format:
    // Type
    //  Length, MinLength, MaxLength, Pattern, Enumeration,whiteSpace
    //  MaxInclusive, MaxExclusive, MinInclusive, MinExclusive, TotalDigits, FractionDigits
    //

    // 0
    { "anySimpleType",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 1
    { "anyType",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },


    // 2
    { "anyURI",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 3
    { "base64Binary",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 4
    { "boolean",
        "N", "N", "N", "Y", "N", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 5
    { "byte",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 6
    { "date",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 7
    { "dateTime",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 8
    { "decimal",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },


    // 9
    { "double",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 10
    { "duration",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 11
    { "ENTITY",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 12
    { "ENTITIES",
        "Y", "Y", "Y", "N", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 13
    { "float",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 14
    { "gDay",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 15
    { "gMonth",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 16
    { "gMonthDay",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 17
    { "gYear",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 18
    { "gYearMonth",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 19
    { "hexBinary",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 20
    { "ID",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 21
    { "IDREF",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 22
    { "IDREFS",
        "Y", "Y", "Y", "N", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 23
    { "int",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 24
    { "integer",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 25
    { "language",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 26
    { "long",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 27
    { "Name",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },


    // 28
    { "NCName",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 29
    { "negativeInteger",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 30
    { "NMTOKEN",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 31
    { "NMTOKENS",
        "Y", "Y", "Y", "N", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 32
    { "nonNegativeInteger",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 33
    { "nonPositiveInteger",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 34
    { "normalizedString",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",

    },

    // 35
    { "NOTATION",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",

    },

    // 36
    { "positiveInteger",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 37
    { "QName",
        "N", "N", "N", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 38
    { "short",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 39
    { "string",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 40
    { "time",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "N", "N",
    },

    // 41
    { "token",
        "Y", "Y", "Y", "Y", "Y", "Y",
        "N", "N", "N", "N", "N", "N",
    },

    // 42
    { "unsignedByte",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 43
    { "unsignedInt",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 44
    { "unsignedLong",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

    // 45
    { "unsignedShort",
        "N", "N", "N", "Y", "Y", "Y",
        "Y", "Y", "Y", "Y", "Y", "Y",
    },

  };

  public static String XMLSchemaURI = "http://www.w3.org/2001/XMLSchema";

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
   
    String name = null;
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

  public static String SIMPLE_TYPE = "Simple";
  public static String USER_SIMPLE_TYPE = "simpleType";
  public static String USER_COMPLEX_TYPE = "complexType";
/*
  public void setElementType(Element element, String type)
  {
    Document doc = element.getOwnerDocument();
    if (type.equals(SIMPLE_TYPE))
    {
      removeChild(element, USER_SIMPLE_TYPE);
      removeChild(element, USER_COMPLEX_TYPE);
      element.setAttribute("type","xsd:string");
      return;
    }
    else if (type.equals(USER_SIMPLE_TYPE))
    {
      removeChild(element, USER_COMPLEX_TYPE);
    }
    else
    {
      removeChild(element, USER_SIMPLE_TYPE);
    }
    element.removeAttribute("type");
    element.appendChild(doc.createElement("xsd:"+type));
  }

  public String getElementType(Element element)
  {
    String tagName = element.getLocalName();

    if (tagName.equals(XSDConstants.ELEMENT_ELEMENT_TAG) ||
        tagName.equals(XSDConstants.ATTRIBUTE_ELEMENT_TAG))
    {
      if (element.hasAttribute("type"))
      {
        return SIMPLE_TYPE;
      }
      NodeList nodes = element.getElementsByTagNameNS(XMLSchemaURI, USER_SIMPLE_TYPE);
      if (nodes.getLength() > 0)
      {
        return USER_SIMPLE_TYPE;
      }
      nodes = element.getElementsByTagNameNS(XMLSchemaURI, USER_COMPLEX_TYPE);
      if (nodes.getLength() > 0)
      {
        return USER_COMPLEX_TYPE;
      }
    }
    return "";
  }
*/
  public void removeDerivedByElement(Element element)
  {
    removeChild(element, XSDConstants.RESTRICTION_ELEMENT_TAG);
    removeChild(element, XSDConstants.EXTENSION_ELEMENT_TAG);
  }

  public String getBaseType(Element element) // for SimpleContent and ComplexContent
  {
    Node restrictionChild = getChildNode(element, "restriction");
    Node extensionChild = getChildNode(element, "extension");
    String baseType = "";
    if (restrictionChild != null)
    {
      if (restrictionChild instanceof Element)
      {
        baseType = ((Element)restrictionChild).getAttribute("base");
//        String prefix = element.getPrefix();
//        if (prefix != null && prefix.length() > 0)
//        {
//          baseType = baseType.substring(baseType.indexOf(prefix) + prefix.length() + 1);
//        }
      }
    }
    else if (extensionChild != null) // should be one or the other
    {
      if (extensionChild instanceof Element)
      {
        baseType = ((Element)extensionChild).getAttribute("base");
//        String prefix = element.getPrefix();
//        if (prefix != null && prefix.length() > 0)
//        {
//          baseType = baseType.substring(baseType.indexOf(prefix) + prefix.length() + 1);
//        }
      }
    }
    return baseType;
  }

  public void setDerivedByBaseType(Element element, String derivedByType, String type)
  {
    Document doc = element.getOwnerDocument();

    Element derivedByElement = getDerivedByElement(element);
    if (derivedByElement != null)
    {
      derivedByElement.setAttribute("base", type);
    }
    else  // there really should be one already...base is required.
    {
      Element newElement = doc.createElement(derivedByType);
      newElement.setAttribute("base", type);
      element.appendChild(newElement);
    }
  }

  public void changeDerivedByType(Element element, String derivedByType, String type)
  {
    Document doc = element.getOwnerDocument();

    String prefix = element.getPrefix();
    prefix = prefix == null ? "" : prefix + ":";

    Element derivedByElement = getDerivedByElement(element);
    
    if (derivedByElement.getLocalName().equals(derivedByType))
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
      newNode.setAttribute("base", type);
      element.replaceChild(newNode, derivedByElement);

    }
  }

  public void setSimpleContentType(Element element, String type)
  {
    String contentTypeName = element.getLocalName();

    if (contentTypeName.equals(XSDConstants.UNION_ELEMENT_TAG))
    {
      element.setAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, type);
    }
    else if (contentTypeName.equals(XSDConstants.LIST_ELEMENT_TAG))
    {
      element.setAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE, type);
    }
    else if (contentTypeName.equals(XSDConstants.RESTRICTION_ELEMENT_TAG))
    {
      element.setAttribute(XSDConstants.BASE_ATTRIBUTE, type);
    }
  }

  public void removeSimpleTypeContent(Element element)
  {
    String contentTypeName = element.getLocalName();
    if (contentTypeName.equals(XSDConstants.UNION_ELEMENT_TAG))
    {
      element.removeAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);
    }
    else if (contentTypeName.equals(XSDConstants.LIST_ELEMENT_TAG))
    {
      element.removeAttribute(XSDConstants.ITEMTYPE_ATTRIBUTE);
    }
    else if (contentTypeName.equals(XSDConstants.RESTRICTION_ELEMENT_TAG))
    {
      element.removeAttribute(XSDConstants.BASE_ATTRIBUTE);
    }
  }

  public String getDerivedByName(Element element)
  {
    Node restrictionChild = getChildNode(element, "restriction");
    Node extensionChild = getChildNode(element, "extension");
    if (restrictionChild != null)
    {
      return "restriction";
    }
    if (extensionChild != null)
    {
      return "extension";
    }
    return "";
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

  /**
   * Get the derived by node given the ComplexType node
   * Returns the first one, if say, the INVALID schema has more than one
   */
  public Element getDerivedByElementFromComplexType(Element element)
  {
    NodeList nl = element.getChildNodes();
    int childNumber = 0;
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
    int childNumber = 0;
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

  public boolean hasElementChildren(Element parentNode)
  {
    boolean hasChildrenElements = false;
    if (parentNode != null && parentNode.hasChildNodes())
    {
      NodeList nodes = parentNode.getChildNodes();
      for (int i = 0; i < nodes.getLength(); i++)
      {
        if (nodes.item(i) instanceof Element)
        {
          hasChildrenElements = true;
          break;
        }
      }
    }
    return hasChildrenElements;
  }

  public void removeChild(Element node, String childName)
  {
    Node child = getChildNode(node,childName);
    if (child != null)
    {
      node.removeChild(child);
    }
  }

  public static boolean isFacet(Object obj)
  {
    if (XSDDOMHelper.inputEquals(obj, XSDConstants.LENGTH_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.MINLENGTH_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.MAXLENGTH_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.WHITESPACE_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.MAXINCLUSIVE_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.MAXEXCLUSIVE_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.MININCLUSIVE_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.MINEXCLUSIVE_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.TOTALDIGITS_ELEMENT_TAG, false) ||
        XSDDOMHelper.inputEquals(obj, XSDConstants.FRACTIONDIGITS_ELEMENT_TAG, false))
    {
      return true;
    }
    return false;
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
    Node previousSibling = node.getPreviousSibling();

		parentNode.removeChild(node);

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
  
  public static Node getLastElementNode(Node parent)
  {
    Node lastChild = parent.getLastChild();
    
    while (!(lastChild instanceof Element) && lastChild != null)
    {
      lastChild = lastChild.getPreviousSibling();
    }
    return lastChild;
  }

  public static Node getNextElementNode(Node node)
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

	public static Node getPreviousElementNode(Node node)
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

  public static void moveNode(Node referenceNode, Node nodeToMove, boolean isBefore)
  {
    // this assumes that the referenceNode and node to move have the same parent
    Node parent = referenceNode.getParentNode();

    // Get reference nodes next and previous text strings
    String referenceNodeNextString = "";
    String referenceNodePreviousString = "";
    if (referenceNode != null)
    {
      Node referenceNodeNextSibling = referenceNode.getNextSibling();
      Node referenceNodePreviousSibling = referenceNode.getPreviousSibling();
      if (referenceNodeNextSibling instanceof Text)
      {
        referenceNodeNextString = ((Text)referenceNodeNextSibling).getData();
      }
      if (referenceNodePreviousSibling instanceof Text)
      {
        referenceNodePreviousString = ((Text)referenceNodePreviousSibling).getData();
      }
    }
    // Get the dragged node's next and previous text strings
    Node nodeToMoveNextSibling = nodeToMove.getNextSibling();
    Node nodeToMovePreviousSibling = nodeToMove.getPreviousSibling();
    Node nodeToMoveNextText = null;
    String nodeToMoveNextString = "";
    String nodeToMovePreviousString = "";
    if (nodeToMoveNextSibling instanceof Text)
    {
      nodeToMoveNextText = (Text)nodeToMoveNextSibling;
      nodeToMoveNextString = ((Text)nodeToMoveNextSibling).getData();
    }
    if (nodeToMovePreviousSibling instanceof Text)
    {
      nodeToMovePreviousString = ((Text)nodeToMovePreviousSibling).getData();
    }

    // Get the last element's next and previous text strings
    Node lastElement = getLastElementNode(parent);
    Node lastElementNextSibling = lastElement.getNextSibling();
    Node lastElementPreviousSibling = lastElement.getPreviousSibling();
    String lastElementNextString = "";
    String lastElementPreviousString = "";		
    if (lastElementNextSibling instanceof Text)
    {
      lastElementNextString = ((Text)lastElementNextSibling).getData();
    }
    if (lastElementPreviousSibling instanceof Text)
    {
      lastElementPreviousString = ((Text)lastElementPreviousSibling).getData();
    }

    boolean isLastElement = false; // whether the last element is dragged/moved
    if (lastElement == nodeToMove)
    {
      isLastElement = true;
    }
		
    // defect 221056 this test is required or else the node will
    // be removed from the tree and the insert will fail
    if (referenceNode != nodeToMove)
    {
      parent.removeChild(nodeToMove);
      if (referenceNode != null)
      {
        if (!isBefore)
        {
          referenceNode = getNextElementNode(referenceNode);
//        referenceNode = referenceNode.getNextSibling();
        }
      }

      if (referenceNode != null)
      {
        insertBefore(nodeToMove, referenceNode);
      }
      else
      {
        parent.appendChild(nodeToMove);
      }

      Node newLastElement = getLastElementNode(parent);
      if (referenceNode != null)
      {
        if (referenceNode != newLastElement) 
        {
          if (!isLastElement)
          {
            setTextData(referenceNode, nodeToMoveNextString, nodeToMovePreviousString);
          }
        }
        setTextData(nodeToMove, referenceNodeNextString, referenceNodePreviousString);
      }
      // Remove the empty space left by the dragged node
      if (nodeToMoveNextText != null)
      {
    	  parent.removeChild(nodeToMoveNextText);
      }
      // special case for the last element
      if ((newLastElement == nodeToMove) || isLastElement)
      {
        setTextData(newLastElement, lastElementNextString, lastElementPreviousString);
      }
    }
  }

  public static void setTextData(Node target, String nextText, String previousText)
  {
    Node parent = target.getParentNode();
    Node nextSibling = target.getNextSibling();
    Node previousSibling = target.getPreviousSibling();
    if (nextSibling instanceof Text)
    {
      ((Text)nextSibling).setData(nextText);	
    }
    if (nextSibling == null || nextSibling instanceof Element)
    {
      Text textNode = parent.getOwnerDocument().createTextNode("");
      textNode.setData(nextText);
      if (nextSibling != null)
      {
        parent.insertBefore(textNode, nextSibling);
      }
      else
      {
        parent.insertBefore(textNode, getNextElementNode(target));
      }
    }				

    if (previousSibling instanceof Text)
    {
      ((Text)previousSibling).setData(previousText);
    }
    if (previousSibling == null || previousSibling instanceof Element)
    {
      Text textNode = parent.getOwnerDocument().createTextNode("");
      textNode.setData(previousText);
      parent.insertBefore(textNode, target);
    }				
  }

  public static void insertBefore(Node nodeToInsert, Node referenceNode)
  {
    // this assumes that the referenceNode and node to move have the same parent
    Node parent = referenceNode.getParentNode();

    Node previousSibling = referenceNode.getPreviousSibling();
    parent.insertBefore(nodeToInsert, referenceNode);
  }

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

  public static void updateElementToNotAnonymous(Element element)
  {
    if (element != null)
    {
      NodeList children = element.getChildNodes();
      if (children != null)
      {
        for (int i = 0; i < children.getLength(); i++)
        {
          Node node = (Node)children.item(i);
          if (node instanceof Element)
          {
            if (node.getLocalName().equals(XSDConstants.SIMPLETYPE_ELEMENT_TAG) ||
                node.getLocalName().equals(XSDConstants.COMPLEXTYPE_ELEMENT_TAG))
            {
              XSDDOMHelper.removeNodeAndWhitespace(node);
              i=0;
            }
          }
        }
      }
    }
  }
  
  public static boolean isAttributeRef(Element ct, String attrName, String ns)
  {
    NodeList list = ct.getChildNodes();
    int length = list.getLength();
    for (int i = 0; i < length; i++)
    {
      if (list.item(i) instanceof Element)
      {
        Element aChild = (Element)list.item(i);
        if (aChild.getLocalName().equals(XSDConstants.ATTRIBUTE_ELEMENT_TAG))
        {
          if (aChild.hasAttribute(XSDConstants.REF_ATTRIBUTE))
          {
            String refValue = aChild.getAttribute(XSDConstants.REF_ATTRIBUTE);
            if (refValue.equals(attrName))
            {
              return true;
            }
          }
        }
      }
      
    }
    
    return false;
  }
}
