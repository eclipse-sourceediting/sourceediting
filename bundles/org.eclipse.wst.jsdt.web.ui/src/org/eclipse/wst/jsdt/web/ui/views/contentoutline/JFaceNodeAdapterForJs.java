package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.core.JavaElement;
import org.eclipse.wst.jsdt.internal.core.SourceRefElement;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;
import org.eclipse.wst.jsdt.ui.StandardJavaElementContentProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslationAdapter;
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
					JsTranslation translation = getTranslation(parent);
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
			Object[] results = getJSElementsFromNode(node.getFirstChild());
			return results;
		}
		return super.getChildren(object);
	}
	
	
	public Object[] getElements(Object object) {
		if (object instanceof IJavaElement) {
			return getJavaElementProvider().getElements(object);
		}
		return super.getElements(object);
	}
	
	private JavaElementLabelProvider getJavaElementLabelProvider() {
		return new JavaElementLabelProvider();
	}
	
	private StandardJavaElementContentProvider getJavaElementProvider() {
		return new StandardJavaElementContentProvider(true);
	}
	
	private synchronized Object[] getJSElementsFromNode(Node node) {
		int startOffset = 0;
		int endOffset = 0;
		int type = node.getNodeType();
		IJavaElement[] result = null;
		JsTranslation translation = null;
		if (node.getNodeType() == Node.TEXT_NODE && (node instanceof NodeImpl)) {
			startOffset = ((NodeImpl) node).getStartOffset();
			endOffset = ((NodeImpl) node).getEndOffset();
			translation = getTranslation(node);
			result = translation.getAllElementsInJsRange(startOffset, endOffset);
		}
		if (result == null) {
			return null;
		}
		Object[] newResults = new Object[result.length];
		for (int i = 0; i < result.length; i++) {
			int htmllength = 0;
			int htmloffset = 0;
			Position position = null;
			try {
				htmllength = ((SourceRefElement) (result[i])).getSourceRange().getLength();
				htmloffset = ((SourceRefElement) (result[i])).getSourceRange().getOffset();
				position = new Position(htmloffset, htmllength);
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			newResults[i] = getJsNode(node.getParentNode(), result[i], position);
		}
		return newResults;
	}
	
	private Object getJsNode(Node parent, IJavaElement root, Position position) {
		JsJfaceNode instance = null;
		if (root.getElementType() == IJavaElement.TYPE) {
			instance = new JsJfaceNode(parent, position, ((SourceRefElement) root).getElementName());
		} else if (root.getElementType() == IJavaElement.FIELD) {
			/* Field refrence, possibly to a type may need to implement later */
			instance = new JsJfaceNode(parent, position);
		} else {
			instance = new JsJfaceNode(parent, position);
		}
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
			return getJavaElementLabelProvider().getImage(((JsJfaceNode) node).getJavaElement());
		}
		if (node instanceof IJavaElement) {
			return getJavaElementLabelProvider().getImage(node);
		}
		return super.getLabelImage(node);
	}
	
	
	public String getLabelText(Object node) {
		if (node instanceof JsJfaceNode) {
			return getJavaElementLabelProvider().getText(((JsJfaceNode) node).getJavaElement());
		}
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
	
	private JsTranslation getTranslation(Node node) {
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
		return translationAdapter.getJSPTranslation();
	}
	
	
	public boolean hasChildren(Object object) {
		if (object instanceof IJavaElement) {
			return getJavaElementProvider().hasChildren(object);
		}
		Node node = (Node) object;
		if (node instanceof IJavaWebNode) {
			JavaElement enclosedElement = (JavaElement) ((IJavaWebNode) object).getJavaElement();
			if (enclosedElement != null) {
				try {
					return enclosedElement.hasChildren();
				} catch (JavaModelException ex) {
				}
			}
		}
		if (isJSElementParent(node)) {
			Object[] nodes = getJSElementsFromNode(node.getFirstChild());
			boolean hasElements = (nodes != null && nodes.length > 0);
			return hasElements;
		}
		return super.hasChildren(object);
	}
	
	private boolean isJSElementParent(Node node) {
		return (node.hasChildNodes() && node.getNodeName().equalsIgnoreCase("script"));
	}
}
