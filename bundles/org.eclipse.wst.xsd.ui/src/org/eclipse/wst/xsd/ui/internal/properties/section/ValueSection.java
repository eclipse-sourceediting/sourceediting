/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.properties.section;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.ui.properties.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class ValueSection extends AbstractSection
{
  Text valueText;
  /**
   * 
   */
  public ValueSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		Composite composite =	getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		valueText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		valueText.setLayoutData(data);
		valueText.addListener(SWT.Modify, this);

		CLabel valueLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_VALUE") + ":"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(valueText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(valueText, 0, SWT.CENTER);
		valueLabel.setLayoutData(data);
		
//		listener.startListeningForEnter(valueText);
//		listener.startListeningTo(valueText);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
	  setListenerEnabled(false);
	  Object input = getInput();
	  valueText.setText(""); //$NON-NLS-1$
	  if (input != null)
	  {
	    if (input instanceof XSDEnumerationFacet)
	    {
	      XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)input;
	      Element element = enumFacet.getElement();
        String value = element.getAttribute(XSDConstants.VALUE_ATTRIBUTE);
        
        if (value != null)
        {
          valueText.setText(value);
        }
	    }
	  }
	  setListenerEnabled(true);
	}
	
  public void doHandleEvent(Event event)
  {
    if (event.widget == valueText)
    {
      Object input = getInput();
	    if (input instanceof XSDEnumerationFacet)
	    {
	      XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)input;
	      Element element = enumFacet.getElement();
        
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ENUM_VALUE_CHANGE"), element); //$NON-NLS-1$
        String value = valueText.getText();
        element.setAttribute(XSDConstants.VALUE_ATTRIBUTE, value);
        endRecording(element);
	    }
    }
  }


  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return false;
  }

}
