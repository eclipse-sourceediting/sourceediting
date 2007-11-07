/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.util.List;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AddDocumentationCommand extends BaseCommand
{
  XSDAnnotation xsdAnnotation;
  XSDConcreteComponent input;
  String newValue, oldValue;
  boolean documentationExists;
  Element documentationElement;

  public AddDocumentationCommand(String label, XSDAnnotation xsdAnnotation, XSDConcreteComponent input, String newValue, String oldValue)
  {
    super(label);
    this.xsdAnnotation = xsdAnnotation;
    this.input = input;
    this.newValue = newValue;
    this.oldValue = oldValue;
  }

  public void execute()
  {
    if (input instanceof XSDSchema)
    {
      ensureSchemaElement((XSDSchema)input);
    }
    
    try
    {
      beginRecording(input.getElement());

      xsdAnnotation = XSDCommonUIUtils.getInputXSDAnnotation(input, true);
      Element element = xsdAnnotation.getElement();

      List documentationList = xsdAnnotation.getUserInformation();
      documentationElement = null;
      documentationExists = false;
      if (documentationList.size() > 0)
      {
        documentationExists = true;
        documentationElement = (Element) documentationList.get(0);
      }

      if (documentationElement == null)
      {
        documentationElement = xsdAnnotation.createUserInformation(null);
        element.appendChild(documentationElement);
        formatChild(documentationElement);
        // Defect in model....I create it but the model object doesn't appear
        // to be updated
        xsdAnnotation.updateElement();
        xsdAnnotation.setElement(element);
      }

      if (documentationElement.hasChildNodes())
      {
        if (documentationElement instanceof IDOMElement)
        {
          IDOMElement domElement = (IDOMElement) documentationElement;

          Node firstChild = documentationElement.getFirstChild();
          Node lastChild = documentationElement.getLastChild();
          int start = 0;
          int end = 0;

          // IDOMModel model = domElement.getModel();
          // IDOMDocument doc = model.getDocument();
          IDOMNode first = null;
          if (firstChild instanceof IDOMNode)
          {
            first = (IDOMNode) firstChild;
            start = first.getStartOffset();
          }
          if (lastChild instanceof IDOMNode)
          {
            IDOMNode last = (IDOMNode) lastChild;
            end = last.getEndOffset();
          }

          if (domElement != null)
          {
            oldValue = domElement.getModel().getStructuredDocument().get(start, end - start);
            domElement.getModel().getStructuredDocument().replaceText(documentationElement, start, end - start, newValue);
          }
        }
      }
      else
      {
        if (newValue.length() > 0)
        {
          oldValue = ""; //$NON-NLS-1$
          Node childNode = documentationElement.getOwnerDocument().createTextNode(newValue);
          documentationElement.appendChild(childNode);
        }
      }
    }
    catch (Exception e)
    {

    }
    finally
    {
      endRecording();
    }
  }

  public void undo()
  {
    super.undo();
  }
}
