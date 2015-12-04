/**
 *  Copyright (c) 2013-2014 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.wst.json.core.internal.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.json.core.JSONCorePlugin;
import org.eclipse.wst.json.core.contenttype.ContentTypeIdForJSON;
import org.eclipse.wst.json.core.internal.JSONCoreMessages;
import org.eclipse.wst.json.core.internal.Logger;
import org.eclipse.wst.json.core.internal.parser.JSONLineTokenizer;
import org.eclipse.wst.json.core.preferences.JSONCorePreferenceNames;
import org.eclipse.wst.json.core.regions.JSONRegionContexts;
import org.eclipse.wst.json.core.util.JSONUtil;
import org.eclipse.wst.json.core.validation.AnnotationMsg;
import org.eclipse.wst.json.core.validation.ISeverityProvider;
import org.eclipse.wst.json.core.validation.JSONSyntaxValidatorHelper;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.document.DocumentReader;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

public class JSONSyntaxValidator extends AbstractValidator implements
		IValidator, ISeverityProvider {

	/** The validation reporter */
	private IReporter fReporter;

	private IContentType jsonContentType;

	@Override
	public void cleanup(IReporter reporter) {
		jsonContentType = null;
	}

	@Override
	public void validate(IValidationContext helper, IReporter reporter)
			throws ValidationException {
		final String[] uris = helper.getURIs();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		if (uris.length > 0) {
			IFile currentFile = null;

			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				// might be called with just project path?
				IPath path = new Path(uris[i]);
				if (path.segmentCount() > 1) {
					currentFile = root.getFile(path);
					if (shouldValidate(currentFile, true)) {
						validateFile(currentFile, reporter);
					}
				} else if (uris.length == 1) {
					validateProject(helper, reporter);
				}
			}
		} else
			validateProject(helper, reporter);
	}

	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		ValidationResult result = new ValidationResult();
		fReporter = result.getReporter(monitor);
		validateFile((IFile) resource, fReporter);
		return result;
	}

	/**
	 * Convenience method for validating a resource and getting back the
	 * reporter
	 * 
	 * @param resource
	 *            The resource to be validated.
	 * @param kind
	 *            The way the resource changed. It uses the same values as the
	 *            kind parameter in IResourceDelta.
	 * @param state
	 *            A way to pass arbitrary, validator specific, data from one
	 *            invocation of a validator to the next, during the validation
	 *            phase. At the end of the validation phase, this object will be
	 *            cleared, thereby allowing any of this state information to be
	 *            garbaged collected.
	 * @return the validator's reporter
	 */
	public IReporter validate(IResource resource, int kind,
			ValidationState state) {
		validate(resource, kind, state, new NullProgressMonitor());
		return fReporter;
	}

	/**
	 * Validates the given file. It will stream the contents of the file without
	 * creating a model for the file; it will only use existing
	 * 
	 * @param file
	 *            the file to validate
	 * @param reporter
	 *            the reporter
	 */
	private void validateFile(IFile file, IReporter reporter) {
		Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, file
				.getFullPath().toString().substring(1));
		reporter.displaySubtask(JSONSyntaxValidator.this, message);

		JSONLineTokenizer tokenizer = null;
		try {
			IStructuredModel model = StructuredModelManager.getModelManager()
					.getExistingModelForRead(file);
			try {
				if (model == null) {
					tokenizer = new JSONLineTokenizer(new BufferedReader(
							new InputStreamReader(file.getContents(true),
									getCharset(file))));
				} else {
					tokenizer = new JSONLineTokenizer(new BufferedReader(
							new DocumentReader(model.getStructuredDocument())));
				}
				JSONSyntaxValidatorHelper.validate(tokenizer, reporter, this,
						this);
			} finally {
				if (model != null) {
					model.releaseFromRead();
					model = null;
				}
			}
		} catch (UnsupportedEncodingException e) {
		} catch (CoreException e) {
		} catch (IOException e) {
		}
	}

	private void validateProject(IValidationContext helper,
			final IReporter reporter) {
		// if uris[] length 0 -> validate() gets called for each project
		if (helper instanceof IWorkbenchContext) {
			IProject project = ((IWorkbenchContext) helper).getProject();
			IResourceProxyVisitor visitor = new IResourceProxyVisitor() {
				public boolean visit(IResourceProxy proxy) throws CoreException {
					if (shouldValidate(proxy)) {
						validateFile((IFile) proxy.requestResource(), reporter);
					}
					return true;
				}
			};
			try {
				// collect all jsp files for the project
				project.accept(visitor, IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
				Logger.logException(e);
			}
		}
	}

	private boolean shouldValidate(IResourceProxy proxy) {
		if (proxy.getType() == IResource.FILE) {
			String name = proxy.getName();
			if (name.toLowerCase(Locale.US).endsWith(".json")) { //$NON-NLS-1$
				return true;
			}
		}
		return shouldValidate(proxy.requestResource(), false);
	}

	private boolean shouldValidate(IResource file, boolean checkExtension) {
		if (file == null || !file.exists() || file.getType() != IResource.FILE)
			return false;
		if (checkExtension) {
			String extension = file.getFileExtension();
			if (extension != null
					&& "json".endsWith(extension.toLowerCase(Locale.US))) //$NON-NLS-1$
				return true;
		}

		IContentDescription contentDescription = null;
		try {
			contentDescription = ((IFile) file).getContentDescription();
			if (contentDescription != null) {
				IContentType contentType = contentDescription.getContentType();
				return contentDescription != null
						&& contentType.isKindOf(getJSONContentType());
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}
		return false;
	}

	private IContentType getJSONContentType() {
		if (jsonContentType == null) {
			jsonContentType = Platform.getContentTypeManager().getContentType(
					ContentTypeIdForJSON.ContentTypeID_JSON); //$NON-NLS-1$
		}
		return jsonContentType;
	}

	private String getCharset(IFile file) {
		if (file != null && file.isAccessible()) {
			try {
				return file.getCharset(true);
			} catch (CoreException e) {
			}
		}
		return ResourcesPlugin.getEncoding();
	}

	@Override
	public int getSeverity(String preferenceName) {
		return getPluginPreference().getInt(preferenceName);
	}

	private Preferences getPluginPreference() {
		return JSONCorePlugin.getDefault().getPluginPreferences();
	}
}
