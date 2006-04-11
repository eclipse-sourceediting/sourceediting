package org.eclipse.wst.xsd.ui.internal.common.commands;

import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.SpecificationForExtensionsSchema;
import org.eclipse.xsd.XSDConcreteComponent;

public class AddAppInfoCommand extends Command
{
  protected SpecificationForExtensionsSchema extensionsSchemaSpec;

  protected AddAppInfoCommand(String label)
  {
    super(label);
  }

  public void setSchemaProperties(SpecificationForExtensionsSchema appInfoSchemaSpec)
  {
    this.extensionsSchemaSpec = appInfoSchemaSpec;
  }
  
  protected String createUniquePrefix(XSDConcreteComponent component)
  {
    String prefix = "sdo";
    Map prefMapper = component.getSchema().getQNamePrefixToNamespaceMap();
    if ( prefMapper.get(prefix) != null){
      int i = 1;
      while ( prefMapper.get(prefix + i) != null)
        i++;
      prefix += i;
    }
    return prefix;
  }
}
