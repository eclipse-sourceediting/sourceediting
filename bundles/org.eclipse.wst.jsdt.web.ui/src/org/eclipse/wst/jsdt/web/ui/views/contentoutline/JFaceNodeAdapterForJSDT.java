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
import java.util.Vector;

import org.eclipse.wst.jsdt.internal.ui.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ILabelProvider;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImageHelper;
import org.eclipse.wst.html.ui.internal.editor.HTMLEditorPluginImages;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.ISourceRange;
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
  
    
    private IJavaElement _cachedJavaElement=null;
    private Node         _cachedNode=null;
     
    public Object[] getChildren(Object object) {
        Node node = (Node) object;
        
        if(isJSDTElementParent(node) && node.getChildNodes()!=null){
            NodeList list = node.getChildNodes();
            Vector elements = new Vector();
            
            for(int i = 0;i<list.getLength();i++){
                IJavaElement elm = getJavaElement(list.item(i));
                Object elms[] = getJavaElementProvider().getChildren(elm.getParent());
                for(int j = 0;j<elms.length;j++){
                    int type = ((IJavaElement)elms[j]).getElementType();
                    if(type !=IJavaElement.PACKAGE_DECLARATION){
                        IJavaElement javaElement = getJavaElement(elms[j]);
                        elements.add(javaElement);
                    }
                }
                
            }
            return elements.toArray();
           
        }else if(isJSDTElement(object)){
           
            IJavaElement element = getJavaElement(object);
            Object[] elms = getJavaElementProvider().getChildren(element);
            /* need to convert to a combo type of java elemnt and dom element */
            for(int i = 0;i<elms.length;i++){
                elms[i] = getJavaElement(elms[i]);
            }
            
            return elms;
            
        }
        System.out.println("method JFaceNodeAdapterForJSDT.getChildren" );
        return super.getChildren(object);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(Object object) {
       
        Node node = (Node) object;
        
        if(isJSDTElementParent(node) || isJSDTElement(object)){
            IJavaElement jElement = getJavaElement(object);
            return getJavaElementProvider().getElements(jElement);
           
        }
//        else if(){
//            NodeList list = node.getChildNodes();
//            Object o[] = new Object[list.getLength()];
//            for(int i = 0;i<list.getLength();i++){
//                o[i]= list.item(i);
//                return o;
//            }
//        }
        System.out.println("Umiplement method JFaceNodeAdapterForJSDT.getElements" );
        return super.getElements(node);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#getLabelText(java.lang.Object)
     */
    @Override
    public String getLabelText(Object object) {
        Node node = (Node) object;
        
        if(isJSDTElement(object)){
            IJavaElement jElement = getJavaElement(object);
            return getJavaElementLabelProvider().getText(jElement);
           
        }
        System.out.println("Umiplement method JFaceNodeAdapterForJSDT.getLabelText" );
        return super.getLabelText(node);
    }

    
    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object object) {
       
        System.out.println("Umiplement method JFaceNodeAdapterForJSDT.getParent" );
        Node node = (Node) object;
        if(isJSDTElement(object)){
            IJavaElement jElement = getJavaElement(object);
            getJavaElementProvider().getParent(jElement);
           
        }
        return super.getParent(object);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapter#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object object) {
       
        System.out.println("Umiplement method JFaceNodeAdapterForJSDT.hasChildren" );
        Node node = (Node) object;
        
        if(isJSDTElementParent(node)){
           
                NodeList list = node.getChildNodes();
                return list.getLength()>0;
           
        }else if(isJSDTElement(node)){
            IJavaElement jElement = getJavaElement(node);
            return getJavaElementProvider().hasChildren(jElement);
           
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

		Node node = (Node) object;
        
        if(isJSDTElement(object)){
            IJavaElement jElement = getJavaElement(node);
            return getJavaElementLabelProvider().getImage(jElement);
           
        }
//		if (node.getNodeType() == Node.ELEMENT_NODE) {
//			if (node.getNodeName().equalsIgnoreCase("table")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TABLE);
//			else if (node.getNodeName().equalsIgnoreCase("a")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_ANCHOR);
//			else if (node.getNodeName().equalsIgnoreCase("body")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_BODY);
//			else if (node.getNodeName().equalsIgnoreCase("button")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_BUTTON);
//			else if (node.getNodeName().equalsIgnoreCase("font")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_FONT);
//			else if (node.getNodeName().equalsIgnoreCase("form")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_FORM);
//			else if (node.getNodeName().equalsIgnoreCase("html")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_HTML);
//			else if (node.getNodeName().equalsIgnoreCase("img")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_IMAGE);
//			else if (node.getNodeName().equalsIgnoreCase("map")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_IMAGE_MAP);
//			else if (node.getNodeName().equalsIgnoreCase("title")) //$NON-NLS-1$
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG_TITLE);
//			else
//				image = createHTMLImage(HTMLEditorPluginImages.IMG_OBJ_TAG);
//		}
		if (image == null) {
			image = super.createImage(node);
		}
		return image;
	}
    
    private boolean isJSDTElementParent(Node node){
        
            return (node.hasChildNodes() && isJSDTElement(node.getFirstChild()));
       
    }
    
    private boolean isJSDTElement(Object object){
        if(object instanceof IJavaElement) return true;
        Node node = (Node)object;
        Node parent = node.getParentNode();
        if(parent!=null && parent.getNodeName().equalsIgnoreCase("script")){
            // Probably in a JS region, so lets translate to make sure
            return getJavaElement(node)!=null;
        }
        return false;
    }
    
    private class mappedSourceRange{
        IJavaElement element;
        Position htmlPosition;
        Position jsPosition;
        
        mappedSourceRange(IJavaElement element, Position htmlPosition, Position jsPosition){
            this.element=element;
            this.htmlPosition=htmlPosition;
            this.jsPosition=jsPosition;
        }
    }
    
    
    
    private IJavaElement getJavaElement(Object object){
        
        if(object instanceof IJavaElement) return (IJavaElement)object;
        
        Node node = (Node)object;
        if(node==_cachedNode) return _cachedJavaElement;
        IStructuredModel model = null;
        
        
            // get existing model for read, then get document from it
        IModelManager modelManager = StructuredModelManager.getModelManager();
       
        JSPTranslation translation = null;
        
        try {
        
            if (modelManager != null && (node instanceof NodeImpl)) {
                IStructuredDocument doc = ((NodeImpl)node).getStructuredDocument();
               model = modelManager.getExistingModelForRead(doc);
            }
           
            IDOMModel domModel = (IDOMModel) model;
            // setupAdapterFactory(domModel);
    
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
        
       IJavaElement element = null;
        
        if(node.getNodeType()==Node.TEXT_NODE  && (node instanceof NodeImpl) && translation!=null){
            int startOffset = ((NodeImpl)node).getStartOffset();
            int endOffset = ((NodeImpl)node).getEndOffset();
            element = translation.getJavaElementAtOffset(startOffset);   
           
        }
        
        // int offset = elementRange.getOffset();
         //int length = elementRange.getLength();
         return element;
         
    }
    
    private boolean isInParentRegion(Node parentRegion, IJavaElement element){
        
        return false;
    }
}