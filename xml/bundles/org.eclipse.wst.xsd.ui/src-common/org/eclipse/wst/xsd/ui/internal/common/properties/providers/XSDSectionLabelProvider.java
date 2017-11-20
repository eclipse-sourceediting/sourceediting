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
package org.eclipse.wst.xsd.ui.internal.common.properties.providers;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
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
        return ((ITreeElement)adapter).getImage();
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
      return org.eclipse.wst.xsd.ui.internal.common.util.Messages._UI_LABEL_NO_ITEMS_SELECTED;
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
            sb.append(" ref");//$NON-NLS-1$
            // This string is not easily translatable to other languages.
            // For now, make it english-only since we use the element tag as the title anyway
//            sb.append(Messages.UI_PAGE_HEADING_REFERENCE);
          }

          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
          if (workbenchWindow != null)
          {
            IWorkbenchPage page = workbenchWindow.getActivePage();
            if (page != null)
            {
              IEditorPart editorPart = page.getActiveEditor();
              XSDSchema xsdSchema = ((XSDConcreteComponent) selected).getSchema();
              IEditorInput editorInput = editorPart.getEditorInput();
              boolean isReadOnly = false;
              if (!(editorInput instanceof IFileEditorInput || editorInput instanceof FileStoreEditorInput))
              {
                isReadOnly = true;
              }
              if (editorPart != null && xsdSchema != editorPart.getAdapter(XSDSchema.class) || isReadOnly)
              {
                sb.append(" (" + Messages.UI_LABEL_READ_ONLY + ")"); //$NON-NLS-1$ //$NON-NLS-2$
              }
            }
          }
          return sb.toString();
        }
        else
        {
          // If the element is null, then let's use the model object to find
          // an appropriate name
          if ((XSDConcreteComponent) selected instanceof XSDNamedComponent)
          {
            return ((XSDNamedComponent)selected).getName();
          }
          else if ((XSDConcreteComponent) selected instanceof XSDSchema)
          {
            return XSDConstants.SCHEMA_ELEMENT_TAG;
          }
          // last resort....
          return "(" + Messages.UI_LABEL_READ_ONLY + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
      }

      if (object instanceof Element)
      {
        return ((Element) object).getLocalName();
      }
    }

    return result;
  }
}
