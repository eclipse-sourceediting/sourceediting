package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class ExtensionDetailsViewer extends Viewer
{
  private final static String ITEM_DATA = "ITEM_DATA"; //$NON-NLS-1$
  Composite control;  
  Composite composite;
  ExtensionDetailsContentProvider contentProvider;
  TabbedPropertySheetWidgetFactory widgetFactory;  
  InternalFocusListener internalFocusListener;
  ExtensionItemEditManager editManager;
  
  public ExtensionDetailsViewer(Composite parent, TabbedPropertySheetWidgetFactory widgetFactory)
  {
    this.widgetFactory = widgetFactory;    
    control =  widgetFactory.createComposite(parent);
    internalFocusListener = new InternalFocusListener();
    control.setLayout(new GridLayout());    
  }
  public Control getControl()
  {
    return control;
  }
  

  public Object getInput()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public ISelection getSelection()
  {
    // TODO Auto-generated method stub
    return null;
  }

  public void refresh()
  {
    // TODO Auto-generated method stub
    
  }

  public void setInput(Object input)
  { 
    // TODO (cs) add assertions
    //
    if (editManager == null)
      return;
    
    if (contentProvider == null)
      return;
    
    if (composite != null)
    {/*
      for (Iterator i = controlsThatWeAreListeningTo.iterator(); i.hasNext(); )
      {
        Control control = (Control)i.next();       
        if (control != null)
        {  
          control.removeFocusListener(internalFocusListener);
        }  
      } */ 
      composite.dispose();       
    }   

    composite = widgetFactory.createComposite(control);
    composite.setBackground(ColorConstants.white);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    composite.setLayout(gridLayout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    Object[] items = contentProvider.getItems(input);      

    for (int i = 0; i < items.length; i++)
    {
      Object item = items[i];
      String name = contentProvider.getName(item);
      String value = contentProvider.getValue(item);
      Label label = widgetFactory.createLabel(composite, name + ":"); //$NON-NLS-1$
      label.setLayoutData(new GridData());
      
      Control control = null;
      String style = editManager.getTextControlStyle(item);

      if (style == ExtensionItemEditManager.STYLE_COMBO)
      {
        CCombo combo = widgetFactory.createCCombo(composite);
        combo.setText(value);
        String[] values = contentProvider.getPossibleValues(item);       
        for (int j = 0; j < values.length; j++)
        {  
          combo.add(values[j]);
        }   
        control = combo;        
      }     
      else if (style == ExtensionItemEditManager.STYLE_CUSTOM)
      {
        control = editManager.createCustomTextControl(composite, item);
      }  
      else // (style == ExtensionItemEditManager.STYLE_TEXT)
      {  
        Text text = widgetFactory.createText(composite,value);
        control = text;
      }      
      control.setData(ITEM_DATA, item);
      control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));      
      control.addFocusListener(internalFocusListener);      
      
      String buttongStyle = editManager.getButtonControlStyle(item);
      if (buttongStyle == ExtensionItemEditManager.STYLE_CUSTOM)
      {
        editManager.createCustomButtonControl(composite, item);
      }
      else
      {
        Control placeHolder = new Label(composite, SWT.NONE);
        placeHolder.setVisible(false);
        placeHolder.setEnabled(false);
        placeHolder.setLayoutData(new GridData()); 
      }  
    }  
    control.layout(true);    
  }

  public void setSelection(ISelection selection, boolean reveal)
  {
    // TODO Auto-generated method stub
    
  }
  public ExtensionDetailsContentProvider getContentProvider()
  {
    return contentProvider;
  }
  public void setContentProvider(ExtensionDetailsContentProvider contentProvider)
  {
    this.contentProvider = contentProvider;
  }
  
  class InternalFocusListener implements FocusListener
  {
    public void focusGained(FocusEvent e)
    {
    }    
    
    public void focusLost(FocusEvent e)
    {
      if (editManager != null)
      {  
        Object item = e.widget.getData(ITEM_DATA);
        if (item != null)
        {
          editManager.handleEdit(item, e.widget);
        }          
      }
    }
  }

  public ExtensionItemEditManager getEditManager()
  {
    return editManager;
  }
  public void setEditManager(ExtensionItemEditManager editManager)
  {
    this.editManager = editManager;
  }
}
