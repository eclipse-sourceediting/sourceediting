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
package org.eclipse.wst.xsd.ui.internal;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.wst.xml.uriresolver.internal.util.IdResolver;
import org.eclipse.wst.xml.uriresolver.internal.util.IdResolverImpl;

public class XSDURIConverter extends URIConverterImpl
{
  IFile resourceFile;
  public XSDURIConverter(IFile resourceFile)
  {
    super();
    this.resourceFile = resourceFile;
  }
  
  /**
   * @see org.eclipse.emf.ecore.resource.URIConverter#createInputStream(URI)
   */
  public InputStream createInputStream(URI uri) throws IOException
  {
    String scheme = uri.scheme();
    URI mappedURI = uri;
    if (scheme != null && !scheme.equals("file") && !scheme.equals("platform"))
    // if ("http".equals(scheme))
    {
      String theURI = uri.toString();
      IdResolver idResolver = new IdResolverImpl(theURI);
      String result = idResolver.resolveId("/", null, theURI);
      if (result != null)
      {
        mappedURI = createURI(result);
      }  
    }  
    return super.createURLInputStream(mappedURI);
  }
  
  public static URI createURI(String uriString)
  {
    if (hasProtocol(uriString))
      return URI.createURI(uriString);
    else
      return URI.createFileURI(uriString);
  }
  
  private static boolean hasProtocol(String uri)
  {
    boolean result = false;     
    if (uri != null)
    {
      int index = uri.indexOf(":");
      if (index != -1 && index > 2) // assume protocol with be length 3 so that the'C' in 'C:/' is not interpreted as a protocol
      {
        result = true;
      }
    }
    return result;
  }

  private String getRelativePathToSchema(String a, String b)
  {
    String result;
    if (b.startsWith(a))
    {
      result = b.substring(a.length() + 1);
      return result;
    }
    else
    {
      return b;
    }
  }
}
