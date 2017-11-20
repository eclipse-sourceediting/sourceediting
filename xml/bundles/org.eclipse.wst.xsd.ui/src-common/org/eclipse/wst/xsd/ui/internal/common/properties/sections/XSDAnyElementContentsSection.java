/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDProcessContents;
import org.eclipse.xsd.XSDWildcard;

public class XSDAnyElementContentsSection extends MultiplicitySection
{
  CCombo namespaceCombo;
  CCombo processContentsCombo;

  private String[] namespaceComboValues = { "", //$NON-NLS-1$
      "##any", //$NON-NLS-1$
      "##other", //$NON-NLS-1$
      "##targetNamespace", //$NON-NLS-1$
      "##local" //$NON-NLS-1$
  };

  /**
   * 
   */
  public XSDAnyElementContentsSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridData data = new GridData();

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 2;
    composite.setLayout(gridLayout);

    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;

    CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_ATTRIBUTES_NAMESPACE);
    namespaceLabel.setLayoutData(data);

    namespaceCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    namespaceCombo.setLayoutData(data);
    namespaceCombo.setItems(namespaceComboValues);
    namespaceCombo.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(namespaceCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__ANYELEMENT__NAMESPACE);

    CLabel processContentsLabel = getWidgetFactory().createCLabel(composite, Messages._UI_LABEL_ATTRIBUTES_PROCESSCONTENTS);
    data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    processContentsLabel.setLayoutData(data);

    processContentsCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    processContentsCombo.setLayoutData(data);
    Iterator list = XSDProcessContents.VALUES.iterator();
    processContentsCombo.add(""); //$NON-NLS-1$
    while (list.hasNext())
    {
      processContentsCombo.add(((XSDProcessContents) list.next()).getName());
    }
    processContentsCombo.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(processContentsCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__ANYELEMENT__PROCESSCONTENTS);

    // ------------------------------------------------------------------
    // min property
    // ------------------------------------------------------------------

    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_MINOCCURS);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    minCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    minCombo.setLayoutData(data);
    minCombo.add("0"); //$NON-NLS-1$
    minCombo.add("1"); //$NON-NLS-1$
    applyAllListeners(minCombo);
    minCombo.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(minCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__ANYELEMENT__MIN_OCCURENCE);

    // ------------------------------------------------------------------
    // max property
    // ------------------------------------------------------------------
    getWidgetFactory().createCLabel(composite, Messages.UI_LABEL_MAXOCCURS);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    maxCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    maxCombo.setLayoutData(data);
    maxCombo.add("0"); //$NON-NLS-1$
    maxCombo.add("1"); //$NON-NLS-1$
    maxCombo.add("unbounded"); //$NON-NLS-1$
    applyAllListeners(maxCombo);
    maxCombo.addSelectionListener(this);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(maxCombo,
    		XSDEditorCSHelpIds.GENERAL_TAB__ANYELEMENT__MAX_OCCURENCE);
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    setListenerEnabled(false);
    namespaceCombo.setText(""); //$NON-NLS-1$
    processContentsCombo.setText(""); //$NON-NLS-1$
    if (input != null)
    {
      if (input instanceof XSDWildcard)
      {
        XSDWildcard wildcard = (XSDWildcard) input;
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
        
        if (wildcard.eContainer() instanceof XSDParticle)
        {
          minCombo.setEnabled(!isReadOnly);
          maxCombo.setEnabled(!isReadOnly);
        }
        else
        {
          minCombo.setEnabled(false);
          maxCombo.setEnabled(false);
        }
      }
    }
    refreshMinMax();
    setListenerEnabled(true);
  }

  public boolean shouldUseExtraSpace()
  {
    return false;
  }

  public void doWidgetSelected(SelectionEvent e)
  {
    XSDConcreteComponent concreteComponent = (XSDConcreteComponent) input;
    if (concreteComponent instanceof XSDWildcard)
    {
      XSDWildcard wildcard = (XSDWildcard) concreteComponent;
      if (e.widget == namespaceCombo)
      {
        String newValue = namespaceCombo.getText();
        boolean removeAttribute = false;
        if (newValue.length() == 0)
        {
          removeAttribute = true;
        }
        // TODO use commands
        // beginRecording(XSDEditorPlugin.getXSDString("_UI_NAMESPACE_CHANGE"),
        // element); //$NON-NLS-1$
        if (removeAttribute)
        {
          wildcard.unsetLexicalNamespaceConstraint();
        }
        else
        {
          wildcard.setStringLexicalNamespaceConstraint(newValue);
        }
        // endRecording(element);
      }
      else if (e.widget == processContentsCombo)
      {
        String newValue = processContentsCombo.getText();
        boolean removeAttribute = false;
        if (newValue.length() == 0)
        {
          removeAttribute = true;
        }
        // beginRecording(XSDEditorPlugin.getXSDString("_UI_PROCESSCONTENTS_CHANGE"),
        // element); //$NON-NLS-1$
        if (removeAttribute)
        {
          wildcard.unsetProcessContents();
        }
        else
        {
          wildcard.setProcessContents(XSDProcessContents.get(processContentsCombo.getItem(processContentsCombo.getSelectionIndex())));
        }
        // endRecording(element);
      }
    }
    super.doWidgetSelected(e);
  }

  public void dispose()
  {
    if (minCombo != null && !minCombo.isDisposed())
      minCombo.removeSelectionListener(this);
    if (maxCombo != null && !maxCombo.isDisposed())
      maxCombo.removeSelectionListener(this);
    super.dispose();
  }

}
