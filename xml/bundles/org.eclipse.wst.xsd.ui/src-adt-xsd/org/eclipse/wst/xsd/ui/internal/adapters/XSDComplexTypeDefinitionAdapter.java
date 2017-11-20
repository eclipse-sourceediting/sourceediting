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
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.DeleteAction;
import org.eclipse.wst.xsd.ui.internal.adt.actions.SetInputToGraphView;
import org.eclipse.wst.xsd.ui.internal.adt.actions.ShowPropertiesViewAction;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObject;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IComplexType;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IModel;
import org.eclipse.wst.xsd.ui.internal.adt.facade.IType;
import org.eclipse.wst.xsd.ui.internal.adt.outline.ITreeElement;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAnyAttributeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAnyElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeDeclarationAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDAttributeGroupDefinitionAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDElementAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDEnumerationFacetAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDModelGroupAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.SetBaseTypeAction;
import org.eclipse.wst.xsd.ui.internal.common.actions.OpenInNewEditor;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDElementCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateNameCommand;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.SpaceFiller;
import org.eclipse.wst.xsd.ui.internal.design.editparts.model.TargetConnectionSpaceFiller;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDAttributeGroupContent;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDRedefine;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;

public class XSDComplexTypeDefinitionAdapter extends XSDTypeDefinitionAdapter implements IComplexType, IADTObjectListener
{
  protected List fields = null;
  protected List otherThingsToListenTo = null;
  protected boolean readOnly;
  protected boolean changeReadOnlyField =false;
  protected HashMap deletedTypes = new HashMap();

  public XSDComplexTypeDefinition getXSDComplexTypeDefinition()
  {
    return (XSDComplexTypeDefinition) target;
  }

  public IType getSuperType()
  {
    XSDTypeDefinition td = getXSDTypeDefinition().getBaseType();

    // test to filter out the 'anyType' type ... don't want to see that
    //
    if (td != null && !td.getName().equals("anyType")) //$NON-NLS-1$
    {
      return (IType) XSDAdapterFactory.getInstance().adapt(td);
    }
    return null;
  }

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
    if (fields == null)
    {
      fields = new ArrayList();
      otherThingsToListenTo = new ArrayList();

      XSDVisitorForFields visitor = new XSDVisitorForFieldsWithSpaceFillers();
      visitor.visitComplexTypeDefinition(getXSDComplexTypeDefinition());
      populateAdapterList(visitor.concreteComponentList, fields);
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
    }
    return fields;
  }

  class XSDVisitorForFieldsWithSpaceFillers extends XSDVisitorForFields
  {
    public XSDVisitorForFieldsWithSpaceFillers()
    {
      super();
    }

    public void visitAttributeGroupDefinition(XSDAttributeGroupDefinition attributeGroup)
    {
      for (Iterator it = attributeGroup.getContents().iterator(); it.hasNext();)
      {
        Object o = it.next();
        if (o instanceof XSDAttributeUse)
        {
          XSDAttributeUse attributeUse = (XSDAttributeUse) o;
          concreteComponentList.add(attributeUse.getAttributeDeclaration());
          thingsWeNeedToListenTo.add(attributeUse.getAttributeDeclaration());
        }
        else if (o instanceof XSDAttributeGroupDefinition)
        {
          XSDAttributeGroupDefinition attrGroup = (XSDAttributeGroupDefinition) o;
          thingsWeNeedToListenTo.add(attrGroup);
          if (attrGroup.isAttributeGroupDefinitionReference())
          {
            attrGroup = attrGroup.getResolvedAttributeGroupDefinition();
            if (attrGroup.getContents().size() == 0)
            {
              concreteComponentList.add(new SpaceFiller("attribute")); //$NON-NLS-1$
            }
            visitAttributeGroupDefinition(attrGroup);
          }
        }
      }
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

  public List getModelGroups()
  {
    List groups = new ArrayList();
    groups.addAll(XSDChildUtility.getModelChildren(getXSDComplexTypeDefinition()));
    return groups;
  }

  public List getAttributeGroupContent()
  {
    EList attrContent = getXSDComplexTypeDefinition().getAttributeContents();
    List attrUses = new ArrayList();
    List list = new ArrayList();

    for (Iterator it = attrContent.iterator(); it.hasNext();)
    {
      XSDAttributeGroupContent attrGroupContent = (XSDAttributeGroupContent) it.next();

      if (attrGroupContent instanceof XSDAttributeGroupDefinition)
      {
        XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition) attrGroupContent;
        list.add(XSDAdapterFactory.getInstance().adapt(attributeGroupDefinition));
        getAttributeUses(attributeGroupDefinition, attrUses);
      }
      else
      {
        attrUses.add(attrGroupContent);
        list.add(new TargetConnectionSpaceFiller(this));
      }
    }
    return list;
  }

  public boolean isComplexType()
  {
    return true;
  }

  public void notifyChanged(Notification msg)
  {
    clearFields();
    super.notifyChanged(msg);
  }

  public Command getUpdateNameCommand(String newName)
  {
    return new UpdateNameCommand(Messages._UI_ACTION_UPDATE_NAME, getXSDComplexTypeDefinition(), newName);
  }

  public Command getAddNewFieldCommand(String fieldKind)
  {
    return new AddXSDElementCommand(Messages._UI_ACTION_ADD_FIELD, getXSDComplexTypeDefinition());
  }

  public Command getDeleteCommand()
  {
    return new DeleteCommand(getXSDComplexTypeDefinition());
  }

  protected class AddNewFieldCommand extends Command
  {
    protected String defaultName;
    protected String fieldKind;

    AddNewFieldCommand(String defaultName, String fieldKind)
    {
      this.defaultName = defaultName;
      this.fieldKind = fieldKind;
    }
  }

  public String[] getActions(Object object)
  {
    List list = new ArrayList();
    Object schema = getEditorSchema();
    
    XSDComplexTypeDefinition complexType = getXSDComplexTypeDefinition();
    Object contentType = getContentType();
    XSDDerivationMethod derivation = complexType.getDerivationMethod();
    if (contentType instanceof XSDSimpleTypeDefinition)
    {
      List fields = getFields();
      boolean hasSimpleContentAttributes = false;
      for (Iterator iterator = fields.iterator(); iterator.hasNext(); )
      {
        Object field = iterator.next();
        // We have attributes, so we need to add the compartment for housing the attributes
        if (field instanceof XSDAttributeDeclarationAdapter)
        {
          hasSimpleContentAttributes = true;
          break;
        }
      }

      if (hasSimpleContentAttributes || XSDDerivationMethod.EXTENSION_LITERAL.equals(derivation))
      {
        list.add(AddXSDAttributeDeclarationAction.ID);
      }
      else if (XSDDerivationMethod.RESTRICTION_LITERAL.equals(derivation))
      {
        list.add(AddXSDEnumerationFacetAction.ID);
      }
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(SetBaseTypeAction.ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(DeleteAction.ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
    }
    else
    {
      list.add(AddXSDElementAction.ID);
      list.add(AddXSDElementAction.REF_ID);
      list.add(AddXSDAnyElementAction.ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(AddXSDAttributeDeclarationAction.ID);
      list.add(AddXSDAttributeDeclarationAction.REF_ID);
      list.add(AddXSDAttributeGroupDefinitionAction.REF_ID);
      list.add(AddXSDAnyAttributeAction.ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(AddXSDModelGroupAction.SEQUENCE_ID);
      list.add(AddXSDModelGroupAction.CHOICE_ID);
      list.add(AddXSDModelGroupAction.ALL_ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(DeleteAction.ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
      list.add(SetBaseTypeAction.ID);
      list.add(BaseSelectionAction.SEPARATOR_ID);
    }
    if (complexType.getSchema() == schema)
    {
      XSDConcreteComponent container = complexType.getContainer();
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
    String[] result = new String[list.size()];
    list.toArray(result);
    return result;
  }

  public void propertyChanged(Object object, String property)
  {
    clearFields();
    notifyListeners(this, null);
  }

  class BogusAction extends Action
  {
    BogusAction(String name)
    {
      super(name);
    }

    public void run()
    {
      // TODO Auto-generated method stub
      super.run();
    }
  }

  public ITreeElement[] getChildren()
  {
    XSDComplexTypeDefinition xsdComplexTypeDefinition = getXSDComplexTypeDefinition();
    List list = new ArrayList();
    // Add attributes
    for (Iterator i = xsdComplexTypeDefinition.getAttributeContents().iterator(); i.hasNext();)
    {
      Object obj = i.next();
      if (obj instanceof XSDAttributeUse)
      {
        list.add(((XSDAttributeUse)obj).getAttributeDeclaration());
      }
      else if (obj instanceof XSDAttributeGroupDefinition)
      {
        getAttributeUses((XSDAttributeGroupDefinition) obj, list);
      }
    }
    // Add enumerations     
    boolean canHaveEnumerations = xsdComplexTypeDefinition.getContentType() instanceof XSDSimpleTypeDefinition &&	
    		XSDDerivationMethod.RESTRICTION_LITERAL.equals(xsdComplexTypeDefinition.getDerivationMethod());
    if (canHaveEnumerations)
    {
    	Object contentType = getContentType();
    	if (contentType != null)
    	{
    		for (Iterator iterator = ((XSDSimpleTypeDefinition)contentType).getEnumerationFacets().iterator(); iterator.hasNext();)
    		{
    			XSDEnumerationFacet enumerationFacet = (XSDEnumerationFacet)iterator.next();
    			list.add(enumerationFacet);
    		}
    	}
    }
    XSDWildcard anyAttr = xsdComplexTypeDefinition.getAttributeWildcard();
    if (anyAttr != null)
      list.add(anyAttr);
    // get immediate XSD Model Group of this complex type
    if (xsdComplexTypeDefinition.getContent() != null)
    {
      XSDComplexTypeContent xsdComplexTypeContent = xsdComplexTypeDefinition.getContent();
      if (xsdComplexTypeContent instanceof XSDParticle)
      {
        XSDParticleContent particleContent = ((XSDParticle) xsdComplexTypeContent).getContent();
        if (particleContent instanceof XSDModelGroup)
        {
          list.add(particleContent);
        }
      }
    }
    // get inherited XSD Model Group of this complex type
    boolean showInheritedContent = XSDEditorPlugin.getPlugin().getShowInheritedContent();
    if (showInheritedContent)
    {
      XSDTypeDefinition typeDef = xsdComplexTypeDefinition.getBaseTypeDefinition();
      if (typeDef instanceof XSDComplexTypeDefinition)
      {
        XSDComplexTypeDefinition baseCT = (XSDComplexTypeDefinition) typeDef;
        if (baseCT.getTargetNamespace() != null && !baseCT.getTargetNamespace().equals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001))
        {
          if (baseCT.getContent() != null)
          {
            XSDComplexTypeContent xsdComplexTypeContent = baseCT.getContent();
            if (xsdComplexTypeContent instanceof XSDParticle)
            {
              XSDParticleContent particleContent = ((XSDParticle) xsdComplexTypeContent).getContent();
              if (particleContent instanceof XSDModelGroup)
              {
                list.add(particleContent);
              }
            }
          }
        }
      }
    }
    List adapterList = new ArrayList();
    populateAdapterList(list, adapterList);
    return (ITreeElement[]) adapterList.toArray(new ITreeElement[0]);
  }

  public Image getImage()
  {
    if (isReadOnly())
    {
      return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDComplexTypedis.gif"); //$NON-NLS-1$
    }
    return XSDEditorPlugin.getPlugin().getIcon("obj16/XSDComplexType.gif"); //$NON-NLS-1$
  }

  public String getText()
  {
    XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition) target;

    StringBuffer result = new StringBuffer();

    result.append(xsdComplexTypeDefinition.getName() == null ? "local type" : xsdComplexTypeDefinition.getName()); //$NON-NLS-1$

    XSDTypeDefinition baseTypeDefinition = xsdComplexTypeDefinition.getBaseTypeDefinition();
    if (baseTypeDefinition != null && baseTypeDefinition != xsdComplexTypeDefinition.getContent() && baseTypeDefinition.getName() != null && !XSDConstants.isURType(baseTypeDefinition))
    {
    	
   	try{
    		String qName = baseTypeDefinition.getQName(xsdComplexTypeDefinition);
    		result.append(" : "); //$NON-NLS-1$
    	    result.append(qName);
    	}
    	catch (Exception e)    	
    	{    		
    		return result.toString();
    	}
    
    }

    return result.toString();
  }

  public void getAttributeUses(XSDAttributeGroupDefinition attributeGroupDefinition, List list)
  {
    Iterator i = attributeGroupDefinition.getResolvedAttributeGroupDefinition().getContents().iterator();

    while (i.hasNext())
    {
      XSDAttributeGroupContent attrGroupContent = (XSDAttributeGroupContent) i.next();

      if (attrGroupContent instanceof XSDAttributeGroupDefinition)
      {
        getAttributeUses((XSDAttributeGroupDefinition) attrGroupContent, list);
      }
      else
      {
        list.add(XSDAdapterFactory.getInstance().adapt(attrGroupContent));
      }
    }
  }

  public IModel getModel()
  {
    Adapter adapter = XSDAdapterFactory.getInstance().adapt(getXSDComplexTypeDefinition().getSchema());
    return (IModel)adapter;
  }

  public boolean isFocusAllowed()
  {
    return true;
  }

  public boolean isAnonymous()
  {
    XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition) target;
    return !(xsdComplexTypeDefinition.eContainer() instanceof XSDSchema ||
             xsdComplexTypeDefinition.eContainer() instanceof XSDRedefine);
  }

  public IADTObject getTopContainer()
  {
    return this;
  }
  
  public boolean isAbstract()
  {
    return getXSDComplexTypeDefinition().isAbstract();
  }

  public Object getContentType()
  {
    return getXSDComplexTypeDefinition().getContent();
  }
  public boolean isReadOnly()
  {
	  XSDComplexTypeDefinition xsdComplexTypeDefinition = (XSDComplexTypeDefinition) target;
	  if (hasSetReadOnlyField())
	  {
		  deletedTypes.put(xsdComplexTypeDefinition.getName(), new Boolean(true));
		  changeReadOnlyField = false;
		  return readOnly;
	  }
	  else
	  {
		  if (deletedTypes!= null )
		  {
			  Boolean deleted = ((Boolean)deletedTypes.get(xsdComplexTypeDefinition.getName()));
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

public void updateDeletedMap(String addComponent)
{
	if (deletedTypes.get(addComponent) != null)
	{
		deletedTypes.clear();
	}
}
}
