/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.providers;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

public class XSDSectionLabelProvider extends LabelProvider
{
  /**
   * 
   */
  public XSDSectionLabelProvider()
  {
    super();
  }

  /**
   * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
   */
  public Image getImage(Object object)
  {
    if (object == null || object.equals(StructuredSelection.EMPTY))
    {
      return null;
    }
    Image result = null;
    if (object instanceof StructuredSelection)
    {
      Object selected = ((StructuredSelection) object).getFirstElement();

      if (selected instanceof XSDConcreteComponent)
      {
        XSDBaseAdapter adapter = (XSDBaseAdapter)XSDAdapterFactory.getInstance().adapt((XSDConcreteComponent)selected);
        if (adapter instanceof ITreeElement)
        {
          return ((ITreeElement)adapter).getImage();
        }
        
      }
    }
    return result;
  }

  /**
   * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
   */
  public String getText(Object object)
  {
    if (object == null || object.equals(StructuredSelection.EMPTY))
    {
      return "No items selected";
    }

    String result = null;

    boolean isReference = false;
    Object selected = null;
    if (object instanceof StructuredSelection)
    {
      selected = ((StructuredSelection) object).getFirstElement();

      if (selected instanceof XSDConcreteComponent)
      {
        if (selected instanceof XSDElementDeclaration)
        {
          XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) selected;
          if (xsdElementDeclaration.isElementDeclarationReference())
          {
            isReference = true;
          }
        } else if (selected instanceof XSDAttributeDeclaration)
        {
          if (((XSDAttributeDeclaration) selected).isAttributeDeclarationReference())
          {
            isReference = true;
          }
        } else if (selected instanceof XSDModelGroupDefinition)
        {
          if (((XSDModelGroupDefinition) selected).isModelGroupDefinitionReference())
          {
            isReference = true;
          }
        }
        StringBuffer sb = new StringBuffer();
        Element element = ((XSDConcreteComponent) selected).getElement();
        if (element != null)
        {
          sb.append(((XSDConcreteComponent) selected).getElement().getLocalName());

          if (isReference)
          {
            sb.append(" ");//$NON-NLS-1$
            sb.append(Messages.UI_PAGE_HEADING_REFERENCE);
          }

          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
          IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
          XSDSchema xsdSchema = ((XSDConcreteComponent) selected).getSchema();
          if (xsdSchema != editorPart.getAdapter(XSDSchema.class))
          {
            sb.append(" (" + Messages.UI_LABEL_READ_ONLY + ")"); //$NON-NLS-1$
          }
          return sb.toString();
        }
        else
        {
          return "(" + Messages.UI_LABEL_READ_ONLY + ")"; //$NON-NLS-1$
        }
      }

      if (object instanceof Element)
      {
        return ((Element) object).getLocalName();
      }
    }

    return result;
  }

  private Object getObject(Object objects, boolean multiple[])
  {
    Assert.isNotNull(objects);
    Object object = null;
    return object;
  }

}
