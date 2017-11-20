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

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

// todo.. move this class to another package (perhaps xmlutility)
//
public class DOMVisitor
{         
  public void visitNode(Node node)
  {
    switch (node.getNodeType())
    {
      case Node.ATTRIBUTE_NODE :
      {
        visitAttr((Attr)node);
        break;
      }
      case Node.CDATA_SECTION_NODE :
      {
        visitCDATASection((CDATASection)node);
        break;
      }
      case Node.COMMENT_NODE :
      {
        visitComment((Comment)node);
        break;
      }
      case Node.DOCUMENT_NODE :
      {
        visitDocument((Document)node);
        break;
      }
      case Node.DOCUMENT_TYPE_NODE :
      {
        visitDocumentType((DocumentType)node);
        break;
      }
      case Node.ELEMENT_NODE :
      {
        visitElement((Element)node);
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE :
      {
        visitProcessingInstruction((ProcessingInstruction)node);
        break;
      }
      case Node.TEXT_NODE :
      {
        visitText((Text)node);
        break;
      }
    }  
  }    
    
  protected void visitDocument(Document document)
  {                   
    visitChildNodesHelper(document);
  }

  protected void visitDocumentType(DocumentType doctype)
  {         

  }  

  protected void visitElement(Element element)
  {        
    visitAttributesHelper(element);
    visitChildNodesHelper(element);
  } 
 

  public void visitAttr(Attr attr)
  {                                  
  }

  protected void visitText(Text text)
  {    
  }

  protected void visitCDATASection(CDATASection cdataSection)
  {                       
  }     

  protected void visitComment(Comment comment)
  {                   
  }   

  protected void visitProcessingInstruction(ProcessingInstruction pi)
  {    
  }
 

  protected void visitChildNodesHelper(Node node)
  {
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++)
    {
      visitNode(children.item(i));
    }
  }

  protected void visitAttributesHelper(Node node)
  {
    NamedNodeMap map = node.getAttributes();
    for (int i = 0; i < map.getLength(); i++ )
    {
      visitNode(map.item(i));
    }
  }         
}
