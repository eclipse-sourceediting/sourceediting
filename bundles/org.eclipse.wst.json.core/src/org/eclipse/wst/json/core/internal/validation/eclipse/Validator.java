/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.eclipse.Validator
 *                                           modified in order to process JSON Objects.                                          
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.validation.eclipse;

import java.io.InputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.internal.validation.JSONNestedValidatorContext;
import org.eclipse.wst.json.core.internal.validation.JSONValidationConfiguration;
import org.eclipse.wst.json.core.internal.validation.JSONValidationReport;
import org.eclipse.wst.json.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.json.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.json.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.json.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

public class Validator extends AbstractNestedValidator {

	private static final String JSON_VALIDATOR_CONTEXT = "org.eclipse.wst.json.core.validatorContext"; //$NON-NLS-1$
	protected int indicateNoGrammar = 0;
	private IScopeContext[] fPreferenceScopes = null;

	@Override
	protected void setupValidation(NestedValidatorContext context) {
		super.setupValidation(context);
		fPreferenceScopes = createPreferenceScopes(context);
		indicateNoGrammar = Platform.getPreferencesService().getInt(
				JSONCorePlugin.getDefault().getBundle().getSymbolicName(),
				JSONCorePreferenceNames.INDICATE_NO_GRAMMAR, 0,
				fPreferenceScopes);
	}

	protected IScopeContext[] createPreferenceScopes(
			NestedValidatorContext context) {
		if (context != null) {
			final IProject project = context.getProject();
			if (project != null && project.isAccessible()) {
				final ProjectScope projectScope = new ProjectScope(project);
				if (projectScope.getNode(
						JSONCorePlugin.getDefault().getBundle()
								.getSymbolicName()).getBoolean(
						JSONCorePreferenceNames.USE_PROJECT_SETTINGS, false))
					return new IScopeContext[] { projectScope,
							new InstanceScope(), new DefaultScope() };
			}
		}
		return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context) {
		return validate(uri, inputstream, context, null);
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context, ValidationResult result) {
		JSONValidator validator = JSONValidator.getInstance();

		JSONValidationConfiguration configuration = new JSONValidationConfiguration();
		// try {
		// // Preferences pluginPreferences =
		// // JSONCorePlugin.getDefault().getPluginPreferences();
		// configuration.setFeature(
		// JSONValidationConfiguration.INDICATE_NO_GRAMMAR,
		// indicateNoGrammar);
		// final IPreferencesService preferencesService = Platform
		// .getPreferencesService();
		// configuration
		// .setFeature(
		// JSONValidationConfiguration.INDICATE_NO_DOCUMENT_ELEMENT,
		// preferencesService
		// .getInt(JSONCorePlugin.getDefault()
		// .getBundle().getSymbolicName(),
		// JSONCorePreferenceNames.INDICATE_NO_DOCUMENT_ELEMENT,
		// -1, fPreferenceScopes));
		// configuration.setFeature(JSONValidationConfiguration.USE_XINCLUDE,
		// preferencesService.getBoolean(JSONCorePlugin.getDefault()
		// .getBundle().getSymbolicName(),
		// JSONCorePreferenceNames.USE_XINCLUDE, false,
		// fPreferenceScopes));
		// configuration
		// .setFeature(
		// JSONValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS,
		// preferencesService
		// .getBoolean(
		// JSONCorePlugin.getDefault()
		// .getBundle()
		// .getSymbolicName(),
		// JSONCorePreferenceNames.HONOUR_ALL_SCHEMA_LOCATIONS,
		// true, fPreferenceScopes));
		// } catch (Exception e) {
		// // TODO: Unable to set the preference. Log this problem.
		// }

		JSONValidationReport valreport = validator.validate(uri, inputstream,
				configuration, result, context);

		return valreport;
	}

	/**
	 * Store additional information in the message parameters. For JSON
	 * validation there are three additional pieces of information to store:
	 * param[0] = the column number of the error param[1] = the 'squiggle
	 * selection strategy' for which DOM part to squiggle param[2] = the name or
	 * value of what is to be squiggled
	 * 
	 * @see org.eclipse.wst.json.core.internal.validation.core.AbstractNestedValidator#addInfoToMessage(org.eclipse.wst.json.core.internal.validation.core.ValidationMessage,
	 *      org.eclipse.wst.validation.internal.provisional.core.IMessage)
	 */
	@Override
	protected void addInfoToMessage(ValidationMessage validationMessage,
			IMessage message) {
		String key = validationMessage.getKey();
		if (key != null) {
			JSONMessageInfoHelper messageInfoHelper = new JSONMessageInfoHelper();
			String[] messageInfo = messageInfoHelper.createMessageInfo(key,
					validationMessage.getMessageArguments());

			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(
					validationMessage.getColumnNumber()));
			/*message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE,
					messageInfo[0]);
			message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE,
					messageInfo[1]);
					*/
		}
	}

	/**
	 * Get the nested validation context.
	 * 
	 * @param state
	 *            the validation state.
	 * @param create
	 *            when true, a new context will be created if one is not found
	 * @return the nested validation context.
	 */
	@Override
	protected NestedValidatorContext getNestedContext(ValidationState state,
			boolean create) {
		NestedValidatorContext context = null;
		Object o = state.get(JSON_VALIDATOR_CONTEXT);
		if (o instanceof JSONNestedValidatorContext)
			context = (JSONNestedValidatorContext) o;
		else if (create) {
			context = new JSONNestedValidatorContext();
		}
		return context;
	}

	@Override
	public void validationStarting(IProject project, ValidationState state,
			IProgressMonitor monitor) {
		if (project != null) {
			NestedValidatorContext context = getNestedContext(state, false);
			if (context == null) {
				context = getNestedContext(state, true);
				if (context != null)
					context.setProject(project);
				setupValidation(context);
				state.put(JSON_VALIDATOR_CONTEXT, context);
			}
			super.validationStarting(project, state, monitor);
		}
	}

	@Override
	public void validationFinishing(IProject project, ValidationState state,
			IProgressMonitor monitor) {
		if (project != null) {
			super.validationFinishing(project, state, monitor);
			NestedValidatorContext context = getNestedContext(state, false);
			if (context != null) {
				teardownValidation(context);
				state.put(JSON_VALIDATOR_CONTEXT, null);
			}
		}
	}

}
