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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.JSPCorePlugin;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.preferences.JSPCorePreferenceNames;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSPValidator implements IValidator {

	static private class LocalizedMessage extends Message {
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

	static boolean shouldValidate(IFile file) {
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

	private class JSPFileVisitor implements IResourceProxyVisitor {

		private List fFiles = new ArrayList();
		private IContentType fContentTypeJSP = null;
		private IReporter fReporter = null;

		public JSPFileVisitor(IReporter reporter) {
			fReporter = reporter;
		}

		public boolean visit(IResourceProxy proxy) throws CoreException {

			// check validation
			if (fReporter.isCancelled())
				return false;

			if (proxy.getType() == IResource.FILE) {

				if (getJspContentType().isAssociatedWith(proxy.getName())) {
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

		private IContentType getJspContentType() {
			if (fContentTypeJSP == null)
				fContentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSP);
			return fContentTypeJSP;
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
				currentFile = wsRoot.getFile(new Path(uris[i]));
				if (currentFile != null && currentFile.exists()) {
					validateFile(currentFile, reporter);
					if (DEBUG)
						System.out.println("validating: [" + uris[i] + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		else {
			
			// if uris[] length 0 -> validate() gets called for each project
			if(helper instanceof IWorkbenchContext) {
				
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
					validateFile(files[i], reporter);
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
	private void validateFile(IFile f, IReporter reporter) {
		if (!shouldValidate(f)) {
			return;
		}
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=87351
		if (!shouldValidate2(f)) {
			return;
		}

	    Message message = new LocalizedMessage(IMessage.LOW_SEVERITY, NLS.bind(JSPCoreMessages.MESSAGE_JSP_VALIDATING_MESSAGE_UI_, new String[]{f.getFullPath().toString()}));
	    reporter.displaySubtask(this, message);
		
		IDOMModel model = null;
		try {
			// get jsp model, get tranlsation
			model = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(f);
			if (model != null) {

				setupAdapterFactory(model);
				IDOMDocument xmlDoc = model.getDocument();
				JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
				JSPTranslation translation = translationAdapter.getJSPTranslation();

				translation.setProblemCollectingActive(true);
				translation.reconcileCompilationUnit();
				List problems = translation.getProblems();
				// remove old messages
				reporter.removeAllMessages(this, f);
				// add new messages
				for (int i = 0; i < problems.size() && !reporter.isCancelled(); i++) {
					IMessage m = createMessageFromProblem((IProblem) problems.get(i), f, translation, model.getStructuredDocument());
					if (m != null)
						reporter.addMessage(this, m);
				}
			}
		}
		catch (IOException e) {
			if (DEBUG)
				e.printStackTrace();
		}
		catch (CoreException e) {
			if (DEBUG)
				e.printStackTrace();
		}
		finally {
			if (model != null)
				model.releaseFromRead();
		}
	}

	/**
	 * Creates an IMessage from an IProblem
	 * 
	 * @param problem
	 * @param f
	 * @param translation
	 * @param structuredDoc
	 * @return message representation of the problem, or null if it could not
	 *         create one
	 */
	private IMessage createMessageFromProblem(IProblem problem, IFile f, JSPTranslation translation, IStructuredDocument structuredDoc) {

		int sourceStart = translation.getJspOffset(problem.getSourceStart());
		int sourceEnd = translation.getJspOffset(problem.getSourceEnd());
		if (sourceStart == -1)
			return null;
		// line number for marker starts @ 1
		// line number from document starts @ 0
		int lineNo = structuredDoc.getLineOfOffset(sourceStart) + 1;

		int sev = problem.isError() ? IMessage.HIGH_SEVERITY : IMessage.NORMAL_SEVERITY;

		IMessage m = new LocalizedMessage(sev, problem.getMessage(), f);

		m.setLineNo(lineNo);
		m.setOffset(sourceStart);
		m.setLength(sourceEnd - sourceStart + 1);

		return m;
	}

	/**
	 * When loading model from a file, you need to explicitly add adapter
	 * factory.
	 * 
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {
		JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
		sm.getFactoryRegistry().addFactory(factory);
	}

	/**
	 * Determines if file is jsp fragment or not
	 * 
	 * @param file
	 *            assumes file is not null and exists
	 * @return true if file is jsp fragment, false otherwise
	 */
	private boolean isFragment(IFile file) {
		boolean isFragment = false;
		InputStream is = null;
		try {
			IContentType contentTypeJSP = Platform.getContentTypeManager().getContentType(ContentTypeIdForJSP.ContentTypeID_JSPFRAGMENT);
			// check this before description, it's less expensive
			if (contentTypeJSP.isAssociatedWith(file.getName())) {

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

	/**
	 * Performs extra checks on the file to see if file should really be
	 * validated.
	 * 
	 * @param file
	 *            Assumes shouldValidate was already called on file so it
	 *            should not be null and does exist
	 * @return true if should validate file, false otherwise
	 */
	private boolean shouldValidate2(IFile file) {
		// get preference for validate jsp fragments
		boolean shouldValidate = Platform.getPreferencesService().getBoolean(JSPCorePlugin.getDefault().getBundle().getSymbolicName(), JSPCorePreferenceNames.VALIDATE_FRAGMENTS, true, null);

		/*
		 * if jsp fragments should not be validated, check if file is jsp
		 * fragment
		 */
		if (!shouldValidate) {
			boolean isFragment = isFragment(file);
			shouldValidate = !isFragment;
		}

		return shouldValidate;
	}
}