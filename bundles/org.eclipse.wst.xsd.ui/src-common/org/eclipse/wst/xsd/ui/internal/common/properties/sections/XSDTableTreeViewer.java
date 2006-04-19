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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeContentProvider;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeViewer;
import org.w3c.dom.Element;

public class XSDTableTreeViewer extends XMLTableTreeViewer
{

  String filter = ""; //$NON-NLS-1$

  class XSDActionMenuListener implements IMenuListener
  {
    public void menuAboutToShow(IMenuManager menuManager)
    {
      // used to disable NodeSelection listening while running NodeAction
      // XSDActionManager nodeActionManager = new XSDActionManager(fModel,
      // XSDTableTreeViewer.this);
      // nodeActionManager.setCommandStack(commandStack);
      // nodeActionManager.fillContextMenu(menuManager, getSelection());

      // used to disable NodeSelection listening while running NodeAction
      // XMLNodeActionManager nodeActionManager = new
      // XMLNodeActionManager(((IDOMDocument) getInput()).getModel(),
      // XMLTableTreeViewer.this) {
      if (getInput() != null)
      {
        XSDActionManager nodeActionManager = new XSDActionManager(((IDOMDocument) (((Element) getInput()).getOwnerDocument())).getModel(), XSDTableTreeViewer.this);
        // nodeActionManager.setCommandStack(commandStack);
        nodeActionManager.fillContextMenu(menuManager, getSelection());
      }

    }
  }

  public XSDTableTreeViewer(Composite parent)
  {
    super(parent);
    // treeExtension.setCellModifier(null);
    getTree().setLinesVisible(true);

    // treeExtension = new XMLTreeExtension(getTree());

    // Reassign the content provider
    XMLTableTreeContentProvider provider = new MyContentProvider();
    // provider.addViewer(this);

    setContentProvider(provider);
    setLabelProvider(provider);

    // setViewerSelectionManager(new ViewerSelectionManagerImpl(null));
  }

  protected Object getRoot()
  {
    return super.getRoot();
  }

  public void setFilter(String filter)
  {
    this.filter = filter;
  }

  protected void createContextMenu()
  {
    // TODO Verify if this is okay to override the MenuManager
    MenuManager contextMenu = new MenuManager("#PopUp"); //$NON-NLS-1$
    contextMenu.add(new Separator("additions")); //$NON-NLS-1$
    contextMenu.setRemoveAllWhenShown(true);

    // This is the line we have to modify
    contextMenu.addMenuListener(new XSDActionMenuListener());
    Menu menu = contextMenu.createContextMenu(getControl());
    getControl().setMenu(menu);
  }

  boolean added = false;

  class MyContentProvider extends XMLTableTreeContentProvider
  {

    // public Object[] getChildren(Object element) {
    //			
    // if (!added) {
    // if (element instanceof Element) {
    // added = true;
    // Element elem = (Element)element;
    // if (elem instanceof INodeNotifier) {
    // viewerNotifyingAdapterFactory.adapt((INodeNotifier) elem);
    // }
    // // return new Object[] {elem};
    // }
    // }
    // return super.getChildren(element);
    // }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
      added = false;
      if (oldInput instanceof Element)
        oldInput = ((Element) oldInput).getOwnerDocument();

      if (newInput instanceof Element)
        newInput = ((Element) newInput).getOwnerDocument();
      super.inputChanged(viewer, oldInput, newInput);
    }
  }
}
