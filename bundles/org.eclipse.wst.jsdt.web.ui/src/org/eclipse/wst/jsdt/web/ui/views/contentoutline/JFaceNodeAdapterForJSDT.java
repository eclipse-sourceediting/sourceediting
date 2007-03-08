/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.views.contentoutline;


import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;


import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.internal.ui.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.ISourceRange;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JSPTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.exceptions.UnsupportedCharsetExceptionWithDetail;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Adapts a DOM node to a JFace viewer.
 */
public class JFaceNodeAdapterForJSDT extends JFaceNodeAdapterForHTML {

//	private Image createHTMLImage(String imageResourceName) {
//		ImageDescriptor imageDescriptor = HTMLEditorPluginImageHelper.getInstance().getImageDescriptor(imageResourceName);
//		if (imageDescriptor != null)
//			return imageDescriptor.createImage();
//		return null;
//	}

	/* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#getChildren(java.lang.Object)
     */
  
    
    private IJavaElement[] _cachedJavaElements=null;
    private Node           _cachedNode=null;
   
    public Object[] getChildren(Object object) {
//       
//        
//        if(JSDTJfaceNode.hasJsPart(object)){
//            
//            return super.getChildren(((JSDTJfaceNode)object).getJsPart());
//        }
//        
        Node node = (Node) object;
        // && !(node.getFirstChild() instanceof JSDTJfaceNode )
        if(isJSElementParent(node)){
            return getJSElementsFromNode(node.getFirstChild());
        } 
  
        System.out.println("method JFaceNodeAdapterForJSDT.getChildren" );
        return super.getChildren(object);
    
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object object) {
       
//         Node node = (Node) object;
//        
//        if(isJSElementParent(node)){
//            return getJavaChildren(node);
//           
//        }
//        else if(){
//            NodeList list = node.getChildNodes();
//            Object o[] = new Object[list.getLength()];
//            for(int i = 0;i<list.getLength();i++){
//                o[i]= list.item(i);
//                return o;
//            }
//        }
        System.out.println("method JFaceNodeAdapterForJSDT.getElements" );
        return super.getElements(object);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#getLabelText(java.lang.Object)
     */
    @Override
    public String getLabelText(Object object) {
        
        if(JSDTJfaceNode.hasJsPart(object)){
            return getJavaElementLabelProvider().getText(((JSDTJfaceNode)object).getJsPart());
        }
        Node node = (Node) object;
        return super.getLabelText(node);
    }

    
    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object object) {
       
        System.out.println("Umiplement method JFaceNodeAdapterForJSDT.getParent" );
        
//        if(object instanceof IJavaElement){
//            // Need to return the HTML 'parent' (not the java elements compilation unit
//            
//            
//        }
//   
        Object parent = super.getParent(object);
        return parent;
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object object) {
       
         System.out.println("Umiplement method JFaceNodeAdapterForJSDT.hasChildren" );
        Node node = (Node) object;
      
        
        if(isJSElementParent(node) && !(node.getFirstChild() instanceof JSDTJfaceNode )){
            Object[] nodes = getJSElementsFromNode(node.getFirstChild());
           
            return (nodes!=null && nodes.length>0);
        } 
  
        return super.hasChildren(object);
    }

    /**
	 * Constructor for JFaceNodeAdapterForHTML.
	 * 
	 * @param adapterFactory
	 */
    private StandardJavaElementContentProvider fJavaElementProvider;
    private JavaElementLabelProvider fJavaElementLabelProvider;
    
	public JFaceNodeAdapterForJSDT(JFaceNodeAdapterFactory adapterFactory) {
		super(adapterFactory);
       
	}

    private StandardJavaElementContentProvider getJavaElementProvider(){
        if(fJavaElementProvider==null){
            fJavaElementProvider = new StandardJavaElementContentProvider(true);
            
        }
        return fJavaElementProvider;
        
    }
    
    private JavaElementLabelProvider getJavaElementLabelProvider(){
        if(fJavaElementLabelProvider==null){
            fJavaElementLabelProvider = new JavaElementLabelProvider();
            
        }
        return fJavaElementLabelProvider;
        
    }
    
	protected Image createImage(Object object) {
		Image image = null;
		if(JSDTJfaceNode.hasJsPart(object)){
            IJavaElement jElement = ((JSDTJfaceNode)object).getJsPart();
            return getJavaElementLabelProvider().getImage(jElement);
        }
        
        Node node = (Node) object;
        
		if (image == null) {
			image = super.createImage(node);
		}
		return image;
	}
    
    private boolean isJSElementParent(Node node){
      return (node.hasChildNodes() && isJSElement(node.getFirstChild()));
    }
    
    
    
    private boolean isJSElement(Object object){
        if(JSDTJfaceNode.hasJsPart(object)) return true;
        Node node = (Node)object;
        Node parent = node.getParentNode();
        if(parent!=null && parent.getNodeName().equalsIgnoreCase("script") && node.getNodeType()==Node.TEXT_NODE){
            // Probably in a JS region, so lets translate to make sure
            return true;
        }
        return false;
    }
    
//    private IJavaElement[] getJavaChildren(Node node){
//        if(isJSElementParent(node) && (node.getChildNodes())!=null){
//            NodeList list = node.getChildNodes();
//            Vector vElements = new Vector();
//            
//            for(int i = 0;i<list.getLength();i++){
//                IJavaElement[] elements = getJavaElementsFromNode(list.item(i));
//                for(int j = 0;j<elements.length;j++){
//                    int type = elements[j].getElementType();
//                    if(type !=IJavaElement.PACKAGE_DECLARATION){
//                        
//                    }
//                        vElements .add(elements[j]);
//                    }
//                }
//            }
//            return (IJavaElement[])vElements .toArray(new IJavaElement[]{});
//    }
//    
//    public Node getJavaElementParent(IJavaElement element){
//        /* kinda complicated, need to get the translation from last accecced node (assuming it hasn't changed)
//         * 
//         * */
//        IStructuredModel model = null;
//        IModelManager modelManager = StructuredModelManager.getModelManager();
//        JSPTranslation translation = null;
//        IStructuredDocument doc;
//        try {
//            if (modelManager != null && (_lastAccesed instanceof NodeImpl)) {
//                doc = ((NodeImpl)_lastAccesed).getStructuredDocument();
//                model = modelManager.getExistingModelForRead(doc);
//            }else{
//                return null;
//            }
//            IDOMModel domModel = (IDOMModel) model;
//            IDOMDocument xmlDoc = domModel.getDocument();
//            JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
//            translation = translationAdapter.getJSPTranslation();
//        } catch (Exception e) {
//            Logger.logException(e);
//        } finally {
//            if (model != null) {
//                model.releaseFromRead();
//            }
//        }
//        
//        ISourceRange range = translation.getJSSourceRangeOf(element);
//        
//        int htmlOffset = translation.getJspOffset(range.getOffset());
//        
//        Node node = null;
//        
//        if(range!=null){
//            node = ((NodeImpl)_lastAccesed).getModel().getIndexedRegion(htmlOffset));
//        }
//
//        return node;
//    }
   
    private JSDTJfaceNode[] getJSElementsFromNode(Node node){
        IStructuredModel model = null;
        IModelManager modelManager = StructuredModelManager.getModelManager();
        JSPTranslation translation = null;
        try {
            if (modelManager != null ) {
                IStructuredDocument doc = ((NodeImpl)node).getStructuredDocument();
                model = modelManager.getExistingModelForRead(doc);
            }
            IDOMModel domModel = (IDOMModel) model;
            IDOMDocument xmlDoc = domModel.getDocument();
            JSPTranslationAdapter translationAdapter = (JSPTranslationAdapter) xmlDoc.getAdapterFor(IJSPTranslation.class);
            translation = translationAdapter.getJSPTranslation();
        } catch (Exception e) {
            Logger.logException(e);
        } finally {
            if (model != null) {
                model.releaseFromRead();
            }
        }
        
       IJavaElement[] result=null;
       int startOffset = 0;
       int endOffset = 0;
      int type = node.getNodeType();
       if(node.getNodeType()==Node.TEXT_NODE  && (node instanceof NodeImpl) && translation!=null){
            startOffset = ((NodeImpl)node).getStartOffset();
            endOffset = ((NodeImpl)node).getEndOffset();
           
            result = translation.getElementsFromJspRange(startOffset,endOffset);   
           
        }
       if(result==null) return null;
       
       JSDTJfaceNode nodes[] = new JSDTJfaceNode[result.length];
            
       Node parentJsNode =node.getParentNode();
       
       //int offset = startOffset;
       
       /* build the node list before an append */
       for(int i = 0;i<result.length;i++){
          nodes[i] = new JSDTJfaceNode(parentJsNode,null);
          int htmllength=0;
          int htmloffset=0;
          try {
             htmllength = translation.getJspOffset(((SourceRefElement)(result[i])).getSourceRange().getLength());
             htmloffset = translation.getJspOffset(((SourceRefElement)(result[i])).getSourceRange().getOffset());
             
          } catch (JavaModelException e) {
            e.printStackTrace();
          }
           //int adjustedLength = (i==result.length-1)?(-offset):htmlLength;
          nodes[i].setJsPart(result[i], new Position(htmloffset,htmllength));
          
      }

       return  nodes;
     }
    public ISelection getSelection(TreeViewer viewer, ISelection selection) {
        if(selection instanceof StructuredSelection){
            StructuredSelection ss = (StructuredSelection)selection;
            Object firstElement = ss.getFirstElement();
            if(isJSElement(firstElement)) return new StructuredSelection(firstElement);
            if(isJSElementParent((Node)firstElement)) return new StructuredSelection(getJSElementsFromNode(((Node)firstElement).getFirstChild()));
        }
        
        return null;
    }
   
}