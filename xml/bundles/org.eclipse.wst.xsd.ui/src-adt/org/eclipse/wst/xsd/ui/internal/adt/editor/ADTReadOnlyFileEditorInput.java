/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.wst.common.uriresolver.internal.URI;
import org.eclipse.xsd.XSDSchema;

/**
 * 
 * Used for any read-only files, ie. HTTP files.
 *
 */
public class ADTReadOnlyFileEditorInput implements IStorageEditorInput, IADTEditorInput
{
  protected String urlString;
  protected IStorage storage;
  protected ADTExternalResourceVariant variant;
  protected XSDSchema xsdSchema;
  protected String editorName;
  protected String editorID = "";

  public ADTReadOnlyFileEditorInput(String urlString)
  {
    this.urlString = urlString;
    variant = new ADTExternalResourceVariant(urlString);
  }

  /*
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object o)
  {
    if (o == this)
      return true;

    if (o instanceof ADTReadOnlyFileEditorInput)
    {
      ADTReadOnlyFileEditorInput input = (ADTReadOnlyFileEditorInput) o;
      
      return urlString.equals(input.getUrlString()) && input.getEditorID().equals(editorID);
    }

    return false;
  }

  public String getUrlString()
  {
    return urlString;
  }
  
  public IStorage getStorage() throws CoreException
  {
    storage = variant.getStorage();
    return storage;
  }

  public boolean exists()
  {
    return false;
  }

  public ImageDescriptor getImageDescriptor()
  {
    return null;
  }

  public String getName()
  {
    if (editorName == null)
    {
      URI uri = URI.createURI(urlString);
      return uri.lastSegment();
    }
    return editorName;
  }

  public IPersistableElement getPersistable()
  {
    // http files cannot persist
    return null;
  }

  public String getToolTipText()
  {
    return urlString;
  }

  public Object getAdapter(Class adapter)
  {
    if (adapter == IWorkbenchAdapter.class)
      return this;
    if (adapter == IStorage.class)
      return storage;
    return null;
  }
  
  public void setSchema(XSDSchema xsdSchema)
  {
    this.xsdSchema = xsdSchema;
  }

  public XSDSchema getSchema()
  {
    return xsdSchema;
  }

  public void setEditorName(String name)
  {
    editorName = name;
  }

  public void setEditorID(String editorID)
  {
    this.editorID = editorID;
  }
  
  public String getEditorID()
  {
    return editorID;
  }
}
