/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

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


/**
 * This is a hacked up dom writer stolen from a Xerces sample.
 * I'd like to use an exisitng 'generic DOM' writer
 * If anyone can find such a thing then please go ahead and junk this.
 *
 * @version
 */
public class DOMWriter
{
  protected boolean formattingEnabled = true;
   protected boolean outputDoctypeEnabled = true;
   protected PrintWriter out;
   protected int indent = 0;  

   public DOMWriter() throws UnsupportedEncodingException
   {
     this(System.out);
   }

   public DOMWriter(OutputStream outputSteam)
   {
     out = new PrintWriter(outputSteam);
   }

   public DOMWriter(Writer writer)
   {
     out = new PrintWriter(writer);
   }

   public void setFormattingEnabled(boolean enabled)
   {
     formattingEnabled = enabled;
   }

   public boolean getFormattingEnabled()
   {
     return formattingEnabled;
   }

   public void setOutputDoctypeEnabled(boolean enabled)
   {
     outputDoctypeEnabled = enabled;
   }

   public class XMLVisitor
   {
     protected boolean currentElementHasChildElements = false;

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

     public void visitDocument(Document document)
     {
       visitChildNodesHelper(document);
     }

     public void visitDocumentType(DocumentType doctype)
     {                       
       if (outputDoctypeEnabled)
       {
         String data = getDocumentTypeData(doctype);
         print("<!DOCTYPE " + data + ">");
       }
     }

     public void visitElement(Element element)
     {                                  
       if (!doShow(element))
         return;

       boolean parentElementHasChildNodes = currentElementHasChildElements;
       currentElementHasChildElements = hasChildElements(element);

       printIndent();
       print("<");
       print(element.getNodeName());
       visitAttributesHelper(element);

       boolean hasChildNodes = element.getChildNodes().getLength() > 0;                
       boolean isRootElement = element.getParentNode().getNodeType() == Node.DOCUMENT_NODE;
       if (hasChildNodes || isRootElement)
       {
         if (currentElementHasChildElements || isRootElement)
         {
           println(">");
         }
         else
         {
           print(">");
         }
         indent += 2;
         visitChildNodesHelper(element);
         indent -= 2;

         if (currentElementHasChildElements || isRootElement)
         {
           printIndent();
         }
         print("</");
         print(element.getNodeName());
         println(">");
       }
       else
       {
         println("/>");
       }

       currentElementHasChildElements = parentElementHasChildNodes;
     }

     public void visitAttr(Attr attr)
     {
       print(" ");
       print(attr.getNodeName());
       print("=\"");
       print(createPrintableCharacterData(attr.getValue()));
       print("\"");
     }

     public void visitText(Text text)
     {
       if (currentElementHasChildElements)
       {
         printIndent();
         print(createPrintableCharacterData(text.getNodeValue()));
         println();
       }
       else
       {
         print(createPrintableCharacterData(text.getNodeValue()));
       }
     }

     public void visitCDATASection(CDATASection cdataSection)
     {
     }

     public void visitComment(Comment comment)
     {
       printIndent();
       print("<!--");
       print(comment.getNodeValue());
       println("-->");
     }

     public void visitProcessingInstruction(ProcessingInstruction pi)
     {
       printIndent();
       print("<?");
       print(pi.getNodeName());
       print(" ");
       print(pi.getNodeValue());
       println("?>");
     }
             

     public boolean hasChildElements(Node node)
     {
       boolean result = false;
       NodeList children = node.getChildNodes();
       for (int i = 0; i < children.getLength(); i++)
       {
         if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
         {
           result = true;
           break;
         }
       }
       return result;
     }

     public void visitChildNodesHelper(Node node)
     {
       NodeList children = node.getChildNodes();
       for (int i = 0; i < children.getLength(); i++)
       {
         visitNode(children.item(i));
       }
     }

     public void visitAttributesHelper(Node node)
     {
       NamedNodeMap map = node.getAttributes();
       for (int i = 0; i < map.getLength(); i++ )
       {
         visitNode(map.item(i));
       }
     }
   }

  /** an ugly hack until I restruct this code a little
   *  
   */  
  protected boolean doShow(Element element)    
  {
    return true;
  }
                                               
  /** converts DOM text values to 'printable' values 
   *  - converts '&' to '&amp;'
   */
  protected String createPrintableCharacterData(String string)
  {              
    String result = "";
    int index = 0;                             
    while (true)
    {                                                 
      int ampersandIndex = string.indexOf("&", index);
      if (ampersandIndex != -1)
      {
        result += string.substring(index, ampersandIndex);
        result += "&amp;";
        index = ampersandIndex + 1; 
      }
      else
      {
        break;
      }
    } 
    result += string.substring(index);
    return result;
  }


  /** Prints the specified node, recursively. */
  public void print(Node node)
  {
    // is there anything to do?
    if (node != null)
    {
      XMLVisitor visitor = new XMLVisitor();
      visitor.visitNode(node);
    }
    out.flush();
  }

  /** a temporary hack to workaround our inability to create a DocumentType tag*/
  public void print(Document document, String grammarURL)
  {
    String systemId = null;
    if (grammarURL.endsWith("dtd"))
    {
      int lastSlashIndex = Math.max(grammarURL.lastIndexOf("/"), grammarURL.lastIndexOf("\\"));
      if (lastSlashIndex != -1)
      {
        systemId = grammarURL.substring(lastSlashIndex + 1);
      }
    }
    print(document, "UTF-8", grammarURL, null, systemId);

  }

  /** a temporary hack to workaround our inability to create a DocumentType tag*/
  public void print(Document document, String encoding, String grammarFileName, String publicId, String systemId)
  {
    out.println("<?xml version=\"1.0\"" + " encoding=\"" + encoding + "\"?>");  
    if (grammarFileName.endsWith(".dtd"))
    {
      String docTypeLine = "<!DOCTYPE " + document.getDocumentElement().getNodeName() + " ";
      if (publicId != null) 
      {
        docTypeLine += "PUBLIC \"" + publicId + "\" ";
        if (systemId != null)
        {
          docTypeLine += "\"" + systemId + "\" ";
        }
        docTypeLine += ">";
        out.println(docTypeLine);
      }
      else if (systemId != null)
      {
        docTypeLine += "SYSTEM \"" + systemId + "\" >";
        out.println(docTypeLine);
      }
    }
    print(document);
  }  

  public static String getDocumentTypeData(DocumentType doctype)
  {
    String data = doctype.getName();
    if (doctype.getPublicId() != null)
    {
      data += " PUBLIC \"" + doctype.getPublicId() + "\"";
      String systemId = doctype.getSystemId();
      if (systemId == null)
      {
        systemId = "";
      }
      data += " \"" + systemId + "\"";     
    }
    else
    {
      data += " SYSTEM \"" + doctype.getSystemId() + "\"";
    }
    return data;
  }     

  public void println()
  {
    if (formattingEnabled)
    {
      out.println();
    }
  }

  public void println(String string)
  {
    if (formattingEnabled)
    {
      out.println(string);
    }
    else
    {
      out.print(string);
    }
  }

  public void printIndent()
  {             
    if (formattingEnabled)
    {
      for (int i = 0; i < indent; i++)
      {
        out.print(" ");
      }
    }
  }       

  public void print(String string)
  {
    out.print(string);
  }
}

