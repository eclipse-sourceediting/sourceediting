/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Jesper Steen Moller - added support for lookups out of workspace
 *******************************************************************************/

package org.eclipse.wst.xsd.ui.internal.editor;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xsd.ui.internal.adt.editor.ADTReadOnlyFileEditorInput;
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
    return NLS.bind(Messages._UI_HYPERLINK_TEXT, fComponent.getElement().getLocalName());
  }

  public void open()
  {
    XSDSchema schema = fComponent.getSchema();

    if (schema == null)
    {
      return;
    }

    String schemaLocation = schema.getSchemaLocation();

	IFile schemaFile = null;
    URI schemaUri = null;
    
	if (URIHelper.isPlatformResourceProtocol(schemaLocation))
	{
      IPath schemaPath = new Path(URIHelper.removePlatformResourceProtocol(schemaLocation));
      schemaFile = ResourcesPlugin.getWorkspace().getRoot().getFile(schemaPath);
      if (! schemaFile.exists())
      {
    	return;
      }
    }
	else
	{
		try {
			schemaUri = new URI(schemaLocation);
		} catch (Exception exception) {}
	}
	
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (workbenchWindow != null)
    {
      IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
      IEditorPart editorPart = workbenchPage.getActiveEditor();
      
      workbenchPage.getNavigationHistory().markLocation(editorPart);
      
      try
      {
      	if (schemaFile != null)
    	{
    		editorPart = IDE.openEditor(workbenchPage, schemaFile, true);
    	}
    	else if (schemaUri != null && "file".equals(schemaUri.getScheme())) //$NON-NLS-1$
    	{
    		editorPart = IDE.openEditor(workbenchPage, schemaUri, XSDEditorPlugin.EDITOR_ID, true);	
    	}
    	else
    	{
    	    IEditorInput input = new ADTReadOnlyFileEditorInput(schemaLocation);
    	    editorPart = IDE.openEditor(workbenchPage, input, XSDEditorPlugin.EDITOR_ID, true);
		}
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
