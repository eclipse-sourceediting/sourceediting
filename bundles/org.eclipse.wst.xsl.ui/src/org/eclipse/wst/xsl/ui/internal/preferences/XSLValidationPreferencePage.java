/*******************************************************************************
 *Copyright (c) 2008, 2009 Standards for Technology in Automotive Retail and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *Contributors:
 *    David Carver - bug 230072 - initial API and implementation based on code from
 *                                Doug Satchwell, Jesper Moeller, and the
 *                                HTML Validation PreferencePage.
 *    David Carver - bug 297714 - Values not being loaded from preferences.
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.wst.xsl.core.ValidationPreferences;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.ui.internal.Messages;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

public class XSLValidationPreferencePage extends AbstractValidationSettingsPage
		implements ModifyListener {

	private static final String XSL_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID = "org.eclipse.wst.xsl.ui.propertyPage.project.validation"; //$NON-NLS-1$
	private static final String XSL_UI_PREFERENCES_VALIDATION_ID = "org.eclipse.wst.xsl.ui.preferences.Validation"; //$NON-NLS-1$
	private static final String[] ERRORS = new String[] {
			Messages.ErrorLevelText, Messages.WarningLevelText,
			Messages.IgnoreLevelText };
	private static final int[] ERROR_VALUES = new int[] {
			IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING,
			IMarker.SEVERITY_INFO };
	private static final Map<Integer, Integer> ERROR_MAP = new ConcurrentHashMap<Integer, Integer>();
	private Text maxErrorsText;
	private Map<String, Combo> combos = new ConcurrentHashMap<String, Combo>();
	private List<ExpandableComposite> Expandables = new CopyOnWriteArrayList<ExpandableComposite>();
	private static final String SETTINGS_SECTION_NAME = "XSLValidationSeverities";//$NON-NLS-1$
	private PixelConverter fPixelConverter;

	static {
		ERROR_MAP.put(IMarker.SEVERITY_ERROR, 0);
		ERROR_MAP.put(IMarker.SEVERITY_WARNING, 1);
		ERROR_MAP.put(IMarker.SEVERITY_INFO, 2);
	}

	@Override
	protected Control createCommonContents(Composite parent) {
		final Composite page = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		page.setLayout(layout);

		fPixelConverter = new PixelConverter(parent);

		final Composite content = createValidationSection(page);

		loadPreferences();
		restoreSectionExpansionStates(getDialogSettings().getSection(
				SETTINGS_SECTION_NAME));

		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		gridData.heightHint = fPixelConverter.convertHeightInCharsToPixels(20);
		content.setLayoutData(gridData);

		return page;
	}

	protected Composite createValidationSection(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		final ScrolledPageContent pageContent = new ScrolledPageContent(parent);
		pageContent.setLayoutData(new GridData(GridData.FILL_BOTH));
		pageContent.setExpandHorizontal(true);
		pageContent.setExpandVertical(true);

		Composite body = pageContent.getBody();
		body.setLayout(layout);

		GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false,
				2, 1);
		gd.horizontalIndent = 0;

		createLabel(body, Messages.XSLValidationPreferenceMaxErrorsLabel);
		maxErrorsText = createTextField(body);
		maxErrorsText.addModifyListener(this);

		Label description = new Label(body, SWT.NONE);
		description.setText(Messages.XSLValidationPageSeverityLevel);
		description.setFont(pageContent.getFont());
		description.setLayoutData(gd);

		ExpandableComposite twistie;

		int columns = 3;
		twistie = createTwistie(body,
				Messages.XSLValidationPreferenceImportsIncludesLabel, columns);
		Composite inner = createInnerComposite(parent, twistie, columns);

		String label = Messages.XSLValidationPreferenceUnresolveImportIncludeLabel;
		createCombo(inner, label, ValidationPreferences.MISSING_INCLUDE);

		inner = createInnerComposite(parent, twistie, columns);
		createCombo(inner, label, ValidationPreferences.MISSING_INCLUDE);
		createCombo(inner,
				Messages.XSLValidationPreferenceCircularReferencesLabel,
				ValidationPreferences.CIRCULAR_REF);

		twistie = createTwistie(body,
				Messages.XSLValidationPreferenceNamedTemplatesLabel, columns);
		inner = createInnerComposite(parent, twistie, columns);

		createCombo(inner,
				Messages.XSLValidationPreferenceTemplateConflictsLabel,
				ValidationPreferences.TEMPLATE_CONFLICT);
		createCombo(inner,
				Messages.XSLValidationPreferenceDuplicateParameterLabel,
				ValidationPreferences.DUPLICATE_PARAMETER);
		createCombo(inner,
				Messages.XSLValidationPreferenceParamtersWithoutValueLabel,
				ValidationPreferences.NAME_ATTRIBUTE_MISSING);
		createCombo(inner,
				Messages.XSLValidationPreferenceMissingParameterAttributeLabel,
				ValidationPreferences.NAME_ATTRIBUTE_EMPTY);

		twistie = createTwistie(body,
				Messages.XSLValidationPreferenceCallTemplatesLabel, columns);
		inner = createInnerComposite(parent, twistie, columns);

		createCombo(inner,
				Messages.XSLValidationPreferenceUnresolvedTemplatesLabel,
				ValidationPreferences.CALL_TEMPLATES);
		createCombo(inner,
				Messages.XSLValidationPreferenceMissingParamtersLabel,
				ValidationPreferences.MISSING_PARAM);
		createCombo(inner,
				Messages.XSLValidationPreferenceMissingParameterAttributeLabel,
				ValidationPreferences.EMPTY_PARAM);

		twistie = createTwistie(body,
				Messages.XSLValidationPreferenceXPathLabel, columns);
		inner = createInnerComposite(parent, twistie, columns);
		createCombo(inner, Messages.XSLValidationPreferenceXPathSyntaxLabel,
				ValidationPreferences.XPATHS);

		return parent;
	}

	private Composite createInnerComposite(Composite parent,
			ExpandableComposite twistie, int columns) {
		Composite inner = new Composite(twistie, SWT.NONE);
		inner.setFont(parent.getFont());
		inner.setLayout(new GridLayout(columns, false));
		twistie.setClient(inner);
		return inner;
	}

	protected Combo createCombo(Composite parent, String label, String key) {
		Combo combo = addComboBox(parent, label, key, ERROR_VALUES, ERRORS, 0);
		combos.put(key, combo);
		return combo;
	}

	protected Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);

		// GridData
		GridData data = new GridData(GridData.FILL);
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);

		return label;
	}

	protected Text createTextField(Composite parent) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

		// GridData
		GridData data = new GridData();
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);

		return text;
	}

	@Override
	protected ExpandableComposite createTwistie(Composite parent, String label,
			int nColumns) {
		ExpandableComposite excomposite = new ExpandableComposite(parent,
				SWT.NONE, ExpandableComposite.TWISTIE
						| ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(
				JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, nColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});
		Expandables.add(excomposite);
		makeScrollableCompositeAware(excomposite);
		return excomposite;
	}

	private void makeScrollableCompositeAware(Control control) {
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);
		if (parentScrolledComposite != null) {
			parentScrolledComposite.adaptChild(control);
		}
	}

	@Override
	public void dispose() {
		storeSectionExpansionStates(getDialogSettings().addNewSection(
				SETTINGS_SECTION_NAME));
		super.dispose();
	}

	protected String getQualifier() {
		return XSLCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	@Override
	protected String getPreferenceNodeQualifier() {
		return XSLCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	@Override
	protected String getPreferencePageID() {
		return XSL_UI_PREFERENCES_VALIDATION_ID;
	}

	@Override
	protected String getProjectSettingsKey() {
		return XSLCorePlugin.USE_PROJECT_SETTINGS;
	}

	protected IDialogSettings getDialogSettings() {
		return XSLUIPlugin.getDefault().getDialogSettings();
	}

	@Override
	protected String getPropertyPageID() {
		return XSL_UI_PROPERTY_PAGE_PROJECT_VALIDATION_ID;
	}

	public void init(IWorkbench workbench) {

	}

	public void modifyText(ModifyEvent e) {
		// If we are called too early, i.e. before the controls are created
		// then return
		// to avoid null pointer exceptions
		if (e.widget != null && e.widget.isDisposed())
			return;

		validateValues();
		enableValues();
	}

	@Override
	protected void storeValues() {
		super.storeValues();
		int maxErrors = Integer.parseInt(maxErrorsText.getText());
		Preferences prefs = getModelPreferences();
		
		prefs.putInt(ValidationPreferences.MAX_ERRORS, maxErrors);
		
		try {
			prefs.flush();
		} catch (BackingStoreException ex) {
			XSLUIPlugin.log(ex);
		}
	}

	protected Preferences getModelPreferences() {
		IEclipsePreferences prefs = Platform.getPreferencesService().getRootNode();
		IProject project = getProject();
		if (project != null) {
			return prefs.node(ProjectScope.SCOPE).node(getPreferenceNodeQualifier());
		}
		Preferences instanceScope = prefs.node(InstanceScope.SCOPE).node(getPreferenceNodeQualifier());
		if (instanceScope != null) {
			return instanceScope;
		}
		return prefs.node(DefaultScope.SCOPE).node(getPreferenceNodeQualifier());
	}

	protected boolean loadPreferences() {
		BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
			public void run() {
				initializeValues();
				validateValues();
				enableValues();
			}
		});
		return true;
	}

	protected void initializeValues() {
		Preferences prefs = getModelPreferences();
		int maxErrors = prefs.getInt(ValidationPreferences.MAX_ERRORS, 100);
		maxErrorsText.setText(String.valueOf(maxErrors));
		for (Map.Entry<String, Combo> entry : combos.entrySet()) {
			int val = prefs.getInt(entry.getKey(), IMarker.SEVERITY_WARNING);
			if (val < 0) {
				val = IMarker.SEVERITY_WARNING;
			}
			entry.getValue().select(ERROR_MAP.get(val));
		}
	}

	protected void validateValues() {
		String errorMessage = null;
		try {
			int maxErrors = Integer.parseInt(maxErrorsText.getText());
			if (maxErrors < 0)
				errorMessage = Messages.XSLValidationPreferenceMaxErrorsMsgError;
		} catch (NumberFormatException e) {
			errorMessage = Messages.XSLValidationPreferenceMaxErrorsMsgError;
		}
		setErrorMessage(errorMessage);
		setValid(errorMessage == null);
	}

	protected void enableValues() {
	}

	@Override
	protected void performDefaults() {
		resetSeverities();
		super.performDefaults();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xsl.ui.internal.preferences.AbstractValidationSettingsPage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		storeValues();
		return result;
	}
}
