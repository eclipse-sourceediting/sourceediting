package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;

public class JSDTLabelElementProvider extends JavaElementLabelProvider {
    
    ILabelProvider fParentLabelProvider;
    
    public JSDTLabelElementProvider(ILabelProvider parentLabelProvider){
        fParentLabelProvider = parentLabelProvider;
        
    }
    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.JavaElementLabelProvider#getImage(java.lang.Object)
     */
    @Override
    public Image getImage(Object element) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method JSDTLabelElementProvider.getImage" );
        if(element instanceof IJavaElement) return super.getImage(element);
        return fParentLabelProvider.getImage(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.JavaElementLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method JSDTLabelElementProvider.getText" );
        if(element instanceof IJavaElement) return super.getText(element);
        return fParentLabelProvider.getText(element);
    }

   
    
    
}
