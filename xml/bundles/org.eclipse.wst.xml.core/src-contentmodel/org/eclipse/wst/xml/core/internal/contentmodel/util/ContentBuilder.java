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
package org.eclipse.wst.xml.core.internal.contentmodel.util;

import java.util.Vector;

import org.eclipse.wst.xml.core.internal.contentmodel.CMAnyElement;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMContent;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMGroup;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNodeList;

                   
/**
 * This class lets you traverse a 'CM' model providing callbacks to build content.
 */
public class ContentBuilder extends CMVisitor
{
  public static final int BUILD_ALL_CONTENT = 1;
  public static final int BUILD_ONLY_REQUIRED_CONTENT = 2;
  protected int buildPolicy = BUILD_ALL_CONTENT;

  protected boolean alwaysVisit;
  protected Vector visitedCMElementDeclarationList = new Vector();

  public ContentBuilder()
  {
  }
                
  public void setBuildPolicy(int buildPolicy)
  {
    this.buildPolicy = buildPolicy;
  }

  public int getBuildPolicy()
  {
    return buildPolicy;
  }                
           
  protected void createAnyElementNode(CMAnyElement anyElement)
  {
  }

  protected void createElementNodeStart(CMElementDeclaration ed)
  {      
  }

  protected void createElementNodeEnd(CMElementDeclaration ed)
  {
  }

  protected void createTextNode(CMDataType dataType)
  {
  } 

  protected void createAttributeNode(CMAttributeDeclaration attribute)
  {
  } 
 
  public void visitCMElementDeclaration(CMElementDeclaration ed)
  {
    int forcedMin = (buildPolicy == BUILD_ALL_CONTENT || alwaysVisit) ? 1 : 0;
    int min = Math.max(ed.getMinOccur(), forcedMin);                          
    alwaysVisit = false;

    if (min > 0 && !visitedCMElementDeclarationList.contains(ed))
    {
      visitedCMElementDeclarationList.add(ed);
      for (int i = 1; i <= min; i++)
      {       
        createElementNodeStart(ed);       
        
        // instead of calling super.visitCMElementDeclaration()
        // we duplicate the code with some minor modifications
        CMNamedNodeMap nodeMap = ed.getAttributes();
        int size = nodeMap.getLength();
        for (int j = 0; j < size; j++)
        {
          visitCMNode(nodeMap.item(j));
        }

        CMContent content = ed.getContent();
        if (content != null)
        {
          visitCMNode(content);
        }

        if (ed.getContentType() == CMElementDeclaration.PCDATA)
        {
          CMDataType dataType = ed.getDataType();
          if (dataType != null)
          {
            visitCMDataType(dataType);
          }
        }
        // end duplication
        createElementNodeEnd(ed);  
      }
      int size = visitedCMElementDeclarationList.size();
      visitedCMElementDeclarationList.remove(size - 1);
    }
  }
    

  public void visitCMDataType(CMDataType dataType)
  {
    createTextNode(dataType);   
  }


  public void visitCMGroup(CMGroup e)
  { 
    int forcedMin = (buildPolicy == BUILD_ALL_CONTENT || alwaysVisit) ? 1 : 0;
    int min = Math.max(e.getMinOccur(), forcedMin);                          
    alwaysVisit = false;

    for (int i = 1; i <= min; i++)
    {   
      if (e.getOperator() == CMGroup.CHOICE)
      {
        // add only 1 element from the group
        // todo... perhaps add something other than the first one        
        CMNodeList nodeList = e.getChildNodes();
        if (nodeList.getLength() > 0)
        {
          visitCMNode(nodeList.item(0));
        }
      }
      else // SEQUENCE, ALL
      {
        // visit all of the content
        super.visitCMGroup(e);
      }
    }
  } 

  public void visitCMAttributeDeclaration(CMAttributeDeclaration ad)
  {
    if (alwaysVisit ||
        buildPolicy == BUILD_ALL_CONTENT ||
        ad.getUsage() == CMAttributeDeclaration.REQUIRED)
    {
      createAttributeNode(ad);      
    }
  }                     
  

  public void visitCMAnyElement(CMAnyElement anyElement)
  {  
    int forcedMin = (buildPolicy == BUILD_ALL_CONTENT || alwaysVisit) ? 1 : 0;
    alwaysVisit = false; 
    int min = Math.max(anyElement.getMinOccur(), forcedMin);                          
    for (int i = 1; i <= min; i++)
    {                                
      createAnyElementNode(anyElement);
    }
  }     
}
