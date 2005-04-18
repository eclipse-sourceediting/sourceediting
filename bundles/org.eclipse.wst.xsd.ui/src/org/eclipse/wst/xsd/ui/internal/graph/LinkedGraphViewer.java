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
package org.eclipse.wst.xsd.ui.internal.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xsd.ui.internal.XSDEditor;
import org.eclipse.wst.xsd.ui.internal.util.ViewUtility;
import org.eclipse.xsd.XSDConcreteComponent;
import org.w3c.dom.Element;



public class LinkedGraphViewer
{
  
  protected ISelectionProvider menuSelectionProvider;
  protected XSDEditor editor;
  protected BaseGraphicalViewer majorViewer, minorViewer;
  
  /**
   * 
   */
  public LinkedGraphViewer(XSDEditor editor, ISelectionProvider selectionProvider)
  {
    menuSelectionProvider = selectionProvider;
    this.editor = editor;
  }

  public void setMajorViewer(BaseGraphicalViewer majorViewer)
  {
    this.majorViewer = majorViewer;
  }

  public void setMinorViewer(BaseGraphicalViewer minorViewer)
  {
    this.minorViewer = minorViewer;
  }
  
  protected Composite control;
  protected SashForm sashForm;
  
  public Control createControl(Composite parent)
  {
    //control = new Composite(parent, SWT.DEFAULT);
   
    control = sashForm = new SashForm(parent, SWT.VERTICAL | SWT.BORDER);
    sashForm.setLayoutData(ViewUtility.createFill());
    
    majorViewer.createControl(sashForm);
    minorViewer.createControl(sashForm);
//    control.setLayout(new GridLayout());
//    control.setLayoutData(ViewUtility.createFill());
    return control;
  }
  
  public void addSelectionChangedListener(ISelectionChangedListener selectionListener)
  {
    if (majorViewer != null)
    {
      majorViewer.addSelectionChangedListener(selectionListener);
      majorViewer.addSelectionChangedListener(majorViewerListener);
    }
    if (minorViewer != null)
    {
      minorViewer.addSelectionChangedListener(selectionListener);
    }
  }
  
  /**
   * @return Composite
   */
  public Composite getControl()
  {
    return control;
  }

  /**
   * 
   */
  protected XSDConcreteComponent getInput()
  {
    return majorViewer.getInput();
  }

  /**
   * @param schema
   */
  protected void setInput(XSDConcreteComponent input)
  {
    majorViewer.setInput(input);
    minorViewer.setInput(input);
  }

  /**
   * @param component
   */
  public void setSelection(XSDConcreteComponent component)
  {
    majorViewer.setSelection(component);
  }

  protected MajorViewerSelectionChangedListener majorViewerListener = new MajorViewerSelectionChangedListener();
  
  private class MajorViewerSelectionChangedListener implements ISelectionChangedListener
  {
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent event)
    {
      ISelection editPartSelection = event.getSelection();
      List nodeList = new ArrayList();
      if (editPartSelection instanceof IStructuredSelection)
      {
        for (Iterator i = ((IStructuredSelection)editPartSelection).iterator(); i.hasNext(); )
        {
          EditPart editPart = (EditPart)i.next();
          if (editPart != null)
          {
            Object model = editPart.getModel();
            if (model instanceof XSDConcreteComponent)
            {
              Element element = ((XSDConcreteComponent)model).getElement();

              // this test ensures that we don't attempt to select an element for an external schema
              //
              if (element instanceof IDOMNode)
              {
                // now update the minor viewer based on the selected component in the major viewer
                minorViewer.setInput((XSDConcreteComponent)model);
                minorViewer.setSelection((XSDConcreteComponent)model);
              }
            }
          }
        }
      }                
    }

  }
  
}
