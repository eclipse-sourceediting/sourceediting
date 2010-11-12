/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.contentmodel.internal;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.factory.CMDocumentFactory;
import org.eclipse.xsd.XSDPackage;

/**
 *  This builder handles building .dtd / .xsd grammar files
 */
public class CMDocumentFactoryXSD implements CMDocumentFactory
{
  public static final String XSD_FILE_TYPE = "XSD";

  public CMDocumentFactoryXSD() 
  {  
    // here we call init on the XSD and DTD packages to avoid strange initialization bugs
    //
    XSDPackage.eINSTANCE.eClass();
    XSDPackage.eINSTANCE.getXSDFactory();  
  }

 
  public CMDocument createCMDocument(String uri)
  {                  	
    CMDocument result = null;
    try
    {                                
        result = XSDImpl.buildCMDocument(uri);     
    }
    catch (Exception e)
    {
    	e.printStackTrace();
    }
    return result;  
  } 
}
