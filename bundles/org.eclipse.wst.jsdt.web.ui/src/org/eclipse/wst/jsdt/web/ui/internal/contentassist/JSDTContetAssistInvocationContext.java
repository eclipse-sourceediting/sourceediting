package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationExtension;
import org.eclipse.wst.jsdt.web.ui.internal.contentassist.JSDTProposalCollector;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSDTContetAssistInvocationContext extends JavaContentAssistInvocationContext{
    
    JSPTranslationAdapter fTranslationAdapter;
    JSPTranslation fTranslation;
    ITextViewer viewer;
    int offset;
    
    public JSDTContetAssistInvocationContext(ITextViewer viewer, int offset, JSDTProposalCollector theCollector){
        
        super(viewer,offset,null);
        this.viewer=viewer;
        this.offset=getJSPTranslation().getJavaOffset(offset);
        //CompletionProposalCollector theCollector = getProposalCollector();
        super.setCollector(theCollector);
   
    }
    
    public IDocument getDocument() {
      return ((JSPTranslationExtension)fTranslation).getJavaDocument();
      
    }
    
//    protected CompletionProposalCollector getProposalCollector() {
//       
//        return ((CompletionProposalCollector) (  new JSPProposalCollector( getJSPTranslation())       ));
//    }
    
    private JSPTranslation getJSPTranslation(){
            if(fTranslation != null ) return fTranslation;   
        
            IDOMModel xmlModel = null;
        
            try {
                xmlModel = (IDOMModel) StructuredModelManager.getModelManager()
                        .getExistingModelForRead(viewer.getDocument());
                
                IDOMDocument xmlDoc = xmlModel.getDocument();
                
                if (fTranslationAdapter == null) {
                    fTranslationAdapter = (JSPTranslationAdapter) xmlDoc
                            .getAdapterFor(IJSPTranslation.class);
                }
                if (fTranslationAdapter != null) {
                    
                    fTranslation =  fTranslationAdapter.getJSPTranslation();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                if (xmlModel != null) {
                    xmlModel.releaseFromRead();
                }
            }
            return fTranslation;
    }
    
    public ICompilationUnit getCompilationUnit() {
        return getJSPTranslation().getCompilationUnit();
    }
}
