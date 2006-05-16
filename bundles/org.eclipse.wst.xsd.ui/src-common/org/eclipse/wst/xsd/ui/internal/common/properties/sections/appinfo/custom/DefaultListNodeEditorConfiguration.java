package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;


public class DefaultListNodeEditorConfiguration extends ListNodeEditorConfiguration
{
  private String[] values;
  
  public DefaultListNodeEditorConfiguration(String[] values)
  {
    this.values = values; 
  }
 
  public Object[] getValues(Object propertyObject)
  {
    return values;
  }
}
