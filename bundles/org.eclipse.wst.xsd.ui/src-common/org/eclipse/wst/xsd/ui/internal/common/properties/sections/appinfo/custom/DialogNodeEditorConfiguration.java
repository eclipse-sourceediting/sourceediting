package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;

import org.eclipse.swt.graphics.Image;

public abstract class DialogNodeEditorConfiguration extends NodeEditorConfiguration
{
  public int getStyle()
  {        
    return STYLE_DIALOG;
  }
  
  public String getButonText()
  {
    return null;
  }
  
  public Image getButtonImage()
  {
    return null;
  }
  
  public abstract void invokeDialog();
}
