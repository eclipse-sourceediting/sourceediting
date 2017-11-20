/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
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
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.IAnnotationProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicityAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDElementDeclarationAdapter extends XSDParticleAdapter implements IField, IActionProvider, IAnnotationProvider, IGraphElement
{
  protected XSDElementDeclaration getXSDElementDeclaration()
  {
    return (XSDElementDeclaration) target;
  }

  public String getName()
  {
    String name = getXSDElementDeclaration().getResolvedElementDeclaration().getName();
    return (name == null) ? "" : name; //$NON-NLS-1$
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
    XSDTypeDefinition type = getXSDElementDeclaration().getResolvedElementDeclaration().getTypeDefinition();
    if (type != null)
    {
      return type.getTargetNamespace();
    }
    return "";
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
      if (isReadOnly())
      {
        return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDElementdis.gif");
      }
      return XSDEditorPlugin.getXSDImage("icons/XSDElement.gif"); //$NON-NLS-1$
    }
    else
    {
      if (isReadOnly())
      {
        return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDElementRefdis.gif");
      }        
      return XSDEditorPlugin.getXSDImage("icons/XSDElementRef.gif"); //$NON-NLS-1$
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
      result.append(Messages._UI_LABEL_ABSENT);
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
        result.append(" ["); //$NON-NLS-1$
        if (hasMinOccurs)
        {
          int min = ((XSDParticle) xsdElementDeclaration.getContainer()).getMinOccurs();
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
          int min = ((XSDParticle) xsdElementDeclaration.getContainer()).getMinOccurs();
          result.append(String.valueOf(min));
        }
        if (hasMaxOccurs)
        {
          int max = ((XSDParticle) xsdElementDeclaration.getContainer()).getMaxOccurs();
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
          int max = ((XSDParticle) xsdElementDeclaration.getContainer()).getMaxOccurs();
          result.append(String.valueOf(max));

        }
        result.append("]"); //$NON-NLS-1$
      }
    }

    if (resolvedElementDeclaration.getAnonymousTypeDefinition() == null && resolvedElementDeclaration.getTypeDefinition() != null)
    {
      result.append(" : "); //$NON-NLS-1$
      // result.append(resolvedElementDeclaration.getTypeDefinition().getQName(xsdElementDeclaration));
      result.append(resolvedElementDeclaration.getTypeDefinition().getName());
    }

    String text = result.toString();
    String processedString = TextProcessor.process(text, ":");
    return processedString;

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
    return "element"; //$NON-NLS-1$
  }
  
  public boolean isGlobal()
  {
    return getXSDElementDeclaration().eContainer() instanceof XSDSchema;
  }
  
  public boolean isReference()
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
    return new UpdateNameCommand(Messages._UI_ACTION_UPDATE_NAME, getXSDElementDeclaration().getResolvedElementDeclaration(), name);
  }

  public Command getUpdateTypeNameCommand(String typeName, String quailifier)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public Command getDeleteCommand()
  {
    // TODO Auto-generated method stub
    return new DeleteCommand(getXSDElementDeclaration());
  }
  
  public String[] getActions(Object object)
  {
    List list = new ArrayList();

    if (!isGlobal())
    {
      list.add(BaseSelectionAction.SUBMENU_START_ID + Messages._UI_ACTION_INSERT_ELEMENT);
      list.add(AddXSDElementAction.BEFORE_SELECTED_ID);
      list.add(AddXSDElementAction.AFTER_SELECTED_ID);
      list.add(BaseSelectionAction.SUBMENU_END_ID);
    }

    list.add(BaseSelectionAction.SUBMENU_START_ID + Messages._UI_ACTION_SET_TYPE);
    list.add(SetTypeAction.SET_NEW_TYPE_ID);
    list.add(SetTypeAction.SELECT_EXISTING_TYPE_ID);
    list.add(BaseSelectionAction.SUBMENU_END_ID);

    list.add(BaseSelectionAction.SUBMENU_START_ID + Messages._UI_ACTION_SET_MULTIPLICITY);
    list.add(SetMultiplicityAction.REQUIRED_ID);
    list.add(SetMultiplicityAction.ZERO_OR_ONE_ID);
    list.add(SetMultiplicityAction.ZERO_OR_MORE_ID);
    list.add(SetMultiplicityAction.ONE_OR_MORE_ID);    
    list.add(BaseSelectionAction.SUBMENU_END_ID);

    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(DeleteAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    Object schema = getEditorSchema();
    if (getXSDElementDeclaration().getSchema() == schema)
    {
      if (getXSDElementDeclaration().getContainer() == schema)
      {
        list.add(SetInputToGraphView.ID);
      }
    }
    else
    {
      list.add(OpenInNewEditor.ID);
    }
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(ShowPropertiesViewAction.ID);
    return (String [])list.toArray(new String[0]);
  }
  
  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDElementDeclaration().getSchema());
    return (IModel)adapter;
  }

  public boolean isFocusAllowed()
  {
    return isGlobal();
  }

  public IADTObject getTopContainer()
  {
    if (!isGlobal())
    {
      return getGlobalXSDContainer(getXSDElementDeclaration());
    }
    return this;
  }
  
  public boolean isAbstract()
  {
    return getXSDElementDeclaration().isAbstract();
  }
}
