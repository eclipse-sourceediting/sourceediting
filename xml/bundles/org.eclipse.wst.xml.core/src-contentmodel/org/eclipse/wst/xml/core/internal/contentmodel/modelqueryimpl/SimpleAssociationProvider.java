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

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQueryCMProvider;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *
 */
public class SimpleAssociationProvider extends BaseAssociationProvider
{
  protected ModelQueryCMProvider modelQueryCMProvider;
          
  public SimpleAssociationProvider(ModelQueryCMProvider modelQueryCMProvider)
  {                                              
    this.modelQueryCMProvider = modelQueryCMProvider;
  }

  public CMDocument getCorrespondingCMDocument(Node node)
  {
    return modelQueryCMProvider.getCorrespondingCMDocument(node);
  }
  
  public CMElementDeclaration getCMElementDeclaration(Element element)
  {
    CMElementDeclaration result = null;
    CMDocument cmDocument = getCorrespondingCMDocument(element);
    if (cmDocument != null)
    {        
      result = (CMElementDeclaration)cmDocument.getElements().getNamedItem(element.getNodeName());    
    }
    return result;
  }
}
