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
package org.eclipse.wst.xsd.ui.internal.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.core.internal.format.IStructuredFormatProcessor;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.wst.xsd.ui.internal.widgets.SetBaseTypeDialog;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SetBaseTypeAction extends Action
{
  XSDSchema xsdSchema;
  Element element;
  String type = "";
  String derivedByString = "";
  XSDDOMHelper domHelper;
  
  /**
   * Constructor for SetBaseTypeAction.
   */
  public SetBaseTypeAction()
  {
    super();
    domHelper = new XSDDOMHelper();
  }
  /**
   * Constructor for SetBaseTypeAction.
   * @param arg0
   */
  public SetBaseTypeAction(String arg0)
  {
    super(arg0);
    domHelper = new XSDDOMHelper();
  }
  /**
   * Constructor for SetBaseTypeAction.
   * @param arg0
   * @param arg1
   */
  public SetBaseTypeAction(String arg0, ImageDescriptor arg1)
  {
    super(arg0, arg1);
    domHelper = new XSDDOMHelper();
  }
  /**
   * Constructor for SetBaseTypeAction.
   * @param arg0
   * @param arg1
   */
  public SetBaseTypeAction(String arg0, int arg1)
  {
    super(arg0, arg1);
    domHelper = new XSDDOMHelper();
  }

  
  public void setXSDSchema(XSDSchema schema)
  {
    this.xsdSchema = schema;    
  }

  public void setComplexTypeElement(Element element)
  {
    this.element = element;
  }
  
  public void setType(String type)
  {
    this.type = type; 
  }

  public void setDerivedBy(String derivedByString)
  {
    this.derivedByString = derivedByString;
  }

  public void run()
  {
    Display display = Display.getCurrent();
    // if it is null, get the default one
    display = display == null ? Display.getDefault() : display;
    Shell parentShell = display.getActiveShell();

    SetBaseTypeDialog dialog = new SetBaseTypeDialog(parentShell, xsdSchema, element);
    dialog.setBlockOnOpen(true);

    Element contentModelElement = domHelper.getContentModelFromParent(element);    
    if (contentModelElement != null)
    {
      // to set the current values to show in the dialog
      if (XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false) ||
          XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false))
      {
        Element derivedByElement = domHelper.getDerivedByElementFromComplexType(element);
        if (derivedByElement != null)
        {
          String currentBaseType = derivedByElement.getAttribute(XSDConstants.BASE_ATTRIBUTE);
          dialog.setCurrentBaseType(currentBaseType);
          dialog.setCurrentDerivedBy(derivedByElement.getLocalName());
        }
        else
        {
          dialog.setCurrentBaseType("");
          dialog.setCurrentDerivedBy("");
        }
      }
    }

    int result = dialog.open();
    
    if (result == Window.OK) 
    {
      type = dialog.getBaseType();
      derivedByString = dialog.getDerivedBy();
      performAction();
    }
  }

  XSDTypeDefinition newTypeDefinition = null;
  public XSDTypeDefinition getXSDTypeDefinition()
  {
    return newTypeDefinition;
  }

  public void performAction()
  {
    TypesHelper helper = new TypesHelper(xsdSchema);

    XSDConcreteComponent xsdComp = xsdSchema.getCorrespondingComponent(element);
    
    Element contentModelElement = domHelper.getContentModelFromParent(element);
    boolean contentModelExists = true;
    if (contentModelElement == null)
    {
      contentModelExists = false;
    }

//  get XSD component of the new type chosen
    newTypeDefinition = null;
    for (Iterator i = xsdSchema.getTypeDefinitions().iterator(); i.hasNext(); )
    {
      XSDTypeDefinition typeDef = (XSDTypeDefinition)i.next();
      if (typeDef.getQName().equals(type))
      {
        newTypeDefinition = typeDef;
        break;
      }
    }
   
    boolean needsComplexContent = false;
    boolean needsSimpleContent = false;

    if (helper.getBuiltInTypeNamesList().contains(type) ||
       helper.getUserSimpleTypeNamesList().contains(type))
//    if (newTypeDefinition instanceof XSDSimpleTypeDefinition)
    {
      needsSimpleContent = true; 
    }        
    else if (newTypeDefinition instanceof XSDComplexTypeDefinition)
    {
      needsComplexContent = true;
//      XSDComplexTypeDefinition newCTObj = (XSDComplexTypeDefinition)newTypeDefinition;
//      XSDContentTypeCategory category = newCTObj.getContentTypeCategory();
    }
    
    beginRecording(XSDEditorPlugin.getXSDString("_UI_LABEL_SET_BASE_TYPE"), element);
    String prefix = element.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");

    DOMAttribute attr = new DOMAttribute(XSDConstants.BASE_ATTRIBUTE, type);
    boolean hasChildrenElements = domHelper.hasElementChildren(element);
    if (!contentModelExists) // if no content model exists, then add the new nodes
    {
      if (helper.getBuiltInTypeNamesList().contains(type) ||
          helper.getUserSimpleTypeNamesList().contains(type))
      {
        updateModelAndDerivedByKind(prefix + XSDConstants.SIMPLECONTENT_ELEMENT_TAG, prefix + XSDConstants.EXTENSION_ELEMENT_TAG, attr);
      }
      else if (helper.getUserComplexTypeNamesList().contains(type))
      {
        if (derivedByString.equals("")) // default
        {
          derivedByString = XSDConstants.EXTENSION_ELEMENT_TAG;
        }
        Element derivedByElement = updateModelAndDerivedByKind(prefix + XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, prefix + derivedByString, attr);
        Element newModelGroupElement = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.SEQUENCE_ELEMENT_TAG);
        derivedByElement.appendChild(newModelGroupElement);
        formatChild(derivedByElement);
      }
    }
    else  // else there is a content model
    {
      if (type.equals(""))  // the chosen type is blank, ie. there is no base type
      {
        Element derivedByElement = domHelper.getDerivedByElementFromComplexType(element);
        Element sourceContentModelElement = domHelper.getContentModelFromParent(derivedByElement);
        // Retain the content model if there is one from the complex or simple content derive by element
        if (XSDDOMHelper.inputEquals(sourceContentModelElement, XSDConstants.SEQUENCE_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(sourceContentModelElement, XSDConstants.CHOICE_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(sourceContentModelElement, XSDConstants.ALL_ELEMENT_TAG, false))
        {      
          Element newNode = domHelper.cloneElement(element, sourceContentModelElement);
          element.replaceChild(newNode, contentModelElement);
          formatChild(element); 
        }
        else // otherwise just add the nodes
        {
          XSDDOMHelper.removeNodeAndWhitespace(contentModelElement);
          Element newSequenceElement = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + XSDConstants.SEQUENCE_ELEMENT_TAG);
          element.appendChild(newSequenceElement);
          formatChild(newSequenceElement);
        }
      }           
      else // a base type is specified
      {
        Element sequenceChoiceOrAllElement = null;  // copy the model to reposition it off of the new derived by element
        if (XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.SEQUENCE_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.CHOICE_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.ALL_ELEMENT_TAG, false))
        {
           sequenceChoiceOrAllElement = domHelper.cloneElement(element, contentModelElement);
        }

        if (needsComplexContent)
        {
          if (!(XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false)))
          {
            domHelper.changeContentModel(element, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, sequenceChoiceOrAllElement);
            contentModelElement = domHelper.getContentModelFromParent(element);
          }          
        }
        if (needsSimpleContent)
        {
          if (!(XSDDOMHelper.inputEquals(contentModelElement, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, false)))
          {
            // we don't want to append the element content to a simple content
            sequenceChoiceOrAllElement = null;
            domHelper.changeContentModel(element, XSDConstants.SIMPLECONTENT_ELEMENT_TAG, sequenceChoiceOrAllElement);
            contentModelElement = domHelper.getContentModelFromParent(element);
          }
        }

        Element derivedByElement = domHelper.getDerivedByElementFromComplexType(element);        
        if (derivedByElement == null)
        {
          if (derivedByString == null || (derivedByString != null && derivedByString.equals("")))
          {
            derivedByString = XSDConstants.EXTENSION_ELEMENT_TAG;  // since there is no derivedByElement
          }
          derivedByElement = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, derivedByString);
          contentModelElement.appendChild(derivedByElement);
          formatChild(contentModelElement);
          if (sequenceChoiceOrAllElement != null)
          {
            derivedByElement.appendChild(sequenceChoiceOrAllElement);
            formatChild(derivedByElement);
          }
        }
        else
        {
          if (helper.getBuiltInTypeNamesList().contains(type) ||
              helper.getUserSimpleTypeNamesList().contains(type))
          {
            derivedByString = XSDConstants.EXTENSION_ELEMENT_TAG;
            Element aContentModelElement = domHelper.getContentModelFromParent(derivedByElement);
            if (aContentModelElement != null)
            {
              XSDDOMHelper.removeNodeAndWhitespace(aContentModelElement);
            }
          }
          domHelper.changeDerivedByType(contentModelElement, derivedByString, type);
        }
        derivedByElement.setAttribute(attr.getName(), attr.getValue());
      }
    }
    
    xsdComp.setElement(element);
    endRecording(element);
  }

  private Element updateModelAndDerivedByKind(String modelType, String derivedByKind, DOMAttribute attr)
  {
    Element newContentModelElement = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, modelType);
    element.appendChild(newContentModelElement);

    Element newDerivedByElement = element.getOwnerDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, derivedByKind);
    newContentModelElement.appendChild(newDerivedByElement);

    newDerivedByElement.setAttribute(attr.getName(), attr.getValue());
    
    NodeList children = element.getChildNodes();
    int length = children.getLength();
    ArrayList nodesToRemove = new ArrayList();
    for (int i = 0; i < length; i++)
    {
      Node node = children.item(i);
      if (XSDDOMHelper.inputEquals(node, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false) ||
          XSDDOMHelper.inputEquals(node, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true) ||
          XSDDOMHelper.inputEquals(node, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true) ||
          XSDDOMHelper.inputEquals(node, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false))
      {
        newDerivedByElement.appendChild(node.cloneNode(true));
        nodesToRemove.add(node);
      }
    }
    for (Iterator i = nodesToRemove.iterator(); i.hasNext(); )
    {
      XSDDOMHelper.removeNodeAndWhitespace((Node)i.next());
    }
    
    formatChild(newContentModelElement);

    return newDerivedByElement;
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

  public DocumentImpl getDocument(Element element)
  {
    return (DocumentImpl) element.getOwnerDocument();
  }

  public void beginRecording(String description, Element element)
  {
    getDocument(element).getModel().beginRecording(this, description);
  }
  
  public void endRecording(Element element)
  {
    DocumentImpl doc = (DocumentImpl) getDocument(element);
    
    doc.getModel().endRecording(this);    
  }
}
