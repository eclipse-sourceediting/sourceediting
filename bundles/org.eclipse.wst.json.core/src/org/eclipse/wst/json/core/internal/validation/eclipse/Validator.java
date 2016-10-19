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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.json.impl.schema.JSONSchemaNode;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonArray;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonObject.Member;
import org.eclipse.json.provisonnal.com.eclipsesource.json.JsonValue;
import org.eclipse.json.schema.IJSONSchemaDocument;
import org.eclipse.json.schema.IJSONSchemaNode;
import org.eclipse.json.schema.IJSONSchemaProperty;
import org.eclipse.json.schema.JSONSchemaType;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.document.IJSONArray;
import org.eclipse.wst.json.core.document.IJSONDocument;
import org.eclipse.wst.json.core.document.IJSONModel;
import org.eclipse.wst.json.core.document.IJSONNode;
import org.eclipse.wst.json.core.document.IJSONObject;
import org.eclipse.wst.json.core.document.IJSONPair;
import org.eclipse.wst.json.core.document.IJSONStringValue;
import org.eclipse.wst.json.core.document.IJSONValue;
import org.eclipse.wst.json.core.internal.schema.SchemaProcessorRegistryReader;
import org.eclipse.wst.json.core.internal.validation.JSONNestedValidatorContext;
import org.eclipse.wst.json.core.internal.validation.JSONValidationConfiguration;
import org.eclipse.wst.json.core.internal.validation.JSONValidationInfo;
import org.eclipse.wst.json.core.internal.validation.JSONValidationReport;
import org.eclipse.wst.json.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.json.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.json.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.json.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.core.util.JSONUtil;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;

public class Validator extends AbstractNestedValidator {

	private static final String CLOSE_BRACKET = "]"; //$NON-NLS-1$
	private static final String OPEN_BRACKET = "["; //$NON-NLS-1$
	private static final String COMMA = ","; //$NON-NLS-1$
	private static final String JSON_VALIDATOR_CONTEXT = "org.eclipse.wst.json.core.validatorContext"; //$NON-NLS-1$
	protected int indicateNoGrammar = 0;
	private IScopeContext[] fPreferenceScopes = null;

	@Override
	protected void setupValidation(NestedValidatorContext context) {
		super.setupValidation(context);
		fPreferenceScopes = createPreferenceScopes(context);
		indicateNoGrammar = Platform.getPreferencesService().getInt(
				JSONCorePlugin.getDefault().getBundle().getSymbolicName(), JSONCorePreferenceNames.INDICATE_NO_GRAMMAR,
				0, fPreferenceScopes);
	}

	protected IScopeContext[] createPreferenceScopes(NestedValidatorContext context) {
		if (context != null) {
			final IProject project = context.getProject();
			if (project != null && project.isAccessible()) {
				final ProjectScope projectScope = new ProjectScope(project);
				if (projectScope.getNode(JSONCorePlugin.getDefault().getBundle().getSymbolicName())
						.getBoolean(JSONCorePreferenceNames.USE_PROJECT_SETTINGS, false))
					return new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
			}
		}
		return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context) {
		return validate(uri, inputstream, context, null);
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context,
			ValidationResult result) {
		JSONValidator validator = JSONValidator.getInstance();
		JSONValidationConfiguration configuration = new JSONValidationConfiguration();
		JSONValidationReport valreport = validator.validate(uri, inputstream, configuration, result, context);
		String prefs = JSONCorePlugin.getDefault().getBundle().getSymbolicName();
		IEclipsePreferences modelPreferences = InstanceScope.INSTANCE.getNode(prefs);
		boolean validateSchema = modelPreferences.getBoolean(JSONCorePreferenceNames.SCHEMA_VALIDATION, false);
		if (validateSchema) {
			IJSONModel model = null;
			try {
				IStructuredModel temp = getModel(uri);
				if (!(temp instanceof IJSONModel)) {
					return valreport;
				}
				model = (IJSONModel) temp;
				IJSONSchemaDocument schemaDocument = SchemaProcessorRegistryReader.getInstance()
						.getSchemaDocument(model);
				if (schemaDocument != null) {
					JSONValidationInfo valinfo = null;
					if (valreport instanceof JSONValidationInfo) {
						valinfo = (JSONValidationInfo) valreport;
					} else {
						valinfo = new JSONValidationInfo(uri);
					}
					validate(model, schemaDocument, valinfo);
					// ValidationMessage[] messages =
					// valreport.getValidationMessages();
					return valreport;
				}
			} catch (IOException e) {
				logWarning(e);
				return valreport;
			} finally {
				if (model != null) {
					model.releaseFromRead();
				}
			}
		}
		return valreport;
	}

	private void validate(IJSONModel model, IJSONSchemaProperty schemaProperty, JSONValidationInfo valinfo) {
		IJSONDocument document = model.getDocument();
		IJSONNode node = document.getFirstChild();
		while (node != null) {
			validate(node, schemaProperty, valinfo);
			node = node.getNextSibling();
		}
	}

	private void validate(IJSONNode node, IJSONSchemaProperty schemaProperty, JSONValidationInfo valinfo) {
		if (node == null || schemaProperty == null) {
			return;
		}
		JsonObject schema = schemaProperty.getJsonObject();
		validate(node, schema, valinfo);
		IJSONNode child = node.getFirstChild();
		while (child != null) {
			IJSONSchemaProperty property = schemaProperty.getSchemaDocument().getProperty(child.getPath());
			validate(child, property, valinfo);
			if (child instanceof IJSONPair) {
				IJSONValue value = ((IJSONPair) child).getValue();
				if (value instanceof IJSONObject) {
					IJSONSchemaProperty prop = schemaProperty.getSchemaDocument().getProperty(value.getPath());
					validate(value, prop, valinfo);
				}
			}
			child = child.getNextSibling();
		}
	}

	private void validate(IJSONNode node, JsonObject schema, JSONValidationInfo valinfo) {
		Iterator<Member> members = schema.iterator();
		while (members.hasNext()) {
			Member member = members.next();
			validate(node, schema, member, valinfo);
		}
	}

	private void validate(IJSONNode node, JsonObject schema, Member member, JSONValidationInfo valinfo) {
		if (IJSONSchemaNode.ALL_OF.equals(member.getName()) && member.getValue() instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) member.getValue();
			Iterator<JsonValue> iter = jsonArray.iterator();
			while (iter.hasNext()) {
				JsonValue value = iter.next();
				if (value instanceof JsonObject) {
					validate(node, (JsonObject) value, valinfo);
				}
			}
		}
		if (IJSONSchemaNode.ANY_OF.equals(member.getName()) && member.getValue() instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) member.getValue();
			Iterator<JsonValue> iter = jsonArray.iterator();
			while (iter.hasNext()) {
				JsonValue value = iter.next();
				if (value instanceof JsonObject) {
					JSONValidationInfo info = new JSONValidationInfo("");
					validate(node, (JsonObject) value, info);
					if (info.getValidationMessages() == null || info.getValidationMessages().length == 0) {
						return;
					}
				}
			}
			int offset = node.getStartOffset();
			int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
			valinfo.addMessage("Matches a schema that is not allowed", line, 0, offset == 0 ? 1 : offset);
		}
		if (IJSONSchemaNode.ONE_OF.equals(member.getName()) && member.getValue() instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) member.getValue();
			Iterator<JsonValue> iter = jsonArray.iterator();
			int count = 0;
			while (iter.hasNext()) {
				JsonValue value = iter.next();
				if (value instanceof JsonObject) {
					JSONValidationInfo info = new JSONValidationInfo("");
					validate(node, (JsonObject) value, info);
					if (info.getValidationMessages() == null || info.getValidationMessages().length == 0) {
						count = count + 1;
					}
				}
			}
			if (count != 1) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("Matches a schema that is not allowed", line, 0, offset == 0 ? 1 : offset);
			}
		}
		if (IJSONSchemaNode.NOT.equals(member.getName()) && member.getValue() instanceof JsonObject) {
			JsonObject json = (JsonObject) member.getValue();
			JSONValidationInfo info = new JSONValidationInfo("");
			validate(node, json, info);
			if (info.getValidationMessages() == null || info.getValidationMessages().length == 0) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("Matches a schema that is not allowed", line, 0, offset == 0 ? 1 : offset);
			}
		}
		if (IJSONSchemaNode.TYPE.equals(member.getName())) {
			validateType(node, member, valinfo);
		}
		if (IJSONSchemaNode.ENUM.equals(member.getName())) {
			validateEnum(node, schema, valinfo);
		}
		if (node.getNodeType() == IJSONNode.OBJECT_NODE) {
			if (IJSONSchemaNode.REQUIRED.equals(member.getName())) {
				validateRequired(node, schema, valinfo);
			}
			if (IJSONSchemaNode.MAX_PROPERTIES.equals(member.getName())) {
				validateMaxProperties(node, schema, valinfo);
			}
			if (IJSONSchemaNode.MIN_PROPERTIES.equals(member.getName())) {
				validateMinProperties(node, schema, valinfo);
			}
			if (IJSONSchemaNode.ADDITIONAL_PROPERTIES.equals(member.getName())) {
				validateAdditionalProperties(node, schema, member.getValue(), valinfo);
			}
		}
		if (node.getNodeType() == IJSONNode.PAIR_NODE) {
			IJSONValue value = ((IJSONPair) node).getValue();
			JSONSchemaType[] types = JSONSchemaNode.getType(schema.get(IJSONSchemaNode.TYPE));
			if (value != null) {
				if (value.getNodeType() == IJSONNode.VALUE_STRING_NODE && isType(types, JSONSchemaType.String)) {
					validateString(node, schema, member, valinfo, value);
				}
				if (value.getNodeType() == IJSONNode.VALUE_NUMBER_NODE
						&& (isType(types, JSONSchemaType.Integer) || isType(types, JSONSchemaType.Number))) {
					validateNumber(node, schema, member, valinfo, value);
				}
				if (value.getNodeType() == IJSONNode.ARRAY_NODE && isType(types, JSONSchemaType.Array)) {
					validateArray(node, schema, member, valinfo, value);
				}
			}
		}
	}

	private void validateAdditionalProperties(IJSONNode node, JsonObject schema, JsonValue value,
			JSONValidationInfo valinfo) {
		if (value != null && value.isBoolean() && !value.asBoolean()) {
			Set<String> s = getProperties(node);
			Set<String> p = getProperties(schema.get(IJSONSchemaNode.PROPERTIES));
			Set<String> pp = getProperties(schema.get(IJSONSchemaNode.PATTERN_PROPERTIES));
			for (String string : p) {
				if (s.contains(string)) {
					s.remove(string);
				}
			}
			for (String patternStr : pp) {
				Pattern pattern = Pattern.compile(patternStr);
				Iterator<String> iter = s.iterator();
				while (iter.hasNext()) {
					String ss = iter.next();
					if (ss != null) {
						Matcher matcher = pattern.matcher(ss);
						if (matcher.find()) {
							iter.remove();
						}
					}
				}
			}
			for (String string : s) {
				if ("$schema".equals(string)) { //$NON-NLS-1$
					continue;
				}
				IJSONNode n = node.getFirstChild();
				while (n != null) {
					if (n instanceof IJSONPair) {
						IJSONPair pair = (IJSONPair) n;
						if (string.equals(pair.getName())) {
							break;
						}
					}
					n = n.getNextSibling();
				}
				if (n == null) {
					node = n;
				}
				int offset = n.getStartOffset();
				int line = n.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("Property " + string + " is not allowed", line, 0, offset == 0 ? 1 : offset);
			}
		}
	}

	private Set<String> getProperties(JsonValue value) {
		Set<String> result = new HashSet<String>();
		if (value instanceof JsonObject) {
			Iterator<Member> members = ((JsonObject) value).iterator();
			while (members.hasNext()) {
				result.add(members.next().getName());
			}
		}
		return result;
	}

	private void validateArray(IJSONNode node, JsonObject schema, Member member, JSONValidationInfo valinfo,
			IJSONValue value) {
		if (IJSONSchemaNode.ITEMS.equals(member.getName())) {
			validateItems(node, schema, value, member.getValue(), valinfo);
		}
		if (IJSONSchemaNode.MAX_ITEMS.equals(member.getName())) {
			validateMaxItems(node, schema, value, member.getValue(), valinfo);
		}
		if (IJSONSchemaNode.MIN_ITEMS.equals(member.getName())) {
			validateMinItems(node, schema, value, member.getValue(), valinfo);
		}
		if (IJSONSchemaNode.UNIQUE_ITEMS.equals(member.getName())) {
			validateUniqueItems(node, schema, value, member.getValue(), valinfo);
		}
	}

	private void validateMaxItems(IJSONNode node, JsonObject schema, IJSONValue value, JsonValue memberValue,
			JSONValidationInfo valinfo) {
		if (memberValue != null && memberValue.isNumber() && memberValue.asInt() > 0) {
			if (value instanceof IJSONArray) {
				int instanceSize = getSize((IJSONArray) value);
				int size = memberValue.asInt();
				if (instanceSize > size) {
					int offset = node.getStartOffset();
					int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
					valinfo.addMessage("Array has too many items. Expected " + size + " or fewer", line, 0,
							offset == 0 ? 1 : offset);
				}
			}
		}
	}

	private void validateMinItems(IJSONNode node, JsonObject schema, IJSONValue value, JsonValue memberValue,
			JSONValidationInfo valinfo) {
		if (memberValue != null && memberValue.isNumber() && memberValue.asInt() > 0) {
			if (value instanceof IJSONArray) {
				int instanceSize = getSize((IJSONArray) value);
				int size = memberValue.asInt();
				if (instanceSize < size) {
					int offset = node.getStartOffset();
					int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
					valinfo.addMessage("Array has too few items. Expected " + size + " or more", line, 0,
							offset == 0 ? 1 : offset);
				}
			}
		}
	}

	private void validateUniqueItems(IJSONNode node, JsonObject schema, IJSONValue value, JsonValue memberValue,
			JSONValidationInfo valinfo) {
		if (memberValue != null && memberValue.isBoolean() && memberValue.asBoolean()) {
			if (value instanceof IJSONArray) {
				Set<String> instanceValues = new HashSet<String>();
				IJSONNode child = value.getFirstChild();
				int instanceSize = 0;
				while (child != null) {
					instanceSize = instanceSize + 1;
					instanceValues.add(JSONUtil.getString(child));
					child = child.getNextSibling();
				}
				;
				if (instanceSize != instanceValues.size()) {
					int offset = node.getStartOffset();
					int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
					valinfo.addMessage("Array has duplicate items", line, 0, offset == 0 ? 1 : offset);
				}
			}
		}
	}

	private void validateItems(IJSONNode node, JsonObject schema, IJSONValue value, JsonValue memberValue,
			JSONValidationInfo valinfo) {
		JsonValue additionalItems = schema.get(IJSONSchemaNode.ADDITIONAL_ITEMS);
		if (additionalItems != null && additionalItems.isBoolean() && !additionalItems.asBoolean()) {
			if (memberValue != null && memberValue.isArray()) {
				if (value instanceof IJSONArray) {
					int instanceSize = getSize((IJSONArray) value);
					int size = memberValue.asArray().size();
					if (instanceSize > size) {
						int offset = node.getStartOffset();
						int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
						valinfo.addMessage("Array has too many items. Expected " + size + " or fewer", line, 0,
								offset == 0 ? 1 : offset);
					}
				}
			}
		}
	}

	private int getSize(IJSONArray instance) {
		if (instance == null) {
			return 0;
		}
		int instanceSize = 0;
		IJSONNode child = instance.getFirstChild();
		while (child != null) {
			instanceSize = instanceSize + 1;
			child = child.getNextSibling();
		}
		return instanceSize;
	}

	private void validateNumber(IJSONNode node, JsonObject schema, Member member, JSONValidationInfo valinfo,
			IJSONValue value) {
		if (IJSONSchemaNode.MULTIPLEOF.equals(member.getName())) {
			validateMultipleOf(node, schema, value, valinfo);
		}
		if (IJSONSchemaNode.MAXIMUM.equals(member.getName())) {
			validateMaximum(node, schema, value, valinfo);
		}
		if (IJSONSchemaNode.MINIMUM.equals(member.getName())) {
			validateMinimum(node, schema, value, valinfo);
		}
	}

	private void validateMaximum(IJSONNode node, JsonObject schema, IJSONValue valueNode, JSONValidationInfo valinfo) {
		double maximum;
		try {
			maximum = schema.getDouble(IJSONSchemaNode.MAXIMUM, Double.MIN_VALUE);
		} catch (Exception e) {
			maximum = Double.MIN_VALUE;
		}
		if (maximum > Double.MIN_VALUE) {
			boolean exclusiveMaximum;
			try {
				exclusiveMaximum = schema.getBoolean(IJSONSchemaNode.EXCLUSIVE_MAXIMUM, false);
			} catch (Exception e1) {
				exclusiveMaximum = false;
			}
			String valueStr = JSONUtil.getString(valueNode);
			try {
				double value = new Double(valueStr).doubleValue();
				boolean valid = exclusiveMaximum ? value < maximum : value <= maximum;
				if (!valid) {
					int offset = node.getStartOffset();
					int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
					if (exclusiveMaximum) {
						valinfo.addMessage("Value is above the exclusive maximum of " + maximum, line, 0,
								offset == 0 ? 1 : offset);
					} else {
						valinfo.addMessage("Value is above the maximum of " + maximum, line, 0,
								offset == 0 ? 1 : offset);
					}
				}
			} catch (NumberFormatException e) {
				// ignore
				return;
			}
		}
	}

	private void validateMinimum(IJSONNode node, JsonObject schema, IJSONValue valueNode, JSONValidationInfo valinfo) {
		double minimum;
		try {
			minimum = schema.getDouble(IJSONSchemaNode.MINIMUM, Double.MAX_VALUE);
		} catch (Exception e) {
			minimum = Double.MAX_VALUE;
		}
		if (minimum < Double.MAX_VALUE) {
			boolean exclusiveMinimum;
			try {
				exclusiveMinimum = schema.getBoolean(IJSONSchemaNode.EXCLUSIVE_MINIMUM, false);
			} catch (Exception e1) {
				exclusiveMinimum = false;
			}
			String valueStr = JSONUtil.getString(valueNode);
			try {
				double value = new Double(valueStr).doubleValue();
				boolean valid = exclusiveMinimum ? value > minimum : value >= minimum;
				if (!valid) {
					int offset = node.getStartOffset();
					int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
					if (exclusiveMinimum) {
						valinfo.addMessage("Value is below the exclusive minimum of " + minimum, line, 0,
								offset == 0 ? 1 : offset);
					} else {
						valinfo.addMessage("Value is below the minimum of " + minimum, line, 0,
								offset == 0 ? 1 : offset);
					}
				}
			} catch (NumberFormatException e) {
				// ignore
				return;
			}
		}
	}

	private void validateMultipleOf(IJSONNode node, JsonObject schema, IJSONValue valueNode,
			JSONValidationInfo valinfo) {
		int multipleOff;
		try {
			multipleOff = schema.getInt(IJSONSchemaNode.MULTIPLEOF, -1);
		} catch (Exception e) {
			multipleOff = -1;
		}
		if (multipleOff > 0) {
			String value = JSONUtil.getString(valueNode);
			double n;
			try {
				n = new Double(value).doubleValue();
			} catch (NumberFormatException e) {
				// ignore
				return;
			}
			long div = Math.round(n / multipleOff);
			if (Math.abs(div * multipleOff - n) > 1e-12) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("Value is not divisible by " + multipleOff, line, 0, offset == 0 ? 1 : offset);
			}
		}
	}

	private boolean isType(JSONSchemaType[] types, JSONSchemaType type) {
		for (JSONSchemaType t : types) {
			if (t == type) {
				return true;
			}
		}
		return false;
	}

	private void validateString(IJSONNode node, JsonObject schema, Member member, JSONValidationInfo valinfo,
			IJSONValue value) {
		if (IJSONSchemaNode.MIN_LENGTH.equals(member.getName())) {
			validateMinLength(node, schema, value, valinfo);
		}
		if (IJSONSchemaNode.MAX_LENGTH.equals(member.getName())) {
			validateMaxLength(node, schema, value, valinfo);
		}
		if (IJSONSchemaNode.PATTERN.equals(member.getName())) {
			validatePattern(node, schema, value, valinfo);
		}
	}

	private void validateMaxLength(IJSONNode node, JsonObject schema, IJSONValue valueNode,
			JSONValidationInfo valinfo) {
		int maxLength;
		try {
			maxLength = schema.getInt(IJSONSchemaNode.MAX_LENGTH, -1);
		} catch (Exception e) {
			maxLength = -1;
		}
		if (maxLength >= 0) {
			String value = JSONUtil.getString(valueNode);
			boolean valid = value == null || value.length() <= maxLength;
			if (!valid) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("String is longer than the maximum length of " + maxLength, line, 0,
						offset == 0 ? 1 : offset);
			}
		}
	}

	private void validateMinLength(IJSONNode node, JsonObject schema, IJSONValue valueNode,
			JSONValidationInfo valinfo) {
		int minLength;
		try {
			minLength = schema.getInt(IJSONSchemaNode.MIN_LENGTH, -1);
		} catch (Exception e) {
			minLength = -1;
		}
		if (minLength >= 0) {
			String value = JSONUtil.getString(valueNode);
			boolean valid = value == null || value.length() >= minLength;
			if (!valid) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("String is shorter than the minimum length of " + minLength, line, 0,
						offset == 0 ? 1 : offset);
			}
		}
	}

	private void validatePattern(IJSONNode node, JsonObject schema, IJSONValue valueNode, JSONValidationInfo valinfo) {
		String patternStr;
		try {
			patternStr = schema.getString(IJSONSchemaNode.PATTERN, null);
		} catch (Exception e) {
			patternStr = null;
		}
		if (patternStr != null) {
			String value = JSONUtil.getString(valueNode);
			if (value != null) {
				Pattern pattern = Pattern.compile(patternStr);
				Matcher matcher = pattern.matcher(value);
				if (!matcher.matches()) {
					int offset = node.getStartOffset();
					int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
					valinfo.addMessage("String does not match the pattern of " + patternStr, line, 0,
							offset == 0 ? 1 : offset);
				}
			}
		}
	}

	private void validateType(IJSONNode node, Member member, JSONValidationInfo valinfo) {
		if (IJSONSchemaNode.TYPE.equals(member.getName())) {
			Set<String> types = new HashSet<String>();
			if (member.getValue().isString()) {
				types.add(member.getValue().asString());
			} else if (member.getValue().isArray()) {
				JsonArray array = (JsonArray) member.getValue();
				for (JsonValue item : array) {
					types.add(item.asString());
				}
			}
			boolean valid = false;
			for (String type : types) {
				if (node.getNodeType() == IJSONNode.OBJECT_NODE && JSONSchemaType.Object.getName().equals(type)) {
					valid = true;
					break;
				}
				if (node.getNodeType() == IJSONNode.PAIR_NODE) {
					IJSONValue value = ((IJSONPair) node).getValue();
					if (value == null && JSONSchemaType.Null.getName().equals(type)) {
						valid = true;
						break;
					}
					if (value == null) {
						valid = false;
						break;
					}
					if (value.getNodeType() == IJSONNode.OBJECT_NODE && JSONSchemaType.Object.getName().equals(type)) {
						valid = true;
						break;
					}
					if (value.getNodeType() == IJSONNode.VALUE_STRING_NODE
							&& JSONSchemaType.String.getName().equals(type)) {
						valid = true;
						break;
					}
					if (value.getNodeType() == IJSONNode.ARRAY_NODE && JSONSchemaType.Array.getName().equals(type)) {
						valid = true;
						break;
					}
					if (value.getNodeType() == IJSONNode.VALUE_BOOLEAN_NODE
							&& JSONSchemaType.Boolean.getName().equals(type)) {
						valid = true;
						break;
					}
					if (value.getNodeType() == IJSONNode.VALUE_NULL_NODE
							&& JSONSchemaType.Null.getName().equals(type)) {
						valid = true;
						break;
					}
					if (value.getNodeType() == IJSONNode.VALUE_NUMBER_NODE
							&& JSONSchemaType.Number.getName().equals(type)) {
						valid = true;
						break;
					}
					if (value.getNodeType() == IJSONNode.VALUE_NUMBER_NODE
							&& JSONSchemaType.Integer.getName().equals(type)) {
						valid = true;
						break;
					}
				}
			}
			if (!valid) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				StringBuffer buffer = new StringBuffer();
				Iterator<String> iter = types.iterator();
				buffer.append(OPEN_BRACKET);
				while (iter.hasNext()) {
					buffer.append(iter.next());
					if (iter.hasNext()) {
						buffer.append(COMMA);
					}
				}
				buffer.append(CLOSE_BRACKET);
				valinfo.addMessage("Incorrect type. Expected " + buffer.toString(), line, 0, offset == 0 ? 1 : offset);
			}
		}
	}

	private void validateEnum(IJSONNode node, JsonObject schema, JSONValidationInfo valinfo) {
		JsonValue value = schema.get(IJSONSchemaNode.ENUM);
		if (value instanceof JsonArray) {
			JsonArray array = value.asArray();
			Iterator<JsonValue> iter = array.iterator();
			Set<String> values = new HashSet<String>();
			while (iter.hasNext()) {
				String v = iter.next().toString();
				values.add(JSONUtil.removeQuote(v));
			}
			if (node instanceof IJSONPair) {
				IJSONPair pair = (IJSONPair) node;
				String v = JSONUtil.getString(pair.getValue());
				if (!values.contains(v)) {
					int offset = node.getStartOffset();
					int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
					valinfo.addMessage("Value is not an accepted value. Valid values " + values + "'", line, 0,
							offset == 0 ? 1 : offset);
				}
			}
		}
	}

	private void validateRequired(IJSONNode node, JsonObject schema, JSONValidationInfo valinfo) {
		JsonValue required = schema.get(IJSONSchemaNode.REQUIRED);
		if (required instanceof JsonArray) {
			JsonArray array = required.asArray();
			Iterator<JsonValue> iter = array.iterator();
			Set<String> values = new HashSet<String>();
			while (iter.hasNext()) {
				JsonValue v = iter.next();
				if (v.isString()) {
					values.add(v.asString());
				}
			}
			Set<String> properties = getProperties(node);
			for (String property : properties) {
				if (property != null && values.contains(property)) {
					values.remove(property);
				}
			}
			for (String value : values) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("Missing property '" + value + "'", line, 0, offset == 0 ? 1 : offset);
			}
		}
	}

	private void validateMinProperties(IJSONNode node, JsonObject schema, JSONValidationInfo valinfo) {
		int value;
		try {
			value = schema.getInt(IJSONSchemaNode.MIN_PROPERTIES, -1);
		} catch (Exception e) {
			value = -1;
		}
		if (value >= 0) {
			Set<String> properties = getProperties(node);
			if (properties.size() < value) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("Object has fewer properties than the required number of" + value, line, 0,
						offset == 0 ? 1 : offset);
			}
		}
	}

	private void validateMaxProperties(IJSONNode node, JsonObject schema, JSONValidationInfo valinfo) {
		int value;
		try {
			value = schema.getInt(IJSONSchemaNode.MAX_PROPERTIES, -1);
		} catch (Exception e) {
			value = -1;
		}
		if (value >= 0) {
			Set<String> properties = getProperties(node);
			if (properties.size() > value) {
				int offset = node.getStartOffset();
				int line = node.getModel().getStructuredDocument().getLineOfOffset(offset);
				valinfo.addMessage("Object has more properties than limit of" + value, line, 0,
						offset == 0 ? 1 : offset);
			}
		}
	}

	private Set<String> getProperties(IJSONNode node) {
		Set<String> properties = new HashSet<String>();
		IJSONNode child = node.getFirstChild();
		while (child != null) {
			if (child instanceof IJSONPair) {
				IJSONPair pair = (IJSONPair) child;
				if (pair.getName() != null) {
					properties.add(pair.getName());
				}
			}
			child = child.getNextSibling();
		}
		return properties;
	}

	protected IStructuredModel getModel(String uriString) {
		URI uri;
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e) {
			logWarning(e);
			return null;
		}
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
		if (files == null || files.length <= 0 || !files[0].exists()) {
			return null;
		}
		IFile file = files[0];
		IModelManager manager = StructuredModelManager.getModelManager();
		if (manager == null)
			return null;

		IStructuredModel model = null;
		try {
			file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		} catch (CoreException e) {
			logWarning(e);
		}
		try {
			try {
				model = manager.getModelForRead(file);
			} catch (UnsupportedEncodingException ex) {
				// retry ignoring META charset for invalid META charset
				// specification
				// recreate input stream, because it is already partially read
				model = manager.getModelForRead(file, new String(), null);
			}
		} catch (UnsupportedEncodingException ex) {
		} catch (IOException ex) {
		} catch (CoreException e) {
			logWarning(e);
		}
		return model;
	}

	private static void logWarning(Exception e) {
		IStatus status = new Status(IStatus.WARNING, JSONCorePlugin.PLUGIN_ID, e.getMessage(), e);
		JSONCorePlugin.getDefault().getLog().log(status);
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
	protected void addInfoToMessage(ValidationMessage validationMessage, IMessage message) {
		String key = validationMessage.getKey();
		if (key != null) {
			JSONMessageInfoHelper messageInfoHelper = new JSONMessageInfoHelper();
			String[] messageInfo = messageInfoHelper.createMessageInfo(key, validationMessage.getMessageArguments());

			message.setAttribute(COLUMN_NUMBER_ATTRIBUTE, new Integer(validationMessage.getColumnNumber()));
			/*
			 * message.setAttribute(SQUIGGLE_SELECTION_STRATEGY_ATTRIBUTE,
			 * messageInfo[0]);
			 * message.setAttribute(SQUIGGLE_NAME_OR_VALUE_ATTRIBUTE,
			 * messageInfo[1]);
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
	protected NestedValidatorContext getNestedContext(ValidationState state, boolean create) {
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
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor) {
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
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor) {
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
