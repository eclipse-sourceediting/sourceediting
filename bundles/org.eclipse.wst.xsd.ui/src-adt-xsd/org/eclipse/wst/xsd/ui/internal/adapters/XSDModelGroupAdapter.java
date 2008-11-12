/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAnyElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicityAction;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.design.figures.ModelGroupFigure;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDModelGroupAdapter extends XSDParticleAdapter implements IActionProvider, IGraphElement
{
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
   * @see org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement#getImage()
   */
  public Image getImage()
  {
    XSDModelGroup xsdModelGroup = getXSDModelGroup();
    if (XSDCompositor.CHOICE_LITERAL == xsdModelGroup.getCompositor())
    {
      return ModelGroupFigure.CHOICE_ICON_IMAGE;
    }
    else if (XSDCompositor.ALL_LITERAL == xsdModelGroup.getCompositor())
    {
      return ModelGroupFigure.ALL_ICON_IMAGE;
    }
    else
    {
      return ModelGroupFigure.SEQUENCE_ICON_IMAGE;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement#getText()
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
        result.append(" ["); //$NON-NLS-1$
        if (hasMinOccurs)
        {
          int min = ((XSDParticle) xsdModelGroup.getContainer()).getMinOccurs();
          if (min == XSDParticle.UNBOUNDED)
          {
            result.append("*"); //$NON-NLS-1$
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
          result.append(".."); //$NON-NLS-1$
          if (max == XSDParticle.UNBOUNDED)
          {
            result.append("*"); //$NON-NLS-1$
          }
          else
          {
            result.append(String.valueOf(max));
          }
        }
        else
        // print default
        {
          result.append(".."); //$NON-NLS-1$
          int max = ((XSDParticle) xsdModelGroup.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));
        }
        result.append("]"); //$NON-NLS-1$
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
         list.add(particle);
       }
       else if (particle instanceof XSDWildcard)
       {
         list.add(particle);
       }
       else if (particle instanceof XSDModelGroup)
       {
         list.add(particle);
       }
       else if (particle instanceof XSDModelGroupDefinition)
       { 
    	 //list.add(((XSDModelGroupDefinition)particle).getResolvedModelGroupDefinition());
    	 list.add(particle);
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
   * @see org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider#getActions(java.lang.Object)
   */
  public String[] getActions(Object object)
  {
     Collection actionIDs = new ArrayList();
     actionIDs.add(AddXSDElementAction.ID);
     actionIDs.add(AddXSDElementAction.REF_ID);
     actionIDs.add(AddXSDAnyElementAction.ID);
     // Add Element Ref
     actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
     actionIDs.add(AddXSDModelGroupAction.SEQUENCE_ID);
     actionIDs.add(AddXSDModelGroupAction.CHOICE_ID);
     actionIDs.add(AddXSDModelGroupAction.ALL_ID);
     actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
     actionIDs.add(AddXSDModelGroupDefinitionAction.MODELGROUPDEFINITIONREF_ID);
//     actionIDs.add(AddFieldAction.ID);
     actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
     // Add Any

     actionIDs.add(BaseSelectionAction.SUBMENU_START_ID + Messages._UI_ACTION_SET_MULTIPLICITY);
     actionIDs.add(SetMultiplicityAction.REQUIRED_ID);
     actionIDs.add(SetMultiplicityAction.ZERO_OR_ONE_ID);
     actionIDs.add(SetMultiplicityAction.ZERO_OR_MORE_ID);
     actionIDs.add(SetMultiplicityAction.ONE_OR_MORE_ID);    
     actionIDs.add(BaseSelectionAction.SUBMENU_END_ID);
    
     if (!(getParent(target) instanceof XSDModelGroupDefinition))
     {
       actionIDs.add(BaseSelectionAction.SEPARATOR_ID);
       actionIDs.add(DeleteAction.ID);
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

  public IADTObject getTopContainer()
  {
    XSDModelGroup xsdModelGroup = getXSDModelGroup();
    return getGlobalXSDContainer(xsdModelGroup);
  }

  public boolean isFocusAllowed()
  {
    return false;
  }

  public Command getDeleteCommand() 
  {
    return new DeleteCommand(getXSDModelGroup());	
  }
  
  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDModelGroup().getSchema());
    return (IModel)adapter;
  }
}
