/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.xml.core.internal.document;



import org.eclipse.wst.xml.core.internal.commentelement.impl.CommentElementRegistry;
import org.eclipse.wst.xml.core.internal.contentmodel.CMAttributeDeclaration;
import org.eclipse.wst.xml.core.internal.contentmodel.CMDataType;
import org.eclipse.wst.xml.core.internal.provisional.IXMLCharEntity;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.ISourceGenerator;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


/** 
 */
public class XMLGeneratorImpl implements ISourceGenerator {
	private static final String CDATA_CLOSE = "]]>";//$NON-NLS-1$
	private static final String CDATA_OPEN = "<![CDATA[";//$NON-NLS-1$
	private static final String COMMENT_CLOSE = "-->";//$NON-NLS-1$
	private static final String COMMENT_OPEN = "<!--";//$NON-NLS-1$
	private static final String DOCTYPE_OPEN = "<!DOCTYPE";//$NON-NLS-1$
	private static final String EMPTY_CLOSE = " />";//$NON-NLS-1$
	private static final String END_OPEN = "</";//$NON-NLS-1$

	private static XMLGeneratorImpl instance = null;
	private static final String PI_CLOSE = "?>";//$NON-NLS-1$
	private static final String PI_OPEN = "<?";//$NON-NLS-1$
	private static final String PUBLIC_ID = "PUBLIC";//$NON-NLS-1$
	private static final String SSI_PREFIX = "ssi";//$NON-NLS-1$
	//private static final String SSI_FEATURE = "SSI";//$NON-NLS-1$
	private static final String SSI_TOKEN = "#";//$NON-NLS-1$
	private static final String SYSTEM_ID = "SYSTEM";//$NON-NLS-1$
	private static final String TAG_CLOSE = ">";//$NON-NLS-1$

	/**
	 */
	public synchronized static ISourceGenerator getInstance() {
		if (instance == null)
			instance = new XMLGeneratorImpl();
		return instance;
	}

	/**
	 */
	//private boolean isCommentTag(XMLElement element) {
	//	if (element == null) return false;
	//	DocumentImpl document = (DocumentImpl)element.getOwnerDocument();
	//	if (document == null) return false;
	//	DocumentTypeAdapter adapter = document.getDocumentTypeAdapter();
	//	if (adapter == null) return false;
	//	if (!adapter.hasFeature(SSI_FEATURE)) return false;
	//	String prefix = element.getPrefix();
	//	return (prefix != null && prefix.equals(SSI_PREFIX));
	//}
	/**
	 * Helper to modify the tag name in sub-classes
	 */
	private static void setTagName(Element element, String tagName) {
		if (element == null || tagName == null)
			return;
		((ElementImpl) element).setTagName(tagName);
	}

	/**
	 * XMLModelGenerator constructor
	 */
	private XMLGeneratorImpl() {
		super();
	}

	/**
	 */
	public String generateAttrName(Attr attr) {
		if (attr == null)
			return null;
		String attrName = attr.getName();
		if (attrName == null)
			return null;
		if (attrName.startsWith(JSPTag.TAG_OPEN)) {
			if (!attrName.endsWith(JSPTag.TAG_CLOSE)) {
				// close JSP
				return (attrName + JSPTag.TAG_CLOSE);
			}
		}
		if (((IDOMAttr) attr).isGlobalAttr() && CMNodeUtil.getAttributeDeclaration(attr) != null) {
			switch (getAttrNameCase(attr)) {
				case DocumentTypeAdapter.UPPER_CASE :
					attrName = attrName.toUpperCase();
					break;
				case DocumentTypeAdapter.LOWER_CASE :
					attrName = attrName.toLowerCase();
					break;
				default :
					// ASIS_CASE
					break;
			}
		}
		return attrName;
	}

	/**
	 */
	public String generateAttrValue(Attr attr) {
		return generateAttrValue(attr, (char) 0); // no quote preference
	}

	/**
	 */
	public String generateAttrValue(Attr attr, char quote) {
		if (attr == null)
			return null;
		String name = attr.getName();
		SourceValidator validator = new SourceValidator(attr);
		String value = validator.convertSource(((IDOMNode) attr).getValueSource());
		if (value == null || value.length() == 0) {
			if (name != null && name.startsWith(JSPTag.TAG_OPEN))
				return null;
			if (isBooleanAttr(attr)) {
				if (((AttrImpl) attr).isXMLAttr()) {
					// generate the name as value
					value = attr.getName();
				} else {
					// not to generate '=' and value for HTML boolean
					return null;
				}
			}
		}
		return generateAttrValue(value, quote);
	}

	/**
	 */
	public String generateAttrValue(String value, char quote) {
		// assume the valid is already validated not to include both quotes
		if (quote == '"') {
			if ((value != null) && (value.indexOf('"') >= 0))
				quote = '\''; // force
		} else if (quote == '\'') {
			if ((value != null) && (value.indexOf('\'') >= 0))
				quote = '"'; // force
		} else { // no preference
			if ((value != null) && (value.indexOf('"') < 0))
				quote = '"';
			else
				quote = '\'';
		}

		int length = (value == null ? 0 : value.length());
		StringBuffer buffer = new StringBuffer(length + 2);
		buffer.append(quote);
		if (value != null)
			buffer.append(value);
		buffer.append(quote);
		return buffer.toString();
	}

	/**
	 * generateCDATASection method
	 * 
	 * @return java.lang.String
	 * @param comment
	 *            org.w3c.dom.CDATASection
	 */
	public String generateCDATASection(CDATASection cdata) {
		if (cdata == null)
			return null;

		String data = cdata.getData();
		int length = (data != null ? data.length() : 0);
		StringBuffer buffer = new StringBuffer(length + 16);
		buffer.append(CDATA_OPEN);
		if (data != null)
			buffer.append(data);
		buffer.append(CDATA_CLOSE);
		return buffer.toString();
	}

	/**
	 * generateChild method
	 * 
	 * @return java.lang.String
	 * @param org.w3c.dom.Node
	 */
	public String generateChild(Node parentNode) {
		if (parentNode == null)
			return null;
		if (!parentNode.hasChildNodes())
			return null;

		StringBuffer buffer = new StringBuffer();
		for (Node child = parentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
			String childSource = generateSource(child);
			if (childSource != null)
				buffer.append(childSource);
		}
		return buffer.toString();
	}

	/**
	 */
	public String generateCloseTag(Node node) {
		if (node == null)
			return null;

		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE : {
				ElementImpl element = (ElementImpl) node;
				if (element.isCommentTag()) {
					if (element.isJSPTag())
						return JSPTag.COMMENT_CLOSE;
					return COMMENT_CLOSE;
				}
				if (element.isJSPTag())
					return JSPTag.TAG_CLOSE;
				if (element.isEmptyTag())
					return EMPTY_CLOSE;
				return TAG_CLOSE;
			}
			case Node.COMMENT_NODE : {
				CommentImpl comment = (CommentImpl) node;
				if (comment.isJSPTag())
					return JSPTag.COMMENT_CLOSE;
				return COMMENT_CLOSE;
			}
			case Node.DOCUMENT_TYPE_NODE :
				return TAG_CLOSE;
			case Node.PROCESSING_INSTRUCTION_NODE :
				return PI_CLOSE;
			case Node.CDATA_SECTION_NODE :
				return CDATA_CLOSE;
			default :
				break;
		}

		return null;
	}

	/**
	 * generateComment method
	 * 
	 * @return java.lang.String
	 * @param comment
	 *            org.w3c.dom.Comment
	 */
	public String generateComment(Comment comment) {
		if (comment == null)
			return null;

		String data = comment.getData();
		int length = (data != null ? data.length() : 0);
		StringBuffer buffer = new StringBuffer(length + 8);
		CommentImpl impl = (CommentImpl) comment;
		if (!impl.isJSPTag())
			buffer.append(COMMENT_OPEN);
		else
			buffer.append(JSPTag.COMMENT_OPEN);
		if (data != null)
			buffer.append(data);
		if (!impl.isJSPTag())
			buffer.append(COMMENT_CLOSE);
		else
			buffer.append(JSPTag.COMMENT_CLOSE);
		return buffer.toString();
	}

	/**
	 * generateDoctype method
	 * 
	 * @return java.lang.String
	 * @param docType
	 *            org.w3c.dom.DocumentType
	 */
	public String generateDoctype(DocumentType docType) {
		if (docType == null)
			return null;

		String name = docType.getName();
		int length = (name != null ? name.length() : 0);
		StringBuffer buffer = new StringBuffer(length + 16);
		buffer.append(DOCTYPE_OPEN);
		buffer.append(' ');
		if (name != null)
			buffer.append(name);
		DocumentTypeImpl dt = (DocumentTypeImpl) docType;
		String publicID = dt.getPublicId();
		String systemID = dt.getSystemId();
		if (publicID != null) {
			buffer.append(' ');
			buffer.append(PUBLIC_ID);
			buffer.append(' ');
			buffer.append('"');
			buffer.append(publicID);
			buffer.append('"');
			if (systemID != null) {
				buffer.append(' ');
				buffer.append('"');
				buffer.append(systemID);
				buffer.append('"');
			}
		} else {
			if (systemID != null) {
				buffer.append(' ');
				buffer.append(SYSTEM_ID);
				buffer.append(' ');
				buffer.append('"');
				buffer.append(systemID);
				buffer.append('"');
			}
		}
		buffer.append('>');
		return buffer.toString();
	}

	/**
	 * generateElement method
	 * 
	 * @return java.lang.String
	 * @param element
	 *            Element
	 */
	public String generateElement(Element element) {
		if (element == null)
			return null;

		// if empty tag is preferrable, generate as empty tag
		ElementImpl impl = (ElementImpl) element;
		if (impl.preferEmptyTag())
			impl.setEmptyTag(true);

		StringBuffer buffer = new StringBuffer();
		String startTag = generateStartTag(element);
		if (startTag != null)
			buffer.append(startTag);
		String child = generateChild(element);
		if (child != null)
			buffer.append(child);
		String endTag = generateEndTag(element);
		if (endTag != null)
			buffer.append(endTag);
		return buffer.toString();
	}

	/**
	 * generateEndTag method
	 * 
	 * @return java.lang.String
	 * @param element
	 *            org.w3c.dom.Element
	 */
	public String generateEndTag(Element element) {
		if (element == null)
			return null;

		ElementImpl impl = (ElementImpl) element;

		// first check if tag adapter exists
		TagAdapter adapter = (TagAdapter) impl.getExistingAdapter(TagAdapter.class);
		if (adapter != null) {
			String endTag = adapter.getEndTag(impl);
			if (endTag != null)
				return endTag;
		}

		if (impl.isEmptyTag())
			return null;
		if (!impl.isContainer())
			return null;
		if (impl.isJSPTag())
			return JSPTag.TAG_CLOSE;

		String tagName = generateTagName(element);
		int length = (tagName != null ? tagName.length() : 0);
		StringBuffer buffer = new StringBuffer(length + 4);
		buffer.append(END_OPEN);
		if (tagName != null)
			buffer.append(tagName);
		buffer.append('>');
		return buffer.toString();
	}

	/**
	 * generateEntityRef method
	 * 
	 * @return java.lang.String
	 * @param entityRef
	 *            org.w3c.dom.EntityReference
	 */
	public String generateEntityRef(EntityReference entityRef) {
		if (entityRef == null)
			return null;

		String name = entityRef.getNodeName();
		int length = (name != null ? name.length() : 0);
		StringBuffer buffer = new StringBuffer(length + 4);
		buffer.append('&');
		if (name != null)
			buffer.append(name);
		buffer.append(';');
		return buffer.toString();
	}

	/**
	 * generatePI method
	 * 
	 * @return java.lang.String
	 * @param pi
	 *            org.w3c.dom.ProcessingInstruction
	 */
	public String generatePI(ProcessingInstruction pi) {
		if (pi == null)
			return null;

		String target = pi.getTarget();
		String data = pi.getData();
		int length = (target != null ? target.length() : 0);
		if (data != null)
			length += data.length();
		StringBuffer buffer = new StringBuffer(length + 8);
		buffer.append(PI_OPEN);
		if (target != null)
			buffer.append(target);
		buffer.append(' ');
		if (data != null)
			buffer.append(data);
		buffer.append(PI_CLOSE);
		return buffer.toString();
	}

	/**
	 * generateSource method
	 * 
	 * @return java.lang.String
	 * @param node
	 *            org.w3c.dom.Node
	 */
	public String generateSource(Node node) {
		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE :
				return generateElement((Element) node);
			case Node.TEXT_NODE :
				return generateText((Text) node);
			case Node.COMMENT_NODE :
				return generateComment((Comment) node);
			case Node.DOCUMENT_TYPE_NODE :
				return generateDoctype((DocumentType) node);
			case Node.PROCESSING_INSTRUCTION_NODE :
				return generatePI((ProcessingInstruction) node);
			case Node.CDATA_SECTION_NODE :
				return generateCDATASection((CDATASection) node);
			case Node.ENTITY_REFERENCE_NODE :
				return generateEntityRef((EntityReference) node);
			default :
				// DOCUMENT
				break;
		}
		return generateChild(node);
	}

	/**
	 * generateStartTag method
	 * 
	 * @return java.lang.String
	 * @param element
	 *            Element
	 */
	public String generateStartTag(Element element) {
		if (element == null)
			return null;

		ElementImpl impl = (ElementImpl) element;

		if (impl.isJSPTag()) {
			// check if JSP content type and JSP Document
			IDOMDocument document = (IDOMDocument) element.getOwnerDocument();
			if (document != null && document.isJSPType()) {
				if (document.isJSPDocument() && !impl.hasChildNodes()) {
					impl.setJSPTag(false);
				}
			} else {
				impl.setJSPTag(false);
			}
		}
		if (impl.isCommentTag() && impl.getExistingAdapter(TagAdapter.class) == null) {
			CommentElementRegistry registry = CommentElementRegistry.getInstance();
			registry.setupCommentElement(impl);
		}

		// first check if tag adapter exists
		TagAdapter adapter = (TagAdapter) impl.getExistingAdapter(TagAdapter.class);
		if (adapter != null) {
			String startTag = adapter.getStartTag(impl);
			if (startTag != null)
				return startTag;
		}

		StringBuffer buffer = new StringBuffer();

		if (impl.isCommentTag()) {
			if (impl.isJSPTag())
				buffer.append(JSPTag.COMMENT_OPEN);
			else
				buffer.append(COMMENT_OPEN);
			String tagName = generateTagName(element);
			if (tagName != null)
				buffer.append(tagName);
		} else if (impl.isJSPTag()) {
			buffer.append(JSPTag.TAG_OPEN);
			String tagName = generateTagName(element);
			if (tagName != null)
				buffer.append(tagName);
			if (impl.isContainer())
				return buffer.toString(); // JSP container
		} else {
			buffer.append('<');
			String tagName = generateTagName(element);
			if (tagName != null)
				buffer.append(tagName);
		}

		NamedNodeMap attributes = element.getAttributes();
		int length = attributes.getLength();
		for (int i = 0; i < length; i++) {
			AttrImpl attr = (AttrImpl) attributes.item(i);
			if (attr == null)
				continue;
			buffer.append(' ');
			String attrName = generateAttrName(attr);
			if (attrName != null)
				buffer.append(attrName);
			String attrValue = generateAttrValue(attr);
			if (attrValue != null) {
				// attr name only for HTML boolean and JSP
				buffer.append('=');
				buffer.append(attrValue);
			}
		}

		String closeTag = generateCloseTag(element);
		if (closeTag != null)
			buffer.append(closeTag);

		return buffer.toString();
	}

	/**
	 */
	public String generateTagName(Element element) {
		if (element == null)
			return null;
		IDOMElement xe = (IDOMElement) element;
		String tagName = element.getTagName();
		if (tagName == null)
			return null;
		if (xe.isJSPTag()) {
			if (tagName.equals(JSPTag.JSP_EXPRESSION))
				return JSPTag.EXPRESSION_TOKEN;
			if (tagName.equals(JSPTag.JSP_DECLARATION))
				return JSPTag.DECLARATION_TOKEN;
			if (tagName.equals(JSPTag.JSP_DIRECTIVE))
				return JSPTag.DIRECTIVE_TOKEN;
			if (tagName.startsWith(JSPTag.JSP_DIRECTIVE)) {
				int offset = JSPTag.JSP_DIRECTIVE.length() + 1; // after '.'
				return (JSPTag.DIRECTIVE_TOKEN + tagName.substring(offset));
			}
			return (xe.isCommentTag()) ? tagName : null;
		} else if (tagName.startsWith(JSPTag.TAG_OPEN)) {
			if (!tagName.endsWith(JSPTag.TAG_CLOSE)) {
				// close JSP
				return (tagName + JSPTag.TAG_CLOSE);
			}
		} else if (xe.isCommentTag()) {
			String prefix = element.getPrefix();
			if (prefix.equals(SSI_PREFIX)) {
				return (SSI_TOKEN + element.getLocalName());
			}
		} else {
			if (!xe.isJSPTag() && xe.isGlobalTag() && // global tag
						CMNodeUtil.getElementDeclaration(xe) != null) {
				String newName = tagName;
				switch (getTagNameCase(xe)) {
					case DocumentTypeAdapter.UPPER_CASE :
						newName = tagName.toUpperCase();
						break;
					case DocumentTypeAdapter.LOWER_CASE :
						newName = tagName.toLowerCase();
						break;
				}
				if (newName != tagName) {
					tagName = newName;
					setTagName(element, tagName);
				}
			}
		}
		return tagName;
	}

	/**
	 * generateText method
	 * 
	 * @return java.lang.String
	 * @param text
	 *            org.w3c.dom.Text
	 */
	public String generateText(Text text) {
		if (text == null)
			return null;
		TextImpl impl = (TextImpl) text;
		String source = impl.getTextSource();
		if (source != null)
			return source;
		return generateTextData(text, impl.getData());
	}

	/**
	 */
	public String generateTextData(Text text, String data) {
		if (data == null)
			return null;
		if (text == null)
			return null;
		TextImpl impl = (TextImpl) text;
		if (impl.isJSPContent() || impl.isCDATAContent()) {
			return new SourceValidator(impl).convertSource(data);
		}
		String source = data;

		// convert special characters to character entities
		StringBuffer buffer = null;
		int offset = 0;
		int length = data.length();
		for (int i = 0; i < length; i++) {
			String name = getCharName(data.charAt(i));
			if (name == null)
				continue;
			if (buffer == null)
				buffer = new StringBuffer(length + 8);
			if (i > offset)
				buffer.append(data.substring(offset, i));
			buffer.append('&');
			buffer.append(name);
			buffer.append(';');
			offset = i + 1;
		}
		if (buffer != null) {
			if (length > offset)
				buffer.append(data.substring(offset));
			source = buffer.toString();
		}

		if (source == null || source.length() == 0)
			return null;
		return source;
	}

	/**
	 */
	private int getAttrNameCase(Attr attr) {
		DocumentImpl document = (DocumentImpl) attr.getOwnerDocument();
		if (document == null)
			return DocumentTypeAdapter.STRICT_CASE;
		DocumentTypeAdapter adapter = (DocumentTypeAdapter) document.getAdapterFor(DocumentTypeAdapter.class);
		if (adapter == null)
			return DocumentTypeAdapter.STRICT_CASE;
		return adapter.getAttrNameCase();
	}

	/**
	 */
	private String getCharName(char c) {
		switch (c) {
			case '<' :
				return IXMLCharEntity.LT_NAME;
			case '>' :
				return IXMLCharEntity.GT_NAME;
			case '&' :
				return IXMLCharEntity.AMP_NAME;
			case '"' :
				return IXMLCharEntity.QUOT_NAME;
		}
		return null;
	}

	/**
	 */
	private int getTagNameCase(Element element) {
		DocumentImpl document = (DocumentImpl) element.getOwnerDocument();
		if (document == null)
			return DocumentTypeAdapter.STRICT_CASE;
		DocumentTypeAdapter adapter = (DocumentTypeAdapter) document.getAdapterFor(DocumentTypeAdapter.class);
		if (adapter == null)
			return DocumentTypeAdapter.STRICT_CASE;
		return adapter.getTagNameCase();
	}

	/**
	 */
	private boolean isBooleanAttr(Attr attr) {
		if (attr == null)
			return false;
		CMAttributeDeclaration decl = CMNodeUtil.getAttributeDeclaration(attr);
		if (decl == null)
			return false;
		CMDataType type = decl.getAttrType();
		if (type == null)
			return false;
		String values[] = type.getEnumeratedValues();
		if (values == null)
			return false;
		return (values.length == 1 && values[0].equals(decl.getAttrName()));
	}
}
