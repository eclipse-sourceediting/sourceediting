package org.eclipse.wst.jsdt.web.core.internal.contentmodel;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.filebuffers.BasicStructuredDocumentFactory;

public class JSDTtructuredDocumentFactory extends BasicStructuredDocumentFactory {

    /* (non-Javadoc)
     * @see org.eclipse.wst.sse.core.internal.filebuffers.BasicStructuredDocumentFactory#createDocument()
     */
    @Override
    public IDocument createDocument() {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method createDocument" );
        return super.createDocument();
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.sse.core.internal.filebuffers.BasicStructuredDocumentFactory#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
     */
    @Override
    public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method setInitializationData" );
        super.setInitializationData(config, propertyName, data);
    }
    
}
