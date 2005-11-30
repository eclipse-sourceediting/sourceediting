/*******************************************************************************
* Copyright (c) 2004, 2005 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     IBM Corporation - Initial API and implementation
*******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.commands;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.window.Window;
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
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class SetTypeCommand extends AbstractCommand
{
  protected int typeKind = XSDConstants.COMPLEXTYPE_ELEMENT;
  
  public SetTypeCommand(XSDConcreteComponent parent)
  {
    super(parent);
  }
  
  public void setTypeKind(int type)
  {
    this.typeKind = type;
  }

  public void run()
  {
    XSDConcreteComponent parent = getParent();
    if (parent instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration element = (XSDElementDeclaration)parent;
      XSDSchema schema = element.getSchema();
      if (typeKind == XSDConstants.COMPLEXTYPE_ELEMENT)
      {
        AddModelGroupCommand sequenceCommand = new AddModelGroupCommand(element, XSDCompositor.SEQUENCE_LITERAL);
        sequenceCommand.run();
      }
      else if (typeKind == XSDConstants.SIMPLETYPE_ELEMENT)
      {
        AddSimpleTypeDefinitionCommand stCommand = new AddSimpleTypeDefinitionCommand(element, null);
        stCommand.run();
        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)stCommand.getModelObject();
        XSDSimpleTypeDefinition base = schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string");
        st.setBaseTypeDefinition(base);
      }
      else
      {
          Shell shell = Display.getCurrent().getActiveShell();
          IFile currentIFile = ((IFileEditorInput)getActiveEditor().getEditorInput()).getFile();
          
          XSDComponentSelectionProvider provider = new XSDComponentSelectionProvider(currentIFile, schema);
          XSDComponentSelectionDialog dialog = new XSDComponentSelectionDialog(shell, XSDEditorPlugin.getXSDString("_UI_LABEL_SET_TYPE"), provider);
          provider.setDialog(dialog);
          
          dialog.setBlockOnOpen(true);
          dialog.create();
          
          if (dialog.open() == Window.OK) {
              XSDSetTypeHelper helper = new XSDSetTypeHelper(currentIFile, schema);
              helper.setType(element.getElement(), "type", dialog.getSelection());
          }
      }
      formatChild(element.getElement());
    }

  }
  
  private IEditorPart getActiveEditor()
  {
    IWorkbench workbench = XSDEditorPlugin.getPlugin().getWorkbench();
    IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
    IEditorPart editorPart = workbenchWindow.getActivePage().getActiveEditor();
//    IEditorPart editorPart = part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();

    return editorPart;
  }

  protected boolean adopt(XSDConcreteComponent model)
  {
    return false;
  }

}
