/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.ui.tests;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Kihup Boo
 */
public class XMLDiff
{
  private List errorMessages = new ArrayList();
  private boolean DIFF_ELEMENT_NODE_ONLY = false;
  private String fileType = "";
  public boolean diff(String file1, String file2, String fileType) throws ParserConfigurationException, SAXException, IOException
  {
    return diff(file1,file2, fileType, false);
  }
  
  public boolean diff(String file1, String file2, String fileType, boolean elementOnly) throws ParserConfigurationException, SAXException, IOException
  {
    Document doc1 = getDocument(file1);
    Document doc2 = getDocument(file2);
  	DIFF_ELEMENT_NODE_ONLY = elementOnly;
    return diff(doc1,doc2, fileType);
  }
  
  public boolean diff(Document doc1, Document doc2, String fileType)
  {
    Element root1 = doc1.getDocumentElement();
    Element root2 = doc2.getDocumentElement();
	this.fileType = fileType;
    return compareNodes(root1,root2);
  }
  
  private Document getDocument(String uri) throws ParserConfigurationException, SAXException, IOException
  {
    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document doc = builder.parse(uri);
    return doc;
  }
  
  private boolean compareNodes(Node node1, Node node2)
  {
  	if (DIFF_ELEMENT_NODE_ONLY)
  	{
      // Compare only element nodes in the children.
      filterNonElementNodes(node1);
      filterNonElementNodes(node2);
  	}
  	
    if (node1.getNodeType() != node2.getNodeType()
        || node1.getNodeName() != node2.getNodeName())
    {
    	errorMessages.add(this.fileType + ": Type or name different: " + node1.getNodeName() + " " + node2.getNodeName() + ".");
      println("Node type or node name is different:");
      println("Node 1: " + node1.getNodeName());
      println("Node 2: " + node2.getNodeName());
      return false;
    }
    
    if (!compareAttributes(node1,node2))
      return false;
    
    NodeList nodeList1 = node1.getChildNodes();
    NodeList nodeList2 = node2.getChildNodes();
    if (nodeList1.getLength() != nodeList2.getLength())
    {
    	errorMessages.add(this.fileType + ": Number of children different: " + node1.getNodeName() + ".");
      println("The number of children different:");
      //println("Node 1: " + nodeList1.getLength());
      //println("Node 2: " + nodeList2.getLength());
      println("Node 1: " + node1.getNodeName());
      println("Node 2: " + node2.getNodeName());
      return false;
    }
    
    boolean result = true;
    int length = nodeList1.getLength();
    for (int i=0; i<length; i++)
    {
      result = compareNodes(nodeList1.item(i),nodeList2.item(i));
      if (!result)
        return false;
    }
  	return true;
  }
  
  private void filterNonElementNodes(Node node)
  {
    Node firstChild = node.getFirstChild();
    while (firstChild.getNodeType() != Node.ELEMENT_NODE)
    {
      node.removeChild(firstChild);
      firstChild = node.getFirstChild();
    }
    
    Node sibling = firstChild.getNextSibling();
    Node deleteMe = null;
    while (sibling != null)
    {
      if (sibling.getNodeType() != Node.ELEMENT_NODE)
      {
      	deleteMe = sibling;
      	sibling = sibling.getNextSibling();
        node.removeChild(deleteMe);
      }
      
    }
  }
  
  private boolean compareAttributes(Node node1, Node node2)
  {
    NamedNodeMap nodeMap1 = node1.getAttributes();
    NamedNodeMap nodeMap2 = node2.getAttributes();
    
    if (nodeMap1 == null || nodeMap2 == null)
    {
      if (nodeMap1 == null && nodeMap2 == null)
        return true;
      else
        return false;
    }
    
    if (nodeMap1.getLength() != nodeMap2.getLength())
    {
    	errorMessages.add(this.fileType + ": Number of attributes different: " + node1.getNodeName() + ".");
    	println("The number of attributes is different:");
        println("Node 1: " + node1.getNodeName());
        println("Node 2: " + node2.getNodeName());
        return false;
    }
    
    Node attrNode1 = null;
    Node attrNode2 = null;
    int length = nodeMap1.getLength();
    for (int i=0; i<length; i++)
    {
      attrNode1 = nodeMap1.item(i);
      attrNode2 = nodeMap2.getNamedItem(attrNode1.getNodeName());
      if (attrNode2 == null)
      {
      	errorMessages.add(this.fileType + ": Attribute not found: " + node1.getNodeName() + ".");
    	println("The attribute is not found in Node 2: " + attrNode1.getNodeName());
        println("Node 1: " + node1.getNodeName());
        println("Node 2: " + node2.getNodeName());
        return false;
      }
      else if (!attrNode1.getNodeValue().equals(attrNode2.getNodeValue()))
      {
      	errorMessages.add(this.fileType + ": Attribute values different: " + attrNode1.getNodeValue() + " " + attrNode2.getNodeValue() + ".");
    	println("The attribute values are different:");
        println("Node 1: " + node1.getNodeName() + "," + attrNode1.getNodeValue());
        println("Node 2: " + node2.getNodeName() + "," + attrNode2.getNodeValue());
        return false;      
      }
    }
  	return true;
  }
  
  private void println(String s)
  {
    //out.println(s);
  }

	/**
	 * @return Returns the errorMessages.
	 */
	public List getErrorMessages()
	{
		return errorMessages;
	}
}
