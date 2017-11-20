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
package org.eclipse.wst.xsd.ui.internal.refactor;


import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Node;


public abstract class RefactorEnablementTester extends PropertyTester
{

  public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
  {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window == null)
    {
      return false;
    }

    IWorkbenchPage activePage = window.getActivePage();
    if (activePage == null)
    {
      return false;
    }

    IEditorPart editor = activePage.getActiveEditor();
    if (editor == null)
    {
      return false;
    }

    XSDSchema schema = (XSDSchema)editor.getAdapter(XSDSchema.class);

    if (receiver instanceof IStructuredSelection)
    {
      IStructuredSelection fStructuredSelection = (IStructuredSelection)receiver;
      receiver = fStructuredSelection.getFirstElement();

      if (receiver instanceof XSDBaseAdapter)
      {
        receiver = ((XSDBaseAdapter)receiver).getTarget();
      }

      if (receiver instanceof XSDConcreteComponent)
      {
        return canEnable((XSDConcreteComponent)receiver, schema);
      }
      else if (receiver instanceof Node)
      {
        Node node = (Node)receiver;
        if (schema != null)
        {
          XSDConcreteComponent concreteComponent = schema.getCorrespondingComponent(node);
          return canEnable(concreteComponent, schema);
        }
      }

      return true;
    }
    return false;
  }

  protected abstract boolean canEnable(XSDConcreteComponent selectedObject, XSDSchema schema);

}