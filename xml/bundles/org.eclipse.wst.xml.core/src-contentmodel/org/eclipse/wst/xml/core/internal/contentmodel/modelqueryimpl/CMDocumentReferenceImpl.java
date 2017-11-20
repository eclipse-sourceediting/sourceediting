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

import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.CMDocumentReference;

public class CMDocumentReferenceImpl implements CMDocumentReference
{              
  protected String systemId;
  protected String publicId;

  public CMDocumentReferenceImpl(String publicId, String systemId)
  {
    this.publicId = publicId;                                    
    this.systemId = systemId;
  }

  public String getPublicId()
  {                          
    return publicId;
  }

  public String getSystemId()
  {                          
    return systemId;
  }      
  
  public String toString()
  {
    return "[" + publicId + ", " + systemId + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
}
