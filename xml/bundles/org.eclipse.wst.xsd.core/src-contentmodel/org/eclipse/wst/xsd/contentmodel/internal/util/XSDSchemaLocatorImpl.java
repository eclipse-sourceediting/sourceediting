/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.contentmodel.internal.util;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.eclipse.xsd.util.XSDSchemaLocator;

public class XSDSchemaLocatorImpl extends AdapterImpl implements XSDSchemaLocator
{
    /**
     * @see org.eclipse.xsd.util.XSDSchemaLocator#locateSchema(org.eclipse.xsd.XSDSchema,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    public XSDSchema locateSchema(XSDSchema xsdSchema, String namespaceURI, String rawSchemaLocationURI, String resolvedSchemaLocationURI)
    {
      XSDSchema result = null;
      String baseLocation = xsdSchema.getSchemaLocation();      
      String resolvedURI = URIResolverPlugin.createResolver().resolve(baseLocation, namespaceURI, rawSchemaLocationURI); 
      if (resolvedURI == null) 
      {
        resolvedURI = resolvedSchemaLocationURI;       
      }
      try
      {        
        ResourceSet resourceSet = xsdSchema.eResource().getResourceSet();
        URI uri = URI.createURI(resolvedURI);
        Resource r = resourceSet.getResource(uri, false); 
        XSDResourceImpl resolvedResource = null;
        if (r instanceof XSDResourceImpl)
        {
          resolvedResource = (XSDResourceImpl)r;
        }
        else        
        {  
          String physicalLocation = URIResolverPlugin.createResolver().resolvePhysicalLocation(baseLocation, namespaceURI, resolvedURI);     
          InputStream inputStream = resourceSet.getURIConverter().createInputStream(URI.createURI(physicalLocation));
          resolvedResource = (XSDResourceImpl)resourceSet.createResource(URI.createURI("*.xsd"));
          resolvedResource.setURI(uri);
          resolvedResource.load(inputStream, null);           
        }

        result = resolvedResource.getSchema();
      }
      catch (IOException exception)
      {
        // It is generally not an error to fail to resolve.
        // If a resource is actually created, 
        // which happens only when we can create an input stream,
        // then it's an error if it's not a good schema
      }
      return result;
    }

    public boolean isAdatperForType(Object type)
    {
      return type == XSDSchemaLocator.class;
    }  
}
