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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.properties.XSDPropertySourceProvider;
import org.eclipse.xsd.XSDElementDeclaration;

public class OtherAttributesSection extends AbstractSection
{
  PropertySheetPage propertySheetPage;
  
  /**
   * 
   */
  public OtherAttributesSection()
  {
    super();
  }

	/**
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.internal.provisional.TabbedPropertySheetWidgetFactory)
	 */
	public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
	{
		super.createControls(parent, factory);
		composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);

    propertySheetPage = new PropertySheetPage();
		propertySheetPage.createControl(composite);
    propertySheetPage.setPropertySourceProvider(new XSDPropertySourceProvider());
    propertySheetPage.getControl().setLayoutData(data);
	}
	
	/*
	 * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
	 */
	public void refresh()
	{
    if (doRefresh)
    {
      if (isReadOnly)
      {
        composite.setEnabled(false);
      }
      else
      {
        composite.setEnabled(true);
      }
    }
    propertySheetPage.refresh();
	}

  public void dispose()
  {
    super.dispose();
    propertySheetPage.dispose();
    propertySheetPage = null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return true;
  }
  
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);
    if (input instanceof XSDElementDeclaration)
    {
      XSDElementDeclaration elementDeclaration = (XSDElementDeclaration)input;
      if (elementDeclaration.isElementDeclarationReference())
      {
        input = elementDeclaration.getResolvedElementDeclaration();
        
        isReadOnly = (!(elementDeclaration.getResolvedElementDeclaration().getRootContainer() == xsdSchema));
      }
    }
    // update property sheet because of new input change
    propertySheetPage.selectionChanged(part, selection);
  }

}
