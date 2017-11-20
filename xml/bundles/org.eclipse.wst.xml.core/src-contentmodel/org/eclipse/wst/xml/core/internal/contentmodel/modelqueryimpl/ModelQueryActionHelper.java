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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;
import org.eclipse.wst.xml.core.internal.contentmodel.internal.util.CMValidator;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryAction;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 */
public class ModelQueryActionHelper
{
  protected ModelQueryImpl modelQuery;

  protected static class Action implements ModelQueryAction
  {
    public int kind;
    public int startIndex;
    public int endIndex;
    public Node parent;
    public CMNode cmNode;
    public Object userData;

    public Action(int kind, Node parent, CMNode cmNode)
    {
      this.kind = kind;
      this.parent = parent;
      this.cmNode = cmNode;
    }

    public Action(int kind, Node parent, CMNode cmNode, int startIndex, int endIndex)
    {
      this.kind = kind;
      this.parent = parent;
      this.cmNode = cmNode;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
    }

    public int getKind()
    {
      return kind;
    }

    public int getStartIndex()
    {
      return startIndex;
    }

    public int getEndIndex()
    {
      return endIndex;
    }

    public Node getParent()
    {
      return parent;
    }

    public CMNode getCMNode()
    {
      return cmNode;
    }

    public Object getUserData()
    {
      return userData;
    }

    public void setUserData(Object object)
    {
      userData = object;
    }

    public void performAction()
    {
    }
  }


  public ModelQueryActionHelper(ModelQueryImpl modelQuery)
  {
    this.modelQuery = modelQuery;
  }


  public void getAllActions(Element parent, CMElementDeclaration ed, int validityChecking, List actionList)
  {
  }


  // insert actions
  //
  public void getInsertActions(Element parent, CMElementDeclaration ed, int index, int includeOptions, int validityChecking, List actionList)
  {
    if ((includeOptions & ModelQuery.INCLUDE_ATTRIBUTES) != 0)
    {
      getInsertAttributeActions(parent, ed, validityChecking, actionList);
    }
    includeOptions &= ~ModelQuery.INCLUDE_ATTRIBUTES;
    if ((includeOptions & ModelQuery.INCLUDE_CHILD_NODES) != 0)
    {
      if (index != -1)
      {
        getInsertChildNodeActionsAtIndex(parent, ed, index, includeOptions, validityChecking, actionList);
      }
      else
      {
        getInsertChildNodeActions(parent, ed, includeOptions, validityChecking, actionList);
      }
    }
  }



  protected void getInsertAttributeActions(Element parent, CMElementDeclaration ed, int validityChecking, List actionList)
  {
    // get actions for each insertable attribute
    //
    List availableAttributeList = modelQuery.getAvailableContent(parent, ed, ModelQuery.INCLUDE_ATTRIBUTES);

    for (Iterator i = availableAttributeList.iterator(); i.hasNext(); )
    {
      CMAttributeDeclaration ad = (CMAttributeDeclaration)i.next();
      if (modelQuery.canInsert(parent, ed, ad, 0, validityChecking))
      {
        Action action = new Action(ModelQueryAction.INSERT, parent, ad);
        actionList.add(action);
      }
    }
  }


  protected void getInsertChildNodeActionsAtIndex(Element parent, CMElementDeclaration ed, int index, int includeOptions, int validityChecking, List actionList)
  {                       
    // get actions for each insertable attribute
    //
    int size = parent.getChildNodes().getLength();
    if (index <= size)
    {                                                                                          
      List contentSpecificationList = modelQuery.getValidator().createContentSpecificationList(parent, ed); 
      List availableChildNodeList = modelQuery.getAvailableContent(parent, ed, includeOptions);

      boolean isSimpleChoice = isSimpleChoiceGroupContentModel(ed);
     
      for (Iterator i = availableChildNodeList.iterator(); i.hasNext(); )
      {
        CMNode cmNode = (CMNode)i.next();      
        if (isSimpleChoice || modelQuery.canInsert(parent, ed, cmNode, index, validityChecking, contentSpecificationList))
        {
          Action action = new Action(ModelQueryAction.INSERT, parent, cmNode, index, index);
          actionList.add(action);
        }
      }
    }
  }
                  
 
  protected boolean isSimpleChoiceGroupContentModel(CMElementDeclaration ed)
  {       
    boolean result = false;
    CMNode cmNode = ed.getContent();
    if (cmNode != null && cmNode.getNodeType() == CMNode.GROUP)
    {
      CMGroup cmGroup = (CMGroup)cmNode;
      if (cmGroup.getOperator() == CMGroup.CHOICE && cmGroup.getMaxOccur() == -1)
      {                   
        result = true;
        CMNodeList list = cmGroup.getChildNodes();
        for (int i = list.getLength() - 1; i >= 0; i--)
        {
          if (list.item(i).getNodeType() != CMNode.ELEMENT_DECLARATION)
          {
            result = false;
            break;
          }
        }
      }
    }
    return result;
  }


  protected void getInsertChildNodeActions(Element parent, CMElementDeclaration ed, int includeOptions, int validityChecking, List actionList)
  {
    int size = parent.getChildNodes().getLength();
    List contentSpecificationList = modelQuery.getValidator().createContentSpecificationList(parent, ed);
    List availableChildNodeList = modelQuery.getAvailableContent(parent, ed, includeOptions);

    boolean isSimpleChoice = isSimpleChoiceGroupContentModel(ed);

    for (Iterator iterator = availableChildNodeList.iterator(); iterator.hasNext(); )
    {
      CMNode cmNode = (CMNode)iterator.next();
      for (int i = size; i >= 0; i--)
      {
        if (isSimpleChoice || modelQuery.canInsert(parent, ed, cmNode, i, validityChecking, contentSpecificationList))
        {
          Action action = new Action(ModelQueryAction.INSERT, parent, cmNode, i, i);
          actionList.add(action);
          break;
        }
      }
    }
  }

  public void getInsertActions(Document parent, CMDocument cmDocument, int index, int includeOptions, int validityChecking, List actionList)
  {
    // get the root element and doctype index (if any)
    //
    int doctypeIndex = -1;
    DocumentType doctype = null;
    Element rootElement = null;
    NodeList nodeList = parent.getChildNodes();
    int nodeListLength = nodeList.getLength();
    for (int i = 0; i < nodeListLength; i++)
    {
      Node childNode = nodeList.item(i);
      if (childNode.getNodeType() == Node.ELEMENT_NODE)
      {
        rootElement = (Element)childNode;
        break;
      }
      else if (childNode.getNodeType() == Node.DOCUMENT_TYPE_NODE)
      {
        doctype = (DocumentType)childNode;
        doctypeIndex = i;
      }
    }

    // make sure that root elements are only added after the doctype (if any)
    if (rootElement == null && index > doctypeIndex)
    {
      CMNamedNodeMap map = cmDocument.getElements();
      int mapLength = map.getLength();
      for (int i = 0; i < mapLength; i++)
      {
        CMNode cmNode = map.item(i);

        boolean canAdd = true;
        if (validityChecking == ModelQuery.VALIDITY_STRICT)
        {
          canAdd = doctype == null || doctype.getName().equals(cmNode.getNodeName());
        }

        if (canAdd)
        {
          Action action = new Action(ModelQueryAction.INSERT, parent, cmNode, index, index);
          actionList.add(action);
        }
      }
    }
  }



  public void getInsertChildNodeActionTable(Element parent, CMElementDeclaration ed, int validityChecking, Hashtable actionTable)
  {
  }


  public void getReplaceActions(Element parent, CMElementDeclaration ed, int includeOptions, int validityChecking, List actionList)
  {
    CMValidator.MatchModelNode matchModelNode = modelQuery.getValidator().getMatchModel(ed, parent);
    if (matchModelNode != null)
    {
      MatchModelVisitor visitor = new MatchModelVisitor(parent, actionList);
      visitor.visitMatchModelNode(matchModelNode);
    }     
  }

  public void getReplaceActions(Element parent, CMElementDeclaration ed, List selectedChildren, int includeOptions, int validityChecking, List actionList)
  {
    int[] range = getRange(parent, selectedChildren);
    if (range != null)
    {                
      if (isContiguous(parent, range, selectedChildren))
      {
        List tempList = new Vector();
        getReplaceActions(parent, ed, includeOptions, validityChecking, tempList);
        if ((includeOptions & ModelQuery.INCLUDE_ENCLOSING_REPLACE_ACTIONS) != 0)
        {
          removeActionsNotContainingRange(tempList, range[0], range[1]);            
        }
        else
        {
          removeActionsNotMatchingRange(tempList, range[0], range[1]);    
        }
        actionList.addAll(tempList);
      }
    }   
    
    if (selectedChildren.size() == 1)
    {
      Node node = (Node)selectedChildren.get(0);
      if (node.getNodeType() == Node.ELEMENT_NODE)
      {        
        Element childElement = (Element)node;       
        CMNode childEd = modelQuery.getCMElementDeclaration(childElement);
        if (childEd != null)
        {         

          CMNode childOrigin= modelQuery.getOrigin(childElement);

          CMNodeList cmNodeList = childOrigin != null ? 
                                    (CMNodeList)childOrigin.getProperty("SubstitutionGroup") : //$NON-NLS-1$
                                    (CMNodeList)childEd.getProperty("SubstitutionGroup"); //$NON-NLS-1$

          if (cmNodeList != null && cmNodeList.getLength() > 1)
          {                                                 
            int replaceIndex = getIndex(parent, childElement);
            String childEdName = childEd.getNodeName();
            for (int i = 0; i < cmNodeList.getLength(); i++)
            {         
              CMNode substitution = cmNodeList.item(i);
              if (!substitution.getNodeName().equals(childEdName) && !Boolean.TRUE.equals(substitution.getProperty("Abstract"))) //$NON-NLS-1$
              {
                Action action = new Action(ModelQueryAction.REPLACE, parent, cmNodeList.item(i), replaceIndex, replaceIndex);
                actionList.add(action);
              }
            }
          }
        }
      }   
    }
  }     
           
  
  // returns true if the selected nodes are contiguous
  //  
  protected boolean isContiguous(Element parent, int[] range, List selectedNodeList)
  {         
    boolean result = true;
    NodeList nodeList = parent.getChildNodes();
	// issue: nodeListLength was never read, but in theory, 
	// nodelList.getLength() might cause some clearing of cached 
	// data, or something, so leaving in a potential meaningless call, for now.
    //int nodeListLength = nodeList.getLength();
	nodeList.getLength();
    for (int i = range[0]; i < range[1]; i++)
    {       
      Node node = nodeList.item(i);    
      if (!isWhitespaceNode(node) && !selectedNodeList.contains(node))
      {             
        result = false;
        break;
      }                       
    }         
    return result;
  }
 
 
  protected int[] getRange(Element parent, List list)
  {
    int[] result = null;
    int first = -1;
    int last = -1;                     

    NodeList nodeList = parent.getChildNodes();
    int nodeListLength = nodeList.getLength();
    for (int i = 0; i < nodeListLength; i++)
    {       
      Node node = nodeList.item(i);    
      if (list.contains(node))
      {             
        first = (first == -1) ? i : Math.min(first, i);        
        last = Math.max(last, i);
      }    
    }
   
    if (first != -1 && last!= -1)
    {             
      result = new int[2];
      result[0] = first;
      result[1] = last;
    }   
    return result;
  } 


  protected boolean isWhitespaceNode(Node node)
  {
    return node.getNodeType() == Node.TEXT_NODE &&
           node.getNodeValue().trim().length() == 0;
  } 


  protected int getIndex(Node parentNode, Node child)
  {
    NodeList nodeList = parentNode.getChildNodes();
    int index = -1;
    int size = nodeList.getLength();
    for (int i = 0; i < size; i++)
    {
      if (nodeList.item(i) == child)
      {
        index = i;
        break;
      }
    }
    return index;
  }                    


  protected boolean isActionContainingRange(ModelQueryAction action, int startIndex, int endIndex)
  {
    int actionStartIndex = action.getStartIndex();
    int actionEndIndex = action.getEndIndex();

    return (actionStartIndex <= startIndex &&
            actionEndIndex >= endIndex);
  } 
           

  protected boolean isActionMatchingRange(ModelQueryAction action, int startIndex, int endIndex)
  {
    int actionStartIndex = action.getStartIndex();
    int actionEndIndex = action.getEndIndex();
    return (actionStartIndex == startIndex &&        
            actionEndIndex == endIndex);
  } 
           

  protected void removeActionsNotContainingRange(List actionList, int startIndex, int endIndex)
  {
    for (int i = actionList.size() - 1; i >= 0; i--)
    {
      ModelQueryAction action = (ModelQueryAction)actionList.get(i);
      if (!isActionContainingRange(action, startIndex, endIndex))
      {
        actionList.remove(i);
      }
    }
  }


  protected void removeActionsNotMatchingRange(List actionList, int startIndex, int endIndex)
  {
    for (int i = actionList.size() - 1; i >= 0; i--)
    {
      ModelQueryAction action = (ModelQueryAction)actionList.get(i);
      if (!isActionMatchingRange(action, startIndex, endIndex))
      {
        actionList.remove(i);
      }
    }
  }


  public static class MatchModelVisitor
  {
    int indent;
    int elementIndex;
    Node parent;
    List actionList;

    public MatchModelVisitor(Node parent, List actionList)
    {
      this.parent = parent;
      this.actionList = actionList;
    }

    public int indexOfNextElement(int start)
    {
      NodeList nodeList = parent.getChildNodes();
      int length = nodeList.getLength();
      int result = length;
      for (int i = start; i < length; i++)
      {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
          result = i;
          break;
        }
      }
      return result;
    }

    public void visitMatchModelNode(CMValidator.MatchModelNode matchModelNode)
    {
      int startIndex = indexOfNextElement(elementIndex);

      //String cmNodeName = matchModelNode.cmNode != null ? matchModelNode.cmNode.getNodeName() : "null";
      //printIndented(indent, "+MatchModelNode : " + cmNodeName +  " " + startIndex);

      indent += 2;
      for (Iterator iterator = matchModelNode.children.iterator(); iterator.hasNext(); )
      {
        CMValidator.MatchModelNode child = (CMValidator.MatchModelNode)iterator.next();
        visitMatchModelNode(child);
      }
      indent -= 2;

      if (matchModelNode.cmNode != null)
      {
        int nodeType = matchModelNode.cmNode.getNodeType();
        if (nodeType == CMNode.GROUP)
        {
          CMGroup group = (CMGroup)matchModelNode.cmNode;
          if (group.getOperator() == CMGroup.CHOICE)
          {
            addReplaceActions(matchModelNode, group, startIndex, elementIndex - 1);
          }
        }
        else if (nodeType == CMNode.ELEMENT_DECLARATION)
        {
          elementIndex = startIndex + 1;
        }
        //printIndented(indent, "-MatchModelNode : " + cmNodeName +  " " + (elementIndex - 1));
      }
    }

    public void addReplaceActions(CMValidator.MatchModelNode matchModelNode, CMGroup group, int startIndex, int endIndex)
    {
      CMNode excludeCMNode = null;
      if (matchModelNode.children.size() > 0)
      {
        CMValidator.MatchModelNode child = (CMValidator.MatchModelNode)matchModelNode.children.get(0);
        excludeCMNode = child.cmNode;
      }

      CMNodeList nodeList = group.getChildNodes();
      int size = nodeList.getLength();
      for (int i = 0; i < size; i++)
      {
        CMNode alternative = nodeList.item(i);
        if (alternative != excludeCMNode)
        {
          Action action = new Action(ModelQueryAction.REPLACE, parent, alternative, startIndex, endIndex);
          actionList.add(action);
        }
      }
    }
  }

  //public static void printIndented(int indent, String string)
  //{
  //  for (int i = 0; i < indent; i++)
  //  {
  //    System.out.print(" ");
  //  }
  //  System.out.println(string);
  //}
}
