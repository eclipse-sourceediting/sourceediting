package org.eclipse.wst.jsdt.web.ui.views.contentoutline;


import java.util.Enumeration;
import java.util.Hashtable;
import org.eclipse.wst.jsdt.internal.ui.compare.JavaNode;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.core.NamedMember;
import org.eclipse.wst.jsdt.internal.core.SourceMethod;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.internal.core.SourceType;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.jsdt.web.ui.views.contentoutline.JSDTElementContentProvider.pLocationMap;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JFaceNodeAdapterForJs extends JFaceNodeAdapterForHTML implements IContentProvider{
    
    public JFaceNodeAdapterForJs(JFaceNodeAdapterFactory adapterFactory) {
        super(adapterFactory);   
    }

    private StandardJavaElementContentProvider fJavaElementProvider;
    private JavaElementLabelProvider           fJavaElementLabelProvider;
    private Hashtable                           parents = new Hashtable();
    
    public Object[] getChildren(Object object) {
        if(object instanceof IJavaElement) return getJavaElementProvider().getChildren(object);
            Node node = (Node) object;
            if (isJSElementParent(node)) {
                Object[] results = getJSElementsFromNode(node.getFirstChild());
                return results;
            }
            return super.getChildren(object);
    }
  
    public Object[] getElements(Object object) {
        if(object instanceof IJavaElement) return getJavaElementProvider().getElements(object);
//        if(parents.contains(object)){
//           NodeList list= ((Node)parents.get(object)).getChildNodes();
//           Object obj[] = new Object[list.getLength()];
//           for(int i = 0;i<list.getLength();i++){
//               obj[i] = list.item(i);
//           }
//           return obj;
//        }
         return super.getElements(object);
    }

    
    
    public String getLabelText(Object node) {
        if(node instanceof JsJfaceNode) return getJavaElementLabelProvider().getText(((JsJfaceNode)node).getJsElement());
        if(node instanceof IJavaElement) return getJavaElementLabelProvider().getText((IJavaElement)node);
        return super.getLabelText(node);
    }

    
    
    public Image getLabelImage(Object node) {
        if(node instanceof JsJfaceNode) return getJavaElementLabelProvider().getImage(((JsJfaceNode)node).getJsElement());
        if(node instanceof IJavaElement) return getJavaElementLabelProvider().getImage((IJavaElement)node);
        return super.getLabelImage(node);
    }

    public Object getParent(Object element) {
        
//        if(parents.contains(element)){
//            return  ((Node)parents.get(element));
//        }
        if(element instanceof IJavaElement) return getJavaElementProvider().getParent(element);
        return super.getParent(element);
    }

    public boolean hasChildren(Object object) {
        if(object instanceof IJavaElement) return getJavaElementProvider().hasChildren(object);
       
        Node node = (Node) object;
        
        if ( isJSElementParent(node) ) {
           // if(parents.contains(node)) return true;
            
            Object[] nodes = getJSElementsFromNode(node.getFirstChild());
            
            return (nodes != null && nodes.length > 0);
        }
        
        return super.hasChildren(object);
    }
    
    private boolean isJSElementParent(Node node) {
        return parents.contains(node) || (node.hasChildNodes() && node.getNodeName().equalsIgnoreCase("script"));
    }
    
    private boolean isJSElement(Object object) {
        
        if(object instanceof IJavaElement) return true;
       
        Node node = (Node) object;
        Node parent = node.getParentNode();
        if (parent != null && parent.getNodeName().equalsIgnoreCase("script") && node.getNodeType() == Node.TEXT_NODE) {
            return true;
        }
        return false;
    }
    
    private Object[] getJSElementsFromNode(Node node) {
        IStructuredModel model = null;
        IModelManager modelManager = StructuredModelManager.getModelManager();
        JSPTranslation translation = null;
       
        IJavaElement[] result = null;
        IDocument viewerDoc = null;
        
        try {
            if (modelManager != null) {
                IStructuredDocument doc = ((NodeImpl) node).getStructuredDocument();
               // model = modelManager.getExistingModelForRead(doc);
                model = modelManager.getExistingModelForRead(doc);
            }
            IDOMModel domModel = (IDOMModel) model;
            IDOMDocument xmlDoc = domModel.getDocument();
            viewerDoc = xmlDoc.getStructuredDocument();
            
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
            
            
            Object[] newResults=new Object[result.length];
           
            for (int i = 0; i < result.length; i++) {
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
              
                
                newResults[i] = getJsNode(node.getParentNode(), (IJavaElement)result[i], position, model.getFactoryRegistry());
                //newResults[i] = new JavaNode(parent,result[i].getElementType(),result[i].getElementName(),htmloffset,htmllength);
                //parents.put(result[i], node);
                parents.put(newResults[i], node);
                
            }
            return newResults;
    }
    
    private Object getJsNode(Node parent, IJavaElement root, Position position, FactoryRegistry registry){
        //JavaNode node = new JavaNode(parent,root.getElementType(),root.getElementName(),position.getOffset(), position.getLength());
        //if(true) return node;
        if(true) return new JsElement(root);
        JsJfaceNode instance = new JsJfaceNode(parent, root, position);
        ((JsJfaceNode)instance).setAdapterRegistry(registry);
        ((JsJfaceNode)instance).addAdapter(this);
        return instance;
    }
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
       // super.inputChanged(viewer, oldInput, newInput);
        
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
    private StandardJavaElementContentProvider getJavaElementProvider() {
        if (fJavaElementProvider == null) {
            fJavaElementProvider = new StandardJavaElementContentProvider(true);
        }
        return fJavaElementProvider;
        
    }
    
    private JavaElementLabelProvider getJavaElementLabelProvider() {
        if (fJavaElementLabelProvider == null) {
            fJavaElementLabelProvider = new JavaElementLabelProvider();
        }
        return fJavaElementLabelProvider;
        
    }


    public void dispose() {
       if(parents!=null) parents.clear();
        
    }
}
