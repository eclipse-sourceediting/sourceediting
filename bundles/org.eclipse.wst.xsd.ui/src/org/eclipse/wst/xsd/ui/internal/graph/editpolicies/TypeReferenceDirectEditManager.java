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
package org.eclipse.wst.xsd.ui.internal.graph.editpolicies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.graph.editparts.BaseEditPart;
import org.eclipse.wst.xsd.ui.internal.util.TypesHelper;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;



public class TypeReferenceDirectEditManager extends ComboBoxCellEditorManager
{
  protected BaseEditPart editPart;                                                
  protected XSDElementDeclaration ed;

  
  public TypeReferenceDirectEditManager(BaseEditPart source,	XSDElementDeclaration ed, Label label)
  {
    super(source, label);  
    editPart = source;               
    this.ed = ed;
  }

  protected List computeComboContent()
  {             
    XSDSchema schema = ed.getSchema();
    List typeNameList = new ArrayList();
    if (schema != null)
    {         
      TypesHelper typesHelper = new TypesHelper(schema);
      typeNameList.addAll(typesHelper.getUserSimpleTypeNamesList());
      typeNameList.addAll(typesHelper.getUserComplexTypeNamesList());               
      typeNameList.addAll(typesHelper.getBuiltInTypeNamesList());
    }                        
    return typeNameList;
  }
    
  public void performModify(String value)
  {                                               
    // we need to perform an asyncExec here since the 'host' editpart may be removed
    // as a side effect of performing the action
    DelayedRenameRunnable runnable = new DelayedRenameRunnable(editPart, ed, value);
    Display.getCurrent().asyncExec(runnable);   
  }  
   

  protected List computeSortedList(List list)
  {
    return TypesHelper.sortList(list);
  }

  protected static class DelayedRenameRunnable implements Runnable
  {
    protected BaseEditPart editPart;                                                
    protected EditPart editPartParent;
    protected XSDElementDeclaration ed;
    protected String name;

    public DelayedRenameRunnable(BaseEditPart editPart, XSDElementDeclaration ed, String name)
    {                                                               
      this.editPart = editPart;
      editPartParent = editPart.getParent();
      this.ed = ed;
      this.name = name;
    }                                                              

    public void run()
    {                             
      //BaseGraphicalViewer viewer = editPart.getBaseGraphicalViewer();
      //viewer.setInputEnabled(false);
      //viewer.setSelectionEnabled(false);      
      if (name.equals("<anonymous>"))
      {          
        try
        {
          ed.getResolvedElementDeclaration().getElement().removeAttribute("type");
        }
        catch (Exception e)
        {
        }
      }
      else
      {
        if (ed.getResolvedElementDeclaration().getAnonymousTypeDefinition() != null) // isSetAnonymousTypeDefinition())
        {
          if (!(name.equals("<anonymous>")))
          {
            ed.getResolvedElementDeclaration().getElement().setAttribute("type", name);
            XSDDOMHelper.updateElementToNotAnonymous(ed.getResolvedElementDeclaration().getElement());
//            ed.getResolvedElementDeclaration().setAnonymousTypeDefinition(null);
            return;
          }
        }
        ed.getResolvedElementDeclaration().getElement().setAttribute("type", name);
      }
      //viewer.setInputEnabled(true);
      //viewer.setSelectionEnabled(true);
      //viewer.setSelection(new StructuredSelection(editPartParent));
    }
  }

}
