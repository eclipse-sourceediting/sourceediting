package org.eclipse.wst.jsdt.web.ui.internal.contentassist;
import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.jsdt.core.CompletionProposal;
import org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

/**
 * 
 */
/**
 * @author childsb
 *
 */
public class JSDTHtmlCompletionProcessor {
	
	public JSDTHtmlCompletionProcessor() {}
	
	
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		/* add </script if necisary */
		ArrayList allProposals = new ArrayList();
		JsTranslation tran = getJSPTranslation(viewer);
		int missingAtOffset = tran.getMissingTagStart();
		
		return (ICompletionProposal[])allProposals.toArray(new ICompletionProposal[allProposals.size()]);
	}
	
	public ICompletionProposal getEndScriptProposal(ITextViewer viewer, int offset) {
		/* add </script if necisary */
		ArrayList allProposals = new ArrayList();
		JsTranslation tran = getJSPTranslation(viewer);
		int missingAtOffset = tran.getMissingTagStart();
		
		if(offset>=missingAtOffset&& missingAtOffset>-1) {
			String text = "\n</script>\n";
			String displayText = "<> end with </script>";
			return new CustomCompletionProposal(text,offset,0,offset,null,displayText,null,"Close the script tag.",100);
		}
		
		return null;
	}
	private JsTranslation getJSPTranslation(ITextViewer viewer) {
		IDOMModel xmlModel = null;
		try {
			xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
			IDOMDocument xmlDoc = xmlModel.getDocument();
			
			JsTranslationAdapter fTranslationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
			
			if (fTranslationAdapter != null) {
				return fTranslationAdapter.getJSPTranslation();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (xmlModel != null) {
				xmlModel.releaseFromRead();
			}
		}
		return null;
	}
	
}
