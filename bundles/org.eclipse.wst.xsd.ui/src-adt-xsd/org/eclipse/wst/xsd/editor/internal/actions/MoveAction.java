/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.editor.internal.actions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class MoveAction extends Action
{
  protected List selectedNodes;
  protected Node parentNode;
  protected Node previousRefChild, nextRefChild;
  boolean doInsertBefore;
  
  List selectedComponentsList;
  XSDModelGroup parentModelGroup;
  XSDConcreteComponent previousRefComponent, nextRefComponent;

  public MoveAction(XSDModelGroup parentComponent, List selectedComponents, XSDConcreteComponent previousRefChildComponent, XSDConcreteComponent nextRefChildComponent)
  {
	this.parentModelGroup = parentComponent;
	this.selectedComponentsList = selectedComponents;
	this.previousRefComponent = previousRefChildComponent;
	this.nextRefComponent = nextRefChildComponent;
	  
    selectedNodes = new ArrayList(selectedComponents.size());
    for (Iterator i = selectedComponents.iterator(); i.hasNext(); )
    {
      XSDConcreteComponent concreteComponent = (XSDConcreteComponent)i.next();
      selectedNodes.add(concreteComponent.getElement());
    }
    parentNode = parentComponent.getElement();
    nextRefChild = nextRefChildComponent != null ? nextRefChildComponent.getElement() : null;
    previousRefChild = previousRefChildComponent != null ? previousRefChildComponent.getElement() : null;
    
    doInsertBefore = (nextRefChild != null);
    if (nextRefComponent != null)
    {
      if (nextRefComponent.getContainer().getContainer() == parentModelGroup)
      {
    	doInsertBefore = true;
      }
    }
    if (previousRefComponent != null)
    {
      if (previousRefComponent.getContainer().getContainer() == parentModelGroup)
      {
    	doInsertBefore = false;
      }
    }
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

  protected IDOMModel getModel()
  {
    IDOMModel model = null;
    if (parentNode instanceof IDOMNode)
    {                            
      model = ((IDOMNode)parentNode).getModel();
    }
    return model;
  }
  

     
  /*
   * @see IAction#run()
   */
  public void run()
  {                            
    try
    {
      for (Iterator i = selectedComponentsList.iterator(); i.hasNext(); )
      {
 	    XSDConcreteComponent concreteComponent = (XSDConcreteComponent)i.next();
 	    
        if (doInsertBefore)
        {
          if (concreteComponent == nextRefComponent)
            continue;
        }
        else
        {
          if (concreteComponent == previousRefComponent)
            continue;
        }
 	    
 	    
 	    for (Iterator particles = parentModelGroup.getContents().iterator(); particles.hasNext(); )
 	    {
 	      XSDParticle particle = (XSDParticle) particles.next();
 	      XSDParticleContent particleContent = particle.getContent();
 	      if (particleContent == concreteComponent)
 	      {
 	    	parentModelGroup.getContents().remove(particle);
 	    	break;
 	      }
 	    }
 	    int index = 0;
 	    List particles = parentModelGroup.getContents();
 	    for (Iterator iterator = particles.iterator(); iterator.hasNext(); )
 	    {
 	      XSDParticle particle = (XSDParticle) iterator.next();
 	      XSDParticleContent particleContent = particle.getContent();
 	      if (doInsertBefore)
 	      {
  	        if (particleContent == nextRefComponent)
 	        {
  	          parentModelGroup.getContents().add(index, (XSDParticle)concreteComponent.getContainer());
  	          break;
 	        }
 	      }
 	      else
 	      {
 	    	if (particleContent == previousRefComponent)
 	    	{
 	    	  parentModelGroup.getContents().add(index + 1, (XSDParticle)concreteComponent.getContainer());
 	    	 break;
 	    	}
 	      }
 	      index ++;
 	    }
 	    if (particles.size() == 0)
 	    {
      	  parentModelGroup.getContents().add((XSDParticle)concreteComponent.getContainer());
 	    }

      }
    }
    catch (Exception e)
    {         
      e.printStackTrace();
    }
  }


  public void repositionBefore(Node child)
  {   
    // TODO... when the refChild (inserting as the last element) we need to
    // special case the way we preserve indentation 
    Node oldParent = child.getParentNode();
    if (oldParent != null && nextRefChild != child)
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
      Node adjustedRefChild = nextRefChild;
      if (nextRefChild != null && isWhitespaceTextNode(nextRefChild.getPreviousSibling()))
      {
        adjustedRefChild = nextRefChild.getPreviousSibling();
      }
          
      // reposition the child and any indentation text node 
      //
      if (doInsertBefore)
      {
        try {
        parentNode.insertBefore(child, adjustedRefChild);
        if (textNode != null)
        {
          oldParent.removeChild(textNode);
          parentNode.insertBefore(textNode, child);
        }
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
      }
      else
      {
    	adjustedRefChild =  previousRefChild.getNextSibling();
    	parentNode.insertBefore(child, adjustedRefChild);
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
