/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.ui.internal.validation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.validation.JSPActionValidator;
import org.eclipse.jst.jsp.core.internal.validation.JSPContentValidator;
import org.eclipse.jst.jsp.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.FileBufferModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.internal.reconcile.validator.ISourceValidator;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidationConfiguration;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;

/**
 * Source validator (able to check partial document) that checks for: -
 * missing required attributes & undefined attributes in jsp action tags such
 * as jsp directives and jsp custom tags
 */
public class JSPActionSourceValidator extends JSPActionValidator implements ISourceValidator {
	private IDocument fDocument;
	private boolean fEnableSourceValidation;
	private IContentType fJSPFContentType = null;

	public void connect(IDocument document) {
		fDocument = document;

		// special checks to see source validation should really execute
		IFile file = null;
		IStructuredModel model = null;
		try {
			model = StructuredModelManager.getModelManager().getExistingModelForRead(document);
			if (model != null) {
				String baseLocation = model.getBaseLocation();
				// The baseLocation may be a path on disk or relative to the
				// workspace root. Don't translate on-disk paths to
				// in-workspace resources.
				IPath basePath = new Path(baseLocation);
				if (basePath.segmentCount() > 1) {
					file = ResourcesPlugin.getWorkspace().getRoot().getFile(basePath);
					/*
					 * If the IFile doesn't exist, make sure it's not returned
					 */
					if (!file.exists())
						file = null;
				}
			}
		}
		finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
		fEnableSourceValidation = (file != null && isBatchValidatorPreferenceEnabled(file) && shouldValidate(file) && fragmentCheck(file));
	}

	public void disconnect(IDocument document) {
		fDocument = null;
	}

	public void validate(IRegion dirtyRegion, IValidationContext helper, IReporter reporter) {
		if (helper == null || fDocument == null || !fEnableSourceValidation)
			return;

		if ((reporter != null) && (reporter.isCancelled() == true)) {
			throw new OperationCanceledException();
		}

		IStructuredModel model = StructuredModelManager.getModelManager().getExistingModelForRead(fDocument);
		if (model == null)
			return;

		try {
			ITextFileBuffer fb = FileBufferModelManager.getInstance().getBuffer(fDocument);
			if (fb == null)
				return;
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(fb.getLocation());
			if (file == null || !file.exists())
				return;
			performValidation(file, reporter, model, dirtyRegion);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Gets current validation configuration based on current project (which
	 * is based on current document) or global configuration if project does
	 * not override
	 * 
	 * @return ValidationConfiguration
	 */
	private ValidationConfiguration getValidationConfiguration(IFile file) {
		ValidationConfiguration configuration = null;
		if (file != null) {
			IProject project = file.getProject();
			if (project != null) {
				try {
					ProjectConfiguration projectConfiguration = ConfigurationManager.getManager().getProjectConfiguration(project);
					configuration = projectConfiguration;
					if (projectConfiguration == null || projectConfiguration.useGlobalPreference()) {
						configuration = ConfigurationManager.getManager().getGlobalConfiguration();
					}
				}
				catch (InvocationTargetException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
			}
		}

		return configuration;
	}

	/**
	 * Checks if validator is enabled according in Validation preferences
	 * 
	 * @param vmd
	 * @return
	 */
	private boolean isBatchValidatorPreferenceEnabled(IFile file) {
		if (file == null) {
			return true;
		}

		boolean enabled = true;
		ValidationConfiguration configuration = getValidationConfiguration(file);
		if (configuration != null) {
			org.eclipse.wst.validation.internal.ValidatorMetaData metadata = ValidationRegistryReader.getReader().getValidatorMetaData(JSPContentValidator.class.getName());
			if (metadata != null) {
				if (!configuration.isBuildEnabled(metadata) && !configuration.isManualEnabled(metadata))
					enabled = false;
			}
		}
		return enabled;
	}

	/**
	 * Checks if file is a jsp fragment or not. If so, check if the fragment
	 * should be validated or not.
	 * 
	 * @param file
	 *            Assumes shouldValidate was already called on file so it
	 *            should not be null and does exist
	 * @return false if file is a fragment and it should not be validated,
	 *         true otherwise
	 */
	private boolean fragmentCheck(IFile file) {
		// copied from JSPValidator
		boolean shouldValidate = true;
		// quick check to see if this is possibly a jsp fragment
		if (getJSPFContentType().isAssociatedWith(file.getName())) {
			// get preference for validate jsp fragments
			boolean shouldValidateFragments = Boolean.valueOf(JSPFContentProperties.getProperty(JSPFContentProperties.VALIDATE_FRAGMENTS, file, true)).booleanValue();
			/*
			 * if jsp fragments should not be validated, check if file is
			 * really jsp fragment
			 */
			if (!shouldValidateFragments) {
				boolean isFragment = isFragment(file);
				shouldValidate = !isFragment;
			}
		}
		return shouldValidate;
	}

	/**
	 * Determines if file is jsp fragment or not (does a deep, indepth check,
	 * looking into contents of file)
	 * 
	 * @param file
	 *            assumes file is not null and exists
	 * @return true if file is jsp fragment, false otherwise
	 */
	private boolean isFragment(IFile file) {
		// copied from JSPValidator
		boolean isFragment = false;
		InputStream is = null;
		try {
			IContentDescription contentDescription = file.getContentDescription();
			// it can be null
			if (contentDescription == null) {
				is = file.getContents();
				contentDescription = Platform.getContentTypeManager().getDescriptionFor(is, file.getName(), new QualifiedName[]{IContentDescription.CHARSET});
			}
			if (contentDescription != null) {
				String fileCtId = contentDescription.getContentType().getId();
				isFragment = (fileCtId != null && ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT.equals(fileCtId));
			}
		}
		catch (IOException e) {
			// ignore, assume it's invalid JSP
		}
		catch (CoreException e) {
			// ignore, assume it's invalid JSP
		}
		finally {
			// must close input stream in case others need it
			if (is != null)
				try {
					is.close();
				}
				catch (Exception e) {
					// not sure how to recover at this point
				}
		}
		return isFragment;
	}

	private boolean shouldValidate(IFile file) {
		// copied from JSPValidator
		IResource resource = file;
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
				return false;
			}
			resource = resource.getParent();
		}
		while ((resource.getType() & IResource.PROJECT) == 0);
		return true;
	}

	/**
	 * Returns JSP fragment content type
	 * 
	 * @return jspf content type
	 */
	private IContentType getJSPFContentType() {
		// copied from JSPValidator
		if (fJSPFContentType == null) {
			fJSPFContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
		}
		return fJSPFContentType;
	}
}
