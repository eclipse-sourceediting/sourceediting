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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaDirectiveAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.InternalXSDMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.editor.XSDFileEditorInput;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
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

    if (selection instanceof XSDBaseAdapter)
    {
      XSDBaseAdapter xsdAdapter = (XSDBaseAdapter) selection;
      XSDConcreteComponent fComponent = (XSDConcreteComponent) xsdAdapter.getTarget();
      XSDSchema schema = fComponent.getSchema();
      
      boolean isReference = false;
      if (fComponent instanceof XSDFeature)
      {
        isReference = ((XSDFeature)fComponent).isFeatureReference();
        fComponent = ((XSDFeature)fComponent).getResolvedFeature();
      }

      if (fComponent.getSchema() != null && fComponent.eContainer() instanceof XSDSchema || isReference)
      {
        String schemaLocation = URIHelper.removePlatformResourceProtocol(fComponent.getSchema().getSchemaLocation());
        IPath schemaPath = new Path(schemaLocation);
        IFile schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);

        // Special case any imports/includes
        if (selection instanceof XSDSchemaDirectiveAdapter)
        {
          XSDSchemaDirective dir = (XSDSchemaDirective)((XSDSchemaDirectiveAdapter)selection).getTarget();
          // force load of imported schema
          if (dir instanceof XSDImportImpl)
          {
            ((XSDImportImpl)dir).importSchema();
          }
          if (dir.getResolvedSchema() != null)
          {
            schemaLocation = URIHelper.removePlatformResourceProtocol(dir.getResolvedSchema().getSchemaLocation());
            schemaPath = new Path(schemaLocation);
            schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
            schema = dir.getResolvedSchema(); 
            fComponent = dir.getResolvedSchema();
          }
        }

        if (schemaFile != null && schemaFile.exists())
        {
          IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          if (workbenchWindow != null)
          {
            IWorkbenchPage page = workbenchWindow.getActivePage();
            try
            {
              // Get the current editor's schema
              XSDSchema xsdSchema = (XSDSchema)getWorkbenchPart().getAdapter(XSDSchema.class);
              
              IEditorPart editorPart = null;
              // This first check is to ensure that the schema is actually
              // different than the current one we are editing against in the editor, and that we
              // are in the same resource file....hence multiple schemas in the same file.
              if (xsdSchema != null && fComponent.getRootContainer().eResource() == xsdSchema.eResource() && xsdSchema != schema)
              {
                XSDFileEditorInput xsdFileEditorInput = new XSDFileEditorInput(schemaFile, fComponent.getSchema());
                IEditorPart activeEditor = page.getActiveEditor();
                String editorName = null; 
                
                // will use FileEditorInput's name if still null
                // Try to use the same editor name as the current one
                if (activeEditor != null)
                {
                  IEditorInput input = activeEditor.getEditorInput();
                  if (input != null)
                  {
                    editorName = input.getName();
                    xsdFileEditorInput.setEditorName(editorName);
                  }
                }
                
                IEditorReference [] refs = page.getEditorReferences();
                int length = refs.length;
                for (int i = 0; i < length; i++)
                {
                  IEditorInput input = refs[i].getEditorInput();
                  if (input instanceof XSDFileEditorInput)
                  {
                    IFile aFile = ((XSDFileEditorInput)input).getFile();
                    if (aFile.getFullPath().equals(schemaFile.getFullPath()))
                    {
                      if (((XSDFileEditorInput)input).getSchema() == schema)
                      {
                        editorPart = refs[i].getEditor(true);
                        page.activate(refs[i].getPart(true));
                        break;
                      }
                    }
                  }
                }

                if (editorPart == null)
                {
                  editorPart = page.openEditor(xsdFileEditorInput, XSDEditorPlugin.EDITOR_ID, true, 0);
                }
              }
              else
              {
                editorPart = page.openEditor(new FileEditorInput(schemaFile), XSDEditorPlugin.EDITOR_ID);
              }

              if (editorPart instanceof InternalXSDMultiPageEditor)
              {
                ((InternalXSDMultiPageEditor) editorPart).openOnGlobalReference(fComponent);
              }
            }
            catch (Exception e)
            {
            }
          }
        }
      }
    }
  }
}
