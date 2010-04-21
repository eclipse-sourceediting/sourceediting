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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IGraphElement;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IStructure;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetMultiplicityAction;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.SpaceFiller;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;

public class XSDModelGroupDefinitionAdapter extends XSDParticleAdapter implements IStructure, IActionProvider, IGraphElement, IADTObjectListener
{
  public static final Image MODEL_GROUP_ICON = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDGroup.gif"); //$NON-NLS-1$
  public static final Image MODEL_GROUP_DISABLED_ICON = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDGroupdis.gif"); //$NON-NLS-1$
  public static final Image MODEL_GROUP_REF_ICON = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDGroupRef.gif"); //$NON-NLS-1$
  public static final Image MODEL_GROUP_REF_DISABLED_ICON = XSDEditorPlugin.getPlugin().getIcon("obj16/XSDGroupRefdis.gif"); //$NON-NLS-1$

  protected List fields = null;
  protected List otherThingsToListenTo = null;
  protected boolean readOnly;
  protected boolean changeReadOnlyField =false;
  protected HashMap deletedTypes = new HashMap();
  
  public XSDModelGroupDefinitionAdapter()
  {
    super();
  }

  public XSDModelGroupDefinition getXSDModelGroupDefinition()
  {
    return (XSDModelGroupDefinition) target;
  }

  public Image getImage()
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition) target;

    if (xsdModelGroupDefinition.isModelGroupDefinitionReference())
    {
      if (isReadOnly())
      {
        return MODEL_GROUP_REF_DISABLED_ICON;
      }
      return MODEL_GROUP_REF_ICON;
    }
    else
    {
      if (isReadOnly())
      {
        return MODEL_GROUP_DISABLED_ICON;
      }
      return MODEL_GROUP_ICON;
    }
  }

  public String getText()
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition) target;
    if (xsdModelGroupDefinition.getResolvedModelGroupDefinition().getContainer() == null && xsdModelGroupDefinition.getName() ==null) return "";   // Removed
    String result = xsdModelGroupDefinition.isModelGroupDefinitionReference() ? xsdModelGroupDefinition.getQName() : xsdModelGroupDefinition.getName();
    return result == null ? Messages._UI_LABEL_ABSENT : result;
  }

  public ITreeElement[] getChildren()
  {
    XSDModelGroupDefinition def = (XSDModelGroupDefinition)target;
    List list = new ArrayList();
    // Bug246036 - need to stop showing element content in a cycle.
    // And, we should not show any element content for references otherwise there will be two
    // entries in the tree viewer for the same item
    if (!def.isModelGroupDefinitionReference())
    {
      XSDModelGroup xsdModelGroup = ((XSDModelGroupDefinition) target).getResolvedModelGroupDefinition().getModelGroup();
      if (xsdModelGroup != null)
        list.add(xsdModelGroup);
    }

    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.xsd.ui.internal.adt.design.editparts.model.IActionProvider#getActions(java.lang.Object)
   */
  public String[] getActions(Object object)
  {
    Collection list = new ArrayList();

    XSDModelGroupDefinition modelGroupDefinition = getXSDModelGroupDefinition();
    boolean isModelGroupReference = modelGroupDefinition.isModelGroupDefinitionReference();
    if (!isModelGroupReference)
    {
      list.add(AddXSDElementAction.ID);
      list.add(AddXSDElementAction.REF_ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(AddXSDModelGroupAction.SEQUENCE_ID);
      list.add(AddXSDModelGroupAction.CHOICE_ID);
      list.add(AddXSDModelGroupAction.ALL_ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
    }
    
    list.add(DeleteAction.ID);
    
    if (isModelGroupReference)
    {
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(BaseSelectionAction.SUBMENU_START_ID + Messages._UI_ACTION_SET_MULTIPLICITY);
      list.add(SetMultiplicityAction.REQUIRED_ID);
      list.add(SetMultiplicityAction.ZERO_OR_ONE_ID);
      list.add(SetMultiplicityAction.ZERO_OR_MORE_ID);
      list.add(SetMultiplicityAction.ONE_OR_MORE_ID);    
      list.add(BaseSelectionAction.SUBMENU_END_ID); 
    }
    list.add(BaseSelectionAction.SEPARATOR_ID);
    Object schema = getEditorSchema();
    if (modelGroupDefinition.getSchema() == schema)
    {
      XSDConcreteComponent container = modelGroupDefinition.getContainer();
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

  public Command getAddNewFieldCommand(String fieldKind)
  {
    return null;
  }

  public Command getDeleteCommand()
  {
    return new DeleteCommand(getXSDModelGroupDefinition());
  }

  // TODO Common this up with XSDComplexType's.  See also getFields 
  protected void clearFields()
  {
    if (otherThingsToListenTo != null)
    {
      for (Iterator i = otherThingsToListenTo.iterator(); i.hasNext();)
      {
        Adapter adapter = (Adapter) i.next();
        if (adapter instanceof IADTObject)
        {
          IADTObject adtObject = (IADTObject) adapter;
          adtObject.unregisterListener(this);
        }
      }
    }
    fields = null;
    otherThingsToListenTo = null;
  }

  public List getFields()
  {
    List fields = new ArrayList();
    otherThingsToListenTo = new ArrayList();
    XSDVisitorForFields visitor = new XSDVisitorForGroupFieldsWithSpaceFillers();
    visitor.visitModelGroupDefinition(getXSDModelGroupDefinition());
    populateAdapterList(visitor.concreteComponentList, fields);
    
    // TODO (cs) common a base class for a structure thingee
    //
    populateAdapterList(visitor.thingsWeNeedToListenTo, otherThingsToListenTo);
    for (Iterator i = otherThingsToListenTo.iterator(); i.hasNext();)
    {
      Adapter adapter = (Adapter) i.next();
      if (adapter instanceof IADTObject)
      {
        IADTObject adtObject = (IADTObject) adapter;
        adtObject.registerListener(this);
      }
    }
    return fields;
  }

  protected class XSDVisitorForGroupFieldsWithSpaceFillers extends XSDVisitorForFields
  {
    public XSDVisitorForGroupFieldsWithSpaceFillers()
    {
      super();
    }

    public void visitModelGroup(XSDModelGroup modelGroup)
    {
      int numOfChildren = modelGroup.getContents().size();
      if (numOfChildren == 0)
      {
        concreteComponentList.add(new SpaceFiller("element")); //$NON-NLS-1$
      }
      super.visitModelGroup(modelGroup);
    }
    
    public void visitModelGroupDefinition(XSDModelGroupDefinition modelGroupDef)
    {
      XSDModelGroupDefinition resolvedModelGroupDef = modelGroupDef.getResolvedModelGroupDefinition();
      if (visitedGroups.contains(resolvedModelGroupDef.getModelGroup()))
      {
        concreteComponentList.add(new SpaceFiller("element")); //$NON-NLS-1$
      }
      super.visitModelGroupDefinition(modelGroupDef);
    }
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDModelGroupDefinition().getSchema());
    return (IModel)adapter;
  }
  public String getName()
  {
    return getText();
  }

  public boolean isFocusAllowed()
  {
    XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition) target;
    if (xsdModelGroupDefinition.isModelGroupDefinitionReference())
    { 
      return false;
    }
    return true;
  }

  public void propertyChanged(Object object, String property)
  {
    clearFields();
    notifyListeners(this, null);
  }
  
  public int getMaxOccurs()
  {
    return getMaxOccurs(getXSDModelGroupDefinition());
  }

  public int getMinOccurs()
  {
    return getMinOccurs(getXSDModelGroupDefinition());
  }

  public IADTObject getTopContainer()
  {
    XSDModelGroupDefinition group = getXSDModelGroupDefinition();
    XSDConcreteComponent container = group.getContainer();
    if (container instanceof XSDSchema || container instanceof XSDRedefine)
      return this;
    else
      return getGlobalXSDContainer(group);
  }
  public boolean isReadOnly()
  {
	  XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition) target;
	  if (hasSetReadOnlyField())
	  {
		  deletedTypes.put(xsdModelGroupDefinition.getName(), new Boolean(true));
		  changeReadOnlyField = false;
		  return readOnly;
	  }
	  else
	  {
		  if (deletedTypes!= null )
		  {
			  Boolean deleted = ((Boolean)deletedTypes.get(xsdModelGroupDefinition.getName()));
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
