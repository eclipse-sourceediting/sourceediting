/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

public class CMDescriptionBuilder extends CMVisitor
{
  protected StringBuffer sb;
  protected CMNode root;
  protected boolean isRootVisited;

  public String buildDescription(CMNode node)
  {
    sb = new StringBuffer();
    root = node;
    isRootVisited = false;
    visitCMNode(node);
    return sb.toString();
  }       

  public void visitCMAnyElement(CMAnyElement anyElement)
  {
    sb.append("namespace:uri=\"" + anyElement.getNamespaceURI() + "\"");
  }

  public void visitCMDataType(CMDataType dataType)
  {
    sb.append("#PCDATA");
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
    int op = group.getOperator();
    if (op == CMGroup.ALL)
    {
      sb.append("all");
    }

    sb.append("(");

    String separator = ", ";

    if (op == CMGroup.CHOICE)
    {
      separator = " | ";
    }

    CMNodeList nodeList = group.getChildNodes();
    int size = nodeList.getLength();
    for (int i = 0; i < size; i++)
    {
      visitCMNode(nodeList.item(i));
      if (i < size - 1)
      {
        sb.append(separator);
      }
    }
    sb.append(")");
    addOccurenceSymbol(group);
  }

  public void visitCMElementDeclaration(CMElementDeclaration ed)
  {
    if (ed == root && !isRootVisited)
    {
      isRootVisited = true;
      CMContent content = ed.getContent();
      if (content != null)
      {
        if (content.getNodeType() != CMNode.GROUP)
        {
          sb.append("(");
          visitCMNode(content);
          sb.append(")");
        }
        else
        {
          visitCMNode(content);
        }
      }
    }
    else
    {
      sb.append(ed.getElementName());
      addOccurenceSymbol(ed);
    }
  }

  public void addOccurenceSymbol(CMContent content)
  {
    int min = content.getMinOccur();
    int max = content.getMaxOccur();
    if (min == 0)
    {
      if (max > 1 || max == -1)
      {
        sb.append("*");
      }
      else
      {
        sb.append("?");
      }
    }
    else if (max > 1 || max == -1)
    {
      sb.append("+");
    }
  }
}
