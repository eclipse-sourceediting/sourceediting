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
package org.eclipse.wst.xsd.editor;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.wst.xsd.adt.facade.IADTObjectListener;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDAdapterFactory;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDBaseAdapter;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDElementDeclarationAdapter;
import org.eclipse.wst.xsd.editor.internal.adapters.XSDParticleAdapter;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;

public class XSDTabbedPropertySheetPage extends TabbedPropertySheetPage implements IADTObjectListener
{
  XSDBaseAdapter oldSelection;
  public XSDTabbedPropertySheetPage(ITabbedPropertySheetPageContributor tabbedPropertySheetPageContributor)
  {
    super(tabbedPropertySheetPageContributor);
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged(IWorkbenchPart part, ISelection selection) {

      Object selected = ((StructuredSelection)selection).getFirstElement();
      System.out.println(selected);
      if (selected instanceof XSDBaseAdapter)
      {
        XSDBaseAdapter adapter = (XSDBaseAdapter)selected;
        if (oldSelection != null)
        {
          oldSelection.unregisterListener(this);
          if (oldSelection instanceof XSDElementDeclarationAdapter)
          {
            XSDElementDeclaration elem = (XSDElementDeclaration)((XSDElementDeclarationAdapter)oldSelection).getTarget();
            Adapter adap = XSDAdapterFactory.getInstance().adapt(elem.getContainer());
            if (adap instanceof XSDParticleAdapter)
            {
              XSDParticleAdapter particleAdapter = (XSDParticleAdapter)adap;
              particleAdapter.unregisterListener(this);
            }
          }
        }
        if (adapter instanceof XSDElementDeclarationAdapter)
        {
          XSDElementDeclaration elem = (XSDElementDeclaration)((XSDElementDeclarationAdapter)adapter).getTarget();
          Adapter adap = XSDAdapterFactory.getInstance().adapt(elem.getContainer());
          if (adap instanceof XSDParticleAdapter)
          {
            XSDParticleAdapter particleAdapter = (XSDParticleAdapter)adap;
            particleAdapter.registerListener(this);
          }
          if (elem.isElementDeclarationReference())
          {
            XSDElementDeclarationAdapter resolvedElementAdapter = (XSDElementDeclarationAdapter)XSDAdapterFactory.getInstance().adapt(elem.getResolvedElementDeclaration());
            resolvedElementAdapter.registerListener(this);
          }
        }
        adapter.registerListener(this);
        oldSelection = adapter;
        Object model = adapter.getTarget();
        if (model instanceof XSDConcreteComponent)
        {
          selection = new StructuredSelection((XSDConcreteComponent)model);
        }
        super.selectionChanged(part, selection);
        return;
      }
      super.selectionChanged(part, selection);
  }
  
  public void propertyChanged(Object object, String property)
  {
    if (getCurrentTab() != null)
    {
      refresh();
    }
  }

}
