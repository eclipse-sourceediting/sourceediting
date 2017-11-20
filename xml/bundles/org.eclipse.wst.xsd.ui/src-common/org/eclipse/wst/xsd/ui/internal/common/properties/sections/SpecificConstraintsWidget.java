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

import org.eclipse.gef.commands.CommandStack;
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.eclipse.wst.common.ui.internal.viewers.NavigableTableViewer;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddEnumerationsCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.ChangeToLocalSimpleTypeCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.DeleteCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.SetXSDFacetValueCommand;
import org.eclipse.wst.xsd.ui.internal.common.commands.UpdateXSDPatternFacetCommand;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.common.util.XSDCommonUIUtils;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorPlugin;
import org.eclipse.wst.xsd.ui.internal.widgets.EnumerationsDialog;
import org.eclipse.wst.xsd.ui.internal.wizards.RegexWizard;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDPatternFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

public class SpecificConstraintsWidget implements SelectionListener, Listener
{
  public static int ENUMERATION = 0;
  public static int PATTERN = 1;
  
  int kind;
  ConstraintsTableViewer constraintsTableViewer;
  Button addButton;
  Button addUsingDialogButton;
  Button deleteButton;
  Button editButton;
  Composite composite;
  boolean isEnabled;
  TabbedPropertySheetWidgetFactory factory;
  XSDSimpleTypeDefinition input;
  XSDFeature feature;
  boolean isReadOnly;
  CommandStack commandStack;
  XSDFacetSection facetSection;

  /**
   * @deprecated
   * @param composite
   * @param factory
   * @param feature
   * @param input
   * @param facetSection
   */
  public SpecificConstraintsWidget(Composite composite, TabbedPropertySheetWidgetFactory factory, XSDFeature feature, XSDSimpleTypeDefinition input, XSDFacetSection facetSection)
  {
    this(composite, factory, feature, input, facetSection, ENUMERATION);
  }

  public SpecificConstraintsWidget(Composite composite, TabbedPropertySheetWidgetFactory factory, XSDFeature feature, XSDSimpleTypeDefinition input, XSDFacetSection facetSection, int kind)
  {
    this.factory = factory;
    this.input = input;
    this.composite = composite;
    this.feature = feature;
    this.facetSection = facetSection;
    this.kind = kind;
    
    createControl(composite);
  }

  public void setCommandStack(CommandStack commandStack)
  {
    this.commandStack = commandStack; 
  }

  public void setIsReadOnly(boolean isReadOnly)
  {
    this.isReadOnly = isReadOnly;
  }

  public TabbedPropertySheetWidgetFactory getWidgetFactory()
  {
    return factory;
  }
  
  public Control getControl()
  {
    return composite;
  }
  
  public void setEnabled(boolean isEnabled)
  {
    this.isEnabled = isEnabled;
    addButton.setEnabled(isEnabled);
    addUsingDialogButton.setEnabled(isEnabled);
    editButton.setEnabled(isEnabled);
    constraintsTableViewer.getTable().setEnabled(isEnabled);
    composite.setEnabled(isEnabled);
  }

  public Control createControl(Composite parent)
  {
    composite = factory.createFlatFormComposite(parent);
    GridData data = new GridData();

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 2;
    composite.setLayout(gridLayout);

    constraintsTableViewer = new ConstraintsTableViewer(getWidgetFactory().createTable(composite, SWT.MULTI | SWT.FULL_SELECTION));
    constraintsTableViewer.setInput(input);
    Table table = constraintsTableViewer.getTable();
    table.addSelectionListener(this);
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = true;
    data.widthHint = 150;
    data.grabExcessVerticalSpace = true;
    table.setLayoutData(data);
    table.addListener(SWT.Resize, this);
    
    Composite buttonComposite = getWidgetFactory().createComposite(composite, SWT.FLAT);
    GridLayout buttonCompositeLayout = new GridLayout();
    buttonCompositeLayout.marginTop = 0;
    buttonCompositeLayout.marginBottom = 0;
    buttonCompositeLayout.numColumns = 1;
    buttonComposite.setLayout(buttonCompositeLayout);
    data = new GridData();
    data.horizontalAlignment = GridData.FILL;
    data.grabExcessHorizontalSpace = false;
    buttonComposite.setLayoutData(data);

    
    addButton = getWidgetFactory().createButton(buttonComposite, Messages._UI_ACTION_ADD, SWT.PUSH);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.BEGINNING;
    addButton.setLayoutData(data);
    addButton.addSelectionListener(this);
    
    addUsingDialogButton = getWidgetFactory().createButton(buttonComposite, Messages._UI_ACTION_ADD_WITH_DOTS, SWT.PUSH);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    data.verticalAlignment = GridData.BEGINNING;
    addUsingDialogButton.setLayoutData(data);
    addUsingDialogButton.addSelectionListener(this);

    editButton = getWidgetFactory().createButton(buttonComposite, Messages._UI_ACTION_EDIT_WITH_DOTS, SWT.PUSH);
    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    editButton.setLayoutData(data);
    editButton.addSelectionListener(this);
    
    
    deleteButton = getWidgetFactory().createButton(buttonComposite, Messages._UI_ACTION_DELETE, SWT.PUSH);

    data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    deleteButton.setLayoutData(data);
    deleteButton.addSelectionListener(this);

    setButtonStates(kind);
    return composite;
  }

  public void handleEvent(Event event)
  {
    Table table = constraintsTableViewer.getTable();
    if (event.type == SWT.Resize && event.widget == table)
    {
      TableColumn tableColumn = table.getColumn(0);
      tableColumn.setWidth(table.getSize().x);
    }
  }

  public void setInput(Object input)
  {
    constraintsTableViewer.setInput(input);
    if (isReadOnly)
    {
      composite.setEnabled(false);
    }
    else
    {
      composite.setEnabled(true);
    }
//    constraintsTableViewer.refresh();
  }

  public int getConstraintKind()
  {
	  return this.kind;
  }
  
  protected void setButtonStates(int kind)
  {
    boolean add, addUsing, delete, edit;

    boolean listHasItems = false;
    StructuredSelection selection = (StructuredSelection) constraintsTableViewer.getSelection();
    if (selection != null)
    {
      if (selection.toList().size() > 0)
      {
        listHasItems = true;
      }
    }

    if (kind == ENUMERATION)
    {
      add = true;
      addUsing = true;
      edit = false;
    }
    else if (kind == PATTERN)
    {
      add = false;
      addUsing = true;
      edit = listHasItems;
    }
    else
    {
      add = true;
      addUsing = true;
      edit = true;
    }
    delete = listHasItems;

    if (!addButton.isDisposed())
    {
      addButton.setEnabled(add);
    }
    if (!addUsingDialogButton.isDisposed())
    {
      addUsingDialogButton.setEnabled(addUsing);
    }
    if (!deleteButton.isDisposed())
    {
      deleteButton.setEnabled(delete);
    }
    if (!editButton.isDisposed())
    {
      editButton.setEnabled(edit);
    }
  }  
  
  public void widgetSelected(SelectionEvent e)
  {
    XSDSimpleTypeDefinition st = input;
    if (e.widget == addButton)
    {
      List enumList = st.getEnumerationFacets();
      String newName = XSDCommonUIUtils.createUniqueEnumerationValue("value", enumList); //$NON-NLS-1$

      if (kind == ENUMERATION)
      {
        CompoundCommand compoundCommand = new CompoundCommand();
        XSDSimpleTypeDefinition targetSimpleType = null;
        if (feature != null)
        {
          XSDSimpleTypeDefinition anonymousSimpleType = XSDCommonUIUtils.getAnonymousSimpleType(feature, input);
          if (anonymousSimpleType == null)
          {
            anonymousSimpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
            anonymousSimpleType.setBaseTypeDefinition(input);

            ChangeToLocalSimpleTypeCommand changeToAnonymousCommand = new ChangeToLocalSimpleTypeCommand(Messages._UI_ACTION_CHANGE_PATTERN, feature);
            changeToAnonymousCommand.setAnonymousSimpleType(anonymousSimpleType);
            compoundCommand.add(changeToAnonymousCommand);
            input = anonymousSimpleType;
          }
          targetSimpleType = anonymousSimpleType;
        }
        else
        {
          targetSimpleType = input;
        }

        AddEnumerationsCommand command = new AddEnumerationsCommand(Messages._UI_ACTION_ADD_ENUMERATION, targetSimpleType);
        command.setValue(newName);
        compoundCommand.add(command);
        commandStack.execute(compoundCommand);
        setInput(input);
        constraintsTableViewer.refresh();
        int newItemIndex = constraintsTableViewer.getTable().getItemCount() - 1;
        constraintsTableViewer.editElement(constraintsTableViewer.getElementAt(newItemIndex), 0);
      }
    }
    else if (e.widget == addUsingDialogButton)
    {
      Display display = Display.getCurrent();
      // if it is null, get the default one
      display = display == null ? Display.getDefault() : display;
      Shell shell = display.getActiveShell();

      if (kind == PATTERN)
      {
        String initialValue = ""; //$NON-NLS-1$
        RegexWizard wizard = new RegexWizard(initialValue);

        WizardDialog wizardDialog = new WizardDialog(shell, wizard);
        wizardDialog.setBlockOnOpen(true);
        wizardDialog.create();

        int result = wizardDialog.open();

        if (result == Window.OK)
        {
          String newPattern = wizard.getPattern();
          CompoundCommand compoundCommand = new CompoundCommand();
          XSDSimpleTypeDefinition targetSimpleType = null;
          if (feature != null)
          {
            XSDSimpleTypeDefinition anonymousSimpleType = XSDCommonUIUtils.getAnonymousSimpleType(feature, input);
            if (anonymousSimpleType == null)
            {
              anonymousSimpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
              anonymousSimpleType.setBaseTypeDefinition(input);

              ChangeToLocalSimpleTypeCommand changeToAnonymousCommand = new ChangeToLocalSimpleTypeCommand(Messages._UI_ACTION_CHANGE_PATTERN, feature);
              changeToAnonymousCommand.setAnonymousSimpleType(anonymousSimpleType);
              compoundCommand.add(changeToAnonymousCommand);
              input = anonymousSimpleType;
            }
            targetSimpleType = anonymousSimpleType;
          }
          else
          {
            targetSimpleType = input;
          }
          
          UpdateXSDPatternFacetCommand command = new UpdateXSDPatternFacetCommand(Messages._UI_ACTION_ADD_PATTERN, targetSimpleType, UpdateXSDPatternFacetCommand.ADD);
          command.setValue(newPattern);
          setInput(input);
          compoundCommand.add(command);
          commandStack.execute(compoundCommand);
          facetSection.doSetInput();
        }
        constraintsTableViewer.refresh();
      }
      else
      {
        EnumerationsDialog dialog = new EnumerationsDialog(shell);
        dialog.setBlockOnOpen(true);
        int result = dialog.open();

        if (result == Window.OK)
        {
          String text = dialog.getText();
          String delimiter = dialog.getDelimiter();
          StringTokenizer tokenizer = new StringTokenizer(text, delimiter);
          CompoundCommand compoundCommand = new CompoundCommand(Messages._UI_ACTION_ADD_ENUMERATIONS);
          
          XSDSimpleTypeDefinition targetSimpleType = null;
          if (feature != null)
          {
            XSDSimpleTypeDefinition anonymousSimpleType = XSDCommonUIUtils.getAnonymousSimpleType(feature, input);
            if (anonymousSimpleType == null)
            {
              anonymousSimpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
              anonymousSimpleType.setBaseTypeDefinition(input);

              ChangeToLocalSimpleTypeCommand changeToAnonymousCommand = new ChangeToLocalSimpleTypeCommand("", feature); //$NON-NLS-1$
              changeToAnonymousCommand.setAnonymousSimpleType(anonymousSimpleType);
              compoundCommand.add(changeToAnonymousCommand);
              input = anonymousSimpleType;
            }
            targetSimpleType = anonymousSimpleType;
          }
          else
          {
            targetSimpleType = input;
          }

          while (tokenizer.hasMoreTokens())
          {
            String token = tokenizer.nextToken();
            if (dialog.isPreserveWhitespace() == false)
            {
              token = token.trim();
            }
            AddEnumerationsCommand command = new AddEnumerationsCommand(Messages._UI_ACTION_ADD_ENUMERATIONS, targetSimpleType);
            command.setValue(token);
            compoundCommand.add(command);
          }
          commandStack.execute(compoundCommand);
        }
        //setInput(input);
        facetSection.doSetInput();
        constraintsTableViewer.refresh();
      }
    }
    else if (e.widget == deleteButton)
    {
      StructuredSelection selection = (StructuredSelection) constraintsTableViewer.getSelection();
      CompoundCommand compoundCommand = new CompoundCommand();
      if (selection != null)
      {
        Iterator i = selection.iterator();
        if (selection.size() > 0)
        {
          compoundCommand.setLabel(Messages._UI_ACTION_DELETE_CONSTRAINTS);
        }
        else
        {
          compoundCommand.setLabel(Messages._UI_ACTION_DELETE_PATTERN);
        }
        while (i.hasNext())
        {
          Object obj = i.next();
          if (obj != null)
          {
            if (obj instanceof XSDPatternFacet)
            {
              UpdateXSDPatternFacetCommand command = new UpdateXSDPatternFacetCommand("", input, UpdateXSDPatternFacetCommand.DELETE); //$NON-NLS-1$
              command.setPatternToEdit((XSDPatternFacet)obj);
              compoundCommand.add(command);
            }
            else if (obj instanceof XSDEnumerationFacet)
            {
              XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) obj;
              DeleteCommand deleteCommand = new DeleteCommand(Messages._UI_ACTION_DELETE_ENUMERATION, enumFacet);
              compoundCommand.add(deleteCommand);
            }
          }
        }
        commandStack.execute(compoundCommand);
        constraintsTableViewer.refresh();
      }
    }
    else if (e.widget == editButton)
    {
      StructuredSelection selection = (StructuredSelection) constraintsTableViewer.getSelection();
      if (selection != null)
      {
        Object obj = selection.getFirstElement();
        if (obj instanceof XSDPatternFacet)
        {
          XSDPatternFacet pattern = (XSDPatternFacet) obj;
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
            pattern.setLexicalValue(newPattern);
            constraintsTableViewer.refresh();
          }
        }
      }
    }
    
    setButtonStates(this.kind);
  }
  
  public void widgetDefaultSelected(SelectionEvent e)
  {

  }

  
  public void setConstraintKind(int kind)
  {
    this.kind = kind;
    setButtonStates(kind);
    constraintsTableViewer.setInput(input);
    constraintsTableViewer.refresh();
  }
  
  public void doModify(Object element, String property, Object value)
  {
    setButtonStates(this.kind);
    if (element instanceof TableItem && (value != null))
    {
      TableItem item = (TableItem) element;

      if (item.getData() instanceof XSDPatternFacet)
      {
        XSDPatternFacet patternFacet = (XSDPatternFacet) item.getData();
        patternFacet.setLexicalValue((String) value);

        item.setData(patternFacet);
        item.setText((String) value);
      }
      else if (item.getData() instanceof XSDEnumerationFacet)
      {
        XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) item.getData();
        SetXSDFacetValueCommand command = new SetXSDFacetValueCommand(Messages._UI_ACTION_SET_ENUMERATION_VALUE, enumFacet);
        command.setValue((String) value);
        commandStack.execute(command);
        item.setData(enumFacet);
        item.setText((String) value);
      }
    }
  }
  
  public Object doGetValue(Object element, String property)
  {
    if (element instanceof XSDPatternFacet)
    {
      XSDPatternFacet patternFacet = (XSDPatternFacet) element;
      String value = patternFacet.getLexicalValue();
      if (value == null)
        value = ""; //$NON-NLS-1$
      return value;
    }
    else if (element instanceof XSDEnumerationFacet)
    {
      XSDEnumerationFacet enumFacet = (XSDEnumerationFacet) element;
      String value = enumFacet.getLexicalValue();
      if (value == null)
        value = ""; //$NON-NLS-1$
      return value;
    }

    return ""; //$NON-NLS-1$
  }
  
  class ConstraintsTableViewer extends NavigableTableViewer implements ICellModifier
  {
    protected String[] columnProperties = { Messages._UI_LABEL_PATTERN };

    protected CellEditor[] cellEditors;

    Table table;

    public ConstraintsTableViewer(Table table)
    {
      super(table);
      table = getTable();

      table.setLinesVisible(true);

      setContentProvider(new ConstraintsContentProvider());
      setLabelProvider(new ConstraintsTableLabelProvider());
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
      doModify(element, property, value);
    }

    public Object getValue(Object element, String property)
    {
      return doGetValue(element, property);
    }

  }

  class ConstraintsContentProvider implements IStructuredContentProvider
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
        boolean isDefined = false;
        Iterator iter;
        if (kind == PATTERN)
        {
          iter = st.getPatternFacets().iterator();
        }
        else
        {
          iter = st.getEnumerationFacets().iterator();
        }

        while (iter.hasNext())
        {
          XSDFacet facet = (XSDFacet) iter.next();
          isDefined = (facet.getRootContainer() == facetSection.xsdSchema);
        }

        if (kind == PATTERN)
        {
          if (isDefined)
          {
            return st.getPatternFacets().toArray();
          }
        }
        else
        {
          if (isDefined)
          {
            return st.getEnumerationFacets().toArray();
          }
        }
      }
      return list.toArray();
    }

    public void dispose()
    {
    }
  }

  class ConstraintsTableLabelProvider extends LabelProvider implements ITableLabelProvider
  {
    public ConstraintsTableLabelProvider()
    {

    }

    public Image getColumnImage(Object element, int columnIndex)
    {
      if (kind == PATTERN)
      {
        return XSDEditorPlugin.getXSDImage("icons/XSDSimplePattern.gif"); //$NON-NLS-1$
      }
      else
      {
        return XSDEditorPlugin.getXSDImage("icons/XSDSimpleEnum.gif"); //$NON-NLS-1$
      }
    }

    public String getColumnText(Object element, int columnIndex)
    {
      if (element instanceof XSDPatternFacet)
      {
        XSDPatternFacet pattern = (XSDPatternFacet) element;
        String value = pattern.getLexicalValue();
        if (value == null)
          value = ""; //$NON-NLS-1$
        return value;
      }
      else if (element instanceof XSDEnumerationFacet)
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
