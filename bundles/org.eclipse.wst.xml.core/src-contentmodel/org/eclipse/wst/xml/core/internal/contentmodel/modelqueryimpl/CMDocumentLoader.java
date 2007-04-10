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
                          
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 */
public class CMDocumentLoader
{                                           
  protected Document document;
  protected ModelQuery modelQuery;
  protected CMDocumentManager cmDocumentManager;
  protected boolean isInferredGrammarEnabled = true;  
  protected CMDocumentLoadingNamespaceTable namespaceTable;
  protected int count = 0;
    
  public CMDocumentLoader(Document document, ModelQuery modelQuery)
  {                             
    this(document, modelQuery.getCMDocumentManager());
  }
  
  public CMDocumentLoader(Document document, CMDocumentManager cmDocumentManager)
  {   
    this.document = document;                     
    this.cmDocumentManager = cmDocumentManager;  	
  }
  
  public void loadCMDocuments()
  {          
    //System.out.println("----------loadCMDocuments ------------");          
    //long time = System.currentTimeMillis();
       
    boolean walkDocument = false;
            
    cmDocumentManager.removeAllReferences();
      
    String[] doctypeInfo = XMLAssociationProvider.getDoctypeInfo(document);
    if (doctypeInfo != null)
    {
      // load the doctype if required
      walkDocument = handleGrammar(doctypeInfo[0], doctypeInfo[1], "DTD"); //$NON-NLS-1$
    }                                   
    else
    {                           
      Element element = getRootElement(document);
      if (element != null)
      {
        namespaceTable = new CMDocumentLoadingNamespaceTable(document);   
        namespaceTable.addElement(element);
        if (namespaceTable.isNamespaceEncountered())
        {   
          walkDocument = true;
          //System.out.println("isNamespaceAware");
        }
        else
        {
          namespaceTable = null;
          walkDocument = isInferredGrammarEnabled;
          //System.out.println("is NOT namespaceAware");
        }        
      }  
    } 

    if (walkDocument)
    {
      visitNode(document);   
    } 

    //System.out.println("--- elapsed time (" + count + ") = " + (System.currentTimeMillis() - time));
  }


  public boolean handleGrammar(String publicId, String systemId, String type)
  {           
    boolean result = false;
    
    int status = cmDocumentManager.getCMDocumentStatus(publicId);
    if (status == CMDocumentCache.STATUS_NOT_LOADED)
    {
      cmDocumentManager.addCMDocumentReference(publicId, systemId, type);
    }                 
    else if (status == CMDocumentCache.STATUS_ERROR)
    {
      result = true;
    }
    return result;
  } 
    

  public void handleElement(Element element)
  {  
    visitChildNodes(element);
  }

                             
  public void handleElementNS(Element element)
  {
    namespaceTable.addElement(element);
    visitChildNodes(element);
  }
                                                    

  public void visitNode(Node node)
  {                    
    int nodeType = node.getNodeType();
    if (nodeType == Node.ELEMENT_NODE)
    {
      count++;       

      Element element = (Element)node;    
      if (namespaceTable == null)
      {
        handleElement(element); 
      }
      else
      {
        handleElementNS(element);
      }            
    }
    else if (nodeType == Node.DOCUMENT_NODE)
    {
      visitChildNodes(node);
    }
  }


  protected void visitChildNodes(Node node)
  {   
	  for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) 
    {
	    visitNode(child);
    }
	}             


  protected class CMDocumentLoadingNamespaceTable extends NamespaceTable
  {                                        
    protected List newNamespaceList;

    public CMDocumentLoadingNamespaceTable(Document document)
    {                                                          
      super(document);     
    }  
                                           

    public void addElement(Element element)
    {                               
      newNamespaceList = null;
      super.addElement(element);  
      if (newNamespaceList != null)
      {
        for (Iterator i = newNamespaceList.iterator(); i.hasNext(); )
        {
          NamespaceInfo info = (NamespaceInfo)i.next();
          handleGrammar(info.uri, info.locationHint, "XSD"); //$NON-NLS-1$
        }
      }
    }                 
     
                               
    protected void internalAddNamespaceInfo(String key, NamespaceInfo info)
    {
      super.internalAddNamespaceInfo(key, info);           
      if (newNamespaceList == null)
      {
        newNamespaceList = new ArrayList();
      }
      newNamespaceList.add(info);    
    }                     
  }

  
  protected Element getRootElement(Document document)
  {
    Element result = null;
    NodeList nodeList = document.getChildNodes();
    int nodeListLength = nodeList.getLength();
    for (int i = 0 ; i < nodeListLength; i++)
    {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        result = (Element)node;
        break;
      }
    }
    return result;
  }
}
