package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class DOMExtensionItemEditManager implements ExtensionItemEditManager
{
  public void handleEdit(Object item, Widget widget)
  {
    if (item instanceof DOMExtensionItem)
    {
      DOMExtensionItem extensionItem = (DOMExtensionItem)item;
      String value = null;
      if (widget instanceof Text)
      {
        Text text = (Text)widget;
        value = text.getText();    
      }
      else if (widget instanceof CCombo)
      {
        CCombo combo = (CCombo)widget;
        int index = combo.getSelectionIndex();
        if (index != -1)
        {  
          value = combo.getItem(index);
        }  
      }       
      if (value != null)
      {  
        Command command = extensionItem.getUpdateValueCommand(value);
        if (command != null)
        {
          // TODO (cs) add command stack handling stuff
          command.execute();
        }
      }  
    }
  }

  public Control createCustomButtonControl(Composite composite, Object item)
  {
    Button button = new Button(composite, SWT.NONE);
    button.setText("...");
    return button;
  }

  public Control createCustomTextControl(Composite composite, Object item)
  {
    return null;
  }

  public String getButtonControlStyle(Object object)
  {
    /*
    DOMExtensionItem item = (DOMExtensionItem)object;
    if (item.getName().startsWith("n"))
    {  
      return ExtensionItemEditManager.STYLE_CUSTOM;
    }*/
    return ExtensionItemEditManager.STYLE_NONE;
  }

  public String getTextControlStyle(Object object)
  {
    DOMExtensionItem item = (DOMExtensionItem)object;
    String[] values = item.getPossibleValues();
    
    if (values != null && values.length > 1)
    {
      return ExtensionItemEditManager.STYLE_COMBO;      
    } 
    return ExtensionItemEditManager.STYLE_TEXT;
  }
}
