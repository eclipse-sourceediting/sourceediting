package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.SpecificationForExtensionsSchema;

public class AddExtensionCommand extends Command
{
  protected SpecificationForExtensionsSchema extensionsSchemaSpec;

  protected AddExtensionCommand(String label)
  {
    super(label);
  }

  public void setSchemaProperties(SpecificationForExtensionsSchema appInfoSchemaSpec)
  {
    this.extensionsSchemaSpec = appInfoSchemaSpec;
  }
  
  public Object getNewObject()
  {
    return null;
  }
}
