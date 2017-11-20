/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;
import org.eclipse.wst.xml.core.internal.catalog.CatalogSet;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalog;
import org.eclipse.wst.xml.core.internal.catalog.provisional.ICatalogEntry;
import org.eclipse.wst.xml.core.internal.catalog.provisional.INextCatalog;
import org.eclipse.wst.xml.ui.internal.catalog.XMLCatalogEntriesView;
import org.eclipse.wst.xml.ui.internal.catalog.XMLCatalogEntryDetailsView;
import org.eclipse.wst.xml.ui.internal.catalog.XMLCatalogMessages;
import org.eclipse.wst.xml.ui.internal.catalog.XMLCatalogTreeViewer;

public class SelectFromCatalogDialog extends Dialog
{

  private ICatalog workingUserCatalog;
  private ICatalog userCatalog;
  private ICatalog defaultCatalog;
  private XMLCatalogEntriesView catalogEntriesView;
  private ICatalog systemCatalog;

  private String currentSelectionLocation;
  private String currentSelectionNamespace;

  public SelectFromCatalogDialog(Shell parentShell)
  {
    super(parentShell);

    defaultCatalog = XMLCorePlugin.getDefault().getDefaultXMLCatalog();
    INextCatalog[] nextCatalogs = defaultCatalog.getNextCatalogs();
    for (int i = 0; i < nextCatalogs.length; i++)
    {
      INextCatalog catalog = nextCatalogs[i];
      ICatalog referencedCatalog = catalog.getReferencedCatalog();
      if (referencedCatalog != null)
      {
        if (XMLCorePlugin.SYSTEM_CATALOG_ID.equals(referencedCatalog.getId()))
        {
          systemCatalog = referencedCatalog;
        }
        else if (XMLCorePlugin.USER_CATALOG_ID.equals(referencedCatalog.getId()))
        {
          userCatalog = referencedCatalog;
        }
      }
    }
  }

  protected Control createDialogArea(Composite parent)
  {
    // we create a working copy of the 'User Settings' for the Catalog
    // that we can modify
    CatalogSet tempCatalogSet = new CatalogSet();
    workingUserCatalog = tempCatalogSet.lookupOrCreateCatalog("working", ""); //$NON-NLS-1$ //$NON-NLS-2$

    // TODO: add entries from the nested catalogs as well
    workingUserCatalog.addEntriesFromCatalog(userCatalog);

    Composite composite = new Composite(parent, SWT.NULL);
    composite.setLayout(new GridLayout());
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.heightHint = 500;
    composite.setLayoutData(gridData);
    createCatalogEntriesView(composite);
    createCatalogDetailsView(composite);

    return composite;
  }

  protected void createCatalogEntriesView(Composite parent)
  {
    Group group = new Group(parent, SWT.NONE);
    group.setLayout(new GridLayout());
    GridData gridData = new GridData(GridData.FILL_BOTH);
    gridData.widthHint = 370;
    group.setLayoutData(gridData);
    group.setText(XMLCatalogMessages.UI_LABEL_USER_ENTRIES);
    group.setToolTipText(XMLCatalogMessages.UI_LABEL_USER_ENTRIES_TOOL_TIP);

    /*
     * create a subclass of XMLCatalogEntriesView which suppresses - the
     * creation of 'Add', 'Edit', 'Delete' buttons - any method involving the
     * above buttons
     */
    catalogEntriesView = new XMLCatalogEntriesView(group, workingUserCatalog, systemCatalog)
    {
      protected void createButtons(Composite parent)
      {
      }

      protected void updateWidgetEnabledState()
      {
      }

    };

    // Only XML Schema entry is selectable
    catalogEntriesView.setLayoutData(gridData);
    XMLCatalogTreeViewer catalogTreeViewer = ((XMLCatalogTreeViewer) catalogEntriesView.getViewer());
    catalogTreeViewer.resetFilters();

    catalogTreeViewer.addFilter(new XMLCatalogTableViewerFilter(new String[] { ".xsd" }));
  }

  // Bug in the filter of the XML plugin, have to give a correct version here
  // TODO: Waiting for the fix to be commited to XML plugin and
  // be used by constellation
  private class XMLCatalogTableViewerFilter extends ViewerFilter
  {
    private static final String W3_XMLSCHEMA_NAMESPACE = "http://www.w3.org/2001/";
    protected String[] extensions;

    public XMLCatalogTableViewerFilter(String[] extensions1)
    {
      this.extensions = extensions1;
    }

    public boolean select(Viewer viewer, Object parent, Object element)
    {
      boolean result = false;
      if (element instanceof ICatalogEntry)
      {
        ICatalogEntry catalogEntry = (ICatalogEntry) element;
        for (int i = 0; i < extensions.length; i++)
        {
          // if the extension is correct and the namespace indicates
          // that this entry is not the W3 XML Schema
          if (catalogEntry.getURI().endsWith(extensions[i]) && !catalogEntry.getKey().startsWith(W3_XMLSCHEMA_NAMESPACE))
          {
            result = true;
            break;
          }
        }
      }
      else if (element.equals(XMLCatalogTreeViewer.PLUGIN_SPECIFIED_ENTRIES_OBJECT) || element.equals(XMLCatalogTreeViewer.USER_SPECIFIED_ENTRIES_OBJECT))
      {
        return true;
      }
      return result;
    }
  }

  protected void createCatalogDetailsView(Composite parent)
  {
    Group detailsGroup = new Group(parent, SWT.NONE);
    detailsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    detailsGroup.setLayout(new GridLayout());
    detailsGroup.setText(XMLCatalogMessages.UI_LABEL_DETAILS);
    final XMLCatalogEntryDetailsView detailsView = new XMLCatalogEntryDetailsView(detailsGroup);
    ISelectionChangedListener listener = new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        ISelection selection = event.getSelection();
        Object selectedObject = (selection instanceof IStructuredSelection) ? ((IStructuredSelection) selection).getFirstElement() : null;
        if (selectedObject instanceof ICatalogEntry)
        {
          ICatalogEntry entry = (ICatalogEntry) selectedObject;
          detailsView.setCatalogElement(entry);
          currentSelectionLocation = entry.getURI();
          currentSelectionNamespace = entry.getKey();
        }
        else
        {
          detailsView.setCatalogElement((ICatalogEntry) null);
          currentSelectionLocation = "";
          currentSelectionNamespace = "";
        }
      }
    };
    catalogEntriesView.getViewer().addSelectionChangedListener(listener);
  }

  public String getCurrentSelectionLocation()
  {
    return currentSelectionLocation;
  }

  public String getCurrentSelectionNamespace()
  {
    return currentSelectionNamespace;
  }
}
