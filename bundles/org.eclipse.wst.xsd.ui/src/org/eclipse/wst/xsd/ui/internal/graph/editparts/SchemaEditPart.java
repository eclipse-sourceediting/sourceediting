/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.graph.editparts;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.EditPart;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.graph.GraphicsConstants;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.FillLayout;
import org.eclipse.wst.xsd.ui.internal.graph.figures.RoundedLineBorder;
import org.eclipse.wst.xsd.ui.internal.graph.model.Category;
import org.eclipse.wst.xsd.ui.internal.graph.model.XSDModelAdapterFactory;
import org.eclipse.xsd.XSDSchema;

public class SchemaEditPart extends BaseEditPart
{
  protected ContainerFigure containerFigure;
  protected Label label;

  //protected ContainerFigure childExpansionContainer;
  public IFigure getContentPane()
  {
    return containerFigure;
  }

  protected IFigure createFigure()
  {
    ContainerFigure outer = new ContainerFigure();
    outer.setBorder(new RoundedLineBorder(1, 6));
    outer.setForegroundColor(categoryBorderColor);
    FillLayout fillLayout = new FillLayout(4);
    outer.setLayoutManager(fillLayout); 
    //outer.getContainerLayout().setHorizontal(false);
    
    ContainerFigure r = new ContainerFigure();  
    r.setOutline(false);
    r.setMinimumSize(new Dimension(0, 0));
    r.setFill(true);
    r.setBackgroundColor(GraphicsConstants.elementBackgroundColor);
    outer.add(r);
    
	final int theMinHeight = 200;
    FillLayout outerLayout = new FillLayout()
    {
      protected Dimension calculatePreferredSize(IFigure parent, int width, int height)
      {
        Dimension d = super.calculatePreferredSize(parent, width, height);
        d.union(new Dimension(100, theMinHeight));
        return d;
      }
    };
    outerLayout.setHorizontal(false);
    outer.setLayoutManager(outerLayout);
    
    label = new Label();
    label.setForegroundColor(ColorConstants.black);
    label.setBorder(new MarginBorder(2, 4, 2, 4));    
    r.add(label);
    
    RectangleFigure line = new RectangleFigure();
    line.setPreferredSize(20, 1);
    outer.add(line);
    
    containerFigure = new ContainerFigure();
    //containerFigure.setBackgroundColor(ColorConstants.red);
    containerFigure.setBorder(new MarginBorder(4, 4, 4, 4));
    fillLayout = new FillLayout(4);
    containerFigure.setLayoutManager(fillLayout);
    //containerFigure.setLayoutManager(new FillLayout(false));
    /*
     * FlowLayout layout1 = new FlowLayout(false); layout1.setMajorSpacing(0);
     * layout1.setMinorSpacing(0); layout1.setStretchMinorAxis(true);
     * containerFigure.setLayoutManager(layout1);
     */
    outer.add(containerFigure);
    //childExpansionContainer = new ContainerFigure();
    //childExpansionContainer.getContainerLayout().setHorizontal(false);
    //childExpansionContainer.setOutlined(true);
    return outer;
  }

  protected List getModelChildren()
  {
    List list = new ArrayList();
    list.add(CategoryRowEditPart.DIRECTIVES_AND_NOTATIONS);
    list.add(CategoryRowEditPart.ELEMENTS_AND_TYPES);
    list.add(CategoryRowEditPart.MODEL_GROUPS_AND_ATTRIBUTES);      
    return list;
  }

  protected EditPart createChild(Object model)
  {
    CategoryRowEditPart result = new CategoryRowEditPart();
    result.setModel(model);
    result.setParent(this);
    result.setSchema((XSDSchema)getModel());
    return result;
  }
  

  protected void refreshVisuals()
  {
    super.refreshVisuals();
    String targetNamespaceValue = ((XSDSchema)getModel()).getTargetNamespace();
    if (targetNamespaceValue == null || targetNamespaceValue.length() == 0)
    {
      targetNamespaceValue = XSDEditorPlugin.getXSDString("_UI_GRAPH_XSDSCHEMA_NO_NAMESPACE");
    }     
    label.setText(XSDEditorPlugin.getXSDString("_UI_GRAPH_XSDSCHEMA") + " : " + targetNamespaceValue);
  }
}

class CategoryRowEditPart extends BaseEditPart
{  
  public static final int[] ELEMENTS_AND_TYPES = {Category.ELEMENTS, Category.TYPES };
  public static final int[] DIRECTIVES_AND_NOTATIONS =  {Category.DIRECTIVES, Category.NOTATIONS };
  public static final int[] MODEL_GROUPS_AND_ATTRIBUTES = {Category.GROUPS, Category.ATTRIBUTES};//, Category.COMPLEX_TYPES };
  
  protected XSDSchema schema;
  protected Figure contentPane;
  
  protected IFigure createFigure()
  {
    ContainerFigure containerFigure = new ContainerFigure();
    //containerFigure.setBackgroundColor(ColorConstants.red);
    containerFigure.setFill(true);
    containerFigure.setBorder(new MarginBorder(4, 4, 4, 4));
    org.eclipse.wst.xsd.ui.internal.gef.util.figures.FillLayout fillLayout = new org.eclipse.wst.xsd.ui.internal.gef.util.figures.FillLayout(4);
    fillLayout.setHorizontal(true);
    containerFigure.setLayoutManager(fillLayout);
    //containerFigure.setLayoutManager(new FillLayout(4));
    return containerFigure;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getContentPane()
   */
  public IFigure getContentPane()
  {
    return super.getContentPane();
  }

  public XSDSchema getSchema()
  {
    return schema;
  }

  public void setSchema(XSDSchema schema)
  {
    this.schema = schema;
  }
  

  protected List getModelChildren()
  {
    List categoryList = (List) XSDModelAdapterFactory.getAdapter(schema).getProperty(schema, "groups");
    return filterCategoryList(categoryList);
  }
  
  protected List filterCategoryList(List list)
  {
    List result = new ArrayList();
    int[] categoryTypes = (int[])getModel();
    for (Iterator i = list.iterator(); i.hasNext(); )
    {
      Category category = (Category)i.next();
      if (isMatching(categoryTypes, category))
      {
        result.add(category);
      }
    }
    return result;
  }
  
  private boolean isMatching(int[] categoryTypes, Category category)
  {
    boolean result = false;
    for (int i = 0; i < categoryTypes.length; i++)
    {
      if (categoryTypes[i] == category.getGroupType())
      {
        result = true;
        break;
      }
    }
    return result;
  }
}