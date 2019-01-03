/*******************************************************************************
 * Copyright (c) 2002, 2008 IBM Corporation and others.
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
package org.eclipse.wst.xml.core.internal.contentmodel.modelquery.extension;

import java.util.List;

import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMNode;
import org.w3c.dom.Element;



public interface ModelQueryExtensionManager
{
  List getDataTypeValues(Element element, CMNode cmNode);                             

  void filterAvailableElementContent(List availableContent, Element element, CMElementDeclaration ed);

  void filterAvailableElementContent(List availableContent, Element element, CMElementDeclaration ed, int includeOptions);
}
