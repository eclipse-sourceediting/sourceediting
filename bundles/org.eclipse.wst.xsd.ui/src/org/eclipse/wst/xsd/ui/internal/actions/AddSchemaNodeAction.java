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
import org.eclipse.wst.xsd.ui.internal.XSDEditor;


public class AddSchemaNodeAction extends Action
{
  /**
   * Constructor for AddSchemaNodeAction.
   */
  public AddSchemaNodeAction()
  {
    super();
  }

  /**
   * Constructor for AddSchemaNodeAction.
   * @param text
   */
  public AddSchemaNodeAction(String text)
  {
    super(text);
  }

  /**
   * Constructor for AddSchemaNodeAction.
   * @param text
   * @param image
   */
  public AddSchemaNodeAction(String text, ImageDescriptor image)
  {
    super(text, image);
  }

  public void setEditor(XSDEditor editor)
  {
    this.editor = editor;
  }
  
  protected XSDEditor editor;
  
  /**
   * @see org.eclipse.jface.action.IAction#run()
   */
  public void run()
  {
    editor.createDefaultSchemaNode();
  }
}
