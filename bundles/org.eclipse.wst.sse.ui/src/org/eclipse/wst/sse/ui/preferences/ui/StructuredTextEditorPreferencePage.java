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
package org.eclipse.wst.sse.ui.preferences.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.debug.internal.ui.actions.StatusInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.wst.sse.core.IModelManagerPlugin;
import org.eclipse.wst.sse.core.preferences.CommonModelPreferenceNames;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore;
import org.eclipse.wst.sse.ui.internal.preferences.TabFolderLayout;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.CommonEditorPreferenceNames;


/**
 * Gutted version of JavaEditorPreferencePage
 * @author pavery
 */
public class StructuredTextEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {	
	private Map fCheckBoxes = new HashMap();
	private SelectionListener fCheckBoxListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			Button button = (Button) e.widget;
			fOverlayStore.setValue((String) fCheckBoxes.get(button), button.getSelection());
		}
	};

	private ArrayList fNumberFields = new ArrayList();
	private ModifyListener fNumberFieldListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			numberFieldChanged((Text) e.widget);
		}
	};

	private Map fTextFields = new HashMap();
	private ModifyListener fTextFieldListener = new ModifyListener() {
		public void modifyText(ModifyEvent e) {
			Text text = (Text) e.widget;
			fOverlayStore.setValue((String) fTextFields.get(text), text.getText());
		}
	};

	private Map fColorButtons = new HashMap();
	/** Button controlling default setting of the selected reference provider. */
	private Button fSetDefaultButton;
	private OverlayPreferenceStore fOverlayStore;

	private final String[][] fAppearanceColorListModel = new String[][]{{ResourceHandler.getString("StructuredTextEditorPreferencePage.1"), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER_COLOR}, {ResourceHandler.getString("StructuredTextEditorPreferencePage.2"), CommonEditorPreferenceNames.MATCHING_BRACKETS_COLOR}, {ResourceHandler.getString("StructuredTextEditorPreferencePage.3"), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR}, {ResourceHandler.getString("StructuredTextEditorPreferencePage.4"), AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR}, {ResourceHandler.getString("StructuredTextEditorPreferencePage.5"), CommonEditorPreferenceNames.LINK_COLOR},}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	private List fAppearanceColorList;
	private ColorEditor fAppearanceColorEditor;
	private IPreferenceTab[] fTabs = null;

	public StructuredTextEditorPreferencePage() {
		setDescription(ResourceHandler.getString("StructuredTextEditorPreferencePage.6")); //$NON-NLS-1$
		setPreferenceStore(((AbstractUIPlugin)Platform.getPlugin(EditorPlugin.ID)).getPreferenceStore());

		fOverlayStore = new OverlayPreferenceStore(getPreferenceStore(), createOverlayStoreKeys());
	}

	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		ArrayList overlayKeys = new ArrayList();

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, CommonEditorPreferenceNames.SHOW_QUICK_FIXABLES));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, CommonEditorPreferenceNames.MATCHING_BRACKETS_COLOR));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, CommonEditorPreferenceNames.MATCHING_BRACKETS));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.INT, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLOR));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.INT, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_OVERVIEW_RULER));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER_COLOR));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER));

		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}

	/*
	 * @see IWorkbenchPreferencePage#init()
	 */
	public void init(IWorkbench workbench) {
		// nothing to do
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		//WorkbenchHelp.setHelp(getControl(), IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE);
	}

	private void handleAppearanceColorListSelection() {
		int i = fAppearanceColorList.getSelectionIndex();
		String key = fAppearanceColorListModel[i][1];
		RGB rgb = PreferenceConverter.getColor(fOverlayStore, key);
		fAppearanceColorEditor.setColorValue(rgb);
	}

	private Control createAppearancePage(Composite parent) {

		Composite appearanceComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		appearanceComposite.setLayout(layout);

		String label = ResourceHandler.getString("StructuredTextEditorPreferencePage.16"); //$NON-NLS-1$
		addTextField(appearanceComposite, label, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 3, 0, true);

		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.17"); //$NON-NLS-1$
		addTextField(appearanceComposite, label, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN, 3, 0, true);

		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.18"); //$NON-NLS-1$
		addCheckBox(appearanceComposite, label, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_OVERVIEW_RULER, 0);

		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.19"); //$NON-NLS-1$
		addCheckBox(appearanceComposite, label, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER, 0);

		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.20"); //$NON-NLS-1$
		addCheckBox(appearanceComposite, label, CommonEditorPreferenceNames.MATCHING_BRACKETS, 0);

		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.21"); //$NON-NLS-1$
		addCheckBox(appearanceComposite, label, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, 0);

		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.22"); //$NON-NLS-1$
		addCheckBox(appearanceComposite, label, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN, 0);

		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.31"); //$NON-NLS-1$
		addCheckBox(appearanceComposite, label, CommonEditorPreferenceNames.SHOW_QUICK_FIXABLES, 0);
		
		label = ResourceHandler.getString("StructuredTextEditorPreferencePage.30"); //$NON-NLS-1$
		addCheckBox(appearanceComposite, label, CommonEditorPreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, 0);

		Label l = new Label(appearanceComposite, SWT.LEFT);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.heightHint = convertHeightInCharsToPixels(1) / 2;
		l.setLayoutData(gd);

		l = new Label(appearanceComposite, SWT.LEFT);
		l.setText(ResourceHandler.getString("StructuredTextEditorPreferencePage.23")); //$NON-NLS-1$
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		l.setLayoutData(gd);

		Composite editorComposite = new Composite(appearanceComposite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		editorComposite.setLayout(layout);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_VERTICAL);
		gd.horizontalSpan = 2;
		editorComposite.setLayoutData(gd);

		fAppearanceColorList = new List(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		gd.heightHint = convertHeightInCharsToPixels(5);
		fAppearanceColorList.setLayoutData(gd);

		Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		l = new Label(stylesComposite, SWT.LEFT);
		// needs to be made final so label can be set in foregroundcolorbutton's acc listener
		final String buttonLabel = ResourceHandler.getString("StructuredTextEditorPreferencePage.24"); //$NON-NLS-1$ 
		l.setText(buttonLabel);
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		l.setLayoutData(gd);

		fAppearanceColorEditor = new ColorEditor(stylesComposite);
		Button foregroundColorButton = fAppearanceColorEditor.getButton();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		foregroundColorButton.setLayoutData(gd);

		fAppearanceColorList.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				handleAppearanceColorListSelection();
			}
		});
		foregroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				int i = fAppearanceColorList.getSelectionIndex();
				String key = fAppearanceColorListModel[i][1];

				PreferenceConverter.setValue(fOverlayStore, key, fAppearanceColorEditor.getColorValue());
			}
		});

		// bug2541 - associate color label to button's label field
		foregroundColorButton.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			public void getName(AccessibleEvent e) {
				if (e.childID == ACC.CHILDID_SELF)
					e.result = buttonLabel;
			}
		});
		
		WorkbenchHelp.setHelp(appearanceComposite, IHelpContextIds.PREFSTE_APPEARANCE_HELPID);
		return appearanceComposite;
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		// need to create tabs before loading/starting overlaystore in case tabs also add values
		IPreferenceTab navigationTab = new NavigationPreferenceTab(this, fOverlayStore);
		IPreferenceTab hoversTab = new TextHoverPreferenceTab(this, fOverlayStore);
		
		fOverlayStore.load();
		fOverlayStore.start();

		TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayout(new TabFolderLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(ResourceHandler.getString("StructuredTextEditorPreferencePage.0")); //$NON-NLS-1$
		item.setControl(createAppearancePage(folder));

		item = new TabItem(folder, SWT.NONE);
		item.setText(navigationTab.getTitle());
		item.setControl(navigationTab.createContents(folder));
		
		item = new TabItem(folder, SWT.NONE);
		item.setText(hoversTab.getTitle());
		item.setControl(hoversTab.createContents(folder));

		fTabs = new IPreferenceTab[]{navigationTab, hoversTab};

		initialize();

		Dialog.applyDialogFont(folder);
		return folder;
	}

	private void initialize() {
		initializeFields();

		for (int i = 0; i < fAppearanceColorListModel.length; i++)
			fAppearanceColorList.add(fAppearanceColorListModel[i][0]);
		fAppearanceColorList.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (fAppearanceColorList != null && !fAppearanceColorList.isDisposed()) {
					fAppearanceColorList.select(0);
					handleAppearanceColorListSelection();
				}
			}
		});
	}

	private void initializeFields() {
		Iterator e = fColorButtons.keySet().iterator();
		while (e.hasNext()) {
			ColorEditor c = (ColorEditor) e.next();
			String key = (String) fColorButtons.get(c);
			RGB rgb = PreferenceConverter.getColor(fOverlayStore, key);
			c.setColorValue(rgb);
		}

		e = fCheckBoxes.keySet().iterator();
		while (e.hasNext()) {
			Button b = (Button) e.next();
			String key = (String) fCheckBoxes.get(b);
			b.setSelection(fOverlayStore.getBoolean(key));
		}

		e = fTextFields.keySet().iterator();
		while (e.hasNext()) {
			Text t = (Text) e.next();
			String key = (String) fTextFields.get(t);
			t.setText(fOverlayStore.getString(key));
		}
	}

	private IModelManagerPlugin getModelManagerPlugin() {

		IModelManagerPlugin plugin = (IModelManagerPlugin) Platform.getPlugin(IModelManagerPlugin.ID);
		return plugin;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	protected void performApply() {
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performApply();
		}
		super.performApply();
	}
	
	/*
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performOk();
		}
		
		fOverlayStore.propagate();
		EditorPlugin.getDefault().savePluginPreferences();

		// tab width is also a model-side preference so need to set it
		int tabWidth = getPreferenceStore().getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		getModelManagerPlugin().getPluginPreferences().setValue(CommonModelPreferenceNames.TAB_WIDTH, tabWidth);
		((Plugin)getModelManagerPlugin()).savePluginPreferences();

		return true;
	}

	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		fOverlayStore.loadDefaults();

		initializeFields();

		handleAppearanceColorListSelection();
		
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performDefaults();
		}

		super.performDefaults();

		// there is currently no need for a viewer
		//		fPreviewViewer.invalidateTextPresentation();
	}

	/*
	 * @see DialogPage#dispose()
	 */
	public void dispose() {
		if (fOverlayStore != null) {
			fOverlayStore.stop();
			fOverlayStore = null;
		}

		super.dispose();
	}

	private Button addCheckBox(Composite parent, String label, String key, int indentation) {
		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText(label);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = indentation;
		gd.horizontalSpan = 2;
		checkBox.setLayoutData(gd);
		checkBox.addSelectionListener(fCheckBoxListener);

		fCheckBoxes.put(checkBox, key);

		return checkBox;
	}

	private Text addTextField(Composite composite, String label, String key, int textLimit, int indentation, boolean isNumber) {
		return getTextControl(addLabelledTextField(composite, label, key, textLimit, indentation, isNumber));
	}

	private static Text getTextControl(Control[] labelledTextField) {
		return (Text) labelledTextField[1];
	}

	/**
	 * Returns an array of size 2:
	 *  - first element is of type <code>Label</code>
	 *  - second element is of type <code>Text</code>
	 * Use <code>getLabelControl</code> and <code>getTextControl</code> to get the 2 controls.
	 */
	private Control[] addLabelledTextField(Composite composite, String label, String key, int textLimit, int indentation, boolean isNumber) {
		Label labelControl = new Label(composite, SWT.NONE);
		labelControl.setText(label);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent = indentation;
		labelControl.setLayoutData(gd);

		Text textControl = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = convertWidthInCharsToPixels(textLimit + 1);
		textControl.setLayoutData(gd);
		textControl.setTextLimit(textLimit);
		fTextFields.put(textControl, key);
		if (isNumber) {
			fNumberFields.add(textControl);
			textControl.addModifyListener(fNumberFieldListener);
		}
		else {
			textControl.addModifyListener(fTextFieldListener);
		}

		return new Control[]{labelControl, textControl};
	}

	private void numberFieldChanged(Text textControl) {
		String number = textControl.getText();
		IStatus status = validatePositiveNumber(number);
		if (!status.matches(IStatus.ERROR))
			fOverlayStore.setValue((String) fTextFields.get(textControl), number);
		updateStatus(status);
	}

	private IStatus validatePositiveNumber(String number) {
		StatusInfo status = new StatusInfo();
		if (number.length() == 0) {
			status.setError(ResourceHandler.getString("StructuredTextEditorPreferencePage.37")); //$NON-NLS-1$
		}
		else {
			try {
				int value = Integer.parseInt(number);
				if (value < 0)
					status.setError(number + ResourceHandler.getString("StructuredTextEditorPreferencePage.38")); //$NON-NLS-1$
			}
			catch (NumberFormatException e) {
				status.setError(number + ResourceHandler.getString("StructuredTextEditorPreferencePage.39")); //$NON-NLS-1$
			}
		}
		return status;
	}

	void updateStatus(IStatus status) {
		if (!status.matches(IStatus.ERROR)) {
			for (int i = 0; i < fNumberFields.size(); i++) {
				Text text = (Text) fNumberFields.get(i);
				IStatus s = validatePositiveNumber(text.getText());
				status = s.getSeverity() > status.getSeverity() ? s : status;
			}
		}

		setValid(!status.matches(IStatus.ERROR));
		applyToStatusLine(this, status);
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 */
	public void applyToStatusLine(DialogPage page, IStatus status) {
		String message = status.getMessage();
		switch (status.getSeverity()) {
			case IStatus.OK :
				page.setMessage(message, IMessageProvider.NONE);
				page.setErrorMessage(null);
				break;
			case IStatus.WARNING :
				page.setMessage(message, IMessageProvider.WARNING);
				page.setErrorMessage(null);
				break;
			case IStatus.INFO :
				page.setMessage(message, IMessageProvider.INFORMATION);
				page.setErrorMessage(null);
				break;
			default :
				if (message.length() == 0) {
					message = null;
				}
				page.setMessage(null);
				page.setErrorMessage(message);
				break;
		}
	}
}
