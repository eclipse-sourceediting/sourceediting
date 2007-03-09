package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.internal.ui.text.correction.AssistContext;
import org.eclipse.wst.jsdt.internal.ui.text.java.TemplateCompletionProposalComputer;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSDTTemplateAssistProcessor {
 
    TemplateCompletionProposalComputer fJavaTemplateCompletion;
    JSPTranslationAdapter fTranslationAdapter;
    IProgressMonitor monitor;
    JSDTProposalCollector fProposalCollector;
    
    public JSDTTemplateAssistProcessor(){
        monitor = new NullProgressMonitor();
        
    }
    
    private TemplateCompletionProposalComputer getTemplateCompletionProposalComputer(){
        if(fJavaTemplateCompletion==null){
            fJavaTemplateCompletion = new TemplateCompletionProposalComputer();
        }
        return fJavaTemplateCompletion;
    }
    
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        JavaContentAssistInvocationContext context = getInvocationContext(viewer, offset);
        List props = getTemplateCompletionProposalComputer().computeCompletionProposals(context, monitor);
        
        return (ICompletionProposal[])props.toArray(new ICompletionProposal[]{});
        
    }
    
    private JavaContentAssistInvocationContext getInvocationContext(ITextViewer viewer, int offset){
        return JSDTContetAssistInvocationContext.getInstance(viewer,offset,getProposalCollector());
    }
    
    protected JSDTProposalCollector getProposalCollector() {
        return fProposalCollector;
       // return new JSPProposalCollector(translation);
    }
    
    public void setProposalCollector(JSDTProposalCollector translation){
        this.fProposalCollector = translation;
    }

    
}
