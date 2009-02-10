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

/**
 * Preference page for XSL validator preferences.
 * 
 * @author Doug Satchwell
 * @deprecated  This has been replaced by XSLValidationPreferencePage
 */
@Deprecated
public class ValidationPreferencePage extends AbstractXSLPreferencePage
{
	private static final String[] ERRORS = new String[] { "Error", "Warning", "Ignore" };
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
		setTitle("XSL Validation");
		setDescription("Configure validation preferences");
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

		createLabel(body, "Maximum number of errors reported per stylesheet:");
		maxErrorsText = createTextField(body);
		maxErrorsText.addModifyListener(this);

		Composite twistie;
		
		twistie = createTwistie(body,"Imports and Includes",2);		
		createCombo(twistie, "Unresolved include/import:", ValidationPreferences.MISSING_INCLUDE);
		createCombo(twistie, "Circular references:", ValidationPreferences.CIRCULAR_REF);

		twistie = createTwistie(body,"Named Templates",2);		
		createCombo(twistie, "Template name conflicts:", ValidationPreferences.TEMPLATE_CONFLICT);
		createCombo(twistie, "Duplicate parameterw:", ValidationPreferences.DUPLICATE_PARAMETER);
		createCombo(twistie, "Parameter without name attribute:", ValidationPreferences.NAME_ATTRIBUTE_MISSING);
		createCombo(twistie, "Parameter with empty name attribute:", ValidationPreferences.NAME_ATTRIBUTE_EMPTY);
		
		twistie = createTwistie(body,"Template Calls",2);		
		createCombo(twistie, "Unresolved templates:", ValidationPreferences.CALL_TEMPLATES);
		createCombo(twistie, "Missing parameters:", ValidationPreferences.MISSING_PARAM);
		createCombo(twistie, "Parameters without value:", ValidationPreferences.EMPTY_PARAM);

		twistie = createTwistie(body,"XPath Problems",2);
		createCombo(twistie, "Incorrect XPath syntax:", ValidationPreferences.XPATHS);

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
				errorMessage = "Max errors must be a positive integer";
		}
		catch (NumberFormatException e)
		{
			errorMessage = "Max errors must be a positive integer";
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
