/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDEnumerationFacetAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetBaseTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;

public class XSDSimpleTypeDefinitionAdapter extends XSDTypeDefinitionAdapter
{
	protected boolean readOnly;
	protected boolean changeReadOnlyField =false;
	protected HashMap deletedTypes = new HashMap();
  public Image getImage()
  {
    if (isReadOnly())
    {
      return XSDEditorPlugin.getPlugin().getIcon("obj16/simpletypedis_obj.gif"); //$NON-NLS-1$
    }
    return XSDEditorPlugin.getPlugin().getIcon("obj16/simpletype_obj.gif"); //$NON-NLS-1$
  }
  
  public String getDisplayName()
  {
    return getName();
  }

  public String getText()
  {
    return getText(true);
  }

  public String getText(boolean showType)
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;

    StringBuffer result = new StringBuffer();

    result.append(xsdSimpleTypeDefinition.getName() == null ? Messages._UI_LABEL_LOCAL_TYPE : xsdSimpleTypeDefinition.getName());

    if (showType)
    {
      XSDSimpleTypeDefinition baseTypeDefinition = xsdSimpleTypeDefinition.getBaseTypeDefinition();
      if (baseTypeDefinition != null && XSDVariety.ATOMIC_LITERAL == xsdSimpleTypeDefinition.getVariety())
      {
        if (baseTypeDefinition.getName() != null && !xsdSimpleTypeDefinition.getContents().contains(baseTypeDefinition) && !XSDConstants.isAnySimpleType(baseTypeDefinition))
        {
        	try {
        		String qName = baseTypeDefinition.getQName(xsdSimpleTypeDefinition);
        		result.append(" : "); //$NON-NLS-1$
                result.append(qName);
        	}
        	catch (Exception e)
        	{
        	}
        }
      }
      else
      {
        XSDSimpleTypeDefinition itemTypeDefinition = xsdSimpleTypeDefinition.getItemTypeDefinition();
        if (itemTypeDefinition != null)
        {
          if (itemTypeDefinition.getName() != null)
          {
            result.append(" : "); //$NON-NLS-1$
            result.append(itemTypeDefinition.getQName(xsdSimpleTypeDefinition));
          }
        }
        else
        {
          List memberTypeDefinitions = xsdSimpleTypeDefinition.getMemberTypeDefinitions();
          if (!memberTypeDefinitions.isEmpty())
          {
            boolean first = true;
            for (Iterator members = memberTypeDefinitions.iterator(); members.hasNext();)
            {
              XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition) members.next();
              if (memberTypeDefinition.getName() != null)
              {
                if (first)
                {
                  result.append(" : "); //$NON-NLS-1$
                  first = false;
                }
                else
                {
                  result.append(" | "); //$NON-NLS-1$
                }
                result.append(memberTypeDefinition.getQName(xsdSimpleTypeDefinition));
              }
              else
              {
                break;
              }
            }
          }
          else if (result.length() == 0)
          {
            result.append(Messages._UI_LABEL_ABSENT);
          }
        }
      }
    }

    return result.toString();
  }

  public boolean hasChildren()
  {
    return true;
  }
  
  public boolean isComplexType()
  {
    return false;
  }

  public boolean isFocusAllowed()
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;
    if (XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(xsdSimpleTypeDefinition.getTargetNamespace()))
    {
      return false;
    }
    if (xsdSimpleTypeDefinition.getName() == null)
    {
      return isAnonymous();
    }
    return true;
  }
  
  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    list.add(AddXSDEnumerationFacetAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(SetBaseTypeAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    list.add(DeleteAction.ID);
    list.add(BaseSelectionAction.SEPARATOR_ID);
    Object schema = getEditorSchema();
    XSDTypeDefinition typeDefinition = getXSDTypeDefinition();
    if (typeDefinition.getSchema() == schema)
    {
      XSDConcreteComponent container = typeDefinition.getContainer();
      if (container == schema || container instanceof XSDRedefine)
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

  public boolean isAnonymous()
  {
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;
    EObject eContainer = xsdSimpleTypeDefinition.eContainer();
    return !(eContainer instanceof XSDSchema ||
             eContainer instanceof XSDRedefine);
  }

  public IADTObject getTopContainer()
  {
    return this;
  }

  public Command getDeleteCommand() 
  {
    return new DeleteCommand(getXSDTypeDefinition());	
  }
  
  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDTypeDefinition().getSchema());
    return (IModel)adapter;
  }

  public ITreeElement[] getChildren()
  {
    List adapterList = new ArrayList();
    XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;
    List list = xsdSimpleTypeDefinition.getEnumerationFacets();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);
  }
  public boolean isReadOnly()
  {
	  XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) target;
	  if (hasSetReadOnlyField())
	  {
		  deletedTypes.put(xsdSimpleTypeDefinition.getName(), new Boolean(true));
		  changeReadOnlyField = false;
		  return readOnly;
	  }
	  else
	  {
		  if (deletedTypes!= null )
		  {
			  Boolean deleted = ((Boolean)deletedTypes.get(xsdSimpleTypeDefinition.getName()));
			  if (deleted != null && deleted.booleanValue())
				  return true;
			  else return super.isReadOnly();
		  }
		  else
			  return super.isReadOnly();
	  }
	
	  
  }

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean hasSetReadOnlyField() {
		return changeReadOnlyField;
	}

	public void setChangeReadOnlyField(boolean setReadOnlyField) {
		this.changeReadOnlyField = setReadOnlyField;
	}

	public void updateDeletedMap(String addComponent) {
		if (deletedTypes.get(addComponent) != null)
			deletedTypes.clear();
	}
}
