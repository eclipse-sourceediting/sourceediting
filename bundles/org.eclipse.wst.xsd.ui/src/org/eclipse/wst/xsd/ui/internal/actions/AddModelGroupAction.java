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
package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xsd.ui.internal.commands.AddModelGroupCommand;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;


public class AddModelGroupAction extends Action
{
   protected AddModelGroupCommand command;
   protected XSDConcreteComponent parent;
  
   public static String getLabel(XSDCompositor compositor)
   {
     String result = XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_SEQUENCE"); //$NON-NLS-1$
     if (compositor != null)
     {
       if (compositor == XSDCompositor.CHOICE_LITERAL)
       {
         result = XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_CHOICE");  //$NON-NLS-1$
       }
       else if (compositor == XSDCompositor.ALL_LITERAL)
       {
         result = XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ALL");//$NON-NLS-1$
       }  
     }
     return result;
   }
  
   public AddModelGroupAction(XSDConcreteComponent parent, XSDCompositor compositor)
   {
     command = new AddModelGroupCommand(parent, compositor);
     this.parent = parent;
     setText(getLabel(compositor));     
   }   
   
   public void run()
   {
     DocumentImpl doc = (DocumentImpl) parent.getElement().getOwnerDocument();
     doc.getModel().beginRecording(this, getText());
     command.run();
     doc.getModel().endRecording(this);
   }
}
