/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.wst.xsd.ui.internal.actions.MoveXSDAttributeAction;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAttributeDeclarationAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDWildcardAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.CompartmentEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.ComplexTypeEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.AttributeGroupDefinitionEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.TargetConnectionSpacingFigureEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDAttributesForAnnotationEditPart;
import org.eclipse.wst.xsd.ui.internal.design.editparts.XSDBaseFieldEditPart;
import org.eclipse.wst.xsd.ui.internal.design.figures.GenericGroupFigure;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDWildcard;
import org.w3c.dom.Element;

public class XSDAttributeDragAndDropCommand extends BaseDragAndDropCommand
{
  public XSDAttributeDragAndDropCommand(EditPartViewer viewer, ChangeBoundsRequest request, GraphicalEditPart target, XSDBaseFieldEditPart itemToDrag, Point location)
  {
    super(viewer, request);
    setLabel(Messages._UI_ACTION_DRAG_DROP_ATTRIBUTE);
    this.target = target;
    this.itemToDrag = itemToDrag;
    this.location = location;
    setup();
  }

  public XSDAttributeDragAndDropCommand(XSDBaseFieldEditPart itemToDrag, XSDBaseFieldEditPart leftField, XSDBaseFieldEditPart rightField, int direction)
  {
    super(itemToDrag.getViewer(), null); 
    setLabel(Messages._UI_ACTION_DRAG_DROP_ATTRIBUTE); 
    this.itemToDrag = itemToDrag;
    canExecute = false;
    handleKeyboardDragAndDrop(leftField, rightField, direction);
  }
  
  protected void handleKeyboardDragAndDrop(XSDBaseFieldEditPart leftField, XSDBaseFieldEditPart rightField, int direction)
  {
    super.handleKeyboardDragAndDrop(leftField, rightField, direction);
    if (direction == PositionConstants.NORTH)
    {
      if (target == null)
      {
        target = rightField;
        this.location = target.getFigure().getBounds().getTop();
      }
      else if (!(leftField.getModel() instanceof XSDAttributeDeclarationAdapter)
                 || leftField.getModel() instanceof XSDWildcardAdapter)
      {
        target = rightField;
        this.location = target.getFigure().getBounds().getTop();
      }
    }
    if (location == null) return;
    setup();
  }
  
  protected void setup()
  {
    canExecute = false;

    // Drop target is attribute group ref
    if (target instanceof AttributeGroupDefinitionEditPart)
    {
      parentEditPart = (AttributeGroupDefinitionEditPart) target;
      if (((GenericGroupFigure) parentEditPart.getFigure()).getIconFigure().getBounds().contains(location))
      {
        xsdComponentToDrag = (XSDConcreteComponent) ((XSDAttributeDeclarationAdapter) itemToDrag.getModel()).getTarget();
        action = new MoveXSDAttributeAction(((AttributeGroupDefinitionEditPart) parentEditPart).getXSDAttributeGroupDefinition(), xsdComponentToDrag, null, null);
        canExecute = action.canMove();
      }
    }
    else if (target instanceof BaseFieldEditPart)
    {
      targetSpacesList = new ArrayList();
      // Calculate the list of all sibling field edit parts;
      List targetEditPartSiblings = calculateFieldEditParts();
      calculateAttributeGroupList();

      // Get 'left' and 'right' siblings
      doDrop(targetEditPartSiblings, itemToDrag);
    }
  }  
  
  /*
   * (non-Javadoc)
   * @see org.eclipse.wst.xsd.ui.internal.commands.BaseDragAndDropCommand#getElement()
   */
  protected Element getElement()
  {
	  XSDAttributeDeclarationAdapter adapter = (XSDAttributeDeclarationAdapter) itemToDrag.getModel();
	  XSDAttributeDeclaration target = (XSDAttributeDeclaration)adapter.getTarget();
	  XSDConcreteComponent parent = (XSDConcreteComponent)target.eContainer();
	  return parent.getElement();
  }

  protected void doDrop(List siblings, GraphicalEditPart movingEditPart)
  {
    commonSetup(siblings, movingEditPart);

    if ((previousRefComponent instanceof XSDAttributeDeclaration || previousRefComponent instanceof XSDWildcard) 
        && (nextRefComponent instanceof XSDAttributeDeclaration || nextRefComponent instanceof XSDWildcard))
    {
      XSDConcreteComponent parent = previousRefComponent.getContainer().getContainer();
      if (closerSibling == BELOW_IS_CLOSER)
      {
        parent = nextRefComponent.getContainer().getContainer();
      }
      action = new MoveXSDAttributeAction(parent, xsdComponentToDrag, previousRefComponent, nextRefComponent);
    }
    else if (previousRefComponent == null && (nextRefComponent instanceof XSDAttributeDeclaration || nextRefComponent instanceof XSDWildcard))
    {
      XSDConcreteComponent parent = nextRefComponent.getContainer().getContainer();
      if (closerSibling == ABOVE_IS_CLOSER)
      {
        if (leftSiblingEditPart == null)
        {
          action = new MoveXSDAttributeAction(parent, xsdComponentToDrag, previousRefComponent, nextRefComponent);
        }
        else if (parentEditPart != null)
        {
          action = new MoveXSDAttributeAction(parentEditPart.getXSDConcreteComponent(), xsdComponentToDrag, previousRefComponent, nextRefComponent);
        }
      }
      else
      {
        action = new MoveXSDAttributeAction(parent, xsdComponentToDrag, previousRefComponent, nextRefComponent);
      }
    }
    else if (previousRefComponent instanceof XSDAttributeDeclaration && nextRefComponent == null)
    {
      XSDConcreteComponent parent = previousRefComponent.getContainer().getContainer();
      if (closerSibling == ABOVE_IS_CLOSER)
      {
        action = new MoveXSDAttributeAction(parent, xsdComponentToDrag, previousRefComponent, nextRefComponent);
      }
      else
      {
        if (rightSiblingEditPart == null)
        {
          action = new MoveXSDAttributeAction(parent, xsdComponentToDrag, previousRefComponent, nextRefComponent);
        }
        else
        {
          action = new MoveXSDAttributeAction(parent, xsdComponentToDrag, previousRefComponent, nextRefComponent);
        }
      }
    }

    if (action != null)
      canExecute = action.canMove();
  }

  
  // Attribute Group related helper method 
  
  protected void calculateAttributeGroupList()
  {
    EditPart editPart = target;
    while (editPart != null)
    {
      if (editPart instanceof ComplexTypeEditPart)
      {
        List list = editPart.getChildren();
        for (Iterator i = list.iterator(); i.hasNext();)
        {
          Object child = i.next();
          if (child instanceof CompartmentEditPart)
          {
            List compartmentList = ((CompartmentEditPart) child).getChildren();
            for (Iterator it = compartmentList.iterator(); it.hasNext();)
            {
              Object obj = it.next();
              if (obj instanceof XSDAttributesForAnnotationEditPart)
              {
                XSDAttributesForAnnotationEditPart groups = (XSDAttributesForAnnotationEditPart) obj;
                List groupList = groups.getChildren();
                for (Iterator iter = groupList.iterator(); iter.hasNext();)
                {
                  Object groupChild = iter.next();
                  if (groupChild instanceof TargetConnectionSpacingFigureEditPart)
                  {
                    targetSpacesList.add(groupChild);
                  }
                  else if (groupChild instanceof AttributeGroupDefinitionEditPart)
                  {
                    getAttributeGroupEditParts((AttributeGroupDefinitionEditPart) groupChild);
                  }
                }
              }
            }
          }
        }
      }
      editPart = editPart.getParent();
    }

  }

  // Attribute Group related helper method
  
  protected List getAttributeGroupEditParts(AttributeGroupDefinitionEditPart attributeGroupEditPart)
  {
    List groupList = new ArrayList();
    List list = attributeGroupEditPart.getChildren();
    for (Iterator i = list.iterator(); i.hasNext();)
    {
      Object object = i.next();
      if (object instanceof TargetConnectionSpacingFigureEditPart)
      {
        targetSpacesList.add(object);
      }
      else if (object instanceof AttributeGroupDefinitionEditPart)
      {
        AttributeGroupDefinitionEditPart groupRef = (AttributeGroupDefinitionEditPart) object;
        List groupRefChildren = groupRef.getChildren();
        for (Iterator it = groupRefChildren.iterator(); it.hasNext();)
        {
          Object o = it.next();
          if (o instanceof TargetConnectionSpacingFigureEditPart)
          {
            targetSpacesList.add(o);
          }
          else if (o instanceof AttributeGroupDefinitionEditPart)
          {
            AttributeGroupDefinitionEditPart aGroup = (AttributeGroupDefinitionEditPart) o;
            groupList.add(aGroup);
            groupList.addAll(getAttributeGroupEditParts(aGroup));
          }
        }
      }
    }
    return groupList;
  }
}
