package org.eclipse.wst.xml.ui.tests.commentelement;

import org.eclipse.wst.xml.core.internal.commentelement.CommentElementHandler;
import org.eclipse.wst.xml.core.internal.commentelement.util.CommentElementFactory;
import org.eclipse.wst.xml.core.internal.commentelement.util.TagScanner;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class CommentElementHandlerForFoo implements CommentElementHandler{

	private static final String PREFIX = "foo"; //$NON-NLS-1$
	public Element createElement(Document document, String data, boolean isJSPTag) {

		TagScanner scanner = new TagScanner(data, 1);
		String name = scanner.nextName();
		if (name == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer(name.length() + 4);
		buffer.append(PREFIX);
		buffer.append(':');
		buffer.append(name);
		String tagName = buffer.toString();

		CommentElementFactory factory = new CommentElementFactory(document, isJSPTag, this);
		Element element = factory.create(tagName, CommentElementFactory.IS_START);

		// set attributes
		String attrName = scanner.nextName();
		while (attrName != null) {
			String attrValue = scanner.nextValue();
			Attr attr = document.createAttribute(attrName);
			if (attr != null) {
				if (attrValue != null)
					attr.setValue(attrValue);
				element.setAttributeNode(attr);
			}
			attrName = scanner.nextName();
		}
		return element;
	}

	public String generateEndTagContent(IDOMElement element) {
		return null;
	}

	public String generateStartTagContent(IDOMElement element) {
		StringBuffer buffer = new StringBuffer();
		buffer.append('#');
		buffer.append(element.getLocalName());

		NamedNodeMap attributes = element.getAttributes();
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			Attr attr = (Attr) attributes.item(i);
			if (attr == null) {
				continue;
			}
			buffer.append(' ');
			String attrName = attr.getNodeName();
			if (attrName != null) {
				buffer.append(attrName);
			}
			String attrValue = attr.getNodeValue();
			if (attrValue != null) {
				// attr name only for HTML boolean and JSP
				buffer.append('=');
				buffer.append(attrValue);
			}
		}

		return buffer.toString();
	}

	public boolean isCommentElement(IDOMElement element) {
		String prefix = element.getPrefix();
		if (prefix == null || !prefix.equals(PREFIX)) {
			return false;
		}
		String tagName = element.getTagName();
		if (tagName.length() <= 4) {
			return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return true;
	}

}
