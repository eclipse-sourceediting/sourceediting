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
package org.eclipse.wst.sse.core.internal.contentmodel.modelquery;

import org.eclipse.wst.sse.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.sse.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.sse.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.sse.core.internal.contentmodel.CMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 * 
 *
 */
public interface ModelQueryAssociationProvider extends ModelQueryCMProvider
{
  public CMNode getCMNode(Node node);
  public CMDataType getCMDataType(Text text); 
  public CMAttributeDeclaration getCMAttributeDeclaration(Attr attr);
  public CMElementDeclaration getCMElementDeclaration(Element element);
}
