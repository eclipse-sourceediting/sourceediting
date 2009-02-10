/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.sse.ui.internal.SSEUIMessages;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ColorHelper;
import org.eclipse.wst.sse.ui.internal.util.EditorUtility;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.eclipse.wst.xml.ui.internal.XMLUIMessages;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;
import org.eclipse.wst.xsl.ui.internal.style.IStyleConstantsXSL;
import org.eclipse.wst.xml.ui.internal.editor.IHelpContextIds;
import org.eclipse.wst.xsl.ui.internal.Messages;

import com.ibm.icu.text.Collator;

/**
 * A preference page to configure our XSL syntax color. It resembles the XML
 * pages.
 * 
 * @since 1.0
 */
public class XSLSyntaxColoringPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private static final String SYNTAXCOLORING_XSL = "syntaxcoloring.xsl"; //$NON-NLS-1$
	private static final String XSLFILES_DIR = "xslfiles"; //$NON-NLS-1$
	private Button bold;
	private Label foregroundLabel;
	private Label backgroundLabel;
	private Button clearStyle;
	private Map contextToXSLStyleMap;
	private Color defaultForeground = null;
	private Color defaultBackground = null;
	private IStructuredDocument document;
	private ColorSelector foregroundColorEditor;
	private ColorSelector backgroundColorEditor;
	private Button italic;
	private OverlayPreferenceStore overlayStore;
	private Button strike;
	private Collection<String> stylePreferenceKeys;
	private StructuredViewer stylesViewer = null;
	private Map styleToDescriptionMap;
	private StyledText styledText;
	private Button underline;

	// activate controls based on the given local color type
	private void activate(String namedStyle) {
		Color foreground = defaultForeground;
		Color background = defaultBackground;
		if (namedStyle == null) {
			clearStyle.setEnabled(false);
			bold.setEnabled(false);
			italic.setEnabled(false);
			strike.setEnabled(false);
			underline.setEnabled(false);
			foregroundLabel.setEnabled(false);
			backgroundLabel.setEnabled(false);
			foregroundColorEditor.setEnabled(false);
			backgroundColorEditor.setEnabled(false);
			bold.setSelection(false);
			italic.setSelection(false);
			strike.setSelection(false);
			underline.setSelection(false);
		} else {
			TextAttribute attribute = getAttributeFor(namedStyle);
			clearStyle.setEnabled(true);
			bold.setEnabled(true);
			italic.setEnabled(true);
			strike.setEnabled(true);
			underline.setEnabled(true);
			foregroundLabel.setEnabled(true);
			backgroundLabel.setEnabled(true);
			foregroundColorEditor.setEnabled(true);
			backgroundColorEditor.setEnabled(true);
			bold.setSelection((attribute.getStyle() & SWT.BOLD) != 0);
			italic.setSelection((attribute.getStyle() & SWT.ITALIC) != 0);
			strike
					.setSelection((attribute.getStyle() & TextAttribute.STRIKETHROUGH) != 0);
			underline
					.setSelection((attribute.getStyle() & TextAttribute.UNDERLINE) != 0);
			if (attribute.getForeground() != null) {
				foreground = attribute.getForeground();
			}
			if (attribute.getBackground() != null) {
				background = attribute.getBackground();
			}
		}

		foregroundColorEditor.setColorValue(foreground.getRGB());
		backgroundColorEditor.setColorValue(background.getRGB());
	}

	/**
	 * Color the text in the sample area according to the current preferences
	 */
	public void applyStyles() {
		if (styledText == null || styledText.isDisposed())
			return;
		IStructuredDocumentRegion documentRegion = document
				.getFirstStructuredDocumentRegion();

		while (documentRegion != null) {
			ITextRegionList regions = documentRegion.getRegions();
			for (int i = 0; i < regions.size(); i++) {
				ITextRegion currentRegion = regions.get(i);
				// lookup the local coloring type and apply it
				// This could be potentially expensive as we get the model and
				// read it pretty consistently.
				String namedStyle = null;
				if (contextToXSLStyleMap.containsKey(currentRegion.getType())) {
					namedStyle = (String) contextToXSLStyleMap
							.get(currentRegion.getType());
					if (namedStyle == null)
						continue;
					TextAttribute attribute = getAttributeFor(namedStyle);
					if (attribute == null)
						continue;
					StyleRange style = new StyleRange(documentRegion
							.getStartOffset(currentRegion), currentRegion
							.getTextLength(), attribute.getForeground(),
							attribute.getBackground(), attribute.getStyle());
					style.strikeout = (attribute.getStyle() & TextAttribute.STRIKETHROUGH) != 0;
					style.underline = (attribute.getStyle() & TextAttribute.UNDERLINE) != 0;
					styledText.setStyleRange(style);
				}
			}
			documentRegion = documentRegion.getNext();
		}
	}

	public Button createCheckbox(Composite parent, String label) {
		Button button = new Button(parent, SWT.CHECK);
		button.setText(label);
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return button;
	}

	/**
	 * Creates composite control and sets the default layout data.
	 */
	private Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		// GridData
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(data);
		return composite;
	}

	@Override
	protected Control createContents(final Composite parent) {
		initializeDialogUnits(parent);

		defaultForeground = parent.getDisplay().getSystemColor(
				SWT.COLOR_LIST_FOREGROUND);
		defaultBackground = parent.getDisplay().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND);
		Composite pageComponent = createComposite(parent, 2);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(pageComponent,
				IHelpContextIds.XML_PREFWEBX_STYLES_HELPID);

		Link link = new Link(pageComponent, SWT.WRAP);
		link.setText(Messages.XSLSyntaxColoringPage);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PreferencesUtil.createPreferenceDialogOn(parent.getShell(),
						e.text, null, null);
			}
		});

		GridData linkData = new GridData(SWT.FILL, SWT.BEGINNING, true, false,
				2, 1);
		linkData.widthHint = 150; // only expand further if anyone else requires
									// it
		link.setLayoutData(linkData);

		new Label(pageComponent, SWT.NONE).setLayoutData(new GridData());
		new Label(pageComponent, SWT.NONE).setLayoutData(new GridData());

		SashForm editor = new SashForm(pageComponent, SWT.VERTICAL);
		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData2.horizontalSpan = 2;
		editor.setLayoutData(gridData2);
		SashForm top = new SashForm(editor, SWT.HORIZONTAL);
		Composite styleEditor = createComposite(top, 1);
		((GridLayout) styleEditor.getLayout()).marginRight = 5;
		((GridLayout) styleEditor.getLayout()).marginLeft = 0;
		createLabel(styleEditor, XMLUIMessages.SyntaxColoringPage_0);
		stylesViewer = createStylesViewer(styleEditor);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalIndent = 0;
		Iterator iterator = styleToDescriptionMap.values().iterator();
		while (iterator.hasNext()) {
			gridData.widthHint = Math.max(gridData.widthHint,
					convertWidthInCharsToPixels(iterator.next().toString()
							.length()));
		}
		gridData.heightHint = convertHeightInCharsToPixels(5);
		stylesViewer.getControl().setLayoutData(gridData);

		Composite editingComposite = createComposite(top, 1);
		((GridLayout) styleEditor.getLayout()).marginLeft = 5;
		createLabel(editingComposite, ""); //$NON-NLS-1$
		Button enabler = createCheckbox(editingComposite,
				XMLUIMessages.SyntaxColoringPage_2);
		enabler.setEnabled(false);
		enabler.setSelection(true);
		Composite editControls = createComposite(editingComposite, 2);
		((GridLayout) editControls.getLayout()).marginLeft = 20;

		foregroundLabel = createLabel(editControls,
				SSEUIMessages.Foreground_UI_);
		((GridData) foregroundLabel.getLayoutData()).verticalAlignment = SWT.CENTER;
		foregroundLabel.setEnabled(false);

		foregroundColorEditor = new ColorSelector(editControls);
		Button fForegroundColor = foregroundColorEditor.getButton();
		GridData gd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		fForegroundColor.setLayoutData(gd);
		foregroundColorEditor.setEnabled(false);

		backgroundLabel = createLabel(editControls,
				SSEUIMessages.Background_UI_);
		((GridData) backgroundLabel.getLayoutData()).verticalAlignment = SWT.CENTER;
		backgroundLabel.setEnabled(false);

		backgroundColorEditor = new ColorSelector(editControls);
		Button fBackgroundColor = backgroundColorEditor.getButton();
		gd = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
		fBackgroundColor.setLayoutData(gd);
		backgroundColorEditor.setEnabled(false);

		bold = createCheckbox(editControls, XMLUIMessages.SyntaxColoringPage_3);
		bold.setEnabled(false);
		((GridData) bold.getLayoutData()).horizontalSpan = 2;
		italic = createCheckbox(editControls,
				XMLUIMessages.SyntaxColoringPage_4);
		italic.setEnabled(false);
		((GridData) italic.getLayoutData()).horizontalSpan = 2;
		strike = createCheckbox(editControls,
				XMLUIMessages.SyntaxColoringPage_5);
		strike.setEnabled(false);
		((GridData) strike.getLayoutData()).horizontalSpan = 2;
		underline = createCheckbox(editControls,
				XMLUIMessages.SyntaxColoringPage_6);
		underline.setEnabled(false);
		((GridData) underline.getLayoutData()).horizontalSpan = 2;
		clearStyle = new Button(editingComposite, SWT.PUSH);
		clearStyle.setText(SSEUIMessages.Restore_Default_UI_); // = "Restore Default"
		clearStyle.setLayoutData(new GridData(SWT.BEGINNING));
		((GridData) clearStyle.getLayoutData()).horizontalIndent = 20;
		clearStyle.setEnabled(false);

		Composite sampleArea = createComposite(editor, 1);

		((GridLayout) sampleArea.getLayout()).marginLeft = 5;
		((GridLayout) sampleArea.getLayout()).marginTop = 5;
		createLabel(sampleArea, SSEUIMessages.Sample_text__UI_); // = "&Sample text:"
		SourceViewer viewer = new SourceViewer(sampleArea, null, SWT.BORDER
				| SWT.LEFT_TO_RIGHT | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.READ_ONLY);
		styledText = viewer.getTextWidget();
		GridData gridData3 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData3.widthHint = convertWidthInCharsToPixels(20);
		gridData3.heightHint = convertHeightInCharsToPixels(5);
		gridData3.horizontalSpan = 2;
		styledText.setLayoutData(gridData3);
		styledText.setEditable(false);
		styledText.setFont(JFaceResources
				.getFont("org.eclipse.wst.sse.ui.textfont")); //$NON-NLS-1$
		styledText.addKeyListener(getTextKeyListener());
		styledText.addSelectionListener(getTextSelectionListener());
		styledText.addMouseListener(getTextMouseListener());
		styledText.addTraverseListener(getTraverseListener());
		setAccessible(styledText, SSEUIMessages.Sample_text__UI_);

		try {
			File file = XSLUIPlugin.makeFileFor(XSLSyntaxColoringPage.XSLFILES_DIR,
					XSLSyntaxColoringPage.SYNTAXCOLORING_XSL);
			document = StructuredModelManager.getModelManager()
					.createStructuredDocumentFor(file.getAbsolutePath(),
							new FileInputStream(file), null);
		} catch (Exception ex) {
			XSLUIPlugin.log(ex);
		}
		
		viewer.setDocument(document);

		top.setWeights(new int[] { 1, 1 });
		editor.setWeights(new int[] { 1, 1 });
		PlatformUI.getWorkbench().getHelpSystem().setHelp(pageComponent,
				IHelpContextIds.XML_PREFWEBX_STYLES_HELPID);

		stylesViewer.setInput(getStylePreferenceKeys());

		applyStyles();

		stylesViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						if (!event.getSelection().isEmpty()) {
							Object o = ((IStructuredSelection) event
									.getSelection()).getFirstElement();
							String namedStyle = o.toString();
							activate(namedStyle);
							if (namedStyle == null)
								return;
						}
					}
				});

		foregroundColorEditor.addListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(ColorSelector.PROP_COLORCHANGE)) {
					Object o = ((IStructuredSelection) stylesViewer
							.getSelection()).getFirstElement();
					String namedStyle = o.toString();
					String prefString = getOverlayStore().getString(namedStyle);
					String[] stylePrefs = ColorHelper
							.unpackStylePreferences(prefString);
					if (stylePrefs != null) {
						String oldValue = stylePrefs[0];
						// open color dialog to get new color
						String newValue = ColorHelper
								.toRGBString(foregroundColorEditor
										.getColorValue());

						if (!newValue.equals(oldValue)) {
							stylePrefs[0] = newValue;
							String newPrefString = ColorHelper
									.packStylePreferences(stylePrefs);
							getOverlayStore().setValue(namedStyle,
									newPrefString);
							applyStyles();
							styledText.redraw();
						}
					}
				}
			}
		});

		backgroundColorEditor.addListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(ColorSelector.PROP_COLORCHANGE)) {
					Object o = ((IStructuredSelection) stylesViewer
							.getSelection()).getFirstElement();
					String namedStyle = o.toString();
					String prefString = getOverlayStore().getString(namedStyle);
					String[] stylePrefs = ColorHelper
							.unpackStylePreferences(prefString);
					if (stylePrefs != null) {
						String oldValue = stylePrefs[1];
						// open color dialog to get new color
						String newValue = ColorHelper
								.toRGBString(backgroundColorEditor
										.getColorValue());

						if (!newValue.equals(oldValue)) {
							stylePrefs[1] = newValue;
							String newPrefString = ColorHelper
									.packStylePreferences(stylePrefs);
							getOverlayStore().setValue(namedStyle,
									newPrefString);
							applyStyles();
							styledText.redraw();
							activate(namedStyle);
						}
					}
				}
			}
		});

		bold.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				// get current (newly old) style
				Object o = ((IStructuredSelection) stylesViewer.getSelection())
						.getFirstElement();
				String namedStyle = o.toString();
				String prefString = getOverlayStore().getString(namedStyle);
				String[] stylePrefs = ColorHelper
						.unpackStylePreferences(prefString);
				if (stylePrefs != null) {
					String oldValue = stylePrefs[2];
					String newValue = String.valueOf(bold.getSelection());
					if (!newValue.equals(oldValue)) {
						stylePrefs[2] = newValue;
						String newPrefString = ColorHelper
								.packStylePreferences(stylePrefs);
						getOverlayStore().setValue(namedStyle, newPrefString);
						applyStyles();
						styledText.redraw();
					}
				}
			}
		});

		italic.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				// get current (newly old) style
				Object o = ((IStructuredSelection) stylesViewer.getSelection())
						.getFirstElement();
				String namedStyle = o.toString();
				String prefString = getOverlayStore().getString(namedStyle);
				String[] stylePrefs = ColorHelper
						.unpackStylePreferences(prefString);
				if (stylePrefs != null) {
					String oldValue = stylePrefs[3];
					String newValue = String.valueOf(italic.getSelection());
					if (!newValue.equals(oldValue)) {
						stylePrefs[3] = newValue;
						String newPrefString = ColorHelper
								.packStylePreferences(stylePrefs);
						getOverlayStore().setValue(namedStyle, newPrefString);
						applyStyles();
						styledText.redraw();
					}
				}
			}
		});

		strike.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				// get current (newly old) style
				Object o = ((IStructuredSelection) stylesViewer.getSelection())
						.getFirstElement();
				String namedStyle = o.toString();
				String prefString = getOverlayStore().getString(namedStyle);
				String[] stylePrefs = ColorHelper
						.unpackStylePreferences(prefString);
				if (stylePrefs != null) {
					String oldValue = stylePrefs[4];
					String newValue = String.valueOf(strike.getSelection());
					if (!newValue.equals(oldValue)) {
						stylePrefs[4] = newValue;
						String newPrefString = ColorHelper
								.packStylePreferences(stylePrefs);
						getOverlayStore().setValue(namedStyle, newPrefString);
						applyStyles();
						styledText.redraw();
					}
				}
			}
		});

		underline.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				// get current (newly old) style
				Object o = ((IStructuredSelection) stylesViewer.getSelection())
						.getFirstElement();
				String namedStyle = o.toString();
				String prefString = getOverlayStore().getString(namedStyle);
				String[] stylePrefs = ColorHelper
						.unpackStylePreferences(prefString);
				if (stylePrefs != null) {
					String oldValue = stylePrefs[5];
					String newValue = String.valueOf(underline.getSelection());
					if (!newValue.equals(oldValue)) {
						stylePrefs[5] = newValue;
						String newPrefString = ColorHelper
								.packStylePreferences(stylePrefs);
						getOverlayStore().setValue(namedStyle, newPrefString);
						applyStyles();
						styledText.redraw();
					}
				}
			}
		});

		clearStyle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (stylesViewer.getSelection().isEmpty())
					return;
				String namedStyle = ((IStructuredSelection) stylesViewer
						.getSelection()).getFirstElement().toString();
				getOverlayStore().setToDefault(namedStyle);
				applyStyles();
				styledText.redraw();
				activate(namedStyle);
			}
		});

		return pageComponent;
	}

	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		label.setLayoutData(data);
		label.setBackground(parent.getBackground());
		return label;
	}

	// protected Label createDescriptionLabel(Composite parent) {
	// return null;
	// }

	/**
	 * Set up all the style preference keys in the overlay store
	 */
	private OverlayKey[] createOverlayStoreKeys() {
		List overlayKeys = new ArrayList();

		Iterator i = getStylePreferenceKeys().iterator();
		while (i.hasNext()) {
			overlayKeys.add(new OverlayPreferenceStore.OverlayKey(
					OverlayPreferenceStore.STRING, (String) i.next()));
		}

		OverlayPreferenceStore.OverlayKey[] keys = new OverlayPreferenceStore.OverlayKey[overlayKeys
				.size()];
		overlayKeys.toArray(keys);
		return keys;
	}

	/**
	 * Creates the List viewer where we see the various syntax element display
	 * names--would it ever be a Tree like JDT's?
	 * 
	 * @param parent
	 * @return
	 */
	private StructuredViewer createStylesViewer(Composite parent) {
		StructuredViewer stylesViewer = new ListViewer(parent, SWT.SINGLE
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		stylesViewer
				.setComparator(new ViewerComparator(Collator.getInstance()));
		stylesViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Object description = styleToDescriptionMap.get(element);
				if (description != null)
					return description.toString();
				return super.getText(element);
			}
		});
		stylesViewer.setContentProvider(new ITreeContentProvider() {
			public void dispose() {
			}

			public Object[] getChildren(Object parentElement) {
				return getStylePreferenceKeys().toArray();
			}

			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}

			public Object getParent(Object element) {
				return getStylePreferenceKeys();
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
		return stylesViewer;
	}

	@Override
	public void dispose() {
		if (overlayStore != null) {
			overlayStore.stop();
		}
		super.dispose();
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return XSLUIPlugin.getDefault().getPreferenceStore();
	}

	private TextAttribute getAttributeFor(String namedStyle) {
		TextAttribute ta = new TextAttribute(defaultForeground,
				defaultBackground, SWT.NORMAL);

		if (namedStyle != null && overlayStore != null) {
			// note: "namedStyle" *is* the preference key
			String prefString = getOverlayStore().getString(namedStyle);
			String[] stylePrefs = ColorHelper
					.unpackStylePreferences(prefString);
			if (stylePrefs != null) {
				RGB foreground = ColorHelper.toRGB(stylePrefs[0]);
				RGB background = ColorHelper.toRGB(stylePrefs[1]);

				int fontModifier = SWT.NORMAL;

				if (stylePrefs.length > 2) {
					boolean on = Boolean.valueOf(stylePrefs[2]).booleanValue();
					if (on)
						fontModifier = fontModifier | SWT.BOLD;
				}
				if (stylePrefs.length > 3) {
					boolean on = Boolean.valueOf(stylePrefs[3]).booleanValue();
					if (on)
						fontModifier = fontModifier | SWT.ITALIC;
				}
				if (stylePrefs.length > 4) {
					boolean on = Boolean.valueOf(stylePrefs[4]).booleanValue();
					if (on)
						fontModifier = fontModifier
								| TextAttribute.STRIKETHROUGH;
				}
				if (stylePrefs.length > 5) {
					boolean on = Boolean.valueOf(stylePrefs[5]).booleanValue();
					if (on)
						fontModifier = fontModifier | TextAttribute.UNDERLINE;
				}

				ta = new TextAttribute((foreground != null) ? EditorUtility
						.getColor(foreground) : null,
						(background != null) ? EditorUtility
								.getColor(background) : null, fontModifier);
			}
		}
		return ta;
	}

	private String getNamedStyleAtOffset(int offset) {
		// ensure the offset is clean
		if (offset >= document.getLength())
			return getNamedStyleAtOffset(document.getLength() - 1);
		else if (offset < 0)
			return getNamedStyleAtOffset(0);
		IStructuredDocumentRegion documentRegion = document
				.getFirstStructuredDocumentRegion();
		while (documentRegion != null && !documentRegion.containsOffset(offset)) {
			documentRegion = documentRegion.getNext();
		}
		if (documentRegion != null) {
			// find the ITextRegion's Context at this offset
			ITextRegion interest = documentRegion
					.getRegionAtCharacterOffset(offset);
			if (interest == null)
				return null;
			if (offset > documentRegion.getTextEndOffset(interest))
				return null;
			String regionContext = interest.getType();
			if (regionContext == null)
				return null;
			// find the named style (internal/selectable name) for that
			// context
			String namedStyle = (String) contextToXSLStyleMap.get(regionContext);
			if (namedStyle != null) {
				return namedStyle;
			}
		}
		return null;
	}

	private OverlayPreferenceStore getOverlayStore() {
		return overlayStore;
	}

	private Collection<String> getStylePreferenceKeys() {
		if (stylePreferenceKeys == null) {
			List<String> styles = new ArrayList<String>();
			styles.add(IStyleConstantsXSL.TAG_BORDER);
			styles.add(IStyleConstantsXSL.TAG_NAME);
			styles.add(IStyleConstantsXSL.TAG_ATTRIBUTE_NAME);
			styles.add(IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE);
			stylePreferenceKeys = styles;
		}
		return stylePreferenceKeys;
	}

	private KeyListener getTextKeyListener() {
		return new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.widget instanceof StyledText) {
					int x = ((StyledText) e.widget).getCaretOffset();
					selectColorAtOffset(x);
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.widget instanceof StyledText) {
					int x = ((StyledText) e.widget).getCaretOffset();
					selectColorAtOffset(x);
				}
			}
		};
	}

	private MouseListener getTextMouseListener() {
		return new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				if (e.widget instanceof StyledText) {
					int x = ((StyledText) e.widget).getCaretOffset();
					selectColorAtOffset(x);
				}
			}
		};
	}

	private SelectionListener getTextSelectionListener() {
		return new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				selectColorAtOffset(e.x);
				if (e.widget instanceof StyledText) {
					((StyledText) e.widget).setSelection(e.x);
				}
			}

			public void widgetSelected(SelectionEvent e) {
				selectColorAtOffset(e.x);
				if (e.widget instanceof StyledText) {
					((StyledText) e.widget).setSelection(e.x);
				}
			}
		};
	}

	private TraverseListener getTraverseListener() {
		return new TraverseListener() {
			/**
			 * @see org.eclipse.swt.events.TraverseListener#keyTraversed(TraverseEvent)
			 */
			public void keyTraversed(TraverseEvent e) {
				if (e.widget instanceof StyledText) {
					if ((e.detail == SWT.TRAVERSE_TAB_NEXT)
							|| (e.detail == SWT.TRAVERSE_TAB_PREVIOUS))
						e.doit = true;
				}
			}
		};
	}

	public void init(IWorkbench workbench) {
		setDescription(SSEUIMessages.SyntaxColoring_Description);

		styleToDescriptionMap = new HashMap();
		contextToXSLStyleMap = new HashMap();

		initStyleToDescriptionMap();
		initRegionContextToStyleMap();

		overlayStore = new OverlayPreferenceStore(getPreferenceStore(),
				createOverlayStoreKeys());
		overlayStore.load();
		overlayStore.start();
	}

	private void initRegionContextToStyleMap() {
		xslContextToStyleMap();
	}


	@SuppressWarnings("unchecked")
	private void xslContextToStyleMap() {
		contextToXSLStyleMap.put(DOMRegionContext.XML_TAG_OPEN,
				IStyleConstantsXSL.TAG_BORDER);
		contextToXSLStyleMap.put(DOMRegionContext.XML_END_TAG_OPEN,
				IStyleConstantsXSL.TAG_BORDER);
		contextToXSLStyleMap.put(DOMRegionContext.XML_TAG_NAME,
				IStyleConstantsXSL.TAG_NAME);
		contextToXSLStyleMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_NAME,
				IStyleConstantsXSL.TAG_ATTRIBUTE_NAME);
		contextToXSLStyleMap.put(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE,
				IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE);
		contextToXSLStyleMap.put(DOMRegionContext.XML_TAG_CLOSE,
				IStyleConstantsXSL.TAG_BORDER);
		contextToXSLStyleMap.put(DOMRegionContext.XML_EMPTY_TAG_CLOSE,
				IStyleConstantsXSL.TAG_BORDER);
	}

	@SuppressWarnings("unchecked")
	private void initStyleToDescriptionMap() {
		styleToDescriptionMap.put(IStyleConstantsXSL.TAG_BORDER,
				XMLUIMessages.Tag_Delimiters_UI_);
		styleToDescriptionMap.put(IStyleConstantsXSL.TAG_NAME,
				XMLUIMessages.Tag_Names_UI_);
		styleToDescriptionMap.put(IStyleConstantsXSL.TAG_ATTRIBUTE_NAME,
				XMLUIMessages.Attribute_Names_UI_);
		styleToDescriptionMap.put(IStyleConstantsXSL.TAG_ATTRIBUTE_VALUE,
				XMLUIMessages.Attribute_Values_UI_);
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		getOverlayStore().loadDefaults();
		applyStyles();
		stylesViewer.setSelection(StructuredSelection.EMPTY);
		activate(null);
		styledText.redraw();
	}

	@Override
	public boolean performOk() {
		getOverlayStore().propagate();

		XSLUIPlugin.getDefault().savePluginPreferences();
		SSEUIPlugin.getDefault().savePluginPreferences();
		return true;
	}

	private void selectColorAtOffset(int offset) {
		String namedStyle = getNamedStyleAtOffset(offset);
		if (namedStyle != null) {
			stylesViewer.setSelection(new StructuredSelection(namedStyle));
			stylesViewer.reveal(namedStyle);
		} else {
			stylesViewer.setSelection(StructuredSelection.EMPTY);
		}
		activate(namedStyle);
	}

	/**
	 * Specifically set the reporting name of a control for accessibility
	 */
	private void setAccessible(Control control, String name) {
		if (control == null)
			return;
		final String n = name;
		control.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(AccessibleEvent e) {
				if (e.childID == ACC.CHILDID_SELF)
					e.result = n;
			}
		});
	}
}
