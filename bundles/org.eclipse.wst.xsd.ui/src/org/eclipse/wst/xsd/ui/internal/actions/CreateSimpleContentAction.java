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
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @version   1.0
 * @author
 */
public class CreateSimpleContentAction extends CreateElementAction
{
  XSDSchema xsdSchema;
  //IDocument document;

  /**
   * Constructor for CreateSimpleContentAction.
   */
  public CreateSimpleContentAction()
  {
    super();
  }
  /**
   * Constructor for CreateSimpleContentAction.
   * @param text
   */
  public CreateSimpleContentAction(String text)
  {
    super(text);
  }
  /**
   * Constructor for CreateSimpleContentAction.
   * @param text
   * @param XSDSchema
   */
  public CreateSimpleContentAction(String text, XSDSchema xsdSchema)
  {
    super(text);
    this.xsdSchema = xsdSchema;
  }
  /**
   * Constructor for CreateSimpleContentAction.
   * @param text
   * @param image
   */
  public CreateSimpleContentAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public Element createAndAddNewChildElement()
  {
    Element childNode = super.createAndAddNewChildElement();

    String prefix = parentNode.getPrefix();
    prefix = (prefix == null) ? "" : (prefix + ":");
    Element derivedByNode = getDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + "restriction");
    childNode.appendChild(derivedByNode);
    Element sequence = null;

    if (XSDDOMHelper.inputEquals(childNode, XSDConstants.COMPLEXCONTENT_ELEMENT_TAG, false))
    {
      sequence = getDocument().createElementNS(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, prefix + "sequence");
      derivedByNode.appendChild(sequence);
    }

    // now add the required base attribute for the derived by node
    TypesHelper typesHelper = new TypesHelper(xsdSchema);
    List listOfCT = typesHelper.getUserComplexTypeNamesList();
    String firstType = "";
    if (listOfCT.size() > 0)
    {
      firstType = (String)(listOfCT).get(0);
    }
    DOMAttribute attr = new DOMAttribute(XSDConstants.BASE_ATTRIBUTE, firstType);
    derivedByNode.setAttribute(attr.getName(), attr.getValue());

    formatChild(derivedByNode);
    if (sequence != null)
    {
      formatChild(sequence);
      formatChild(derivedByNode);
    }
    formatChild(childNode);

    
    return childNode;
  }

  /*
   * @see IAction#run()
   */
  public void run()
  {
    ArrayList message = new ArrayList();
    beginRecording(getDescription());

    Element child = createAndAddNewChildElement();
    endRecording();

    NodeList children = parentNode.getChildNodes();
/*
    for (int i=0; i < children.getLength(); i++)
    {
      Node aChild = children.item(i);
      if (aChild != null && aChild instanceof Element)
      {
        if (XSDDOMHelper.inputEquals((Element)aChild, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals((Element)aChild, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true))
        {
          // REMOVE ATTRIBUTES AND ATTRIBUTE GROUP REFS FROM COMPLEX TYPES LIST OF CHILDREN
          message.addElement(new ModelMessage
                                 (XSDPlugin.getSchemaString("_INFO_REMOVE_ATTRIBUTE_FROM") +
              " <" + parentNode.getAttribute("name") + ">", aChild));
        }
      }
    }
    domainModel.createMarkers(message);
*/
    for (int i=0; i < children.getLength(); i++)
    {
      Node aChild = children.item(i);
      if (aChild != null && aChild instanceof Element)
      {
        if (XSDDOMHelper.inputEquals((Element)aChild, XSDConstants.ATTRIBUTE_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals((Element)aChild, XSDConstants.ATTRIBUTE_ELEMENT_TAG, true) ||
            XSDDOMHelper.inputEquals((Element)aChild, XSDConstants.ANYATTRIBUTE_ELEMENT_TAG, false) ||
            XSDDOMHelper.inputEquals((Element)aChild, XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG, true))
        {
          // REMOVE ATTRIBUTES AND ATTRIBUTE GROUP REFS FROM COMPLEX TYPES LIST OF CHILDREN
// KCPort TODO
//          ErrorMessage aTask = new ErrorMessage();
//          Node parent = aChild;
//          if (parent instanceof NodeImpl)
//          {
//            aTask.setModelObject(parent);
//          }
//          aTask.setLocalizedMessage(XSDPlugin.getSchemaString("_INFO_REMOVE_ATTRIBUTE_FROM") + " <" + parentNode.getAttribute("name") + ">");
//          message.add(aTask);
        }
      }
    }    
  }
}
