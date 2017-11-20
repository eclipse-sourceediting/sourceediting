/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelqueryimpl;
                          
import java.util.Hashtable;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.modelqueryimpl.InferredGrammarFactory;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 */
public class InferredGrammarBuildingCMDocumentLoader extends CMDocumentLoader
{     
  protected CMElementDeclaration inferredCMElementDeclaration;
  protected CMDocument inferredCMDocument;
  protected InferredGrammarFactory inferredGrammarFactory;
  protected Hashtable createdCMDocumentTable;

  public InferredGrammarBuildingCMDocumentLoader(Document document, ModelQuery modelQuery)
  {                             
    this(document, modelQuery.getCMDocumentManager());
  }
  
  public InferredGrammarBuildingCMDocumentLoader(Document document, CMDocumentManager cmDocumentManager)
  {                             
    super(document, cmDocumentManager);
    createdCMDocumentTable = new Hashtable();             
    inferredGrammarFactory = new InferredGrammarFactory();
  }     

     
  public void loadCMDocuments()
  {   
    //System.out.println("----------loadCMDocuments ------------");          
    if (inferredGrammarFactory != null)
    {
      //long time = System.currentTimeMillis();
      super.loadCMDocuments();
      //System.out.println("--- elapsed time (" + count + ") = " + (System.currentTimeMillis() - time));
      //inferredGrammarFactory.debugPrint(createdCMDocumentTable.values());
    }
    
  }

  public void handleElement(Element element)
  { 
    CMElementDeclaration parentInferredCMElementDeclaration = inferredCMElementDeclaration;
                
    if (inferredCMDocument == null)
    {     
      String cacheKey = "inferred-document";                    //$NON-NLS-1$
      inferredCMDocument = inferredGrammarFactory.createCMDocument("");   //$NON-NLS-1$
      cmDocumentManager.addCMDocument("", "", cacheKey, "DTD", inferredCMDocument); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      createdCMDocumentTable.put(cacheKey, inferredCMDocument);
    } 

    inferredCMElementDeclaration = inferredGrammarFactory.createCMElementDeclaration(inferredCMDocument, element, false);                             
     
    if (parentInferredCMElementDeclaration != null)
    {
      inferredGrammarFactory.createCMContent(inferredCMDocument, parentInferredCMElementDeclaration, inferredCMDocument, inferredCMElementDeclaration, false, null);
    }     
       

    visitChildNodes(element);   
           
    // reset the 'current' state to inital values
    inferredCMElementDeclaration = parentInferredCMElementDeclaration;
  }
  

  public void handleElementNS(Element element)
  {                             
    CMDocument parentInferredCMDocument = inferredCMDocument;
    CMElementDeclaration parentInferredCMElementDeclaration = inferredCMElementDeclaration;
        
    inferredCMDocument = null;                                                            
    inferredCMElementDeclaration = null;

    // by adding the element to the namespaceTable, handleGrammar() will get called for any schema references
    if (element.getParentNode() != document)
    {
      namespaceTable.addElement(element);
    }

    String prefix = element.getPrefix();
    String uri = namespaceTable.getURIForPrefix(prefix);

    if (uri == null && element.getParentNode() == document)
    {
      // when this is the root element
      // we need to add an implied "no namespace schema location"
      uri = "ommitted-namespace"; //$NON-NLS-1$
      namespaceTable.addNamespaceInfo(prefix, uri, ""); //$NON-NLS-1$
    }  

    // here's where we update the inferred grammar if required
    // 
    boolean createCMElementDeclaration = true;
          
    boolean isLocal = (uri == null && prefix == null);         
    if (isLocal)
    {          
      if (parentInferredCMDocument == null)
      {
        // this is a local element... and the parent is not inferred (e.g) it has a known grammar
        // so we don't need to create an element declaration for this element
        createCMElementDeclaration = false; 
      }                             
      else
      {
        if (uri == null)
        {
          uri = "ommitted-namespace"; //$NON-NLS-1$
        }
      }
    }

    if (createCMElementDeclaration)
    {            
      if (isLocal)
      {
        inferredCMDocument = parentInferredCMDocument;
        inferredCMElementDeclaration = inferredGrammarFactory.createCMElementDeclaration(inferredCMDocument, element, true);       
      }          
      else
      {
        boolean createCMDocument = false;

        String cacheKey = "inferred-document" + uri;  //$NON-NLS-1$
        inferredCMDocument = (CMDocument)createdCMDocumentTable.get(cacheKey);

        if (inferredCMDocument == null)
        {
          // we don't have an inferred document for this uri yet... let's see of we need one
          int status = cmDocumentManager.getCMDocumentStatus(uri);                                                                                           
          if (status == CMDocumentCache.STATUS_NOT_LOADED || status == CMDocumentCache.STATUS_ERROR)
          {                  
            // the cache does not contain a 'proper' CMDocument for this uri
            // so we need to create an inferred one
            createCMDocument = true;
          } 
        } 
                
        if (createCMDocument)
        {           
          //System.out.println("encountered element {" + element.getNodeName() + "} ... creating inferred CMDocument for " + uri);
          inferredCMDocument = inferredGrammarFactory.createCMDocument(uri);
          cmDocumentManager.addCMDocument(uri, "", cacheKey, "XSD", inferredCMDocument); //$NON-NLS-1$ //$NON-NLS-2$
          createdCMDocumentTable.put(cacheKey, inferredCMDocument);
        }

        if (inferredCMDocument != null)
        {
          inferredCMElementDeclaration = inferredGrammarFactory.createCMElementDeclaration(inferredCMDocument, element, false);
        }                                                                                                                                                   
      }

      if (parentInferredCMElementDeclaration != null)
      {
        inferredGrammarFactory.createCMContent(parentInferredCMDocument, parentInferredCMElementDeclaration, inferredCMDocument, inferredCMElementDeclaration, isLocal, uri);
      } 
    }               
          
    visitChildNodes(element);
      
    // reset the 'current' state to inital values
    inferredCMElementDeclaration = parentInferredCMElementDeclaration;
    inferredCMDocument = parentInferredCMDocument;
  }                                                                                                       
}
