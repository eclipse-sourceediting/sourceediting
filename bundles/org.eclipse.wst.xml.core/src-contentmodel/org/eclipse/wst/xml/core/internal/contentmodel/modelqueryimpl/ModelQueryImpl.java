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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.modelqueryimpl.ModelQueryExtensionManagerImpl;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.util.CMDataTypeValueHelper;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.util.DOMValidator;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentManager;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryAssociationProvider;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension.ModelQueryExtensionManager;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMVisitor;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceInfo;
import org.eclipse.wst.xml.core.internal.contentmodel.util.NamespaceTable;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * This class implements a large portion of the ModelQuery interfaces.
 * Some work is delegated to the DOMHelper, CMDocumentManager, and DOMValidator.
 */
public class ModelQueryImpl implements ModelQuery
{
  protected ModelQueryAssociationProvider modelQueryAssociationProvider;
  protected ModelQueryActionHelper modelQueryActionHelper;
  protected DOMValidator validator;   
  protected ModelQueryExtensionManagerImpl extensionManager;   
  protected CMDataTypeValueHelper valueHelper;
  protected int editMode = EDIT_MODE_CONSTRAINED_STRICT;

  public ModelQueryImpl(ModelQueryAssociationProvider modelQueryAssociationProvider)
  {
    this.modelQueryAssociationProvider = modelQueryAssociationProvider;
    modelQueryActionHelper = createModelQueryActionHelper();
    validator = new DOMValidator();                         
    extensionManager = new ModelQueryExtensionManagerImpl();
    valueHelper = new CMDataTypeValueHelper();
  }
                         
  public int getEditMode()
  {
    return editMode;
  }
                    
  public void setEditMode(int editMode)
  {
    this.editMode =editMode;
  }


  // factory methods
  public ModelQueryActionHelper createModelQueryActionHelper()
  {
    return new ModelQueryActionHelper(this);
  } 

  public DOMValidator getValidator()
  {
    return validator;
  }

  public CMDocument getCorrespondingCMDocument(Node node)
  {
    return modelQueryAssociationProvider.getCorrespondingCMDocument(node);
  }

  public CMNode getCMNode(Node node)
  {
    return modelQueryAssociationProvider.getCMNode(node);
  }

  public CMDataType getCMDataType(Text text)
  {
    return modelQueryAssociationProvider.getCMDataType(text);
  }

  public CMAttributeDeclaration getCMAttributeDeclaration(Attr attr)
  {
    return modelQueryAssociationProvider.getCMAttributeDeclaration(attr);
  }

  public CMElementDeclaration getCMElementDeclaration(Element element)
  {
    return modelQueryAssociationProvider.getCMElementDeclaration(element);
  }
   
  public CMDocumentManager getCMDocumentManager()
  {
    CMDocumentManager result = null;
    if (modelQueryAssociationProvider instanceof XMLAssociationProvider)
    {             
      XMLAssociationProvider xmlAssociationProvider = (XMLAssociationProvider)modelQueryAssociationProvider;
      result = xmlAssociationProvider.getCMDocumentManager();
    }    
    return result;
  }
       

  /**
   * @deprected - use 3 arg version below
   */
  public List getCMDocumentList(Element element, String uri)
  {        
    return Collections.EMPTY_LIST;
  }

  public List getCMDocumentList(Element element, CMElementDeclaration ed, String uri)
  {                
    List result = new ArrayList();
    if (modelQueryAssociationProvider instanceof XMLAssociationProvider)
    {              
      XMLAssociationProvider xmlAssociationProvider = (XMLAssociationProvider)modelQueryAssociationProvider;
     
      // todo... revist... handle each ##thing explicitly
      //          
      if (uri == null)
      {
        uri = "##any"; //$NON-NLS-1$
      }               

      if (uri.equals("##targetNamespace")) //$NON-NLS-1$
      {                                                      
        CMDocument cmDocument = (CMDocument)ed.getProperty("CMDocument"); //$NON-NLS-1$
        if (cmDocument != null)
        {  
          result.add(cmDocument);
        }
      }
      else if (uri.equals("##any") || uri.equals("##other")) //$NON-NLS-1$ //$NON-NLS-2$
      {                                        
        String excludedURI = null;
        if (uri.equals("##other")) //$NON-NLS-1$
        {
          CMDocument cmDocument = (CMDocument)ed.getProperty("CMDocument");        //$NON-NLS-1$
          if (cmDocument != null)
          {
            excludedURI = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI"); //$NON-NLS-1$
          }
        }
                               
        // in this case we should consider all of the schema related to this document
        //
        NamespaceTable namespaceTable = new NamespaceTable(element.getOwnerDocument());
        namespaceTable.addElementLineage(element);
        List list = namespaceTable.getNamespaceInfoList();
        for (Iterator i = list.iterator(); i.hasNext();)
        {
          NamespaceInfo info = (NamespaceInfo)i.next();
          if (info.uri != null && !info.uri.equals(excludedURI))
          {
            CMDocument document = xmlAssociationProvider.getCMDocument(info.uri, info.locationHint, "XSD"); //$NON-NLS-1$
            if (document != null)
            {
              result.add(document);
            }
          }
        }
      }   
      else
      {        
        CMDocument document = xmlAssociationProvider.getCMDocument(element, uri);
        if (document != null)
        {
          result.add(document);
        }
      }      
    }
    return result;
  }


  public CMDocument getCMDocument(Element element, String uri)
  {                

    CMDocument result = null;
    if (modelQueryAssociationProvider instanceof XMLAssociationProvider)
    {             
      XMLAssociationProvider xmlAssociationProvider = (XMLAssociationProvider)modelQueryAssociationProvider;
      result = xmlAssociationProvider.getCMDocument(element, uri);
    }
    //ContentModelManager.println("ModelQueryImpl.getCMDocument(" + element.getNodeName() + ", " + uri + ") = " + result);
    return result;
  }

  public boolean isContentValid(Element element)
  {               
    CMElementDeclaration ed = getCMElementDeclaration(element);
    return isContentValid(ed, element);
  }

  public boolean isContentValid(CMElementDeclaration ed, Element element)
  {                                               
    boolean result = true;    
    if (ed != null)
    { 
      // first check to see if all the required attributes are present
      //                                                      
      CMNamedNodeMap map = ed.getAttributes();
      int mapLength = map.getLength();
      for (int i = 0; i < mapLength; i++)
      {                                                           
        CMAttributeDeclaration ad = (CMAttributeDeclaration)map.item(i);
        String attributeName = DOMNamespaceHelper.computeName(ad, element, null);
        if (ad.getUsage() == CMAttributeDeclaration.REQUIRED)
        {               
           Attr attr = element.getAttributeNode(attributeName);
           if (attr == null)
           {
             result = false;
             break;
           }
        }
      }

      // now check to see of the children validate properly
      //
      if (result) 
      {
        CMNode[] originArray = getOriginArray(element);
        result = originArray != null && originArray.length == element.getChildNodes().getLength();
      }
    }
    return result;
  }


  public CMNode getOrigin(Node node)
  {
    CMNode result = null;
    // todo... make sure parent is right
    //
    Node parentNode = getParentOrOwnerNode(node);
    if (parentNode != null && parentNode.getNodeType() == Node.ELEMENT_NODE)
    {
      Element parentElement = (Element)parentNode;
      CMNode[] array = getOriginArray(parentElement);
      if (array != null)
      {
        int index = getIndexOfNode(parentElement.getChildNodes(), node);
        if (index < array.length)
        {
          result = array[index];
        }
      }
    }
    return result;
  }

  public CMNode[] getOriginArray(Element element)
  {
    CMElementDeclaration ed = getCMElementDeclaration(element);
    return (ed != null) ? getValidator().getOriginArray(ed, element) : null;
  }

  public int getIndexOfNode(NodeList nodeList, Node node)
  {
    int result = -1;
    int size = nodeList.getLength();
    for (int i = 0; i < size; i++)
    {
       if (nodeList.item(i) == node)
       {
         result = i;
         break;
       }
    }
    return result;
  }


  /**
   * Returns a list of all CMNode 'meta data' that may be potentially added to the element.
   */
  public List getAvailableContent(Element element, CMElementDeclaration ed, int includeOptions)
  {
    AvailableContentCMVisitor visitor = new AvailableContentCMVisitor(element, ed);
    List list = visitor.computeAvailableContent(includeOptions);
    if (extensionManager != null)
    {                    
      extensionManager.filterAvailableElementContent(list, element, ed, includeOptions);
    }  
    return list;
  }  


  public boolean canInsert(Element parent, CMNode cmNode, int index, int validityChecking)
  {
    boolean result = true;
    CMElementDeclaration ed = getCMElementDeclaration(parent);
    if (ed != null)
    {
      result = canInsert(parent, ed, cmNode, index, validityChecking);
    }
    return result;
  }


  public boolean canInsert(Element parent, CMElementDeclaration ed, CMNode cmNode, int index, int validityChecking)
  {
    return canInsert(parent, ed, cmNode, index, validityChecking, null);
  }         

  protected boolean canInsert(Element parent, CMElementDeclaration ed, CMNode cmNode, int index, int validityChecking, Object reuseableData)
  {
    boolean result = true;
    switch (cmNode.getNodeType())
    {
      case CMNode.ATTRIBUTE_DECLARATION :
      {
        String attributeName = DOMNamespaceHelper.computeName(cmNode, parent, null);
        result = parent.getAttributeNode(attributeName) == null;
        break;
      }
      case CMNode.ELEMENT_DECLARATION :
      case CMNode.GROUP :
      {
        if (validityChecking == VALIDITY_STRICT)
        {                                  
          // create list                       
          List contentSpecificationList = null;
          if (reuseableData != null)
          {                            
            contentSpecificationList = (List)reuseableData;
          }    
          else
          {                                                                                  
            contentSpecificationList = getValidator().createContentSpecificationList(parent, ed);
          }
          result = getValidator().canInsert(ed, contentSpecificationList, index, cmNode);
        }
        break;
      }
      case CMNode.DATA_TYPE :
      {
        int contentType = ed.getContentType();
        result = (contentType == CMElementDeclaration.MIXED ||
                  contentType == CMElementDeclaration.PCDATA ||
                  contentType == CMElementDeclaration.ANY);
        break;
      }
      default :
      {
        result = false;
        break;
      }
    }
    return result;
  }

  public boolean canInsert(Element parent, List cmNodeList, int index, int validityChecking)
  {
    // todo
    return true;
  }


  public boolean canRemove(Node node, int validityChecking)
  {
    boolean result = true;      
    if (validityChecking == VALIDITY_STRICT)
    {
      int nodeType = node.getNodeType();
      switch (nodeType)
      {
        case Node.ATTRIBUTE_NODE:
        {
          CMAttributeDeclaration ad = getCMAttributeDeclaration((Attr)node);
          if (ad != null)
          {
            result = (ad.getUsage() == CMAttributeDeclaration.OPTIONAL);
          }
          break;
        }
        case Node.ELEMENT_NODE:
        {
          Node parentNode = node.getParentNode();
          if (parentNode.getNodeType() == Node.ELEMENT_NODE)
          {
            Element parentElement = (Element)parentNode;
            CMElementDeclaration ed = getCMElementDeclaration(parentElement);
            if (ed != null)
            {
              List contentSpecificationList = getValidator().createContentSpecificationList(parentElement, ed);
              int index = getIndexOfNode(parentElement.getChildNodes(), node);
              result = getValidator().canRemove(ed, contentSpecificationList, index);
            }
          }
          break;
        }
      }
    }
    return result;
  }


  public boolean canRemove(List nodeList, int validityChecking)
  {
    boolean result = true;

    if (validityChecking == VALIDITY_STRICT)
    {
      Element parentElement = null;
      List childList = null;

      for (Iterator i = nodeList.iterator(); i.hasNext(); )
      {
        Node node = (Node)i.next();

        if (parentElement == null)
        {
          parentElement = getParentOrOwnerElement(node);
        }
        else if (parentElement != getParentOrOwnerElement(node))
        {
          // make sure the parent are the same
          result = false;
          break;
        }

        if (parentElement == null)
        {
          result = true;
          break;
        }

        int nodeType = node.getNodeType();
        if (nodeType == Node.ATTRIBUTE_NODE)
        {
          if (!canRemove(node, validityChecking))
          {
            result = false;
            break;
          }
        }
        else
        {
          if (childList == null)
          {
            childList = nodeListToList(parentElement.getChildNodes());
          }
          childList.remove(node);
        }
      }

      if (result && childList != null)
      {
        CMElementDeclaration ed = getCMElementDeclaration(parentElement);
        if (ed != null)
        {                                
          List contentSpecificationList = getValidator().createContentSpecificationList(childList, ed);
          result = getValidator().isValid(ed, contentSpecificationList);
        }
      }
    }

    return result;
  }

  public boolean canReplace(Element parent, int startIndex, int endIndex, CMNode cmNode, int validityChecking)
  {
    return true;
  }

  public boolean canReplace(Element parent, int startIndex, int endIndex, List cmNodeList, int validityChecking)
  {
    return true;
  }     
   
  /**
   * This method is experimental... use at your own risk
   */
  public boolean canWrap(Element childElement, CMElementDeclaration wrapElement, int validityChecking)
  {                        
    boolean result = true;  
    Node parentNode = childElement.getParentNode();                      
    if (parentNode.getNodeType() == Node.ELEMENT_NODE)
    {           
      Element parentElement = (Element)parentNode;      
      CMElementDeclaration parentEd = getCMElementDeclaration(parentElement);
      if (parentEd != null)
      {                                                                                         
        if (validityChecking == VALIDITY_STRICT)
        {
          int index = getIndexOfNode(parentElement.getChildNodes(), childElement);

          List contentSpecificationList = getValidator().createContentSpecificationList(parentElement, parentEd);
          List subList = contentSpecificationList.subList(index, index + 1);
          result = getValidator().canReplace(parentEd, contentSpecificationList, index, index, wrapElement);
          if (result)
          {
            result = getValidator().isValid(wrapElement, subList);
          }
        }
      }
    }
    else
    {
      result = false;
    }                
    return result;
  }

  public void getInsertActions(Element parent, CMElementDeclaration ed, int index, int includeOptions, int validityChecking, List actionList)
  {
    modelQueryActionHelper.getInsertActions(parent, ed, index, includeOptions, validityChecking, actionList);
  }

  public void getInsertActions(Document parent, CMDocument cmDocument, int index, int includeOptions, int validityChecking, List actionList)
  {
    modelQueryActionHelper.getInsertActions(parent, cmDocument, index, includeOptions, validityChecking, actionList);
  }

  public void getReplaceActions(Element parent, CMElementDeclaration ed, int includeOptions, int validityChecking, List actionList)
  {
    modelQueryActionHelper.getReplaceActions(parent, ed, includeOptions, validityChecking, actionList);
  }                     

  public void getReplaceActions(Element parent, CMElementDeclaration ed, List selectedChildren, int includeOptions, int validityChecking, List actionList)
  {
    modelQueryActionHelper.getReplaceActions(parent, ed, selectedChildren, includeOptions, validityChecking, actionList);
  }

  public void getInsertChildNodeActionTable(Element parent, CMElementDeclaration ed, int validityChecking, Hashtable actionTable)
  {
    modelQueryActionHelper.getInsertChildNodeActionTable(parent, ed, validityChecking, actionTable);
  }

  public void getActionTable(Element parent, CMElementDeclaration ed, int index, int validityChecking, Hashtable actionTable)
  {
    //modelQueryActionHelper.getAllActions(parent, ed, validityChecking, actionList);
  }


  // some helper methods
  //
  protected Node getParentOrOwnerNode(Node node)
  {
    return (node.getNodeType() == Node.ATTRIBUTE_NODE) ?
           ((Attr)node).getOwnerElement() :
           node.getParentNode();
  }

  protected Element getParentOrOwnerElement(Node node)
  {
    Node parent = getParentOrOwnerNode(node);
    return (parent.getNodeType() == Node.ELEMENT_NODE) ? (Element)parent : null;
  }
               

  protected List nodeListToList(NodeList nodeList)
  {
    int size = nodeList.getLength();
    List v = new ArrayList(size);
    for (int i = 0; i < size; i++)
    {
      v.add(nodeList.item(i));
    }
    return v;
  }   
   
  /**
  protected List getCMNodeList(NodeList nodeList)
  {
    int size = nodeList.getLength();
    Vector v = new Vector(size);
    for (int i = 0; i < size; i++)
    {
      v.add(getCMNode(nodeList.item(i));
    }
    return v;
  }
  */  

  public class AvailableContentCMVisitor extends CMVisitor
  {
    public Hashtable childNodeTable = new Hashtable();
    public Hashtable attributeTable = new Hashtable();
    public Element rootElement;
    public CMElementDeclaration rootElementDeclaration; 
    public boolean isRootVisited;
    protected boolean includeSequenceGroups;

    public AvailableContentCMVisitor(Element rootElement, CMElementDeclaration rootElementDeclaration)
    {                                     
      this.rootElement = rootElement;
      this.rootElementDeclaration = rootElementDeclaration;
    }

    protected String getKey(CMNode cmNode)
    {
      String key = cmNode.getNodeName();
      CMDocument cmDocument = (CMDocument)cmNode.getProperty("CMDocument"); //$NON-NLS-1$
      if (cmDocument != null)
      {                         
        String namespaceURI = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI");    //$NON-NLS-1$
        if (namespaceURI != null)
        {   
          key = "[" + namespaceURI + "]" + key; //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      return key;
    }
    
    protected void addToTable(Hashtable table, CMNode cmNode)
    {
      String nodeName = cmNode.getNodeName();
      if (nodeName != null && nodeName.length() > 0)
      {  
        table.put(getKey(cmNode), cmNode);
      }  
    }

    public List computeAvailableContent(int includeOptions)
    {                   
      List v = new ArrayList();  

      int contentType = rootElementDeclaration.getContentType();
      includeSequenceGroups = ((includeOptions & INCLUDE_SEQUENCE_GROUPS) != 0);
      visitCMNode(rootElementDeclaration);
      
      if ((includeOptions & INCLUDE_ATTRIBUTES) != 0)
      {
        v.addAll(attributeTable.values());
        CMAttributeDeclaration nillableAttribute = (CMAttributeDeclaration)rootElementDeclaration.getProperty("http://org.eclipse.wst/cm/properties/nillable"); //$NON-NLS-1$
        if (nillableAttribute != null)
        {
          v.add(nillableAttribute);
        }
      }  

      if ((includeOptions & INCLUDE_CHILD_NODES) != 0)
      {      
        if (contentType == CMElementDeclaration.MIXED ||
            contentType == CMElementDeclaration.ELEMENT)
        {
          v.addAll(childNodeTable.values());
        }
        else if (contentType == CMElementDeclaration.ANY)
        {      
          CMDocument cmDocument =  (CMDocument)rootElementDeclaration.getProperty("CMDocument"); //$NON-NLS-1$
          if (cmDocument != null)
          {
            CMNamedNodeMap elements = cmDocument.getElements();            
            for (Iterator i = elements.iterator(); i.hasNext(); )
            {
              v.add(i.next());
            } 
          }
        }
      }
      return v;
    }   

    public void visitCMAnyElement(CMAnyElement anyElement)
    {            
      String uri = anyElement.getNamespaceURI();                          
      List list = getCMDocumentList(rootElement, rootElementDeclaration, uri);
      for (Iterator iterator = list.iterator(); iterator.hasNext(); )
      {
        CMDocument cmdocument = (CMDocument)iterator.next();
        if (cmdocument != null)
        {                          
          CMNamedNodeMap map = cmdocument.getElements();
          int size = map.getLength();
          for (int i = 0; i < size; i++)
          {                       
            CMNode ed = map.item(i);                  
            addToTable(childNodeTable,ed);
          }        
        }                
      }
    }

    public void visitCMAttributeDeclaration(CMAttributeDeclaration ad)
    {
      super.visitCMAttributeDeclaration(ad);
      attributeTable.put(ad.getNodeName(), ad);
    }

    public void visitCMElementDeclaration(CMElementDeclaration ed)
    {
      if (ed == rootElementDeclaration && !isRootVisited)
      {
        isRootVisited = true;
        super.visitCMElementDeclaration(ed);
      }
      else
      {                                                                                  
        if (!Boolean.TRUE.equals(ed.getProperty("Abstract"))) //$NON-NLS-1$
        {
          addToTable(childNodeTable,ed);
        }

        CMNodeList substitutionGroup = (CMNodeList)ed.getProperty("SubstitutionGroup"); //$NON-NLS-1$
        if (substitutionGroup != null)
        {
          handleSubstitutionGroup(substitutionGroup);
        }
      }
    }                                              

    protected void handleSubstitutionGroup(CMNodeList substitutionGroup)
    {
      int substitutionGroupLength = substitutionGroup.getLength();
      for (int i = 0; i < substitutionGroupLength; i++)
      {
        CMNode ed = substitutionGroup.item(i);
        if (!Boolean.TRUE.equals(ed.getProperty("Abstract"))) //$NON-NLS-1$
        {
          addToTable(childNodeTable,ed);
        }
      }
    }

    public void visitCMGroup(CMGroup group)
    {
      if (includeSequenceGroups)
      {
        if (group.getOperator() == CMGroup.SEQUENCE &&
            group.getChildNodes().getLength() > 1 &&
            includesRequiredContent(group))
        {                                        
          childNodeTable.put(group, group);
        }
      }  
      super.visitCMGroup(group);
    }   

    public boolean includesRequiredContent(CMGroup group)
    {
      List list = getValidator().createContentSpecificationList(group);
      return list.size() > 1;
    }
  }    

 
  /**
   * @deprected - use getPossibleDataTypeValues()
   */
  public List getDataTypeValues(Element element, CMNode cmNode)
  {                                                                             
    return Arrays.asList(getPossibleDataTypeValues(element, cmNode));
  }
  
  /**
   * This methods return an array of possible values corresponding to the datatype of the CMNode (either an CMAttributeDeclaration or a CMElementDeclaration)
   */
  public String[] getPossibleDataTypeValues(Element element, CMNode cmNode)
  {
    List list = new ArrayList();                            
                               
    if (cmNode != null)
    {       
      CMDataType dataType = null;
      if (cmNode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION)
      {
        dataType = ((CMAttributeDeclaration)cmNode).getAttrType();
      }
      else if (cmNode.getNodeType() == CMNode.ELEMENT_DECLARATION)
      {
        dataType = ((CMElementDeclaration)cmNode).getDataType();
      }         
     
      String[] enumeratedValues = dataType != null ? dataType.getEnumeratedValues() : null;      
      if (enumeratedValues != null)
      {
        for (int i = 0; i < enumeratedValues.length; i++)
        {
          list.add(enumeratedValues[i]);
        } 
      }                              
    }
                         
    addValuesForXSIType(element, cmNode, list);
    
    if (extensionManager != null)
    {                    
      list.addAll(extensionManager.getDataTypeValues(element, cmNode));
    }          
    
    // Remove duplicates
    List duplicateFreeList = new ArrayList();
    Iterator iterator = list.iterator();
    while(iterator.hasNext()) {
    	Object next = iterator.next();
    	if(duplicateFreeList.indexOf(next) == -1) {
    		duplicateFreeList.add(next);
    	}
    }
    
    return (String[]) duplicateFreeList.toArray(new String[duplicateFreeList.size()]);
  }    

           
  protected void addValuesForXSIType(Element element, CMNode cmNode, List list)
  {               
    if (cmNode != null && cmNode.getNodeType() == CMNode.ATTRIBUTE_DECLARATION) 
    {                         
      CMAttributeDeclaration ad = (CMAttributeDeclaration)cmNode;                              
      if (valueHelper.isXSIType(ad))
      {             
        NamespaceTable table = new NamespaceTable(element.getOwnerDocument());
        table.addElementLineage(element);
        list.addAll(valueHelper.getQualifiedXSITypes(ad, table));     
      }
    }
  }
    

  public ModelQueryExtensionManager getExtensionManager()
  {
    return extensionManager;
  }
}
