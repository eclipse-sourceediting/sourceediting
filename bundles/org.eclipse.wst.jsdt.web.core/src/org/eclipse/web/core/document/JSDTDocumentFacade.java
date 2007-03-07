package org.eclipse.web.core.document;



import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

public class JSDTDocumentFacade extends AbstractDocument implements IDocumentFacade {
   
    IDocument fMasterDoc=null;
    IDocument fSubDoc=null;
    
    private final String POSITION_CATEGORY = "org.eclipse.wst.HTML.SCRPIT";
    private final String ABSTRACT_POSITION_CATEGORY="org.eclipse.wst.HTML.SCRIPT.EVENT";
    
    public JSDTDocumentFacade(IDocument document){
        super();
        this.fMasterDoc=document;
        
        
        try {
            Position[] positions = fMasterDoc.getPositions(POSITION_CATEGORY);
            for(int i = 0;i<positions.length;i++){
                super.addPosition(positions[i]);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    
    private void handleAbstractPosition(Position masterPosition){
        // Surrounds the abstract position with 
        masterPosition.
    }
    
}
