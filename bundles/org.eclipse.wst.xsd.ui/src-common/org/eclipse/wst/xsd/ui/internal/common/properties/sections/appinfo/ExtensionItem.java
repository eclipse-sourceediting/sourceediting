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
