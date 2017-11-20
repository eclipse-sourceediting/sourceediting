/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaDirectiveAdapter;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDImportCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDIncludeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddXSDRedefineCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.BaseCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDDirectivesSchemaLocationUpdater;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.xsd.XSDSchema;

public class AddXSDSchemaDirectiveAction extends XSDBaseAction
{
  public static String INCLUDE_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDIncludeAction"; //$NON-NLS-1$
  public static String IMPORT_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDImportAction"; //$NON-NLS-1$
  public static String REDEFINE_ID = "org.eclipse.wst.xsd.ui.internal.common.actions.AddXSDRedefineAction"; //$NON-NLS-1$
  String label;
  
  public AddXSDSchemaDirectiveAction(IWorkbenchPart part, String ID, String label)
  {
    super(part);
    setText(label);
    setId(ID);
    this.label = label;
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDBaseAdapter)
    {
      selection = ((XSDBaseAdapter) selection).getTarget();
    }

    BaseCommand command = null;
    if (selection instanceof XSDSchema)
    {
      if (INCLUDE_ID.equals(getId()))
      {
        command = new AddXSDIncludeCommand(label, (XSDSchema) selection);
      }
      else if (IMPORT_ID.equals(getId()))
      {
        command = new AddXSDImportCommand(label, (XSDSchema) selection);
      }
      else if (REDEFINE_ID.equals(getId()))
      {
        command = new AddXSDRedefineCommand(label, (XSDSchema) selection);
      }
      getCommandStack().execute(command);
    }

    if (command != null)
    {
      Adapter adapter = XSDAdapterFactory.getInstance().adapt(command.getAddedComponent());
      if (adapter != null)
      {
    	  provider.setSelection(new StructuredSelection(adapter));
        // Automatically open the schema location dialog if the preference is enabled
        if(XSDEditorPlugin.getDefault().getAutomaticallyOpenSchemaLocationDialogSetting())
        {
          XSDSchemaDirectiveAdapter xsdSchemaDirectiveAdapter = null;
          if(adapter instanceof XSDSchemaDirectiveAdapter)
          {
            xsdSchemaDirectiveAdapter = (XSDSchemaDirectiveAdapter)adapter;
          }    	  
          XSDDirectivesSchemaLocationUpdater.updateSchemaLocation((XSDSchema) selection, xsdSchemaDirectiveAdapter.getTarget(), 
            		(command instanceof AddXSDIncludeCommand || command instanceof AddXSDRedefineCommand));
        }
        // The graphical view may deselect, so select again
        provider.setSelection(new StructuredSelection(adapter));
      }
    }
  }
}
