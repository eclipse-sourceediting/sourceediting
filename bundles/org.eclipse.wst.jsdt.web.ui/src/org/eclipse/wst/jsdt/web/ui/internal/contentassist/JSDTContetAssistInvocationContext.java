package org.eclipse.wst.jsdt.web.ui.internal.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class JSDTContetAssistInvocationContext extends JavaContentAssistInvocationContext{
    
    
    
    ITextViewer viewer;
    
    
    private JSDTContetAssistInvocationContext(ITextViewer viewer, int offset, JSDTProposalCollector theCollector){
        
        super(viewer,offset,null);
        this.viewer=viewer;
        //this.offset=getJSPTranslation().getJavaOffset(offset);
        //CompletionProposalCollector theCollector = getProposalCollector();
        super.setCollector(theCollector);
   
    }
    
    public static JSDTContetAssistInvocationContext getInstance(ITextViewer viewer, int offset, JSDTProposalCollector theCollector){
        JSPTranslation tran = getJSPTranslation(viewer);
        int jsOffset = tran.getJsOffset(offset);
        return new JSDTContetAssistInvocationContext(viewer,offset,theCollector);
    }
  public IDocument getDocument() {
      return viewer.getDocument();
  
  }
    
//    public IDocument getDocument() {
//      return ((JSPTranslationExtension)getJSPTranslation(viewer)).getJavaDocument();
//      
//    }
    
//    protected CompletionProposalCollector getProposalCollector() {
//       
//        return ((CompletionProposalCollector) (  new JSPProposalCollector( getJSPTranslation())       ));
//    }
    
    private static JSPTranslation getJSPTranslation(ITextViewer viewer){
            JSPTranslation fTranslation = null;;
            
            IDOMModel xmlModel = null;
        
            try {
                xmlModel = (IDOMModel) StructuredModelManager.getModelManager()
                        .getExistingModelForRead(viewer.getDocument());
                
                IDOMDocument xmlDoc = xmlModel.getDocument();
                
                
                    JSPTranslationAdapter   fTranslationAdapter = (JSPTranslationAdapter) xmlDoc
                            .getAdapterFor(IJSPTranslation.class);
               
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
    
//    public ICompilationUnit getCompilationUnit() {
//        return getJSPTranslation(viewer).getCompilationUnit();
//    }
}
