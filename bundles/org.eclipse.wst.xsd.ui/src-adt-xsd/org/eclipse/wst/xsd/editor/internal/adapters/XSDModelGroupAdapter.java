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
package org.eclipse.wst.xsd.editor.internal.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.common.actions.AddXSDModelGroupAction;
import org.eclipse.wst.xsd.ui.common.actions.AddXSDModelGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.common.actions.DeleteXSDConcreteComponentAction;
import org.eclipse.wst.xsd.ui.common.actions.SetMultiplicityAction;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDModelGroupAdapter extends XSDParticleAdapter implements IActionProvider
{
  // TODO: common these up with ModelGroupFigure's
  public static final Image SEQUENCE_ICON_IMAGE = XSDEditorPlugin.getImageDescriptor("seq_obj.gif", true).createImage();
  public static final Image CHOICE_ICON_IMAGE = XSDEditorPlugin.getImageDescriptor("choice_obj.gif", true).createImage();
  public static final Image ALL_ICON_IMAGE = XSDEditorPlugin.getImageDescriptor("all_obj.gif", true).createImage();

  XSDModelGroup getXSDModelGroup()
  {
    return (XSDModelGroup) target;
  }

  public XSDModelGroupAdapter()
  {

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.adt.outline.ITreeElement#getImage()
   */
  public Image getImage()
  {
    XSDModelGroup xsdModelGroup = getXSDModelGroup();
    if (XSDCompositor.CHOICE_LITERAL == xsdModelGroup.getCompositor())
    {
      return CHOICE_ICON_IMAGE;
    }
    else if (XSDCompositor.ALL_LITERAL == xsdModelGroup.getCompositor())
    {
      return ALL_ICON_IMAGE;
    }
    else
    {
      return SEQUENCE_ICON_IMAGE;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.adt.outline.ITreeElement#getText()
   */
  public String getText()
  {
    XSDModelGroup xsdModelGroup = getXSDModelGroup();

    StringBuffer result = new StringBuffer();
    String name = xsdModelGroup.getCompositor().getName();
    if (name != null)
    {
      result.append(name);
    }

    Element element = xsdModelGroup.getElement();

    if (element != null)
    {
      boolean hasMinOccurs = element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
      boolean hasMaxOccurs = element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);

      if (hasMinOccurs || hasMaxOccurs)
      {
        result.append(" [");
        if (hasMinOccurs)
        {
          int min = ((XSDParticle) xsdModelGroup.getContainer()).getMinOccurs();
          if (min == XSDParticle.UNBOUNDED)
          {
            result.append("*");
          }
          else
          {
            result.append(String.valueOf(min));
          }
        }
        else
        // print default
        {
          int min = ((XSDParticle) xsdModelGroup.getContainer()).getMinOccurs();
          result.append(String.valueOf(min));
        }
        if (hasMaxOccurs)
        {
          int max = ((XSDParticle) xsdModelGroup.getContainer()).getMaxOccurs();
          result.append("..");
          if (max == XSDParticle.UNBOUNDED)
          {
            result.append("*");
          }
          else
          {
            result.append(String.valueOf(max));
          }
        }
        else
        // print default
        {
          result.append("..");
          int max = ((XSDParticle) xsdModelGroup.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));
        }
        result.append("]");
      }
    }
    return result.toString();
  }

  public ITreeElement[] getChildren()
  {
    XSDModelGroup xsdModelGroup = getXSDModelGroup();
    List list = new ArrayList();
    for (Iterator i = xsdModelGroup.getContents().iterator(); i.hasNext(); )
    {
       Object object = i.next();
       XSDParticleContent particle = ((XSDParticle)object).getContent();
       if (particle instanceof XSDElementDeclaration)
       {
         list.add((XSDElementDeclaration)particle);
       }
       else if (particle instanceof XSDWildcard)
       {
         list.add((XSDWildcard)particle);
       }
       else if (particle instanceof XSDModelGroup)
       {
         list.add((XSDModelGroup)particle);
       }
    }

    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement []) adapterList.toArray(new ITreeElement[0]);
  }

  public Object getParent(Object object)
  {
    XSDModelGroup element = (XSDModelGroup) object;
    return element.getContainer();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.adt.design.editparts.model.IActionProvider#getActions(java.lang.Object)
   */
  public String[] getActions(Object object)
  {
     Collection actionIDs = new ArrayList();
     actionIDs.add(AddXSDElementAction.ID);
     actionIDs.add(AddXSDElementAction.REF_ID);
     // Add Element Ref
     actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
     actionIDs.add(AddXSDModelGroupAction.SEQUENCE_ID);
     actionIDs.add(AddXSDModelGroupAction.CHOICE_ID);
     actionIDs.add(AddXSDModelGroupDefinitionAction.MODELGROUPDEFINITIONREF_ID);
//     actionIDs.add(AddFieldAction.ID);
     actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
     // Add Any

     actionIDs.add(BaseSelectionAction.SUBMENU_START_ID + "Set Multiplicity");
     actionIDs.add(SetMultiplicityAction.REQUIRED_ID);
     actionIDs.add(SetMultiplicityAction.ZERO_OR_ONE_ID);
     actionIDs.add(SetMultiplicityAction.ZERO_OR_MORE_ID);
     actionIDs.add(SetMultiplicityAction.ONE_OR_MORE_ID);    
     actionIDs.add(BaseSelectionAction.SUBMENU_END_ID);
    
     if (!(getParent(target) instanceof XSDModelGroupDefinition))
     {
       actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
       actionIDs.add(DeleteXSDConcreteComponentAction.DELETE_XSD_COMPONENT_ID);
     }    
     actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
     actionIDs.add(ShowPropertiesViewAction.ID);
        
     return (String [])actionIDs.toArray(new String[0]);
  }

  public int getMaxOccurs()
  {
    return getMaxOccurs(getXSDModelGroup());
  }

  public int getMinOccurs()
  {
    return getMinOccurs(getXSDModelGroup());
  }

}
