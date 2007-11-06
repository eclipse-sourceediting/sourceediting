package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.Collection;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.core.JavaModelException;
import org.eclipse.wst.jsdt.internal.core.JavaElement;
import org.eclipse.wst.jsdt.ui.JavaElementLabelProvider;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.internal.java.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslation;
import org.eclipse.wst.jsdt.web.core.internal.java.JsTranslationAdapter;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.AbstractNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;

public class JsJfaceNode extends ElementImpl implements IndexedRegion, INodeNotifier, Node, IJavaWebNode {
	private class JsAdaptableNode extends AbstractNotifier {
		
		public FactoryRegistry getFactoryRegistry() {
			return adapterRegistry;
		}
	}
	private JsAdaptableNode adaptableDomNode = new JsAdaptableNode();
	private FactoryRegistry adapterRegistry;
	private Position fDocPosition;
//	private Node parent;
	private String typeName;
	//private IJavaElement dirtyElement;
	private boolean hasChildren;
	//private String name;
	private Image me;
	
	public JsJfaceNode(Node parent, IJavaElement originalElement, Position structureDocLocation) {
		this(parent, originalElement, structureDocLocation, null);
	}
	
	public JsJfaceNode(Node parent, IJavaElement originalElement, Position structureDocLocation, String typeName) {
		//super();
		super(((ElementImpl)parent));
		// super(parentObject, parentObject.getElementName());
		fDocPosition = structureDocLocation;
		//this.parent = parent;
		this.typeName = typeName;
		try {
			hasChildren=((JavaElement)originalElement).hasChildren();
		} catch (JavaModelException ex) {
			hasChildren=false;
		}
		removeAttributes();
		me = (new JavaElementLabelProvider()).getImage(originalElement);
	}
	
	public Image getImage() {
		return me;
	}

	public boolean hasChildren() {
//		try {
//			return ((JavaElement)this.dirtyElement).hasChildren();
//		} catch (JavaModelException ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//		}
		return hasChildren;
	}
	

	public void setName(String name) {
		super.setTagName(name);
	}
		
	public void addAdapter(INodeAdapter adapter) {
		adaptableDomNode.addAdapter(adapter);
	}
	
	
	public boolean contains(int testPosition) {
		// TODO Auto-generated method stub
		System.out.println(Messages.getString("JsJfaceNode.0")); //$NON-NLS-1$
		return false;
	}
	
	
	public boolean equals(Object o) {
		return (o != null && o instanceof JsJfaceNode && ((JsJfaceNode) o).fDocPosition == this.fDocPosition);
	}
	
// public static Object getInstance(Object parent, Position structureDocLocation
// ){
// //return new JsJfaceNode((JavaElement)parent.getParent(),
// parent.getElementName(),structureDocLocation);
// Vector interfaces = new Vector();
// interfaces.addAll(Arrays.asList(parent.getClass().getInterfaces()));
// JsJfaceNode me = new JsJfaceNode(parent,structureDocLocation);
// interfaces.addAll(Arrays.asList(me.getClass().getInterfaces()));
// Object proxy = null;
// try {
// proxy= Proxy.newProxyInstance(
// parent.getClass().getClassLoader(),
// (Class[])interfaces.toArray(new Class[]{}),me);
// } catch (Exception e) {
// // TODO Auto-generated catch block
// System.out.println(e);
// }
// return proxy;
// }
//   
	
	public INodeAdapter getAdapterFor(Object type) {
		return adaptableDomNode.getAdapterFor(type);
	}
	
	
	public Collection getAdapters() {
		return adaptableDomNode.getAdapters();
	}
	
	
	public int getEndOffset() {
		return fDocPosition.getOffset() + fDocPosition.getLength();
	}
	
	
	public INodeAdapter getExistingAdapter(Object type) {
		// TODO Auto-generated method stub
		return adaptableDomNode.getExistingAdapter(type);
	}
	
	public synchronized IJavaElement getJavaElement() {
		/*
		 * since this may become 'stale' we need to rediscover our element every
		 * time we're asked
		 */
		JsTranslation tran = getTranslation();
		int startOffset = getStartOffset();
		int endOffset = getLength();
		if (typeName != null) {
			IJavaElement myType = tran.getCompilationUnit().getType(typeName);
			return myType;
		}
		IJavaElement elements[] = tran.getAllElementsInJsRange(startOffset, startOffset + endOffset);
		if (elements != null) {
			return elements[0];
		} else {
			System.out.println(Messages.getString("JsJfaceNode.1")); //$NON-NLS-1$
			return null;
		}
	}
	
	
	public int getLength() {
		return fDocPosition.getLength();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.core.internal.document.NodeImpl#getOwnerDocument()
	 */
	

	
	

	
	
	public int getStartOffset() {
		return fDocPosition.getOffset();
	}
	
// private Method[] getMethods(){
// // returns the methods this class supports (as declared in interfaces)
// Class[] interfaces = getClass().getInterfaces();
// Vector vMethods = new Vector();
// for(int i = 0;i<interfaces.length;i++){
// Method methods[] = interfaces[i].getDeclaredMethods();
// vMethods.addAll(Arrays.asList(methods));
// }
//        
// return (Method[])vMethods.toArray();
// }
// public Object invoke(Object proxy, Method method, Object[] args) throws
// Throwable {
// Object result;
// Method[] myMethods = getMethods();
//        
// try {
// for(int i = 0;i<myMethods.length;i++){
// if(myMethods[i]==method){
// return method.invoke(this, args);
// }
// }
// result = method.invoke(parentType, args);
// } catch (InvocationTargetException e) {
// throw e.getTargetException();
// } catch (Exception e) {
// throw new RuntimeException("unexpected invocation exception: " +
// e.getMessage());
// }
//
// return result;
// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.xml.core.internal.document.NodeImpl#getStructuredDocument()
	 */
	

	
	public JsTranslation getTranslation() {
		IStructuredModel model = null;
		IModelManager modelManager = StructuredModelManager.getModelManager();
		IDOMDocument xmlDoc = null;
		try {
			if (modelManager != null) {
				IStructuredDocument doc = getStructuredDocument();
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
	
	
	public void notify(int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
		adaptableDomNode.notify(eventType, changedFeature, oldValue, newValue, pos);
	}
	
	
	public void removeAdapter(INodeAdapter adapter) {
		adaptableDomNode.removeAdapter(adapter);
	}
	
	public void setAdapterRegistry(FactoryRegistry registry) {
		this.adapterRegistry = registry;
	}
}
