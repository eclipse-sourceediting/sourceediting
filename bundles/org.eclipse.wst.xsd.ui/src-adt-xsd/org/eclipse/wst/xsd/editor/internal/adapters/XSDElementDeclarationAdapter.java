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
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.adt.design.IAnnotationProvider;
import org.eclipse.wst.xsd.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.adt.facade.IField;
import org.eclipse.wst.xsd.adt.facade.IModel;
import org.eclipse.wst.xsd.adt.facade.IType;
import org.eclipse.wst.xsd.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.common.actions.SetMultiplicityAction;
import org.eclipse.wst.xsd.ui.common.actions.SetTypeAction;
import org.eclipse.wst.xsd.ui.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.common.commands.UpdateNameCommand;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDElementDeclarationAdapter extends XSDParticleAdapter implements IField, IActionProvider, IAnnotationProvider
{
  protected XSDElementDeclaration getXSDElementDeclaration()
  {
    return (XSDElementDeclaration) target;
  }

  public String getName()
  {
    String name = getXSDElementDeclaration().getResolvedElementDeclaration().getName();
    return (name == null) ? "" : name;
  }

  public String getTypeName()
  {
    IType type = getType();
    if (type != null)
    {  
      return type.getName();
    }
    return null;
  }

  public String getTypeNameQualifier()
  {
    return getXSDElementDeclaration().getTypeDefinition().getTargetNamespace();
  }

  public IType getType()
  {
    XSDTypeDefinition td = getXSDElementDeclaration().getResolvedElementDeclaration().getTypeDefinition();
    //if (td != null &&
    //    td.getTargetNamespace() != null && td.getTargetNamespace().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001)) return null;
    return (td != null) ? (IType) XSDAdapterFactory.getInstance().adapt(td) : null;
  }
 
  public Image getImage()
  {
    XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) target;

    if (!xsdElementDeclaration.isElementDeclarationReference())
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDElement.gif");
    }
    else
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDElementRef.gif");
    }
  }

  public String getText()
  {
    XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) target;
    XSDElementDeclaration resolvedElementDeclaration = xsdElementDeclaration.getResolvedElementDeclaration();
    //String name = xsdElementDeclaration != resolvedElementDeclaration ? xsdElementDeclaration.getQName() : xsdElementDeclaration.getName();
    String name = resolvedElementDeclaration.getName();

    StringBuffer result = new StringBuffer();
    if (name == null)
    {
      result.append("'absent'");
    }
    else
    {
      result.append(name);
    }

    if (!xsdElementDeclaration.isGlobal())
    {
      Element element = xsdElementDeclaration.getElement();
      boolean hasMinOccurs = element.hasAttribute(XSDConstants.MINOCCURS_ATTRIBUTE);
      boolean hasMaxOccurs = element.hasAttribute(XSDConstants.MAXOCCURS_ATTRIBUTE);

      if (hasMinOccurs || hasMaxOccurs)
      {
        result.append(" [");
        if (hasMinOccurs)
        {
          int min = ((XSDParticle) xsdElementDeclaration.getContainer()).getMinOccurs();
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
          int min = ((XSDParticle) xsdElementDeclaration.getContainer()).getMinOccurs();
          result.append(String.valueOf(min));
        }
        if (hasMaxOccurs)
        {
          int max = ((XSDParticle) xsdElementDeclaration.getContainer()).getMaxOccurs();
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
          int max = ((XSDParticle) xsdElementDeclaration.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));

        }
        result.append("]");
      }
    }

    if (resolvedElementDeclaration.getAnonymousTypeDefinition() == null && resolvedElementDeclaration.getTypeDefinition() != null)
    {
      result.append(" : ");
      // result.append(resolvedElementDeclaration.getTypeDefinition().getQName(xsdElementDeclaration));
      result.append(resolvedElementDeclaration.getTypeDefinition().getName());
    }

    return result.toString();

  }

  public ITreeElement[] getChildren()
  {
    XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) target;
    List list = new ArrayList();
    XSDTypeDefinition type = null;
    if (xsdElementDeclaration.isElementDeclarationReference())
    {
      type = xsdElementDeclaration.getResolvedElementDeclaration().getTypeDefinition();
    }
    else
    {
      type = xsdElementDeclaration.getAnonymousTypeDefinition();
      if (type == null)
      {
        type = xsdElementDeclaration.getTypeDefinition();
      }
    }

    if (type instanceof XSDComplexTypeDefinition && type.getTargetNamespace() != null && !type.getTargetNamespace().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001))
    {
      XSDComplexTypeDefinition ctType = (XSDComplexTypeDefinition) type;
      if (ctType != null)
      {
        if (xsdElementDeclaration.isGlobal())
          list.add(ctType);
      }
    }

    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);

  }
  
  public String getKind()
  {
    return "element";
  }
  
  public boolean isGlobal()
  {
    return getXSDElementDeclaration().eContainer() instanceof XSDSchema;
  }
  
  public boolean isElementDeclarationReference()
  {
	  return ((XSDElementDeclaration) target).isElementDeclarationReference();
  }

  public Command getUpdateMaxOccursCommand(int maxOccurs)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateMinOccursCommand(int minOccurs)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getUpdateNameCommand(String name)
  {
    return new UpdateNameCommand("Update Name", getXSDElementDeclaration().getResolvedElementDeclaration(), name);
  }

  public Command getUpdateTypeNameCommand(String typeName, String quailifier)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getDeleteCommand()
  {
    // TODO Auto-generated method stub
    return new DeleteCommand("", getXSDElementDeclaration());
  }
  
  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    if (!isGlobal())
      list.add(AddXSDElementAction.ID);

    list.add(BaseSelectionAction.SUBMENU_START_ID + "Set Type");
    list.add(SetTypeAction.SET_NEW_TYPE_ID);
    list.add(SetTypeAction.SELECT_EXISTING_TYPE_ID);
    list.add(BaseSelectionAction.SUBMENU_END_ID);

    list.add(BaseSelectionAction.SUBMENU_START_ID + "Set Multiplicity");
    list.add(SetMultiplicityAction.REQUIRED_ID);
    list.add(SetMultiplicityAction.ZERO_OR_ONE_ID);
    list.add(SetMultiplicityAction.ZERO_OR_MORE_ID);
    list.add(SetMultiplicityAction.ONE_OR_MORE_ID);    
    list.add(BaseSelectionAction.SUBMENU_END_ID);

    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(DeleteAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(ShowPropertiesViewAction.ID);
    return (String [])list.toArray(new String[0]);
  }
  
  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDElementDeclaration().getSchema());
    return (IModel)adapter;
  }
}
