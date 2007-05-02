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
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.contentmodel.CMElementDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.ui.internal.contentoutline.XMLNodeActionManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DOMExtensionItemMenuListener implements IMenuListener
{
  TreeViewer treeViewer;

  public DOMExtensionItemMenuListener(TreeViewer treeViewer)
  {
    this.treeViewer = treeViewer;
  }

  public void menuAboutToShow(IMenuManager manager)
  {
    manager.removeAll();
    ISelection selection = treeViewer.getSelection();
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection structuredSelection = (IStructuredSelection) selection;
      if (structuredSelection.getFirstElement() instanceof ElementImpl)
      {
        ElementImpl elementImpl = (ElementImpl) structuredSelection.getFirstElement();
        IDOMDocument domDocument = (IDOMDocument) elementImpl.getOwnerDocument();
        InternalNodeActionManager actionManager = new InternalNodeActionManager(domDocument.getModel(), treeViewer);
        actionManager.fillContextMenu(manager, structuredSelection);
      }
    }
  }
  
  
  class InternalNodeActionManager extends XMLNodeActionManager
  {
    public InternalNodeActionManager(IStructuredModel model, Viewer viewer)
    {
      super(model, viewer);
    }

    public void contributeActions(IMenuManager menu, List selection)
    {
      //menu.add(new Action("there"){});     
      try
      {
      int editMode = modelQuery.getEditMode();
      int ic = ModelQuery.INCLUDE_CHILD_NODES;
      int vc = (editMode == ModelQuery.EDIT_MODE_CONSTRAINED_STRICT) ? ModelQuery.VALIDITY_STRICT : ModelQuery.VALIDITY_NONE;
      List implicitlySelectedNodeList = null;

      if (selection.size() == 1)
      {
        Node node = (Node) selection.get(0);
        // contribute add child actions
        contributeAddChildActions(menu, node, ic, vc);
      }
      if (selection.size() > 0)
      {
        implicitlySelectedNodeList = getSelectedNodes(selection, true);
        // contribute delete actions
        contributeDeleteActions(menu, implicitlySelectedNodeList, ic, vc);
      }
      }
      catch(Exception e)
      {
        menu.add(new Action(e.getMessage()){});
      }
      /*
      if (selection.size() > 0)
      {
        // contribute replace actions
        contributeReplaceActions(menu, implicitlySelectedNodeList, ic, vc);
      }*/
    }

    protected void contributeAddChildActions(IMenuManager menu, Node node, int ic, int vc)
    {
      int nodeType = node.getNodeType();
      if (nodeType == Node.ELEMENT_NODE)
      {
        // 'Add Child...' and 'Add Attribute...' actions
        //
        Element element = (Element) node;
        MyMenuManager newMenu = new MyMenuManager("New"){
          public boolean isVisible() { return true; }          
        };//$NON-NLS-1$
        newMenu.setVisible(true);
        menu.add(newMenu);
        
        CMElementDeclaration ed = modelQuery.getCMElementDeclaration(element);
        if (ed != null)
        {
          List modelQueryActionList = new ArrayList();
          // add insert child node actions
          //
          modelQueryActionList = new ArrayList();
          modelQuery.getInsertActions(element, ed, -1, ic, vc, modelQueryActionList);
          addActionHelper(newMenu, modelQueryActionList);
        }
      }
    }
  }
}
