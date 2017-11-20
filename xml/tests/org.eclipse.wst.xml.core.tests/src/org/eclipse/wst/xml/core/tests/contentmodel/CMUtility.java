/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xml.core.tests.contentmodel;

import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.util.CMVisitor;


public class CMUtility
{                                    
  /**
   * return true if the child or any of its ancestor group nodes are repeatable
   */
  public static boolean isNodeOrAncestorGroupRepeatable(CMContent content, CMNode child)
  {                                                                            
    // since we can't walk up the CMNode tree (why you ask? ... its a long story)
    // we walk down the tree to consider the child's parent node's
    //
    boolean result = isRepeatable(child);
    if (!result)
    {
      CanRepeatVisitor visitor = new CanRepeatVisitor(content, child);
      visitor.visitCMNode(content);
      result = visitor.getResult();
    }
    return result;
  }

  /**
   * return true if the child is repeatable
   */
  public static boolean isNodeRepeatable(CMContent content, CMNode child)
  {                                                                            
    // since we can't walk up the CMNode tree (why you ask? ... its a long story)
    // we walk down the tree to consider the child's parent node's
    //
    boolean result = isRepeatable(child);
    if (!result)
    {
      IsRepeatableVisitor visitor = new IsRepeatableVisitor(content, child);
      visitor.visitCMNode(content);
      result = visitor.getResult();
    }
    return result;
  }

  public static boolean isRepeatable(CMNode child)
  {                                
    boolean result = false;
    if (child instanceof CMContent)
    {
      CMContent content = (CMContent)child;
      result = content.getMaxOccur() > 1 || content.getMaxOccur() == -1;
    } 
    return result;
  }
    
   
  protected static class CanRepeatVisitor extends CMVisitor
  {                                             
    public boolean result;
    protected boolean isWithinRepeatableGroup;

    protected CMNode root;
    protected CMNode target;

    public CanRepeatVisitor(CMNode root, CMNode target)
    {
      this.root = root;
      this.target = target;
    }

    public void visitCMGroup(CMGroup group)
    {         
      boolean oldIsWithinRepeatableGroup = isWithinRepeatableGroup;
                                           
      isWithinRepeatableGroup = isRepeatable(group);
      
      super.visitCMGroup(group);

      isWithinRepeatableGroup = oldIsWithinRepeatableGroup;
    }                           

    public void visitCMElementDeclaration(CMElementDeclaration cmelement)
    {
      if (cmelement == target)
      {
        result = isWithinRepeatableGroup;
      }
    }

    public boolean getResult()
    {
      return result;
    }  
  }


  protected static class IsRepeatableVisitor extends CMVisitor
  {                                             
    public boolean result = false;
    protected CMGroup currentGroup;

    protected CMNode root;
    protected CMNode target;

    public IsRepeatableVisitor(CMNode root, CMNode target)
    {
      this.root = root;
      this.target = target;
    }

    public void visitCMGroup(CMGroup group)
    {         
      CMGroup previousGroup = currentGroup;
      currentGroup = group;
      super.visitCMGroup(group);
      currentGroup = previousGroup;
    }                           

    public void visitCMElementDeclaration(CMElementDeclaration cmelement)
    {
      if (cmelement == target)
      {
        if (currentGroup != null)
        {
          result = ((currentGroup.getOperator() == CMGroup.CHOICE) &&
                    (isRepeatable(currentGroup)));
        }
      }
    }

    public boolean getResult()
    {
      return result;
    }  
  }
}
