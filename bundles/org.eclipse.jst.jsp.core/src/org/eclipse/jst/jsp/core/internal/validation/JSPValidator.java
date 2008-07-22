/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.contentproperties.JSPFContentProperties;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionCollection;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * Performs some common JSP validation tasks
 */
public class JSPValidator extends AbstractValidator implements IValidatorJob {

	private static final String PLUGIN_ID_JSP_CORE = "org.eclipse.jst.jsp.core"; //$NON-NLS-1$
	private IContentType fJSPFContentType = null;

	protected class LocalizedMessage extends Message {

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

		public void setLocalizedMessage(String message) {
			_message = message;
		}

		public String getLocalizedMessage() {
			return _message;
		}

		public String getText() {
			return getLocalizedMessage();
		}

		public String getText(ClassLoader cl) {
			return getLocalizedMessage();
		}

		public String getText(Locale l) {
			return getLocalizedMessage();
		}

		public String getText(Locale l, ClassLoader cl) {
			return getLocalizedMessage();
		}
	}

	protected class JSPFileVisitor implements IResourceProxyVisitor {

		private List fFiles = new ArrayList();
		private IContentType[] fContentTypes = null;
		private IReporter fReporter = null;

		public JSPFileVisitor(IReporter reporter) {
			fReporter = reporter;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {

			// check validation
			if (fReporter.isCancelled())
				return false;

			if (proxy.getType() == IResource.FILE) {

				if (isJSPType(proxy.getName())) {
					IFile file = (IFile) proxy.requestResource();
					if (file.exists()) {

						if (DEBUG)
							System.out.println("(+) JSPValidator adding file: " + file.getName()); //$NON-NLS-1$
						fFiles.add(file);

						// don't search deeper for files
						return false;
					}
				}
			}
			return true;
		}

		public final IFile[] getFiles() {
			return (IFile[]) fFiles.toArray(new IFile[fFiles.size()]);
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
			}
			return fContentTypes;
		}

		/**
		 * Checks if fileName is some type of JSP (including JSP fragments)
		 * 
		 * @param fileName
		 * @return true if filename indicates some type of JSP, false
		 *         otherwise
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
	}

	public void cleanup(IReporter reporter) {
		// nothing to do
	}

	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		String[] uris = helper.getURIs();
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		if (uris.length > 0) {
			IFile currentFile = null;

			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				// might be called with just project path?
				currentFile = wsRoot.getFile(new Path(uris[i]));
				if (currentFile != null && currentFile.exists()) {
					if (shouldValidate(currentFile) && fragmentCheck(currentFile)) {

						Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, uris[i]);
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
						int percent = (i * 100) / files.length + 1;
						Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, percent + "% " + files[i].getFullPath().toString());
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
	 * Validate one file. It's assumed that the file has JSP content type.
	 * 
	 * @param f
	 * @param reporter
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		// subclasses should implement (for batch validation)
	}

	public ValidationResult validate(final IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE)
			return null;
		ValidationResult result = new ValidationResult();
		final IReporter reporter = result.getReporter(monitor);
		validateFile((IFile) resource, reporter);
		return result;
	}

	/**
	 * 
	 * @param collection
	 * @return the jsp directive name
	 */
	protected String getDirectiveName(ITextRegionCollection collection) {
		String name = ""; //$NON-NLS-1$
		ITextRegionList subRegions = collection.getRegions();
		for (int j = 0; j < subRegions.size(); j++) {
			ITextRegion subRegion = subRegions.get(j);
			if (subRegion.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				name = collection.getText(subRegion);
				break;
			}
		}
		return name;
	}

	/**
	 * 
	 * @param sdr
	 * @param attrName
	 * @return the ITextRegion for the attribute value of the given attribute
	 *         name, case sensitive, null if no matching attribute is found
	 */
	protected ITextRegion getAttributeValueRegion(ITextRegionCollection sdr, String attrName) {
		ITextRegion valueRegion = null;
		ITextRegionList subRegions = sdr.getRegions();
		for (int i = 0; i < subRegions.size(); i++) {
			ITextRegion subRegion = subRegions.get(i);
			if (subRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME && sdr.getText(subRegion).equals(attrName)) {
				for (int j = i; j < subRegions.size(); j++) {
					subRegion = subRegions.get(j);
					if (subRegion.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						valueRegion = subRegion;
						break;
					}
				}
				break;
			}
		}
		return valueRegion;
	}

	protected String getAttributeValue(ITextRegionCollection sdr, String attrName) {
		ITextRegion r = getAttributeValueRegion(sdr, attrName);
		if (r != null)
			return sdr.getText(r).trim();
		return ""; //$NON-NLS-1$
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

	// for debugging
	static final boolean DEBUG;
	static {
		String value = Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator"); //$NON-NLS-1$
		DEBUG = value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
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
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter) throws ValidationException {
		IStatus status = Status.OK_STATUS;
		try {
			validate(helper, reporter);
		}
		catch (ValidationException e) {
			Logger.logException(e);
			status = new Status(IStatus.ERROR, PLUGIN_ID_JSP_CORE, IStatus.ERROR, e.getLocalizedMessage(), e);
		}
		return status;
	}
}