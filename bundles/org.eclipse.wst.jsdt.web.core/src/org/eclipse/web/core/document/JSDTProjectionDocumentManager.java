package org.eclipse.web.core.document;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.projection.ProjectionDocument;
import org.eclipse.jface.text.projection.ProjectionDocumentManager;

public class JSDTProjectionDocumentManager extends ProjectionDocumentManager{

    /* (non-Javadoc)
     * @see org.eclipse.jface.text.projection.ProjectionDocumentManager#createProjectionDocument(org.eclipse.jface.text.IDocument)
     */
    @Override
    protected ProjectionDocument createProjectionDocument(IDocument master) {
        ProjectionDocument doc = new ProjectionDocument(master);
        
        
        

        return super.createProjectionDocument(master);
    }
    
    
    
}
