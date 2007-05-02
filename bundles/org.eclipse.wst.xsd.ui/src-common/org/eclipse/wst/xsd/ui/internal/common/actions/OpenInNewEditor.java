/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaDirectiveAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDTypeDefinitionAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.InternalXSDMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.utils.OpenOnSelectionHelper;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;

public class OpenInNewEditor extends BaseSelectionAction
{
  public static final String ID = "OpenInNewEditor"; //$NON-NLS-1$

  public OpenInNewEditor(IWorkbenchPart part)
  {
    super(part);
    setText(Messages._UI_ACTION_OPEN_IN_NEW_EDITOR); //$NON-NLS-1$
    setId(ID);
  }

  protected boolean calculateEnabled()
  {
    return true;
  }

  public void run()
  {
    Object selection = ((IStructuredSelection) getSelection()).getFirstElement();

    if (selection instanceof XSDTypeDefinitionAdapter)
    {
      XSDTypeDefinitionAdapter xsdAdapter = (XSDTypeDefinitionAdapter) selection;
      XSDTypeDefinition fComponent = (XSDTypeDefinition) xsdAdapter.getTarget();

      if (fComponent.getSchema() != null)
      {
        String schemaLocation = URIHelper.removePlatformResourceProtocol(fComponent.getSchema().getSchemaLocation());
        IPath schemaPath = new Path(schemaLocation);
        IFile schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
        if (schemaFile != null && schemaFile.exists())
        {
          IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          if (workbenchWindow != null)
          {
            IWorkbenchPage page = workbenchWindow.getActivePage();
            try
            {
              // TODO: Should use this to open in default editor
              // IEditorPart editorPart = IDE.openEditor(page, schemaFile, true);
              IEditorPart editorPart = page.openEditor(new FileEditorInput(schemaFile), "org.eclipse.wst.xsd.ui.internal.editor.InternalXSDMultiPageEditor", true); //$NON-NLS-1$

              if (editorPart instanceof InternalXSDMultiPageEditor)
              {
                InternalXSDMultiPageEditor xsdEditor = (InternalXSDMultiPageEditor) editorPart;

                xsdEditor.openOnGlobalReference(fComponent);
              }

            }
            catch (PartInitException pie)
            {
            }
          }
        }
      }
    }
    else if (selection instanceof XSDSchemaDirectiveAdapter)
    {
      XSDSchemaDirective dir = (XSDSchemaDirective)((XSDSchemaDirectiveAdapter)selection).getTarget();
      String schemaLocation = "";
      // force load of imported schema
      if (dir instanceof XSDImportImpl)
      {
        ((XSDImportImpl)dir).importSchema();
      }
      if (dir.getResolvedSchema() != null)
      {
        schemaLocation = URIHelper.removePlatformResourceProtocol(dir.getResolvedSchema().getSchemaLocation());
        if (schemaLocation != null)
        {
          OpenOnSelectionHelper.openXSDEditor(schemaLocation);
        }
      }
    }
  }
}
