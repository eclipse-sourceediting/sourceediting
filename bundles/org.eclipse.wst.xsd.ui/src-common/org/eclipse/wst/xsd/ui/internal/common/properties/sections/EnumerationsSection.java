/*******************************************************************************
 * Copyright (c) 2001, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.ibm.icu.util.StringTokenizer;

import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.internal.viewers.NavigableTableViewer;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddEnumerationsCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.SetXSDFacetValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.widgets.EnumerationsDialog;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;

public class EnumerationsSection extends AbstractSection
{
  private EnumerationsTableViewer enumerationsTable;
  private Button addButton;
  private Button addManyButton;
  private Button deleteButton;

  /**
   * 
   */
  public EnumerationsSection()
  {
    super();
  }

  public void widgetSelected(SelectionEvent e)
  {
    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition) input;

    if (e.widget == addButton)
    {
      List enumList = st.getEnumerationFacets();
      String newName = XSDCommonUIUtils.createUniqueEnumerationValue("value", enumList); //$NON-NLS-1$

      AddEnumerationsCommand command = new AddEnumerationsCommand(Messages._UI_ACTION_ADD_ENUMERATION, (XSDSimpleTypeDefinition) input);
      command.setValue(newName);
      getCommandStack().execute(command);

      enumerationsTable.refresh();
      int newItemIndex = enumerationsTable.getTable().getItemCount() - 1;
      enumerationsTable.editElement(enumerationsTable.getElementAt(newItemIndex), 0);
    }
    else if (e.widget == addManyButton)
    {
      Display display = Display.getCurrent();
      // if it is null, get the default one
      display = display == null ? Display.getDefault() : display;
      Shell parentShell = display.getActiveShell();
      EnumerationsDialog dialog = new EnumerationsDialog(parentShell);
      dialog.setBlockOnOpen(true);
      int result = dialog.open();

      if (result == Window.OK)
      {
        String text = dialog.getText();
        String delimiter = dialog.getDelimiter();
        StringTokenizer tokenizer = new StringTokenizer(text, delimiter);
        CompoundCommand compoundCommand = new CompoundCommand(Messages._UI_ACTION_ADD_ENUMERATIONS);
        while (tokenizer.hasMoreTokens())
        {
          String token = tokenizer.nextToken();
          if (dialog.isPreserveWhitespace() == false)
          {
            token = token.trim();
          }
          AddEnumerationsCommand command = new AddEnumerationsCommand(Messages._UI_ACTION_ADD_ENUMERATIONS, (XSDSimpleTypeDefinition) input);
          command.setValue(token);
          compoundCommand.add(command);
        }
        getCommandStack().execute(compoundCommand);
      }
      enumerationsTable.refresh();
    }
    else if (e.widget == deleteButton)
    {
      StructuredSelection selection = (StructuredSelection) enumerationsTable.getSelection();
      if (selection != null)
      {
        Iterator i = selection.iterator();
        CompoundCommand compoundCommand = new CompoundCommand(Messages._UI_ACTION_DELETE_ENUMERATION);
        while (i.hasNext())
        {
          Object obj = i.next();
          if (obj != null)
          {
            if (obj instanceof XSDEnumerationFacet)
            {
              XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) obj;

              DeleteCommand deleteCommand = new DeleteCommand(Messages._UI_ACTION_DELETE_ENUMERATION, enumFacet);
              compoundCommand.add(deleteCommand);
            }
          }
        }
        getCommandStack().execute(compoundCommand);
        enumerationsTable.refresh();
      }
    }
    else if (e.widget == enumerationsTable.getTable())
    {
      StructuredSelection selection = (StructuredSelection) enumerationsTable.getSelection();
      if (selection.getFirstElement() != null)
      {
        deleteButton.setEnabled(true);
      }
      else
      {
        deleteButton.setEnabled(false);
      }
    }

  }

  public void widgetDefaultSelected(SelectionEvent e)
  {

  }

  public void createContents(Composite parent)
  {
    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    composite = factory.createFlatFormComposite(parent);

    enumerationsTable = new EnumerationsTableViewer(getWidgetFactory().createTable(composite, SWT.MULTI | SWT.FULL_SELECTION));
    enumerationsTable.setInput(input);
    Table table = enumerationsTable.getTable();
    table.addSelectionListener(this);

    addButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_ADD_BUTTON_LABEL"), SWT.PUSH); //$NON-NLS-1$
    addManyButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_ADD_BUTTON_LABEL") + "...", SWT.PUSH); //$NON-NLS-1$ //$NON-NLS-2$
    deleteButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE_INCLUDE"), SWT.PUSH); //$NON-NLS-1$

    FormData data2 = new FormData();
    data2.top = new FormAttachment(0, 0);
    data2.left = new FormAttachment(100, -100);
    data2.right = new FormAttachment(100, 0);
    // data2.width = 50;
    addButton.setLayoutData(data2);
    addButton.addSelectionListener(this);

    FormData data = new FormData();
    data.left = new FormAttachment(addButton, 0, SWT.LEFT);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(addButton, 0);
    addManyButton.setLayoutData(data);
    addManyButton.addSelectionListener(this);

    data = new FormData();
    data.left = new FormAttachment(addButton, 0, SWT.LEFT);
    data.right = new FormAttachment(100, 0);
    data.top = new FormAttachment(addManyButton, 0);
    deleteButton.setLayoutData(data);
    deleteButton.setEnabled(false);
    deleteButton.addSelectionListener(this);

    data = new FormData();
    data.top = new FormAttachment(0, 0);
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(addButton, 0);
    data.bottom = new FormAttachment(100, 0);
    data.width = 50;
    table.setLayoutData(data);
    table.addListener(SWT.Resize, this);
  }

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition) input;

    Iterator validFacets = st.getValidFacets().iterator();

    boolean isApplicable = false;
    while (validFacets.hasNext())
    {
      String aValidFacet = (String) validFacets.next();
      if (aValidFacet.equals(XSDConstants.ENUMERATION_ELEMENT_TAG))
      {
        isApplicable = true;
      }
    }

    if (isApplicable)
    {
      addButton.setEnabled(true);
      addManyButton.setEnabled(true);
    }
    else
    {
      addButton.setEnabled(false);
      addManyButton.setEnabled(false);
    }
    enumerationsTable.setInput(input);
  }

  public void handleEvent(Event event)
  {
    Table table = enumerationsTable.getTable();
    if (event.type == SWT.Resize && event.widget == table)
    {
      TableColumn tableColumn = table.getColumn(0);
      tableColumn.setWidth(table.getSize().x);
    }
  }

  public void dispose()
  {
  }

  public boolean shouldUseExtraSpace()
  {
    return true;
  }

  class EnumerationsTableViewer extends NavigableTableViewer implements ICellModifier
  {
    protected String[] columnProperties = { XSDConstants.ENUMERATION_ELEMENT_TAG };

    protected CellEditor[] cellEditors;

    Table table;

    public EnumerationsTableViewer(Table table)
    {
      super(table);
      table = getTable();

      table.setLinesVisible(true);

      setContentProvider(new EnumerationsTableContentProvider());
      setLabelProvider(new EnumerationsTableLabelProvider());
      setColumnProperties(columnProperties);

      setCellModifier(this);

      TableColumn column = new TableColumn(table, SWT.NONE, 0);
      column.setText(columnProperties[0]);
      column.setAlignment(SWT.LEFT);
      column.setResizable(true);

      cellEditors = new CellEditor[1];

      TableLayout layout = new TableLayout();
      ColumnWeightData data = new ColumnWeightData(100);

      layout.addColumnData(data);
      cellEditors[0] = new TextCellEditor(table);

      getTable().setLayout(layout);
      setCellEditors(cellEditors);
    }

    public boolean canModify(Object element, String property)
    {
      return true;
    }

    public void modify(Object element, String property, Object value)
    {
      if (element instanceof TableItem && (value != null))
      {
        TableItem item = (TableItem) element;

        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) item.getData();
        SetXSDFacetValueCommand command = new SetXSDFacetValueCommand(Messages._UI_ACTION_SET_ENUMERATION_VALUE, enumFacet);
        command.setValue((String) value);
        getCommandStack().execute(command);
        item.setData(enumFacet);
        item.setText((String) value);
      }
    }

    public Object getValue(Object element, String property)
    {
      if (element instanceof XSDEnumerationFacet)
      {
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) element;
        String value = enumFacet.getLexicalValue();
        if (value == null)
          value = ""; //$NON-NLS-1$
        return value;
      }
      return ""; //$NON-NLS-1$
    }

  }

  class EnumerationsTableContentProvider implements IStructuredContentProvider
  {
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public java.lang.Object[] getElements(java.lang.Object inputElement)
    {
      java.util.List list = new ArrayList();
      if (inputElement instanceof XSDSimpleTypeDefinition)
      {
        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition) inputElement;
        return st.getEnumerationFacets().toArray();
      }
      return list.toArray();
    }

    public void dispose()
    {
    }
  }

  class EnumerationsTableLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    public EnumerationsTableLabelProvider()
    {

    }

    public Image getColumnImage(Object element, int columnIndex)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleEnum.gif"); //$NON-NLS-1$
    }

    public String getColumnText(Object element, int columnIndex)
    {
      if (element instanceof XSDEnumerationFacet)
      {
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) element;
        String value = enumFacet.getLexicalValue();
        if (value == null)
          value = ""; //$NON-NLS-1$
        return value;
      }
      return ""; //$NON-NLS-1$
    }

  }

}
