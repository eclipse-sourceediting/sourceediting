/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.common.properties.sections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

public class XSDAttributeGroupDefinitionSection extends AbstractSection
{
  protected CCombo refCombo;

  public XSDAttributeGroupDefinitionSection()
  {
    super();
  }

  protected void createContents(Composite parent)
  {
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    composite.setLayout(gridLayout);

    // ------------------------------------------------------------------
    // Ref Label
    // ------------------------------------------------------------------
    GridData data = new GridData();
    data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
    data.grabExcessHorizontalSpace = false;
    CLabel refLabel = getWidgetFactory().createCLabel(composite, XSDConstants.REF_ATTRIBUTE + ":");  //$NON-NLS-1$
    refLabel.setLayoutData(data);

    // ------------------------------------------------------------------
    // Ref Combo
    // ------------------------------------------------------------------

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;

    refCombo = getWidgetFactory().createCCombo(composite, SWT.FLAT);
    refCombo.addSelectionListener(this);
    refCombo.setLayoutData(data);
  }

  public void refresh()
  {
    super.refresh();

    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }

    setListenerEnabled(false);

    if (input instanceof XSDNamedComponent)
    {
      XSDNamedComponent namedComponent = (XSDNamedComponent)input;
      Element element = namedComponent.getElement();
      if (element != null)
      {
        String attrValue = element.getAttribute(XSDConstants.REF_ATTRIBUTE);
        if (attrValue == null)
        {
          attrValue = "";
        }
        refCombo.setText(attrValue);
      }
    }

    setListenerEnabled(true);
  }

}
