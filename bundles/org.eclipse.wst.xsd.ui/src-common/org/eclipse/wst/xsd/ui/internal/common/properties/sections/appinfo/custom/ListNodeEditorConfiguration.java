package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;

import org.eclipse.jface.viewers.LabelProvider;

public abstract class ListNodeEditorConfiguration extends NodeEditorConfiguration
{
  private LabelProvider labelProvider;
 
  public LabelProvider getLabelProvider()
  {
    return labelProvider;
  }
  
  public int getStyle()
  {
    return STYLE_COMBO;
  }    

  public void setLabelProvider(LabelProvider labelProvider)
  {
    this.labelProvider = labelProvider;
  }

  public abstract Object[] getValues(Object propertyObject);
}
