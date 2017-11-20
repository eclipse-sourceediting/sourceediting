/*******************************************************************************
 * Copyright (c) 2007, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     bug:244839 - eugene@genuitec.com
 *******************************************************************************/
package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IMember;
import org.eclipse.wst.jsdt.core.ISourceRange;
import org.eclipse.wst.jsdt.core.ISourceReference;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.internal.core.JavaElement;
import org.eclipse.wst.jsdt.internal.core.Member;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.ui.JavaScriptElementLabelProvider;
import org.eclipse.wst.jsdt.ui.StandardJavaScriptElementContentProvider;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.ui.internal.Logger;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.ui.internal.contentoutline.IJFaceNodeAdapter;
import org.eclipse.wst.xml.core.internal.document.NodeImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.ui.internal.contentoutline.JFaceNodeAdapterFactory;
import org.w3c.dom.Node;
/**
*

* Provisional API: This class/interface is part of an interim API that is still under development and expected to
* change significantly before reaching stability. It is being made available at this early stage to solicit feedback
* from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
* (repeatedly) as the API evolves.
*/
public class JFaceNodeAdapterForJs extends JFaceNodeAdapterForHTML {
	
	public JFaceNodeAdapterForJs(JFaceNodeAdapterFactory adapterFactory) {
		super(adapterFactory);
	}
	
	private IJavaScriptUnit lazyCu;
	private IProgressMonitor monitor;
	private JavaScriptElementLabelProvider javaElementLabelProvider;
	
	public Object[] getChildren(Object object) {
		if (object instanceof IJavaScriptElement) {
			return getJavaElementProvider().getChildren(object);
		}
		if (object instanceof IJavaWebNode) {
			JavaElement enclosedElement = (JavaElement) ((IJavaWebNode) object).getJavaElement();
			if (enclosedElement != null) {
				try {
					IJavaScriptElement[] children = enclosedElement.getChildren();
					if (children == null) {
						return new IJavaScriptElement[0];
					}
					Object[] nodes = new Object[children.length];
					Node parent = ((IJavaWebNode) object).getParentNode();
					
					for (int i = 0; i < children.length; i++) {
					//	int htmllength = ((SourceRefElement) (children[i])).getSourceRange().getLength();
					//	int htmloffset = ((SourceRefElement) (children[i])).getSourceRange().getOffset();
						IJavaScriptElement javaElement = children[i];
						ISourceRange range = null;
						if (javaElement instanceof Member) {
								range = ((IMember) javaElement).getNameRange();
						} else {
								range = ((ISourceReference) javaElement).getSourceRange();
						}
						int htmllength = range.getLength();
						int htmloffset = range.getOffset();

						
						Position position = new Position(htmloffset, htmllength);
						nodes[i] = getJsNode(parent, javaElement, position);
					}
					return nodes;
				} catch (JavaScriptModelException ex) {
				}
			}
		}
		Node node = (Node) object;
		if (isJSElementParent(node)) {
			Object[] results = getJSElementsFromNode(node.getFirstChild(), true);
			
			
			return filter( results );
		}
		return super.getChildren(object);
	}
	
	/*
	 * @GINO: Anonymous -- matches anonymous types on the top level
	 */
	protected boolean matches(Object elementObj) {
		
		if( elementObj instanceof IJavaWebNode ){
			IJavaScriptElement element = ((IJavaWebNode)elementObj).getJavaElement();
			if (element.getElementType() == IJavaScriptElement.TYPE && element.getParent().getElementType() == IJavaScriptElement.JAVASCRIPT_UNIT ) {
				
				IType type = (IType)element;
				try {
					return type.isAnonymous();
				} catch (JavaScriptModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}

	/*
	 * @GINO: Anonymous Filter from top level
	 *
	 */
	protected Object[] filter(Object[] children) {
		boolean initializers= false;
		for (int i= 0; i < children.length; i++) {
			if (matches(children[i])) {
				initializers= true;
				break;
			}
		}

		if (!initializers)
			return children;

		Vector v= new Vector();
		for (int i= 0; i < children.length; i++) {
			if (matches(children[i]))
				continue;
			v.addElement(children[i]);
		}

		Object[] result= new Object[v.size()];
		v.copyInto(result);
		return result;
	}
	
	public Object[] getElements(Object object) {
		if (object instanceof IJavaScriptElement) {
			return getJavaElementProvider().getElements(object);
		}
		return super.getElements(object);
	}
	
	private JavaScriptElementLabelProvider getJavaElementLabelProvider() {
		if(javaElementLabelProvider==null) {
			javaElementLabelProvider = new JavaScriptElementLabelProvider();
		}
		return javaElementLabelProvider;
	}
	
	private StandardJavaScriptElementContentProvider getJavaElementProvider() {
		return new StandardJavaScriptElementContentProvider(true);
	}
	
	private Object[] filterChildrenForRange(IJavaScriptElement[] allChildren, Node node) {
		int javaPositionEnd = ((NodeImpl) node).getEndOffset();
		int javaPositionStart = ((NodeImpl) node).getStartOffset();
		javaPositionStart = getTranslation(node).getJavaScriptOffset(((NodeImpl) node).getStartOffset());
		if (javaPositionStart < 0)
			javaPositionStart = ((NodeImpl) node).getStartOffset();
		javaPositionEnd = getTranslation(node).getJavaScriptOffset(((NodeImpl) node).getEndOffset() - 1);
		if (javaPositionEnd < 0)
			javaPositionEnd = ((NodeImpl) node).getEndOffset();

		List validChildren = new ArrayList();
		for (int i = 0; i < allChildren.length; i++) {
			ISourceRange range = null;
			if (allChildren[i] instanceof Member) {
				try {
					range = ((Member) allChildren[i]).getNameRange();
				} catch (JavaScriptModelException e) {
					Logger.logException("Error getting range of Member child.", e);
				}
			} else if (allChildren[i]  instanceof SourceRefElement) {
				try {
					range = ((SourceRefElement)allChildren[i] ).getSourceRange();
				} catch (JavaScriptModelException e) {
					Logger.logException("Error getting range of SourceRefElement child.", e);
				}
			}
			
			if (range != null && javaPositionStart <= range.getOffset() &&
						(range.getLength() + range.getOffset()) <= (javaPositionEnd)) {
				
				int htmlLength = range.getLength();
				int htmlOffset = range.getOffset();
				if (htmlLength >= 0 && htmlOffset >= 0) {
					Position position = new Position(htmlOffset, htmlLength);
					validChildren.add(getJsNode(node.getParentNode(), allChildren[i], position));
				}
			}
		}
		Object[] result = new Object[0];

		if (validChildren.size() > 0) {
			result = validChildren.toArray();
		}
		if (result == null || result.length == 0) {
			return new IJavaScriptElement[0];
		}
		return result;
	}
	
	private synchronized Object[] getJSElementsFromNode(Node node, boolean ensureConsistant) {
				
//		int startOffset = 0;
//		int endOffset = 0;
//		int type = node.getNodeType();
		Object[] result = null;
		//JsTranslation translation = null;
		if (node.getNodeType() == Node.TEXT_NODE && (node instanceof NodeImpl)) {
//			startOffset = ((NodeImpl) node).getStartOffset();
//			endOffset = ((NodeImpl) node).getEndOffset();
			IJavaScriptUnit unit = getLazyCu(node);
	        // Genuitec Begin Fix 6149: Exception opening external HTML file
			if (unit == null) {
			    return new Object[0];
			}
	        // Genuitec End Fix 6149: Exception opening external HTML file
			try {
				if(ensureConsistant) unit.makeConsistent(getProgressMonitor());
			} catch (JavaScriptModelException ex1) {
				// TODO Auto-generated catch block
				ex1.printStackTrace();
			}
			try {
				result = filterChildrenForRange(unit.getChildren(),node);
			} catch (JavaScriptModelException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
				result = new Object[0];
				lazyCu=null;
			}
			
			//translation = getTranslation(node);
			//result = translation.getAllElementsInJsRange(startOffset, endOffset);
		}
		return result;
	}
	
	private IProgressMonitor getProgressMonitor() {
		if(monitor==null) {
			monitor = new NullProgressMonitor();
		}
		
		return monitor;
		
	}
	
	private Object getJsNode(Node parent, IJavaScriptElement root, Position position) {
		JsJfaceNode instance = null;
		if (root.getElementType() == IJavaScriptElement.TYPE) {
			instance = new JsJfaceNode(parent, root, position, ((SourceRefElement) root).getElementName());
		} else if (root.getElementType() == IJavaScriptElement.FIELD) {
			/* Field refrence, possibly to a type may need to implement later */
			instance = new JsJfaceNode(parent, root,  position);
		} else {
			instance = new JsJfaceNode(parent, root, position);
		}
		String name = getJavaElementLabelProvider().getText(root);
		instance.setName(name);
		// ((JsJfaceNode)instance).setAdapterRegistry(registry);
		INodeAdapter adapter = (instance).getAdapterFor(IJFaceNodeAdapter.class);
		if (!(adapter instanceof JFaceNodeAdapterForJs)) {
			(instance).removeAdapter(adapter);
			(instance).addAdapter(this);
		}
		return instance;
	}
	
	
	public Image getLabelImage(Object node) {
		if (node instanceof JsJfaceNode) {
			return ((JsJfaceNode) node).getImage();
		}
		if (node instanceof IJavaScriptElement) {
			return getJavaElementLabelProvider().getImage(node);
		}
		return super.getLabelImage(node);
	}
	
	
	public String getLabelText(Object node) {
//		if (node instanceof JsJfaceNode) {
//			return ((JsJfaceNode) node).getName();
//		}
		if (node instanceof IJavaScriptElement) {
			return getJavaElementLabelProvider().getText(node);
		}
		return super.getLabelText(node);
	}
	
	
	public Object getParent(Object element) {
		if (element instanceof IJavaScriptElement) {
			return getJavaElementProvider().getParent(element);
		}
		return super.getParent(element);
	}
	
	private IJavaScriptUnit getLazyCu(Node node) {
		if(lazyCu==null) {
			IJsTranslation tran = getTranslation(node);
			if(tran== null) return null;
			lazyCu = tran.getCompilationUnit();
			if(lazyCu==null) return null;
			
			try {
				lazyCu.makeConsistent( new NullProgressMonitor() );
			} catch (JavaScriptModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lazyCu;
	}
	
	private IJsTranslation getTranslation(Node node) {
		IStructuredModel model = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IDOMDocument xmlDoc = null;
		try {
			if (modelManager != null) {
				IStructuredDocument doc = ((NodeImpl) node).getStructuredDocument();
				model = modelManager.getExistingModelForRead(doc);
				// model = modelManager.getModelForRead(doc);
			}
			IDOMModel domModel = (IDOMModel) model;
			if(domModel == null) return null;
			xmlDoc = domModel.getDocument();
		} catch (Exception e) {
			Logger.logException(e);
		} finally {
			if (model != null) {
				// model.changedModel();
				model.releaseFromRead();
			}
		}
		if (xmlDoc == null) {
			return null;
		}
		JsTranslationAdapter translationAdapter = (JsTranslationAdapter) xmlDoc.getAdapterFor(IJsTranslation.class);
		return translationAdapter.getJsTranslation(true);
	}
	
	
	public boolean hasChildren(Object object) {
		if (object instanceof IJavaScriptElement) {
			return getJavaElementProvider().hasChildren(object);
		}
		Node node = (Node) object;
		if (node instanceof IJavaWebNode) {
			return ((IJavaWebNode) object).hasChildren();
			
		}
		if (isJSElementParent(node)) {
			Object[] nodes = getJSElementsFromNode(node.getFirstChild(),false);
			boolean hasElements = (nodes != null && nodes.length > 0);
			return hasElements;
		}
		return super.hasChildren(object);
	}
	
	private boolean isJSElementParent(Node node) {
		return (node.hasChildNodes() && node.getNodeName().equalsIgnoreCase("script")); //$NON-NLS-1$
	}
}
