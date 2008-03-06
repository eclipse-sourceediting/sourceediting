package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML;
import org.eclipse.wst.jsdt.core.ICompilationUnit;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.ISourceRange;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.core.JavaElement;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslationAdapter;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
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

public class JFaceNodeAdapterForJs extends JFaceNodeAdapterForHTML {
	
	public JFaceNodeAdapterForJs(JFaceNodeAdapterFactory adapterFactory) {
		super(adapterFactory);
	}
	
	private ICompilationUnit lazyCu;
	private IProgressMonitor monitor;
	private JavaElementLabelProvider javaElementLabelProvider;
	
	public Object[] getChildren(Object object) {
		if (object instanceof IJavaElement) {
			return getJavaElementProvider().getChildren(object);
		}
		if (object instanceof IJavaWebNode) {
			JavaElement enclosedElement = (JavaElement) ((IJavaWebNode) object).getJavaElement();
			if (enclosedElement != null) {
				try {
					IJavaElement[] children = enclosedElement.getChildren();
					if (children == null) {
						return new IJavaElement[0];
					}
					Object[] nodes = new Object[children.length];
					Node parent = ((IJavaWebNode) object).getParentNode();
					
					for (int i = 0; i < children.length; i++) {
						int htmllength = ((SourceRefElement) (children[i])).getSourceRange().getLength();
						int htmloffset = ((SourceRefElement) (children[i])).getSourceRange().getOffset();
						Position position = new Position(htmloffset, htmllength);
						nodes[i] = getJsNode(parent, children[i], position);
					}
					return nodes;
				} catch (JavaModelException ex) {
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
			IJavaElement element = ((IJavaWebNode)elementObj).getJavaElement();
			if (element.getElementType() == IJavaElement.TYPE && element.getParent().getElementType() == IJavaElement.COMPILATION_UNIT ) {
				
				IType type = (IType)element;
				try {
					return type.isAnonymous();
				} catch (JavaModelException e) {
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
		if (object instanceof IJavaElement) {
			return getJavaElementProvider().getElements(object);
		}
		return super.getElements(object);
	}
	
	private JavaElementLabelProvider getJavaElementLabelProvider() {
		if(javaElementLabelProvider==null) {
			javaElementLabelProvider = new JavaElementLabelProvider();
		}
		return javaElementLabelProvider;
	}
	
	private StandardJavaElementContentProvider getJavaElementProvider() {
		return new StandardJavaElementContentProvider(true);
	}
	
	private Object[] filterChildrenForRange(IJavaElement[] allChildren, Node node) {
		int javaPositionStart = ((NodeImpl) node).getStartOffset();
		int javaPositionEnd   = ((NodeImpl) node).getEndOffset();
		
		Object[] result =new Object[0];
		
		
		Vector validChildren = new Vector();
		for (int i = 0; i < allChildren.length; i++) {
			if (allChildren[i] instanceof IJavaElement && allChildren[i].getElementType() != IJavaElement.PACKAGE_DECLARATION) {
				ISourceRange range = null;
				if (allChildren[i]  instanceof SourceRefElement) {
					try {
						range = ((SourceRefElement)allChildren[i] ).getSourceRange();
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (allChildren[i].getElementType() == IJavaElement.TYPE || (javaPositionStart <= range.getOffset() && range.getLength() + range.getOffset() <= (javaPositionEnd))) {
					
				int htmllength = range==null?0:range.getLength();
				int htmloffset = range==null?0:range.getOffset();
				if(htmllength<0 || htmloffset<0) {
					continue;
				}
				Position position = new Position(htmloffset, htmllength);
				validChildren.add( getJsNode(node.getParentNode(), allChildren[i], position));
					
				
				}
			}
		}
		if (validChildren.size() > 0) {
			result = validChildren.toArray();
		}
		if (result == null || result.length == 0) {
			return new IJavaElement[0];
		}
		return result;
	}
	
	private synchronized Object[] getJSElementsFromNode(Node node, boolean ensureConsistant) {
				
		int startOffset = 0;
		int endOffset = 0;
		int type = node.getNodeType();
		Object[] result = null;
		JsTranslation translation = null;
		if (node.getNodeType() == Node.TEXT_NODE && (node instanceof NodeImpl)) {
			startOffset = ((NodeImpl) node).getStartOffset();
			endOffset = ((NodeImpl) node).getEndOffset();
			ICompilationUnit unit = getLazyCu(node);
			try {
				if(ensureConsistant) unit.makeConsistent(getProgressMonitor());
			} catch (JavaModelException ex1) {
				// TODO Auto-generated catch block
				ex1.printStackTrace();
			}
			try {
				result = filterChildrenForRange(unit.getChildren(),node);
			} catch (JavaModelException ex) {
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
	
	private Object getJsNode(Node parent, IJavaElement root, Position position) {
		JsJfaceNode instance = null;
		if (root.getElementType() == IJavaElement.TYPE) {
			instance = new JsJfaceNode(parent, root, position, ((SourceRefElement) root).getElementName());
		} else if (root.getElementType() == IJavaElement.FIELD) {
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
		if (node instanceof IJavaElement) {
			return getJavaElementLabelProvider().getImage(node);
		}
		return super.getLabelImage(node);
	}
	
	
	public String getLabelText(Object node) {
//		if (node instanceof JsJfaceNode) {
//			return ((JsJfaceNode) node).getName();
//		}
		if (node instanceof IJavaElement) {
			return getJavaElementLabelProvider().getText(node);
		}
		return super.getLabelText(node);
	}
	
	
	public Object getParent(Object element) {
		if (element instanceof IJavaElement) {
			return getJavaElementProvider().getParent(element);
		}
		return super.getParent(element);
	}
	
	private ICompilationUnit getLazyCu(Node node) {
		if(lazyCu==null) {
			lazyCu = getTranslation(node).getCompilationUnit();
			try {
				lazyCu.makeConsistent( new NullProgressMonitor() );
			} catch (JavaModelException e) {
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
		return translationAdapter.getJSPTranslation(true);
	}
	
	
	public boolean hasChildren(Object object) {
		if (object instanceof IJavaElement) {
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
