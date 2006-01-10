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
package org.eclipse.wst.xsd.ui.internal.properties;

import java.text.MessageFormat;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.PopupList;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class XSDComboBoxPropertyDescriptor extends PropertyDescriptor
{
	private String[] values;
	
	public XSDComboBoxPropertyDescriptor(Object id, String displayName, String[] valuesArray) {
		super(id, displayName);
		values = valuesArray;
	}
	/**
	 * The <code>ComboBoxPropertyDescriptor</code> implementation of this 
	 * <code>IPropertyDescriptor</code> method creates and returns a new
	 * <code>ComboBoxCellEditor</code>.
	 * <p>
	 * The editor is configured with the current validator if there is one.
	 * </p>
	 */
	public CellEditor createPropertyEditor(Composite parent) {
    CellEditor editor = new XSDComboBoxCellEditor(parent, values);
    //CellEditor editor = new StringComboBoxCellEditor(parent, values);
		if (getValidator() != null)
			editor.setValidator(getValidator());
		return editor;
	}
	
	public class XSDComboBoxCellEditor extends CellEditor
	{
		DynamicCellEditor comboBox;

		private String fSelection;
		protected String[] fItems;
		protected Object fValue;
		int selection;
		Object typeObject;
		
		public void createItems(String[] items)
		{
			fItems = items;
		}

		public String[] getComboBoxItems()
		{
			return fItems;
		}

	/**
	 * Creates a new combo box cell editor with the given choices.
	 */
		 public XSDComboBoxCellEditor(Composite parent, String[] items)
		 {
			 super(parent);
			 fSelection = "";
			 setItems(items);
//			 fText.setText("");
		 }

		public void activate() {
      if (doGetValue() != null)
      {
//			  int i = ((Integer)doGetValue()).intValue();
//			  if (i >= 0)
//		  	{
//		  	  comboBox.setText(fItems[i]);
//		  	}
        comboBox.setText((String)fValue);
			}
		}

		public void deactivate() {
		  super.deactivate();
		}

		public void setItems(String[] items) {
			Assert.isNotNull(items);
			this.fItems = items;
			populateComboBoxItems();
		}

		private void populateComboBoxItems() {
			if (comboBox != null && fItems != null) {
				comboBox.removeAll();
				for (int i = 0; i < fItems.length; i++)
					comboBox.add(fItems[i], i);

				setValueValid(true);
				selection = 0;
			}
		}

	/**
	 * Creates the actual UI representation.
	 */

		protected Control createControl(Composite parent)
		{
			comboBox = new DynamicCellEditor(parent, SWT.READ_ONLY |SWT.NONE | SWT.NO_TRIM);
			comboBox.addKeyListener(new KeyAdapter() {
				// hook key pressed - see PR 14201  
				public void keyPressed(KeyEvent e) {
//					System.out.println("Key e " + e);
					keyReleaseOccured(e);
				}
			});

			comboBox.addSelectionListener(new SelectionAdapter() {
				public void widgetDefaultSelected(SelectionEvent event) {
				}
		
				public void widgetSelected(SelectionEvent event) {
//				  System.out.println("combo selected");
					selection = comboBox.getSelectionIndex();
					if (!comboBox.isDropped()) // allows user to traverse list using keyboard without applying value
					applyEditorValueAndDeactivate();
				}
			});

			comboBox.addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent e) {
//				  System.out.println("TRAVERSE e " + e);
					if (e.detail == SWT.TRAVERSE_ESCAPE || e.detail == SWT.TRAVERSE_RETURN) {
						e.doit = false;
					}
				}
			});
			
      
			comboBox.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					XSDComboBoxCellEditor.this.focusLost();
				}
				public void focusGained(FocusEvent e) {
//				  System.out.println("focusGained");
				}
			});
		  setValueValid(true);
			return comboBox;
		}

		protected void focusLost() {
//		  System.out.println("CELLEDITOR FOCUS LOST");
			if (isActivated()) {
				applyEditorValueAndDeactivate();
			}
		}
		
		protected void keyReleaseOccured(KeyEvent keyEvent) {
			if (keyEvent.character == '\u001b') { // Escape character
			  comboBox.setText(""); // clear text
				fireCancelEditor();
			} else if (keyEvent.character == '\r') { // Return key
				//fireApplyEditorValue();
				applyEditorValueAndDeactivate();
				deactivate();
			}
		}

		void applyEditorValueAndDeactivate() {
			//	must set the selection before getting value
			selection = comboBox.getSelectionIndex();
			if (selection < 0)
			{
			  deactivate();
			  return;
			}
			// Object newValue = new Integer(selection);
      Object newValue = fItems[selection];
			markDirty();
			boolean isValid = isCorrect(newValue);
			setValueValid(isValid);
			if (!isValid) {
				// try to insert the current value into the error message.
				setErrorMessage(
					MessageFormat.format(getErrorMessage(), new Object[] {fItems[selection]})); 
			}
			doSetValue(newValue);
			fireApplyEditorValue();
			deactivate();
		}

    protected Object doGetValue() {
      return fValue;
      // otherwise limits to set of valid values
//      Object index = super.doGetValue();
//      int selection = -1;
//      if (index instanceof Integer)
//        selection = ((Integer) index).intValue();
//      if (selection >= 0)
//        return fItems[selection];
//      else if (getControl() instanceof CCombo) {
//        // retrieve the actual text as the list of valid items doesn't contain the value
//        return ((CCombo) getControl()).getText();
//      }
//      return null;
    }
    private boolean fSettingValue = false;
    protected void doSetValue(Object value) {
      if (fSettingValue)
        return;
      fSettingValue = true;
      if (value instanceof Integer) {
        //super.doSetValue(value);
        fValue = value;
      }
      else {
        String stringValue = value.toString();
        int selection = -1;
        for (int i = 0; i < fItems.length; i++)
          if (fItems[i].equals(stringValue))
            selection = i;
        if (selection >= 0)
          //super.doSetValue(new Integer(selection));
          fValue = stringValue;
        else {
          // super.doSetValue(new Integer(-1));
          // fValue = new Integer(-1);
          fValue = stringValue;
          if (getControl() instanceof CCombo && !stringValue.equals(((CCombo) getControl()).getText())) {
            // update the Text widget
            ((CCombo) getControl()).setText(stringValue);
          }
        }
      }
      fSettingValue = false;
    }		
		
	/**
		* Returns the cell editor's value.
		*/
//		protected Object doGetValue() 
//		{
//			return fValue;
//		}

	/**
	 * Set the focus to the cell editor's UI representation.
	 */
		protected void doSetFocus()
		{
//			fButton.setFocus();
//      System.out.println("doSetFocus() " + moreButton.setFocus());
      comboBox.setFocus();
		}

	/**
	 * Sets the value of the cell editor to the given value.
	 */
//		protected void doSetValue(Object value)
//		{
//			fValue = value;
//		}

		protected void fillPopupList(PopupList list) 
		{
			String[] labels= new String[fItems.length];
			for (int i= 0; i < labels.length; i++)
			{
				String item= fItems[i];
				labels[i]= item;
//				System.out.println(fItems[i]);
				if (fSelection == null && fValue != null && fValue.equals(item)) 
				{
					fSelection = item;
				}
			}

			list.setItems(labels);
			if (fSelection != null) 
			{
//				fText.setText(fSelection);
				list.select(fSelection);
			}	
		}
	}
}
