/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.refactor.handlers;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.refactor.structure.MakeLocalElementGlobalCommand;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Node;


public class MakeLocalElementGlobalHandler extends RefactoringHandler
{

  public Object doExecute(ISelection selection, XSDSchema schema)
  {
    if (selection != null)
    {
      Object selectedObject = ((StructuredSelection)selection).getFirstElement();
      if (selectedObject instanceof XSDBaseAdapter)
      {
        selectedObject = ((XSDBaseAdapter)selectedObject).getTarget();
      }
      if (selectedObject instanceof XSDElementDeclaration)
      {
        run(selection, (XSDElementDeclaration)selectedObject);
      }
      else if (selectedObject instanceof Node)
      {
        Node node = (Node)selectedObject;
        XSDConcreteComponent concreteComponent = schema.getCorrespondingComponent(node);
        if (concreteComponent instanceof XSDElementDeclaration)
          run(selection, (XSDElementDeclaration)concreteComponent);
      }

    }
    return null;
  }

  public void run(ISelection selection, XSDElementDeclaration selectedComponent)
  {
    DocumentImpl doc = (DocumentImpl)selectedComponent.getElement().getOwnerDocument();
    doc.getModel().beginRecording(this, "MakeLocalElementGlobalHandler.text");
    MakeLocalElementGlobalCommand command = new MakeLocalElementGlobalCommand(selectedComponent);
    command.run();
    doc.getModel().endRecording(this);
  }
}