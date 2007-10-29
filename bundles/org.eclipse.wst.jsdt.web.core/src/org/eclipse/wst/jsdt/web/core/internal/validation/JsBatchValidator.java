/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.core.internal.validation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspace;
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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.jsdt.core.IClasspathAttribute;
import org.eclipse.wst.jsdt.core.IClasspathEntry;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.web.core.internal.JsCoreMessages;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;

/**
 * Performs JSP validation tasks for batch validation. The individual validator
 * classes will still be used for source validation.
 */
public final class JsBatchValidator implements IValidatorJob, IExecutableExtension {
	// for debugging
	static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption("org.eclipse.wst.jsdt.web.core/debug/jspvalidator")).booleanValue(); //$NON-NLS-1$
	private static final String PLUGIN_ID_JSP_CORE = "org.eclipse.wst.jsdt.web.core"; //$NON-NLS-1$
	private IPath[] excludeLibPaths;
	
	private final static String [] rhinoValidator = {"org.eclipse.atf.javascript.internal.validation.JSSyntaxValidator"};
      static { 
              // Temp code to clear Rhino Syntax validation markers.
              IWorkspace workspace = ResourcesPlugin.getWorkspace();
              IProject[] projects = workspace.getRoot().getProjects();
              for (int j = 0; j < projects.length; j++) {
                      IProject project = projects[j];
                      //try {
                              if (project.isOpen()) {
                                      try {
                                              if (project.hasNature(JavaCore.NATURE_ID)) {
                                                    WorkbenchReporter.removeAllMessages(project, rhinoValidator, null);
                                              }
                                      } catch (CoreException e) {
                                              // Do nothing
                                      }
                              }
              }
              
      } 
      
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
				} catch (InvocationTargetException e) {
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
		ProjectConfiguration configuration = JsBatchValidator.getProjectConfiguration(file);
		if (configuration != null) {
			org.eclipse.wst.validation.internal.ValidatorMetaData metadata = ValidationRegistryReader.getReader().getValidatorMetaData(JsBatchValidator.class.getName());
			if (configuration != null && metadata != null) {
				if (!configuration.isBuildEnabled(metadata) && !configuration.isManualEnabled(metadata)) {
					enabled = false;
				}
			}
		}
		return enabled;
	}
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
			if (fReporter.isCancelled()) {
				return false;
			}
			if (proxy.getType() == IResource.FILE) {
				if (Util.isJsType(proxy.getName()) && proxy.isAccessible()) {
					IFile file = (IFile) proxy.requestResource();
					if (JsBatchValidator.DEBUG) {
						System.out.println("(+) JSPValidator adding file: " + file.getName()); //$NON-NLS-1$
					}
					fFiles.add(file);
					// don't search deeper for files
					return false;
				}
			}
			return true;
		}
	}
	class LocalizedMessage extends Message {
		private String _message = null;
		
		public LocalizedMessage(int severity, String messageText) {
			this(severity, messageText, null);
		}
		
		public LocalizedMessage(int severity, String messageText, IResource targetObject) {
			this(severity, messageText, (Object) targetObject);
		}
		
		public LocalizedMessage(int severity, String messageText, Object targetObject) {
			super(null, severity, null);
			setLocalizedMessage(messageText);
			setTargetObject(targetObject);
		}
		
		private String getLocalizedText() {
			return _message;
		}
		
		
		public String getText() {
			return getLocalizedText();
		}
		
		
		public String getText(ClassLoader cl) {
			return getLocalizedText();
		}
		
		
		public String getText(Locale l) {
			return getLocalizedText();
		}
		
		
		public String getText(Locale l, ClassLoader cl) {
			return getLocalizedText();
		}
		
		public void setLocalizedMessage(String message) {
			_message = message;
		}
	}
	//String fAdditionalContentTypesIDs[] = null;
	private IContentType[] fContentTypes = null;
	private IContentType fJSPFContentType = null;
	private JsValidator fJSPJavaValidator = new JsValidator(this);
	
	public void cleanup(IReporter reporter) {
		fJSPJavaValidator.cleanup(reporter);
	}
	private IPath[] getLibraryPaths(IFile file) {
		
		if(excludeLibPaths!=null) return excludeLibPaths;
		
		IProject project = file.getProject();
		IJavaProject javaProject= JavaCore.create(project);
		
		if(javaProject==null) return new IPath[0];
		
		IClasspathEntry[] entries = new IClasspathEntry[0];
		try {
			entries = javaProject.getResolvedClasspath(true);
		} catch (JavaModelException ex) {
			// May run into an exception if the project isn't jsdt.
		}
		ArrayList ignorePaths = new ArrayList();
		nextEntry: for(int i = 0;i<entries.length;i++) {
			if(entries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				IClasspathAttribute[] attribs = entries[i].getExtraAttributes();
				for(int k=0; attribs!=null && k<attribs.length;k++) {
					if(attribs[k].getName().equalsIgnoreCase("validate") && attribs[k].getValue().equalsIgnoreCase("false")) {
						ignorePaths.add(entries[i].getPath());
						continue nextEntry;
					}
				}
			}
		}
		
		excludeLibPaths =  (Path[])ignorePaths.toArray(new Path[ignorePaths.size()]);
		return excludeLibPaths;
	}
	
	
	void doValidate(IValidationContext helper, IReporter reporter) throws ValidationException {
		
		String[] uris = helper.getURIs();
		if (uris.length > 0) {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IFile currentFile = null;
			
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				currentFile = wsRoot.getFile(new Path(uris[i]));
				if (currentFile != null && currentFile.exists()) {
					if (shouldValidate(currentFile) ) {
						Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, "" + (i + 1) + "/" + uris.length + " - " + currentFile.getFullPath().toString().substring(1));
						reporter.displaySubtask(this, message);
						reporter.removeAllMessages(this, currentFile);
						validateFile(currentFile, reporter);
					}
					if (JsBatchValidator.DEBUG) {
						System.out.println("validating: [" + uris[i] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		} else {
			// if uris[] length 0 -> validate() gets called for each project
			if (helper instanceof IWorkbenchContext) {
				IProject project = ((IWorkbenchContext) helper).getProject();
				Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, NLS.bind(JsCoreMessages.JSPBatchValidator_0, project.getFullPath()));
				reporter.displaySubtask(this, message);
				JSPFileVisitor visitor = new JSPFileVisitor(reporter);
				try {
					// collect all jsp files for the project
					project.accept(visitor, IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
					if (JsBatchValidator.DEBUG) {
						e.printStackTrace();
					}
				}
				IFile[] files = visitor.getFiles();
				for (int i = 0; i < files.length && !reporter.isCancelled(); i++) {
					if (shouldValidate(files[i]) ) {
						message = new LocalizedMessage(IMessage.LOW_SEVERITY, "" + (i + 1) + "/" + files.length + " - " + files[i].getFullPath().toString().substring(1));
						reporter.displaySubtask(this, message);
						validateFile(files[i], reporter);
					}
					if (JsBatchValidator.DEBUG) {
						System.out.println("validating: [" + files[i] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
	}
	
//	/**
//	 * Checks if file is a jsp fragment or not. If so, check if the fragment
//	 * should be validated or not.
//	 * 
//	 * @param file
//	 *            Assumes shouldValidate was already called on file so it should
//	 *            not be null and does exist
//	 * @return false if file is a fragment and it should not be validated, true
//	 *         otherwise
//	 */
//	private boolean fragmentCheck(IFile file) {
//		return isFragment(file);
//	}
	
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
	



	
	private void performValidation(IFile f, IReporter reporter, IStructuredModel model) {
		if (!reporter.isCancelled()) {
			fJSPJavaValidator.performValidation(f, reporter, model,true);
		}
	}
	
	/**
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		
	}
	
	private boolean shouldValidate(IFile file) {
		//if(true) return true;
		IResource resource = file;
		IPath[] libPaths = getLibraryPaths(file);
		IPath filePath = file.getFullPath().removeLastSegments(1);
		for(int i = 0;i<libPaths.length;i++) {
			if(libPaths[i].isPrefixOf(filePath)){
				return false;
			}
		}
		
		do {
			if (resource.isDerived() || resource.isTeamPrivateMember() || !resource.isAccessible() || resource.getName().charAt(0) == '.') {
				return false;
			}
			resource = resource.getParent();
		} while ((resource.getType() & IResource.PROJECT) == 0);
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
		IStructuredModel model = null;
		try {
			// get JSP model on behalf of all JSP validators
			model = StructuredModelManager.getModelManager().getExistingModelForRead(f);
			if(model==null) {
				model = StructuredModelManager.getModelManager().getModelForRead(f);
			}
			if (!reporter.isCancelled() && model != null) {
				reporter.removeAllMessages(this, f);
				performValidation(f, reporter, model);
			}
		} catch (IOException e) {
			Logger.logException(e);
		} catch (CoreException e) {
			Logger.logException(e);
		} finally {
			if (model != null) {
				model.releaseFromRead();
			}
		}
	}
	
	public IStatus validateInJob(final IValidationContext helper, final IReporter reporter) throws ValidationException {
		Job currentJob = Platform.getJobManager().currentJob();
		ISchedulingRule rule = null;
		if (currentJob != null) {
			rule = currentJob.getRule();
		}
		IWorkspaceRunnable validationRunnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					doValidate(helper, reporter);
				} catch (ValidationException e) {
					throw new CoreException(new Status(IStatus.ERROR, JsBatchValidator.PLUGIN_ID_JSP_CORE, 0, JsBatchValidator.PLUGIN_ID_JSP_CORE, e));
				}
			}
		};
		try {
			JavaCore.run(validationRunnable, rule, new NullProgressMonitor());
		} catch (CoreException e) {
			if (e.getCause() instanceof ValidationException) {
				throw (ValidationException) e.getCause();
			}
			throw new ValidationException(new LocalizedMessage(IMessage.ERROR_AND_WARNING, e.getMessage()), e);
		}
		return Status.OK_STATUS;
	}
}
