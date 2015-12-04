/*******************************************************************************
 * Copyright (c) 2001, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Carver - STAR - [205989] - [validation] validate XML after XInclude resolution
 *     Angelo Zerr <angelo.zerr@gmail.com> - copied from org.eclipse.wst.xml.core.internal.validation.XMLValidator
 *                                           modified in order to process JSON Objects.            
 *******************************************************************************/
package org.eclipse.wst.json.core.internal.validation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.json.ValidatorHelper;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolver;
import org.eclipse.wst.common.uriresolver.internal.util.URIHelper;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.core.validation.AnnotationMsg;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;

public class JSONValidator {
	  
	protected URIResolver uriResolver = null;
	private JSONSyntaxValidator val = new JSONSyntaxValidator();
	/**
	 * Validate the inputStream
	 * 
	 * @param uri
	 *            The URI of the file to validate.
	 * @param the
	 *            inputStream of the file to validate
	 * @return Returns an JSON validation report.
	 */
	public JSONValidationReport validate(String uri, InputStream inputStream) {
		return validate(uri, inputStream, new JSONValidationConfiguration());
	}

	/**
	 * Validate the inputStream
	 * 
	 * @param uri
	 *            The URI of the file to validate.
	 * @param inputstream
	 *            The inputStream of the file to validate
	 * @param configuration
	 *            A configuration for this validation session.
	 * @return Returns an JSON validation report.
	 */
	public JSONValidationReport validate(String uri, InputStream inputStream,
			JSONValidationConfiguration configuration) {
		return validate(uri, inputStream, configuration, null);
	}

	/**
	 * Validate the inputStream
	 * 
	 * @param uri
	 *            The URI of the file to validate.
	 * @param inputstream
	 *            The inputStream of the file to validate
	 * @param configuration
	 *            A configuration for this validation session.
	 * @param result
	 *            The validation result
	 * @return Returns an JSON validation report.
	 */
	public JSONValidationReport validate(String uri, InputStream inputStream,
			JSONValidationConfiguration configuration, ValidationResult result) {
		return validate(uri, inputStream, configuration, null, null);
	}

	/**
	 * Validate the inputStream
	 * 
	 * @param uri
	 *            The URI of the file to validate.
	 * @param inputstream
	 *            The inputStream of the file to validate
	 * @param configuration
	 *            A configuration for this validation session.
	 * @param result
	 *            The validation result
	 * @param context
	 *            The validation context
	 * @return Returns an JSON validation report.
	 */
	public JSONValidationReport validate(String uri, InputStream inputStream,
			JSONValidationConfiguration configuration, ValidationResult result,
			NestedValidatorContext context) {
		/*String grammarFile = ""; //$NON-NLS-1$
		Reader reader1 = null; // Used for the preparse.
		Reader reader2 = null; // Used for validation parse.

		if (inputStream != null) {
			String string = createStringForInputStream(inputStream);
			reader1 = new StringReader(string);
			reader2 = new StringReader(string);
		}*/

		JSONValidationInfo valinfo = new JSONValidationInfo(uri);
		if (inputStream != null) {
			ValidatorHelper.validate(new InputStreamReader(inputStream),
					valinfo);
		}
		//JsonValidatorHelper.validate(text, reporter);
		
		/*MyEntityResolver entityResolver = new MyEntityResolver(uriResolver,
				context);
		ValidatorHelper helper = new ValidatorHelper();
		try {
			helper.computeValidationInformation(uri, reader1, uriResolver);
			valinfo.setDTDEncountered(helper.isDTDEncountered);
			valinfo.setElementDeclarationCount(helper.numDTDElements);
			valinfo.setNamespaceEncountered(helper.isNamespaceEncountered);
			valinfo.setGrammarEncountered(helper.isGrammarEncountered);
			JSONReader reader = createJSONReader(valinfo, entityResolver);
			// Set the configuration option
			if (configuration
					.getFeature(JSONValidationConfiguration.HONOUR_ALL_SCHEMA_LOCATIONS)) {
				reader.setFeature(
						"http://apache.org/xml/features/honour-all-schemaLocations", true); //$NON-NLS-1$
			}
			if (configuration
					.getFeature(JSONValidationConfiguration.USE_XINCLUDE)) {
				reader.setFeature(
						"http://apache.org/xml/features/xinclude", true); //$NON-NLS-1$      
			}

			// Support external schema locations
			boolean isGrammarEncountered = helper.isGrammarEncountered;
			if (!isGrammarEncountered) {
				isGrammarEncountered = checkExternalSchemas(reader,
						valinfo.getFileURI());
			}
			reader.setFeature(
					"http://xml.org/sax/features/validation", isGrammarEncountered); //$NON-NLS-1$
			reader.setFeature(
					"http://apache.org/xml/features/validation/schema", isGrammarEncountered); //$NON-NLS-1$

			JSONErrorHandler errorhandler = new JSONErrorHandler(valinfo);
			reader.setErrorHandler(errorhandler);

			InputSource inputSource = new InputSource(uri);
			inputSource.setCharacterStream(reader2);

			ClassLoader originalClzLoader = Thread.currentThread()
					.getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					getClass().getClassLoader());

			try {
				reader.parse(inputSource);
			} finally {
				Thread.currentThread().setContextClassLoader(originalClzLoader);
			}

			if (configuration
					.getIntFeature(JSONValidationConfiguration.INDICATE_NO_GRAMMAR) > 0
					&& valinfo.isValid() && !isGrammarEncountered) {
				if (configuration
						.getIntFeature(JSONValidationConfiguration.INDICATE_NO_GRAMMAR) == 1)
					valinfo.addWarning(JSONValidationMessages._WARN_NO_GRAMMAR,
							1, 0, uri, NO_GRAMMAR_FOUND, null);
				else
					// 2
					valinfo.addError(JSONValidationMessages._WARN_NO_GRAMMAR,
							1, 0, uri, NO_GRAMMAR_FOUND, null);
			}
			if (configuration
					.getIntFeature(JSONValidationConfiguration.INDICATE_NO_DOCUMENT_ELEMENT) > 0
					&& valinfo.isValid()
					&& !helper.isDocumentElementEncountered) {
				if (configuration
						.getIntFeature(JSONValidationConfiguration.INDICATE_NO_DOCUMENT_ELEMENT) == 1)
					valinfo.addWarning(
							JSONValidationMessages._NO_DOCUMENT_ELEMENT, 1, 0,
							uri, NO_DOCUMENT_ELEMENT_FOUND, null);
				else
					// 2
					valinfo.addError(
							JSONValidationMessages._NO_DOCUMENT_ELEMENT, 1, 0,
							uri, NO_DOCUMENT_ELEMENT_FOUND, null);
			}
			if (helper.isDTDEncountered)
				grammarFile = entityResolver.getLocation();
			else
				grammarFile = helper.schemaLocationString;
		} catch (SAXParseException saxParseException) {
			// These errors are caught by the error handler.
			// addValidationMessage(valinfo, saxParseException);
		} catch (IOException ioException) {
			addValidationMessage(valinfo, ioException);
		} catch (Exception exception) {
			Logger.logException(exception.getLocalizedMessage(), exception);
		}

		// Now set up the dependencies
		// Wrap with try catch so that if something wrong happens, validation
		// can
		// still proceed as before
		if (result != null) {
			try {
				IResource resource = getWorkspaceFileFromLocation(grammarFile);
				ArrayList resources = new ArrayList();
				if (resource != null)
					resources.add(resource);
				result.setDependsOn((IResource[]) resources
						.toArray(new IResource[0]));
			} catch (Exception e) {
				Logger.logException(e.getLocalizedMessage(), e);
			}
		}*/

		//if (JSONCorePlugin.getDefault().getPluginPreferences()
		//		.getBoolean(JSONCorePreferenceNames.SYNTAX_VALIDATION)) {
		if (false) {
			IReporter reporter = executeMarkupValidator(uri);
			if (reporter != null) {
				List msgList = reporter.getMessages();
				for (int i = 0; i < msgList.size(); i++) {
					LocalizedMessage msg = (LocalizedMessage) msgList.get(i);
					if (msg.getSeverity() == 2)
						valinfo.addError(msg.getLocalizedMessage(),
								msg.getLineNumber(), msg.getOffset(),
								valinfo.getFileURI(),
								"null", getMsgArguments(msg)); //$NON-NLS-1$
					else if (msg.getSeverity() == 1)
						valinfo.addWarning(msg.getLocalizedMessage(),
								msg.getLineNumber(), msg.getOffset(),
								valinfo.getFileURI(),
								"null", getMsgArguments(msg)); //$NON-NLS-1$
				}
			}
		}

		return valinfo;

	}

	  private Object[] getMsgArguments(LocalizedMessage msg){
		  Object obj = msg.getAttribute(AnnotationMsg.ID);
		  return new Object[]{obj};
	  }
	 

	private IReporter executeMarkupValidator(String uri) {
		Path path = new Path(uri);
		String fileProtocol = "file://"; //$NON-NLS-1$
		int index = uri.indexOf(fileProtocol);

		IFile resource = null;
		if (index == 0) {
			String transformedUri = uri.substring(fileProtocol.length());
			Path transformedPath = new Path(transformedUri);
			resource = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(transformedPath);
		} else {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

		}
		IReporter reporter = null;
		if (resource != null) {
			reporter = val.validate(resource, 0, new ValOperation().getState());
		}
		return reporter;
	}
	
	protected IResource getWorkspaceFileFromLocation(String location) {
		if (location == null)
			return null;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// To canonicalize the EMF URI
		IPath canonicalForm = new Path(location);
		// Need to convert to absolute location...
		IPath pathLocation = new Path(URIHelper.removeProtocol(canonicalForm
				.toString()));
		// ...to find the resource file that is in the workspace
		IResource resourceFile = workspace.getRoot().getFileForLocation(
				pathLocation);
		// If the resource is resolved to a file from http, or a file outside
		// the workspace, then we will just ignore it.
		return resourceFile;
	}

	/**
	 * Set the URI Resolver to use.
	 * 
	 * @param uriResolver
	 *            The URI Resolver to use.
	 */
	public void setURIResolver(URIResolver uriResolver) {
		this.uriResolver = uriResolver;
		// entityResolver = new MyEntityResolver(uriResolver);
	}
}
