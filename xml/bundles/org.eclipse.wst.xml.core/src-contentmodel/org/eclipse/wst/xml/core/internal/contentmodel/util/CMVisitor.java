/*******************************************************************************
 * Copyright (c) 2002, 2008 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.util.Stack;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

public class CMVisitor
{
  protected int indent = 0;
  protected Stack visitedCMGroupStack = new Stack();

  public void visitCMNode(CMNode node)
  {
    if (node != null)
    {
      //ContentModelManager.printlnIndented("visitCMNode : " + node.getNodeName() + " " + node);
      indent += 2;
      int nodeType = node.getNodeType();
      switch (nodeType)
      {
        case CMNode.ANY_ELEMENT :
        {
          visitCMAnyElement((CMAnyElement)node);
          break;
        }
        case CMNode.ATTRIBUTE_DECLARATION :
        {
          visitCMAttributeDeclaration((CMAttributeDeclaration)node);
          break;
        }
        case CMNode.DATA_TYPE :
        {
          visitCMDataType((CMDataType)node);
          break;
        }
        case CMNode.DOCUMENT :
        {
          visitCMDocument((CMDocument)node);
          break;
        }
        case CMNode.ELEMENT_DECLARATION :
        {
          visitCMElementDeclaration((CMElementDeclaration)node);
          break;
        }
        case CMNode.GROUP :
        {
          CMGroup group = (CMGroup)node;
          
          // This is to prevent recursion.
          if (visitedCMGroupStack.contains(group))
          {
            break;
          }
          
          // Push the current group to check later to avoid potential recursion
          visitedCMGroupStack.push(group);
          
          visitCMGroup(group);

          // Pop the current group
          visitedCMGroupStack.pop();
          break;
        }
      }
      indent -= 2;
    }
  }
      
  public void visitCMAnyElement(CMAnyElement anyElement)
  {
  }

  public void visitCMAttributeDeclaration(CMAttributeDeclaration ad)
  {
  }

  public void visitCMDataType(CMDataType dataType)
  {
  }

  public void visitCMDocument(CMDocument document)
  {
    CMNamedNodeMap map = document.getElements();
    int size = map.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(map.item(i));
    }
  }

  public void visitCMGroup(CMGroup group)
  {
    CMNodeList nodeList = group.getChildNodes();
    int size = nodeList.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(nodeList.item(i));
    }
  }

  public void visitCMElementDeclaration(CMElementDeclaration ed)
  {
    CMNamedNodeMap nodeMap = ed.getAttributes();
    int size = nodeMap.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(nodeMap.item(i));
    }

    visitCMNode(ed.getContent());

    visitCMDataType(ed.getDataType());
  }
}
