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
                              
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.wst.xsd.ui.internal.graph.XSDInheritanceViewer;
import org.eclipse.wst.xsd.ui.internal.graph.model.Category;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDWildcard;



public class XSDEditPartFactory implements EditPartFactory
{   
  protected static XSDEditPartFactory instance;
                      
  public static XSDEditPartFactory getInstance()
  {
    if (instance == null)
    {               
      instance = new XSDEditPartFactory();
    }
    return instance;
  }
  
  public EditPart createEditPart(EditPart parent, Object model)
  {
    EditPart editPart = null;
           
    if (model instanceof Category)
    {                                  
      editPart = new CategoryEditPart();
    }
    else if (model instanceof XSDElementDeclaration)
    {       
      editPart = new ElementDeclarationEditPart();
    }    
    else if (model instanceof XSDComplexTypeDefinition)
    {
      if (parent.getViewer() instanceof XSDInheritanceViewer)
      {
        editPart = new RootComplexTypeDefinitionEditPart();
      }
      else
      {
        if (parent instanceof CategoryEditPart)
          editPart = new RootComplexTypeDefinitionEditPart();
        else
          editPart = new ComplexTypeDefinitionEditPart();             
      }
    }      
    else if (model instanceof XSDModelGroup)
    {       
      editPart = new ModelGroupEditPart();
    }      
    else if (model instanceof XSDModelGroupDefinition)
    {       
      if (parent instanceof CategoryEditPart)
        editPart = new RootModelGroupDefinitionEditPart();
      else
        editPart = new ModelGroupDefinitionEditPart();
    }  
    else if (model instanceof XSDSchema)
    {                                  
      editPart = new SchemaEditPart();
    }
    else if (model instanceof XSDWildcard)
    {                                  
      editPart = new WildcardEditPart();
    }
    else if (model instanceof XSDSimpleTypeDefinition)
    {
      editPart = new SimpleTypeDefinitionEditPart();
    }

    if (editPart != null)   
    {
      editPart.setModel(model);
      editPart.setParent(parent);
    }
    else
    {      
//      System.out.println("can't create editPart for " + model);
//      Thread.dumpStack();
    }
    return editPart;
  }
}
