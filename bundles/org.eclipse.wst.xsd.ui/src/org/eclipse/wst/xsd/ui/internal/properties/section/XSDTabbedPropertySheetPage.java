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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.properties.ITabbedPropertySheetPageContributor;
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetPage;
import org.eclipse.wst.sse.ui.ViewerSelectionManager;
import org.eclipse.wst.xsd.ui.internal.XSDSelectionManager;
import org.eclipse.wst.xsd.ui.internal.graph.model.Category;
import org.eclipse.wst.xsd.ui.internal.provider.CategoryAdapter;
import org.eclipse.wst.xsd.ui.internal.provider.XSDModelAdapterFactoryImpl;
import org.eclipse.xsd.XSDSchema;

public class XSDTabbedPropertySheetPage extends TabbedPropertySheetPage
  implements ISelectionChangedListener, INotifyChangedListener 
{
  private ViewerSelectionManager fViewerSelectionManager;
  private XSDSelectionManager selectionManager;
  private XSDModelAdapterFactoryImpl adapterFactory;
  /**
   * @param tabbedPropertySheetPageContributor
   */
  public XSDTabbedPropertySheetPage(ITabbedPropertySheetPageContributor tabbedPropertySheetPageContributor)
  {
    super(tabbedPropertySheetPageContributor);
  }
  
  XSDSchema xsdSchema;
  public void setXSDSchema(XSDSchema xsdSchema)
  {
    this.xsdSchema = xsdSchema;
  }
  
  public void setXSDModelAdapterFactory(XSDModelAdapterFactoryImpl adapterFactory) {
    // disconnect from old one
    if (adapterFactory != null) {
      adapterFactory.removeListener(this);
    }

    this.adapterFactory = adapterFactory;

    // connect to new one
    if (adapterFactory != null) {
      adapterFactory.addListener(this);
    }
  }
  
  public void setSelectionManager(XSDSelectionManager newSelectionManager)
  { 
    // disconnect from old one
    if (selectionManager != null)
    {                                                        
      selectionManager.removeSelectionChangedListener(this);  
    }

    selectionManager = newSelectionManager;

    // connect to new one
    if (selectionManager != null)
    {
      selectionManager.addSelectionChangedListener(this);  
    }
  }    

  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
    // override for category
    if (selection != null)
    {
      if (selection instanceof StructuredSelection)
      {
        StructuredSelection structuredSelection = (StructuredSelection)selection;
        if (structuredSelection.isEmpty())
        {
          return;
        }
        Object obj = structuredSelection.getFirstElement();        
        if (obj instanceof CategoryAdapter)
        {
          selection = new StructuredSelection(((CategoryAdapter)obj).getXSDSchema());
        }
        else if (obj instanceof Category)
        {
          selection = new StructuredSelection(((Category)obj).getXSDSchema());
        }  
      }
      else if (selection instanceof TextSelection)
      {
        return;
      }
    }
    super.selectionChanged(part, selection);
  }
	
  public void selectionChanged(SelectionChangedEvent event)
  {
    if (!event.getSelection().isEmpty())
    {
      selectionChanged(getSite().getWorkbenchWindow().getActivePage().getActivePart(), event.getSelection());
      //super.selectionChanged(getSite().getWorkbenchWindow().getActivePage().getActivePart(), event.getSelection());
    }
  }
  
  public void dispose()
  {
    if (selectionManager != null)
    {
      selectionManager.removeSelectionChangedListener(this);
    }
    if (adapterFactory != null)
    {
      adapterFactory.removeListener(this);
    }
    super.dispose();
  }
  
  public void notifyChanged(Notification notification)
  {
    if (getCurrentTab() != null)
    {
      refresh();
    }
  }
}
