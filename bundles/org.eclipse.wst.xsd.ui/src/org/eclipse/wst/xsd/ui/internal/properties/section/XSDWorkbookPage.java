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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class XSDWorkbookPage {
  public CTabItem tabItem;
/**
 * WorkbookPage constructor comment.
 */
public XSDWorkbookPage(XSDWorkbook parent) {
  CTabFolder folder = parent.getTabFolder();
  tabItem = new CTabItem(folder,SWT.NONE);
  tabItem.setData(this);
}
public void activate() {

  if (tabItem.getControl() == null)
    tabItem.setControl(createControl(tabItem.getParent()));
      
}
protected abstract Control createControl (Composite parent);
public boolean deactivate() {
  return true;
}
public void dispose() {

  if (tabItem == null)
    return;

  CTabItem oldItem = tabItem;
  tabItem = null;
  oldItem.dispose();
}
public CTabItem getTabItem() {
  return tabItem;
}
}
