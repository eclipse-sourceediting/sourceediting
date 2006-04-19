package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public interface ExtensionItemEditManager
{
  public final static String STYLE_NONE = "none";   //$NON-NLS-1$
  public final static String STYLE_TEXT = "text"; //$NON-NLS-1$
  public final static String STYLE_COMBO = "combo"; //$NON-NLS-1$
  public final static String STYLE_CUSTOM = "custom";     //$NON-NLS-1$
  
  void handleEdit(Object item, Widget widget);
  String getTextControlStyle(Object item);
  String getButtonControlStyle(Object item);
  Control createCustomTextControl(Composite composite, Object item);
  Control createCustomButtonControl(Composite composite, Object item);  
}
