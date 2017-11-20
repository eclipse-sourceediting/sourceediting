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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.modelqueryimpl;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMAnyElementImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMAttributeDeclarationImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMDocumentImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMElementDeclarationImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMGroupImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMNamedNodeMapImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.basic.CMNodeListImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMDescriptionBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

 

public class InferredGrammarFactory
{                                      
  public InferredGrammarFactory()
  {
  }

  public CMDocument createCMDocument(String uri)
  {                      
    CMDocumentImpl cmdocument =new CMDocumentImpl(uri);
    cmdocument.setInferred(true);
    return cmdocument;
  }    
 
  public CMElementDeclaration createCMElementDeclaration(CMDocument cmDocument, Element element, boolean isLocal)
  {
    String localName = element.getLocalName();                                   
    CMDocumentImpl cmDocumentImpl = (CMDocumentImpl)cmDocument;

    CMNamedNodeMapImpl elementMap = isLocal ?
                                    (CMNamedNodeMapImpl)cmDocumentImpl.getLocalElementPool() :
                                    (CMNamedNodeMapImpl)cmDocumentImpl.getElements();

    CMElementDeclarationImpl ed = (CMElementDeclarationImpl)elementMap.getNamedItem(localName);
    if (ed == null)
    {                                                                                          
      //System.out.println("create ed " + localName);
      ed = new CMElementDeclarationImpl(cmDocument, localName);
      ed.setInferred(true);
      ed.setLocal(isLocal);
      ed.setMaxOccur(1);
      CMGroupImpl group = new CMGroupImpl(new CMNodeListImpl(), CMGroup.CHOICE);
      group.setInferred(true);
      group.setMinOccur(0);
      group.setMaxOccur(-1);
      ed.setContent(group);            
      elementMap.put(ed);
    } 

    // lookup or create the attributes
    //
    NamedNodeMap attributeMap = element.getAttributes();
    int attributeMapLength = attributeMap.getLength();
    for (int i = 0; i < attributeMapLength; i++)
    {
      Attr attr = (Attr)attributeMap.item(i);
      CMAttributeDeclarationImpl ad = (CMAttributeDeclarationImpl)ed.getAttributeMap().getNamedItem(attr.getNodeName());
      if (ad == null)
      {     
        // todo... use an attribute pool to be more efficient?
        ad = new CMAttributeDeclarationImpl(attr.getNodeName(), CMAttributeDeclaration.OPTIONAL);
        ad.setInferred(true);
        ed.getAttributeMap().put(ad);
      }
    }
    return ed;
  }         

  public void createCMContent(CMDocument parentCMDocument, CMElementDeclaration parentEd, CMDocument childCMDocument, CMElementDeclaration childEd, boolean isLocal, String uri)
  {
    // add element to the parent's content
    // consider all content to be of the form (A | B | C)*    
    //
    CMGroupImpl group = (CMGroupImpl)parentEd.getContent();
    CMNodeListImpl groupChildNodeList = (CMNodeListImpl)group.getChildNodes();

    if (parentCMDocument == childCMDocument)
    {
      if (!groupChildNodeList.contains(childEd))
      {
        groupChildNodeList.add(childEd);
      }
      if (isLocal)
      {
        CMNamedNodeMapImpl localElementMap = (CMNamedNodeMapImpl)parentEd.getLocalElements();
        localElementMap.put(childEd);
      }
    }     
    else
    {                                                    
      CMAnyElement cmAnyElement = lookupOrCreateCMAnyElement((CMDocumentImpl)parentCMDocument, uri);
      if (!groupChildNodeList.contains(cmAnyElement))
      {
        groupChildNodeList.add(cmAnyElement);
      }
    }      
  }   

  protected CMAnyElement lookupOrCreateCMAnyElement(CMDocumentImpl parentCMDocument, String uri)
  {
    CMNamedNodeMapImpl anyElementMap = parentCMDocument.getAnyElements();
    CMAnyElementImpl anyElement = (CMAnyElementImpl)anyElementMap.getNamedItem(CMAnyElementImpl.computeNodeName(uri));
    if (anyElement == null)
    {                                                                                     
      //System.out.println("create anyElement " + uri);
      anyElement = new CMAnyElementImpl(uri);
      anyElement.setInferred(true);
      anyElementMap.put(anyElement);
    }
    return anyElement;
  } 
      

  public void debugPrint(Collection collection)
  {                
    for (Iterator iter = collection.iterator(); iter.hasNext(); )
    {
      CMDocument cmDocument = (CMDocument)iter.next(); 
      System.out.println("-----------------------------------------------"); //$NON-NLS-1$
      System.out.println("cmDocument (" + cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI") +")");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      CMNamedNodeMapImpl elementMap = (CMNamedNodeMapImpl)cmDocument.getElements();
      int size = elementMap.getLength();
      for (int i = 0; i < size; i++)
      {
        CMElementDeclaration ed = (CMElementDeclaration)elementMap.item(i);
        CMDescriptionBuilder builder = new CMDescriptionBuilder();
        System.out.println("  ELEMENT " + ed.getNodeName() + " = " + builder.buildDescription(ed)); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
  }        
}
