/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jesper Steen Moller - broader recognition of URIs
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.text;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.util.ModelReconcileAdapter;
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
  private ModelReconcileAdapter modelReconcileAdapter;

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
	if (schema != null) {
		Document doc = schema.getDocument();
		if (doc instanceof IDOMDocument) {
			IDOMDocument domDocument = (IDOMDocument)doc;
			domDocument.getModel().removeModelStateListener(getModelReconcileAdapter());
			domDocument.removeAdapter(getModelReconcileAdapter());
			domDocument.removeAdapter(this);		
		}
	    schema = null;
	}
    resourceSet = null;
  }

  public boolean isAdapterForType(Object type)
  {
    return type == XSDModelAdapter.class;
  }

  public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos)
  {
  }
  
  public ModelReconcileAdapter getModelReconcileAdapter()
  {
    return modelReconcileAdapter;
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
      URI uri = getURI(baseLocation);

      Resource resource = new XSDResourceImpl();
      resource.setURI(uri);
      schema = XSDFactory.eINSTANCE.createXSDSchema(); 
      resource.getContents().add(schema);
      resourceSet.getResources().add(resource);     

      schema.setDocument(document);
      final Element element = document.getDocumentElement();
      if (element != null)
      {  
        // Force the loading of the "meta" schema for schema instance instance.
        //
        String schemaForSchemaNamespace = element.getNamespaceURI();
        XSDSchemaImpl.getSchemaForSchema(schemaForSchemaNamespace);            
      }
        
      IRunnableWithProgress setElementOperation = new IRunnableWithProgress()
      {
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
          // Use the animated flavour as we don't know beforehand how many ticks we need.
          // The task name will be displayed by the code in XSDResourceImpl.
          
          monitor.beginTask("", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
          Map loadOptions = resourceSet.getLoadOptions();
          loadOptions.put(XSDResourceImpl.XSD_PROGRESS_MONITOR, monitor);
          
          schema.setElement(element);
          
          loadOptions.remove(XSDResourceImpl.XSD_PROGRESS_MONITOR);
        }
      };

      IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
      try
      {
        progressService.busyCursorWhile(setElementOperation);
      }
      catch (InvocationTargetException e)
      {
        e.printStackTrace();
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }       
        
      // attach an adapter to keep the XSD model and DOM in sync
      //
      modelReconcileAdapter = new XSDModelReconcileAdapter(document, schema);
      domNode.getModel().addModelStateListener(modelReconcileAdapter);
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

  public XSDSchema resetSchema(Document document)
  {
    // The document has changed so the schema should be updated as well.
    try
    {
      IDOMNode domNode = (IDOMNode)document;
      schema.setDocument(document);
      schema.setElement(document.getDocumentElement());

      resourceSet = schema.eResource().getResourceSet();
      
      String baseLocation = domNode.getModel().getBaseLocation();           
      URI uri = getURI(baseLocation);
      schema.eResource().setURI(uri);

      modelReconcileAdapter = new XSDModelReconcileAdapter(document, schema);
      domNode.getModel().addModelStateListener(modelReconcileAdapter);
    }
    catch (Exception ex)
    {
    }
    return schema;
  }  

  public static XSDModelAdapter lookupOrCreateModelAdapter(Document document)
  {
    XSDModelAdapter adapter = null;
    if (document instanceof INodeNotifier)
    {
      INodeNotifier notifier = (INodeNotifier)document;
      adapter = (XSDModelAdapter)notifier.getAdapterFor(XSDModelAdapter.class);
      if (adapter == null)
      {
        adapter = new XSDModelAdapter();
        notifier.addAdapter(adapter);
      } 
    }   
    return adapter;
  }
  
  public static XSDSchema lookupOrCreateSchema(final Document document)
  {    
    XSDSchema result = null;    
    XSDModelAdapter adapter = lookupOrCreateModelAdapter(document);      
    if (adapter.getSchema() == null)
    {  
      
      adapter.createSchema(document); 
    }   
    result = adapter.getSchema();    
    return result;    
  }
  
  private URI getURI(String baseLocation)
  {
    URI uri = null;
    if (baseLocation.startsWith("/")) //$NON-NLS-1$
    {
      uri = URI.createPlatformResourceURI(baseLocation);
    }
    else if (baseLocation.indexOf(':') > 1)
    {
      // Looks like an URL
      uri = URI.createURI(baseLocation);
    }
    else
    {
      // Supposedly a file, then
      uri = URI.createFileURI(baseLocation);
    }
    return uri;
  }
}


