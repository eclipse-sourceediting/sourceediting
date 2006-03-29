package org.eclipse.wst.xsd.ui.common.commands;

import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.wst.xsd.ui.common.properties.sections.appinfo.SpecificationForAppinfoSchema;
import org.eclipse.xsd.XSDConcreteComponent;

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
