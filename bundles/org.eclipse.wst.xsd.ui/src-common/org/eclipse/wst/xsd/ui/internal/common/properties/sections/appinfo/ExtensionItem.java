/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
