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
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.ElementCSSInlineStyle;
import org.w3c.dom.stylesheets.LinkStyle;

public class JSDTJfaceNode extends ElementStyleImpl implements IJavaElement{
   
    
    Node fParent = null;
    IJavaElement fJSElement;
    
    boolean hasChildren = false;
    
    Position fDocPosition = null;
    
    private short nodeType = Node.TEXT_NODE;
    
    Vector children = new Vector();
    
    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeImpl#getCommonAncestor(org.w3c.dom.Node)
     */
    @Override
    public Node getCommonAncestor(Node node) {
      if(node.getParentNode()==getParentNode()){
          return getParentNode(); 
      }
      return getParentNode().getParentNode();
      
    }


    private Node getNextSibling(Object o) {
        int index = children.indexOf(o);
        if(index>children.size()) return null;
        return (Node)children.get(index+1);
    }

    public Node getNextSibling() {
       if(fParent instanceof JSDTJfaceNode){
           ((JSDTJfaceNode)fParent).getNextSibling(this);
       }
       return super.getNextSibling();
    }

    private Node getPreviousSibling(Object o) {
        int index = children.indexOf(o);
        if(index<=0) return null;
        return (Node)children.get(index-1);
    }

    public Node getPreviousSibling() {
        if(fParent instanceof JSDTJfaceNode){
            ((JSDTJfaceNode)fParent).getPreviousSibling(this);
        }
        return super.getPreviousSibling();
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeImpl#getParentNode()
     */
    @Override
    public Node getParentNode() {
       
        return fParent;
    }


    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#appendChild(org.w3c.dom.Node)
     */
    @Override
    public Node appendChild(Node newChild) {
        if(!super.isChildEditable()) setChildEditable(true);
       
        children.add(newChild);
       
       return newChild;
    }

    class children implements NodeList{
        
        public int getLength(){
            return children.size();
        }
        
        public Node item(int i ){
            return (Node)children.get(i);
        }
        
        protected void add(Object o){
            children.add(o);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#getChildNodes()
     */
    @Override
    
    public NodeList getChildNodes() {
       return new children();
    }


    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#getFirstChild()
     */
    @Override
    public Node getFirstChild() {
        return hasChildNodes()?(Node)children.get(0):null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#getLastChild()
     */
    @Override
    public Node getLastChild() {
        return hasChildNodes()?(Node)children.get(children.size()):null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#hasChildNodes()
     */
    @Override
    public boolean hasChildNodes() {
       return !children.isEmpty();
    }


    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#isChildEditable()
     */
    @Override
    public boolean isChildEditable() {
        
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#removeChild(org.w3c.dom.Node)
     */
    @Override
    public Node removeChild(Node oldChild) {
        children.remove(oldChild);
        return oldChild;
    }


    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.core.internal.document.NodeContainer#removeChildNodes()
     */
    @Override
    public void removeChildNodes() {
       children.removeAllElements();
    }


   


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
//    /* valid for a parent node, replaces old node with this node and copies the parents attribs */
//    public JSDTJfaceNode(Node oldNode){
//       super((ElementImpl)oldNode);
//       setParentNode(oldNode.getParentNode());
//       super.replaceChild(this, oldNode); 
//       nodeType = oldNode.getNodeType();
//       
//    }
    
    private static ElementImpl getFirstElementType(Node node){
        if(node instanceof ElementImpl){
            return (ElementImpl)node;
        }else{
            Node newNode = null;
            while((newNode = node.getFirstChild())!=null){
                if(newNode instanceof ElementImpl){
                    return (ElementImpl)newNode;
                }
            }
        }
        return new ElementStyleImpl();
    }
 
    public JSDTJfaceNode(Node parent, boolean childNode ){
        super(getFirstElementType(parent));
        
        if(!childNode){
            
            setParentNode(parent.getParentNode());
            //super.replaceChild(this, parent); 
            nodeType = parent.getNodeType();
            
        }else{
       
            setParentNode(parent);
        }
       // parent.appendChild(this);
        //fParent = parent;
        
     }
 
    protected void setParentNode(Node parentNode) {
        this.fParent =  parentNode;
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
        if(!super.isDataEditable()) setEditable(true,false);
    }
    
    public IJavaElement getJsPart(){
        return fJSElement;
    }
    
    public boolean isJsNode(){
        return fJSElement!=null || hasChildNodes();
    }


    public boolean exists() {
        if(isJsNode()){
            return fJSElement.exists();
        }
        return false;
    }


    public IJavaElement getAncestor(int ancestorType) {
        if(isJsNode()){
            return fJSElement.getAncestor(ancestorType);
        }
        return null;
    }


    public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
        if(isJsNode()){
            return fJSElement. getAttachedJavadoc(monitor);
        }
        return null;
    }


    public IResource getCorrespondingResource() throws JavaModelException {
        if(isJsNode()){
            return fJSElement. getCorrespondingResource();
        }
        return null;
    }


    public String getElementName() {
        if(isJsNode()){
            return fJSElement.getElementName();
        }
        return null;
    }


    public int getElementType() {
        if(isJsNode()){
            return fJSElement.getElementType();
        }
        return 0;
    }


    public String getHandleIdentifier() {
        if(isJsNode()){
            return fJSElement.getHandleIdentifier();
        }
        return null;
    }


    public IJavaModel getJavaModel() {
        if(isJsNode()){
            return fJSElement.getJavaModel();
        }
        return null;
    }


    public IJavaProject getJavaProject() {
        if(isJsNode()){
            return fJSElement.getJavaProject();
        }
        return null;
    }


    public IOpenable getOpenable() {
        if(isJsNode()){
            return fJSElement.getOpenable();
        }
        return null;
    }


    public IJavaElement getParent() {
        if(isJsNode()){
            return fJSElement.getParent();
        }
        return null;
    }


    public IPath getPath() {
        if(isJsNode()){
            return fJSElement.getPath();
        }
        return null;
    }


    public IJavaElement getPrimaryElement() {
        if(isJsNode()){
            return fJSElement.getPrimaryElement();
        }
        return null;
    }


    public IResource getResource() {
        if(isJsNode()){
            return fJSElement.getResource();
        }
        return null;
    }


    public ISchedulingRule getSchedulingRule() {
        if(isJsNode()){
            return fJSElement.getSchedulingRule();
        }
        return null;
    }


    public IResource getUnderlyingResource() throws JavaModelException {
        if(isJsNode()){
            return fJSElement.getUnderlyingResource();
        }
        return null;
    }


    public boolean isReadOnly() {
        if(isJsNode()){
            return fJSElement.isReadOnly();
        }
        return false;
    }


    public boolean isStructureKnown() throws JavaModelException {
        if(isJsNode()){
            return fJSElement.isStructureKnown();
        }
        return false;
    }


    public Object getAdapter(Class adapter) {
        if(isJsNode()){
            return fJSElement.getAdapter(adapter);
        }
        return super.getAdapterFor(adapter);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
       if(obj instanceof IJavaElement && isJsNode()){
           return fJSElement.equals(obj);
       }
       return super.equals(obj);
    }
    
    
 }
