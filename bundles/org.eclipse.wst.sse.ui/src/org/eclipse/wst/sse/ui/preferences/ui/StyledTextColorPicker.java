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

import java.io.CharArrayReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleControlListener;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.sse.core.parser.RegionParser;
import org.eclipse.wst.sse.core.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.text.ITextRegion;
import org.eclipse.wst.sse.core.text.ITextRegionList;
import org.eclipse.wst.sse.core.util.Debug;
import org.eclipse.wst.sse.ui.Logger;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This class is configurable by setting 3 properties: 1) an array of Strings
 * as the styleNames; one unique entry for every style type meant to be
 * configurable by the user 2) a Dictionary of descriptions, mapping the
 * styleNames to unique descriptions - meant for use within the selection
 * ComboBox
 * 
 * TODO (pa) this should probably be working off document partitions now (2.1+) 3)
 * a Dictionary mapping parsed ITextRegion contexts (strings) to the locally
 * defined styleNames
 *  
 */
public class StyledTextColorPicker extends Composite {
	protected class DescriptionSorter extends org.eclipse.wst.sse.ui.util.Sorter {
		Collator collator = Collator.getInstance();

		public boolean compare(Object elementOne, Object elementTwo) {
			/**
			 * Returns true if elementTwo is 'greater than' elementOne This is
			 * the 'ordering' method of the sort operation. Each subclass
			 * overides this method with the particular implementation of the
			 * 'greater than' concept for the objects being sorted.
			 */
			return (collator.compare(elementOne.toString(), elementTwo.toString())) < 0;
		}
	}

	// names for preference elements ... non-NLS
	public static final String FOREGROUND = "foreground"; //$NON-NLS-1$
	public static final String BACKGROUND = "background"; //$NON-NLS-1$
	public static final String BOLD = "bold"; //$NON-NLS-1$
	public static final String ITALIC = "italic"; //$NON-NLS-1$
	public static final String NAME = "name"; //$NON-NLS-1$
	public static final String COLOR = "color"; //$NON-NLS-1$
	protected StyledText fText = null;
	private IStructuredDocumentRegion fNodes = null;
	/*
	 * A DOM Node named "colors" with many children Elements named "color" Each
	 * color Element supports 'name', 'bold', 'italic'(future?), 'foreground'
	 * and 'background' HTML-style color definitions
	 * 
	 * <colors> <color foreground="#008080" name="TAG_BORDER" bold="false"/>
	 */
	protected Node fColorsNode = null;
	// An identical Node holding the default values for use when restoring a
	// <em>single</em> setting; optional.
	protected Node fDefaultColorsNode = null;
	// A RegionParser, which will turn the input into
	// IStructuredDocumentRegion(s) and Regions
	protected RegionParser fParser = null;
	// The list of supported ITextRegion types [Strings]
	protected List fStyleList = null;
	// Dictionary mapping the ITextRegion types above to display strings, for
	// use in the combo box
	protected Dictionary fDescriptions = null;
	// Dictionary mapping the ITextRegion types above to color names, which
	// are, in turn, attributes
	protected Dictionary fContextStyleMap = null;
	protected Combo fStyleCombo = null;
	protected Button fForeground;
	protected Button fBackground;
	protected Button fClearStyle;
	protected Button fBold;
	protected Button fItalic;
	protected Label fForegroundLabel;
	protected Label fBackgroundLabel;
	protected String fInput = ""; //$NON-NLS-1$
	protected Color fDefaultForeground = getDisplay().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	protected Color fDefaultBackground = getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	protected List usedColors = new ArrayList();
	private static final boolean showItalic = false;
	protected SelectionListener comboListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			int selectedIndex = fStyleCombo.getSelectionIndex();
			String description = selectedIndex >= 0 ? fStyleCombo.getItem(selectedIndex) : null;
			activate(getStyleName(description));
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	protected SelectionListener buttonListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			String namedStyle = getStyleName(fStyleCombo.getItem(fStyleCombo.getSelectionIndex()));
			if (namedStyle == null)
				return;
			if (e.widget == fForeground) {
				String oldValue = toRGBString(getAttribute(namedStyle).getForeground().getRGB());
				String newValue = changeColor(oldValue);
				getStyleElement(namedStyle).setAttribute(FOREGROUND, newValue);
				if (!newValue.equals(oldValue))
					refresh();
			}
			else if (e.widget == fBackground) {
				String oldValue = toRGBString(getAttribute(namedStyle).getBackground().getRGB());
				String newValue = changeColor(oldValue);
				getStyleElement(namedStyle).setAttribute(BACKGROUND, newValue);
				if (!newValue.equals(oldValue))
					refresh();
			}
			else if (e.widget == fClearStyle) {
				clearStyle(getStyleElement(namedStyle));
				refresh();
			}
			else if (e.widget == fBold) {
				String oldValue = getStyleElement(namedStyle).getAttribute(BOLD);
				String newValue = String.valueOf(fBold.getSelection());
				getStyleElement(namedStyle).setAttribute(BOLD, newValue);
				if (!newValue.equals(oldValue))
					refresh();
			}
			else if (showItalic && e.widget == fItalic) {
				String oldValue = getStyleElement(namedStyle).getAttribute(ITALIC);
				String newValue = String.valueOf(fItalic.getSelection());
				getStyleElement(namedStyle).setAttribute(ITALIC, newValue);
				if (!newValue.equals(oldValue))
					refresh();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	// defect 200764 - ACC:display values for color buttons
	protected AccessibleControlListener foregroundAccListener = new AccessibleControlAdapter() {
		/**
		 * @see org.eclipse.swt.accessibility.AccessibleControlAdapter#getValue(AccessibleControlEvent)
		 */
		public void getValue(AccessibleControlEvent e) {
			if (e.childID == ACC.CHILDID_SELF) {
				e.result = getColorButtonValue(fForeground);
			}
		}
	};
	protected AccessibleControlListener backgroundAccListener = new AccessibleControlAdapter() {
		/**
		 * @see org.eclipse.swt.accessibility.AccessibleControlAdapter#getValue(AccessibleControlEvent)
		 */
		public void getValue(AccessibleControlEvent e) {
			if (e.childID == ACC.CHILDID_SELF) {
				e.result = getColorButtonValue(fBackground);
			}
		}
	};

	/**
	 * XMLTextColorPicker constructor comment.
	 * 
	 * @param parent
	 *            org.eclipse.swt.widgets.Composite
	 * @param style
	 *            int
	 */
	public StyledTextColorPicker(Composite parent, int style) {
		super(parent, style);
		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		setLayout(layout);
		createControls(this);
	}

	// activate controls based on the given local color type
	private void activate(String namedStyle) {
		if (namedStyle == null) {
			fForeground.setEnabled(false);
			fBackground.setEnabled(false);
			fClearStyle.setEnabled(false);
			fBold.setEnabled(false);
			if (showItalic)
				fItalic.setEnabled(false);
			fForegroundLabel.setEnabled(false);
			fBackgroundLabel.setEnabled(false);
		}
		else {
			fForeground.setEnabled(true);
			fBackground.setEnabled(true);
			fClearStyle.setEnabled(true);
			fBold.setEnabled(true);
			if (showItalic)
				fItalic.setEnabled(true);
			fForegroundLabel.setEnabled(true);
			fBackgroundLabel.setEnabled(true);
		}
		TextAttribute attribute = getAttribute(namedStyle);
		if (fForeground.getSize().x > 0 && fForeground.getSize().y > 0 && (fForeground.getImage() == null || fForeground.getImage().getImageData() == null || fForeground.getImage().getImageData().getRGBs() == null || fForeground.getImage().getImageData().getRGBs().length < 1 || !fForeground.getImage().getImageData().getRGBs()[0].equals(attribute.getForeground().getRGB()))) {
			if (fForeground.getImage() != null)
				fForeground.getImage().dispose();
			Image foreground = new Image(getDisplay(), new ImageData(fForeground.getSize().x, fForeground.getSize().y, 1, new PaletteData(new RGB[]{attribute.getForeground().getRGB()})));
			fForeground.setImage(foreground);
		}
		if (fBackground.getSize().x > 0 && fBackground.getSize().y > 0 && (fBackground.getImage() == null || fBackground.getImage().getImageData() == null || fBackground.getImage().getImageData().getRGBs() == null || fBackground.getImage().getImageData().getRGBs().length < 1 || !fBackground.getImage().getImageData().getRGBs()[0].equals(attribute.getBackground().getRGB()))) {
			if (fBackground.getImage() != null)
				fBackground.getImage().dispose();
			Image background = new Image(getDisplay(), new ImageData(fBackground.getSize().x, fBackground.getSize().y, 1, new PaletteData(new RGB[]{attribute.getBackground().getRGB()})));
			fBackground.setImage(background);
		}
		fBold.setSelection((attribute.getStyle() & SWT.BOLD) != 0);
		if (showItalic)
			fItalic.setSelection((attribute.getStyle() & SWT.ITALIC) != 0);
	}

	protected void applyStyles() {
		if (fText == null || fText.isDisposed() || fInput == null || fInput.length() == 0)
			return;
		//		List regions = fParser.getRegions();
		IStructuredDocumentRegion node = fNodes;
		while (node != null) {
			ITextRegionList regions = node.getRegions();
			for (int i = 0; i < regions.size(); i++) {
				ITextRegion currentRegion = regions.get(i);
				// lookup the local coloring type and apply it
				String namedStyle = (String) getContextStyleMap().get(currentRegion.getType());
				if (namedStyle == null)
					continue;
				TextAttribute attribute = getAttribute(namedStyle);
				if (attribute == null)
					continue;
				StyleRange style = new StyleRange(node.getStartOffset(currentRegion), currentRegion.getLength(), attribute.getForeground(), attribute.getBackground(), attribute.getStyle());
				fText.setStyleRange(style);
			}
			node = node.getNext();
		}
	}

	private String changeColor(String rgb) {
		return toRGBString(changeColor(toRGB(rgb)));
	}

	private RGB changeColor(RGB startValue) {
		ColorDialog colorDlg = new ColorDialog(getShell());
		if (startValue != null)
			colorDlg.setRGB(startValue);
		colorDlg.open();
		RGB newRGB = colorDlg.getRGB();
		if (newRGB != null)
			return newRGB;
		return startValue;
	}

	public void clearStyle(Element color) {
		if (color == null)
			return;
		final Element colorToClear = color;
		BusyIndicator.showWhile(getDisplay(), new Runnable() {
			public void run() {
				primClearStyle(colorToClear);
			}
		});
	}

	protected void close() {
		releaseColors();
	}

	/**
	 * Creates an new checkbox instance and sets the default layout data.
	 * 
	 * @param group
	 *            the composite in which to create the checkbox
	 * @param label
	 *            the string to set into the checkbox
	 * @return the new checkbox
	 */
	private Button createCheckBox(Composite group, String label) {
		Button button = new Button(group, SWT.CHECK | SWT.CENTER);
		if (label != null)
			button.setText(label);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
		//	data.verticalAlignment = GridData.VERTICAL_ALIGN_FILL;
		button.setLayoutData(data);
		return button;
	}

	private Combo createCombo(Composite parent, String[] labels, int selectedItem) {
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(labels);
		if (selectedItem >= 0)
			combo.select(selectedItem);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
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
		layout.horizontalSpacing = 5;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);
		//GridData
		GridData data = new GridData(GridData.FILL_VERTICAL);
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		composite.setLayoutData(data);
		return composite;
	}

	protected void createControls(Composite parent) {
		Composite styleRow = createComposite(parent, 3);
		// row 1 - content type label, combo box, restore defaults
		createLabel(styleRow, ResourceHandler.getString("Content_type__UI_")); //$NON-NLS-1$ = "Content type:"
		// Contexts combo box
		fStyleCombo = createCombo(styleRow, new String[0], -1);
		fClearStyle = createPushButton(styleRow, ResourceHandler.getString("Restore_Default_UI_")); //$NON-NLS-1$ = "Restore Default"
		Composite styleRow2;
		if (showItalic)
			styleRow2 = createComposite(parent, 7);
		else
			styleRow2 = createComposite(parent, 6);
		// row 2 - foreground label, button, background label, button, bold,
		// italics?
		fForegroundLabel = createLabel(styleRow2, ResourceHandler.getString("Foreground_UI_")); //$NON-NLS-1$ = "Foreground"
		fForeground = createPushButton(styleRow2, ""); //$NON-NLS-1$
		setAccessible(fForeground, fForegroundLabel.getText());
		fForeground.getAccessible().addAccessibleControlListener(foregroundAccListener); // defect
		// 200764
		// -
		// ACC:display
		// values
		// for
		// color
		// buttons
		Point buttonSize = computeImageSize(parent);
		((GridData) fForeground.getLayoutData()).widthHint = buttonSize.x;
		((GridData) fForeground.getLayoutData()).heightHint = buttonSize.y;
		fBackgroundLabel = createLabel(styleRow2, ResourceHandler.getString("Background_UI_")); //$NON-NLS-1$ = "Background"
		fBackground = createPushButton(styleRow2, ""); //$NON-NLS-1$
		setAccessible(fBackground, fBackgroundLabel.getText());
		fBackground.getAccessible().addAccessibleControlListener(backgroundAccListener); // defect
		// 200764
		// -
		// ACC:display
		// values
		// for
		// color
		// buttons
		((GridData) fBackground.getLayoutData()).widthHint = buttonSize.x;
		((GridData) fBackground.getLayoutData()).heightHint = buttonSize.y;
		createLabel(styleRow2, ""); //$NON-NLS-1$
		fBold = createCheckBox(styleRow2, ResourceHandler.getString("Bold_UI_")); //$NON-NLS-1$ = "Bold"
		if (showItalic)
			fItalic = createCheckBox(styleRow2, ResourceHandler.getString("Italic")); //$NON-NLS-1$
		//		// Defaults checkbox
		fForeground.setEnabled(false);
		fBackground.setEnabled(false);
		fClearStyle.setEnabled(false);
		fBold.setEnabled(false);
		if (showItalic)
			fItalic.setEnabled(false);
		fForegroundLabel.setEnabled(false);
		fBackgroundLabel.setEnabled(false);
		Composite sample = createComposite(parent, 1);
		createLabel(sample, ResourceHandler.getString("Sample_text__UI_")); //$NON-NLS-1$ = "&Sample text:"
		fText = new StyledText(sample, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_BOTH);
		fText.setLayoutData(data);
		fText.setEditable(false);
		fText.setBackground(fDefaultBackground);
		fText.setFont(JFaceResources.getTextFont());
		fText.addKeyListener(getTextKeyListener());
		fText.addSelectionListener(getTextSelectionListener());
		fText.addMouseListener(getTextMouseListener());
		fText.addTraverseListener(getTraverseListener()); // defect 220377 -
		// Provide tab
		// traversal for
		// fText widget
		setAccessible(fText, ResourceHandler.getString("Sample_text__UI_")); //$NON-NLS-1$ = "&Sample text:"
		fForeground.addSelectionListener(buttonListener);
		fBackground.addSelectionListener(buttonListener);
		fClearStyle.addSelectionListener(buttonListener);
		fBold.addSelectionListener(buttonListener);
		if (showItalic)
			fItalic.addSelectionListener(buttonListener);
		fStyleCombo.addSelectionListener(comboListener);
		parent.addDisposeListener(getDisposeListener());
	}

	/**
	 * Utility method that creates a label instance and sets the default layout
	 * data.
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
		GridData data = new GridData(GridData.FILL_BOTH);
		//	data.horizontalAlignment = GridData.FILL;
		button.setLayoutData(data);
		return button;
	}

	/**
	 * Specifically set the reporting name of a control for accessibility
	 */
	private void setAccessible(Control control, String name) {
		if (control == null)
			return;
		final String n = name;
		control.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			public void getName(AccessibleEvent e) {
				if (e.childID == ACC.CHILDID_SELF)
					e.result = n;
			}
		});
	}

	// defect 200764 - ACC:display values for color buttons
	/**
	 * @return String - color Button b's current RBG value
	 */
	private String getColorButtonValue(Button b) {
		if ((b == null) || (b.getImage() == null) || (b.getImage().getImageData() == null) || (b.getImage().getImageData().getRGBs() == null) || (b.getImage().getImageData().getRGBs()[0] == null))
			return null;
		String val = b.getImage().getImageData().getRGBs()[0].toString();
		return val;
	}

	protected TextAttribute getAttribute(String namedStyle) {
		if (namedStyle == null || namedStyle.length() < 1 || fColorsNode == null)
			return new TextAttribute(getDefaultForeground(), getDefaultBackground(), SWT.NORMAL);
		Node colorsElement = (Element) fColorsNode;
		NodeList colors = colorsElement.getChildNodes();
		Element color = null;
		for (int i = 0; i < colors.getLength(); i++) {
			Node tester = colors.item(i);
			if (tester.getNodeType() == Node.ELEMENT_NODE && ((Element) tester).getAttribute(NAME).equals(namedStyle)) {
				color = (Element) tester;
				break;
			}
		}
		if (color == null) {
			// create one
			color = colorsElement.getOwnerDocument().createElement(COLOR);
			color.setAttribute(NAME, namedStyle);
			colorsElement.appendChild(color);
		}
		int fontModifier = SWT.NORMAL;
		if (Boolean.valueOf(color.getAttribute(BOLD)).booleanValue())
			fontModifier = fontModifier | SWT.BOLD;
		if (showItalic && Boolean.valueOf(color.getAttribute(ITALIC)).booleanValue())
			fontModifier = fontModifier | SWT.ITALIC;
		return new TextAttribute(getColor(toRGB(color.getAttribute(FOREGROUND), getDefaultForeground().getRGB())), getColor(toRGB(color.getAttribute(BACKGROUND), getDefaultBackground().getRGB())), fontModifier);
	}

	private Color getColor(int r, int g, int b) {
		for (int i = 0; i < usedColors.size(); i++) {
			Color color = (Color) usedColors.get(i);
			if (color.getRed() == r && color.getGreen() == g && color.getBlue() == b)
				return color;
		}
		Color newColor = new Color(getDisplay(), r, g, b);
		usedColors.add(newColor);
		return newColor;
	}

	private Color getColor(RGB rgb) {
		return getColor(rgb.red, rgb.green, rgb.blue);
	}

	/**
	 * @return org.w3c.dom.Node
	 */
	public Node getColorsNode() {
		return fColorsNode;
	}

	/**
	 * @return java.util.Dictionary
	 */
	public Dictionary getContextStyleMap() {
		return fContextStyleMap;
	}

	/**
	 * @return org.eclipse.swt.graphics.Color
	 */
	public Color getDefaultBackground() {
		return fDefaultBackground;
	}

	/**
	 * @return org.w3c.dom.Node
	 */
	public Node getDefaultColorsNode() {
		return fDefaultColorsNode;
	}

	/**
	 * @return org.eclipse.swt.graphics.Color
	 */
	public Color getDefaultForeground() {
		return fDefaultForeground;
	}

	/**
	 * @return java.util.Dictionary
	 */
	public Dictionary getDescriptions() {
		return fDescriptions;
	}

	private DisposeListener getDisposeListener() {
		return new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				close();
			}
		};
	}

	public Font getFont() {
		return fText.getFont();
	}

	protected String getNamedStyleAtOffset(int offset) {
		// ensure the offset is clean
		if (offset >= fInput.length())
			return getNamedStyleAtOffset(fInput.length() - 1);
		else if (offset < 0)
			return getNamedStyleAtOffset(0);
		// find the ITextRegion at this offset
		if (fNodes == null)
			return null;
		IStructuredDocumentRegion aNode = fNodes;
		while (aNode != null && !aNode.containsOffset(offset))
			aNode = aNode.getNext();
		if (aNode != null) {
			// find the ITextRegion's Context at this offset
			ITextRegion interest = aNode.getRegionAtCharacterOffset(offset);
			if (interest == null)
				return null;
			if (offset > aNode.getTextEndOffset(interest))
				return null;
			String regionContext = interest.getType();
			if (regionContext == null)
				return null;
			// find the named style (internal/selectable name) for that context
			String namedStyle = (String) getContextStyleMap().get(regionContext);
			if (namedStyle != null) {
				return namedStyle;
			}
		}
		return null;
	}

	/**
	 */
	public RegionParser getParser() {
		return fParser;
	}

	private Element getStyleElement(String namedStyle) {
		if (namedStyle == null || namedStyle.length() < 1)
			return null;
		Element colorsElement = (Element) fColorsNode;
		NodeList colors = colorsElement.getChildNodes();
		Element color = null;
		for (int i = 0; i < colors.getLength(); i++) {
			Node tester = colors.item(i);
			if (tester.getNodeType() == Node.ELEMENT_NODE && ((Element) tester).getAttribute(NAME).equals(namedStyle)) {
				color = (Element) tester;
				break;
			}
		}
		if (color == null) {
			// create one
			color = fColorsNode.getOwnerDocument().createElement(COLOR);
			color.setAttribute(NAME, namedStyle);
			colorsElement.appendChild(color);
		}
		return color;
	}

	/**
	 * @return String[]
	 */
	public List getStyleList() {
		return fStyleList;
	}

	private String getStyleName(String description) {
		if (description == null)
			return null;
		String styleName = null;
		java.util.Enumeration keys = getDescriptions().keys();
		while (keys.hasMoreElements()) {
			String test = keys.nextElement().toString();
			if (getDescriptions().get(test).equals(description)) {
				styleName = test;
				break;
			}
		}
		return styleName;
	}

	public String getText() {
		return fInput;
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

			public void widgetDoubleSelected(SelectionEvent e) {
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

	// defect 220377 - Provide tab traversal for fText widget
	private TraverseListener getTraverseListener() {
		return new TraverseListener() {
			/**
			 * @see org.eclipse.swt.events.TraverseListener#keyTraversed(TraverseEvent)
			 */
			public void keyTraversed(TraverseEvent e) {
				if (e.widget instanceof StyledText) {
					if ((e.detail == SWT.TRAVERSE_TAB_NEXT) || (e.detail == SWT.TRAVERSE_TAB_PREVIOUS))
						e.doit = true;
				}
			}
		};
	}

	protected void primClearStyle(Element color) {
		// if display is null by the time we execute, we've been disposed
		// already
		if (getDisplay() == null)
			return;
		if (color == null)
			return;
		boolean stylesCleared = false;
		if (fDefaultColorsNode != null) {
			// Look for a default color with the same name
			NodeList defaultColors = fDefaultColorsNode.getChildNodes();
			Element defaultColor = null;
			for (int i = 0; i < defaultColors.getLength(); i++) {
				Node tester = defaultColors.item(i);
				if (tester.getNodeType() == Node.ELEMENT_NODE && ((Element) tester).getAttribute(NAME).equals(color.getAttribute(NAME))) {
					defaultColor = (Element) tester;
					break;
				}
			}
			// If a default color was found, clear the current attributes and
			// copy all of the defaults
			if (defaultColor != null) {
				NamedNodeMap attributes = defaultColor.getAttributes();
				if (attributes != null && attributes.getLength() > 0) {
					NamedNodeMap oldAttributes = color.getAttributes();
					int count = 0;
					int totalAttributes = (oldAttributes == null) ? 0 : oldAttributes.getLength();
					while (oldAttributes.getLength() > 0 && count < totalAttributes) {
						color.removeAttributeNode((Attr) oldAttributes.item(0));
						count++;
					}
					stylesCleared = true;
					for (int i = 0; i < attributes.getLength(); i++) {
						Node attribute = attributes.item(i);
						color.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
					}
				}
			}
		}
		if (!stylesCleared) {
			// restore widget defaults
			color.removeAttribute(ColorNames.FOREGROUND);
			color.removeAttribute(ColorNames.BACKGROUND);
			color.removeAttribute(ColorNames.BOLD);
			color.removeAttribute(ColorNames.ITALIC);
		}
	}

	// refresh the GUI after a color change
	public void refresh() {
		fText.setRedraw(false);
		int selectedIndex = fStyleCombo.getSelectionIndex();
		String description = selectedIndex >= 0 ? fStyleCombo.getItem(selectedIndex) : null;
		activate(getStyleName(description));
		// update Font
		fText.setFont(JFaceResources.getTextFont());
		// reapplyStyles
		applyStyles();
		fText.setRedraw(true);
	}

	private void releaseColors() {
		java.util.Iterator colors = usedColors.iterator();
		while (colors.hasNext())
			((Color) colors.next()).dispose();
	}

	public void releasePickerResources() {
		if (fForeground != null && !fForeground.isDisposed() && fForeground.getImage() != null)
			fForeground.getImage().dispose();
		if (fBackground != null && !fBackground.isDisposed() && fBackground.getImage() != null)
			fBackground.getImage().dispose();
	}

	private void selectColorAtOffset(int offset) {
		String namedStyle = getNamedStyleAtOffset(offset);
		if (namedStyle == null) {
			fStyleCombo.deselectAll();
			activate(null);
			return;
		}
		String description = (String) getDescriptions().get(namedStyle);
		if (description == null)
			return;
		int itemCount = fStyleCombo.getItemCount();
		for (int i = 0; i < itemCount; i++) {
			if (fStyleCombo.getItem(i).equals(description)) {
				fStyleCombo.select(i);
				break;
			}
		}
		activate(namedStyle);
	}

	/**
	 * @param newColorsNode
	 *            org.w3c.dom.Node
	 */
	public void setColorsNode(Node newColorsNode) {
		fColorsNode = newColorsNode;
	}

	/**
	 * @param newContextStyleMap
	 *            java.util.Dictionary
	 */
	public void setContextStyleMap(Dictionary newContextStyleMap) {
		fContextStyleMap = newContextStyleMap;
	}

	/**
	 * @param newDefaultBackground
	 *            org.eclipse.swt.graphics.Color
	 */
	public void setDefaultBackground(Color newDefaultBackground) {
		fDefaultBackground = newDefaultBackground;
	}

	/**
	 * @param newDefaultColorsNode
	 *            org.w3c.dom.Node
	 */
	public void setDefaultColorsNode(Node newDefaultColorsNode) {
		fDefaultColorsNode = newDefaultColorsNode;
	}

	/**
	 * @param newDefaultForeground
	 *            org.eclipse.swt.graphics.Color
	 */
	public void setDefaultForeground(Color newDefaultForeground) {
		fDefaultForeground = newDefaultForeground;
	}

	/**
	 * @param newDescriptions
	 *            java.util.Dictionary
	 */
	public void setDescriptions(Dictionary newDescriptions) {
		fDescriptions = newDescriptions;
		updateStyleList();
	}

	public void setFont(Font font) {
		fText.setFont(font);
		fText.redraw();
	}

	/**
	 * @param newParser
	 */
	public void setParser(RegionParser newParser) {
		fParser = newParser;
	}

	/**
	 * @param newStyleList
	 *            String[]
	 */
	public void setStyleList(List newStyleList) {
		fStyleList = newStyleList;
		updateStyleList();
	}

	public void setText(String s) {
		fInput = s;
		getParser().reset(new CharArrayReader(fInput.toCharArray()));
		fNodes = getParser().getDocumentRegions();
		if (Debug.displayInfo)
			System.out.println("Length of input: " //$NON-NLS-1$
						//$NON-NLS-1$
						+ s.length() + ", " //$NON-NLS-1$
						+ getParser().getRegions().size() + " regions."); //$NON-NLS-1$
		if (fText != null)
			fText.setText(s);
		applyStyles();
	}

	/**
	 * @return org.eclipse.swt.graphics.RGB
	 * @param anRGBString
	 *            java.lang.String
	 */
	private RGB toRGB(String anRGBString) {
		RGB result = null;
		if (anRGBString.length() > 6 && anRGBString.charAt(0) == '#') {
			int r = 0;
			int g = 0;
			int b = 0;
			try {
				r = Integer.valueOf(anRGBString.substring(1, 3), 16).intValue();
				g = Integer.valueOf(anRGBString.substring(3, 5), 16).intValue();
				b = Integer.valueOf(anRGBString.substring(5, 7), 16).intValue();
				result = new RGB(r, g, b);
			}
			catch (NumberFormatException nfExc) {
				Logger.logException("Could not load highlighting preference for color " + anRGBString, nfExc); //$NON-NLS-1$
			}
		}
		return result;
	}

	/**
	 * @return org.eclipse.swt.graphics.RGB
	 * @param anRGBString
	 *            java.lang.String
	 * @param defaultRGB
	 *            org.eclipse.swt.graphics.RGB
	 */
	private RGB toRGB(String anRGBString, RGB defaultRGB) {
		RGB result = toRGB(anRGBString);
		if (result == null)
			return defaultRGB;
		return result;
	}

	/**
	 * @return java.lang.String
	 * @param anRGB
	 *            org.eclipse.swt.graphics.RGB
	 */
	private String toRGBString(RGB anRGB) {
		if (anRGB == null)
			return "#000000"; //$NON-NLS-1$
		String red = Integer.toHexString(anRGB.red);
		while (red.length() < 2)
			red = "0" + red; //$NON-NLS-1$
		String green = Integer.toHexString(anRGB.green);
		while (green.length() < 2)
			green = "0" + green; //$NON-NLS-1$
		String blue = Integer.toHexString(anRGB.blue);
		while (blue.length() < 2)
			blue = "0" + blue; //$NON-NLS-1$
		return "#" + red + green + blue; //$NON-NLS-1$
	}

	private void updateStyleList() {
		if (fStyleList == null || fDescriptions == null)
			return;
		String[] descriptions = new String[fStyleList.size()];
		for (int i = 0; i < fStyleList.size(); i++) {
			if (fStyleList.get(i) != null)
				descriptions[i] = (String) getDescriptions().get(fStyleList.get(i));
			else
				descriptions[i] = (String) fStyleList.get(i);
		}
		Object[] sortedObjects = new DescriptionSorter().sort(descriptions);
		String[] sortedDescriptions = new String[descriptions.length];
		for (int i = 0; i < descriptions.length; i++) {
			sortedDescriptions[i] = sortedObjects[i].toString();
		}
		fStyleCombo.setItems(sortedDescriptions);
		fStyleCombo.select(0); //defect 219855 - initially select first item
		// in comboBox
		//		fStyleCombo.deselectAll();
	}

	/**
	 * Determines size of color button copied from
	 * org.eclipse.jdt.internal.ui.preferences.ColorEditor 1 modification -
	 * added 4 to final height
	 */
	private Point computeImageSize(Control window) {
		GC gc = new GC(window);
		Font f = JFaceResources.getFontRegistry().get(JFaceResources.DEFAULT_FONT);
		gc.setFont(f);
		int height = gc.getFontMetrics().getHeight();
		gc.dispose();
		Point p = new Point(height * 3 - 6, height + 4);
		return p;
	}
}
