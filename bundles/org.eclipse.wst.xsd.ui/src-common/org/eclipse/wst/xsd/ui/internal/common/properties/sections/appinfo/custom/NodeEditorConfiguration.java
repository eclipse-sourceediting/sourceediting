package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.custom;

public abstract class NodeEditorConfiguration
{  
  public final static int STYLE_NONE = 0;   
  public final static int STYLE_TEXT = 1; 
  public final static int STYLE_COMBO = 2;
  public final static int STYLE_DIALOG = 4;   
  
  public abstract int getStyle();
}
