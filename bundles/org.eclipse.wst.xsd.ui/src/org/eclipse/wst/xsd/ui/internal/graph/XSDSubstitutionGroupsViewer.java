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
package org.eclipse.wst.xsd.ui.internal.graph;
             
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.SubstitutionGroupViewerRootEditPart;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;


public class XSDSubstitutionGroupsViewer extends BaseGraphicalViewer
{               
  protected SubstitutionGroupViewerRootEditPart subGroupViewerRootEditPart;
  public XSDSubstitutionGroupsViewer(XSDEditor editor, ISelectionProvider menuSelectionProvider)
  {
    super(editor, menuSelectionProvider);       
  }        

  public void setInput(XSDConcreteComponent component)
  {               
    if (isInputEnabled)
    {
      input = null;         

      if (component instanceof XSDElementDeclaration || 
          component instanceof XSDSchema)
      {            
        input = component;
      }
      
      subGroupViewerRootEditPart.setInput(input);       
    }
  }     

  public void setSelection(XSDConcreteComponent component)
  {                    
    if (isSelectionEnabled)
    {                      
      //System.out.println("XSDComponentViewer.setSelection(" + component + ")");
      List editPartList = new ArrayList();    
      StructuredSelection selection = new StructuredSelection();
      if (component instanceof XSDElementDeclaration || 
          component instanceof XSDSchema ||
          component instanceof XSDModelGroup ||        
          component instanceof XSDModelGroupDefinition ||
          component instanceof XSDComplexTypeDefinition)
      {                     
        if (component != null)
        {
          EditPart editPart = getEditPart(subGroupViewerRootEditPart, component);    
          if (editPart != null)
          { 
            // TODO ... take a look at this to figure our why a newly added component
            // seems to have the wrong bounds at this point... is this a layout issue?           
            // As a temp hack I'm ignoring the selection of component with bounds (x, y) == (0, 0)
            // Perhaps a delayed selection is required?
            Rectangle bounds = ((GraphicalEditPart)editPart).getFigure().getBounds();
            if (bounds.x > 0 || bounds.y > 0)
            {
              editPartList.add(editPart); 
            }
          } 
        }    
      }              
      setSelection(new StructuredSelection(editPartList));
    }
  }
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.parts.AbstractEditPartViewer#hookControl()
   */
  protected void hookControl()
  {
    super.hookControl();
    subGroupViewerRootEditPart = new SubstitutionGroupViewerRootEditPart();
    setContents(subGroupViewerRootEditPart);
  }

}

