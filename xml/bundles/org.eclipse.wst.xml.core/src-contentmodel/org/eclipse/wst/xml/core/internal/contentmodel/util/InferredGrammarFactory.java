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

import java.util.Collection;

import org.eclipse.wst.xml.core.internal.contentmodel.CMDocument;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.w3c.dom.Element;


// this interface is used to build a grammar document given a local file name
//
public interface InferredGrammarFactory
{   
  public CMDocument createCMDocument(String uri); 
  public CMElementDeclaration createCMElementDeclaration(CMDocument cmDocument, Element element, boolean isLocal);
  public void createCMContent(CMDocument parentCMDocument, CMElementDeclaration parentEd, CMDocument childCMDocument, CMElementDeclaration childEd, boolean isLocal, String uri);
  public void debugPrint(Collection collection); 
}
