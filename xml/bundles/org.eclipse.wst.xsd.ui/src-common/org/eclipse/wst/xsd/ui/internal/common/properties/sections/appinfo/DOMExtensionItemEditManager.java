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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


/**
 * @deprecated
 */
public class DOMExtensionItemEditManager implements ExtensionItemEditManager
{
  public void handleEdit(Object item, Widget widget)
  {   
  }

  public Control createCustomButtonControl(Composite composite, Object item)
  {
    Button button = new Button(composite, SWT.NONE);
    button.setText("..."); //$NON-NLS-1$
    return button;
  }

  public Control createCustomTextControl(Composite composite, Object item)
  {
    return null;
  }

  public String getButtonControlStyle(Object object)
  {
    return ExtensionItemEditManager.STYLE_NONE;
  }

  public String getTextControlStyle(Object object)
  {
    return ExtensionItemEditManager.STYLE_NONE;
  }
}
