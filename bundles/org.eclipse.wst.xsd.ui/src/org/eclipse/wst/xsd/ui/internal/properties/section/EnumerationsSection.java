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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.viewers.NavigableTableViewer;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.AddEnumsAction;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


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
    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)getInput();
    Element element = st.getElement();
    if (e.widget == addButton || e.widget == addManyButton)
    {
      XSDDOMHelper helper = new XSDDOMHelper();
      
      int variety = st.getVariety().getValue();
      Node varietyElement = null;
      if (variety == XSDVariety.ATOMIC)
      {
        varietyElement = helper.getChildNode(element, XSDConstants.RESTRICTION_ELEMENT_TAG);
      }
      else if (variety == XSDVariety.UNION)
      {
        varietyElement = helper.getChildNode(element, XSDConstants.UNION_ELEMENT_TAG);
      }
      else if (variety == XSDVariety.LIST)
      {
        varietyElement = helper.getChildNode(element, XSDConstants.LIST_ELEMENT_TAG);
      }
            
      if (varietyElement != null)
      {
        if (e.widget == addButton)
        {
          java.util.List attributes = new ArrayList();
          
          List enumList = st.getEnumerationFacets();
          StringBuffer newName = new StringBuffer("value1"); //$NON-NLS-1$
          int suffix = 1;
          for (Iterator i = enumList.iterator(); i.hasNext(); )
          {
            XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)i.next();
            String value = enumFacet.getLexicalValue();
            if (value != null)
            {
              if (value.equals(newName.toString()))
              {
                suffix++;
                newName = new StringBuffer("value" + String.valueOf(suffix)); //$NON-NLS-1$
              }
            }
          }
          attributes.add(new DOMAttribute(XSDConstants.VALUE_ATTRIBUTE, newName.toString()));
          beginRecording(XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ENUM"), element); //$NON-NLS-1$
          Action action = getNewElementAction(XSDConstants.ENUMERATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ENUM"), attributes, (Element)varietyElement, null);
          action.run();
          st.setElement(element);

          endRecording(element);
          enumerationsTable.refresh();
          int newItemIndex = enumerationsTable.getTable().getItemCount() - 1;
          enumerationsTable.editElement(enumerationsTable.getElementAt(newItemIndex), 0);
          attributes = null;
        }
        else if (e.widget == addManyButton)
        {
          AddEnumsAction action = new AddEnumsAction(XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ENUMS")); //$NON-NLS-1$
          action.setElementTag(XSDConstants.ENUMERATION_ELEMENT_TAG);
          action.setAttributes(null);
          action.setParentNode((Element)varietyElement);
          action.setRelativeNode(null);
          action.setDescription(XSDEditorPlugin.getXSDString("_UI_ENUMERATIONS_DIALOG_TITLE")); //$NON-NLS-1$
          action.run();
          st.setElement(element);
          enumerationsTable.refresh();
        }
      }
    }
    else if (e.widget == deleteButton)
    {
      StructuredSelection selection = (StructuredSelection)enumerationsTable.getSelection();
      if (selection != null)
      {
        Iterator i = selection.iterator();
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE_ENUMERATION"), element); //$NON-NLS-1$
        while (i.hasNext())
        {
          Object obj = i.next();
          if (obj != null)
          {
            if (obj instanceof XSDEnumerationFacet)
            {
              XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)obj;
    
              // I have to update using DOM
              XSDDOMHelper.removeNodeAndWhitespace(enumFacet.getElement());            
              
            }
          }
        }
        enumerationsTable.refresh();
        st.setElement(element);
        endRecording(element);
      }
    }
    else if (e.widget == enumerationsTable.getTable())
    {
      StructuredSelection selection = (StructuredSelection)enumerationsTable.getSelection();
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
  
  /**
   * @see org.eclipse.wst.common.ui.properties.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory)
   */
  public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
  {
    super.createControls(parent, factory);

    composite = getWidgetFactory().createFlatFormComposite(parent);
    FormData data;
    
    GC gc = new GC(parent);
    Point extent = gc.textExtent("  " + XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE_INCLUDE") + "  "); //$NON-NLS-1$
    gc.dispose();

    enumerationsTable = new EnumerationsTableViewer(getWidgetFactory().createTable(composite, SWT.MULTI | SWT.FULL_SELECTION));
    enumerationsTable.setInput(getInput());
    Table table = enumerationsTable.getTable();
    table.addSelectionListener(this);
    
    addButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_ADD_BUTTON_LABEL"), SWT.PUSH); //$NON-NLS-1$
    addManyButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_REGEX_WIZARD_ADD_BUTTON_LABEL") + "...", SWT.PUSH); //$NON-NLS-1$
    deleteButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE_INCLUDE"), SWT.PUSH); //$NON-NLS-1$

    FormData data2 = new FormData();
    data2.top = new FormAttachment(0, 0);
    data2.left = new FormAttachment(100, -100);
    data2.right = new FormAttachment(100, 0);
//    data2.width = 50;
    addButton.setLayoutData(data2);
    addButton.addSelectionListener(this);

    data = new FormData();
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
    data.width = tableMinimumWidth;
    table.setLayoutData(data);
    table.addListener(SWT.Resize, this);
  }
  
  /*
   * @see org.eclipse.wst.common.ui.properties.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    Object input = getInput();
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
    XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)input;
    Element element = st.getElement();
    XSDDOMHelper helper = new XSDDOMHelper();
    Node restrictionElement = helper.getChildNode(element, XSDConstants.RESTRICTION_ELEMENT_TAG);
    
    Iterator validFacets = st.getValidFacets().iterator();
    
    boolean isApplicable = false;
    while (validFacets.hasNext())
    {
      String aValidFacet = (String)validFacets.next();
      if (aValidFacet.equals(XSDConstants.ENUMERATION_ELEMENT_TAG))
      {
        isApplicable = true;
      }
    }
    
    if (isApplicable)  
//    if (restrictionElement != null)
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

  /* (non-Javadoc)
   * @see org.eclipse.wst.common.ui.properties.ISection#shouldUseExtraSpace()
   */
  public boolean shouldUseExtraSpace()
  {
    return true;
  }

  
  class EnumerationsTableViewer extends NavigableTableViewer implements ICellModifier
  {
    protected String[] columnProperties = {XSDConstants.ENUMERATION_ELEMENT_TAG};

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
        TableItem item = (TableItem)element;
        
        Element simpleTypeElement = ((XSDSimpleTypeDefinition)getInput()).getElement();
        beginRecording(XSDEditorPlugin.getXSDString("_UI_ENUM_VALUE_CHANGE"), simpleTypeElement); //$NON-NLS-1$
        
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)item.getData();
        enumFacet.setLexicalValue((String)value);
        item.setData(enumFacet);
        item.setText((String)value);
        endRecording(simpleTypeElement);
      }
    }    

    public Object getValue(Object element, String property)
    {
      if (element instanceof XSDEnumerationFacet)
      {
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)element;
        String value = enumFacet.getLexicalValue();
        if (value == null) value = ""; //$NON-NLS-1$
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
        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)inputElement;
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
      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleEnum.gif");
    }
    
    public String getColumnText(Object element, int columnIndex)
    {
      if (element instanceof XSDEnumerationFacet)
      {
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)element;
        String value = enumFacet.getLexicalValue();
        if (value == null) value = "";
        return value;
      }
      return "";
    }

  }

}
