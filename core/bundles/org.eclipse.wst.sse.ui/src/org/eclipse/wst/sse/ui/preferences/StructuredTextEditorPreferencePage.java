/*******************************************************************************
 * Copyright (c) 2001, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     Nitin Dahyabhai <nitind@us.ibm.com> with Andrew Obuchowicz <aobuchow@redhat.com> - Add color preview to table, removed dead code
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore;
import org.eclipse.wst.sse.ui.internal.preferences.TabFolderLayout;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorEditor;
import org.eclipse.wst.sse.ui.internal.preferences.ui.IPreferenceTab;
import org.eclipse.wst.sse.ui.internal.preferences.ui.TextHoverPreferenceTab;

/**
 * Adapted from
 * org.eclipse.ui.internal.editors.text.TextEditorDefaultsPreferencePage, made
 * reusable by extension configuration parameters that are respected in the
 * StructuredTextEditor.
 */
public class StructuredTextEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IExecutableExtension {
	/**
	 * Initialization parameter to determine which context in the INSTANCE and DEFAULT scopes to load/store shown preference values
	 */
	public static final String PREFERENCE_SCOPE_NAME = "org.eclipse.wst.sse.ui.appearancePreferenceScopeName"; // $NON-NLS-1$
	/**
	 * Initialization parameter to indicate a custom page description
	 */
	public static final String DESCRIPTION = "org.eclipse.wst.sse.ui.appearancePreferencePageDescription"; // $NON-NLS-1$


	private class ColorEntry implements Comparable<ColorEntry> {
		public final String colorKey;
		public final String isSystemDefaultKey;

		public final String label;
		public final RGB systemColorRGB;

		public ColorEntry(String label, String colorKey, String isSystemDefaultKey, Color systemColor) {
			this.label = label;
			this.colorKey = colorKey;
			this.isSystemDefaultKey = isSystemDefaultKey;
			this.systemColorRGB = (systemColor != null) ? systemColor.getRGB() : null;
		}

		@Override
		public int compareTo(ColorEntry o) {
			return label.compareTo(o.label);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ColorEntry))
				return false;
			return label.equals(((ColorEntry)obj).label) && colorKey.equals(((ColorEntry)obj).colorKey);
		}

		public RGB getRGB() {
			return PreferenceConverter.getColor(fOverlayStore, this.colorKey);
		}

		public boolean isSystemDefault() {
			return this.isSystemDefaultKey != null && fOverlayStore.getBoolean(isSystemDefaultKey);
		}
	}

	private List<Image> colorPreviewImages;
	private ColorEditor fAppearanceColorEditor;

	private final ColorEntry[] fAppearanceColorListModel = new ColorEntry[] {
		new ColorEntry(SSEUIMessages.StructuredTextEditorPreferencePage_2, AppearancePreferenceNames.MATCHING_BRACKETS_COLOR, null, null),
		new ColorEntry(SSEUIMessages.StructuredTextEditorPreferencePage_41, AppearancePreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND, null, null),
		new ColorEntry(SSEUIMessages.StructuredTextEditorPreferencePage_42, AppearancePreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND, null, null),
		new ColorEntry(SSEUIMessages.StructuredTextEditorPreferencePage_43, AppearancePreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND, null, null),
		new ColorEntry(SSEUIMessages.StructuredTextEditorPreferencePage_44, AppearancePreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND, null, null)};

	private TableViewer fAppearanceColorTableViewer;
	private Map<Button, String> fCheckBoxes = new HashMap<>();

	private SelectionListener fCheckBoxListener = new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			Button button = (Button) e.widget;
			fOverlayStore.setValue(fCheckBoxes.get(button), button.getSelection());
		}
	};

	private Map<ColorEditor, String> fColorButtons = new HashMap<>();
	private OverlayPreferenceStore fOverlayStore;
	private IPreferenceTab[] fTabs = null;

	/*
	 * Unless/until the hover management can be broken out into a per-plug-in
	 * setup rather than a singleton in SSSE UI, the Hover preferences need to
	 * remain in one place only
	 */
	private boolean showHoverTab = true;

	public StructuredTextEditorPreferencePage() {
		// subject to be overridden via initialization data
		setDescription(SSEUIMessages.StructuredTextEditorPreferencePage_6); // $NON-NLS-1$
		setPreferenceStore(SSEUIPlugin.getDefault().getPreferenceStore());
		fOverlayStore = new OverlayPreferenceStore(getPreferenceStore(), createOverlayStoreKeys());
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

	/**
	 * Applies the status to the status line of a dialog page.
	 */
	void applyToStatusLine(DialogPage page, IStatus status) {
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

	private Control createAppearancePage(Composite parent) {
		Composite appearanceComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		appearanceComposite.setLayout(layout);

		String label = SSEUIMessages.StructuredTextEditorPreferencePage_20; // $NON-NLS-1$
		addCheckBox(appearanceComposite, label, AppearancePreferenceNames.MATCHING_BRACKETS, 0);

		label = SSEUIMessages.StructuredTextEditorPreferencePage_30; // $NON-NLS-1$
		addCheckBox(appearanceComposite, label, AppearancePreferenceNames.EVALUATE_TEMPORARY_PROBLEMS, 0);

		PreferenceLinkArea contentTypeArea = new PreferenceLinkArea(appearanceComposite, SWT.NONE, "ValidationPreferencePage", SSEUIMessages.StructuredTextEditorPreferencePage_40, (IWorkbenchPreferenceContainer) getContainer(), null); //$NON-NLS-1$

		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.horizontalIndent = 20;
		contentTypeArea.getControl().setLayoutData(data);

		label = SSEUIMessages.StructuredTextEditorPreferencePage_39;
		addCheckBox(appearanceComposite, label, AppearancePreferenceNames.SHOW_UNKNOWN_CONTENT_TYPE_MSG, 0);

		label = SSEUIMessages.StructuredTextEditorPreferencePage_3;
		addCheckBox(appearanceComposite, label, AppearancePreferenceNames.FOLDING_ENABLED, 0);

		label = SSEUIMessages.StructuredTextEditorPreferencePage_1;
		addCheckBox(appearanceComposite, label, AppearancePreferenceNames.SEMANTIC_HIGHLIGHTING, 0);

		Label l = new Label(appearanceComposite, SWT.LEFT);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.heightHint = convertHeightInCharsToPixels(1) / 2;
		l.setLayoutData(gd);

		l = new Label(appearanceComposite, SWT.LEFT);
		l.setText(SSEUIMessages.StructuredTextEditorPreferencePage_23); // $NON-NLS-1$
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

		Composite tableComposite = new Composite(editorComposite, SWT.NONE);
		GridData tableGD = new GridData(GridData.FILL_VERTICAL);
		tableComposite.setLayoutData(tableGD);
		fAppearanceColorTableViewer = new TableViewer(tableComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		initializeAppearanceColorTable(tableComposite);
		Arrays.sort(fAppearanceColorListModel);
		fAppearanceColorTableViewer.setInput(fAppearanceColorListModel);

		Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		stylesComposite.setLayout(layout);
		stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		l = new Label(stylesComposite, SWT.LEFT);
		// needs to be made final so label can be set in
		// foregroundcolorbutton's acc listener
		final String buttonLabel = SSEUIMessages.StructuredTextEditorPreferencePage_24; // $NON-NLS-1$
		l.setText(buttonLabel);
		gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		l.setLayoutData(gd);

		fAppearanceColorEditor = new ColorEditor(stylesComposite);
		Button foregroundColorButton = fAppearanceColorEditor.getButton();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalAlignment = GridData.BEGINNING;
		foregroundColorButton.setLayoutData(gd);

		foregroundColorButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				if (!fAppearanceColorTableViewer.getSelection().isEmpty()) {
					Object element = fAppearanceColorTableViewer.getStructuredSelection().getFirstElement();
					int i = Arrays.binarySearch(fAppearanceColorListModel, element);
					String key = fAppearanceColorListModel[i].colorKey;

					PreferenceConverter.setValue(fOverlayStore, key, fAppearanceColorEditor.getColorValue());
					fAppearanceColorTableViewer.refresh(element, true);
				}
			}
		});

		// bug2541 - associate color label to button's label field
		foregroundColorButton.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(AccessibleEvent e) {
				if (e.childID == ACC.CHILDID_SELF)
					e.result = buttonLabel;
			}
		});

		PlatformUI.getWorkbench().getHelpSystem().setHelp(appearanceComposite, IHelpContextIds.PREFSTE_APPEARANCE_HELPID);
		return appearanceComposite;
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		// need to create tabs before loading/starting overlaystore in case
		// tabs also add values
		IPreferenceTab hoversTab = null;
		if (showHoverTab) {
			hoversTab = new TextHoverPreferenceTab(this, fOverlayStore);
		}

		fOverlayStore.load();
		fOverlayStore.start();

		TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayout(new TabFolderLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(SSEUIMessages.StructuredTextEditorPreferencePage_0); // $NON-NLS-1$
		item.setControl(createAppearancePage(folder));

		if (hoversTab != null) {
			item = new TabItem(folder, SWT.NONE);
			item.setText(hoversTab.getTitle());
			item.setControl(hoversTab.createContents(folder));

			fTabs = new IPreferenceTab[]{hoversTab};
		}
		else {
			fTabs = new IPreferenceTab[0];
		}
		
		initialize();

		Dialog.applyDialogFont(folder);
		return folder;
	}

	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		List<OverlayPreferenceStore.OverlayKey> overlayKeys = new ArrayList<>();

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AppearancePreferenceNames.EVALUATE_TEMPORARY_PROBLEMS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AppearancePreferenceNames.SHOW_UNKNOWN_CONTENT_TYPE_MSG));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AppearancePreferenceNames.FOLDING_ENABLED));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AppearancePreferenceNames.SEMANTIC_HIGHLIGHTING));

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, AppearancePreferenceNames.MATCHING_BRACKETS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AppearancePreferenceNames.MATCHING_BRACKETS_COLOR));
		
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AppearancePreferenceNames.CODEASSIST_PROPOSALS_BACKGROUND));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AppearancePreferenceNames.CODEASSIST_PROPOSALS_FOREGROUND));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AppearancePreferenceNames.CODEASSIST_PARAMETERS_BACKGROUND));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.STRING, AppearancePreferenceNames.CODEASSIST_PARAMETERS_FOREGROUND));

		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}
	
	@Override
	public void dispose() {
		if (fOverlayStore != null) {
			fOverlayStore.stop();
			fOverlayStore = null;
		}
		for (Image image : colorPreviewImages) {
			image.dispose();
		}
		colorPreviewImages= null;

		super.dispose();
	}

	private void handleAppearanceColorViewerSelection() {
		if (!fAppearanceColorTableViewer.getSelection().isEmpty()) {
			Object firstElement = fAppearanceColorTableViewer.getStructuredSelection().getFirstElement();
			int i = Arrays.binarySearch(fAppearanceColorListModel, firstElement);
			String key = fAppearanceColorListModel[i].colorKey;
			RGB rgb = PreferenceConverter.getColor(fOverlayStore, key);
			fAppearanceColorEditor.setColorValue(rgb);
		}
		else {
			fAppearanceColorTableViewer.refresh(true);
		}
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}

	private void initialize() {
		initializeFields();
	}

	private void initializeAppearanceColorTable(Composite tableComposite) {
		fAppearanceColorTableViewer.addSelectionChangedListener((SelectionChangedEvent event) -> handleAppearanceColorViewerSelection());
		colorPreviewImages = new ArrayList<>();
		fAppearanceColorTableViewer.setContentProvider(new ArrayContentProvider());
		fAppearanceColorTableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				ColorEntry colorEntry = ((ColorEntry) element);
				if (colorEntry.isSystemDefault() && colorEntry.systemColorRGB == null) {
					return null;
				}
				RGB rgb = colorEntry.isSystemDefault() ? colorEntry.systemColorRGB : colorEntry.getRGB();
				Color color = new Color(tableComposite.getParent().getDisplay(), rgb.red, rgb.green, rgb.blue);
				int dimensions = 10;
				Image image = new Image(tableComposite.getParent().getDisplay(), dimensions, dimensions);
				GC gc = new GC(image);
				// Draw color preview
				gc.setBackground(color);
				gc.fillRectangle(0, 0, dimensions, dimensions);
				// Draw outline around color preview
				gc.setBackground(new Color(tableComposite.getParent().getDisplay(), 0, 0, 0));
				gc.setLineWidth(2);
				gc.drawRectangle(0, 0, dimensions, dimensions);
				gc.dispose();
				color.dispose();
				colorPreviewImages.add(image);
				return image;
			}

			@Override
			public String getText(Object element) {
				return ((ColorEntry) element).label;
			}
		});
		TableColumn tc = new TableColumn(fAppearanceColorTableViewer.getTable(), SWT.NONE, 0);
		TableColumnLayout tableColumnLayout = new TableColumnLayout(true);
		PixelConverter pixelConverter = new PixelConverter(tableComposite.getParent().getFont());
		tableColumnLayout.setColumnData(tc, new ColumnWeightData(1, pixelConverter.convertWidthInCharsToPixels(30)));
		tableComposite.setLayout(tableColumnLayout);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_BOTH);
		Table fAppearanceColorTable = fAppearanceColorTableViewer.getTable();
		gd.heightHint = fAppearanceColorTable.getItemHeight() * fAppearanceColorListModel.length + fAppearanceColorTable.getItemHeight()/2;
		fAppearanceColorTable.setLayoutData(gd);
	}

	private void initializeFields() {
		Iterator<ColorEditor> colorEditors = fColorButtons.keySet().iterator();
		while (colorEditors.hasNext()) {
			ColorEditor c = colorEditors.next();
			String key = fColorButtons.get(c);
			RGB rgb = PreferenceConverter.getColor(fOverlayStore, key);
			c.setColorValue(rgb);
		}

		Iterator<Button> checkBoxes = fCheckBoxes.keySet().iterator();
		while (checkBoxes.hasNext()) {
			Button b = checkBoxes.next();
			String key = fCheckBoxes.get(b);
			b.setSelection(fOverlayStore.getBoolean(key));
		}
	}

	@Override
	protected void performApply() {
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performApply();
		}
		super.performApply();
	}

	@Override
	protected void performDefaults() {
		fOverlayStore.loadDefaults();

		initializeFields();

		handleAppearanceColorViewerSelection();

		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performDefaults();
		}

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		for (int i = 0; i < fTabs.length; i++) {
			fTabs[i].performOk();
		}

		fOverlayStore.propagate();
		SSEUIPlugin.getDefault().savePluginPreferences();

		return true;
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		if (data instanceof Map) {
			// clear the default description
			setDescription(""); // $NON-NLS-1$
			Map<?, ?> initializationData = (Map<?, ?>) data;
			initializationData.entrySet().forEach((entry) -> {
				if (PREFERENCE_SCOPE_NAME.equalsIgnoreCase(entry.getKey().toString())) {
					setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, entry.getValue().toString().toLowerCase(Locale.US)));
					fOverlayStore = new OverlayPreferenceStore(getPreferenceStore(), createOverlayStoreKeys());
					showHoverTab = false;
				}
				if (DESCRIPTION.equalsIgnoreCase(entry.getKey().toString())) {
					setDescription(entry.getValue().toString());
				}
			});
		}
	}
}
