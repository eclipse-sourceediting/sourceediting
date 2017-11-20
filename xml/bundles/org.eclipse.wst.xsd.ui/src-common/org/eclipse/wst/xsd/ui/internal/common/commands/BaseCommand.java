/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.commands.Command;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class BaseCommand extends Command
{
  private static final String XML = "xml"; //$NON-NLS-1$

  XSDConcreteComponent addedXSDConcreteComponent;

  public BaseCommand()
  {
    super();
  }

  public BaseCommand(String label)
  {
    // Commands inherit their name from the associated action.
    // This label shows up in the undo menu and it would look ugly with the mnemonic in it.
    // Ideally, we'd have separate strings.

    super(label != null ? label.replaceFirst("&", "") : "");
  }
  
  public XSDConcreteComponent getAddedComponent()
  {
    return addedXSDConcreteComponent;
  }
  
  IDOMNode domNode;
  protected void beginRecording(Object element) {
    if (element instanceof IDOMNode)
    {
      domNode = (IDOMNode) element; 
      domNode.getModel().beginRecording(this, getUndoDescription());
    }
  }
  
  protected void endRecording()
  {
    if (domNode != null)
      domNode.getModel().endRecording(this);
  }
  
  protected String getUndoDescription() {
    return getLabel();
  }

  protected void formatChild(Element child)
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
 
  protected static void ensureSchemaElement(XSDSchema schema)
  {
    Document document = schema.getDocument();
    
    Element schemaElement = document.getDocumentElement();

    if (schemaElement == null)
    {
      String targetNamespace = getDefaultNamespace(schema);
      schema.setTargetNamespace(targetNamespace);
      Map qNamePrefixToNamespaceMap = schema.getQNamePrefixToNamespaceMap();
      qNamePrefixToNamespaceMap.put("tns", targetNamespace);      
      if (XSDEditorPlugin.getDefault().isQualifyXMLSchemaLanguage())
      {
        String prefix = XSDEditorPlugin.getDefault().getXMLSchemaPrefix();
        schema.setSchemaForSchemaQNamePrefix(prefix);
        qNamePrefixToNamespaceMap.put(prefix, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);      
      }
      else
      {
        qNamePrefixToNamespaceMap.put(null, XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);      
      }
      
      schema.updateElement();
      ensureXMLDirective(document);
    }
  }

  private static void ensureXMLDirective(Document document)
  {
    if (hasXMLDirective(document))
    {
      return;
    }
    
    Node firstChild = document.getFirstChild();
    ProcessingInstruction xmlDeclaration = getXMLDeclaration(document);
    document.insertBefore(xmlDeclaration, firstChild);
    Text textNode = document.createTextNode(System.getProperty("line.separator"));
    document.insertBefore(textNode, firstChild);
  }
  
  private static boolean hasXMLDirective(Document document)
  {
    Node firstChild = document.getFirstChild();
   
    if (firstChild == null)
    {
      return false;
    }
    
    if (firstChild.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE)
    {
      return false;
    }
    
    ProcessingInstruction processingInstruction  = (ProcessingInstruction)firstChild;
    
    if (!XML.equals(processingInstruction.getTarget())) 
    {
      return false;
    }
    
    return true;
  }

  private static ProcessingInstruction getXMLDeclaration(Document document)
  {
    Preferences preference = XMLCorePlugin.getDefault().getPluginPreferences();
    String charSet = preference.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
    if (charSet == null || charSet.trim().equals(""))
    {
      charSet = "UTF-8";
    }
    ProcessingInstruction xmlDeclaration = document.createProcessingInstruction(XML, "version=\"1.0\" encoding=\"" + charSet + "\"");
    return xmlDeclaration;
  }
  
  private static String getDefaultNamespace(XSDSchema schema)
  {
    String namespace = XSDEditorPlugin.getDefault().getXMLSchemaTargetNamespace();

    if (!namespace.endsWith("/"))
    {
      namespace = namespace.concat("/");
    }

    namespace += getFileName(schema) + "/";

    return namespace;

  }
  
  private static String getFileName(XSDSchema schema)
  {
    URI schemaURI = schema.eResource().getURI();
    IPath filePath = new Path(schemaURI.toString());
    return filePath.removeFileExtension().lastSegment().toString();
  }  
}
