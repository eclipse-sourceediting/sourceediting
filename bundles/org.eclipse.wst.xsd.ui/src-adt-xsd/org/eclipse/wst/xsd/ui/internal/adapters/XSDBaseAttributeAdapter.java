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
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IField;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

// a base adapter for reuse by an AttributeUse and AttributeDeclaration
//
public abstract class XSDBaseAttributeAdapter extends XSDBaseAdapter implements IField, IGraphElement
{
  protected abstract XSDAttributeDeclaration getXSDAttributeDeclaration();
  protected abstract XSDAttributeDeclaration getResolvedXSDAttributeDeclaration();

  public XSDBaseAttributeAdapter()
  {
    super();
  }

  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    if (!isGlobal())
    {
      list.add(AddXSDAttributeDeclarationAction.ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
    }
    list.add(DeleteAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    Object schema = getEditorSchema();
    if (getXSDAttributeDeclaration().getSchema() == schema)
    {
      if (getXSDAttributeDeclaration().getContainer() == schema)
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
    return (String[]) list.toArray(new String[0]);
  }

  public Command getDeleteCommand()
  {
    return new DeleteCommand(getXSDAttributeDeclaration());
  }

  public String getKind()
  {
    return XSDConstants.ATTRIBUTE_ELEMENT_TAG;
  }

  public int getMaxOccurs()
  {
    // TODO Auto-generated method stub
    return -3;
  }

  public int getMinOccurs()
  {
    // TODO Auto-generated method stub
    return -3;
  }

  public String getName()
  {
    XSDAttributeDeclaration resolvedAttributeDeclaration = getResolvedXSDAttributeDeclaration();
    String name = resolvedAttributeDeclaration.getName();
    return (name == null) ? "" : name; //$NON-NLS-1$
  }

  public IType getType()
  {
    XSDTypeDefinition td = getResolvedXSDAttributeDeclaration().getTypeDefinition();
    return (td != null) ? (IType) XSDAdapterFactory.getInstance().adapt(td) : null;
  }

  public String getTypeName()
  {
    IType type = getType();
    if (type != null)
    {  
      return type.getName();
    }
    return "";
  }

  public String getTypeNameQualifier()
  {
    XSDAttributeDeclaration attr = getResolvedXSDAttributeDeclaration();
    if (attr != null)
    {
      if (attr.getTypeDefinition() != null)
      {
        return attr.getTypeDefinition().getTargetNamespace();        
      }
    }
    return null;
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
    return new UpdateNameCommand(Messages._UI_ACTION_UPDATE_NAME, getResolvedXSDAttributeDeclaration(), name);
  }

  public Command getUpdateTypeNameCommand(String typeName, String quailifier)
  {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement#getImage()
   */
  public Image getImage()
  {
    XSDAttributeDeclaration xsdAttributeDeclaration = getXSDAttributeDeclaration();  // don't want the resolved attribute
    if (xsdAttributeDeclaration.isAttributeDeclarationReference())
    {
      if (isReadOnly())
      {
        return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAttributeRefdis.gif");
      }
      return XSDEditorPlugin.getXSDImage("icons/XSDAttributeRef.gif"); //$NON-NLS-1$
    }
    else
    {
      if (isReadOnly())
      {
        return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDAttributedis.gif");
      }
      return XSDEditorPlugin.getXSDImage("icons/XSDAttribute.gif"); //$NON-NLS-1$
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement#getText()
   */
  public String getText()
  {
    return getTextForAttribute(getResolvedXSDAttributeDeclaration(), true);
  }

  public String getTextForAttribute(XSDAttributeDeclaration ad, boolean showType)
  {
    ad = ad.getResolvedAttributeDeclaration();
    String name = ad.getName();
    StringBuffer result = new StringBuffer();
    if (name == null)
    {
      result.append(" " + Messages._UI_LABEL_ABSENT + " ");  //$NON-NLS-1$ //$NON-NLS-2$
    }
    else
    {
      result.append(name);
    }
    if (ad.getAnonymousTypeDefinition() == null && ad.getTypeDefinition() != null)
    {
      result.append(" : "); //$NON-NLS-1$
      // result.append(resolvedAttributeDeclaration.getTypeDefinition().getQName(xsdAttributeDeclaration));
      result.append(ad.getTypeDefinition().getName());
    }
    return result.toString();
  }

  public boolean isGlobal()
  {
    return false;
  }
  
  public boolean isReference()
  {
    return false;
  }
  
  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDAttributeDeclaration().getSchema());
    return (IModel)adapter;
  }  

  public boolean isFocusAllowed()
  {
    return false;
  }
}

