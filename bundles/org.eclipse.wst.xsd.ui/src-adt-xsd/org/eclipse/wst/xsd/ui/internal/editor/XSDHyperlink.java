/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xsd.ui.internal.adt.editor.Messages;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;

/**
 * XSDHyperlink knows how to open links from XSD files.
 * 
 * @see XSDHyperlinkDetector
 */
public class XSDHyperlink implements IHyperlink
{
  private IRegion fRegion;
  private XSDConcreteComponent fComponent;

  public XSDHyperlink(IRegion region, XSDConcreteComponent component)
  {
    fRegion = region;
    fComponent = component;
  }

  public IRegion getHyperlinkRegion()
  {
    return fRegion;
  }

  public String getTypeLabel()
  {
    return null;
  }

  public String getHyperlinkText()
  {
    return Messages._UI_ACTION_SET_AS_FOCUS;
  }

  public void open()
  {
    XSDSchema schema = fComponent.getSchema();

    if (schema == null)
    {
      return;
    }

    String schemaLocation = schema.getSchemaLocation();
    schemaLocation = URIHelper.removePlatformResourceProtocol(schemaLocation);
    IPath schemaPath = new Path(schemaLocation);
    IFile schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);

    boolean fileExists = schemaFile != null && schemaFile.exists();

    if (!fileExists)
    {
      return;
    }
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (workbenchWindow != null)
    {
      IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
      IEditorPart editorPart = workbenchPage.getActiveEditor();
      
      workbenchPage.getNavigationHistory().markLocation(editorPart);
      
      try
      {
        editorPart = IDE.openEditor(workbenchPage, schemaFile, true);
        if (editorPart instanceof InternalXSDMultiPageEditor)
        {
          ((InternalXSDMultiPageEditor) editorPart).openOnGlobalReference(fComponent);
        }
      }
      catch (PartInitException pie)
      {
        Logger.log(Logger.WARNING_DEBUG, pie.getMessage(), pie);
      }
    }
  }
}
