/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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



// for org.apache.xerces 3.2.1
// import org.apache.xerces.utils.XMLCharacterProperties;
// DMW modified for XML4J 4.0.1
import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.contentmodel.CMDocument;
import org.eclipse.wst.common.contentmodel.CMEntityDeclaration;
import org.eclipse.wst.common.contentmodel.CMNamedNodeMap;
import org.eclipse.wst.common.contentmodel.modelquery.ModelQuery;
import org.eclipse.wst.sse.core.modelhandler.IModelHandler;
import org.eclipse.wst.xml.core.NameValidator;
import org.eclipse.wst.xml.core.commentelement.impl.CommentElementRegistry;
import org.eclipse.wst.xml.core.document.DocumentTypeAdapter;
import org.eclipse.wst.xml.core.document.JSPTag;
import org.eclipse.wst.xml.core.document.XMLCharEntity;
import org.eclipse.wst.xml.core.document.XMLDocument;
import org.eclipse.wst.xml.core.document.XMLElement;
import org.eclipse.wst.xml.core.document.XMLModel;
import org.eclipse.wst.xml.core.modelquery.ModelQueryUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;


/**
 * DocumentImpl class
 */
public class DocumentImpl extends NodeContainer implements XMLDocument {

	/**
	 * Internal-use only class. This class was added to better able to handle
	 * repetetive request for getElementsByTagName. The cache is cleared when
	 * ever the document changes at all, so still not real efficient,
	 */
	class TagNameCache {

		private boolean active = true;

		private Map cache;

		public TagNameCache() {
			super();
			cache = new HashMap();
		}

		/**
		 * @param b
		 */
		public void activate(boolean b) {
			active = b;
			if (!b)
				clear();
		}

		public void addItem(String tagname, NodeListImpl nodelist) {
			if (tagname == null || nodelist == null)
				return;
			cache.put(tagname, nodelist);
		}

		public void clear() {
			cache.clear();
		}

		public NodeListImpl getItem(String tagName) {
			NodeListImpl result = null;
			if (active) {
				result = (NodeListImpl) cache.get(tagName);
				//					if (result != null) {
				//						System.out.println("getElementsByTagname from cache: " +
				// tagName);
				//					}
			}
			return result;
		}

	}

	// this is a constant just to give compile-time control over
	// whether or not to use the cache. If, in future, its found that
	// there are no (or few) "duplicate requests" ... then this cache
	// is not needed.
	private static final boolean usetagnamecache = true;
	private DocumentTypeAdapter documentTypeAdapter = null;

	private XMLModelImpl model = null;
	private TagNameCache tagNameCache;

	/**
	 * DocumentImpl constructor
	 */
	protected DocumentImpl() {
		super();
		if (usetagnamecache) {
			tagNameCache = new TagNameCache();
		}
	}

	/**
	 * DocumentImpl constructor
	 * 
	 * @param that
	 *            DocumentImpl
	 */
	protected DocumentImpl(DocumentImpl that) {
		super(that);
		if (usetagnamecache) {
			tagNameCache = new TagNameCache();
		}
	}

	/**
	 * @param b
	 */
	void activateTagNameCache(boolean b) {
		tagNameCache.activate(b);
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * Changes the <code>ownerDocument</code> of a node, its children, as
	 * well as the attached attribute nodes if there are any. If the node has
	 * a parent it is first removed from its parent child list. This
	 * effectively allows moving a subtree from one document to another. The
	 * following list describes the specifics for each type of node.
	 * <dl>
	 * <dt>ATTRIBUTE_NODE</dt>
	 * <dd>The <code>ownerElement</code> attribute is set to
	 * <code>null</code> and the <code>specified</code> flag is set to
	 * <code>true</code> on the adopted <code>Attr</code>. The
	 * descendants of the source <code>Attr</code> are recursively adopted.
	 * </dd>
	 * <dt>DOCUMENT_FRAGMENT_NODE</dt>
	 * <dd>The descendants of the source node are recursively adopted.</dd>
	 * <dt>DOCUMENT_NODE</dt>
	 * <dd><code>Document</code> nodes cannot be adopted.</dd>
	 * <dt>DOCUMENT_TYPE_NODE</dt>
	 * <dd><code>DocumentType</code> nodes cannot be adopted.</dd>
	 * <dt>ELEMENT_NODE</dt>
	 * <dd>Specified attribute nodes of the source element are adopted, and
	 * the generated <code>Attr</code> nodes. Default attributes are
	 * discarded, though if the document being adopted into defines default
	 * attributes for this element name, those are assigned. The descendants
	 * of the source element are recursively adopted.</dd>
	 * <dt>ENTITY_NODE</dt>
	 * <dd><code>Entity</code> nodes cannot be adopted.</dd>
	 * <dt>ENTITY_REFERENCE_NODE</dt>
	 * <dd>Only the <code>EntityReference</code> node itself is adopted,
	 * the descendants are discarded, since the source and destination
	 * documents might have defined the entity differently. If the document
	 * being imported into provides a definition for this entity name, its
	 * value is assigned.</dd>
	 * <dt>NOTATION_NODE</dt>
	 * <dd><code>Notation</code> nodes cannot be adopted.</dd>
	 * <dt>PROCESSING_INSTRUCTION_NODE, TEXT_NODE, CDATA_SECTION_NODE,
	 * COMMENT_NODE</dt>
	 * <dd>These nodes can all be adopted. No specifics.</dd>
	 * Should this method simply return null when it fails? How "exceptional"
	 * is failure for this method?Stick with raising exceptions only in
	 * exceptional circumstances, return null on failure (F2F 19 Jun 2000).Can
	 * an entity node really be adopted?No, neither can Notation nodes (Telcon
	 * 13 Dec 2000).Does this affect keys and hashCode's of the adopted
	 * subtree nodes?If so, what about readonly-ness of key and hashCode?if
	 * not, would appendChild affect keys/hashCodes or would it generate
	 * exceptions if key's are duplicate? Update: Hashcodes have been dropped.
	 * Given that the key is only unique within a document an adopted node
	 * needs to be given a new key, but what does it mean for the application?
	 * 
	 * @param source
	 *            The node to move into this document.
	 * @return The adopted node, or <code>null</code> if this operation
	 *         fails, such as when the source node comes from a different
	 *         implementation.
	 * @exception DOMException
	 *                NOT_SUPPORTED_ERR: Raised if the source node is of type
	 *                <code>DOCUMENT</code>,<code>DOCUMENT_TYPE</code>.
	 *                <br>
	 *                NO_MODIFICATION_ALLOWED_ERR: Raised when the source node
	 *                is readonly.
	 * @since DOM Level 3
	 */
	public org.w3c.dom.Node adoptNode(org.w3c.dom.Node source) throws org.w3c.dom.DOMException {
		return null;
	}

	/**
	 * @param tagName
	 */
	protected void checkTagNameValidity(String tagName) {
		if (!isValidName(tagName)) {
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, createDOMExceptionMessage(DOMException.INVALID_CHARACTER_ERR, tagName));
		}
	}

	/**
	 * cloneNode method
	 * 
	 * @return org.w3c.dom.Node
	 * @param deep
	 *            boolean
	 */
	public Node cloneNode(boolean deep) {
		DocumentImpl cloned = new DocumentImpl(this);
		if (deep)
			cloned.importChildNodes(this, true);
		return cloned;
	}

	/**
	 * createAttribute method
	 * 
	 * @return org.w3c.dom.Attr
	 * @param name
	 *            java.lang.String
	 */
	public Attr createAttribute(String name) throws DOMException {
		AttrImpl attr = new AttrImpl();
		attr.setOwnerDocument(this);
		attr.setName(name);
		return attr;
	}

	/**
	 */
	public Attr createAttributeNS(String uri, String name) throws DOMException {
		AttrImpl attr = new AttrImpl();
		attr.setOwnerDocument(this);
		attr.setName(name);
		attr.setNamespaceURI(uri);
		return attr;
	}

	/**
	 * createCDATASection method
	 * 
	 * @return org.w3c.dom.CDATASection
	 * @param data
	 *            java.lang.String
	 */
	public CDATASection createCDATASection(String data) throws DOMException {
		// allow CDATA section
		// if (!isXMLType()) {
		//	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, new
		// String());
		// }
		CDATASectionImpl cdata = new CDATASectionImpl();
		cdata.setOwnerDocument(this);
		if (data != null)
			cdata.setData(data);
		return cdata;
	}

	/**
	 * createComment method
	 * 
	 * @return org.w3c.dom.Comment
	 * @param data
	 *            java.lang.String
	 */
	public Comment createComment(String data) {
		CommentImpl comment = new CommentImpl();
		comment.setOwnerDocument(this);
		if (data != null)
			comment.setData(data);
		return comment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sed.model.xml.XMLDocument#createCommentElement(java.lang.String,
	 *      boolean)
	 */
	public Element createCommentElement(String tagName, boolean isJSPTag) throws DOMException {
		if (!isJSPType() && isJSPTag) {
			throw new DOMException(DOMException.INVALID_MODIFICATION_ERR, new String());
		}
		ElementImpl element = (ElementImpl) createElement(tagName);
		element.setJSPTag(isJSPTag);
		CommentElementRegistry registry = CommentElementRegistry.getInstance();
		if (registry.setupCommentElement(element)) {
			return element;
		} else {
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, new String());
		}
	}

	/**
	 * createDoctype method
	 * 
	 * @return org.w3c.dom.DocumentType
	 * @param name
	 *            java.lang.String
	 */
	public DocumentType createDoctype(String name) {
		DocumentTypeImpl docType = new DocumentTypeImpl();
		docType.setOwnerDocument(this);
		docType.setName(name);
		return docType;
	}

	/**
	 * createDocumentFragment method
	 * 
	 * @return org.w3c.dom.DocumentFragment
	 */
	public DocumentFragment createDocumentFragment() {
		DocumentFragmentImpl fragment = new DocumentFragmentImpl();
		fragment.setOwnerDocument(this);
		return fragment;
	}

	/**
	 * createElement method
	 * 
	 * @return org.w3c.dom.Element
	 * @param tagName
	 *            java.lang.String
	 */
	public Element createElement(String tagName) throws DOMException {
		checkTagNameValidity(tagName);

		ElementImpl element = new ElementImpl();
		element.setOwnerDocument(this);
		element.setTagName(tagName);
		return element;
	}

	/**
	 */
	public Element createElementNS(String uri, String tagName) throws DOMException {
		if (!isValidName(tagName)) {
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, new String());
		}

		ElementImpl element = new ElementImpl();
		element.setOwnerDocument(this);
		element.setTagName(tagName);
		element.setNamespaceURI(uri);
		return element;
	}

	/**
	 * createEntity method
	 * 
	 * @return org.w3c.dom.Entity
	 * @param name
	 *            java.lang.String
	 */
	public Entity createEntity(String name) {
		EntityImpl entity = new EntityImpl();
		entity.setOwnerDocument(this);
		entity.setName(name);
		return entity;
	}

	/**
	 * createEntityReference method
	 * 
	 * @return org.w3c.dom.EntityReference
	 * @param name
	 *            java.lang.String
	 */
	public EntityReference createEntityReference(String name) throws DOMException {
		if (!isXMLType()) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, new String());
		}

		EntityReferenceImpl ref = new EntityReferenceImpl();
		ref.setOwnerDocument(this);
		ref.setName(name);
		return ref;
	}

	/**
	 */
	public NodeIterator createNodeIterator(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) {
		if (root == null)
			root = this;
		return new NodeIteratorImpl(root, whatToShow, filter);
	}

	/**
	 * createNotation method
	 * 
	 * @return org.w3c.dom.Notation
	 * @param name
	 *            java.lang.String
	 */
	public Notation createNotation(String name) {
		NotationImpl notation = new NotationImpl();
		notation.setOwnerDocument(this);
		notation.setName(name);
		return notation;
	}

	/**
	 * createProcessingInstruction method
	 * 
	 * @return org.w3c.dom.ProcessingInstruction
	 * @param target
	 *            java.lang.String
	 * @param data
	 *            java.lang.String
	 */
	public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
		ProcessingInstructionImpl pi = new ProcessingInstructionImpl();
		pi.setOwnerDocument(this);
		pi.setTarget(target);
		if (data != null)
			pi.setData(data);
		return pi;
	}

	/**
	 */
	public Range createRange() {
		return new RangeImpl();
	}

	/**
	 * createTextNode method
	 * 
	 * @return org.w3c.dom.Text
	 * @param data
	 *            java.lang.String
	 */
	public Text createTextNode(String data) {
		TextImpl text = new TextImpl();
		text.setOwnerDocument(this);
		text.setData(data);
		return text;
	}

	/**
	 */
	public TreeWalker createTreeWalker(Node root, int whatToShow, NodeFilter filter, boolean entityReferenceExpansion) {
		// not suppoerted
		return null;
	}

	private DocumentType findDoctype(Node node) {
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() == DOCUMENT_TYPE_NODE && child instanceof DocumentType) {
				return (DocumentType) child;
			} else if (child.getNodeType() == ELEMENT_NODE && ((XMLElement) child).isCommentTag()) {
				// search DOCTYPE inside of generic comment element
				DocumentType docType = findDoctype(child);
				if (docType != null) {
					return docType;
				}
			}
		}

		return null;
	}

	private Element findDocumentElement(String docName, Node node, Node[] firstFound) {
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() != ELEMENT_NODE)
				continue;
			ElementImpl element = (ElementImpl) child;
			if (element.isCommentTag()) {
				Element docElement = findDocumentElement(docName, element, firstFound);
				if (docElement != null) {
					return docElement;
				} else {
					// added 'else continue' to better handle cases where
					// there is "more than one root" element
					// especially complicated by CommentElements, which are
					// sometimes treated as elements, but should
					// be treated as comments in this context.
					continue;
				}
			}
			// note: the "name" won't match in the event of a jsp tag ... but
			// incase
			// the name is null, we do not want the jsp element returned as
			// documentElement
			if (element.isJSPTag())
				continue;
			if (docName == null)
				return element;
			// use local name for namespace
			String localName = element.getLocalName();
			if (localName == null)
				continue;
			if (isXMLType()) {
				if (localName.equals(docName))
					return element;
			} else {
				if (localName.equalsIgnoreCase(docName))
					return element;
			}
			if (firstFound[0] == null)
				firstFound[0] = element;
		}
		return null;
	}

	/**
	 * getCharValue method
	 * 
	 * @return java.lang.String
	 * @param name
	 *            java.lang.String
	 */
	protected String getCharValue(String name) {
		if (name == null)
			return null;
		int length = name.length();
		if (length == 0)
			return null;

		if (name.charAt(0) == '#') { // character reference
			if (length == 1)
				return null;
			int radix = 10;
			String s = null;
			// now allow hexadecimal also for non XML document
			if (name.charAt(1) == 'x') { // hexadecimal
				radix = 16;
				s = name.substring(2);
			} else { // decimal
				s = name.substring(1);
			}
			if (s == null || s.length() == 0)
				return null;
			if (s.charAt(0) == '-')
				return null; // no minus accepted
			char c = 0;
			try {
				c = (char) Integer.parseInt(s, radix);
			} catch (NumberFormatException ex) {
			}
			if (c == 0)
				return null;
			return String.valueOf(c);
		}

		// implicit character entities for XML
		if (name.equals(XMLCharEntity.LT_NAME))
			return XMLCharEntity.LT_VALUE;
		if (name.equals(XMLCharEntity.GT_NAME))
			return XMLCharEntity.GT_VALUE;
		if (name.equals(XMLCharEntity.AMP_NAME))
			return XMLCharEntity.AMP_VALUE;
		if (name.equals(XMLCharEntity.QUOT_NAME))
			return XMLCharEntity.QUOT_VALUE;
		if (isXMLType()) {
			if (name.equals(XMLCharEntity.APOS_NAME))
				return XMLCharEntity.APOS_VALUE;
		}

		CMDocument cm = getCMDocument();
		if (cm != null) {
			CMNamedNodeMap map = cm.getEntities();
			if (map != null) {
				CMEntityDeclaration decl = (CMEntityDeclaration) map.getNamedItem(name);
				if (decl != null) {
					String value = decl.getValue();
					if (value == null)
						return null;
					int valueLength = value.length();
					if (valueLength > 1 && value.charAt(0) == '&' && value.charAt(1) == '#' && value.charAt(valueLength - 1) == ';') {
						// character reference
						return getCharValue(value.substring(1, valueLength - 1));
					}
					return value;
				}
			}
		}

		return null;
	}

	/**
	 */
	protected CMDocument getCMDocument() {
		ModelQuery modelQuery = ModelQueryUtil.getModelQuery(this);
		if (modelQuery == null)
			return null;
		return modelQuery.getCorrespondingCMDocument(this);
	}

	/**
	 * getDoctype method
	 * 
	 * @return org.w3c.dom.DocumentType
	 */
	public DocumentType getDoctype() {
		return findDoctype(this);
	}

	/**
	 * getDocumentElement
	 * 
	 * @return org.w3c.dom.Element From DOM 2 Spec: documentElement of type
	 *         Element [p.62] , readonly This is a convenience [p.119]
	 *         attribute that allows direct access to the child node that is
	 *         the root element of the document. For HTML documents, this is
	 *         the element with the tagName "HTML". Note: we differ from this
	 *         definition a little in that we don't necessarily take the first
	 *         child but also look to match the name. In a well formed
	 *         document, of course, the result is the same, but not
	 *         necessarily the same in an ill-formed document.
	 */
	public Element getDocumentElement() {
		String name = null;
		DocumentType docType = getDocumentType();
		if (docType != null) {
			name = docType.getName();
		}

		Element first[] = new Element[1];
		Element docElement = findDocumentElement(name, this, first);
		if (docElement == null) {
			docElement = first[0];
		}

		return docElement;
	}

	/**
	 */
	protected DocumentType getDocumentType() {
		DocumentTypeAdapter adapter = getDocumentTypeAdapter();
		if (adapter == null)
			return getDoctype();
		return adapter.getDocumentType();
	}

	/**
	 */
	protected DocumentTypeAdapter getDocumentTypeAdapter() {
		if (this.documentTypeAdapter == null) {
			this.documentTypeAdapter = (DocumentTypeAdapter) getAdapterFor(DocumentTypeAdapter.class);
			if (this.documentTypeAdapter == null) {
				// add default adapter
				this.documentTypeAdapter = new DocumentTypeAdapterImpl(this);
				addAdapter(this.documentTypeAdapter);
			}
		}
		return this.documentTypeAdapter;
	}

	/**
	 */
	public String getDocumentTypeId() {
		DocumentType docType = getDocumentType();
		if (docType == null)
			return null;
		String id = docType.getPublicId();
		if (id == null)
			id = docType.getSystemId();
		return id;
	}

	/**
	 */
	public Element getElementById(String id) {
		if (id == null)
			return null;
		NodeIterator it = createNodeIterator(this, NodeFilter.SHOW_ALL, null, false);
		if (it == null)
			return null;

		for (Node node = it.nextNode(); node != null; node = it.nextNode()) {
			if (node.getNodeType() != ELEMENT_NODE)
				continue;
			ElementImpl element = (ElementImpl) node;
			String value = element.getAttribute("id");//$NON-NLS-1$
			if (value != null && value.equals(id))
				return element;
		}

		return null;
	}

	/**
	 * getElementsByTagName method
	 * 
	 * @return org.w3c.dom.NodeList
	 * @param tagName
	 *            java.lang.String
	 */
	public NodeList getElementsByTagName(String tagName) {
		if (tagName == null)
			return new NodeListImpl();

		NodeListImpl elements = null;

		if (usetagnamecache) {
			elements = tagNameCache.getItem(tagName);
		}

		if (elements == null) {
			elements = internalGetElementsByTagName(tagName);

		}

		return elements;
	}

	/**
	 */
	public NodeList getElementsByTagNameNS(String uri, String tagName) {
		if (tagName == null)
			return new NodeListImpl();

		NodeIterator it = createNodeIterator(this, NodeFilter.SHOW_ALL, null, false);
		if (it == null)
			return new NodeListImpl();
		NodeListImpl elements = new NodeListImpl();

		if (uri != null && uri.length() == 1 && uri.charAt(0) == '*') {
			uri = null; // do not care
		}
		if (tagName.length() == 1 && tagName.charAt(0) == '*') {
			tagName = null; // do not care
		}

		for (Node node = it.nextNode(); node != null; node = it.nextNode()) {
			if (node.getNodeType() != ELEMENT_NODE)
				continue;
			ElementImpl element = (ElementImpl) node;
			if (tagName != null) {
				String localName = element.getLocalName();
				if (localName == null || !localName.equals(tagName))
					continue;
			}
			if (uri != null) {
				String nsURI = element.getNamespaceURI();
				if (nsURI == null || !nsURI.equals(uri))
					continue;
			}
			elements.appendNode(element);
		}

		return elements;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the XML declaration, the encoding
	 * of this document. This is <code>null</code> when unspecified.
	 * 
	 * @since DOM Level 3
	 */
	public java.lang.String getEncoding() {
		return null;
	}

	/**
	 */
	public DOMImplementation getImplementation() {
		return model;
	}

	/**
	 * other nodes will be referring to this one to get the owning model
	 */
	public XMLModel getModel() {
		return model;
	}

	/**
	 * getNodeName method
	 * 
	 * @return java.lang.String
	 */
	public String getNodeName() {
		return "#document";//$NON-NLS-1$
	}

	/**
	 * getNodeType method
	 * 
	 * @return short
	 */
	public short getNodeType() {
		return DOCUMENT_NODE;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the XML declaration, whether this
	 * document is standalone.
	 * 
	 * @since DOM Level 3
	 */
	public boolean getStandalone() {
		return false;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying whether errors checking is enforced or not.
	 * When set to <code>false</code>, the implementation is free to not
	 * test every possible error case normally defined on DOM operations, and
	 * not raise any <code>DOMException</code>. In case of error, the
	 * behavior is undefined. This attribute is <code>true</code> by
	 * defaults.
	 * 
	 * @since DOM Level 3
	 */
	public boolean getStrictErrorChecking() {
		return false;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the XML declaration, the version
	 * number of this document. This is <code>null</code> when unspecified.
	 * 
	 * @since DOM Level 3
	 */
	public String getVersion() {
		return null;
	}

	/**
	 */
	protected boolean ignoreCase() {
		DocumentTypeAdapter adapter = getDocumentTypeAdapter();
		if (adapter == null)
			return false;
		return (adapter.getTagNameCase() != DocumentTypeAdapter.STRICT_CASE);
	}

	/**
	 */
	protected void importChildNodes(Node parent, boolean deep) {
		if (parent == null)
			return;

		removeChildNodes();

		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			Node imported = importNode(child, deep);
			if (imported == null)
				continue;
			appendChild(imported);
		}
	}

	/**
	 */
	public Node importNode(Node node, boolean deep) throws DOMException {
		if (node == null)
			return null;
		NodeImpl imported = (NodeImpl) node.cloneNode(deep);
		if (imported == null)
			return null;
		imported.setOwnerDocument(this, deep);
		return imported;
	}

	private NodeListImpl internalGetElementsByTagName(String tagName) {
		//System.out.println("getElementsByTagname: " + tagName);
		NodeIterator it = createNodeIterator(this, NodeFilter.SHOW_ALL, null, false);
		if (it == null)
			return new NodeListImpl();
		NodeListImpl elements = new NodeListImpl();

		if (tagName.length() == 1 && tagName.charAt(0) == '*') {
			tagName = null; // do not care
		}

		for (Node node = it.nextNode(); node != null; node = it.nextNode()) {
			if (node.getNodeType() != ELEMENT_NODE)
				continue;
			if (tagName != null) {
				ElementImpl element = (ElementImpl) node;
				if (!element.matchTagName(tagName))
					continue;
			}
			elements.appendNode(node);
		}
		if (usetagnamecache) {
			tagNameCache.addItem(tagName, elements);
		}
		return elements;
	}

	/**
	 */
	public boolean isJSPDocument() {
		Element element = getDocumentElement();
		if (element == null)
			return false;
		String tagName = element.getTagName();
		if (tagName == null)
			return false;
		return tagName.equals(JSPTag.JSP_ROOT);
	}

	/**
	 */
	public boolean isJSPType() {
		if (this.model == null)
			return false;
		IModelHandler handler = this.model.getModelHandler();
		if (handler == null)
			return false;
		String id = handler.getAssociatedContentTypeId();
		if (id == null)
			return false;
		// ISSUE: -- avoid this hardcoded string
		return id.equals("org.eclipse.jst.jsp.core.jspsource");
	}

	/**
	 */
	protected boolean isValidName(String name) {
		if (name == null || name.length() == 0)
			return false;
		//	// DMW: modified for XML4J 4.0.1
		//	if (XMLChar.isValidName(name)) return true;
		if (NameValidator.isValid(name))
			return true;
		// special for invalid declaration
		if (name.length() == 1 && name.charAt(0) == '!')
			return true;
		// special for JSP tag in tag name
		if (name.startsWith(JSPTag.TAG_OPEN))
			return true;
		return false;
	}

	/**
	 */
	public boolean isXMLType() {
		DocumentTypeAdapter adapter = getDocumentTypeAdapter();
		if (adapter == null)
			return true;
		return adapter.isXMLType();
	}

	/**
	 */
	protected void releaseDocumentType() {
		if (this.documentTypeAdapter == null)
			return;
		this.documentTypeAdapter.release();
		this.documentTypeAdapter = null;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the XML declaration, the encoding
	 * of this document. This is <code>null</code> when unspecified.
	 * 
	 * @since DOM Level 3
	 */
	public void setEncoding(java.lang.String encoding) {
	}

	/**
	 * setModel method
	 * 
	 * @param model
	 *            XMLModel
	 */

	protected void setModel(XMLModel model) {
		this.model = (XMLModelImpl) model;
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the XML declaration, whether this
	 * document is standalone.
	 * 
	 * @since DOM Level 3
	 */
	public void setStandalone(boolean standalone) {
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying whether errors checking is enforced or not.
	 * When set to <code>false</code>, the implementation is free to not
	 * test every possible error case normally defined on DOM operations, and
	 * not raise any <code>DOMException</code>. In case of error, the
	 * behavior is undefined. This attribute is <code>true</code> by
	 * defaults.
	 * 
	 * @since DOM Level 3
	 */
	public void setStrictErrorChecking(boolean strictErrorChecking) {
	}

	/**
	 * <p>
	 * EXPERIMENTAL! Based on the <a
	 * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
	 * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001. </a>.
	 * <p>
	 * An attribute specifying, as part of the XML declaration, the version
	 * number of this document. This is <code>null</code> when unspecified.
	 * 
	 * @since DOM Level 3
	 */
	public void setVersion(java.lang.String version) {
	}
}
