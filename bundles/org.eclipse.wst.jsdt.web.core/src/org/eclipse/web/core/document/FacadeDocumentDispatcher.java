/*
 * Given a structured/partitioned document this factory hands
 * out IDocument compadible objects that map / represent a subset
 * of the partitioned documents content.
 * 
 * These IDocument objects can then be used by classes that don't
 * understand structured documents or dont expected mixed content
 * 
 */
package org.eclipse.web.core.document;

import java.util.Hashtable;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;


public class FacadeDocumentDispatcher implements IDocumentListener{
    
    private ParentDocumentChangeListener fPdocChangeListener;
    
    private  IDocument masterDocument;
    
    Hashtable dispatchedDocuments;
    
    private FacadeDocumentDispatcher(){
        
    }
    
    public FacadeDocumentDispatcher(IDocument document){
        fPdocChangeListener = new ParentDocumentChangeListener();
        document.addDocumentListener(fPdocChangeListener);
        this.masterDocument=document;
    }
    
    public IDocument getDocumentFor(String partitionType){
        
        
        return masterDocument;
    }

    public void documentAboutToBeChanged(DocumentEvent event) {
        // TODO Auto-generated method stub
        int docChangedAtOffset = event.fOffset;
        int docChangedElementLength = event.getLength();
        
        
    }

    public void documentChanged(DocumentEvent event) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method documentChanged" );
        
    }
    
}
