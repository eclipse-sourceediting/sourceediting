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
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ModelQueryExtension
{  
  protected static final String[] EMPTY_STRING_ARRAY = {};
  
  public String[] getAttributeValues(Element ownerElement, String namespace, String name)
  {
    return EMPTY_STRING_ARRAY;
  }
  
  public String[] getElementValues(Node parentNode, String namespace, String name)
  {
    return EMPTY_STRING_ARRAY;
  }
  
  public boolean isApplicableChildElement(Node parentNode, String namespace, String name)
  {
    return true;
  }
}
