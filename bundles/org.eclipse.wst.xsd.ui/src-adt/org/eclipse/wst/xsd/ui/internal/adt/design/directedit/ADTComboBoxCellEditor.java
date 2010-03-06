/*******************************************************************************
 * Copyright (c) 2001, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.adt.design.directedit;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.wst.xsd.ui.internal.adt.edit.ComponentReferenceEditManager;
import org.eclipse.wst.xsd.ui.internal.adt.edit.IComponentDialog;
import org.eclipse.wst.xsd.ui.internal.editor.Messages;

/*
 * This wraps the ComboBoxCellEditor.
 * We need to apply and deactivate the combo on a single click (not on a double click like
 * the ComboBoxCellEditor).
 */
public class ADTComboBoxCellEditor extends ComboBoxCellEditor
{
  private static final int adtDefaultStyle = SWT.NONE;

  /**
   * Used to determine if the value should be applied to the cell.
   */
  private boolean continueApply;
  private boolean isTraversing = false;
  private Object selectedValue;
  private ComponentReferenceEditManager componentReferenceEditManager;
  
  // This prevents the cell editor from being deactivated while handling the New or Browse selection.
  private boolean isHandlingSelection = false;

  /**
   * Creates a new cell editor with a combo containing the given list of choices
   * and parented under the given control. The cell editor value is the
   * zero-based index of the selected item. Initially, the cell editor has no
   * cell validator and the first item in the list is selected.
   * 
   * @param parent
   *          the parent control
   * @param items
   *          the list of strings for the combo box
   */
  public ADTComboBoxCellEditor(Composite parent, String[] items, ComponentReferenceEditManager editManager)
  {
    super(parent, items, adtDefaultStyle | SWT.READ_ONLY);
    setItems(items);
    componentReferenceEditManager = editManager;
  }

  private void removeListeners(CCombo comboBox, int event)
  {
    Listener [] listeners = comboBox.getListeners(event);
    int length = listeners.length;
    for (int i = 0; i < length; i++)
    {
      Listener l = listeners[i];
      if (l instanceof TypedListener)
      {
        TypedListener typedListener = (TypedListener)l;
        String className = typedListener.getEventListener().getClass().getCanonicalName();
        // It's possible that there are other typed listeners added to the CCombo.
        // Currently there are none, but as an extra check, I want to ensure 
        // I'm removing the ones added from the inherited class.
        // I've tested this and I know it removes the following:
        // org.eclipse.jface.viewers.ComboBoxCellEditor$2
        // org.eclipse.jface.viewers.ComboBoxCellEditor$4
        // which are indeed the ones I'm customizing
        if (className != null && className.contains("org.eclipse.jface.viewers.ComboBoxCellEditor"))
        {
          comboBox.removeListener(event, l);
        }
      }
    }
  }
  
  /*
   * (non-Javadoc) Method declared on CellEditor.
   */
  protected Control createControl(Composite parent)
  {
    CCombo comboBox = (CCombo)super.createControl(parent);

    comboBox.setFont(parent.getFont());

    // Need to remove the listeners added from ComboBoxCellEditor
    removeListeners(comboBox, SWT.Selection);
    removeListeners(comboBox, SWT.DefaultSelection);
    removeListeners(comboBox, SWT.FocusIn);
    removeListeners(comboBox, SWT.FocusOut);
    
    // Now add our custom listeners
    comboBox.addSelectionListener(new SelectionAdapter()
    {
      public void widgetDefaultSelected(SelectionEvent event)
      {
        // Want the same behaviour since hitting return on Browse or New should launch the dialogs,
        // and not immediately apply the value
//      applyEditorValueAndDeactivate();
        widgetSelected(event);
      }

      public void widgetSelected(SelectionEvent event)
      {
        continueApply = true;
        CCombo comboBox = getCCombo();
        isHandlingSelection = true;
        Object newValue = null;

        try
        {
          int selection = comboBox.getSelectionIndex();
          if (isTraversing)
          {
            isTraversing = false;
            return;
          }

          String[] items = getItems();
          String stringSelection = items[selection];

          if (stringSelection.equals(Messages._UI_COMBO_BROWSE))
          {
            continueApply = true;
            newValue = invokeDialog(componentReferenceEditManager.getBrowseDialog());
          }
          else if (stringSelection.equals(Messages._UI_COMBO_NEW))
          {
            continueApply = true;
            newValue = invokeDialog(componentReferenceEditManager.getNewDialog());
          }
        }
        finally
        {
          isHandlingSelection = false;
        }

        if (continueApply)
        {
          if (newValue == null)
          {
            int index = comboBox.getSelectionIndex();
            if (index != -1)
            {
              selectedValue = comboBox.getItem(index);
            }
          }
          else
          {
            selectedValue = newValue;
          }

          applyEditorValueAndDeactivate();
          focusLost();
        }
      }
    });

    comboBox.addTraverseListener(new TraverseListener()
    {
      public void keyTraversed(TraverseEvent e)
      {
        if (e.detail == SWT.TRAVERSE_ARROW_NEXT || e.detail == SWT.TRAVERSE_ARROW_PREVIOUS)
        {
          isTraversing = true;
        }
      }
    });

    comboBox.addFocusListener(new FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        if (!isHandlingSelection)
          ADTComboBoxCellEditor.this.focusLost();
      }
    });
    return comboBox;
  }

  private Object invokeDialog(IComponentDialog dialog)
  {
    Object newValue = null;

    if (dialog == null)
    {
      return null;
    }

    //dialog.setInitialComponent(setObject);
    if (dialog.createAndOpen() == Window.OK)
    {
      newValue = dialog.getSelectedComponent();
    }
    else
    {
      continueApply = false;
      focusLost();
    }

    return newValue;
  }

  public Object getSelectedValue()
  {
    return selectedValue;
  }

  protected CCombo getCCombo()
  {
    return (CCombo)getControl();
  }
  
///////// ComboBox cell editor  

  void applyEditorValueAndDeactivate()
  {
    // must set the selection before getting value
    CCombo comboBox = getCCombo();
    String[] items = getItems();

    int selection = comboBox.getSelectionIndex();
    Object newValue = doGetValue();
    markDirty();
    boolean isValid = isCorrect(newValue);
    setValueValid(isValid);

    if (!isValid)
    {
      // Only format if the 'index' is valid
      if (items.length > 0 && selection >= 0 && selection < items.length)
      {
        // try to insert the current value into the error message.
        setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { items[selection] }));
      }
      else
      {
        // Since we don't have a valid index, assume we're using an
        // 'edit'
        // combo so format using its text value
        setErrorMessage(MessageFormat.format(getErrorMessage(), new Object[] { comboBox.getText() }));
      }
    }

    fireApplyEditorValue();
    deactivate();
  }
}
