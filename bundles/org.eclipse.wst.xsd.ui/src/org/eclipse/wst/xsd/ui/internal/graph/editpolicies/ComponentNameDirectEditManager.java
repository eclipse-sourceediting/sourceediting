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
                                                  
import org.eclipse.draw2d.Label;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.GlobalElementRenamer;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.GlobalGroupRenamer;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.GlobalSimpleOrComplexTypeRenamer;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDSwitch;



public class ComponentNameDirectEditManager extends TextCellEditorManager
{
  protected XSDNamedComponent component;

  public ComponentNameDirectEditManager(GraphicalEditPart source,	Label label, XSDNamedComponent component)
  {
    super(source, label);  
    this.component = component;
  }

  public void performModify(final String value)
  {                     
    DelayedRenameRunnable runnable = new DelayedRenameRunnable(component, value);
    Display.getCurrent().asyncExec(runnable);  
  }      

  protected static class DelayedRenameRunnable implements Runnable
  {
    protected XSDNamedComponent component;
    protected String name;

    public DelayedRenameRunnable(XSDNamedComponent component, String name)
    {                                                               
      this.component = component;
      this.name = name;
    }                                                              

    public void run()
    {                             
      XSDSwitch xsdSwitch = new XSDSwitch()
      {                   
        public Object caseXSDTypeDefinition(XSDTypeDefinition object)
        {
          new GlobalSimpleOrComplexTypeRenamer(object, name).visitSchema(object.getSchema());
          return null;
        } 
      
        public Object caseXSDElementDeclaration(XSDElementDeclaration object)
        {           
          if (object.isGlobal())
          {
            new GlobalElementRenamer(object, name).visitSchema(object.getSchema());
          }
          return null;
        }
      
        public Object caseXSDModelGroupDefinition(XSDModelGroupDefinition object)
        {
          new GlobalGroupRenamer(object, name).visitSchema(object.getSchema());
          return null;
        }
      };
      xsdSwitch.doSwitch(component); 
      component.setName(name);
    }
  }
}
