package org.eclipse.wst.jsdt.web.ui.views.contentoutline;

import java.util.Collection;

import org.eclipse.jface.text.Position;

import org.eclipse.wst.jsdt.core.IJavaElement;
import org.eclipse.wst.jsdt.internal.core.JavaElement;
import org.eclipse.wst.jsdt.internal.core.SourceMethod;
import org.eclipse.wst.sse.core.internal.model.FactoryRegistry;
import org.eclipse.wst.sse.core.internal.provisional.AbstractNotifier;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class JsJfaceNode_HYBRID extends SourceMethod implements INodeNotifier, IndexedRegion,Node{
	
	private FactoryRegistry  adapterRegistry;
	private Position fDocPosition;
	private JsAdaptableNode adaptableDomNode = new JsAdaptableNode();
	private Node parentDomNode;
	private SourceMethod parentJavaNode;
	
	public JsJfaceNode_HYBRID(Node parentDomNode, IJavaElement parent, Position structureDocLocation){
		super((JavaElement)parent.getParent(),parent.getElementName(),((SourceMethod)parent).getParameterTypes());
		parentJavaNode = (SourceMethod)parent;
		fDocPosition = structureDocLocation;
		this.parentDomNode=parentDomNode;
	}
	 
    private class JsAdaptableNode extends ElementImpl{
        public FactoryRegistry getFactoryRegistry() {
             return adapterRegistry;
        }
    }
    
 
	
    public void setAdapterRegistry(FactoryRegistry registry){
        this.adapterRegistry = registry;
    }
    public void addAdapter(INodeAdapter adapter) {
        adaptableDomNode.addAdapter(adapter);
        
    }

    public INodeAdapter getAdapterFor(Object type) {
      
        Object adapter  = parentJavaNode.getAdapter(type.getClass());
        return adaptableDomNode.getAdapterFor(type);
    }


    public Collection getAdapters() {
    	System.out.println("get adapters");
        return adaptableDomNode.getAdapters();
    }


    public INodeAdapter getExistingAdapter(Object type) {
    	System.out.println("get existing adapters");
    	return adaptableDomNode.getExistingAdapter(type);
    }


    public void notify(int eventType, Object changedFeature, Object oldValue, Object newValue, int pos) {
        adaptableDomNode.notify(eventType, changedFeature, oldValue, newValue, pos);
        
    }


    public void removeAdapter(INodeAdapter adapter) {
        adaptableDomNode.removeAdapter(adapter);
    }

    public int getEndOffset() {
        return fDocPosition.getOffset() + fDocPosition.getLength();
    }

    public int getLength() {
        return fDocPosition.getLength();
    }

    public int getStartOffset() {
        return fDocPosition.getOffset();
    }
	public boolean contains(int testPosition) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method contains" );
		return false;
	}
	
	public Node getParentDomNode(){
		return this.parentDomNode;
	
	}
	public Node appendChild(Node newChild) throws DOMException {
		// TODO Auto-generated method stub
		return adaptableDomNode.appendChild(newChild);
	}
	public Node cloneNode(boolean deep) {
		// TODO Auto-generated method stub
		return adaptableDomNode.cloneNode(deep);
	}
	public short compareDocumentPosition(Node other) throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method compareDocumentPosition" );
		return adaptableDomNode.compareDocumentPosition(other);
	}
	public NamedNodeMap getAttributes() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getAttributes" );
		return adaptableDomNode.getAttributes();
	}
	public String getBaseURI() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getBaseURI" );
		return adaptableDomNode.getBaseURI();
	}
	public NodeList getChildNodes() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getChildNodes" );
		return adaptableDomNode.getChildNodes();
	}
	public Object getFeature(String feature, String version) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getFeature" );
		return adaptableDomNode.getFeature(feature, version);
	}
	
	public Node getFirstChild() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getFirstChild" );
		return adaptableDomNode.getFirstChild();
		
	}
	public Node getLastChild() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getLastChild" );
		return adaptableDomNode.getLastChild();
	}
	public String getLocalName() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getLocalName" );
		return adaptableDomNode.getLocalName();
	}
	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getNamespaceURI" );
		return adaptableDomNode.getNamespaceURI();
	}
	public Node getNextSibling() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getNextSibling" );
		return adaptableDomNode.getNextSibling();
	}
	public String getNodeName() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getNodeName" );
		return adaptableDomNode.getNodeName();
	}
	public short getNodeType() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getNodeType" );
		return parentDomNode.getNodeType();
	}
	public String getNodeValue() throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getNodeValue" );
		return adaptableDomNode.getNodeValue();
	}
	public Document getOwnerDocument() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getOwnerDocument" );
		return parentDomNode.getOwnerDocument();
	}
	public Node getParentNode() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getParentNode" );
		return getParentDomNode();
	}
	public String getPrefix() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getPrefix" );
		return adaptableDomNode.getPrefix();
	}
	public Node getPreviousSibling() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getPreviousSibling" );
		return adaptableDomNode.getPreviousSibling();
	}
	public String getTextContent() throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getTextContent" );
		return adaptableDomNode.getTextContent();
	}
	public Object getUserData(String key) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method getUserData" );
		return adaptableDomNode.getUserData(key);
	}
	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method hasAttributes" );
		return adaptableDomNode.hasAttributes();
	}
	public boolean hasChildNodes() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method hasChildNodes" );
		return adaptableDomNode.hasChildNodes();
	}
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method insertBefore" );
		return adaptableDomNode.insertBefore(newChild, refChild);
	}
	public boolean isDefaultNamespace(String namespaceURI) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method isDefaultNamespace" );
		return adaptableDomNode.isDefaultNamespace(namespaceURI);
	}
	public boolean isEqualNode(Node arg) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method isEqualNode" );
		return adaptableDomNode.isEqualNode(arg);
	}
	public boolean isSameNode(Node other) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method isSameNode" );
		return (other instanceof JsJfaceNode && other==this);
	}
	public boolean isSupported(String feature, String version) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method isSupported" );
		return adaptableDomNode.isSupported(feature, version);
	}
	public String lookupNamespaceURI(String prefix) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method lookupNamespaceURI" );
		return adaptableDomNode.lookupNamespaceURI(prefix);
	}
	public String lookupPrefix(String namespaceURI) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method lookupPrefix" );
		return adaptableDomNode.lookupPrefix(namespaceURI);
	}
	public void normalize() {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method normalize" );
		adaptableDomNode.normalize();
		
	}
	public Node removeChild(Node oldChild) throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method removeChild" );
		return adaptableDomNode.removeChild(oldChild);
	}
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method replaceChild" );
		return adaptableDomNode.replaceChild(newChild, oldChild);
	}
	public void setNodeValue(String nodeValue) throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method setNodeValue" );
		adaptableDomNode.setNodeValue(nodeValue);
		
	}
	public void setPrefix(String prefix) throws DOMException {
		// TODO Auto-generated method stub
		adaptableDomNode.setPrefix(prefix);
		
	}
	public void setTextContent(String textContent) throws DOMException {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method setTextContent" );
		adaptableDomNode.setTextContent(textContent);
	}
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		// TODO Auto-generated method stub
		System.out.println("Umiplement method setUserData" );
		return adaptableDomNode.setUserData(key, data, handler);
	}
}
