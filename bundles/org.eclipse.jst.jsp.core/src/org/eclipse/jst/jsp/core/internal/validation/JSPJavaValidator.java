package org.eclipse.jst.jsp.core.internal.validation;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jst.jsp.core.internal.JSPCoreMessages;
import org.eclipse.jst.jsp.core.internal.Logger;
import org.eclipse.jst.jsp.core.internal.java.IJSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapter;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationAdapterFactory;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslationExtension;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSPJavaValidator extends JSPValidator {
	
	/**
	 * Validate one file. It's assumed that the file has JSP content type.
	 * 
	 * @param f
	 * @param reporter
	 */
	protected void validateFile(IFile f, IReporter reporter) {
		
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

		// need additional adjustment for problems from
		// indirect (included) files
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=119633
		if(translation.isIndirect(problem.getSourceStart())) {
			adjustIndirectPosition(m, translation);
		}
		
		return m;
	}

	/**
	 * Assumed the message offset is an indirect position.
	 * In other words, an error from an included file.
	 * 
	 * @param m
	 * @param translation
	 */
	private void adjustIndirectPosition(IMessage m, JSPTranslation translation) {
		
		if(!(translation instanceof JSPTranslationExtension))
			return;
		
		IDocument jspDoc = ((JSPTranslationExtension)translation).getJspDocument();
		if(!(jspDoc instanceof IStructuredDocument))
			return;
		
		IStructuredDocument sDoc = (IStructuredDocument)jspDoc;
		IStructuredDocumentRegion[] regions = sDoc.getStructuredDocumentRegions(0, m.getOffset() + m.getLength());
		// iterate backwards until you hit the include directive
		for(int i=regions.length-1; i>=0; i--) {
			
			IStructuredDocumentRegion region = regions[i];
			if(region.getType() == DOMJSPRegionContexts.JSP_DIRECTIVE_NAME) {
				if(getDirectiveName(region).equals("include")) { //$NON-NLS-1$
					ITextRegion fileValueRegion = getAttributeValueRegion(region, "file"); //$NON-NLS-1$
					m.setOffset(region.getStartOffset(fileValueRegion));
					m.setLength(fileValueRegion.getTextLength());
					break;
				}
			}
		}
	}
}
