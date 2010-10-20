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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.eclipse.osgi.util.TextProcessor;
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
         print("<!DOCTYPE " + data + ">"); //$NON-NLS-1$ //$NON-NLS-2$
       }
     }

     public void visitElement(Element element)
     {                                  
       if (!doShow(element))
         return;

       boolean parentElementHasChildNodes = currentElementHasChildElements;
       currentElementHasChildElements = hasChildElements(element);

       printIndent();
       print("<"); //$NON-NLS-1$
       print(element.getNodeName());
       visitAttributesHelper(element);

       boolean hasChildNodes = element.getChildNodes().getLength() > 0;                
       boolean isRootElement = element.getParentNode().getNodeType() == Node.DOCUMENT_NODE;
       if (hasChildNodes || isRootElement)
       {
         if (currentElementHasChildElements)
         {
           println(">"); //$NON-NLS-1$
         }
         else
         {
           print(">"); //$NON-NLS-1$
         }
         indent += 2;
         visitChildNodesHelper(element);
         indent -= 2;

         if (currentElementHasChildElements || isRootElement)
         {
           printIndent();
         }
         print("</"); //$NON-NLS-1$
         print(element.getNodeName());
         println(">"); //$NON-NLS-1$
       }
       else
       {
         println("/>"); //$NON-NLS-1$
       }

       currentElementHasChildElements = parentElementHasChildNodes;
     }

     public void visitAttr(Attr attr)
     {
       print(" "); //$NON-NLS-1$
       print(attr.getNodeName());
       print("=\""); //$NON-NLS-1$
       print(createPrintableCharacterData(attr.getValue()));
       print("\""); //$NON-NLS-1$
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
       print("<!--"); //$NON-NLS-1$
       print(comment.getNodeValue());
       println("-->"); //$NON-NLS-1$
     }

     public void visitProcessingInstruction(ProcessingInstruction pi)
     {
       printIndent();
       print("<?"); //$NON-NLS-1$
       print(pi.getNodeName());
       print(" "); //$NON-NLS-1$
       print(pi.getNodeValue());
       println("?>"); //$NON-NLS-1$
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
    String result = ""; //$NON-NLS-1$
    int index = 0;                             
    while (true)
    {                                                 
      int ampersandIndex = string.indexOf("&", index); //$NON-NLS-1$
      if (ampersandIndex != -1)
      {
        result += string.substring(index, ampersandIndex);
        result += "&amp;"; //$NON-NLS-1$
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
    if (grammarURL.endsWith("dtd")) //$NON-NLS-1$
    {
      int lastSlashIndex = Math.max(grammarURL.lastIndexOf("/"), grammarURL.lastIndexOf("\\")); //$NON-NLS-1$ //$NON-NLS-2$
      if (lastSlashIndex != -1)
      {
        systemId = grammarURL.substring(lastSlashIndex + 1);
      }
    }
    print(document, "UTF-8", grammarURL, null, systemId); //$NON-NLS-1$

  }

  /** a temporary hack to workaround our inability to create a DocumentType tag*/
  public void print(Document document, String encoding, String grammarFileName, String publicId, String systemId)
  {
	publicId = TextProcessor.process(publicId);
	systemId = TextProcessor.process(systemId);
    out.println("<?xml version=\"1.0\"" + " encoding=\"" + encoding + "\"?>");   //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    if (grammarFileName.endsWith(".dtd")) //$NON-NLS-1$
    {
      String docTypeLine = "<!DOCTYPE " + document.getDocumentElement().getNodeName() + " "; //$NON-NLS-1$ //$NON-NLS-2$
      if (publicId != null) 
      {
        docTypeLine += "PUBLIC \"" + publicId + "\" "; //$NON-NLS-1$ //$NON-NLS-2$
        if (systemId != null)
        {
          docTypeLine += "\"" + systemId + "\" "; //$NON-NLS-1$ //$NON-NLS-2$
        }
        docTypeLine += ">"; //$NON-NLS-1$
        out.println(docTypeLine);
      }
      else if (systemId != null)
      {
        docTypeLine += "SYSTEM \"" + systemId + "\" >"; //$NON-NLS-1$ //$NON-NLS-2$
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
      data += " PUBLIC \"" + doctype.getPublicId() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
      String systemId = doctype.getSystemId();
      if (systemId == null)
      {
        systemId = ""; //$NON-NLS-1$
      }
      data += " \"" + systemId + "\"";      //$NON-NLS-1$ //$NON-NLS-2$
    }
    else
    {
      data += " SYSTEM \"" + doctype.getSystemId() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
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
        out.print(" "); //$NON-NLS-1$
      }
    }
  }       

  public void print(String string)
  {
    out.print(string);
  }
}

