package org.eclipse.web.core.document;

import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.ITextStore;
import org.eclipse.jface.text.projection.FragmentUpdater;
import org.eclipse.jface.text.projection.ProjectionDocument;
import org.eclipse.jface.text.projection.ProjectionMapping;
import org.eclipse.jface.text.projection.ProjectionTextStore;

public class JSDTProjectionDocument extends ProjectionDocument{
   
    
    public JSDTProjectionDocument(IDocument masterDocument) {
        super(masterDocument);
        fMapping= new ProjectionMapping(masterDocument, fFragmentsCategory, this, fSegmentsCategory);
        
        /* need to adjust the Mapper to use our own */
        
        
    }
}
