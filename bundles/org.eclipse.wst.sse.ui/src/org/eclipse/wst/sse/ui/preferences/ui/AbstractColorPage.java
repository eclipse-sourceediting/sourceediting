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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.sse.ui.nls.ResourceHandler;
import org.eclipse.wst.sse.ui.preferences.PreferenceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public abstract class AbstractColorPage extends org.eclipse.jface.preference.PreferencePage implements org.eclipse.ui.IWorkbenchPreferencePage {

	protected StyledTextColorPicker fPicker = null;

	protected Control createContents(Composite parent) {
		// create scrollbars for this preference page when needed
		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite pageComponent = createComposite(sc1, 1);
		sc1.setContent(pageComponent);

		Label descLabel = createDescriptionLabel(pageComponent, ResourceHandler.getString("AbstractColorPageDescription")); //$NON-NLS-1$
		Composite coloringComposite = createColoringComposite(pageComponent);

		createContentsForPicker(coloringComposite);

		GridData gd = (GridData) descLabel.getLayoutData();
		gd.widthHint = (coloringComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT)).x;
		setSize(pageComponent);
		return pageComponent;
	}

	/**
	 * Creates the coloring group used in createContents
	 * This method can be overwritten to set the text of the group
	 * or provide an infopop
	 */
	protected Composite createColoringComposite(Composite parent) {
		Composite coloringComposite = createComposite(parent, 1);
		return coloringComposite;
	}

	/**
	 * Creates the StyledTextColorPicker used in createContents
	 * This method can be overwritten to set up StyledTextColorPicker
	 * differently
	 */
	protected void createContentsForPicker(Composite parent) {
		// create the color picker
		fPicker = new StyledTextColorPicker(parent, SWT.NULL);
		GridData data = new GridData(GridData.FILL_BOTH);
		fPicker.setLayoutData(data);
		fPicker.setColorsNode(getColorManager().getRootElement().cloneNode(true));
		fPicker.setDefaultColorsNode(getColorManager().createDefaultPreferences().getDocumentElement());

		setupPicker(fPicker);

		fPicker.setText(getSampleText());
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
	 * Creates composite control and sets the default layout data.
	 */
	protected Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		return composite;
	}

	/**
	 * Creates composite control and sets the default layout data.
	 */

	protected Group createGroup(Composite parent, int numColumns) {
		Group group = new Group(parent, SWT.NULL);

		//GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		group.setLayout(layout);

		//GridData
		GridData data = new GridData(GridData.FILL);
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		group.setLayoutData(data);

		return group;
	}

	/**
	 * Utility method that creates a label instance
	 * and sets the default layout data.
	 */
	protected Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData(GridData.FILL);
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}

	protected Button createPushButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		GridData data = new GridData(GridData.FILL);
		data.horizontalAlignment = GridData.FILL;
		button.setLayoutData(data);
		return button;
	}

	/**
	 * Utility method that creates a text instance
	 * and sets the default layout data.
	 */
	protected Text createTextField(Composite parent, String text) {
		Text textfield = new Text(parent, SWT.LEFT);
		textfield.setText(text);
		GridData data = new GridData(GridData.FILL);
		data.horizontalAlignment = GridData.FILL;
		textfield.setLayoutData(data);
		return textfield;
	}

	public void dispose() {
		super.dispose();
		if (fPicker != null && !fPicker.isDisposed())
			fPicker.releasePickerResources();
	}

	protected abstract PreferenceManager getColorManager();

	public StyledTextColorPicker getPicker() {
		return fPicker;
	}

	public abstract String getSampleText();

	protected Node getSetting(String settingName) {
		if (getColorManager().getRootElement() == null)
			return null;
		NodeList settingsList = getColorManager().getRootElement().getChildNodes();
		for (int k = 0; k < settingsList.getLength(); k++) {
			Node setting = settingsList.item(k);
			if (setting.getNodeType() != Node.TEXT_NODE && setting.getNodeName().equals(settingName)) {
				return setting;
			}
		}
		// Hopefully, this only happens if the UI preferences have new settings not reflected in the user's
		// stored Document
		Node namedSetting = null;
		Document defaultDoc = getColorManager().createDefaultPreferences();
		NodeList possibleSettings = defaultDoc.getDocumentElement().getChildNodes();
		for (int i = 0; i < possibleSettings.getLength(); i++) {
			if (possibleSettings.item(i).getNodeName().equals(settingName)) {
				namedSetting = possibleSettings.item(i).cloneNode(true);
				if (getColorManager().getDocument() != null)
					namedSetting = getColorManager().getDocument().importNode(namedSetting, true);
				getColorManager().getRootElement().appendChild(namedSetting);
				break;
			}
		}
		if (namedSetting == null)
			namedSetting = getColorManager().getNode(getColorManager().getDocument(), getColorManager().getRootElementName() + "/" + settingName);//$NON-NLS-1$
		return namedSetting;
	}

	/**
	 * Initializes this preference page for the given workbench.
	 * <p>
	 * This method is called automatically as the preference page is being created
	 * and initialized. Clients must not call this method.
	 * </p>
	 *
	 * @param workbench the workbench
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * Initializes states of the controls using default values
	 * in the preference store.
	 */
	protected void performDefaults() {
		fPicker.setColorsNode(getColorManager().createDefaultPreferences().getDocumentElement().cloneNode(true));
		fPicker.refresh();
	}

	public boolean performOk() {
		// get new
		Node newColors = fPicker.getColorsNode();
		if (newColors != null) {
			// remove old
			Document persistentDocument = getColorManager().getDocument();
			while (persistentDocument.getFirstChild() != null)
				persistentDocument.removeChild(persistentDocument.getFirstChild());
			// add new
			Node persistentColors = newColors.cloneNode(true);
			persistentColors = persistentDocument.importNode(persistentColors, true);
			persistentDocument.appendChild(persistentColors);
		}

		getColorManager().save();
		return true;
	}

	protected abstract void setupPicker(StyledTextColorPicker picker);

	public void setVisible(boolean visible) {
		boolean doShrink = false;
		// limiter, for the really huge fonts
		if (visible) {
			getPicker().refresh();
			int x = Math.min(getControl().getShell().getSize().x, getControl().getDisplay().getClientArea().width * 9 / 10);
			int y = Math.min(getControl().getShell().getSize().y, getControl().getDisplay().getClientArea().height * 9 / 10);
			boolean shrinkWidth = (x != getControl().getShell().getSize().x);
			boolean shrinkHeight = (y != getControl().getShell().getSize().y);
			doShrink = shrinkWidth || shrinkHeight;
			if (doShrink) {
				// modify just the height
				if (shrinkHeight && !shrinkWidth)
					getShell().setBounds(getShell().getLocation().x, 0, getShell().getSize().x, getControl().getDisplay().getClientArea().height);
				// modify just the width
				else if (!shrinkHeight && shrinkWidth)
					getShell().setBounds(0, getShell().getLocation().y, getControl().getDisplay().getClientArea().width, getShell().getSize().y);
				// change the entire shell size to only fill the display, and move it to the origin
				else
					getShell().setBounds(0, 0, getControl().getDisplay().getClientArea().width, getControl().getDisplay().getClientArea().height);
			}
		}
		super.setVisible(visible);
		if (doShrink) {
			getControl().getShell().redraw();
			getControl().getShell().update();
		}
	}
}
