package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


/**
 * @deprecated
 */
public class DOMExtensionItemEditManager implements ExtensionItemEditManager
{
  public void handleEdit(Object item, Widget widget)
  {   
  }

  public Control createCustomButtonControl(Composite composite, Object item)
  {
    Button button = new Button(composite, SWT.NONE);
    button.setText("..."); //$NON-NLS-1$
    return button;
  }

  public Control createCustomTextControl(Composite composite, Object item)
  {
    return null;
  }

  public String getButtonControlStyle(Object object)
  {
    return ExtensionItemEditManager.STYLE_NONE;
  }

  public String getTextControlStyle(Object object)
  {
    return ExtensionItemEditManager.STYLE_NONE;
  }
}
