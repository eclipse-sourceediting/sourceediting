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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class XSDWorkbook {
  private CTabFolder tabFolder;
  private CTabItem selectedTab;
/**
 * Workbook constructor comment.
 */
public XSDWorkbook(Composite parent, int style) {
  tabFolder = new CTabFolder(parent, style);

  tabFolder.addSelectionListener(new SelectionAdapter() {
    public void widgetSelected(SelectionEvent event) {
      CTabItem newSelectedTab = (CTabItem) event.item;
      if (selectedTab == newSelectedTab) // Do nothing if the selection did not change.
        return;

      if (selectedTab != null && (!selectedTab.isDisposed())) {
        XSDWorkbookPage selectedPage = getWorkbookPage(selectedTab);
        if (!selectedPage.deactivate()) {
          // tabFolder.setSelection(new CTabItem[] {selectedTab});
          tabFolder.setSelection(selectedTab);
          return;
        }
      }

      selectedTab = newSelectedTab;
      XSDWorkbookPage newSelectedPage = getWorkbookPage(newSelectedTab);
      if (newSelectedPage != null)
      newSelectedPage.activate();

    }
  });

}
public XSDWorkbookPage getSelectedPage() {

  int index = tabFolder.getSelectionIndex();
  if (index == -1) // When can this be -1
    return null;

  CTabItem selectedItem = tabFolder.getItem(index);

  return (XSDWorkbookPage)selectedItem.getData();
}
public CTabFolder getTabFolder() {

  return tabFolder;

}
protected XSDWorkbookPage getWorkbookPage(CTabItem item) {

  try {
    return (XSDWorkbookPage) item.getData();
  } catch (ClassCastException e) {
    return null;
  }
}
public XSDWorkbookPage[] getWorkbookPages() {

  CTabItem[] tabItems = tabFolder.getItems();
  int nItems = tabItems.length;
  XSDWorkbookPage[] workbookPages = new XSDWorkbookPage[nItems];
  for (int i = 0; i < nItems; i++)
    workbookPages[i] = getWorkbookPage(tabItems[i]);
  return workbookPages;
}
public void setSelectedPage (XSDWorkbookPage workbookPage)
{
  CTabItem newSelectedTab = workbookPage.getTabItem();

  if (selectedTab == newSelectedTab)
    return;

  selectedTab = newSelectedTab;
  workbookPage.activate();
  // tabFolder.setSelection(new CTabItem[] {newSelectedTab});
  tabFolder.setSelection(newSelectedTab);

}
}
