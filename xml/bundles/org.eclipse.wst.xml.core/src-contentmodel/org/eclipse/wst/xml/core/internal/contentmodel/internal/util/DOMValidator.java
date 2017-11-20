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
package org.eclipse.wst.xml.core.internal.contentmodel.internal.util;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMVisitor;
import org.eclipse.wst.xml.core.internal.contentmodel.util.DOMNamespaceHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * A special CMValidator that knows about DOMs
 */
public class DOMValidator extends CMValidator
{               
  protected String getNamespaceURI(Node node)
  {   
    return DOMNamespaceHelper.getNamespaceURI(node);
    //return node.getNamespaceURI();
  }
          

  //
  // This is a temporary hack!!
  //
  protected String getFallbackNamepaceURI(CMElementDeclaration ed)
  {   
    String fallbackNamepaceURI = null;
    CMDocument cmDocument = (CMDocument)ed.getProperty("CMDocument"); //$NON-NLS-1$
    if (cmDocument != null)
    {
      fallbackNamepaceURI = (String)cmDocument.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI");   //$NON-NLS-1$
    }  
    return fallbackNamepaceURI;
  }

  /**
   * Encode the Element's NodeList as a List of strings that the validator recognizes
   */
  public List createContentSpecificationList(Element element, CMElementDeclaration ed)
  {                                                                    
    boolean isNamespaceAware = isNamespaceAware(ed);
    Vector v = new Vector();         
    for (Node childNode = element.getFirstChild(); childNode != null; childNode = childNode.getNextSibling())
    { 
      v.add(createContentSpecification(childNode, isNamespaceAware, isNamespaceAware ? getFallbackNamepaceURI(ed) : null));
    }
    return v;
  }


  public List createContentSpecificationList(List nodeList, CMElementDeclaration ed)
  {             
    boolean isNamespaceAware = isNamespaceAware(ed);
    Vector v = new Vector();            
 
    for (Iterator i = nodeList.iterator(); i.hasNext(); )
    {
      Node node = (Node)i.next();
      v.add(createContentSpecification(node, isNamespaceAware, getFallbackNamepaceURI(ed)));
    }
    return v;
  }


  /**
   * Encode the Node as a string that the validator recognizes
   */
  public String createContentSpecification(Node node, boolean isNamespaceAware, String fallbackNamepaceURI)
  {
    String result = "!"; //$NON-NLS-1$
    switch (node.getNodeType())
    {
      case Node.ELEMENT_NODE :
      {  
        String nodeName = node.getNodeName();  
        if (nodeName.startsWith("jsp:")) //$NON-NLS-1$
        {  
          result = "!"; // treat it as a comment so that it's ignored by the validator //$NON-NLS-1$
        }
        else
        {
          if (isNamespaceAware)
          {
            result = DOMNamespaceHelper.getUnprefixedName(nodeName);
            String uri = getNamespaceURI(node);
            if (uri != null)
            {
              result = "[" + uri + "]" + result;    //$NON-NLS-1$ //$NON-NLS-2$
            } 
            else if (fallbackNamepaceURI != null)
            {
              result = "[" + fallbackNamepaceURI + "]" + result;   //$NON-NLS-1$ //$NON-NLS-2$
            }
          }  
          else
          {
            result = nodeName;
          }
        }        
        //ContentModelManager.println("result " + result);
        break;
      }
      case Node.PROCESSING_INSTRUCTION_NODE :
      {
        result = "?"; //$NON-NLS-1$
        break;
      }
      case Node.COMMENT_NODE :
      {
        result = "!"; //$NON-NLS-1$
        break;
      }
      case Node.CDATA_SECTION_NODE :
      {
        result = "\"" + node.getNodeName() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
        break;
      }
      case Node.TEXT_NODE :
      {
        String data = ((Text)node).getData();
        // here we test to see if the test node is 'ignorable'
        if (data != null && data.trim().length() > 0)
        {
          result = "\"" + node.getNodeName() + "\""; //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
          result = "!"; // todo... use another symbol? //$NON-NLS-1$
        }
        break;
      }
    }
    return result;
  }


  /**
   *
   */
  public List createContentSpecificationList(CMNode cmNode)
  {
    List list = new Vector();
    switch (cmNode.getNodeType())
    {
      case CMNode.ELEMENT_DECLARATION :
      {         
        list.add(createContentSpecificationForCMElementDeclaration((CMElementDeclaration)cmNode));   
        break;
      }
      case CMNode.DATA_TYPE :
      {
        list.add("\"" + cmNode.getNodeName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        break;
      }
      case CMNode.GROUP :
      {
        createContentSpecificationListForCMGroup((CMGroup)cmNode, list);
        break;
      }
      case CMNode.ANY_ELEMENT :
      {
        list.add("*"); //$NON-NLS-1$
        break;
      }
      default :
      {
        list.add("!"); //$NON-NLS-1$
      }
    }
    return list;
  }
     

  /**
   * 
   */              
  protected String createContentSpecificationForCMElementDeclaration(CMElementDeclaration ed)
  {  
    CMDocument document = (CMDocument)ed.getProperty("CMDocument"); //$NON-NLS-1$
    String uri = document != null ? (String)document.getProperty("http://org.eclipse.wst/cm/properties/targetNamespaceURI") : null; //$NON-NLS-1$
    String string = ed.getNodeName();
    if (uri != null)
    {            
      string = "[" + uri + "]" + string; //$NON-NLS-1$ //$NON-NLS-2$
    }
    return string;
  }
  
  /**
   *
   */
  protected void createContentSpecificationListForCMGroup(CMGroup group, List list)
  {         
    CMGroupContentVisitor visitor = new CMGroupContentVisitor(group, list);
    visitor.visitCMNode(group);
  } 
     
  protected class CMGroupContentVisitor extends CMVisitor
  {  
    protected CMGroup root;                             
    protected List list;

    public CMGroupContentVisitor(CMGroup root, List list)
    {                                        
      this.root = root;        
      this.list = list;
    }

    public void visitCMElementDeclaration(CMElementDeclaration ed)
    {           
      if (ed.getMinOccur() > 0)
      {
        list.add(createContentSpecificationForCMElementDeclaration(ed));
      }
    }       

    public void visitCMAnyElement(CMAnyElement anyElement)
    {   
      list.add("*"); //$NON-NLS-1$
    }

    public void visitCMGroup(CMGroup group)
    {                              
      if (group == root || group.getMinOccur() > 0)
      {
        int op = group.getOperator();
        if (op == CMGroup.SEQUENCE)
        {
          super.visitCMGroup(group);
        }
        else if (op == CMGroup.CHOICE)
        {
          CMNodeList nodeList = group.getChildNodes();
          if (nodeList.getLength() > 0)
          {
            visitCMNode(nodeList.item(0));
          }      
        }
      }
    }
  }

  public boolean isNamespaceAware(CMElementDeclaration ed)
  { 
    return ed != null ? ed.getProperty("http://org.eclipse.wst/cm/properties/isNameSpaceAware") != null : false; //$NON-NLS-1$
  }
     
  /**
   *
   */
  public CMNode[] getOriginArray(CMElementDeclaration ed, Element element)
  {
    ElementPathRecordingResult result = new ElementPathRecordingResult();
    getOriginArray(ed, createContentSpecificationList(element, ed), stringContentComparitor, result);
    return result.getOriginArray();
  }
                   
  /**
   *
   */
  public MatchModelNode getMatchModel(CMElementDeclaration ed, Element element)
  {
    MatchModelNode matchModelNode = null;
    PathRecordingResult result = new PathRecordingResult();
    validate(ed, createContentSpecificationList(element, ed), stringContentComparitor, result);
    if (result.isValid)
    {
      matchModelNode = result.getMatchModel();
    }
    return matchModelNode;
  }
                           

  public List clone(List list)
  {   
    List result = new Vector(list.size());
    result.addAll(list);
    return result;
  }
 
  /**
   *
   */
  public boolean canInsert(CMElementDeclaration ed, List contentSpecificationList, int insertIndex, CMNode cmNode)
  {           
    List clonedList = clone(contentSpecificationList);
    insert(clonedList, insertIndex, cmNode);
    boolean result = isPartiallyValid(ed, clonedList);   
    return result;
  }  

  /**
   *
   */
  public boolean canInsert(CMElementDeclaration ed, List contentSpecificationList, int insertIndex, List cmNodeList)
  {              
    List clonedList = clone(contentSpecificationList);
    insert(clonedList, insertIndex, cmNodeList);
    return isValid(ed, clonedList);
  }  

  /**
   *
   */
  public boolean canRemove(CMElementDeclaration ed, List contentSpecificationList, int startRemoveIndex)
  {
    return canRemove(ed, contentSpecificationList, startRemoveIndex, startRemoveIndex);
  }

  /**
   *
   */
  public boolean canRemove(CMElementDeclaration ed, List contentSpecificationList, int startRemoveIndex, int endRemoveIndex)
  {
    List clonedList = clone(contentSpecificationList);
    remove(clonedList, startRemoveIndex, endRemoveIndex);
    return isValid(ed, clonedList);
  }
                        
  /**
   *
   */
  public boolean canReplace(CMElementDeclaration ed, List contentSpecificationList, int startRemoveIndex, int endRemoveIndex, CMNode cmNode)
  {
    List clonedList = clone(contentSpecificationList);
    remove(clonedList, startRemoveIndex, endRemoveIndex); 
    insert(clonedList, startRemoveIndex, cmNode);
    return isValid(ed, clonedList);
  }

  /**
   *
   */                      
  public boolean isValid(CMElementDeclaration ed, List contentSpecificationList)
  {
    Result result = new Result();
    validate(ed, contentSpecificationList, stringContentComparitor, result);
    return result.isValid;
  }

  public boolean isPartiallyValid(CMElementDeclaration ed, List contentSpecificationList)
  {
    CMValidator.ElementPathRecordingResult result = new CMValidator.ElementPathRecordingResult();
    validate(ed, contentSpecificationList, stringContentComparitor, result);
    int count = getElementCount(contentSpecificationList);
    //System.out.println("elementOriginList " + result.getPartialValidationCount() + "vs" + count);
    return result.getPartialValidationCount() >= count;
  }  
  
  public int getElementCount(List contentSpecificationList)
  {
    int count = 0;
    for (Iterator i = contentSpecificationList.iterator(); i.hasNext(); )
    {
      if (stringContentComparitor.isElement(i.next()))
      {
        count++;
      }  
    }  
    return count;
  }

  protected Result validate(CMElementDeclaration ed, Element element)
  {
    Result result = new Result();
    validate(ed, createContentSpecificationList(element, ed), stringContentComparitor, result);
    return result;
  }


  protected void remove(List stringList, int startRemoveIndex, int endRemoveIndex)
  {
    if (startRemoveIndex != -1)
    {
      for (int i = startRemoveIndex; i <= endRemoveIndex; i++)
      {
        stringList.remove(i);
      }
    }
  }

  protected void insert(List stringList, int insertIndex, CMNode cmNode)
  {
    if (insertIndex != -1)
    {
      stringList.addAll(insertIndex, createContentSpecificationList(cmNode));
    }
  }

  protected void insert(List stringList, int insertIndex, List cmNodeList)
  {
    if (insertIndex != -1)
    {
      int insertListSize = cmNodeList.size();
      for (int i = insertListSize - 1; i >= 0; i--)
      {
        CMNode cmNode = (CMNode)cmNodeList.get(i);
        stringList.addAll(insertIndex, createContentSpecificationList(cmNode));
      }
    }
  }
}
