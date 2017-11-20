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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
 
/**
 * @deprecated
 */ 
public interface ExtensionItemEditManager
{  
  public final static String STYLE_NONE = "none";   //$NON-NLS-1$  
  public final static String STYLE_TEXT = "text"; //$NON-NLS-1$  
  public final static String STYLE_COMBO = "combo"; //$NON-NLS-1$
  public final static String STYLE_CUSTOM = "custom";     //$NON-NLS-1$
  
  void handleEdit(Object item, Widget widget);
  String getTextControlStyle(Object item);
  String getButtonControlStyle(Object item);
  Control createCustomTextControl(Composite composite, Object item);
  Control createCustomButtonControl(Composite composite, Object item);  
}
