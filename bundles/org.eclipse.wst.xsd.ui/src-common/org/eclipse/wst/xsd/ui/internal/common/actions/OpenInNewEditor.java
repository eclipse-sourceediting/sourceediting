/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.actions;

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.ui.internal.adapters.XSDSchemaDirectiveAdapter;
import org.eclipse.wst.xsd.ui.internal.adt.actions.BaseSelectionAction;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ADTFileStoreEditorInput;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ADTReadOnlyFileEditorInput;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.InternalXSDMultiPageEditor;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.editor.XSDFileEditorInput;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDRedefine;
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
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = null;
    IEditorInput editorInput = null;
    if (workbenchWindow != null)
    {
      page = workbenchWindow.getActivePage();
      IEditorPart activeEditor = page.getActiveEditor();
      if (activeEditor != null)
      {
        editorInput = activeEditor.getEditorInput();
      }
      
    }
    
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

      String schemaLocation = null;
      IPath schemaPath = null;
      IFile schemaFile = null;

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
      } // Handle any other external components
      else if (fComponent.getSchema() != null && fComponent.eContainer() instanceof XSDSchema ||
          fComponent.eContainer() instanceof XSDRedefine || isReference)
      {
        schemaLocation = URIHelper.removePlatformResourceProtocol(fComponent.getSchema().getSchemaLocation());
        schemaPath = new Path(schemaLocation);
        schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
        
        try
        {
          XSDSchema xsdSchema = (XSDSchema)getWorkbenchPart().getAdapter(XSDSchema.class);
          if (fComponent.getSchema() == xsdSchema)
          {
            IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if (editorPart instanceof InternalXSDMultiPageEditor)
            {
              ((InternalXSDMultiPageEditor) editorPart).openOnGlobalReference(fComponent);
            }
            return;
          }
        }
        catch (Exception e)
        {
          
        }
      }
      
      // If the schemaFile exists in the workspace
      if (page != null && schemaFile != null && schemaFile.exists())
      {
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
            String editorName = null;
            XSDFileEditorInput xsdFileEditorInput = new XSDFileEditorInput(schemaFile, fComponent.getSchema());
            // will use FileEditorInput's name if still null
            // Try to use the same editor name as the current one
            if (editorInput != null)
            {
              editorName = editorInput.getName();
              xsdFileEditorInput.setEditorName(editorName);
            }
            editorPart = getExistingEditorForInlineSchema(page, schemaFile, schema);
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
      else
      {
        // open the xsd externally
        if (schemaLocation != null)
          openExternalFiles(page, schemaLocation, fComponent);
      }
    }
  }

  public static void openXSDEditor(IEditorInput editorInput, XSDConcreteComponent xsdComponent)
  {
    openXSDEditor(editorInput, xsdComponent.getSchema(), xsdComponent);
  }

  public static void openXSDEditor(IEditorInput editorInput, XSDSchema schema, XSDConcreteComponent xsdComponent)
  {
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = null;
    if (workbenchWindow != null)
    {
      page = workbenchWindow.getActivePage();
    }

    String resource = schema.getSchemaLocation();
    
    if (editorInput instanceof FileEditorInput && resource != null && !resource.startsWith("http"))
    {
      String schemaLocation = URIHelper.removePlatformResourceProtocol(resource);
      openWorkspaceFile(page, schemaLocation, xsdComponent);
    }
    else
    {
      openExternalFiles(page, resource, xsdComponent);
    }
  }
  
  public static void openInlineSchema(IEditorInput editorInput, XSDConcreteComponent xsdComponent, XSDSchema schema, String editorName)
  {
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    IWorkbenchPage page = null;
    if (workbenchWindow != null)
    {
      page = workbenchWindow.getActivePage();
    }

    boolean isWorkspaceFile = false;
    
    String schemaLocation = schema.getSchemaLocation();
    String workspaceFileLocation = URIHelper.removePlatformResourceProtocol(schemaLocation);
    IPath workspaceFilePath = new Path(workspaceFileLocation);
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(workspaceFilePath);
    
    if (page != null && file != null && file.exists())
    {
      isWorkspaceFile = true;  
    }

    if (isWorkspaceFile)
    {
      try
      {
        IEditorPart editorPart = null;
        XSDFileEditorInput xsdFileEditorInput = new XSDFileEditorInput(file, schema);
        xsdFileEditorInput.setEditorName(editorName);
        IEditorReference[] refs = page.getEditorReferences();
        int length = refs.length;
        for (int i = 0; i < length; i++)
        {
          IEditorInput input = refs[i].getEditorInput();
          if (input instanceof XSDFileEditorInput)
          {
            IFile aFile = ((XSDFileEditorInput) input).getFile();
            if (aFile.getFullPath().equals(file.getFullPath()))
            {
              if (((XSDFileEditorInput) input).getSchema() == schema)
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
          editorPart = page.openEditor(xsdFileEditorInput, XSDEditorPlugin.EDITOR_ID, true, 0); //$NON-NLS-1$
        }
        if (editorPart instanceof InternalXSDMultiPageEditor)
        {
          InternalXSDMultiPageEditor xsdEditor = (InternalXSDMultiPageEditor) editorPart;
          xsdEditor.openOnGlobalReference(xsdComponent);
        }
      }
      catch (PartInitException pie)
      {
      }
    }
    else
    {
      if (schemaLocation != null && !schemaLocation.startsWith("http"))
      {
        String fileLocation = null;
        // This is to workaround the difference in URI resolution. On linux, the resolved location is
        // platform:/resource/   On Windows, it's file://
        // 
       	if (java.io.File.separatorChar == '/')
      	{
          fileLocation = "/" + URIHelper.removePlatformResourceProtocol(schemaLocation);
      	}
    	  else // Windows
      	{
          fileLocation = URIHelper.removeProtocol(schemaLocation);
      	}
        IPath schemaPath = new Path(fileLocation);
        IFileStore fileStore = EFS.getLocalFileSystem().getStore(schemaPath);
        if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists())
        {
          try
          {
            ADTFileStoreEditorInput xsdFileStoreEditorInput = new ADTFileStoreEditorInput(fileStore, schema);
            xsdFileStoreEditorInput.setEditorName(editorName);

            IEditorPart editorPart = null;
            IEditorReference[] refs = page.getEditorReferences();
            int length = refs.length;
            for (int i = 0; i < length; i++)
            {
              IEditorInput input = refs[i].getEditorInput();
              if (input instanceof ADTFileStoreEditorInput)
              {
                URI uri = ((ADTFileStoreEditorInput) input).getURI();
                if (uri.equals(xsdFileStoreEditorInput.getURI()) && ((ADTFileStoreEditorInput) input).getSchema() == xsdFileStoreEditorInput.getSchema())
                {
                  editorPart = refs[i].getEditor(true);
                  page.activate(refs[i].getPart(true));
                  break;
                }
              }
            }

            if (page != null && editorPart == null)
            {
              editorPart = page.openEditor(xsdFileStoreEditorInput, XSDEditorPlugin.EDITOR_ID, true, 0); //$NON-NLS-1$
            }
            if (editorPart instanceof InternalXSDMultiPageEditor)
            {
              InternalXSDMultiPageEditor xsdEditor = (InternalXSDMultiPageEditor) editorPart;
              xsdEditor.openOnGlobalReference(xsdComponent);
            }
          }
          catch (PartInitException pie)
          {

          }
        }
      }
      else
      {
        try
        {
          IEditorPart editorPart = null;
          IEditorReference[] refs = page.getEditorReferences();
          int length = refs.length;
          // Need to find if an editor on that schema has already been opened
          for (int i = 0; i < length; i++)
          {
            IEditorInput input = refs[i].getEditorInput();
            if (input instanceof ADTReadOnlyFileEditorInput)
            {
              ADTReadOnlyFileEditorInput xsdFileStorageEditorInput = (ADTReadOnlyFileEditorInput) input;
              if (xsdFileStorageEditorInput.getUrlString().equals(schemaLocation)
                  && xsdFileStorageEditorInput.getEditorID().equals(XSDEditorPlugin.EDITOR_ID))
              {
                editorPart = refs[i].getEditor(true);
                page.activate(refs[i].getPart(true));
                break;
              }
            }
          }
          if (editorPart == null)
          {
            ADTReadOnlyFileEditorInput xsdFileStorageEditorInput = new ADTReadOnlyFileEditorInput(schemaLocation);
            xsdFileStorageEditorInput.setSchema(schema);
            xsdFileStorageEditorInput.setEditorName(editorName);
            xsdFileStorageEditorInput.setEditorID(XSDEditorPlugin.EDITOR_ID);
            editorPart = page.openEditor(xsdFileStorageEditorInput, XSDEditorPlugin.EDITOR_ID, true, 0);
          }
          if (editorPart instanceof InternalXSDMultiPageEditor)
          {
            InternalXSDMultiPageEditor xsdEditor = (InternalXSDMultiPageEditor) editorPart;
            xsdEditor.openOnGlobalReference(xsdComponent);
          }
        }
        catch (PartInitException pie)
        {
        }
        
      }
      return;
    }
  }
  
  private IEditorPart getExistingEditorForInlineSchema(IWorkbenchPage page, IFile schemaFile, XSDSchema schema)
  {
    IEditorReference [] refs = page.getEditorReferences();
    int length = refs.length;
    IEditorPart editorPart = null;
    try
    {
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
    }
    catch (PartInitException e)
    {
    }
    return editorPart;
  }

  public static void openWorkspaceFile(IWorkbenchPage page, String schemaLocation, XSDConcreteComponent xsdComponent)
  {
    IPath schemaPath = new Path(schemaLocation);
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
    if (page != null && file != null && file.exists())
    {
      try
      {
        IEditorPart editorPart = IDE.openEditor(page, file, true);
        if (editorPart instanceof InternalXSDMultiPageEditor)
        {
          InternalXSDMultiPageEditor xsdEditor = (InternalXSDMultiPageEditor) editorPart;
          xsdEditor.openOnGlobalReference(xsdComponent);
        }
      }
      catch (PartInitException pie)
      {

      }
    }
  }

  public static void openExternalFiles(IWorkbenchPage page, String schemaLocation, XSDConcreteComponent fComponent)
  {
    if (schemaLocation == null) return;  // Assert not null
  
    IPath schemaPath = new Path(schemaLocation);
//  Initially tried to use schemaPath.getDevice() to determine if it is an http reference.  However, on Linux, it is null.
//  So as a result of bug 221421, we will just use the schemaLocation.
    if (!schemaLocation.startsWith("http"))
    {
      schemaPath = new Path(URIHelper.removeProtocol(schemaLocation));
    }
    IFileStore fileStore = EFS.getLocalFileSystem().getStore(schemaPath);
    URI schemaURI = URI.create(schemaLocation);
    if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists())
    {
      try
      {
        IEditorPart editorPart = null;
        IEditorReference[] refs = page.getEditorReferences();
        int length = refs.length;
        // Need to find if an editor on that schema has already been opened
        for (int i = 0; i < length; i++)
        {
          IEditorInput input = refs[i].getEditorInput();
          if (input instanceof FileStoreEditorInput)
          {
            URI uri = ((FileStoreEditorInput) input).getURI();
            if (uri.equals(schemaURI))
            {
              editorPart = refs[i].getEditor(true);
              page.activate(refs[i].getPart(true));
              break;
            }
          }
        }
        if (editorPart == null)
        {
          editorPart = IDE.openEditorOnFileStore(page, fileStore);
        }
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
    else
    {
      try
      {
        if (schemaLocation.startsWith("http"))
        {
          try
          {
            IEditorPart editorPart = null;
            IEditorReference[] refs = page.getEditorReferences();
            int length = refs.length;
            // Need to find if an editor on that schema has already been opened
            for (int i = 0; i < length; i++)
            {
              IEditorInput input = refs[i].getEditorInput();
              if (input instanceof ADTReadOnlyFileEditorInput)
              {
                ADTReadOnlyFileEditorInput readOnlyEditorInput = (ADTReadOnlyFileEditorInput) input;
                if (readOnlyEditorInput.getUrlString().equals(schemaLocation) &&
                    XSDEditorPlugin.EDITOR_ID.equals(readOnlyEditorInput.getEditorID()))
                {
                  editorPart = refs[i].getEditor(true);
                  page.activate(refs[i].getPart(true));
                  break;
                }
              }
            }
            if (editorPart == null)
            {
              ADTReadOnlyFileEditorInput readOnlyStorageEditorInput = new ADTReadOnlyFileEditorInput(schemaLocation);
              readOnlyStorageEditorInput.setEditorID(XSDEditorPlugin.EDITOR_ID);
              editorPart = page.openEditor(readOnlyStorageEditorInput, XSDEditorPlugin.EDITOR_ID, true, 0); //$NON-NLS-1$
            }
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
        else
        {
          FileStoreEditorInput xsdFileStoreEditorInput = new FileStoreEditorInput(fileStore);        
          page.openEditor(xsdFileStoreEditorInput, XSDEditorPlugin.EDITOR_ID, true, 0); //$NON-NLS-1$
        }

      }
      catch (PartInitException e)
      {
        
      }
    }
  }
}
