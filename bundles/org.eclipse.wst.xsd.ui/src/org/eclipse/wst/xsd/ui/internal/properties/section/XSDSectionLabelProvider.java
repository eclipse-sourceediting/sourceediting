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
package org.eclipse.wst.xsd.ui.internal.properties.section;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
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
		if (object == null || object.equals(StructuredSelection.EMPTY)) {
			return null;
		}
    Image result = null;           
    if (object instanceof StructuredSelection)
    {
      Object selected = ((StructuredSelection)object).getFirstElement();
      
      if (selected instanceof XSDConcreteComponent)
      {
        IWorkbench workbench = XSDEditorPlugin.getPlugin().getWorkbench();
        IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
        IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
        if (editorPart instanceof XSDEditor)
        {
          return ((XSDEditor)editorPart).getLabelProvider().getImage((XSDConcreteComponent)selected);
        }
      }
      
//      selected  = typeMapper.remapObject(selected);
//      ModelAdapter modelAdapter = adapterFactory.getAdapter(selected);
//      if (modelAdapter != null)
//      {
//        result = (Image)modelAdapter.getProperty(selected, ModelAdapter.IMAGE_PROPERTY);     
//      }
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
      selected = ((StructuredSelection)object).getFirstElement();
      
      if (selected instanceof XSDConcreteComponent)
      {
        if (selected instanceof XSDElementDeclaration)
        {
          XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)selected;
          if (xsdElementDeclaration.isElementDeclarationReference())
          {
            isReference = true;
          }
        }
        else if (selected instanceof XSDAttributeDeclaration)
        {
          if (((XSDAttributeDeclaration)selected).isAttributeDeclarationReference())
          {
            isReference = true;
          }
        }
        else if (selected instanceof XSDModelGroupDefinition)
        {
          if (((XSDModelGroupDefinition)selected).isModelGroupDefinitionReference())
          {
            isReference = true;
          }
        }
        StringBuffer sb = new StringBuffer();
        Element element = ((XSDConcreteComponent)selected).getElement();
        if (element != null)
        {
          sb.append(((XSDConcreteComponent)selected).getElement().getLocalName());
          
          if (isReference)
          {
            sb.append(" ");
            sb.append(XSDEditorPlugin.getXSDString("_UI_PAGE_HEADING_REFERENCE"));
          }
          
          if (!(element instanceof IDOMNode))
          {
            sb.append(" (" + XSDEditorPlugin.getXSDString("_UI_LABEL_READ_ONLY") + ")");   //$NON-NLS-1$
          }
          return sb.toString();
        }
        else
        {
          return "(" + XSDEditorPlugin.getXSDString("_UI_LABEL_READ_ONLY") + ")";  //$NON-NLS-1$
        }
      }

//      selected  = typeMapper.remapObject(selected);
//      
//       ModelAdapter modelAdapter = adapterFactory.getAdapter(selected);
//       if (modelAdapter != null)
//       {                       
//         // result = (String)modelAdapter.getProperty(selected, ModelAdapter.LABEL_PROPERTY);
//         result = ((WSDLElement)selected).getElement().getLocalName();
//       }
      if (object instanceof Element)
      {
        return ((Element)object).getLocalName();
      }
    }
    else if (object instanceof TextSelection)
    {
    }
    

    return result;
	}

	/**
	 * Determine if a multiple object selection has been passed to the 
	 * label provider. If the objects is a IStructuredSelection, see if 
	 * all the objects in the selection are the same and if so, we want
	 * to provide labels for the common selected element.
	 * @param objects a single object or a IStructuredSelection.
	 * @param multiple first element in the array is true if there is multiple
	 * unequal selected elements in a IStructuredSelection.
	 * @return the object to get labels for.
	 */
	private Object getObject(Object objects, boolean multiple[]) {
		Assert.isNotNull(objects);
		Object object = null;
		return object;
	}

}
