package org.eclipse.wst.xsl.ui.internal.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Preferences;
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
import org.eclipse.wst.xsl.core.ValidationPreferences;
import org.eclipse.wst.xsl.core.internal.XSLCorePlugin;
import org.eclipse.wst.xsl.ui.internal.XSLUIPlugin;

public class XSLValidationPreferencePage extends AbstractValidationSettingsPage implements ModifyListener {

	private static final String[] ERRORS = new String[] { "Error", "Warning", "Ignore" };
	private static final int[] ERROR_VALUES = new int[] { IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO };
	private static final Map<Integer, Integer> ERROR_MAP = new HashMap<Integer, Integer>();
	private Text maxErrorsText;
	private Map<String, Combo> combos = new HashMap<String, Combo>();
	private List<ExpandableComposite> Expandables = new ArrayList<ExpandableComposite>();
	private static final String SETTINGS_SECTION_NAME = "XSLValidationSeverities";//$NON-NLS-1$



	static
	{
		ERROR_MAP.put(IMarker.SEVERITY_ERROR, 0);
		ERROR_MAP.put(IMarker.SEVERITY_WARNING, 1);
		ERROR_MAP.put(IMarker.SEVERITY_INFO, 2);
	}
	
	@Override
	protected Composite createCommonContents(Composite parent)
	{
		GridLayout layout= new GridLayout();
		layout.numColumns= 2;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		
		final ScrolledPageContent pageContent = new ScrolledPageContent(parent);
		pageContent.setLayoutData(new GridData(GridData.FILL_BOTH));
		pageContent.setExpandHorizontal(true);
		pageContent.setExpandVertical(true);
		
		Composite body = pageContent.getBody();
		body.setLayout(layout);

		GridData gd= new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1);
		gd.horizontalIndent= 0;

		Label description = new Label(body, SWT.NONE);
		description.setText("Select the serverity level for the following validation problems:");
		description.setFont(pageContent.getFont());
		description.setLayoutData(gd);
		

		createLabel(body, "Maximum number of errors reported per stylesheet:");
		maxErrorsText = createTextField(body);
		maxErrorsText.addModifyListener(this);

		ExpandableComposite twistie;
		
		int columns = 3;
		twistie = createTwistie(body,"Imports and Includes",columns);
		Composite inner = createInnerComposite(parent, twistie, columns);
		
		String label = "Unresolved include/import:";
		createCombo(inner, label, ValidationPreferences.MISSING_INCLUDE);

		label = "Unresolved include/import:";
		inner = createInnerComposite(parent, twistie, columns);
		createCombo(inner, label, ValidationPreferences.MISSING_INCLUDE);
		createCombo(inner, "Circular references:", ValidationPreferences.CIRCULAR_REF);

		twistie = createTwistie(body,"Named Templates",columns);
		inner = createInnerComposite(parent, twistie, columns);
		
		createCombo(inner, "Template name conflicts:", ValidationPreferences.TEMPLATE_CONFLICT);
		createCombo(inner, "Duplicate parameterw:", ValidationPreferences.DUPLICATE_PARAMETER);
		createCombo(inner, "Parameter without name attribute:", ValidationPreferences.NAME_ATTRIBUTE_MISSING);
		createCombo(inner, "Parameter with empty name attribute:", ValidationPreferences.NAME_ATTRIBUTE_EMPTY);
		
		twistie = createTwistie(body,"Template Calls",columns);
		inner = createInnerComposite(parent, twistie, columns);
		
		createCombo(inner, "Unresolved templates:", ValidationPreferences.CALL_TEMPLATES);
		createCombo(inner, "Missing parameters:", ValidationPreferences.MISSING_PARAM);
		createCombo(inner, "Parameters without value:", ValidationPreferences.EMPTY_PARAM);

		twistie = createTwistie(body,"XPath Problems",columns);
		inner = createInnerComposite(parent, twistie, columns);
		createCombo(inner, "Incorrect XPath syntax:", ValidationPreferences.XPATHS);

		loadPreferences();
		restoreSectionExpansionStates(getDialogSettings().getSection(SETTINGS_SECTION_NAME));
		
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
		return addComboBox(parent, label, key, ERROR_VALUES, ERRORS, 0);
	}
	
	protected Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);


		//GridData
		GridData data = new GridData(GridData.FILL);
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);

		return label;
	}
	
	protected Text createTextField(Composite parent) {
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

		//GridData
		GridData data = new GridData();
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		text.setLayoutData(data);

		return text;
	}
	
	protected ExpandableComposite createTwistie(Composite parent, String label, int nColumns) {
		ExpandableComposite excomposite= new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, nColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});
		Expandables.add(excomposite);
		makeScrollableCompositeAware(excomposite);
		return excomposite;
	}
	
	private void makeScrollableCompositeAware(Control control)
	{
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);
		if (parentScrolledComposite != null)
		{
			parentScrolledComposite.adaptChild(control);
		}
	}
	
	
	@Override
	protected String getPreferenceNodeQualifier() {
		return XSLCorePlugin.getDefault().getBundle().getSymbolicName();
	}

	@Override
	protected String getPreferencePageID() {
		return "org.eclipse.wst.xsl.ui.preferences.Validation";
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
		return "org.eclipse.wst.xsl.ui.propertyPage.project.validation";
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
	protected void storeValues()
	{
		int maxErrors = Integer.parseInt(maxErrorsText.getText());
		getModelPreferences().setValue(ValidationPreferences.MAX_ERRORS, maxErrors);
		for (Map.Entry<String, Combo> entry : combos.entrySet())
		{
			int index = entry.getValue().getSelectionIndex();
			getModelPreferences().setValue(entry.getKey(), ERROR_VALUES[index]);
		}
		super.storeValues();
	}
	
	protected Preferences getModelPreferences()
	{
		return XSLCorePlugin.getDefault().getPluginPreferences();
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
	
	protected void initializeValues()
	{
		int maxErrors = getModelPreferences().getInt(ValidationPreferences.MAX_ERRORS);
		maxErrorsText.setText(String.valueOf(maxErrors));
		for (Map.Entry<String, Combo> entry : combos.entrySet())
		{
			int val = getModelPreferences().getInt(entry.getKey());
			entry.getValue().select(ERROR_MAP.get(val));
		}
	}
	
	protected void validateValues()
	{
		String errorMessage = null;
		try
		{
			int maxErrors = Integer.parseInt(maxErrorsText.getText());
			if (maxErrors < 0)
				errorMessage = "Max errors must be a positive integer";
		}
		catch (NumberFormatException e)
		{
			errorMessage = "Max errors must be a positive integer";
		}
		setErrorMessage(errorMessage);
		setValid(errorMessage == null);
	}	
	
	protected void enableValues() {
	}	
	
	protected void performDefaults() {
		resetSeverities();
		super.performDefaults();
	}	
}
