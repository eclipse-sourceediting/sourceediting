package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.ContentTypeIdForJSP;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSPValidator implements IValidator {
	
	// for debugging
	private static final boolean DEBUG;
	static {
		String value= Platform.getDebugOption("org.eclipse.jst.jsp.core/debug/jspvalidator"); //$NON-NLS-1$
		DEBUG= value != null && value.equalsIgnoreCase("true"); //$NON-NLS-1$
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
				
				if(getJspContentType().isAssociatedWith(proxy.getName())) {
					IFile file = (IFile) proxy.requestResource();
					if(file.exists()) {
						
						if(DEBUG)
							System.out.println("(+) JSPValidator adding file: " + file.getName()); //$NON-NLS-1$
						// this call will check the ContentTypeDescription, so don't need to do it here.
						//JSPSearchSupport.getInstance().addJspFile(file);
						fFiles.add(file);
	
						// don't search deeper for files
						return false;
					}
				}
			}
			return true;
		}
		
		public final IFile[] getFiles() {
		    return (IFile[])fFiles.toArray(new IFile[this.fFiles.size()]);
		}
		
		IContentType getJspContentType() {
			if(fContentTypeJSP == null)
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
		if(uris.length > 0) {
			IFile currentFile = null;
			for (int i = 0; i < uris.length; i++) {
				currentFile = wsRoot.getFile(new Path(uris[i]));
				if(currentFile != null && currentFile.exists()) {
					validateFile(currentFile, reporter);
					if(DEBUG)
						System.out.println("validating: ["+ uris[i]+"]");
				}
			}
		}
		else {
			// it's an entire workspace "clean"
		    JSPFileVisitor visitor = new JSPFileVisitor(reporter);
			try {
				//  collect all jsp files
				ResourcesPlugin.getWorkspace().getRoot().accept(visitor, IResource.DEPTH_INFINITE);
			}
			catch (CoreException e) {
				if(DEBUG)
					e.printStackTrace();
			}
			IFile[] files = visitor.getFiles();
			for (int i = 0; i < files.length; i++) {
				validateFile(files[i], reporter);
				if(DEBUG)
					System.out.println("validating: ["+ files[i]+"]");
			}
		}
	}

	/**
	 * Validate one file. It's assumed that the file has JSP content type.
	 * @param f
	 * @param reporter
	 */
	private void validateFile(IFile f, IReporter reporter) {
		
		IDOMModel model = null;
		try {
			// get jsp model, get tranlsation
			model = (IDOMModel)StructuredModelManager.getModelManager().getModelForRead(f);
			if(model != null) {
				
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
				for (int i = 0; i < problems.size(); i++) {
					IMessage m = createMessageFromProblem((IProblem)problems.get(i), f, translation, model.getStructuredDocument());
					if(m != null)
						reporter.addMessage(this, m);
				}
			}
		}
		catch (IOException e) {
			if(DEBUG)
				e.printStackTrace();
		}
		catch (CoreException e) {
			if(DEBUG)
				e.printStackTrace();
		}
		finally {
			if(model != null)
				model.releaseFromRead();
		}
	}
	
	/**
	 * Creates an IMessage from an IProblem
	 * @param problem
	 * @param f
	 * @param translation
	 * @param structuredDoc
	 * @return message representation of the problem, or null if it could not create one
	 */
	private IMessage createMessageFromProblem(IProblem problem, IFile f, JSPTranslation translation, IStructuredDocument structuredDoc) {
		
		int sourceStart = translation.getJspOffset(problem.getSourceStart());
		int sourceEnd = translation.getJspOffset(problem.getSourceStart());
		if(sourceStart == -1)
			return null;
		// line number for marker starts @ 1
		// line number from document starts @ 0
		int lineNo = structuredDoc.getLineOfOffset(sourceStart) +1;
		
		int sev = problem.isError() ? IMessage.HIGH_SEVERITY : IMessage.LOW_SEVERITY;
		
		IMessage m = new LocalizedMessage(sev, problem.getMessage(), f);

		m.setLineNo(lineNo);
		m.setOffset(sourceStart);
		m.setLength(sourceEnd - sourceStart);
		
		return m;
	}

	/**
	 * When loading model from a file, you need to explicitly add adapter factory.
	 * @param sm
	 */
	private void setupAdapterFactory(IStructuredModel sm) {
		JSPTranslationAdapterFactory factory = new JSPTranslationAdapterFactory();
		sm.getFactoryRegistry().addFactory(factory);
	}
}