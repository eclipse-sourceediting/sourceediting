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

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDProcessContents;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class NamespaceProcessContentsSection extends AbstractSection
{
  CCombo namespaceCombo;
  CCombo processContentsCombo;
  
  private String[] namespaceComboValues = {
      "",               //$NON-NLS-1$
      "##any",          //$NON-NLS-1$
      "##other",        //$NON-NLS-1$
      "##targetNamespace", //$NON-NLS-1$
      "##local" //$NON-NLS-1$
  };

  /**
   * 
   */
  public NamespaceProcessContentsSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent,TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		Composite composite =	getWidgetFactory().createFlatFormComposite(parent);
		FormData data;
		

		namespaceCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(0, 0);
		namespaceCombo.setLayoutData(data);
//		Iterator list = XSDNamespaceConstraintCategory.VALUES.iterator();
//		while (list.hasNext())
//		{
//		  namespaceCombo.add(((XSDNamespaceConstraintCategory)list.next()).getName());
//		}
		namespaceCombo.setItems(namespaceComboValues);
		namespaceCombo.addSelectionListener(this);

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, XSDConstants.NAMESPACE_ATTRIBUTE);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(namespaceCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(namespaceCombo, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);

		processContentsCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
		data = new FormData();
		data.left = new FormAttachment(0, 100);
		data.right = new FormAttachment(100, -rightMarginSpace - ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(namespaceCombo, +ITabbedPropertyConstants.VSPACE);
		processContentsCombo.setLayoutData(data);
		Iterator list = XSDProcessContents.VALUES.iterator();
		processContentsCombo.add(""); //$NON-NLS-1$
		while (list.hasNext())
		{
		  processContentsCombo.add(((XSDProcessContents)list.next()).getName());
		}
		processContentsCombo.addSelectionListener(this);

		CLabel processContentsLabel = getWidgetFactory().createCLabel(composite, XSDConstants.PROCESSCONTENTS_ATTRIBUTE);
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(processContentsCombo, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(processContentsCombo, 0, SWT.CENTER);
		processContentsLabel.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
//	  namespaceCombo.removeSelectionListener(this);
//	  processContentsCombo.removeSelectionListener(this);
	  namespaceCombo.setText(""); //$NON-NLS-1$
	  processContentsCombo.setText(""); //$NON-NLS-1$
	  Object input = getInput();
	  if (input != null)
	  {
	    if (input instanceof XSDWildcard)
	    {
	      XSDWildcard wildcard = (XSDWildcard)input;
	      List listNS = wildcard.getNamespaceConstraint();
	      if (wildcard.isSetLexicalNamespaceConstraint())
	      {
	        namespaceCombo.setText(wildcard.getStringLexicalNamespaceConstraint());
	      }
	      else
	      {
	        namespaceCombo.setText("");
	      }
	      if (wildcard.isSetProcessContents())
	      {
	        XSDProcessContents pc = wildcard.getProcessContents();
	        processContentsCombo.setText(pc.getName());
	      }
	    }
	  }
//	  namespaceCombo.addSelectionListener(this);
//	  processContentsCombo.addSelectionListener(this);
	}
	
  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  public void widgetSelected(SelectionEvent e)
  {
    XSDConcreteComponent concreteComponent = (XSDConcreteComponent)getInput();
    Element element = concreteComponent.getElement();
    if (concreteComponent instanceof XSDWildcard)
    {
      XSDWildcard wildcard =  (XSDWildcard)concreteComponent;
      if (e.widget == namespaceCombo)
      {
        String newValue = namespaceCombo.getText();
        boolean removeAttribute = false;
        if (newValue.length() == 0)
        {
          removeAttribute = true;
        }

        beginRecording(XSDEditorPlugin.getXSDString("_UI_NAMESPACE_CHANGE"), element); //$NON-NLS-1$
        if (removeAttribute)
        {
          wildcard.unsetLexicalNamespaceConstraint();
        }
        else
        {
          wildcard.setStringLexicalNamespaceConstraint(newValue);
        }
        endRecording(element);
      }
      else if (e.widget == processContentsCombo)
      {
        String newValue = processContentsCombo.getText();
        boolean removeAttribute = false;
        if (newValue.length() == 0)
        {
          removeAttribute = true;
        }
        beginRecording(XSDEditorPlugin.getXSDString("_UI_PROCESSCONTENTS_CHANGE"), element); //$NON-NLS-1$
        if (removeAttribute)
        {
          wildcard.unsetProcessContents();
        }
        else
        {
          wildcard.setProcessContents(XSDProcessContents.get(processContentsCombo.getItem(processContentsCombo.getSelectionIndex())));
        }
        endRecording(element);
      }
    }
    refresh();
  }

}
