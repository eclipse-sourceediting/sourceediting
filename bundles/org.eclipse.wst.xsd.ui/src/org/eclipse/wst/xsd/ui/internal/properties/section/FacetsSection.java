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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.viewers.NavigableTableViewer;
import org.eclipse.wst.xsd.ui.internal.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.actions.DOMAttribute;
import org.eclipse.wst.xsd.ui.internal.util.XSDDOMHelper;
import org.eclipse.wst.xsd.ui.internal.wizards.RegexWizard;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class FacetsSection extends AbstractSection
{
  IWorkbenchPart part;
  ISelection selection;
  FacetViewer facetViewer;
  
  XSDWorkbook workbook;
  FacetsWorkbookPage facetsWorkbookPage;
// If you want to add the enumerations tab to this facets tab
//  EnumsWorkbookPage enumsWorkbookPage;
  PatternsWorkbookPage patternsWorkbookPage;

  /**
   * 
   */
  public FacetsSection()
  {
    super();
  }
  
  /**
   * @see org.eclipse.wst.common.ui.properties.ITabbedPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.wst.common.ui.properties.TabbedPropertySheetWidgetFactory)
   */
  public void createControls(Composite parent, TabbedPropertySheetWidgetFactory factory)
  {
    super.createControls(parent, factory);

    workbook = new XSDWorkbook(parent, SWT.BOTTOM | SWT.FLAT);
    
    facetsWorkbookPage = new FacetsWorkbookPage(workbook, this);
//    enumsWorkbookPage = new EnumsWorkbookPage(workbook);
    patternsWorkbookPage = new PatternsWorkbookPage(workbook);
    facetsWorkbookPage.activate();
//    enumsWorkbookPage.activate();
    patternsWorkbookPage.activate();
    workbook.setSelectedPage(facetsWorkbookPage);
  }
  
  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
    this.part = part;
    this.selection = selection;
  }
  

  /*
   * @see org.eclipse.wst.common.ui.properties.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    Object input = getInput();
    
    if (isReadOnly)
    {
      facetViewer.getControl().getParent().setEnabled(false);      
    }
    else
    {
      facetViewer.getControl().getParent().setEnabled(true);
    }

    if (facetViewer != null)
    {
      facetViewer.setInput(input);
    }
    
//    if (enumsWorkbookPage != null)
//    {
//      enumsWorkbookPage.setInput(input);
//    }
    
    if (patternsWorkbookPage != null)
    {
      patternsWorkbookPage.setInput(input);
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
  

  /* General Facets Page */
  
  class FacetsWorkbookPage extends XSDWorkbookPage
  {
    FacetsSection facetsSection;
    Composite page1;
    
    public FacetsWorkbookPage(XSDWorkbook workbook, FacetsSection facetsSection)
    {
      super(workbook);
      this.getTabItem().setText(XSDEditorPlugin.getXSDString("_UI_LABEL_FACETS")); //$NON-NLS-1$
      this.facetsSection = facetsSection;
    }

    public Control createControl (Composite parent)
    {
      page1 = getWidgetFactory().createFlatFormComposite(parent);

      FormData data = new FormData();
      data.left = new FormAttachment(0, 0);
      data.right = new FormAttachment(100, 0);
      data.top = new FormAttachment(0, 0);
      data.bottom = new FormAttachment(100, 0);
      
      facetViewer = new FacetViewer(page1, facetsSection);
      facetViewer.setInput(getInput());
      facetViewer.getControl().setLayoutData(data);
                  
      return page1;
    }
  }

  /* Enumerations Page */
  
//  class EnumsWorkbookPage extends XSDWorkbookPage implements SelectionListener
//  {
//    EnumerationsTableViewer enumerationsTable;
//    Button addButton;
//    Button deleteButton;
//    
//    public EnumsWorkbookPage(XSDWorkbook workbook)
//    {
//      super(workbook);
//      this.getTabItem().setText("Enumerations");
//    }
//
//    public Control createControl (Composite parent)
//    {
//      Composite composite = getWidgetFactory().createFlatFormComposite(parent);
//      FormData data;
//      
//      addButton = getWidgetFactory().createButton(composite, "Add", SWT.PUSH);
//      deleteButton = getWidgetFactory().createButton(composite, "Delete", SWT.PUSH);
//      enumerationsTable = new EnumerationsTableViewer(getWidgetFactory().createTable(composite, SWT.MULTI | SWT.FULL_SELECTION));
//      
//      enumerationsTable.setInput(getInput());
//      Table table = enumerationsTable.getTable();
//      table.addSelectionListener(this);
//
//      data = new FormData();
//      data.left = new FormAttachment(deleteButton, 0, SWT.LEFT);
//      data.right = new FormAttachment(100, 0);
//      data.top = new FormAttachment(0, 0);
//      data.bottom = new FormAttachment(deleteButton, 0); //-ITabbedPropertyConstants.VSPACE);
//      addButton.setLayoutData(data);
//      addButton.addSelectionListener(this);
//      
//      data = new FormData();
//      data.left = new FormAttachment(table, +ITabbedPropertyConstants.HSPACE);
//      data.right = new FormAttachment(100, 0);
//      data.top = new FormAttachment(addButton, 0);
////      data.bottom = new FormAttachment(deleteButton, +ITabbedPropertyConstants.VSPACE);
//      deleteButton.setLayoutData(data);
//      deleteButton.setEnabled(false);
//      deleteButton.addSelectionListener(this);
//      
//      data = new FormData();
//      data.left = new FormAttachment(0, 0);
//      data.right = new FormAttachment(85, 0);
//      data.top = new FormAttachment(0, 0);
//      data.bottom = new FormAttachment(100, 0);
//      table.setLayoutData(data);
//      
//      return composite;
//    }
//    
//    public void setInput(Object input)
//    {
//      enumerationsTable.setInput(input);
//    }
//    
//    public void widgetSelected(SelectionEvent e)
//    {
//      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)getInput();
//      Element element = st.getElement();
//      if (e.widget == addButton)
//      {
//        XSDDOMHelper helper = new XSDDOMHelper();
//
//        int variety = st.getVariety().getValue();
//        Node varietyElement = null;
//        if (variety == XSDVariety.ATOMIC)
//        {
//          varietyElement = helper.getChildNode(element, XSDConstants.RESTRICTION_ELEMENT_TAG);
//        }
//        else if (variety == XSDVariety.UNION)
//        {
//          varietyElement = helper.getChildNode(element, XSDConstants.UNION_ELEMENT_TAG);
//        }
//        else if (variety == XSDVariety.LIST)
//        {
//          varietyElement = helper.getChildNode(element, XSDConstants.LIST_ELEMENT_TAG);
//        }
//              
//        if (varietyElement != null)
//        {
//          java.util.List attributes = new ArrayList();
//          attributes.add(new DOMAttribute(XSDConstants.VALUE_ATTRIBUTE, ""));
//          beginRecording("Add Enumeration", element);
//          Action action = getNewElementAction(XSDConstants.ENUMERATION_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_ENUM"), attributes, (Element)varietyElement, null);
//          action.run();
//          st.setElement(element);
//
//          endRecording(element);
//          enumerationsTable.refresh();
//          attributes = null;
//        }
//      }
//      else if (e.widget == deleteButton)
//      {
//        StructuredSelection selection = (StructuredSelection)enumerationsTable.getSelection();
//        if (selection != null)
//        {
//          Iterator i = selection.iterator();
//          beginRecording("Delete Enumeration", element);
//          while (i.hasNext())
//          {
//            Object obj = i.next();
//            if (obj != null)
//            {
//              if (obj instanceof XSDEnumerationFacet)
//              {
//                XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)obj;
//      
//                // I have to update using DOM
//                XSDDOMHelper.removeNodeAndWhitespace(enumFacet.getElement());            
//                
//              }
//            }
//          }
//          enumerationsTable.refresh();
//          st.setElement(element);
//          endRecording(element);
//        }
//      }
//      else if (e.widget == enumerationsTable.getTable())
//      {
//        StructuredSelection selection = (StructuredSelection)enumerationsTable.getSelection();
//        if (selection.getFirstElement() != null)
//        {
//          deleteButton.setEnabled(true);
//        }
//        else
//        {
//          deleteButton.setEnabled(false);
//        }
//      }
//      
//    }
//
//    public void widgetDefaultSelected(SelectionEvent e)
//    {
//      
//    }
//  }
//  
//  class EnumerationsTableViewer extends NavigableTableViewer implements ICellModifier
//  {
//    protected String[] columnProperties = {"Enumeration"};
//
//    protected CellEditor[] cellEditors;
//
//    Table table;
//    
//    public EnumerationsTableViewer(Table table)
//    {
//      super(table);
//      table = getTable();
//      
//      table.setLinesVisible(true);
//      
//      setContentProvider(new EnumerationsTableContentProvider());
//      setLabelProvider(new EnumerationsTableLabelProvider());
//      setColumnProperties(columnProperties);
//
//      setCellModifier(this);
//
//      TableColumn column = new TableColumn(table, SWT.NONE, 0);
//      column.setText(columnProperties[0]);
//      column.setAlignment(SWT.LEFT);
// 
//      cellEditors = new CellEditor[1];
//
//      TableLayout layout = new TableLayout();
//      ColumnWeightData data = new ColumnWeightData(100);
//      layout.addColumnData(data);
//      cellEditors[0] = new TextCellEditor(table);
//
//      getTable().setLayout(layout);
//      setCellEditors(cellEditors);
//    }
//    
//    public boolean canModify(Object element, String property)
//    {
//      return true;
//    }
//
//    public void modify(Object element, String property, Object value)
//    {
//      if (element instanceof TableItem && (value != null))
//      {
//        TableItem item = (TableItem)element;
//        
//        Element simpleTypeElement = ((XSDSimpleTypeDefinition)getInput()).getElement();
//        FacetsSection.this.beginRecording(XSDEditorPlugin.getXSDString("_UI_ENUM_VALUE_CHANGE"), simpleTypeElement);
//        
//        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)item.getData();
//        enumFacet.setLexicalValue((String)value);
//        item.setData(enumFacet);
//        item.setText((String)value);
//        FacetsSection.this.endRecording(simpleTypeElement);
//      }
//    }    
//
//    public Object getValue(Object element, String property)
//    {
//      if (element instanceof XSDEnumerationFacet)
//      {
//        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet)element;
//        String value = enumFacet.getLexicalValue();
//        if (value == null) value = "";
//        return value;
//      }
//      return "";
//    }
//
//  }
//    
//  class EnumerationsTableContentProvider implements IStructuredContentProvider
//  {
//    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
//    {
//    }
//
//    public java.lang.Object[] getElements(java.lang.Object inputElement)
//    {
//      java.util.List list = new ArrayList();
//      if (inputElement instanceof XSDSimpleTypeDefinition)
//      {
//        XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)inputElement;
//        return st.getEnumerationFacets().toArray();
//      }
//      return list.toArray();
//    }
//
//    public void dispose()
//    {
//    }
//  }
//
//  class EnumerationsTableLabelProvider extends LabelProvider implements ITableLabelProvider
//  {
//    public EnumerationsTableLabelProvider()
//    {
//      
//    }
//    
//    public Image getColumnImage(Object element, int columnIndex)
//    {
//      return XSDEditorPlugin.getXSDImage("icons/XSDSimpleEnum.gif");
//    }
//    
//    public String getColumnText(Object element, int columnIndex)
//    {
//      if (element instanceof XSDEnumerationFacet)
//      {
//        XSDEnumerationFacet enum = (XSDEnumerationFacet)element;
//        String value = enum.getLexicalValue();
//        if (value == null) value = "";
//        return value;
//      }
//      return "";
//    }
//
//  }

  /* Patterns Page */
  
  class PatternsWorkbookPage extends XSDWorkbookPage implements SelectionListener, Listener
  {
    PatternsTableViewer patternsTable;
    Button addButton;
    Button deleteButton;
    Button editButton;
    Composite composite;
    
    public PatternsWorkbookPage(XSDWorkbook workbook)
    {
      super(workbook);
      this.getTabItem().setText(XSDEditorPlugin.getXSDString("_UI_LABEL_PATTERNS")); //$NON-NLS-1$
    }

    public Control createControl (Composite parent)
    {
      composite = getWidgetFactory().createFlatFormComposite(parent);
      FormData data;

      patternsTable = new PatternsTableViewer(getWidgetFactory().createTable(composite, SWT.MULTI | SWT.FULL_SELECTION));
      patternsTable.setInput(getInput());
      Table table = patternsTable.getTable();
      table.addSelectionListener(this);

      addButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_ADD"), SWT.PUSH); //$NON-NLS-1$
      editButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_LABEL_EDIT"), SWT.PUSH); //$NON-NLS-1$
      deleteButton = getWidgetFactory().createButton(composite, XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE_INCLUDE"), SWT.PUSH); //$NON-NLS-1$

      data = new FormData();
      data.left = new FormAttachment(100, -100);
      data.right = new FormAttachment(100, 0);
      data.top = new FormAttachment(0, 0);
      addButton.setLayoutData(data);
      addButton.addSelectionListener(this);

      data = new FormData();
      data.left = new FormAttachment(addButton, 0, SWT.LEFT);
      data.right = new FormAttachment(100, 0);
      data.top = new FormAttachment(addButton, 0);
      editButton.setLayoutData(data);
      editButton.setEnabled(false);
      editButton.addSelectionListener(this);
      
      data = new FormData();
      data.left = new FormAttachment(addButton, 0, SWT.LEFT);
      data.right = new FormAttachment(100, 0);
      data.top = new FormAttachment(editButton, 0);
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
      
      return composite;
    }
    
    public void handleEvent(Event event)
    {
      Table table = patternsTable.getTable();
      if (event.type == SWT.Resize && event.widget == table)
      {
        TableColumn tableColumn = table.getColumn(0);
        tableColumn.setWidth(table.getSize().x);
      }
    }

    
    public void setInput(Object input)
    {
      patternsTable.setInput(input);
      if (isReadOnly)
      {
        composite.setEnabled(false);
      }
      else
      {
        composite.setEnabled(true);
      }
    }
    
    public void widgetSelected(SelectionEvent e)
    {
      XSDSimpleTypeDefinition st = (XSDSimpleTypeDefinition)getInput();
      Element element = st.getElement();

      if (e.widget == addButton)
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
          Shell shell = Display.getCurrent().getActiveShell();

          String initialValue = ""; //$NON-NLS-1$
          RegexWizard wizard = new RegexWizard(initialValue);

          WizardDialog wizardDialog = new WizardDialog(shell, wizard);
          wizardDialog.setBlockOnOpen(true);
          wizardDialog.create();
          
          int result = wizardDialog.open();

          if (result == Window.OK)
          {
            String newPattern = wizard.getPattern();
            
            java.util.List attributes = new ArrayList();
            attributes.add(new DOMAttribute(XSDConstants.VALUE_ATTRIBUTE, newPattern));
            beginRecording(XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_PATTERN"), element); //$NON-NLS-1$
            Action action = getNewElementAction(XSDConstants.PATTERN_ELEMENT_TAG, XSDEditorPlugin.getXSDString("_UI_ACTION_ADD_PATTERN"), attributes, (Element)varietyElement, null); //$NON-NLS-1$
            action.run();
            st.setElement(element);
            endRecording(element);
            patternsTable.refresh();
            attributes = null;
          }
        }
      }
      else if (e.widget == deleteButton)
      {
        StructuredSelection selection = (StructuredSelection)patternsTable.getSelection();
        if (selection != null)
        {
          Iterator i = selection.iterator();
          beginRecording(XSDEditorPlugin.getXSDString("_UI_ACTION_DELETE_INCLUDE"), element); // Reword ?
          while (i.hasNext())
          {
            Object obj = i.next();
            if (obj != null)
            {
              if (obj instanceof XSDPatternFacet)
              {
                XSDPatternFacet patternFacet = (XSDPatternFacet)obj;
      
                // I have to update using DOM
                XSDDOMHelper.removeNodeAndWhitespace(patternFacet.getElement());            
              }
            }
          }
          st.setElement(element);
          endRecording(element);
          patternsTable.refresh();
          if (patternsTable.getTable().getItemCount() == 0)
          {
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
          }
        }
      }
      else if (e.widget == editButton)
      {
        StructuredSelection selection = (StructuredSelection)patternsTable.getSelection();
        if (selection != null)
        {
          Object obj = selection.getFirstElement();
          if (obj instanceof XSDPatternFacet)
          {
            XSDPatternFacet pattern = (XSDPatternFacet)obj;
            String initialValue = pattern.getLexicalValue();
            if (initialValue == null)
            {
              initialValue = ""; //$NON-NLS-1$
            }

            Shell shell = Display.getCurrent().getActiveShell();

            RegexWizard wizard = new RegexWizard(initialValue);

            WizardDialog wizardDialog = new WizardDialog(shell, wizard);
            wizardDialog.setBlockOnOpen(true);
            wizardDialog.create();
            
            int result = wizardDialog.open();

            if (result == Window.OK)
            {
              String newPattern = wizard.getPattern();
              beginRecording(XSDEditorPlugin.getXSDString("_UI_PATTERN_VALUE_CHANGE"), element); //$NON-NLS-1$
              element.setAttribute(XSDConstants.VALUE_ATTRIBUTE, newPattern);
              pattern.setLexicalValue(newPattern);
              endRecording(element);
              patternsTable.refresh();
            }
          }
        }
      }
      else if (e.widget == patternsTable.getTable())
      {
        StructuredSelection selection = (StructuredSelection)patternsTable.getSelection();
        if (selection.getFirstElement() != null)
        {
          editButton.setEnabled(true);
          deleteButton.setEnabled(true);
        }
        else
        {
          editButton.setEnabled(false);
          deleteButton.setEnabled(false);
        }
      }
      
    }

    public void widgetDefaultSelected(SelectionEvent e)
    {
      
    }
  }

  
  class PatternsTableViewer extends NavigableTableViewer implements ICellModifier
  {
    protected String[] columnProperties = {"Pattern"};

    protected CellEditor[] cellEditors;

    Table table;
    
    public PatternsTableViewer(Table table)
    {
      super(table);
      table = getTable();
      
      table.setLinesVisible(true);
      
      setContentProvider(new PatternsTableContentProvider());
      setLabelProvider(new PatternsTableLabelProvider());
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
        FacetsSection.this.beginRecording(XSDEditorPlugin.getXSDString("_UI_PATTERN_VALUE_CHANGE"), simpleTypeElement); //$NON-NLS-1$
        
        XSDPatternFacet patternFacet = (XSDPatternFacet)item.getData();
        patternFacet.setLexicalValue((String)value);

        item.setData(patternFacet);
        item.setText((String)value);
        FacetsSection.this.endRecording(simpleTypeElement);
      }
    }    

    public Object getValue(Object element, String property)
    {
      if (element instanceof XSDPatternFacet)
      {
        XSDPatternFacet patternFacet = (XSDPatternFacet)element;
        String value = patternFacet.getLexicalValue();
        if (value == null) value = ""; //$NON-NLS-1$
        return value;
      }
      return ""; //$NON-NLS-1$
    }

  }
    
  class PatternsTableContentProvider implements IStructuredContentProvider
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
        return st.getPatternFacets().toArray();
      }
      return list.toArray();
    }

    public void dispose()
    {
    }
  }

  class PatternsTableLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    public PatternsTableLabelProvider()
    {
      
    }
    
    public Image getColumnImage(Object element, int columnIndex)
    {
      return XSDEditorPlugin.getXSDImage("icons/XSDSimplePattern.gif"); //$NON-NLS-1$
    }
    
    public String getColumnText(Object element, int columnIndex)
    {
      if (element instanceof XSDPatternFacet)
      {
        XSDPatternFacet pattern = (XSDPatternFacet)element;
        String value = pattern.getLexicalValue();
        if (value == null) value = ""; //$NON-NLS-1$
        return value;
      }
      return ""; //$NON-NLS-1$
    }

  }
}
