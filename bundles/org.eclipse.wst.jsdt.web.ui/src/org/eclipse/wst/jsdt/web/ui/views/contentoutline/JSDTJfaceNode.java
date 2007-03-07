package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.html.core.internal.document.ElementStyleImpl;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IJavaModel;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.IOpenable;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.web.core.internal.domdocument.ElementImplForJSP;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.stylesheets.LinkStyle;

public class JSDTJfaceNode extends ElementStyleImpl implements IJavaElement,IDOMElement, ElementCSSInlineStyle, LinkStyle{
   
    private IJavaElement fParentElement;
    
    
    public JSDTJfaceNode(IJavaElement element){
        super();
        fParentElement = element;
       
        if(super.getAdapterFor(IJFaceNodeAdapter.class)==null){
            JFaceNodeAdapterFactoryForJSDT factory = new JFaceNodeAdapterFactoryForJSDT();
            super.addAdapter(factory.createAdapter(this));
        }
        //JFaceNodeAdapterFactoryForJSDT factory = new JFaceNodeAdapterFactoryForJSDT();
        
    }
    
    public boolean exists() {
       return fParentElement.exists();
    }

    public IJavaElement getAncestor(int ancestorType) {
        return fParentElement.getAncestor(ancestorType);
    }

    public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
        return fParentElement.getAttachedJavadoc(monitor);
    }

    public IResource getCorrespondingResource() throws JavaModelException {
       return fParentElement.getCorrespondingResource();
    }

    public String getElementName() {
       return fParentElement.getElementName();
    }

    public int getElementType() {
        return fParentElement.getElementType();
    }

    public String getHandleIdentifier() {
         return fParentElement.getHandleIdentifier();
    }

    public IJavaModel getJavaModel() {
        return fParentElement.getJavaModel();
    }

    public IJavaProject getJavaProject() {
        return fParentElement.getJavaProject();
    }

    public IOpenable getOpenable() {
       return fParentElement.getOpenable();
    }

    public IJavaElement getParent() {
       return fParentElement.getParent();
    }

    public IPath getPath() {
       return fParentElement.getPath();
    }

    public IJavaElement getPrimaryElement() {
        return fParentElement.getPrimaryElement();
    }

    public IResource getResource() {
       return fParentElement.getResource();
    }

    public ISchedulingRule getSchedulingRule() {
       return fParentElement.getSchedulingRule();
    }

    public IResource getUnderlyingResource() throws JavaModelException {
        return fParentElement.getUnderlyingResource();
    }

    public boolean isReadOnly() {
        return fParentElement.isReadOnly();
    }

    public boolean isStructureKnown() throws JavaModelException {
        return fParentElement.isStructureKnown();
    }

    public Object getAdapter(Class adapter) {
        return fParentElement.getAdapter(adapter);
    }
    
}
