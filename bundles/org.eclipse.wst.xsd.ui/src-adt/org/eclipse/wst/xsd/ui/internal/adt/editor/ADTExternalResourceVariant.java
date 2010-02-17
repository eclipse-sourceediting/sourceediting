/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jesper Steen Moller - added resolver lookups
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;

public class ADTExternalResourceVariant extends PlatformObject
{
  private IStorage storage;
  private String urlString;

  public static String getCharset(String name, InputStream stream) throws IOException
  {
    IContentDescription description = getContentDescription(name, stream);
    return description == null ? null : description.getCharset();
  }

  public static IContentDescription getContentDescription(String name, InputStream stream) throws IOException
  {
    IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
    try
    {
      return contentTypeManager.getDescriptionFor(stream, name, IContentDescription.ALL);
    }
    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }
        catch (IOException e)
        {

        }
      }
    }
  }

  public ADTExternalResourceVariant(String urlString)
  {
    this.urlString = urlString;
  }

  class XSDResourceVariantStorage implements IEncodedStorage
  {

    public InputStream getContents() throws CoreException
    {
      try
      {
    	  String physicalUrlString = URIResolverPlugin.createResolver().resolvePhysicalLocation(null, null, urlString);
          URL url = new URL(physicalUrlString);
          URLConnection urlConnection = url.openConnection();
          return urlConnection.getInputStream();
      }
      catch (SocketTimeoutException toException)
      {
        // handle
      }
      catch (IOException e)
      {

      }
      return new ByteArrayInputStream(new byte[0]);
    }

    public IPath getFullPath()
    {
      // Since this is loaded from an URL, this should never be interpreted as a path
      return null;
    }

    public String getName()
    {
      return urlString;
    }

    public boolean isReadOnly()
    {
      return true;
    }

    public Object getAdapter(Class adapter)
    {
      return ADTExternalResourceVariant.this.getAdapter(adapter);
    }

    public String getCharset() throws CoreException
    {
      InputStream contents = getContents();
      try
      {
        String charSet = ADTExternalResourceVariant.getCharset(getName(), contents);
        return charSet;
      }
      catch (IOException e)
      {
        throw new CoreException(new Status(IStatus.ERROR, XSDEditorPlugin.PLUGIN_ID, IResourceStatus.FAILED_DESCRIBING_CONTENTS, NLS.bind("", new String[] { getFullPath().toString() }), e));
      }
      finally
      {
        try
        {
          contents.close();
        }
        catch (IOException ioException)
        {

        }
      }
    }
  }

  public IStorage getStorage()
  {
    if (storage == null)
    {
      storage = new XSDResourceVariantStorage();
    }
    return storage;
  }
}
