package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;

public class JSDTElementContentProvider extends StandardJavaElementContentProvider {

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#exists(java.lang.Object)
     */
    ITreeContentProvider fParentContentProvider=null;
    
    public  JSDTElementContentProvider(IContentProvider parentContentProvider){
        if(parentContentProvider instanceof ITreeContentProvider) fParentContentProvider =  (ITreeContentProvider)parentContentProvider;
        
        
    }

    public final static Object[] NO_PARENT = new Object[0];
    
    


    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object element) {
       
        System.out.println("Umiplement method getChildren" );
        //if(element instanceof IJavaElement) 
        Object children[] = super.getChildren(element);
        if(children==NO_CHILDREN) {
            return fParentContentProvider.getChildren(element);
        }else{
            return children;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object parent) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method getElements" );
        //return super.getElements(parent);
        if(parent instanceof IJavaElement) super.getElements(parent);
        
        return fParentContentProvider.getElements(parent);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method getParent" );
        if(element instanceof IJavaElement) return super.getParent(element);
        if(element instanceof IDOMElement) return ((IDOMElement)element).getParentNode();
        return fParentContentProvider.getParent(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method hasChildren" );
        if(element instanceof IJavaElement) return super.hasChildren(element);
        if(element instanceof IDOMElement) return ((IDOMElement)element).hasChildNodes();
        return fParentContentProvider.hasChildren(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub
        System.out.println("Umiplement method inputChanged" );
        super.inputChanged(viewer, oldInput, newInput);
        if(oldInput instanceof IJavaElement){
            if(newInput instanceof IJavaElement){
                super.inputChanged(viewer, oldInput, newInput);
            }else{
                super.inputChanged(viewer, oldInput, NO_PARENT);
            }
        }
        fParentContentProvider.inputChanged(viewer, oldInput, newInput);
    }
    
}
