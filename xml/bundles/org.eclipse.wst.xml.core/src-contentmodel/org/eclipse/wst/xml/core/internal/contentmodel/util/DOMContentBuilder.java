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

import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.w3c.dom.Node;


public interface DOMContentBuilder
{
  public static final int BUILD_OPTIONAL_ATTRIBUTES = 1;
  public static final int BUILD_OPTIONAL_ELEMENTS = 1<<1;
  public static final int BUILD_FIRST_CHOICE = 1<<2;
  public static final int BUILD_TEXT_NODES = 1<<3;
  public static final int BUILD_FIRST_SUBSTITUTION = 1<<4;
  
  public static final int 
    BUILD_ONLY_REQUIRED_CONTENT =
      BUILD_FIRST_CHOICE
      | BUILD_TEXT_NODES;
  public static final int 
    BUILD_ALL_CONTENT = 
      BUILD_OPTIONAL_ATTRIBUTES 
      | BUILD_OPTIONAL_ELEMENTS 
      | BUILD_FIRST_CHOICE
      | BUILD_TEXT_NODES;
      
  public static final String PROPERTY_BUILD_BLANK_TEXT_NODES = "buildBlankTextNodes"; //$NON-NLS-1$
  
  public void setBuildPolicy(int buildPolicy);
  public int  getBuildPolicy();
  public void setProperty(String propertyName, Object value);
  public Object getProperty(String propertyName);
  public List getResult();
  public void build(Node parent, CMNode child);
  public void createDefaultRootContent(CMDocument cmDocument, CMElementDeclaration rootCMElementDeclaration) throws Exception;
  public void createDefaultRootContent(CMDocument cmDocument, CMElementDeclaration rootCMElementDeclaration, List namespaceInfoList) throws Exception;
  public void createDefaultContent(Node parent, CMElementDeclaration ed) throws Exception;
}
