/*******************************************************************************
 * Copyright (c) 2008 Chase Technology Ltd - http://www.chasetechnology.co.uk
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Satchwell (Chase Technology Ltd) - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsl.ui.internal.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.sse.ui.internal.preferences.ui.ScrolledPageContent;
import org.eclipse.wst.xsl.core.ValidationPreferences;
import org.eclipse.wst.xsl.ui.internal.Messages;

/**
 * Preference page for XSL validator preferences.
 * 
 * @author Doug Satchwell
 * @deprecated  This has been replaced by XSLValidationPreferencePage
 */
@Deprecated
public class ValidationPreferencePage extends AbstractXSLPreferencePage
{
	private static final String[] ERRORS = new String[] { Messages.ErrorLevelText, Messages.WarningLevelText, Messages.IgnoreLevelText };
	private static final int[] ERROR_VALUES = new int[] { IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO };
	private static final Map<Integer, Integer> ERROR_MAP = new HashMap<Integer, Integer>();
	private Text maxErrorsText;
	private Map<String, Combo> combos = new HashMap<String, Combo>();

	static
	{
		ERROR_MAP.put(IMarker.SEVERITY_ERROR, 0);
		ERROR_MAP.put(IMarker.SEVERITY_WARNING, 1);
		ERROR_MAP.put(IMarker.SEVERITY_INFO, 2);
	}

	/**
	 * Create a new instance of this.
	 */
	public ValidationPreferencePage()
	{
		super();
		// only used when page is shown programatically
		setTitle(Messages.XSLValidationPreferencePageTitle);
		setDescription(Messages.XSLValidationPreferencePageDescription);
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

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.XML_PREFWEBX_FILES_HELPID);

		createLabel(body, Messages.XSLValidationPreferenceMaxErrorsLabel);
		maxErrorsText = createTextField(body);
		maxErrorsText.addModifyListener(this);

		Composite twistie;
		
		twistie = createTwistie(body,Messages.XSLValidationPreferenceImportsIncludesLabel,2);		
		createCombo(twistie, Messages.XSLValidationPreferenceUnresolveImportIncludeLabel, ValidationPreferences.MISSING_INCLUDE);
		createCombo(twistie, Messages.XSLValidationPreferenceCircularReferencesLabel, ValidationPreferences.CIRCULAR_REF);

		twistie = createTwistie(body, Messages.XSLValidationPreferenceNamedTemplatesLabel, 2);		
		createCombo(twistie, Messages.XSLValidationPreferenceTemplateConflictsLabel, ValidationPreferences.TEMPLATE_CONFLICT);
		createCombo(twistie, Messages.XSLValidationPreferenceDuplicateParameterLabel, ValidationPreferences.DUPLICATE_PARAMETER);
		createCombo(twistie, Messages.XSLValidationPreferenceMissingParameterAttributeLabel, ValidationPreferences.NAME_ATTRIBUTE_MISSING);
		createCombo(twistie, Messages.XSLValidationPreferenceParameterEmptyAttributeLabel, ValidationPreferences.NAME_ATTRIBUTE_EMPTY);
		
		twistie = createTwistie(body, Messages.XSLValidationPreferenceCallTemplatesLabel,2);		
		createCombo(twistie, Messages.XSLValidationPreferenceUnresolvedTemplatesLabel, ValidationPreferences.CALL_TEMPLATES);
		createCombo(twistie, Messages.XSLValidationPreferenceMissingParamtersLabel, ValidationPreferences.MISSING_PARAM);
		createCombo(twistie, Messages.XSLValidationPreferenceParamtersWithoutValueLabel, ValidationPreferences.EMPTY_PARAM);

		twistie = createTwistie(body, Messages.XSLValidationPreferenceXPathLabel, 2);
		createCombo(twistie, Messages.XSLValidationPreferenceXPathSyntaxLabel, ValidationPreferences.XPATHS);

		return parent;
	}

	@Override
	protected Combo createCombo(Composite parent, String label, String prefKey)
	{
		Combo combo = super.createCombo(parent, label, prefKey);
		combo.setItems(ERRORS);
		combos.put(prefKey, combo);
		return combo;
	}

	@Override
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
	}

	@Override
	protected void validateValues()
	{
		String errorMessage = null;
		try
		{
			int maxErrors = Integer.parseInt(maxErrorsText.getText());
			if (maxErrors < 0)
				errorMessage = Messages.XSLValidationPreferenceMaxErrorsMsgError;
		}
		catch (NumberFormatException e)
		{
			errorMessage = Messages.XSLValidationPreferenceMaxErrorsMsgError;
		}
		setErrorMessage(errorMessage);
		setValid(errorMessage == null);
	}

	@Override
	protected void performDefaults()
	{
		int maxErrors = getModelPreferences().getDefaultInt(ValidationPreferences.MAX_ERRORS);
		maxErrorsText.setText(String.valueOf(maxErrors));
		for (Map.Entry<String, Combo> entry : combos.entrySet())
		{
			int def = getModelPreferences().getDefaultInt(entry.getKey());
			entry.getValue().select(ERROR_MAP.get(def));
		}
		super.performDefaults();
	}
}
