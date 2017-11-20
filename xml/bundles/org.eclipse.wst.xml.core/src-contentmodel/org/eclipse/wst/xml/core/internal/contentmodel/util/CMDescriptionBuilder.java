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
    sb.append("namespace:uri=\"" + anyElement.getNamespaceURI() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public void visitCMDataType(CMDataType dataType)
  {
    sb.append("#PCDATA"); //$NON-NLS-1$
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
      sb.append("all"); //$NON-NLS-1$
    }

    sb.append("("); //$NON-NLS-1$

    String separator = ", "; //$NON-NLS-1$

    if (op == CMGroup.CHOICE)
    {
      separator = " | "; //$NON-NLS-1$
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
    
    sb.append(")"); //$NON-NLS-1$
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
          sb.append("("); //$NON-NLS-1$
          visitCMNode(content);
          sb.append(")"); //$NON-NLS-1$
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
        sb.append("*"); //$NON-NLS-1$
      }
      else
      {
        sb.append("?"); //$NON-NLS-1$
      }
    }
    else if (max > 1 || max == -1)
    {
      sb.append("+"); //$NON-NLS-1$
    }
  }
}
