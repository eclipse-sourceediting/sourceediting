package org.eclipse.web.core.document;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

public class ParentDocumentChangeListener implements IDocumentListener{

    public void documentAboutToBeChanged(DocumentEvent event) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method documentAboutToBeChanged" );
        
    }

    public void documentChanged(DocumentEvent event) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method documentChanged" );
        
    }
    
}
