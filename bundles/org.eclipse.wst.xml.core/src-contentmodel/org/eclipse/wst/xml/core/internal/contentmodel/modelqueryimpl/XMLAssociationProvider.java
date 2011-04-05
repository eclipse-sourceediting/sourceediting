/*******************************************************************************
 * Copyright (c) 2002, 2011 IBM Corporation and others.
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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentReferenceProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.IExternalSchemaLocationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDocumentCache;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XMLAssociationProvider extends BaseAssociationProvider implements CMDocumentReferenceProvider
{              
  protected CMDocumentCache cmDocumentCache; 
  protected CMDocumentManagerImpl documentManager;

  private final static boolean _trace = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.xml.core/externalSchemaLocation")).booleanValue(); //$NON-NLS-1$

  public XMLAssociationProvider(CMDocumentCache cmDocumentCache)
  {
    this.cmDocumentCache = cmDocumentCache; 
    documentManager = new CMDocumentManagerImpl(cmDocumentCache, this);
  }                              

  public CMDocumentManager getCMDocumentManager()
  {
    return documentManager;
  }
 
      
  public static String[] getDoctypeInfo(Document document)
  {   
    String[] result = null;
    DocumentType doctype = document.getDoctype();

    // defect 206833 ... here we test for DTDs that are declared inline
    // since we currently have no way of making use of inline DTDs we ignore them
    // so that the implict DTD (if any) can be used
    if (doctype != null && (doctype.getPublicId() != null || doctype.getSystemId() != null))
    {
      result = new String[2];
      result[0] = doctype.getPublicId();
      result[1] = doctype.getSystemId();
    }   
    else if (getImplictDoctype(document) != null)
    {
      result = getImplictDoctype(document);
    }                  
    return result;
  }   


  protected static String[] getImplictDoctype(Document document)
  { 
    String[] result = null;
    /*
    DOMExtension domExtension = DOMExtensionProviderRegistry.getInstance().getDOMExtension(document);
    if (domExtension != null)
    {
      result = domExtension.getImplicitDoctype();
    }*/
    return result;
  }

  public CMDocument getCorrespondingCMDocument(Node node)
  {        
    return getCorrespondingCMDocument(node, true);
  }

  protected CMDocument getCorrespondingCMDocument(Node node, boolean getDocumentFromCMNode)
  {            
    CMDocument result = null;
    try
    {
      Document document = node.getNodeType() == Node.DOCUMENT_NODE ? (Document)node : node.getOwnerDocument();
   
      String[] doctypeInfo = getDoctypeInfo(document);

      if (doctypeInfo != null)
      {
        result = getCMDocument(doctypeInfo[0], doctypeInfo[1], "DTD"); //$NON-NLS-1$
      }                                             
      // defect 211236 ... in some cases calling this method can result in a cycle
      // we use the getDocumentFromCMNode as a flag to avoid this 
      // TODO... see if there is a way to re-organize to avoid the need for this flag
      else if (getDocumentFromCMNode)
      {
        CMNode cmNode = getCMNode(node);
        if (cmNode != null)       
        {                
          // todo... add a getCMDocument() methods to CMNode
          // for now use the getProperty interface
          result = (CMDocument)cmNode.getProperty("CMDocument"); //$NON-NLS-1$
        }
      }
    }
    catch (Exception e)
    {
      Logger.logException("exception locating CMDocument for " + node, e); //$NON-NLS-1$
    }
    return result;
  }    

      
  public CMDocument getCMDocument(Element element, String uri)
  {
    CMDocument result = null;
    NamespaceTable namespaceTable = new NamespaceTable(element.getOwnerDocument());
    namespaceTable.addElementLineage(element);
    NamespaceInfo namespaceInfo = namespaceTable.getNamespaceInfoForURI(uri);
    if (namespaceInfo != null)
    {
      result = getCMDocument(namespaceInfo.uri, namespaceInfo.locationHint, "XSD"); //$NON-NLS-1$
    }
    return result;
  }         
  
                          
  public CMDocument getCMDocument(String publicId, String systemId, String type)
  {                   
    //String resolvedGrammarURI = resolveGrammarURI(document, publicId, systemId);
    return documentManager.getCMDocument(publicId, systemId, type);  
  }

  //public CMDocument getCMDocument(Document document, String publicId, String systemId)
  //{                   
  //  //String resolvedGrammarURI = resolveGrammarURI(document, publicId, systemId);
  //  return documentManager.getCMDocument(publicId, systemId);  
  //}
   
  public String resolveGrammarURI(String publicId, String systemId)
  {
    return resolveGrammarURI(null, publicId, systemId);
  }


  /**
   * This method should be specialized in order to implement specialized uri resolution
   */
  protected String resolveGrammarURI(Document document, String publicId, String systemId)
  {
    return systemId;
  }
  

  public CMElementDeclaration getCMElementDeclaration(Element element)
  { 
    CMElementDeclaration result = null; 
    Document document = element.getOwnerDocument();
    String[] doctypeInfo = getDoctypeInfo(document);
    if (doctypeInfo != null)
    {   
      // we have detected doctype information so we assume that we can locate the CMElementDeclaration 
      // in the CMDocument's table of global elements 
      CMDocument cmDocument = getCorrespondingCMDocument(element, false);

      // TODO... consider replacing above with 
      // CMDocument cmDocument = getCMDocument(document, doctypeInfo[0], doctypeInfo[1]);

      if (cmDocument != null)
      {        
        result = (CMElementDeclaration)cmDocument.getElements().getNamedItem(element.getNodeName());    
                 
        // this is a hack to get our xsl code assist working... we might want to handle similar
        // grammar behaviour via some established model query setting 
        if (result == null && getImplictDoctype(document) != null)
        {         
          Node parent = element.getParentNode();
          if (parent != null && parent.getNodeType() == Node.ELEMENT_NODE)
          {
            result = getCMElementDeclaration((Element)parent);
          }
        }
      }
    }    
    else
    {  
      // here we use a namespaceTable to consider if the root element has any namespace information
      //
      NamespaceTable namespaceTable = new NamespaceTable(element.getOwnerDocument());
      List list = NamespaceTable.getElementLineage(element);
      Element rootElement = (Element)list.get(0);
      namespaceTable.addElement(rootElement);
         
      if (namespaceTable.isNamespaceEncountered())
      {                                         
        // we assume that this is an XMLSchema style namespace aware document
        result = getCMElementDeclaration(element, list, namespaceTable);
      }
      else
      { 
    	  result = checkExternalSchema(element);
    	  if (result == null) {
    		// we assume that this is an inferred CMDocument for a DTD style 'namespaceless' document
  	        CMDocument cmDocument = getCMDocument("", "", "DTD"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		      if (cmDocument != null) {
				  result = (CMElementDeclaration)cmDocument.getElements().getNamedItem(element.getNodeName());
			  }
    	  }
      }
    }             
    return result;
  } 
     
  protected CMElementDeclaration checkExternalSchema(Element element) {
	  final Document document = element.getOwnerDocument();
	  if (document instanceof IDOMDocument) {
		  final String baseLocation = ((IDOMDocument) document).getModel().getBaseLocation();
		  if (baseLocation != null) {
			  final IPath basePath = new Path(baseLocation);
			  IFile file = null;
			  if (basePath.segmentCount() > 1) {
				  file = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
			  }
			  final URI uri = (file == null || !file.isAccessible()) ? new File(baseLocation).toURI() : file.getLocationURI();
			  if (uri != null) {
				  IExternalSchemaLocationProvider[] providers = ExternalSchemaLocationProviderRegistry.getInstance().getProviders();
				  for (int i = 0; i < providers.length; i++) {
					  long time = _trace ? System.currentTimeMillis(): 0;
					  final Map locations = providers[i].getExternalSchemaLocation(uri);
					  if (_trace) {
						  long diff = System.currentTimeMillis() - time;
						  if (diff > 250)
							  Logger.log(Logger.INFO, "Schema location provider took [" + diff + "ms] for URI [" + uri + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					  }
					  if (locations != null && !locations.isEmpty()) {
						  Object location = locations.get(IExternalSchemaLocationProvider.NO_NAMESPACE_SCHEMA_LOCATION);
						  if (location != null)
							  return getCMElementDeclaration(element, NamespaceTable.getElementLineage(element), uri.toString(), location.toString());
					  }
				  }
			  }
		  }
	  }
	  return null;
  }

  protected CMElementDeclaration getCMElementDeclaration(Element targetElement, List list, String publicId, String systemId)
  {         
    CMElementDeclaration currentED = null;
    try
    {    
      int listSize = list.size();
      for (int i = 0; i < listSize; i++)
      {
        Element element = (Element)list.get(i);                                     

        final String nodeName = element.getNodeName();
 
        CMElementDeclaration ed = null;
 
        // see if the element is a local of the currentED
        //             
        if (currentED != null)
        {  
          ed = (CMElementDeclaration)currentED.getLocalElements().getNamedItem(nodeName);
        } 
                                                                   
        if (ed == null) 
        {               
            CMDocument cmDocument = getCMDocument(publicId, systemId, "XSD"); //$NON-NLS-1$
            if (cmDocument != null)
            { 
              ed = (CMElementDeclaration)cmDocument.getElements().getNamedItem(nodeName);   
            }                                        
        }                                                   
        currentED = ed;     
      }                                       
    }
    catch (Exception e)
    { 
      Logger.logException("exception locating element declaration for " + targetElement, e); //$NON-NLS-1$
    } 
  
    return currentED;
  }  

  protected CMElementDeclaration getCMElementDeclaration(Element targetElement, List list, NamespaceTable namespaceTable)
  {         
    CMElementDeclaration currentED = null;
    try
    {    
      int listSize = list.size();
      for (int i = 0; i < listSize; i++)
      {
        Element element = (Element)list.get(i);                                     
                    
        if (i != 0)
        {
          namespaceTable.addElement(element);                        
        }

        String nodeName = element.getNodeName();
        String unprefixedName = DOMNamespaceHelper.getUnprefixedName(nodeName);
        String prefix = DOMNamespaceHelper.getPrefix(nodeName);
 
        CMElementDeclaration ed = null;
 
        // see if the element is a local of the currentED
        //             
        if (currentED != null)
        {  
          ed = (CMElementDeclaration)currentED.getLocalElements().getNamedItem(unprefixedName);
        } 
                                                                   
        if (ed == null) 
        {               
          NamespaceInfo namespaceInfo = namespaceTable.getNamespaceInfoForPrefix(prefix);                   
          if (namespaceInfo != null) 
          {
            CMDocument cmDocument = getCMDocument(namespaceInfo.uri, namespaceInfo.locationHint, "XSD"); //$NON-NLS-1$
            if (cmDocument != null)
            { 
              ed = (CMElementDeclaration)cmDocument.getElements().getNamedItem(unprefixedName);   
            }                                        
          }
        }                                                   
        currentED = ed;     

        // handle XSIType     
        if (currentED != null)
        {
          CMElementDeclaration derivedED = getDerivedCMElementDeclaration(element, currentED, namespaceTable);
          if (derivedED != null)
          {                           
            currentED = derivedED;
          }    
        }
      }                                       
    }
    catch (Exception e)
    { 
      Logger.logException("exception locating element declaration for " + targetElement, e); //$NON-NLS-1$
    } 
  
    return currentED;
  }  
      

  protected CMElementDeclaration getDerivedCMElementDeclaration(Element element, CMElementDeclaration ed, NamespaceTable namespaceTable)
  {                      
    CMElementDeclaration result = null;
    String xsiPrefix = namespaceTable.getPrefixForURI("http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$
    if (xsiPrefix != null)
    {
      String xsiTypeValue = element.getAttribute(xsiPrefix + ":type"); //$NON-NLS-1$
      if (xsiTypeValue != null && xsiTypeValue.length() > 0)
      {  
        String typePrefix = DOMNamespaceHelper.getPrefix(xsiTypeValue);
        String typeName = DOMNamespaceHelper.getUnprefixedName(xsiTypeValue);
        String typeURI = namespaceTable.getURIForPrefix(typePrefix);
        String uriQualifiedTypeName = typeName;
        if (typeURI != null && typeURI.length() > 0) 
        {
          uriQualifiedTypeName = "[" +  typeURI + "]" + typeName; //$NON-NLS-1$ //$NON-NLS-2$
        }
        result = (CMElementDeclaration)ed.getProperty("DerivedElementDeclaration=" + uriQualifiedTypeName);   //$NON-NLS-1$
      }
    }                                                                                                    
    return result;
  }   


  public CMAttributeDeclaration getCMAttributeDeclaration(Attr attr)
  {
    CMAttributeDeclaration result = null;
    Element element = attr.getOwnerElement();
    if (element != null)
    {
      CMElementDeclaration ed = getCMElementDeclaration(element);
      if (ed != null)
      {                                                
        result = (CMAttributeDeclaration)ed.getAttributes().getNamedItem(attr.getName());
        if (result == null)
        {                                              
          // try to get the unprefixed name             
          String name = DOMNamespaceHelper.getUnprefixedName(attr.getName());
          result = (CMAttributeDeclaration)ed.getAttributes().getNamedItem(name);
        }                                                                        
        if (result == null)
        {
          // todo... perhaps this is a globally defined attribute... 
        }
      }
    }
    return result;
  }               

  /**
   * This method returns a list of CMDocumentReferences associated with a particular node or subtree
   */                                                                                                          
  public List getCMDocumentReferences(Node node, boolean deep)
  { 
    List result = new ArrayList();  
    Document document = (node.getNodeType() == Node.DOCUMENT_NODE) ? (Document)node : node.getOwnerDocument();
    DocumentType doctype = document.getDoctype();
    // defect 206833 ... here we test for DTDs that are declared inline
    // since we currently have no way of making use of inline DTDs we ingore them
    // so that the implict DTD (if any) can be used
    if (doctype != null && (doctype.getPublicId() != null || doctype.getSystemId() != null))
    {                                                                               
      String uri = resolveGrammarURI(document, doctype.getPublicId(), doctype.getSystemId());
      result.add(new CMDocumentReferenceImpl(doctype.getPublicId(), uri));
    }   
    else if (getImplictDoctype(document) != null)
    {                  
      String[] implicitDoctype = getImplictDoctype(document);
      String uri = resolveGrammarURI(document, implicitDoctype[0], implicitDoctype[1]);
      result.add(new CMDocumentReferenceImpl(implicitDoctype[0], uri));
    }                              
    else
    {   
      NamespaceTable namespaceTable = new NamespaceTable(document);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {
		    namespaceTable.addElement((Element)node);
      }                                     
      if (deep)
      {
        addChildElementsToNamespaceTable(node, namespaceTable);
      }
	    List list = namespaceTable.getNamespaceInfoList();
		  for (Iterator i = list.iterator(); i.hasNext();) 
      {
			  NamespaceInfo info = (NamespaceInfo) i.next();    
        String uri = resolveGrammarURI(document, info.uri, info.locationHint);
        result.add(new CMDocumentReferenceImpl(info.uri, uri));
		  }	
    } 
    return result;
  }

  protected void addChildElementsToNamespaceTable(Node node, NamespaceTable namespaceTable)
  {
    NodeList nodeList = node.getChildNodes();
	  if (nodeList != null) 
    {
		  int nodeListLength = nodeList.getLength();
		  for (int i = 0; i < nodeListLength; i++) 
      {
			  Node childNode = nodeList.item(i);
        if (childNode.getNodeType() == Node.ELEMENT_NODE)
        {   
          namespaceTable.addElement((Element)childNode);
          addChildElementsToNamespaceTable(childNode, namespaceTable);
        }
		  }
	  }
  }  
}
