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
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.xsd.ui.internal.properties.XSDPropertySourceProvider;
import org.eclipse.xsd.XSDElementDeclaration;

public class OtherAttributesSection extends AbstractSection
{
  PropertySheetPage propertySheetPage;
  IWorkbenchPart part;
  
  /**
   * 
   */
  public OtherAttributesSection()
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
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
    
//    composite = new Composite(parent, SWT.FLAT);
//    GridLayout gl = new GridLayout(1, true);
//    composite.setLayout(gl);
//    GridData data = new GridData();
//    data.grabExcessHorizontalSpace = true;
//    data.grabExcessVerticalSpace = true; 
//    composite.setLayoutData(data);
    
    propertySheetPage = new PropertySheetPage();
		propertySheetPage.createControl(composite);
    propertySheetPage.setPropertySourceProvider(new XSDPropertySourceProvider());
    propertySheetPage.getControl().setLayoutData(data);
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
	  this.part = part;
	  this.selection = selection;
	  if (propertySheetPage == null)
	  {
	    propertySheetPage = new PropertySheetPage();
	  }
	  propertySheetPage.selectionChanged(part, selection);
	}
  
	/*
	 * @see org.eclipse.wst.common.ui.properties.view.ITabbedPropertySection#refresh()
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

	    Object input = getInput();
      if (!propertySheetPage.getControl().isDisposed())
  	    propertySheetPage.selectionChanged(part, selection);
    }
	}

  public void dispose()
  {
//    propertySheetPage.dispose();
//    propertySheetPage = null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISection#shouldUseExtraSpace()
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
  }

}
