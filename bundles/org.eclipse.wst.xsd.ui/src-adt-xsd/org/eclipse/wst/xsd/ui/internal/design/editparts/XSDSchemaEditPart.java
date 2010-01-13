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
package org.eclipse.wst.xsd.ui.internal.design.editparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xsd.ui.internal.adapters.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.design.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.adt.design.editpolicies.KeyBoardAccessibilityEditPolicy;
import org.eclipse.wst.xsd.ui.internal.adt.typeviz.design.figures.HeadingFigure;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.design.editpolicies.SelectionHandlesEditPolicyImpl;
import org.eclipse.wst.xsd.ui.internal.design.layouts.FillLayout;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

public class XSDSchemaEditPart extends BaseEditPart
{
  protected Label label;

  protected Figure outer, contentFigure;
  protected HeadingFigure headingFigure;

  public IFigure getContentPane()
  {
    return contentFigure;
  }

  protected IFigure createFigure()
  {
    outer = new Figure();
    outer.setBorder(new LineBorder(1));

    FillLayout fillLayout = new FillLayout(4);
    outer.setLayoutManager(fillLayout);

    headingFigure = new HeadingFigure();
    outer.add(headingFigure);

    RectangleFigure line = new RectangleFigure()
    {
      public Dimension getPreferredSize(int wHint, int hHint)
      {
        Dimension d = super.getPreferredSize(wHint, hHint);
        d.height = 1;
        return d;
      }
    };
    ToolbarLayout lineLayout = new ToolbarLayout(false);
    lineLayout.setVertical(true);
    lineLayout.setStretchMinorAxis(true);
    line.setLayoutManager(lineLayout);
    outer.add(line);

    contentFigure = new Figure();
    contentFigure.setBorder(new MarginBorder(4));
    fillLayout = new FillLayout(4);
    contentFigure.setLayoutManager(fillLayout);

    outer.add(contentFigure);
    return outer;
  }

  protected List getModelChildren()
  {
    XSDSchemaAdapter schemaAdapter = (XSDSchemaAdapter) getModel();
    List list = new ArrayList();

// Bug 103870: undo blanks out schema view    
//    schemaAdapter.updateCategories();

    List templist = new ArrayList();
    templist.add(schemaAdapter.getCategory(CategoryAdapter.DIRECTIVES));
    Holder holder = new Holder(templist);
    list.add(holder);

    templist = new ArrayList();
    templist.add(schemaAdapter.getCategory(CategoryAdapter.ELEMENTS));
    templist.add(schemaAdapter.getCategory(CategoryAdapter.TYPES));
    holder = new Holder(templist);
    list.add(holder);

    templist = new ArrayList();
    templist.add(schemaAdapter.getCategory(CategoryAdapter.ATTRIBUTES));
    templist.add(schemaAdapter.getCategory(CategoryAdapter.GROUPS));
    holder = new Holder(templist);
    list.add(holder);

    return list;
  }

  protected EditPart createChild(Object model)
  {
    CategoryRowEditPart result = new CategoryRowEditPart();
    result.setModel(model);
    result.setParent(this);
    return result;
  }

  protected void refreshVisuals()
  {
    super.refreshVisuals();
    
    LineBorder border = (LineBorder) outer.getBorder();
    border.setWidth(isSelected ? 2 : 1);
    headingFigure.setSelected(isSelected);

    XSDSchemaAdapter schemaAdapter = (XSDSchemaAdapter) getModel();
    Image image = schemaAdapter.getImage();
    headingFigure.getLabel().setIcon(image);
    if (image != null)
    {
      headingFigure.getLabel().setIcon(XSDCommonUIUtils.getUpdatedImage((XSDConcreteComponent) schemaAdapter.getTarget(), image, false));
    }
    outer.repaint();
    
    String targetNamespaceValue = ((XSDSchema) ((XSDSchemaAdapter) getModel()).getTarget()).getTargetNamespace();
    targetNamespaceValue = TextProcessor.process(targetNamespaceValue);

    if (targetNamespaceValue == null || targetNamespaceValue.length() == 0)
    {
      targetNamespaceValue = Messages._UI_GRAPH_XSDSCHEMA_NO_NAMESPACE;
    }
    headingFigure.getLabel().setText(Messages._UI_GRAPH_XSDSCHEMA + " : " + targetNamespaceValue);  //$NON-NLS-1$
  }
  
  public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
  {
      EditPart result = null;
      if (editPart instanceof CategoryEditPart)
      {
        CategoryAdapter adapter = (CategoryAdapter)editPart.getModel();    
        switch (adapter.getGroupType())
        { 
          case CategoryAdapter.DIRECTIVES:
          {
            if (direction == PositionConstants.SOUTH)
            {
              result = getCategoryEditPart(CategoryAdapter.ELEMENTS);
            }  
            break;
          }
          case CategoryAdapter.ELEMENTS:
          {
            if (direction == PositionConstants.SOUTH)
            {
              result = getCategoryEditPart(CategoryAdapter.ATTRIBUTES);
            }           
            else if (direction == PositionConstants.NORTH)
            {
              result = getCategoryEditPart(CategoryAdapter.DIRECTIVES);
            } 
            break;
          }
          case CategoryAdapter.TYPES:
          {
            if (direction == PositionConstants.SOUTH)
            {
              result = getCategoryEditPart(CategoryAdapter.GROUPS);
            }           
            else if (direction == PositionConstants.NORTH)
            {
              result = getCategoryEditPart(CategoryAdapter.DIRECTIVES);
            } 
            break;        
          }
          case CategoryAdapter.ATTRIBUTES:      
          {
            if (direction == PositionConstants.NORTH)
            {
              result = getCategoryEditPart(CategoryAdapter.ELEMENTS);
            }    
            break;
          }   
          case CategoryAdapter.GROUPS:      
          {
            if (direction == PositionConstants.NORTH)
            {
              result = getCategoryEditPart(CategoryAdapter.TYPES);
            }    
            break;
          }
        }        
      } 
      else if (editPart == this)
      {       
        if (direction == KeyBoardAccessibilityEditPolicy.IN_TO_FIRST_CHILD)
        {
          result = ((CategoryRowEditPart)getChildren().get(0)).doGetRelativeEditPart(editPart, direction);        
        }          
      }  
      return result;               
  }
  
  protected EditPart getCategoryEditPart(int kind)
  {
    for (Iterator j = getChildren().iterator(); j.hasNext(); )      
    {    
      EditPart row = (EditPart)j.next();    
      for (Iterator i = row.getChildren().iterator(); i.hasNext(); )
      {
        EditPart editPart = (EditPart)i.next();
        if (editPart instanceof CategoryEditPart)
        {
          CategoryEditPart categoryEditPart = (CategoryEditPart)editPart;
          CategoryAdapter adapter = (CategoryAdapter)categoryEditPart.getModel();
          if (adapter.getGroupType() == kind)
          {
            return editPart;
          }
        }  
      }
    }
    return null;
  }  

  protected void createEditPolicies()
  {
    super.createEditPolicies();
    installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SelectionHandlesEditPolicyImpl());    
  }
  
  public String getReaderText()
  {
	  return headingFigure.getLabel().getSubStringText();
  }


  protected class Holder
  {
    List list;

    public Holder(List list)
    {
      this.list = list;
    }

    public List getList()
    {
      return list;
    }
  }

  protected class CategoryRowEditPart extends BaseEditPart
  {
    protected XSDSchema schema;
    protected Figure contentPane;

    protected IFigure createFigure()
    {
      Figure containerFigure = new Figure();
      containerFigure.setBorder(new MarginBorder(4, 4, 4, 4));
      // containerFigure.setBorder(new LineBorder(1));
      // containerFigure.setBackgroundColor(ColorConstants.green);

      FillLayout fillLayout = new FillLayout(4);
      fillLayout.setHorizontal(true);
      containerFigure.setLayoutManager(fillLayout);
      
      return containerFigure;
    }
    
    public EditPart doGetRelativeEditPart(EditPart editPart, int direction)
    {
      if (editPart instanceof CategoryEditPart)
      {
        if (direction == KeyBoardAccessibilityEditPolicy.OUT_TO_PARENT)
        {
          return getParent();
        }  
      }  
      else if (editPart instanceof XSDSchemaEditPart)
      {
        if (direction == KeyBoardAccessibilityEditPolicy.IN_TO_FIRST_CHILD)
        {
          return (EditPart)getChildren().get(0);
        }  
      }  
      return ((XSDSchemaEditPart)getParent()).doGetRelativeEditPart(editPart, direction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getContentPane()
     */
    public IFigure getContentPane()
    {
      return super.getContentPane();
    }

    protected List getModelChildren()
    {
      Holder holder = (Holder) getModel();
      return holder.getList();
    }

    protected void createEditPolicies()
    {
      super.createEditPolicies();
    }
    
    public boolean isSelectable()
    {
      return false;
    }

  }
}
