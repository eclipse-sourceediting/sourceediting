/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.xml.ui.preferences.ui;



import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wst.sse.core.util.StringUtils;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.eclipse.wst.sse.ui.util.AccessibleInputDialog;
import org.eclipse.wst.xml.ui.Logger;
import org.eclipse.wst.xml.ui.contentassist.SourceEditorImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.preferences.MacroHelper;
import org.eclipse.wst.xml.ui.preferences.XMLMacroManager;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/** 
 * @deprecated use XMLTemplatePreferencePage TODO remove in C5
 */
public class XMLMacroPage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {

	protected class UIHelper {

		public String getUIString(String context) {
			if (context == null || context.length() == 0)
				return ALL;
			else if (context.equals(MacroHelper.TAG))
				return TAG;
			else if (context.equals(MacroHelper.ATTRIBUTE))
				return ATTRIBUTE;
			else if (context.equals(MacroHelper.ATTRIBUTEVALUE))
				return ATTRIBUTEVALUE;
			return ALL;
		}

		public String getDataString(String uistring) {
			if (uistring == null || uistring.length() == 0)
				return MacroHelper.ANY;
			else if (uistring.equals(TAG))
				return MacroHelper.TAG;
			else if (uistring.equals(ATTRIBUTE))
				return MacroHelper.ATTRIBUTE;
			else if (uistring.equals(ATTRIBUTEVALUE))
				return MacroHelper.ATTRIBUTEVALUE;
			return MacroHelper.ANY;
		}
	}

	protected class MacroNameInputValidator implements IInputValidator {

		public String isValid(String macroName) {
			// Don't allow blank macro names
			if (macroName == null || macroName.equals("")) { //$NON-NLS-1$
				return (ResourceHandler.getString("Macro_must_have_a_name_INFO_")); //$NON-NLS-1$ = "Macro must have a name"
			}
			// Don't allow macro names that start with blanks
			if (Character.isWhitespace(macroName.charAt(0))) {
				return ResourceHandler.getString("Macro_name_can_not_start_w_INFO_"); //$NON-NLS-1$ = "Macro name can not start with whitespace"
			}
			// See whether an existing macro has the name of the one selected
			if (getMacro(macroName.trim()) != null) {
				return ResourceHandler.getString("2concat", (new Object[]{macroName})); //$NON-NLS-1$ = "Macro name \"{0}\" is already in use"
			}
			return null;
		}
	}

	protected class FocusEnabler implements FocusListener {

		public void focusGained(FocusEvent e) {
			if (e.widget == fExpansionText)
				fCursorButton.setEnabled(true);
			else
				fCursorButton.setEnabled(false);
		}

		public void focusLost(FocusEvent e) {
		}
	}

	protected class MacroSelectionListener extends SelectionAdapter {

		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
			if (e.widget == fNewButton) {
				updateSelectedMacro();
				newPressed();
			}
			else if (e.widget == fCopyButton) {
				updateSelectedMacro();
				copyPressed();
			}
			else if (e.widget == fRenameButton) {
				updateSelectedMacro();
				renamePressed();
			}
			else if (e.widget == fRemoveButton) {
				updateSelectedMacro();
				removePressed();
			}
			else if (e.widget == fCursorButton) {
				addCursorPositionPressed(e);
			}
			else if (e.widget == fMacroEnabled) {
				enableMacro(e);
			}
			else if (e.widget == fMacroTable) {
				if (fMacroTable.getSelectionCount() > 1)
					return;
				if (fMacroTable.getSelectionCount() < 1) {
					fRenameButton.setEnabled(false);
					fRemoveButton.setEnabled(false);
				}
				else {
					String selectedMacroName = fMacroTable.getSelection()[0].getText();
					macroSelected(selectedMacroName);
				}
			}
		}
	}

	protected Table fMacroTable = null;
	protected Button fNewButton;
	protected Button fCopyButton;
	protected Button fRenameButton;
	protected Button fRemoveButton;
	protected Combo fContextCombo;
	protected Button fMacroEnabled;
	protected Label fExpansionLabel;
	protected Text fExpansionText;
	protected Button fCursorButton;
	protected Node fMacros;
	protected SourceEditorImageHelper imageHelper = new SourceEditorImageHelper();
	protected MacroHelper macroHelper = new MacroHelper();
	protected UIHelper uiHelper = new UIHelper();
	// contexts - UI display strings
	public final static String ALL = ResourceHandler.getString("All_UI_"); //$NON-NLS-1$ = "All"
	public final static String TAG = ResourceHandler.getString("Tag_UI_"); //$NON-NLS-1$ = "Tag"
	public final static String ATTRIBUTE = ResourceHandler.getString("Attribute_UI_"); //$NON-NLS-1$ = "Attribute"
	public final static String ATTRIBUTEVALUE = ResourceHandler.getString("Attribute_Value_UI_"); //$NON-NLS-1$ = "Attribute Value"
	protected Node selectedMacro = null;
	protected SelectionListener macroListener = new MacroSelectionListener();
	protected FocusEnabler focusEnabler = new FocusEnabler();
	protected MacroNameInputValidator macroNameInputValidator = new MacroNameInputValidator();

	protected void addCursorPositionPressed(SelectionEvent e) {
		if (fExpansionText.getCaretPosition() < 0)
			return;
		int caretPosition = fExpansionText.getCaretPosition();
		String text = fExpansionText.getText();
		int firstCursorPosition = text.indexOf(MacroHelper.CURSOR_POSITION);
		if (firstCursorPosition >= 0 && caretPosition <= firstCursorPosition + MacroHelper.CURSOR_POSITION.length() && caretPosition >= firstCursorPosition) {
			// do nothing
		}
		else if (firstCursorPosition >= 0) {
			if (firstCursorPosition > caretPosition) {
				// remove the current CURSOR_POSITION(s)
				text = StringUtils.replace(text, MacroHelper.CURSOR_POSITION, "");//$NON-NLS-1$
				// add the new CURSOR_POSITION
				String text2 = text.substring(0, caretPosition) + MacroHelper.CURSOR_POSITION + text.substring(caretPosition);
				fExpansionText.setText(text2);
			}
			else if (firstCursorPosition < caretPosition) {
				// remove the current CURSOR_POSITION(s)
				int cursorMarkerCount = 1;
				int currentMarkerOffsetInText = firstCursorPosition;
				while (currentMarkerOffsetInText >= 0) {
					currentMarkerOffsetInText = text.indexOf(MacroHelper.CURSOR_POSITION, currentMarkerOffsetInText + 1);
					if (0 < currentMarkerOffsetInText && currentMarkerOffsetInText < caretPosition)
						cursorMarkerCount++;
				}
				int newCursorPosition = caretPosition - cursorMarkerCount * MacroHelper.CURSOR_POSITION.length();
				text = StringUtils.replace(text, MacroHelper.CURSOR_POSITION, "");//$NON-NLS-1$
				// more than one cursor position marker, somehow
				// add the new CURSOR_POSITION remembering that the positions have changed
				String text2 = text.substring(0, newCursorPosition) + MacroHelper.CURSOR_POSITION + text.substring(newCursorPosition);
				fExpansionText.setText(text2);
			}
		}
		else { // no CURSOR_POSITION was found or the caret is at the start of the CURSOR_POSITION
			// add the new CURSOR_POSITION
			text = StringUtils.replace(text, MacroHelper.CURSOR_POSITION, "");//$NON-NLS-1$
			String text2 = text.substring(0, caretPosition) + MacroHelper.CURSOR_POSITION + text.substring(caretPosition);
			fExpansionText.setText(text2);
		}
		fCursorButton.setEnabled(false);
	}

	/**
	 * Add was pressed.  Prompt for the name of the new macro
	 * (adapted from HTMLMacroPreferencePage)
	 */
	protected void newPressed() {
		//as a workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=61069 use AccessibleInputDialog instead of InputDialog
		AccessibleInputDialog d = new AccessibleInputDialog(getShell(), ResourceHandler.getString("New_Macro_UI_"), ResourceHandler.getString("Macro_name__UI_"), "", macroNameInputValidator); //$NON-NLS-3$ //$NON-NLS-2$ = "Macro name:" //$NON-NLS-1$ = "New Macro"
		d.open();
		if (d.getReturnCode() == Window.CANCEL) {
			return;
		}
		String macroName = d.getValue();

		// Create a macro with the new name
		Node newMacro = fMacros.getOwnerDocument().createElement(MacroHelper.MACRO);

		Attr attr = fMacros.getOwnerDocument().createAttribute(MacroHelper.NAME);
		attr.setNodeValue(macroName);
		newMacro.getAttributes().setNamedItem(attr);

		attr = fMacros.getOwnerDocument().createAttribute(MacroHelper.CONTEXT);
		attr.setNodeValue(MacroHelper.ANY);
		newMacro.getAttributes().setNamedItem(attr);

		fMacros.appendChild(newMacro);
		setTableContents(fMacroTable, fMacros);
		// select the new macro
		selectMacro(macroName);
	}

	protected void copyPressed() {
		// Rename the currently selected macro
		if (fMacroTable.getSelectionCount() != 1)
			return;
		String selectedMacroName = fMacroTable.getItem(fMacroTable.getSelectionIndex()).getText();

		// Iterate over the current macros finding the one for the tree item and removing it
		Node macroToBeCopied = getMacro(selectedMacroName);
		if (macroToBeCopied == null) {
			return;
		}
		//as a workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=61069 use AccessibleInputDialog instead of InputDialog
		AccessibleInputDialog d = new AccessibleInputDialog(getShell(), ResourceHandler.getString("Copy_Macro_UI_"), ResourceHandler.getString("New_macro_name__UI_"), selectedMacroName, macroNameInputValidator); //$NON-NLS-2$ = "New macro name:" //$NON-NLS-1$ = "Copy Macro"
		d.open();
		if (d.getReturnCode() == Window.CANCEL) {
			return;
		}

		String newMacroName = d.getValue();
		Node newMacro = macroToBeCopied.getOwnerDocument().importNode(macroToBeCopied.cloneNode(true), true);
		Node nextNode = macroToBeCopied.getNextSibling();
		newMacro.getAttributes().getNamedItem(MacroHelper.NAME).setNodeValue(newMacroName); //$NON-NLS-1$
		if (macroToBeCopied.getParentNode() != null) {
			if (nextNode != null)
				macroToBeCopied.getParentNode().insertBefore(newMacro, nextNode);
			else
				macroToBeCopied.getParentNode().appendChild(newMacro);
		}

		// refresh the tree from the list of current macros
		setTableContents(fMacroTable, fMacros);
		// select the new macro
		selectMacro(newMacroName);
	}

	/**
	 * Creates an new checkbox instance and sets the default
	 * layout data.
	 *
	 * @param group  the composite in which to create the checkbox
	 * @param label  the string to set into the checkbox
	 * @return the new checkbox
	 */
	private Button createCheckBox(Composite group, String label) {
		Button button = new Button(group, SWT.CHECK | SWT.LEFT);
		button.setText(label);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		button.setLayoutData(data);
		return button;
	}

	private Combo createCombo(Composite parent, String[] labels, int selectedItem) {
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(labels);
		if (selectedItem >= 0)
			combo.select(selectedItem);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		combo.setLayoutData(data);
		return combo;
	}

	/**
	 * Creates composite control and sets the default layout data.
	 */

	private Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		//GridData
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		return composite;
	}

	protected Control createContents(Composite parent) {
		// create scrollbars for this preference page when needed
		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite mainComponent = createComposite(sc1, 1);
		sc1.setContent(mainComponent);

		//  createSourceFormat(pageComponent);
		Label descLabel = createDescriptionLabel(mainComponent, ResourceHandler.getString("XMLMacroPageDescription")); //$NON-NLS-1$
		Composite pageComponent = createComposite(mainComponent, 2);
		Composite macroTableComposite = createComposite(pageComponent, 2);

		((GridData) macroTableComposite.getLayoutData()).horizontalSpan = 2;
		((GridData) macroTableComposite.getLayoutData()).grabExcessHorizontalSpace = true;

		fMacroTable = createTable(macroTableComposite);
		((GridData) fMacroTable.getLayoutData()).grabExcessHorizontalSpace = true;
		((GridData) fMacroTable.getLayoutData()).widthHint = 128 /*(640/5)*/;
		fMacroTable.addSelectionListener(macroListener);
		fMacroTable.addFocusListener(focusEnabler);

		Composite buttonComposite = createComposite(macroTableComposite, 1);
		fNewButton = createPushButton(buttonComposite, ResourceHandler.getString("New..._UI_")); //$NON-NLS-1$ = "New..."
		fNewButton.addSelectionListener(macroListener);
		fNewButton.addFocusListener(focusEnabler);
		fCopyButton = createPushButton(buttonComposite, ResourceHandler.getString("Copy..._UI_")); //$NON-NLS-1$ = "Copy..."
		fCopyButton.addSelectionListener(macroListener);
		fCopyButton.addFocusListener(focusEnabler);
		fRenameButton = createPushButton(buttonComposite, ResourceHandler.getString("Rename..._UI_")); //$NON-NLS-1$ = "Rename..."
		fRenameButton.addSelectionListener(macroListener);
		fRenameButton.addFocusListener(focusEnabler);
		fRemoveButton = createPushButton(buttonComposite, ResourceHandler.getString("Remove_UI_")); //$NON-NLS-1$ = "Remove"
		fRemoveButton.addSelectionListener(macroListener);
		fRemoveButton.addFocusListener(focusEnabler);

		Composite contextComposite = createComposite(macroTableComposite, 2);
		((GridData) contextComposite.getLayoutData()).verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		((GridData) contextComposite.getLayoutData()).horizontalSpan = 2;

		fMacroEnabled = createCheckBox(contextComposite, ResourceHandler.getString("Enabled_at_Location__UI_")); //$NON-NLS-1$ = "Enabled at Location:"
		((GridData) fMacroEnabled.getLayoutData()).horizontalSpan = 1;
		((GridData) fMacroEnabled.getLayoutData()).horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		((GridData) fMacroEnabled.getLayoutData()).verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		fMacroEnabled.addSelectionListener(macroListener);

		fContextCombo = createCombo(contextComposite, new String[]{ALL, TAG, ATTRIBUTE, ATTRIBUTEVALUE}, -1);
		((GridData) fContextCombo.getLayoutData()).horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		((GridData) fContextCombo.getLayoutData()).verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		fContextCombo.addFocusListener(focusEnabler);

		createLabel(pageComponent, ""); //$NON-NLS-1$
		createLabel(pageComponent, ""); //$NON-NLS-1$

		fExpansionLabel = createLabel(pageComponent, ResourceHandler.getString("Content__UI_")); //$NON-NLS-1$ = "Content:"
		((GridData) fExpansionLabel.getLayoutData()).horizontalSpan = 2;
		((GridData) fExpansionLabel.getLayoutData()).horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		fExpansionText = createTextArea(pageComponent);
		((GridData) fExpansionText.getLayoutData()).horizontalSpan = 2;
		((GridData) fExpansionText.getLayoutData()).grabExcessHorizontalSpace = true;
		fExpansionText.addFocusListener(focusEnabler);

		fCursorButton = createPushButton(pageComponent, ResourceHandler.getString("Set_Cursor_Position_UI_")); //$NON-NLS-1$ = "Set Cursor Position"
		((GridData) fCursorButton.getLayoutData()).horizontalSpan = 2;
		((GridData) fCursorButton.getLayoutData()).grabExcessHorizontalSpace = false;
		fCursorButton.addSelectionListener(macroListener);

		fCopyButton.setEnabled(false);
		fRenameButton.setEnabled(false);
		fRemoveButton.setEnabled(false);
		fCursorButton.setEnabled(false);

		loadMacros();

		GridData gd = (GridData) descLabel.getLayoutData();
		gd.widthHint = (pageComponent.computeSize(SWT.DEFAULT, SWT.DEFAULT)).x;
		setSize(mainComponent);
		return pageComponent;
	}

	/**
	 * Sets the size of composite to the default value
	 */
	protected void setSize(Composite composite) {
		if (composite != null) {
			Point minSize = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			composite.setSize(minSize);
			// set scrollbar composite's min size so page is expandable but has scrollbars when needed
			if (composite.getParent() instanceof ScrolledComposite) {
				ScrolledComposite sc1 = (ScrolledComposite) composite.getParent();
				sc1.setMinSize(minSize);
				sc1.setExpandHorizontal(true);
				sc1.setExpandVertical(true);
			}
		}
	}

	/**
	 * Create description label displayed at top of preference page.  This
	 * method/label is used instead of PreferencePage's description label
	 * because the ScrolledComposite contained in this page will not fully
	 * work (horizontal scrolling) with PreferencePage's description label.
	 */
	protected Label createDescriptionLabel(Composite parent, String description) {
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText(description);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		label.setLayoutData(data);

		return label;
	}

	/**
	 * Utility method that creates a label instance
	 * and sets the default layout data.
	 */
	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}

	private Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		button.setLayoutData(data);
		return button;
	}

	//private Button createRadioButton(Composite group, String label) {
	//	Button button =  new Button(group, SWT.RADIO);
	//	button.setText(label);
	//	GridData data = new GridData();
	//	data.horizontalAlignment = GridData.FILL;
	//	data.horizontalSpan = 1;
	//	button.setLayoutData(data);
	//	return button;
	//}
	//private Label createSeparator(Composite parent, int columnSpan) {
	//	// Create a spacer line
	//	Label separator = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL);
	//	GridData data = new GridData();
	//	data.verticalAlignment = GridData.FILL;
	//	data.horizontalAlignment = GridData.FILL;
	//	data.horizontalSpan = columnSpan;
	//	separator.setLayoutData(data);
	//	return separator;
	//}
	/**
	 * Create a listbox
	 */
	private Table createTable(Composite group) {
		final Table t = new Table(group, SWT.BORDER | SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_BOTH);
		//	data.verticalAlignment = GridData.FILL_VERTICAL;
		//	data.horizontalAlignment = GridData.FILL_HORIZONTAL;
		data.heightHint = t.getItemHeight() * 6;
		t.setLayoutData(data);
		return t;
	}

	/**
	 * Create a text field specific for this application
	 */
	private Text createTextArea(Composite parent) {
		Text text = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData data = new GridData();
		if (fMacroTable != null)
			data.heightHint = fMacroTable.getItemHeight() * 4;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessVerticalSpace = true;
		text.setLayoutData(data);
		return text;
	}

	protected void enableMacro(SelectionEvent e) {
		if (e.widget != null && !e.widget.isDisposed() && !fMacroTable.isDisposed()) {
			enableMacro(((Button) e.widget).getSelection());
			fMacroTable.redraw();
		}
	}

	protected void enableMacro(boolean enabled) {
		if (selectedMacro != null) {
			if (enabled) {
				TableItem[] items = fMacroTable.getSelection();
				for (int i = 0; i < items.length; i++)
					items[i].setGrayed(false);
				fContextCombo.setEnabled(true);
				fExpansionLabel.setEnabled(true);
				fExpansionText.setEnabled(true);
				// remove disabled attribute
				NamedNodeMap attrs = selectedMacro.getAttributes();
				if (attrs != null) {
					Node disabled = attrs.getNamedItem(MacroHelper.DISABLED);
					if (disabled != null)
						attrs.removeNamedItem(MacroHelper.DISABLED);
				}
			}
			else {
				TableItem[] items = fMacroTable.getSelection();
				for (int i = 0; i < items.length; i++)
					items[i].setGrayed(true);
				fContextCombo.setEnabled(false);
				fExpansionLabel.setEnabled(false);
				fExpansionText.setEnabled(false);
				// add disabled attribute
				NamedNodeMap attrs = selectedMacro.getAttributes();
				if (attrs != null) {
					Node disabled = selectedMacro.getOwnerDocument().createAttribute(MacroHelper.DISABLED);
					if (disabled != null) {
						disabled.setNodeValue(MacroHelper.DISABLED);
						attrs.setNamedItem(disabled);
					}
				}
			}
		}
	}

	Node getMacro(String macroName) {
		if (fMacros == null)
			return null;
		NodeList macros = fMacros.getChildNodes();
		for (int i = 0; i < macros.getLength(); i++) {
			Node aMacro = macros.item(i);
			if (aMacro.getNodeType() == Node.TEXT_NODE)
				continue;
			if (aMacro.getAttributes().getNamedItem(MacroHelper.NAME).getNodeValue().equals(macroName)) { //$NON-NLS-1$
				return aMacro;
			}
		}
		return null;
	}

	protected PreferenceManager getMacroManager() {
		return XMLMacroManager.getXMLMacroManager();
	}

	public static String getSelected(String values[], Button buttons[]) {
		if (values == null || buttons == null)
			return null;

		int length = Math.min(values.length, buttons.length);
		for (int i = 0; i < length; i++) {
			if (buttons[i].getSelection()) {
				return values[i];
			}
		}
		return null;
	}

	/**
	 * Initializes this preference page using the passed workbench.
	 *
	 * @param workbench the current workbench
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Initializes states of the controls from the preference store.
	 */
	void initializeValues() {
		Node macrosNode = getMacroManager().getRootElement();
		fMacros = macrosNode.cloneNode(true);
		setTableContents(fMacroTable, fMacros);
	}

	public boolean loadMacros() {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				initializeValues();
			}
		});
		return true;
	}

	protected void macroSelected(String macroName) {
		fRemoveButton.setEnabled(true);
		fRenameButton.setEnabled(true);
		fCopyButton.setEnabled(true);
		fMacroEnabled.setEnabled(true);
		if (selectedMacro != null)
			updateSelectedMacro();
		selectedMacro = getMacro(macroName);
		refresh();
	}

	/**
	 * Initializes states of the controls using default values
	 * in the preference store.
	 */
	protected void performDefaults() {
		super.performDefaults();
		NodeList macrosList = getMacroManager().createDefaultPreferences().getElementsByTagName(MacroHelper.MACROS); //$NON-NLS-1$
		Node macrosNode = null;
		if (macrosList.getLength() > 0)
			macrosNode = macrosList.item(0);
		else
			macrosNode = getMacroManager().getRootElement(getMacroManager().createDefaultPreferences());
		fMacros = macrosNode.cloneNode(true);
		setTableContents(fMacroTable, fMacros);
	}

	public boolean performOk() {
		super.performOk();
		updateSelectedMacro();
		saveMacros();
		return true;
	}

	/**
	 * Update the current controls' settings using the currently selected macro
	 */
	protected void refresh() {
		if (selectedMacro == null) {
			fExpansionLabel.setEnabled(false);
			fExpansionText.setEnabled(false);
			fContextCombo.setEnabled(false);
			return;
		}
		// name can not be changed here

		// context
		fContextCombo.setEnabled(true);
		if (selectedMacro.getAttributes() != null) {
			Node context = selectedMacro.getAttributes().getNamedItem(MacroHelper.CONTEXT);
			if (context != null) {
				String contextValue = uiHelper.getUIString(context.getNodeValue());
				if (contextValue != null) {
					String items[] = fContextCombo.getItems();
					boolean selectionMatched = false;
					for (int i = 0; i < items.length; i++) {
						if (items[i].equals(contextValue)) {
							fContextCombo.select(i);
							selectionMatched = true;
							break;
						}
					}
					if (!selectionMatched) {
						for (int i = 0; i < items.length; i++) {
							if (items[i].equalsIgnoreCase(ALL)) {
								fContextCombo.select(i);
								break;
							}
						}
					}
				}
			}
		}
		boolean enabled = macroHelper.isEnabled(selectedMacro);
		fMacroEnabled.setSelection(enabled);
		fExpansionLabel.setEnabled(enabled);
		fExpansionText.setEnabled(enabled);
		enableMacro(enabled);

		// text
		NodeList macroChildren = selectedMacro.getChildNodes();
		Node cdata = null;
		for (int i = 0; i < macroChildren.getLength(); i++) {
			if (macroChildren.item(i).getNodeType() == Node.CDATA_SECTION_NODE) {
				cdata = macroChildren.item(i);
				break;
			}
		}
		if (cdata != null) {
			fExpansionText.setText(cdata.getNodeValue());
		}
		else {
			fExpansionText.setText(""); //$NON-NLS-1$
		}
	}

	protected void removePressed() {
		// Remove the currently selected macro
		if (fMacroTable.getSelectionCount() != 1)
			return;
		String selectedMacroName = fMacroTable.getItem(fMacroTable.getSelectionIndex()).getText();

		// Iterate over the current macros finding the one for the tree item and removing it
		Node macroToBeRemoved = getMacro(selectedMacroName);
		if (macroToBeRemoved != null) {
			fMacros.removeChild(macroToBeRemoved);
		}

		// Refresh the tree from the list of current macros
		setTableContents(fMacroTable, fMacros);
	}

	protected void renamePressed() {
		// Rename the currently selected macro
		if (fMacroTable.getSelectionCount() != 1)
			return;
		String selectedMacroName = fMacroTable.getItem(fMacroTable.getSelectionIndex()).getText();

		// Iterate over the current macros finding the one for the tree item and removing it
		Node macroToBeRenamed = getMacro(selectedMacroName);
		if (macroToBeRenamed == null) {
			return;
		}

		//as a workaround for https://bugs.eclipse.org/bugs/show_bug.cgi?id=61069 use AccessibleInputDialog instead of InputDialog
		AccessibleInputDialog d = new AccessibleInputDialog(getShell(), ResourceHandler.getString("Rename_Macro_UI_"), ResourceHandler.getString("New_macro_name__UI_"), selectedMacroName, macroNameInputValidator); //$NON-NLS-2$ = "New macro name:" //$NON-NLS-1$ = "Rename Macro"
		d.open();
		if (d.getReturnCode() == Window.CANCEL) {
			return;
		}

		String macroName = d.getValue();
		macroToBeRenamed.getAttributes().getNamedItem(MacroHelper.NAME).setNodeValue(macroName); //$NON-NLS-1$

		// refresh the tree from the list of current macros
		setTableContents(fMacroTable, fMacros);
		// select the renamed macro
		selectMacro(macroName);
	}

	public boolean saveMacros() {
		BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			public void run() {
				storeValues();
			}
		});
		return true;
	}

	private void selectMacro(String macroName) {
		// update GUI
		macroSelected(macroName);
		// ensure the table has the correct macro highlighted
		for (int i = 0; i < fMacroTable.getItemCount(); i++) {
			if (fMacroTable.getItem(i).getText().equals(macroName)) {
				fMacroTable.forceFocus();
				fMacroTable.select(i);
				fMacroTable.showSelection();
				break;
			}
		}
	}

	public static void setSelected(String selectedValue, String values[], Button buttons[]) {
		if (selectedValue == null || values == null || buttons == null)
			return;

		int length = Math.min(values.length, buttons.length);
		for (int i = 0; i < length; i++) {
			if (selectedValue.equalsIgnoreCase(values[i])) {
				buttons[i].setSelection(true);
				break;
			}
		}
	}

	protected void setTableContents(Table macroTable, Node macros) {

		macroTable.setRedraw(false);
		macroTable.removeAll();

		NodeList macroList = macros.getChildNodes();
		int numberOfMacros = macroList.getLength();
		Node macroName = null;
		Node macro = null;
		for (int i = 0; i < numberOfMacros; i++) {
			macro = macroList.item(i);
			NamedNodeMap attrs = macro.getAttributes();
			if (attrs == null)
				continue;
			if ((macroName = attrs.getNamedItem(MacroHelper.NAME)) != null) { //$NON-NLS-1$
				TableItem item = new TableItem(macroTable, SWT.NULL);
				item.setGrayed(!macroHelper.isEnabled(macro));
				item.setImage(XMLEditorPluginImageHelper.getInstance().getImage(macroHelper.getIconLocation(macro)));
				item.setText(macroName.getNodeValue());
			}
		}

		// There cannot be a selected macro
		// so we should disable the remove button and the expansion text
		fCopyButton.setEnabled(false);
		fRenameButton.setEnabled(false);
		fRemoveButton.setEnabled(false);
		fExpansionText.setText(""); //$NON-NLS-1$
		fExpansionText.setEnabled(false);
		fExpansionLabel.setEnabled(false);
		fContextCombo.deselectAll();
		fContextCombo.setEnabled(false);
		fCursorButton.setEnabled(false);
		fMacroEnabled.setEnabled(false);
		selectedMacro = null;
		// The tree refreshing was done in deferred redraw so we need to switch it back on
		macroTable.setRedraw(true);
	}

	/**
	 * Store the values of the controls back to the preference store.
	 */
	void storeValues() {
		Node macrosNode = getMacroManager().getRootElement();

		Vector v = new Vector();
		NodeList terminalChildren = macrosNode.getChildNodes();
		for (int i = 0; i < terminalChildren.getLength(); i++) {
			v.addElement(terminalChildren.item(i));
		}

		try {
			Document macroDocument = getMacroManager().getDocument();
			Node macro = null;
			NodeList macros = fMacros.getChildNodes();
			for (int i = 0; i < macros.getLength(); i++) {
				macro = macros.item(i).cloneNode(true);
				macro = macroDocument.importNode(macro, true);
				macrosNode.appendChild(macro);
			}

			Enumeration e = v.elements();
			while (e.hasMoreElements())
				macrosNode.removeChild((Node) e.nextElement());
		}
		catch (DOMException e) {
			Logger.logException(e);
		}
		getMacroManager().save();
	}

	/**
	 * Save the current controls' settings to the currently selected macro
	 */
	protected void updateSelectedMacro() {
		if (selectedMacro == null)
			return;
		// name can not be changed here
		// context
		if (fContextCombo.getSelectionIndex() >= 0) {
			Attr attr = fMacros.getOwnerDocument().createAttribute(MacroHelper.CONTEXT);
			attr.setNodeValue(uiHelper.getDataString(fContextCombo.getItem(fContextCombo.getSelectionIndex())));
			selectedMacro.getAttributes().setNamedItem(attr);
		}
		// text
		NodeList macroChildren = selectedMacro.getChildNodes();
		//	CDATASection cdata = null;
		Node cdata = null;
		for (int i = 0; i < macroChildren.getLength(); i++) {
			if (macroChildren.item(i).getNodeType() == Node.CDATA_SECTION_NODE) {
				cdata = macroChildren.item(i);
				break;
			}
		}
		if (cdata != null)
			cdata.setNodeValue(fExpansionText.getText());
		else {
			cdata = fMacros.getOwnerDocument().createCDATASection(fExpansionText.getText());
			selectedMacro.appendChild(cdata);
		}
	}
}
