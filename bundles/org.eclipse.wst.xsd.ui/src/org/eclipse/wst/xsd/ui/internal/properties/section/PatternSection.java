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
import org.eclipse.jface.wizard.WizardDialog;
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
import org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertyConstants;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.wizards.RegexWizard;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class PatternSection extends AbstractSection
{
  /**
   * 
   */
  public PatternSection()
  {
    super();
  }
  
  Text patternText;
  Button button;

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{

		super.createControls(parent, factory);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		patternText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		CLabel nameLabel = getWidgetFactory().createCLabel(composite, XSDEditorPlugin.getXSDString("_UI_VALUE") + ":"); //$NON-NLS-1$
    button = getWidgetFactory().createButton(composite, "", SWT.PUSH); //$NON-NLS-1$
    button.setImage(XSDEditorPlugin.getXSDImage("icons/browsebutton.gif")); //$NON-NLS-1$
    
    patternText.addListener(SWT.Modify, this);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(patternText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(button, 0, SWT.CENTER);
		nameLabel.setLayoutData(data);

    button.addSelectionListener(this);
		data = new FormData();
		data.left = new FormAttachment(100, -rightMarginSpace + 2);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(patternText, 0, SWT.CENTER);
		button.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(0, 100);
    data.right = new FormAttachment(button, 0);
    patternText.setLayoutData(data);
	}

	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
	  setListenerEnabled(false);
	  Object input = getInput();
	  patternText.setText(""); //$NON-NLS-1$
	  if (input != null)
	  {
	    Element element = null;
	    if (input instanceof XSDPatternFacet)
	    {
	      element = ((XSDPatternFacet)input).getElement();
	    }
	    if (element != null)
	    {
        String result = element.getAttribute(XSDConstants.VALUE_ATTRIBUTE);
        if (result != null)
        {
          patternText.setText(result);
        }
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
	    Element element = ((XSDPatternFacet)getInput()).getElement();

      String initialValue = element.getAttribute(XSDConstants.VALUE_ATTRIBUTE);
      if (initialValue == null)
      {
        initialValue = ""; //$NON-NLS-1$
      }
      RegexWizard wizard = new RegexWizard(initialValue);

      WizardDialog wizardDialog = new WizardDialog(shell, wizard);
      wizardDialog.setBlockOnOpen(true);
      wizardDialog.create();
      
      int result = wizardDialog.open();

      if (result == Window.OK)
      {
        String newPattern = wizard.getPattern();
        beginRecording(XSDEditorPlugin.getXSDString("_UI_PATTERN_VALUE_CHANGE"), element); //$NON-NLS-1$
        element.setAttribute(XSDConstants.VALUE_ATTRIBUTE, newPattern);
        ((XSDPatternFacet)getInput()).setLexicalValue(newPattern);
        endRecording(element);
      }

      refresh();
    }
  }
  
  public void doHandleEvent(Event event)
  {
	  if (event.widget == patternText)
	  {
		  XSDPatternFacet pattern = (XSDPatternFacet)getInput();
		  
      String newValue = patternText.getText();
      if (newValue.length() > 0)
      {
        pattern.setLexicalValue(newValue);
      }
	  }

  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }
}
