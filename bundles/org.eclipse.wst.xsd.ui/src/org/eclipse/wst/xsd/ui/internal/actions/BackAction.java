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
package org.eclipse.wst.xsd.ui.internal.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wst.xsd.ui.internal.graph.XSDGraphViewer;
import org.eclipse.xsd.XSDSchema;

/**
 * @author kchong
 *
 * <a href="mailto:kchong@ca.ibm.com">kchong@ca.ibm.com</a>
 *
 */
public class BackAction extends Action
{
  ISelectionProvider selectionProvider;
  XSDGraphViewer xsdGraphViewer;
  XSDSchema xsdSchema;
  
  /**
   * 
   */
  public BackAction()
  {
    super();
  }

  /**
   * @param text
   */
  public BackAction(String text)
  {
    super(text);
  }

  public BackAction(String text, XSDGraphViewer viewer)
  {
    super(text);
    xsdGraphViewer = viewer;
  }
  
  /**
   * @param text
   * @param image
   */
  public BackAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  /**
   * @param text
   * @param style
   */
  public BackAction(String text, int style)
  {
    super(text, style);
  }

  public void setSelectionProvider(ISelectionProvider selectionProvider)
  {
    this.selectionProvider = selectionProvider;
  }

  public void setXSDSchema(XSDSchema xsdSchema)
  {
    this.xsdSchema = xsdSchema;
  }
  
  /*
   * @see IAction#run()
   */
  public void run()
  {
    StructuredSelection selection = new StructuredSelection(xsdSchema);
    selectionProvider.setSelection(selection);
  }
}
