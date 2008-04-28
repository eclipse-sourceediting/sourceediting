package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;

import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSDTContetAssistInvocationContext extends JavaContentAssistInvocationContext {
	public static JSDTContetAssistInvocationContext getInstance(ITextViewer viewer, int offset, JSDTProposalCollector theCollector) {
		IJsTranslation tran = JSDTContetAssistInvocationContext.getJSPTranslation(viewer);
		return new JSDTContetAssistInvocationContext(viewer, offset, theCollector);
	}
	
	private static IJsTranslation getJSPTranslation(ITextViewer viewer) {
		IJsTranslation fTranslation = null;
		
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
			IDOMDocument xmlDoc = xmlModel.getDocument();
			JsTranslationAdapter fTranslationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			if (fTranslationAdapter != null) {
				fTranslation = fTranslationAdapter.getJSPTranslation(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return fTranslation;
	}
	ITextViewer viewer;
	
	private JSDTContetAssistInvocationContext(ITextViewer viewer, int offset, JSDTProposalCollector theCollector) {
		super(viewer, offset, null);
		this.viewer = viewer;
		// this.offset=getJSPTranslation().getJavaOffset(offset);
		// CompletionProposalCollector theCollector = getProposalCollector();
		super.setCollector(theCollector);
	}
	
// public IDocument getDocument() {
// return
// ((JSPTranslationExtension)getJSPTranslation(viewer)).getJavaDocument();
//      
// }
// protected CompletionProposalCollector getProposalCollector() {
//       
// return ((CompletionProposalCollector) ( new JSPProposalCollector(
// getJSPTranslation()) ));
// }
	
	public IDocument getDocument() {
		return viewer.getDocument();
	}
// public IJavaScriptUnit getCompilationUnit() {
// return getJSPTranslation(viewer).getCompilationUnit();
// }
}
