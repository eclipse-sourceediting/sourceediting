package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;
import org.eclipse.wst.jsdt.internal.core.JavaElement;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.internal.core.util.MementoTokenizer;

public class JsElement extends SourceRefElement{
    
    IJavaElement enclosedType;
    
    public JsElement(IJavaElement enclosedType){
        super((JavaElement)enclosedType);
        this.enclosedType = enclosedType;
    }
    
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.PlatformObject#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter(Class adapter) {
        
        System.out.println("Umiplement method getAdapter" );
        return enclosedType.getAdapter(adapter);
    }


    @Override
    protected char getHandleMementoDelimiter() {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method getHandleMementoDelimiter" );
        return 0;
    }

    public int getElementType() {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method getElementType" );
        return 0;
    }

   
    
    
}
