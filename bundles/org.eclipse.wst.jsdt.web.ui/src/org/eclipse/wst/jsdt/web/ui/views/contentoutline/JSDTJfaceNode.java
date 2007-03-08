package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.Arrays;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.html.core.internal.document.ElementStyleImpl;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.IJavaModel;
import org.eclipse.wst.jsdt.core.IJavaProject;
import org.eclipse.wst.jsdt.core.IOpenable;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.web.core.internal.domdocument.ElementImplForJSP;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.stylesheets.LinkStyle;

public class JSDTJfaceNode extends ElementStyleImpl{
   
    
    Node fParent = null;
    IJavaElement fJSElement;
    Vector children = new Vector();
    boolean hasChildren = false;
    
    Position fDocPosition = null;
    
    public static boolean hasJsPart(Object o){
        return (o instanceof JSDTJfaceNode && ((JSDTJfaceNode)o).isJsNode() );
    }

    
    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.ElementImpl#getEndOffset()
     */
    @Override
    public int getEndOffset() {
        // TODO Auto-generated method stub
        if(isJsNode()){
            System.out.println("getEndOffset(): " + (fDocPosition.getOffset() + fDocPosition.getLength()));
            return fDocPosition.getOffset() + fDocPosition.getLength();
        }
        return super.getEndOffset();
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.ElementImpl#getEndStartOffset()
     */
    @Override
    public int getEndStartOffset() {
        // TODO Auto-generated method stub
       
        if(isJsNode()){
            System.out.println("getEndStartOffset(): " + getEndOffset());
            return getEndOffset();
        }
        return super.getEndOffset();
    }


 /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.ElementImpl#getStartEndOffset()
     */
    @Override
    public int getStartEndOffset() {
        // TODO Auto-generated method stub
       
        if(isJsNode()){
            System.out.println("getStartEndOffset(): " + fDocPosition.getOffset());
            return fDocPosition.getOffset();
        }
        return super.getStartEndOffset();
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.ElementImpl#getStartOffset()
     */
    @Override
    public int getStartOffset() {
        
        if(isJsNode()){
            System.out.println("getStartOffset(): " + fDocPosition.getOffset());
            return fDocPosition.getOffset();
        }
        return super.getStartOffset();
    }

    
    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.ElementImpl#isContainer()
     */
    @Override
    public boolean isContainer() {
        // TODO Auto-generated method stub
        return super.isContainer();
        
    }

    public JSDTJfaceNode(Node parent, Node oldNode, JSDTJfaceNode[] children){
        this(parent instanceof ElementImpl ? (ElementImpl)parent: new ElementStyleImpl() , children);
        
       
    }
    
 
    private JSDTJfaceNode(ElementImpl parent, JSDTJfaceNode[] children ){
        super(parent);
        //super.removeChildNodes();
        setParentNode(parent);
       
       // parent.appendChild(this);
        //fParent = parent;
        if(children!=null){
            for(int i = 0;i<children.length;i++){
                super.appendChild(children[i]);
            }
        }
     }
    
    public JSDTJfaceNode(Node parent, JSDTJfaceNode[] children){
        this(parent instanceof ElementImpl ? (ElementImpl)parent: new ElementStyleImpl() , children);
    }
    
    public JSDTJfaceNode(Node parent){
        this(parent,null);
        
    }
  

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.ElementImpl#getNodeType()
     */
    @Override
    public short getNodeType() {
       return Node.TEXT_NODE;
    }

    public void setJsPart(IJavaElement JSElement, Position docPosition){
        fJSElement = JSElement;
        fDocPosition = docPosition;
    }
    
    public IJavaElement getJsPart(){
        return fJSElement;
    }
    
    public boolean isJsNode(){
        return fJSElement!=null || hasChildNodes();
    }
    
    
 }
