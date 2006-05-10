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
package org.eclipse.wst.xsd.ui.internal.text;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.util.XSDSchemaLocationResolverAdapterFactory;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XSDModelAdapter implements INodeAdapter
{
  protected ResourceSet resourceSet;
  protected XSDSchema schema;

  public XSDSchema getSchema()
  {
    return schema;
  }

  public void setSchema(XSDSchema schema)
  {
    this.schema = schema;
  }
  
  public void clear()
  {
    schema = null;
    resourceSet = null;
  }

  public boolean isAdapterForType(Object type)
  {
    return type == XSDModelAdapter.class;
  }

  public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos)
  {
  }

  public XSDSchema createSchema(Document document)
  {    
    try
    {
      // (cs) note that we always want to ensure that a 
      // schema model object get's returned
      schema = XSDFactory.eINSTANCE.createXSDSchema();
      resourceSet = XSDSchemaImpl.createResourceSet();
      resourceSet.getAdapterFactories().add(new XSDSchemaLocationResolverAdapterFactory());                

      IDOMNode domNode = (IDOMNode)document;
      String baseLocation = domNode.getModel().getBaseLocation();           

      // TODO... gotta pester SSE folks to provide 'useful' baseLocations
      // 
      URI uri = null;
      if (baseLocation.startsWith("/"))
      {
        uri = URI.createPlatformResourceURI(baseLocation);
      }
      else
      {
        uri = URI.createFileURI(baseLocation);
      }  
      Resource resource = new XSDResourceImpl();
      resource.setURI(uri);
      schema = XSDFactory.eINSTANCE.createXSDSchema(); 
      resource.getContents().add(schema);
      resourceSet.getResources().add(resource);     

      Element element = document.getDocumentElement();
      if (element != null)
      {  
        // Force the loading of the "meta" schema for schema instance instance.
        //
        String schemaForSchemaNamespace = element.getNamespaceURI();
        XSDSchemaImpl.getSchemaForSchema(schemaForSchemaNamespace);            
        schema.setElement(element);
      }    

      // attach an adapter to keep the XSD model and DOM in sync
      //
      new XSDModelReconcileAdapter(document, schema);       
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return schema;    
  }
  
  /**
   * @deprecated
   */
  public XSDSchema createSchema(Element element)
  {     
    return createSchema(element.getOwnerDocument());
  }
  
  public static XSDSchema lookupOrCreateSchema(Document document)
  {    
    XSDSchema result = null;
    if (document instanceof INodeNotifier)
    {
      INodeNotifier notifier = (INodeNotifier)document;
      XSDModelAdapter adapter = (XSDModelAdapter)notifier.getAdapterFor(XSDModelAdapter.class);
      if (adapter == null)
      {
        adapter = new XSDModelAdapter();       
        notifier.addAdapter(adapter);        
        adapter.createSchema(document); 
      } 
      result = adapter.getSchema();
    }    
    return result;    
  }  
}