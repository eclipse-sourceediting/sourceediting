/*******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery;

import java.util.List;

import org.w3c.dom.Node;
 
/**
 * The interface is used to abstract the task of computing the document references associated with a DOM
 * or a particular node within the DOM.  
 */
public interface CMDocumentReferenceProvider
{
  List getCMDocumentReferences(Node node, boolean deep);
  String resolveGrammarURI(String publicId, String systemId);
}
