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
package org.eclipse.wst.xsd.ui.internal.properties;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.wst.sse.core.IStructuredModel;
import org.eclipse.wst.sse.ui.ViewerSelectionManager;
import org.eclipse.wst.sse.ui.internal.properties.RemoveAction;
import org.eclipse.wst.sse.ui.view.events.INodeSelectionListener;
import org.eclipse.wst.sse.ui.view.events.NodeSelectionChangedEvent;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XSDPropertySheetPage extends PropertySheetPage implements ISelectionChangedListener, INodeSelectionListener
{
  /**
   * @param model
   */

  IEditorPart editorPart;
	private ViewerSelectionManager fViewerSelectionManager;
  IStructuredModel model;
  protected RemoveAction fRemoveAction;
  Control designControl;

  public XSDPropertySheetPage(IStructuredModel model, IEditorPart editorPart)
  {
    super();
    this.model = model;
    this.editorPart = editorPart;
  }

  public void selectionChanged(SelectionChangedEvent event)
  {
		super.selectionChanged(null, event.getSelection());
  }
  
	public void setViewerSelectionManager(ViewerSelectionManager viewerSelectionManager) {
		// disconnect from old one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.removeNodeSelectionListener(this);
		}

		fViewerSelectionManager = viewerSelectionManager;

		// connect to new one
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.addNodeSelectionListener(this);
		}
	}

	public void dispose() {
		// disconnect from the ViewerSelectionManager
		if (fViewerSelectionManager != null) {
			fViewerSelectionManager.removeNodeSelectionListener(this);
		}
		super.dispose();
	}

	public void nodeSelectionChanged(NodeSelectionChangedEvent event) {
		// multiple selection is unsupported
		if (event.getSelectedNodes().size() > 1)
		{
			selectionChanged(null, StructuredSelection.EMPTY);
		}
		else if (event.getSelectedNodes().size() == 0)
		{
		  
		}
		else
		{
      Object item = event.getSelectedNodes().get(0);
      if (item instanceof Text)
      {
        Node parent = ((Text)item).getParentNode();
        selectionChanged(null, new StructuredSelection(parent));
      }
      else
      {
  			selectionChanged(null, new StructuredSelection(event.getSelectedNodes()));
      }
		}
	}

  private void setPropertiesTitle(PropertySheet thePart, String title)
	{
		Control control = thePart.getDefaultPage().getControl();
		for (Composite parent = control.getParent(); parent != null; parent = parent.getParent())
		{
			if (parent instanceof ViewForm)
			{
				Control[] children = parent.getChildren();
				if (children.length > 0 && children[0] instanceof CLabel)
				{
					CLabel clabel = (CLabel)children[0];
  				clabel.setText(title);
				}
			}
		}
	}
  
}