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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.LocationRequest;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionDialog;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDComponentSelectionProvider;
import org.eclipse.wst.xsd.ui.internal.dialogs.types.xsd.XSDSetTypeHelper;
import org.eclipse.wst.xsd.ui.internal.gef.util.figures.SpacingFigure;
import org.eclipse.wst.xsd.ui.internal.graph.XSDChildUtility;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphUtil;
import org.eclipse.wst.xsd.ui.internal.graph.XSDSubstitutionGroupChildUtility;
import org.eclipse.wst.xsd.ui.internal.graph.XSDSubstitutionGroupsViewer;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.ComboBoxCellEditorManager;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.ComponentNameDirectEditManager;
import org.eclipse.wst.xsd.ui.internal.graph.editpolicies.SimpleDirectEditPolicy;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ContainerFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.ExpandableGraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.figures.FillLayout;
import org.eclipse.wst.xsd.ui.internal.graph.figures.GraphNodeFigure;
import org.eclipse.wst.xsd.ui.internal.graph.model.XSDModelAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.w3c.dom.Element;

              

public class ElementDeclarationEditPart extends ExpandableGraphNodeEditPart
{
  public Label label;  
  protected Label contentIconLabel;
  protected Label typeValueLabel;
  protected SimpleDirectEditPolicy simpleDirectEditPolicy = new SimpleDirectEditPolicy();
  protected boolean isContentIconLabelSelected = false;

  protected final static String ELEMENT_TYPE_PLACE_HOLDER = "ELEMENT_TYPE_PLACE_HOLDER";

  public XSDParticle getXSDParticle()
  {
    Object o = getXSDElementDeclaration().getContainer(); 
    return (o instanceof XSDParticle) ? (XSDParticle)o : null;
  }        

  public XSDElementDeclaration getXSDElementDeclaration()
  {         
    return (XSDElementDeclaration)getModel();
  }
     
  protected boolean isDefaultExpanded()
  {
    // hack to expand up to its content.  The previous test didn't appear to work
    int depth = 0;
    for (EditPart part = this; part != null; part = part.getParent())
    {
      depth++;
    }      
    return depth <= 3;
  }
                     
  protected GraphNodeFigure createGraphNodeFigure()
  {
    ExpandableGraphNodeFigure figure = new ExpandableGraphNodeFigure();

    figure.getOutlinedArea().setFill(true);
    figure.getOutlinedArea().setLayoutManager(new FillLayout());

    label = new Label();    
    figure.getIconArea().add(label);     
    label.setFont(mediumBoldFont);
    
    SpacingFigure spacingFigure = new SpacingFigure();
    figure.getIconArea().add(spacingFigure);
    
    contentIconLabel = new Label();
    //contentIcon.setBorder(new MarginBorder(2, 2, 2, 10));
    figure.getIconArea().add(contentIconLabel);

    // A sneaky null check.... getViewer() does a getRoot(), but getRoot() might be null
    // same with getParent()
    if (getParent() != null && getRoot() != null && getViewer() instanceof XSDSubstitutionGroupsViewer)
    {
      figure.getOuterContentArea().getContainerLayout().setSpacing(5);
    }
    else
    {  
      RectangleFigure line = new RectangleFigure();
      line.setPreferredSize(20, 1);   
      figure.getOutlinedArea().add(line, 1);

      figure.getInnerContentArea().setLayoutManager(new FillLayout(2));
      figure.getInnerContentArea().setBorder(new MarginBorder(2,2,2,1));

      ContainerFigure labelGroup = new ContainerFigure();   
      Label typeLabel = new Label("type");
      labelGroup.add(typeLabel);
      labelGroup.setBorder(new MarginBorder(0, 4, 0, 4));      

      Label equalsLabel = new Label(" = ");
      labelGroup.add(equalsLabel);

      typeValueLabel = new Label();                         
      labelGroup.add(typeValueLabel);
      figure.getOutlinedArea().add(labelGroup, 2);
    }
    return figure;
  }          

  protected ExpandableGraphNodeFigure getExpandableGraphNodeFigure()
  {
    return (ExpandableGraphNodeFigure)graphNodeFigure;
  }                                            
                    
  protected List getModelChildren() 
  {
    XSDTypeDefinition typeDef = getXSDElementDeclaration().getTypeDefinition();
    
    // Special case simple type.   Need to add it to the list as well
    List list = new ArrayList();
    if (typeDef instanceof XSDSimpleTypeDefinition)
    {
      list.add((XSDSimpleTypeDefinition)typeDef);
      if (getExpandableGraphNodeFigure().isExpanded())
      {
        list.addAll(getModelChildrenHelper());
      }
      return list;
    }
    return getExpandableGraphNodeFigure().isExpanded() ? getModelChildrenHelper() : Collections.EMPTY_LIST;
  }  
                       
  protected List getModelChildrenHelper()
  {
    if (getViewer() instanceof XSDSubstitutionGroupsViewer)
    {
      return XSDSubstitutionGroupChildUtility.getModelChildren(getXSDElementDeclaration().getResolvedElementDeclaration());
    }
    else
    {
      return XSDChildUtility.getModelChildren(getXSDElementDeclaration().getResolvedElementDeclaration());
    }
  }           
                
  protected void refreshContentIcon()
  {
    String iconName = null;
    XSDTypeDefinition td = getXSDElementDeclaration().getResolvedElementDeclaration().getTypeDefinition();

    if (td instanceof XSDComplexTypeDefinition)
    {
      XSDComplexTypeDefinition complexTypeDefinition = (XSDComplexTypeDefinition)td;
      if (complexTypeDefinition.getAttributeUses().size() > 0)
      {
        iconName = "icons/XSDAttribute.gif";  
      }  
    }  
    Image image = iconName != null ? XSDEditorPlugin.getXSDImage(iconName) : null;
    contentIconLabel.setIcon(image);    
  }

  protected void refreshVisuals()
  { 
    String text = getXSDElementDeclaration().isElementDeclarationReference() ?
                  getXSDElementDeclaration().getResolvedElementDeclaration().getQName(getXSDElementDeclaration().getSchema()) :
                  getXSDElementDeclaration().getName();

    label.setText(text);
    
    ContainerFigure rectangle = graphNodeFigure.getOutlinedArea();
    if (XSDGraphUtil.isEditable(getXSDElementDeclaration()))
    {
      rectangle.setBorder(new LineBorder(isSelected ? ColorConstants.black : elementBorderColor, 2));
      rectangle.setBackgroundColor(elementBackgroundColor);
      rectangle.setForegroundColor(elementBorderColor);
      
      graphNodeFigure.getInnerContentArea().setForegroundColor(ColorConstants.black);
      if (XSDGraphUtil.isEditable(getXSDElementDeclaration().getResolvedElementDeclaration()))
      { 
        // give label 'editable' colour
        graphNodeFigure.getInnerContentArea().setForegroundColor(elementLabelColor);    
      }
      else
      {   
        // give label 'read only' colour
        graphNodeFigure.getInnerContentArea().setForegroundColor(elementBorderColor);
      }
      label.setBackgroundColor(elementBackgroundColor);
      label.setForegroundColor(elementLabelColor);
    }
    else
    {
      rectangle.setBorder(new LineBorder(isSelected ? ColorConstants.black : readOnlyBorderColor, 2));
      rectangle.setBackgroundColor(readOnlyBackgroundColor);
      rectangle.setForegroundColor(readOnlyBorderColor);
      graphNodeFigure.getInnerContentArea().setForegroundColor(readOnlyBorderColor);     
      label.setBackgroundColor(readOnlyBackgroundColor);
    }
                                                                                   
    if (getXSDElementDeclaration().isElementDeclarationReference())
    {
      label.setIcon(XSDEditorPlugin.getXSDImage("icons/GraphViewElementRef.gif"));
      label.setBorder(new MarginBorder(0, 0, 0, 4)); 
    }
    else
    {                     
      label.setIcon(null);
      label.setBorder(new MarginBorder(0, 6, 0, 4));
    }
    
    if (getXSDParticle() != null)
    { 
      refreshOccurenceLabel(getXSDParticle().getMinOccurs(), getXSDParticle().getMaxOccurs());
    }                      
     
   
    if (typeValueLabel != null)
    {
      XSDElementDeclaration ed = getXSDElementDeclaration();
      if (ed.getElement() != null)
      {
        String type = ed.getElement().getAttribute("type");
        if (type == null)
        {
          type = "";
        }
        if (!getXSDElementDeclaration().isElementDeclarationReference())
				{   
          typeValueLabel.setText(type.equals("") ? "<anonymous>" : type);
				}
        else // if it is a ref, we show the resolved type
        {
        	String resolvedType = "";
        	if (ed.getResolvedElementDeclaration() != null)
        	{
        		if (ed.getResolvedElementDeclaration().getTypeDefinition() != null)
        	  {
        	    resolvedType = ed.getResolvedElementDeclaration().getTypeDefinition().getQName(ed.getSchema());
              
              // if null, it has an anonymous type that has no resolved type
              if (resolvedType == null)
              {
                resolvedType = "<anonymous>";
              }
        	  }
        	}
          typeValueLabel.setText(resolvedType);
				}
      }
    }
    refreshContentIcon();
  } 
 
                                                                        
  public void performRequest(Request request)
  {
  	if (request.getType() == RequestConstants.REQ_DIRECT_EDIT ||
        request.getType() == RequestConstants.REQ_OPEN)
    {                                        
      if (XSDGraphUtil.isEditable(getXSDElementDeclaration()))
      {
        if (request instanceof LocationRequest)
        {
          LocationRequest locationRequest = (LocationRequest)request;
          Point p = locationRequest.getLocation();
          isContentIconLabelSelected = false;
          
          if (hitTest(label, p))
          {
  		      performDirectEditForLabel();
          }
          else if (hitTest(typeValueLabel, p))
          {                             
   		      performDirectEditForTypeValueLabel();
          }
        }
      }
    }
  } 
         
  private void performDirectEditForTypeValueLabel()
  {
		if (!getXSDElementDeclaration().isElementDeclarationReference())
		{    
//      TypeReferenceDirectEditManager manager = new TypeReferenceDirectEditManager(this, getXSDElementDeclaration(), typeValueLabel);   
//      simpleDirectEditPolicy.setDelegate(manager);
//      manager.show();
//TODO remove TypeReferenceDirectEditManager since it is not used any longer
            
            Shell shell = Display.getCurrent().getActiveShell();
            IWorkbench workbench = XSDEditorPlugin.getPlugin().getWorkbench();
            IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
            IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
            IFile currentIFile = ((IFileEditorInput)editorPart.getEditorInput()).getFile();
            
            XSDSchema schema = getXSDElementDeclaration().getSchema();
                
            XSDComponentSelectionProvider provider = new XSDComponentSelectionProvider(currentIFile, schema);
            XSDComponentSelectionDialog dialog = new XSDComponentSelectionDialog(shell, "Set Type", provider);  // TODO: Externalize This
            provider.setDialog(dialog);
            
            dialog.setBlockOnOpen(true);
            dialog.create();

            if (dialog.open() == Window.OK) {
                Element element = getXSDElementDeclaration().getElement();
                XSDSetTypeHelper helper = new XSDSetTypeHelper(currentIFile, schema);
                helper.setType(element, "type", dialog.getSelection());
            }        

            
		}
		// just ignore type edit for element ref's
  }                                                                

                                                       
  private void performDirectEditForLabel()
  {    
    if (getXSDElementDeclaration().isElementDeclarationReference())   
    {
      ComboBoxCellEditorManager manager = new ComboBoxCellEditorManager(this, label)
      {
         protected List computeComboContent()
         {             
           XSDSchema schema = getXSDElementDeclaration().getSchema();
           List globalElementNameList = new ArrayList();
           if (schema != null)
           {
             TypesHelper typesHelper = new TypesHelper(schema);
             globalElementNameList = typesHelper.getGlobalElements();
           }                                           
           return globalElementNameList;
         }

         public void performModify(String value)
         { 
           getXSDElementDeclaration().getElement().setAttribute("ref", value);
         }
      };
      simpleDirectEditPolicy.setDelegate(manager);
      manager.show();
    }
    else
    {
      ComponentNameDirectEditManager manager = new ComponentNameDirectEditManager(this, label, getXSDElementDeclaration());   
      simpleDirectEditPolicy.setDelegate(manager);
      manager.show();
    }
  }                                
  

  protected void createEditPolicies()
  {  
    super.createEditPolicies();
  	installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, simpleDirectEditPolicy);
  }
  
  
  public void activate() 
  {
    super.activate();
    if (getXSDParticle() != null)
    {
      XSDModelAdapterFactory.addModelAdapterListener(getXSDParticle(), this);
    }
  }
  /** 
   * Apart from the deactivation done in super, the source
   * and target connections are deactivated, and the visual
   * part of the this is removed.
   *
   * @see #activate() 
   */
  public void deactivate() 
  {
    if (getXSDParticle() != null)
    {
      XSDModelAdapterFactory.removeModelAdapterListener(getXSDParticle(), this);
    }
    super.deactivate();
  }   

  public boolean isContentIconLabelSelected()
  {
    return isContentIconLabelSelected;
  }

  protected void addChildVisual(EditPart childEditPart, int index)
  {
    IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
    if (childEditPart instanceof SimpleTypeDefinitionEditPart)
    {
      graphNodeFigure.getIconArea().add(child, index+ 1);
      SpacingFigure spacingFigure = new SpacingFigure();
      graphNodeFigure.getIconArea().add(spacingFigure, index+1);
    }
    else
    {
      getContentPane().add(child, index);
    }
  }
  
  protected void removeChildVisual(EditPart childEditPart)
  {
    IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
    if (childEditPart instanceof SimpleTypeDefinitionEditPart)
    {
      graphNodeFigure.getIconArea().remove(child);
    }    
    else
    {
      super.removeChildVisual(childEditPart);
    }
  }

  public void doEditName()
  {
    ComponentNameDirectEditManager manager = new ComponentNameDirectEditManager(this, label, getXSDElementDeclaration());
    simpleDirectEditPolicy.setDelegate(manager);
    manager.show();
  }

}
