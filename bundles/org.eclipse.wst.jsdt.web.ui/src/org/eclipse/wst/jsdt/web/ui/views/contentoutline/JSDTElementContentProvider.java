package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JSDTElementContentProvider extends StandardJavaElementContentProvider {

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#exists(java.lang.Object)
     */
    ITreeContentProvider fParentContentProvider=null;
    
    private Object[] NO_PARENT = new Object[0];
    private Hashtable parents;
    
    public  JSDTElementContentProvider(IContentProvider parentContentProvider){
        if(parentContentProvider instanceof ITreeContentProvider) fParentContentProvider =  (ITreeContentProvider)parentContentProvider;
        parents = new Hashtable();
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object object) {
        if(object instanceof IJavaElement) return super.getChildren(object);
        System.out.println("Get Children.\n Node Name: " + ((Node)object).getNodeName() + 
                "\nType:\n" + ((Node)object).getNodeType() + 
                "# of Children:" + (((Node)object).hasChildNodes()?((Node)object).getChildNodes().getLength():0) + 
                "\nContents:\n"+((Node)object).getNodeValue() +
                "\n--------------------------------");  
            Node node = (Node) object;
            // && !(node.getFirstChild() instanceof JSDTJfaceNode )
            if (isJSElementParent(node)) {
                Object[] results = getJSElementsFromNode(node.getFirstChild());
                return results;
            }
            return fParentContentProvider.getChildren(object);
            
    }
  

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object object) {
        if(object instanceof IJavaElement) return super.getElements(object);
        if(parents.contains(object)){
           NodeList list= (((pLocationMap)parents.get(object)).parent).getChildNodes();
           Object obj[] = new Object[list.getLength()];
           for(int i = 0;i<list.getLength();i++){
               obj[i] = list.item(i);
           }
           return obj;
        }
         return fParentContentProvider.getElements(object);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object element) {
        
        if(parents.contains(element)){
            return  ((pLocationMap)parents.get(element)).parent;
        }
        if(element instanceof IJavaElement) return super.getParent(element);
        return fParentContentProvider.getParent(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object object) {
        if(object instanceof IJavaElement) return super.hasChildren(object);
       
        Node node = (Node) object;
        
        if ( isJSElementParent(node) ) {
            /* cheaper to check parent array */
            Enumeration parentEnum = parents.elements();
            
            while(parentEnum.hasMoreElements()){
                pLocationMap value = (pLocationMap)parentEnum.nextElement();
                
                /* found a child */
                if(value.parent==node) return true;
                
            }
            
            Object[] nodes = getJSElementsFromNode(node.getFirstChild());
            
            return (nodes != null && nodes.length > 0);
        }
        
        return fParentContentProvider.hasChildren(object);
    }
    
    private boolean isJSElementParent(Node node) {
        return (node.hasChildNodes() && node.getNodeName().equalsIgnoreCase("script"));
    }
    
    private boolean isJSElement(Object object) {
        if(object instanceof IJavaElement) return true;
        if (JSDTJfaceNode.hasJsPart(object))
            return true;
        Node node = (Node) object;
        Node parent = node.getParentNode();
        if (parent != null && parent.getNodeName().equalsIgnoreCase("script") && node.getNodeType() == Node.TEXT_NODE) {
            
            return true;
        }
        return false;
    }
    
    class pLocationMap{
        Node parent;
        Position htmlLocation;
        pLocationMap(Node parent, Position position){
            this.parent=parent;
            this.htmlLocation=position;
        }
    }
    
    private IJavaElement[] getJSElementsFromNode(Node node) {
        IStructuredModel model = null;
        IModelManager modelManager = StructuredModelManager.getModelManager();
        JSPTranslation translation = null;
       
        IJavaElement[] result = null;
        try {
            if (modelManager != null) {
                IStructuredDocument doc = ((NodeImpl) node).getStructuredDocument();
               // model = modelManager.getExistingModelForRead(doc);
                model = modelManager.getExistingModelForRead(doc);
            }
            IDOMModel domModel = (IDOMModel) model;
            IDOMDocument xmlDoc = domModel.getDocument();
            JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
            translation = translationAdapter.getJSPTranslation();
            
            
            int startOffset = 0;
            int endOffset = 0;
            int type = node.getNodeType();
            if (node.getNodeType() == Node.TEXT_NODE && (node instanceof NodeImpl) && translation != null) {
                startOffset = ((NodeImpl) node).getStartOffset();
                endOffset = ((NodeImpl) node).getEndOffset();
                
                result = translation.getElementsFromJspRange(startOffset, endOffset,true);
                
            }
            if (result == null)
                return null;
            } catch (Exception e) {
                    Logger.logException(e);
            } finally {
                    if (model != null) {
                       // model.changedModel();
                        model.releaseFromRead();
                    }
            }
        
            //nodes = new JSDTJfaceNode[result.length];
           // JSDTJfaceNode parentJsNode = new JSDTJfaceNode(node.getParentNode(),false);
           
            
//            //model.aboutToChangeModel();
//            JSDTJfaceNode parentJsNode;
//            
//            if(!(parentnode instanceof JSDTJfaceNode)){
//                parentJsNode = new JSDTJfaceNode(parentnode,false);
//            }else{
//                parentJsNode = (JSDTJfaceNode)parentnode;
//               
//            }
//            parentJsNode.removeChildNodes();
            
            // int offset = startOffset;
            
          /* build the node list and append to parent */
            for (int i = 0; i < result.length; i++) {
               // nodes[i] = new JSDTJfaceNode(parentJsNode, true);
                int htmllength = 0;
                int htmloffset = 0;
                Position position=null;
                try {
                    htmllength = ((SourceRefElement) (result[i])).getSourceRange().getLength();
                    htmloffset = translation.getJspOffset(((SourceRefElement) (result[i])).getSourceRange().getOffset());
                    position = new Position(htmloffset, htmllength);
                } catch (JavaModelException e) {
                    e.printStackTrace();
                }
                // int adjustedLength =
                // (i==result.length-1)?(-offset):htmlLength;
               // nodes[i].setJsPart(result[i], new Position(htmloffset, htmllength));
                //nodes[i].addAdapter(this);
               // parentJsNode.appendChild(nodes[i]);
                
                parents.put(result[i], new  pLocationMap(node,position));
                
            }
            return result;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
//        // TODO Auto-generated method stub
//        System.out.println("Umiplement method inputChanged" );
//        //super.inputChanged(viewer, oldInput, newInput);
//        if(oldInput instanceof JSDTJfaceNode){
//            if(newInput instanceof JSDTJfaceNode){
//                super.inputChanged(viewer, oldInput, newInput);
//            }
//        }
       
        if(oldInput!=null && parents.contains(oldInput)) parents.remove(oldInput);
        fParentContentProvider.inputChanged(viewer, oldInput, newInput);
        
    }
    
    public ISelection getSelection(TreeViewer viewer, ISelection selection) {
        
        if (selection instanceof StructuredSelection) {
            StructuredSelection ss = (StructuredSelection) selection;
            if(ss==null) return null;
            Object firstElement = ss.getFirstElement();
            if (isJSElement(firstElement))
                return new StructuredSelection(firstElement);
            if (isJSElementParent((Node) firstElement))
                return new StructuredSelection(getJSElementsFromNode(((Node) firstElement).getFirstChild()));
        }
        
        return null;
    }
    
}
