/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.actions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.xml.core.document.DOMModel;
import org.eclipse.wst.xml.core.document.DOMNode;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class MoveAction extends Action
{
  protected List selectedNodes;
  protected Node parentNode;
  protected Node refChild;

  /**
   * Constructor for DeleteAction.
   * @param text
   */
  public MoveAction(Node parentNode, List selectedNodes, Node refChild)
  {
    this.parentNode = parentNode;
    this.selectedNodes = selectedNodes;
    this.refChild = refChild;
  }    

  public MoveAction(XSDConcreteComponent parentComponent, List selectedComponents, XSDConcreteComponent refChildComponent)
  {
    selectedNodes = new ArrayList(selectedComponents.size());
    for (Iterator i = selectedComponents.iterator(); i.hasNext(); )
    {
      XSDConcreteComponent concreteComponent = (XSDConcreteComponent)i.next();
      selectedNodes.add(concreteComponent.getElement());
    }
    parentNode = parentComponent.getElement();
    refChild = refChildComponent != null ? refChildComponent.getElement() : null;
  }
                   
  public boolean canMove()
  {                           
    // TODO... there are likely more restriction to consider here
    boolean result = true;
    for (Iterator i = selectedNodes.iterator(); i.hasNext(); )
    {
      Node child = (Node)i.next();
      if (isDecendantOrSelf(child, parentNode)) 
      {
        result = false;
        break;
      }
    }   
    return result;
  }           
                  
  protected boolean isDecendantOrSelf(Node potentialParent, Node node)
  { 
    boolean result = false;
    while (node != null)
    {
      if (node == potentialParent)    
      {
        result = true;
        break;
      }           
      node = node.getParentNode();
    }
    return result;
  }


  protected void beginRecording()
  {
    DOMModel model = getModel();      
    if (model != null)
    {
      model.beginRecording(this, "Move");
    }
  }
  
  protected void endRecording()
  {
    DOMModel model = getModel();      
    if (model != null)
    {
      model.endRecording(this);    
    }
  }

  protected DOMModel getModel()
  {
    DOMModel model = null;
    if (parentNode instanceof DOMNode)
    {                            
      model = ((DOMNode)parentNode).getModel();
    }
    return model;
  }
  

     
  /*
   * @see IAction#run()
   */
  public void run()
  {                            
    beginRecording();
    try
    {
      for (Iterator i = selectedNodes.iterator(); i.hasNext(); )
      {
        Node child = (Node)i.next();
        repositionBefore(parentNode, child, refChild);
      }   
    }
    catch (Exception e)
    {         
      e.printStackTrace();
    }
    endRecording();
  }


  public void repositionBefore(Node parent, Node child, Node refChild)
  {   
    // TODO... when the refChild (inserting as the last element) we need to
    // special case the way we preserve indentation 
    Node oldParent = child.getParentNode();
    if (oldParent != null && refChild != child)
    {  
      // consider any indentation text node that preceeds the child
      //      
      Node textNode = isWhitespaceTextNode(child.getPreviousSibling()) ? child.getPreviousSibling() : null;

      // remove the child
      //
      oldParent.removeChild(child);
      
      // Instead of inserting the child immediatlely infront of the refChild, we first check to see if there
      // is an indentation text node preceeding the refChild.  If we find such a node, we perform the insertion
      // so that the child is inserted before the indentation text node.
      Node adjustedRefChild = refChild;
      if (refChild != null && isWhitespaceTextNode(refChild.getPreviousSibling()))
      {
        adjustedRefChild = refChild.getPreviousSibling();
      }
          
      // reposition the child and any indentation text node 
      //
      parent.insertBefore(child, adjustedRefChild);
      if (textNode != null)
      {
        oldParent.removeChild(textNode);
        parent.insertBefore(textNode, child);
      }
    }
  }     
    

  protected static boolean isWhitespaceTextNode(Node node)
  {
    boolean result = false;
    if (node != null && node.getNodeType() == Node.TEXT_NODE)
    {
      String data = ((Text)node).getData();
      result = (data == null || data.trim().length() == 0);
    }   
    return result;
  }
}
