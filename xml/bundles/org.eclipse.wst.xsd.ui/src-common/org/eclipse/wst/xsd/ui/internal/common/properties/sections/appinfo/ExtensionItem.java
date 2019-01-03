/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom.NodeEditorConfiguration;

public abstract class ExtensionItem
{
  NodeEditorConfiguration propertyEditorConfiguration;

  public NodeEditorConfiguration getPropertyEditorConfiguration()
  {
    return propertyEditorConfiguration;
  }

  public void setPropertyEditorConfiguration(NodeEditorConfiguration propertyEditorConfiguration)
  {
    this.propertyEditorConfiguration = propertyEditorConfiguration;
  }  
  
  public abstract Command getUpdateValueCommand(String newValue);
}
