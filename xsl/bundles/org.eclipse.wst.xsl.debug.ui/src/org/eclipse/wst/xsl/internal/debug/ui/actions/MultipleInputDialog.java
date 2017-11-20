/*******************************************************************************
 * Copyright (c) 2007 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *     David Carver (STAR) - bug 245772 - NLS Message refactor
 *******************************************************************************/
package org.eclipse.wst.xsl.internal.debug.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.xsl.internal.debug.ui.Messages;
import org.eclipse.wst.xsl.internal.debug.ui.XSLDebugUIPlugin;

/**
 * A dialog with handy methods for adding simple controls to itself.
 * 
 * @author Doug Satchwell
 */
public class MultipleInputDialog extends Dialog {
	protected static final String FIELD_NAME = "FIELD_NAME"; //$NON-NLS-1$
	protected static final int TEXT = 100;
	protected static final int BROWSE = 101;
	protected static final int VARIABLE = 102;
	protected static final int COMBO = 103;

	protected Composite panel;

	protected List<FieldSummary> fieldList = new ArrayList<FieldSummary>();
	protected List<Scrollable> controlList = new ArrayList<Scrollable>();
	protected List<Validator> validators = new ArrayList<Validator>();
	@SuppressWarnings("unchecked")
	protected Map<Object, Comparable> valueMap = new HashMap<Object, Comparable>();

	private final String title;

	/**
	 * Create a new instance of this.
	 * 
	 * @param shell
	 *            the shell to open the dialog on
	 * @param title
	 *            the title for the dialog
	 */
	public MultipleInputDialog(final Shell shell, final String title) {
		super(shell);
		this.title = title;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}

	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control bar = super.createButtonBar(parent);
		validateFields();
		return bar;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		// ((GridData) parent.getLayoutData()).heightHint = 400;
		// ((GridData) parent.getLayoutData()).widthHint = 400;

		container.setLayout(new GridLayout(2, false));

		GridData gd = new GridData(GridData.FILL_BOTH);
		// gd.heightHint = 200;
		container.setLayoutData(gd);

		panel = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		for (Iterator<FieldSummary> i = fieldList.iterator(); i.hasNext();) {
			FieldSummary field = i.next();
			switch (field.type) {
			case TEXT:
				createTextField(field.name, field.initialValue,
						field.allowsEmpty);
				break;
			// case BROWSE:
			// createBrowseField(field.name, field.initialValue,
			// field.allowsEmpty);
			// break;
			case VARIABLE:
				createVariablesField(field.name, field.initialValue,
						field.allowsEmpty);
				break;
			case COMBO:
				createComboField(field.name, field.initialIndex, field.items);
				break;
			}
		}

		fieldList = null; // allow it to be gc'd
		Dialog.applyDialogFont(container);

		return container;
	}

	/**
	 * Add a label, text box and button for browsing the for a file.
	 * 
	 * @param labelText
	 *            the label
	 * @param initialValue
	 *            the initial value
	 * @param allowsEmpty
	 *            true if the text box can be empty
	 */
	public void addBrowseField(String labelText, String initialValue,
			boolean allowsEmpty) {
		fieldList.add(new FieldSummary(BROWSE, labelText, initialValue,
				allowsEmpty));
	}

	/**
	 * Add a label and a text box.
	 * 
	 * @param labelText
	 *            the label
	 * @param initialValue
	 *            the initial value
	 * @param allowsEmpty
	 *            true if the text box can be empty
	 */
	public void addTextField(String labelText, String initialValue,
			boolean allowsEmpty) {
		fieldList.add(new FieldSummary(TEXT, labelText, initialValue,
				allowsEmpty));
	}

	/**
	 * Add a label, a text box and a button for selecting variables.
	 * 
	 * @param labelText
	 *            the label
	 * @param initialValue
	 *            the initial value
	 * @param allowsEmpty
	 *            true if the text box can be empty
	 */
	public void addVariablesField(String labelText, String initialValue,
			boolean allowsEmpty) {
		fieldList.add(new FieldSummary(VARIABLE, labelText, initialValue,
				allowsEmpty));
	}

	/**
	 * Add a label and a combo.
	 * 
	 * @param labelText
	 *            the label
	 * @param initialIndex
	 *            the initial selection index
	 * @param items
	 *            the array of items for the combo
	 */
	public void addComboField(String labelText, int initialIndex, String[] items) {
		fieldList.add(new FieldSummary(COMBO, labelText, items, initialIndex));
	}

	protected void createTextField(String labelText, String initialValue,
			boolean allowEmpty) {
		Label label = new Label(panel, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		final Text text = new Text(panel, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setData(FIELD_NAME, labelText);

		// make sure rows are the same height on both panels.
		label.setSize(label.getSize().x, text.getSize().y);

		if (initialValue != null) {
			text.setText(initialValue);
		}

		if (!allowEmpty) {
			validators.add(new Validator() {
				@Override
				public boolean validate() {
					return !text.getText().equals(""); //$NON-NLS-1$
				}
			});
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateFields();
				}
			});
		}

		controlList.add(text);
	}

	protected void createVariablesField(String labelText, String initialValue,
			boolean allowEmpty) {
		Label label = new Label(panel, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		Composite comp = new Composite(panel, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Text text = new Text(comp, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 200;
		text.setLayoutData(data);
		text.setData(FIELD_NAME, labelText);

		// make sure rows are the same height on both panels.
		label.setSize(label.getSize().x, text.getSize().y);

		if (initialValue != null) {
			text.setText(initialValue);
		}

		if (!allowEmpty) {
			validators.add(new Validator() {
				@Override
				public boolean validate() {
					return !text.getText().equals(""); //$NON-NLS-1$
				}
			});

			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateFields();
				}
			});
		}

		Button button = createButton(comp, IDialogConstants.IGNORE_ID,
				Messages.VariablesFieldButton_Text, false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(
						getShell());
				int code = dialog.open();
				if (code == IDialogConstants.OK_ID) {
					String variable = dialog.getVariableExpression();
					if (variable != null) {
						text.insert(variable);
					}
				}
			}
		});

		controlList.add(text);

	}

	protected void createComboField(String labelText, int initialValue,
			String[] items) {
		Label label = new Label(panel, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		Composite comp = new Composite(panel, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Combo combo = new Combo(comp, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);
		// GridData data = new GridData(GridData.FILL_HORIZONTAL);
		// data.widthHint = 200;
		// combo.setLayoutData(data);

		combo.setData(FIELD_NAME, labelText);

		// make sure rows are the same height on both panels.
		label.setSize(label.getSize().x, combo.getSize().y);

		combo.setItems(items);
		combo.select(initialValue);

		controlList.add(combo);
	}

	@Override
	protected void okPressed() {
		for (Iterator<Scrollable> i = controlList.iterator(); i.hasNext();) {
			Control control = i.next();
			if (control instanceof Text) {
				valueMap.put(control.getData(FIELD_NAME), ((Text) control)
						.getText());
			} else if (control instanceof Combo) {
				Combo combo = (Combo) control;
				valueMap.put(control.getData(FIELD_NAME), Integer.valueOf(combo
						.getSelectionIndex()));
			}
		}
		controlList = null;
		super.okPressed();
	}

	@Override
	public int open() {
		applyDialogFont(panel);
		return super.open();
	}

	protected Object getValue(String key) {
		return valueMap.get(key);
	}

	protected String getStringValue(String key) {
		return (String) getValue(key);
	}

	protected int getIntValue(String key) {
		return ((Integer) getValue(key)).intValue();
	}

	protected void validateFields() {
		for (Iterator<Validator> i = validators.iterator(); i.hasNext();) {
			Validator validator = i.next();
			if (!validator.validate()) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			}
		}
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	private String getDialogSettingsSectionName() {
		return XSLDebugUIPlugin.PLUGIN_ID + ".MULTIPLE_INPUT_DIALOG"; //$NON-NLS-1$
	}

	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = XSLDebugUIPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings section = settings
				.getSection(getDialogSettingsSectionName());
		if (section == null) {
			section = settings.addNewSection(getDialogSettingsSectionName());
		}
		return section;
	}

	protected static class FieldSummary {
		int initialIndex;
		int type;
		String name;
		String initialValue;
		boolean allowsEmpty;
		String[] items;

		public FieldSummary(int type, String name, String initialValue,
				boolean allowsEmpty) {
			this.type = type;
			this.name = name;
			this.initialValue = initialValue;
			this.allowsEmpty = allowsEmpty;
		}

		public FieldSummary(int type, String name, String[] items,
				int initialIndex) {
			this.type = type;
			this.name = name;
			this.items = items;
		}
	}

	protected class Validator {
		boolean validate() {
			return true;
		}
	}

}
