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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.ui.properties.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class SimpleTypeUnionSection extends AbstractSection
{
  Text memberTypesText;
  Button button;

  /**
   * 
   */
  public SimpleTypeUnionSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		memberTypesText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		CLabel label = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES")); //$NON-NLS-1$
    button = getWidgetFactory().createButton(composite, "...", SWT.PUSH); //$NON-NLS-1$
    
    memberTypesText.addListener(SWT.Modify, this);
//    memberTypesText.addSelectionListener(this);

		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(95, 0);
		data.top = new FormAttachment(button, 0, SWT.CENTER);
		memberTypesText.setLayoutData(data);


		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(memberTypesText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(button, 0, SWT.CENTER);
		label.setLayoutData(data);

    button.addSelectionListener(this);
		data = new FormData();
		data.left = new FormAttachment(memberTypesText, +ITabbedPropertyConstants.HSPACE);
		data.right = new FormAttachment(100,0);
		// data.top = new FormAttachment(typeCombo, 0, SWT.CENTER);
		data.top = new FormAttachment(0,0);
		button.setLayoutData(data);
		
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
	  setListenerEnabled(false);
	  Object input = getInput();
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
	  memberTypesText.setText(""); //$NON-NLS-1$
	  if (input != null)
	  {
	    Element element = null;
	    if (input instanceof XSDSimpleTypeDefinition)
	    {
	      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
	      element = st.getElement();
        Element unionElement = (Element)domHelper.getChildNode(element, XSDConstants.UNION_ELEMENT_TAG);
        String memberTypes = unionElement.getAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);
        if (memberTypes != null)
        {
          memberTypesText.setText(memberTypes);
        }
	      
//        StringBuffer buf = new StringBuffer();	      
//	      if (st.getMemberTypeDefinitions().size() > 0)
//	      {
//	        for (Iterator i = st.getMemberTypeDefinitions().iterator(); i.hasNext(); )
//	        {
//	          String name = ((XSDSimpleTypeDefinition)i.next()).getQName(getSchema());
//	          if (name != null)
//	          {
//	            buf.append(name);
//	            buf.append(" ");
//	          }
//	        }
//	      }
//	      memberTypesText.setText(buf.toString());
	    }
	  }
	  setListenerEnabled(true);
	}

  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
   */
  public void widgetSelected(SelectionEvent e)
  {
    if (e.widget == button)
    {
	    Shell shell = Display.getCurrent().getActiveShell();
      
	    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)getInput();
	    Element element = st.getElement();
      SimpleContentUnionMemberTypesDialog dialog = new SimpleContentUnionMemberTypesDialog(shell, st);
      dialog.setBlockOnOpen(true);
      dialog.create();
      
      int result = dialog.open();
      if (result == Window.OK)
      {
        String newValue = dialog.getResult();
        beginRecording(XSDEditorPlugin.getXSDString("_UI_LABEL_MEMBERTYPES_CHANGE"), element); //$NON-NLS-1$
        Element unionElement = (Element)domHelper.getChildNode(element, XSDConstants.UNION_ELEMENT_TAG);
        unionElement.setAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, newValue);

        if (newValue.length() > 0)
        {
          unionElement.setAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE, newValue);
        }
        else
        {
          unionElement.removeAttribute(XSDConstants.MEMBERTYPES_ATTRIBUTE);  
        }
        endRecording(unionElement);

      }

      refresh();
    }
  }
  
  public void doHandleEvent(Event event)
  {
	  if (event.widget == memberTypesText)
	  {
	  }

  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }

}
