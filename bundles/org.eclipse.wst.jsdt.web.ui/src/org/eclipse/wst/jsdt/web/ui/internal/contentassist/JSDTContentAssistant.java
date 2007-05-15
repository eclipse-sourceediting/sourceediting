package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import java.util.Arrays;
import java.util.Vector;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.contentassist.AbstractContentAssistProcessor;

public class JSDTContentAssistant extends AbstractContentAssistProcessor {
    
    private JSDTContentAssistantProcessor fContentAssistProcessor;
   
    private JSDTTemplateAssistProcessor   fTemplateAssistProcessor;
    
    private JSPTranslationAdapter fTranslationAdapter ;
   
    
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
            int documentPosition) {
        
        Vector proposals = new Vector();
        
        ICompletionProposal[] completionProposals;
        
        JSDTProposalCollector theCollector =  getProposalCollector(viewer, documentPosition);
        /* --------- Content Assistant --------- */
         getContentAssistProcessor().setProposalCollector(theCollector);
         completionProposals = getContentAssistProcessor().computeCompletionProposals(viewer, documentPosition);
         proposals.addAll(Arrays.asList(completionProposals));
         
         
        /* --------- template completions --------- */
        getTemplateCompletionProcessor().setProposalCollector(theCollector);
        completionProposals = getTemplateCompletionProcessor().computeCompletionProposals(viewer, documentPosition);
        proposals.addAll(Arrays.asList(completionProposals));
     

        return (ICompletionProposal[]) proposals
                .toArray(new ICompletionProposal[0]);
    }
    
    protected JSDTProposalCollector getProposalCollector(ITextViewer viewer, int offset) {
        JSPTranslation tran =  getJSPTranslation(viewer,offset);
        
        return  new JSDTProposalCollector( tran );
    }
    
    private JSPTranslation getJSPTranslation(ITextViewer viewer, int offset){
        
    
        IDOMModel xmlModel = null;
    
      
        
        try {
        	xmlModel = (IDOMModel) StructuredModelManager.getModelManager().getExistingModelForRead(viewer.getDocument());
        	IDOMDocument xmlDoc = xmlModel.getDocument();
            
            if (fTranslationAdapter == null) {
                fTranslationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
            }
            if (fTranslationAdapter != null) {
                
                return fTranslationAdapter.getJSPTranslation();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if (xmlModel != null) {
                xmlModel.releaseFromRead();
            }
        }
        return null;
}
    
    private JSDTContentAssistantProcessor getContentAssistProcessor() {
        if (fContentAssistProcessor == null) {
            fContentAssistProcessor = new JSDTContentAssistantProcessor();
        }
        
        return fContentAssistProcessor;
    }
    
    private JSDTTemplateAssistProcessor getTemplateCompletionProcessor() {
        if (fTemplateAssistProcessor == null) {
            fTemplateAssistProcessor = new JSDTTemplateAssistProcessor();
        }
      
        return fTemplateAssistProcessor;
    }
    
}
