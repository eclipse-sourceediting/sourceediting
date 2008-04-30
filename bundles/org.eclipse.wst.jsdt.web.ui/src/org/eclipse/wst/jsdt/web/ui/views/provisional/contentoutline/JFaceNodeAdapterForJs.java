package org.eclipse.wst.jsdt.web.ui.views.provisional.contentoutline;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.html.ui.internal.contentoutline.JFaceNodeAdapterForHTML;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.ui.JavaScriptElementLabelProvider;
import org.eclipse.wst.jsdt.ui.StandardJavaScriptElementContentProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;

import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
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
		if (object instanceof IJavaScriptElement) {
			return getJavaElementProvider().getChildren(object);
		}
		Node node = (Node) object;
		if (isJSElementParent(node)) {
// Object[] results = getJSElementsFromNode(node.getFirstChild());
// IMember[] allResults = new IMember[results.length];
// for(int i = 0;i<results.length;i++) {
// if(results[i]==null) continue;
// IMember member = ((IMember)results[i]);
// allResults[i] = member;
			// }
			return getJSElementsFromNode(node.getFirstChild());
		}
		return super.getChildren(object);
	}
	
	
	public Object[] getElements(Object object) {
		if (object instanceof IJavaScriptElement) {
			return getJavaElementProvider().getElements(object);
		}
		return super.getElements(object);
	}
	
	private JavaScriptElementLabelProvider getJavaElementLabelProvider() {
		return new JavaScriptElementLabelProvider();
	}
	
	private StandardJavaScriptElementContentProvider getJavaElementProvider() {
		return new StandardJavaScriptElementContentProvider(true);
	}
	
	private synchronized Object[] getJSElementsFromNode(Node node) {
		if (node == null) {
			return new Object[0];
		}
		int startOffset = 0;
		int endOffset = 0;
		int type = node.getNodeType();
		IJavaScriptElement[] result = null;
		IJsTranslation translation = null;
		if (node.getNodeType() == Node.TEXT_NODE && (node instanceof NodeImpl)) {
			startOffset = ((NodeImpl) node).getStartOffset();
			endOffset = ((NodeImpl) node).getEndOffset();
			translation = getTranslation(node);
			result = translation.getAllElementsInJsRange(startOffset, endOffset);
		}
		return result;
//			
// if (result == null) return null;
// Object[] newResults = new Object[result.length];
// for (int i = 0; i < result.length; i++) {
// int htmllength = 0;
// int htmloffset = 0;
// Position position = null;
// try {
// htmllength = ((SourceRefElement) (result[i])).getSourceRange().getLength();
// htmloffset = translation.getJspOffset(((SourceRefElement)
// (result[i])).getSourceRange().getOffset());
// position = new Position(htmloffset, htmllength);
// } catch (JavaScriptModelException e) {
// e.printStackTrace();
// }
// newResults[i] = getJsNode(node.getParentNode(), (IJavaScriptElement) result[i],
// position);
// }
// return newResults;
	}
	
	
	public Image getLabelImage(Object node) {
		if (node instanceof IJavaScriptElement) {
			return getJavaElementLabelProvider().getImage(node);
		}
		return super.getLabelImage(node);
	}
	
	
	public String getLabelText(Object node) {
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
		if (object instanceof IJavaScriptElement) {
			return getJavaElementProvider().hasChildren(object);
		}
		Node node = (Node) object;
		if (isJSElementParent(node)) {
			Object[] results = getJSElementsFromNode(node.getFirstChild());
			return (results != null && results.length > 0);
		}
		return super.hasChildren(object);
	}
	
	private boolean isJSElementParent(Node node) {
		return (node.getNodeName().equalsIgnoreCase("script")); //$NON-NLS-1$
	}
}
