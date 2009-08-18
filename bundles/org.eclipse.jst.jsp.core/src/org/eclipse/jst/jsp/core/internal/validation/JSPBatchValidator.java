/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache;
import org.eclipse.jst.jsp.core.internal.contenttype.DeploymentDescriptorPropertyCache.PropertyGroup;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.utils.StringUtils;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;

/**
 * Performs JSP validation tasks for batch validation. The individual
 * validator classes will still be used for source validation.
 */
public final class JSPBatchValidator extends AbstractValidator implements IValidatorJob, IExecutableExtension {
	class JSPFileVisitor implements IResourceProxyVisitor {

		private List fFiles = new ArrayList();
		private IReporter fReporter = null;

		public JSPFileVisitor(IReporter reporter) {
			fReporter = reporter;
		}

		final IFile[] getFiles() {
			return (IFile[]) fFiles.toArray(new IFile[fFiles.size()]);
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {

			// check validation
			if (fReporter.isCancelled())
				return false;

			if (proxy.getType() == IResource.FILE) {

				if (isJSPType(proxy.getName()) && proxy.isAccessible()) {
					IFile file = (IFile) proxy.requestResource();
					if (DEBUG)
						System.out.println("(+) JSPValidator adding file: " + file.getName()); //$NON-NLS-1$
					fFiles.add(file);

					// don't search deeper for files
					return false;
				}
			}
			return true;
		}
	}

	// for debugging
	static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$

	private static final String PLUGIN_ID_JSP_CORE = "org.eclipse.jst.jsp.core"; //$NON-NLS-1$

	/**
	 * List of IResources that the currently validating file depends upon
	 */
	private Collection fDependsOn;

	/**
	 * Gets current validation project configuration based on current project
	 * (which is based on current document)
	 * 
	 * @return ProjectConfiguration
	 */
	static private ProjectConfiguration getProjectConfiguration(IFile file) {
		ProjectConfiguration projectConfiguration = null;
		if (file != null) {
			IProject project = file.getProject();
			if (project != null) {
				try {
					projectConfiguration = ConfigurationManager.getManager().getProjectConfiguration(project);
				}
				catch (InvocationTargetException e) {
					Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
				}
			}
		}

		return projectConfiguration;
	}

	/**
	 * Checks if validator is enabled according in Validation preferences
	 * 
	 * @param vmd
	 * @return
	 */
	static boolean isBatchValidatorPreferenceEnabled(IFile file) {
		if (file == null) {
			return true;
		}

		boolean enabled = true;
		ProjectConfiguration configuration = getProjectConfiguration(file);
		if (configuration != null) {
			org.eclipse.wst.validation.internal.ValidatorMetaData metadata = ValidationRegistryReader.getReader().getValidatorMetaData(JSPBatchValidator.class.getName());
			if (configuration != null && metadata != null) {
				if (!configuration.isBuildEnabled(metadata) && !configuration.isManualEnabled(metadata))
					enabled = false;
			}
		}
		return enabled;
	}

	String fAdditionalContentTypesIDs[] = null;

	private IContentType[] fContentTypes = null;

	private JSPDirectiveValidator fJSPDirectiveValidator = new JSPDirectiveValidator(this);

	private JSPELValidator fJSPELValidator = new JSPELValidator(this);

	private IContentType fJSPFContentType = null;

	private JSPJavaValidator fJSPJavaValidator = new JSPJavaValidator(this);

	private JSPActionValidator fJSPActionValidator = new JSPActionValidator(this);

	void addDependsOn(IResource resource) {
		if(resource != null) {
			fDependsOn.add(resource);
		}
	}

	public void cleanup(IReporter reporter) {
		fJSPDirectiveValidator.cleanup(reporter);
		fJSPELValidator.cleanup(reporter);
		fJSPJavaValidator.cleanup(reporter);
		fJSPActionValidator.cleanup(reporter);
	}


	void doValidate(IValidationContext helper, IReporter reporter) throws ValidationException {
		String[] uris = helper.getURIs();
		if (uris.length > 0) {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IFile currentFile = null;
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				currentFile = wsRoot.getFile(new Path(uris[i]));
				if (currentFile != null && currentFile.exists()) {
					if (shouldValidate(currentFile) && fragmentCheck(currentFile)) {
						Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, currentFile.getFullPath().toString().substring(1));
						reporter.displaySubtask(this, message);
						validateFile(currentFile, reporter);
					}
					if (DEBUG)
						System.out.println("validating: [" + uris[i] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		else {
			// if uris[] length 0 -> validate() gets called for each project
			if (helper instanceof IWorkbenchContext) {
				IProject project = ((IWorkbenchContext) helper).getProject();

				Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, NLS.bind(JSPCoreMessages.JSPBatchValidator_0, project.getFullPath()));
				reporter.displaySubtask(this, message);

				JSPFileVisitor visitor = new JSPFileVisitor(reporter);
				try {
					// collect all jsp files for the project
					project.accept(visitor, IResource.DEPTH_INFINITE);
				}
				catch (CoreException e) {
					if (DEBUG)
						e.printStackTrace();
				}
				IFile[] files = visitor.getFiles();
				for (int i = 0; i < files.length && !reporter.isCancelled(); i++) {
					if (shouldValidate(files[i]) && fragmentCheck(files[i])) {

						message = new LocalizedMessage(IMessage.LOW_SEVERITY, files[i].getFullPath().toString().substring(1));
						reporter.displaySubtask(this, message);

						validateFile(files[i], reporter);
					}
					if (DEBUG)
						System.out.println("validating: [" + files[i] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
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
		boolean shouldValidate = true;
		// quick check to see if this is possibly a jsp fragment
		if (getJSPFContentType().isAssociatedWith(file.getName())) {
			// get preference for validate jsp fragments
			boolean shouldValidateFragments = FragmentValidationTools.shouldValidateFragment(file);
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
	 * Returns JSP fragment content type
	 * 
	 * @return jspf content type
	 */
	private IContentType getJSPFContentType() {
		if (fJSPFContentType == null) {
			fJSPFContentType = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
		}
		return fJSPFContentType;
	}

	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		if (helper instanceof IWorkbenchContext) {
			/*
			 * Use a single build rule when running batch validation.
			 */
			return ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
		}
		/*
		 * For other kinds of validation, use no specific rule
		 */
		return null;
	}

	/**
	 * Gets list of content types this visitor is interested in
	 * 
	 * @return All JSP-related content types
	 */
	private IContentType[] getValidContentTypes() {
		if (fContentTypes == null) {
			// currently "hard-coded" to be jsp & jspf
			fContentTypes = new IContentType[]{Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP), Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT)};
			if (fAdditionalContentTypesIDs != null) {
				List allTypes = new ArrayList(Arrays.asList(fContentTypes));
				for (int i = 0; i < fAdditionalContentTypesIDs.length; i++) {
					IContentType type = Platform.getContentTypeManager().getContentType(fAdditionalContentTypesIDs[i]);
					if (type != null) {
						allTypes.add(type);
					}
				}
				fContentTypes = (IContentType[]) allTypes.toArray(new IContentType[allTypes.size()]);
			}
		}
		return fContentTypes;
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
			/*
			 * must close input stream in case others need it
			 * (IFile.getContents() requirement as well)
			 */
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

	/**
	 * Checks if fileName is some type of JSP (including JSP fragments)
	 * 
	 * @param fileName
	 * @return true if filename indicates some type of JSP, false otherwise
	 */
	private boolean isJSPType(String fileName) {
		boolean valid = false;

		IContentType[] types = getValidContentTypes();
		int i = 0;
		while (i < types.length && !valid) {
			valid = types[i].isAssociatedWith(fileName);
			++i;
		}
		return valid;
	}

	private void performValidation(IFile f, IReporter reporter, IStructuredModel model) {
		if (!reporter.isCancelled())
			fJSPJavaValidator.performValidation(f, reporter, model);
		if (!reporter.isCancelled())
			fJSPDirectiveValidator.performValidation(f, reporter, model.getStructuredDocument());
		if (!reporter.isCancelled())
			fJSPELValidator.performValidation(f, reporter, model.getStructuredDocument());
		if (!reporter.isCancelled())
			fJSPActionValidator.performValidation(f, reporter, model);
	}

	/**
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		fAdditionalContentTypesIDs = new String[0];
		if (data != null) {
			if (data instanceof String && data.toString().length() > 0) {
				fAdditionalContentTypesIDs = StringUtils.unpack(data.toString());
			}
		}
	}

	private boolean shouldValidate(IFile file) {
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

	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		doValidate(helper, reporter);
	}

	/**
	 * Validate one file. It's assumed that the file has JSP content type.
	 * 
	 * @param f
	 * @param reporter
	 */
	void validateFile(IFile f, IReporter reporter) {
		try {
			f.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		IStructuredModel model = null;
		try {
			// get JSP model on behalf of all JSP validators
			model = StructuredModelManager.getModelManager().getModelForRead(f);
			if (!reporter.isCancelled() && model != null) {
				reporter.removeAllMessages(this, f);
				performValidation(f, reporter, model);
			}
		}
		catch (IOException e) {
			Logger.logException(e);
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	public IStatus validateInJob(final IValidationContext helper, final IReporter reporter) throws ValidationException {
		Job currentJob = Job.getJobManager().currentJob();
		ISchedulingRule rule = null;
		if (currentJob != null) {
			rule = currentJob.getRule();
		}
		IWorkspaceRunnable validationRunnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					doValidate(helper, reporter);
				}
				catch (ValidationException e) {
					throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID_JSP_CORE, 0, PLUGIN_ID_JSP_CORE, e));
				}
			}
		};
		try {
			JavaCore.run(validationRunnable, rule, new NullProgressMonitor());
		}
		catch (CoreException e) {
			if (e.getCause() instanceof ValidationException) {
				throw (ValidationException) e.getCause();
			}
			throw new ValidationException(new LocalizedMessage(IMessage.ERROR_AND_WARNING, e.getMessage()), e);
		}
		return Status.OK_STATUS;
	}

	public ValidationResult validate(final IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		final ValidationResult result = new ValidationResult();
		final IReporter reporter = result.getReporter(monitor);
		
		if(result.getDependsOn() != null) {
			fDependsOn = new HashSet(Arrays.asList(result.getDependsOn()));
		}
		else {
			fDependsOn = new HashSet();
		}
		
		// add web.xml as a dependency
		addDependsOn(DeploymentDescriptorPropertyCache.getInstance().getWebXML(resource.getFullPath()));

		// List relevant JSP 2.0 preludes/codas as dependencies
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		PropertyGroup[] propertyGroups = DeploymentDescriptorPropertyCache.getInstance().getPropertyGroups(resource.getFullPath());
		for (int j = 0; j < propertyGroups.length; j++) {
			IPath[] preludes = propertyGroups[j].getIncludePrelude();
			for (int i = 0; i < preludes.length; i++) {
				addDependsOn(workspaceRoot.getFile(preludes[i]));
			}
			IPath[] codas = propertyGroups[j].getIncludeCoda();
			for (int i = 0; i < codas.length; i++) {
				addDependsOn(workspaceRoot.getFile(codas[i]));
			}
		}
		
		IWorkspaceRunnable validationRunnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				if (fragmentCheck((IFile) resource)) {
					validateFile((IFile) resource, reporter);
				}
				IResource[] resources = (IResource[]) fDependsOn.toArray(new IResource[fDependsOn.size()]);
				result.setDependsOn(resources);
				fDependsOn.clear();
			}
		};
		Job currentJob = Job.getJobManager().currentJob();
		ISchedulingRule rule = null;
		if (currentJob != null) {
			rule = currentJob.getRule();
		}
		try {
			JavaCore.run(validationRunnable, rule, new NullProgressMonitor());
		}
		catch (CoreException e) {
			Logger.logException(e);
		}
		return result;
	}
}
