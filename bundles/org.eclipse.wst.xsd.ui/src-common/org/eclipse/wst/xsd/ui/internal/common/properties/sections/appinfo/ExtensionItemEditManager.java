package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public interface ExtensionItemEditManager
{
  public final static String STYLE_NONE = "none";  
  public final static String STYLE_TEXT = "text";
  public final static String STYLE_COMBO = "combo";
  public final static String STYLE_CUSTOM = "custom";    
  
  void handleEdit(Object item, Widget widget);
  String getTextControlStyle(Object item);
  String getButtonControlStyle(Object item);
  Control createCustomTextControl(Composite composite, Object item);
  Control createCustomButtonControl(Composite composite, Object item);  
}
