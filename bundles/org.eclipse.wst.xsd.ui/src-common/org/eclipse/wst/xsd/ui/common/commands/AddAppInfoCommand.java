package org.eclipse.wst.xsd.ui.common.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.SpecificationForAppinfoSchema;

public class AddAppInfoCommand extends Command
{
  protected SpecificationForAppinfoSchema appInfoSchemaSpec;

  protected AddAppInfoCommand(String label)
  {
    super(label);
  }

  public void setSchemaProperties(SpecificationForAppinfoSchema appInfoSchemaSpec)
  {
    this.appInfoSchemaSpec = appInfoSchemaSpec;
  }
}
